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
    	 if(msg.method().equals(HttpMethod.OPTIONS)) {
    		 HttpResponseUtil.responseMsg(webChannel, null);
    	 }else {
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
    			 ret = matchUrl(msg.uri(),msg.method());
    			 if(ret != null && ret.length == 2 && ret[1] instanceof NodeInfo){
    				 NodeInfo node = (NodeInfo)ret[1];
    				 if("http".equals(node.getProtocalTemp())) {
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

