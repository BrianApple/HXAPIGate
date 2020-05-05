package com.usthe.bootshiro.service.impl;

import com.usthe.bootshiro.dao.AuthUserMapper;
import com.usthe.bootshiro.domain.bo.AuthUser;
import com.usthe.bootshiro.domain.vo.Account;
import com.usthe.bootshiro.service.AccountService;
import com.usthe.bootshiro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author tomsun28
 * @date 22:04 2018/3/7
 */
@Service("AccountService")
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AuthUserMapper userMapper;

    @Autowired
    private UserService userService;

    @Override
    public Account loadAccount(String appId) throws DataAccessException {
        AuthUser user = userMapper.selectByUniqueKey(appId);
        return user != null ? new Account(user.getUsername(),user.getPassword(),user.getSalt()) : null;
    }

    @Override
    public String  isAccountExist(AuthUser authUser) {
    	if(userMapper.selectByPrimaryKey(authUser.getUid()) != null) {
    		return "用户名已存在";
    	}
    	if(authUser.getPhone() != null) {
    		if(userMapper.isExistPhone(authUser).size()>0) {
    			return "手机号已存在";
    		}
    	}
    	if(authUser.getEmail() != null) {
    		if(userMapper.isExistEmail(authUser).size()>0) {
    			return "邮箱已存在";
    		}
    	}
    	return null;
    }

    @Transactional
    @Override
    public boolean registerAccount(AuthUser account) throws DataAccessException {
//    	int size = userMapper.isUserExist(account).size();
//    	if(size != 0) {
//    		return false;
//    	}
        // 给新用户授权普通用户角色
        userService.authorityUserRole(account.getUid(),102);

        return userMapper.insertSelective(account) ==1 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public String loadAccountRole(String appId) throws DataAccessException {

        return userMapper.selectUserRoles(appId);
    }



}
