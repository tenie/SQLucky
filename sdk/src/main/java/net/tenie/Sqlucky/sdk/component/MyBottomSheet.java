package net.tenie.Sqlucky.sdk.component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.SqluckyCodeAreaHolder;
import net.tenie.Sqlucky.sdk.db.InsertDao;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.UpdateDao;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.ImportCsvWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportExcelWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportSQLWindow;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.DateUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.sqlExecute.SqlExecuteOption;

/**
 * 
 * @author tenie extends Tab
 */
public class MyBottomSheet implements SqluckyBottomSheet {
	public SqluckyCodeAreaHolder sqlArea;
	private SheetDataValue tableData;
	private boolean isDDL = false;
//	private Button saveBtn;
//	private Button detailBtn;
	JFXButton saveBtn;
	JFXButton detailBtn;
	JFXButton tableSQLBtn;
	JFXButton refreshBtn;
	JFXButton addBtn;
	JFXButton minusBtn;
	JFXButton copyBtn;
	JFXButton dockSideBtn;
	private int idx;
	private Tab tab;

	public void clean() {
		if (sqlArea != null) {
			this.sqlArea = null;
		}
		if (tableData != null) {
			this.tableData.clean();
			tableData = null;
		}
		if (saveBtn != null) {
			this.saveBtn = null;
		}
		if (detailBtn != null) {
			this.detailBtn = null;
		}
		if (tab != null) {
			this.tab.setContent(null);
			this.tab = null;
		}
	}

