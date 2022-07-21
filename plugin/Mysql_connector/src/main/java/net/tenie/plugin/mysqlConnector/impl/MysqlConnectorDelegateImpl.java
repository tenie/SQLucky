package net.tenie.plugin.mysqlConnector.impl;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class MysqlConnectorDelegateImpl implements SqluckyPluginDelegate {
	public static final String pluginName = "Mysql Connector";
	public static final String pluginCode = "net.tenie.plugin.MysqlConnector";
	public static final String pluginDescribe = "Mysql Connector";
	public static final String version ="0.0.1";
	@Override
	public void register() {
		System.out.println("register:  MysqlConnectorDelegateImpl ..."); 
		// 注册
		MysqlRegister reg = new MysqlRegister();
		ComponentGetter.appComponent.registerDBConnector(reg);
		
		
	}
	@Override
	public void showed() {
		
	}

	@Override
	public void unload() {
		System.out.println("unload: MysqlConnectorDelegateImpl...");

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
