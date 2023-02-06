package hx.apigate.circuitBreaker;

import hx.apigate.circuitBreaker.state.ACBState;
import hx.apigate.circuitBreaker.state.ClosedState;
import hx.apigate.circuitBreaker.state.HalfOpenState;
import hx.apigate.circuitBreaker.state.OpenState;
import hx.apigate.util.IgniteUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class CBManager {
	private volatile String sigKey;
    public volatile AtomicInteger failureCount;
    public volatile AtomicInteger consecutiveSuccessCount;
    public volatile AtomicInteger failureThreshold;
    public volatile AtomicInteger consecutiveSuccessThreshold;
    public volatile long timeout;
    private volatile ACBState state;

    public CBManager(String sigKey , int failureThreshold, int consecutiveSuccessThreshold, int timeout)
    {
        if (failureThreshold < 1 || consecutiveSuccessThreshold < 1) {
            throw new RuntimeException("熔断器闭合状态的最大失败次数和半熔断状态的最大连续成功次数必须大于0！");
        }
        if (timeout < 1) {
            throw new RuntimeException("熔断器断开状态超时时间必须大于0！");
        }
        this.failureThreshold = new AtomicInteger(failureThreshold);
        this.consecutiveSuccessThreshold = new AtomicInteger(consecutiveSuccessThreshold) ;
        this.timeout = timeout;
        this.sigKey = sigKey;
        failureCount = new AtomicInteger(0);
        consecutiveSuccessCount = new AtomicInteger(0);
        moveToClosedState();
    }

    public void close() {
        moveToClosedState();
    }

    public void open() {
        moveToOpenState();
    }
    
    public void moveToClosedState() {
        state = new ClosedState(this);
        load2Cache();
    }

    public void moveToOpenState() {
        state = new OpenState(this);
        load2Cache();
    }

    public void moveToHalfOpenState() {
        state = new HalfOpenState(this);
        load2Cache();
    }
    
    
    public boolean isClosed() {
        return state instanceof ClosedState;
    }

    public boolean isOpen() {
        return state instanceof OpenState;
    }

    public boolean isHalfOpen() {
        return state instanceof  HalfOpenState;
    }

    public void increaseFailureCount() {
        failureCount.incrementAndGet();
        resetConsecutiveSuccessCount();
        load2Cache();
    }

    public void resetFailureCount() {
        failureCount.set(0);
        load2Cache();
    }

    public boolean failureThresholdReached() {
        return failureCount.get() >= failureThreshold.get();
    }

    public void increaseSuccessCount() {
        consecutiveSuccessCount.incrementAndGet();
        load2Cache();
    }

    public void resetConsecutiveSuccessCount() {
        consecutiveSuccessCount.set(0);
    }

    public boolean consecutiveSuccessThresholdReached() {
        return consecutiveSuccessCount.get() >= consecutiveSuccessThreshold.get();
    }

	public ACBState getState() {
		return state;
	}

	public void setState(ACBState state) {
		this.state = state;
	}

	public void load2Cache() {
		IgniteUtil.getCircleBreakCache().putAsync(sigKey, this);
	}

	public String getSigKey() {
		return sigKey;
	}
    

}
