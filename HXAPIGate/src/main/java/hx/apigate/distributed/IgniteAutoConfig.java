package hx.apigate.distributed;


import java.util.Arrays;
import java.util.Collections;

import javax.annotation.PostConstruct;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.distributed.config.IgniteProperties;

/**
 * 自动配置apache ignite
 */
public class IgniteAutoConfig {

	private String instanceName ;
	private IgniteProperties igniteProperties;

	public IgniteProperties igniteProperties() {
		igniteProperties =  igniteProperties == null ?  new IgniteProperties() : igniteProperties;
		return igniteProperties;
	}
	public Ignite ignite() throws Exception {
		IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
		instanceName = ("".equals(instanceName) || instanceName == null) ? (hx.apigate.Entrance.nodeName+hx.apigate.Entrance.gateNum) : instanceName;
		igniteConfiguration.setIgniteInstanceName(instanceName);
		Logger logger = LoggerFactory.getLogger("org.apache.ignite");
		igniteConfiguration.setGridLogger(new Slf4jLogger(logger));
		
		igniteConfiguration.setUserAttributes(Collections.singletonMap("ROLE", "HXAPIGate"));//
		DataRegionConfiguration notPersistence = new DataRegionConfiguration().setPersistenceEnabled(false)
			.setInitialSize(igniteProperties().getNotPersistenceInitialSize() * 1024 * 1024)
			.setMaxSize(igniteProperties().getNotPersistenceMaxSize() * 1024 * 1024).setName("not-persistence-data-region");
		DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration().setDefaultDataRegionConfiguration(notPersistence);
		igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
		TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
		String tcpDiscoveryWay = igniteProperties().getTcpDiscoveryWay();
		switch (tcpDiscoveryWay) {
		case "multicastIpFinder":
			TcpDiscoveryMulticastIpFinder tcpDiscoveryMulticastIpFinder = new TcpDiscoveryMulticastIpFinder();
			tcpDiscoveryMulticastIpFinder.setMulticastGroup(igniteProperties().getMulticastGroup());
			tcpDiscoverySpi.setIpFinder(tcpDiscoveryMulticastIpFinder);
			break;
		case "vmIpFinder":
			TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder();
			tcpDiscoveryVmIpFinder.setAddresses(Arrays.asList(igniteProperties().getStaticIpAddresses()));
			tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
			break;
		case "zooKeeperIpFinder":

			TcpDiscoveryZookeeperIpFinder  ipFinder = new TcpDiscoveryZookeeperIpFinder();
			// Specify ZooKeeper connection string.
			String zkAddr = hx.apigate.Entrance.zkAddr.replace("zookeeper://", "");
			zkAddr = zkAddr.replace("?backup=", ",");
			ipFinder.setZkConnectionString(zkAddr);//"127.0.0.1:2181"
			tcpDiscoverySpi.setIpFinder(ipFinder);
		default:
			break;
		}
		igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
		igniteConfiguration.setPeerClassLoadingEnabled(true);
		igniteConfiguration.setMetricsLogFrequency(0);//关闭定时打印服务器负载装填信息
		TcpCommunicationSpi commSpi = new TcpCommunicationSpi();
		commSpi.setSlowClientQueueLimit(1000);
		igniteConfiguration.setCommunicationSpi(commSpi);
		Ignite ignite = Ignition.start(igniteConfiguration);
		ignite.cluster().active(true);
		return ignite;
	}
	/**
	 * 分布式消息API对象
	 * @return
	 * @throws Exception
	 */
	public IgniteMessaging igniteMessaging(Ignite ignite) throws Exception {
		return ignite.message(ignite.cluster().forRemotes());
	}
	

}
