package net.tenie.fx.factory;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.MenuAction;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.TableRowDataDetail;
import net.tenie.lib.po.DbConnectionPo;

public class TreeMenu {
	private ContextMenu contextMenu;
	private MenuItem tableAddNewCol ;
	private MenuItem tableShow;
	private MenuItem tableDrop;
	
	private MenuItem link;
	private MenuItem unlink;
	private MenuItem Edit;
	private MenuItem delete;
	
	
	public TreeMenu() {

		contextMenu = new ContextMenu();  

		link = new MenuItem("Open Connection");
		link.setOnAction(e->{
			ConnectionEditor.openDbConn();
		});
		link.setGraphic(ImageViewGenerator.svgImageDefActive("link"));
		link.setDisable(true);
		link.setId("OpenConnection");
		
	    unlink = new MenuItem("Close Connection");
		unlink.setOnAction(e->{
			ConnectionEditor.closeDbConn();
		});
		unlink.setGraphic(ImageViewGenerator.svgImageDefActive("unlink"));
		unlink.setDisable(true);
		unlink.setId("CloseConnection");
		
	    Edit = new MenuItem("Edit Connection");
		Edit.setOnAction(e->{
			ConnectionEditor.closeDbConn();
			ConnectionEditor.editDbConn();
		});
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
		
		
		tableAddNewCol = new MenuItem("Table Add New Column");
	    tableAddNewCol.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
	    tableAddNewCol.setId("tableAddNewCol");
	    tableAddNewCol.setDisable(true);
	    
	    tableShow = new MenuItem("Show Table Field Type");
	    tableShow.setGraphic(ImageViewGenerator.svgImageDefActive("search-plus"));
	    tableShow.setId("tableShow");
	    tableShow.setDisable(true);
	    
	    tableDrop = new MenuItem("Drop table");
	    tableDrop.setGraphic(ImageViewGenerator.svgImageDefActive("minus-square"));
	    tableDrop.setId("tableDrop");
	    tableDrop.setDisable(true);
		
		contextMenu.getItems().addAll(
//				add,
				link, unlink, Edit, delete, new SeparatorMenuItem(),
				tableAddNewCol, tableShow, tableDrop);
	
	}
	
	public void setConnectDisable(boolean tf) {
		link.setDisable(tf);
		unlink.setDisable(tf);
		Edit.setDisable(tf);
		delete.setDisable(tf);
		
	}
	
	public void setTableDisable(boolean tf) {
		tableAddNewCol.setDisable(tf);
		tableDrop.setDisable(tf);
		tableShow.setDisable(tf);
		 
	}
	
	public void setTableAction(DbConnectionPo  dbc ,String schema ,String tablename ) {
		tableAddNewCol.setOnAction(e->{ 
			MenuAction.addNewColumn(dbc, schema, tablename);
		});
	 
		tableDrop.setOnAction(e->{ 
			MenuAction.dropTable(dbc, schema, tablename);
		});
	 
		tableShow.setOnAction(e->{ 
			TableRowDataDetail.showTableFieldType(dbc, schema, tablename); 
		});
	}
	


	public ContextMenu getContextMenu() {
		return contextMenu;
	}


	public void setContextMenu(ContextMenu contextMenu) {
		this.contextMenu = contextMenu;
	}
	
	
}
