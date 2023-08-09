package net.tenie.fx.component.dataView;

import java.util.function.Consumer;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.subwindow.DialogTools;
//import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;

public class DataTableContextMenuAction {

	// 删除字段
	public static void dropColumn(MyBottomSheet myBottomSheet, String colname) {
		RsVal rv = CommonAction.exportSQL(myBottomSheet, CommonAction.DROP_COLUMN, colname);
		if (StrUtils.isNotNullOrEmpty(rv.sql)) {
			// 要被执行的函数
			Consumer<String> caller = x -> {
				CommonAction.execExportSql(rv.sql, rv.conn, rv.dbconnPo);
			};
//			ModalDialog.showComfirmExec("Confirm drop!", "Execute Sql: " + rv.sql + " ?", caller);
			MyAlert.myConfirmation("Execute Sql: " + rv.sql + " ?", caller);
		}

	}

	// 修改字段
	public static void alterColumn(MyBottomSheet myBottomSheet, String colname) {
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			String str = colname + " " + x;
			RsVal rv = CommonAction.exportSQL(myBottomSheet, CommonAction.ALTER_COLUMN, str);
			CommonAction.execExportSql(rv.sql, rv.conn, rv.dbconnPo);
		};
		DialogTools.showExecWindow("Alter " + colname + " Date Type: input words like 'CHAR(10) ", "", caller);

	}

	// 更新表中字段的值
	public static void updateTableColumn(SheetDataValue dataObj, String colname) {
		RsVal rv = new RsVal(dataObj); // SqluckyBottomSheetUtility.tableInfo(dataObj);
		String sql = "UPDATE " + rv.tableName + " SET " + colname + " = ";
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			String strsql = sql + x;
			CommonAction.execExportSql(strsql, rv.conn, rv.dbconnPo);
		};
		DialogTools.showExecWindow("Execute : " + sql + " ? : input your value", "", caller);

	}

	// 更新查询结果中字段的值
	public static void updateCurrentColumn(MyBottomSheet myBottomSheet, String colname, int colIdx) {
		RsVal rv = myBottomSheet.tableInfo();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			updateAllColumn(myBottomSheet, colIdx, x);
		};
		DialogTools.showExecWindow(
				"Execute : Update Current " + rv.tableName + " Column :" + colname + " data ? : input your value", "",
				caller);
	}

	// 更新选中数据的字段的值
	public static void updateSelectColumn(MyBottomSheet myBottomSheet, String colname, int colIdx) {
		RsVal rv = myBottomSheet.tableInfo();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			updateSelectedDataColumn(myBottomSheet, colIdx, x);
		};
		DialogTools.showExecWindow(
				"Execute : Update Selected " + rv.tableName + " Column :" + colname + " data ? : input your value", "",
				caller);

	}

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
		RsVal rv = myBottomSheet.tableInfo();
		value = needTrimChar(value);
		if ("null".equals(value)) {
			value = "<null>";
		}
		FilteredTableView<ResultSetRowPo> dataTableView = rv.dataTableView;
		ObservableList<ResultSetRowPo> alls = dataTableView.getItems();
		for (ResultSetRowPo ls : alls) {
			// 打开cell的值监听, 这样改变值会被缓存起来, 便于更新
			ls.cellAddChangeListener();// null
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

		ObservableList<ResultSetRowPo> alls = myBottomSheet.getTableData().getTable().getSelectionModel()
				.getSelectedItems(); //// SqluckyBottomSheetUtility.dataTableViewSelectedItems(myBottomSheet);
		for (ResultSetRowPo ls : alls) {
			StringProperty tmp = ls.getRowDatas().get(colIdx).getCellData();
			tmp.setValue(value);
		}
		myBottomSheet.dataSave();
	}

}
