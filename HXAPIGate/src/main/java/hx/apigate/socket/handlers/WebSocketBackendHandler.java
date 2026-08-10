package hx.apigate.socket.handlers;

import hx.apigate.util.MixAll;
import hx.apigate.util.TraceUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 后端 WebSocket 连接响应处理器：
 * 1. 客户端握手完成（HANDSHAKE_COMPLETE）→ 对前端连接执行服务端握手，替换前端 HTTP pipeline 为 WS 帧编解码 + 双向转发；
 * 2. 后端 WS 帧 → 原样转发前端；
 * 3. 后端断开 → 关闭前端（连接传播）。
 */
public class WebSocketBackendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketBackendHandler.class);
    private final Channel inboundChannel;
    private final FullHttpRequest handshakeRequest;
    private final WebSocketClientHandshaker clientHandshaker;

    public WebSocketBackendHandler(Channel inboundChannel, FullHttpRequest handshakeRequest,
                                   WebSocketClientHandshaker clientHandshaker) {
        this.inboundChannel = inboundChannel;
        this.handshakeRequest = handshakeRequest;
        this.clientHandshaker = clientHandshaker;
    }

    /** 握手请求引用释放（只释放一次，防双释放） */
    private boolean requestReleased = false;
    private void releaseRequest() {
        if (!requestReleased && handshakeRequest != null) {
            requestReleased = true;
            handshakeRequest.release();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // 后端 WS 握手成功 → 对前端执行服务端握手
            String wsUrl = "ws://" + inboundChannel.remoteAddress();
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(wsUrl, null, true);
            WebSocketServerHandshaker serverHandshaker = wsFactory.newHandshaker(handshakeRequest);
            if (serverHandshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(inboundChannel);
                ctx.channel().close();
                return;
            }
            serverHandshaker.handshake(inboundChannel, handshakeRequest).addListener(future -> {
                if (future.isSuccess()) {
                    // 前端升级成功：替换前端 pipeline 为 WS 帧编解码 + 双向转发 handler
                    io.netty.channel.ChannelPipeline p = inboundChannel.pipeline();
                    if (p.get(HttpRequestDecoder.class) != null) {
                        p.remove(HttpRequestDecoder.class);
                    }
                    if (p.get(HttpResponseEncoder.class) != null) {
                        p.remove(HttpResponseEncoder.class);
                    }
                    if (p.get(HttpObjectAggregator.class) != null) {
                        p.remove(HttpObjectAggregator.class);
                    }
                    p.addLast(new WebSocketFrontendHandler(ctx.channel()));
                    TraceUtil.putTraceId(inboundChannel.attr(MixAll.ATTRIBUTEKEY_TRACE_ID).get());
                    TraceUtil.putProto("websocket");
                    logger.info("WebSocket 代理握手成功: 前端 {} <-> 后端 {}", inboundChannel.remoteAddress(), ctx.channel().remoteAddress());
                } else {
                    logger.error("WebSocket 前端握手失败", future.cause());
                    ctx.channel().close();
                    inboundChannel.close();
                }
                releaseRequest();
            });
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            // 帧引用计数：retain 后跨 channel 转发
            inboundChannel.writeAndFlush(frame.retain());
        } else {
            // 非帧消息（握手期间可能出现的中间产物）释放
            io.netty.util.ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("websocket backend channel closed, close frontend");
        releaseRequest();
        inboundChannel.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("网关与后端 websocket 连接异常", cause);
        releaseRequest();
        inboundChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(502, "backend websocket error !"));
        ctx.close();
        inboundChannel.close();
    }
}
