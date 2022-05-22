package net.tenie.plugin.DataModel.po;

public class DataModelTableFieldsPo {

	private String id; 
	private int rowNo;
	private String defKey;
	private String defName;
	private String comment; 
	private String domain;
	private String type;
	private int len; 
	private String scale;
	private boolean primaryKey;
	private boolean notNull; 
	private boolean autoIncrement;
	private String defaultValue;
	private boolean hideInGraph; 
	private String typeFullName;
	private String primaryKeyName;
	private String notNullName; 
	private String autoIncrementName;
	private String refDict;
	private boolean isStandard;
	public int getRowNo() {
		return rowNo;
	}
	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}
	public String getDefKey() {
		return defKey;
	}
	public void setDefKey(String defKey) {
		this.defKey = defKey;
	}
	public String getDefName() {
		return defName;
	}
	public void setDefName(String defName) {
		this.defName = defName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	public boolean isNotNull() {
		return notNull;
	}
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}
	public boolean isAutoIncrement() {
		return autoIncrement;
	}
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public boolean isHideInGraph() {
		return hideInGraph;
	}
	public void setHideInGraph(boolean hideInGraph) {
		this.hideInGraph = hideInGraph;
	}
	public String getTypeFullName() {
		return typeFullName;
	}
	public void setTypeFullName(String typeFullName) {
		this.typeFullName = typeFullName;
	}
	public String getPrimaryKeyName() {
		return primaryKeyName;
	}
	public void setPrimaryKeyName(String primaryKeyName) {
		this.primaryKeyName = primaryKeyName;
	}
	public String getNotNullName() {
		return notNullName;
	}
	public void setNotNullName(String notNullName) {
		this.notNullName = notNullName;
	}
	public String getAutoIncrementName() {
		return autoIncrementName;
	}
	public void setAutoIncrementName(String autoIncrementName) {
		this.autoIncrementName = autoIncrementName;
	}
	public String getRefDict() {
		return refDict;
	}
	public void setRefDict(String refDict) {
		this.refDict = refDict;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isStandard() {
		return isStandard;
	}
	public void setStandard(boolean isStandard) {
		this.isStandard = isStandard;
	}
	@Override
	public String toString() {
		return "DataModelTableFieldsPo [rowNo=" + rowNo + ", defKey=" + defKey + ", defName=" + defName + ", comment="
				+ comment + ", domain=" + domain + ", type=" + type + ", len=" + len + ", scale=" + scale
				+ ", primaryKey=" + primaryKey + ", notNull=" + notNull + ", autoIncrement=" + autoIncrement
				+ ", defaultValue=" + defaultValue + ", hideInGraph=" + hideInGraph + ", typeFullName=" + typeFullName
				+ ", primaryKeyName=" + primaryKeyName + ", notNullName=" + notNullName + ", autoIncrementName="
				+ autoIncrementName + ", refDict=" + refDict + ", id=" + id + ", isStandard=" + isStandard + "]";
	} 
	
}
