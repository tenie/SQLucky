package net.tenie.Sqlucky.sdk.component;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.SqluckyCodeAreaHolder;
import net.tenie.Sqlucky.sdk.db.DeleteDao;
import net.tenie.Sqlucky.sdk.db.InsertDao;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.UpdateDao;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.TreeObjCache;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.subwindow.DockSideWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportCsvWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportExcelWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportSQLWindow;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.DateUtils;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.GenerateSQLString;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtils;
import net.tenie.Sqlucky.sdk.utility.TreeObjAction;

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
//		Button saveBtn = SqluckyBottomSheetUtility.dataPaneSaveBtn();
		String tabName = SqluckyBottomSheetUtility.getTableName(tableData);
		Connection conn = SqluckyBottomSheetUtility.getDbconn(tableData);
		SqluckyConnector dpo = SqluckyBottomSheetUtility.getDbConnection(tableData);
		if (tabName != null && tabName.length() > 0) {
			// 字段
			ObservableList<SheetFieldPo> fpos = SqluckyBottomSheetUtility.getFields(tableData);
			// 待保存数据
			ObservableList<ResultSetRowPo> modifyData = SqluckyBottomSheetUtility.getModifyData(tableData);
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
				SqluckyBottomSheetUtility.rmUpdateData(tableData);
			}

			// 插入操作
			ObservableList<ResultSetRowPo> dataList = SqluckyBottomSheetUtility.getAppendData(tableData);
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
			SqluckyBottomSheetUtility.rmAppendData(tableData);

			// 保存按钮禁用
			saveBtn.setDisable(btnDisable);
			TableViewUtils.showInfo(ddlDmlpo, null);

		}

	}

	// 获取tree 节点中的 table 的sql
	public void findTable() {
		RsVal rv = SqluckyBottomSheetUtility.tableInfo(tableData);
		SqluckyConnector dbcp = rv.dbconnPo;
		if (dbcp == null) {
			return;
		}
		String tbn = rv.tableName;
		String key = "";
		int idx = tbn.indexOf(".");
		if (idx > 0) {
			key = dbcp.getConnName() + "_" + tbn.substring(0, idx);
			tbn = tbn.substring(idx + 1); // 去除schema , 得到表名
		} else {
			key = dbcp.getConnName() + "_" + dbcp.getDefaultSchema();
		}

		// 从表格缓存中查找表
		List<TablePo> tbs = TreeObjCache.tableCache.get(key.toUpperCase());

		TablePo tbrs = null;
		for (TablePo po : tbs) {
			if (po.getTableName().toUpperCase().equals(tbn)) {
				tbrs = po;
				break;
			}
		}
		// 从试图缓存中查找
		if (tbrs == null) {
			tbs = TreeObjCache.viewCache.get(key.toUpperCase());
			for (TablePo po : tbs) {
				if (po.getTableName().toUpperCase().equals(tbn)) {
					tbrs = po;
					break;
				}
			}
		}
		if (tbrs != null)
			TreeObjAction.showTableSql(dbcp, tbrs);

	}

	// 刷新查询结果
	public void refreshData(boolean isLock) {
		String sql = SqluckyBottomSheetUtility.getSelectSQL(tableData);
		Connection conn = SqluckyBottomSheetUtility.getDbconn(tableData);
		String connName = SqluckyBottomSheetUtility.getConnName(tableData);
		if (conn != null) {
			// TODO 关闭当前tab
			var dataTab = ComponentGetter.dataTabPane;
			int selidx = dataTab.getSelectionModel().getSelectedIndex();
			SdkComponent.clearDataTable(selidx);
//			RunSQLHelper.refresh(DBConns.get(connName), sql, selidx + "", isLock);
			ComponentGetter.appComponent.refreshDataTableView(connName, sql, selidx + "", isLock);
		}
	}

	// 添加一行数据
	public void addData() {
//		SqluckyBottomSheet mtd = ComponentGetter.currentDataTab();
		var tbv = this.getTableData().getTable();

		tbv.scrollTo(0);
		ResultSetPo rspo = SqluckyBottomSheetUtility.getResultSet(tableData);
		ResultSetRowPo rowpo = rspo.manualAppendNewRow(0);

		ObservableList<SheetFieldPo> fs = rspo.getFields();
		for (int i = 0; i < fs.size(); i++) {
			SheetFieldPo fieldpo = fs.get(i);
			SimpleStringProperty sp = new SimpleStringProperty("");
//			SimpleStringProperty sp = new SimpleStringProperty("<null>");
			rowpo.addCell(sp, null, fieldpo);
		}
		Platform.runLater(() -> {
			ObservableList<ResultSetCellPo> vals = rowpo.getRowDatas();
			for (ResultSetCellPo val : vals) {
				var cel = val.getCellData();
				cel.set("<null>");
			}
		});

		// 使用 ResultSetPo对象的createAppendNewRow（）函数， 不需要手动给表添加行了
//		tbv.getItems().add(0, rowpo);

		// 点亮保存按钮
		saveBtn.setDisable(false);
	}

	public void deleteData() {

		// 获取当前的table view
//		FilteredTableView<ResultSetRowPo> table = SqluckyBottomSheetUtility.dataTableView();
		FilteredTableView<ResultSetRowPo> table = this.getTableData().getTable();
		String tabName = SqluckyBottomSheetUtility.getTableName(tableData);
		Connection conn = SqluckyBottomSheetUtility.getDbconn(tableData);
//			ObservableList<SheetFieldPo> fpos = SqluckyBottomSheetUtility.getFields();

		ObservableList<ResultSetRowPo> vals = table.getSelectionModel().getSelectedItems();
		List<ResultSetRowPo> selectRows = new ArrayList<>();
		for (var vl : vals) {
			selectRows.add(vl);
		}

		// 行号集合
//			List<String> temp = new ArrayList<>();

		// 执行sql 后的信息 (主要是错误后显示到界面上)
		DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
		Consumer<String> caller = x -> {
			Boolean showDBExecInfo = false;
			try {
				for (int i = 0; i < selectRows.size(); i++) {
					ResultSetRowPo sps = selectRows.get(i);
					String msg = "";
					// 如果不是后期手动添加的行, 就不需要执行数据库删除操作
					Boolean isNewAdd = sps.getIsNewAdd();
					if (isNewAdd == false) {
						showDBExecInfo = true;
						msg = DeleteDao.execDelete(conn, tabName, sps);
					}

					var rs = sps.getResultSet();
					rs.getDatas().remove(sps);
					var fs = ddlDmlpo.getFields();
					var row = ddlDmlpo.addRow();
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
							fs.get(0));
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fs.get(1));
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("success"), fs.get(2));

				}

			} catch (Exception e1) {
				var fs = ddlDmlpo.getFields();
				var row = ddlDmlpo.addRow();
				ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
						fs.get(0));
				ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(e1.getMessage()), fs.get(1));
				ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("fail."), fs.get(2));
			} finally {
				if (showDBExecInfo) {
					TableViewUtils.showInfo(ddlDmlpo, null);
				}
			}
		};
		if (selectRows.size() > 0) {
			MyAlert.myConfirmation("Sure to delete selected rows?", caller);
		}

	}

	public void copyData() {
		// 获取当前的table view
		FilteredTableView<ResultSetRowPo> table = SqluckyBottomSheetUtility.dataTableView(this);
		// 获取字段属性信息
		ObservableList<SheetFieldPo> fs = SqluckyBottomSheetUtility.getFields(tableData);
		// 选中的行数据
		ObservableList<ResultSetRowPo> selectedRows = SqluckyBottomSheetUtility.dataTableViewSelectedItems(this);
		if (selectedRows == null || selectedRows.size() == 0) {
			return;
		}
		try {
			// 遍历选中的行
			for (int i = 0; i < selectedRows.size(); i++) {
				// 一行数据, 提醒: 最后一列是行号
				ResultSetRowPo rowPo = selectedRows.get(i);
				var rs = rowPo.getResultSet();
				ResultSetRowPo appendRow = rs.manualAppendNewRow();
				ObservableList<ResultSetCellPo> cells = rowPo.getRowDatas();
				// copy 一行
				ObservableList<StringProperty> item = FXCollections.observableArrayList();
				for (int j = 0; j < cells.size(); j++) {
					ResultSetCellPo cellPo = cells.get(j);

					StringProperty newsp = new SimpleStringProperty(cellPo.getCellData().get());
					appendRow.addCell(newsp, cellPo.getDbOriginalValue(), cellPo.getField());
					int dataType = fs.get(j).getColumnType().get();
					CommonUtility.newStringPropertyChangeListener(newsp, dataType);
					item.add(newsp);
				}

			}
			table.scrollTo(table.getItems().size() - 1);

			// 保存按钮亮起
//			SqluckyBottomSheetUtility.dataPaneSaveBtn().setDisable(false);
			saveBtn.setDisable(false);
		} catch (Exception e2) {
			MyAlert.errorAlert(e2.getMessage());
		}

	}

	/**
	 * 将数据表, 独立显示
	 */
	public void dockSide() {
//		Tab tab = ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
		String tableName = CommonUtility.tabText(tab);

		FilteredTableView<ResultSetRowPo> table = SqluckyBottomSheetUtility.dataTableView(this);
		table.getColumns().forEach(tabCol -> {
			tabCol.setContextMenu(null);
		});

		DockSideWindow dsw = new DockSideWindow();
		dsw.showWindow(table, tableName);

		TabPane dataTab = ComponentGetter.dataTabPane;
		if (dataTab.getTabs().contains(tab)) {
			dataTab.getTabs().remove(tab);
		}
	}

	public EventHandler<ActionEvent> InsertSQLClipboard(boolean isSelected, boolean isFile, SqluckyBottomSheet mtd) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				File tmpFile = null;
				if (isFile) {
					tmpFile = CommonUtility.getFilePathHelper("sql");
				}
				final File ff = tmpFile;
				LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
					Thread t = new Thread() {
						public void run() {
							String tableName = SqluckyBottomSheetUtility.getTableName(tableData);
							final ObservableList<ResultSetRowPo> fvals = SqluckyBottomSheetUtility
									.getValsHelper(isSelected, mtd, tableData);

							String sql = GenerateSQLString.insertSQLHelper(fvals, tableName);
							if (StrUtils.isNotNullOrEmpty(sql)) {
								if (isFile) {
									if (ff != null) {
										try {
											FileTools.save(ff, sql);
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								} else {
									CommonUtility.setClipboardVal(sql);
								}

							}
						}
					};
					t.start();
				});

			}

		};
	}

	public static EventHandler<ActionEvent> csvStrClipboard(boolean isSelected, boolean isFile, SqluckyBottomSheet mtd,
			SheetDataValue tableData) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {

				File tmpFile = null;
				if (isFile) {
					tmpFile = CommonUtility.getFilePathHelper("csv");
				}
				final File ff = tmpFile;

				LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
					Thread t = new Thread() {
						public void run() {
							ObservableList<ResultSetRowPo> vals = SqluckyBottomSheetUtility.getValsHelper(isSelected,
									mtd, tableData);
							String sql = GenerateSQLString.csvStrHelper(vals);
							if (StrUtils.isNotNullOrEmpty(sql)) {
								if (isFile) {
									if (ff != null) {
										try {
											FileTools.save(ff, sql);
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								} else {
									CommonUtility.setClipboardVal(sql);
								}
							}
						}
					};
					t.start();
				});

			}

		};
	}

	// 导出表的字段, 使用逗号分割
	public static EventHandler<ActionEvent> commaSplitTableFields(SheetDataValue tableData) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
					ObservableList<SheetFieldPo> fs = SqluckyBottomSheetUtility.getFields(tableData);
					Thread t = new Thread() {
						public void run() {
							int size = fs.size();
							StringBuilder fieldsName = new StringBuilder("");
							for (int i = 0; i < size; i++) {
								SheetFieldPo po = fs.get(i);
								String name = po.getColumnName().get();
								fieldsName.append(name);
								fieldsName.append(", \n");

							}
							if (StrUtils.isNotNullOrEmpty(fieldsName.toString())) {
								String rsStr = fieldsName.toString().trim();
								CommonUtility.setClipboardVal(fieldsName.substring(0, rsStr.length() - 1));
							}
						}
					};
					t.start();

				});

			}

		};
	}

	// 导出表的字段包含类型, 使用逗号分割
	public static EventHandler<ActionEvent> commaSplitTableFiledsIncludeType(SheetDataValue tableData) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
					ObservableList<SheetFieldPo> fs = SqluckyBottomSheetUtility.getFields(tableData);
					Thread t = new Thread() {
						public void run() {
							int size = fs.size();
							StringBuilder fieldsName = new StringBuilder("");
							for (int i = 0; i < size; i++) {
								SheetFieldPo po = fs.get(i);
								String name = po.getColumnName().get();
								fieldsName.append(name);
								fieldsName.append(", --");
								fieldsName.append(po.getColumnTypeName().get());
								fieldsName.append("\n");

							}
							if (StrUtils.isNotNullOrEmpty(fieldsName.toString())) {
								CommonUtility.setClipboardVal(fieldsName.toString());
							}
						}
					};
					t.start();
				});
			}

		};
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
			findTable();
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
			addData();
		});
		addBtn.setTooltip(MyTooltipTool.instance("add new data "));
		addBtn.setDisable(disable);

		minusBtn = new JFXButton();
		minusBtn.setGraphic(IconGenerator.svgImage("minus-square", "#EC7774"));

		minusBtn.setOnMouseClicked(e -> {
			deleteData();
		});
		minusBtn.setTooltip(MyTooltipTool.instance("delete data "));
		minusBtn.setDisable(disable);

