package net.tenie.plugin.H2Connector.impl;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class H2ConnectorDelegateImpl implements SqluckyPluginDelegate {  
	public static final String pluginName = "H2 Connector";
	public static final String pluginCode = "net.tenie.plugin.H2Connector";
	public static final String pluginDescribe = "H2 Connector";
	public static final String version ="0.0.1";
	
	@Override
	public void register() {
		System.out.println("register:  h2-file-ConnectorDelegateImpl..."); 
		// 注册
		H2Register reg = new H2Register();
		ComponentGetter.appComponent.registerDBConnector(reg);
		H2FileRegister regFile = new H2FileRegister();
		ComponentGetter.appComponent.registerDBConnector(regFile);
		
	}
	@Override
	public void showed() {
		
	}

	@Override
	public void unload() {
		System.out.println("unload: H2ConnectorDelegateImpl...");

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
