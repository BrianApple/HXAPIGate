package hx.apigate.hxqueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

/**
 * <p>Description: 浩心消息队列</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年12月9日
　 * @version 1.0
 */
@Component("HXMQ")
public class HXMQ {
	ExecutorService executorService;
	public HXMQ() {
		super();
		executorService = Executors.newFixedThreadPool(HXBlockedQueueConfig.threadSize, 
				new ThreadFactoryImpl(HXBlockedQueueConfig.threadPoolNamePrefix, true));
	}

	public void sendRunnable(Runnable obj) {
		this.executorService.execute(obj);
	}
	

}
