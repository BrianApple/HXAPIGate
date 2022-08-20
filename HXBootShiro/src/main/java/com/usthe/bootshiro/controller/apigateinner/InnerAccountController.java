package com.usthe.bootshiro.controller.apigateinner;

import com.usthe.bootshiro.domain.bo.AuthUser;
import com.usthe.bootshiro.domain.vo.*;
import com.usthe.bootshiro.ignite.IgniteAutoConfig;
import com.usthe.bootshiro.service.AccountService;
import com.usthe.bootshiro.service.UserService;
import com.usthe.bootshiro.shiro.provider.AccountProvider;
import com.usthe.bootshiro.support.factory.LogTaskFactory;
import com.usthe.bootshiro.support.manager.LogExeManager;
import com.usthe.bootshiro.util.*;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * 登录---限API自己访问
 * @Description: 
 * @author  yangcheng
 * @date: 2020年4月18日
 */
@Controller
@RequestMapping("/inner/user")
public class InnerAccountController {
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
	private IgniteAutoConfig igniteAutoConfig;

	@Autowired
	private UserService userService;

	@Autowired
	private AccountProvider accountProvider;

	@PostMapping("/login")
	@ResponseBody
	public RetData accountLogin(ReqWebData reqArgs,HttpServletRequest request) {
		try {
			//加密密码
			String password = (String)reqArgs.getData().get("password");//AesUtil.aesEncode((String)reqArgs.getData().get("password"), token);

			//调用登录接口
			Map<String, String> header = new HashMap<>();
//			header.put("content-type", "text/html");

			Map<String, String> arg = new HashMap<>();
			String appId=(String) reqArgs.getData().get("appId");//username

			// 根据appId获取其对应所拥有的角色(这里设计为角色对应资源，没有权限对应资源)
			String roles = accountService.loadAccountRole(appId);
			// 时间以秒计算,token有效刷新时间是token有效过期时间的2倍
			long refreshPeriodTime = 36000;//refreshPeriodTime右移一位是jwt中的过期时间。

			String jwt = null;
			if(appId != null &&  appId.startsWith("guest0")) {
				/**
				 * 访客账户多端登录---演示账户特殊处理
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
			AuthUser authUser = userService.getUserByAppId(appId);
			authUser.setPassword(null);
			authUser.setSalt(null);
			//记录登陆日志
			LogExeManager.getInstance().executeLogTask(LogTaskFactory.loginLog(appId, IpUtil.getAllIpFromRequest(WebUtils.toHttp(request)), (short) 1, "登录成功"));


			Message message=  new Message().ok(200, "issue jwt success").addData("jwt", jwt).addData("user", authUser);
			return new RetData(200,message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
}
