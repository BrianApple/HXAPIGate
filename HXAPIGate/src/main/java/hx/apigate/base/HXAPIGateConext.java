
package hx.apigate.base;
import io.netty.channel.nio.NioEventLoopGroup;
/**
 * <p>Description: 浩心API网关上下文</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotp.com</p>
 * @author yangcheng
 * @date 2019年11月12日
 * @version 1.0
 */
public class HXAPIGateConext {
	public static int PORT = 8081;//HXAPIGate默认端口为8081，通过启动参数可修改
	private static String VERSION = "1.0.0";
	public static NioEventLoopGroup boss = new NioEventLoopGroup(1);
	public static String NUM ="1";
	

}
