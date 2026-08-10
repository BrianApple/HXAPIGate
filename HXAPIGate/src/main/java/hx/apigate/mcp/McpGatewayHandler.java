package hx.apigate.mcp;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * MCP 协议转换网关处理器：内置 /mcp 端点，把已注册的 HTTP 接口包装为 MCP 工具。
 * <p>
 * 支持 MCP streamable HTTP（单端点）：
 * <ul>
 *   <li>initialize / notifications/initialized / ping</li>
 *   <li>tools/list（从路由缓存自动生成工具清单）</li>
 *   <li>tools/call（转换为真实 HTTP 请求并返回结果）</li>
 *   <li>响应支持 application/json 与 text/event-stream（SSE）两种模式</li>
 * </ul>
 * 协议版本：2025-06-18（兼容 2025-03-26 / 2024-11-05 客户端协商）。
 */
public class McpGatewayHandler {

    private static final Logger logger = LoggerFactory.getLogger(McpGatewayHandler.class);

    /** 内置 MCP 端点路径（网关保留路径，不参与路由匹配） */
    public static final String MCP_ENDPOINT = "/mcp";

    /** 伪协议类型：TranceDataHandler 据此分流到 MCP 网关处理，不转发后端 */
    public static final String MCP_GATEWAY_PROTOCOL = "mcp-gateway";

    /** 支持协商的协议版本（从新到旧） */
    private static final String[] SUPPORTED_VERSIONS = {
            "2025-06-18", "2025-03-26", "2024-11-05"
    };

    /** JSON-RPC 错误码 */
    private static final int ERROR_PARSE = -32700;
    private static final int ERROR_INVALID_REQUEST = -32600;
    private static final int ERROR_METHOD_NOT_FOUND = -32601;
    private static final int ERROR_INVALID_PARAMS = -32602;
    private static final int ERROR_INTERNAL = -32603;

    /** 会话缓存：sessionId → 客户端信息（进程内，网关重启失效；生产可换 Redis） */
    private static final Map<String, String> SESSIONS = new ConcurrentHashMap<>();

    private McpGatewayHandler() {
    }

    /**
     * 处理 MCP 端点请求（由 TranceDataHandler 分流调用）
     */
    public static void handle(Channel webChannel, FullHttpRequest msg) {
        String bodyStr = msg.content().toString(StandardCharsets.UTF_8);
        JSONObject rpc;
        try {
            rpc = JSON.parseObject(bodyStr);
        } catch (Exception e) {
            writeResponse(webChannel, msg, error(null, ERROR_PARSE, "Parse error: " + e.getMessage()));
            return;
        }
        if (rpc == null || !"2.0".equals(String.valueOf(rpc.get("jsonrpc")))) {
            writeResponse(webChannel, msg, error(null, ERROR_INVALID_REQUEST, "Invalid Request"));
            return;
        }

        String method = rpc.getString("method");
        Object id = rpc.get("id");
        JSONObject params = rpc.getJSONObject("params");

        // 通知类消息（无 id）不响应
        boolean isNotification = id == null;
        if ("notifications/initialized".equals(method)) {
            return;
        }

        Map<String, Object> response;
        try {
            switch (method == null ? "" : method) {
                case "initialize":
                    response = handleInitialize(id, params);
                    break;
                case "ping":
                    response = ok(id, new HashMap<>());
                    break;
                case "tools/list":
                    response = handleToolsList(id);
                    break;
                case "tools/call":
                    response = handleToolsCall(id, params, msg);
                    break;
                default:
                    response = error(id, ERROR_METHOD_NOT_FOUND, "Method not found: " + method);
            }
        } catch (Exception e) {
            logger.error("MCP 请求处理异常 method={}: {}", method, e.getMessage(), e);
            response = error(id, ERROR_INTERNAL, "Internal error: " + e.getMessage());
        }
        writeResponse(webChannel, msg, response);
    }

