package hx.apigate.socket.handlers;

import hx.apigate.util.MixAll;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * MasterBackHandler 双模式单元测试：
 * 透传模式（MCP/SSE 流式）与包装模式（RetMessage 统一格式）
 */
public class MasterBackHandlerTest {

    private EmbeddedChannel newWebChannel(boolean transparent) {
        EmbeddedChannel web = new EmbeddedChannel();
        web.attr(MixAll.ATTRIBUTEKEY_TRANSPARENT).set(transparent);
        return web;
    }

    @Test
    public void 透传模式_SSE流式原样转发_含业务头透传() {
        EmbeddedChannel web = newWebChannel(true);
        EmbeddedChannel backend = new EmbeddedChannel(new MasterBackHandler(web));

        // 响应头：SSE 类型 + MCP 会话头
        DefaultHttpResponse head = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        head.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
        head.headers().set(HttpHeaderNames.TRANSFER_ENCODING, "chunked");
        head.headers().set("Mcp-Session-Id", "sess-123");
        backend.writeInbound(head);

        HttpResponse outHead = web.readOutbound();
        assertNotNull("透传模式应先写出响应头", outHead);
        assertEquals(HttpResponseStatus.OK, outHead.status());
        assertEquals("text/event-stream", outHead.headers().get(HttpHeaderNames.CONTENT_TYPE));
        assertEquals("sess-123", outHead.headers().get("Mcp-Session-Id"));
        assertNull("hop-by-hop 头 Transfer-Encoding 应剔除", outHead.headers().get(HttpHeaderNames.TRANSFER_ENCODING));

        // 两个事件 chunk + 结束
        backend.writeInbound(new DefaultHttpContent(Unpooled.copiedBuffer("data: hello\n\n", CharsetUtil.UTF_8)));
        backend.writeInbound(new DefaultHttpContent(Unpooled.copiedBuffer("data: world\n\n", CharsetUtil.UTF_8)));
        backend.writeInbound(LastHttpContent.EMPTY_LAST_CONTENT);

        HttpContent c1 = web.readOutbound();
        assertEquals("data: hello\n\n", c1.content().toString(CharsetUtil.UTF_8));
        HttpContent c2 = web.readOutbound();
        assertEquals("data: world\n\n", c2.content().toString(CharsetUtil.UTF_8));
        HttpContent last = web.readOutbound();
        assertTrue("透传模式以 LastHttpContent 结束", last instanceof LastHttpContent);
    }

    @Test
    public void 透传模式_错误状态码保留() {
        EmbeddedChannel web = newWebChannel(true);
        EmbeddedChannel backend = new EmbeddedChannel(new MasterBackHandler(web));
        backend.writeInbound(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR));
        HttpResponse out = web.readOutbound();
        assertEquals("透传模式 500 状态码原样保留", HttpResponseStatus.INTERNAL_SERVER_ERROR, out.status());
    }

    @Test
    public void 透传模式_非JSON文本原样转发() {
        EmbeddedChannel web = newWebChannel(true);
        EmbeddedChannel backend = new EmbeddedChannel(new MasterBackHandler(web));
        DefaultHttpResponse head = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        head.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=utf-8");
        backend.writeInbound(head);
        backend.writeInbound(new DefaultHttpContent(Unpooled.copiedBuffer("纯文本内容，非 JSON 也能透传", CharsetUtil.UTF_8)));
        backend.writeInbound(LastHttpContent.EMPTY_LAST_CONTENT);

        HttpResponse outHead = web.readOutbound();
        assertEquals("text/plain; charset=utf-8", outHead.headers().get(HttpHeaderNames.CONTENT_TYPE));
        HttpContent body = web.readOutbound();
        assertEquals("纯文本内容，非 JSON 也能透传", body.content().toString(CharsetUtil.UTF_8));
    }

    @Test
    public void 包装模式_聚合后RetMessage统一包装() {
        EmbeddedChannel web = newWebChannel(false);
        EmbeddedChannel backend = new EmbeddedChannel(new MasterBackHandler(web));

        backend.writeInbound(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
        backend.writeInbound(new DefaultHttpContent(Unpooled.copiedBuffer("{\"name\":\"hxapi\"}", CharsetUtil.UTF_8)));
        backend.writeInbound(LastHttpContent.EMPTY_LAST_CONTENT);

        FullHttpResponse out = web.readOutbound();
        assertNotNull("包装模式应输出完整响应", out);
        String body = out.content().toString(CharsetUtil.UTF_8);
        assertTrue("RetMessage 统一格式: " + body, body.contains("\"code\"") && body.contains("\"data\""));
        assertTrue("data 内容包含后端返回值: " + body, body.contains("hxapi"));
    }
}
