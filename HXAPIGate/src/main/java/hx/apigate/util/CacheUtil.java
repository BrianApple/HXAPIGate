package hx.apigate.util;

import hx.apigate.distributedCache.DefualtDistributedCacheManager;

/**
 * redis缓存工具类
 * 
 * @Description: 
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.xianglong.work</p>
 * @author  yangcheng
 * @date:   2018年12月3日
 */
public class CacheUtil {
	
	private CacheUtil() {
		throw new AssertionError();
	}
	/**
	 * 设置失效时间
	 * @param key
	 * @param timeout  单位秒
	 */
	public static void expireString(String key,int timeout){
		DefualtDistributedCacheManager.getInstance().expireString(key, timeout);
	}
	
	public static void setString(String key,String value){
		DefualtDistributedCacheManager.getInstance().setString(key, value);
	}
	
	public static String getString(String key){
		return DefualtDistributedCacheManager.getInstance().getString(key);
	}
	
	public static void setStringObj(String key,Object value){
		DefualtDistributedCacheManager.getInstance().setStringObj(key, value);
	}
	
	public static void setStringObjWithExpire(String key,Object value,int second){
		DefualtDistributedCacheManager.getInstance().setStringObjWithExpire(key, value,second);
	}
	
	public static Object getStringObj(String key,String clazz){
		return DefualtDistributedCacheManager.getInstance().getStringObj(key,clazz);
	}
	
	public static void setStringWithExpire(String key,String value,int expire){
		DefualtDistributedCacheManager.getInstance().setStringWithExpire(key, value, expire);
	}
	

	public static void setHashString(String key,String field,String value){
		DefualtDistributedCacheManager.getInstance().setHashString(key, field, value);
	}
	

	public static String getHashString(String key,String field){
		return DefualtDistributedCacheManager.getInstance().getHashString(key, field);
	}
	

	public static void setHashObj(String key,String field,Object value){
		DefualtDistributedCacheManager.getInstance().setHashObj(key, field, value);
	}
	

	public static Object getHashObj(String key,String field,String clazz){
		return DefualtDistributedCacheManager.getInstance().getHashObj(key, field,clazz);
	}
	
	public static void setBit(String key,long offset,boolean value ){
		DefualtDistributedCacheManager.getInstance().setBit(key, offset, value);
	}
	public static boolean getBit(String key,long offset){
		return DefualtDistributedCacheManager.getInstance().getBit(key, offset);
	}
	public static boolean exist(String key) {
		return DefualtDistributedCacheManager.getInstance().exist(key);
	}
	/**
	 * 对应value为Object时使用
	 * @param bytesKey
	 * @return
	 */
	public static boolean exist(byte[] bytesKey) {
		return DefualtDistributedCacheManager.getInstance().exist(bytesKey);
	}
}
