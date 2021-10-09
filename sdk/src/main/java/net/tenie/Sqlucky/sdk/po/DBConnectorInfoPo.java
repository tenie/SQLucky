package net.tenie.Sqlucky.sdk.po;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;

import net.tenie.Sqlucky.sdk.db.ExportDDL;

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
	private ExportDDL exportDDL; 
	private boolean JdbcUrlIsFile = false;
	private Map<String, DbSchemaPo> schemas;

	public DBConnectorInfoPo(String connName, String driver, String host, String port, String user, String passWord,
			String dbVendor, String defaultSchema,String dbName , String jdbcurlStr
	) { 
		this.connName = connName;
		this.hostOrFile = host;
		this.port = port;
//		this.defaultSchema = defaultSchema.trim();
		this.dbVendor = dbVendor;
//		if (DbVendor.postgresql.toUpperCase().equals(dbVendor.toUpperCase())) {
//			this.defaultSchema = "public";
//		} else {
//			this.defaultSchema = defaultSchema.trim();
//		}
		this.defaultSchema = defaultSchema.trim();
		this.driver = driver;
		this.user = user;
		this.passWord = passWord;
		this.dbName = dbName;
		this.jdbcUrl = jdbcurlStr;
//		this.exportDDL = exportDDL;

//		setExportDDL(dbVendor);
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


	public ExportDDL getExportDDL() {
		return exportDDL;
	}


	public void setExportDDL(ExportDDL exportDDL) {
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






	
}
