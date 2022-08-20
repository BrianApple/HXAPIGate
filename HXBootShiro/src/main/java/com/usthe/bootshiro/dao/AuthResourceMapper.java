package com.usthe.bootshiro.dao;

import com.usthe.bootshiro.domain.bo.AuthResource;
import com.usthe.bootshiro.shiro.rule.RolePermRule;
import org.springframework.dao.DataAccessException;
import java.util.List;

/**
 * @author tomsun28
 * @date 9:28 2018/4/22
 */
public interface AuthResourceMapper {
    /**
     * description TODO
     *
     * @param id 1
     * @return int
     * @throws DataAccessException when
     */
    int deleteByPrimaryKey(Integer id) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int insert(AuthResource record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int insertSelective(AuthResource record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param id 1
     * @return com.usthe.bootshiro.domain.bo.AuthResource
     * @throws DataAccessException when
     */
    AuthResource selectByPrimaryKey(Integer id) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int updateByPrimaryKeySelective(AuthResource record) throws DataAccessException;

    /**
     * description TODO
     *
     * @param record 1
     * @return int
     * @throws DataAccessException when
     */
    int updateByPrimaryKey(AuthResource record) throws DataAccessException;

    /**
     * description TODO
     *
     * @return java.util.List<com.usthe.bootshiro.shiro.rule.RolePermRule>
     * @throws DataAccessException when
     */
    List<RolePermRule> selectRoleRules()  throws DataAccessException;
    /**
     * description TODO
     *
     * @return java.util.List<com.usthe.bootshiro.shiro.rule.RolePermRule>
     * @throws DataAccessException when
     */
    RolePermRule selectRoleRulesByResouceId(String id)  throws DataAccessException;

    /**
     * description TODO
     *
     * @param appId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     * @throws DataAccessException when
     */
    List<AuthResource> selectAuthorityMenusByUid(String appId) throws DataAccessException;

    /**
     * description TODO
     *
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     * @throws DataAccessException when
     */
    List<AuthResource> selectMenus() throws DataAccessException;

    /**
     * description TODO
     *
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     * @throws DataAccessException when
     */
    List<AuthResource> selectApiTeamList(AuthResource authResource) throws DataAccessException;

    /**
     * description TODO
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     * @throws DataAccessException when
     */
    List<AuthResource> selectApiList() throws DataAccessException;

    /**
     * description TODO
     *
     * @param teamId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     * @throws DataAccessException when
     */
    List<AuthResource> selectApiListByTeamId(Integer teamId) throws DataAccessException;

    /**
     * description 根据资源URI和设备版本信息组合查询
     */
    List<AuthResource> selectApiByIdAndVersion(AuthResource authResource) throws DataAccessException;

    /**
     * description TODO
     *
     * @param roleId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     * @throws DataAccessException when
     */
    List<AuthResource> selectApisByRoleId(Integer roleId) throws DataAccessException;

    /**
     * description TODO
     *
     * @param roleId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     * @throws DataAccessException when
     */
    List<AuthResource> selectMenusByRoleId(Integer roleId) throws DataAccessException;

    /**
     * description TODO
     *
     * @param roleId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     * @throws DataAccessException when
     */
    List<AuthResource> selectNotAuthorityApisByRoleId(Integer roleId) throws DataAccessException;

    /**
     * description TODO
     *
     * @param roleId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     * @throws DataAccessException when
     */
    List<AuthResource> selectNotAuthorityMenusByRoleId(Integer roleId) throws DataAccessException;

    /**
     * 通过资源名称查询资源，用于资源类型的去重
     * @param menu
     */
    List<AuthResource>  selectApiListByName(AuthResource menu);

    /**
     * 获取itmId对应资源类型下，所有没有和角色id关联的api
     * @param itmId
     * @param rId 角色id
     * @return
     */
    List<AuthResource> selectNotAuthorityApisByTeamIdAndRID(int itmId, int rId);
}