package hx.apigate.distributedCache.redis.single;



import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.JedisPool;

/**
 * 自定义redis连接池的工厂类
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.xianglong.work</p>
 * @author yangcheng  
 * @date 2019年6月24日  
 * @version V1.0
 */
public class DogJedisPoolFactory {
	
	private DogJedisPool myJedisPool = null;
	
	public JedisPool getJedisClientNoPassword(GenericObjectPoolConfig poolConfig,String ip,int port,int timeout) {
		if(myJedisPool == null){
			synchronized (this) {
				if(myJedisPool == null){
					myJedisPool = new DogJedisPool(poolConfig, ip, port,timeout);
				}
			}
		}
		
		return myJedisPool;
	}
	
	public JedisPool getJedisClientWithPassword(GenericObjectPoolConfig poolConfig,String ip,int port,int timeout,String password) {
		if(myJedisPool == null){
			synchronized (this) {
				if(myJedisPool == null){
					myJedisPool = new DogJedisPool(poolConfig, ip, port,timeout,password);
				}
			}
		}
		
		return myJedisPool;
	}
	/**
	 * 销毁
	 */
	public void distroy(){
		try{
			if(myJedisPool != null){
				myJedisPool.destroy();
			}
		}finally{
			myJedisPool = null;
		}
		
	}
	

}
