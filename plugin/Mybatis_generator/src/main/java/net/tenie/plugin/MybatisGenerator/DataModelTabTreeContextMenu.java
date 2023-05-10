package net.tenie.plugin.MybatisGenerator;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.plugin.MybatisGenerator.tools.DataModelUtility;

public class DataModelTabTreeContextMenu {

	private ContextMenu contextMenu;

	private MenuItem modelImport;
	private MenuItem open;
	private MenuItem close;
	private MenuItem rename;
	private MenuItem query;
	
	
	private MenuItem delete;
	
	private MenuItem refresh;
	
	
	public DataModelTabTreeContextMenu(DataModelTabTree mdTree) {
		
		contextMenu = new ContextMenu();  
		
		modelImport = new MenuItem("Import Model");
		modelImport.setGraphic(IconGenerator.svgImageDefActive("folder-open"));
		modelImport.setOnAction(e -> {
			DataModelUtility.modelFileImport("UTF-8");
		});
		 
		query = new MenuItem("Query Model");
		query.setGraphic(IconGenerator.svgImageDefActive("windows-magnify-browse"));
		query.setOnAction(e -> {
			var panel = mdTree.getOptionPanel();
			CommonUtility.leftHideOrShowSecondOptionBox(panel.getOptionVbox(),
					panel.getFilterHbox(), panel.getTxt());
		});
		open = new MenuItem("Open Model");
		open.setGraphic( IconGenerator.svgImage("database", "#7CFC00 "));
		open.setDisable(true);
		open.setOnAction(e -> {
			var item = DataModelTabTree.currentSelectItem();
			var itemPo = item.getValue();
			if(itemPo.getIsModel()) {
				DataModelTabTree.modelInfoTreeAddTableTreeNode(item);
			}
			
		});
		close = new MenuItem("Close Model");
		close.setGraphic( IconGenerator.svgImageUnactive("database"));
		close.setDisable(true);
		close.setOnAction(e -> {
			DataModelUtility.closeModel();
			
		});
		
		
		
		rename = new MenuItem("Rename Model");
		rename.setGraphic( IconGenerator.svgImageDefActive("icomoon-pencil2"));
		rename.setDisable(true);
		rename.setOnAction(e -> {
			DataModelUtility.renameModelName();
		});
		
		

		delete = new MenuItem("Delete Model");
		delete.setGraphic(IconGenerator.svgImageDefActive("trash"));
		delete.setDisable(true);
		delete.setOnAction(e -> {
			DataModelUtility.delAction();
		});
			
//		
//		refresh = new MenuItem("Refresh Connection");
//		
//		refresh.setGraphic(IconGenerator.svgImageDefActive("refresh"));
//		refresh.setDisable(true);
		
		
		
		contextMenu.getItems().addAll(modelImport,query,new SeparatorMenuItem(),  open, close, rename, delete);
		contextMenu.setOnShowing(e->{
			var item = DataModelTabTree.currentSelectItem();
			boolean ismodel = item.getValue().getIsModel();
			if(ismodel) {
				delete.setDisable(false);
//				refresh.setDisable(false);
				if( item.getChildren().size() == 0) {
					open.setDisable(false);
					close.setDisable(true);
					rename.setDisable(false);
				}else {
					open.setDisable(true);
					close.setDisable(false);
					rename.setDisable(true);
				}
			}else {
				delete.setDisable(true);
//				refresh.setDisable(true);
				open.setDisable(true);
				close.setDisable(true);
				rename.setDisable(true);
			}
		
			
		});
	}


	public ContextMenu getContextMenu() {
		return contextMenu;
	}


	 

}
