package net.tenie.Sqlucky.sdk.po;

public class BDConnJsonObj{
	private String connName;
	private String driver;
	private String hostOrFile; 
	private String port; 
	private String user; 
	private String passWord;
	private String dbVendor;
	private String defaultSchema;
	private String dbName ; 
	private String jdbcurlStr; 
	private Boolean autoConnect;
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	 
	public String getHostOrFile() {
		return hostOrFile;
	}
	public void setHostOrFile(String hostOrFile) {
		this.hostOrFile = hostOrFile;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
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
	public String getDbVendor() {
		return dbVendor;
	}
	public void setDbVendor(String dbVendor) {
		this.dbVendor = dbVendor;
	}
	public String getDefaultSchema() {
		return defaultSchema;
	}
	public void setDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getJdbcurlStr() {
		return jdbcurlStr;
	}
	public void setJdbcurlStr(String jdbcurlStr) {
		this.jdbcurlStr = jdbcurlStr;
	}
	public String getConnName() {
		return connName;
	}
	public void setConnName(String connName) {
		this.connName = connName;
	}
	public Boolean getAutoConnect() {
		return autoConnect;
	}
	public void setAutoConnect(Boolean autoConnect) {
		this.autoConnect = autoConnect;
	}
	@Override
	public String toString() {
		return "BDConnJsonObj [connName=" + connName + ", driver=" + driver + ", hostOrFile=" + hostOrFile + ", port="
				+ port + ", user=" + user + ", passWord=" + passWord + ", dbVendor=" + dbVendor + ", defaultSchema="
				+ defaultSchema + ", dbName=" + dbName + ", jdbcurlStr=" + jdbcurlStr + ", autoConnect=" + autoConnect
				+ "]";
	}
 
	 
	
	
}
