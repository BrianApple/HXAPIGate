package com.usthe.bootshiro.ignite;
/**
 * 分布式相关常量
 * @Description: 
 * @author  yangcheng
 * @date: 2020年2月17日
 */
public class Constance {
	/**
	 * HX网关通信主题
	 */
	public static String HXAPI_CONTROLLER_TOPIC = "HXAPI_CONTROLLER_TOPIC";
	/**
	 * API网关路由更新消息类型 
	 */
	public static String RouteCacheUpdate_SIG = "API00";
	
	
	//缓存
	
	/**
	 * api与授权角色关系缓存，bootshiro初始化时加载
	 */
	public static String APIAUTH_CACHE = "APIAUTH_CACHE";
	/**
	 * URI路由信息缓存，api对应的路由及限流信息
	 */
	public static String URI_ROUTE_INFO = "URI_ROUTE_INFO";
	/**
	 * JWT_SESSION缓存
	 */
	public static String JWT_SESSION = "JWT_SESSION";
	/**
	 * bootshiro登录动态密钥缓存
	 */
	@Deprecated
	public static String IGNITE_TOKEN = "IGNITE_TOKEN";
	
	/**
	 * bootshiro api与授权角色缓存前缀
	 */
	public static final String API_RESOURCE_ROLE = "API:";
}
