package hx.apigate.socket.handlers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.util.HttpResponseUtil;
import hx.apigate.util.MixAll;
import hx.apigate.util.TraceUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Description: 响应http（双模式）</p>
 * <p>1. 透传模式（默认，HTTP 代理原样转发）：HttpResponse 头 + HttpContent chunk 原样流式转发到 web 端，
 *        保留上游状态码与业务 headers，支持 text/event-stream 增量推送（MCP/SSE 场景）；
 *        请求头 X-HXAPI-Transparent: false 可回退到包装模式。</p>
 * <p>2. 包装模式（兼容旧行为）：聚合完整响应后按 RetMessage 统一格式包装（JSON）。</p>
 * 　 * <p>Copyright: Copyright (c) 2019</p>
 * 　 * <p>Company: www.uiotp.com</p>
 * 　 * @author yangcheng,hjj
 * 　 * @date 2019年10月30日
 * 　 * @version 1.0
 */
public class MasterBackHandler extends SimpleChannelInboundHandler<HttpObject> {
	Logger logger = LoggerFactory.getLogger(MasterBackHandler.class);

	/** 非透传模式聚合上限（16MB，防内存膨胀；原 HttpObjectAggregator 为 1MB） */
	private static final int MAX_AGGREGATE_LENGTH = 16 * 1024 * 1024;
	/** hop-by-hop 头，透传时必须剔除（由 Netty 按连接语义重新处理）。
	 *  注意：transfer-encoding 不能剔除——透传模式依赖它让 Netty HttpObjectEncoder 输出正确的 chunked 帧，
	 *  否则响应变成"无 Content-Length、无 chunked、无 close"三无响应，客户端永远等不到响应结束（挂起超时）。 */
	private static final Set<String> HOP_BY_HOP_HEADERS = new HashSet<>(Arrays.asList(
			"connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
			"te", "trailer", "upgrade"));

	/**
	 * web端channel
	 */
    private final Channel inboundChannel;

    private volatile boolean transparent = false;
    private volatile boolean headersSent = false;
    // 包装模式聚合态
    private HttpResponseStatus pendingStatus;
    private ByteBuf pendingBody;

    public MasterBackHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Boolean flag = inboundChannel.attr(MixAll.ATTRIBUTEKEY_TRANSPARENT).get();
        transparent = flag != null && flag;
        logger.debug("toMaster Handler is Active! transparent={}", transparent);
    }
    /**
     * 获取到微服务响应（透传或聚合包装）
     */
    @Override
    public void channelRead0(final ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    	if (msg instanceof HttpResponse) {
    		handleResponseHead(ctx, (HttpResponse) msg);
    	} else if (msg instanceof HttpContent) {
    		handleContent(ctx, (HttpContent) msg);
    	}
    }

    /** 响应头到达 */
    private void handleResponseHead(ChannelHandlerContext ctx, HttpResponse resp) {
    	if (transparent) {
    		// 透传：复制状态码 + 业务头（剔除 hop-by-hop），原样写给 web 端
    		DefaultHttpResponse out = new DefaultHttpResponse(resp.protocolVersion(), resp.status());
    		copyTransparentHeaders(resp.headers(), out.headers());
    		// 兜底：后端响应既无 Content-Length 也无 chunked 时（极端情况），
    		// 强制 Connection: close，让客户端以连接关闭判断响应结束，避免无限挂起
    		if (!resp.headers().contains(HttpHeaderNames.CONTENT_LENGTH)
    				&& !HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase(resp.headers().get(HttpHeaderNames.TRANSFER_ENCODING))) {
    			out.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
    		}
    		inboundChannel.writeAndFlush(out);
    		headersSent = true;
    		// 响应链路可查：透传模式打印状态码（body 流式逐 chunk 转发，见 handleContent）
    		String traceId = inboundChannel.attr(MixAll.ATTRIBUTEKEY_TRACE_ID).get();
    		if (traceId != null) TraceUtil.putTraceId(traceId);
    		TraceUtil.putProto("http");
    		logger.info("HTTP响应[{}] 状态 {}（透传流式）", traceId == null ? "--" : traceId, resp.status().code());
    		TraceUtil.clear();
    	} else {
    		pendingStatus = resp.status();
    		pendingBody = Unpooled.buffer();
    	}
    }

    /** body 分片到达 */
    private void handleContent(ChannelHandlerContext ctx, HttpContent content) {
    	if (transparent) {
    		// 流式透传：retain 后逐 chunk flush（低延迟，SSE 逐事件可达）；SimpleChannelInboundHandler 会自动释放原 msg
    		inboundChannel.writeAndFlush(content.retain());
    		if (content instanceof LastHttpContent) {
    			ctx.close(); // 响应结束，关闭后端连接（每请求新建，避免泄漏）
    		}
    	} else {
    		if (pendingBody == null) {
    			pendingBody = Unpooled.buffer();
    		}
    		if (pendingBody.readableBytes() + content.content().readableBytes() > MAX_AGGREGATE_LENGTH) {
    			// 超限：丢弃并返回 413（与旧 aggregator 超限行为等价，但改为显式响应）
    			logger.warn("网关聚合响应超限 {}B，返回 413", MAX_AGGREGATE_LENGTH);
    			pendingBody.release();
    			pendingBody = null;
    			pendingStatus = null;
    			inboundChannel.writeAndFlush(new DefaultHttpResponse(
    					HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE));
    			inboundChannel.writeAndFlush(new DefaultLastHttpContent());
    			ctx.close();
    			return;
    		}
    		pendingBody.writeBytes(content.content());
    		if (content instanceof LastHttpContent) {
    			HttpResponseStatus status = pendingStatus != null ? pendingStatus : HttpResponseStatus.OK;
    			// 响应链路可查：聚合模式打印状态码 + 响应体内容（截断预览）
    			String traceId = inboundChannel.attr(MixAll.ATTRIBUTEKEY_TRACE_ID).get();
    			if (traceId != null) TraceUtil.putTraceId(traceId);
    			TraceUtil.putProto("http");
    			logger.info("HTTP响应[{}] 状态 {}, 内容: {}", traceId == null ? "--" : traceId, status.code(), MixAll.describeBytes(pendingBody));
    			TraceUtil.clear();
    			FullHttpResponse full = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, pendingBody);
    			try {
    				// 旧逻辑：RetMessage 统一包装（JSON）
    				HttpResponseUtil.responseMsg4Http(inboundChannel, full);
    			} finally {
    				full.release();
    				pendingBody = null;
    				pendingStatus = null;
    			}
    			ctx.close();
    		}
    	}
    }

    /** 复制业务头（剔除 hop-by-hop + Connection 语义），补网关 CORS 头 */
    private void copyTransparentHeaders(HttpHeaders from, HttpHeaders to) {
    	for (String name : from.names()) {
    		String lower = name.toLowerCase();
    		if (HOP_BY_HOP_HEADERS.contains(lower)) {
    			continue;
    		}
    		to.set(name, from.getAll(name));
    	}
    	to.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    	to.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    	to.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
    	to.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE");
    	to.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("toMaster Handler destroyed!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("网关与微服务端http连接异常", cause);
        if (!headersSent) {
        	// 头未发出：可安全返回 502
        	inboundChannel.writeAndFlush(new DefaultHttpResponse(
        			HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_GATEWAY));
        	inboundChannel.writeAndFlush(new DefaultLastHttpContent());
        } else {
        	// 头已发出（流式中断）：直接断开 web 连接
        	inboundChannel.close();
        }
        ctx.deregister();
    }
}
