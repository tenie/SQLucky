package net.tenie.plugin.backup.component;

import java.util.Date;


public class SqluckyBackup {
	private Long id; 
	private Long userId;
	private String backupName;
	private String filePath;
	private Integer type;
	private Date createdAt;
	private Date updatedAt;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	 
	public String getBackupName() {
		return backupName;
	}
	public void setBackupName(String backupName) {
		this.backupName = backupName;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "SqluckyBackup [id=" + id + ", userId=" + userId + ", backupName=" + backupName + ", filePath="
				+ filePath + ", type=" + type + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
 
	  
	
	
	
}
