package net.tenie.lib.db.h2;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.DBTools;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;

/**
 * 
 * @author tenie
 *
 */
public class H2Db {
	private static Connection conn;
	// 连接打开次数的计数, 只有当connTimes = 0 , 调用close, 才会真的关闭
//	private static AtomicInteger connTimes = new AtomicInteger(0);
	
	// 使用阻塞队列, 串行获取: 连接, 和关闭连接
	private static BlockingQueue<Connection> bQueue=new ArrayBlockingQueue<>(1);
	public   static Connection getConn() {
		try {
			if (conn == null) {
				conn =  execConn() ;
				// 第一次启动
				if (!tabExist(conn, "CONNECTION_INFO")) {
					SqlTextDao.createTab(conn);
				}else {// 之后的启动, 更新脚本
//					UpdateScript.execUpdate(conn);
					
				}
			}else if( conn.isClosed()) {
				conn =  execConn() ;
			}
			bQueue.put(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		int v = connTimes.addAndGet(1);
//		System.out.println("getConn = connIdx = "+ connTimes.get() + " v = " +v);
		
		return conn;
	}
	
	public static boolean isDev() {
		String  modulePath = System.getProperty("jdk.module.path");
		String strSplit = ":";
		if(CommonUtility.isWinOS()) {
			strSplit = ";";
		}
		String[] ls  = modulePath.split( strSplit );
		if(ls.length > 1) {
			return true;
		}
		return false;
	}
	
	private  static Connection execConn() {
		String dir = "/.sqlucky/";
		if(isDev()) {
			dir = "/.sqlucky_dev/";
		}
		String path = FileUtils.getUserDirectoryPath() + dir;
		Dbinfo dbinfo = new Dbinfo("jdbc:h2:" + path + "h2db3", "sa", "xyz123qweasd");
		Connection connection = dbinfo.getconn();
		return connection;
	}

	public  static void closeConn() {
		if (conn != null) {
			try { 
				var tmp_conn = bQueue.take();
				tmp_conn.close(); 			
				 
//				int v = connTimes.addAndGet(-1);
//				System.out.println("closeConncloseConncloseConn = connIdx = "+ connTimes.get());
//				if(v <= 0) {
//					conn.close(); 					
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static List<String> updateSQL(){
		List<String> ls = new ArrayList<>(); 
		String path = FileUtils.getUserDirectoryPath() + "/.sqlucky/updatesql.txt";
		File fl = new File(path);
		if(fl.exists()) {
			try {
				ls = FileUtils.readLines(fl, "UTF-8");
				FileUtils.forceDelete(fl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ls;
	}
	// 检查表是否存在
	public static boolean tabExist(Connection conn, String tablename) {
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
				for (String s : sql) {
					try {
						if (s.length() > 0) {
							DBTools.execDDL(conn, s);
						}

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			 setConfigVal(conn,  "UPDATE_SQL", "");
		 }
		 
		List<String> ls =  updateSQL();
		for(String sql : ls) {
			try {
				if (sql.length() > 0) {
					DBTools.execDDL(conn, sql);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
