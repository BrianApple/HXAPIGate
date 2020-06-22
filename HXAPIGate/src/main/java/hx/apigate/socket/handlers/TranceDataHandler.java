
package hx.apigate.socket.handlers;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import hx.apigate.base.Message;
import hx.apigate.databridge.NodeInfo;
import hx.apigate.databridge.xmlBean.RouteNode;
import hx.apigate.dubbo.SpringContextHelper;
import hx.apigate.hxqueue.HXMQ;
import hx.apigate.socket.BackendHandlerInitializer;
import hx.apigate.socket.Constance;
import hx.apigate.util.LocalCache;
import hx.apigate.util.MixAll;
import hx.apigate.util.RouteSelectUtil;

/**
 * <p>Description: 透传数据
 * </p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月31日
　 * @version 1.0
 */
public class TranceDataHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	Logger logger = LoggerFactory.getLogger(TranceDataHandler.class);
	private Channel toMasterChannel;
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final FullHttpRequest msg) {
    	 final Channel webChannel = ctx.channel();
    	 String gateHost = msg.headers().get("Host");
    	 NodeInfo nodeInfo = webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).get();
    	
    	 if(RouteSelectUtil.HTTP.equals(nodeInfo.getProtocalTemp())) {
    		 
    		 String pattern = webChannel.attr(MixAll.ATTRIBUTEKEY_URL).get();
    		 Bootstrap b = new Bootstrap();
    		 b.option(ChannelOption.SO_KEEPALIVE, false)
    		 .group(webChannel.eventLoop()) 
    		 .channel(NioSocketChannel.class)
    		 .handler(new BackendHandlerInitializer(webChannel));
    		 ChannelFuture f = b.connect(nodeInfo.getRouteNode().getIp(), nodeInfo.getRouteNode().getPort());
    		 toMasterChannel = f.channel();
    		 msg.retain();
    		 ChannelFuture channelFuture = f.addListener(new ChannelFutureListener() {
    			 public void operationComplete(ChannelFuture future) throws Exception {
    				 if (future.isSuccess()) {
    					 toMasterChannel.writeAndFlush(msg);
    				 } else {
    					 NodeInfo nodeInfo = RouteSelectUtil.getRouteByPattern(null,null ,pattern);
    					 logger.error(MixAll.LOG_INFO_PRIFEX+String.format("网关[%s]访问url%s 失败，请求再次转发至路由%s:%s ",gateHost,msg.uri(),nodeInfo.getRouteNode().getIp(),String.valueOf(nodeInfo.getRouteNode().getPort())) );
    					 Bootstrap b = new Bootstrap();
    					 b.option(ChannelOption.SO_KEEPALIVE, false)
    					 .group(webChannel.eventLoop())
    					 .channel(NioSocketChannel.class)
    					 .handler(new BackendHandlerInitializer(webChannel));
    					 ChannelFuture f = b.connect(nodeInfo.getRouteNode().getIp(), nodeInfo.getRouteNode().getPort());//连接到微服务端
    					 toMasterChannel = f.channel();
    					 msg.retain();
    					 ChannelFuture channelFuture = f.addListener(new ChannelFutureListener() {
    						 public void operationComplete(ChannelFuture future) throws Exception {
    							 if (future.isSuccess()) {
    								 toMasterChannel.writeAndFlush(msg);
    							 } else {
    								 logger.error(MixAll.LOG_INFO_PRIFEX+String.format("网关[%s]访问url%s,切换路由至%s:%s重试失败 返回异常信息到web端 ",gateHost,msg.uri(),nodeInfo.getRouteNode().getIp(),nodeInfo.getRouteNode().getPort()) );
    								 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse(404, "The path you accessed does not work !"));
    							 }
    						 }
    					 });
    				 }
    			 }
    		 });
    	 }else {
    		 HXMQ hxMQ= SpringContextHelper.getBean(Constance.HX_MQ);
    		 ByteBuf bufferContent = msg.content().copy();
    		 hxMQ.sendRunnable(new Runnable() {
				
				@Override
				public void run() {
					GenericService genericService= SpringContextHelper.getBean(nodeInfo.getRouteNode().getInterfaceName());
		    		 if(genericService != null) {
		    			 
		    			 Map<String,Object> allRequestParams = new HashMap();
		    			 Map<String,String> headers = new HashMap();
		    			 
		    			 Iterator<Entry<String, String>> ite = msg.headers().iteratorAsString();
		    			 HttpMethod method = msg.method();
		    			 while(ite.hasNext()) {
		    				 Entry<String, String> entry =ite.next();
		    				 headers.put(entry.getKey(), entry.getValue());
		    			 }
		    			 if( bufferContent.isReadable()) {
		    				 String content = bufferContent.toString(Charset.forName(Constance.ENCODING));
		    				 if(method.equals(Constance.HTTP_GET_REQUEST) && content != null && content.contains("=")) {
		    					 Map<String,Object> contentParams = new HashMap();
		    					String[] formParams =  content.split("\\&");
		    					int len = formParams.length;
		    					for(int i = 0  ; i < len ;i++) {
		    						String[] entry =  formParams[i].split("\\=");
			    					 try {
			    						 contentParams.put( URLDecoder.decode(entry[0], Constance.ENCODING), URLDecoder.decode(entry[1], Constance.ENCODING));
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									}
		    					}
		    					allRequestParams.put("contentParams", contentParams);
		    				 }else if(!method.equals(Constance.HTTP_GET_REQUEST) && content != null){
		    					 Map map = (Map) JSON.parse(content, Feature.AllowUnQuotedFieldNames);
		    					 allRequestParams.put("contentParams", map);
		    					 ReferenceCountUtil.release(bufferContent);
		    				 }
		    			 }
		    			 String sourceUrl = nodeInfo.getRequestUrl();
		    			 String[]  sourceUrlArray = sourceUrl.split("\\?");
		    			 if(sourceUrlArray.length  == 2) {
		    				 
		    				 allRequestParams.put("requestUrl", sourceUrlArray[0]);
		    				 String[] level01 = sourceUrlArray[1].split("\\&");
		    				 int level01Size = level01.length;
		    				 Map<String,Object> requestParams = new HashMap();
		    				 for(int i = 0 ; i <level01Size ; i ++ ) {
		    					 String[] entry =  level01[i].split("\\=");
		    					 try {
									requestParams.put( URLDecoder.decode(entry[0], Constance.ENCODING), URLDecoder.decode(entry[1], Constance.ENCODING));
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
		    				 }
		    				 
		    				 allRequestParams.put("requestParams", requestParams);
		    			 }
		    			 
		    			 allRequestParams.put("headers", headers);
		    			 try {
		    				Object retMsg = genericService.$invoke(nodeInfo.getMethodName(), new String[] {"java.util.Map"}, new Object[] {allRequestParams});
		    				responseMsg(webChannel,retMsg);
						} catch (Exception e) {
							 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse(404, "The path you accessed failed to execute !"));
						}
		    			 
		    		 }else {
		    			 logger.error(MixAll.LOG_INFO_PRIFEX+String.format("网关[%s]访问url%s不存在",gateHost,msg.uri()) );
						 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse(404, "The path you accessed does not work !"));
		    		 }
				}
			});
    	 }
    }

    private void responseMsg(Channel webChannel,Object retMsg) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
   	 
	   	response.content().writeBytes(JSON.toJSONBytes(retMsg, SerializerFeature.PrettyFormat));
	   	HttpHeaders heads = response.headers();
	   	heads.add(HttpHeaderNames.CONTENT_TYPE, new StringBuilder(HttpHeaderValues.APPLICATION_JSON).append("; charset=UTF-8") );
	   	heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
	   	heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
	   	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
	   	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"*");
	   	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"GET, POST, PUT,DELETE");
	   	heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS,"true");
	   	webChannel.writeAndFlush(response) ;
    }
    
    
    /**
     * 发送失败之后重试
     */
    @Deprecated
	private void retrySendMsg(RouteNode node,FullHttpRequest msg) {
		 Channel masterChannel = LocalCache.masterChannelCache.get(node.getIp());
		 String sourceId = msg.headers().get(Constance.HXAPIGate_SOURCE_ID);
    	 Channel webChannel = LocalCache.webChannelCache.get(sourceId);
		 String gateHost = msg.headers().get("Host");
		 if(webChannel != null && masterChannel != null ) {
        	 msg.retain();
        	 masterChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {

     			@Override
     			public void operationComplete(ChannelFuture future) throws Exception {
     				if(!future.isSuccess()) {
     					logger.error(MixAll.LOG_INFO_PRIFEX+String.format("网关[%s]访问url%s,切换路由至%s:%s重试失败 返回异常信息到web端 ",gateHost,msg.uri(),node.getIp(),node.getPort()) );
     					webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse(404, "The path you accessed does not exist !"));
     				}
     			}});
         }else if(webChannel != null && masterChannel == null ){
        	 Bootstrap b = new Bootstrap();
             b.option(ChannelOption.SO_KEEPALIVE, true)
             .group(webChannel.eventLoop())
             .channel(NioSocketChannel.class)
             .handler(new BackendHandlerInitializer(null));
             ChannelFuture f = b.connect(node.getIp(), node.getPort());
             Channel toMasterChannel = f.channel();
             msg.retain();
             ChannelFuture channelFuture = f.addListener(new ChannelFutureListener() {
	             public void operationComplete(ChannelFuture future) throws Exception {
	                 if (future.isSuccess()) {
	                	 toMasterChannel.writeAndFlush(msg);
	                 } else {
	                    	 logger.error(MixAll.LOG_INFO_PRIFEX+String.format("网关[%s]访问url%s,切换路由至%s:%s重试失败 返回异常信息到web端 ",gateHost,msg.uri(),node.getIp(),node.getPort()) );
	                    	 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse(404, "The path you accessed does not exist !"));
	                 }
	             }
             });
         }
	}

}

