package hx.apigate.distributed.config;



public class IgniteProperties {

	private int persistenceInitialSize = 64;
	private int persistenceMaxSize = 128;
	private String persistenceStorePath ;
	private int NotPersistenceInitialSize = 64;
	private int NotPersistenceMaxSize = 128;
	private String TcpDiscoveryWay = "zooKeeperIpFinder";
	private String multicastGroup = "233.255.255.255";
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
