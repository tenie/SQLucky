open module SQLucky.app {
	requires java.desktop;
//	requires java.naming;
//	requires java.security.jgss;
//	requires java.transaction.xa;
	requires java.compiler;
	requires java.logging;
	requires java.management;
//	requires java.rmi;
//	requires java.scripting;
	requires java.xml;
    requires java.sql;
    requires java.sql.rowset;

	requires jasypt;
	requires cn.hutool;
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
    requires javafx.media;
//    requires javafx.web;
  	requires javafx.swing;
//	requires mybatis.plus.annotation;
//	requires mybatis.plus.generator;

	// mods
	requires com.jfoenix;  
	requires org.controlsfx.controls;
	requires org.apache.logging.log4j;	
	
	requires org.dom4j;
	requires com.fasterxml.jackson.dataformat.xml;
	requires com.fasterxml.jackson.core; 
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;
	requires lombok;
	

	requires org.apache.httpcomponents.client5.httpclient5;
	requires org.apache.httpcomponents.client5.httpclient5.fluent;
	requires org.apache.httpcomponents.core5.httpcore5;
	
	requires com.github.albfernandez.juniversalchardet;
	requires org.apache.poi.poi;
	requires org.apache.poi.ooxml;
	
	// non-mods
	requires org.apache.commons.io;
	requires org.fxmisc.richtext; 
//	requires com.h2database;	
	requires h2;
	requires reactfx;
	requires flowless;
	requires sql.formatter;
	requires org.slf4j;
//	requires batik.transcoder;
//	requires batik.script;
//	requires org.girod.javafx.svgimage;
	
	requires SQLucky.sdk; 
	
	
	
	
	uses  net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
	exports net.tenie.fx.controller  to    javafx.fxml;
    exports net.tenie.fx.main        to    javafx.graphics;
}
