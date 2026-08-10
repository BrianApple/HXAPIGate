package hx.apigate.util;

import java.util.UUID;

import org.slf4j.MDC;

/**
 * 请求溯源工具：traceId（请求链路唯一标识）+ proto（协议标识）写入 slf4j MDC，
 * 配合 logback pattern 中的 %X{traceId} / %X{proto} 输出到每行日志。
 * <p>
 * 使用约定：
 * <ul>
 *   <li>HTTP 短请求：GatewayServerHandler 入口生成/复用 traceId → 各处理阶段 put/clear</li>
 *   <li>TCP / WebSocket 长连接：连接建立时 put，channelInactive 时 clear（连接级 traceId）</li>
 *   <li>调用方可通过请求头 X-Trace-Id 传入自定义 traceId（跨服务链路溯源）</li>
 * </ul>
 */
public class TraceUtil {

    /** MDC key：请求链路唯一标识 */
    public static final String TRACE_ID = "traceId";
    /** MDC key：代理协议（http / mcp / tcp / websocket / dubbo） */
    public static final String PROTO = "proto";
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

    /** 清理当前线程 MDC 中的 traceId / proto，避免线程复用导致串号 */
    public static void clear() {
        MDC.remove(TRACE_ID);
        MDC.remove(PROTO);
    }
}
