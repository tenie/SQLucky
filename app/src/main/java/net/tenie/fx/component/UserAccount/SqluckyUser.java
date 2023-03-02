package net.tenie.fx.component.UserAccount;

import java.util.Date;

public class SqluckyUser {
	private Long id; 
	private String userName;
	private String email;
	private String password;
	private Integer isVip;
	private Date vipExpirationTime;
	
	private Date createdAt;
	private Date updatedAt;
	
	 
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getIsVip() {
		return isVip;
	}
	public void setIsVip(Integer isVip) {
		this.isVip = isVip;
	}
	public Date getVipExpirationTime() {
		return vipExpirationTime;
	}
	public void setVipExpirationTime(Date vipExpirationTime) {
		this.vipExpirationTime = vipExpirationTime;
	}
	
	
	
	
}
