package com.usthe.bootshiro.service.impl;

import com.usthe.bootshiro.dao.AuthResourceMapper;
import com.usthe.bootshiro.domain.bo.AuthResource;
import com.usthe.bootshiro.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tomsun28
 * @date 10:59 2018/3/26
 */
@Service("ResourceService")
public class ResourceServiceImpl implements ResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Autowired
    private AuthResourceMapper authResourceMapper;

    @Override
    public List<AuthResource> getAuthorityMenusByUid(String appId) throws DataAccessException {
        return authResourceMapper.selectAuthorityMenusByUid(appId);
    }

    @Override
    public List<AuthResource> getMenus() throws DataAccessException {
        return authResourceMapper.selectMenus();
    }

    @Override
    public Boolean addMenu(AuthResource menu) throws DataAccessException {

        if(3 == menu.getType()){
            //当添加的是资源类别时，根据资源类别名称去重
            List<AuthResource>  resources = authResourceMapper.selectApiListByName(menu);
            if (resources.size()>0){
                return false;
            }
        }else {
            //添加的是普通的APi资源时
            List<AuthResource>  resources = authResourceMapper.selectApiByIdAndVersion(menu);
            if (resources.size() > 0 ){
                //同一uri+version  不能重复
                return false;
            }
        }
        int num = authResourceMapper.insertSelective(menu);
        return num == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Boolean modifyMenu(AuthResource menu) throws DataAccessException {
        int num = authResourceMapper.updateByPrimaryKeySelective(menu);
        return num == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Boolean deleteMenuByMenuId(Integer menuId) throws DataAccessException {
        int num = authResourceMapper.deleteByPrimaryKey(menuId);
        return num == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public List<AuthResource> getApiTeamList(AuthResource authResource) throws DataAccessException {
        return authResourceMapper.selectApiTeamList(authResource);
    }

    @Override
    public List<AuthResource> getApiList() throws DataAccessException {
        return authResourceMapper.selectApiList();
    }

    @Override
    public List<AuthResource> getApiListByTeamId(Integer teamId) throws DataAccessException {
        return authResourceMapper.selectApiListByTeamId(teamId);
    }

    @Override
    public List<AuthResource> getAuthorityApisByRoleId(Integer roleId) throws DataAccessException {
        return authResourceMapper.selectApisByRoleId(roleId);
    }

    @Override
    public List<AuthResource> getAuthorityMenusByRoleId(Integer roleId) throws DataAccessException {
        return authResourceMapper.selectMenusByRoleId(roleId);
    }

    @Override
    public List<AuthResource> getNotAuthorityApisByRoleId(Integer roleId) throws DataAccessException {
        return authResourceMapper.selectNotAuthorityApisByRoleId(roleId);
    }

    @Override
    public List<AuthResource> getNotAuthorityMenusByRoleId(Integer roleId) throws DataAccessException {
        return authResourceMapper.selectNotAuthorityMenusByRoleId(roleId);
    }

	@Override
	public AuthResource getApiInfoByapiId(Integer apiId) {
		// TODO Auto-generated method stub
		return authResourceMapper.selectByPrimaryKey(apiId);
	}

    @Override
    public List<AuthResource> getApiListNotRelatedByRID(int rId) {
        return authResourceMapper.selectNotAuthorityApisByRoleId(rId);
    }

    @Override
    public List<AuthResource> getApiListByTeamIdAndRID(int itmId, int rId) {
        return authResourceMapper.selectNotAuthorityApisByTeamIdAndRID(itmId,rId);
    }

}
