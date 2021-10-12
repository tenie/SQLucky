package net.tenie.plugin.MariadbConnector.impl;

import net.tenie.Sqlucky.sdk.PluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class MariadbConnectorDelegateImpl implements PluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.MariadbConnector";
	@Override
	public void load() {
		System.out.println("load:  MariadbConnectorDelegateImpl ..."); 
		// 注册
		MariadbRegister reg = new MariadbRegister();
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
