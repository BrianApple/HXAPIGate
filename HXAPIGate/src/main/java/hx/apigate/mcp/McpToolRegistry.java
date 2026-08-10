package hx.apigate.mcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.databridge.xmlBean.Route;
import hx.apigate.databridge.xmlBean.RouteAll;
import hx.apigate.util.RedisUtil;
import hx.apigate.util.RouteSelectUtil;

/**
 * MCP 工具注册表：从网关已注册路由中筛选「暴露为 MCP 工具」的 HTTP 接口，自动生成 MCP tool 定义。
 * <p>
 * 每个被标记的接口对应一个 MCP 工具：
 * <ul>
 *   <li>name: {HTTP_METHOD}_{uri}（如 POST_/user/list/{id}，method+uri 保证唯一）</li>
 *   <li>description: 自动生成，说明后端接口与转换语义</li>
 *   <li>inputSchema: 宽松对象（additionalProperties=true）+ 路径模板参数提取（{xxx} 必填）</li>
 * </ul>
 */
public class McpToolRegistry {

    private static final Logger logger = LoggerFactory.getLogger(McpToolRegistry.class);

    /** 路径模板参数：{id} */
    private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{(\\w+)\\}");

    /**
     * 生成全部 MCP 工具定义（实时从路由缓存读取，路由变更后自动生效）
     */
    public static List<Map<String, Object>> listTools() {
        List<Map<String, Object>> tools = new ArrayList<>();
        Map<String, RouteAll> routes = RedisUtil.getAllRoute();
        if (routes == null || routes.isEmpty()) {
            return tools;
        }
        for (Map.Entry<String, RouteAll> entry : routes.entrySet()) {
            RouteAll routeAll = entry.getValue();
            if (routeAll == null || routeAll.getRoutes() == null) {
                continue;
            }
            for (Route route : routeAll.getRoutes()) {
                if (route == null || !route.isMcpExpose() || !RouteSelectUtil.HTTP.equals(route.getProtocal())) {
                    continue;
                }
                try {
                    tools.add(buildTool(entry.getKey(), route));
                } catch (Exception e) {
                    logger.warn("生成 MCP 工具失败, uri={}: {}", entry.getKey(), e.getMessage());
                }
            }
        }
        return tools;
    }

    /**
     * 构造单个工具定义
     * @param routeKey Redis 路由 key（uri==METHOD）
     * @param route    路由
     */
    private static Map<String, Object> buildTool(String routeKey, Route route) {
        Map<String, Object> tool = new HashMap<>();
        String name = buildToolName(routeKey);
        String method = routeKey.contains("==") ? routeKey.substring(routeKey.indexOf("==") + 2) : "POST";
        tool.put("name", name);
        tool.put("description", "HTTP API 转换工具：调用后端接口 " + method + " " + route.getMatchUrl()
                + "。参数自动映射：路径模板参数替换进 URL，其余参数 GET/DELETE 拼 query、POST/PUT 拼 JSON body。");

        // inputSchema：路径参数必填 + 其余宽松
        Map<String, Object> inputSchema = new HashMap<>();
        inputSchema.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();
        Matcher m = PATH_PARAM_PATTERN.matcher(route.getMatchUrl());
        while (m.find()) {
            String param = m.group(1);
            Map<String, Object> prop = new HashMap<>();
            prop.put("type", "string");
            prop.put("description", "路径参数 " + param);
            properties.put(param, prop);
            required.add(param);
        }
        inputSchema.put("properties", properties);
        inputSchema.put("required", required);
        inputSchema.put("additionalProperties", true); // 其余参数（query/body）宽松接收
        tool.put("inputSchema", inputSchema);
        return tool;
    }

    /**
     * 工具名：{METHOD}_{uri}（Redis key 格式为 uri==METHOD，此处反转），解析时用 split("_", 2) 还原
     */
    public static String buildToolName(String routeKey) {
        int idx = routeKey.indexOf("==");
        String uri = idx > 0 ? routeKey.substring(0, idx) : routeKey;
        String method = idx > 0 ? routeKey.substring(idx + 2) : "POST";
        return method + "_" + uri;
    }

    /**
     * 解析工具名 → [method, uri]，解析失败返回 null
     */
    public static String[] parseToolName(String toolName) {
        if (toolName == null || toolName.isEmpty()) {
            return null;
        }
        int idx = toolName.indexOf('_');
        if (idx <= 0) {
            return null;
        }
        String method = toolName.substring(0, idx);
        String uri = toolName.substring(idx + 1);
        if (!uri.startsWith("/") || uri.length() <= 1) {
            return null;
        }
        return new String[]{method, uri};
    }
}
