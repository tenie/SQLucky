package net.tenie.fx.component.dataView;

import java.util.function.Consumer;

import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonAction;

public class DataTableContextMenuAction {

	// 删除字段
	public static void dropColumn(String colname) {
		RsVal rv = CommonAction.exportSQL(CommonAction.DROP_COLUMN, colname);
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
	public static void alterColumn(String colname) {
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			String str = colname + " " + x;
			RsVal rv = CommonAction.exportSQL(CommonAction.ALTER_COLUMN, str);
			CommonAction.execExportSql(rv.sql, rv.conn, rv.dbconnPo);
		};
		ModalDialog.showExecWindow("Alter " + colname + " Date Type: input words like 'CHAR(10) ", "", caller);

	}

	// 更新表中字段的值
	public static void updateTableColumn(String colname) {
		RsVal rv = SqluckyBottomSheetUtility.tableInfo();
		String sql = "UPDATE " + rv.tableName + " SET " + colname + " = ";
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			String strsql = sql + x;
			CommonAction.execExportSql(strsql, rv.conn, rv.dbconnPo);
		};
		ModalDialog.showExecWindow("Execute : " + sql + " ? : input your value", "", caller);

	}

	// 更新查询结果中字段的值
	public static void updateCurrentColumn(String colname, int colIdx) {
		RsVal rv = SqluckyBottomSheetUtility.tableInfo();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			ButtonAction.updateAllColumn(colIdx, x);
		};
		ModalDialog.showExecWindow(
				"Execute : Update Current " + rv.tableName + " Column :" + colname + " data ? : input your value", "",
				caller);
	}

	// 更新选中数据的字段的值
	public static void updateSelectColumn(String colname, int colIdx) {
		RsVal rv = SqluckyBottomSheetUtility.tableInfo();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			ButtonAction.updateSelectedDataColumn(colIdx, x);
		};
		ModalDialog.showExecWindow(
				"Execute : Update Selected " + rv.tableName + " Column :" + colname + " data ? : input your value", "",
				caller);

	}

}
