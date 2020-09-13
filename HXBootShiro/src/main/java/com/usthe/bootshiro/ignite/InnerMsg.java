package com.usthe.bootshiro.ignite;

import java.io.Serializable;

/**
 * 节点通信Bean
 * @Description:    
 * @author: yangcheng,hjj    
 * @date:   2019年9月11日
 */
public class InnerMsg implements Serializable{
	private static final long serialVersionUID = -2652925673553505696L;
	/**
	 * 消息类型
	 * 00 为普通消息
	 * 01 为rpc请求消息
	 * 02 为rpc响应消息
	 * 
	 * API00 更新API网关路由信息
	 * API01 删除API网关路由信息
	 */
	private String type;
	private String uriPattern;
	private String data;
	
	public InnerMsg(String type, String data) {
		super();
		this.type = type;
		this.data = data;
	}
	

	public InnerMsg(String type, String uriPattern, String data) {
		super();
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	
	
}
