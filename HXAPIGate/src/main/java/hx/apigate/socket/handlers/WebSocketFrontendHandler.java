package hx.apigate.socket.handlers;

import hx.apigate.util.HXAPIGateConext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 前端 WebSocket 连接转发处理器：
 * 1. 前端 WS 帧（文本/二进制/关闭/心跳）→ 原样转发后端；
 * 2. 空闲超时（IdleStateEvent，可配 HXAPI_WS_IDLE_TIMEOUT / -Dws.idle.timeout）→ 关闭两端；
 * 3. 前端断开 → 关闭后端（连接传播）。
 */
public class WebSocketFrontendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketFrontendHandler.class);
    private final Channel backendChannel;

    public WebSocketFrontendHandler(Channel backendChannel) {
        this.backendChannel = backendChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof PingWebSocketFrame) {
                // 前端心跳：回 Pong + 透传后端（保持后端连接活性）
                ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
                backendChannel.writeAndFlush(frame.retain());
                return;
            }
            // 帧引用计数：retain 后跨 channel 转发
            backendChannel.writeAndFlush(frame.retain());
        } else {
            io.netty.util.ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            logger.info("WebSocket 前端连接空闲超时({}s)，关闭双向连接", HXAPIGateConext.WS_IDLE_TIMEOUT_SECONDS);
            ctx.close();
            backendChannel.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("websocket frontend channel closed, close backend");
        backendChannel.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("前端 websocket 连接异常", cause);
        ctx.close();
        backendChannel.close();
    }
}
