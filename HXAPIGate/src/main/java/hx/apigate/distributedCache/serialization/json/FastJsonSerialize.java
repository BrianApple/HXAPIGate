package hx.apigate.distributedCache.serialization.json;



import com.alibaba.fastjson.JSON;

import hx.apigate.distributedCache.serialization.Serialization;
import hx.apigate.distributedCache.serialization.SerializeTypeMapperUtil;

/**
 * json序列化器
 * @Description: 
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.xianglong.work</p>
 * @author  yangcheng
 * @date:   2019年6月25日
 */
public class FastJsonSerialize implements Serialization{

	@Override
	public Object SerializeObject(Object obj) {
		return JSON.toJSONString(obj);
	}

	@Override
	public Object deSerializeObject(Object obj,String clazz) {
		if(obj == null){
			return null;
		}
		if(obj instanceof String){
			Object e = JSON.parseObject((String)obj,SerializeTypeMapperUtil.getTypeClass(clazz));
			return e;
		}
		throw new RuntimeException("反序列化失败："+obj.getClass().getName()+"不是标准json字符串");
	}

	
}
