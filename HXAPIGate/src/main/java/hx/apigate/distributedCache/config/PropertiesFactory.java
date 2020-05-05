package hx.apigate.distributedCache.config;

import hx.apigate.distributedCache.config.DistributedCachingProperties;

/**
 * 配置文件工厂
 * @Description: 
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.xianglong.work</p>
 * @author yangcheng
 * @date:   2019年6月25日
 */
public class PropertiesFactory {
	
	public static DistributedCachingProperties distributedCachingProperties = null;
	
	public static  DistributedCachingProperties getDistributedCachingPropertiesInstance(){
		if(distributedCachingProperties == null){
			synchronized (PropertiesFactory.class) {
				if(distributedCachingProperties == null){
					distributedCachingProperties = new DistributedCachingProperties();
				}
			}
		}
		return distributedCachingProperties;
	}
	
	
	

}
