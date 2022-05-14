package net.tenie.plugin.DataModel.impl;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.plugin.DataModel.DataModelTabTree; 

public class DataModelDelegateImpl implements SqluckyPluginDelegate { 
	TitledPane NotePane;
	public static final String pluginName = "net.tenie.plugin.note";
	@Override
	public void load() {
		System.out.println("load:  NoteDelegateImp..."); 
		AppComponent appComponent = ComponentGetter.appComponent;
		
		
//		// 添加图标
//		appComponent.addIconBySvg( "icomoon-pencil", 
//				"M576 1376v-192q0-14-9-23t-23-9h-320q-14 0-23 9t-9 23v192q0 14 9 23t23 9h320q14 0 23-9t9-23zm0-384v-192q0-14-9-23t-23-9h-320q-14 0-23 9t-9 23v192q0 14 9 23t23 9h320q14 0 23-9t9-23zm512 384v-192q0-14-9-23t-23-9h-320q-14 0-23 9t-9 23v192q0 14 9 23t23 9h320q14 0 23-9t9-23zm-512-768v-192q0-14-9-23t-23-9h-320q-14 0-23 9t-9 23v192q0 14 9 23t23 9h320q14 0 23-9t9-23zm512 384v-192q0-14-9-23t-23-9h-320q-14 0-23 9t-9 23v192q0 14 9 23t23 9h320q14 0 23-9t9-23zm512 384v-192q0-14-9-23t-23-9h-320q-14 0-23 9t-9 23v192q0 14 9 23t23 9h320q14 0 23-9t9-23zm-512-768v-192q0-14-9-23t-23-9h-320q-14 0-23 9t-9 23v192q0 14 9 23t23 9h320q14 0 23-9t9-23zm512 384v-192q0-14-9-23t-23-9h-320q-14 0-23 9t-9 23v192q0 14 9 23t23 9h320q14 0 23-9t9-23zm0-384v-192q0-14-9-23t-23-9h-320q-14 0-23 9t-9 23v192q0 14 9 23t23 9h320q14 0 23-9t9-23zm128-320v1088q0 66-47 113t-113 47h-1344q-66 0-113-47t-47-113v-1088q0-66 47-113t113-47h1344q66 0 113 47t47 113z");

		
		
		DataModelTabTree tree = new DataModelTabTree(); 
//		var tv  = tree.DataModelTreeView;
		var tv  = tree.vbox;
		
		
	    NotePane = new TitledPane();
		NotePane.setText("Data Model");  
		CommonUtility.addCssClass(NotePane, "titledPane-color");
		NotePane.setContent( tv);
		
		appComponent.addTitledPane(NotePane);
	}
	@Override
	public void showed() {
		final StackPane Node = (StackPane)NotePane.lookup(".arrow-button");
		Node.getChildren().clear(); 
		var icon = ComponentGetter.getIconDefActive("table");
		Node.getChildren().add(icon);
		
		var title = NotePane.lookup(".title");
		title.setOnMouseEntered( e->{ 
			Node.getChildren().clear();
			if(NotePane.isExpanded()) {
				Node.getChildren().add(ComponentGetter.iconLeft);
			}else {
				Node.getChildren().add(ComponentGetter.iconRight);
			}
			
		});
		
		title.setOnMouseExited( e->{ 
			Node.getChildren().clear();
			Node.getChildren().add(icon);
		});
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
