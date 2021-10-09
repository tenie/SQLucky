package net.tenie.plugin.H2Connector.impl;

import net.tenie.Sqlucky.sdk.PluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class H2ConnectorDelegateImpl implements PluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.DB2Connector";
	@Override
	public void load() {
		System.out.println("load:  h2-file-ConnectorDelegateImpl..."); 
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
		System.out.println("unload: NoteDelegateImp...");

	}

	@Override
	public String pluginName() { 
		return pluginName;
	}

}
