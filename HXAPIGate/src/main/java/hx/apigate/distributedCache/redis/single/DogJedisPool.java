package hx.apigate.distributedCache.redis.single;




import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

/**
 * 自定义redis连接池
 * @author yangcheng
 * @date 2019年6月24日  
 * @version V1.0
 */
public class DogJedisPool extends JedisPool{

	public DogJedisPool(GenericObjectPoolConfig poolConfig, String host, int port,int timeout) {
		super(poolConfig, host, port,timeout);
	}
	
	public DogJedisPool(GenericObjectPoolConfig poolConfig,String host,int port,int timeout,String password){
		super(poolConfig, host, port, timeout, password);
	}
	
	

}
