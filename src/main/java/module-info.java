open module SQLucky {
	requires java.desktop;  
    requires java.sql; 
    requires java.logging;  
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
	requires org.apache.commons.io;
	requires org.apache.logging.log4j;	
//	requires com.h2database;	
	requires h2;
	requires org.fxmisc.richtext;
	requires reactfx;
	requires flowless;
	requires com.jfoenix;
	requires org.controlsfx.controls;
	requires sql.formatter;
	requires slf4j.api;
	exports net.tenie.fx.controller  to    javafx.fxml;
    exports net.tenie.fx.main to    javafx.graphics;
}
