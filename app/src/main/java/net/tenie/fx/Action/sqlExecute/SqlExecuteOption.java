package net.tenie.fx.Action.sqlExecute;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.fxmisc.richtext.CodeArea;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.component.CacheDataTableViewShapeChange;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.DataViewContainer;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.component.SqluckyEditor;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.Dbinfo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.db.TablePrimaryKeysPo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Po.SqlData;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemDbObjects;
import net.tenie.fx.component.dataView.DataTableContextMenu;
import net.tenie.fx.config.DBConns;

public class SqlExecuteOption {
	public static void rmWaitingPane(boolean holdSheet) {
		SdkComponent.rmWaitingPane();
		Platform.runLater(() -> {
			if (holdSheet == false) { // 非刷新的， 删除多余的页
				TabPane dataTab = ComponentGetter.dataTabPane;
				SdkComponent.deleteEmptyTab(dataTab);
			}
		});

	}

	public static Tab addWaitingPane(int tabIdx, boolean holdSheet) {
		Tab v = SdkComponent.addWaitingPane(tabIdx);
		Platform.runLater(() -> {
			if (holdSheet == false) { // 非刷新的， 删除多余的页
				TabPane dataTab = ComponentGetter.dataTabPane;
				SdkComponent.deleteEmptyTab(dataTab);
			}
		});

		return v;
	}

	// 根据表名获取表的主键字段名称集合
	public static List<String> findPrimaryKeys(Connection conn, String tableName) {

		String schemaName = "";
		List<String> keys = new ArrayList<>();
		String tempTableName = tableName;
		if (tableName.contains(".")) {
			String[] arrs = tableName.split("\\.");
			schemaName = arrs[0];
			tempTableName = arrs[1];
		}
		TreeNodePo tnp = DBinfoTree.getSchemaTableNodePo(schemaName);
		if (tnp != null && tnp.getConnItem() != null && tnp.getConnItem().getTableNode() != null) {
			ConnItemDbObjects ci = tnp.getConnItem();
			ObservableList<TreeItem<TreeNodePo>> tabs = ci.getTableNode().getChildren();
			for (TreeItem<TreeNodePo> node : tabs) {
				if (node.getValue().getName().toUpperCase().equals(tempTableName.toUpperCase())) {
					keys = getKeys(conn, node);
				}
			}
		}
		return keys;
	}

