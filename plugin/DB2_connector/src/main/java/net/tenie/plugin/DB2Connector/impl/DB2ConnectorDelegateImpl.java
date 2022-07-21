package net.tenie.plugin.DB2Connector.impl;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class DB2ConnectorDelegateImpl implements SqluckyPluginDelegate {  
	public static final String pluginName = "DB2 Connector";
	public static final String pluginCode = "net.tenie.plugin.DB2Connector";
	public static final String pluginDescribe = "DB2 Connector";
	public static final String version ="0.0.1";
	
	@Override
	public void register() {
		System.out.println("register:  DB2ConnectorDelegateImpl..."); 
		// 注册
		Db2Register reg = new Db2Register();
		ComponentGetter.appComponent.registerDBConnector(reg);
		
		
	}
	@Override
	public void showed() {
		
	}

	@Override
	public void unload() {
		System.out.println("unload: DB2ConnectorDelegateImpl...");

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
