open module SQLucky.sdk {
	requires java.desktop;
	requires java.naming;
	requires java.security.jgss;
	requires java.transaction.xa;
	requires java.compiler;
	requires java.logging;
	requires java.management;
	requires java.rmi;
	requires java.scripting;
	requires java.xml;
    requires java.sql;
    requires java.sql.rowset;
    
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
    requires javafx.swing;  
    // mods
	requires com.jfoenix;  
	requires org.controlsfx.controls;
	requires org.apache.logging.log4j;	
	
	
	// non-mods
	requires org.apache.commons.io;
	requires org.fxmisc.richtext;  
	requires reactfx;
	requires flowless;
	requires sql.formatter;
	requires org.slf4j;

	exports net.tenie.Sqlucky.sdk.utility;
	exports net.tenie.Sqlucky.sdk.subwindow;
	exports net.tenie.Sqlucky.sdk.config;
	exports net.tenie.Sqlucky.sdk.component;
	exports net.tenie.Sqlucky.sdk.po;
	exports net.tenie.Sqlucky.sdk;
	exports net.tenie.Sqlucky.sdk.db;
}
