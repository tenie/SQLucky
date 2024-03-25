package net.tenie.plugin.note.impl;

import javafx.scene.control.TitledPane;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.FinderAction;
import net.tenie.plugin.note.component.NoteTabTree;

public class NoteDelegateImpl implements SqluckyPluginDelegate {
	public static final String pluginName = "Note";
	public static final String pluginCode = "net.tenie.plugin.note";
	public static final String pluginDescribe = "Note";
	public static final String version = "0.0.1";
	NoteTabTree tree;
	@Override
	public void load() {
		AppComponent appComponent = ComponentGetter.appComponent;

		// 添加图标
		appComponent.addIconBySvg("icomoon-pencil",
				"M13.5 0a2.5 2.5 0 0 1 2 4l-1 1L11 1.5l1-1c.418-.314.937-.5 1.5-.5zM1 11.5L0 16l4.5-1 9.25-9.25-3.5-3.5L1 11.5zm10.181-5.819l-7 7-.862-.862 7-7 .862.862z");

		tree = new NoteTabTree(pluginName);
		FinderAction.putSqluckyTitledPane(pluginName, tree);
//		NotePane = new TitledPane();
//		NotePane.setUserData(new SqlcukyTitledPaneInfoPo(pluginName, tree.getOptionBox()));

//		NotePane.setText("Note");
//		CommonUtils.addCssClass(NotePane, "titledPane-color");
//		NotePane.setContent(tree.noteStackPane);

		appComponent.addTitledPane(tree);
	}

	@Override
	public void showed() {
		// 显示的时候切换图标
		var icon = ComponentGetter.getIconDefActive("icomoon-pencil");
		var uaicon = ComponentGetter.getIconUnActive("icomoon-pencil");

		CommonUtils.setLeftPaneIcon(tree, icon, uaicon);
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
