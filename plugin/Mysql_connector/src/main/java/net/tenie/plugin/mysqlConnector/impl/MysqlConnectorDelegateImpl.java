package net.tenie.plugin.mysqlConnector.impl;

import net.tenie.Sqlucky.sdk.PluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class MysqlConnectorDelegateImpl implements PluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.MysqlConnector";
	@Override
	public void load() {
		System.out.println("load:  MysqlConnectorDelegateImpl ..."); 
		// 注册
		MysqlRegister reg = new MysqlRegister();
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
