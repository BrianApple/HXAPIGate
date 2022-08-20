package com.usthe.bootshiro.ignite;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 通用的数据缓存
 */
public class CommonCacheData{
	long initTime;//初始化时间
	long existTime;//有效时间，单位秒
	String data;//具体的数据
	
	/**
	 * 
	 * @param existTime 存在时间，单位秒
	 * @param data 数据
	 */
	public CommonCacheData(long existTime, String data) {
		super();
		initTime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
		this.existTime = existTime;
		this.data = data;
	}
	
	
	public long getInitTime() {
		return initTime;
	}
	public void setInitTime(long initTime) {
		this.initTime = initTime;
	}
	public long getExistTime() {
		return existTime;
	}
	public void setExistTime(long existTime) {
		this.existTime = existTime;
	}
	public String getData() {
		long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
		if(now - initTime > existTime) {
			return null;
		}else {
			return data;
		}
		
	}
	public void setData(String data) {
		this.data = data;
	}
	
	
}