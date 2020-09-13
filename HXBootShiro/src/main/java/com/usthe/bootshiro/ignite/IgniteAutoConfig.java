package com.usthe.bootshiro.ignite;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.IgniteSemaphore;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.usthe.bootshiro.domain.bo.AuthResource;
import com.usthe.bootshiro.ignite.config.IgniteProperties;

import hx.apigate.databridge.xmlBean.Route;
import hx.apigate.databridge.xmlBean.RouteAll;
import hx.apigate.databridge.xmlBean.RouteNode;



/**
 * 
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author yangcheng,hjj
 * @date 2020年9月13日
 * @version 1.0
 */
@Component
public class IgniteAutoConfig {

	@Value("${zkAddr}")
	private String zkAddr;
	private String instanceName ;

	private IgniteProperties igniteProperties;

	public IgniteProperties igniteProperties() {
		igniteProperties =  igniteProperties == null ?  new IgniteProperties() : igniteProperties;
		return igniteProperties;
	}
	private Ignite ignite;
	@PostConstruct
	private void ignite() throws Exception {
		IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
		instanceName = ("".equals(instanceName) || instanceName == null) ? "managerPlatform1" : instanceName;
		igniteConfiguration.setIgniteInstanceName(instanceName);
		Logger logger = LoggerFactory.getLogger("org.apache.ignite");
		igniteConfiguration.setGridLogger(new Slf4jLogger(logger));
		
		igniteConfiguration.setUserAttributes(Collections.singletonMap("ROLE", "Console"));//
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
			ipFinder.setZkConnectionString(zkAddr);//"127.0.0.1:2181"
			tcpDiscoverySpi.setIpFinder(ipFinder);
		default:
			break;
		}
		igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
		igniteConfiguration.setPeerClassLoadingEnabled(true);
		igniteConfiguration.setMetricsLogFrequency(0);
		
