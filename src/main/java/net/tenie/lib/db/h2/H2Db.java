package net.tenie.lib.db.h2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.io.FileUtils;
import net.tenie.lib.db.Dbinfo;

/*   @author tenie */
public class H2Db {
	private static Connection conn;

	public static Connection getConn() {
		try {
			if (conn == null || conn.isClosed()) {

				String path = FileUtils.getUserDirectoryPath() + "/";
				Dbinfo dbinfo = new Dbinfo("org.h2.Driver", "jdbc:h2:" + path + "h2db", "sa", "xyz123qweasd");
				conn = dbinfo.getconn();
				if (!tabExist(conn, "CONNECTION_INFO")) {
					SqlTextDao.createTab(conn);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void closeConn() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 检查表是否存在
	private static boolean tabExist(Connection conn, String tablename) {
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			ResultSet tablesResultSet = dmd.getTables(null, null, tablename, new String[] { "TABLE" });
			if (tablesResultSet.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

}
