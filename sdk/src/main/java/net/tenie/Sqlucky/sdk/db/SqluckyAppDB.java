package net.tenie.Sqlucky.sdk.db;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.fxmisc.richtext.CodeArea;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.component.CacheDataTableViewShapeChange;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.component.ConnItemDbObjects;
import net.tenie.Sqlucky.sdk.po.component.DataTableContextMenu;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.po.db.SqlData;
import net.tenie.Sqlucky.sdk.po.db.TablePrimaryKeysPo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 
 * @author tenie
 *
 */
public class SqluckyAppDB {

	// 连接打开次数的计数, 只有当connTimes = 0 , 调用close, 才会真的关闭
	private static AtomicInteger connTimes = new AtomicInteger(0);
	private static Connection conn;
	// 使用阻塞队列, 串行获取: 连接, 和关闭连接
//	private static BlockingQueue<Connection> bQueue=new ArrayBlockingQueue<>(1);

	// 获取应用本身的数据库链接
	public static Connection getConn() {
		try {
			if (conn == null) {
//				conn = createH2Conn();
				conn = createSqliteConn();
//				System.out.println("获取应用本身的数据库链接");
			} else if (conn.isClosed()) {
//				conn = createH2Conn();
				conn = createSqliteConn();
//				System.out.println("获取应用本身的数据库链接");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		int v = connTimes.addAndGet(1);

		return conn;
	}

	// 获取 应用数据库连接的SqluckyConnector对象
	public static SqluckyConnector getSqluckyConnector() {
		SqluckyConnector sqluckyConn = SqluckySqliteConnector.createTmpConnector(ConfigVal.USER, ConfigVal.PASSWD,
				sqliteJdbcURL());
		return sqluckyConn;
	}

	public static SqluckyConnector getSqluckyConnector(String user, String passwd, String jdbcUrl) {
		SqluckyConnector sqluckyConn = SqluckySqliteConnector.createTmpConnector(user, passwd, jdbcUrl);
		return sqluckyConn;
	}

	public static void closeSqluckyConnector(SqluckyConnector sqluckyConn) {
		sqluckyConn.closeConn();
	}

	public static Connection getConnNotAutoCommit() {
		Connection conn = createSqliteConn();
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void closeConnAndRollback(Connection conn) {
		try {
			conn.rollback();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeConnAndCommit(Connection conn) {
		try {
			conn.commit();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeConn(Connection conn) {
		if (conn != null) {
			try {
				Thread th = new Thread(() -> {
					try {
						Thread.sleep(4000);
						connTimes.addAndGet(-1);
						if (connTimes.get() <= 0) {
							conn.close();
						}

					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
				th.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// test mybites
	public static DataSource getSqliteDataSource() {
		UnpooledDataSource dataSource = null;
		dataSource = new UnpooledDataSource("", sqliteJdbcURL(), ConfigVal.USER, ConfigVal.PASSWD);

		return dataSource;
	}

	private static String getH2FilePath() {
		String path = DBTools.dbFilePath();
		ConfigVal.H2_DB_FILE_NAME = path + ConfigVal.H2_DB_NAME + ConfigVal.H2_DB_VERSION;
		ConfigVal.H2_DB_FULL_FILE_NAME = ConfigVal.H2_DB_FILE_NAME + ".mv.db";
		return ConfigVal.H2_DB_FILE_NAME;
	}

	public static String getSqliteFilePath() {
		String path = DBTools.dbFilePath();
		ConfigVal.H2_DB_FILE_NAME = path + ConfigVal.H2_DB_NAME + ConfigVal.H2_DB_VERSION + "_sqlite.db";
		ConfigVal.H2_DB_FULL_FILE_NAME = ConfigVal.H2_DB_FILE_NAME;
		// 如果db文件还不存在, 就说明时新的版本
		File dbfile = new File(ConfigVal.H2_DB_FILE_NAME);
		if (dbfile.exists() == false) {
			ConfigVal.IS_NEW_DB_VERSION = true;
		}
		return ConfigVal.H2_DB_FILE_NAME;
	}

	private synchronized static Connection createH2Conn() {
		Connection connection = createH2Conn(getH2FilePath(), ConfigVal.USER, ConfigVal.PASSWD);
		return connection;
	}

	private static Connection createH2Conn(String path, String user, String pw) {
		Dbinfo dbinfo = new Dbinfo("jdbc:h2:" + path, user, pw);
		Connection connection = dbinfo.getconn();
		return connection;
	}

	private synchronized static Connection createSqliteConn() {
		Connection connection = createSqliteConn(getSqliteFilePath(), ConfigVal.USER, ConfigVal.PASSWD);
		return connection;
	}

	private static Connection createSqliteConn(String path, String user, String pw) {
		Dbinfo dbinfo = new Dbinfo("jdbc:sqlite:" + path, user, pw);
		Connection connection = dbinfo.getconn();
		return connection;
	}

	private static String h2JdbcURL() {
		String val = "jdbc:h2:" + getH2FilePath();
		return val;
	}

	private static String sqliteJdbcURL() {
		String val = "jdbc:sqlite:" + getSqliteFilePath();
		return val;
	}

	// 给app的数据库插入数据
	public static void execDDL(String sql) {
		var conn = SqluckyAppDB.getConn();
		try {
			DBTools.execDDL(conn, sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

	}

	// 返回第一个字段的字符串值
	public static String selectOne(String sql) {
		var conn = SqluckyAppDB.getConn();
		String val = "";
		try {
			val = DBTools.selectOne(conn, sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
		return val;
	}
	
	


//	public static Tab addWaitingPane(int tabIdx, boolean holdSheet) {
//		Platform.runLater(() -> {
//			SdkComponent.showDetailPane();
//		});
//		Tab v = SdkComponent.addWaitingPane(tabIdx);
//		Platform.runLater(() -> {
//			if (holdSheet == false) { // 非刷新的， 删除多余的页
//				TabPane dataTab = ComponentGetter.dataTabPane;
//				SdkComponent.deleteEmptyTab(dataTab);
//			}
//		});
//
//		return v;
//	}

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
		TreeNodePo tnp = getSchemaTableNodePo(schemaName);
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
	// 获取schema节点的 TreeNodePo
	public static TreeNodePo getSchemaTableNodePo(String schema) {
		Label lb = ComponentGetter.connComboBox.getValue();
		if (lb != null) {
			String str = lb.getText();
			TreeItem<TreeNodePo> tnp =  getConnNode(str);
			if (StrUtils.isNullOrEmpty(schema)) {
				SqluckyConnector dbpo = DBConns.get(str);
				schema = dbpo.getDefaultSchema();
			}

			if (tnp != null) {
				if (tnp.getChildren().size() > 0) {
					ObservableList<TreeItem<TreeNodePo>> lsShc = tnp.getChildren().get(0).getChildren();
					for (TreeItem<TreeNodePo> sche : lsShc) {
						if (sche.getValue().getName().equals(schema)) {
							return sche.getValue();
						}
					}
				}

			}
		}
		return null;
	}
	// 根据链接名称,获取链接Node
	public static TreeItem<TreeNodePo> getConnNode(String dbName) {
//			TreeItem<TreeNodePo> conn =
		TreeItem<TreeNodePo> root = ComponentGetter.treeView.getRoot();
		// 遍历tree root 找到对于的数据库节点
		for (TreeItem<TreeNodePo> connNode : root.getChildren()) {
			if (connNode.getValue().getName().equals(dbName)) {
				return connNode;
			}

		}
		return null;
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
	public static void setDataTableContextMenu(MyBottomSheet myBottomSheet,
			ObservableList<FilteredTableColumn<ResultSetRowPo, String>> colList, ObservableList<SheetFieldPo> cols) {
		int len = cols.size();
		for (int i = 0; i < len; i++) {
			FilteredTableColumn<ResultSetRowPo, String> col = colList.get(i);
			String colname = cols.get(i).getColumnLabel().get();
			int type = cols.get(i).getColumnType().get();
			// 右点菜单
			ContextMenu cm = DataTableContextMenu.DataTableColumnContextMenu(myBottomSheet, colname, type, col, i);
			col.setContextMenu(cm);
		}
	}

	/**
	 * 获取要执行的sql, 去除无效的(如-- 开头的)
	 */
	public static List<SqlData> willExecSql(boolean isCurrentLine) {
		List<SqlData> sds = new ArrayList<>();
		String str = "";
		CodeArea codeArea = MyEditorSheetHelper.getCodeArea();
		// 如果是执行当前行
		if (isCurrentLine) {
			try {
				str = MyEditorSheetHelper.getCurrentLineText();
			} finally {
				isCurrentLine = false;
			}
		} else {
			str = MyEditorSheetHelper.getCurrentCodeAreaSQLSelectedText();
		}

		int start = 0;
		if (str != null && str.length() > 0) {
			start = codeArea.getSelection().getStart();
		} else {
			str = MyEditorSheetHelper.getCurrentCodeAreaSQLText();
		}
		// 去除注释, 包注释字符串转换为空白字符串
		str = MyEditorSheetHelper.trimCommentToSpace(str, "--");
//		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		sds = epurateSql(str, start);
		return sds;
	}

	// 将sql 字符串根据;分割成多个字符串 并计算其他信息
	private static List<SqlData> epurateSql(String str, int start) {
		List<SqlData> sds = new ArrayList<>();
		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		List<String> sqls = MyEditorSheetHelper.findSQLFromTxt(str);

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
		List<String> sqls = MyEditorSheetHelper.findSQLFromTxt(str);

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


	public static void saveConfig(Connection conn, String key, String val) {
		deleteConfigKey(conn, key);
		String sql = "insert into APP_CONFIG (NAME, VAL) values ( '" + key + "' , '" + val + "' )";
		try {
			DBTools.execDML(conn, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void deleteConfigKey(Connection conn, String key) {
		try {
			DBTools.execDDL(conn, "DELETE from APP_CONFIG where name = '" + key + "' ");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String readConfig(Connection conn, String name) {
		String sql = "select   VAL   from   APP_CONFIG   where name = '" + name + "' ";
		String vals = DBTools.selectOne(conn, sql);
		return vals;
	}


}
