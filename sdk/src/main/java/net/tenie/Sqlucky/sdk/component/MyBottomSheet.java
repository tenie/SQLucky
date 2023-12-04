package net.tenie.Sqlucky.sdk.component;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.DeleteDao;
import net.tenie.Sqlucky.sdk.db.InsertDao;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.UpdateDao;
import net.tenie.Sqlucky.sdk.excel.ExcelDataPo;
import net.tenie.Sqlucky.sdk.excel.ExcelUtil;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.po.SelectExecInfo;
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
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.DateUtils;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.GenerateSQLString;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtils;
import net.tenie.Sqlucky.sdk.utility.TreeObjAction;

/**
 * 底部展示数据的sheet 页对象, 数据部分在SheetDataValue对象中, 该类主要是对数据操作的逻辑
 * 
 * @author tenie
 */
public class MyBottomSheet {
	private SqluckyEditor sqlArea;
	private SheetDataValue tableData;
	private boolean isDDL = false;
	private boolean isDockSide = false; // 判断是否已经独立窗口了

	private int idx = -1;
	private Tab tab;
	private ContextMenu contextMenu;

	public void clean() {
		if (sqlArea != null) {
			this.sqlArea = null;
		}
		if (tableData != null) {
			this.tableData.clean();
			tableData = null;
		}
		if (tab != null) {
			this.tab.setContent(null);
			this.tab = null;
		}
		if (contextMenu != null) {
			contextMenu = null;
		}
	}

	public MyBottomSheet(String tabName) {
		tableData = new SheetDataValue();
		tableData.setTabName(tabName);
		tab = new Tab(tabName);
		tab.setOnCloseRequest(SdkComponent.dataTabCloseReq(this));
		tab.setContextMenu(tableViewMenu());
		tab.setUserData(this); // 再关闭tab时 获取 MyBottomSheet, 来clean

		FilteredTableView<ResultSetRowPo> table = SdkComponent.creatFilteredTableView(this);
		tableData.setTable(table);
	}

	// 保存按钮逻辑
	public void dataSave() {
		String tabName = tableData.getTabName();
		Connection conn = tableData.getDbConnection().getConn();
		SqluckyConnector dpo = tableData.getDbConnection();
		if (tabName != null && tabName.length() > 0) {
			// 待保存数据
			ObservableList<ResultSetRowPo> modifyData = tableData.getDataRs().getUpdateDatas();
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
									CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
									fds.get(0));
							ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fds.get(1));
							ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty("success"), fds.get(2));
						}

					} catch (Exception e1) {
						e1.printStackTrace();
						btnDisable = false;
						String msg = "failed : " + e1.getMessage();
						msg += "\n" + dpo.translateErrMsg(msg);
						var fds = ddlDmlpo.getFields();
						var row = ddlDmlpo.addRow();
						ddlDmlpo.addData(row,
								CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())), fds.get(0));
						ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fds.get(1));
						ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty("failed"), fds.get(2));
					}
				}
				rmUpdateData();
			}

			// 插入操作
			ObservableList<ResultSetRowPo> dataList = tableData.getDataRs().getNewDatas();// SqluckyBottomSheetUtility.getAppendData(tableData);
			for (ResultSetRowPo os : dataList) {
				try {
					ObservableList<ResultSetCellPo> cells = os.getRowDatas();
					String msg = InsertDao.execInsert(conn, tabName, cells);
					var fds = ddlDmlpo.getFields();
					var row = ddlDmlpo.addRow();
					ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
							fds.get(0));
					ddlDmlpo.addData(row, new SimpleStringProperty(msg), fds.get(1));
					ddlDmlpo.addData(row, new SimpleStringProperty("success"), fds.get(2));

					// 对insert 的数据保存后 , 不能再修改
//					ObservableList<ResultSetCellPo> cells = os.getRowDatas();
					for (int i = 0; i < cells.size(); i++) {
						var cellpo = cells.get(i);
						StringProperty sp = cellpo.getCellData();
						CommonUtils.prohibitChangeListener(sp, sp.get());
					}

				} catch (Exception e1) {
					e1.printStackTrace();
					btnDisable = false;
					var fs = ddlDmlpo.getFields();
					var row = ddlDmlpo.addRow();
					ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
							fs.get(0));
					ddlDmlpo.addData(row, new SimpleStringProperty(e1.getMessage()), fs.get(1));
					ddlDmlpo.addData(row, new SimpleStringProperty("failed"), fs.get(2));
				}
			}
			// 删除缓存数据
