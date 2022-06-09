package net.tenie.Sqlucky.sdk.po;

import java.util.HashMap;
import java.util.Map;

public class DataTableViewShapePo { 
	private String TabName;
	private Map<String, Double> column = new HashMap<>();
	
	public DataTableViewShapePo(String tn, String cn, Double wd) {
		TabName = tn;
		column.put(cn, wd);
	}
	
	public void  saveVal(String cn, Double wd) {
		column.put(cn, wd);
	}
	
	public String getTabName() {
		return TabName;
	}
	public void setTabName(String tabName) {
		TabName = tabName;
	}
	public Map<String, Double> getColumn() {
		return column;
	}
	public void setColumn(Map<String, Double> column) {
		this.column = column;
	}
	 
	
	
}
