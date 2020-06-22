package hx.apigate.base;
/**
 * 
 * <p>Description: </p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public interface IProcessor extends InitProcessor,IBeforeEndProcessor{
	int getStartOrder();
	int getStopOrder();
}