//			SqluckyBottomSheetUtility.rmAppendData(tableData);
			tableData.getDataRs().getNewDatas().clear();

			// 保存按钮禁用
			tableData.getSaveBtn().setDisable(btnDisable);
			TableViewUtils.showInfo(ddlDmlpo, null);

		}

	}

	// 清空更新过的数据缓存和新加的数据缓存
	public void rmUpdateData() {
		if (tableData != null) {
			tableData.getDataRs().getNewDatas().clear();
			tableData.getDataRs().getUpdateDatas().clear();
		}
	}

	// 获取被更新过的数据缓存
	public ObservableList<ResultSetRowPo> getModifyData() {
		var v = tableData.getDataRs().getUpdateDatas();
		return v;
	}

	// 获取tree 节点中的 table 的sql
	public void findTable() {
		RsVal rv = tableInfo();
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
		String sql = tableData.getSqlStr();
		Connection conn = tableData.getDbConnection().getConn();
		String connName = tableData.getConnName();
		if (conn != null) {
			if(isDockSide) {
				SelectExecInfo execInfo;
				try {
					execInfo = SelectDao.selectSql2(sql, ConfigVal.MaxRows, DBConns.get(connName));
					ObservableList<ResultSetRowPo> allRawData = execInfo.getDataRs().getDatas();
					tableData.getTable().setItems(allRawData);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else {
				// TODO 关闭当前tab
				var dataTab = ComponentGetter.dataTabPane;
				int selidx = dataTab.getSelectionModel().getSelectedIndex();
				SdkComponent.clearDataTable(selidx);
				ComponentGetter.appComponent.refreshDataTableView(connName, sql, selidx + "", isLock);
			}
		}
	}

	// 添加一行数据
	public void addData() {
		var tbv = this.getTableData().getTable();
		tbv.scrollTo(0);
		ResultSetPo rspo = tableData.getDataRs();
		ResultSetRowPo rowpo = rspo.manualAppendNewRow(0);

		ObservableList<SheetFieldPo> fs = rspo.getFields();
		for (int i = 0; i < fs.size(); i++) {
			SheetFieldPo fieldpo = fs.get(i);
			SimpleStringProperty sp = new SimpleStringProperty("");
			rowpo.addCell(sp, null, fieldpo);
		}
		Platform.runLater(() -> {
			ObservableList<ResultSetCellPo> vals = rowpo.getRowDatas();
			for (ResultSetCellPo val : vals) {
				var cel = val.getCellData();
				cel.set("<null>");
			}
		});

		// 点亮保存按钮
		tableData.getSaveBtn().setDisable(false);
	}

	public void deleteData() {
		// 获取当前的table view
		FilteredTableView<ResultSetRowPo> table = this.getTableData().getTable();
		String tabName = tableData.getTabName();
		Connection conn = tableData.getDbConnection().getConn();

		ObservableList<ResultSetRowPo> vals = table.getSelectionModel().getSelectedItems();
		List<ResultSetRowPo> selectRows = new ArrayList<>();
		for (var vl : vals) {
			selectRows.add(vl);
		}

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
					ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
							fs.get(0));
					ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fs.get(1));
					ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty("success"), fs.get(2));

				}

			} catch (Exception e1) {
				var fs = ddlDmlpo.getFields();
				var row = ddlDmlpo.addRow();
				ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
						fs.get(0));
				ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(e1.getMessage()), fs.get(1));
				ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty("fail."), fs.get(2));
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
		FilteredTableView<ResultSetRowPo> table = tableData.getTable();
		// 获取字段属性信息
		ObservableList<SheetFieldPo> fs = tableData.getColss();
		// 选中的行数据
		ObservableList<ResultSetRowPo> selectedRows = tableData.getTable().getSelectionModel().getSelectedItems();
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
					CommonUtils.newStringPropertyChangeListener(newsp, dataType);
					item.add(newsp);
				}

			}
			table.scrollTo(table.getItems().size() - 1);

			// 保存按钮亮起
			tableData.getSaveBtn().setDisable(false);
		} catch (Exception e2) {
			MyAlert.errorAlert(e2.getMessage());
		}

	}

	/**
	 * 将数据表, 独立显示
	 */
	public void dockSide() {
		String tableName = CommonUtils.tabText(tab);

		FilteredTableView<ResultSetRowPo> table = tableData.getTable();
		table.getColumns().forEach(tabCol -> {
			tabCol.setContextMenu(null);
		});

		DockSideWindow dsw = new DockSideWindow();
		VBox vb = (VBox) tab.getContent();
		tableData.getLockBtn().setDisable(true);
		tableData.getHideBottom().setDisable(true);

		isDockSide = true;
		dsw.showWindow(this, vb, tableName);

		TabPane dataTab = ComponentGetter.dataTabPane;
		if (dataTab.getTabs().contains(tab)) {
			dataTab.getTabs().remove(tab);
		}
	}

	public void InsertSQLClipboard(boolean isSelected, boolean isFile, MyBottomSheet mtd) {

		File tmpFile = null;
		if (isFile) {
			tmpFile = CommonUtils.getFilePathHelper("sql");
		}
		final File ff = tmpFile;
		LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
			Thread t = new Thread() {
				@Override
				public void run() {
					String tableName = tableData.getTabName();// SqluckyBottomSheetUtility.getTableName(tableData);
					final ObservableList<ResultSetRowPo> fvals = getValsHelper(isSelected);

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
							CommonUtils.setClipboardVal(sql);
						}

					}
				}
			};
			t.start();
		});

	}

	public void csvStrClipboard(boolean isSelected, boolean isFile) {
		File tmpFile = null;
		if (isFile) {
			tmpFile = CommonUtils.getFilePathHelper("csv");
		}
		final File ff = tmpFile;

		LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
			Thread t = new Thread() {
				@Override
				public void run() {
					ObservableList<ResultSetRowPo> vals = getValsHelper(isSelected);
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
							CommonUtils.setClipboardVal(sql);
						}
					}
				}
			};
			t.start();
		});
	}

	// 导出表的字段, 使用逗号分割
	public static EventHandler<ActionEvent> commaSplitTableFields(SheetDataValue tableData) {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
					ObservableList<SheetFieldPo> fs = tableData.getColss();
					Thread t = new Thread() {
						@Override
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
								CommonUtils.setClipboardVal(fieldsName.substring(0, rsStr.length() - 1));
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
			@Override
			public void handle(ActionEvent e) {
				LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
					ObservableList<SheetFieldPo> fs = tableData.getColss();
					Thread t = new Thread() {
						@Override
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
								CommonUtils.setClipboardVal(fieldsName.toString());
							}
						}
					};
					t.start();
				});
			}

		};
	}

	public List<Node> sqlDataOptionBtns(boolean disable, boolean isCreate) {
		List<Node> ls = new ArrayList<>();
		// 锁
		JFXButton lockbtn = tableData.getLockBtn();
//		ls.add(lockbtn);
		if (isCreate) {

			JFXButton saveBtn = tableData.getSaveBtn();
			saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
			saveBtn.setOnMouseClicked(e -> {
				dataSave();
			});
			saveBtn.setTooltip(MyTooltipTool.instance("Save data"));
			saveBtn.setDisable(true);
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

			JFXButton detailBtn = new JFXButton();
			detailBtn.setGraphic(IconGenerator.svgImageDefActive("search-plus"));
			detailBtn.setOnMouseClicked(e -> {
				TableDataDetail.show(this);
			});
			detailBtn.setTooltip(MyTooltipTool.instance("current line detail "));
			detailBtn.setDisable(disable);

			JFXButton tableSQLBtn = new JFXButton();
			tableSQLBtn.setGraphic(IconGenerator.svgImageDefActive("table"));
			tableSQLBtn.setOnMouseClicked(e -> {
				findTable();
			});
			tableSQLBtn.setTooltip(MyTooltipTool.instance("Table SQL"));
			tableSQLBtn.setDisable(disable);

			// refresh
			JFXButton refreshBtn = new JFXButton();
			refreshBtn.setGraphic(IconGenerator.svgImageDefActive("refresh"));
			refreshBtn.setOnMouseClicked(e -> {
				refreshData(tableData.isLock());
			});
			refreshBtn.setTooltip(MyTooltipTool.instance("refresh table "));
			refreshBtn.setDisable(disable);

			// 添加一行数据
			JFXButton addBtn = new JFXButton();
			addBtn.setGraphic(IconGenerator.svgImageDefActive("plus-square"));

			addBtn.setOnMouseClicked(e -> {
				addData();
			});
			addBtn.setTooltip(MyTooltipTool.instance("add new data "));
			addBtn.setDisable(disable);

			JFXButton minusBtn = new JFXButton();
			minusBtn.setGraphic(IconGenerator.svgImage("minus-square", "#EC7774"));

			minusBtn.setOnMouseClicked(e -> {
				deleteData();
			});
			minusBtn.setTooltip(MyTooltipTool.instance("delete data "));
			minusBtn.setDisable(disable);

			// 复制一行数据
			JFXButton copyBtn = new JFXButton();
			copyBtn.setGraphic(IconGenerator.svgImageDefActive("files-o"));
			copyBtn.setOnMouseClicked(e -> {
				copyData();
			});
			copyBtn.setTooltip(MyTooltipTool.instance("copy selected row data "));
			copyBtn.setDisable(disable);

			// 独立窗口
			JFXButton dockSideBtn = new JFXButton();
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
			selected.setOnAction(e -> InsertSQLClipboard(true, false, this));
			MenuItem selectedfile = new MenuItem("Selected Data to file");
			selectedfile.setOnAction(e -> InsertSQLClipboard(true, true, this));

			MenuItem all = new MenuItem("All Data to Clipboard ");
			all.setOnAction(e -> InsertSQLClipboard(false, false, this));
			MenuItem allfile = new MenuItem("All Data to file");
			allfile.setOnAction(e -> InsertSQLClipboard(false, true, this));

			insertSQL.getItems().addAll(selected, selectedfile, all, allfile);

			// 导出csv
			Menu csv = new Menu("Export CSV Format ");
			MenuItem csvselected = new MenuItem("Selected Data to Clipboard ");
			csvselected.setOnAction(e -> csvStrClipboard(true, false));
			MenuItem csvselectedfile = new MenuItem("Selected Data to file");
			csvselectedfile.setOnAction(e -> csvStrClipboard(true, true));

			MenuItem csvall = new MenuItem("All Data to Clipboard ");
			csvall.setOnAction(e -> csvStrClipboard(false, false));
			MenuItem csvallfile = new MenuItem("All Data to file");
			csvallfile.setOnAction(e -> csvStrClipboard(false, true));

			csv.getItems().addAll(csvselected, csvselectedfile, csvall, csvallfile);

			// 导出 excel
			Menu excel = new Menu("Export Excel ");

			// 导出选中的数据
			MenuItem excelSelected = new MenuItem("Export Selected Data ");
			excelSelected.setOnAction(e -> {
				exportExcelAction(true);
			});

			// 导出所有数据
			MenuItem excelAll = new MenuItem("Export All Data  ");
			excelAll.setOnAction(e -> {
				exportExcelAction(false);
			});

			excel.getItems().addAll(excelSelected, excelAll);

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
				var vals = tableData.getTable().getSelectionModel().getSelectedItems(); // SqluckyBottomSheetUtility.dataTableViewSelectedItems(this);
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
		}

		// 搜索
		// 查询框
		TextField searchField = new TextField();
		searchField.getStyleClass().add("myTextField");

		AnchorPane txtAP = UiTools.textFieldAddCleanBtn(searchField);
		txtAP.setVisible(false);

		JFXButton searchBtn = new JFXButton();
		searchBtn.setGraphic(ComponentGetter.getIconDefActive("search"));
		searchBtn.setTooltip(MyTooltipTool.instance("Search "));
		searchBtn.setOnAction(e -> {
			txtAP.setVisible(!txtAP.isVisible());
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

		ls.add(searchBtn);
		ls.add(txtAP);

		return ls;
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

					if (!Objects.equals(tmptab, tab)) {
						ls.add(tmptab);
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

	// TODO show
	public void showSelectData(int idx, boolean disable) {
		this.idx = idx;
		VBox vbox = new VBox();
		// 操作按钮
		List<Node> btnLs = this.sqlDataOptionBtns(disable, true);
		// 操作面板
		AnchorPane dtBtnPane = operatePane(btnLs);
		// 添加sql执行时的信息
		setSqlExecInfo(dtBtnPane);
		// 添加按钮面板
		vbox.getChildren().add(dtBtnPane);
		// 数据表格
		vbox.getChildren().add(tableData.getTable());
		VBox.setVgrow(tableData.getTable(), Priority.ALWAYS);
		// 放入 tab中展示
		this.getTab().setContent(vbox);
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

		VBox vbox = new VBox();
		AnchorPane dtBtnPane = operatePane(null);
		// 添加按钮面板
		vbox.getChildren().add(dtBtnPane);
		// 数据表格
		vbox.getChildren().add(tableData.getTable());
		VBox.setVgrow(tableData.getTable(), Priority.ALWAYS);

		this.getTab().setContent(vbox);
		this.show();

		// 当窗口失去焦点 3秒后关闭(移除)
		this.tab.setOnSelectionChanged(v -> {
			// TODO 锁按钮锁着就不关闭
			if (tableData.isLock() == false) {
				if (this.tab != null && this.tab.selectedProperty().get() == false) {
					CommonUtils.delayRunThread(str -> {
						Platform.runLater(() -> {
							if (this.tab != null && this.tab.selectedProperty().get() == false) {
								SdkComponent.clearDataTable(this.getTab());
								this.tab.setOnSelectionChanged(null);
								this.clean();
							}
						});
					}, 3000);

				}
			}

		});
	}

	public void showCustomBtn(List<Node> btnLs) {
		var data = this.getTableData();
		VBox vbox = new VBox();
		AnchorPane dtBtnPane = operatePane(btnLs);
		// 添加按钮面板和 数据表格
		vbox.getChildren().add(dtBtnPane);
		vbox.getChildren().add(data.getTable());
		VBox.setVgrow(data.getTable(), Priority.ALWAYS);

		this.getTab().setContent(vbox);
		this.show();
	}

	// 视图dll
	public static MyBottomSheet showViewDDLSheet(SqluckyConnector sqluckyConn, TablePo table) {
		String name = table.getTableName();
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
		mtb.setSqlArea(sqlArea);
		VBox vb = new VBox();
		String ddl = table.getDdl();
		StackPane sp = sqlArea.getCodeAreaPane(ddl, false);
		// 表格上面的按钮
		List<Node> btnLs = new ArrayList<>();
		JFXButton sbtn = createSelectBtn(sqluckyConn, table.getTableSchema(), name);
		btnLs.add(sbtn);
		AnchorPane fp = mtb.operatePane(btnLs);
		vb.getChildren().add(fp);
		vb.getChildren().add(sp);
		VBox.setVgrow(sp, Priority.ALWAYS);

		mtb.getTab().setContent(vb);
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
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(sqluckyConn, mtb, ddl);
		mtb.getTab().setContent(box);
		mtb.show();
		return mtb;

	}

	// 双击treeview 表格节点, 显示表信息
	public static MyBottomSheet showTableInfoSheet(SqluckyConnector sqluckyConn, TablePo table) {
		String name = table.getTableName();
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
		mtb.setSqlArea(sqlArea);
		VBox vb = new VBox();
		String ddl = table.getDdl();
		StackPane sp = sqlArea.getCodeAreaPane(ddl, false);
		// 表格上面的按钮
		List<Node> btnLs = mtb.tableDDLOptionBtns(sqluckyConn, vb, sp, table);
		AnchorPane fp = mtb.operatePane(btnLs);
		vb.getChildren().add(fp);
		vb.getChildren().add(sp);
		VBox.setVgrow(sp, Priority.ALWAYS);

		mtb.getTab().setContent(vb);
		mtb.show();
		return mtb;
	}

	/**
	 * 存储过程ddl语句的的页面
	 * 
	 * @param sqluckyConn
	 * @param name
	 * @param ddl
	 * @param isRunFunc
	 * @return
	 */
	public static MyBottomSheet showProcedureSheet(SqluckyConnector sqluckyConn, String name, String ddl,
			boolean isRunFunc) {
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(sqluckyConn, mtb, ddl);
		mtb.getTab().setContent(box);
		mtb.show();
		return mtb;
	}

	// 数据tab中的组件
	public static VBox DDLBox(SqluckyConnector sqluckyConn, MyBottomSheet mtb, String ddl) {
		VBox vb = new VBox();

		StackPane sp = mtb.getSqlArea().getCodeAreaPane(ddl, false);
		// 表格上面的按钮, 暂时先不显示操作按钮
//		List<Node> btnLs = mtb.DDLOptionBtns(sqluckyConn, ddl, isRunFunc, isProc, name, isSelect, vb, sp, null);
		AnchorPane fp = mtb.operatePane(null); // 没有操作按钮
		vb.getChildren().add(fp);
		vb.getChildren().add(sp);
		VBox.setVgrow(sp, Priority.ALWAYS);
		return vb;
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
		AnchorPane pane = new AnchorPane();
		// 按钮摆放的容器
		HBox btnHbox = new HBox();

		CommonUtils.addCssClass(pane, "data-table-btn-anchor-pane");
		pane.prefHeight(25);

		// 隐藏按钮
		JFXButton hideBottom = tableData.getHideBottom();
		// 锁
		JFXButton lockbtn = tableData.getLockBtn();
		btnHbox.getChildren().add(lockbtn);
		// 将按钮放入容器
		if (btnLs != null) {
			for (var nd : btnLs) {
				if (nd instanceof Label) {
					nd.getStyleClass().add("padding5");
				}
				btnHbox.getChildren().add(nd);
			}
		}

		pane.getChildren().addAll(btnHbox, hideBottom);

		AnchorPane.setRightAnchor(hideBottom, 0.0);
		AnchorPane.setTopAnchor(hideBottom, 3.0);
		AnchorPane.setTopAnchor(btnHbox, 3.0);

		return pane;
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
	 * @param mytb
	 * @param ddl
	 * @param isRunFunc
	 * @param isProc
	 * @param name
	 * @return
	 */
	public List<Node> tableDDLOptionBtns(SqluckyConnector sqluckyConn, VBox vb, StackPane sp, TablePo table) {
		String name = table.getTableName();
		List<Node> ls = new ArrayList<>();

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
		MenuButton importFileBtn = new MenuButton();
		importFileBtn.setGraphic(IconGenerator.svgImageDefActive("bootstrap-save-file"));
		importFileBtn.setTooltip(MyTooltipTool.instance("Import data"));

		MenuItem excelImportBtn = new MenuItem("Import Excel");
		excelImportBtn.setGraphic(IconGenerator.svgImageDefActive("EXCEL"));
		excelImportBtn.setOnAction(e -> {
			ImportExcelWindow.showWindow(this.getTableData().getTabName(), this.getTableData().getConnName());

		});

		MenuItem csvImportBtn = new MenuItem("Import CSV");
		csvImportBtn.setGraphic(IconGenerator.svgImageDefActive("CSV"));
		csvImportBtn.setOnAction(e -> {
			ImportCsvWindow.showWindow(this.getTableData().getTabName(), this.getTableData().getConnName());

		});

		MenuItem sqlImportBtn = new MenuItem("Import Sql File");
		sqlImportBtn.setGraphic(IconGenerator.svgImageDefActive("SQL"));
		sqlImportBtn.setOnAction(e -> {
			ImportSQLWindow.showWindow(this.getTableData().getTabName(), this.getTableData().getConnName());

		});

		importFileBtn.getItems().addAll(excelImportBtn, csvImportBtn, sqlImportBtn);
		ls.add(importFileBtn);

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
		tab.setText(tableData.getTabName());
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

	public ObservableList<ResultSetRowPo> getValsHelper(boolean isSelected) {
		ObservableList<ResultSetRowPo> vals = null;
		if (isSelected) {
			vals = this.getTableData().getTable().getSelectionModel().getSelectedItems();
		} else {
			vals = getTabData();
		}
		return vals;
	}

	// table view 数据转换为excel导出的数据结构
	public ExcelDataPo tableValueToExcelDataPo(boolean isSelect) {
		String tabName = tableData.getTabName();
		ObservableList<SheetFieldPo> fpos = tableData.getColss();

		ObservableList<ResultSetRowPo> rows = getValsHelper(isSelect);

		ExcelDataPo po = new ExcelDataPo();

		// 表头字段
		List<String> fields = new ArrayList<>();
		for (var fpo : fpos) {
			fields.add(fpo.getColumnLabel().get());
		}
		// 数据
		List<List<String>> datas = new ArrayList<>();
		for (var rowpo : rows) {
			List<String> rowlist = new ArrayList<>();
			ObservableList<ResultSetCellPo> cells = rowpo.getRowDatas();
			for (ResultSetCellPo cell : cells) {
				var cellval = cell.getCellData().get();
				if (cellval != null && "<null>".equals(cellval)) {
					cellval = null;
				}
				rowlist.add(cellval);
			}
			datas.add(rowlist);

		}

		po.setSheetName(tabName);
		po.setHeaderFields(fields);
		po.setDatas(datas);

		return po;
	}

	/**
	 * 表格数据导出到excel
	 * 
	 * @param isSelect  true 导出选中行的数据, fasle 全部导出
	 * @param mtd       可以输入null , null时从当前tabpane中查找对象
	 * @param tableData 可以输入null , null时从当前tabpane中查找对象
	 */
	public void exportExcelAction(boolean isSelect) {
		File ff = CommonUtils.getFilePathHelper("xls");
		if (ff == null)
			return;
		if (ff.exists()) {
			MyAlert.errorAlert("File Name Exist. Need A New File Name, Please!");
			return;
		}
		String filePath =  ff.getAbsolutePath();
		if( ! filePath.endsWith(".xls") && ! filePath.endsWith(".xlsx") ) {
			filePath +=".xls";
		}
		
		File excleFile = new File(filePath);
		LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
			ExcelDataPo po = tableValueToExcelDataPo(isSelect);
			try {
				ExcelUtil.createExcel(po, excleFile);
			} catch (Exception e1) {
				e1.printStackTrace();
				MyAlert.errorAlert("Error");
			}

		});
	}

	public SheetDataValue getTableData() {
		return tableData;
	}

	public void setTableData(SheetDataValue tableData) {
		this.tableData = tableData;
	}

	public SqluckyEditor getSqlArea() {
		return sqlArea;
	}

	public void setSqlArea(SqluckyEditor sqlArea) {
		this.sqlArea = sqlArea;
	}

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

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

	/**
	 * 数据库对象 的ddl语句， 操作按钮 , 除了表格和视图对象, 暂时先不使用
	 * 
	 * @param mytb
	 * @param ddl
	 * @param isRunFunc
	 * @param isProc
	 * @param name
	 * @return
	 */
	private List<Node> DDLOptionBtns_bak(SqluckyConnector sqluckyConn, String ddl, boolean isRunFunc, boolean isProc,
			String name, boolean isSelect, VBox vb, StackPane sp) {
		List<Node> ls = new ArrayList<>();
		// 锁
		JFXButton lockbtn = tableData.getLockBtn();

		if (false) {
			// 保存
			JFXButton saveBtn = new JFXButton();
			saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
			saveBtn.setOnMouseClicked(e -> {
				// TODO 保存存储过程
				ComponentGetter.appComponent.runSQL(sqluckyConn, this.getSqlArea().getCodeArea().getText(), true);
				saveBtn.setDisable(true);

			});
			saveBtn.setTooltip(MyTooltipTool.instance("save"));
			saveBtn.setDisable(true);
			this.getTableData().setSaveBtn(saveBtn);
			ls.add(saveBtn);
			// 编辑
			JFXButton editBtn = new JFXButton();
			editBtn.setGraphic(IconGenerator.svgImageDefActive("edit"));
			editBtn.setOnMouseClicked(e -> {
				if (this.getSqlArea() != null) {
					MyCodeArea codeArea = this.getSqlArea().getCodeArea();
					codeArea.setEditable(true);
					saveBtn.setDisable(false);
//					ButtonFactory.lockLockBtn(mytb, lockbtn);
					lockLockBtn(lockbtn);

				}
			});
			editBtn.setTooltip(MyTooltipTool.instance("Edit"));
			ls.add(editBtn);
		}

		// 运行按钮
//		if (isRunFunc ) {
//			JFXButton runFuncBtn = new JFXButton();
//			runFuncBtn.setGraphic(IconGenerator.svgImageDefActive("play"));
//			runFuncBtn.setOnMouseClicked(e -> {
//				Consumer<String> caller;
//				lockLockBtn(mytb, lockbtn);
////				ButtonFactory.lockLockBtn(mytb, lockbtn);
//				if (isProc) {
//					var fields = CommonUtility.getProcedureFields(ddl);
//					if (fields.size() > 0) {
//						// 有参数的存储过程
//						new ProcedureExecuteWindow(name, fields);
//					} else {
//						// 调用无参数的存储过程
//						caller = x -> {
//							SqluckyConnector dpo = DBConns.getCurrentConnectPO();
//							RunSQLHelper.callProcedure(name, dpo, fields);
//						};
//						ModalDialog.showExecWindow("Run Procedure", name, caller);
//
//					}
//
//				} else {
//					caller = x -> {
//						SqluckyConnector dpo = DBConns.getCurrentConnectPO();
//						String sql = dpo.getExportDDL().exportCallFuncSql(x);
//						RunSQLHelper.refresh(dpo, sql, null, false);
//					};
//					ModalDialog.showExecWindow("Run function", name + "()", caller);
//				}
//
//			});
//			runFuncBtn.setTooltip(MyTooltipTool.instance("Run"));
//			ls.add(runFuncBtn);
//		}

		return ls;
	}

}
