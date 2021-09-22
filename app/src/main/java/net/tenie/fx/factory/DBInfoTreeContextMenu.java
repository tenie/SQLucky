package net.tenie.fx.factory;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import net.tenie.fx.Action.MenuAction;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.container.DBinfoTree;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.TableDataDetail;
import net.tenie.lib.tools.IconGenerator;

public class DBInfoTreeContextMenu {
	public static List<MenuItem> menuItems = new ArrayList<>();
	private ContextMenu contextMenu;
	private MenuItem tableAddNewCol ;
	private MenuItem tableShow;
	private MenuItem tableDrop;
	
	private MenuItem link;
	private MenuItem unlink;
	private MenuItem Edit;
	private MenuItem Add;
	
	private MenuItem delete;
	
	private MenuItem refresh;
	
	
	public DBInfoTreeContextMenu() {

		contextMenu = new ContextMenu();  

		link = new MenuItem("Open Connection");
		link.setOnAction(e->{
			ConnectionEditor.openDbConn();
		});
		link.setGraphic(IconGenerator.svgImageDefActive("link"));
		link.setDisable(true);
		link.setId("OpenConnection");
		menuItems.add(link);
		
	    unlink = new MenuItem("Close Connection");
		unlink.setOnAction(e->{
			ConnectionEditor.closeDbConn();
		});
		unlink.setGraphic(IconGenerator.svgImageDefActive("unlink"));
		unlink.setDisable(true);
		unlink.setId("CloseConnection");
		menuItems.add(unlink);
		
	    Edit = new MenuItem("Edit Connection");
		Edit.setOnAction(e->{
			ConnectionEditor.closeDbConn();
			ConnectionEditor.editDbConn();
		});
		Edit.setGraphic(IconGenerator.svgImageDefActive("edit"));
		Edit.setDisable(true);
		Edit.setId("EditConnection");
		menuItems.add(Edit);
		
		Add = new MenuItem("Add Connection");
		Add.setOnAction(e->{
			ConnectionEditor.ConnectionInfoSetting();
		});
		Add.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));
