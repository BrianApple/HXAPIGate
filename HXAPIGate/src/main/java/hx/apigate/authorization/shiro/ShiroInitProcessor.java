package hx.apigate.authorization.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.Factory;

import hx.apigate.base.IProcessor;
/**
 * <p>Description: shiro初始化</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.xianglong.work</p>
　 * @author yangcheng
　 * @date 2019年10月31日
　 * @version 1.0
 */
public class ShiroInitProcessor implements IProcessor{
	@Override
	public void start() throws Exception {
		Factory<org.apache.shiro.mgt.SecurityManager> factory=new IniSecurityManagerFactory("classpath:shiroPermission.ini");
		org.apache.shiro.mgt.SecurityManager securityManager=factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public int getStartOrder() {
		return 3;
	}

	@Override
	public int getStopOrder() {
		return 3;
	}

}
