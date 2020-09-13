package hx.apigate.hxqueue;

import org.springframework.stereotype.Component;

/**
 * <p>Description: 浩心消息队列</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author hjj
　 * @date 2019年12月9日
　 * @version 1.0
 */
public class HXUnlockedMQ {
	private static FixThreadWorkerPool fixThreadWorkerPool;
	private  HXUnlockedMQ() {
	}
	static {
		fixThreadWorkerPool = new FixThreadWorkerPool(HXUnblockedQueueConfig.threadSize,HXUnblockedQueueConfig.threadPoolNamePrefix);
	}

	public static void sendRunnable(Runnable obj) {
		fixThreadWorkerPool.getFixThreadPool().execute(obj);
	}
	

}
