package hx.apigate.distributedCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.base.DistributedCacheProcessor;
import hx.apigate.base.IProcessor;
import hx.apigate.distributedCache.config.PropertiesFactory;
import hx.apigate.distributedCache.redis.cluster.RedisClusterProcesor;
import hx.apigate.distributedCache.redis.single.RedisSingleProcessor;
import hx.apigate.util.MixAll;

/**
 * 分布式缓存入口类
 * @Description: 
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotp.com</p>
 * @author  yangcheng
 * @date:   2019年6月24日
 */
public class DefualtDistributedCacheManager implements IProcessor{
	Logger logger = LoggerFactory.getLogger(DefualtDistributedCacheManager.class);
	private static DistributedCacheProcessor distributedCacheProcessor;
	
	private String oldName = null;
	public void start() throws Exception {
		if("single".equals(PropertiesFactory.getDistributedCachingPropertiesInstance().getDefualtCacheProcessorName())) {
			distributedCacheProcessor = new RedisSingleProcessor();
		}else {
			distributedCacheProcessor = new RedisClusterProcesor();
			
		}
		distributedCacheProcessor.start();
		logger.info(MixAll.LOG_INFO_PRIFEX+"DistributedCacheManager is started ! DefualtCacheProcessorName is "+PropertiesFactory.getDistributedCachingPropertiesInstance().getDefualtCacheProcessorName());
	}

	public void stop() throws Exception {
		distributedCacheProcessor.stop();
	}

	public static DistributedCacheProcessor getInstance() {
		if(DefualtDistributedCacheManager.distributedCacheProcessor == null) {
			synchronized (DefualtDistributedCacheManager.class) {
				if(DefualtDistributedCacheManager.distributedCacheProcessor == null) {
					DefualtDistributedCacheManager.distributedCacheProcessor = new RedisSingleProcessor();
					try {
						DefualtDistributedCacheManager.distributedCacheProcessor.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return DefualtDistributedCacheManager.distributedCacheProcessor;
		
	}

	@Override
	public int getStartOrder() {
		return 2;
	}

	@Override
	public int getStopOrder() {
		return 2;
	}
	
}
