package net.tenie.plugin.H2Connector.impl;

import net.tenie.Sqlucky.sdk.PluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class H2ConnectorDelegateImpl implements PluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.DB2Connector";
	@Override
	public void load() {
		System.out.println("load:  h2-file-ConnectorDelegateImpl..."); 
		// 注册
		H2FileRegister reg = new H2FileRegister();
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
