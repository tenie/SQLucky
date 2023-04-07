package net.tenie.fx.plugin;

import java.util.Date;


public class SqluckyPluginInfo { 
	private Long id;
	private String pluginName;
	private String pluginCode;
	private String pluginDescribe; 
	private String comment;
	private String filePath;
	private String version;
	private Date createdAt;
	private Date updatedAt;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPluginName() {
		return pluginName;
	}
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	public String getPluginCode() {
		return pluginCode;
	}
	public void setPluginCode(String pluginCode) {
		this.pluginCode = pluginCode;
	}
	public String getPluginDescribe() {
		return pluginDescribe;
	}
	public void setPluginDescribe(String pluginDescribe) {
		this.pluginDescribe = pluginDescribe;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	@Override
	public String toString() {
		return "SqluckyPluginInfo [id=" + id + ", pluginName=" + pluginName + ", pluginCode=" + pluginCode
				+ ", pluginDescribe=" + pluginDescribe + ", comment=" + comment + ", filePath=" + filePath
				+ ", version=" + version + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	 
	
	 
	
	
}
