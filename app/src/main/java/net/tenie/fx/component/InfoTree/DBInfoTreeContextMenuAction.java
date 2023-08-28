package net.tenie.fx.component.InfoTree;

import java.sql.Connection;
import java.util.function.Consumer;

import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.fx.Action.RunSQLHelper;

public class DBInfoTreeContextMenuAction {

	// 删除成功后的回调， 移除节点
	public static void successFunc(TreeItem<TreeNodePo> treeItem) {
		TreeItem<TreeNodePo> pti = treeItem.getParent();
		pti.getChildren().remove(treeItem);

	}

	// 删表
	public static void dropTable(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String tablename) {
		Connection conn = dbc.getConn();
		String sql = dbc.getExportDDL().exportDropTable(schema, tablename);
		Consumer<String> caller = x -> {
			Long key = AppCommonAction.execExportSql(sql, conn, dbc);
			rmTreeNode(key, treeItem, DBInfoTreeContextMenuAction::successFunc);

		};
		MyAlert.myConfirmation("Execute : '" + sql + "' ?", caller);

	}

	// 删视图
	public static void dropView(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String viewName) {
		Connection conn = dbc.getConn();
		String sql = dbc.getExportDDL().exportDropView(schema, viewName);
		Consumer<String> caller = x -> {
			var key = AppCommonAction.execExportSql(sql, conn, dbc);
			rmTreeNode(key, treeItem, DBInfoTreeContextMenuAction::successFunc);
		};
		MyAlert.myConfirmation("Execute : '" + sql + "' ?", caller);

	}

	// 删函数
	public static void dropFunc(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String funcName) {
		Connection conn = dbc.getConn();
		String sql = dbc.getExportDDL().exportDropFunction(schema, funcName);
		Consumer<String> caller = x -> {
			var key = AppCommonAction.execExportSql(sql, conn, dbc);
			rmTreeNode(key, treeItem, DBInfoTreeContextMenuAction::successFunc);
		};
		MyAlert.myConfirmation("Execute : '" + sql + "' ?", caller);

	}

	// 删函过程
	public static void dropProc(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema, String funcName) {
		Connection conn = dbc.getConn();
		String sql = dbc.getExportDDL().exportDropProcedure(schema, funcName);
		Consumer<String> caller = x -> {
			var key = AppCommonAction.execExportSql(sql, conn, dbc);
			rmTreeNode(key, treeItem, DBInfoTreeContextMenuAction::successFunc);
		};
		MyAlert.myConfirmation("Execute : '" + sql + "' ?", caller);

	}

	// 删函触发器
	public static void dropTrigger(TreeItem<TreeNodePo> treeItem, SqluckyConnector dbc, String schema,
			String funcName) {
		Connection conn = dbc.getConn();
		String sql = dbc.getExportDDL().exportDropTrigger(schema, funcName);
		Consumer<String> caller = x -> {
			var key = AppCommonAction.execExportSql(sql, conn, dbc);
			rmTreeNode(key, treeItem, DBInfoTreeContextMenuAction::successFunc);
		};
		MyAlert.myConfirmation("Execute : '" + sql + "' ?", caller);

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
			@Override
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
}
