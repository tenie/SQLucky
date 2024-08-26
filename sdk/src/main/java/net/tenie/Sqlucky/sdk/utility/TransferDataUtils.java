package net.tenie.Sqlucky.sdk.utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.DBTools;
import net.tenie.Sqlucky.sdk.db.InsertPreparedStatementDao;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;

public class TransferDataUtils {

	private static Logger logger = LogManager.getLogger(TransferDataUtils.class);

	// 设置数据库下拉选的值
	public static void setupDBComboBox(ComboBox<Label> cbox1, ComboBox<Label> cbox2) {
		cbox1.setOnMouseClicked(e -> {
			var items = DBConns.sortLiveChoiceBoxItems();
			if (items == null || items.size() == 0)
				return;
			if (cbox2.getValue() != null) {
				String taDBVal = cbox2.getValue().getText();
				if (taDBVal != null && taDBVal.length() > 0) {
					for (int i = 0; i < items.size(); i++) {
						var itm = items.get(i);
						if (taDBVal.equals(itm.getText())) {
							items.remove(i);
							break;
						}
					}
				}
			}
			cbox1.setItems(items);
		});

		cbox2.setOnMouseClicked(e -> {
			var items = DBConns.sortLiveChoiceBoxItems();
			if (items == null || items.size() == 0)
				return;
			if (cbox1.getValue() != null) {
				String taDBVal = cbox1.getValue().getText();
				if (taDBVal != null && taDBVal.length() > 0) {
					for (int i = 0; i < items.size(); i++) {
						var itm = items.get(i);
						if (taDBVal.equals(itm.getText())) {
							items.remove(i);
							break;
						}
					}
				}
			}
			cbox2.setItems(items);
		});
	}