//		Add.setDisable(true);
		Add.setId("AddConnection");
		menuItems.add(Add);
		
		
	    delete = new MenuItem("Delete Connection");
		delete.setOnAction(e -> {
			ConnectionEditor.deleteDbConn();
		});
		delete.setGraphic(IconGenerator.svgImageDefActive("trash"));
		delete.setDisable(true);
		delete.setId("DeleteConnection");
		menuItems.add(delete);
		
		
		refresh = new MenuItem("Refresh");
		
		refresh.setGraphic(IconGenerator.svgImageDefActive("refresh"));
		refresh.setDisable(true);
		refresh.setId("DeleteConnection");
		menuItems.add(refresh);
		
		
		tableAddNewCol = new MenuItem("Table Add New Column");
	    tableAddNewCol.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));
	    tableAddNewCol.setId("tableAddNewCol");
	    tableAddNewCol.setDisable(true);
	    menuItems.add(tableAddNewCol);
	    
	    tableShow = new MenuItem("Show Table Field Type");
	    tableShow.setGraphic(IconGenerator.svgImageDefActive("search-plus"));
	    tableShow.setId("tableShow");
	    tableShow.setDisable(true);
	    menuItems.add(tableShow);
	    
	    tableDrop = new MenuItem("Drop ");
	    tableDrop.setGraphic(IconGenerator.svgImageDefActive("minus-square"));
	    tableDrop.setId("tableDrop");
	    tableDrop.setDisable(true);
	    menuItems.add(tableDrop);
		
		contextMenu.getItems().addAll(
//				add,
				link, unlink, Edit, Add, delete, new SeparatorMenuItem(), refresh, new SeparatorMenuItem(),
				tableAddNewCol, tableShow, tableDrop);
	
	}
	
	public void setLinkDisable(boolean tf) {
		link.setDisable(tf);
	}
	
	public void setConnectDisable(boolean tf) { 
		unlink.setDisable(tf);
		Edit.setDisable(tf);
		delete.setDisable(tf); 
	}
	
	// 设置选中 Table 时右键菜单的可以和禁用
	public void setTableDisable(boolean tf) {
		tableAddNewCol.setDisable(tf);
		tableDrop.setDisable(tf);
		tableShow.setDisable(tf); 
	}
	
	// 设置选中 视图/函数 时右键菜单的可以和禁用
	public void setViewFuncProcTriDisable(boolean tf) {
		tableDrop.setDisable(tf);
	}
	
	 
	
	public void setRefreshDisable(boolean tf) {
		refresh.setDisable(tf);
	}
	
	
	
	public void setTableAction(DbConnectionPo  dbc ,String schema ,String tablename ) {
		tableAddNewCol.setOnAction(e->{ 
			MenuAction.addNewColumn(dbc, schema, tablename);
		});
	 
		tableDrop.setOnAction(e->{ 
			MenuAction.dropTable(dbc, schema, tablename);
		});
	 
		tableShow.setOnAction(e->{ 
			TableDataDetail.showTableFieldType(dbc, schema, tablename); 
		});
	}
	
	// 设置选中视图时 对应的按钮action
	public void setViewAction(DbConnectionPo  dbc ,String schema ,String viewName ) {
		tableDrop.setOnAction(e->{ 
			MenuAction.dropView(dbc, schema, viewName);
		});
	  
	}
	// 设置选中函数时 对应的按钮action
	public void setFuncAction(DbConnectionPo  dbc ,String schema ,String viewName ) {
		tableDrop.setOnAction(e->{ 
			MenuAction.dropFunc(dbc, schema, viewName);
		});
	  
	}
	
	public void setProcAction(DbConnectionPo  dbc ,String schema ,String viewName ) {
		tableDrop.setOnAction(e->{ 
			MenuAction.dropProc(dbc, schema, viewName);
		});
	  
	}
	
	public void setTriggerAction(DbConnectionPo  dbc ,String schema ,String viewName ) {
		tableDrop.setOnAction(e->{ 
			MenuAction.dropTrigger(dbc, schema, viewName);
		});
	  
	}
	
	
	public void setRefreshAction(TreeItem<TreeNodePo> newValue) {
		
		refresh.setOnAction(e -> {
			DBinfoTree.DBinfoTreeView.getSelectionModel().select(newValue);
			ConnectionEditor.closeDbConn();
			ConnectionEditor.openDbConn();
		});
		
	}


	public ContextMenu getContextMenu() {
		return contextMenu;
	}


	public void setContextMenu(ContextMenu contextMenu) {
		this.contextMenu = contextMenu;
	}

	public MenuItem getTableAddNewCol() {
		return tableAddNewCol;
	}

	public void setTableAddNewCol(MenuItem tableAddNewCol) {
		this.tableAddNewCol = tableAddNewCol;
	}

	public MenuItem getTableShow() {
		return tableShow;
	}

	public void setTableShow(MenuItem tableShow) {
		this.tableShow = tableShow;
	}

	public MenuItem getTableDrop() {
		return tableDrop;
	}

	public void setTableDrop(MenuItem tableDrop) {
		this.tableDrop = tableDrop;
	}

	public MenuItem getLink() {
		return link;
	}

	public void setLink(MenuItem link) {
		this.link = link;
	}

	public MenuItem getUnlink() {
		return unlink;
	}

	public void setUnlink(MenuItem unlink) {
		this.unlink = unlink;
	}

	public MenuItem getEdit() {
		return Edit;
	}

	public void setEdit(MenuItem edit) {
		Edit = edit;
	}

	public MenuItem getDelete() {
		return delete;
	}

	public void setDelete(MenuItem delete) {
		this.delete = delete;
	}

	public MenuItem getRefresh() {
		return refresh;
	}

	public void setRefresh(MenuItem refresh) {
		this.refresh = refresh;
	}
	
	
}