package net.tenie.fx.Action;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 查询slq后, 面板上的操作按钮要执行的逻辑
 * 
 * @author tenie
 *
 */
public class ButtonAction {
	/**
	 * 执行保存按钮的逻辑: 新数据插入数据库 字段更新的,执行update
	 */
//	public static void dataSave() {
//		Button saveBtn = SqluckyBottomSheetUtility.dataPaneSaveBtn();
//		String tabName = SqluckyBottomSheetUtility.getTableName();
//		Connection conn = SqluckyBottomSheetUtility.getDbconn();
//		SqluckyConnector dpo = SqluckyBottomSheetUtility.getDbConnection();
//		if (tabName != null && tabName.length() > 0) {
//			// 字段
//			ObservableList<SheetFieldPo> fpos = SqluckyBottomSheetUtility.getFields();
//			// 待保存数据
//			ObservableList<ResultSetRowPo> modifyData = SqluckyBottomSheetUtility.getModifyData();
//			// 执行sql 后的信息 (主要是错误后显示到界面上)
//			DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
//			boolean btnDisable = true;
//			if (!modifyData.isEmpty()) {
//				for (ResultSetRowPo val : modifyData) {
//					try {
//						String msg = UpdateDao.execUpdate(conn, tabName, val);
//
//						if (StrUtils.isNotNullOrEmpty(msg)) {
//							var fds = ddlDmlpo.getFields();
//							var row = ddlDmlpo.addRow();
//							ddlDmlpo.addData(row,
//									CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
//									fds.get(0));
//							ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fds.get(1));
//							ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("success"), fds.get(2));
//						}
//
//					} catch (Exception e1) {
//						e1.printStackTrace();
//						btnDisable = false;
//						String msg = "failed : " + e1.getMessage();
//						msg += "\n" + dpo.translateErrMsg(msg);
//						var fds = ddlDmlpo.getFields();
//						var row = ddlDmlpo.addRow();
//						ddlDmlpo.addData(row,
//								CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
//								fds.get(0));
//						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fds.get(1));
//						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("failed"), fds.get(2));
//					}
//				}
//				SqluckyBottomSheetUtility.rmUpdateData();
//			}
//
//			// 插入操作
//			ObservableList<ResultSetRowPo> dataList = SqluckyBottomSheetUtility.getAppendData();
//			for (ResultSetRowPo os : dataList) {
//				try {
//					ObservableList<ResultSetCellPo> cells = os.getRowDatas();
//					String msg = InsertDao.execInsert(conn, tabName, cells);
//					var fds = ddlDmlpo.getFields();
//					var row = ddlDmlpo.addRow();
//					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
//							fds.get(0));
//					ddlDmlpo.addData(row, new SimpleStringProperty(msg), fds.get(1));
//					ddlDmlpo.addData(row, new SimpleStringProperty("success"), fds.get(2));
//
//					// 对insert 的数据保存后 , 不能再修改
////					ObservableList<ResultSetCellPo> cells = os.getRowDatas();
//					for (int i = 0; i < cells.size(); i++) {
//						var cellpo = cells.get(i);
//						StringProperty sp = cellpo.getCellData();
//						CommonUtility.prohibitChangeListener(sp, sp.get());
//					}
//
//				} catch (Exception e1) {
//					e1.printStackTrace();
//					btnDisable = false;
//					var fs = ddlDmlpo.getFields();
//					var row = ddlDmlpo.addRow();
//					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
//							fs.get(0));
//					ddlDmlpo.addData(row, new SimpleStringProperty(e1.getMessage()), fs.get(1));
//					ddlDmlpo.addData(row, new SimpleStringProperty("failed"), fs.get(2));
//				}
//			}
//			// 删除缓存数据
//			SqluckyBottomSheetUtility.rmAppendData();
//
//			// 保存按钮禁用
//			saveBtn.setDisable(btnDisable);
//			TableViewUtils.showInfo(ddlDmlpo, null);
//
//		}
//
//	}

//	public static void deleteData() {
//
//		// 获取当前的table view
//		FilteredTableView<ResultSetRowPo> table = SqluckyBottomSheetUtility.dataTableView();
//		String tabName = SqluckyBottomSheetUtility.getTableName();
//		Connection conn = SqluckyBottomSheetUtility.getDbconn();
////			ObservableList<SheetFieldPo> fpos = SqluckyBottomSheetUtility.getFields();
//
//		ObservableList<ResultSetRowPo> vals = table.getSelectionModel().getSelectedItems();
//		List<ResultSetRowPo> selectRows = new ArrayList<>();
//		for (var vl : vals) {
//			selectRows.add(vl);
//		}
//
//		// 行号集合
////			List<String> temp = new ArrayList<>();
//
//		// 执行sql 后的信息 (主要是错误后显示到界面上)
//		DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
//		Consumer<String> caller = x -> {
//			Boolean showDBExecInfo = false;
//			try {
//				for (int i = 0; i < selectRows.size(); i++) {
//					ResultSetRowPo sps = selectRows.get(i);
//					String msg = "";
//					// 如果不是后期手动添加的行, 就不需要执行数据库删除操作
//					Boolean isNewAdd = sps.getIsNewAdd();
//					if (isNewAdd == false) {
//						showDBExecInfo = true;
//						msg = DeleteDao.execDelete(conn, tabName, sps);
//					}
//
//					var rs = sps.getResultSet();
//					rs.getDatas().remove(sps);
//					var fs = ddlDmlpo.getFields();
//					var row = ddlDmlpo.addRow();
//					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
//							fs.get(0));
//					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fs.get(1));
//					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("success"), fs.get(2));
//
//				}
//
//			} catch (Exception e1) {
//				var fs = ddlDmlpo.getFields();
//				var row = ddlDmlpo.addRow();
//				ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
//						fs.get(0));
//				ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(e1.getMessage()), fs.get(1));
//				ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("fail."), fs.get(2));
//			} finally {
//				if (showDBExecInfo) {
//					TableViewUtils.showInfo(ddlDmlpo, null);
//				}
//			}
//		};
//		if (selectRows.size() > 0) {
//			MyAlert.myConfirmation("Sure to delete selected rows?", caller);
//		}
//
//	}

