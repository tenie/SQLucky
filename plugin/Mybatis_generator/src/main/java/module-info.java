open module SQLucky.plugin.MybatisGenerator{

	requires java.desktop;
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
	requires commons.lang3;
	requires commons.collections;
	requires mybatis.generator.core;
	requires jsch;
	requires org.slf4j;
	
	exports net.tenie.plugin.MybatisGenerator.impl;
    provides net.tenie.Sqlucky.sdk.SqluckyPluginDelegate with net.tenie.plugin.MybatisGenerator.impl.DataModelDelegateImpl;
}
