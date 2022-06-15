package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;

import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.DBTools;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;

/**
 * 
 * @author tenie
 *
 */
public class SqluckyAppDB {
	
	// 连接打开次数的计数, 只有当connTimes = 0 , 调用close, 才会真的关闭
	private static AtomicInteger connTimes = new AtomicInteger(0);
//	private static Connection conn;
	// 使用阻塞队列, 串行获取: 连接, 和关闭连接 
//	private static BlockingQueue<Connection> bQueue=new ArrayBlockingQueue<>(1);
//	public  static Connection getConn() {
//		while(connTimes.get() != 0) {
//			CommonUtility.sleep(300);
//		}
//		try {
//			if (conn == null) {
//				conn = createH2Conn();
//			} else if (conn.isClosed()) {
//				conn = createH2Conn();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		int v = connTimes.addAndGet(1);
//		System.out.println("getConn = connIdx = " + connTimes.get() + " v = " + v);
//
//		return conn;
//	}
	public  static Connection getConn() {
		Connection conn = createH2Conn();
		return conn;
	}
	
	public  static Connection getConnNotAutoCommit() {
		Connection conn = createH2Conn();
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public  static void closeConn(Connection conn) {
		try { 
				conn.close(); 					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  static void closeConnAndRollback(Connection conn) {
		try { 
				conn.rollback();
				conn.close(); 					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  static void closeConnAndCommit(Connection conn) {
		try { 
				conn.commit();
				conn.close(); 					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public  static void closeConn() {
//		if (conn != null) {
//			try { 
//				int v = connTimes.addAndGet(-1);
//				System.out.println("closeConncloseConncloseConn = connIdx = "+ connTimes.get());
//				if(v <= 0) {
//					conn.close(); 					
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
 
	
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


//	@SuppressWarnings("exports")
	public static DataSource getH2DataSource() {
		  UnpooledDataSource dataSource = null;
		  dataSource = new UnpooledDataSource("org.h2.Driver", jdbcURL(), ConfigVal.USER, ConfigVal.PASSWD);
		  
		  return  dataSource;
	}

//	public static void openAvtiveJDBC() {
//		Base.open(ConfigVal.H2_DIRVER, jdbcURL(),  ConfigVal.USER, ConfigVal.PASSWD);
//
//	}
	
	
	private static String getH2FilePath() {
		String path = DBTools.dbFilePath(); 
		ConfigVal.H2_DB_FILE_NAME = path + ConfigVal.H2_DB_NAME + ConfigVal.H2_DB_VERSION;
		ConfigVal.H2_DB_FULL_FILE_NAME = ConfigVal.H2_DB_FILE_NAME + ".mv.db";
		return ConfigVal.H2_DB_FILE_NAME;
	}

	private static Connection createH2Conn() {
		Connection connection = createH2Conn(getH2FilePath(), ConfigVal.USER, ConfigVal.PASSWD);
		return connection;
	}

	private static Connection createH2Conn(String path, String user, String pw) {
		Dbinfo dbinfo = new Dbinfo("jdbc:h2:" + path, user, pw);
		Connection connection = dbinfo.getconn();
		return connection;
	}
	
	private static String jdbcURL() {
		String val = "jdbc:h2:" + getH2FilePath();
		return val;
	}

	
	
}
