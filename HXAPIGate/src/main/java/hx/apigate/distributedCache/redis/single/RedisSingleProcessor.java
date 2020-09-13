package hx.apigate.distributedCache.redis.single;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.base.DistributedCacheProcessor;
import hx.apigate.distributedCache.config.DistributedCachingProperties;
import hx.apigate.distributedCache.config.PropertiesFactory;
import hx.apigate.distributedCache.config.DistributedCachingProperties.RedisNodes;
import hx.apigate.distributedCache.serialization.SerializationFactory;
import hx.apigate.distributedCache.serialization.bytes.ProtobufSerialize;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * 单机版redis
 * @Description: 
 * @author  yangcheng
 * @date:   2019年6月24日
 */
public class RedisSingleProcessor implements DistributedCacheProcessor {
	Logger logger = LoggerFactory.getLogger(RedisSingleProcessor.class);
	private DistributedCachingProperties distributedCachingProperties;
	private SerializationFactory serializationFactory;//序列化工厂类
	
	
	DogJedisPoolFactory myJedisPoolFactory = new DogJedisPoolFactory();
	
	private JedisPool jedisPool;
	@Override
	public void start() throws Exception {
		distributedCachingProperties = PropertiesFactory.getDistributedCachingPropertiesInstance();
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(distributedCachingProperties.getRedis_maxIdle());
		config.setMaxTotal(distributedCachingProperties.getRedis_maxTotal());
		config.setMaxWaitMillis(distributedCachingProperties.getRedis_maxWaitMillis());
		config.setMinIdle(distributedCachingProperties.getRedis_minEvictableIdleTimeMillis());
		config.setTestOnBorrow(distributedCachingProperties.isRedis_testOnBorrow());
		config.setTestOnReturn(distributedCachingProperties.isRedis_testOnReturn());
		
		if("OFF".equals(distributedCachingProperties.getRedis_password())){
			RedisNodes nod = distributedCachingProperties.getRedis_host();
			jedisPool = myJedisPoolFactory.getJedisClientNoPassword(config, nod.getIp(), nod.getPort(), distributedCachingProperties.getRedis_timeout());
			
		}else{
			RedisNodes node = distributedCachingProperties.getRedis_host();
			jedisPool = myJedisPoolFactory.getJedisClientWithPassword(config, node.getIp(), node.getPort(), distributedCachingProperties.getRedis_timeout(),distributedCachingProperties.getRedis_password());
		}
	}

	@Override
	public void stop() throws Exception {
		jedisPool=null;
		myJedisPoolFactory.distroy();
	}

	@Override
	public void expireString(String key, int timeout) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.expire(key, timeout);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void setString(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String getString(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void setStringObj(String key, Object value) {
		Jedis jedis = null;
		try {
			Object val = serializationFactory.getSerializationInstance().SerializeObject(value);
			
			if(val instanceof String){
				jedis = jedisPool.getResource();
				jedis.set(key, (String)val);
			}else{
				jedis = jedisPool.getResource();
				jedis.set(key.getBytes(), (byte[])val);
			}
		} catch (Exception e) {
			logger.error("数据写入缓存失败", e);
			throw new RuntimeException("数据写入缓存失败",e);
		}finally {
			if(jedis != null) {
				jedis.close();
			}
		}
		
		
	}
	
	@Override
	public void setStringObjWithExpire(String key, Object value, int second) {
		Jedis jedis = null;
		try {
			Object val = serializationFactory.getSerializationInstance().SerializeObject(value);
			if(val instanceof String){
				jedis = jedisPool.getResource();
				jedis.setex(key, second, (String)val);
			}else{
				jedis = jedisPool.getResource();
				jedis.setex(key.getBytes(), second, (byte[])val);
			}
		} catch (Exception e) {
			logger.error("数据写入缓存失败", e);
			throw new RuntimeException("数据写入缓存失败",e);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	

	@Override
	public Object getStringObj(String key,String clazz) {
		Jedis jedis = null;
		try {
			if(serializationFactory.getSerializationInstance() instanceof ProtobufSerialize){
				jedis = jedisPool.getResource();
				byte[] data = jedis.get(key.getBytes());
				return serializationFactory.getSerializationInstance().deSerializeObject(data,clazz);
			}else{
				jedis = jedisPool.getResource();
				String data = jedis.get(key);
				return serializationFactory.getSerializationInstance().deSerializeObject(data,clazz);
			}
			
		} catch (Exception e) {
			logger.error("数据从缓存中读取失败", e);
			throw new RuntimeException("数据写入缓存失败",e);
		}finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void setStringWithExpire(String key, String value, int expire) {
		Jedis jedis = null;
		try {
			jedis =jedisPool.getResource();
			jedis.setex(key, expire, value);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
		
		
	}

	@Override
	public void setHashString(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.hset(key, field, value);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public String getHashString(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hget(key, field);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void setHashObj(String key, String field, Object value) {
		Jedis jedis = null;
		try {
			Object val = serializationFactory.getSerializationInstance().SerializeObject(value);
			jedis = jedisPool.getResource();
			if(val instanceof String){
				jedis.hset(key, field, (String)val);
			}else{
				jedis.hset(key.getBytes(),field.getBytes(), (byte[])val);
			}
		} catch (Exception e) {
			logger.error("数据写入缓存失败", e);
			throw new RuntimeException("数据写入缓存失败",e);
		}finally {
			if(jedis != null) {
				jedis.close();
			}
		}
		
	}

	@Override
	public Object getHashObj(String key, String field,String clazz) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if(serializationFactory.getSerializationInstance() instanceof ProtobufSerialize){
				byte[] data = jedis.hget(key.getBytes(),field.getBytes());
				return serializationFactory.getSerializationInstance().deSerializeObject(data,clazz);
			}else{
				String data = jedis.hget(key,field);
				return serializationFactory.getSerializationInstance().deSerializeObject(data,clazz);
			}
			
		} catch (Exception e) {
			logger.error("数据从缓存中读取失败", e);
			throw new RuntimeException("数据写入缓存失败",e);
		}finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void setBit(String key, long offset, boolean value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.setbit(key, offset, value);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public boolean getBit(String key, long offset) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getbit(key, offset);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public boolean exist(String key) {
		
		
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(key);
		}catch (Exception e) {
			logger.error("数据从缓存中读取失败", e);
			throw new RuntimeException("数据写入缓存失败",e);
		}finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public boolean exist(byte[] bytesKey) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(bytesKey);
		}catch (Exception e) {
			logger.error("数据从缓存中读取失败", e);
			throw new RuntimeException("数据写入缓存失败",e);
		}finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public Object excuteluaScript(String luaScriptStr,int keyCount,String... params ) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.eval(luaScriptStr, keyCount, params);
		}catch (Exception e) {
			logger.error("数据从缓存中读取失败", e);
			throw new RuntimeException("数据写入缓存失败",e);
		}finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

}
