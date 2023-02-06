package hx.apigate.util;

import java.util.Map;

import hx.apigate.circuitBreaker.CBManager;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;

import hx.apigate.databridge.xmlBean.Route;
import hx.apigate.databridge.xmlBean.RouteAll;
import org.apache.ignite.configuration.CacheConfiguration;

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
	public static String CIRCLEBREAK_CACHE = "CIRCLEBREAK_CACHE";
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


	/**
	 * CircleBreakCache
	 * @return
	 */
	public static IgniteCache<String, CBManager> getCircleBreakCache() {
		CacheConfiguration<String, CBManager> cacheCfg = new CacheConfiguration<String, CBManager>(CIRCLEBREAK_CACHE);
//		cacheCfg.setCacheMode(CacheMode.REPLICATED);
		cacheCfg.setCopyOnRead(true);
		return ignite.getOrCreateCache(cacheCfg);
	}
}
