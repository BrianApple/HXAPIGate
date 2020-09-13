package hx.apigate.databridge;
/**
 * @Description: 
 * @author  hejuanjuan,yangcheng
 * @date: 2020年4月30日
 */
public class SemphareException extends RuntimeException{
	private static final long serialVersionUID = 4944745444205954258L;
	boolean isRouteExp;
	String msg;
	
	
	
	public SemphareException(boolean isRouteExp, String msg) {
		super();
		this.isRouteExp = isRouteExp;
		this.msg = msg;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isRouteExp() {
		return isRouteExp;
	}
	public void setRouteExp(boolean isRouteExp) {
		this.isRouteExp = isRouteExp;
	}
	

}
