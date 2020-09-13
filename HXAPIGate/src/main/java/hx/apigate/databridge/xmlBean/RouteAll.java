package hx.apigate.databridge.xmlBean;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Description: 一个接口的所有版本route的对象封装</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng,hjj
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class RouteAll implements Serializable{
	private boolean isUpdated;
	
	private String url;//接口名称
	private List<Route> routes;
	private boolean isWeight;

	private AtomicInteger index ;
	private int[] nodeWeight ;
	private CopyOnWriteArrayList<Integer> current_weight;
	private CopyOnWriteArrayList<Integer> temp_weight;
	private int totalWeight = 0;
	public RouteAll() {
		super();
	}
	public RouteAll(boolean isWeight,String url) {
		super();
		this.url = url;
		this.isWeight = isWeight;
		routes = new CopyOnWriteArrayList<Route>();
	}
	public void addInfo(Route route) {
		routes.add(route);
		init();
	}
	public void init() {
		index = new AtomicInteger(-1);
		nodeWeight = new int[routes.size()];
		current_weight = new CopyOnWriteArrayList<Integer>();
		temp_weight = new CopyOnWriteArrayList<Integer>();
		int size = routes.size();
		for (int i = 0 ; i < size ; i++) {
			int curNodeWeight =  1;
			if(routes.get(i).getVersionWeight() >10) {
				curNodeWeight = 10;
			}else if(routes.get(i).getVersionWeight() < 1) {
				curNodeWeight = 1;
			}else {
				curNodeWeight = routes.get(i).getVersionWeight();
			}
			totalWeight += curNodeWeight;
			nodeWeight[i] = curNodeWeight;
			temp_weight.add(curNodeWeight);
			current_weight.add(0);
		}
	}
	private Route nextNodeByWeight() {
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
        return  routes.get(maxIndex);

	}
	private Route nextNodeByCircle() {
		int nextIndex = this.index.addAndGet(1) % routes.size();
		this.index.set(nextIndex);
        return  routes.get(nextIndex);

	}
	public Route nextRoute() {
		if(isWeight) {
			return nextNodeByWeight();
		}else {
			System.out.println("轮寻选择版本集");
			return nextNodeByCircle();
		}
		
	}
	public List<Route> getRoutes() {
		return routes;
	}
	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	public boolean isUpdated() {
		return isUpdated;
	}
	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}
	
	

}
