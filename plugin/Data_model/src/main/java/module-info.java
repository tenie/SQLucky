open module SQLucky.plugin.DataModel{
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
	requires SQLucky.sdk;
	requires org.apache.commons.io;
	requires com.jfoenix;  
	requires fastjson;
	requires java.sql;
	requires org.mybatis; 
	requires org.apache.logging.log4j;	

	requires org.controlsfx.controls;
	
	exports net.tenie.plugin.DataModel.impl;
    provides net.tenie.Sqlucky.sdk.SqluckyPluginDelegate with net.tenie.plugin.DataModel.impl.DataModelDelegateImpl;
}
