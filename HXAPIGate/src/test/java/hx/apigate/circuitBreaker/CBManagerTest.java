package hx.apigate.circuitBreaker;

import hx.apigate.databridge.CircleBreakException;
import hx.apigate.util.RedisUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * CBManager 熔断状态机单元测试：
 *   Closed(关闭) --失败达阈值--> Open(打开) --超时--> HalfOpen(半开) --连续成功--> Closed
 *                                                            \--失败--> Open
 */
public class CBManagerTest {

    @Before
    public void setUp() {
        RedisUtil.getCircleBreakCache().clear();
    }

    @Test
    public void testInitialStateIsClosed() {
        CBManager manager = new CBManager("test-cb-1", 3, 2, 60);
        assertTrue("初始应为 Closed 状态", manager.isClosed());
        try {
            manager.getState().preMethodExecute();
        } catch (CircleBreakException e) {
            fail("Closed 状态 preMethodExecute 不应抛异常");
        }
    }

    @Test
    public void testFailureThresholdTriggersOpen() throws CircleBreakException {
        CBManager manager = new CBManager("test-cb-2", 3, 2, 60);
        // 失败 2 次仍为 Closed
        manager.getState().ActUponException();
        manager.getState().ActUponException();
        assertTrue("未达阈值前应为 Closed", manager.isClosed());
        // 第 3 次失败达到阈值 -> Open
        manager.getState().ActUponException();
        assertTrue("达到失败阈值后应切换为 Open", manager.isOpen());
    }

    @Test
    public void testOpenStateRejectsRequest() {
        CBManager manager = new CBManager("test-cb-3", 2, 2, 60);
        manager.getState().ActUponException();
        manager.getState().ActUponException();
        assertTrue(manager.isOpen());
        try {
            manager.getState().preMethodExecute();
            fail("Open 状态 preMethodExecute 应抛 CircleBreakException");
        } catch (CircleBreakException expected) {
            // 预期异常
        }
    }

    @Test
    public void testHalfOpenSuccessRecoversToClosed() throws CircleBreakException {
        CBManager manager = new CBManager("test-cb-4", 2, 2, 60);
        // 触发 Open
        manager.getState().ActUponException();
        manager.getState().ActUponException();
        assertTrue(manager.isOpen());
        // 超时后进入 HalfOpen
        manager.moveToHalfOpenState();
        assertTrue(manager.isHalfOpen());
        // 连续成功 2 次 -> Closed
        manager.getState().postMethodExecute();
        assertTrue("1 次成功未达阈值仍为 HalfOpen", manager.isHalfOpen());
        manager.getState().postMethodExecute();
        assertTrue("连续成功达阈值应恢复 Closed", manager.isClosed());
    }

    @Test
    public void testHalfOpenFailureReopens() {
        CBManager manager = new CBManager("test-cb-5", 2, 2, 60);
        manager.getState().ActUponException();
        manager.getState().ActUponException();
        manager.moveToHalfOpenState();
        assertTrue(manager.isHalfOpen());
        // HalfOpen 下失败一次立即回到 Open
        manager.getState().ActUponException();
        assertTrue("HalfOpen 失败应回到 Open", manager.isOpen());
    }

    @Test
    public void testConstructorValidatesParams() {
        try {
            new CBManager("test-cb-6", 0, 2, 60);
            fail("failureThreshold<1 应抛异常");
        } catch (RuntimeException expected) {
        }
        try {
            new CBManager("test-cb-7", 2, 0, 60);
            fail("consecutiveSuccessThreshold<1 应抛异常");
        } catch (RuntimeException expected) {
        }
        try {
            new CBManager("test-cb-8", 2, 2, 0);
            fail("timeout<1 应抛异常");
        } catch (RuntimeException expected) {
        }
    }
}
