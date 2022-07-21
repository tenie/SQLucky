package net.tenie.plugin.PostgresqlConnector.impl;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class PostgresqlConnectorDelegateImpl implements SqluckyPluginDelegate {
	public static final String pluginName = "Postgresql Connector";
	public static final String pluginCode = "net.tenie.plugin.PostgresqlConnector";
	public static final String pluginDescribe = "Postgresql Connector";
	public static final String version ="0.0.1";
	 
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
		System.out.println("unload: PostgresqlConnectorDelegateImpl...");

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
