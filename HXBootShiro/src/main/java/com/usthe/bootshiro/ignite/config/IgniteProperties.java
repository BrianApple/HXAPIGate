package com.usthe.bootshiro.ignite.config;



/**
 * ignite属性配置
 */
public class IgniteProperties {
	
	/**
	 * 持久化缓存内存初始化大小(MB), 默认值: 64
	 */
	private int persistenceInitialSize = 64;

	/**
	 * 持久化缓存占用内存最大值(MB), 默认值: 128
	 */
	private int persistenceMaxSize = 128;

	/**
	 * 持久化磁盘存储路径
	 */
	private String persistenceStorePath ;

	/**
	 * 非持久化缓存内存初始化大小(MB), 默认值: 64
	 */
	private int NotPersistenceInitialSize = 64;

	/**
	 * 非持久化缓存占用内存最大值(MB), 默认值: 128
	 */
	private int NotPersistenceMaxSize = 128;
	
	/**
	 * Ignite集群节点发现方式：multicastIpFinder(组播)  vmIpFinder(静态ip，可设定端口范围)  zooKeeperIpFinder(通过zk；zk服务地址通过启动参数-z指定)
	 * 默认为zooKeeperIpFinder
	 */
	private String TcpDiscoveryWay = "zooKeeperIpFinder";
	
	/**
	 * 组播
	 */
	private String multicastGroup = "233.255.255.255";

	/**
	 * 静态地址
	 */
	private String[] staticIpAddresses = {"172.16.49.77","172.16.16.98"};
	
	

	public int getPersistenceInitialSize() {
		return persistenceInitialSize;
	}

	public IgniteProperties setPersistenceInitialSize(int persistenceInitialSize) {
		this.persistenceInitialSize = persistenceInitialSize;
		return this;
	}

	public int getPersistenceMaxSize() {
		return persistenceMaxSize;
	}

	public IgniteProperties setPersistenceMaxSize(int persistenceMaxSize) {
		this.persistenceMaxSize = persistenceMaxSize;
		return this;
	}

	public String getPersistenceStorePath() {
		return System.getProperty("BasicDir");
	}

	public IgniteProperties setPersistenceStorePath(String persistenceStorePath) {
		this.persistenceStorePath = persistenceStorePath;
		return this;
	}

	public int getNotPersistenceInitialSize() {
		return NotPersistenceInitialSize;
	}

	public IgniteProperties setNotPersistenceInitialSize(int notPersistenceInitialSize) {
		NotPersistenceInitialSize = notPersistenceInitialSize;
		return this;
	}

	public int getNotPersistenceMaxSize() {
		return NotPersistenceMaxSize;
	}

	public String getMulticastGroup() {
		return multicastGroup;
	}

	public void setMulticastGroup(String multicastGroup) {
		this.multicastGroup = multicastGroup;
	}

	public String[] getStaticIpAddresses() {
		return staticIpAddresses;
	}

	public void setStaticIpAddresses(String[] staticIpAddresses) {
		this.staticIpAddresses = staticIpAddresses;
	}

	public IgniteProperties setNotPersistenceMaxSize(int notPersistenceMaxSize) {
		NotPersistenceMaxSize = notPersistenceMaxSize;
		return this;
	}

	public String getTcpDiscoveryWay() {
		return TcpDiscoveryWay;
	}

	public void setTcpDiscoveryWay(String tcpDiscoveryWay) {
		TcpDiscoveryWay = tcpDiscoveryWay;
	}
	
	
}
