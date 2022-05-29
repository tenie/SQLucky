package net.tenie.plugin.DataModel.po;

import java.util.Date;
import java.util.List;

public class DataModelTablePo {
	private Long itemId;
	private Long modelId;
	private String id;
	private String defKey;
	private String defName;
	private String comment; 
	
	private Date createdTime;
	private Date updatedTime;
	
	private List<DataModelTableFieldsPo> fields;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<DataModelTableFieldsPo> getFields() {
		return fields;
	}

	public void setFields(List<DataModelTableFieldsPo> fields) {
		this.fields = fields;
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

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	@Override
	public String toString() {
		return "DataModelTablePo [itemId=" + itemId + ", modelId=" + modelId + ", id=" + id + ", defKey=" + defKey
				+ ", defName=" + defName + ", comment=" + comment + ", createdTime=" + createdTime + ", updatedTime="
				+ updatedTime + ", fields=" + fields + "]";
	}


	 
	
}
