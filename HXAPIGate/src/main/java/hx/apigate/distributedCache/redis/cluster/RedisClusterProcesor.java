package hx.apigate.distributedCache.redis.cluster;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.base.DistributedCacheProcessor;
import hx.apigate.distributedCache.config.DistributedCachingProperties;
import hx.apigate.distributedCache.config.PropertiesFactory;
import hx.apigate.distributedCache.serialization.SerializationFactory;
import hx.apigate.distributedCache.serialization.bytes.ProtobufSerialize;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
/**
 * 分布式缓存
 * @Description: 
 * @author  yangcheng
 * @date:   2019年6月24日
 */
public class RedisClusterProcesor implements DistributedCacheProcessor {
	Logger logger = LoggerFactory.getLogger(RedisClusterProcesor.class);
	private DistributedCachingProperties distributedCachingProperties;
	private SerializationFactory serializationFactory;
	
	
	RedisClusterFactory redisClusterFactory = new RedisClusterFactory();

	private JedisCluster  jedisCluster= null;
	
	@Override
	public void start() throws Exception {
		distributedCachingProperties = PropertiesFactory.getDistributedCachingPropertiesInstance();
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(distributedCachingProperties.getRedis_cluster_maxIdle());
		config.setMaxTotal(distributedCachingProperties.getRedis_cluster_maxTotal());
		config.setMaxWaitMillis(distributedCachingProperties.getRedis_cluster_maxWaitMillis());
		config.setMinIdle(distributedCachingProperties.getRedis_cluster_minEvictableIdleTimeMillis());
		config.setTestOnBorrow(distributedCachingProperties.isRedis_cluster_testOnBorrow());
		config.setTestOnReturn(distributedCachingProperties.isRedis_cluster_testOnReturn());
		if("OFF".equals(distributedCachingProperties.getRedis_cluster_password())){
			//无密码
			jedisCluster = redisClusterFactory.getJedisClusterNoPassword(distributedCachingProperties.getRedis_cluster_host(), config, 
					distributedCachingProperties.getRedis_cluster_connectionTimeout(), distributedCachingProperties.getRedis_cluster_soTimeout(),
					distributedCachingProperties.getRedis_cluster_maxAttempts());
		}else{
			//有密码
			jedisCluster = redisClusterFactory.getJedisClusterWithPassword(distributedCachingProperties.getRedis_cluster_host(), config, 
					distributedCachingProperties.getRedis_cluster_connectionTimeout(), distributedCachingProperties.getRedis_cluster_soTimeout(),
					distributedCachingProperties.getRedis_cluster_maxAttempts(),distributedCachingProperties.getRedis_cluster_password());
		}
	}

	@Override
	public void stop() throws Exception {
		jedisCluster= null;
		redisClusterFactory.destory();
	}

	@Override
	public void expireString(String key, int timeout) {
		jedisCluster.expire(key, timeout);
	}

	@Override
	public void setString(String key, String value) {
		jedisCluster.set(key, value);
		
	}

	@Override
	public String getString(String key) {
		return jedisCluster.get(key);
	}

	@Override
	public void setStringObj(String key, Object value) {
		try {
			/**
			 * 根据返回值类型决定何种方式写数据
			 */
			Object val = serializationFactory.getSerializationInstance().SerializeObject(value);
			if(val instanceof String){
				jedisCluster.set(key, (String)val);
			}else{
				jedisCluster.set(key.getBytes(), (byte[])val);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("数据写入缓存失败", e);
		}
		
	}

	@Override
	public void setStringObjWithExpire(String key, Object value, int second) {
		try {
			Object val = serializationFactory.getSerializationInstance().SerializeObject(value);
			if(val instanceof String){
				jedisCluster.setex(key, second, (String)val);
			}else{
				jedisCluster.setex(key.getBytes(), second, (byte[])val);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("数据写入缓存失败", e);
		}
	}
	
	
	@Override
	public Object getStringObj(String key,String clazz) {
		try {
			
			if(serializationFactory.getSerializationInstance() instanceof ProtobufSerialize){
				
				byte[] data = jedisCluster.get(key.getBytes());
				return serializationFactory.getSerializationInstance().deSerializeObject(data,clazz);
			}else{
				String data = jedisCluster.get(key);
				return serializationFactory.getSerializationInstance().deSerializeObject(data,clazz);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("数据从缓存中读取失败", e);
		}
		return null;
	}

	@Override
	public void setStringWithExpire(String key, String value, int expire) {
		jedisCluster.setex(key, expire, value);
	}

	@Override
	public void setHashString(String key, String field, String value) {
		jedisCluster.hset(key, field, value);
	}

	@Override
	public String getHashString(String key, String field) {
		return jedisCluster.hget(key, field);
	}

	@Override
	public void setHashObj(String key, String field,  Object value) {
		try {
			Object val = serializationFactory.getSerializationInstance().SerializeObject(value);
			if(val instanceof String){
				jedisCluster.hset(key, field, (String)val);
			}else{
				jedisCluster.hset(key.getBytes(),field.getBytes(), (byte[])val);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("数据写入缓存失败", e);
		}
	}

	@Override
	public Object getHashObj(String key, String field,String clazz) {
		try {
			
			if(serializationFactory.getSerializationInstance() instanceof ProtobufSerialize){
				
				byte[] data = jedisCluster.hget(key.getBytes(),field.getBytes());
				return serializationFactory.getSerializationInstance().deSerializeObject(data,clazz);
			}else{
				String data = jedisCluster.hget(key,field);
				return serializationFactory.getSerializationInstance().deSerializeObject(data,clazz);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("数据从缓存中读取失败", e);
		}
		return null;
	}

	@Override
	public void setBit(String key, long offset, boolean value) {
		jedisCluster.setbit(key, offset, value);
	}

	@Override
	public boolean getBit(String key, long offset) {
		return jedisCluster.getbit(key, offset);
	}

	@Override
	public boolean exist(String key) {
		return jedisCluster.exists(key);
	}

	@Override
	public boolean exist(byte[] bytesKey) {
		return jedisCluster.exists(bytesKey);
	}

	@Override
	public Object excuteluaScript(String luaScriptStr,int keyCount,String... params ) {
		return jedisCluster.eval(luaScriptStr, keyCount, params);
	}

}
