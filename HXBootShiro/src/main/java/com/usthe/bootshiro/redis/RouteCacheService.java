package com.usthe.bootshiro.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.usthe.bootshiro.domain.bo.AuthResource;

import hx.apigate.databridge.xmlBean.Route;
import hx.apigate.databridge.xmlBean.RouteAll;
import hx.apigate.databridge.xmlBean.RouteNode;

/**
 * 路由缓存服务（替代 Ignite AutoConfig / IgniteCache）
 *
 * 替代方案：
 *  - 路由全量：Redis String 存 Map&lt;"uri==method", RouteAll&gt; 的 JSON（fastjson2 序列化）
 *  - 变更通知：每次写后 PUBLISH 到 HXAPI:CHANNEL:ROUTE，网关订阅后重新加载本地缓存
 *  - 限流：原 IgniteSemaphore 改为 Redis Lua 计数信号量（由网关侧 RateLimiter 实现），
 *          平台侧仅把 tps 数值写入 Route/RouteNode（allTps/intTps）
 *  - JWT 会话：由 JwtSessionStore 负责（本类不再处理）
 *
 * @author Hermes (Redis 替代 Ignite/ZK 改造)
 */
@Component
public class RouteCacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteCacheService.class);

    private final StringRedisTemplate redisTemplate;

    public RouteCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 初始化 API 路由信息（全量覆盖），接口名称为 "uri==method"
     * @return 是否成功
     */
    @SuppressWarnings("unchecked")
    public boolean initApiRouteInfo(List<AuthResource> allApi) {
        Map<String, RouteAll> map = new ConcurrentHashMap<>();
        for (AuthResource authResource : allApi) {
            String uri = authResource.getUri();
            String method = authResource.getMethod();
            // 缓存新增、更新，没有路由的 API 不进缓存
            Map<String, Object> routeInfo = (Map<String, Object>) JSON.parse(authResource.getRouteInfo());
            if (routeInfo == null || routeInfo.isEmpty()) {
                continue;
            }
            String all_tps = routeInfo.get("all_tps") != null ? String.valueOf(routeInfo.get("all_tps")) : "200";
            String balance = (String) routeInfo.get("balance");
            String isAuth = (String) routeInfo.get("isAuth");
            String pType = (String) routeInfo.get("pType");
            String api_version = (String) routeInfo.get("api_version");
            boolean isWeight = !"1".equals(routeInfo.get("api_version_balance"));

            Route route = new Route();
            route.setMatchUrl(uri);
            route.setProtocal(pType);
            route.setVersion(api_version);
            if (isWeight) {
                // 防御：api_version_weightNum 缺失/为空时默认 1，避免新类型(routeInfo 不完整)启动崩溃
                Object weightNum = routeInfo.get("api_version_weightNum");
                route.setVersionWeight(weightNum == null ? 1 : Integer.parseInt(String.valueOf(weightNum)));
            }
            route.setNeedAuth("1".equals(isAuth));
            // 是否暴露为 MCP 工具（协议转换，默认不暴露）
            Object mcpExposeVal = routeInfo.get("mcp_expose");
            route.setMcpExpose(mcpExposeVal != null && "1".equals(String.valueOf(mcpExposeVal)));
            route.setStratege("1".equals(balance) ? "circle" : "weight");
            route.setAllTps(Integer.parseInt(all_tps));
            // 熔断配置（可选，0=网关自动推导）
            route.setCbFailThreshold(routeInfo.get("cb_fail_threshold") == null ? 0
                    : Integer.parseInt(String.valueOf(routeInfo.get("cb_fail_threshold"))));
            route.setCbSuccessThreshold(routeInfo.get("cb_success_threshold") == null ? 0
                    : Integer.parseInt(String.valueOf(routeInfo.get("cb_success_threshold"))));
            route.setCbTimeout(routeInfo.get("cb_timeout") == null ? 0
                    : Integer.parseInt(String.valueOf(routeInfo.get("cb_timeout"))));

            List<RouteNode> nodes = new ArrayList<>();
            if ("http".equals(routeInfo.get("pType")) || "tcp".equals(routeInfo.get("pType")) || "mcp".equals(routeInfo.get("pType")) || "websocket".equals(routeInfo.get("pType"))) {
                // 节点数优先取 routeNum 字段，缺失时按字段数推算（兼容旧数据）
                int routeNum = parseRouteNum(routeInfo, 5);
                for (int i = 1; i <= routeNum; i++) {
                    RouteNode node = new RouteNode();
                    String rout_ipAddr = (String) routeInfo.get("rout_ipAddr" + i);
                    String rout_order = (String) routeInfo.get("rout_order" + i);
                    String rout_port = (String) routeInfo.get("rout_port" + i);
                    String rout_tps = (String) routeInfo.get("rout_tps" + i);
                    String rout_weight = (String) routeInfo.get("rout_weight" + i);

                    node.setIp(rout_ipAddr);
                    node.setOrder(Integer.parseInt(rout_order == null || "".equals(rout_order) ? "0" : rout_order));
                    node.setPort(Integer.parseInt(rout_port == null || "".equals(rout_port) ? "18080" : rout_port));
                    int nodeTpsVal = Integer.parseInt(rout_tps == null || "".equals(rout_tps) ? "200" : rout_tps);
                    node.setIntTps(nodeTpsVal);
                    node.setWeight(Integer.parseInt(rout_weight == null || "".equals(rout_weight) ? "1" : rout_weight));
                    nodes.add(node);
                }
            } else if ("dubbo".equals(routeInfo.get("pType"))) {
                int routeNum = parseRouteNum(routeInfo, 2);
                for (int i = 1; i <= routeNum; i++) {
                    RouteNode node = new RouteNode();
                    String route_InterfaceName = (String) routeInfo.get("route_InterfaceName" + i);
                    String route_tps = routeInfo.get("route_tps" + i) == null ? "200" : String.valueOf(routeInfo.get("route_tps" + i));
                    node.setInterfaceName(route_InterfaceName);
                    int nodeTpsVal = Integer.parseInt(route_tps == null || "".equals(route_tps) ? "200" : route_tps);
                    node.setIntTps(nodeTpsVal);
                    nodes.add(node);
                }
            }
            route.setRouteNodes(nodes);
            route.init();

            if (map.containsKey(uri + "==" + method)) {
                map.get(uri + "==" + method).addInfo(route);
            } else {
                RouteAll routeAll = new RouteAll(isWeight, uri);
                routeAll.addInfo(route);
                map.put(uri + "==" + method, routeAll);
            }
        }
        saveAllRoute(map);
        LOGGER.info("路由缓存全量初始化完成, 共 {} 个接口", map.size());
        return true;
    }

    /**
     * 添加/更新 API 路由信息
     * @param isAdd   是否新增
     * @param uri     接口路径
     * @param method  HTTP 方法
     * @param jsonStr 路由信息 JSON
     * @return 是否成功
     */
    @SuppressWarnings("unchecked")
    public boolean addApiInfo(boolean isAdd, String uri, String method, String jsonStr) {
        Map<String, Object> routeInfo = (Map<String, Object>) JSON.parse(jsonStr);
        if (routeInfo == null) {
            return false;
        }
        String all_tps = routeInfo.get("all_tps") != null ? String.valueOf(routeInfo.get("all_tps")) : "200";
        String balance = (String) routeInfo.get("balance");
        String isAuth = (String) routeInfo.get("isAuth");
        String pType = (String) routeInfo.get("pType");
        boolean isWeight = !"1".equals(routeInfo.get("api_version_balance"));
        String api_version = (String) routeInfo.get("api_version");

        Map<String, RouteAll> allRoute = getAllRouteCache();
        if (allRoute == null) {
            allRoute = new ConcurrentHashMap<>();
        }
        RouteAll oldRouteAll = allRoute.get(uri + "==" + method);
        if (oldRouteAll == null) {
            oldRouteAll = new RouteAll(isWeight, uri);
        } else if (!isAdd && api_version != null) {
            // 修改时移除当前版本旧路由
            for (int i = 0; i < oldRouteAll.getRoutes().size(); i++) {
                if (api_version.equals(oldRouteAll.getRoutes().get(i).getVersion())) {
                    oldRouteAll.getRoutes().remove(i);
                    i--;
                }
            }
        }

        Route route = new Route();
        route.setMatchUrl(uri);
        route.setProtocal(pType);
        route.setVersion(api_version);
        if (isWeight) {
            // 防御：api_version_weightNum 缺失/为空时默认 1（与 initApiRouteInfo 全量方法保持一致）
            Object weightNum = routeInfo.get("api_version_weightNum");
            route.setVersionWeight(weightNum == null ? 1 : Integer.parseInt(String.valueOf(weightNum)));
        }
        route.setNeedAuth("1".equals(isAuth));
        // 是否暴露为 MCP 工具（协议转换，默认不暴露）
        Object mcpExposeVal = routeInfo.get("mcp_expose");
        route.setMcpExpose(mcpExposeVal != null && "1".equals(String.valueOf(mcpExposeVal)));
        route.setStratege("1".equals(balance) ? "circle" : "weight");
        route.setAllTps(Integer.parseInt(all_tps));
        // 熔断配置（可选，0=网关自动推导）
        route.setCbFailThreshold(routeInfo.get("cb_fail_threshold") == null ? 0
                : Integer.parseInt(String.valueOf(routeInfo.get("cb_fail_threshold"))));
        route.setCbSuccessThreshold(routeInfo.get("cb_success_threshold") == null ? 0
                : Integer.parseInt(String.valueOf(routeInfo.get("cb_success_threshold"))));
        route.setCbTimeout(routeInfo.get("cb_timeout") == null ? 0
                : Integer.parseInt(String.valueOf(routeInfo.get("cb_timeout"))));

        List<RouteNode> nodes = new ArrayList<>();
        if ("http".equals(routeInfo.get("pType")) || "tcp".equals(routeInfo.get("pType")) || "mcp".equals(routeInfo.get("pType")) || "websocket".equals(routeInfo.get("pType"))) {
            // 节点数优先取 routeNum 字段，缺失时按字段数推算（兼容旧数据）
            int routeNum = parseRouteNum(routeInfo, 5);
            for (int i = 1; i <= routeNum; i++) {
                RouteNode node = new RouteNode();
                String rout_ipAddr = (String) routeInfo.get("rout_ipAddr" + i);
                String rout_order = (String) routeInfo.get("rout_order" + i);
                String rout_port = (String) routeInfo.get("rout_port" + i);
                String rout_tps = (String) routeInfo.get("rout_tps" + i);
                String rout_weight = (String) routeInfo.get("rout_weight" + i);

                node.setIp(rout_ipAddr);
                node.setOrder(Integer.parseInt(rout_order == null || "".equals(rout_order) ? "0" : rout_order));
                node.setPort(Integer.parseInt(rout_port == null || "".equals(rout_port) ? "18080" : rout_port));
                int nodeTpsVal = Integer.parseInt(rout_tps == null || "".equals(rout_tps) ? "200" : rout_tps);
                node.setIntTps(nodeTpsVal);
                node.setWeight(Integer.parseInt(rout_weight == null || "".equals(rout_weight) ? "1" : rout_weight));
                nodes.add(node);
            }
        } else if ("dubbo".equals(routeInfo.get("pType"))) {
            int routeNum = parseRouteNum(routeInfo, 2);
            for (int i = 1; i <= routeNum; i++) {
                RouteNode node = new RouteNode();
                String route_InterfaceName = (String) routeInfo.get("route_InterfaceName" + i);
                String route_tps = routeInfo.get("route_tps" + i) == null ? "200" : String.valueOf(routeInfo.get("route_tps" + i));
                node.setInterfaceName(route_InterfaceName);
                int nodeTpsVal = Integer.parseInt(route_tps == null || "".equals(route_tps) ? "200" : route_tps);
                node.setIntTps(nodeTpsVal);
                nodes.add(node);
            }
        }
        route.setRouteNodes(nodes);
        route.init();
        oldRouteAll.addInfo(route);
        oldRouteAll.setUpdated(true);
        allRoute.put(uri + "==" + method, oldRouteAll);
        saveAllRoute(allRoute);
        publishRouteChange(RedisConstance.RouteCacheUpdate_SIG, uri, jsonStr);
        return true;
    }

    /**
     * 解析路由节点数：优先取 routeNum 字段，缺失时按字段数推算（每节点占 fieldsPerNode 个字段）
     * @param routeInfo 路由信息 map
     * @param fieldsPerNode http/tcp=5，dubbo=2
     */
    private int parseRouteNum(Map<String, Object> routeInfo, int fieldsPerNode) {
        Object routeNumObj = routeInfo.get("routeNum");
        if (routeNumObj != null && !"".equals(String.valueOf(routeNumObj))) {
            try {
                return Integer.parseInt(String.valueOf(routeNumObj));
            } catch (NumberFormatException ignored) {
                // 非法值走兜底推算
            }
        }
        int size = routeInfo.size();
        return size > 6 ? (size - 6) / fieldsPerNode : 0;
    }

    /**
     * 从缓存中删除指定 API 的对应版本
     * @param uri        接口路径
     * @param httpMethod HTTP 方法
     * @param apiVersion API 版本
     * @return 是否成功
     */
    public boolean removeApiInfo(String uri, String httpMethod, String apiVersion) {
        Map<String, RouteAll> allRoute = getAllRouteCache();
        if (allRoute == null) {
            return false;
        }
        RouteAll routeAll = allRoute.get(uri + "==" + httpMethod);
        if (routeAll == null) {
            return true;
        }
        routeAll.setUpdated(true);
        routeAll.getRoutes().removeIf(r -> apiVersion.equals(r.getVersion()));
        if (routeAll.getRoutes().isEmpty()) {
            allRoute.remove(uri + "==" + httpMethod);
        } else {
            routeAll.init();
        }
        saveAllRoute(allRoute);
        publishRouteChange(RedisConstance.RouteCacheDelete_SIG, uri, apiVersion);
        return true;
    }

    /**
     * 获取全量路由缓存
     */
    public Map<String, RouteAll> getAllRouteCache() {
        String json = redisTemplate.opsForValue().get(RedisConstance.ROUTE_ALL_KEY);
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, new com.alibaba.fastjson2.TypeReference<Map<String, RouteAll>>() {});
    }

    /**
     * 保存全量路由到 Redis
     */
    private void saveAllRoute(Map<String, RouteAll> map) {
        redisTemplate.opsForValue().set(RedisConstance.ROUTE_ALL_KEY, JSON.toJSONString(map));
    }

    /**
     * 发布路由变更通知（网关订阅后重新加载本地缓存）
     */
    private void publishRouteChange(String type, String uriPattern, String data) {
        try {
            InnerMsg msg = new InnerMsg(type, uriPattern, data);
            redisTemplate.convertAndSend(RedisConstance.ROUTE_CHANNEL, JSON.toJSONString(msg));
        } catch (Exception e) {
            LOGGER.warn("发布路由变更通知失败: {}", e.getMessage());
        }
    }

    /**
     * JWT 会话写入（委托 JwtSessionStore 的等价 Redis 操作，保留原方法签名兼容调用方）
     * @param refreshPeriodTime TTL 秒
     * @param key   会话 key
     * @param data  jwt
     */
    public boolean cacheJWTSessionData(long refreshPeriodTime, String key, String data) {
        redisTemplate.opsForValue().set(key, data, refreshPeriodTime, TimeUnit.SECONDS);
        return true;
    }

    /**
     * JWT 会话读取
     */
    public Object getJWTSessionData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * JWT 会话删除
     */
    public boolean removeJWTSessionData(String key) {
        redisTemplate.delete(key);
        return true;
    }
}
