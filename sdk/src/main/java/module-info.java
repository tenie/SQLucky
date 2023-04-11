open module SQLucky.sdk {
	requires java.base;
	requires java.desktop;
//	requires java.naming;
//	requires java.transaction.xa;
	requires java.compiler;
	requires java.logging;
	requires java.xml;
    requires java.sql;
    requires java.sql.rowset;
    
    // requires transitive 表示使用这个库的模块也必须依赖这个模块（javafx.base）
	requires transitive javafx.base; 
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
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
	requires fastjson;
	
	
	requires org.apache.httpcomponents.client5.httpclient5;
	requires org.apache.httpcomponents.client5.httpclient5.fluent;
	requires org.apache.httpcomponents.core5.httpcore5;
	requires org.mybatis;
	requires com.github.albfernandez.juniversalchardet;
//	requires okhttp3;
	
	exports net.tenie.Sqlucky.sdk;
	exports net.tenie.Sqlucky.sdk.utility;
	exports net.tenie.Sqlucky.sdk.utility.net;
	exports net.tenie.Sqlucky.sdk.subwindow;
	exports net.tenie.Sqlucky.sdk.config;
	exports net.tenie.Sqlucky.sdk.component;
	exports net.tenie.Sqlucky.sdk.po; 
	exports net.tenie.Sqlucky.sdk.po.component;
	exports net.tenie.Sqlucky.sdk.db; 
	exports net.tenie.Sqlucky.sdk.ui;
	
}
