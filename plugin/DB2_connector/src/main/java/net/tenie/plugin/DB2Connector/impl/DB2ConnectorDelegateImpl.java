package net.tenie.plugin.DB2Connector.impl;

import net.tenie.Sqlucky.sdk.PluginDelegate;

public class DB2ConnectorDelegateImpl implements PluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.note";
	@Override
	public void load() {
		System.out.println("load:  NoteDelegateImp..."); 
	 
	}
	@Override
	public void showed() { }

	@Override
	public void unload() {
		System.out.println("unload: NoteDelegateImp...");

	}

	@Override
	public String pluginName() { 
		return pluginName;
	}

}
