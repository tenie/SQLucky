package net.tenie.plugin.backup.component;

import java.util.Date;


public class SqluckyBackup {
	private Long id; 
	private Long userId;
	private String backupName;
	private String filePath;
	private String typeInfo;
	private String usePrivateKey;
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

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getTypeInfo() {
		return typeInfo;
	}
	public void setTypeInfo(String typeInfo) {
		this.typeInfo = typeInfo;
	}
	public String getUsePrivateKey() {
		return usePrivateKey;
	}
	public void setUsePrivateKey(String usePrivateKey) {
		this.usePrivateKey = usePrivateKey;
	}
	@Override
	public String toString() {
		return "SqluckyBackup [id=" + id + ", userId=" + userId + ", backupName=" + backupName + ", filePath="
				+ filePath + ", typeInfo=" + typeInfo + ", usePrivateKey=" + usePrivateKey + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}
	 
	  
	
	
	
}
