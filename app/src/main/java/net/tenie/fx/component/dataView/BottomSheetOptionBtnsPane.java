package net.tenie.fx.component.dataView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.subwindow.ImportCsvWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportExcelWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportSQLWindow;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.window.ProcedureExecuteWindow;

/**
 * 
 * @author tenie
 *
 */
public class BottomSheetOptionBtnsPane extends AnchorPane {

	public BottomSheetOptionBtnsPane(List<Node> btnLs, String connName) {
		super();
		initObj(btnLs, null, null, connName);
	}

	public BottomSheetOptionBtnsPane(List<Node> btnLs, String time, String rows, String connName) {
		super();
		initObj(btnLs, time, rows, connName);

	}

	public void initObj(List<Node> btnLs, String time, String rows, String connName) {
		CommonUtility.addCssClass(this, "data-table-btn-anchor-pane");
		this.prefHeight(25);

		// 隐藏按钮
		JFXButton hideBottom = new JFXButton();
		hideBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-down"));
		hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom());

		// 计时/查询行数
		String info = "";
		if (StrUtils.isNotNullOrEmpty(connName)) {
			info = connName;
		}
		if (StrUtils.isNotNullOrEmpty(time)) {
			info += " : " + time + " s / " + rows + " rows";
		}
		Label lb = new Label(info);

		// 按钮摆放的容器
		HBox btnHbox = new HBox();
		// 将按钮放入容器
		if (btnLs != null) {
			for (var nd : btnLs) {
				if (nd instanceof Label) {
					nd.getStyleClass().add("padding5");
				}
				btnHbox.getChildren().add(nd);
			}
		}

		this.getChildren().addAll(btnHbox, hideBottom, lb);

