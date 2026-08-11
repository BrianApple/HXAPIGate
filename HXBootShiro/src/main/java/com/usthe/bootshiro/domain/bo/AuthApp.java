package com.usthe.bootshiro.domain.bo;

import java.util.Date;

/**
 * 应用信息实体（应用管理：为第三方应用生成访问网关 API 的 JWT license）
 */
public class AuthApp {
    private Integer id;
    /** 应用唯一标识（调用网关 API 时作为 userId 请求头） */
    private String appId;
    /** 应用名称 */
    private String appName;
    /** 应用密钥 */
    private String appSecret;
    /** 应用描述 */
    private String description;
    /** 状态 1:启用 0:停用 */
    private Byte status;
    private Date createTime;
    private Date updateTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    public String getAppSecret() { return appSecret; }
    public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
