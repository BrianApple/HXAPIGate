package hx.apigate.socket;

import hx.apigate.socket.handlers.WebSocketBackendHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;

/**
 * 后端 WebSocket 服务连接的 pipeline 初始化器。
 * <p>Description: 与 BackendHandlerInitializer 的区别：完成后端标准 WS 客户端握手，
 * 握手成功后由 WebSocketBackendHandler 触发前端升级并建立双向帧透传。</p>
 * <p>Company: www.uiotp.com</p>
 */
public class WebSocketBackendInitializer extends ChannelInitializer<SocketChannel> {

    final Channel inboundChannel;
    final FullHttpRequest handshakeRequest;
    final String backendPath;

    public WebSocketBackendInitializer(Channel inboundChannel, FullHttpRequest handshakeRequest, String backendPath) {
        this.inboundChannel = inboundChannel;
        this.handshakeRequest = handshakeRequest;
        this.backendPath = backendPath;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        // 后端 WS 地址：ws://ip:port<path>，path 取前端请求的 URI 路径（去掉 query）
        String host = ch.remoteAddress() != null ? ch.remoteAddress().getHostString() : "127.0.0.1";
        int port = ch.remoteAddress() != null ? ch.remoteAddress().getPort() : 80;
        String wsPath = (backendPath == null || backendPath.isEmpty()) ? "/" : backendPath;
        URI wsUri = URI.create("ws://" + host + ":" + port + wsPath);
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                wsUri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
        ch.pipeline().addLast(new HttpClientCodec())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new WebSocketClientProtocolHandler(handshaker, true))
                .addLast(new WebSocketBackendHandler(inboundChannel, handshakeRequest, handshaker));
    }
}
