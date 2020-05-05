package hx.apigate.aware;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

import hx.apigate.Entrance;
import hx.apigate.base.IProcessor;
import hx.apigate.databridge.xmlBean.Route;
import hx.apigate.databridge.xmlBean.RouteAll;
import hx.apigate.databridge.xmlBean.RouteNode;
import hx.apigate.distributedCache.config.DistributedCachingProperties;
import hx.apigate.distributedCache.config.DistributedCachingProperties.RedisNodes;
import hx.apigate.distributedCache.config.PropertiesFactory;
import hx.apigate.util.LocalCache;

/**
 * <p>Description: 加载xml配置文件</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.xianglong.work</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class ReloadXmlAware implements IProcessor{
	Logger logger = LoggerFactory.getLogger(ReloadXmlAware.class);

	@Override
	public void start() throws Exception {
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
		logger.info("xml is loaded............................................");
		XStream xStream = new XStream(new DomDriver("utf-8",new XmlFriendlyReplacer("_-", "_")));
		xStream.alias("RouteAll", RouteAll.class);
		xStream.alias("Route", Route.class);
		xStream.alias("RouteNode", RouteNode.class);
		xStream.alias("DistributedCachingProperties", DistributedCachingProperties.class);
		xStream.alias("RedisNodes", RedisNodes.class);
		PropertiesFactory.distributedCachingProperties = (DistributedCachingProperties) xStream.fromXML(DistributedCachingProperties.class.getClassLoader().getResourceAsStream("DistributeCacheInfo.xml"));
		RouteAll routeAll = (RouteAll) xStream.fromXML(Entrance.class.getClassLoader().getResourceAsStream("RouteInfo.xml"));
		List<Route> routeAllList = routeAll.getRouteAll();
		for (Route route : routeAllList) {
			route.init();
			LocalCache.routeCache.put(route.getMatchUrl(), route);
		}
	}

	@Override
	public void stop() throws Exception {
		LocalCache.routeCache.clear();
	}

	@Override
	public int getStartOrder() {
		return 0;
	}

	@Override
	public int getStopOrder() {
		return 0;
	}
	

}
