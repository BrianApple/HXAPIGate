package hx.apigate.base;
/**
 * <p>Description: 初始化不会自动执行</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月30日
　 * @version 1.0
 */
public interface DistributedCacheProcessor extends InitProcessor,IBeforeEndProcessor{
	
	/**
	 * 设置失效时间
	 * @param key
	 * @param timeout  单位秒
	 */
	void expireString(String key,int timeout);
	/**
	 * 设置String类型的KEY VALUE
	 * @param key
	 * @param value
	 */
	void setString(String key,String value);
	/**
	 * 获取String类型的value
	 * @param key
	 * @return
	 */
	String getString(String key);
	
	/**
	 * Value为任意POJO对象，序列化之后 只能是json字符串或者byte[]数组格式
	 * @param key
	 * @param value
	 */
	void setStringObj(String key,Object value);
	/**
	 * Value为任意POJO对象，序列化之后 只能是json字符串或者byte[]数组格式
	 * @param key
	 * @param value
	 * @param second  过期时间
	 */
	void setStringObjWithExpire(String key,Object value,int second);
	
	/**
	 * 返回值为clazz类型的POJO对象
	 * @param key
	 * @param clazz 返回结果类型的类名（SimpleName）
	 * @return 
	 */
	Object getStringObj(String key,String clazz);
	/**
	 * 
	 * @param key
	 * @param value
	 * @param expire  单位秒
	 */
	void setStringWithExpire(String key,String value,int expire);
	
	/**
	 * 设置String类型的Hash结构缓存
	 * @param key
	 * @param field
	 * @param value
	 */
	void setHashString(String key,String field,String value);
	/**
	 * 获取Hash结构的指定字段值--String类型值
	 * @param key
	 * @param field
	 * @return
	 */
	String getHashString(String key,String field);
	
	void setHashObj(String key, String field, Object value);
	
	Object getHashObj(String key, String field,String clazz);
	
	void setBit(String key,long offset,boolean value );
	 
	boolean getBit(String key,long offset);
	/**
	 * 对应value为String时使用
	 * @param bytesKey
	 * @return
	 */
	boolean exist(String key);
	/**
	 * 对应value为Object时使用
	 * @param bytesKey
	 * @return
	 */
	boolean exist(byte[] bytesKey);
}
