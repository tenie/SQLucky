package net.tenie.Sqlucky.sdk.po;

import java.util.Date;

public class PluginInfoPO  {

	private Integer reloadStatus;
	private Date createdTime;
	private Date updatedTime;
	private String pluginCode;
	private String pluginDescribe;
	private String pluginName;
	private Integer installStatus;
	private Integer id;
	private String comment;

	public void setReloadStatus(Integer reloadStatus){
		this.reloadStatus=reloadStatus;
	}

	public Integer getReloadStatus(){
		return this.reloadStatus;
	}

	public void setCreatedTime(Date createdTime){
		this.createdTime=createdTime;
	}

	public Date getCreatedTime(){
		return this.createdTime;
	}

	public void setUpdatedTime(Date updatedTime){
		this.updatedTime=updatedTime;
	}

	public Date getUpdatedTime(){
		return this.updatedTime;
	}

	public void setPluginCode(String pluginCode){
		this.pluginCode=pluginCode;
	}

	public String getPluginCode(){
		return this.pluginCode;
	}

	public void setPluginDescribe(String pluginDescribe){
		this.pluginDescribe=pluginDescribe;
	}

	public String getPluginDescribe(){
		return this.pluginDescribe;
	}

	public void setPluginName(String pluginName){
		this.pluginName=pluginName;
	}

	public String getPluginName(){
		return this.pluginName;
	}

	public void setInstallStatus(Integer installStatus){
		this.installStatus=installStatus;
	}

	public Integer getInstallStatus(){
		return this.installStatus;
	}

	public void setId(Integer id){
		this.id=id;
	}

	public Integer getId(){
		return this.id;
	}

	public void setComment(String comment){
		this.comment=comment;
	}

	public String getComment(){
		return this.comment;
	}

}