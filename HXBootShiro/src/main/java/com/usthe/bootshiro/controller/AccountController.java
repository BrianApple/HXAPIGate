package com.usthe.bootshiro.controller;

import com.usthe.bootshiro.domain.bo.AuthUser;
import com.usthe.bootshiro.domain.vo.Account;
import com.usthe.bootshiro.domain.vo.Message;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *   post新增,get读取,put完整更新,patch部分更新,delete删除
 *   
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
        String appId = params.get("appId");
        // 根据appId获取其对应所拥有的角色(这里设计为角色对应资源，没有权限对应资源)
        String roles = accountService.loadAccountRole(appId);
        // 时间以秒计算,token有效刷新时间是token有效过期时间的2倍
        long refreshPeriodTime = 36000L;//refreshPeriodTime右移一位是jwt中的过期时间。
        
        String jwt = JsonWebTokenUtil.issueJWT(UUID.randomUUID().toString(), appId,
        		ISSUER, refreshPeriodTime >> 1 , roles, null, SignatureAlgorithm.HS512);
        // 将签发的JWT存储到Redis： {JWT-SESSION-{appID} , jwt}
        redisTemplate.opsForValue().set(JWT_SESSION + appId, jwt, refreshPeriodTime, TimeUnit.SECONDS);
        AuthUser authUser = userService.getUserByAppId(appId);
        authUser.setPassword(null);
        authUser.setSalt(null);

        LogExeManager.getInstance().executeLogTask(LogTaskFactory.loginLog(appId, IpUtil.getIpFromRequest(WebUtils.toHttp(request)), (short) 1, "登录成功"));

        return new Message().ok(1003, "issue jwt success").addData("jwt", jwt).addData("user", authUser);
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
        String uid = params.get("uid");//uid,用户账号,主键
        String password = params.get("password");
        String userKey = params.get("userKey");
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(password)) {
            // 必须信息缺一不可,返回注册账号信息缺失
            return new Message().error(1111, "账户信息缺失");
        }
       

        authUser.setUid(uid);

        // 从Redis取出密码传输加密解密秘钥
        String ss = IpUtil.getIpFromRequest(WebUtils.toHttp(request)).toUpperCase();
        String tokenKey = redisTemplate.opsForValue().get("TOKEN_KEY_" + IpUtil.getIpFromRequest(WebUtils.toHttp(request)).toUpperCase()+userKey);
        String realPassword = AesUtil.aesDecode(password, tokenKey);
        String salt = CommonUtil.getRandomString(6);
        // 存储到数据库的密码为 MD5(原密码+盐值)
        authUser.setPassword(Md5Util.md5(realPassword + salt));
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
            return new Message().error(1111, msg);
        }
        
        if (accountService.registerAccount(authUser)) {
            LogExeManager.getInstance().executeLogTask(LogTaskFactory.registerLog(uid, IpUtil.getIpFromRequest(WebUtils.toHttp(request)), (short) 1, "注册成功"));
            return new Message().ok(2002, "注册成功");
        } else {
            LogExeManager.getInstance().executeLogTask(LogTaskFactory.registerLog(uid, IpUtil.getIpFromRequest(WebUtils.toHttp(request)), (short) 0, "注册失败"));
            return new Message().ok(1111, "注册失败");
        }
    }

}
