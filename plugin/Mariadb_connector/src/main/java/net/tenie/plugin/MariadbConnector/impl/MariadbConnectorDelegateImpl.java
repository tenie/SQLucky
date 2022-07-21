package net.tenie.plugin.MariadbConnector.impl;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class MariadbConnectorDelegateImpl implements SqluckyPluginDelegate {  
	public static final String pluginName = "Mariadb Connector";
	public static final String pluginCode = "net.tenie.plugin.MariadbConnector";
	public static final String pluginDescribe = "Mariadb Connector";
	public static final String version ="0.0.1";
	
	@Override
	public void register() {
		System.out.println("register:  MariadbConnectorDelegateImpl ..."); 
		// 注册
		MariadbRegister reg = new MariadbRegister();
		ComponentGetter.appComponent.registerDBConnector(reg);
		
		
	}
	@Override
	public void showed() {
		
	}

	@Override
	public void unload() {
		System.out.println("unload: MariadbConnectorDelegateImpl...");

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
