package hx.apigate.distributedCache.serialization;

/**
 * 序列化器
 * @Description: 
 * @author  yangcheng
 * @date:   2019年6月25日
 */
public interface Serialization {
	Object SerializeObject(Object obj) throws Exception;
	Object deSerializeObject(Object obj,String clazz) throws Exception;
	
}
