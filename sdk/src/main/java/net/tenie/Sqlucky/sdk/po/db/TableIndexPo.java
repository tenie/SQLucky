package net.tenie.Sqlucky.sdk.po.db;

/**
 * 索引, 
 * db2 通过 下面sql 查询
 * @author tenie
 * 
 * 
 */
public class TableIndexPo {
	private String indname; // INDNAME 索引名称
	private String tabname;  // TABNAME 表名
	private String indschema; // INDSCHEMA 索引schema
	private String colnames; // COLNAMES 索引的列
//  UNIQUERULE 
//	D = Permits duplicates D = 允许重复
//	U = Unique U = 唯一
//	P = Implements primary key P = 实现主键

	public String getIndname() {
		return indname;
	}
	public void setIndname(String indname) {
		this.indname = indname;
	}
	public String getTabname() {
		return tabname;
	}
	public void setTabname(String tabname) {
		this.tabname = tabname;
	}
	public String getIndschema() {
		return indschema;
	}
	public void setIndschema(String indschema) {
		this.indschema = indschema;
	}
	public String getColnames() {
		return colnames;
	}
	public void setColnames(String colnames) {
		this.colnames = colnames;
	}
	@Override
	public String toString() {
		return "TableIndex [indname=" + indname + ", tabname=" + tabname + ", indschema=" + indschema + ", colnames="
				+ colnames + "]";
	}
	

	
	
}
