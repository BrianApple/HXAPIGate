package com.usthe.bootshiro.redis;

/**
 * Redis 缓存 key 常量（替代原 Ignite Constance）
 *
 * 键设计：
 *  - HXAPI:ROUTE:ALL            String  全量路由 Map<"uri==method", RouteAll> 的 JSON
 *  - HXAPI:AUTH                 Hash    field=API:{uri==method}  value=needRoles
 *  - JWT-SESSION:{userId}       String  平台/网关共享的 JWT 会话（TTL）
 *  - HXAPI:LIMIT:{routeKey}     String  Redis Lua 计数信号量（全局限流/节点限流）
 *  - HXAPI:CHANNEL:ROUTE        Pub/Sub 路由变更通知频道（InnerMsg JSON）
 *
 * @author Hermes (Redis 替代 Ignite/ZK 改造)
 */
public class RedisConstance {

    /**
     * 全量路由缓存 key（String，值为 Map<String, RouteAll> 的 JSON）
     */
    public static final String ROUTE_ALL_KEY = "HXAPI:ROUTE:ALL";

    /**
     * 路由缓存内部固定 field（与旧 Ignite 保持一致）
     */
    public static final String ALL_ROUTE_FIELD = "ALL_ROUTE";

    /**
     * API 鉴权规则缓存（Hash：field=API:{uri==method}，value=needRoles）
     */
    public static final String APIAUTH_CACHE = "HXAPI:AUTH";

    /**
     * 路由变更通知频道（Pub/Sub，消息体为 InnerMsg JSON）
     */
    public static final String ROUTE_CHANNEL = "HXAPI:CHANNEL:ROUTE";

    /**
     * 路由更新消息类型（API00 更新 / API01 删除）
     */
    public static final String RouteCacheUpdate_SIG = "API00";
    public static final String RouteCacheDelete_SIG = "API01";

    /**
     * api 与授权角色缓存前缀
     */
    public static final String API_RESOURCE_ROLE = "API:";

    /**
     * JWT 会话前缀（平台/网关统一）
     */
    public static final String JWT_SESSION_PREFIX = "JWT-SESSION:";

    /**
     * 限流信号量 key 前缀
     */
    public static final String LIMIT_PREFIX = "HXAPI:LIMIT:";

    /**
     * 应用信息缓存（Hash：field=appId，value=status）。平台增删改同步写入，
     * 网关 JwtRealm 校验应用 license 时直读（不存在=应用已删除，status!=1=应用已停用）
     */
    public static final String APP_INFO_CACHE = "HXAPI:APP:INFO";
}
