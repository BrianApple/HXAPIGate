
package hx.apigate.socket.handlers;


import hx.apigate.circuitBreaker.CBManager;
import hx.apigate.databridge.CircleBreakException;
import hx.apigate.util.*;
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
import org.apache.ignite.IgniteSemaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import hx.apigate.databridge.NodeInfo;
import hx.apigate.databridge.SemphareException;
import hx.apigate.databridge.xmlBean.RouteNode;
import hx.apigate.dubbo.util.DubboServiceFactory;
import hx.apigate.hxqueue.HXUnlockedMQ;
import hx.apigate.socket.BackendHandlerInitializer;
import hx.apigate.socket.Constance;

/**
 * <p>Description: 透传数据，
 * </p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng,hjj
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
    		 
    		 String patternUri = webChannel.attr(MixAll.ATTRIBUTEKEY_URL).get();
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
    				 try {
    					 if (future.isSuccess()) {
							 CBManager manager= IgniteUtil.getCircleBreakCache().get(nodeInfo.getCircleBreakKey());
							 manager.getState().postMethodExecute();
    						 toMasterChannel.writeAndFlush(msg);
    					 } else {
    						 try {
								 CBManager manager= IgniteUtil.getCircleBreakCache().get(nodeInfo.getCircleBreakKey());
								 manager.getState().ActUponException();
    							 NodeInfo nextNodeInfo = RouteSelectUtil.getRouteByPattern(nodeInfo.getRequestUrl(),null ,patternUri);
    							 logger.error(MixAll.LOG_INFO_PRIFEX+String.format("网关[%s]访问url%s 失败，请求再次转发至路由%s:%s ",gateHost,msg.uri(),nextNodeInfo.getRouteNode().getIp(),String.valueOf(nodeInfo.getRouteNode().getPort())) );
    							 Bootstrap b = new Bootstrap();
    							 b.option(ChannelOption.SO_KEEPALIVE, false)
    							 .group(webChannel.eventLoop()) 
    							 .channel(NioSocketChannel.class)
    							 .handler(new BackendHandlerInitializer(webChannel));
    							 ChannelFuture f = b.connect(nextNodeInfo.getRouteNode().getIp(), nextNodeInfo.getRouteNode().getPort());//连接到微服务端
    							 toMasterChannel = f.channel();
    							 msg.retain();
    							 ChannelFuture channelFuture = f.addListener(new ChannelFutureListener() {
    								 public void operationComplete(ChannelFuture future) throws Exception {
    									 try {
											 CBManager manager= IgniteUtil.getCircleBreakCache().get(nextNodeInfo.getCircleBreakKey());
    										 if (future.isSuccess()) {
												 manager.getState().postMethodExecute();
    											 toMasterChannel.writeAndFlush(msg);
    										 } else {
												 manager.getState().ActUponException();//失败计数
    											 logger.error(MixAll.LOG_INFO_PRIFEX+String.format("网关[%s]访问url%s,切换路由至%s:%s重试失败 返回异常信息到web端 ",gateHost,msg.uri(),nodeInfo.getRouteNode().getIp(),nodeInfo.getRouteNode().getPort()) );
    											 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(502, "The path you accessed does not work !"));
    										 }
    									 } finally {
    										 IgniteSemaphore semaphore = RouteSelectUtil.selectRouteByUri(patternUri,nextNodeInfo.getInterfaceVserion());
    										 if(semaphore != null) {
    											 semaphore.release();
    										 }
											 nextNodeInfo.getRouteNode().getTps().release();
    									 }
    								 }
    							 });
							} catch (SemphareException e) {
								 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(503, e.getMsg()));
							} catch (CircleBreakException e) {
								 IgniteSemaphore semaphore = RouteSelectUtil.selectRouteByUri(patternUri,nodeInfo.getInterfaceVserion());
								 if(semaphore != null) {
									 semaphore.release();
								 }
								 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(503, e.getMsg()));
							 }
    					 }
					} finally {
						IgniteSemaphore semaphore = RouteSelectUtil.selectRouteByUri(patternUri,nodeInfo.getInterfaceVserion());
						 if(semaphore != null) {
							 semaphore.release();
						 }
						webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).get().getRouteNode().getTps().release();
					}
    			 }
    		 });
    	 }else {
    		 //dubbo
    		 ByteBuf bufferContent = msg.content().copy();
    		 HXUnlockedMQ.sendRunnable(new Runnable() {
				
				@Override
				public void run() {
					 DubboServiceFactory dubbo = DubboServiceFactory.getInstance();
					if (/* genericService != null || */ dubbo != null) {
		    			 
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
		    				 ReferenceCountUtil.release(bufferContent);
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
		    					 //异步post请求
		    					 Map map = (Map) JSON.parse(content, Feature.AllowUnQuotedFieldNames);
		    					 allRequestParams.put("contentParams", map);
		    					
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
		    			 CBManager manager= IgniteUtil.getCircleBreakCache().get(nodeInfo.getCircleBreakKey());
		    			 try {
		    				Object retMsg = dubbo.genericInvoke(nodeInfo.getRouteNode().getInterfaceName(), nodeInfo.getMethodName(), new Object[] {allRequestParams});//(nodeInfo.getMethodName(), new String[] {"java.util.Map"}, new Object[] {allRequestParams});

							 manager.getState().postMethodExecute();
//		    				 System.out.println("请求参数==="+JSON.toJSONString(allRequestParams));
//		    				System.out.println("响应参数==="+JSON.toJSONString(retMsg));
		    				HttpResponseUtil.responseMsg(webChannel,retMsg);
		    				
						} catch (Exception e) {
							 manager.getState().ActUponException();
							e.printStackTrace();
							 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(404, "The path you accessed failed to execute !"));
						}finally {
							String patternUri = webChannel.attr(MixAll.ATTRIBUTEKEY_URL).get();
							IgniteSemaphore semaphore = RouteSelectUtil.selectRouteByUri(patternUri,nodeInfo.getInterfaceVserion());
							 if(semaphore != null) {
								 semaphore.release();
							 }
							webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).get().getRouteNode().getTps().release();
						}
		    		 }else {
		    			 ReferenceCountUtil.release(bufferContent);
		    			 try {
		    				 logger.error(MixAll.LOG_INFO_PRIFEX+String.format("网关[%s]访问url%s不存在",gateHost,msg.uri()) );
		    				 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(404, "The path you accessed does not work !"));
						} finally {
							String patternUri = webChannel.attr(MixAll.ATTRIBUTEKEY_URL).get();
							IgniteSemaphore semaphore = RouteSelectUtil.selectRouteByUri(patternUri,nodeInfo.getInterfaceVserion());
							 if(semaphore != null) {
								 semaphore.release();
							 }
							webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).get().getRouteNode().getTps().release();
						}
		    		 }
				}
			});
    	 }
    }
   
}

