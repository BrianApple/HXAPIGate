package hx.apigate.util;

import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;

import hx.apigate.databridge.xmlBean.Route;
import hx.apigate.databridge.xmlBean.RouteAll;

/**
 * Ignite相关工具方法
 * @Description: 
 * @author  yangcheng,hjj
 * @date: 2020年2月17日
 */
public class IgniteUtil {
	public static String APIAUTH_CACHE = "APIAUTH_CACHE";
	public static String URI_ROUTE_INFO = "URI_ROUTE_INFO";
	public static String JWT_SESSION = "JWT_SESSION";
	//通过EventProcessor初始化
	public static Ignite ignite;
	
	
	public static IgniteCache<String, Map<String,RouteAll>> getAPIRouteCache(){
		return ignite.getOrCreateCache(URI_ROUTE_INFO);
	}
	
	public static IgniteCache<String, String> getAPIAuthCache(){
		return ignite.cache(APIAUTH_CACHE);
	}
	/**
	 * 获取JWT授权信息缓存
	 * @return
	 */
	public static IgniteCache<String, String> getJWTCache(){
		return ignite.cache(JWT_SESSION);
	}
	
	

}
