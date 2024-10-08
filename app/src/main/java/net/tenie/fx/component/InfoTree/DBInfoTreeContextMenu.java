package net.tenie.fx.component.InfoTree;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.Action.sqlExecute.RunSqlStatePo;
import net.tenie.fx.window.ConnectionEditor;

import java.sql.SQLException;
import java.util.function.Consumer;

public class DBInfoTreeContextMenu extends ContextMenu {
	private MenuItem tableAddNewCol;
	private MenuItem tableShow;
	private MenuItem tableDrop;

	private MenuItem link;
	private MenuItem unlink;
	private MenuItem Edit;
	private MenuItem Add;

	private MenuItem delete;

	private MenuItem refresh;
	private MenuItem selectMenu;
	private MenuItem copyNameMenu;

	private TreeItemType nodeType;

	public DBInfoTreeContextMenu() {
		super();

		link = new MenuItem("Open Connection");
		link.setOnAction(e -> {
			DBinfoTree.openDbConn();
		});
		link.setGraphic(IconGenerator.svgImageDefActive("link"));
		link.setDisable(true);

		unlink = new MenuItem("Close Connection");
		unlink.setOnAction(e -> {
			ConnectionEditor.closeDbConn();
			link.setDisable(false);
		});
		unlink.setGraphic(IconGenerator.svgImageDefActive("unlink"));
		unlink.setDisable(true);

		Edit = new MenuItem("Edit Connection");
		Edit.setOnAction(e -> {
			ConnectionEditor.editDbConn();
		});
		Edit.setGraphic(IconGenerator.svgImageDefActive("edit"));
		Edit.setDisable(true);
		Edit.setId("EditConnection");

		Add = new MenuItem("Add Connection");
		Add.setOnAction(e -> {
//			ConnectionEditor.ConnectionInfoSetting();
			new ConnectionEditor();
		});
		Add.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));

		delete = new MenuItem("Delete Connection");
		delete.setOnAction(e -> {
			ConnectionEditor.deleteDbConn();
		});
		delete.setGraphic(IconGenerator.svgImageDefActive("trash"));
		delete.setDisable(true);



		// export Connection
		MenuItem exportDBConn = new MenuItem("Export Connection Info");
		exportDBConn.setOnAction(e -> {
			ConnectionEditor.exportDbConn();
		});

		// import Connection
		MenuItem importDBConn = new MenuItem("Import Connection Info");
		importDBConn.setOnAction(e -> {
			ConnectionEditor.importDbConn();
		});


		refresh = new MenuItem("Refresh Connection");

		refresh.setGraphic(IconGenerator.svgImageDefActive("refresh"));
		refresh.setDisable(true);

		tableAddNewCol = new MenuItem("Table Add New Column");
		tableAddNewCol.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));
		tableAddNewCol.setDisable(true);

		tableShow = new MenuItem("Show Table Field Type");
		tableShow.setGraphic(IconGenerator.svgImageDefActive("search-plus"));
		tableShow.setDisable(true);

		tableDrop = new MenuItem("Drop ");
		tableDrop.setGraphic(IconGenerator.svgImageDefActive("minus-square"));
		tableDrop.setDisable(true);

		// 查询
		selectMenu = new MenuItem("Select * from ");
		selectMenu.setGraphic(IconGenerator.svgImageDefActive("search"));
		selectMenu.setDisable(true);

		copyNameMenu = new MenuItem("Copy Name");
		copyNameMenu.setGraphic(IconGenerator.svgImageDefActive("clipboard"));

		this.getItems().addAll(link, unlink, Edit, Add, delete, new SeparatorMenuItem(), exportDBConn, importDBConn, new SeparatorMenuItem(), refresh,
				new SeparatorMenuItem(), tableAddNewCol, tableShow, tableDrop, new SeparatorMenuItem(), selectMenu, new SeparatorMenuItem(),
				copyNameMenu);

		// 菜单显示调用回调函数
		this.setOnShowing(v -> {
			Consumer<String> consumer = ComponentGetter.appComponent.getDBInfoMenuOnShowing();
			if (consumer != null) {
				consumer.accept("");
			}
		});

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
		selectMenu.setDisable(tf);
		if (tf) {
			selectMenu.setText("Select");
		}
		tableShow.setDisable(tf);

	}

	// 设置选中 视图/函数 时右键菜单的可以和禁用
	public void setViewFuncProcTriDisable(boolean tf) {
		tableDrop.setDisable(tf);
	}

	// 设置select * from 按钮的启用/禁用
	public void setSelectMenuDisable(boolean tf, SqluckyConnector sqluckyConn, TreeNodePo treeNPO) {
		if (tf == false) {
			// selectMenu 的设置
			setSelectMenuAction(sqluckyConn, treeNPO);
		}
		selectMenu.setDisable(tf);
	}

	// 设置复制节点名称
	public void copuNodeName(String nodeName) {
		copyNameMenu.setOnAction(e -> {
			CommonUtils.setClipboardVal(nodeName);
		});
	}

	public void setRefreshDisable(boolean tf) {
		refresh.setDisable(tf);
	}

	/**
	 * 右键是table类型的node的时候, 设置对应按钮执行的函数
	 * 
	 * @param treeItem
	 * @param sqluckyConn
	 * @param schema
	 * @param tablename
	 */
	public void setTableAction(TreeItem<TreeNodePo> treeItem, SqluckyConnector sqluckyConn, String schema,
			String tablename) {
		tableAddNewCol.setOnAction(e -> {
			AppCommonAction.addNewColumn(sqluckyConn, schema, tablename);
		});

		tableDrop.setOnAction(e -> {
			DBInfoTreeContextMenuAction.dropTable(treeItem, sqluckyConn, schema, tablename);
		});

		tableShow.setOnAction(e -> {
			showTableFieldType(sqluckyConn, schema, tablename);
		});
		// selectMenu 的设置
		setSelectMenuAction(sqluckyConn, treeItem.getValue());

	}

	// 右键菜单, 查询按钮设置
	public void setSelectMenuAction(SqluckyConnector sqluckyConn, TreeNodePo treeNPO) {
		var tbpo = treeNPO.getTable();
		String tablename = tbpo.getTableName();
		String tabSchema = tbpo.getTableSchema();
		String str = sqluckyConn.getExportDDL().select20(tabSchema, tablename);;
		selectMenu.setText(str);
		selectMenu.setOnAction(e -> {
			RunSqlStatePo state = new RunSqlStatePo(str, sqluckyConn);
			state.setIsCreateFunc(false);
			state.setSelectLimit(20);
			RunSQLHelper.runSQLByRunSqlStatePo(sqluckyConn, state);
		});
	}

	public static void showTableFieldType(SqluckyConnector dbc, String schema, String tablename) {
		String sql = "SELECT * FROM " + tablename + " WHERE 1=2";
		try {
			DbTableDatePo DP = SelectDao.selectSqlField(dbc.getConn(), sql);
			ObservableList<SheetFieldPo> fields = DP.getFields();

			for (int i = 0; i < fields.size(); i++) {
				SheetFieldPo p = fields.get(i);
				String tyNa = p.getColumnTypeName().get() + "(" + p.getColumnDisplaySize().get();
				if (p.getScale() != null && p.getScale().get() > 0) {
					tyNa += ", " + p.getScale().get();
				}
				tyNa += ")";
				StringProperty strp = new SimpleStringProperty(tyNa);
				p.setValue(strp);
			}
			TableDataDetail.showTableDetail(tablename, "Field Name", "Field Type", fields);

		} catch (SQLException e) {
			e.printStackTrace();
			MyAlert.errorAlert(e.getMessage());
		}

	}

	// 设置选中视图时 对应的按钮action
	public void setViewAction(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String viewName) {
		tableDrop.setOnAction(e -> {
			DBInfoTreeContextMenuAction.dropView(treeItem, dbc, schema, viewName);
		});

	}

	// 设置选中函数时 对应的按钮action
	public void setFuncAction(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String viewName) {
		tableDrop.setOnAction(e -> {
			DBInfoTreeContextMenuAction.dropFunc(treeItem, dbc, schema, viewName);
		});

	}

	public void setProcAction(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String viewName) {
		tableDrop.setOnAction(e -> {
			DBInfoTreeContextMenuAction.dropProc(treeItem, dbc, schema, viewName);
		});

	}

	public void setTriggerAction(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String viewName) {
		tableDrop.setOnAction(e -> {
			DBInfoTreeContextMenuAction.dropTrigger(treeItem, dbc, schema, viewName);
		});

	}

	public void setRefreshAction(TreeItem<TreeNodePo> newValue) {
		refresh.setOnAction(e -> {
			DBinfoTree.DBinfoTreeView.getSelectionModel().select(newValue);
			ConnectionEditor.closeDbConn();
			DBinfoTree.openDbConn();
		});

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

	public TreeItemType getNodeType() {
		return nodeType;
	}

	public void setNodeType(TreeItemType nodeType) {
		this.nodeType = nodeType;

	}

}
