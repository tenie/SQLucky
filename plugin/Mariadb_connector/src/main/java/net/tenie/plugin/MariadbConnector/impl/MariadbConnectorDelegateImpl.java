package net.tenie.plugin.MariadbConnector.impl;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class MariadbConnectorDelegateImpl implements SqluckyPluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.MariadbConnector";
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
