package microservice.interfaces;

import java.util.Map;

/**
 * <p>Description: 网关代理第三方微服务接口声明，可另行新增接口类</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.xianglong.work</p>
　 * @author yangcheng
　 * @date 2019年12月5日
　 * @version 1.0
 */
public interface CommonService{
	Object getStandardCode(Map<String,Object> params);
	Object getIndexNum(Map<String, Object> params);
}
