package hx.apigate.socket;

import hx.apigate.util.HXAPIGateConext;
import hx.apigate.util.MixAll;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 请求体大小前置预检：位于 HttpObjectAggregator 之前，基于 Content-Length 头快速拦截超限请求，
 * 直接返回 413（带错误说明）并关闭连接——不进入聚合器，避免大请求占用内存，且不受
 * HttpObjectAggregator 内部超限处理路径（不同 Netty 版本行为有差异）影响。
 * 对 chunked（无 Content-Length）的请求不拦截，交由聚合器超限兜底处理。
 */
public class GatewayContentLengthPrecheckHandler extends ChannelInboundHandlerAdapter {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GatewayContentLengthPrecheckHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            String cl = req.headers().get(HttpHeaderNames.CONTENT_LENGTH);
            if (cl != null) {
                try {
                    long len = Long.parseLong(cl.trim());
                    if (len > HXAPIGateConext.MAX_CONTENT_LENGTH) {
                        logger.warn("请求体 Content-Length={} 超过上限 {}B，返回 413: {} {}",
                                len, HXAPIGateConext.MAX_CONTENT_LENGTH, req.method(), req.uri());
                        if (ctx.channel().isActive()) {
                            FullHttpResponse resp = MixAll.getDefaultFullHttpResponse4Error(413,
                                    "request body too large, max " + HXAPIGateConext.MAX_CONTENT_LENGTH + " bytes");
                            resp.setStatus(HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE);
                            ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
                        } else {
                            ctx.close();
                        }
                        // 不继续传播，后续 HttpContent 随连接关闭被丢弃
                        return;
                    }
                } catch (NumberFormatException e) {
                    logger.debug("非法 Content-Length 头: {}", cl);
                }
            }
        }
        ctx.fireChannelRead(msg);
    }
}
