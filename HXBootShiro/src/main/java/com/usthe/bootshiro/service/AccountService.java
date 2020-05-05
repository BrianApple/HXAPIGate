package com.usthe.bootshiro.service;

import com.usthe.bootshiro.domain.bo.AuthUser;
import com.usthe.bootshiro.domain.vo.Account;

/**
 * @author tomsun28
 * @date 22:02 2018/3/7
 */
public interface AccountService {

    /**
     * description TODO
     *
     * @param appId 1
     * @return com.usthe.bootshiro.domain.vo.Account
     */
    Account loadAccount(String appId);
    
    /**
     * description 判断当前用户是否存在
     *
     * @param authUser  主要判断uid  phone和email
     * @return String 错误信息
     */
    String isAccountExist(AuthUser authUser);
    /**
     * description TODO
     *
     * @param account 1
     * @return boolean
     */
    boolean registerAccount(AuthUser account);
    /**
     * description TODO
     *
     * @param appId 1
     * @return java.lang.String
     */
    String loadAccountRole(String appId);
}
