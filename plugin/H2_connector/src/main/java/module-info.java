open module SQLucky.plugin.h2.connector{
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
	
	exports net.tenie.plugin.H2Connector.impl;
    provides net.tenie.Sqlucky.sdk.PluginDelegate with net.tenie.plugin.H2Connector.impl.H2ConnectorDelegateImpl;
}
