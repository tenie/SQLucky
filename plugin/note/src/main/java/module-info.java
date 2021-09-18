open module SQLucky.plugin.note{
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
    requires javafx.swing;  
//    requires java.sql;
	requires SQLucky.sdk;
	requires org.apache.commons.io;
	requires com.jfoenix;  
	         
	
	exports net.tenie.plugin.note.impl;
    provides net.tenie.Sqlucky.sdk.PluginDelegate with net.tenie.plugin.note.impl.NoteDelegateImpl;
}
