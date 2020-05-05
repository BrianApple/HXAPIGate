package hx.apigate.distributedCache.serialization;

/**
 * @Description: 
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.xianglong.work</p>
 * @author  yangcheng
 * @date:   2019年6月25日
 */
public interface Serialization {
	Object SerializeObject(Object obj) throws Exception;
	Object deSerializeObject(Object obj,String clazz) throws Exception;
	
}
