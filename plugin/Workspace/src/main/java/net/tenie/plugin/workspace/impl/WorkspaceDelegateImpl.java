package net.tenie.plugin.workspace.impl;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.workspace.component.WorkspaceEditor;

public class WorkspaceDelegateImpl implements SqluckyPluginDelegate { 
	TitledPane NotePane;
	public static final String pluginName = "net.tenie.plugin.workspace";
	@Override
	public void load() {
		System.out.println("load:  NoteDelegateImp..."); 
		AppComponent appComponent = ComponentGetter.appComponent; 
		Menu pluginMenu = ComponentGetter.pluginMenu;

		MenuItem workspace = new MenuItem(StrUtils.MenuItemNameFormat("Workspace"));
		workspace.setGraphic(appComponent.getIconDefActive("info-circle"));
		workspace.setOnAction(value -> {
			WorkspaceEditor.createWorkspaceConfigWindow();
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

}
