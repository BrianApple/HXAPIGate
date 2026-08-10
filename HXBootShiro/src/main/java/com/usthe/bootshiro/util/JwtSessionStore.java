package com.usthe.bootshiro.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * JWT 会话存储（替代 Ignite JWT_SESSION 缓存）
 *
 * Redis 可选设计：
 *  - 配置 jwt.session.store=redis  强制 Redis（不可用时抛错）
 *  - 配置 jwt.session.store=local  强制本地内存
 *  - 配置 jwt.session.store=auto   自动探测：Redis 可用用 Redis，否则本地内存（默认）
 *
 * Redis 在线：SETEX/GET/DEL，跨网关节点共享会话（支持互踢/强制下线）
 * 本地模式：ConcurrentHashMap + TTL 清理，JWT 本身无状态可验签，仅"互踢/强制下线"能力降级
 *
 * @author Hermes (P1 改造)
 */
@Component
public class JwtSessionStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtSessionStore.class);

    private final StringRedisTemplate redisTemplate;
    private final boolean redisEnabled;
    private final Map<String, ExpiringValue> localStore = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "jwt-session-cleaner");
        t.setDaemon(true);
        return t;
    });

    public JwtSessionStore(StringRedisTemplate redisTemplate,
                           @Value("${jwt.session.store:auto}") String mode) {
        this.redisTemplate = redisTemplate;
        boolean auto = mode == null || mode.isEmpty() || "auto".equalsIgnoreCase(mode);
        if ("redis".equalsIgnoreCase(mode)) {
            this.redisEnabled = true;
        } else if ("local".equalsIgnoreCase(mode)) {
            this.redisEnabled = false;
        } else {
            this.redisEnabled = auto && pingRedis();
        }
        LOGGER.info("JwtSessionStore 初始化完成, mode={}, 实际使用 {}", mode, this.redisEnabled ? "Redis" : "本地内存");
        if (!this.redisEnabled) {
            // 本地模式定时清理过期 key
            cleaner.scheduleAtFixedRate(this::cleanExpired, 60, 60, TimeUnit.SECONDS);
        }
    }

    private boolean pingRedis() {
        try {
            if (redisTemplate.getConnectionFactory() == null) {
                return false;
            }
            Object ping = redisTemplate.getConnectionFactory().getConnection().ping();
            // Lettuce 的 ping() 返回 String "PONG"；Jedis 返回 String "PONG"；兼容 Boolean 类型
            if (ping instanceof Boolean) {
                return Boolean.TRUE.equals(ping);
            }
            return ping != null && "PONG".equalsIgnoreCase(String.valueOf(ping));
        } catch (Exception e) {
            LOGGER.warn("Redis 不可用，JWT 会话降级为本地内存: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 写入会话
     * @param key 会话 key（如 JWT-SESSION:admin）
     * @param value jwt
     * @param ttlSeconds 有效秒数
     */
    public void set(String key, String value, long ttlSeconds) {
        if (redisEnabled) {
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
        } else {
            localStore.put(key, new ExpiringValue(value, System.currentTimeMillis() + ttlSeconds * 1000));
        }
    }

    /**
     * 读取会话
     */
    public String get(String key) {
        if (redisEnabled) {
            return redisTemplate.opsForValue().get(key);
        }
        ExpiringValue ev = localStore.get(key);
        if (ev == null) {
            return null;
        }
        if (ev.expireAt < System.currentTimeMillis()) {
            localStore.remove(key);
            return null;
        }
        return ev.value;
    }

    /**
     * 删除会话
     */
    public void remove(String key) {
        if (redisEnabled) {
            redisTemplate.delete(key);
        } else {
            localStore.remove(key);
        }
    }

    private void cleanExpired() {
        long now = System.currentTimeMillis();
        localStore.entrySet().removeIf(e -> e.getValue().expireAt < now);
    }

    private static class ExpiringValue {
        final String value;
        final long expireAt;
        ExpiringValue(String value, long expireAt) {
            this.value = value;
            this.expireAt = expireAt;
        }
    }
}