//	    复制一行数据
		copyBtn = new JFXButton();
		copyBtn.setGraphic(IconGenerator.svgImageDefActive("files-o"));
		copyBtn.setOnMouseClicked(e -> {
			copyData();
		});
		copyBtn.setTooltip(MyTooltipTool.instance("copy selected row data "));
		copyBtn.setDisable(disable);

		// 独立窗口
		dockSideBtn = new JFXButton();
		dockSideBtn.setGraphic(IconGenerator.svgImageDefActive("material-filter-none"));
		dockSideBtn.setOnMouseClicked(e -> {
			dockSide();
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
			ImportExcelWindow.showWindow(this.getTableData().getTabName(), this.getTableData().getConnName());

		});

		MenuItem csvImportBtn = new MenuItem("Import CSV");
		csvImportBtn.setGraphic(IconGenerator.svgImageDefActive("CSV"));
		csvImportBtn.setDisable(disable);
		csvImportBtn.setOnAction(e -> {
			ImportCsvWindow.showWindow(this.getTableData().getTabName(), this.getTableData().getConnName());

		});

		MenuItem sqlImportBtn = new MenuItem("Import Sql File");
		sqlImportBtn.setGraphic(IconGenerator.svgImageDefActive("SQL"));
		sqlImportBtn.setDisable(disable);
		sqlImportBtn.setOnAction(e -> {
			ImportSQLWindow.showWindow(this.getTableData().getTabName(), this.getTableData().getConnName());

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
		selected.setOnAction(InsertSQLClipboard(true, false, this));
		MenuItem selectedfile = new MenuItem("Selected Data to file");
		selectedfile.setOnAction(InsertSQLClipboard(true, true, this));

		MenuItem all = new MenuItem("All Data to Clipboard ");
		all.setOnAction(InsertSQLClipboard(false, false, this));
		MenuItem allfile = new MenuItem("All Data to file");
		allfile.setOnAction(InsertSQLClipboard(false, true, this));

		insertSQL.getItems().addAll(selected, selectedfile, all, allfile);

		// 导出csv
		Menu csv = new Menu("Export CSV Format ");
		MenuItem csvselected = new MenuItem("Selected Data to Clipboard ");
		csvselected.setOnAction(csvStrClipboard(true, false, this, tableData));
		MenuItem csvselectedfile = new MenuItem("Selected Data to file");
		csvselectedfile.setOnAction(csvStrClipboard(true, true, this, tableData));

		MenuItem csvall = new MenuItem("All Data to Clipboard ");
		csvall.setOnAction(csvStrClipboard(false, false, this, tableData));
		MenuItem csvallfile = new MenuItem("All Data to file");
		csvallfile.setOnAction(csvStrClipboard(false, true, this, tableData));

		csv.getItems().addAll(csvselected, csvselectedfile, csvall, csvallfile);

		// 导出 excel
		Menu excel = new Menu("Export Excel ");

		// 导出选中的数据
		MenuItem excelSelected = new MenuItem("Export Selected Data ");
		excelSelected.setOnAction(e -> {
			SqluckyBottomSheetUtility.exportExcelAction(true, this, tableData);
		});

		// 导出所有数据
		MenuItem excelAll = new MenuItem("Export All Data  ");
		excelAll.setOnAction(e -> {
			SqluckyBottomSheetUtility.exportExcelAction(false, this, tableData);
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
		CommaSplit.setOnAction(commaSplitTableFields(tableData));

		MenuItem CommaSplitIncludeType = new MenuItem("Comma splitting Include Field Type");
		CommaSplitIncludeType.setOnAction(commaSplitTableFiledsIncludeType(tableData));

		fieldNames.getItems().addAll(CommaSplit, CommaSplitIncludeType);

		exportBtn.getItems().addAll(insertSQL, csv, excel, fieldNames);

		exportBtn.setOnShowing(e -> {
			var vals = SqluckyBottomSheetUtility.dataTableViewSelectedItems(this);
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
		TableView<ResultSetRowPo> tableView = this.getTableData().getDbValTable();
		ObservableList<ResultSetRowPo> items = tableView.getItems();

		// 添加过滤功能
		searchField.textProperty().addListener((o, oldVal, newVal) -> {
			if (StrUtils.isNotNullOrEmpty(newVal)) {
				TableViewUtils.tableViewAllDataFilter(tableView, items, newVal);
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

	public void setSaveBtn(JFXButton saveBtn) {
		this.saveBtn = saveBtn;
	}

	@Override
	public Button getDetailBtn() {
		return detailBtn;
	}

	public void setDetailBtn(JFXButton detailBtn) {
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
