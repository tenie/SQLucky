package net.tenie.Sqlucky.sdk.db;

import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;

public interface SqluckyDbRegister {
	String getDriver();

	String getName();

	String getInstanceName();

	SqluckyConnector createConnector(DBConnectorInfoPo connPo);

	boolean getJdbcUrlIsFile();

	boolean getMustUseJdbcUrl();
}
