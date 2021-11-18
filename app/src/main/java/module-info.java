open module SQLucky {
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
    requires javafx.media;
    
    // mods
	requires com.jfoenix;  
	requires org.controlsfx.controls;
	requires org.apache.logging.log4j;	
	
	
	// non-mods
	requires org.apache.commons.io;
	requires org.fxmisc.richtext; 
//	requires com.h2database;	
	requires h2;
	requires reactfx;
	requires flowless;
	requires sql.formatter;
	requires org.slf4j;
	requires SQLucky.sdk;
	uses  net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
	exports net.tenie.fx.controller  to    javafx.fxml;
    exports net.tenie.fx.main        to    javafx.graphics;
}