		ignite = Ignition.start(igniteConfiguration);
		ignite.cluster().active(true);
		
	}
	/**
	 * 分布式消息API对象，客户端用
	 * @return
	 * @throws Exception
	 */
	public IgniteMessaging igniteMessaging() throws Exception {
		return ignite.message(ignite.cluster().forRemotes());
	}
	public Ignite getIgnite() {
		return ignite;
	}

	
	/**
	 * 初始化API信息,接口名称为：“uri==method”
	 * @param uri
	 * @param jsonStr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean initApiRouteInfo(List<AuthResource> allApi) {
		Map<String, RouteAll> map = new ConcurrentHashMap<String, RouteAll>();
		
		for (AuthResource authResource : allApi) {
			String uri = authResource.getUri();
			String method = authResource.getMethod();
			Map routeInfo = (Map) JSON.parse(authResource.getRouteInfo());
			if(routeInfo == null) {
				continue;
			}
			String all_tps = routeInfo.get("all_tps") != null ?  (String) routeInfo.get("all_tps") : "200";
			String balance = (String) routeInfo.get("balance");
			String isAuth = (String) routeInfo.get("isAuth");
			String pType = (String) routeInfo.get("pType");
			String api_version = (String) routeInfo.get("api_version");
			boolean isWeight = "1".equals(routeInfo.get("api_version_balance")) ? false : true;
			
			Route  route = new Route();
			route.setMatchUrl(uri);
			route.setProtocal(pType);//规约
			route.setVersion(api_version);
			if(isWeight) {
				route.setVersionWeight(Integer.parseInt(String.valueOf(routeInfo.get("api_version_weightNum"))));
			}
			route.setNeedAuth(("1".equals(isAuth) ? true : false));
			route.setStratege(("1".equals(balance) ? "circle" : "weight"));//负载策略
			
			System.out.println(uri+" 的allTps == "+all_tps+"=="+method);
			IgniteSemaphore allTps = ignite.semaphore(uri+"=="+method+"=="+api_version, Integer.parseInt(all_tps), true, true);
			route.setTps(allTps);
			
			List<RouteNode> nodes = new ArrayList<RouteNode>();
			if("http".equals(routeInfo.get("pType"))) {
				
				int routeNum = (routeInfo.size() - 6) / 5; 
				for(int i = 1 ; i <= routeNum ; i++) {
					RouteNode node = new RouteNode();
					
					String rout_ipAddr = (String) routeInfo.get("rout_ipAddr"+i);
					String rout_order = (String) routeInfo.get("rout_order"+i);
					String rout_port = (String) routeInfo.get("rout_port"+i);
					String rout_tps = (String) routeInfo.get("rout_tps"+i);
					String rout_weight = (String) routeInfo.get("rout_weight"+i);
					
					node.setIp(rout_ipAddr);
					node.setOrder(Integer.parseInt(rout_order == null || "".equals(rout_order) ? "0" : rout_order));
					node.setPort(Integer.parseInt(rout_port == null || "".equals(rout_port) ? "18080" : rout_port));
					
					int nodeTpsVal = Integer.parseInt(rout_tps == null || "".equals(rout_tps) ? "200" : rout_tps);
					IgniteSemaphore nodeTps = ignite.semaphore(uri+"=="+method+"==="+rout_ipAddr+":"+rout_port, nodeTpsVal, true, true);
					node.setTps(nodeTps);
					
					node.setWeight(Integer.parseInt(rout_weight == null || "".equals(rout_weight) ? "1" : rout_weight));
					
					nodes.add(node);
				}
				
				
			}else if("dubbo".equals(routeInfo.get("pType"))) {
				int routeNum = (routeInfo.size() - 6) / 2 ; 
				for(int i = 1 ; i <= routeNum ; i++) {
					RouteNode node = new RouteNode();
					String route_InterfaceName = (String) routeInfo.get("route_InterfaceName"+i);
					String route_tps = routeInfo.get("route_tps"+i) == null || "".equals("route_tps"+i) ? "200" : (String)routeInfo.get("route_tps"+i);
					
					node.setInterfaceName(route_InterfaceName);
					
					int nodeTpsVal = Integer.parseInt(route_tps == null || "".equals(route_tps) ? "200" : route_tps);
					IgniteSemaphore nodeTps = ignite.semaphore(uri+"=="+method+"==="+route_InterfaceName+"=="+api_version, nodeTpsVal, true, true);
					node.setTps(nodeTps);
					nodes.add(node);
				}
			}
			route.setRouteNodes(nodes);
			route.init();
			
			if(map.containsKey(uri+"=="+method)) {
				map.get(uri+"=="+method).addInfo( route);
			}else {
				RouteAll routeAll = new RouteAll(isWeight,uri);
				routeAll.addInfo( route);
				map.put(uri+"=="+method, routeAll);
			}
		}
		CacheConfiguration<String, Map<String, RouteAll>> cacheCfg = new CacheConfiguration<String, Map<String, RouteAll>>(Constance.URI_ROUTE_INFO);
		cacheCfg.setCacheMode(CacheMode.REPLICATED);
		cacheCfg.setCopyOnRead(true);
		IgniteCache<String, Map<String,RouteAll>> cache = ignite.getOrCreateCache(cacheCfg);
		System.out.println("所有路由==="+JSON.toJSONString(map));
		cache.put("ALL_ROUTE", map);
		return true;
	}
	/**
	 * 添加API信息
	 * @param uri
	 * @param jsonStr
	 * @param routeInfo2 
	 * @return
	 */
	public boolean addApiInfo(boolean isAdd,String uri, String method,String jsonStr) {
		
		Map routeInfo = (Map) JSON.parse(jsonStr);
		if(routeInfo == null) {
			return false;
		}
		String all_tps = routeInfo.get("all_tps") != null ?  (String) routeInfo.get("all_tps") : "200";
		String balance = (String) routeInfo.get("balance");
		String isAuth = (String) routeInfo.get("isAuth");
		String pType = (String) routeInfo.get("pType");
		boolean isWeight = "1".equals(routeInfo.get("api_version_balance")) ? false : true;
		String api_version = (String) routeInfo.get("api_version") ;
		Map<String, RouteAll> allRoute = (Map) ignite.getOrCreateCache(Constance.URI_ROUTE_INFO).get("ALL_ROUTE");//为并发Map
		
		RouteAll oldRouteAll = null;
		if(!isAdd) {
			oldRouteAll = allRoute.get(uri+"=="+method);
			for(int i = 0 ; i < oldRouteAll.getRoutes().size() ; i ++) {
				Route oldRoute = oldRouteAll.getRoutes().get(i);
				if(api_version != null && api_version.equals(oldRoute.getVersion())) {
					oldRouteAll.getRoutes().remove(i);
					oldRoute.getTps().close();
					List<RouteNode> oldNodes = oldRoute.getRouteNodes();
					for (RouteNode routeNode : oldNodes) {
						routeNode.getTps().close();
					}
				}
			}
		}else {
			if(allRoute.containsKey(uri+"=="+method)) {
				oldRouteAll = allRoute.get(uri+"=="+method);
			}else {
				oldRouteAll = new RouteAll(isWeight,uri);
			}
		}
		
		Route route = new Route();
		
		route.setMatchUrl(uri);
		route.setProtocal(pType);
		route.setVersion(api_version);
		if(isWeight) {
			route.setVersionWeight(Integer.parseInt(String.valueOf(routeInfo.get("api_version_weightNum"))));
		}
		route.setNeedAuth(("1".equals(isAuth) ? true : false));
		route.setStratege(("1".equals(balance) ? "circle" : "weight"));
		
		IgniteSemaphore allTps = ignite.semaphore(uri+"=="+method+"=="+api_version, Integer.parseInt(all_tps), true, true);
		route.setTps(allTps);
		
		List<RouteNode> nodes = new ArrayList<RouteNode>();
		if("http".equals(routeInfo.get("pType"))) {
			
			int routeNum = (routeInfo.size() - 7) / 5; 
			for(int i = 1 ; i <= routeNum ; i++) {
				RouteNode node = new RouteNode();
				
				String rout_ipAddr = (String) routeInfo.get("rout_ipAddr"+i);
				String rout_order = (String) routeInfo.get("rout_order"+i);
				String rout_port = (String) routeInfo.get("rout_port"+i);
				String rout_tps = (String) routeInfo.get("rout_tps"+i);
				String rout_weight = (String) routeInfo.get("rout_weight"+i);
				
				node.setIp(rout_ipAddr);
				node.setOrder(Integer.parseInt(rout_order == null || "".equals(rout_order) ? "0" : rout_order));
				node.setPort(Integer.parseInt(rout_port == null || "".equals(rout_port) ? "18080" : rout_port));
				
				int nodeTpsVal = Integer.parseInt(rout_tps == null || "".equals(rout_tps) ? "200" : rout_tps);
				IgniteSemaphore nodeTps = ignite.semaphore(uri+"=="+method+"==="+rout_ipAddr+":"+rout_port, nodeTpsVal, true, true);
				node.setTps(nodeTps);
				
				node.setWeight(Integer.parseInt(rout_weight == null || "".equals(rout_weight) ? "1" : rout_weight));
				
				nodes.add(node);
			}
			
			
		}else if("dubbo".equals(routeInfo.get("pType"))) {
			int routeNum = ((routeInfo.size() - 7) / 2 ); 
			for(int i = 1 ; i <= routeNum ; i++) {
				RouteNode node = new RouteNode();
				String route_InterfaceName = (String) routeInfo.get("route_InterfaceName"+i);
				String route_tps = routeInfo.get("route_tps"+i) == null || "".equals("route_tps"+i) ? "200" : (String)routeInfo.get("route_tps"+i);
				
				node.setInterfaceName(route_InterfaceName);
				
				int nodeTpsVal = Integer.parseInt(route_tps == null || "".equals(route_tps) ? "200" : route_tps);
				IgniteSemaphore nodeTps = ignite.semaphore(uri+"=="+method+"==="+route_InterfaceName+"=="+api_version, nodeTpsVal, true, true);
				node.setTps(nodeTps);
				nodes.add(node);
			}
		}
		route.setRouteNodes(nodes);
		route.init();
		oldRouteAll.addInfo( route);
		oldRouteAll.setUpdated(true);
		allRoute.put(uri+"=="+method, oldRouteAll);
		ignite.getOrCreateCache(Constance.URI_ROUTE_INFO).put("ALL_ROUTE",allRoute);
		return true;
	}
	
	/**
	 * 从缓存中删除指定API的对应版本
	 * @param uri
	 * @param httpMethod 
	 * @param apiVersion api版本信息
	 * @return
	 */
	public boolean removeApiInfo(String uri, String httpMethod, String apiVersion) {
		@SuppressWarnings("unchecked")
		Map<String, RouteAll> allRoute = (Map<String, RouteAll>) ignite.getOrCreateCache(Constance.URI_ROUTE_INFO).get("ALL_ROUTE");//为并发Map
		RouteAll routeAll = allRoute.get(uri+"=="+httpMethod);
		int size = routeAll.getRoutes().size();
		for(int i = 0 ; i < size ; i++) {
			Route route = routeAll.getRoutes().get(i);
			if(apiVersion.equals(route.getVersion())) {
				routeAll.setUpdated(true);
				routeAll.getRoutes().remove(i);
				routeAll.init();
			}
		}
		
		ignite.getOrCreateCache(Constance.URI_ROUTE_INFO).put("ALL_ROUTE", allRoute);
		return true;
	}
	
	/**
	 * 缓存TOKEN_KAY
	 * @param refreshPeriodTime TTL时间  单位秒
	 * @param key 
	 * @param data
	 * @return
	 */
	public boolean cacheTOKEN_KAY(long refreshPeriodTime,String key,String data) {
		CacheConfiguration<Object, Object> cacheCfg = new CacheConfiguration<Object, Object>(Constance.IGNITE_TOKEN);
		cacheCfg.setCacheMode(CacheMode.LOCAL);
		cacheCfg.setCopyOnRead(true);
		@SuppressWarnings("unchecked")
		IgniteCache<Object, Object>  cache = ignite.getOrCreateCache(cacheCfg)
				.withExpiryPolicy(new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, refreshPeriodTime)));
		cache.put(key, data);
		return true;
	}
	/**
	 * 缓存TOKEN_KAY
	 * @param refreshPeriodTime TTL时间  单位秒
	 * @param key 
	 * @param data
	 * @return
	 */
	public String getTOKEN_KAY(String key) {
		CacheConfiguration cacheCfg = new CacheConfiguration(Constance.IGNITE_TOKEN);
		cacheCfg.setCacheMode(CacheMode.LOCAL);
		cacheCfg.setCopyOnRead(true);
		@SuppressWarnings("unchecked")
		IgniteCache<Object, Object>  cache = ignite.getOrCreateCache(cacheCfg)
				.withExpiryPolicy(new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, 500)));
		return (String) cache.get(key);
	}
	/**
	 * 缓存JWT_SESSION数据
	 * @param refreshPeriodTime TTL时间  单位秒
	 * @param key 
	 * @param data
	 * @return
	 */
	public boolean cacheCommonData(long refreshPeriodTime,String key,String data) {
		CacheConfiguration cacheCfg = new CacheConfiguration(Constance.JWT_SESSION);
		cacheCfg.setCacheMode(CacheMode.REPLICATED);
		cacheCfg.setCopyOnRead(true);
		@SuppressWarnings("unchecked")
		IgniteCache<Object, Object>  cache = ignite.getOrCreateCache(cacheCfg)
				.withExpiryPolicy(new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, refreshPeriodTime)));
		cache.put(key, data);
		return true;
	}
	/**
	 * 从缓存获取JWT_SESSION数据
	 * @param refreshPeriodTime TTL时间  单位秒
	 * @param key 
	 * @param data
	 * @return
	 */
	public Object getCommonData(String key) {
		IgniteCache<Object, Object>  cache = ignite.cache(Constance.JWT_SESSION);
		return cache != null ? cache.get(key) : null;
	}
	/**
	 * 通过指定key删除JWT_SESSION对应缓存
	 * @param key
	 * @return
	 */
	public boolean removeCommonData(String key) {
		ignite.cache(Constance.JWT_SESSION).remove(key);
		return true;
	}
	
	/**
	 * API资源授权信息缓存
	 * @return
	 */
	public IgniteCache<String,String> getApiAuthCache() {
		CacheConfiguration cacheCfg = new CacheConfiguration(Constance.APIAUTH_CACHE);
		cacheCfg.setCacheMode(CacheMode.REPLICATED);
		cacheCfg.setCopyOnRead(true);
		return ignite.getOrCreateCache(cacheCfg);
	}
}
