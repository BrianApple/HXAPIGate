package hx.apigate.aware;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.Entrance;
import hx.apigate.base.IProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
/**
 * 各种配置文件的加载
 * @Description: 
 * @author  yangcheng,hjj
 * @date: 2020年4月17日
 */
public class EnvInitProcessor implements IProcessor{
	Logger logger = LoggerFactory.getLogger(EnvInitProcessor.class);
	@Override
	public void start() throws Exception {
		Properties prop = new Properties();
        ClassLoader loader = ReloadXmlAware.class.getClassLoader();

        try {
            prop.load(loader.getResourceAsStream("dubbo.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Entrance.isCluster = "true".equals(prop.getProperty("application.isCluster"));//是否开启分布式模式
        Entrance.gateNum = Integer.parseInt(prop.getProperty("application.gateNum"));
		Entrance.nodeName = prop.getProperty("application.name");
		Entrance.zkAddr = prop.getProperty("registry.address");
		
		
		
		

		logger.info("System configuration file loading completed ...........................................");
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getStartOrder() {
		return 0;
	}

	@Override
	public int getStopOrder() {
		return 0;
	}
	
}
