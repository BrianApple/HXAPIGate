package com.usthe.bootshiro.shiro.filter;


import com.alibaba.fastjson.JSON;
import com.usthe.bootshiro.domain.vo.Message;
import com.usthe.bootshiro.ignite.Constance;
import com.usthe.bootshiro.ignite.IgniteAutoConfig;
import com.usthe.bootshiro.shiro.token.PasswordToken;
import com.usthe.bootshiro.util.CommonUtil;
import com.usthe.bootshiro.util.IpUtil;
import com.usthe.bootshiro.util.RequestResponseUtil;

import org.apache.ignite.IgniteCache;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *   基于 用户名密码 的认证过滤器
 *   com.usthe.bootshiro.shiro.filter.ShiroFilterChainManager类中
 *   将所有账户登陆相关的url都关联到了当前filter
 * @author tomsun28
 * @date 20:18 2018/2/10
 */
public class PasswordFilter extends AccessControlFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordFilter.class);

    private StringRedisTemplate redisTemplate;
    private IgniteAutoConfig igniteAutoConfig;
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {

        Subject subject = getSubject(request,response);
        // 如果其已经登录，再此发送登录请求
        //  拒绝，统一交给 onAccessDenied 处理
        return null != subject && subject.isAuthenticated();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        // 判断是否是登录请求
        if(isPasswordLoginPost(request)){

            AuthenticationToken authenticationToken = null;
            try {
                authenticationToken = createPasswordToken(request);
            }catch (Exception e) {
                // response 告知无效请求
                Message message = new Message().error(400,"error request");
                RequestResponseUtil.responseWrite(JSON.toJSONString(message),response);
                return false;
            }

            Subject subject = getSubject(request,response);
            try {
                subject.login(authenticationToken);
                //登录认证成功,进入请求派发json web token url资源内
                return true;
            }catch (AuthenticationException e) {
                LOGGER.warn(authenticationToken.getPrincipal()+"::"+e.getMessage());
                // 返回response告诉客户端认证失败
                Message message = new Message().error(400,"login fail");
                RequestResponseUtil.responseWrite(JSON.toJSONString(message),response);
                return false;
            }catch (Exception e) {
                LOGGER.error(authenticationToken.getPrincipal()+"::认证异常::"+e.getMessage(),e);
                // 返回response告诉客户端认证失败
                Message message = new Message().error(400,"login fail");
                RequestResponseUtil.responseWrite(JSON.toJSONString(message),response);
                return false;
            }
        }
        // 判断是否为注册请求,若是通过过滤链进入controller注册
        if (isAccountRegisterPost(request)) {
            return true;
        }
        // 之后添加对账户的找回等
        // response 告知无效请求
        Message message = new Message().error(500,"error request");
        RequestResponseUtil.responseWrite(JSON.toJSONString(message),response);
        return false;
    }
    /**
         * 动态密钥的请求url示例：'account/login?tokenKey=get'
     * @param request
     * @return
     */
    private boolean isPasswordTokenGet(ServletRequest request) {

        String tokenKey = RequestResponseUtil.getParameter(request,"tokenKey");

        return (request instanceof HttpServletRequest)
                && "GET".equals(((HttpServletRequest) request).getMethod().toUpperCase())
                &&  "get".equals(tokenKey);
    }

    /**
         * 判断是否是POST方式的登录请求
     * @param request
     * @return
     */
    private boolean isPasswordLoginPost(ServletRequest request) {

        Map<String ,String> map = RequestResponseUtil.getRequestBodyMap(request);
        String password = map.get("password");
        if (password == null){
            return false;
        }
        String appId = map.get("appId");
        return (request instanceof HttpServletRequest)
                && null != password
                && null != appId;
    }

    private boolean isAccountRegisterPost(ServletRequest request) {

        Map<String ,String> map = RequestResponseUtil.getRequestBodyMap(request);
        String uid = map.get("uid");
        String username = map.get("username");
        String methodName = map.get("methodName");
        String password = map.get("password");//加密之后的密文
        return (request instanceof HttpServletRequest)
                && "POST".equals(((HttpServletRequest) request).getMethod().toUpperCase())
                && null != username
                && null != password
                && null != uid
                && "register".equals(methodName);
    }

    /**
         * 创建用户认证的token，用于shiro的login认证判断
     * @param request
     * @return
     * @throws Exception
     */
    private AuthenticationToken createPasswordToken(ServletRequest request) throws Exception {

        Map<String ,String> map = RequestResponseUtil.getRequestBodyMap(request);
        String appId = map.get("appId");//用户名
        String password = map.get("password");
        String host = IpUtil.getIpFromRequest(WebUtils.toHttp(request));
        //获取动态密钥时服务端生成的随机数
        //动态密钥
        return new PasswordToken(appId,password,null,host,null);
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

	public void setIgniteAutoConfig(IgniteAutoConfig igniteAutoConfig) {
		this.igniteAutoConfig = igniteAutoConfig;
	}

}
