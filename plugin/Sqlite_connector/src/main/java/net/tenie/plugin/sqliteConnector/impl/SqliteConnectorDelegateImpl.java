package net.tenie.plugin.sqliteConnector.impl;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class SqliteConnectorDelegateImpl implements SqluckyPluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.sqliteConnector";
	@Override
	public void register() {
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
	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

}
