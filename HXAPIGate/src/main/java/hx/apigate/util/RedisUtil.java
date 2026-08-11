package hx.apigate.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import hx.apigate.base.DistributedCacheProcessor;
import hx.apigate.circuitBreaker.CBManager;
import hx.apigate.databridge.xmlBean.Route;
import hx.apigate.databridge.xmlBean.RouteAll;
import hx.apigate.distributedCache.DefualtDistributedCacheManager;

/**
 * Redis 缓存工具类（替代原 IgniteUtil）
 *
 * 数据流（替代 Ignite REPLICATED 缓存）：
 *  - 路由全量：Redis String `HXAPI:ROUTE:ALL` 存 Map&lt;"uri==method", RouteAll&gt; JSON。
 *    网关启动时加载到本地 routeCache；平台每次变更 PUBLISH 通知，网关收到后 reloadRouteCache()
 *  - 鉴权规则：Redis Hash `HXAPI:AUTH`，field=API:{uri}。启动全量加载到本地 authCache
 *  - JWT 会话：Redis String `JWT-SESSION:{userId}`（平台 SETEX 写入，网关直读，不本地缓存）
 *  - 熔断器：本地 ConcurrentHashMap（各网关节点独立，业界标准）
 *
 * @author Hermes (Redis 替代 Ignite/ZK 改造)
 */
public class RedisUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    /** 全量路由缓存 key（与平台侧 RouteCacheService 保持一致） */
    public static final String ROUTE_ALL_KEY = "HXAPI:ROUTE:ALL";

    /** 鉴权规则 Hash key（与平台侧 ApiAuthCacheService 保持一致） */
    public static final String APIAUTH_CACHE_KEY = "HXAPI:AUTH";

    /** 应用信息 Hash key（与平台侧 AppCacheService 保持一致：field=appId, value=status） */
    public static final String APP_INFO_CACHE_KEY = "HXAPI:APP:INFO";

    /** 路由变更通知频道 */
    public static final String ROUTE_CHANNEL = "HXAPI:CHANNEL:ROUTE";

    /** 本地路由缓存：uri==method → RouteAll */
    private static volatile Map<String, RouteAll> routeCache = new ConcurrentHashMap<>();

    /** 本地鉴权规则缓存：API:{uri} → needRoles */
    private static volatile Map<String, String> authCache = new ConcurrentHashMap<>();

    /** 本地熔断器缓存：{matchUrl}{version}{ip}{port} → CBManager（各节点独立） */
    private static final Map<String, CBManager> circleBreakCache = new ConcurrentHashMap<>();

    /** Redis 是否可用（由 RedisProcessor 初始化） */
    private static volatile boolean redisAvailable = false;

    private static DistributedCacheProcessor cacheProcessor() {
        return DefualtDistributedCacheManager.getInstance();
    }

    public static boolean isRedisAvailable() {
        return redisAvailable;
    }

    public static void setRedisAvailable(boolean available) {
        redisAvailable = available;
    }

    // ==================== 路由缓存 ====================

    /**
     * 获取全量路由缓存（本地）
     */
    public static Map<String, RouteAll> getAllRoute() {
        return routeCache;
    }

    /**
     * 从 Redis 全量重新加载路由到本地（启动时 / 收到 Pub/Sub 通知 / 30s 轮询兜底时调用）
     */
    public static void reloadRouteCache() {
        try {
            String json = cacheProcessor().getString(ROUTE_ALL_KEY);
            if (json == null || json.isEmpty()) {
                routeCache = new ConcurrentHashMap<>();
                return;
            }
            Map<String, RouteAll> map = JSON.parseObject(json, new TypeReference<Map<String, RouteAll>>() {});
            if (map != null) {
                // 反序列化后重新初始化各 RouteAll 的权重状态
                for (RouteAll routeAll : map.values()) {
                    if (routeAll != null && routeAll.getRoutes() != null && !routeAll.getRoutes().isEmpty()) {
                        try {
                            routeAll.init();
                            for (Route r : routeAll.getRoutes()) {
                                if (r != null && r.getRouteNodes() != null && !r.getRouteNodes().isEmpty()) {
                                    r.init();
                                }
                            }
                        } catch (Exception ignore) {
                            // 权重初始化失败不影响主流程
                        }
                    }
                }
                routeCache = map;
            }
            LOGGER.info("路由缓存已从 Redis 加载, 共 {} 个接口", routeCache.size());
        } catch (Exception e) {
            LOGGER.warn("路由缓存加载失败: {}", e.getMessage());
        }
    }

    /**
     * 从 Redis 加载全量鉴权规则到本地（启动时 / 通知时调用）
     */
    public static void reloadAuthCache() {
        try {
            Map<String, String> map = new ConcurrentHashMap<>();
            // DistributedCacheProcessor 没有全量 hash 方法，直接由 RedisProcessor 通过 Jedis 加载后调用 setAuthCache
            authCache = map;
        } catch (Exception e) {
            LOGGER.warn("鉴权缓存加载失败: {}", e.getMessage());
        }
    }

    /**
     * 设置鉴权规则缓存（由 RedisProcessor 全量加载后注入）
     */
    public static void setAuthCache(Map<String, String> map) {
        if (map != null) {
            authCache = map;
        }
    }

    /**
     * 获取鉴权规则：API:{uri} → needRoles
     */
    public static String getAuthRule(String key) {
        return authCache.get(key);
    }

    /**
     * 更新单条鉴权规则（Pub/Sub 增量通知时调用）
     */
    public static void updateAuthRule(String key, String roles) {
        if (roles == null || roles.isEmpty()) {
            authCache.remove(key);
        } else {
            authCache.put(key, roles);
        }
    }

    // ==================== JWT 会话 ====================

    /**
     * 读取 JWT 会话（直读 Redis，不本地缓存）
     * @param key 如 JWT-SESSION:admin
     */
    public static String getJwtSession(String key) {
        try {
            return cacheProcessor().getString(key);
        } catch (Exception e) {
            LOGGER.warn("读取 JWT 会话失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 读取应用状态（平台 AppCacheService 写入，field=appId）
     * @return 状态字符串 "1"=启用 "0"=停用；应用不存在返回 null
     */
    public static String getAppStatus(String appId) {
        try {
            return cacheProcessor().getHashString(APP_INFO_CACHE_KEY, appId);
        } catch (Exception e) {
            LOGGER.warn("读取应用状态失败: {}", e.getMessage());
            return null;
        }
    }

    // ==================== 熔断器 ====================

    /**
     * 熔断器缓存（本地，各网关节点独立）
     */
    public static Map<String, CBManager> getCircleBreakCache() {
        return circleBreakCache;
    }

    // ==================== 便捷方法（兼容原 IgniteUtil 调用习惯） ====================

    /**
     * 获取 API 路由缓存（兼容旧调用：.get("ALL_ROUTE") 返回全量 Map）
     */
    @Deprecated
    public static Map<String, RouteAll> getAPIRouteCache() {
        return routeCache;
    }

    /**
     * 获取鉴权缓存（兼容旧调用：.get(key) 返回 needRoles）
     */
    @Deprecated
    public static Map<String, String> getAPIAuthCache() {
        return authCache;
    }
}
