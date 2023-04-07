package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;
 

public interface SqluckyConnector {
	
	public void setDbInfoTreeNode(TreeItem item);
	public TreeItem getDbInfoTreeNode();
	
	public void setDBConnectorInfoPo(DBConnectorInfoPo po);
	public DBConnectorInfoPo getDBConnectorInfoPo();
	
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
	// 当更新或保存新的连接数据的上海需要id
	public Integer getId();
	public void setId(Integer id);
	
	// 获取sehema 列表
	public Set<String> settingSchema();
	
	public String getConnName();
	
	public void setInitConnectionNodeStatus(Boolean tf);
	public boolean isAlive();
	
	//目前没用
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
	public boolean getAutoConnect();
	
	public String getJdbcUrl();
	public boolean isJdbcUrlUse();
	
	// 自定义需要展示的时间格式, 转换成字符串
	public StringProperty DateToStringStringProperty(Object dateVal, int type);
	
//	public  Map<String, DbSchemaPo> fetchSchemasInfo();
//	String getJdbcUrl();
}
