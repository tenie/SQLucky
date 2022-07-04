open module SQLucky.plugin.Code.Generation{
	
    requires java.sql;
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
	requires SQLucky.sdk;
	requires org.apache.commons.io;
	requires com.jfoenix;  
	         
	
	exports net.tenie.plugin.codeGeneration.impl;
    provides net.tenie.Sqlucky.sdk.SqluckyPluginDelegate with net.tenie.plugin.codeGeneration.impl.CodeGenerationDelegateImpl;
}
