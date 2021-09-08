package net.tenie.lib.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.jdbcx.JdbcConnectionPool;

/*   @author tenie */
public class ConnectionPool {

	// 单独使用的单例模式,需要类自己
	private static ConnectionPool cp = null;
	private JdbcConnectionPool jdbcCP = null;

	// 构造函数
	private ConnectionPool(String url, String user, String pw) {
		jdbcCP = JdbcConnectionPool.create(url, user, pw);
		jdbcCP.setMaxConnections(50);
	}

	// 单独使用的单例模式
	private static ConnectionPool getInstance(String pt, String us, String pw) {
		if (cp == null) {
			cp = new ConnectionPool(pt, us, pw);
		}
		return cp;
	}

	public Connection getConnection() throws SQLException {
		return jdbcCP.getConnection();
	}

	// 直接获取连接
	public static Connection getDirectConn(String pt, String us, String pw) throws SQLException {
		if (cp == null) {
			getInstance(pt, us, pw);
		}

		return cp.getConnection();
	}

	/**
	 * 释放资源
	 * 
	 * @param conn
	 * @param stmt
	 * @param rs
	 * @throws SQLException
	 */
	public static void releaseConnection(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
		if (stmt != null) {
			stmt.close();
		}
		if (conn != null) {
			conn.close();
		}
	}

}