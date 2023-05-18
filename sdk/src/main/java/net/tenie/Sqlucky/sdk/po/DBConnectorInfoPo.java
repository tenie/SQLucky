package net.tenie.Sqlucky.sdk.po;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import net.tenie.Sqlucky.sdk.db.ExportDBObjects;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class DBConnectorInfoPo {
	private Integer id;
	private String connName; // 连接名称
	private String hostOrFile;
	private String port;
	private String driver;
	private String dbVendor;
	private String defaultSchema;
	private String displaySchema;  // 
	private String dbName;
	private String user;
	private String passWord;
	private String jdbcUrl;
	private String otherParameter; // 可以为空,
	private String comment; // 可以为空,
	private Date createdAt;
	private Date updatedAt;
	private Integer recordVersion;
	private Connection conn;
	private ExportDBObjects exportDDL; 
	private boolean JdbcUrlIsFile = false;
	private boolean jdbcUrlUse = false;
	private boolean autoConnect = false;
	private Map<String, DbSchemaPo> schemas; 
	
	public DBConnectorInfoPo(BDConnJsonObj obj) {
		init(
				obj.getConnName(), obj.getDriver(), obj.getHostOrFile(), 
				obj.getPort(),  obj.getUser(), 
				obj.getPassWord(), obj.getDbVendor(), 
				obj.getDefaultSchema(), 	obj.getDbName() , 
				obj.getJdbcurlStr(),  obj.getAutoConnect()
				);
		this.comment = obj.getComment();
	}
	
	public DBConnectorInfoPo(
			String connName,
			String driver,
			String HostOrFile, 
			String port, 
			String user, 
			String passWord,
			String dbVendor, 
			String defaultSchema,
			String dbName , 
			String jdbcurlStr, 
			boolean autoConn
	) { 
		init(
				connName, driver,  HostOrFile,   port, 
				 user,   passWord,  dbVendor,   defaultSchema,
				 dbName ,   jdbcurlStr,   autoConn);
	}
	
	public void init(
			String connName,
			String driver,
			String host, 
			String port, 
			String user, 
			String passWord,
			String dbVendor, 
			String defaultSchema,
			String dbName , 
			String jdbcurlStr, 
			boolean autoConn){
		this.connName = connName;
		this.hostOrFile = host;
		this.port = port;
		this.dbVendor = dbVendor;
		this.defaultSchema = defaultSchema.trim();
		this.driver = driver;
		this.user = user;
		this.passWord = passWord;
		this.dbName = dbName;
		if(StrUtils.isNotNullOrEmpty(jdbcurlStr)) {
			jdbcUrlUse = true;
		}
		this.jdbcUrl = jdbcurlStr;
		this.autoConnect = autoConn;
	}
	
	public BDConnJsonObj toJsonObj() {
		BDConnJsonObj obj = new BDConnJsonObj();
		obj.setComment(this.comment);
		obj.setConnName(this.connName );
		obj.setHostOrFile(this.hostOrFile  );
		obj.setPort(this.port  );
		obj.setDbVendor(this.dbVendor );
		obj.setDefaultSchema(this.defaultSchema );
		obj.setDriver(this.driver  );
		obj.setUser(this.user  );
		obj.setPassWord(this.passWord  );
		obj.setDbName(this.dbName   );
		obj.setJdbcurlStr(this.jdbcUrl  );
		obj.setAutoConnect(this.autoConnect  );
		 
		return obj;
	}
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getConnName() {
		return connName;
	}
	public void setConnName(String connName) {
		this.connName = connName;
	}
	public String getHostOrFile() {
		return hostOrFile;
	}
	public void setHostOrFile(String host) {
		this.hostOrFile = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getDbVendor() {
		return dbVendor;
	}
	public void setDbVendor(String dbVendor) {
		this.dbVendor = dbVendor;
	}
//	public String getDefaultSchema() {
//		return defaultSchema;
//	}
//	public void setDefaultSchema(String defaultSchema) {
//		this.defaultSchema = defaultSchema;
//	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	public String getOtherParameter() {
		return otherParameter;
	}
	public void setOtherParameter(String otherParameter) {
		this.otherParameter = otherParameter;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
	public Integer getRecordVersion() {
		return recordVersion;
	}
	public void setRecordVersion(Integer recordVersion) {
		this.recordVersion = recordVersion;
	}
	public Connection getConn() {
		return conn;
	}


	public void setConn(Connection conn) {
		this.conn = conn;
	}


	public ExportDBObjects getExportDDL() {
		return exportDDL;
	}


	public void setExportDDL(ExportDBObjects exportDDL) {
		this.exportDDL = exportDDL;
	}


	public Map<String, DbSchemaPo> getSchemas() {
		return schemas;
	}


	public void setSchemas(Map<String, DbSchemaPo> schemas) {
		this.schemas = schemas;
	}


	public boolean getJdbcUrlIsFile() {
		return JdbcUrlIsFile;
	}


	public void setJdbcUrlIsFile(boolean jdbcUrlIsFile) {
		JdbcUrlIsFile = jdbcUrlIsFile;
	}




	public String getDefaultSchema() {
		return defaultSchema;
	}




	public void setDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
	}




	public String getDisplaySchema() {
		return displaySchema;
	}




	public void setDisplaySchema(String displaySchema) {
		this.displaySchema = displaySchema;
	}




	public boolean isJdbcUrlUse() {
		return jdbcUrlUse;
	}




	public void setJdbcUrlUse(boolean jdbcUrlUse) {
		this.jdbcUrlUse = jdbcUrlUse;
	}




	public boolean isAutoConnect() {
		return autoConnect;
	}




	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
	}




	@Override
	public String toString() {
		return "DBConnectorInfoPo [id=" + id + ", connName=" + connName + ", hostOrFile=" + hostOrFile + ", port="
				+ port + ", driver=" + driver + ", dbVendor=" + dbVendor + ", defaultSchema=" + defaultSchema
				+ ", displaySchema=" + displaySchema + ", dbName=" + dbName + ", user=" + user + ", passWord="
				+ passWord + ", jdbcUrl=" + jdbcUrl + ", otherParameter=" + otherParameter + ", comment=" + comment
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", recordVersion=" + recordVersion
				+ ", conn=" + conn + ", exportDDL=" + exportDDL + ", JdbcUrlIsFile=" + JdbcUrlIsFile + ", schemas="
				+ schemas + ", jdbcUrlUse=" + jdbcUrlUse + ", autoConnect=" + autoConnect + "]";
	}



	//将DBConnectorInfoPo 转为 BDConnJsonObj 在转为 json
	public String toJsonStr() {
		BDConnJsonObj obj =  toJsonObj();
		JSONObject jo =  (JSONObject) JSONObject.toJSON(obj);
		String val = jo.toJSONString();
		return val;
	}
	
	// BDConnJsonObj的字符串 转化为 DBConnectorInfoPo
	public static DBConnectorInfoPo toPo(String json) { 
		BDConnJsonObj val = JSONObject.parseObject(json, BDConnJsonObj.class);
		DBConnectorInfoPo valpo = new DBConnectorInfoPo(val);
		return valpo;
	}
}

