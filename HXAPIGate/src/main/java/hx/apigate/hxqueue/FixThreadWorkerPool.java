package hx.apigate.hxqueue;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 自定义workerPool
 * @Description:    
 * @author: hjj    
 * @date:   2019年8月27日
 */
public class FixThreadWorkerPool{
	private AtomicInteger index ;
	private int poolSize;
	private final String threadNamePrefix;
	private List<ExecutorService> workerCache;
	public FixThreadWorkerPool(int poolSize,String threadNamePrefix) {
		super();
		this.poolSize = poolSize > 0 ? poolSize : 2;
		this.threadNamePrefix = threadNamePrefix;
		initEven();
	}
	
	private void initEven(){
		index = new AtomicInteger(0);
		workerCache = new CopyOnWriteArrayList<>();
		for(int i = 0 ; i < poolSize ; i++){
			workerCache.add(Executors.newFixedThreadPool(1, new ThreadFactoryImpl(threadNamePrefix+i+"_", false)));
		}
	}
	
	public ExecutorService getFixThreadPool(){
		if(poolSize > 0){
			int nextIndex = index.addAndGet(1) % poolSize;
			index.set(nextIndex);
			return workerCache.get(nextIndex);
		}
		return null;
	}
	
	public void shutdownGracefully(){
		for (ExecutorService fixThreadPool : workerCache) {
			fixThreadPool.shutdown();
			try {
				fixThreadPool.awaitTermination(200, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				fixThreadPool.shutdownNow();
			}
		}
	}
	



}
