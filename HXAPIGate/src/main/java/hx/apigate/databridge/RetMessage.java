package hx.apigate.databridge;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hx.apigate.util.SystemClockUtil;

/**
 * @author yangcheng
 * @date 10:48 2018/2/14
 */
public class RetMessage {
	private int code;
	private String msg;
	private Object data;
	private long timestamp;
	
    public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public RetMessage ok(int statusCode,Object retMsg) {
    	this.code = statusCode;
    	this.data = retMsg;
    	this.timestamp = SystemClockUtil.now();
        return this;
    }
    public RetMessage error(int statusCode,String statusMsg) {
    	this.code = statusCode;
    	this.msg = statusMsg;
    	this.timestamp = SystemClockUtil.now();
        return this;
    }
}
