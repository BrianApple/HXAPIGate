package com.usthe.bootshiro.service.impl;

import com.usthe.bootshiro.dao.AuthUserMapper;
import com.usthe.bootshiro.dao.AuthUserRoleMapper;
import com.usthe.bootshiro.domain.bo.AuthUser;
import com.usthe.bootshiro.domain.bo.AuthUserRole;
import com.usthe.bootshiro.service.UserService;
import com.usthe.bootshiro.util.CommonUtil;
import com.usthe.bootshiro.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * @author tomsun28
 * @date 21:15 2018/3/17
 */
@Service("UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthUserMapper userMapper;

    @Autowired
    private AuthUserRoleMapper authUserRoleMapper;

    @Override
    public String loadAccountRole(String appId) throws DataAccessException {

        return userMapper.selectUserRoles(appId);
    }

    @Override
    public List<AuthUser> getUserList() throws DataAccessException {
        return userMapper.selectUserList();
    }

    @Override
    public List<AuthUser> getUserListByRoleId(Integer roleId) throws DataAccessException {
        return userMapper.selectUserListByRoleId(roleId);
    }

    @Override
    public boolean authorityUserRole(String uid, int roleId) throws DataAccessException {
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setRoleId(roleId);
        authUserRole.setUserId(uid);
        authUserRole.setCreateTime(new Date());
        authUserRole.setUpdateTime(new Date());
        return authUserRoleMapper.insert(authUserRole) == 1? Boolean.TRUE :Boolean.FALSE;
    }

    @Override
    public boolean deleteAuthorityUserRole(String uid, int roleId) throws DataAccessException {
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setUserId(uid);
        authUserRole.setRoleId(roleId);
        return authUserRoleMapper.deleteByUniqueKey(authUserRole) == 1? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public AuthUser getUserByAppId(String appId) throws DataAccessException {

        return userMapper.selectByUniqueKey(appId);
    }

    @Override
    public List<AuthUser> getNotAuthorityUserListByRoleId(Integer roleId) throws DataAccessException {

        return userMapper.selectUserListExtendByRoleId(roleId);
    }

    @Override
    public String updatePassword(AuthUser authUser) {
        userMapper.updateByPrimaryKeySelective(authUser);
        return "success";
    }

    // ==================== 管理平台：用户管理 ====================

    @Override
    public boolean addUser(AuthUser user) {
        if (user == null || user.getUid() == null || user.getUid().trim().isEmpty()
                || user.getPassword() == null || user.getPassword().isEmpty()) {
            return false;
        }
        // 生成盐 + MD5(密码+盐)
        String salt = CommonUtil.getRandomString(6);
        user.setSalt(salt);
        user.setPassword(Md5Util.md5(user.getPassword() + salt));
        if (user.getStatus() == null) {
            user.setStatus((byte) 1);
        }
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        return userMapper.insertSelective(user) == 1;
    }

    @Override
    public boolean updateUser(AuthUser user) {
        if (user == null || user.getUid() == null || user.getUid().trim().isEmpty()) {
            return false;
        }
        // 密码/盐不允许通过编辑接口修改（走重置密码）
        user.setPassword(null);
        user.setSalt(null);
        user.setUpdateTime(new Date());
        return userMapper.updateByPrimaryKeySelective(user) == 1;
    }

    @Override
    public boolean deleteUser(String uid) {
        if (uid == null || uid.trim().isEmpty()) {
            return false;
        }
        AuthUser user = new AuthUser();
        user.setUid(uid);
        user.setStatus((byte) 3); // 3.删除（软删）
        user.setUpdateTime(new Date());
        boolean flag = userMapper.updateByPrimaryKeySelective(user) == 1;
        if (flag) {
            // 一并清理角色关联
            authUserRoleMapper.deleteByUserId(uid);
        }
        return flag;
    }

    @Override
    public boolean resetPassword(String uid, String newPassword) {
        if (uid == null || uid.trim().isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return false;
        }
        AuthUser user = new AuthUser();
        user.setUid(uid);
        String salt = CommonUtil.getRandomString(6);
        user.setSalt(salt);
        user.setPassword(Md5Util.md5(newPassword + salt));
        user.setUpdateTime(new Date());
        return userMapper.updateByPrimaryKeySelective(user) == 1;
    }

    @Override
    public boolean authorityUserRoles(String uid, List<Integer> roleIds) {
        if (uid == null || uid.trim().isEmpty()) {
            return false;
        }
        authUserRoleMapper.deleteByUserId(uid);
        if (roleIds == null || roleIds.isEmpty()) {
            return true;
        }
        Date now = new Date();
        for (Integer roleId : roleIds) {
            if (roleId == null) {
                continue;
            }
            AuthUserRole rel = new AuthUserRole();
            rel.setUserId(uid);
            rel.setRoleId(roleId);
            rel.setCreateTime(now);
            rel.setUpdateTime(now);
            authUserRoleMapper.insert(rel);
        }
        return true;
    }

    @Override
    public List<Integer> getUserRoleIds(String uid) {
        return authUserRoleMapper.selectRoleIdsByUserId(uid);
    }

    @Override
    public String loadAccountRoleNames(String appId) {
        return userMapper.selectUserRoleNames(appId);
    }
}
