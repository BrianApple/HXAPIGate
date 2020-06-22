package hx.apigate.distributedCache.serialization;


import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;

import jdk.nashorn.api.scripting.NashornScriptEngine;


/**
 * @Description: 
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotp.com</p>
 * @author  yangcheng
 * @date:   2019年6月25日
 */
public class SerializeTypeMapperUtil {
	public static String DogNodes = "DogNodes";
	public static String TmnlMsgData = "TmnlMsgData";
	public static String MapedMsg = "MapedMsg";
	public static String OnlineInfo = "OnlineInfo";
	

	private static Map<String,Class<?>> typeCache = new HashMap<>();
	
	static{
		typeCache.put("Invocable", Invocable.class);
		typeCache.put("NashornScriptEngine", NashornScriptEngine.class);
	}
	
	public static Class<?> getTypeClass(String clazzName){
		return typeCache.get(clazzName);
	}
}
