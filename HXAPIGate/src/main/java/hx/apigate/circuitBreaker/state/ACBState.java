package hx.apigate.circuitBreaker.state;

import hx.apigate.circuitBreaker.CBManager;
import hx.apigate.databridge.CircleBreakException;

public abstract class ACBState {
    protected CBManager manager;

    public ACBState(CBManager manager) {
        this.manager = manager;
    }

    /**
     * 调用方法之前处理的操作
     */
    public void preMethodExecute() throws CircleBreakException{
        if (manager.isOpen()) {
            throw new CircleBreakException("服务已熔断，请稍等重试！");
        }
    }

    /**
     * 方法调用成功之后的操作
     */
    public void postMethodExecute() {
        manager.increaseSuccessCount();
    }

    /**
     * 方法调用发生异常操作后的操作,即调用失败
     */
    public void ActUponException() {
        manager.increaseFailureCount();
        
    }
}
