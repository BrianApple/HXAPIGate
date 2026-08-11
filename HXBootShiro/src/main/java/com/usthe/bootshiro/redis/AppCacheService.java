package com.usthe.bootshiro.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用信息缓存（Redis Hash HXAPI:APP:INFO）
 *
 * 设计目的：网关 JwtRealm 校验应用 license 时需确认「应用存在且启用」。
 * 平台在应用新增/编辑/删除时同步本缓存，网关认证时直读 Redis 校验：
 *  - field=appId 不存在 → 应用已删除，拒绝访问
 *  - value(status) != 1 → 应用已停用，拒绝访问
 *
 * @author Hermes
 */
@Service
public class AppCacheService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 写入（或更新）应用状态
     */
    public void put(String appId, Byte status) {
        if (appId == null) {
            return;
        }
        redisTemplate.opsForHash().put(RedisConstance.APP_INFO_CACHE, appId, String.valueOf(status == null ? 1 : status));
    }

    /**
     * 读取应用状态（不存在返回 null）
     */
    public String get(String appId) {
        Object v = redisTemplate.opsForHash().get(RedisConstance.APP_INFO_CACHE, appId);
        return v == null ? null : String.valueOf(v);
    }

    /**
     * 删除应用信息（应用删除时调用）
     */
    public void remove(String appId) {
        if (appId == null) {
            return;
        }
        redisTemplate.opsForHash().delete(RedisConstance.APP_INFO_CACHE, appId);
    }

    /**
     * 全量读取（平台启动时同步存量应用用）
     */
    public Map<String, String> getAll() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisConstance.APP_INFO_CACHE);
        Map<String, String> result = new HashMap<>();
        if (entries != null) {
            for (Map.Entry<Object, Object> e : entries.entrySet()) {
                result.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
            }
        }
        return result;
    }
}
