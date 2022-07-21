package net.tenie.plugin.sqliteConnector.impl;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class SqliteConnectorDelegateImpl implements SqluckyPluginDelegate {
	public static final String pluginName = "sqlite Connector";
	public static final String pluginCode = "net.tenie.plugin.sqliteConnector";
	public static final String pluginDescribe = "sqlite Connector";
	public static final String version ="0.0.1";
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
		System.out.println("unload: SqliteConnectorDelegateImpl...");

	}

	@Override
	public String pluginName() { 
		return pluginName;
	}
	@Override
	public void load() {
		
	}
	@Override
	public String pluginCode() {
		return pluginCode;
	}
	@Override
	public String pluginDescribe() {
		return pluginDescribe;
	}
	@Override
	public String version() {
		return version;
	}

}
