package net.tenie.plugin.DB2Connector.impl;

import net.tenie.Sqlucky.sdk.PluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class DB2ConnectorDelegateImpl implements PluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.DB2Connector";
	@Override
	public void load() {
		System.out.println("load:  DB2ConnectorDelegateImpl..."); 
		// 注册
		Db2Register reg = new Db2Register();
		ComponentGetter.appComponent.registerDBConnector(reg);
		
		
	}
	@Override
	public void showed() {
		
	}

	@Override
	public void unload() {
		System.out.println("unload: NoteDelegateImp...");

	}

	@Override
	public String pluginName() { 
		return pluginName;
	}

}