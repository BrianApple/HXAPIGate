package hx.apigate.mcp;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;

import hx.apigate.databridge.NodeInfo;
import hx.apigate.databridge.xmlBean.Route;
import hx.apigate.databridge.xmlBean.RouteAll;
import hx.apigate.util.RedisUtil;
import hx.apigate.util.RouteSelectUtil;

/**
 * MCP 工具调用器：把 MCP tools/call 转换为对后端 HTTP 接口的真实请求。
 * <p>
 * 转换规则：
 * <ul>
 *   <li>工具名 {METHOD}_{uri} 还原 method + 路由模板</li>
 *   <li>路径模板参数 {xxx} → 从 arguments 取值替换进 URL（缺失则原样保留并置空）</li>
 *   <li>GET/DELETE：剩余参数拼 query string</li>
 *   <li>POST/PUT/PATCH：剩余参数作为 JSON body</li>
 *   <li>鉴权：透传 MCP 客户端请求的 authorization/userId 头</li>
 *   <li>路由选择复用网关限流/熔断/负载均衡（getRouteByPattern）</li>
 * </ul>
 */
public class McpInvoker {

    private static final Logger logger = LoggerFactory.getLogger(McpInvoker.class);

    private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{(\\w+)\\}");

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final int REQUEST_TIMEOUT_SECONDS = 30;

    /**
     * 执行工具调用
     * @param toolName   工具名（{METHOD}_{uri}）
     * @param arguments  MCP 调用参数
     * @param headers    透传的 HTTP 头（authorization/userId）
     * @return MCP tools/call 结果（content/isError）
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> call(String toolName, Map<String, Object> arguments,
                                           Map<String, String> headers) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> content = new ArrayList<>();
        result.put("content", content);
        result.put("isError", false);

        try {
            String[] parts = McpToolRegistry.parseToolName(toolName);
            if (parts == null) {
                return errorResult(result, content, "非法工具名: " + toolName);
            }
            String method = parts[0];
            String templateUri = parts[1];
            String routeKey = templateUri + "==" + method;

            // 1. 查路由（含限流/熔断/负载均衡选择节点）
            RouteAll routeAll = RedisUtil.getAllRoute().get(routeKey);
            if (routeAll == null || routeAll.getRoutes() == null || routeAll.getRoutes().isEmpty()) {
                return errorResult(result, content, "未找到后端路由: " + routeKey + "（请确认该接口已注册且暴露为 MCP 工具）");
            }
            Route route = routeAll.getRoutes().get(0);
            if (!route.isMcpExpose()) {
                return errorResult(result, content, "接口 " + routeKey + " 未开启「暴露为 MCP 工具」");
            }
            if (!RouteSelectUtil.HTTP.equals(route.getProtocal())) {
                return errorResult(result, content, "接口 " + routeKey + " 协议类型 " + route.getProtocal() + " 暂不支持 MCP 转换（仅 HTTP）");
            }

            // 2. 通过网关路由选择逻辑取节点（享受限流/熔断/负载均衡）
            NodeInfo nodeInfo;
            try {
                nodeInfo = RouteSelectUtil.getRouteByPattern(templateUri, templateUri, routeKey);
            } catch (Exception e) {
                return errorResult(result, content, "路由选择失败: " + e.getMessage());
            }

            // 3. 参数映射：路径参数替换 + 剩余参数
            Map<String, Object> args = arguments == null ? new HashMap<>() : arguments;
            Map<String, Object> remaining = new HashMap<>(args);
            String realPath = replacePathParams(templateUri, remaining);

            // 4. 构造请求
            String url = "http://" + nodeInfo.getRouteNode().getIp() + ":" + nodeInfo.getRouteNode().getPort() + realPath;
            boolean isBodyMethod = "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)
                    || "PATCH".equalsIgnoreCase(method);
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                    .header("Accept", "application/json, text/plain, */*");
            if (headers != null) {
                if (headers.get("authorization") != null && !headers.get("authorization").isEmpty()) {
                    reqBuilder.header("authorization", headers.get("authorization"));
                }
                if (headers.get("userId") != null && !headers.get("userId").isEmpty()) {
                    reqBuilder.header("userId", headers.get("userId"));
                }
            }

            HttpRequest request;
            if (isBodyMethod) {
                String bodyJson = remaining.isEmpty() ? "{}" : JSON.toJSONString(remaining);
                request = reqBuilder.header("Content-Type", "application/json")
                        .uri(URI.create(url))
                        .method(method, HttpRequest.BodyPublishers.ofString(bodyJson, StandardCharsets.UTF_8))
                        .build();
            } else {
                String query = buildQuery(remaining);
                String finalUrl = query.isEmpty() ? url : url + "?" + query;
                request = reqBuilder.uri(URI.create(finalUrl))
                        .method(method, HttpRequest.BodyPublishers.noBody())
                        .build();
            }

            // 5. 发送并读响应
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String body = response.body() == null ? "" : response.body();
            int status = response.statusCode();
            Map<String, Object> textItem = new HashMap<>();
            textItem.put("type", "text");
            textItem.put("text", status + " " + (body.isEmpty() ? "(empty response)" : body));
            content.add(textItem);
            result.put("isError", status >= 400);
            if (status >= 400) {
                logger.warn("MCP 调用 {} 后端返回异常状态 {}: {}", toolName, status, body.length() > 200 ? body.substring(0, 200) : body);
            }
            return result;
        } catch (Exception e) {
            logger.error("MCP 调用 {} 失败: {}", toolName, e.getMessage(), e);
            return errorResult(result, content, "调用失败: " + e.getMessage());
        }
    }

    /**
     * 路径模板参数替换：{id} → arguments 中值；被消费的参数从 remaining 移除
     */
    private static String replacePathParams(String template, Map<String, Object> remaining) {
        String path = template;
        Matcher m = PATH_PARAM_PATTERN.matcher(template);
        while (m.find()) {
            String param = m.group(1);
            Object val = remaining.remove(param);
            String replacement = val == null ? "" : String.valueOf(val);
            path = path.replace("{" + param + "}", replacement);
        }
        return path;
    }

    /**
     * 剩余参数拼 query string（URL 编码）
     */
    private static String buildQuery(Map<String, Object> remaining) {
        if (remaining.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : remaining.entrySet()) {
            if (e.getValue() == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
              .append('=')
              .append(URLEncoder.encode(String.valueOf(e.getValue()), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private static Map<String, Object> errorResult(Map<String, Object> result, List<Map<String, Object>> content, String msg) {
        Map<String, Object> textItem = new HashMap<>();
        textItem.put("type", "text");
        textItem.put("text", msg);
        content.add(textItem);
        result.put("isError", true);
        return result;
    }
}
