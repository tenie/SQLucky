package net.tenie.plugin.DataModel.po;

import java.util.Date;

public class DataModelTableFieldsPo {

	private Long itemId;
	private Long tableId;
	private String id; 
	private int rowNo;
	private String defKey;
	private String defName;
	private String comment; 
	private String domain;
	private String type;
	private int len; 
	private String scale;

	private String defaultValue;
	private String typeFullName;
	private String primaryKeyName;
	private String notNullName; 
	private String autoIncrementName;
	private String refDict; 

	private Date createdTime;
	private Date updatedTime;
	
	
	private String primaryKey;
	private String notNull; 
	private String autoIncrement; 
	private String hideInGraph; 
	
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
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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
	public Long getItemId() {
		return itemId;
	}
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public Date getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
	public Long getTableId() {
		return tableId;
	}
	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}
	
	
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public String getNotNull() {
		return notNull;
	}
	public void setNotNull(String notNull) {
		this.notNull = notNull;
	}
	public String getAutoIncrement() {
		return autoIncrement;
	}
	public void setAutoIncrement(String autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
	public String getHideInGraph() {
		return hideInGraph;
	}
	public void setHideInGraph(String hideInGraph) {
		this.hideInGraph = hideInGraph;
	}
	@Override
	public String toString() {
		return "DataModelTableFieldsPo [itemId=" + itemId + ", tableId=" + tableId + ", id=" + id + ", rowNo=" + rowNo
				+ ", defKey=" + defKey + ", defName=" + defName + ", comment=" + comment + ", domain=" + domain
				+ ", type=" + type + ", len=" + len + ", scale=" + scale + ", primaryKey=" + primaryKey + ", notNull="
				+ notNull + ", autoIncrement=" + autoIncrement + ", defaultValue=" + defaultValue + ", hideInGraph="
				+ hideInGraph + ", typeFullName=" + typeFullName + ", primaryKeyName=" + primaryKeyName
				+ ", notNullName=" + notNullName + ", autoIncrementName=" + autoIncrementName + ", refDict=" + refDict
				+ ", createdTime=" + createdTime + ", updatedTime=" + updatedTime + "]";
	}
	 
	
}
