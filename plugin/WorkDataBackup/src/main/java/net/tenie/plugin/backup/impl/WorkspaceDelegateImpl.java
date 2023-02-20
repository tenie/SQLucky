package net.tenie.plugin.backup.impl;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.backup.component.WorkDataBackupEditorWindow;

public class WorkspaceDelegateImpl implements SqluckyPluginDelegate {
	public static final String pluginName = "WorkDataBackup";
	public static final String pluginCode = "net.tenie.plugin.workspace";
	public static final String pluginDescribe = "WorkDataBackup";
	public static final String version ="0.0.1";
	
	@Override
	public void load() {
		System.out.println("load:  WorkspaceDelegateImpl..."); 
		AppComponent appComponent = ComponentGetter.appComponent; 
		Menu pluginMenu = ComponentGetter.pluginMenu;

		MenuItem workspace = new MenuItem(StrUtils.MenuItemNameFormat("Work Data Backup"));
		workspace.setGraphic(appComponent.getIconDefActive("info-circle"));
		workspace.setOnAction(value -> {
			WorkDataBackupEditorWindow.createWorkspaceConfigWindow();
		});

		pluginMenu.getItems().addAll(workspace); 
	}
	@Override
	public void showed() {
	}

	@Override
	public void unload() {
		System.out.println("unload: WorkspaceDelegateImpl...");

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