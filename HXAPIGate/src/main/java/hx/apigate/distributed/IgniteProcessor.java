package hx.apigate.distributed;

import hx.apigate.Entrance;
import hx.apigate.base.IProcessor;
import hx.apigate.util.IgniteUtil;
/**
 * 
 * @Description: 
 * @author  hejuanjuan,yangcheng
 * @date: 2020年2月16日
 */
public class IgniteProcessor implements IProcessor {
	@Override
	public void start() throws Exception {
		if(Entrance.isCluster) {
			IgniteAutoConfig igniteAutoConfig = new IgniteAutoConfig();
			IgniteUtil.ignite =igniteAutoConfig.ignite();
		}
	}

	@Override
	public void stop() throws Exception {
		IgniteUtil.ignite.close();
	}

	@Override
	public int getStartOrder() {
		return 4;
	}

	@Override
	public int getStopOrder() {
		return 4;
	}

}
