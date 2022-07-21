package net.tenie.plugin.DataModel.impl;

import javafx.scene.control.TitledPane;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.SqlcukyTitledPaneInfoPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.plugin.DataModel.DataModelTabTree; 

public class DataModelDelegateImpl implements SqluckyPluginDelegate {
	public static final String pluginName = "Data Model";
	public static final String pluginCode = "net.tenie.plugin.DataModel";
	public static final String pluginDescribe = "Data Model";
	public static final String version ="0.0.1";
	
	private static TitledPane NotePane;
	@Override
	public void load() {
		System.out.println("load:  DataModelDelegateImpl..."); 
		AppComponent appComponent = ComponentGetter.appComponent;
		
		
 
		
		DataModelTabTree tree = new DataModelTabTree(); 
//		var tv  = tree.vbox;
		
		
	    NotePane = new TitledPane();
		NotePane.setText("Data Model");
		// 操作按钮面板放入到一个对象中， 切换到这个面板的时候 按钮面板会在按钮区域展示
		SqlcukyTitledPaneInfoPo btnsObj = new SqlcukyTitledPaneInfoPo( pluginName, tree.getBtnsBox());
		NotePane.setUserData(btnsObj);
		CommonUtility.addCssClass(NotePane, "titledPane-color");
		NotePane.setContent( tree.getDataModelTreeView());
		
		appComponent.addTitledPane(NotePane);
	}
	@Override
	public void showed() {
		// 显示的时候切换图标
		var icon = ComponentGetter.getIconDefActive("table");
		var uaicon = ComponentGetter.getIconUnActive("table");
		CommonUtility.setLeftPaneIcon(NotePane, icon, uaicon);
		
	}

	@Override
	public void unload() {
		System.out.println("unload: DataModelDelegateImpl...");

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
