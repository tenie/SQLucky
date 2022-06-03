package net.tenie.fx.Action;

import java.sql.Connection;
import java.util.function.Consumer;

import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.dataView.MyTabData;
import net.tenie.fx.window.ModalDialog;

public class MenuAction {
	static final int DROP_COLUMN = 1;
	static final int ALTER_COLUMN = 2;
	static final int ADD_COLUMN = 3;

	// 添加新字段
	public static void addNewColumn(SqluckyConnector dbc, String schema, String tablename) {

		Connection conn = dbc.getConn();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			String colname = x.trim();
			String sql = dbc.getExportDDL().exportAlterTableAddColumn(conn, schema, tablename, colname);
			execExportSql(sql, conn, dbc);
		};
		ModalDialog.showExecWindow(tablename + " add column : input words like 'MY_COL CHAR(10)'", "", caller);

	}

	public static void addNewColumn() {
		RsVal rv = MyTabData.tableInfo();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			RsVal rv2 = exportSQL(ADD_COLUMN, x);
			MenuAction.execExportSql(rv2.sql, rv2.conn, rv.dbconnPo);
		};
		ModalDialog.showExecWindow(rv.tableName + " add column : input words like 'MY_COL CHAR(10)'", "", caller);

	}

	// 删除字段
	public static void dropColumn(String colname) {
		RsVal rv = exportSQL(DROP_COLUMN, colname);
		if (StrUtils.isNotNullOrEmpty(rv.sql)) {
			// 要被执行的函数
			Consumer<String> caller = x -> {
				execExportSql(rv.sql, rv.conn, rv.dbconnPo);
			};
			ModalDialog.showComfirmExec("Confirm drop!", "Execute Sql: " + rv.sql + " ?", caller);
		}

	}

	// 修改字段
	public static void alterColumn(String colname) {
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			String str = colname + " " + x;
			RsVal rv = exportSQL(ALTER_COLUMN, str);
			MenuAction.execExportSql(rv.sql, rv.conn, rv.dbconnPo);
		};
		ModalDialog.showExecWindow("Alter " + colname + " Date Type: input words like 'CHAR(10) ", "", caller);

	}

	// 更新表中字段的值
	public static void updateTableColumn(String colname) {
		RsVal rv = MyTabData.tableInfo();
		String sql = "UPDATE " + rv.tableName + " SET " + colname + " = ";
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			String strsql = sql + x;
			MenuAction.execExportSql(strsql, rv.conn, rv.dbconnPo);
		};
		ModalDialog.showExecWindow("Execute : " + sql + " ? : input your value", "", caller);

	}

	// 更新查询结果中字段的值
	public static void updateCurrentColumn(String colname, int colIdx) {
		RsVal rv = MyTabData.tableInfo();
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
		RsVal rv = MyTabData.tableInfo();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			ButtonAction.updateSelectedDataColumn(colIdx, x);
		};
		ModalDialog.showExecWindow(
				"Execute : Update Selected " + rv.tableName + " Column :" + colname + " data ? : input your value", "",
				caller);

	}

	// 执行导出的sql
	public static Long execExportSql(String sql, Connection conn, SqluckyConnector dbconnPo) {
		Long key = RunSQLHelper.runSQLMethodRefresh(dbconnPo, sql, "", false);
		return key;
	}

	// 导出SQL
	private static RsVal exportSQL(int ty, String colname, RsVal rv) {
		try {
			// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
			String sql = "";
			if (DROP_COLUMN == ty) {
				sql = rv.dbconnPo.getExportDDL().exportAlterTableDropColumn(rv.conn, rv.dbconnPo.getDefaultSchema(),
						rv.tableName, colname);
			} else if (ALTER_COLUMN == ty) {
				sql = rv.dbconnPo.getExportDDL().exportAlterTableModifyColumn(rv.conn, rv.dbconnPo.getDefaultSchema(),
						rv.tableName, colname);
			} else if (ADD_COLUMN == ty) {
				sql = rv.dbconnPo.getExportDDL().exportAlterTableAddColumn(rv.conn, rv.dbconnPo.getDefaultSchema(),
						rv.tableName, colname);
			}

			rv.sql = sql;
		} catch (Exception e) {
			MyAlert.errorAlert(e.getMessage());

		}
		return rv;

	}

	private static RsVal exportSQL(int ty, String colname) {
		RsVal rv = MyTabData.tableInfo();
		return exportSQL(ty, colname, rv);
	}

	// 删除成功后的回调， 移除节点
	public static void successFunc(TreeItem<TreeNodePo> treeItem) {
		TreeItem<TreeNodePo> pti = treeItem.getParent();
		pti.getChildren().remove(treeItem);

	}

	/**
	 * 删除表后， 把tree node从界面上去掉
	 * 
	 * @param key         运行sql返回的token
	 * @param treeItem
	 * @param successFunc 回调函数
	 */
	public static void rmTreeNode(Long key, TreeItem<TreeNodePo> treeItem, Consumer<TreeItem<TreeNodePo>> successFunc) {
		Thread td = new Thread() {
			public void run() {
				Integer status = RunSQLHelper.runStatus(key);
				while (status != null) {
					if (status.equals(0)) {
						break;
					} else if (status.equals(1)) {
						if (successFunc != null)
							successFunc.accept(treeItem);
						break;
					} else {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					status = RunSQLHelper.runStatus(key);
				}
			}
		};
		td.start();
	}

	// 删表
	public static void dropTable(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String tablename) {
		Connection conn = dbc.getConn();
		String sql = dbc.getExportDDL().exportDropTable(schema, tablename);
		Consumer<String> caller = x -> {
			Long key = execExportSql(sql, conn, dbc);
			rmTreeNode(key, treeItem, MenuAction::successFunc);

		};
		MyAlert.myConfirmation("Execute : '" + sql + "' ?", caller);

	}

	// 删视图
	public static void dropView(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String viewName) {
		Connection conn = dbc.getConn();
		String sql = dbc.getExportDDL().exportDropView(schema, viewName);
		Consumer<String> caller = x -> {
			var key = execExportSql(sql, conn, dbc);
			rmTreeNode(key, treeItem, MenuAction::successFunc);
		};
		MyAlert.myConfirmation("Execute : '" + sql + "' ?", caller);

	}

	// 删函数
	public static void dropFunc(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String funcName) {
		Connection conn = dbc.getConn();
		String sql = dbc.getExportDDL().exportDropFunction(schema, funcName);
		Consumer<String> caller = x -> {
			var key = execExportSql(sql, conn, dbc);
			rmTreeNode(key, treeItem, MenuAction::successFunc);
		};
		MyAlert.myConfirmation("Execute : '" + sql + "' ?", caller);

	}

	// 删函过程
	public static void dropProc(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String funcName) {
		Connection conn = dbc.getConn();
		String sql = dbc.getExportDDL().exportDropProcedure(schema, funcName);
		Consumer<String> caller = x -> {
			var key = execExportSql(sql, conn, dbc);
			rmTreeNode(key, treeItem, MenuAction::successFunc);
		};
		MyAlert.myConfirmation("Execute : '" + sql + "' ?", caller);

	}

	// 删函触发器
	public static void dropTrigger(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema,
			String funcName) {
		Connection conn = dbc.getConn();
		String sql = dbc.getExportDDL().exportDropTrigger(schema, funcName);
		Consumer<String> caller = x -> {
			var key = execExportSql(sql, conn, dbc);
			rmTreeNode(key, treeItem, MenuAction::successFunc);
		};
		MyAlert.myConfirmation("Execute : '" + sql + "' ?", caller);

	}

}
