open module SQLucky.plugin.DataModel{
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
	requires SQLucky.sdk;
	requires org.apache.commons.io;
	requires com.jfoenix;  
	         
	
	exports net.tenie.plugin.DataModel.impl;
    provides net.tenie.Sqlucky.sdk.SqluckyPluginDelegate with net.tenie.plugin.DataModel.impl.DataModelDelegateImpl;
}
