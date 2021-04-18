package net.tenie.fx.factory;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.MenuAction;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.lib.po.DbConnectionPo;

public class TreeMenu {
	private ContextMenu contextMenu;
	private MenuItem tableAddNewCol ;
	private MenuItem link;
	private MenuItem unlink;
	private MenuItem Edit;
	private MenuItem delete;
	
	
	public TreeMenu() {

		contextMenu = new ContextMenu(); 
		
	    tableAddNewCol = new MenuItem("Table Add New Column");
	    tableAddNewCol.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
	    tableAddNewCol.setId("tableAddNewCol");
	    tableAddNewCol.setDisable(true);
		
//		addtabNewCol.setOnAction(e -> {
////			ConnectionEditor.ConnectionInfoSetting();
//			addNewColumn();
//		});

		link = new MenuItem("Open Connection");
		link.setOnAction(CommonEventHandler.openConnEvent());
		link.setGraphic(ImageViewGenerator.svgImageDefActive("link"));
		link.setDisable(true);
		link.setId("OpenConnection");
		
	    unlink = new MenuItem("Close Connection");
		unlink.setOnAction(CommonEventHandler.closeConnEvent());
		unlink.setGraphic(ImageViewGenerator.svgImageDefActive("unlink"));
		unlink.setDisable(true);
		unlink.setId("CloseConnection");
		
	    Edit = new MenuItem("Edit Connection");
		Edit.setOnAction(CommonEventHandler.editConnEvent());
		Edit.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
		Edit.setDisable(true);
		Edit.setId("EditConnection");
		
	    delete = new MenuItem("Delete Connection");
		delete.setOnAction(e -> {
			ConnectionEditor.ConnectionInfoSetting();
		});
		delete.setGraphic(ImageViewGenerator.svgImageDefActive("trash"));
		delete.setDisable(true);
		delete.setId("DeleteConnection");
		
		contextMenu.getItems().addAll(
//				add,
				link, unlink, Edit, delete, new SeparatorMenuItem(), tableAddNewCol);
	
	}
	
	public void setConnectDisable(boolean tf) {
		link.setDisable(tf);
		unlink.setDisable(tf);
		Edit.setDisable(tf);
		delete.setDisable(tf);
		
	}
	
	public void setTableDisable(boolean tf) {
		tableAddNewCol.setDisable(tf);
		 
	}
	
	public void setAddNewColAction(DbConnectionPo  dbc ,String schema ,String tablename  ) { 
		tableAddNewCol.setOnAction(e->{ 
			MenuAction.addNewColumn(tablename, schema, dbc);
		});
	 
	}
	


	public ContextMenu getContextMenu() {
		return contextMenu;
	}


	public void setContextMenu(ContextMenu contextMenu) {
		this.contextMenu = contextMenu;
	}
	
	
}
