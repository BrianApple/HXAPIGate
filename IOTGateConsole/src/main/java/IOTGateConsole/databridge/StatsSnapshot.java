package IOTGateConsole.databridge;

import java.io.Serializable;
/**
 * 
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author hejuanjuan
 * @date 2020年9月13日
 * @version 1.0
 */
public class StatsSnapshot  implements Serializable{
	private static final long serialVersionUID = 2214205657809672192L;
    private long sum;
    private double tps;
    private double avgpt;


    public long getSum() {
        return sum;
    }


    public void setSum(long sum) {
        this.sum = sum;
    }


    public double getTps() {
        return tps;
    }


    public void setTps(double tps) {
        this.tps = tps;
    }


    public double getAvgpt() {
        return avgpt;
    }


    public void setAvgpt(double avgpt) {
        this.avgpt = avgpt;
    }


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
}
