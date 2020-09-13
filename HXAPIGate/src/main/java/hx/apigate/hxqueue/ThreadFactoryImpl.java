package hx.apigate.hxqueue;



import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 
 * <p>Description: 自定义的ThreadFactory实现类</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author yangcheng
 * @date 2020年9月13日
 * @version 1.0
 */
public class ThreadFactoryImpl implements ThreadFactory{
	AtomicInteger index = new AtomicInteger(0);
	private String threadNamePrefix;
	private boolean isDaemon;
	
	public ThreadFactoryImpl(String threadNamePrefix,boolean isDaemon) {
		super();
		this.threadNamePrefix = threadNamePrefix;
		this.isDaemon = isDaemon;
	}
	
	
	@Override
	public Thread newThread(Runnable r) {
		
		Thread thread =  new Thread(r,threadNamePrefix+index.addAndGet(1));
		thread.setDaemon(isDaemon);
		return thread;
		
	}
	
	

}
