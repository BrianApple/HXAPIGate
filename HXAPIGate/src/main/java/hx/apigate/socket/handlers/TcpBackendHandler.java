package hx.apigate.socket.handlers;

import hx.apigate.util.MixAll;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Description: 后端 TCP 服务响应字节 → 包成 HTTP 响应原样回写 web 端（不包 RetMessage）</p>
 * 请求-响应模式：一次请求一个响应，响应后即关闭后端连接。
 */
public class TcpBackendHandler extends ChannelInboundHandlerAdapter {
    Logger logger = LoggerFactory.getLogger(TcpBackendHandler.class);
    private final Channel inboundChannel;

    public TcpBackendHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf data = (ByteBuf) msg;
        byte[] bytes = new byte[data.readableBytes()];
        data.readBytes(bytes);
        data.release();
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(bytes));
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
        resp.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        inboundChannel.writeAndFlush(resp);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("tcp backend channel destroyed");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("网关与后端tcp连接异常", cause);
        inboundChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(502, "backend tcp error !"));
        ctx.close();
    }
}
