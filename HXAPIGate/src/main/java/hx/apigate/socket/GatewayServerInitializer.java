/**
 * Copyright (C) 2018 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hx.apigate.socket;

import hx.apigate.authorization.AuthorizationHandler;
import hx.apigate.socket.handlers.GatewayServerHandler;
import hx.apigate.socket.handlers.TranceDataHandler;
import hx.apigate.util.HXAPIGateConext;
import hx.apigate.util.MixAll;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * <p>Description: </p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月31日
　 * @version 1.0
 */
public class GatewayServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        // 请求体大小前置预检：在聚合器之前基于 Content-Length 快速拦截超限请求（返回带说明的 413）
        p.addLast(new GatewayContentLengthPrecheckHandler());
        // 自定义聚合器：请求体超限时返回带说明的 413（Netty 默认仅空 body 413），作为 chunked 请求的兜底
        p.addLast(new GatewayHttpObjectAggregator(HXAPIGateConext.MAX_CONTENT_LENGTH));
        // 出站响应统一附加 X-Trace-Id：置于 GatewayServerHandler 之前，保证 ctx.writeAndFlush 出站路径经过
        p.addLast(new TraceIdOutboundHandler());
        p.addLast(new GatewayServerHandler());
        p.addLast(new AuthorizationHandler());
        p.addLast(new TranceDataHandler());
    }

    /**
     * 请求体聚合器：覆盖超限行为，返回带错误说明的 413 响应。
     * 默认 HttpObjectAggregator 超限仅发送空 body 的 413，客户端无法获知原因。
     */
    static class GatewayHttpObjectAggregator extends HttpObjectAggregator {
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GatewayHttpObjectAggregator.class);

        GatewayHttpObjectAggregator(int maxContentLength) {
            super(maxContentLength);
        }

        @Override
        protected void handleOversizedMessage(ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
            logger.warn("请求体超过聚合上限 {}B（chunked 兜底），返回 413: {} {}", HXAPIGateConext.MAX_CONTENT_LENGTH,
                    oversized instanceof HttpRequest ? ((HttpRequest) oversized).method() : "",
                    oversized instanceof HttpRequest ? ((HttpRequest) oversized).uri() : "");
            if (ctx.channel().isActive()) {
                // getDefaultFullHttpResponse4Error 默认 200 OK + body 错误码，这里把 HTTP 状态码修正为真正的 413
                io.netty.handler.codec.http.FullHttpResponse resp = MixAll.getDefaultFullHttpResponse4Error(413,
                        "request body too large, max " + HXAPIGateConext.MAX_CONTENT_LENGTH + " bytes");
                resp.setStatus(HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE);
                ctx.writeAndFlush(resp)
                        .addListener(io.netty.channel.ChannelFutureListener.CLOSE);
            } else {
                ctx.close();
            }
        }
    }
}

