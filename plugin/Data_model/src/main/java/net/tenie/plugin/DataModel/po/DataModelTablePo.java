package net.tenie.plugin.DataModel.po;

import java.util.List;

public class DataModelTablePo {
	private String id;
	private String defKey;
	private String defName;
	private String comment; 
	
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

	@Override
	public String toString() {
		return "DataModelTablePo [id=" + id + ", defKey=" + defKey + ", defName=" + defName + ", comment=" + comment
				+ ", fields=" + fields + "]";
	}
	
}
