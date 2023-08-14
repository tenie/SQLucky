package net.tenie.Sqlucky.sdk.db;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;

import net.tenie.Sqlucky.sdk.config.ConfigVal;

/**
 * 
 * @author tenie
 *
 */
public class SqluckyAppDB {

	// 连接打开次数的计数, 只有当connTimes = 0 , 调用close, 才会真的关闭
	private static AtomicInteger connTimes = new AtomicInteger(0);
	private static Connection conn;
	// 使用阻塞队列, 串行获取: 连接, 和关闭连接
//	private static BlockingQueue<Connection> bQueue=new ArrayBlockingQueue<>(1);

	// 获取应用本身的数据库链接
	public static Connection getConn() {
		try {
			if (conn == null) {
//				conn = createH2Conn();
				conn = createSqliteConn();
//				System.out.println("获取应用本身的数据库链接");
			} else if (conn.isClosed()) {
//				conn = createH2Conn();
				conn = createSqliteConn();
//				System.out.println("获取应用本身的数据库链接");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		int v = connTimes.addAndGet(1);

		return conn;
	}

	// 获取 应用数据库连接的SqluckyConnector对象
	public static SqluckyConnector getSqluckyConnector() {
		SqluckyConnector sqluckyConn = SqluckySqliteConnector.createTmpConnector(ConfigVal.USER, ConfigVal.PASSWD,
				sqliteJdbcURL());
		return sqluckyConn;
	}

	public static SqluckyConnector getSqluckyConnector(String user, String passwd, String jdbcUrl) {
		SqluckyConnector sqluckyConn = SqluckySqliteConnector.createTmpConnector(user, passwd, jdbcUrl);
		return sqluckyConn;
	}

	public static void closeSqluckyConnector(SqluckyConnector sqluckyConn) {
		sqluckyConn.closeConn();
	}

	public static Connection getConnNotAutoCommit() {
		Connection conn = createSqliteConn();
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void closeConnAndRollback(Connection conn) {
		try {
			conn.rollback();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeConnAndCommit(Connection conn) {
		try {
			conn.commit();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeConn(Connection conn) {
		if (conn != null) {
			try {
				Thread th = new Thread(() -> {
					try {
						Thread.sleep(4000);
						connTimes.addAndGet(-1);
						if (connTimes.get() <= 0) {
							conn.close();
						}

					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
				th.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// test mybites
	public static DataSource getSqliteDataSource() {
		UnpooledDataSource dataSource = null;
		dataSource = new UnpooledDataSource("", sqliteJdbcURL(), ConfigVal.USER, ConfigVal.PASSWD);

		return dataSource;
	}

	private static String getH2FilePath() {
		String path = DBTools.dbFilePath();
		ConfigVal.H2_DB_FILE_NAME = path + ConfigVal.H2_DB_NAME + ConfigVal.H2_DB_VERSION;
		ConfigVal.H2_DB_FULL_FILE_NAME = ConfigVal.H2_DB_FILE_NAME + ".mv.db";
		return ConfigVal.H2_DB_FILE_NAME;
	}

	public static String getSqliteFilePath() {
		String path = DBTools.dbFilePath();
		ConfigVal.H2_DB_FILE_NAME = path + ConfigVal.H2_DB_NAME + ConfigVal.H2_DB_VERSION + "_sqlite.db";
		ConfigVal.H2_DB_FULL_FILE_NAME = ConfigVal.H2_DB_FILE_NAME;
		// 如果db文件还不存在, 就说明时新的版本
		File dbfile = new File(ConfigVal.H2_DB_FILE_NAME);
		if (dbfile.exists() == false) {
			ConfigVal.IS_NEW_DB_VERSION = true;
		}
		return ConfigVal.H2_DB_FILE_NAME;
	}

	private synchronized static Connection createH2Conn() {
		Connection connection = createH2Conn(getH2FilePath(), ConfigVal.USER, ConfigVal.PASSWD);
		return connection;
	}

	private static Connection createH2Conn(String path, String user, String pw) {
		Dbinfo dbinfo = new Dbinfo("jdbc:h2:" + path, user, pw);
		Connection connection = dbinfo.getconn();
		return connection;
	}

	private synchronized static Connection createSqliteConn() {
		Connection connection = createSqliteConn(getSqliteFilePath(), ConfigVal.USER, ConfigVal.PASSWD);
		return connection;
	}

	private static Connection createSqliteConn(String path, String user, String pw) {
		Dbinfo dbinfo = new Dbinfo("jdbc:sqlite:" + path, user, pw);
		Connection connection = dbinfo.getconn();
		return connection;
	}

	private static String h2JdbcURL() {
		String val = "jdbc:h2:" + getH2FilePath();
		return val;
	}

	private static String sqliteJdbcURL() {
		String val = "jdbc:sqlite:" + getSqliteFilePath();
		return val;
	}

	// 给app的数据库插入数据
	public static void execDDL(String sql) {
		var conn = SqluckyAppDB.getConn();
		try {
			DBTools.execDDL(conn, sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

	}

	// 返回第一个字段的字符串值
	public static String selectOne(String sql) {
		var conn = SqluckyAppDB.getConn();
		String val = "";
		try {
			val = DBTools.selectOne(conn, sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
		return val;
	}

}
