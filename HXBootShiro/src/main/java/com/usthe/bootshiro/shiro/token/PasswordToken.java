package com.usthe.bootshiro.shiro.token;

import com.usthe.bootshiro.util.AesUtil;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * 用于封装登录的信息用于shiro的login认证判断
 * @author tomsun28
 * @date 12:34 2018/2/27
 */
public class PasswordToken implements AuthenticationToken{

    private static final long serialVersionUID = 1L;
    /**
         * 用户
     */
    private String appId;
    private String password;
    private String timestamp;
    private String host;
    //登陆是密码加密所需动态密钥
    private String tokenKey;

    public PasswordToken(String appId, String password, String timestamp, String host,String tokenKey) throws Exception {
        this.appId = appId;
        this.timestamp = timestamp;
        this.host = host;
        this.password = password;//明文不用解密
        this.tokenKey = tokenKey;

    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return this.password;
    }

    @Override
    public Object getPrincipal() {
        return this.appId;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }
}
