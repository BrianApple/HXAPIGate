package com.usthe.bootshiro.controller;

import com.usthe.bootshiro.domain.bo.AuthUser;
import com.usthe.bootshiro.domain.vo.Account;
import com.usthe.bootshiro.domain.vo.JwtAccount;
import com.usthe.bootshiro.domain.vo.Message;
import com.usthe.bootshiro.util.JwtSessionStore;
import com.usthe.bootshiro.service.AccountService;
import com.usthe.bootshiro.service.UserService;
import com.usthe.bootshiro.shiro.provider.AccountProvider;
import com.usthe.bootshiro.shiro.token.PasswordToken;
import com.usthe.bootshiro.support.factory.LogTaskFactory;
import com.usthe.bootshiro.support.manager.LogExeManager;
import com.usthe.bootshiro.util.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.Operation;

import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 *   post新增,get读取,put完整更新,patch部分更新,delete删除
 *  第三方系统用户登录/注册
 * @author tomsun28
 * @date 14:40 2018/3/8
 */
@RestController
@RequestMapping("/account")
public class AccountController extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private static final String STR_USERNAME = "username";
    private static final String STR_REALNAME = "realName";
    private static final String STR_AVATAR = "avatar";
    private static final String STR_PHONE = "phone";
    private static final String STR_EMAIL = "email";
    private static final String STR_SEX = "sex";
    private static final String STR_WHERE = "createWhere";
    
    /**
     * jwt签发者标志
     */
    private static final String ISSUER  = "UIOTCP_BOOTSHIRO_PRO";
    /**
     * JWT-SESSION缓存前缀
     */
    private static final String JWT_SESSION  = "JWT-SESSION:";


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;
    @Autowired
    private JwtSessionStore jwtSessionStore;
    @Autowired
    private AccountProvider accountProvider;

    /**
     * description 登录签发 JWT ,这里已经在 passwordFilter 进行了登录认证
     *
     * @param request 1
     * @param response 2
     * @return com.usthe.bootshiro.domain.vo.Message
     */
    @Operation(summary = "用户登录", description = "POST用户登录签发JWT")
    @PostMapping("/login")
    public Message accountLogin(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> params = RequestResponseUtil.getRequestBodyMap(request);
        String appId = params.get("userId");
        String curPassword = params.get("password");
        // 根据appId获取其对应所拥有的角色(这里设计为角色对应资源，没有权限对应资源)
        Account rAccount = accountProvider.loadAccount(appId);
        if (rAccount != null) {
            // 用盐对密码进行MD5加密
            curPassword = Md5Util.md5((curPassword+rAccount.getSalt()));
            if (!rAccount.getPassword().equals(curPassword)){
                return new Message().ok(500, "password error");
            }
        }else {
            return new Message().ok(500, "please regist first");
        }
        String roles = accountService.loadAccountRole(appId);
        // 时间以秒计算,token有效刷新时间是token有效过期时间的2倍
        long refreshPeriodTime = 36000;//refreshPeriodTime右移一位是jwt中的过期时间。
        
        String jwt = null;
        if(appId != null &&  appId.startsWith("guest0")) {
        	/**
        	 * 访客账户多端登录
        	 */
        	jwt = jwtSessionStore.get( JWT_SESSION + appId) ;
        	if(jwt != null  && !"".equals(jwt)) {
        		try {
        			//已经登录，延长jwt失效时间即可
            		JwtAccount account = JsonWebTokenUtil.parseJwt(jwt, JsonWebTokenUtil.SECRET_KEY);
            		jwt = JsonWebTokenUtil.issueJWT(account.getTokenId(), account.getAppId(),
                			ISSUER, refreshPeriodTime, roles, null, SignatureAlgorithm.HS512);
				} catch (Exception e) {
					//原有jwt已过期，则重新生成一个
					jwt = JsonWebTokenUtil.issueJWT(UUID.randomUUID().toString(), appId,
		        			ISSUER, refreshPeriodTime  , roles, null, SignatureAlgorithm.HS512);
				}
        		
        	}else {
        		//若guest账户首次登录，则生成jwt
        		jwt = JsonWebTokenUtil.issueJWT(UUID.randomUUID().toString(), appId,
            			ISSUER, refreshPeriodTime , roles, null, SignatureAlgorithm.HS512);
        	}
        }else {
        	jwt = JsonWebTokenUtil.issueJWT(UUID.randomUUID().toString(), appId,
        			ISSUER, refreshPeriodTime >> 1 , roles, null, SignatureAlgorithm.HS512);
        }
        //  {JWT-SESSION-{appID} , jwt}
        jwtSessionStore.set(JWT_SESSION + appId , jwt, refreshPeriodTime);
//        redisTemplate.opsForValue().set(JWT_SESSION + appId, jwt, refreshPeriodTime, TimeUnit.SECONDS);
        AuthUser authUser = userService.getUserByAppId(appId);
        authUser.setPassword(null);
        authUser.setSalt(null);

        LogExeManager.getInstance().executeLogTask(LogTaskFactory.loginLog(appId, IpUtil.getAllIpFromRequest(WebUtils.toHttp(request)), (short) 1, "登录成功"));

        return new Message().ok(200, "issue jwt success").addData("jwt", jwt).addData("user", authUser);
    }

}
