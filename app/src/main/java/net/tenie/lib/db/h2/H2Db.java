package net.tenie.lib.db.h2;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.FileUtils;

import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.DBTools;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.component.dataView.MyTabDataValue;
import net.tenie.fx.dao.InsertDao;
import net.tenie.fx.dao.SelectDao;

/**
 * 
 * @author tenie
 *
 */
public class H2Db {
	private static Connection conn;
	// 连接打开次数的计数, 只有当connTimes = 0 , 调用close, 才会真的关闭
	private static AtomicInteger connTimes = new AtomicInteger(0);
	
	private static String H2_DB_NAME  = "h2db";
	private static int  H2_DB_VERSION = 4;
	
	private static String USER = "sa";
	private static String PASSWD = "xyz123qweasd";
	
	// 使用阻塞队列, 串行获取: 连接, 和关闭连接 
//	private static BlockingQueue<Connection> bQueue=new ArrayBlockingQueue<>(1);
	public synchronized  static Connection getConn() {
		try {
			if (conn == null) {
				conn =  createH2Conn() ;
				// 第一次启动
				if (!tabExist(conn, "CONNECTION_INFO")) {
					SqlTextDao.createTab(conn);
					// 数据库迁移
					transferOldDbData();
				}else {// 之后的启动, 更新脚本
//					UpdateScript.execUpdate(conn);
					
				}
			}else if( conn.isClosed()) {
				conn =  createH2Conn() ;
			}
//			bQueue.put(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int v = connTimes.addAndGet(1);
		System.out.println("getConn = connIdx = "+ connTimes.get() + " v = " +v);
		
		return conn;
	} 
	
	public synchronized static void closeConn() {
		if (conn != null) {
			try { 
//				var tmp_conn = bQueue.take();
//				tmp_conn.close(); 			
				 
				int v = connTimes.addAndGet(-1);
				System.out.println("closeConncloseConncloseConn = connIdx = "+ connTimes.get());
				if(v <= 0) {
					conn.close(); 					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	
	private static String dbFilePath() {
		String dir = "/.sqlucky/";
//		if(isDev()) {
//			dir = "/.sqlucky_dev/";
//		}
		String path = FileUtils.getUserDirectoryPath() + dir;
		return path;
	}
	
	private static String getH2FilePath() {
		String path = dbFilePath() ;
		
		ConfigVal.H2_DB_FILE_NAME = path + H2_DB_NAME+H2_DB_VERSION;
		ConfigVal.H2_DB_FULL_FILE_NAME = ConfigVal.H2_DB_FILE_NAME+ ".mv.db";
		return ConfigVal.H2_DB_FILE_NAME;
	}
	
	private  static Connection createH2Conn() {
		Connection connection =	createH2Conn(getH2FilePath(), USER , PASSWD);
		return connection;
	}
	
	private  static Connection createH2Conn(String path, String user, String pw) {
		Dbinfo dbinfo = new Dbinfo("jdbc:h2:" + path, user, pw);
		Connection connection = dbinfo.getconn();
		return connection;
	}
	
	
	// 获取目录下的旧db文件, 从旧文件中找一个最新的
	private static String oldDbFiles(){
		String rs = "";
		String path = dbFilePath() ;
		File dir = new File(path);
		
		File[] files = dir.listFiles(name->{
				return name.getName().startsWith(H2_DB_NAME) && name.getName().endsWith(".mv.db");
			});
		if(files != null && files.length > 0) {
			long lastModifiedTime = 0;
			for(var fl : files) {
				String flName = fl.getName(); 
				if(!flName.startsWith(H2_DB_NAME+H2_DB_VERSION) ) {					
					long ltmp = fl.lastModified();
					if(ltmp > lastModifiedTime) {
						lastModifiedTime = ltmp;
						rs =  path + flName.substring(0, flName.indexOf(".mv.db")); 
					}
					System.out.println(rs);
				} 
			}
		}  
		return rs;
	}
	
	// 旧的数据 转移 到新的 表里
	private static void transferOldDbData() {
		String path = oldDbFiles();
		if (StrUtils.isNotNullOrEmpty(path)) {
			DBConnectorInfoPo connPo = new DBConnectorInfoPo("CONN_NAME",  
					"", // rd.getString("DRIVER"),
					"", // rd.getString("HOST"),
					"", // rd.getString("PORT"),
					USER, 
					PASSWD, 
					"VENDOR",  
					"SCHEMA",  
					"DB_NAME",  
					"jdbc:h2:" + path  ,
					false
			);
			SqluckyConnector cnor = new MyH2Connector(connPo);
			List<String> tableNames = new ArrayList<>();
			tableNames.add("CONNECTION_INFO");
			tableNames.add("SQL_TEXT_SAVE");
			tableNames.add("SCRIPT_ARCHIVE");
			tableNames.add("APP_CONFIG");

			for (int i = 0; i < tableNames.size(); i++) {
				String tableName = tableNames.get(i);
				String sql = "select   *   from  " + tableName;
				MyTabDataValue dvt = new MyTabDataValue();
				dvt.setDbConnection(cnor);
				dvt.setSqlStr(sql);
				dvt.setTabName(tableName);
				try {
					SelectDao.selectSql(sql, -1, dvt);
					var datas = dvt.getRawData();
					var fs = dvt.getColss();
					if (datas != null) {
						for (var data : datas) {
							InsertDao.execInsert(conn, tableName, data, fs);
						}
					}

				} catch (Exception e) {
					e.printStackTrace(); 
				}

			}
			cnor.closeConn();

		}
	}
	
	public static void main(String[] args) {
		oldDbFiles();
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
