package com.usthe.bootshiro.shiro.filter;

import com.alibaba.fastjson.JSON;
import com.usthe.bootshiro.domain.vo.Message;
import com.usthe.bootshiro.ignite.IgniteAutoConfig;
import com.usthe.bootshiro.service.AccountService;
import com.usthe.bootshiro.shiro.token.JwtToken;
import com.usthe.bootshiro.support.factory.LogTaskFactory;
import com.usthe.bootshiro.support.manager.LogExeManager;
import com.usthe.bootshiro.util.IpUtil;
import com.usthe.bootshiro.util.JsonWebTokenUtil;
import com.usthe.bootshiro.util.RequestResponseUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 *  支持restful url 的过滤链  JWT json web token 过滤器，无状态验证
 * @author tomsun28
 * @date 0:04 2018/4/20
 */
public class BonJwtFilter extends AbstractPathMatchingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BonJwtFilter.class);
    private static final String STR_EXPIRED = "expiredJwt";


    private StringRedisTemplate redisTemplate;
    
    private IgniteAutoConfig igniteAutoConfig;
    private AccountService accountService;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue) throws Exception {
        Subject subject = getSubject(servletRequest,servletResponse);

        Map<String ,String> map = RequestResponseUtil.getRequestBodyMap(servletRequest);
        //记录调用api日志到数据库
        LogExeManager.getInstance().executeLogTask(LogTaskFactory.bussinssLog(map.get("userId"),
                WebUtils.toHttp(servletRequest).getRequestURI(),WebUtils.toHttp(servletRequest).getMethod(),(short)1,null));

        boolean isJwtPost = (null != subject && !subject.isAuthenticated()) && isJwtSubmission(servletRequest);
        // 判断是否为JWT认证请求
        if (isJwtPost) {
            AuthenticationToken token = createJwtToken(servletRequest);
            try {
                subject.login(token);
                return this.checkRoles(subject,mappedValue);
            }catch (AuthenticationException e) {

                // 如果是JWT过期
                if (STR_EXPIRED.equals(e.getMessage())) {
                    // 这里初始方案先抛出令牌过期，之后设计为在Redis中查询当前userId对应令牌，其设置的过期时间是JWT的两倍，此作为JWT的refresh时间
                    // 当JWT的有效时间过期后，查询其refresh时间，refresh时间有效即重新派发新的JWT给客户端，
                    // refresh也过期则告知客户端JWT时间过期重新认证

                    // 当存储在redis的JWT没有过期，即refresh time 没有过期
                    String userId = map.get("userId");
                    String jwt = map.get("jwt");
                    
                    Object refreshJwt = igniteAutoConfig.getJWTSessionData("JWT-SESSION-"+userId);
//                    String refreshJwt = redisTemplate.opsForValue().get("JWT-SESSION-"+userId);
                    if (null != refreshJwt && refreshJwt.equals(jwt)) {
                        // 重新申请新的JWT
                        // 根据userId获取其对应所拥有的角色(这里设计为角色对应资源，没有权限对应资源)
                        String roles = accountService.loadAccountRole(userId);
                        //seconds为单位,10 hours
                        long refreshPeriodTime = 36000L;
                        String newJwt = JsonWebTokenUtil.issueJWT(UUID.randomUUID().toString(),userId,
                                "token-server",refreshPeriodTime >> 1,roles,null, SignatureAlgorithm.HS512);
                        // 将签发的JWT存储到ignite： {JWT-SESSION-{userId} , jwt}
                        igniteAutoConfig.cacheJWTSessionData(refreshPeriodTime, "JWT-SESSION-"+userId, newJwt);
                        Message message = new Message().ok(201,"new jwt").addData("jwt",newJwt);
                        RequestResponseUtil.responseWrite(JSON.toJSONString(message),servletResponse);
                        return false;
                    }else {
                        // jwt时间失效过期,jwt refresh time失效 返回jwt过期客户端重新登录
                        Message message = new Message().error(400,"expired jwt");
                        RequestResponseUtil.responseWrite(JSON.toJSONString(message),servletResponse);
                        return false;
                    }

                }
                // 其他的判断为JWT错误无效
                Message message = new Message().error(400,"error Jwt");
                RequestResponseUtil.responseWrite(JSON.toJSONString(message),servletResponse);
                return false;

            }catch (Exception e) {
                // 其他错误
                LOGGER.error(IpUtil.getIpFromRequest(WebUtils.toHttp(servletRequest))+"--JWT认证失败"+e.getMessage(),e);
                // 告知客户端JWT错误1005,需重新登录申请jwt
                Message message = new Message().error(400,"error jwt");
                RequestResponseUtil.responseWrite(JSON.toJSONString(message),servletResponse);
                return false;
            }
        }else {
            // 请求未携带jwt 判断为无效请求
            Message message = new Message().error(400,"error request,jwt not exists");
            RequestResponseUtil.responseWrite(JSON.toJSONString(message),servletResponse);
            return false;
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        Subject subject = getSubject(servletRequest,servletResponse);

        // 未认证的情况上面已经处理  这里处理未授权
        if (subject != null && subject.isAuthenticated()){
            //  已经认证但未授权的情况
            // 告知客户端JWT没有权限访问此资源
            Message message = new Message().error(403,"no permission");
            RequestResponseUtil.responseWrite(JSON.toJSONString(message),servletResponse);
        }
        // 过滤链终止
        return false;
    }

    private boolean isJwtSubmission(ServletRequest request) {

        Map<String ,String> map = RequestResponseUtil.getRequestBodyMap(request);
        String jwt = map.get("jwt");
        if (jwt == null){
            return false;
        }
        String appId = map.get("userId");
        return (request instanceof HttpServletRequest)
                && null != jwt
                && null != appId;
    }

    private AuthenticationToken createJwtToken(ServletRequest request) {

        Map<String ,String> map = RequestResponseUtil.getRequestBodyMap(request);
        String userId = map.get("userId");
        String ipHost = request.getRemoteAddr();
        String jwt = map.get("jwt");

        return new JwtToken(ipHost,null,jwt,userId);
    }

    /**
     * description 验证当前用户是否属于mappedValue任意一个角色
     *
     * @param subject 1
     * @param mappedValue 2
     * @return boolean
     */
    private boolean checkRoles(Subject subject, Object mappedValue){
        String[] rolesArray = (String[]) mappedValue;
        return rolesArray == null || rolesArray.length == 0 || Stream.of(rolesArray).anyMatch(role -> subject.hasRole(role.trim()));
    }


    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }


	public void setIgniteAutoConfig(IgniteAutoConfig igniteAutoConfig) {
		this.igniteAutoConfig = igniteAutoConfig;
	}
    
    
}
