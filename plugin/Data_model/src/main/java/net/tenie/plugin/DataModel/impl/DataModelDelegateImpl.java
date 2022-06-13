package net.tenie.plugin.DataModel.impl;

import javafx.scene.control.TitledPane;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.SqlcukyTitledPaneInfoPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.plugin.DataModel.DataModelTabTree; 

public class DataModelDelegateImpl implements SqluckyPluginDelegate { 
	TitledPane NotePane;
	public static final String pluginName = "net.tenie.plugin.note";
	@Override
	public void load() {
		System.out.println("load:  NoteDelegateImp..."); 
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
		System.out.println("unload: NoteDelegateImp...");

	}

	@Override
	public String pluginName() { 
		return pluginName;
	}
	@Override
	public void register() {
		// TODO Auto-generated method stub
		
	}

}
