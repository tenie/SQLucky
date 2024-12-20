package net.tenie.plugin.DataModel.po;

import java.util.Date;

/**
 * @author tenie
 */
public class DataModelTableFieldsPo {
    // 自增长 id
    private Long itemId;
    // table的自增长id
    private Long tableId;
    // 模型的自增长id
    private Long modelId;
    // 模型文件里的id
    private String id;
    //--- mysql: ORDINAL_POSITION
    private Integer rowNo;
    // FIELD , 表的字段    mysql: COLUMN_NAME
    private String defKey;
    // NAME , 字段的名称,  mysql 可以认为是 COLUMN_COMMENT
    private String defName;
    // COMMENT 			mysql: COLUMN_COMMENT
    private String comment;
    private String domain;
    private String type;
    private Integer len;
    private String scale;
    // 默认值	    		mysql: COLUMN_DEFAULT
    private String defaultValue;
    // TYPE_FULL_NAME , 字段类型, 如: char(10)  mysql:COLUMN_TYPE
    private String typeFullName;
    // 标记字段是不是主键字段, 值为 √, 或空   mysql: COLUMN_KEY
    private String primaryKeyName;
    private String notNullName;
    private String autoIncrementName;
    private String refDict;

    private Date createdTime;
    private Date updatedTime;

    // 标记字段是不是主键字段, 值为 √, 或空  mysql: COLUMN_KEY
    private String primaryKey;
    // 标记不允许为null	  mysql: IS_NULLABLE , 不能为空NO 否则YES
    private String notNull;
    // 是不是 自动增长    		 mysql: EXTRA , auto_increment
    private String autoIncrement;
    private String hideInGraph;

    public Integer getRowNo() {
        return rowNo;
    }

    public void setRowNo(Integer rowNo) {
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

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
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


    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    @Override
    public String toString() {
        return "DataModelTableFieldsPo [itemId=" + itemId + ", tableId=" + tableId + ", modelId=" + modelId + ", id="
                + id + ", rowNo=" + rowNo + ", defKey=" + defKey + ", defName=" + defName + ", comment=" + comment
                + ", domain=" + domain + ", type=" + type + ", len=" + len + ", scale=" + scale + ", defaultValue="
                + defaultValue + ", typeFullName=" + typeFullName + ", primaryKeyName=" + primaryKeyName
                + ", notNullName=" + notNullName + ", autoIncrementName=" + autoIncrementName + ", refDict=" + refDict
                + ", createdTime=" + createdTime + ", updatedTime=" + updatedTime + ", primaryKey=" + primaryKey
                + ", notNull=" + notNull + ", autoIncrement=" + autoIncrement + ", hideInGraph=" + hideInGraph + "]";
    }


}
