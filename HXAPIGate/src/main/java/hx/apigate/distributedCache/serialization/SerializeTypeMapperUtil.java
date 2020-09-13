package hx.apigate.distributedCache.serialization;


import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;

import jdk.nashorn.api.scripting.NashornScriptEngine;


/**
 * 反序列化类匹配工具类
 * @Description: 
 * @author  yangcheng
 * @date:   2019年6月25日
 */
public class SerializeTypeMapperUtil {
	public static String DogNodes = "DogNodes";
	public static String TmnlMsgData = "TmnlMsgData";
	public static String MapedMsg = "MapedMsg";
	public static String OnlineInfo = "OnlineInfo";
	public static String GenericService = "GenericService";
	

	private static Map<String,Class<?>> typeCache = new HashMap<>();
	
	static{
		typeCache.put("Invocable", Invocable.class);
		typeCache.put("NashornScriptEngine", NashornScriptEngine.class);
		typeCache.put("GenericService", 
				org.apache.dubbo.rpc.service.GenericService.class);
	}
	
	public static Class<?> getTypeClass(String clazzName){
		return typeCache.get(clazzName);
	}
}
