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
package hx.apigate.socket.handlers;


import hx.apigate.databridge.CircleBreakException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.databridge.NodeInfo;
import hx.apigate.databridge.SemphareException;
import hx.apigate.databridge.xmlBean.RouteNode;
import hx.apigate.util.HXAPIGateConext;
import hx.apigate.util.HttpResponseUtil;
import hx.apigate.util.MixAll;
import hx.apigate.util.RouteSelectUtil;
import hx.apigate.util.TraceUtil;
/**
 * <p>Description: </p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng,hjj
　 * @date 2019年10月31日
　 * @version 1.0
 */
public class GatewayServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	Logger logger = LoggerFactory.getLogger(GatewayServerHandler.class);
    private Channel toMasterChannel;
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final FullHttpRequest msg) {
    	 final Channel webChannel = ctx.channel();
    	 // 请求溯源：优先复用调用方 X-Trace-Id，否则生成；写入 MDC 与 channel attr（供异步转发/日志使用）
    	 String traceId = TraceUtil.putTraceId(msg.headers().get(TraceUtil.HEADER_X_TRACE_ID));
    	 webChannel.attr(MixAll.ATTRIBUTEKEY_TRACE_ID).set(traceId);
    	 if(msg.method().equals(HttpMethod.OPTIONS)) {
    		 HttpResponseUtil.responseMsg(webChannel, null);
    		 TraceUtil.clear();
    	 }else {
    		 // 内置 MCP 协议转换端点（/mcp）：不参与路由匹配，构造伪节点走鉴权链后由 TranceDataHandler 分流
    		 String rawUri = msg.uri();
    		 String pathOnly = rawUri.contains("?") ? rawUri.substring(0, rawUri.indexOf('?')) : rawUri;
    		 if (hx.apigate.mcp.McpGatewayHandler.MCP_ENDPOINT.equals(pathOnly) && msg.method().equals(HttpMethod.POST)) {
    			 try {
    				 NodeInfo mcpNode = new NodeInfo("v1.0", hx.apigate.mcp.McpGatewayHandler.MCP_GATEWAY_PROTOCOL,
    						 new RouteNode(), rawUri, true, "mcp-gateway-cb");
    				 webChannel.attr(MixAll.ATTRIBUTEKEY_URL).set(hx.apigate.mcp.McpGatewayHandler.MCP_ENDPOINT + "==POST");
    				 webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).set(mcpNode);
    				 webChannel.attr(MixAll.ATTRIBUTEKEY_TRANSPARENT).set(true);
    				 TraceUtil.putProto("mcp");
    				 msg.retain();
    				 ctx.fireChannelRead(msg);
    				 TraceUtil.clear();
    				 return;
    			 } catch (Exception e) {
    				 ctx.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(500, "MCP gateway error: " + e.getMessage()));
    				 TraceUtil.clear();
    				 return;
    			 }
    		 }
    		 String localHost = msg.headers().get(hx.apigate.socket.Constance.HOST);
    		 InetSocketAddress webAddress = (InetSocketAddress)webChannel.remoteAddress();
    		 StringBuilder sb ;
    		 if( msg.headers().get(hx.apigate.socket.Constance.X_FORWARDED_FOR) != null) {
    			 sb = new StringBuilder( msg.headers().get(hx.apigate.socket.Constance.X_FORWARDED_FOR));
    			 sb.append(hx.apigate.socket.Constance.DELIMER).append(localHost.split("\\:")[0]);
    		 }else {
    			 sb = new StringBuilder(webAddress.getAddress().getHostAddress());
    			 sb.append(hx.apigate.socket.Constance.DELIMER).append(localHost.split("\\:")[0]);
    		 }
    		 msg.headers().set(hx.apigate.socket.Constance.X_FORWARDED_FOR, sb.toString());
    		 msg.headers().set(hx.apigate.socket.Constance.HXAPIGate_SOURCE_ID,webChannel.id().asLongText() );
    		 
			Object[] ret = null;
			try {
				// WebSocket 握手请求（HTTP GET + Upgrade: websocket）：优先按 WS 协议路由匹配（uri==WS），
				// 未注册 WS 路由时回退到普通 GET 匹配（例如管理端静态资源/普通接口）
				HttpMethod matchMethod = msg.method();
				String upgrade = msg.headers().get("Upgrade");
				if (upgrade != null && "websocket".equalsIgnoreCase(upgrade)) {
					matchMethod = HttpMethod.valueOf("WS");
				}
				ret = matchUrl(msg.uri(), matchMethod);
				if(ret == null && !matchMethod.equals(msg.method())) {
					// WS 路由未命中，回退 GET 匹配
					ret = matchUrl(msg.uri(), msg.method());
				}
				if(ret != null && ret.length == 2 && ret[1] instanceof NodeInfo){
					NodeInfo node = (NodeInfo)ret[1];
					// 协议标识写入 MDC：http / mcp / websocket / dubbo
					TraceUtil.putProto(node.getProtocalTemp());
					// 透传模式：默认开启（HTTP 代理原样转发状态码/headers/body，支持 MCP/SSE 流式）
					// 显式 X-HXAPI-Transparent: false 可回退旧行为（RetMessage 统一包装）
					boolean transparent = true;
					String t = msg.headers().get(hx.apigate.socket.Constance.HXAPI_TRANSPARENT);
					if ("false".equalsIgnoreCase(t)) {
						transparent = false;
					}
					webChannel.attr(MixAll.ATTRIBUTEKEY_TRANSPARENT).set(transparent);
					if("http".equals(node.getProtocalTemp()) || "mcp".equals(node.getProtocalTemp())) {
   					 msg.headers().set(hx.apigate.socket.Constance.HOST,new StringBuilder(node.getRouteNode().getIp())
   							 .append(hx.apigate.socket.Constance.COLON).append(node.getRouteNode().getPort()));
   				 }
    				 webChannel.attr(MixAll.ATTRIBUTEKEY_URL).set(ret[0].toString());
    				 webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).set(node);
    				 msg.retain();
    				 ctx.fireChannelRead(msg);
    			 }else {
    				 ctx.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(404, "The path you accessed does not exist !"));
    			 }
		 } catch (SemphareException e) {
			 ctx.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(400, e.getMsg()));
		 } catch (CircleBreakException e) {
			 ctx.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(503, e.getMsg()));
		 } catch (NullPointerException e){
			 ctx.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(404, "The path you accessed does not exist or does not work!"));
		 } finally {
			 // 请求处理结束（或已转交异步链路），清理 MDC 防止线程复用串号
			 TraceUtil.clear();
		 }
	 }
}

    @Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	if(HXAPIGateConext.rateLimiter.tryAcquire()) {
    		super.channelActive(ctx);
    	}else {
    		ctx.writeAndFlush(MixAll.getDefaultFullHttpResponse4Error(500, "service is busy, please try again later"));
    		ctx.deregister();
    		ctx.close();
    	}
		logger.debug("web channel active");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		logger.debug("web channel destory");
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * @param sourceUrl web端访问的url
     * @param httpMethod 
     * @return 
     * @throws Exception 
     */
    private Object[] matchUrl(String sourceUrl, HttpMethod httpMethod) throws SemphareException  {
    	Object[] ret = RouteSelectUtil.selectOneNode(sourceUrl,httpMethod);
		return ret;
        
    }

}

