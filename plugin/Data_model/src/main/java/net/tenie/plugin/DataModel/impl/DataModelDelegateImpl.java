package net.tenie.plugin.DataModel.impl;

import javafx.scene.control.TitledPane;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.FinderAction;
import net.tenie.plugin.DataModel.DataModelTabTree;

public class DataModelDelegateImpl implements SqluckyPluginDelegate {
	public static final String pluginName = "Data Model";
	public static final String pluginCode = "net.tenie.plugin.DataModel";
	public static final String pluginDescribe = "Data Model";
	public static final String version = "0.0.1";

	DataModelTabTree tree;
	@Override
	public void load() {
		AppComponent appComponent = ComponentGetter.appComponent;
		// 节点的树对象
		tree = new DataModelTabTree(pluginName);
		FinderAction.putSqluckyTitledPane(pluginName, tree);

		appComponent.addTitledPane(tree);
	}

	@Override
	public void showed() {
		// 显示的时候切换图标
		var icon = ComponentGetter.getIconDefActive("table");
		var uaicon = ComponentGetter.getIconUnActive("table");
		CommonUtils.setLeftPaneIcon(tree, icon, uaicon);

	}

	@Override
	public void unload() {
//		System.out.println("unload: DataModelDelegateImpl...");

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
