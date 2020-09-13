package IOTGateConsole.databridge;

import java.io.Serializable;
import java.util.List;

import IOTGateConsole.controller.apigate.HttpClientResult;
/**
 * 
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author hejuanjuan
 * @date 2020年9月13日
 * @version 1.0
 */
public class RetData implements Serializable{
	private static final long serialVersionUID = 1L;
	private int retSig;
	private Object httpRet;
	private List<Object> data;
	
	
	
	public RetData() {
		super();
	}


	public RetData(int retSig) {
		super();
		this.retSig = retSig;
	}
	
	
	public RetData(int retSig, List<Object> data) {
		super();
		this.retSig = retSig;
		this.data = data;
	}

	
	public RetData(int retSig, Object httpRet) {
		super();
		this.retSig = retSig;
		this.httpRet = httpRet;
	}


	public int getRetSig() {
		return retSig;
	}
	public void setRetSig(int retSig) {
		this.retSig = retSig;
	}
	public List<Object> getData() {
		return data;
	}
	public void setData(List<Object> data) {
		this.data = data;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public Object getHttpRet() {
		return httpRet;
	}


	public void setHttpRet(Object httpRet) {
		this.httpRet = httpRet;
	}




	
}
