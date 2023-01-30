package net.tenie.Sqlucky.sdk.utility;

/**
 * 代码运行计时
 * @author tenie
 *
 */
public class CodeRunTimeCalculate {
	
	private Long startTime;
	private Long endTime;
	private Long val = null ;
	
	public CodeRunTimeCalculate() {
		startTime = System.currentTimeMillis();
	}
	
	
	
	
	public long getRunTime() {
		endTime = System.currentTimeMillis();
		val = endTime - startTime; 
		return val;
	}
	
	public String getMs() {
		if(this.val == null ) {
			getRunTime();
		}
		String strV = "运行时间为:" + this.val + "ms";
		return strV;
	}
	
	public String getSecond() {
		if(this.val == null ) {
			getRunTime();
		}
		String strV = "运行时间为:" +( this.val * 1000) + "秒";
		return strV;
	} 
}
