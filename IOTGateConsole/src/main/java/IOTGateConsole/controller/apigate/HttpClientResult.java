package IOTGateConsole.controller.apigate;

import java.io.Serializable;
/**
 * 
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author hejuanjuan
 * @date 2020年7月13日
 * @version 1.0
 */
public class HttpClientResult implements Serializable{
	/**
	 * 响应状态码
	 */
	private int code;

	/**
	 * 响应数据
	 */
	private String content;
	
	public HttpClientResult(int code) {
		super();
		this.code = code;
	}

	public HttpClientResult(int code, String content) {
		super();
		this.code = code;
		this.content = content;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}



	
	
}
