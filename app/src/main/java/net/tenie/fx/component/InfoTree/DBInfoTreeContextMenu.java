package net.tenie.fx.component.InfoTree;

import java.sql.SQLException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.fx.Action.MenuAction;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.dao.SelectDao;
import net.tenie.fx.window.ConnectionEditor;

public class DBInfoTreeContextMenu {
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
		
	    unlink = new MenuItem("Close Connection");
		unlink.setOnAction(e->{
			ConnectionEditor.closeDbConn();
		});
		unlink.setGraphic(IconGenerator.svgImageDefActive("unlink"));
		unlink.setDisable(true);
		unlink.setId("CloseConnection");
		
	    Edit = new MenuItem("Edit Connection");
		Edit.setOnAction(e->{
//			ConnectionEditor.closeDbConn();
			ConnectionEditor.editDbConn();
		});
		Edit.setGraphic(IconGenerator.svgImageDefActive("edit"));
		Edit.setDisable(true);
		Edit.setId("EditConnection");
		
		Add = new MenuItem("Add Connection");
		Add.setOnAction(e->{
			ConnectionEditor.ConnectionInfoSetting();
		});
		Add.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));
//		Add.setDisable(true);
		Add.setId("AddConnection");
		
		
	    delete = new MenuItem("Delete Connection");
		delete.setOnAction(e -> {
			ConnectionEditor.deleteDbConn();
		});
		delete.setGraphic(IconGenerator.svgImageDefActive("trash"));
		delete.setDisable(true);
		delete.setId("DeleteConnection");
		
		
		refresh = new MenuItem("Refresh Connection");
		
		refresh.setGraphic(IconGenerator.svgImageDefActive("refresh"));
		refresh.setDisable(true);
		refresh.setId("DeleteConnection");
		
		
		tableAddNewCol = new MenuItem("Table Add New Column");
	    tableAddNewCol.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));
	    tableAddNewCol.setId("tableAddNewCol");
	    tableAddNewCol.setDisable(true);
	    
	    tableShow = new MenuItem("Show Table Field Type");
	    tableShow.setGraphic(IconGenerator.svgImageDefActive("search-plus"));
	    tableShow.setId("tableShow");
	    tableShow.setDisable(true);
	    
	    tableDrop = new MenuItem("Drop ");
	    tableDrop.setGraphic(IconGenerator.svgImageDefActive("minus-square"));
	    tableDrop.setId("tableDrop");
	    tableDrop.setDisable(true);
		
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
	
	
	
	public void setTableAction(TreeItem<TreeNodePo> treeItem, SqluckyConnector  dbc ,String schema ,String tablename ) {
		tableAddNewCol.setOnAction(e->{ 
			MenuAction.addNewColumn(dbc, schema, tablename);
		});
	 
		tableDrop.setOnAction(e->{ 
			MenuAction.dropTable( treeItem,  dbc, schema, tablename);
		});
	 
		tableShow.setOnAction(e->{ 
			showTableFieldType(dbc, schema, tablename); 
		});
	}
	

	public static void showTableFieldType(SqluckyConnector dbc, String schema, String tablename) {
		String sql = "SELECT * FROM " + tablename + " WHERE 1=2";
		try {
			DbTableDatePo DP = SelectDao.selectSqlField(dbc.getConn(), sql);
			ObservableList<SqlFieldPo> fields = DP.getFields();

			String fieldValue = "Field Type";
			for (int i = 0; i < fields.size(); i++) {
				SqlFieldPo p = fields.get(i);
				String tyNa = p.getColumnTypeName().get() + "(" + p.getColumnDisplaySize().get();
				if (p.getScale() != null && p.getScale().get() > 0) {
					tyNa += ", " + p.getScale().get();
				}
				tyNa += ")";
				StringProperty strp = new SimpleStringProperty(tyNa);
				p.setValue(strp);
			}
			TableDataDetail.showTableDetail(tablename, "Field Name", fieldValue, fields);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	// 设置选中视图时 对应的按钮action
	public void setViewAction(TreeItem<TreeNodePo> treeItem, SqluckyConnector  dbc ,String schema ,String viewName ) {
		tableDrop.setOnAction(e->{ 
			MenuAction.dropView(treeItem, dbc, schema, viewName);
		});
	  
	}
	// 设置选中函数时 对应的按钮action
	public void setFuncAction(TreeItem<TreeNodePo> treeItem, SqluckyConnector  dbc ,String schema ,String viewName ) {
		tableDrop.setOnAction(e->{ 
			MenuAction.dropFunc(treeItem, dbc, schema, viewName);
		});
	  
	}
	
	public void setProcAction(TreeItem<TreeNodePo> treeItem, SqluckyConnector  dbc ,String schema ,String viewName ) {
		tableDrop.setOnAction(e->{ 
			MenuAction.dropProc(treeItem, dbc, schema, viewName);
		});
	  
	}
	
	public void setTriggerAction(TreeItem<TreeNodePo> treeItem, SqluckyConnector  dbc ,String schema ,String viewName ) {
		tableDrop.setOnAction(e->{ 
			MenuAction.dropTrigger(treeItem, dbc, schema, viewName);
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
