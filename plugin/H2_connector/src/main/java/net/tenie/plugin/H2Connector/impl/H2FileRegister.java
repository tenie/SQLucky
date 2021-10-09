package net.tenie.plugin.H2Connector.impl;

import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;

public class H2FileRegister implements SqluckyDbRegister{
	private String driver = "";
	private String dbVendor = "H2-file";
	private boolean JdbcUrlIsFile = false; 
	private boolean mustUseJdbcUrl = false;
	
	public boolean getMustUseJdbcUrl() {
		return mustUseJdbcUrl;
	}
  
	public void setMustUseJdbcUrl(boolean mustUseJdbcUrl) {
		this.mustUseJdbcUrl = mustUseJdbcUrl;
	}
	
	@Override
	public String getDriver() { 
		return driver;
	}
 
	public SqluckyConnector createConnector(DBConnectorInfoPo connPo) {
		return new H2FileConnector(connPo);
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
	public String getInstanceName() {
		return "PUBLIC";
	}
	
	
}
