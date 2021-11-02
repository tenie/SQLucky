package net.tenie.plugin.H2Connector.impl;

import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class H2ConnectorDelegateImpl implements SqluckyPluginDelegate {  
	public static final String pluginName = "net.tenie.plugin.DB2Connector";
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
