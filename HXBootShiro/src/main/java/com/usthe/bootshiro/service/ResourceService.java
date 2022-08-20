package com.usthe.bootshiro.service;

import com.usthe.bootshiro.domain.bo.AuthResource;

import java.util.List;

/**
 * @author tomsun28
 * @date 13:39 2018/3/18
 */
public interface ResourceService {

    /**
     * description TODO
     *
     * @param appId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     */
    List<AuthResource> getAuthorityMenusByUid(String appId);

    /**
     * description TODO
     *
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     */
    List<AuthResource> getMenus();

    /**
     * description 添加资源
     *
     * @param menu 1
     * @return java.lang.Boolean
     */
    Boolean addMenu(AuthResource menu);

    /**
     * description TODO
     *
     * @param menu 1
     * @return java.lang.Boolean
     */
    Boolean modifyMenu(AuthResource menu);

    /**
     * description TODO
     *
     * @param menuId 1
     * @return java.lang.Boolean
     */
    Boolean deleteMenuByMenuId(Integer menuId);


    /**
     * 获取API类型
     * @param isType  是否获取API根类别--根类别是所有Api类别的父级节点
     * @return
     */
    List<AuthResource> getApiTeamList(AuthResource authResource);

    /**
     * description TODO
     *
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     */
    List<AuthResource> getApiList();

    /**
     * description TODO
     *
     * @param teamId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     */
    List<AuthResource> getApiListByTeamId(Integer teamId);

    /**
     * description TODO
     *
     * @param roleId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     */
    List<AuthResource> getAuthorityApisByRoleId(Integer roleId);

    /**
     * description TODO
     *
     * @param roleId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     */
    List<AuthResource> getAuthorityMenusByRoleId(Integer roleId);

    /**
     * description TODO
     *
     * @param roleId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     */
    List<AuthResource> getNotAuthorityApisByRoleId(Integer roleId);

    /**
     * description TODO
     *
     * @param roleId 1
     * @return java.util.List<com.usthe.bootshiro.domain.bo.AuthResource>
     */
    List<AuthResource> getNotAuthorityMenusByRoleId(Integer roleId);

    /**
     * 根据apiId获取api信息
     * @param apiId
     * @return
     */
	AuthResource getApiInfoByapiId(Integer apiId);

    /**
     * 获取没有被当前角色id关联的所有API
     * @param rId
     * @return
     */
    List<AuthResource> getApiListNotRelatedByRID(int rId);

    /**
     * 获取itmId对应资源类型下，所有没有和角色id关联的api
     * @param itmId
     * @param rId
     * @return
     */
    List<AuthResource> getApiListByTeamIdAndRID(int itmId, int rId);
}
