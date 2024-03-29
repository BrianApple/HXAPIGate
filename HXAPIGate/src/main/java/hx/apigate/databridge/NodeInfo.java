package hx.apigate.databridge;

import java.io.Serializable;

import hx.apigate.databridge.xmlBean.RouteNode;

/**
 * <p>Description: 存放当前url选择的路由的信息</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年12月6日
　 * @version 1.0
 */
public class NodeInfo implements Serializable{
	private static final long serialVersionUID = 7278672338145125207L;
	private String interfaceVserion;
	private String protocalTemp;
	private String methodName;
	private RouteNode routeNode;
	private boolean needAuth;
	private String requestUrl;
	private String circleBreakKey;

	/**
	 * @param interfaceVserion
	 * @param protocalTemp
	 * @param routeNode
	 * @param requestUrl APi接口重发时，需要该参数
	 * @param needAuth
	 * @param circleBreakKey
	 */
	public NodeInfo(String interfaceVserion, String protocalTemp, RouteNode routeNode,String requestUrl,boolean needAuth,String circleBreakKey) {
		super();
		this.interfaceVserion = interfaceVserion;
		this.protocalTemp = protocalTemp;
		this.routeNode = routeNode;
		this.needAuth = needAuth;
		this.circleBreakKey = circleBreakKey;
	}
	
	public NodeInfo(String interfaceVserion, String protocalTemp, String methodName, RouteNode routeNode,String requestUrl,boolean needAuth,String circleBreakKey) {
		super();
		this.interfaceVserion = interfaceVserion;
		this.protocalTemp = protocalTemp;
		this.methodName = methodName;
		this.routeNode = routeNode;
		this.requestUrl = requestUrl;
		this.needAuth = needAuth;
		this.circleBreakKey = circleBreakKey;
	}

	public String getInterfaceVserion() {
		return interfaceVserion;
	}

	public void setInterfaceVserion(String interfaceVserion) {
		this.interfaceVserion = interfaceVserion;
	}

	public String getProtocalTemp() {
		return protocalTemp;
	}

	public void setProtocalTemp(String protocalTemp) {
		this.protocalTemp = protocalTemp;
	}

	public RouteNode getRouteNode() {
		return routeNode;
	}

	public void setRouteNode(RouteNode routeNode) {
		this.routeNode = routeNode;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}


	public boolean isNeedAuth() {
		return needAuth;
	}


	public void setNeedAuth(boolean needAuth) {
		this.needAuth = needAuth;
	}


	public String getRequestUrl() {
		return requestUrl;
	}


	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getCircleBreakKey() {
		return circleBreakKey;
	}

	public void setCircleBreakKey(String circleBreakKey) {
		this.circleBreakKey = circleBreakKey;
	}
}
