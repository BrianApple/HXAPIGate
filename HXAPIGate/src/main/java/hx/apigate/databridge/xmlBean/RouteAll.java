package hx.apigate.databridge.xmlBean;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Description: 所有route的对象封装</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class RouteAll implements Serializable{
	private List<Route> routes;

	public List<Route> getRouteAll() {
		return routes;
	}

	public void setRouteAll(List<Route> routes) {
		this.routes = routes;
	}

}
