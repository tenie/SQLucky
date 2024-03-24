package net.tenie.Sqlucky.sdk.component.sheet.bottom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.geometry.Insets;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 底部展示数据的sheet 页对象, 数据部分在SheetDataValue对象中, 该类主要是对数据操作的逻辑
 * 
 * @author tenie
 */
public class MyBottomSheet extends  Tab{
	private SheetDataValue tableData;
	private boolean isDDL = false;
	private boolean isDockSide = false; // 判断是否已经独立窗口了

	private int idx = -1;
//	private Tab tab;
	private ContextMenu contextMenu;
	// 摆放按钮, view等容器
	private VBox tabVBox = new VBox();
	// 按钮面板
	AnchorPane buttonAnchorPane ;

	public void clean() {
		if (tableData != null) {
			this.tableData.clean();
			tableData = null;
		}
		this.setContent(null);
		if (contextMenu != null) {
			contextMenu = null;
		}
		tabVBox = null;
	}

	public MyBottomSheet(String tabName) {
		tableData = new SheetDataValue();
		tableData.setTabName(tabName);
//		tab = new Tab(tabName);
		this.setText(tabName);
		this.setOnCloseRequest(SdkComponent.dataTabCloseReq(this));
		this.setContextMenu(tableViewMenu());
//		this.setUserData(this); // 再关闭tab时 获取 MyBottomSheet, 来clean

		this.setContent(this.tabVBox);

		FilteredTableView<ResultSetRowPo> tableView = SdkComponent.creatFilteredTableView(this);
		tableData.setTable(tableView);
	}


	/**
	 * 添加按钮操作容器
	 * @param btnPane
	 */
	public void addButtonPane(AnchorPane btnPane){
		this.tabVBoxAddComponentView(btnPane);
	}

	/**
	 * vbox容器内添加view组件
	 * @param node
	 */
	public void tabVBoxAddComponentView(Node node){
		this.tabVBox.getChildren().add(node);
	}

	// 获取被更新过的数据缓存
	public ObservableList<ResultSetRowPo> getModifyData() {
		var v = tableData.getDataRs().getUpdateDatas();
		return v;
	}




	// 右键菜单
	public ContextMenu tableViewMenu() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeAll = new MenuItem("Close All");
		closeAll.setOnAction(e -> {
			List<Tab> ls = new ArrayList<>();
			for (Tab tmptab : ComponentGetter.dataTabPane.getTabs()) {
				ls.add(tmptab);
			}
			ls.forEach(tmptab -> {
				SdkComponent.clearDataTable(tmptab);
			});
			ComponentGetter.dataTabPane.getTabs().clear();
		});

		MenuItem closeOther = new MenuItem("Close Other");
		closeOther.setOnAction(e -> {
			int size = ComponentGetter.dataTabPane.getTabs().size();
			if (size > 1) {
				List<Tab> ls = new ArrayList<>();
				for (Tab tmptab : ComponentGetter.dataTabPane.getTabs()) {

					if (!Objects.equals(tmptab, this)) {
						ls.add(tmptab);
					}
				}
				ls.forEach(SdkComponent::clearDataTable);

				ComponentGetter.dataTabPane.getTabs().clear();
				ComponentGetter.dataTabPane.getTabs().add(this);
			}

		});

