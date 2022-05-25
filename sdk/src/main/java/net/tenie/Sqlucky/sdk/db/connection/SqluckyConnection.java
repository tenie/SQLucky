package net.tenie.Sqlucky.sdk.db.connection;

import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.javalite.activejdbc.Base;

import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.DBTools;
import net.tenie.Sqlucky.sdk.utility.Dbinfo; 

public class SqluckyConnection {
	private static Connection conn;

	public synchronized static Connection getConn() {
		try {
			if (conn == null) {
				conn = createH2Conn();
				 
			} else if (conn.isClosed()) {
				conn = createH2Conn();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
  
		return conn;
	}

//	@SuppressWarnings("exports")
	public static DataSource getH2DataSource() {
		  UnpooledDataSource dataSource = null;
		  dataSource = new UnpooledDataSource("org.h2.Driver", jdbcURL(), ConfigVal.USER, ConfigVal.PASSWD);
		  
		  return  dataSource;
	}

	public static void openAvtiveJDBC() {
		Base.open(ConfigVal.H2_DIRVER, jdbcURL(),  ConfigVal.USER, ConfigVal.PASSWD);

	}
	
	
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
