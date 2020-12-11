package net.tenie.lib.db;

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
import net.tenie.lib.po.DbTableDatePo;
import net.tenie.lib.po.RsData;
import net.tenie.lib.po.SqlFieldPo;
import net.tenie.lib.tools.StrUtils;

public class DBTools {

	// 获取查询的结果, 返回字段名称的数据和 值的数据
	public static DbTableDatePo selectSql(Connection conn, String sql, int limit) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
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
				SqlFieldPo po = new SqlFieldPo();
				po.setColumnName(mdata.getColumnName(i));
				po.setColumnClassName(mdata.getColumnClassName(i));
				po.setColumnDisplaySize(mdata.getColumnDisplaySize(i));
				po.setColumnLabel(mdata.getColumnLabel(i));
				po.setColumnType(mdata.getColumnType(i));
				po.setColumnTypeName(mdata.getColumnTypeName(i));
				dpo.addField(po);
			}
			// 数据
			if (limit > 0) {
				execRs(columnnums, limit, rs, dpo);
			} else {
				execRs(columnnums, rs, dpo);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
		}
		return dpo;
	}

	public static DbTableDatePo deleteSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		int i = execDML(conn, sql);
		dpo.addField("Delete Info");
		dpo.addData("ok, delete:" + i);
		return dpo;
	}

	public static DbTableDatePo updateSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		int i = execDML(conn, sql);
		dpo.addField("Update Info");
		dpo.addData("ok, Update: " + i);
		return dpo;
	}

	public static DbTableDatePo insertSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		int i = execDML(conn, sql);
		dpo.addField("Insert Info");
		dpo.addData("ok, Insert: " + i);
		return dpo;
	}

	public static DbTableDatePo dropSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		dpo.addField("drop Info");
		dpo.addData("ok");
		return dpo;
	}

	public static DbTableDatePo alterSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		dpo.addField("Alter Info");
		dpo.addData("ok");
		return dpo;
	}

	public static DbTableDatePo createSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		dpo.addField("Create Info");
		dpo.addData("ok");
		return dpo;
	}

	public static DbTableDatePo otherSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		dpo.addField(" Info");
		dpo.addData("ok");
		return dpo;
	}

	/**
	 * DML需要commit. SELECT INSERT UPDATE DELETE MERGE CALL EXPLAIN PLAN LOCK TABLE
	 */
	public static int execDML(Connection conn, String delSQl) throws SQLException {
		Statement sm = null;
		int i = 0;
		try {
			sm = conn.createStatement();
			i = sm.executeUpdate(delSQl);
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

	public static DbTableDatePo execSql(Connection conn, String delSQl, String sqltype, String content)
			throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		dpo.addField(sqltype + " Info");
		Statement sm = null;
		try {
			sm = conn.createStatement();
			int i = sm.executeUpdate(delSQl);
			dpo.addData(content + i);
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
	public static void execListSQL(List<String> sqls, Connection tarConn) {
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
			}
		}
	}
	
	

	// 调用函数
	public static void CallProcedure(Connection conn, String sql) {
		CallableStatement call = null;
		try {
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

// drop table 
	public static void dropTable(Connection conn, String tabName) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			String Sql = " DROP TABLE " + tabName;
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
	public static List<String> conversionSqlFieldPo(List<SqlFieldPo> v) {
		List<String> rs = new ArrayList<String>();
		for (SqlFieldPo po : v) {
			String na = po.getColumnLabel();
			rs.add(na);
		}
		return rs;
	}

	private static void execRs(int columnnums, ResultSet rs, DbTableDatePo dpo) throws SQLException {
		while (rs.next()) {
			List<String> vals = new ArrayList<String>();
			for (int i = 1; i <= columnnums; i++) {
				String val = rs.getString(i);
				if (val == null) {
					val = "(null)";
				}
				vals.add(val);
			}
			dpo.addData(vals);
		}
	}

	private static void execRs(int columnnums, int limit, ResultSet rs, DbTableDatePo dpo) throws SQLException {
		int idx = 1;
		while (rs.next()) {
			List<String> vals = new ArrayList<String>();
			for (int i = 1; i <= columnnums; i++) {
				String val = rs.getString(i);
				if (val == null) {
					val = "<null>";
				}
				vals.add(val);
			}
			dpo.addData(vals);
			if (idx == limit)
				break;
			idx++;
		}
	}

}
