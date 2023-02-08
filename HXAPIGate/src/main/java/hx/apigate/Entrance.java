package hx.apigate;

import java.util.Set;


import org.reflections.Reflections;

import hx.apigate.base.IProcessor;
import hx.apigate.util.LocalCache;

/**
 * 浩心API网关入口程序
 * 基于springboot
 *
 * 网关统一响应格式为：
	 {
	 "code": xxx,
	 "data": {...}, //微服务返回数据
	 "msg" : xxx,//异常信息
	 "timestamp": xxx
	 }
 * code = 200  微服务调用成功，data为微服务值
 * code = 400  jwt鉴权异常
 * code = 403  无权限
 * code = 404  当前接口路径不存在或者接口服务被降级
 * code = 502  代理接口访问异常
 * code = 503  微服务访问超限（限流）或其他服务异常
 * @author yangcheng,hjj
 * @date
 */
public class Entrance {
	public static int gateNum = 1;//默认为1
	public static boolean isCluster;
	public static String nodeName = null;
	public static String zkAddr = null;
	public static void main(String[] args) {
		System.setProperty("org.jboss.netty.epollBugWorkaround", "true");
		Reflections reflections = new Reflections("hx.apigate");
		Set<Class<? extends IProcessor>> subTypes = reflections.getSubTypesOf(IProcessor.class);
		IProcessor[] array = new IProcessor[subTypes.size()];
		for (Class<? extends IProcessor> class1 : subTypes) {
			try {
				IProcessor processor = class1.newInstance();
				array[processor.getStartOrder()] = processor;
				
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 0 ; i < array.length ; i++) {
			try {
				array[i].start();
				LocalCache.eventProcessorCache.add(array[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
}
