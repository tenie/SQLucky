package net.tenie.plugin.backup.impl;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.backup.component.WorkDataBackupController;

public class WorkDataBackupDelegateImpl implements SqluckyPluginDelegate {
	public static final String pluginName = "WorkDataBackup";
	public static final String pluginCode = "net.tenie.plugin.backup";
	public static final String pluginDescribe = "WorkDataBackup";
	public static final String version = "0.0.1";

	@Override
	public void load() {
		AppComponent appComponent = ComponentGetter.appComponent;
		Menu pluginMenu = ComponentGetter.pluginMenu;

		MenuItem workspace = new MenuItem(StrUtils.MenuItemNameFormat("Work Data Backup"));
		workspace.setGraphic(appComponent.getIconDefActive("oct-cloud-upload"));
		workspace.setOnAction(value -> {
			WorkDataBackupController.showFxml();
		});

		pluginMenu.getItems().addAll(workspace);
	}

	@Override
	public void showed() {
	}

	@Override
	public void unload() {

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
