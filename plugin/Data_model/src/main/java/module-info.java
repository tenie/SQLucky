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
	
	//xml
	requires org.dom4j;
	requires com.fasterxml.jackson.dataformat.xml;
	requires com.fasterxml.jackson.core; 
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;
	requires lombok;
	
	exports net.tenie.plugin.DataModel.impl;
    provides net.tenie.Sqlucky.sdk.SqluckyPluginDelegate with net.tenie.plugin.DataModel.impl.DataModelDelegateImpl;
}
