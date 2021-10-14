package net.tenie.plugin.PostgresqlConnector.impl;

import net.tenie.Sqlucky.sdk.PluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class PostgresqlConnectorDelegateImpl implements PluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.PostgresqlConnector";
	@Override
	public void load() {
		System.out.println("load:  PostgresqlConnectorDelegateImpl ..."); 
		// 注册
		PostgresqlRegister reg = new PostgresqlRegister();
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
