open module SQLucky.plugin.connector.sqlite{
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
	
	exports net.tenie.plugin.sqliteConnector.impl;
    provides net.tenie.Sqlucky.sdk.PluginDelegate with net.tenie.plugin.sqliteConnector.impl.SqliteConnectorDelegateImpl;
}
