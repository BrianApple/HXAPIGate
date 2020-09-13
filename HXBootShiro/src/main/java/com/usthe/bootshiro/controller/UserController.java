package com.usthe.bootshiro.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usthe.bootshiro.domain.bo.AuthUser;
import com.usthe.bootshiro.domain.vo.Account;
import com.usthe.bootshiro.domain.vo.Message;
import com.usthe.bootshiro.ignite.IgniteAutoConfig;
import com.usthe.bootshiro.service.UserService;
import com.usthe.bootshiro.shiro.provider.AccountProvider;
import com.usthe.bootshiro.support.factory.LogTaskFactory;
import com.usthe.bootshiro.support.manager.LogExeManager;
import com.usthe.bootshiro.util.IpUtil;
import com.usthe.bootshiro.util.JsonWebTokenUtil;
import com.usthe.bootshiro.util.Md5Util;
import com.usthe.bootshiro.util.RequestResponseUtil;

import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *   用户相关操作
 * @author tomsun28
 * @date 21:05 2018/3/17
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private IgniteAutoConfig igniteAutoConfig;
    
    @Autowired
    private AccountProvider accountProvider;
    /**
     * 用户密码修改
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "密码修改", notes = "PUT用户密码修改")
    @PutMapping("/accountupdate")
    public Message accountUpdate(HttpServletRequest request, HttpServletResponse response) {
    	Map<String, String> params = RequestResponseUtil.getRequestBodyMap(request);
    	String userId = request.getHeader("userId");//用户名
    	String password = params.get("password");//原密码
    	String npassword = params.get("npassword");//新密码
    	Account account = accountProvider.loadAccount(userId);
    	
    	if (account != null) {
            // 用盐对密码进行MD5加密
            if(Md5Util.md5(password+account.getSalt()).equals(account.getPassword()) ) {
            	//原密码正确
            	npassword = Md5Util.md5(npassword+account.getSalt());
            	AuthUser user = new AuthUser();
            	user.setPassword(npassword);
            	user.setUid(userId);
            	userService.updatePassword(user);
            }else {
            	return new Message().ok(1002, "password error");
            }
        }
    	
//    	LogExeManager.getInstance().executeLogTask(LogTaskFactory.loginLog(appId, IpUtil.getIpFromRequest(WebUtils.toHttp(request)), (short) 1, "密码修改成功"));
    	
    	return new Message().ok(6666, "update success");
    }

    @ApiOperation(value = "获取对应用户角色",notes = "GET根据用户的appId获取对应用户的角色")
    @GetMapping("/role/{appId}")
    public Message getUserRoleList(@PathVariable String appId) {
        String roles = userService.loadAccountRole(appId);
        Set<String> roleSet = JsonWebTokenUtil.split(roles);
        LOGGER.debug(roleSet.toString());
    	return new Message().ok(6666,"return roles success").addData("roles",roleSet);
    	
    }


    @SuppressWarnings("unchecked")
    @ApiOperation(value = "获取用户列表",notes = "GET获取所有注册用户的信息列表")
    @GetMapping("/list/{start}/{limit}")
    public Message getUserList(@PathVariable Integer start, @PathVariable Integer limit) {

        PageHelper.startPage(start,limit);
        List<AuthUser> authUsers = userService.getUserList();
        authUsers.forEach(user->user.setPassword(null));
        PageInfo pageInfo = new PageInfo(authUsers);
        return new Message().ok(6666,"return user list success").addData("pageInfo",pageInfo);
    }

    @ApiOperation(value = "给用户授权添加角色",httpMethod = "POST")
    @PostMapping("/authority/role")
    public Message authorityUserRole(HttpServletRequest request) {
        Map<String,String> map = getRequestBody(request);
        String uid = map.get("uid");
        int roleId = Integer.parseInt(((Object)map.get("roleId")).toString());
        boolean flag = userService.authorityUserRole(uid,roleId);
        return flag ? new Message().ok(6666,"authority success") : new Message().error(1111,"authority error");
    }

    @ApiOperation(value = "删除已经授权的用户角色",httpMethod = "DELETE")
    @DeleteMapping("/authority/role/{uid}/{roleId}")
    public Message deleteAuthorityUserRole(@PathVariable String uid, @PathVariable Integer roleId) {
        return userService.deleteAuthorityUserRole(uid,roleId) ? new Message().ok(6666,"delete success") : new Message().error(1111,"delete fail");
    }


    @ApiOperation(value = "用户登出", httpMethod = "POST")
    @PostMapping("/exit")
    public Message accountExit(HttpServletRequest request) {
        SecurityUtils.getSubject().logout();
        Map<String,String > map = getRequestHeader(request);
        String userId = map.get("userId");
        if (StringUtils.isEmpty(userId)) {
            return new Message().error(1111, "用户未登录无法登出");
        }
        Object jwt = igniteAutoConfig.getCommonData("JWT-SESSION-"+userId);
//        String jwt = redisTemplate.opsForValue().get("JWT-SESSION-"+userId);
        if (StringUtils.isEmpty(jwt)) {
            return new Message().error(1111, "用户未登录无法登出");
        }
        igniteAutoConfig.removeCommonData("JWT-SESSION-"+userId);
//        redisTemplate.opsForValue().getOperations().delete("JWT-SESSION-"+userId);
        LogExeManager.getInstance().executeLogTask(LogTaskFactory.exitLog(userId,request.getRemoteAddr(),(short)1,""));

        return new Message().ok(6666, "用户退出成功");
    }


}
