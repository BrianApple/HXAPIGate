package test;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

import hx.apigate.distributedCache.config.DistributedCachingProperties;
import hx.apigate.distributedCache.config.DistributedCachingProperties.RedisNodes;

/**
 * <p>Description: </p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotcp.com</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class XMLTest {
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, Exception {
		
		
		
		XStream xStream = new XStream(new DomDriver("utf-8",new XmlFriendlyReplacer("_-", "_")));
		xStream.alias("DistributedCachingProperties", DistributedCachingProperties.class);
		xStream.alias("RedisNodes", RedisNodes.class);
		
		DistributedCachingProperties route = (DistributedCachingProperties) xStream.fromXML(DistributedCachingProperties.class.getClassLoader().getResourceAsStream("DistributeCacheInfo.xml"));
		route.toString();
	}
}
