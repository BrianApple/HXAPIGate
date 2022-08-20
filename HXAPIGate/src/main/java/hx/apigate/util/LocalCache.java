package hx.apigate.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import hx.apigate.base.IProcessor;
import hx.apigate.databridge.xmlBean.Route;
import hx.apigate.databridge.xmlBean.RouteAll;
import io.netty.channel.Channel;

/**
 * 本地缓存
 * <p>Description: </p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class LocalCache {
	public static List<IProcessor> eventProcessorCache = null;
	public static ConcurrentHashMap<String,Channel> masterChannelCache = null;
	public static ConcurrentHashMap<String,Channel> webChannelCache = null;
	static{
		eventProcessorCache = new ArrayList<>();
		masterChannelCache = new ConcurrentHashMap<>();
		webChannelCache = new ConcurrentHashMap<>();
	}

}
