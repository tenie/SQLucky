package net.tenie.Sqlucky.sdk.po.db;

/**
 * 表的主键
 * select * from SYSCAT.TABCONST where TYPE in ('P','F') and TABNAME='TABLE_A';
 * @author tenie
 * 
 *  CONSTNAME 常量名	约束名称
 *  TABNAME 选项卡名称	表名称
	TYPE 类型	约束类型（P表示主键，F表示外键）
	ENFORCED 执行	当前约束是否处于启用状态

 *
 */
public class TablePrimaryKeysPo {

	private String tableCat;
	private String tableSchem;
	private String tableName;
	private String columnName;
	private int keySeq;
	private String pkName;

	public String getTableCat() {
		return tableCat;
	}

	public void setTableCat(String tableCat) {
		this.tableCat = tableCat;
	}

	public String getTableSchem() {
		return tableSchem;
	}

	public void setTableSchem(String tableSchem) {
		this.tableSchem = tableSchem;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getKeySeq() {
		return keySeq;
	}

	public void setKeySeq(int keySeq) {
		this.keySeq = keySeq;
	}

	public String getPkName() {
		return pkName;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

	@Override
	public String toString() {
		return "TablePrimaryKeysPo [tableCat=" + tableCat + ", tableSchem=" + tableSchem + ", tableName=" + tableName
				+ ", columnName=" + columnName + ", keySeq=" + keySeq + ", pkName=" + pkName + "]";
	}

}
