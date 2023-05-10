package net.tenie.Sqlucky.sdk.po.db;

import java.util.ArrayList;
import java.util.LinkedHashSet;


/**
 *  数据库里表对象的属性字段， 如表名等
 *  table view 共用
 * @author tenie
 *
 */
public class TablePo {
	private long id;
	private String tableName;
	private String tableRemarks;
	private String tableSchema;
	private String tableType;

	// 创建语句
	private String ddl;
	// 字段
	private LinkedHashSet<TableFieldPo> fields;
	// 主键
	private ArrayList<TablePrimaryKeysPo> primaryKeys; 
	// 外键
	private ArrayList<TableForeignKeyPo> foreignKeys;
	// 索引 
	private ArrayList<TableIndexPo> indexs;
	
	private Boolean dbObj = true;
	
	public TablePo() {}
	
	public TablePo(String name ) {
		tableName = name;
	}
	 // 用于自动补全创建TablePo， 方便遍历表格实现对表名称输入自动补全， noDbObj表示创建的TablePo 是自定义的自动补全字符串
	public static TablePo noDbObj(String name ) {
		TablePo po = new TablePo(name);
		po.setDbObj(false);
		return po;
	}

	public String getDdl() {
		return ddl;
	}

	public void setDdl(String ddl) {
		this.ddl = ddl;
	}

	public ArrayList<TablePrimaryKeysPo> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(ArrayList<TablePrimaryKeysPo> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public String getTableSchema() {
		return tableSchema;
	}

	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTableRemarks() {
		return tableRemarks;
	}

	public void setTableRemarks(String tableRemarks) {
		this.tableRemarks = tableRemarks;
	}

	public LinkedHashSet<TableFieldPo> getFields() {
		return fields;
	}

	public void setFields(LinkedHashSet<TableFieldPo> fields) {
		this.fields = fields;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

 
	public ArrayList<TableForeignKeyPo> getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(ArrayList<TableForeignKeyPo> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public ArrayList<TableIndexPo> getIndexs() {
		return indexs;
	}

	public void setIndexs(ArrayList<TableIndexPo> indexs) {
		this.indexs = indexs;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public Boolean getDbObj() {
		return dbObj;
	}

	public void setDbObj(Boolean dbObj) {
		this.dbObj = dbObj;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((tableSchema == null) ? 0 : tableSchema.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TablePo other = (TablePo) obj;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		if (tableSchema == null) {
			if (other.tableSchema != null)
				return false;
		} else if (!tableSchema.equals(other.tableSchema))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TablePo [id=" + id + ", tableName=" + tableName + ", tableRemarks=" + tableRemarks + ", tableSchema="
				+ tableSchema + ", tableType=" + tableType + ", fields=" + fields + ", primaryKeys=" + primaryKeys
				+ ", foreignKeys=" + foreignKeys + ", indexs=" + indexs + ", ddl=" + ddl + ", dbObj=" + dbObj + "]";
	}

}
