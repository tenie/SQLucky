package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.StringProperty;
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;
 

public interface SqluckyConnector {
	public Map<String, DbSchemaPo> getSchemas();
	public ExportDDL getExportDDL();
	public void setExportDDL(ExportDDL exportDDL);
	public Connection getConn();
	
	public SqluckyConnector copyObj(SqluckyConnector sopo, String schema);
	
	public String getDbVendor();
	
	public Boolean isConnIng();
	public void closeConn();
	public Integer getId();
	public void setId(Integer id);
	// 获取sehema 列表
	public Set<String> settingSchema();
	
	public String getConnName();
	
	public void setConning(Boolean tf);
	public boolean isAlive();
	
	public void setComment(String comment);
	public String getComment();
	
	public String getDriver() ;
	public String getDbName();
	public String getUser();
	public String getPassWord();
	public String getHost();
	public String getPort();
	public String getDefaultSchema();
	public StringProperty DateToStringStringProperty(Date val);
}
