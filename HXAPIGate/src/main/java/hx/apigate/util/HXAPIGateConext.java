package hx.apigate.util;
/**
 * <p>Description: 浩心API网关上下文</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */

import io.netty.channel.nio.NioEventLoopGroup;
/**
 * 
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author yangcheng
 * @date 2018年12月5日
 * @version 1.0
 */
public class HXAPIGateConext {
	public static int PORT = 8081;
	private static String VERSION = "1.0.0";
	public static NioEventLoopGroup boss = new NioEventLoopGroup(1);
	

}
