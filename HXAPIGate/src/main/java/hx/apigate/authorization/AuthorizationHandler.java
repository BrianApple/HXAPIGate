
package hx.apigate.authorization;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import hx.apigate.authorization.shiro.databridge.JwtToken;
import hx.apigate.databridge.NodeInfo;
import hx.apigate.databridge.xmlBean.RouteNode;
import hx.apigate.util.CacheUtil;
import hx.apigate.util.MixAll;
import hx.apigate.util.StringUtils;

/**
 * <p>Description: 授权管理</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.xianglong.work</p>
　 * @author yangcheng
　 * @date 2019年10月31日
　 * @version 1.0
 */
public class AuthorizationHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	Logger logger = LoggerFactory.getLogger(AuthorizationHandler.class);
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final FullHttpRequest msg) {
    	 final Channel webChannel = ctx.channel();
    	 String url = webChannel.attr(MixAll.ATTRIBUTEKEY_URL).get();
    	 NodeInfo nodeInfo = webChannel.attr(MixAll.ATTRIBUTEKEY_ROUTE_NODE).get();
		if (nodeInfo.isNeedAuth()) {
			//需要鉴权
    		 try {
    			 Subject sub = SecurityUtils.getSecurityManager().createSubject(new DefaultSubjectContext());
    			 AuthenticationToken token = createJwtToken( ctx, msg);
        		 sub.login(token);
        		 StringBuilder sb = new StringBuilder(Constance.API_RESOURCE_ROLE);
            	 sb.append(url);
            	 sb.append(Constance.API_RESOURCE_ROLE_SPLIT);
            	 sb.append(msg.method().toString());
        		 String roles = CacheUtil.getString(sb.toString());
        		 if(roles == null || !checkRoles(sub,roles)) {
        			 webChannel.writeAndFlush(MixAll.getDefaultFullHttpResponse(403, "对不起，您无权进行此操作 !"));
        			 return;
        		 }
        		 msg.retain();
        		 ctx.fireChannelRead(msg);
    		 }catch(Exception e) {
    			 ctx.writeAndFlush(MixAll.getDefaultFullHttpResponse(400, e.getMessage()));
    		 }
    	 }else {
			 msg.retain();
             ctx.fireChannelRead(msg);
    	 }
    	 
        
    }
    
    private boolean isJwtSubmission(FullHttpRequest request) {

    	String jwt = request.headers().get(Constance.AUTHORIZATION);
   	 	String userId = request.headers().get(Constance.USERID);
        return (request instanceof FullHttpRequest)
                && !StringUtils.isEmpty(jwt)
                && !StringUtils.isEmpty(userId);
    }
    /**
     * @param ctx
     * @param request
     * @return
     */
    private AuthenticationToken createJwtToken(ChannelHandlerContext ctx,FullHttpRequest request) {

    	HttpHeaders headers = request.headers();
        String userId = headers.get(Constance.USERID);
        InetSocketAddress webAddress = (InetSocketAddress)(ctx.channel().remoteAddress());
        String ipHost = webAddress.getHostString();
        String jwt = headers.get(Constance.AUTHORIZATION);
        if(jwt == null) {
        	throw new RuntimeException("jwt is not exist!");
        }
        String deviceInfo = headers.get(Constance.DEVICEINFO);
        return new JwtToken(ipHost,deviceInfo,jwt,userId);
    }
    
    /**
     *
     * @param subject 1
     * @param roles  该资源所需要的所有角色，“,”分隔的
     * @return boolean
     */
    private boolean checkRoles(Subject subject, String roles){
        String[] rolesArray = roles.split("\\,");
        return rolesArray == null || rolesArray.length == 0 || Stream.of(rolesArray).anyMatch(role -> subject.hasRole(role.trim()));
    }
}

