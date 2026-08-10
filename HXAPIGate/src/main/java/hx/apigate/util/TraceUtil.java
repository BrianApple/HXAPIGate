package hx.apigate.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.MDC;

/**
 * 请求溯源工具：traceId（请求链路唯一标识）+ proto（协议标识）+ frameId（帧号）写入 slf4j MDC，
 * 配合 logback pattern 中的 %X{traceId} / %X{proto} / %X{frameId} 输出到每行日志。
 * <p>
 * 使用约定：
 * <ul>
 *   <li>HTTP 短请求：GatewayServerHandler 入口生成/复用 traceId → 各处理阶段 put/clear（无帧号）</li>
 *   <li>TCP / WebSocket 长连接：连接建立时 put（连接级 traceId），每帧消息分配 frameId
 *       （格式：&lt;连接traceId&gt;-F&lt;序号&gt;，如 abc123-F0001），支持单帧链路追踪</li>
 *   <li>Dubbo 调用：请求级 traceId，每次调用生成帧号</li>
 *   <li>调用方可通过请求头 X-Trace-Id 传入自定义 traceId（跨服务链路溯源）</li>
 * </ul>
 */
public class TraceUtil {

    /** MDC key：请求链路唯一标识 */
    public static final String TRACE_ID = "traceId";
    /** MDC key：代理协议（http / mcp / tcp / websocket / dubbo） */
    public static final String PROTO = "proto";
    /** MDC key：帧号（长连接消息级标识，格式 &lt;traceId&gt;-F&lt;序号&gt;；无帧号时为 --） */
    public static final String FRAME_ID = "frameId";
    /** 请求头/响应头：溯源 ID（调用方传入则复用，否则网关生成） */
    public static final String HEADER_X_TRACE_ID = "X-Trace-Id";

    private TraceUtil() {
        throw new AssertionError();
    }

    /** 生成 16 位十六进制 traceId */
    public static String genTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /** 将 traceId 放入当前线程 MDC（调用方传入的为空时自动生成） */
    public static String putTraceId(String traceId) {
        String id = (traceId == null || traceId.trim().isEmpty()) ? genTraceId() : traceId.trim();
        MDC.put(TRACE_ID, id);
        return id;
    }

    /** 将协议标识放入当前线程 MDC（为空时置为 http） */
    public static void putProto(String proto) {
        MDC.put(PROTO, proto == null || proto.trim().isEmpty() ? "http" : proto.trim());
    }

    /**
     * 生成下一个帧号并放入 MDC：&lt;连接traceId&gt;-F&lt;序号&gt;（序号从 1 递增，不足 4 位补零）。
     *
     * @param connTraceId 连接级 traceId（可空，空时退化为 F&lt;序号&gt;）
     * @param frameSeq    连接内帧计数器（channel attr 持有）
     * @return 生成的帧号（同时写入当前线程 MDC）
     */
    public static String putNextFrameId(String connTraceId, AtomicLong frameSeq) {
        long n = frameSeq == null ? 1 : frameSeq.incrementAndGet();
        String frameId = (connTraceId == null || connTraceId.isEmpty())
                ? String.format("F%04d", n)
                : connTraceId + "-F" + String.format("%04d", n);
        MDC.put(FRAME_ID, frameId);
        return frameId;
    }

    /** 将指定帧号放入当前线程 MDC（为空时置为 --），返回实际写入的帧号 */
    public static String putFrameId(String frameId) {
        String id = frameId == null || frameId.trim().isEmpty() ? "--" : frameId.trim();
        MDC.put(FRAME_ID, id);
        return id;
    }

    /** 清理当前线程 MDC 中的 traceId / proto / frameId，避免线程复用导致串号 */
    public static void clear() {
        MDC.remove(TRACE_ID);
        MDC.remove(PROTO);
        MDC.remove(FRAME_ID);
    }
}
