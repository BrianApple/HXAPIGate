
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
import hx.apigate.util.RateLimiter;
import hx.apigate.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;

import hx.apigate.databridge.NodeInfo;
import hx.apigate.databridge.SemphareException;
import hx.apigate.databridge.xmlBean.RouteNode;
import hx.apigate.dubbo.util.DubboServiceFactory;
import hx.apigate.hxqueue.HXUnlockedMQ;
import hx.apigate.socket.BackendHandlerInitializer;
import hx.apigate.socket.TcpBackendHandlerInitializer;
import hx.apigate.socket.WebSocketBackendInitializer;
import hx.apigate.socket.Constance;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateHandler;

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
    	 NodeInfo nodeInfo = webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).get();
    	 // 请求溯源：从 channel attr 恢复 traceId 与协议到 MDC（跨 handler 传递）
    	 String traceId = webChannel.attr(MixAll.ATTRIBUTEKEY_TRACE_ID).get();
    	 if (traceId != null) TraceUtil.putTraceId(traceId);
    	 TraceUtil.putProto(nodeInfo == null ? "http" : nodeInfo.getProtocalTemp());
    	 // 内置 MCP 协议转换端点：不转发后端，由 MCP 网关处理器完成 JSON-RPC 分发
    	 if (nodeInfo != null && hx.apigate.mcp.McpGatewayHandler.MCP_GATEWAY_PROTOCOL.equals(nodeInfo.getProtocalTemp())) {
    		 hx.apigate.mcp.McpGatewayHandler.handle(webChannel, msg);
    		 return;
    	 }
    	 String gateHost = msg.headers().get("Host");
   	
   	 if(RouteSelectUtil.HTTP.equals(nodeInfo.getProtocalTemp()) || RouteSelectUtil.MCP.equals(nodeInfo.getProtocalTemp())) {
    		 
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
    				 TraceUtil.putTraceId(webChannel.attr(MixAll.ATTRIBUTEKEY_TRACE_ID).get());
    				 TraceUtil.putProto(nodeInfo.getProtocalTemp());
    				 try {
				 if (future.isSuccess()) {
					 CBManager manager= RedisUtil.getCircleBreakCache().get(nodeInfo.getCircleBreakKey());
					 manager.getState().postMethodExecute();
					 toMasterChannel.writeAndFlush(msg);
				 } else {
					 try {
						 CBManager manager= RedisUtil.getCircleBreakCache().get(nodeInfo.getCircleBreakKey());
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
    									 TraceUtil.putTraceId(webChannel.attr(MixAll.ATTRIBUTEKEY_TRACE_ID).get());
    									 TraceUtil.putProto(nodeInfo.getProtocalTemp());
    									 try {
											 CBManager manager= RedisUtil.getCircleBreakCache().get(nextNodeInfo.getCircleBreakKey());
    										 if (future.isSuccess()) {
												 manager.getState().postMethodExecute();
    											 toMasterChannel.writeAndFlush(msg);
    										 } else {
												 manager.getState().ActUponException();//失败计数
    											 logger.error(MixAll.LOG_INFO_PRIFEX+String.format("网关[%s]访问url%s,切换路由至%s:%s重试失败 返回异常信息到web端 ",gateHost,msg.uri(),nodeInfo.getRouteNode().getIp(),nodeInfo.getRouteNode().getPort()) );
    											 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(502, "The path you accessed does not work !"));
    										 }
    									 } finally {
    										 String routeLimitKey = RouteSelectUtil.selectRouteByUri(patternUri,nextNodeInfo.getInterfaceVserion());
    										 if(routeLimitKey != null) {
    											 RateLimiter.release(routeLimitKey);
    										 }
											 RateLimiter.release(RouteSelectUtil.nodeLimitKey(nextNodeInfo.getRouteNode(), patternUri));
    									 TraceUtil.clear();
    									 }
    								 }
    							 });
							} catch (SemphareException e) {
								 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(503, e.getMsg()));
							} catch (CircleBreakException e) {
								 String routeLimitKey = RouteSelectUtil.selectRouteByUri(patternUri,nodeInfo.getInterfaceVserion());
								 if(routeLimitKey != null) {
									 RateLimiter.release(routeLimitKey);
								 }
								 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(503, e.getMsg()));
							 }
    					 }
					} finally {
						String routeLimitKey = RouteSelectUtil.selectRouteByUri(patternUri,nodeInfo.getInterfaceVserion());
						 if(routeLimitKey != null) {
							 RateLimiter.release(routeLimitKey);
						 }
						RateLimiter.release(RouteSelectUtil.nodeLimitKey(webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).get().getRouteNode(), patternUri));
					TraceUtil.clear();
					}
    			 }
    		 });
    	 }else if(RouteSelectUtil.TCP.equals(nodeInfo.getProtocalTemp())) {
    	 	final String patternUri = webChannel.attr(MixAll.ATTRIBUTEKEY_URL).get();
    	 	final ByteBuf body = msg.content().retainedDuplicate();
    	 	Bootstrap b = new Bootstrap();
    	 	b.option(ChannelOption.SO_KEEPALIVE, false)
    	 	 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
    	 	 .group(webChannel.eventLoop())
    	 	 .channel(NioSocketChannel.class)
    	 	 .handler(new TcpBackendHandlerInitializer(webChannel));
    	 	ChannelFuture f = b.connect(nodeInfo.getRouteNode().getIp(), nodeInfo.getRouteNode().getPort());
    	 	f.addListener(new ChannelFutureListener() {
    	 		public void operationComplete(ChannelFuture future) throws Exception {
    	 			TraceUtil.putTraceId(webChannel.attr(MixAll.ATTRIBUTEKEY_TRACE_ID).get());
    	 			TraceUtil.putProto(nodeInfo.getProtocalTemp());
    	 			try {
    	 				if (future.isSuccess()) {
    	 					RedisUtil.getCircleBreakCache().get(nodeInfo.getCircleBreakKey()).getState().postMethodExecute();
    	 					future.channel().writeAndFlush(body);
    	 				} else {
    	 					body.release();
    	 					RedisUtil.getCircleBreakCache().get(nodeInfo.getCircleBreakKey()).getState().ActUponException();
    	 					webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(502, "The path you accessed does not work !"));
    	 				}
    	 			} catch (Exception e) {
    	 				body.release();
    	 				webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(502, "The path you accessed does not work !"));
    	 			} finally {
    	 				String routeLimitKey = RouteSelectUtil.selectRouteByUri(patternUri, nodeInfo.getInterfaceVserion());
    	 				if(routeLimitKey != null) {
    	 					RateLimiter.release(routeLimitKey);
    	 				}
    	 				RateLimiter.release(RouteSelectUtil.nodeLimitKey(webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).get().getRouteNode(), patternUri));
    	 			TraceUtil.clear();
    	 			}
    	 		}
    	 	});
   	 }else if(RouteSelectUtil.WEBSOCKET.equals(nodeInfo.getProtocalTemp())) {
   		 // WebSocket 透传：前端标准 WS ⇄ 后端标准 WS 服务（帧原样透传，TEXT/BINARY 均支持）
   		 final String patternUri = webChannel.attr(MixAll.ATTRIBUTEKEY_URL).get();
   		 // 取前端请求路径（去掉 query）作为后端 WS 连接路径
   		 String rawUri = msg.uri();
   		 final String backendPath = rawUri.contains("?") ? rawUri.substring(0, rawUri.indexOf('?')) : rawUri;
   		 final RouteNode wsNode = nodeInfo.getRouteNode();
   		 // 握手请求 msg 跨 channelRead0 生命周期使用（WebSocketBackendHandler 服务端握手需要），retain 一次，
   		 // 由 WebSocketBackendHandler 在握手完成/连接断开时 release
   		 msg.retain();
   		 // 前端 pipeline 挂空闲超时（可配，默认 60s），超时后由 WebSocketFrontendHandler 关闭双向连接
   		 webChannel.pipeline().addLast(new IdleStateHandler(HXAPIGateConext.WS_IDLE_TIMEOUT_SECONDS, 0, 0, java.util.concurrent.TimeUnit.SECONDS));
   		 Bootstrap b = new Bootstrap();
   		 b.option(ChannelOption.SO_KEEPALIVE, false)
   		  .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
   		  .group(webChannel.eventLoop())
   		  .channel(NioSocketChannel.class)
   		  .handler(new WebSocketBackendInitializer(webChannel, msg, backendPath));
   		 ChannelFuture f = b.connect(wsNode.getIp(), wsNode.getPort());
   		 f.addListener(new ChannelFutureListener() {
   			 public void operationComplete(ChannelFuture future) throws Exception {
   				 TraceUtil.putTraceId(webChannel.attr(MixAll.ATTRIBUTEKEY_TRACE_ID).get());
   				 TraceUtil.putProto(nodeInfo.getProtocalTemp());
   				 try {
   					 if (future.isSuccess()) {
   						 RedisUtil.getCircleBreakCache().get(nodeInfo.getCircleBreakKey()).getState().postMethodExecute();
   						 logger.info("WebSocket 代理后端连接成功: {}:{} -> path {}", wsNode.getIp(), wsNode.getPort(), backendPath);
   					 } else {
   						 RedisUtil.getCircleBreakCache().get(nodeInfo.getCircleBreakKey()).getState().ActUponException();
   						 logger.error("WebSocket 代理后端连接失败: {}:{}", wsNode.getIp(), wsNode.getPort(), future.cause());
   						 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(502, "backend websocket connect fail !"));
   					 }
   				 } catch (Exception e) {
   					 logger.error("WebSocket 代理后端连接处理异常", e);
   				 } finally {
   					 String routeLimitKey = RouteSelectUtil.selectRouteByUri(patternUri, nodeInfo.getInterfaceVserion());
   					 if(routeLimitKey != null) {
   						 RateLimiter.release(routeLimitKey);
   					 }
   					 RateLimiter.release(RouteSelectUtil.nodeLimitKey(wsNode, patternUri));
   				 TraceUtil.clear();
   				 }
   			 }
   		 });
   	 }else {
   		 //dubbo
    		 ByteBuf bufferContent = msg.content().copy();
    		 HXUnlockedMQ.sendRunnable(new Runnable() {
				
				@Override
				public void run() {
					 TraceUtil.putTraceId(webChannel.attr(MixAll.ATTRIBUTEKEY_TRACE_ID).get());
					 TraceUtil.putProto("dubbo");
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
		    					 Map map = (Map) JSON.parse(content, JSONReader.Feature.AllowUnQuotedFieldNames);
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
		    			 CBManager manager= RedisUtil.getCircleBreakCache().get(nodeInfo.getCircleBreakKey());
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
							String routeLimitKey = RouteSelectUtil.selectRouteByUri(patternUri,nodeInfo.getInterfaceVserion());
							 if(routeLimitKey != null) {
								 RateLimiter.release(routeLimitKey);
							 }
							RateLimiter.release(RouteSelectUtil.nodeLimitKey(webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).get().getRouteNode(), patternUri));
						TraceUtil.clear();
						}
		    		 }else {
		    			 ReferenceCountUtil.release(bufferContent);
		    			 try {
		    				 logger.error(MixAll.LOG_INFO_PRIFEX+String.format("网关[%s]访问url%s不存在",gateHost,msg.uri()) );
		    				 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(404, "The path you accessed does not work !"));
						} finally {
							String patternUri = webChannel.attr(MixAll.ATTRIBUTEKEY_URL).get();
							String routeLimitKey = RouteSelectUtil.selectRouteByUri(patternUri,nodeInfo.getInterfaceVserion());
							 if(routeLimitKey != null) {
								 RateLimiter.release(routeLimitKey);
							 }
							RateLimiter.release(RouteSelectUtil.nodeLimitKey(webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).get().getRouteNode(), patternUri));
						TraceUtil.clear();
						}
		    		 }
				}
			});
    	 }
    }
   
}