	// 复制选择的 行数据 插入到表格末尾
//	public static void copyData() {
//		// 获取当前的table view
//		FilteredTableView<ResultSetRowPo> table = SqluckyBottomSheetUtility.dataTableView();
//		// 获取字段属性信息
//		ObservableList<SheetFieldPo> fs = SqluckyBottomSheetUtility.getFields();
//		// 选中的行数据
//		ObservableList<ResultSetRowPo> selectedRows = SqluckyBottomSheetUtility.dataTableViewSelectedItems();
//		if (selectedRows == null || selectedRows.size() == 0) {
//			return;
//		}
//		try {
//			// 遍历选中的行
//			for (int i = 0; i < selectedRows.size(); i++) {
//				// 一行数据, 提醒: 最后一列是行号
//				ResultSetRowPo rowPo = selectedRows.get(i);
//				var rs = rowPo.getResultSet();
//				ResultSetRowPo appendRow = rs.manualAppendNewRow();
//				ObservableList<ResultSetCellPo> cells = rowPo.getRowDatas();
//				// copy 一行
//				ObservableList<StringProperty> item = FXCollections.observableArrayList();
//				for (int j = 0; j < cells.size(); j++) {
//					ResultSetCellPo cellPo = cells.get(j);
//
//					StringProperty newsp = new SimpleStringProperty(cellPo.getCellData().get());
//					appendRow.addCell(newsp, cellPo.getDbOriginalValue(), cellPo.getField());
//					int dataType = fs.get(j).getColumnType().get();
//					CommonUtility.newStringPropertyChangeListener(newsp, dataType);
//					item.add(newsp);
//				}
//
//			}
//			table.scrollTo(table.getItems().size() - 1);
//
//			// 保存按钮亮起
//			SqluckyBottomSheetUtility.dataPaneSaveBtn().setDisable(false);
//		} catch (Exception e2) {
//			MyAlert.errorAlert(e2.getMessage());
//		}
//
//	}

	// 判断是否需要删除两边的单引号'
	private static String needTrimChar(String value) {
		String rs = value;
		char c1 = value.charAt(0);
		char c2 = value.charAt(value.length() - 1);
		if (c1 == c2 && c1 == '\'') {
			rs = StrUtils.trimChar(value, "'");
		}
		return rs;
	}

	// 更新查询结果中所有数据对应列的值
	public static void updateAllColumn(MyBottomSheet myBottomSheet, int colIdx, String value) {
		RsVal rv = SqluckyBottomSheetUtility.tableInfo(myBottomSheet.getTableData());
		value = needTrimChar(value);
		if ("null".equals(value)) {
			value = "<null>";
		}
		FilteredTableView<ResultSetRowPo> dataTableView = rv.dataTableView;
		ObservableList<ResultSetRowPo> alls = dataTableView.getItems();
		for (ResultSetRowPo ls : alls) {
			// 打开cell的值监听, 这样改变值会被缓存起来, 便于更新
			ls.cellAddChangeListener(null);
			ResultSetCellPo cellpo = ls.getRowDatas().get(colIdx);
			StringProperty tmp = cellpo.getCellData();
			tmp.set(value);
		}
		myBottomSheet.dataSave();
	}

	// 更新查询结果中选中的数据 对应列的值
	public static void updateSelectedDataColumn(MyBottomSheet myBottomSheet, int colIdx, String value) {
		value = needTrimChar(value);
		if ("null".equals(value)) {
			value = "<null>";
		}

		ObservableList<ResultSetRowPo> alls = SqluckyBottomSheetUtility.dataTableViewSelectedItems(myBottomSheet);
		for (ResultSetRowPo ls : alls) {
			StringProperty tmp = ls.getRowDatas().get(colIdx).getCellData();
			tmp.setValue(value);
		}
		myBottomSheet.dataSave();
	}

//
//	/**
//	 * 将数据表, 独立显示
//	 */
//	public static void dockSide() {
//		Tab tab = ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
////		String tableName = tab.getText();
//		String tableName = CommonUtility.tabText(tab);
//
//		FilteredTableView<ResultSetRowPo> table = SqluckyBottomSheetUtility.dataTableView();
//		table.getColumns().forEach(tabCol -> {
//			tabCol.setContextMenu(null);
//		});
//		DockSideWindow dsw = new DockSideWindow();
//		dsw.showWindow(table, tableName);
//
//		TabPane dataTab = ComponentGetter.dataTabPane;
//		if (dataTab.getTabs().contains(tab)) {
//			dataTab.getTabs().remove(tab);
//		}
//	}
}
