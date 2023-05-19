package net.tenie.plugin.DataModel.po;

import java.util.List;

public class DataModelInfoPo  {
	private Long id;			// 自增id
	private String name;		// 模型名称
	private String describe;    // 介绍信息
	private String avatar;		// 作者 , 非必要
	private String version;     // 版本, 非必要
	private String createdtime; 
	private String updatedtime;
	
	private Long orderTag;
	
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
 
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrderTag() {
		return orderTag;
	}
	public void setOrderTag(Long orderTag) {
		this.orderTag = orderTag;
	}
	public String getCreatedtime() {
		return createdtime;
	}
	public void setCreatedtime(String createdtime) {
		this.createdtime = createdtime;
	}
	public String getUpdatedtime() {
		return updatedtime;
	}
	public void setUpdatedtime(String updatedtime) {
		this.updatedtime = updatedtime;
	}
	@Override
	public String toString() {
		return "DataModelInfoPo [id=" + id + ", name=" + name + ", describe=" + describe + ", avatar=" + avatar
				+ ", version=" + version + ", createdtime=" + createdtime + ", updatedtime=" + updatedtime
				+ ", orderTag=" + orderTag + ", entities=" + entities + "]";
	}
 
	
 
}
