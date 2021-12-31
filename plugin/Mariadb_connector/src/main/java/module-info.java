open module SQLucky.plugin.connector.mariadb{
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
    requires java.sql;
	requires SQLucky.sdk;
	requires org.apache.commons.io;
	requires com.jfoenix;  
	requires org.apache.logging.log4j;	
	
	exports net.tenie.plugin.MariadbConnector.impl;
    provides net.tenie.Sqlucky.sdk.SqluckyPluginDelegate with net.tenie.plugin.MariadbConnector.impl.MariadbConnectorDelegateImpl;
}
