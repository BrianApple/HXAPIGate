package hx.apigate.databridge;
/**
 * 熔断异常
 * @Description: 
 * @author  yangcheng
 * @date: 2020年6月22日
 */
public class CircleBreakException extends RuntimeException{
	private static final long serialVersionUID = 7378980587804197070L;
	private String msg;
	
	public CircleBreakException(String msg) {
		super();
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "CircleBreakException{" +
				"msg='" + msg + '\'' +
				'}';
	}
}
