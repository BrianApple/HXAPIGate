package hx.apigate.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * RateLimiter 单元测试（Redis 不可用时走本地降级信号量路径）
 */
public class RateLimiterTest {

    @Before
    public void setUp() {
        // 强制走本地降级路径，测试不依赖 Redis
        RedisUtil.setRedisAvailable(false);
    }

    @Test
    public void testPermitsNonPositiveRejected() {
        assertFalse("permits<=0 应直接拒绝", RateLimiter.tryAcquire("test-invalid", 0));
        assertFalse("permits<0 应直接拒绝", RateLimiter.tryAcquire("test-invalid", -5));
    }

    @Test
    public void testLocalSemaphoreAcquireAndRelease() {
        String key = "test-sem-" + System.nanoTime();
        int permits = 3;

        // 3 个许可全部可获取
        assertTrue(RateLimiter.tryAcquire(key, permits));
        assertTrue(RateLimiter.tryAcquire(key, permits));
        assertTrue(RateLimiter.tryAcquire(key, permits));
        assertEquals("占用 3 个后计数应为 3", 3, RateLimiter.availablePermits(key));

        // 第 4 个被拒绝
        assertFalse("超出许可数应被拒绝", RateLimiter.tryAcquire(key, permits));

        // 释放一个后恢复
        RateLimiter.release(key);
        assertEquals("释放后计数应为 2", 2, RateLimiter.availablePermits(key));
        assertTrue("释放后可再次获取", RateLimiter.tryAcquire(key, permits));
    }

    @Test
    public void testLocalReleaseToZero() {
        String key = "test-zero-" + System.nanoTime();
        assertTrue(RateLimiter.tryAcquire(key, 1));
        assertEquals(1, RateLimiter.availablePermits(key));
        RateLimiter.release(key);
        assertEquals("释放到 0", 0, RateLimiter.availablePermits(key));
        // 重复 release 不应产生负数
        RateLimiter.release(key);
        assertEquals("重复 release 计数不为负", 0, RateLimiter.availablePermits(key));
    }

    @Test
    public void testKeysAreolated() {
        String keyA = "test-iso-a-" + System.nanoTime();
        String keyB = "test-iso-b-" + System.nanoTime();
        assertTrue(RateLimiter.tryAcquire(keyA, 1));
        assertFalse("keyA 已满，keyB 不应受影响", RateLimiter.tryAcquire(keyA, 1));
        assertTrue("不同 key 相互隔离", RateLimiter.tryAcquire(keyB, 1));
        RateLimiter.release(keyA);
        RateLimiter.release(keyB);
    }
}
