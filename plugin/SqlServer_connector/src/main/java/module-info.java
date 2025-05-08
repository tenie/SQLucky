open module SQLucky.plugin.connector.sqlServer{
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
    requires java.sql;
	requires SQLucky.sdk;
	requires org.apache.commons.io;
	requires com.jfoenix;  
	requires org.apache.logging.log4j;	

//	exports net.tenie.plugin.sqlServerConnector.impl;
	exports net.tenie.plugin.mysqlConnector.impl;
//	provides net.tenie.Sqlucky.sdk.SqluckyPluginDelegate with MysqlConnectorDelegateImpl;
}
