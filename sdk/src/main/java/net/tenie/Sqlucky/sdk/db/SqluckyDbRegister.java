package net.tenie.Sqlucky.sdk.db;

import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;

public interface SqluckyDbRegister {
	String getDriver();
	String getName();
	
	public SqluckyConnector createConnector(DBConnectorInfoPo connPo);
	public boolean getJdbcUrlIsFile();
}
