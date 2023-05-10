package net.tenie.Sqlucky.sdk.po.db;

import java.util.List;

/*   @author tenie */
public class TableFieldPo {
	private String tableName;
	private String fieldName; // 列名称
	private String type;
	private String isNullable; // N 对应not null, Y 可以null
	private String remarks;
	private Integer length; // 长度
	private Integer scale; // 小数位, 如 DECIMAL(11,7) length = 11, scale = 7
	private String defaultVal;
	private String key; // 表示主键, db2中1 表示联合主键中的第一个

	private String primaryKey;
	private List<String> foreignkeys;

	// 修改字段用
	private boolean isShort;
	// 源表没有这个表
	private boolean myTab;

	private String tableCat;
	private String tableSchem;
	private String columnName; // 列名称
	private int dataType; // int => 来自 java.sql.Types 的 SQL 类型
	private String typeName; // 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的 如: char
	private int columnSize; // int => 列的大小
	private String bufferLength;
	private int decimalDigits;
	private int numPrecRadix;
	private int nullable;
	private String columnDef;
	private int sqlDataType;
	private int sqlDatetimeSub;
	private int charOctetLength;
	private int ordinalPosition;
	private String scopeCatalog;
	private String scopeSchema;
	private String scopeTable;
	private int sourceDataType;
	private String isAutoincrement; // YES --- 如果该列自动增加
									// NO --- 如果该列不自动增加
									// 空字符串 --- 如果不能确定该列是否是自动增加参数
									//

	public TableFieldPo() {
		super();
		this.isShort = false;
		this.myTab = false;
	}

	public boolean isMyTab() {
		return myTab;
	}

	public void setMyTab(boolean myTab) {
		this.myTab = myTab;
	}

	public boolean isShort() {
		return isShort;
	}

	public void setShort(boolean isShort) {
		this.isShort = isShort;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIsNullable() {
		return isNullable;
	}

	public void setIsNullable(String isNullable) {
		this.isNullable = isNullable;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public List<String> getForeignkeys() {
		return foreignkeys;
	}

	public void setForeignkeys(List<String> foreignkeys) {
		this.foreignkeys = foreignkeys;
	}

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

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	public String getBufferLength() {
		return bufferLength;
	}

	public void setBufferLength(String bufferLength) {
		this.bufferLength = bufferLength;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	public int getNumPrecRadix() {
		return numPrecRadix;
	}

	public void setNumPrecRadix(int numPrecRadix) {
		this.numPrecRadix = numPrecRadix;
	}

	public int getNullable() {
		return nullable;
	}

	public void setNullable(int nullable) {
		this.nullable = nullable;
	}

	public String getColumnDef() {
		return columnDef;
	}

	public void setColumnDef(String columnDef) {
		this.columnDef = columnDef;
	}

	public int getSqlDataType() {
		return sqlDataType;
	}

	public void setSqlDataType(int sqlDataType) {
		this.sqlDataType = sqlDataType;
	}

	public int getSqlDatetimeSub() {
		return sqlDatetimeSub;
	}

	public void setSqlDatetimeSub(int sqlDatetimeSub) {
		this.sqlDatetimeSub = sqlDatetimeSub;
	}

	public int getCharOctetLength() {
		return charOctetLength;
	}

	public void setCharOctetLength(int charOctetLength) {
		this.charOctetLength = charOctetLength;
	}

	public int getOrdinalPosition() {
		return ordinalPosition;
	}

	public void setOrdinalPosition(int ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
	}

	public String getScopeCatalog() {
		return scopeCatalog;
	}

	public void setScopeCatalog(String scopeCatalog) {
		this.scopeCatalog = scopeCatalog;
	}

	public String getScopeSchema() {
		return scopeSchema;
	}

	public void setScopeSchema(String scopeSchema) {
		this.scopeSchema = scopeSchema;
	}

	public String getScopeTable() {
		return scopeTable;
	}

	public void setScopeTable(String scopeTable) {
		this.scopeTable = scopeTable;
	}

	public int getSourceDataType() {
		return sourceDataType;
	}

	public void setSourceDataType(int sourceDataType) {
		this.sourceDataType = sourceDataType;
	}

	public String getIsAutoincrement() {
		return isAutoincrement;
	}

	public void setIsAutoincrement(String isAutoincrement) {
		this.isAutoincrement = isAutoincrement;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TableFieldPo other = (TableFieldPo) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "tableFieldPO [tableName=" + tableName + ", fieldName=" + fieldName + ", type=" + type + ", isNullable="
				+ isNullable + ", remarks=" + remarks + ", length=" + length + ", scale=" + scale + ", defaultVal="
				+ defaultVal + ", key=" + key + ", primaryKey=" + primaryKey + ", foreignkeys=" + foreignkeys
				+ ", isShort=" + isShort + ", myTab=" + myTab + ", tableCat=" + tableCat + ", tableSchem=" + tableSchem
				+ ", columnName=" + columnName + ", dataType=" + dataType + ", typeName=" + typeName + ", columnSize="
				+ columnSize + ", bufferLength=" + bufferLength + ", decimalDigits=" + decimalDigits + ", numPrecRadix="
				+ numPrecRadix + ", nullable=" + nullable + ", columnDef=" + columnDef + ", sqlDataType=" + sqlDataType
				+ ", sqlDatetimeSub=" + sqlDatetimeSub + ", charOctetLength=" + charOctetLength + ", ordinalPosition="
				+ ordinalPosition + ", scopeCatalog=" + scopeCatalog + ", scopeSchema=" + scopeSchema + ", scopeTable="
				+ scopeTable + ", sourceDataType=" + sourceDataType + ", isAutoincrement=" + isAutoincrement + "]";
	}

}