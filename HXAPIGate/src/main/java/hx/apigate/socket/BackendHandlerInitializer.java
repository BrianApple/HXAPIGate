package hx.apigate.socket;


import hx.apigate.socket.handlers.MasterBackHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * 响应http请求的socket初始化时pipeline初始化工具类
　 * <p>Description: </p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.xianglong.work</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class BackendHandlerInitializer extends ChannelInitializer<SocketChannel>{

    final Channel inboundChannel;

    public BackendHandlerInitializer(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpClientCodec())
                .addLast(new HttpObjectAggregator(1024 * 1024))
                .addLast(new MasterBackHandler(inboundChannel));
    }
}
