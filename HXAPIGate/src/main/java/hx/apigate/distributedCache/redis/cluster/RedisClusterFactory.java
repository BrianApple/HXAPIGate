package hx.apigate.distributedCache.redis.cluster;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import hx.apigate.distributedCache.config.DistributedCachingProperties.RedisNodes;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @Description: 
 * @author  yangcheng
 * @date:   2019年6月24日
 */
public class RedisClusterFactory {
	
	private JedisCluster jedisCluster;
	
	/**
	 * 将 JedisPoolConfig 适配为 JedisCluster 构造器要求的 GenericObjectPoolConfig
	 * （jedis 5.x 中 JedisPoolConfig 泛型为 Jedis，构造器要求 GenericObjectPoolConfig<Connection>）
	 */
	@SuppressWarnings("unchecked")
	private GenericObjectPoolConfig<redis.clients.jedis.Connection> adaptPoolConfig(JedisPoolConfig config) {
		GenericObjectPoolConfig<redis.clients.jedis.Connection> poolConfig = new GenericObjectPoolConfig<>();
		poolConfig.setMaxTotal(config.getMaxTotal());
		poolConfig.setMaxIdle(config.getMaxIdle());
		poolConfig.setMinIdle(config.getMinIdle());
		poolConfig.setMaxWait(config.getMaxWaitDuration());
		poolConfig.setTestOnBorrow(config.getTestOnBorrow());
		poolConfig.setTestOnReturn(config.getTestOnReturn());
		poolConfig.setTestWhileIdle(config.getTestWhileIdle());
		return poolConfig;
	}
	/**
	 * 有密码
	 * @param nodes
	 * @param config
	 * @param connectionTimeout
	 * @param soTimeout
	 * @param maxAttempts
	 * @return
	 */
	public JedisCluster getJedisClusterNoPassword(List<RedisNodes> nodes,JedisPoolConfig config,int connectionTimeout, int soTimeout, int maxAttempts){
		if(jedisCluster == null){
			synchronized (this) {
				if(jedisCluster == null){
					Set<HostAndPort> nodesSet = new HashSet<HostAndPort>();
					for (RedisNodes node : nodes) {
						
						nodesSet.add(new HostAndPort(node.getIp(),node.getPort()));
					}
					jedisCluster = new JedisCluster(nodesSet, connectionTimeout, soTimeout, maxAttempts, "default", null, adaptPoolConfig(config));
				}
			}
		}
		return jedisCluster;
	}
	/**
	 * 无密码
	 * @param nodes
	 * @param config
	 * @param connectionTimeout
	 * @param soTimeout
	 * @param maxAttempts
	 * @param password
	 * @return
	 */
	public JedisCluster getJedisClusterWithPassword(List<RedisNodes> nodes,JedisPoolConfig config,int connectionTimeout, int soTimeout, int maxAttempts,String password){
		
		if(jedisCluster == null){
			synchronized (this) {
				if(jedisCluster == null){
					Set<HostAndPort> nodesSet = new HashSet<HostAndPort>();
					for (RedisNodes node : nodes) {
						
						nodesSet.add(new HostAndPort(node.getIp(),node.getPort()));
					}
					jedisCluster = new JedisCluster(nodesSet, connectionTimeout, soTimeout, maxAttempts, "default", password, adaptPoolConfig(config));
				}
			}
		}
		return jedisCluster;
		
	}

	/**
	 * 销毁
	 * @throws IOException
	 */
	public void destory() throws IOException{
		if(jedisCluster != null){
			try{
				jedisCluster.close();
			}finally{
				jedisCluster = null;
			}
			
			
		}
	}
}
