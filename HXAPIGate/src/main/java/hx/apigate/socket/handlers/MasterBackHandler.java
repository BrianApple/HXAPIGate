package hx.apigate.socket.handlers;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.util.HttpResponseUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * <p>Description: 响应http</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng,hjj
　 * @date 2019年10月30日
　 * @version 1.0
 */
public class MasterBackHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
	Logger logger = LoggerFactory.getLogger(MasterBackHandler.class);
	/**
	 * web端channel
	 */
    private final Channel inboundChannel;

    public MasterBackHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("toMaster Handler is Active!");
    }
    /**
     * 获取到微服务响应
     */
    @Override
    public void channelRead0(final ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
    	//获取第三方会话并响应数据
    	HttpResponseUtil.responseMsg4Http(inboundChannel, response); 
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        	logger.debug("toMaster Handler destroyed!");
        
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("网关与微服务端http连接异常", cause);
        ctx.deregister();
    }
}
