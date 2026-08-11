package com.usthe.bootshiro.dao;

import com.usthe.bootshiro.domain.bo.AuthApp;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface AuthAppMapper {
    int deleteByPrimaryKey(Integer id) throws DataAccessException;
    int insert(AuthApp record) throws DataAccessException;
    int insertSelective(AuthApp record) throws DataAccessException;
    AuthApp selectByPrimaryKey(Integer id) throws DataAccessException;
    AuthApp selectByAppId(String appId) throws DataAccessException;
    int updateByPrimaryKeySelective(AuthApp record) throws DataAccessException;
    int updateByPrimaryKey(AuthApp record) throws DataAccessException;
    List<AuthApp> selectAppList() throws DataAccessException;
}
