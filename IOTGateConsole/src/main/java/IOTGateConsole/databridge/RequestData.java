package IOTGateConsole.databridge;

import java.io.Serializable;
/**
 * 封装请求参数
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author hejuanjuan
 * @date 2020年9月12日
 * @version 1.0
 */
public class RequestData implements Serializable{
	private static final long serialVersionUID = -497072374733332517L;
	private boolean isBroadcast;//是否广播到所有网关节点
	private String requestNum;//请求编号
	private String className;
	private String methodName;
	private Class<?>[] paramTyps;
	private Object[] args;
	
	/** "args":["你好rpc"],
	 * "className":"IOTGateConsole.rpc.service.RPCExportService",
	 * "methodName":"test",
	 * "paramTyps":["java.lang.String"],
	 * "requestNum":"540bbd93-06a6-4b77-a067-fbd8d0d38f2d"
	 * }
	 */
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getParamTyps() {
		return paramTyps;
	}
	public void setParamTyps(Class<?>[] paramTyps) {
		this.paramTyps = paramTyps;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	public String getRequestNum() {
		return requestNum;
	}
	public void setRequestNum(String requestNum) {
		this.requestNum = requestNum;
	}
	public boolean isBroadcast() {
		return isBroadcast;
	}
	public void setBroadcast(boolean isBroadcast) {
		this.isBroadcast = isBroadcast;
	}
}
