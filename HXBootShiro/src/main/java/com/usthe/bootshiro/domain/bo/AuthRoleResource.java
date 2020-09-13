package com.usthe.bootshiro.domain.bo;

import java.util.Date;

/**
 *   角色资源绑定
 * @author tomsun28
 * @date 11:09 2018/4/22
 */
public class AuthRoleResource {
    private Integer id;

    private Integer roleId;
    //api和菜单id
    private Integer resourceId;
    //用户id
    private String userId;

    private Date createTime;

    private Date updateTime;
    
    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}