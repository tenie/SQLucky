package net.tenie.plugin.DB2Connector.impl;

import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;

public class Db2Register implements SqluckyDbRegister{
	private String driver = "com.ibm.db2.jcc.DB2Driver";
	private String dbVendor = "DB2";
	private boolean JdbcUrlIsFile = false;
	private String instanceName = "";  // 对h2 ,sqlite 没有schemas 就使用这个给来表示schemas的名称
	
	@Override
	public String getDriver() { 
		return driver;
	}
 
	public SqluckyConnector createConnector(DBConnectorInfoPo connPo) {
		return new Db2Connector(connPo, this);
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
		return false;
	}
	
	@Override
	public String getInstanceName() {
		return instanceName;
	}

	@Override
	public boolean hasUser() {
		return true;
	}
	
}
