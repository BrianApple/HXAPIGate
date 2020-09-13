package hx.apigate.base;
/**
 * <p>Description: 初始化不会自动执行</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng,hjj
　 * @date 2019年10月30日
　 * @version 1.0
 */
public interface DistributedCacheProcessor extends InitProcessor,IBeforeEndProcessor{
	
	
	Object excuteluaScript(String luaScriptStr,int keyCount,String... params );
	
	void expireString(String key,int timeout);
	
	void setString(String key,String value);
	
	String getString(String key);
	
	void setStringObj(String key,Object value);
	
	void setStringObjWithExpire(String key,Object value,int second);
	
	
	Object getStringObj(String key,String clazz);
	
	void setStringWithExpire(String key,String value,int expire);
	
	
	void setHashString(String key,String field,String value);
	
	String getHashString(String key,String field);
	
	void setHashObj(String key, String field, Object value);
	
	Object getHashObj(String key, String field,String clazz);
	
	void setBit(String key,long offset,boolean value );
	 
	boolean getBit(String key,long offset);
	
	boolean exist(String key);
	
	boolean exist(byte[] bytesKey);
}
