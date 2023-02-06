package hx.apigate.circuitBreaker.state;


import hx.apigate.circuitBreaker.CBManager;
import hx.apigate.util.HXAPIGateConext;
import io.netty.util.Timeout;

import java.util.concurrent.TimeUnit;

public class OpenState extends ACBState {

    public OpenState(CBManager manager) {
        super(manager);
        HXAPIGateConext.circleBreaktimer.newTimeout(new io.netty.util.TimerTask() {
			@Override
			public void run(Timeout timeout) throws Exception {
				timeoutHasBeenReached();
			}
		}, 60, TimeUnit.SECONDS);
    }

    @Override
    public void preMethodExecute() {
        super.preMethodExecute();
        throw new RuntimeException("Service is broken. Please try again later");
    }

    private void timeoutHasBeenReached(){
        manager.moveToHalfOpenState();
    }
}