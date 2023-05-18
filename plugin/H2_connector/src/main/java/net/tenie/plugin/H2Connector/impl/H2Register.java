package net.tenie.plugin.H2Connector.impl;

import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;

public class H2Register implements SqluckyDbRegister{
	private String driver = "";
	private String dbVendor = "H2";
	private boolean JdbcUrlIsFile = false;
	private String instanceName =  "PUBLIC"; // 对h2 ,sqlite 没有schemas 就使用这个给来表示schemas的名称   
	
	
	
	@Override
	public String getDriver() { 
		return driver;
	}
 
	public SqluckyConnector createConnector(DBConnectorInfoPo connPo) {
		return new H2Connector(connPo, this);
	}

	public String getDbVendor() {
		return dbVendor;
	}

	public void setDbVendor(String dbVendor) {
		this.dbVendor = dbVendor;
	}

	public boolean getJdbcUrlIsFile() {
		return JdbcUrlIsFile;
	}

	public void setJdbcUrlIsFile(boolean jdbcUrlIsFile) {
		JdbcUrlIsFile = jdbcUrlIsFile;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	@Override
	public String getName() {
		return dbVendor;
	}

	@Override
	public boolean getMustUseJdbcUrl() {
		return true;
	}

	@Override
	public String getInstanceName() {
		return  instanceName;
	}

	@Override
	public boolean hasUser() {
		return true;
	}
	
	
	
}
