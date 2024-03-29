package com.usthe.bootshiro.controller;

import com.usthe.bootshiro.domain.bo.AuthUser;
import com.usthe.bootshiro.domain.vo.Account;
import com.usthe.bootshiro.domain.vo.JwtAccount;
import com.usthe.bootshiro.domain.vo.Message;
import com.usthe.bootshiro.ignite.IgniteAutoConfig;
import com.usthe.bootshiro.service.AccountService;
import com.usthe.bootshiro.service.UserService;
import com.usthe.bootshiro.shiro.provider.AccountProvider;
import com.usthe.bootshiro.shiro.token.PasswordToken;
import com.usthe.bootshiro.support.factory.LogTaskFactory;
import com.usthe.bootshiro.support.manager.LogExeManager;
import com.usthe.bootshiro.util.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiOperation;

import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
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
    private IgniteAutoConfig igniteAutoConfig;
    @Autowired
    private AccountProvider accountProvider;

    /**
     * description 登录签发 JWT ,这里已经在 passwordFilter 进行了登录认证
     *
     * @param request 1
     * @param response 2
     * @return com.usthe.bootshiro.domain.vo.Message
     */
    @ApiOperation(value = "用户登录", notes = "POST用户登录签发JWT")
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
        	jwt =(String) igniteAutoConfig.getJWTSessionData( JWT_SESSION + appId) ;
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
        igniteAutoConfig.cacheJWTSessionData(refreshPeriodTime, JWT_SESSION + appId , jwt);
//        redisTemplate.opsForValue().set(JWT_SESSION + appId, jwt, refreshPeriodTime, TimeUnit.SECONDS);
        AuthUser authUser = userService.getUserByAppId(appId);
        authUser.setPassword(null);
        authUser.setSalt(null);

        LogExeManager.getInstance().executeLogTask(LogTaskFactory.loginLog(appId, IpUtil.getAllIpFromRequest(WebUtils.toHttp(request)), (short) 1, "登录成功"));

        return new Message().ok(200, "issue jwt success").addData("jwt", jwt).addData("user", authUser);
    }


    /**
     * description 用户账号的注册
     *
     * @param request 1
     * @param response 2
     * @return com.usthe.bootshiro.domain.vo.Message
     */
    @ApiOperation(value = "用户注册", notes = "POST用户注册")
    @PostMapping("/register")
    public Message accountRegister(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> params = RequestResponseUtil.getRequestBodyMap(request);
        AuthUser authUser = new AuthUser();
        String uid = params.get("userId");//uid,用户账号,主键
        String password = params.get("password");
//        String userKey = params.get("userKey");
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(password)) {
            // 必须信息缺一不可,返回注册账号信息缺失
            return new Message().error(400, "账户信息缺失");
        }
        authUser.setUid(uid);
        // 从Redis取出密码传输加密解密秘钥
        String salt = CommonUtil.getRandomString(6);
        // 存储到数据库的密码为 MD5(原密码+盐值)
        authUser.setPassword(Md5Util.md5(password + salt));
        authUser.setSalt(salt);
        authUser.setCreateTime(new Date());
        if (!StringUtils.isEmpty(params.get(STR_USERNAME))) {//用户名(nick_name)
            authUser.setUsername(params.get(STR_USERNAME));
        }
        if (!StringUtils.isEmpty(params.get(STR_REALNAME))) {//用户真名
            authUser.setRealName(params.get(STR_REALNAME));
        }
        if (!StringUtils.isEmpty(params.get(STR_AVATAR))) {//头像
            authUser.setAvatar(params.get(STR_AVATAR));
        }
        if (!StringUtils.isEmpty(params.get(STR_PHONE))) {//phone
            authUser.setPhone(params.get(STR_PHONE));
        }
        if (!StringUtils.isEmpty(params.get(STR_EMAIL))) {//邮件地址(唯一)
            authUser.setEmail(params.get(STR_EMAIL));
        }
        if (!StringUtils.isEmpty(params.get(STR_SEX))) {//性别(1.男 2.女)
            authUser.setSex(Byte.valueOf(params.get(STR_SEX)));
        }
        if (!StringUtils.isEmpty(params.get(STR_WHERE))) {
            authUser.setCreateWhere(Byte.valueOf(params.get(STR_WHERE)));//创建来源(1.web 2.android 3.ios 4.win 5.macos 6.ubuntu)
        }
        authUser.setStatus((byte) 1);
        String msg = accountService.isAccountExist(authUser) ;
        if (msg != null ){
            // 账户已存在
            return new Message().error(400, msg);
        }
        
        if (accountService.registerAccount(authUser)) {
            LogExeManager.getInstance().executeLogTask(LogTaskFactory.registerLog(uid, IpUtil.getIpFromRequest(WebUtils.toHttp(request)), (short) 1, "注册成功"));
            return new Message().ok(200, "注册成功");
        } else {
            LogExeManager.getInstance().executeLogTask(LogTaskFactory.registerLog(uid, IpUtil.getIpFromRequest(WebUtils.toHttp(request)), (short) 0, "注册失败"));
            return new Message().ok(400, "注册失败");
        }
    }

}
