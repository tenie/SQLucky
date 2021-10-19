open module SQLucky.plugin.connector.postgresql{
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
    requires javafx.swing;  
    requires java.sql;
	requires SQLucky.sdk;
	requires org.apache.commons.io;
	requires com.jfoenix;  
	requires org.apache.logging.log4j;	
	
	exports net.tenie.plugin.PostgresqlConnector.impl;
    provides net.tenie.Sqlucky.sdk.PluginDelegate with net.tenie.plugin.PostgresqlConnector.impl.PostgresqlConnectorDelegateImpl;
}
