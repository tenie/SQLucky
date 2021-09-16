package net.tenie.fx.PropertyPo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.tenie.Sqlucky.sdk.po.TableFieldPo;
import net.tenie.Sqlucky.sdk.po.TablePrimaryKeysPo;

/*   @author tenie */
// table view 共用
public class TablePo2 {
	private long id;
	private String tableName;
	private String tableRemarks;
	private String tableSchema;
	private String tableType;
	private LinkedHashSet<TableFieldPo> fields;
	private ArrayList<TablePrimaryKeysPo> primaryKeys;
	private String ddl;
	private Boolean dbObj = true;
	
	public TablePo2() {}
	
	public TablePo2(String name ) {
		tableName = name;
	}
	public static TablePo2 noDbObj(String name ) {
		TablePo2 po = new TablePo2(name);
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

	@Override
	public String toString() {
		return "TablePo [id=" + id + ", tableName=" + tableName + ", tableRemarks=" + tableRemarks + ", tableSchema="
				+ tableSchema + ", fields=" + fields + "]";
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
		TablePo2 other = (TablePo2) obj;
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

}
