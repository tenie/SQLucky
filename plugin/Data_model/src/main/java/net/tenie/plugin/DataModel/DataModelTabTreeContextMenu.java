package net.tenie.plugin.DataModel;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.plugin.DataModel.po.DataModelTreeNodePo;
import net.tenie.plugin.DataModel.tools.DataModelUtility;

public class DataModelTabTreeContextMenu {

	private ContextMenu contextMenu;

	private MenuItem modelImport;
	private MenuItem Generate;
	private MenuItem open;
	private MenuItem close;
	private MenuItem rename;
	private MenuItem query;
	
	
	private MenuItem delete;
	private MenuItem copyName;
	private MenuItem refresh;
	
	
	public DataModelTabTreeContextMenu(DataModelTabTree mdTree) {
		
		contextMenu = new ContextMenu();  
		
		Menu mdImportMenu = new Menu("Import Model");
		mdImportMenu.setGraphic(IconGenerator.svgImageDefActive("my-import"));
		// 导出模型文件
		modelImport = new MenuItem("Import File Model");
		modelImport.setGraphic(IconGenerator.svgImageDefActive("folder-open"));
		modelImport.setOnAction(e -> {
			DataModelImportWindow.createModelImportWindow();
		});
		
		// 通过mysql create table ddl 生成
		Generate = new MenuItem("Generate Model ");
		Generate.setGraphic(IconGenerator.svgImage("database", "#7CFC00"));
		Generate.setOnAction(e -> {
			DataModelGenerateWindow.showWindow();
		});
		
		mdImportMenu.getItems().add(modelImport);

		mdImportMenu.getItems().add(Generate);
		 
		query = new MenuItem("Query Model");
		query.setGraphic(IconGenerator.svgImageDefActive("windows-magnify-browse"));
		query.setOnAction(e -> {
			var panel = mdTree.getOptionPanel();
			CommonUtils.leftHideOrShowSecondOperateBox(panel.getOptionVbox(),
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
			
		
		copyName = new MenuItem("Copy Name");
		copyName.setGraphic(IconGenerator.svgImageDefActive("files-o"));
		copyName.setOnAction(e -> {
			TreeItem<DataModelTreeNodePo> item = DataModelTabTree.currentSelectItem();
			if(item !=null && item.getValue() !=null) {
				String name = item.getValue().getName();
				CommonUtils.setClipboardVal(name);
			}
			
		});
//		
//		refresh = new MenuItem("Refresh Connection");
//		
//		refresh.setGraphic(IconGenerator.svgImageDefActive("refresh"));
//		refresh.setDisable(true);
		
		
		
		contextMenu.getItems().addAll(
//				modelImport, Generate, 
				mdImportMenu,
				query, new SeparatorMenuItem(),  open, close, rename, delete,
				new SeparatorMenuItem(),copyName);
		contextMenu.setOnShowing(e->{
			var item = DataModelTabTree.currentSelectItem();
			Boolean ismodel = null ;
			if(item != null) {
				if( item.getValue() != null ) {
					ismodel = item.getValue().getIsModel();
				}
			}
//			boolean ismodel = item.getValue().getIsModel();
			if(ismodel !=null && ismodel) {
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
