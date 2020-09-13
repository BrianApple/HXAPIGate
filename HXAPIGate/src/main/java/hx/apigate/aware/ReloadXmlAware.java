package hx.apigate.aware;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import hx.apigate.base.IProcessor;
import hx.apigate.socket.Server4Terminal;
import hx.apigate.util.HXAPIGateConext;
import hx.apigate.util.LocalCache;
import hx.apigate.util.MixAll;

/**
 * <p>Description: 加载xml配置文件</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class ReloadXmlAware implements IProcessor{
	Logger logger = LoggerFactory.getLogger(Server4Terminal.class);

	@Override
	public void start() throws Exception {
		
		//Font: roman
		logger.info("===================================================================");
		logger.info("===================================================================");
		logger.info("oooo oooo      .o      .ooooo.   ooooo   oooo oooo oooo   ooo ");
		logger.info("`88' `88'     .88      8P' `Y8    `888    8'  `88' `88b   `8'     A");
		logger.info(" 88   88      .88.    88     88     Y88..8     88   8 8b   8      ");
		logger.info(" 88ooo88     .8' 8.   88     88      `888'     88   8  8b  8      P");
		logger.info(" 88   88    .88oo88.  88     88     .8Y888     88   8   8b.8       ");
		logger.info(" 88   88   .8'   `88. `8b   d8'    d8' `88b    88   8   `888      I");
		logger.info("o88o o88o o88o   o888o Y8bod8P   o888o o8888o o88o o8o    `8");
		logger.info("===================================================================");
		logger.info("----------------------------------------------powered by yangcheng-");
		logger.info("===================================================================");
		logger.info(MixAll.LOG_INFO_PRIFEX+String.format("HTTPServer is started ! Server port is %s", HXAPIGateConext.PORT));
		
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public int getStartOrder() {
		return 5;
	}

	@Override
	public int getStopOrder() {
		return 5;
	}
	

}
