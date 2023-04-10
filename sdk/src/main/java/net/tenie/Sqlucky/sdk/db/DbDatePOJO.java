package net.tenie.Sqlucky.sdk.db;

import java.util.Date;

/**
 * 数据库时间对象, 保存时间显示的字符串, 和时间的long值
 * @author tenie
 *
 */
public class DbDatePOJO {
	private String dateStr;
	private Date dateVal;
	
	public String getDateStr() {
		return dateStr;
	}
	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	public Date getDateVal() {
		return dateVal;
	}
	public void setDateVal(Date dateVal) {
		this.dateVal = dateVal;
	}
	@Override
	public String toString() {
		return "DbDatePOJO [dateStr=" + dateStr + ", dateVal=" + dateVal + "]";
	}
	
	
	
	
}
