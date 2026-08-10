package com.usthe.bootshiro.redis;

import java.io.Serializable;

/**
 * 节点通信 Bean（Redis Pub/Sub 消息体）
 * type:
 *  API00 更新 API 网关路由信息
 *  API01 删除 API 网关路由信息
 */
public class InnerMsg implements Serializable {
    private static final long serialVersionUID = -2652925673553505696L;
    private String type;
    private String uriPattern;
    private String data;

    public InnerMsg() {
    }

    public InnerMsg(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public InnerMsg(String type, String uriPattern, String data) {
        this.type = type;
        this.uriPattern = uriPattern;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUriPattern() {
        return uriPattern;
    }

    public void setUriPattern(String uriPattern) {
        this.uriPattern = uriPattern;
    }
}
