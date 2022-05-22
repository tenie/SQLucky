package net.tenie.plugin.DataModel.po;

import java.util.List;

public class DataModelPo {
	private String name;
	private String describe;
	private String avatar;
	private String version;
	private String createdTime; 
	private String updatedTime;
	
	private List<DataModelTablePo> entities;
	
	public List<DataModelTablePo> getEntities() {
		return entities;
	}
	public void setEntities(List<DataModelTablePo> entities) {
		this.entities = entities;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}
	@Override
	public String toString() {
		return "DataModelPo [name=" + name + ", describe=" + describe + ", avatar=" + avatar + ", version=" + version
				+ ", createdTime=" + createdTime + ", updatedTime=" + updatedTime + "]";
	}
	
	
 
}
