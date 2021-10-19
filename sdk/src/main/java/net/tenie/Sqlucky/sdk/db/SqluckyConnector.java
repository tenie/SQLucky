package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.StringProperty;
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;
 

public interface SqluckyConnector {
	
	
	public String dbRootNodeName();
	public String translateErrMsg(String errString);
	
	public Map<String, DbSchemaPo> getSchemas();
	public ExportDDL getExportDDL();
//	public void setExportDDL(ExportDDL exportDDL);
	
	
	public Connection getConn();
	
	public SqluckyConnector copyObj( String schema);
	
	public String getDbVendor();
	
//	public Boolean isConnIng();
	
	public void closeConn();
	public Integer getId();
	public void setId(Integer id);
	// 获取sehema 列表
	public Set<String> settingSchema();
	
	public String getConnName();
	
	public void setInitConnectionNodeStatus(Boolean tf);
	public boolean isAlive();
	
	public void setComment(String comment);
	public String getComment();
	
	public String getDriver() ;
	public String getDbName();
	public String getUser();
	public String getPassWord();
	public String getHostOrFile();
	public String getPort();
	public String getDefaultSchema();
	public String getRealDefaultSchema();
	
	public String getJdbcUrl();
	public boolean isJdbcUrlUse();
	
	// 自定义需要展示的时间格式, 转换成字符串
	public StringProperty DateToStringStringProperty(Date val);
	
//	public  Map<String, DbSchemaPo> fetchSchemasInfo();
//	String getJdbcUrl();
}