	// 获取表格的主键
	private static List<String> getKeys(Connection conn, TreeItem<TreeNodePo> node) {
		List<String> keys = new ArrayList<>();
		try {
			List<TablePrimaryKeysPo> pks = node.getValue().getTable().getPrimaryKeys();
			if (pks == null || pks.size() == 0) {
				Dbinfo.fetchTablePrimaryKeys(conn, node.getValue().getTable());
			}
			pks = node.getValue().getTable().getPrimaryKeys();
			if (pks != null) {
				for (TablePrimaryKeysPo kp : pks) {
					keys.add(kp.getColumnName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return keys;
	}

	// table 添加列
	public static ObservableList<FilteredTableColumn<ResultSetRowPo, String>> createTableColForSqlData(
			ObservableList<SheetFieldPo> cols, List<String> keys, SheetDataValue dvt) {
		int len = cols.size();
		ObservableList<FilteredTableColumn<ResultSetRowPo, String>> colList = FXCollections.observableArrayList();
		for (int i = 0; i < len; i++) {
			String colname = cols.get(i).getColumnLabel().get();
			FilteredTableColumn<ResultSetRowPo, String> col = null;

			boolean iskey = false;
			if (keys != null) {
				if (keys.contains(colname)) {
					iskey = true;
				}
			}
			col = createColumnForSqlData(colname, i, iskey, dvt);

			colList.add(col);
		}

		return colList;
	}

	/**
	 * 创建列
	 */
	public static FilteredTableColumn<ResultSetRowPo, String> createColumnForSqlData(String colname, int colIdx,
			boolean iskey, SheetDataValue dvt) {
		FilteredTableColumn<ResultSetRowPo, String> col = SdkComponent.createColumn(colname, colIdx);
		Label label = (Label) col.getGraphic();
		if (iskey) {
			label.setGraphic(IconGenerator.svgImage("material-vpn-key", 10, "#FF6600"));
		}

		String tableName = dvt.getTabName();
		// 设置列宽
		CacheDataTableViewShapeChange.setColWidthByCache(col, tableName, colname);
		return col;
	}

	// 设置 列的 右键菜单
	public static void setDataTableContextMenu(ObservableList<FilteredTableColumn<ResultSetRowPo, String>> colList,
			ObservableList<SheetFieldPo> cols) {
		int len = cols.size();
		for (int i = 0; i < len; i++) {
			FilteredTableColumn<ResultSetRowPo, String> col = colList.get(i);
			String colname = cols.get(i).getColumnLabel().get();
			int type = cols.get(i).getColumnType().get();
			// 右点菜单
			ContextMenu cm = DataTableContextMenu.DataTableColumnContextMenu(colname, type, col, i);
			col.setContextMenu(cm);
		}
	}

	// 展示信息窗口,
	public static void showExecuteSQLInfo(DbTableDatePo ddlDmlpo, Thread thread) {
		// 有数据才展示
		if (ddlDmlpo.getResultSet().getDatas().size() > 0) {
			FilteredTableView<ResultSetRowPo> table = SdkComponent.creatFilteredTableView();
			// 表内容可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true));
			DataViewContainer.setTabRowWith(table, ddlDmlpo.getResultSet().getDatas().size());
			// table 添加列和数据
			ObservableList<SheetFieldPo> colss = ddlDmlpo.getFields();
			ObservableList<ResultSetRowPo> alldata = ddlDmlpo.getResultSet().getDatas();
			SheetDataValue dvt = new SheetDataValue(table, ConfigVal.EXEC_INFO_TITLE, colss, ddlDmlpo.getResultSet());

			var cols = SdkComponent.createTableColForInfo(colss);
			table.getColumns().addAll(cols);
			table.setItems(alldata);

			SqlExecuteOption.rmWaitingPane(true);
			// 渲染界面
			if (thread != null && thread.isInterrupted()) {
				return;
			}

			boolean showtab = true;
			if (showtab) {
				SqluckyBottomSheet mtd = ComponentGetter.appComponent.sqlDataSheet(dvt, -1, true);

//				mtd.show();
				mtd.showAndDelayRemoveTab();
			}

		}
	}

	/**
	 * 获取要执行的sql, 去除无效的(如-- 开头的)
	 */
	public static List<SqlData> willExecSql(boolean isCurrentLine) {
		List<SqlData> sds = new ArrayList<>();
		String str = "";
		CodeArea code = SqluckyEditor.getCodeArea();
		// 如果是执行当前行
		if (isCurrentLine) {
			try {
				str = SqluckyEditor.getCurrentLineText();
			} finally {
				isCurrentLine = false;
			}
		} else {
			str = SqluckyEditor.getCurrentCodeAreaSQLSelectedText();
		}

		int start = 0;
		if (str != null && str.length() > 0) {
			start = code.getSelection().getStart();
		} else {
			str = SqluckyEditor.getCurrentCodeAreaSQLText();
		}
		// 去除注释, 包注释字符串转换为空白字符串
		str = SqluckyEditor.trimCommentToSpace(str, "--");
//		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		sds = epurateSql(str, start);
		return sds;
	}

	// 将sql 字符串根据;分割成多个字符串 并计算其他信息
	private static List<SqlData> epurateSql(String str, int start) {
		List<SqlData> sds = new ArrayList<>();
		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		List<String> sqls = SqluckyEditor.findSQLFromTxt(str);

		if (sqls.size() > 0) {
			for (String s : sqls) {
				String trimSql = s.trim();
				if (trimSql.length() > 1) {
					SqlData sq = new SqlData(s, start, s.length());
					sds.add(sq);
					start += s.length() + 1;
				}
			}
		} else {
			SqlData sq = new SqlData(str, start, str.length());
			sds.add(sq);
		}

		return sds;
	}

	public static List<SqlData> epurateSql(String str) {
		List<SqlData> sds = new ArrayList<>();
		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		List<String> sqls = SqluckyEditor.findSQLFromTxt(str);

		if (sqls.size() > 0) {
			for (String s : sqls) {
				String trimSql = s.trim();
				if (trimSql.length() > 1) {
					SqlData sq = new SqlData(trimSql, 0, 0);
					sds.add(sq);
				}
			}
		} else {
			SqlData sq = new SqlData(str, 0, 0);
			sds.add(sq);
		}

		return sds;
	}

	// 获取当前执行面板中的连接
	private static Connection getComboBoxDbConn() {
		String connboxVal = ComponentGetter.connComboBox.getValue().getText();
		if (StrUtils.isNullOrEmpty(connboxVal))
			return null;
		Connection conn = DBConns.get(connboxVal).getConn();
		return conn;
	}

}
