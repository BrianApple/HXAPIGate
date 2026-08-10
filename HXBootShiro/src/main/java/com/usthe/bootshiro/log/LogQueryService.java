package com.usthe.bootshiro.log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 日志查询服务：读取网关（HXAPIGate）与管理端（HXBootShiro）的 logback 滚动日志文件，
 * 按条件过滤、按 traceId 聚合完整请求链路。
 * <p>
 * 日志文件位置探测（相对管理端进程工作目录）：
 * <ul>
 *   <li>管理端日志：./logs/HXBootShiro/SystemOut*.log、SystemErrOut*.log</li>
 *   <li>网关日志：../HXAPIGate/logs/HXAPIGate/sys*.log（与网关同仓部署时）</li>
 *   <li>兜底：HXAPI_LOG_DIRS 环境变量（冒号分隔的绝对路径列表，可指向任意日志目录）</li>
 * </ul>
 * 当前行格式：时间 [线程] 级别 [traceId] [proto] [frameId] logger : 行号 - 消息
 */
@Service
public class LogQueryService {

    private static final Logger log = LoggerFactory.getLogger(LogQueryService.class);

    /**
     * 日志行解析正则：时间 / 线程 / 级别 / [traceId] / [proto] / [frameId]（可选，兼容旧格式）/ logger / 消息
     * 新格式（含帧号）：2026-08-10 22:00:00.123 [nioEventLoopGroup-2-1] INFO  [abc123] [websocket] [abc123-F0001] hx.apigate.socket.handlers.WebSocketFrontendHandler : 88 - msg
     * 旧格式（无帧号）：2026-08-10 22:00:00.123 [main] INFO  [abc123] [http] hx.apigate.util.RedisUtil : 123 - msg
     */
    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) \\[([^\\]]*)\\]\\s+(\\w+)\\s+\\[([^\\]]*)\\]\\s+\\[([^\\]]*)\\]\\s+(?:\\[([^\\]]*)\\]\\s+)?([\\w.$]+)(?: : (\\d+))? - (.*)$");

    private final List<Path> logFiles = new ArrayList<>();

    public LogQueryService() {
        initLogFiles();
    }

    private void initLogFiles() {
        String envDirs = System.getenv("HXAPI_LOG_DIRS");
        if (envDirs != null && !envDirs.trim().isEmpty()) {
            for (String dir : envDirs.split(":")) {
                collectDir(Paths.get(dir.trim()));
            }
        } else {
            // 管理端 + 网关同仓部署时的相对路径
            collectDir(Paths.get("./logs/HXBootShiro"));
            collectDir(Paths.get("../HXAPIGate/logs/HXAPIGate"));
        }
        if (logFiles.isEmpty()) {
            log.warn("未找到任何日志文件，请检查 HXAPI_LOG_DIRS 或同仓部署结构");
        } else {
            log.info("日志查询服务已加载 {} 个日志文件", logFiles.size());
        }
    }

    /** 收集目录下所有 .log 文件（当前文件 + 滚动归档） */
    private void collectDir(Path dir) {
        if (dir == null || !Files.isDirectory(dir)) {
            return;
        }
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.log")) {
            for (Path p : ds) {
                logFiles.add(p);
            }
        } catch (IOException e) {
            log.warn("读取日志目录失败: {} - {}", dir, e.getMessage());
        }
    }

    /**
     * 条件搜索（分页前全量过滤，日志量级可控）
     *
     * @param traceId   traceId 包含匹配（空=不限）
     * @param proto     协议精确匹配（空=不限）
     * @param level     级别精确匹配（空=不限）
     * @param keyword   关键词（匹配 logger + 消息，空=不限）
     * @param startTime 起始时间 yyyy-MM-dd HH:mm:ss（空=不限）
     * @param endTime   结束时间 yyyy-MM-dd HH:mm:ss（空=不限）
     * @param frameId   帧号包含匹配（空=不限；形如 abc123-F0001）
     */
    public List<LogEntry> search(String traceId, String proto, String level, String keyword,
                                 String startTime, String endTime, String frameId) {
        List<LogEntry> result = new ArrayList<>();
        for (Path file : logFiles) {
            String source = file.toString().contains("HXAPIGate") ? "gateway" : "admin";
            try (java.util.stream.Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
                lines.map(line -> parse(line, source))
                     .filter(e -> e != null)
                     .filter(e -> match(e, traceId, proto, level, keyword, startTime, endTime, frameId))
                     .forEach(result::add);
            } catch (IOException e) {
                // 文件可能被滚动/占用，跳过
            }
        }
        result.sort(Comparator.comparing(LogEntry::getTime));
        return result;
    }

    /**
     * 查询完整链路（该 traceId 或 frameId 在网关+管理端的所有日志，按时间升序）。
     * 支持两种入参：
     * <ul>
     *   <li>traceId（16位hex，连接/请求级）→ 返回该连接/请求全部日志</li>
     *   <li>frameId（形如 abc123-F0001，帧级）→ 返回该帧所属连接的日志，并按帧号保留匹配帧（含同连接其他帧，便于上下文）</li>
     * </ul>
     */
    public List<LogEntry> trace(String traceIdOrFrameId) {
        if (traceIdOrFrameId == null || traceIdOrFrameId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String id = traceIdOrFrameId.trim();
        // 帧 ID 提取连接 traceId（帧号格式：<连接traceId>-F<序号>）
        final String connTraceId;
        boolean isFrame = id.matches("^[0-9a-fA-F]{16}-F\\d+$") || id.matches("^F\\d+$");
        if (isFrame && id.contains("-F")) {
            connTraceId = id.substring(0, id.indexOf("-F"));
        } else {
            connTraceId = id;
        }
        List<LogEntry> result = new ArrayList<>();
        for (Path file : logFiles) {
            String source = file.toString().contains("HXAPIGate") ? "gateway" : "admin";
            try (java.util.stream.Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
                lines.map(line -> parse(line, source))
                     .filter(e -> e != null && e.getTraceId() != null && e.getTraceId().equals(connTraceId))
                     .forEach(result::add);
            } catch (IOException e) {
                // skip
            }
        }
        result.sort(Comparator.comparing(LogEntry::getTime));
        return result;
    }

    private boolean match(LogEntry e, String traceId, String proto, String level, String keyword,
                          String startTime, String endTime, String frameId) {
        if (traceId != null && !traceId.trim().isEmpty() && !e.getTraceId().contains(traceId.trim())) {
            return false;
        }
        if (proto != null && !proto.trim().isEmpty() && !proto.trim().equals(e.getProto())) {
            return false;
        }
        if (frameId != null && !frameId.trim().isEmpty()
                && (e.getFrameId() == null || !e.getFrameId().contains(frameId.trim()))) {
            return false;
        }
        if (level != null && !level.trim().isEmpty() && !level.trim().equalsIgnoreCase(e.getLevel())) {
            return false;
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            if (!e.getMessage().toLowerCase().contains(kw) && !e.getLogger().toLowerCase().contains(kw)) {
                return false;
            }
        }
        if (startTime != null && !startTime.trim().isEmpty() && e.getTime().compareTo(startTime.trim()) < 0) {
            return false;
        }
        if (endTime != null && !endTime.trim().isEmpty() && e.getTime().compareTo(endTime.trim()) > 0) {
            return false;
        }
        return true;
    }

    /** 解析单行日志；无法解析返回 null（跳过非日志行/空行） */
    private LogEntry parse(String line, String source) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        Matcher m = LINE_PATTERN.matcher(line);
        if (!m.matches()) {
            // 兼容旧格式（无 [traceId] [proto] 的历史日志）
            return parseLegacy(line, source);
        }
        // 组：1时间 2线程 3级别 4traceId 5proto 6frameId(可选) 7logger 8行号 9消息
        String traceId = norm(m.group(4));
        String proto = norm(m.group(5));
        String frameId = m.group(6) == null ? "" : norm(m.group(6));
        return new LogEntry(m.group(1), m.group(2), m.group(3),
                traceId, proto, frameId, m.group(7), m.group(9), source);
    }

    /** "--" 等占位符归一化为空串 */
    private String norm(String s) {
        if (s == null || s.trim().isEmpty() || "--".equals(s.trim())) {
            return "";
        }
        return s.trim();
    }

    /** 旧格式（无 MDC 标识）：2026-08-10 16:34:05.261 [ main ] - [ INFO ] [ logger : line ] - msg */
    private static final Pattern LEGACY_PATTERN = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) \\[([^\\]]*)\\] - \\[\\s*(\\w+)\\s*\\] \\[\\s*([\\w.$]+)\\s*:\\s*(\\d+)\\s*\\] - (.*)$");

    private LogEntry parseLegacy(String line, String source) {
        Matcher m = LEGACY_PATTERN.matcher(line);
        if (!m.matches()) {
            return null;
        }
        return new LogEntry(m.group(1), m.group(2), m.group(3), "", "",
                "", m.group(4), m.group(6), source);
    }
}
