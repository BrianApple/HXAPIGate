package com.usthe.bootshiro.service;

import com.usthe.bootshiro.domain.bo.AuthApp;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

/**
 * 应用管理服务：应用 CRUD + 生成 API 访问 JWT license
 */
public interface AppService {

    List<AuthApp> getAppList() throws DataAccessException;

    /** 新增应用（自动生成 appId/secret），并绑定角色 */
    boolean addApp(AuthApp app, List<Integer> roleIds) throws DataAccessException;

    /** 编辑应用基本信息 + 覆盖式角色绑定 */
    boolean updateApp(AuthApp app, List<Integer> roleIds) throws DataAccessException;

    boolean deleteApp(Integer id) throws DataAccessException;

    AuthApp getAppByAppId(String appId) throws DataAccessException;

    /** 应用的角色ID列表 */
    List<Integer> getAppRoleIds(String appId) throws DataAccessException;

    /**
     * 生成 JWT license：按应用角色签发 JWT（subject=appId, roles=角色编码集合）
     * @param appId 应用标识
     * @param expireSeconds license 有效期（秒）
     * @return {jwt, expireAt, roles} 或 null（应用不存在/停用/无角色）
     */
    Map<String, Object> generateLicense(String appId, long expireSeconds) throws DataAccessException;
}
