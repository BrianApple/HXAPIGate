package hx.apigate.dubbo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import hx.apigate.aware.ReloadXmlAware;
/**
 * 
 * @Description: 
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotp.com</p>
 * @author  yangcheng
 * @date:   2018年12月4日
 */
@Component
public class SpringContextHelper implements ApplicationContextAware{
	private static Logger logger = LoggerFactory.getLogger(SpringContextHelper.class);
	private static ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextHelper.applicationContext = applicationContext;
	}
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		Object bean = null;
		try {
			return (T) applicationContext.getBean(name);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("bean获取异常", e);
		}
		return null;
	}

}
