package net.tenie.plugin.PostgresqlConnector.impl;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class PostgresqlConnectorDelegateImpl implements SqluckyPluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.PostgresqlConnector";
	@Override
	public void register() {
		System.out.println("register:  PostgresqlConnectorDelegateImpl ..."); 
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
	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

}
