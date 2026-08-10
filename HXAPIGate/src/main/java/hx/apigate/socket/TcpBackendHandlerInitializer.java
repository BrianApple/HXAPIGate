package hx.apigate.socket;

import hx.apigate.socket.handlers.TcpBackendHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * 后端 TCP 服务连接的 pipeline 初始化工具类。
 * 与 BackendHandlerInitializer 的区别：不挂 HTTP 编解码器，纯字节双向透传。
 * <p>Description: </p>
 */
public class TcpBackendHandlerInitializer extends ChannelInitializer<SocketChannel>{

    final Channel inboundChannel;

    public TcpBackendHandlerInitializer(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new TcpBackendHandler(inboundChannel));
    }
}
