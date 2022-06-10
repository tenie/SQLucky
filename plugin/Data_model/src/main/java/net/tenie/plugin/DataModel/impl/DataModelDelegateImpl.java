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
		
		
 
		
		DataModelTabTree tree = new DataModelTabTree(); 
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
