package hx.apigate.socket;

import hx.apigate.socket.GatewayServerInitializer.GatewayHttpObjectAggregator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 验证请求体超限拦截行为：
 * 1) 前置预检（GatewayContentLengthPrecheckHandler）：基于 Content-Length 快速拦截，返回 413 + 说明；
 * 2) 聚合器兜底（GatewayHttpObjectAggregator）：chunked/无 Content-Length 超限时返回 413 + 说明。
 */
public class GatewayServerInitializerTest {

    /** 记录下游收到的消息，用于验证正常请求被放行 */
    static class RecorderHandler extends ChannelInboundHandlerAdapter {
        final List<Object> received = new ArrayList<>();
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            received.add(msg);
        }
    }

    @Test
    public void 前置预检_ContentLength超限_返回413并关闭() {
        EmbeddedChannel ch = new EmbeddedChannel(new GatewayContentLengthPrecheckHandler());
        // 默认上限 16MB，构造 16MB+1 的 Content-Length 头
        DefaultHttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/upload/echo");
        req.headers().set(HttpHeaderNames.CONTENT_LENGTH, hx.apigate.util.HXAPIGateConext.MAX_CONTENT_LENGTH + 1L);
        req.headers().set(HttpHeaderNames.CONTENT_TYPE, "multipart/form-data; boundary=xxx");

        ch.writeInbound(req);

        FullHttpResponse resp = ch.readOutbound();
        assertNotNull("应返回 413 响应", resp);
        assertEquals(HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, resp.status());
        String body = resp.content().toString(CharsetUtil.UTF_8);
        System.out.println("预检 413 body=" + body);
        assertTrue("响应应包含错误说明", body.contains("too large"));
        assertFalse("超限后连接应关闭", ch.isOpen());
        ch.finish();
    }

    @Test
    public void 前置预检_正常请求_放行到下游() {
        RecorderHandler recorder = new RecorderHandler();
        EmbeddedChannel ch = new EmbeddedChannel(new GatewayContentLengthPrecheckHandler(), recorder);
        DefaultHttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/upload/echo");
        req.headers().set(HttpHeaderNames.CONTENT_LENGTH, 1024);

        ch.writeInbound(req);

        assertEquals("正常请求应放行到下游", 1, recorder.received.size());
        assertTrue(ch.isOpen());
        ch.finish();
    }

    @Test
    public void 前置预检_无ContentLength_放行() {
        RecorderHandler recorder = new RecorderHandler();
        EmbeddedChannel ch = new EmbeddedChannel(new GatewayContentLengthPrecheckHandler(), recorder);
        DefaultHttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/upload/echo");
        // 不带 Content-Length（chunked），不应拦截

        ch.writeInbound(req);

        assertEquals("chunked 请求应放行给聚合器兜底", 1, recorder.received.size());
        ch.finish();
    }

    @Test
    public void 聚合器兜底_超限_返回自定义413() {
        // 注意：必须按真实场景分帧输入（HttpRequest 头帧 + HttpContent body 帧）。
        // EmbeddedChannel 喂单帧 FullHttpRequest 不会触发聚合器的超限检查（该形态只存在于聚合器输出端）。
        EmbeddedChannel ch = new EmbeddedChannel(new GatewayHttpObjectAggregator(1024)); // 1KB 上限
        DefaultHttpRequest head = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/upload/echo");
        head.headers().set(HttpHeaderNames.CONTENT_LENGTH, 2048);
        head.headers().set(HttpHeaderNames.CONTENT_TYPE, "multipart/form-data; boundary=xxx");

        ch.writeInbound(head);

        Object out = ch.readOutbound();
        assertNotNull("应返回 413 响应", out);
        assertTrue("应为 FullHttpResponse", out instanceof FullHttpResponse);
        FullHttpResponse resp = (FullHttpResponse) out;
        assertEquals(HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, resp.status());
        String bodyStr = resp.content().toString(CharsetUtil.UTF_8);
        System.out.println("聚合器兜底 413 body=" + bodyStr);
        assertTrue("响应应包含错误说明", bodyStr.contains("too large"));
        ch.finish();
    }
}
