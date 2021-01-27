package net.tenie.lib.db.h2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.io.FileUtils;

import net.tenie.lib.db.DBTools;
import net.tenie.lib.db.Dbinfo;

/*   @author tenie */
public class H2Db {
	private static Connection conn;

	public static Connection getConn() {
		try {
			if (conn == null) { 
				conn =  execConn() ;
				if (!tabExist(conn, "CONNECTION_INFO")) {
					SqlTextDao.createTab(conn);
				} 
			}else if( conn.isClosed()) {
				conn =  execConn() ;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	private  static Connection execConn() {
		String path = FileUtils.getUserDirectoryPath() + "/";
		Dbinfo dbinfo = new Dbinfo("org.h2.Driver", "jdbc:h2:" + path + "h2db", "sa", "xyz123qweasd");
		Connection connection = dbinfo.getconn();
		return connection;
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
	
	// 获取配置
	public static String getConfigVal(Connection conn, String key) {
		String val = "";
		val = SqlTextDao.readConfig(conn, key); 
		
		return val;
	}
	
	// SET配置
	public static void setConfigVal(Connection conn, String key, String val) {  
		SqlTextDao.saveConfig(conn, key, val);
	}
	
	// 执行更新脚本
	public static void updateAppSql(Connection conn) {
//		 setConfigVal(conn,  "UPDATE_SQL", "ALTER TABLE SQL_TEXT_SAVE ADD PARAGRAPH  INT(11);");
		 String  UPDATE_SQL = getConfigVal(conn , "UPDATE_SQL"); 
		 if(UPDATE_SQL != null &&  UPDATE_SQL.length() > 0) {
			 String[] sql = UPDATE_SQL.split(";");
			 for(String s : sql) {
				 try {
					if(s.length()> 0) {
						DBTools.execDDL(conn, s);
					}
					
				} catch (SQLException e) { 
					e.printStackTrace();
				}
			 }
			 setConfigVal(conn,  "UPDATE_SQL", "");
		 }
	}
}
