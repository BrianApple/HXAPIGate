package hx.apigate.databridge.xmlBean;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ignite.IgniteSemaphore;

/**
　 * <p>Description: 路由转发配置</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class Route implements Serializable{
	//路由名称
	private String matchUrl;//示例：http://localhost:8081/account/login,则为account/login
	
	private String version;//版本
	private int versionWeight;
	
	/**
	 * 使用策略--circle/weight
	 */
	private String stratege;
	/**
	 * 通讯规约--http/dubbo
	 * 当规约类型未dubbo时，不再需要配置路径routeNodes，dubbo本身自动实现路由发现和负载等特性
	 */
	private String protocal;
	/**
	 * 是否需要授权
	 * true  需要 --会从head中获取authorization和userId值
	 * false 不需要
	 */
	private boolean needAuth;
	/**
	 * 配置所有路由
	 */
	private List<RouteNode> routeNodes;
	private AtomicInteger index;//轮寻策略下标值
	private IgniteSemaphore tps;//吞吐量--全路由限流
	
	//权重相关
	private int[] nodeWeight ;//初始化后路由下标与routeNodes中下标一一对应
	private CopyOnWriteArrayList<Integer> current_weight;
	private CopyOnWriteArrayList<Integer> temp_weight;
	private int totalWeight = 0;
	/**
	 * 初始化权重信息
	 */
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
			current_weight.add(0);//默认当前全部为0
		}
	}
	
	/**
	 * 权重策略 获取下一个节点方法
	 * @return
	 */
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
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getVersionWeight() {
		return versionWeight;
	}

	public void setVersionWeight(int versionWeight) {
		this.versionWeight = versionWeight;
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

	public IgniteSemaphore getTps() {
		return tps;
	}

	public void setTps(IgniteSemaphore tps) {
		this.tps = tps;
	}


	
}