    /**
     * initialize：协议协商 + 生成会话
     */
    private static Map<String, Object> handleInitialize(Object id, JSONObject params) {
        String clientVersion = params == null ? null : params.getString("protocolVersion");
        String negotiated = negotiateVersion(clientVersion);
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", negotiated);
        Map<String, Object> capabilities = new HashMap<>();
        Map<String, Object> toolsCap = new HashMap<>();
        toolsCap.put("listChanged", false);
        capabilities.put("tools", toolsCap);
        result.put("capabilities", capabilities);
        Map<String, Object> serverInfo = new HashMap<>();
        serverInfo.put("name", "HXAPIGate MCP Gateway");
        serverInfo.put("version", "3.0.1");
        result.put("serverInfo", serverInfo);
        return ok(id, result);
    }

    private static Map<String, Object> handleToolsList(Object id) {
        Map<String, Object> result = new HashMap<>();
        result.put("tools", McpToolRegistry.listTools());
        return ok(id, result);
    }

    /**
     * tools/call：转换为 HTTP 请求并执行
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> handleToolsCall(Object id, JSONObject params, FullHttpRequest msg) {
        if (params == null) {
            return error(id, ERROR_INVALID_PARAMS, "Invalid params: missing name/arguments");
        }
        String name = params.getString("name");
        Map<String, Object> arguments = params.getJSONObject("arguments");
        if (name == null || name.isEmpty()) {
            return error(id, ERROR_INVALID_PARAMS, "Invalid params: tool name is required");
        }
        Map<String, Object> args = arguments == null ? new HashMap<>() : arguments;

        // 透传鉴权头
        Map<String, String> headers = new HashMap<>();
        String auth = msg.headers().get("authorization");
        if (auth != null) {
            headers.put("authorization", auth);
        }
        String userId = msg.headers().get("userId");
        if (userId != null) {
            headers.put("userId", userId);
        }
        Map<String, Object> result = McpInvoker.call(name, args, headers);
        return ok(id, result);
    }

    // ------------------------------------------------------------------
    // JSON-RPC 响应构造
    // ------------------------------------------------------------------

    private static Map<String, Object> ok(Object id, Object result) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("jsonrpc", "2.0");
        resp.put("id", id);
        resp.put("result", result);
        return resp;
    }

    private static Map<String, Object> error(Object id, int code, String message) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("jsonrpc", "2.0");
        resp.put("id", id);
        Map<String, Object> err = new HashMap<>();
        err.put("code", code);
        err.put("message", message);
        resp.put("error", err);
        return resp;
    }

    // ------------------------------------------------------------------
    // 响应写回：JSON 或 SSE
    // ------------------------------------------------------------------

    private static void writeResponse(Channel webChannel, FullHttpRequest request, Map<String, Object> payload) {
        byte[] bytes = JSON.toJSONBytes(payload);
        boolean sse = acceptsSse(request);
        String sessionId = getOrCreateSession(request);

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes));
        if (sse) {
            // MCP streamable HTTP：SSE 帧封装（event: message）
            String frame = "event: message\ndata: " + new String(bytes, StandardCharsets.UTF_8) + "\n\n";
            byte[] frameBytes = frame.getBytes(StandardCharsets.UTF_8);
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(frameBytes));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream; charset=utf-8");
        } else {
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + "; charset=utf-8");
        }
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
        response.headers().set("Mcp-Session-Id", sessionId);
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "authorization, userId, content-type, mcp-session-id");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, OPTIONS");
        webChannel.writeAndFlush(response);
    }

    private static boolean acceptsSse(FullHttpRequest request) {
        String accept = request.headers().get(HttpHeaderNames.ACCEPT);
        return accept != null && (accept.contains("text/event-stream") || accept.contains("application/json, text/event-stream"));
    }

    /**
     * 会话：优先复用客户端 Mcp-Session-Id，否则生成新会话
     */
    private static String getOrCreateSession(FullHttpRequest request) {
        String existing = request.headers().get("Mcp-Session-Id");
        if (existing != null && !existing.isEmpty() && SESSIONS.containsKey(existing)) {
            return existing;
        }
        String sessionId = "hxapi-mcp-" + UUID.randomUUID();
        SESSIONS.put(sessionId, "");
        return sessionId;
    }

    /**
     * 协议版本协商：客户端版本受支持则原样返回，否则返回服务器最高版本
     */
    private static String negotiateVersion(String clientVersion) {
        if (clientVersion != null) {
            for (String v : SUPPORTED_VERSIONS) {
                if (v.equals(clientVersion)) {
                    return clientVersion;
                }
            }
        }
        return SUPPORTED_VERSIONS[0];
    }
}
