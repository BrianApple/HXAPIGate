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
 　 * @author yangcheng
 　 * @date 2019年10月29日
 　 * @version 1.0
 */
public class RouteAll implements Serializable{
	/**
	 * isUpdated=true则网关会自动加载到本地缓存
	 */
	private boolean isUpdated = true;

	private String url;//接口名称
	private List<Route> routes;//接口各版本路由集合
	private boolean isWeight;//是否权重

	private AtomicInteger index ;
	//权重相关
	private int[] nodeWeight ;//初始化后路由下标与routeNodes中下标一一对应
	private CopyOnWriteArrayList<Integer> current_weight;
	private CopyOnWriteArrayList<Integer> temp_weight;
	private int totalWeight = 0;
	public RouteAll() {
		super();
	}
	/**
	 *
	 * @param isWeight 是否通过权重策略负载
	 */
	public RouteAll(boolean isWeight,String url) {
		super();
		this.url = url;
		this.isWeight = isWeight;
		routes = new CopyOnWriteArrayList<Route>();
	}
	/**
	 * 想route集中添加route,自动初始化
	 * @param route
	 */
	public void addInfo(Route route) {
		routes.add(route);
		init();
	}

	/**
	 * 初始化权重信息
	 */
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
			current_weight.add(0);//默认当前全部为0
		}
	}

	/**
	 * 权重策略 获取下一个  路由集（同一版本路由为一个路由集合）
	 * @return
	 */
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
	/**
	 * 轮寻获取下一路由集
	 * @return
	 */
	private Route nextNodeByCircle() {
		int nextIndex = this.index.addAndGet(1) % routes.size();
		this.index.set(nextIndex);
		return  routes.get(nextIndex);

	}

	/**
	 * 获取下一个路由集信息
	 * @return
	 */
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