	/**
	 * 表数据迁移
	 * 
	 * @param conn           数据来源数据库链接
	 * @param toConn         接手数据数据库链接
	 * @param tableName      数据来源的表
	 * @param schename       数据来源的schename
	 * @param targetSchename 接收数据的schename
	 * @param amount         批处理提交行数
	 * @param isThrow        是否抛出异常, 如果选中抛出异常, 会终端数据插入
	 * @param LoggerCaller   日志输出的caller
	 * @throws SQLException
	 */
	@SuppressWarnings("exports") // insertData
	public static void dbTableDataMigration(Connection conn, Connection toConn, String tableName, String schename,
			String targetSchename, int amount, boolean isThrow, Consumer<String> LoggerCaller) throws SQLException {
		String sorTable = getTableName(schename, tableName);
		String sql = "select   *   from   " + sorTable + "    where   1=1  ";
		String countSQL = "select   count(*)  as val  from   " + sorTable + "    where   1=1  ";
		// 多少行数据
		Long countVal = DBTools.selectOneLongVal(conn, countSQL);
		if (countVal == 0) {
			LoggerCaller.accept(tableName + " 的数据为" + countVal + " , 进入下一个表 ");
			return;
		}
		List<SheetFieldPo> fields = new ArrayList<>();
		// DB对象
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			pstate = conn.prepareStatement(sql);
			// 处理结果集
			rs = pstate.executeQuery();
			// 获取元数据
			ResultSetMetaData mdata = rs.getMetaData();
			// 获取元数据列数
			Integer columnnums = Integer.valueOf(mdata.getColumnCount());
			// 迭代元数据
			for (int i = 1; i <= columnnums; i++) {
				SheetFieldPo po = new SheetFieldPo();
				po.setScale(mdata.getScale(i));
				po.setColumnName(mdata.getColumnName(i));
				po.setColumnClassName(mdata.getColumnClassName(i));
				po.setColumnDisplaySize(mdata.getColumnDisplaySize(i));
				po.setColumnLabel(mdata.getColumnLabel(i));
				po.setColumnType(mdata.getColumnType(i));
				po.setColumnTypeName(mdata.getColumnTypeName(i));
				fields.add(po);
			}

			String tarTableName = getTableName(targetSchename, tableName);

			TransferDataUtils.jdbcResultSetExecMigration(toConn, rs, fields, tarTableName, amount, isThrow, countVal,
					LoggerCaller);

		} catch (SQLException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			if (isThrow)
				throw e;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	/**
	 * 根据sql语句的结果迁移数据倒对应数据库的表tarTableName
	 * 
	 * @param conn         数据源链接
	 * @param toConn       接受数据的链接
	 * @param sql          数据源的sql语句
	 * @param tarTableName 接受的表名
	 * @param isThrow      是否抛出异常
	 * @throws SQLException
	 */
	@SuppressWarnings("exports")
	public static void dbTableDataMigrationBySQL(Connection conn, Connection toConn, String sql, String tarTableName,
			boolean isThrow) throws SQLException {
		List<SheetFieldPo> fields = new ArrayList<>();

		// DB对象
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			pstate = conn.prepareStatement(sql);
			// 处理结果集
			rs = pstate.executeQuery();
			// 获取元数据
			ResultSetMetaData mdata = rs.getMetaData();
			// 获取元数据列数
			Integer columnnums = Integer.valueOf(mdata.getColumnCount());
			// 迭代元数据
			for (int i = 1; i <= columnnums; i++) {
				SheetFieldPo po = new SheetFieldPo();
				po.setScale(mdata.getScale(i));
				po.setColumnName(mdata.getColumnName(i));
				po.setColumnClassName(mdata.getColumnClassName(i));
				po.setColumnDisplaySize(mdata.getColumnDisplaySize(i));
				po.setColumnLabel(mdata.getColumnLabel(i));
				po.setColumnType(mdata.getColumnType(i));
				po.setColumnTypeName(mdata.getColumnTypeName(i));
				fields.add(po);
			}

			TransferDataUtils.jdbcResultSetExecMigrationBySql(toConn, rs, fields, tarTableName, isThrow);

		} catch (SQLException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			if (isThrow)
				throw e;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	/**
	 * 根据sql语句的结果迁移数据倒对应数据库的表tarTableName
	 * 
	 * @param conn         数据源链接
	 * @param toConn       接受数据的链接
	 * @param sql          数据源的sql语句
	 * @param isThrow      是否抛出异常
	 * @throws SQLException
	 */
	public static void dbTableDataMigrationBySQLByInsert(Connection conn, Connection toConn, String sql, String insert,
//			int columnnums,
			boolean isThrow) throws SQLException {
		List<SheetFieldPo> fields = new ArrayList<>();

		// DB对象
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			pstate = conn.prepareStatement(sql);
			// 处理结果集
			rs = pstate.executeQuery();
			// 获取元数据
			ResultSetMetaData mdata = rs.getMetaData();
			// 获取元数据列数
			Integer columnnums = Integer.valueOf(mdata.getColumnCount());

			TransferDataUtils.jdbcResultSetExecMigrationBySqlByInsert(toConn, rs, columnnums, insert, isThrow);

		} catch (SQLException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			if (isThrow)
				throw e;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	// 遍历查询结果,将查询结果的字段作为插入语句的字段值插入到数据库
	public static void jdbcResultSetExecMigrationBySqlByInsert(Connection toConn, ResultSet rs, int columnnums,
			// List<SheetFieldPo> fpo,
			String insert, boolean isThrow) throws SQLException {

		PreparedStatement pstmt = null;
		int execLine = 100;

		boolean isAutoCommit = toConn.getAutoCommit();
		if (isAutoCommit) {
			toConn.setAutoCommit(false);
			logger.info("getAutoCommit = " + isAutoCommit);
		}
		try {

			toConn.setAutoCommit(false);
			String insertSql = insert;
			logger.info(insertSql);

			pstmt = toConn.prepareStatement(insertSql);
			int idx = 0;

			// 循环查询结果
			while (rs.next()) {
				idx++;
				for (int i = 0; i < columnnums; i++) {
					Object obj = null;
					try {
						obj = rs.getObject(i + 1);
					} catch (Exception e) {
						e.printStackTrace();
					}

					pstmt.setObject(i + 1, obj);
				}
				pstmt.addBatch();
				if (idx % execLine == 0) {
					int[] count = pstmt.executeBatch();
					toConn.commit();
					int execCountLen = count.length;
					logger.info("instert = " + execCountLen);
				}

			}

			if (idx % execLine > 0) {
				int[] count = pstmt.executeBatch();
				toConn.commit();
				int execCountLen = count.length;
				logger.info("instert = " + execCountLen);
			}
			logger.info("\n===========idx = " + idx + "==============");
		} catch (Exception e1) {
			e1.printStackTrace();
			logger.debug(e1.getMessage());
			if (isThrow)
				throw e1;
		} finally {
			if (pstmt != null)
				pstmt.close();

			boolean tmpisAutoCommit = toConn.getAutoCommit();
			if (isAutoCommit != tmpisAutoCommit) {
				toConn.setAutoCommit(isAutoCommit);
			}
		}
	}

	// 遍历查询结果,将查询结果的字段作为插入语句的字段值插入到数据库
	public static void jdbcResultSetExecMigrationBySql(Connection toConn, ResultSet rs, List<SheetFieldPo> fpo,
			String tableName, boolean isThrow) throws SQLException {

		PreparedStatement pstmt = null;
		int execLine = 100;

		boolean isAutoCommit = toConn.getAutoCommit();
		if (isAutoCommit) {
			toConn.setAutoCommit(false);
			logger.info("getAutoCommit = " + isAutoCommit);
		}
		try {

			toConn.setAutoCommit(false);
			String insertSql = InsertPreparedStatementDao.createPreparedStatementSql(tableName, fpo);

			logger.info(insertSql);

			pstmt = toConn.prepareStatement(insertSql);
			int idx = 0;

			int columnnums = fpo.size();
			// 循环查询结果
			while (rs.next()) {
				idx++;
				for (int i = 0; i < columnnums; i++) {
					Object obj = null;
					try {
						obj = rs.getObject(i + 1);
					} catch (Exception e) {
						e.printStackTrace();
					}

					pstmt.setObject(i + 1, obj);
				}
//				pstmt.execute();
				pstmt.addBatch();
				if (idx % execLine == 0) {
					int[] count = pstmt.executeBatch();
					toConn.commit();
					int execCountLen = count.length;
					logger.info("instert = " + execCountLen);
				}

			}

			if (idx % execLine > 0) {
				int[] count = pstmt.executeBatch();
				toConn.commit();
				int execCountLen = count.length;
				logger.info("instert = " + execCountLen);
			}
			
			logger.info("============= idx : " + idx + "===============" );
		} catch (Exception e1) {
			e1.printStackTrace();
			logger.debug(e1.getMessage());
			if (isThrow)
				throw e1;
		} finally {
			if (pstmt != null)
				pstmt.close();

			boolean tmpisAutoCommit = toConn.getAutoCommit();
			if (isAutoCommit != tmpisAutoCommit) {
				toConn.setAutoCommit(isAutoCommit);
			}
		}
	}

	// 遍历查询结果,将查询结果的字段作为插入语句的字段值插入到数据库
	public static void jdbcResultSetExecMigration(Connection toConn, ResultSet rs, List<SheetFieldPo> fpo,
			String tableName, int amount, boolean isThrow, Long countVal, Consumer<String> LoggerCaller)
			throws SQLException {

		PreparedStatement pstmt = null;
		int execLine = 100;
		if (amount > 0) {
			execLine = amount;
		}

		boolean isAutoCommit = toConn.getAutoCommit();
		if (isAutoCommit) {
			toConn.setAutoCommit(false);
			LoggerCaller.accept("getAutoCommit = " + isAutoCommit);
		}
		try {

			toConn.setAutoCommit(false);
			String insertSql = InsertPreparedStatementDao.createPreparedStatementSql(tableName, fpo);

			logger.info(insertSql);
//			界面上输出sql
			LoggerCaller.accept(insertSql);

			pstmt = toConn.prepareStatement(insertSql);
			int idx = 0;

			int columnnums = fpo.size();
			// 循环查询结果
			while (rs.next()) {
				idx++;
				for (int i = 0; i < columnnums; i++) {
					Object obj = null;
					try {
						obj = rs.getObject(i + 1);
					} catch (Exception e) {
						e.printStackTrace();
					}

					pstmt.setObject(i + 1, obj);
				}
				pstmt.addBatch();
				if (idx % execLine == 0) {
					int[] count = pstmt.executeBatch();
					toConn.commit();
					int execCountLen = count.length;
					countVal -= execCountLen;
					logger.info("instert = " + execCountLen);
					LoggerCaller
							.accept(tableName + " exec Batch instert = " + execCountLen + "; residue : " + countVal);
				}

			}

			if (idx % execLine > 0) {
				int[] count = pstmt.executeBatch();
				toConn.commit();
				int execCountLen = count.length;
				countVal -= execCountLen;
				logger.info("instert = " + execCountLen);
				LoggerCaller.accept("Batch instert = " + execCountLen + "; residue : " + countVal);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			logger.debug(e1.getMessage());
			LoggerCaller.accept(e1.getMessage());
			if (isThrow)
				throw e1;
		} finally {
			if (pstmt != null)
				pstmt.close();

			boolean tmpisAutoCommit = toConn.getAutoCommit();
			if (isAutoCommit != tmpisAutoCommit) {
				toConn.setAutoCommit(isAutoCommit);
			}
		}
	}

	// 删表数据
	public static void cleanData(Connection toConn, String targetSchename, String tablename,
			Consumer<String> LoggerCaller) throws SQLException {
		String tableName = getTableName(targetSchename, tablename); // targetSchename + "." + tablename;
		LoggerCaller.accept("delete from " + tableName);
		DBTools.execDelTab(toConn, tableName);
	}

	// 执行
	public static void execListSQL(List<String> sqls, Connection tarConn, boolean isThrow,
			Consumer<String> LoggerCaller) throws Exception {

		for (String sql : sqls) {
			try {
				LoggerCaller.accept(sql);
				DBTools.execDDL(tarConn, sql);
			} catch (Exception e1) {
				e1.printStackTrace();
				if (isThrow) {
					throw e1;
				}
			}
		}
	}

	public static String getTableName(String sch, String tn) {
		String rs = tn;
		if (StrUtils.isNotNullOrEmpty(sch)) {
			rs = sch + "." + tn;
		}
		return rs;
	}
}
