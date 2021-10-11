package net.tenie.plugin.sqliteConnector.impl;

import net.tenie.Sqlucky.sdk.PluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class SqliteConnectorDelegateImpl implements PluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.sqliteConnector";
	@Override
	public void load() {
		System.out.println("load:  SqliteConnectorDelegateImpl..."); 
		// 注册
		SqliteRegister reg = new SqliteRegister();
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
