package hx.apigate.socket.handlers;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * <p>Description: 响应http</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.xianglong.work</p>
　 * @author yangcheng
　 * @date 2019年10月30日
　 * @version 1.0
 */
public class MasterBackHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
	Logger logger = LoggerFactory.getLogger(MasterBackHandler.class);
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
    public void channelRead0(final ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
    	inboundChannel.writeAndFlush(msg.retain()).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.channel().close();
                }
            }
        });
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        	logger.debug("toMaster Handler destroyed!");
//        }
        
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("网关与微服务端http连接异常", cause);
        ctx.deregister();
    }
}
