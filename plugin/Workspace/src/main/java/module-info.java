open module SQLucky.plugin.workspace{
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
	requires SQLucky.sdk;
	requires org.apache.commons.io;
	requires org.slf4j;
	requires org.apache.logging.log4j;	
	requires com.jfoenix;  
	         
	
	exports net.tenie.plugin.workspace.impl;
    provides net.tenie.Sqlucky.sdk.SqluckyPluginDelegate with net.tenie.plugin.workspace.impl.WorkspaceDelegateImpl;
}
