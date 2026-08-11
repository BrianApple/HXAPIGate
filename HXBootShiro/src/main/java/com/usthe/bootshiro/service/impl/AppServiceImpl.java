package com.usthe.bootshiro.service.impl;

import com.usthe.bootshiro.dao.AuthAppMapper;
import com.usthe.bootshiro.dao.AuthAppRoleMapper;
import com.usthe.bootshiro.dao.AuthRoleMapper;
import com.usthe.bootshiro.domain.bo.AuthApp;
import com.usthe.bootshiro.domain.bo.AuthAppRole;
import com.usthe.bootshiro.domain.bo.AuthRole;
import com.usthe.bootshiro.service.AppService;
import com.usthe.bootshiro.util.CommonUtil;
import com.usthe.bootshiro.util.JsonWebTokenUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用管理服务实现：应用 CRUD + 生成 API 访问 JWT license。
 * license 与用户登录 JWT 同构（SECRET_KEY + HS512，roles=角色编码），网关 JwtRealm 直接解析校验。
 */
@Service("AppService")
public class AppServiceImpl implements AppService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppServiceImpl.class);
    /** 与登录 JWT 保持一致的签发者 */
    private static final String ISSUER = "UIOTCP_BOOTSHIRO_PRO";
    private static final String APP_ID_PREFIX = "app_";

    @Autowired
    private AuthAppMapper authAppMapper;
    @Autowired
    private AuthAppRoleMapper authAppRoleMapper;
    @Autowired
    private AuthRoleMapper authRoleMapper;

    @Override
    public List<AuthApp> getAppList() throws DataAccessException {
        List<AuthApp> apps = authAppMapper.selectAppList();
        if (apps != null) {
            // 不下发密钥明文
            apps.forEach(app -> app.setAppSecret(null));
        }
        return apps;
    }

    @Override
    public boolean addApp(AuthApp app, List<Integer> roleIds) throws DataAccessException {
        if (app == null || app.getAppName() == null || app.getAppName().trim().isEmpty()) {
            return false;
        }
        // 自动生成应用标识与密钥
        String appId = APP_ID_PREFIX + CommonUtil.getRandomString(12);
        while (authAppMapper.selectByAppId(appId) != null) {
            appId = APP_ID_PREFIX + CommonUtil.getRandomString(12);
        }
        app.setAppId(appId);
        app.setAppSecret(CommonUtil.getRandomString(24));
        if (app.getStatus() == null) {
            app.setStatus((byte) 1);
        }
        Date now = new Date();
        app.setCreateTime(now);
        app.setUpdateTime(now);
        int num = authAppMapper.insertSelective(app);
        if (num != 1) {
            return false;
        }
        // 绑定角色
        bindRoles(appId, roleIds);
        return true;
    }

    @Override
    public boolean updateApp(AuthApp app, List<Integer> roleIds) throws DataAccessException {
        if (app == null || app.getId() == null) {
            return false;
        }
        AuthApp exist = authAppMapper.selectByPrimaryKey(app.getId());
        if (exist == null) {
            return false;
        }
        app.setAppId(exist.getAppId());
        app.setAppSecret(null); // 密钥不通过编辑修改
        app.setUpdateTime(new Date());
        int num = authAppMapper.updateByPrimaryKeySelective(app);
        if (num != 1) {
            return false;
        }
        // 覆盖式重新绑定角色
        bindRoles(exist.getAppId(), roleIds);
        return true;
    }

    @Override
    public boolean deleteApp(Integer id) throws DataAccessException {
        AuthApp exist = authAppMapper.selectByPrimaryKey(id);
        if (exist == null) {
            return false;
        }
        // 删除角色关联 + 应用
        authAppRoleMapper.deleteByAppId(exist.getAppId());
        return authAppMapper.deleteByPrimaryKey(id) == 1;
    }

    @Override
    public AuthApp getAppByAppId(String appId) throws DataAccessException {
        return authAppMapper.selectByAppId(appId);
    }

    @Override
    public List<Integer> getAppRoleIds(String appId) throws DataAccessException {
        return authAppRoleMapper.selectRoleIdsByAppId(appId);
    }

    @Override
    public Map<String, Object> generateLicense(String appId, long expireSeconds) throws DataAccessException {
        AuthApp app = authAppMapper.selectByAppId(appId);
        if (app == null) {
            return null;
        }
        if (app.getStatus() != null && app.getStatus() != 1) {
            throw new IllegalStateException("应用已停用，无法生成 license");
        }
        List<Integer> roleIds = authAppRoleMapper.selectRoleIdsByAppId(appId);
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalStateException("应用未绑定任何角色，无法生成 license");
        }
        // 角色ID → 角色编码（license 中 roles 为编码集合，网关按编码校验）
        Map<Integer, AuthRole> roleMap = authRoleMapper.selectRoles().stream()
                .collect(Collectors.toMap(AuthRole::getId, r -> r, (a, b) -> a));
        List<String> roleCodes = roleIds.stream()
                .map(roleMap::get)
                .filter(Objects::nonNull)
                .map(AuthRole::getCode)
                .collect(Collectors.toList());
        if (roleCodes.isEmpty()) {
            throw new IllegalStateException("应用绑定的角色已失效，无法生成 license");
        }
        long period = expireSeconds > 0 ? expireSeconds : 86400L; // 默认 1 天
        String roles = String.join(",", roleCodes);
        String jwt = JsonWebTokenUtil.issueJWT(UUID.randomUUID().toString(), appId,
                ISSUER, period, roles, null, SignatureAlgorithm.HS512);
        Map<String, Object> result = new HashMap<>();
        result.put("jwt", jwt);
        result.put("appId", appId);
        result.put("roles", roleCodes);
        result.put("expireAt", System.currentTimeMillis() + period * 1000);
        result.put("expireSeconds", period);
        LOGGER.info("生成应用 license: appId={}, roles={}, 有效期={}s", appId, roles, period);
        return result;
    }

    /** 覆盖式绑定角色：先删后插 */
    private void bindRoles(String appId, List<Integer> roleIds) {
        authAppRoleMapper.deleteByAppId(appId);
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        Date now = new Date();
        for (Integer roleId : roleIds) {
            if (roleId == null) {
                continue;
            }
            AuthAppRole rel = new AuthAppRole();
            rel.setAppId(appId);
            rel.setRoleId(roleId);
            rel.setCreateTime(now);
            rel.setUpdateTime(now);
            authAppRoleMapper.insertSelective(rel);
        }
    }
}
