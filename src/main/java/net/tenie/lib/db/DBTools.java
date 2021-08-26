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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.RsData;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.dao.SelectDao;
import net.tenie.fx.main.MainMyDB;
import net.tenie.lib.tools.StrUtils;

public class DBTools {

	private static Logger logger = LogManager.getLogger(DBTools.class);

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
	
	public static void execDDLNoErr(Connection conn, String sql)  {
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
	
	public static DbTableDatePo execSql(Connection conn, String sql, String sqltype, String content)
			throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		dpo.addField(sqltype + " Info");
		Statement sm = null;
		try {
			logger.debug("execSql = " + sql);
			sm = conn.createStatement();
			int i = sm.executeUpdate(sql);
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
	public static int execInsertReturnId( PreparedStatement pstmt) throws SQLException {
		 
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
	public static void execListSQL(List<String> sqls, Connection tarConn , boolean isThrow) throws Exception {
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
				if(isThrow ) throw e1;
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
			ResultSet	rs = call.executeQuery(); 
			
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
	public static List<String> conversionSqlFieldPo(List<SqlFieldPo> v) {
		List<String> rs = new ArrayList<String>();
		for (SqlFieldPo po : v) {
			String na = po.getColumnLabel().get();
			rs.add(na);
		}
		return rs;
	}

//	public static 
//	  select   *   from   INFODMS.TT_ACTIVITY_MODEL    where   1=2
 

}
