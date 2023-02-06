package hx.apigate.circuitBreaker.state;

import hx.apigate.circuitBreaker.CBManager;

public class HalfOpenState extends ACBState {

    public HalfOpenState(CBManager manager) {
        super(manager);

        manager.resetConsecutiveSuccessCount();
    }

    @Override
    public void ActUponException() {
        super.ActUponException();

        manager.moveToOpenState();
    }

    @Override
    public void postMethodExecute() {
        super.postMethodExecute();

        if (manager.consecutiveSuccessThresholdReached()) {
            manager.moveToClosedState();
        }
    }
}