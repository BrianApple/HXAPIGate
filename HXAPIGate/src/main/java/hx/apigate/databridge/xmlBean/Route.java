package hx.apigate.databridge.xmlBean;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
　 * <p>Description: 路由转发配置</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.xianglong.work</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class Route implements Serializable{
	private String matchUrl;
	private String stratege;
	private String protocal;
	private boolean needAuth;
	private List<RouteNode> routeNodes;
	private AtomicInteger index;
	private int[] nodeWeight ;
	private CopyOnWriteArrayList<Integer> current_weight;
	private CopyOnWriteArrayList<Integer> temp_weight;
	private int totalWeight = 0;
	
	public void init() {
		nodeWeight = new int[routeNodes.size()];
		current_weight = new CopyOnWriteArrayList<Integer>();
		temp_weight = new CopyOnWriteArrayList<Integer>();
		int size = routeNodes.size();
		for (int i = 0 ; i < size ; i++) {
			int curNodeWeight =  1;
			if(routeNodes.get(i).getWeight() >10) {
				curNodeWeight = 10;
			}else if(routeNodes.get(i).getWeight() < 1) {
				curNodeWeight = 1;
			}else {
				curNodeWeight = routeNodes.get(i).getWeight();
			}
			totalWeight += curNodeWeight;
			nodeWeight[i] = curNodeWeight;
			temp_weight.add(curNodeWeight);
			current_weight.add(0);
		}
	}
	public RouteNode nextNodeByWeight() {
		int maxIndex=-1;
        for(int i=0;i<temp_weight.size();i++){
            if(maxIndex==-1)
                maxIndex=i;
            else{
                if(current_weight.get(i)>current_weight.get(maxIndex))
                    maxIndex=i;
            }
        }
        temp_weight.set(maxIndex, (temp_weight.get(maxIndex)- totalWeight));
        for(int i=0;i<current_weight.size();i++){
            current_weight.set(i, temp_weight.get(i)+nodeWeight[i]);
        }
        Collections.copy(temp_weight, current_weight);
        return  routeNodes.get(maxIndex);

	}
	public List<RouteNode> getRouteNodes() {
		return routeNodes;
	}
	public void setRouteNodes(List<RouteNode> routeNodes) {
		this.routeNodes = routeNodes;
	}
	public String getStratege() {
		return stratege;
	}
	public void setStratege(String stratege) {
		this.stratege = stratege;
	}
	public String getMatchUrl() {
		return matchUrl;
	}
	public void setMatchUrl(String matchUrl) {
		this.matchUrl = matchUrl;
	}
	public boolean isNeedAuth() {
		return needAuth;
	}
	public void setNeedAuth(boolean needAuth) {
		this.needAuth = needAuth;
	}
	public AtomicInteger getIndex() {
		
		return this.index == null ? (this.index = new AtomicInteger(-1)) : this.index;
	}
	public void setIndex(AtomicInteger index) {
		this.index = index;
	}

	public String getProtocal() {
		return protocal;
	}

	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}
	
}
