package net.tenie.plugin.codeGeneration.impl;

import java.util.List;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.SqlcukyTitledPaneInfoPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.plugin.codeGeneration.component.CodeGenerationMenu;

public class CodeGenerationDelegateImpl implements SqluckyPluginDelegate { 
	public static final String pluginName = "net.tenie.plugin.CodeGeneration";
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
		System.out.println("unload: NoteDelegateImp...");

	}

	@Override
	public String pluginName() { 
		return pluginName;
	}
	@Override
	public void register() {
		
	}

}
