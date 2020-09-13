package hx.apigate.util;

import java.nio.charset.Charset;

import org.jboss.netty.util.internal.ByteBufferUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import hx.apigate.databridge.RetMessage;
import hx.apigate.socket.Constance;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponseUtil {
	/**
	 * dubboResponse
	 * dubbo协议返回数据  由该方法响应客户端
	 * @param webChannel
	 * @param retMsg
	 */
    public static void responseMsg(Channel webChannel,Object retMsg) {
        
        
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
   	 
	   	response.content().writeBytes(JSON.toJSONBytes(new RetMessage().ok(200, retMsg), SerializerFeature.PrettyFormat));
	   	HttpHeaders heads = response.headers();
	   	heads.add(HttpHeaderNames.CONTENT_TYPE, new StringBuilder(HttpHeaderValues.APPLICATION_JSON).append("; charset=UTF-8") );
	   	heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
	   	heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
	   	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
	   	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"*");//允许headers自定义
	   	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"GET, POST, PUT,DELETE");
	   	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS,"true");
	   	webChannel.writeAndFlush(response) ;
    }
    /**
     * httpResponse
     * http透传返回的数据 添加网关统一的响应标识
     * @param webChannel
     * @param retMsg
     */
    public static void responseMsg4Http(Channel webChannel,FullHttpResponse retResponse) {
    	
    	DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
    	response.content().writeBytes(JSON.toJSONBytes(new RetMessage().ok(200, JSON.parse(retResponse.content().toString(Charset.forName(Constance.ENCODING)))), SerializerFeature.PrettyFormat));
    	HttpHeaders heads = response.headers();
    	heads.add(HttpHeaderNames.CONTENT_TYPE, new StringBuilder(HttpHeaderValues.APPLICATION_JSON).append("; charset=UTF-8") );
    	heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
    	heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
    	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"*");//允许headers自定义
    	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"GET, POST, PUT,DELETE");
    	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS,"true");
    	webChannel.writeAndFlush(response) ;
    }
}
