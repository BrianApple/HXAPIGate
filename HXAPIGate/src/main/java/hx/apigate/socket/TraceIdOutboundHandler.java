package hx.apigate.socket;

import hx.apigate.util.MixAll;
import hx.apigate.util.TraceUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 出站响应统一附加 X-Trace-Id 响应头（请求溯源 ID 回传调用方，便于日志联查）。
 * 放在 pipeline 尾部，所有 FullHttpResponse 写出前自动带上当前 channel 的 traceId。
 */
public class TraceIdOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof FullHttpResponse) {
            String traceId = ctx.channel().attr(MixAll.ATTRIBUTEKEY_TRACE_ID).get();
            if (traceId != null && !traceId.isEmpty()) {
                ((FullHttpResponse) msg).headers().set(TraceUtil.HEADER_X_TRACE_ID, traceId);
            }
        }
        super.write(ctx, msg, promise);
    }
}
