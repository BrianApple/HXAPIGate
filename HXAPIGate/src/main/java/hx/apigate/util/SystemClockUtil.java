package hx.apigate.util;



import java.util.concurrent.Executors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 系统时间获取
 * @Description: 
 * @author  yangcheng
 * @date:   2019年3月6日
 */
public class SystemClockUtil {
	private static AtomicLong timeLongVal;
	
	private SystemClockUtil(){
		throw new AssertionError();
	}
	
	static{
		timeLongVal = new AtomicLong(System.currentTimeMillis());
		scheduleColockUpdate();
	}
	
	private static void scheduleColockUpdate(){
		ScheduledExecutorService  executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				timeLongVal.set(System.currentTimeMillis());
			}
		}, 1, 1, TimeUnit.MILLISECONDS);
	}
	public static long now(){
		
		return timeLongVal.get();
		
	}

}
