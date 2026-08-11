package com.usthe.bootshiro.service;

import com.usthe.bootshiro.domain.bo.AuthUser;

import java.util.List;

/**
 * @author tomsun28
 * @date 21:14 2018/3/17
 */
public interface UserService {

    /**
     * description TODO
     *
     * @param appId 1
     * @return java.lang.String
     */
    String loadAccountRole(String appId);

    /**
     * description TODO
     *
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthUser>
     */
    List<AuthUser> getUserList();

    /**
     * description TODO
     *
     * @param roleId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthUser>
     */
    List<AuthUser> getUserListByRoleId(Integer roleId);

    /**
     * description TODO
     *
     * @param appId 1
     * @param roleId 2
     * @return boolean
     */
    boolean authorityUserRole(String appId, int roleId);

    /**
     * description TODO
     *
     * @param uid 1
     * @param roleId 2
     * @return boolean
     */
    boolean deleteAuthorityUserRole(String uid,int roleId);

    /**
     * description TODO
     *
     * @param appId 1
     * @return com.usthe.bootshiro.domain.bo.AuthUser
     */
    AuthUser getUserByAppId(String appId);

    /**
     * description TODO
     *
     * @param roleId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthUser>
     */
    List<AuthUser> getNotAuthorityUserListByRoleId(Integer roleId);

    /**
     * description 更改用户名密码
     *
     * @param authUser  主要判断uid  phone和email
     * @return String 错误信息
     */
    String updatePassword(AuthUser authUser);

    // ==================== 管理平台：用户管理 ====================

    /** 新增用户（自动生成盐 + MD5 密码） */
    boolean addUser(AuthUser user);

    /** 编辑用户基本参数 */
    boolean updateUser(AuthUser user);

    /** 删除用户（软删 status=3） */
    boolean deleteUser(String uid);

    /** 重置密码（生成新盐 + MD5） */
    boolean resetPassword(String uid, String newPassword);

    /** 覆盖式分配用户角色（先删后插） */
    boolean authorityUserRoles(String uid, List<Integer> roleIds);

    /** 用户角色ID列表 */
    List<Integer> getUserRoleIds(String uid);

    /** 用户角色名称（逗号分隔，列表页展示） */
    String loadAccountRoleNames(String appId);
}
