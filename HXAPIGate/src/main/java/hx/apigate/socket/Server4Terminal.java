package hx.apigate.socket;


import java.nio.channels.spi.SelectorProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.base.IProcessor;
import hx.apigate.util.HXAPIGateConext;
import hx.apigate.util.MixAll;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 网关获取终端报文
 * @Description: 
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotp.com</p>
 * @author  yangcheng
 * @date:   2019年3月30日
 */
public class Server4Terminal implements IProcessor{
	Logger logger = LoggerFactory.getLogger(Server4Terminal.class);
	/**
	 * 规约编号作为规约服务以及规约策略的唯一标识
	 */
	private  EventLoopGroup  boss;
	private  EventLoopGroup work;
	
	public Server4Terminal (){
		this.boss = new NioEventLoopGroup(1);
		this.work = new NioEventLoopGroup();
	}
	

	@Override
	public void start() throws Exception {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
		            ServerBootstrap b = new ServerBootstrap();
		            b.group(boss, work)
		                    .channelFactory(new ChannelFactory<NioServerSocketChannel>() {
		                        @Override
		                        public NioServerSocketChannel newChannel() {
		                            return new NioServerSocketChannel(SelectorProvider.provider());
		                        }
		                    })
		                    .handler(new LoggingHandler(LogLevel.WARN))
		                    .childHandler(new GatewayServerInitializer());
		            Channel ch = b.bind(HXAPIGateConext.PORT).sync().channel();
		            ch.closeFuture().sync();
		        } catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
		        	boss.shutdownGracefully();
		        	work.shutdownGracefully();
		        }
				
			}
		}).start();
		logger.info(MixAll.LOG_INFO_PRIFEX+String.format("HTTPServer is started ! Server port is %s", HXAPIGateConext.PORT));
	}


	@Override
	public void stop() throws Exception {
		boss.shutdownGracefully();
    	work.shutdownGracefully();
	}


	@Override
	public int getStartOrder() {
		return 1;
	}


	@Override
	public int getStopOrder() {
		return 1;
	}
	
	
}
