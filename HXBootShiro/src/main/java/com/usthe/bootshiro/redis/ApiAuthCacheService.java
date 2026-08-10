package com.usthe.bootshiro.redis;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * API 鉴权规则缓存服务（替代 Ignite APIAUTH_CACHE REPLICATED 缓存）
 *
 * 存储：Redis Hash
 *  - key:   HXAPI:AUTH
 *  - field: API:{uri==method}
 *  - value: needRoles（逗号分隔角色）
 *
 * 网关侧启动时全量加载到本地 Map，并通过 Pub/Sub 或 30s 轮询增量更新。
 *
 * @author Hermes (Redis 替代 Ignite/ZK 改造)
 */
@Component
public class ApiAuthCacheService {

    private final StringRedisTemplate redisTemplate;

    public ApiAuthCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 写入（或更新）一条鉴权规则
     */
    public void put(String key, String roles) {
        redisTemplate.opsForHash().put(RedisConstance.APIAUTH_CACHE, key, roles);
    }

    /**
     * 读取一条鉴权规则
     */
    public String get(String key) {
        Object v = redisTemplate.opsForHash().get(RedisConstance.APIAUTH_CACHE, key);
        return v == null ? null : String.valueOf(v);
    }

    /**
     * 删除一条鉴权规则
     */
    public void remove(String key) {
        redisTemplate.opsForHash().delete(RedisConstance.APIAUTH_CACHE, key);
    }

    /**
     * 全量读取（网关启动加载用）
     */
    public Map<Object, Object> getAll() {
        return redisTemplate.opsForHash().entries(RedisConstance.APIAUTH_CACHE);
    }

    /**
     * 清空
     */
    public void clear() {
        redisTemplate.delete(RedisConstance.APIAUTH_CACHE);
    }
}
