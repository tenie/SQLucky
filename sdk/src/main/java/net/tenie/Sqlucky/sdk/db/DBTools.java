package net.tenie.Sqlucky.sdk.db;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.RsData;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 
 * @author tenie
 *
 */
public class DBTools {

	private static Logger logger = LogManager.getLogger(DBTools.class);

	public static String dbFilePath() {
		String dir = "/.sqlucky/";
		if (CommonUtils.isDev()) {
			dir = "/.sqlucky_dev/";
		}
		String path = FileUtils.getUserDirectoryPath() + dir;
		File file = new File(path);
		if (file.exists() == false) {
			file.mkdir();
		}
		return path;
	}

	public static DbTableDatePo deleteSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		int i = execDML(conn, sql);
		SheetFieldPo field = dpo.addField("Delete Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok, delete:" + i, field);
		return dpo;
	}

	public static DbTableDatePo updateSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		int i = execDML(conn, sql);
		var fp = dpo.addField("Update Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok, Update: " + i, fp);
		return dpo;
	}

	public static DbTableDatePo insertSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		int i = execDML(conn, sql);
		var fp = dpo.addField("Insert Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok, Insert: " + i, fp);
		return dpo;
	}

	public static DbTableDatePo dropSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		var fp = dpo.addField("drop Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok", fp);
		return dpo;
	}

	public static DbTableDatePo alterSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		var fp = dpo.addField("Alter Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok", fp);
		return dpo;
	}

	public static DbTableDatePo createSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		var fp = dpo.addField("Create Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok", fp);
		return dpo;
	}

	public static DbTableDatePo otherSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		var fp = dpo.addField(" Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok", fp);
		return dpo;
	}

	/**
	 * DML需要commit. SELECT INSERT UPDATE DELETE MERGE CALL EXPLAIN PLAN LOCK TABLE
	 */
	public static int execDML(Connection conn, String sql) throws SQLException {
		Statement sm = null;
		int i = 0;
		try {
			logger.debug("execDML = " + sql);
			sm = conn.createStatement();
			i = sm.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (sm != null)
				sm.close();
		}
		return i;
	}

	/**
	 * DDL不需要commit. CREATE ALTER DROP TRUNCATE COMMENT RENAME DCL（Data Control
	 * Language）数据库控制语言 授权，角色控制等 GRANT 授权 REVOKE 取消授权 TCL（Transaction Control
	 * Language）事务控制语言 SAVEPOINT 设置保存点 ROLLBACK 回滚 SET TRANSACTION
	 */
	public static void execDDL(Connection conn, String sql) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			logger.debug("execDDL = " + sql);
			pstmt = conn.prepareStatement(sql);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
	}

	public static void execDDLNoErr(Connection conn, String sql) {
		try {
			execDDL(conn, sql);
		} catch (Exception e) {
		}
	}

	// 返回第一个字段的字符串值
	public static String selectOne(Connection conn, String sql) {
		ResultSet rs = null;
		String str = "";
		try {
			rs = conn.createStatement().executeQuery(sql);
			if (rs.next()) {
				str = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return str;
	}

	public static Long selectOneLongVal(Connection conn, String sql) {
		ResultSet rs = null;
		Long val = null;
		try {
			rs = conn.createStatement().executeQuery(sql);
			if (rs.next()) {
				val = rs.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return val;
	}

	public static DbTableDatePo execSql(Connection conn, String sql, String sqltype, String content)
			throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		var fp = dpo.addField(sqltype + " Info");
		var row = dpo.addRow();
		Statement sm = null;
		try {
			logger.debug("execSql = " + sql);
			sm = conn.createStatement();
			int i = sm.executeUpdate(sql);
			dpo.addData(row, content + i, fp);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (sm != null)
				sm.close();
		}
		return dpo;
	}

	// 清空表数据
	public static void execDelTab(Connection conn, String tabName) throws SQLException {
		Statement sm = null;
		try {
			String delSQl = "delete from " + tabName;
			logger.debug("execDelTab = " + delSQl);
			sm = conn.createStatement();
			sm.executeUpdate(delSQl);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (sm != null)
				sm.close();
		}
	}

	// 执行插入返回id
	public static int execInsertReturnId(Connection conn, String sql) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int id = -1;
		try {
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.execute();
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (rs != null)
				rs.close();
		}
		return id;
	}

	public static int execInsertReturnId(PreparedStatement pstmt) throws SQLException {

		ResultSet rs = null;
		int id = -1;
		try {
			pstmt.execute();
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null)
				rs.close();
		}
		return id;
	}

	/**
	 * 批量执行
	 */
	public static void execSQL(Connection conn, List<String> ls) throws SQLException {
		Statement sm = null;
		PreparedStatement ps = null;

		try {
			for (String sql : ls) {
				String sqlA[] = sql.split(";");
				for (int i = 0; i < sqlA.length; i++) {
					String subsql = sqlA[i].trim();
					if (subsql.length() > 0) {
						sm = conn.createStatement();
						sm.executeUpdate(subsql);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (ps != null)
				ps.close();
		}
	}

	// 执行
	public static void execListSQL(List<String> sqls, Connection tarConn, boolean isThrow) throws Exception {
		// 执行sql
		for (String sql : sqls) {
			try {
				PreparedStatement pstmt = null;
				try {
					pstmt = tarConn.prepareStatement(sql);
					pstmt.execute();
				} catch (SQLException e) {
					e.printStackTrace();
					throw e;
				} finally {
					if (pstmt != null)
						pstmt.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				if (isThrow)
					throw e1;
			}
		}
	}

	// 调用过程
	public static void CallProcedure(Connection conn, String sql) {
		CallableStatement call = null;
		try {
			logger.debug("CallProcedure = " + sql);
			call = conn.prepareCall(sql);
			call.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				call.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

//	调用函数
	public static void CallFunction(Connection conn, String sql) {
		CallableStatement call = null;
		try {
			logger.debug("CallProcedure = " + sql);
			call = conn.prepareCall(sql);
			ResultSet rs = call.executeQuery();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				call.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

// drop table 
	public static void dropTable(Connection conn, String tabName) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			String Sql = " DROP TABLE " + tabName;
			logger.debug("dropTable() = " + Sql);
			pstmt = conn.prepareStatement(Sql);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
	}

	public static List<RsData> selectSql(Connection conn, String selectsql) throws SQLException {
		List<RsData> ls = new ArrayList<RsData>();
		ResultSet rs = null;
		ResultSetMetaData meta;
		try {
			rs = conn.createStatement().executeQuery(selectsql);
			meta = rs.getMetaData(); // 获取行的元数据
			int count = meta.getColumnCount(); // 字段个数：
			while (rs.next()) {
				RsData rsf = new RsData();
				for (int i = 1; i <= count; i++) {
					Object v = rs.getObject(i);
					String cn = meta.getColumnName(i);
					rsf.put(cn, v);
				}
				ls.add(rsf);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
		}
		return ls;
	}

	public static List<RsData> selectTab(Connection conn, String tableName, String where) throws SQLException {
		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
		String sql = "select * from " + tableName + " where   ";
		if (!StrUtils.isNullOrEmpty(where)) {
			sql += where;
		}
		return selectSql(conn, sql);
	}

	// List<SqlFieldPo> 转为 List<String>
	public static List<String> conversionSqlFieldPo(List<SheetFieldPo> v) {
		List<String> rs = new ArrayList<String>();
		for (SheetFieldPo po : v) {
			String na = po.getColumnLabel().get();
			rs.add(na);
		}
		return rs;
	}

	// 简单的sql 转为ResultSet
	public static ResultSet sqlToResultSet(Connection conn, String sql) {
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			pstate = conn.prepareStatement(sql);
			rs = pstate.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	// 简单的sql 查询, 用于更新之前表的数据
	public static ResultSetPo simpleSelect(Connection conn, String sql, ObservableList<SheetFieldPo> fields,
			SqluckyConnector dpo) throws SQLException {
		ResultSet rs = sqlToResultSet(conn, sql);
		ResultSetPo setPo = null;
		if (rs != null) {
			setPo = SelectInfoTableDao.selectTableData(rs, fields, dpo);
		}

		return setPo;
	}

}
