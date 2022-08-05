package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.po.DbTableDatePo;

/*   @author tenie */
public class DmlDdlDao {

	private static Logger logger = LogManager.getLogger(DmlDdlDao.class);
	public static DbTableDatePo deleteSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		int i = execDML(conn, sql);
		var fp = dpo.addField("Delete Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok, delete:" + i, fp);
		return dpo;
	}

	public static String deleteSql2(Connection conn, String sql) throws SQLException {
		int i = execDML(conn, sql);
		return "ok, delete:" + i;
	}

	public static DbTableDatePo updateSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		int i = execDML(conn, sql);
		var fp = dpo.addField("Update Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok, Update: " + i, fp);
		return dpo;
	}

	public static String updateSql2(Connection conn, String sql) throws SQLException {
		int i = execDML(conn, sql);
		return "ok, Update: " + i;
	}

	public static DbTableDatePo insertSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		int i = execDML(conn, sql);
		var fp = dpo.addField("Insert Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok, Insert: " + i, fp);
		return dpo;
	}

	public static String insertSql2(Connection conn, String sql) throws SQLException {
		int i = execDML(conn, sql);
		return "ok, Insert: " + i;
	}

	public static DbTableDatePo dropSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		var fp = dpo.addField("drop Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok", fp);
		return dpo;
	}

	public static String dropSql2(Connection conn, String sql) throws SQLException {
		execDDL(conn, sql);
		return "ok";
	}

	public static DbTableDatePo alterSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		var fp = dpo.addField("Alter Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok", fp);
		return dpo;
	}

	public static String alterSql2(Connection conn, String sql) throws SQLException {
		execDDL(conn, sql);
		return "ok";
	}

	public static DbTableDatePo createSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		execDDL(conn, sql);
		var fp = dpo.addField("Create Info");
		var row = dpo.addRow();
		dpo.addData(row, "ok", fp);
		return dpo;
	}

	public static String createSql2(Connection conn, String sql) throws SQLException {
		execDDL(conn, sql);
		return "ok";
	}

	public static DbTableDatePo otherSql(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		var row = dpo.addRow();
		execDDL(conn, sql);
		var fp = dpo.addField(" Info");
		dpo.addData(row, "ok", fp);
		return dpo;
	}

	public static String otherSql2(Connection conn, String sql) throws SQLException {
		execDDL(conn, sql);
		return "ok";
	}
	
	public static String callFunc(Connection conn, String sql) throws SQLException {
		execDDL(conn, sql);
		return "ok";
	}
	

	public static int execDML(Connection conn, String delSQl) throws SQLException {
		Statement sm = null;
		int i = 0;
		try {
			sm = conn.createStatement();
			logger.info("执行   " + delSQl);
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

	public static void execDDL(Connection conn, String sql) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			logger.info("执行   " + sql);
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
}
