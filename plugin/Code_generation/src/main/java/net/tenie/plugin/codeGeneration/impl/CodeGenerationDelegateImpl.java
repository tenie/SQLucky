package net.tenie.plugin.codeGeneration.impl;


import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.plugin.codeGeneration.component.CodeGenerationMenu;

public class CodeGenerationDelegateImpl implements SqluckyPluginDelegate {
	public static final String pluginName = "Code Generation";
	public static final String pluginCode = "net.tenie.plugin.CodeGeneration";
	public static final String pluginDescribe = "Code Generation";
	public static final String version ="0.0.1";
	
	@Override
	public void load() {
		System.out.println("load:  CodeGenerationDelegateImpl..."); 
		AppComponent appComponent = ComponentGetter.appComponent;
		CodeGenerationMenu cgm = new CodeGenerationMenu();
		
		appComponent.registerDBInfoMenu(cgm.getMenus(), cgm.getMenuItems());
 
	}
	@Override
	public void showed() {
		
	}

	@Override
	public void unload() {
		System.out.println("unload: CodeGenerationDelegateImpl...");

	}

	@Override
	public String pluginName() { 
		return pluginName;
	}
	@Override
	public void register() {
		
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
