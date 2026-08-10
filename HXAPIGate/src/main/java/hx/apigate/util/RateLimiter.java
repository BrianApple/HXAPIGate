package hx.apigate.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.base.DistributedCacheProcessor;
import hx.apigate.distributedCache.DefualtDistributedCacheManager;

/**
 * 分布式限流工具（替代原 IgniteSemaphore）
 *
 * 原实现：IgniteSemaphore（分布式信号量，tryAcquire/release 配对）
 * 替代方案：Redis Lua 计数信号量
 *  - key: HXAPI:LIMIT:{routeKey}
 *  - tryAcquire: INCR key，若计数 > permits 则 DECR 回滚返回失败；首次创建时设置 TTL 兜底防泄漏
 *  - release:    DECR key
 *
 * Redis 不可用时自动降级为本地 AtomicInteger 信号量（单节点限流，业界可接受）。
 * 使用方无需感知降级，API 与 IgniteSemaphore 保持语义一致。
 *
 * @author Hermes (Redis 替代 Ignite/ZK 改造)
 */
public class RateLimiter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiter.class);

    /** 限流 key 前缀（与平台侧 RedisConstance.LIMIT_PREFIX 保持一致） */
    public static final String LIMIT_PREFIX = "HXAPI:LIMIT:";

    /** 信号量泄漏兜底 TTL（秒）：acquire 后 key 存在超过该时间自动过期，防止 release 丢失导致永久占满 */
    private static final int SEMAPHORE_TTL_SECONDS = 300;

    /**
     * tryAcquire 的 Lua 脚本：
     * KEYS[1] = 限流 key
     * ARGV[1] = 最大并发数 permits
     * 返回 1 成功 / 0 失败
     */
    private static final String TRY_ACQUIRE_LUA =
            "local c = redis.call('INCR', KEYS[1]) " +
            "if c == 1 then redis.call('EXPIRE', KEYS[1], ARGV[2]) end " +
            "if c > tonumber(ARGV[1]) then redis.call('DECR', KEYS[1]) return 0 else return 1 end";

    /** release 的 Lua 脚本：DECR，若减到 0 删除 key */
    private static final String RELEASE_LUA =
            "local c = redis.call('DECR', KEYS[1]) " +
            "if c <= 0 then redis.call('DEL', KEYS[1]) end " +
            "return c";

    /** Redis 不可用时的本地降级信号量 */
    private static final Map<String, AtomicInteger> localSemaphores = new ConcurrentHashMap<>();

    /**
     * 尝试获取信号量
     * @param key     限流 key（不含前缀）
     * @param permits 最大并发数
     * @return true=获取成功
     */
    public static boolean tryAcquire(String key, int permits) {
        if (permits <= 0) {
            return false;
        }
        if (RedisUtil.isRedisAvailable()) {
            try {
                DistributedCacheProcessor processor = DefualtDistributedCacheManager.getInstance();
                Object ret = processor.excuteluaScript(TRY_ACQUIRE_LUA, 1,
                        LIMIT_PREFIX + key, String.valueOf(permits), String.valueOf(SEMAPHORE_TTL_SECONDS));
                return ret != null && "1".equals(String.valueOf(ret));
            } catch (Exception e) {
                LOGGER.warn("Redis 限流失败，降级本地信号量: {}", e.getMessage());
                RedisUtil.setRedisAvailable(false);
                // 降级到本地
            }
        }
        return localTryAcquire(key, permits);
    }

    /**
     * 释放信号量
     * @param key 限流 key（不含前缀）
     */
    public static void release(String key) {
        if (RedisUtil.isRedisAvailable()) {
            try {
                DistributedCacheProcessor processor = DefualtDistributedCacheManager.getInstance();
                processor.excuteluaScript(RELEASE_LUA, 1, LIMIT_PREFIX + key);
                return;
            } catch (Exception e) {
                LOGGER.warn("Redis 限流释放失败，降级本地信号量: {}", e.getMessage());
                RedisUtil.setRedisAvailable(false);
            }
        }
        localRelease(key);
    }

    /**
     * 当前可用许可数（用于日志/监控）
     * @param key 限流 key（不含前缀）
     */
    public static int availablePermits(String key) {
        if (RedisUtil.isRedisAvailable()) {
            try {
                DistributedCacheProcessor processor = DefualtDistributedCacheManager.getInstance();
                Object val = processor.getString(LIMIT_PREFIX + key);
                if (val == null) {
                    return 0;
                }
                return Integer.parseInt(String.valueOf(val));
            } catch (Exception e) {
                LOGGER.warn("Redis 读取限流计数失败: {}", e.getMessage());
            }
        }
        AtomicInteger counter = localSemaphores.get(key);
        return counter == null ? 0 : counter.get();
    }

    /**
     * 本地信号量 tryAcquire
     */
    private static boolean localTryAcquire(String key, int permits) {
        AtomicInteger counter = localSemaphores.computeIfAbsent(key, k -> new AtomicInteger(0));
        int current;
        while (true) {
            current = counter.get();
            if (current >= permits) {
                return false;
            }
            if (counter.compareAndSet(current, current + 1)) {
                return true;
            }
        }
    }

    /**
     * 本地信号量 release
     */
    private static void localRelease(String key) {
        AtomicInteger counter = localSemaphores.get(key);
        if (counter != null) {
            int current;
            while (true) {
                current = counter.get();
                if (current <= 0) {
                    break;
                }
                if (counter.compareAndSet(current, current - 1)) {
                    break;
                }
            }
        }
    }
}
