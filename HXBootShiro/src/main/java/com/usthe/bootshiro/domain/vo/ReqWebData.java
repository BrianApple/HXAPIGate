package com.usthe.bootshiro.domain.vo;


import com.usthe.bootshiro.domain.bo.AuthResource;

import java.util.List;
import java.util.Map;

public class ReqWebData {
	private Map data;
	private List<Object> dataList;
	private String str;
	private String jwt;
	private AuthResource bootShiroResource;//对应bootshiro的资源类，属性与数据库字段一一对应
	private String pageIndex;//从1开始
	private String pageSize;//默认每页10个
	public Map getData() {
		return data;
	}

	public void setData(Map data) {
		this.data = data;
	}

	public List<Object> getDataList() {
		return dataList;
	}

	public void setDataList(List<Object> dataList) {
		this.dataList = dataList;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public String getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(String pageIndex) {
		this.pageIndex = pageIndex;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public AuthResource getBootShiroResource() {
		return bootShiroResource;
	}

	public void setBootShiroResource(AuthResource bootShiroResource) {
		this.bootShiroResource = bootShiroResource;
	}
	


	

	
}
