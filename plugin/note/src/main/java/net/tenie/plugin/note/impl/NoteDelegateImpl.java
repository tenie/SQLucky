package net.tenie.plugin.note.impl;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.PluginDelegate;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.plugin.note.component.NoteTabTree; 

public class NoteDelegateImpl implements PluginDelegate { 
	TitledPane NotePane;
	
	@Override
	public void load() {
		System.out.println("load:  NoteDelegateImp..."); 
		AppComponent appComponent = ComponentGetter.appComponent;
		
		
		// 添加图标
		appComponent.addIconBySvg( "icomoon-pencil", 
				  "M13.5 0a2.5 2.5 0 0 1 2 4l-1 1L11 1.5l1-1c.418-.314.937-.5 1.5-.5zM1 11.5L0 16l4.5-1 9.25-9.25-3.5-3.5L1 11.5zm10.181-5.819l-7 7-.862-.862 7-7 .862.862z"); 
	
		
		
		NoteTabTree tree = new NoteTabTree(); 
		var tv  = tree.NoteTabTreeView;
		
	    NotePane = new TitledPane();
		NotePane.setText("Note");  
		CommonUtility.addCssClass(NotePane, "titledPane-color");
		NotePane.setContent( tv);
		
		appComponent.addTitledPane(NotePane);
	}
	@Override
	public void showed() {
		final StackPane Node = (StackPane)NotePane.lookup(".arrow-button");
		Node.getChildren().clear(); 
		var icon = ComponentGetter.getIconDefActive("icomoon-pencil");
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
		return "note";
	}

}