		contextMenu.getItems().addAll(closeAll, closeOther);
		return contextMenu;
	}

	// TODO show
	public void showSelectData(int idx, boolean disable) {
		this.idx = idx;
		// 操作按钮
		List<Node> btnLs = MyBottomSheetButton.sqlDataOptionBtns(this, disable, true);
		// 操作面板
		operatePane(btnLs);
		// 添加sql执行时的信息
		setSqlExecInfo(buttonAnchorPane);
		// 数据表格
		this.tabVBoxAddComponentView(tableData.getTable());
		VBox.setVgrow(tableData.getTable(), Priority.ALWAYS);
		//tab展示
		this.show();
	}

	/**
	 * 执行ddl sql 后的信息展示, 失去焦点后自动关闭
	 * 
	 * @param idx
	 * @param disable
	 */
	public void showInfoDelayRemoveTab(int idx, boolean disable) {
		this.idx = idx;
		operatePane(null);
		// 数据表格
		this.tabVBoxAddComponentView(tableData.getTable());
		VBox.setVgrow(tableData.getTable(), Priority.ALWAYS);

		this.show();

		// 当窗口失去焦点 3秒后关闭(移除)
		this.setOnSelectionChanged(v -> {
			// TODO 锁按钮锁着就不关闭
			if (!tableData.isLock()) {
				if (!this.selectedProperty().get()) {
					CommonUtils.delayRunThread(str -> {
						Platform.runLater(() -> {
							if (!this.selectedProperty().get()) {
								SdkComponent.clearDataTable(this);
								this.setOnSelectionChanged(null);
								this.clean();
							}
						});
					}, 3000);

				}
			}

		});
	}

	public void showCustomBtn(List<Node> btnLs) {
		var tableData = this.getTableData();
		operatePane(btnLs);
		// 添加按钮面板和 数据表格
		this.tabVBoxAddComponentView(tableData.getTable());
		VBox.setVgrow(tableData.getTable(), Priority.ALWAYS);
		this.show();
	}

	// 视图dll
	public static MyBottomSheet showViewDDLSheet(SqluckyConnector sqluckyConn, TablePo table) {
		String name = table.getTableName();
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
//		mtb.setSqlArea(sqlArea);
//		VBox vb = new VBox();
		String ddl = table.getDdl();
		sqlArea.setCodeArea(ddl, false);
		// 表格上面的按钮
		List<Node> btnLs = new ArrayList<>();
		JFXButton sbtn = createSelectBtn(sqluckyConn, table.getTableSchema(), name);
		btnLs.add(sbtn);
		mtb.operatePane(btnLs);
		mtb.tabVBoxAddComponentView(sqlArea);
		VBox.setVgrow(sqlArea, Priority.ALWAYS);

		mtb.show();
		return mtb;
	}

	/**
	 * 数据库对象的ddl语句, 非表, 视图
	 * 
	 * @param sqluckyConn
	 * @param name
	 * @param ddl
	 * @param isRunFunc   是否显示 运行函数按钮, 如函数, 过程, 需要运行的, true显示运行按钮
	 * @param isSelect    是否显示 查询按钮, 如table view 可以select数据的, true显示select按钮
	 * @return
	 */
	public static MyBottomSheet showDdlSheet(SqluckyConnector sqluckyConn, String name, String ddl, boolean isRunFunc,
			boolean isSelect) {
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
//		mtb.setSqlArea(sqlArea);
		DDLBox(sqluckyConn, mtb, ddl, sqlArea);
		mtb.show();
		return mtb;

	}

	// 双击treeview 表格节点, 显示表信息
	public static MyBottomSheet showTableInfoSheet(SqluckyConnector sqluckyConn, TablePo table) {
		String name = table.getTableName();
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
//		mtb.setSqlArea(sqlArea);
		String ddl = table.getDdl();
		sqlArea.setCodeArea(ddl, false);
		// 表格上面的按钮
		List<Node> btnLs = mtb.tableDDLOptionBtns(sqluckyConn, sqlArea, table);
		mtb.operatePane(btnLs);
		mtb.tabVBoxAddComponentView(sqlArea);
		VBox.setVgrow(sqlArea, Priority.ALWAYS);

		mtb.show();
		return mtb;
	}

	/**
	 * 存储过程ddl语句的的页面
	 * 
	 * @param sqluckyConn
	 * @param name
	 * @param ddl
	 * @return
	 */
	public static MyBottomSheet showProcedureSheet(SqluckyConnector sqluckyConn, String name, String ddl) {
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
//		mtb.setSqlArea(sqlArea);
		DDLBox(sqluckyConn, mtb, ddl, sqlArea);
		mtb.show();
		return mtb;
	}

	// 数据tab中的组件
	public static void DDLBox(SqluckyConnector sqluckyConn, MyBottomSheet mtb, String ddl , SqluckyEditor sqlArea ) {
		sqlArea.setCodeArea(ddl, false);
		// 表格上面的按钮, 暂时先不显示操作按钮
//		List<Node> btnLs = mtb.DDLOptionBtns(sqluckyConn, ddl, isRunFunc, isProc, name, isSelect, vb, sp, null);
		mtb.operatePane(null); // 没有操作按钮
		mtb.tabVBoxAddComponentView(sqlArea);
		VBox.setVgrow(sqlArea, Priority.ALWAYS);
	}

	// 在按钮面板上显示信息: sql执行时间获取数据量, 数据库名称信息
	private void setSqlExecInfo(AnchorPane pane) {
		// TODO
		String time = tableData.getExecTime() == 0 ? "0" : tableData.getExecTime() + "";
		String rows = tableData.getRows() == 0 ? "0" : tableData.getRows() + "";
		String connName = tableData.getConnName();
		// 计时/查询行数
		String info = "";
		if (StrUtils.isNotNullOrEmpty(connName)) {
			info = connName;
		}
		if (StrUtils.isNotNullOrEmpty(time)) {
			info += " : " + time + " s / " + rows + " rows";
		}
		Label lb = new Label(info);
		pane.getChildren().add(lb);
		AnchorPane.setRightAnchor(lb, 40.0);
		AnchorPane.setTopAnchor(lb, 6.0);
	}

	// 查询数据表格的操作按钮pane
	public AnchorPane operatePane(List<Node> btnLs) {
		buttonAnchorPane = new AnchorPane();
		// 按钮摆放的容器
		HBox btnHbox = new HBox();

		CommonUtils.addCssClass(buttonAnchorPane, "data-table-btn-anchor-pane");
		buttonAnchorPane.prefHeight(25);

		// 隐藏按钮
		JFXButton hideBottom = tableData.getHideBottom();
		// 锁
		JFXButton lockbtn = tableData.getLockBtn();
		JFXButton dockSideBtn =	MyBottomSheetButton.createDockBtn(this);
		btnHbox.getChildren().addAll(lockbtn, dockSideBtn);
		btnHbox.setPadding(new Insets(3,0,3,0));
		// 将按钮放入容器
		if (btnLs != null) {
			for (var nd : btnLs) {
				if (nd instanceof Label) {
					nd.getStyleClass().add("padding5");
				}
				btnHbox.getChildren().add(nd);
			}
		}

		buttonAnchorPane.getChildren().addAll(btnHbox, hideBottom);

		AnchorPane.setRightAnchor(hideBottom, 0.0);
		AnchorPane.setTopAnchor(hideBottom, 3.0);
		AnchorPane.setTopAnchor(btnHbox, 3.0);

		this.addButtonPane(buttonAnchorPane);
		return buttonAnchorPane;
	}

	// 锁住<锁按钮>
	public void lockLockBtn(JFXButton btn) {
		boolean islock = this.getTableData().isLock();
		if (!islock) {
			this.getTableData().setLock(true);
			btn.setGraphic(IconGenerator.svgImageDefActive("lock"));
		}
	}

	/**
	 * 数据库 表 ddl语句,操作按钮
	 *
	 * @return
	 */
	public List<Node> tableDDLOptionBtns(SqluckyConnector sqluckyConn, VBox sp, TablePo table) {
		String name = table.getTableName();
		List<Node> ls = new ArrayList<>();
		VBox  vb = this.getTabVBox();
		// 查询按钮
		JFXButton selectBtn = createSelectBtn(sqluckyConn, table.getTableSchema(), name);

		ls.add(selectBtn);

		JFXButton showTableDDLBtn = new JFXButton("Table DDL");
		showTableDDLBtn.setGraphic(IconGenerator.svgImageDefActive("table"));

		JFXButton showIndexBtn = new JFXButton("Index");
		showIndexBtn.setGraphic(IconGenerator.svgImageDefActive("gears"));

		JFXButton showFKBtn = new JFXButton("Foreign Key");
		showFKBtn.setGraphic(IconGenerator.svgImageDefActive("foreign-key"));
		// table ddl
		showTableDDLBtn.setDisable(true);
		showTableDDLBtn.setOnMouseClicked(e -> {
			vb.getChildren().remove(1);
			vb.getChildren().add(sp);
			showTableDDLBtn.setDisable(true);
			showIndexBtn.setDisable(false);
			showFKBtn.setDisable(false);
		});
		ls.add(showTableDDLBtn);

		// 索引
		showIndexBtn.setOnMouseClicked(e -> {
			vb.getChildren().remove(1);
			var indexView = table.indexTableView();
			vb.getChildren().add(indexView);
			showTableDDLBtn.setDisable(false);
			showIndexBtn.setDisable(true);
			showFKBtn.setDisable(false);

		});
		ls.add(showIndexBtn);

		// 外键
		showFKBtn.setOnMouseClicked(e -> {
			vb.getChildren().remove(1);
			var foreignKeyTable = table.foreignKeyTableView();
			vb.getChildren().add(foreignKeyTable);

			showFKBtn.setDisable(true);
			showTableDDLBtn.setDisable(false);
			showIndexBtn.setDisable(false);
		});
		ls.add(showFKBtn);
		// TODO 导入
//		MenuButton importFileBtn = new MenuButton();
//		importFileBtn.setGraphic(IconGenerator.svgImageDefActive("bootstrap-save-file"));
//		importFileBtn.setTooltip(MyTooltipTool.instance("Import data"));
//
//		MenuItem excelImportBtn = new MenuItem("Import Excel");
//		excelImportBtn.setGraphic(IconGenerator.svgImageDefActive("EXCEL"));
//		excelImportBtn.setOnAction(e -> {
//			ImportExcelWindow.showWindow(this.getTableData().getTabName(), this.getTableData().getConnName());
//
//		});
//
//		MenuItem csvImportBtn = new MenuItem("Import CSV");
//		csvImportBtn.setGraphic(IconGenerator.svgImageDefActive("CSV"));
//		csvImportBtn.setOnAction(e -> {
//			ImportCsvWindow.showWindow(this.getTableData().getTabName(), this.getTableData().getConnName());
//
//		});
//
//		MenuItem sqlImportBtn = new MenuItem("Import Sql File");
//		sqlImportBtn.setGraphic(IconGenerator.svgImageDefActive("SQL"));
//		sqlImportBtn.setOnAction(e -> {
//			ImportSQLWindow.showWindow(this.getTableData().getTabName(), this.getTableData().getConnName());
//
//		});
//
//		importFileBtn.getItems().addAll(excelImportBtn, csvImportBtn, sqlImportBtn);
//		ls.add(importFileBtn);

		return ls;
	}

	// 创建查询按钮
	public static JFXButton createSelectBtn(SqluckyConnector sqluckyConn, String schemaName, String tableName) {
		// 查询按钮
		JFXButton selectBtn = new JFXButton();
		selectBtn.setGraphic(IconGenerator.svgImageDefActive("windows-magnify-browse"));
		selectBtn.setTooltip(MyTooltipTool.instance("Run SQL: SELECT * FROM " + tableName));
		selectBtn.setOnAction(e -> {
			String sqlstr = "";
			if (StrUtils.isNotNullOrEmpty(schemaName)) {
				sqlstr = "SELECT * FROM " + schemaName + "." + tableName;
			} else {
				sqlstr = "SELECT * FROM " + tableName;
			}

			ComponentGetter.appComponent.runSelectSqlLockTabPane(sqluckyConn, sqlstr);

		});

		return selectBtn;
	}

	/**
	 *
	 */
	public void show() {
		this.setText(tableData.getTabName());
		Platform.runLater(() -> {
			var dataTab = ComponentGetter.dataTabPane;
			if (isDDL) {
				dataTab.getTabs().add(this);
			} else {
				if (idx > -1) {
					dataTab.getTabs().add(idx, this);
				} else {
					dataTab.getTabs().add(this);
				}
			}

			SdkComponent.showBottomSheetPane();
			dataTab.getSelectionModel().select(this);
		});
	}

	// 获取所有数据
	public ObservableList<ResultSetRowPo> getTabData() {
		ResultSetPo spo = tableData.getDataRs();
		ObservableList<ResultSetRowPo> val = spo.getDatas();
		return val;

	}

	public void rmAppendData() {
		tableData.getDataRs().getNewDatas().clear();
	}

	// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
	public RsVal tableInfo() {
		RsVal rv = new RsVal(tableData);
		return rv;
	}







	public SheetDataValue getTableData() {
		return tableData;
	}

	public void setTableData(SheetDataValue tableData) {
		this.tableData = tableData;
	}

//	public SqluckyEditor getSqlArea() {
//		return sqlArea;
//	}
//
//	public void setSqlArea(SqluckyEditor sqlArea) {
//		this.sqlArea = sqlArea;
//	}

	public boolean isDDL() {
		return isDDL;
	}

	public void setDDL(boolean isDDL) {
		this.isDDL = isDDL;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public boolean isDockSide() {
		return isDockSide;
	}

	public void setDockSide(boolean dockSide) {
		isDockSide = dockSide;
	}

	public AnchorPane getButtonAnchorPane() {
		return buttonAnchorPane;
	}

	public void setButtonAnchorPane(AnchorPane buttonAnchorPane) {
		this.buttonAnchorPane = buttonAnchorPane;
	}

	public VBox getTabVBox() {
		return tabVBox;
	}

	public void setTabVBox(VBox tabVBox) {
		this.tabVBox = tabVBox;
	}
}
