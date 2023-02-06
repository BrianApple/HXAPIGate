package hx.apigate.circuitBreaker.state;

import hx.apigate.circuitBreaker.CBManager;
import hx.apigate.util.HXAPIGateConext;
import hx.apigate.util.IgniteUtil;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

public class ClosedState extends ACBState{

	public ClosedState(CBManager manager) {
        super(manager);
        manager.resetFailureCount();
        initSchedule();
    }

    @Override
    public void ActUponException() {
        super.ActUponException();

        if (manager.failureThresholdReached()) {
            manager.moveToOpenState();
        }
    }
    public void initSchedule() {
    	TimerTask task = new TimerTask() {
			
			@Override
			public void run(Timeout timeout) throws Exception {
				CBManager managerTemp = IgniteUtil.getCircleBreakCache().get(manager.getSigKey());
				
				if(managerTemp != null && managerTemp.getState() instanceof ClosedState ) {
					manager.resetFailureCount();
					HXAPIGateConext.circleBreaktimer.newTimeout(this,20,TimeUnit.SECONDS);
				}
			}
		};
		
		HXAPIGateConext.circleBreaktimer.newTimeout(task,20,TimeUnit.SECONDS);
		
    }
}
