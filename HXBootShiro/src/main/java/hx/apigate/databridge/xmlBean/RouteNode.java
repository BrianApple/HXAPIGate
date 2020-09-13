package hx.apigate.databridge.xmlBean;

import java.io.Serializable;

import org.apache.ignite.IgniteSemaphore;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * <p>Description: 路由节点详情</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class RouteNode implements Serializable{
	
	private String ip;
	private int port;
	private int order;//多个节点时手动给出节点循序 default=0
	private int weight;//权重 default=1
	private IgniteSemaphore tps;//吞吐量-当前路由限流
	/**
	 * rpc 接口名称,当协议类型未dubbo时  RouteNode标签中通过当前标签声名接口名称
	 */
	private String interfaceName;
	
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}

	public IgniteSemaphore getTps() {
		return tps;
	}
	public void setTps(IgniteSemaphore tps) {
		this.tps = tps;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	

}
