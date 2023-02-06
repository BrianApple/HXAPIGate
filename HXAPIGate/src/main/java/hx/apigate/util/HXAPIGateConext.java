package hx.apigate.util;

import com.google.common.util.concurrent.RateLimiter;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 
 * <p>Description: 浩心API网关上下文</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author yangcheng,hjj
 * @date 2020年9月13日
 * @version 1.0
 */
public class HXAPIGateConext {
	public static int PORT = 8081;//HXAPIGate默认端口为8081，通过启动参数可修改
	public static int TPS = 2000;//网关全局限流
	private static String VERSION = "1.0.0";
	public static NioEventLoopGroup boss = new NioEventLoopGroup(1);
	public static RateLimiter rateLimiter = RateLimiter.create(TPS);
	public static HashedWheelTimer circleBreaktimer = null;
	static {
		circleBreaktimer = new HashedWheelTimer(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {

				return new Thread(r , "circleBreadkThread");
			}
		}, 10, TimeUnit.SECONDS);

	}

}
