package hx.apigate.distributedCache.serialization;

import hx.apigate.distributedCache.config.DistributedCachingProperties;
import hx.apigate.distributedCache.config.PropertiesFactory;
import hx.apigate.distributedCache.serialization.bytes.ProtobufSerialize;
import hx.apigate.distributedCache.serialization.json.FastJsonSerialize;

/**
 * 序列化器工厂
 * @Description:
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.xianglong.work</p> 
 * @author  yangcheng
 * @date:   2019年6月25日
 */
public class SerializationFactory {
	
	private static Serialization curSerialization = null;
	
	public static Serialization getSerializationInstance(){
		if(curSerialization == null){
			synchronized (SerializationFactory.class) {
				if(curSerialization == null){
					if("fastJsonSerialize".equals(PropertiesFactory.getDistributedCachingPropertiesInstance().getBASIC_SERIALIZATION())){
						curSerialization = new FastJsonSerialize();
					}else if("protobufSerialize".equals(PropertiesFactory.getDistributedCachingPropertiesInstance().getBASIC_SERIALIZATION())){
						curSerialization = new ProtobufSerialize();
					}else{
						curSerialization = new FastJsonSerialize();
					}
				}
				
			}
		}
		return curSerialization;
	}
	
	
	

}
