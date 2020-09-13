package hx.apigate.distributedCache.config;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


/**
 * 分布式缓存
 * @Description: 
 * @author  yangcheng
 * @date:   2019年6月23日
 */
public class DistributedCachingProperties implements Serializable{
	/**
	 * single or cluster
	 */
	private String defualtCacheProcessorName = "single";//默认使用cache得name  
	/**
	 * 取值为：fastJsonSerialize或protobufSerialize
	 */
	private String BASIC_SERIALIZATION = "fastJsonSerialize";//当前使用的序列化器
	
	//单节点
	private RedisNodes redis_host = new RedisNodes("127.0.0.1",16379);//节点
	private String redis_password = "OFF";
	private int redis_timeout = 10000;
	private int redis_maxIdle =  299;
	private int redis_maxTotal = 1000;
	private int redis_maxWaitMillis = 1000;
	private int redis_minEvictableIdleTimeMillis  = 300000;
	private boolean redis_testOnBorrow = true;
	private boolean redis_testOnReturn = true;
	
	//集群节点
	private List<RedisNodes> redis_cluster_host = Arrays.asList(new RedisNodes("127.0.0.1",6379));//节点
	private String redis_cluster_password = "root";
	private int redis_cluster_timeout = 10000;
	private int redis_cluster_maxIdle  = 299;
	private int redis_cluster_maxTotal = 1000;
	private int redis_cluster_maxWaitMillis = 1000;
	private int redis_cluster_minEvictableIdleTimeMillis = 300000;
	private int redis_cluster_connectionTimeout = 5000;//连接超时时间
	private int redis_cluster_soTimeout = 5000;
	private int redis_cluster_maxAttempts = 10;//连接最大重试次数
	private boolean redis_cluster_testOnBorrow = true;
	private boolean redis_cluster_testOnReturn = true;
	
	
	public RedisNodes getRedis_host() {
		return redis_host;
	}

	public String getRedis_password() {
		return redis_password;
	}

	public int getRedis_timeout() {
		return redis_timeout;
	}

	public int getRedis_maxIdle() {
		return redis_maxIdle;
	}

	public int getRedis_maxTotal() {
		return redis_maxTotal;
	}

	public int getRedis_maxWaitMillis() {
		return redis_maxWaitMillis;
	}

	public int getRedis_minEvictableIdleTimeMillis() {
		return redis_minEvictableIdleTimeMillis;
	}

	public boolean isRedis_testOnBorrow() {
		return redis_testOnBorrow;
	}

	public boolean isRedis_testOnReturn() {
		return redis_testOnReturn;
	}

	public String getDefualtCacheProcessorName() {
		return defualtCacheProcessorName;
	}

	public List<RedisNodes> getRedis_cluster_host() {
		return redis_cluster_host;
	}


	public String getRedis_cluster_password() {
		return redis_cluster_password;
	}

	public int getRedis_cluster_timeout() {
		return redis_cluster_timeout;
	}

	public int getRedis_cluster_maxIdle() {
		return redis_cluster_maxIdle;
	}

	public int getRedis_cluster_maxTotal() {
		return redis_cluster_maxTotal;
	}

	public int getRedis_cluster_maxWaitMillis() {
		return redis_cluster_maxWaitMillis;
	}

	public int getRedis_cluster_minEvictableIdleTimeMillis() {
		return redis_cluster_minEvictableIdleTimeMillis;
	}

	public boolean isRedis_cluster_testOnBorrow() {
		return redis_cluster_testOnBorrow;
	}


	public boolean isRedis_cluster_testOnReturn() {
		return redis_cluster_testOnReturn;
	}

	public int getRedis_cluster_connectionTimeout() {
		return redis_cluster_connectionTimeout;
	}

	public int getRedis_cluster_soTimeout() {
		return redis_cluster_soTimeout;
	}

	public int getRedis_cluster_maxAttempts() {
		return redis_cluster_maxAttempts;
	}

	public String getBASIC_SERIALIZATION() {
		return BASIC_SERIALIZATION;
	}

	public void setBASIC_SERIALIZATION(String bASIC_SERIALIZATION) {
		BASIC_SERIALIZATION = bASIC_SERIALIZATION;
	}

	public static class RedisNodes implements Serializable{

	    private String ip;
	    private int port;
	    
	    public RedisNodes() {
			super();
		}
		public RedisNodes(String ip, int port) {
			super();
			this.ip = ip;
			this.port = port;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}

		@Override
	    public String toString() {
	      return "redisNodes{" +
	          "ip='" + ip + '\'' +
	          ", port=" + port +
	          '}';
	    }
	}
	
}