		AnchorPane.setRightAnchor(hideBottom, 0.0);
		AnchorPane.setRightAnchor(lb, 40.0);
		AnchorPane.setTopAnchor(hideBottom, 3.0);
		AnchorPane.setTopAnchor(lb, 6.0);
		AnchorPane.setTopAnchor(btnHbox, 3.0);
	}

	/**
	 * sql 查询数据后要操作的按钮
	 * 
	 * @param mytb
	 * @param disable
	 * @return
	 */
	public static List<Node> sqlDataOptionBtns(MyBottomSheet mytb, boolean disable) {
		List<Node> ls = new ArrayList<>();
		JFXButton saveBtn = new JFXButton();
		saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
		saveBtn.setOnMouseClicked(e -> {
			ButtonAction.dataSave();
		});
		saveBtn.setTooltip(MyTooltipTool.instance("Save data"));
		saveBtn.setDisable(true);
		mytb.setSaveBtn(saveBtn);

		JFXButton detailBtn = new JFXButton();
		detailBtn.setGraphic(IconGenerator.svgImageDefActive("search-plus"));
		detailBtn.setOnMouseClicked(e -> {
			TableDataDetail.show();
		});
		detailBtn.setTooltip(MyTooltipTool.instance("current line detail "));
		detailBtn.setDisable(disable);

		mytb.setDetailBtn(detailBtn);

		JFXButton tableSQLBtn = new JFXButton();
		tableSQLBtn.setGraphic(IconGenerator.svgImageDefActive("table"));
		tableSQLBtn.setOnMouseClicked(e -> {
			ButtonAction.findTable();
		});
		tableSQLBtn.setTooltip(MyTooltipTool.instance("Table SQL"));
		tableSQLBtn.setDisable(disable);

		// refresh
		JFXButton refreshBtn = new JFXButton();
		refreshBtn.setGraphic(IconGenerator.svgImageDefActive("refresh"));
		refreshBtn.setOnMouseClicked(e -> {
			refreshData(ComponentGetter.currentDataTab().getTableData().isLock());
		});
		refreshBtn.setTooltip(MyTooltipTool.instance("refresh table "));
		refreshBtn.setDisable(disable);

		// 添加一行数据
		JFXButton addBtn = new JFXButton();
		addBtn.setGraphic(IconGenerator.svgImageDefActive("plus-square"));

		addBtn.setOnMouseClicked(e -> {
			addData(saveBtn);
		});
		addBtn.setTooltip(MyTooltipTool.instance("add new data "));
		addBtn.setDisable(disable);

		JFXButton minusBtn = new JFXButton();
		minusBtn.setGraphic(IconGenerator.svgImage("minus-square", "#EC7774"));

		minusBtn.setOnMouseClicked(e -> {
			ButtonAction.deleteData();
		});
		minusBtn.setTooltip(MyTooltipTool.instance("delete data "));
		minusBtn.setDisable(disable);

//	    复制一行数据
		JFXButton copyBtn = new JFXButton();
		copyBtn.setGraphic(IconGenerator.svgImageDefActive("files-o"));
		copyBtn.setOnMouseClicked(e -> {
			ButtonAction.copyData();
		});
		copyBtn.setTooltip(MyTooltipTool.instance("copy selected row data "));
		copyBtn.setDisable(disable);

		// 独立窗口
		JFXButton dockSideBtn = new JFXButton();
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

		// TODO 导出 excel
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
		JFXButton lockbtn = SdkComponent.createLockBtn(mytb);

		// 保存按钮监听 : 保存亮起, 锁住
		saveBtn.disableProperty().addListener(e -> {
			if (!saveBtn.disableProperty().getValue()) {
				if (mytb.getTableData().isLock()) {
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

	/**
	 * 
	 * 
	 * @param mytb
	 * @param disable
	 * @return
	 */
	public static List<Node> infoOptionBtns(MyBottomSheet mytb, boolean disable) {
		List<Node> ls = new ArrayList<>();

		// 锁
		JFXButton lockbtn = SdkComponent.createLockBtn(mytb);

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
		ls.add(searchBtn);
		ls.add(searchField);

		return ls;
	}

	

	/**
	 * 数据库对象（如表，视图）的ddl语句， 操作按钮
	 * 
	 * @param mytb
	 * @param ddl
	 * @param isRunFunc
	 * @param isProc
	 * @param name
	 * @return
	 */
	public static List<Node> DDLOptionBtns(SqluckyConnector sqluckyConn, MyBottomSheet mytb, String ddl,
			boolean isRunFunc, boolean isProc, String name, boolean isSelect, VBox vb, StackPane sp, TablePo table) {
		List<Node> ls = new ArrayList<>();
		// 锁
		JFXButton lockbtn = SdkComponent.createLockBtn(mytb);
		ls.add(lockbtn);

		if (table == null && false) {
			// 保存
			JFXButton saveBtn = new JFXButton();
			saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
			saveBtn.setOnMouseClicked(e -> {
				// TODO 保存存储过程
				RunSQLHelper.runSQL(sqluckyConn, mytb.getSqlArea().getCodeArea().getText(), true);
				saveBtn.setDisable(true);

			});
			saveBtn.setTooltip(MyTooltipTool.instance("save"));
			saveBtn.setDisable(true);
			mytb.setSaveBtn(saveBtn);
			ls.add(saveBtn);
			// 编辑
			JFXButton editBtn = new JFXButton();
			editBtn.setGraphic(IconGenerator.svgImageDefActive("edit"));
			editBtn.setOnMouseClicked(e -> {
				if (mytb.getSqlArea() != null) {
					MyCodeArea codeArea = mytb.getSqlArea().getCodeArea();
					codeArea.setEditable(true);
					saveBtn.setDisable(false);
					ButtonFactory.lockLockBtn(mytb, lockbtn);

				}
			});
			editBtn.setTooltip(MyTooltipTool.instance("Edit"));
			ls.add(editBtn);
		}

		// 运行按钮
		if (isRunFunc && false) {
			JFXButton runFuncBtn = new JFXButton();
			runFuncBtn.setGraphic(IconGenerator.svgImageDefActive("play"));
			runFuncBtn.setOnMouseClicked(e -> {
				Consumer<String> caller;
				ButtonFactory.lockLockBtn(mytb, lockbtn);
				if (isProc) {
					var fields = CommonUtility.getProcedureFields(ddl);
					if (fields.size() > 0) {
						// 有参数的存储过程
						new ProcedureExecuteWindow(name, fields);
					} else {
						// 调用无参数的存储过程
						caller = x -> {
							SqluckyConnector dpo = DBConns.getCurrentConnectPO();
							RunSQLHelper.callProcedure(name, dpo, fields);
						};
						ModalDialog.showExecWindow("Run Procedure", name, caller);

					}

				} else {
					caller = x -> {
						SqluckyConnector dpo = DBConns.getCurrentConnectPO();
						String sql = dpo.getExportDDL().exportCallFuncSql(x);
						RunSQLHelper.refresh(dpo, sql, null, false);
					};
					ModalDialog.showExecWindow("Run function", name + "()", caller);
				}

			});
			runFuncBtn.setTooltip(MyTooltipTool.instance("Run"));
			ls.add(runFuncBtn);
		}

		if (isSelect) {
			// 查询按钮
			JFXButton selectBtn = new JFXButton();
			selectBtn.setGraphic(IconGenerator.svgImageDefActive("windows-magnify-browse"));
			selectBtn.setTooltip(MyTooltipTool.instance("Run SQL: SELECT * FROM " + name));
			selectBtn.setOnAction(e -> {
				String sqlstr = "";
				if (StrUtils.isNotNullOrEmpty(table.getTableSchema())) {
					sqlstr = "SELECT * FROM " + table.getTableSchema() + "." + name;
				} else {
					sqlstr = "SELECT * FROM " + name;
				}
				RunSQLHelper.runSelectSqlLockTabPane(sqluckyConn, sqlstr);

			});

			ls.add(selectBtn);
		}
		if (table != null) {
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

		}

		// TODO 导入
		MenuButton importFileBtn = new MenuButton();
		importFileBtn.setGraphic(IconGenerator.svgImageDefActive("bootstrap-save-file"));
		importFileBtn.setTooltip(MyTooltipTool.instance("Import data"));
//		importFileBtn.setDisable(disable);

		MenuItem excelImportBtn = new MenuItem("Import Excel");
		excelImportBtn.setGraphic(IconGenerator.svgImageDefActive("EXCEL"));
//		excelImportBtn.setDisable(disable);
		excelImportBtn.setOnAction(e -> {
			ImportExcelWindow.showWindow(mytb.getTableData().getTabName(), mytb.getTableData().getConnName());

		});

		MenuItem csvImportBtn = new MenuItem("Import CSV");
		csvImportBtn.setGraphic(IconGenerator.svgImageDefActive("CSV"));
//		csvImportBtn.setDisable(disable);
		csvImportBtn.setOnAction(e -> {
			ImportCsvWindow.showWindow(mytb.getTableData().getTabName(), mytb.getTableData().getConnName());

		});

		MenuItem sqlImportBtn = new MenuItem("Import Sql File");
		sqlImportBtn.setGraphic(IconGenerator.svgImageDefActive("SQL"));
//		sqlImportBtn.setDisable(disable);
		sqlImportBtn.setOnAction(e -> {
			ImportSQLWindow.showWindow(mytb.getTableData().getTabName(), mytb.getTableData().getConnName());

		});

		importFileBtn.getItems().addAll(excelImportBtn, csvImportBtn, sqlImportBtn);
		ls.add(importFileBtn);
		return ls;
	}


}
