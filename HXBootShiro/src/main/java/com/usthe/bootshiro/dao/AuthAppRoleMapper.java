package com.usthe.bootshiro.dao;

import com.usthe.bootshiro.domain.bo.AuthAppRole;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface AuthAppRoleMapper {
    int deleteByPrimaryKey(Integer id) throws DataAccessException;
    int insert(AuthAppRole record) throws DataAccessException;
    int insertSelective(AuthAppRole record) throws DataAccessException;
    AuthAppRole selectByPrimaryKey(Integer id) throws DataAccessException;
    int updateByPrimaryKeySelective(AuthAppRole record) throws DataAccessException;
    int updateByPrimaryKey(AuthAppRole record) throws DataAccessException;
    /** 按应用删除全部角色关联（覆盖式分配用） */
    int deleteByAppId(String appId) throws DataAccessException;
    /** 查询应用的角色ID列表 */
    List<Integer> selectRoleIdsByAppId(String appId) throws DataAccessException;
}
