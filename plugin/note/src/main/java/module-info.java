open module SQLucky.plugin.note{
	requires javafx.base;
	requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;  
    requires javafx.swing;  
	requires SQLucky.plugin.sdk;
	         
	
	exports net.tenie.plugin.note.impl;
    provides net.tenie.plugin.sdk.PluginDelegate with net.tenie.plugin.note.impl.NoteDelegateImpl;
}