	public void dataSave() {
		String tabName = SqluckyBottomSheetUtility.getTableName();
		Connection conn = SqluckyBottomSheetUtility.getDbconn();
		SqluckyConnector dpo = SqluckyBottomSheetUtility.getDbConnection();
		if (tabName != null && tabName.length() > 0) {
			// 字段
			ObservableList<SheetFieldPo> fpos = SqluckyBottomSheetUtility.getFields();
			// 待保存数据
			ObservableList<ResultSetRowPo> modifyData = SqluckyBottomSheetUtility.getModifyData();
			// 执行sql 后的信息 (主要是错误后显示到界面上)
			DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
			boolean btnDisable = true;
			if (!modifyData.isEmpty()) {
				for (ResultSetRowPo val : modifyData) {
					try {
						String msg = UpdateDao.execUpdate(conn, tabName, val);

						if (StrUtils.isNotNullOrEmpty(msg)) {
							var fds = ddlDmlpo.getFields();
							var row = ddlDmlpo.addRow();
							ddlDmlpo.addData(row,
									CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
									fds.get(0));
							ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fds.get(1));
							ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("success"), fds.get(2));
						}

					} catch (Exception e1) {
						e1.printStackTrace();
						btnDisable = false;
						String msg = "failed : " + e1.getMessage();
						msg += "\n" + dpo.translateErrMsg(msg);
						var fds = ddlDmlpo.getFields();
						var row = ddlDmlpo.addRow();
						ddlDmlpo.addData(row,
								CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
								fds.get(0));
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fds.get(1));
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("failed"), fds.get(2));
					}
				}
				SqluckyBottomSheetUtility.rmUpdateData();
			}

			// 插入操作
			ObservableList<ResultSetRowPo> dataList = SqluckyBottomSheetUtility.getAppendData();
			for (ResultSetRowPo os : dataList) {
				try {
					ObservableList<ResultSetCellPo> cells = os.getRowDatas();
					String msg = InsertDao.execInsert(conn, tabName, cells);
					var fds = ddlDmlpo.getFields();
					var row = ddlDmlpo.addRow();
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
							fds.get(0));
					ddlDmlpo.addData(row, new SimpleStringProperty(msg), fds.get(1));
					ddlDmlpo.addData(row, new SimpleStringProperty("success"), fds.get(2));

					// 对insert 的数据保存后 , 不能再修改
//					ObservableList<ResultSetCellPo> cells = os.getRowDatas();
					for (int i = 0; i < cells.size(); i++) {
						var cellpo = cells.get(i);
						StringProperty sp = cellpo.getCellData();
						CommonUtility.prohibitChangeListener(sp, sp.get());
					}

				} catch (Exception e1) {
					e1.printStackTrace();
					btnDisable = false;
					var fs = ddlDmlpo.getFields();
					var row = ddlDmlpo.addRow();
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
							fs.get(0));
					ddlDmlpo.addData(row, new SimpleStringProperty(e1.getMessage()), fs.get(1));
					ddlDmlpo.addData(row, new SimpleStringProperty("failed"), fs.get(2));
				}
			}
			// 删除缓存数据
			SqluckyBottomSheetUtility.rmAppendData();

			// 保存按钮禁用
			saveBtn.setDisable(btnDisable);
			SqlExecuteOption.showExecuteSQLInfo(ddlDmlpo, null);

		}

	}

	public List<Node> sqlDataOptionBtns(boolean disable) {
		List<Node> ls = new ArrayList<>();
		saveBtn = new JFXButton();
		saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
		saveBtn.setOnMouseClicked(e -> {
			dataSave();
		});
		saveBtn.setTooltip(MyTooltipTool.instance("Save data"));
		saveBtn.setDisable(true);

		detailBtn = new JFXButton();
		detailBtn.setGraphic(IconGenerator.svgImageDefActive("search-plus"));
		detailBtn.setOnMouseClicked(e -> {
			TableDataDetail.show();
		});
		detailBtn.setTooltip(MyTooltipTool.instance("current line detail "));
		detailBtn.setDisable(disable);

		tableSQLBtn = new JFXButton();
		tableSQLBtn.setGraphic(IconGenerator.svgImageDefActive("table"));
		tableSQLBtn.setOnMouseClicked(e -> {
			ButtonAction.findTable();
		});
		tableSQLBtn.setTooltip(MyTooltipTool.instance("Table SQL"));
		tableSQLBtn.setDisable(disable);

		// refresh
		refreshBtn = new JFXButton();
		refreshBtn.setGraphic(IconGenerator.svgImageDefActive("refresh"));
		refreshBtn.setOnMouseClicked(e -> {
			refreshData(ComponentGetter.currentDataTab().getTableData().isLock());
		});
		refreshBtn.setTooltip(MyTooltipTool.instance("refresh table "));
		refreshBtn.setDisable(disable);

		// 添加一行数据
		addBtn = new JFXButton();
		addBtn.setGraphic(IconGenerator.svgImageDefActive("plus-square"));

		addBtn.setOnMouseClicked(e -> {
			addData(saveBtn);
		});
		addBtn.setTooltip(MyTooltipTool.instance("add new data "));
		addBtn.setDisable(disable);

		minusBtn = new JFXButton();
		minusBtn.setGraphic(IconGenerator.svgImage("minus-square", "#EC7774"));

		minusBtn.setOnMouseClicked(e -> {
			ButtonAction.deleteData();
		});
		minusBtn.setTooltip(MyTooltipTool.instance("delete data "));
		minusBtn.setDisable(disable);

//	    复制一行数据
		copyBtn = new JFXButton();
		copyBtn.setGraphic(IconGenerator.svgImageDefActive("files-o"));
		copyBtn.setOnMouseClicked(e -> {
			ButtonAction.copyData();
		});
		copyBtn.setTooltip(MyTooltipTool.instance("copy selected row data "));
		copyBtn.setDisable(disable);

		// 独立窗口
		dockSideBtn = new JFXButton();
		dockSideBtn.setGraphic(IconGenerator.svgImageDefActive("material-filter-none"));
		dockSideBtn.setOnMouseClicked(e -> {
			ButtonAction.dockSide();
		});
		dockSideBtn.setTooltip(MyTooltipTool.instance("Dock side"));
		dockSideBtn.setDisable(disable);

		// excel 导入
		MenuButton importFileBtn = new MenuButton();
		importFileBtn.setGraphic(IconGenerator.svgImageDefActive("bootstrap-save-file"));
		importFileBtn.setTooltip(MyTooltipTool.instance("Import data"));
		importFileBtn.setDisable(disable);

		MenuItem excelImportBtn = new MenuItem("Import Excel");
		excelImportBtn.setGraphic(IconGenerator.svgImageDefActive("EXCEL"));
		excelImportBtn.setDisable(disable);
		excelImportBtn.setOnAction(e -> {
			ImportExcelWindow.showWindow(mytb.getTableData().getTabName(), mytb.getTableData().getConnName());

		});

		MenuItem csvImportBtn = new MenuItem("Import CSV");
		csvImportBtn.setGraphic(IconGenerator.svgImageDefActive("CSV"));
		csvImportBtn.setDisable(disable);
		csvImportBtn.setOnAction(e -> {
			ImportCsvWindow.showWindow(mytb.getTableData().getTabName(), mytb.getTableData().getConnName());

		});

		MenuItem sqlImportBtn = new MenuItem("Import Sql File");
		sqlImportBtn.setGraphic(IconGenerator.svgImageDefActive("SQL"));
		sqlImportBtn.setDisable(disable);
		sqlImportBtn.setOnAction(e -> {
			ImportSQLWindow.showWindow(mytb.getTableData().getTabName(), mytb.getTableData().getConnName());

		});

		importFileBtn.getItems().addAll(excelImportBtn, csvImportBtn, sqlImportBtn);

		// 导出
		MenuButton exportBtn = new MenuButton();
		exportBtn.setGraphic(IconGenerator.svgImageDefActive("share-square-o"));
		exportBtn.setTooltip(MyTooltipTool.instance("Export data"));
		exportBtn.setDisable(disable);

		// 导出sql
		Menu insertSQL = new Menu("Export Insert SQL Format ");
		MenuItem selected = new MenuItem("Selected Data to Clipboard ");
		selected.setOnAction(CommonEventHandler.InsertSQLClipboard(true, false));
		MenuItem selectedfile = new MenuItem("Selected Data to file");
		selectedfile.setOnAction(CommonEventHandler.InsertSQLClipboard(true, true));

		MenuItem all = new MenuItem("All Data to Clipboard ");
		all.setOnAction(CommonEventHandler.InsertSQLClipboard(false, false));
		MenuItem allfile = new MenuItem("All Data to file");
		allfile.setOnAction(CommonEventHandler.InsertSQLClipboard(false, true));

		insertSQL.getItems().addAll(selected, selectedfile, all, allfile);

		// 导出csv
		Menu csv = new Menu("Export CSV Format ");
		MenuItem csvselected = new MenuItem("Selected Data to Clipboard ");
		csvselected.setOnAction(CommonEventHandler.csvStrClipboard(true, false));
		MenuItem csvselectedfile = new MenuItem("Selected Data to file");
		csvselectedfile.setOnAction(CommonEventHandler.csvStrClipboard(true, true));

		MenuItem csvall = new MenuItem("All Data to Clipboard ");
		csvall.setOnAction(CommonEventHandler.csvStrClipboard(false, false));
		MenuItem csvallfile = new MenuItem("All Data to file");
		csvallfile.setOnAction(CommonEventHandler.csvStrClipboard(false, true));

		csv.getItems().addAll(csvselected, csvselectedfile, csvall, csvallfile);

		// 导出 excel
		Menu excel = new Menu("Export Excel ");

		// 导出选中的数据
		MenuItem excelSelected = new MenuItem("Export Selected Data ");
		excelSelected.setOnAction(e -> {
			SqluckyBottomSheetUtility.exportExcelAction(true);
		});

		// 导出所有数据
		MenuItem excelAll = new MenuItem("Export All Data  ");
		excelAll.setOnAction(e -> {
			SqluckyBottomSheetUtility.exportExcelAction(false);
		});

		excel.getItems().addAll(excelSelected, excelAll);
//		excel.setOnShowing(e->{
//			
//		});

		// 导出 txt
//		Menu txt = new Menu("Export TXT Format ");
//		MenuItem txtselected = new MenuItem("Selected Data to Clipboard ");
//		txtselected.setOnAction(CommonEventHandler.txtStrClipboard(true, false));
//		MenuItem txtselectedfile = new MenuItem("Selected Data to file");
//		txtselectedfile.setOnAction(CommonEventHandler.txtStrClipboard(true, true));
//
//		MenuItem txtall = new MenuItem("All Data to Clipboard ");
//		txtall.setOnAction(CommonEventHandler.txtStrClipboard(false, false));
//		MenuItem txtallfile = new MenuItem("All Data to file");
//		txtallfile.setOnAction(CommonEventHandler.txtStrClipboard(false, true));
//
//		txt.getItems().addAll(txtselected, txtselectedfile, txtall, txtallfile);

		// 导出字段
		Menu fieldNames = new Menu("Export Table Field Name ");
		MenuItem CommaSplit = new MenuItem("Comma splitting");
		CommaSplit.setOnAction(CommonEventHandler.commaSplitTableFields());

		MenuItem CommaSplitIncludeType = new MenuItem("Comma splitting Include Field Type");
		CommaSplitIncludeType.setOnAction(CommonEventHandler.commaSplitTableFiledsIncludeType());

		fieldNames.getItems().addAll(CommaSplit, CommaSplitIncludeType);

		exportBtn.getItems().addAll(insertSQL, csv, excel, fieldNames);

		exportBtn.setOnShowing(e -> {
			var vals = SqluckyBottomSheetUtility.dataTableViewSelectedItems();
			if (vals != null && vals.size() > 0) {
				selected.setDisable(false);
				selectedfile.setDisable(false);

				csvselected.setDisable(false);
				csvselectedfile.setDisable(false);
				excelSelected.setDisable(false);
			} else {
				selected.setDisable(true);
				selectedfile.setDisable(true);

				csvselected.setDisable(true);
				csvselectedfile.setDisable(true);
				excelSelected.setDisable(true);
			}
		});

		// 锁
		JFXButton lockbtn = SdkComponent.createLockBtn(this);

		// 保存按钮监听 : 保存亮起, 锁住
		saveBtn.disableProperty().addListener(e -> {
			if (!saveBtn.disableProperty().getValue()) {
				if (this.getTableData().isLock()) {
					lockbtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
				} else {
					lockbtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));

				}
			}
		});

		// 搜索
		// 查询框
		TextField searchField = new TextField();
		searchField.getStyleClass().add("myTextField");
		searchField.setVisible(false);
		JFXButton searchBtn = new JFXButton();
		searchBtn.setGraphic(ComponentGetter.getIconDefActive("search"));
		searchBtn.setTooltip(MyTooltipTool.instance("Search "));
		searchBtn.setOnAction(e -> {
			searchField.setVisible(!searchField.isVisible());
		});
		TableView<ResultSetRowPo> tableView = mytb.getTableData().getDbValTable();
		ObservableList<ResultSetRowPo> items = tableView.getItems();

		// 添加过滤功能
		searchField.textProperty().addListener((o, oldVal, newVal) -> {
			if (StrUtils.isNotNullOrEmpty(newVal)) {
				tableViewAllDataFilter(tableView, items, newVal);
			} else {
				tableView.setItems(items);
			}

		});

		ls.add(lockbtn);
		ls.add(saveBtn);
		ls.add(detailBtn);
		ls.add(tableSQLBtn);
		ls.add(refreshBtn);
		ls.add(addBtn);
		ls.add(minusBtn);
		ls.add(copyBtn);
		ls.add(dockSideBtn);
		ls.add(importFileBtn);

		ls.add(exportBtn);
		ls.add(searchBtn);
		ls.add(searchField);

		return ls;
	}

	public MyBottomSheet(SheetDataValue data, int idx, boolean disable) {
		tab = new Tab(data.getTabName());
		this.tableData = data;
		this.idx = idx;
		tab.setOnCloseRequest(SdkComponent.dataTabCloseReq(this));
		tab.setContextMenu(tableViewMenu());
		tab.setUserData(this);
	}

	public MyBottomSheet(String tabName) {
		tab = new Tab(tabName);
		tab.setOnCloseRequest(SdkComponent.dataTabCloseReq(this));
		tab.setContextMenu(tableViewMenu());
		if (tableData == null) {
			tableData = new SheetDataValue();
		}

		tab.setUserData(this);
	}

	public MyBottomSheet(SheetDataValue data) {
		this(data.getTabName());
		this.tableData = data;

	}

	// 右键菜单
	public ContextMenu tableViewMenu() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeAll = new MenuItem("Close ALl");
		closeAll.setOnAction(e -> {
			List<Tab> ls = new ArrayList<>();
			for (Tab tab : ComponentGetter.dataTabPane.getTabs()) {
				ls.add(tab);
			}
			ls.forEach(tab -> {
				SdkComponent.clearDataTable(tab);
			});
			ComponentGetter.dataTabPane.getTabs().clear();
		});

		MenuItem closeOther = new MenuItem("Close Other");
		closeOther.setOnAction(e -> {
			int size = ComponentGetter.dataTabPane.getTabs().size();
			if (size > 1) {
				List<Tab> ls = new ArrayList<>();
				for (Tab tab : ComponentGetter.dataTabPane.getTabs()) {

					if (!Objects.equals(tab, this)) {
						ls.add(tab);
					}
				}
				ls.forEach(tab -> {
					SdkComponent.clearDataTable(tab);
				});

				ComponentGetter.dataTabPane.getTabs().clear();
				ComponentGetter.dataTabPane.getTabs().add(this.tab);

			}

		});

		contextMenu.getItems().addAll(closeAll, closeOther);
		return contextMenu;
	}

	@Override
	public void show() {
		Platform.runLater(() -> {
			var dataTab = ComponentGetter.dataTabPane;
			if (isDDL) {
				dataTab.getTabs().add(this.tab);
			} else {
				if (idx > -1) {
					dataTab.getTabs().add(idx, this.tab);
				} else {
					dataTab.getTabs().add(this.tab);
				}
			}

			SdkComponent.showDetailPane();
			dataTab.getSelectionModel().select(this.tab);
		});
	}

	@Override
	public void showAndDelayRemoveTab() {
		Platform.runLater(() -> {
			var dataTab = ComponentGetter.dataTabPane;
			if (isDDL) {
				dataTab.getTabs().add(this.tab);
			} else {
				if (idx > -1) {
					dataTab.getTabs().add(idx, this.tab);
				} else {
					dataTab.getTabs().add(this.tab);
				}
			}

			SdkComponent.showDetailPane();
			dataTab.getSelectionModel().select(this.tab);

			// 当窗口失去焦点 3秒后关闭(移除)
			this.tab.setOnSelectionChanged(v -> {
				System.out.println(this.tab.selectedProperty());
				if (this.tab.selectedProperty().get() == false) {
					CommonUtility.delayRunThread(str -> {
						Platform.runLater(() -> {
							if (this.tab.selectedProperty().get() == false) {
								SdkComponent.clearDataTable(this.getTab());
							}
						});
					}, 3000);

				}
			});
		});
	}

	@Override
	public SheetDataValue getTableData() {
		return tableData;
	}

	public void setTableData(SheetDataValue tableData) {
		this.tableData = tableData;
	}

	public SqluckyCodeAreaHolder getSqlArea() {
		return sqlArea;
	}

	public void setSqlArea(SqluckyCodeAreaHolder sqlArea) {
		this.sqlArea = sqlArea;
	}

	public boolean isDDL() {
		return isDDL;
	}

	public void setDDL(boolean isDDL) {
		this.isDDL = isDDL;
	}

	@Override
	public Button getSaveBtn() {
		return saveBtn;
	}

	public void setSaveBtn(Button saveBtn) {
		this.saveBtn = saveBtn;
	}

	@Override
	public Button getDetailBtn() {
		return detailBtn;
	}

	public void setDetailBtn(Button detailBtn) {
		this.detailBtn = detailBtn;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

}
