package net.tenie.lib.db.h2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBTools;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.controller.TransferDataUtils;

/**
 * 
 * @author tenie
 *
 */
public class AppDao {
	private static Logger logger = LogManager.getLogger(AppDao.class);
	public static final String CONNECTION_INFO = "CREATE TABLE `CONNECTION_INFO` (\n" +
//			"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" +  
	// sqlite 自增 AUTOINCREMENT
			"  `ID` INTEGER PRIMARY KEY AUTOINCREMENT,\n" + "  `CONN_NAME` VARCHAR(1000)   NOT NULL,\n"
			+ "  `USER` VARCHAR(1000)   NOT NULL,\n" + "  `PASS_WORD` VARCHAR(1000)   NOT NULL,\n"
			+ "  `HOST` VARCHAR(200) ,\n" + "  `PORT` VARCHAR(10) , \n" + "  `JDBC_URL` VARCHAR(500) , \n"
			+ "  `DRIVER` VARCHAR(200) ,\n" + "  `VENDOR` VARCHAR(100)  ,\n" + "  `SCHEMA` VARCHAR(200)  ,\n"
			+ "  `DB_NAME` VARCHAR(200)  ,\n" + "  `COMMENT` VARCHAR(200) DEFAULT NULL,\n"
			+ "  `AUTO_CONNECT` INT(1) DEFAULT '0',\n" + "  `CREATED_AT` DATETIME DEFAULT NULL,\n"
			+ "  `UPDATED_AT` DATETIME DEFAULT NULL,\n" + "  `RECORD_VERSION` INT(11) DEFAULT '0',\n"
			+ "  `ORDER_TAG` DOUBLE(11) DEFAULT '99'" +
			// sqlite 不能建表的时候创建联合组件
//			+ ",\n" 
//			"  PRIMARY KEY (`ID`,`CONN_NAME`)\n" + 
			") ";

	public static final String SCRIPT_ARCHIVE = "CREATE TABLE `SCRIPT_ARCHIVE` (\n" +
//			"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `ID` INTEGER PRIMARY KEY AUTOINCREMENT,\n" + "  `TITLE_NAME` VARCHAR(1000)   NOT NULL,\n"
			+ "  `SQL_TEXT` CLOB, \n" + "  `FILE_NAME` VARCHAR(1000) ,\n" + "  `ENCODE` VARCHAR(100) ,\n"
			+ "  `PARAGRAPH` INT(11) DEFAULT '0' ,\n" + "  `IS_ACTIVATE` INT(1) DEFAULT '0' ,\n" + // 是否激活 1:表示激活状态
			"  `OPEN_STATUS` INT(1) DEFAULT '0' \n" + // 打开状态 1: 打开, 0:未打开

//			+ ",\n" 
//			"  PRIMARY KEY ( `ID`, `TITLE_NAME`)\n" + 
			") ";

	public static final String APP_CONFIG = "CREATE TABLE `APP_CONFIG` (\n" + "  `NAME` VARCHAR(1000)   NOT NULL,\n"
			+ "  `VAL`  VARCHAR(1000), \n" + "  PRIMARY KEY (`NAME`)\n" + ") ";

	public static final String DATA_MODEL_INFO = "CREATE TABLE `DATA_MODEL_INFO` (\n"
			+ "  `ID` INTEGER PRIMARY KEY AUTOINCREMENT,\n" + "  `NAME` VARCHAR(200)   NOT NULL,\n"
			+ "  `DESCRIBE` VARCHAR(300)  , \n" + "  `AVATAR` VARCHAR(200)   ,\n" + "  `VERSION` VARCHAR(100)   ,\n" +

			"  `CREATEDTIME` VARCHAR(100)    ,\n" + "  `UPDATEDTIME` VARCHAR(100)    ,\n" +

			"  `ORDER_TAG` INT(11) DEFAULT '99'" +
//			+ ",\n" +
//			"  PRIMARY KEY ( `ID`, `NAME`)\n" + 
			") ";

	public static final String DATA_MODEL_TABLE = "CREATE TABLE `DATA_MODEL_TABLE` (\n"
			+ "  `ITEM_ID` INTEGER PRIMARY KEY AUTOINCREMENT,\n" + "  `MODEL_ID` INT(11)   ,\n"
			+ "  `ID` VARCHAR(100) ,\n" + "  `DEF_KEY` VARCHAR(200)   NOT NULL,\n" + "  `DEF_NAME` VARCHAR(300)  , \n"
			+ "  `COMMENT` VARCHAR(1000)  , \n" +

			"  `CREATED_TIME` DATETIME  ,\n" + "  `UPDATED_TIME` DATETIME  " +
//			+ ",\n" + 
//			"  PRIMARY KEY ( `ITEM_ID`, `DEF_KEY`)\n" + 
			") ";
	public static final String DATA_MODEL_TABLE_FIELDS = "CREATE TABLE `DATA_MODEL_TABLE_FIELDS` (\n"
			+ "  `ITEM_ID` INTEGER PRIMARY KEY AUTOINCREMENT,\n" + "  `TABLE_ID` INT(11) NOT NULL ,\n"
			+ "  `MODEL_ID` INT(11)   ,\n" + "  `ID` VARCHAR(100)  , \n" + "  `ROW_NO` INT(11) ,\n"
			+ "  `DEF_KEY` VARCHAR(200)    ,   \n" + // 字段名称
			"  `DEF_NAME` VARCHAR(300)  , \n" + "  `COMMENT` VARCHAR(1000)  , \n" +

			"  `DOMAIN` VARCHAR(200)  , \n" + "  `TYPE` VARCHAR(200)  , \n" + "  `LEN` INT(11) ,\n"
			+ "  `SCALE` VARCHAR(100)  , \n" +

			"  `PRIMARY_KEY` VARCHAR(10) ,\n" + "  `NOT_NULL` VARCHAR(10) ,\n" + "  `AUTO_INCREMENT` VARCHAR(10) ,\n"
			+ "  `DEFAULT_VALUE` VARCHAR(500)  , \n" + "  `HIDE_IN_GRAPH` VARCHAR(10) ,\n" +

			"  `TYPE_FULL_NAME` VARCHAR(500)  , \n" + "  `PRIMARY_KEY_NAME` VARCHAR(500)  , \n"
			+ "  `NOT_NULL_NAME` VARCHAR(500)  , \n" + "  `AUTO_INCREMENT_NAME` VARCHAR(500)  , \n"
			+ "  `REF_DICT` VARCHAR(500)  , \n" +

			"  `CREATED_TIME` DATETIME ,\n" + "  `UPDATED_TIME` DATETIME " +
//			+ ",\n" + 
//			"  PRIMARY KEY ( `ITEM_ID`, `TABLE_ID`,`DEF_KEY`)\n" + 
			") ";

	public static final String PLUGIN_INFO = "CREATE TABLE `PLUGIN_INFO` (\n" +
//			"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" +
			"  `ID` INTEGER PRIMARY KEY AUTOINCREMENT, \n" + "  `PLUGIN_NAME` VARCHAR(200)    ,   \n"
			+ "  `PLUGIN_CODE` VARCHAR(200)    ,   \n" + "  `PLUGIN_DESCRIBE` VARCHAR(1000)  , \n"
			+ "  `COMMENT` VARCHAR(1000)  , \n" + "  `DOWNLOAD_STATUS` INT(1) ,\n" + // 下载状态, 0:未安装, 1: 以安装
			"  `RELOAD_STATUS` INT(1) DEFAULT '1',\n" + // 是否需要加载, 0: 不加载, 1: 加载
			"  `VERSION` VARCHAR(30)    , " + // 版本

			"  `CREATED_TIME` DATETIME ,\n" + "  `UPDATED_TIME` DATETIME " +
//			",\n" + 
//			"  PRIMARY KEY ( `ID`, `PLUGIN_NAME`)\n" + 
			") ";

	public static final String SQLUCKY_USER = "CREATE TABLE `SQLUCKY_USER` (\n"
			+ "  `ID` INTEGER PRIMARY KEY AUTOINCREMENT, \n" + "  `USER_NAME`  VARCHAR(300)   ,   \n"
			+ "  `EMAIL` VARCHAR(300)    ,   \n" + "  `PASSWORD` VARCHAR(300)  , \n" +

			"  `CREATED_TIME` DATETIME ,\n" + "  `UPDATED_TIME` DATETIME " + ") ";

	public static final String KEYS_BINDING = "CREATE TABLE `KEYS_BINDING` (\n"
			+ "  `ID` INTEGER PRIMARY KEY AUTOINCREMENT, \n" + "  `ACTION_NAME`  VARCHAR(300)   ,   \n"
			+ "  `BINDING` VARCHAR(300)    ,   \n" + "  `CODE` VARCHAR(300)  , \n" +

			"  `CREATED_TIME` DATETIME ,\n" + "  `UPDATED_TIME` DATETIME " + ") ";

	public static String readSqlFile(String path) {
		String sql = "";
		try {
			URL url = AppDao.class.getResource(path);
			InputStream is = url.openStream();

			sql = IOUtils.toString(is, StandardCharsets.UTF_8.name());
			logger.debug(" sql = " + sql.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sql;
	}

	public static void execSqlFileString(Connection conn, String sql) {
		if (StrUtils.isNotNullOrEmpty(sql)) {
			String[] arr = sql.split(";");
			for (int i = 0; i < arr.length; i++) {
				String createSQL = arr[i];
				createSQL = createSQL.trim();
				if (StrUtils.isNotNullOrEmpty(createSQL)) {
					logger.debug(createSQL);
					DBTools.execDDLNoErr(conn, createSQL);
				}

			}
		}
	}

	public static String macKeyChange(String sqlStr) {

		if (sqlStr.contains("Ctrl ")) {
			sqlStr = sqlStr.replaceAll("Ctrl ", "⌘ ");
		}
		if (sqlStr.contains("Alt ")) {
			sqlStr = sqlStr.replace("Alt ", "⌥ ");
		}

		if (sqlStr.contains("Shift ")) {
			sqlStr = sqlStr.replace("Shift ", "⇧ ");
		}

		return sqlStr;
	}

	// 建表
	public static void createTab(Connection conn) {
		try {
			String sql = readSqlFile("/db/app.sql");
			execSqlFileString(conn, sql.trim());
			sql = readSqlFile("/db/keysBinding.sql");
			if (CommonUtils.isMacOS()) {
				sql = macKeyChange(sql);
			}
			execSqlFileString(conn, sql);

//
//			DBTools.execDDLNoErr(conn, CONNECTION_INFO);
//			DBTools.execDDLNoErr(conn, SCRIPT_ARCHIVE);
//			DBTools.execDDLNoErr(conn, APP_CONFIG);
//
//			DBTools.execDDLNoErr(conn, DATA_MODEL_INFO);
//			DBTools.execDDLNoErr(conn, DATA_MODEL_TABLE);
//			DBTools.execDDLNoErr(conn, DATA_MODEL_TABLE_FIELDS);
//			DBTools.execDDLNoErr(conn, PLUGIN_INFO);
//			DBTools.execDDLNoErr(conn, SQLUCKY_USER);
//			DBTools.execDDLNoErr(conn, KEYS_BINDING);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DocumentPo scriptArchive(Connection conn, String title, String txt, String filename, String encode,
			int paragraph) {
		String sql = "insert into SCRIPT_ARCHIVE (TITLE_NAME, SQL_TEXT, FILE_NAME, ENCODE, PARAGRAPH) values ( ? , ?, ?, ?, ?)";
		PreparedStatement sm = null;
		Integer id = -1;
		try {
			sm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			sm.setString(1, title);
			sm.setString(2, txt);
			sm.setString(3, filename);
			sm.setString(4, encode);
			sm.setInt(5, paragraph);

			id = DBTools.execInsertReturnId(sm);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (sm != null)
				try {
					sm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		DocumentPo po = new DocumentPo();
		po.setId(id);
		po.setTitle(title);
		po.setFileFullName(filename);
		po.setEncode(encode);
		po.setParagraph(paragraph);
		po.setText(txt);

		return po;
	}

	public static DocumentPo scriptArchive(String title, String txt, String filename, String encode, int paragraph) {
		var conn = SqluckyAppDB.getConn();
		DocumentPo po = null;
		try {
			po = scriptArchive(SqluckyAppDB.getConn(), title, txt, filename, encode, paragraph);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
		if (po == null) {
			po = new DocumentPo();
		}
		return po;
	}

	// TODO
	public static void updateScriptArchive(Connection conn, DocumentPo po) {
		PreparedStatement sm = null;
		String sql = "update SCRIPT_ARCHIVE set TITLE_NAME = ?, "
				+ " SQL_TEXT = ?, FILE_NAME = ? , ENCODE = ?, PARAGRAPH = ? , OPEN_STATUS = ?, IS_ACTIVATE = ? where id = ?";
		try {
			sm = conn.prepareStatement(sql);
			sm.setString(1, po.getTitle());
			sm.setString(2, po.getText());
			sm.setString(3, po.getFileFullName());
			sm.setString(4, po.getEncode());
			sm.setInt(5, po.getParagraph());
			sm.setInt(6, po.getOpenStatus());
			sm.setInt(7, po.getIsActivate());
			sm.setInt(8, po.getId());

			sm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (sm != null)
				try {
					sm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

	public static void deleteScriptArchive(Connection conn, DocumentPo po) {
		PreparedStatement sm = null;
		String sql = "delete from  SCRIPT_ARCHIVE  where id = ?";
		try {
			sm = conn.prepareStatement(sql);
			sm.setInt(1, po.getId());

			sm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (sm != null)
				try {
					sm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}

	public static String readConfig(Connection conn, String name) {
		String sql = "select   VAL   from   APP_CONFIG   where name = '" + name + "' ";
		String vals = DBTools.selectOne(conn, sql);
		return vals;
	}

	public static void deleteConfigKey(Connection conn, String key) {
		try {
			DBTools.execDDL(conn, "DELETE from APP_CONFIG where name = '" + key + "' ");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void saveConfig(Connection conn, String key, String val) {
		deleteConfigKey(conn, key);
		String sql = "insert into APP_CONFIG (NAME, VAL) values ( '" + key + "' , '" + val + "' )";
		try {
			DBTools.execDML(conn, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void delScriptPo(Integer id) {
		String sql = "delete   from   SCRIPT_ARCHIVE  where id = " + id;
		var conn = SqluckyAppDB.getConn();
		try {
			DBTools.execDDL(conn, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	public static List<DocumentPo> readScriptPo(Connection conn) {
		String sql = "select   *   from   SCRIPT_ARCHIVE ";
		List<DocumentPo> vals = new ArrayList<>();

		Statement sm = null;
		ResultSet rs = null;
		try {
			sm = conn.createStatement();
			logger.info("执行   " + sql);
			rs = sm.executeQuery(sql);
			while (rs.next()) {
				DocumentPo po = new DocumentPo();
				po.setId(rs.getInt("ID"));
				po.setTitle(rs.getString("TITLE_NAME"));
				po.setText(rs.getString("SQL_TEXT"));
				po.setFileFullName(rs.getString("FILE_NAME"));
				po.setEncode(rs.getString("ENCODE"));
				po.setParagraph(rs.getInt("PARAGRAPH"));
				po.setOpenStatus(rs.getInt("OPEN_STATUS"));
				po.setIsActivate(rs.getInt("IS_ACTIVATE"));
				vals.add(po);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (sm != null)
					sm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return vals;
	}

	public static boolean testDbTableExists(Connection conn) {
		// 第一次启动
		if (!tabExist(conn, "KEYS_BINDING")) {
			AppDao.createTab(conn);
			return false;

		} else {// 之后的启动, 更新脚本
//			UpdateScript.execUpdate(conn);

		}
		return true;
	}

//	public static void testDbTableExists() {
//		Connection conn = SqluckyAppDB.getConn();
//		try {
//			testDbTableExists(conn);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			SqluckyAppDB.closeConn(conn);
//			SQLucky.beginInit = true;
//		}
//	}

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

	public static void printLog(String val) {
		logger.debug(val);
	}

	// 旧的数据 转移 到新的 表里
	public static void transferOldDbData(File path) throws Exception {

		SqluckyConnector sqluckyConn = null;
		Connection conn = null;
		try {
			conn = SqluckyAppDB.getConn();
			sqluckyConn = SqluckyAppDB.getSqluckyConnector(ConfigVal.USER, ConfigVal.PASSWD,
					"jdbc:sqlite:" + path.getAbsolutePath());
			List<String> tableNames = new ArrayList<>();
			tableNames.add("CONNECTION_INFO");
//			tableNames.add("SQL_TEXT_SAVE");
			tableNames.add("SCRIPT_ARCHIVE");
			tableNames.add("APP_CONFIG");

			tableNames.add("DATA_MODEL_INFO");
			tableNames.add("DATA_MODEL_TABLE");
			tableNames.add("DATA_MODEL_TABLE_FIELDS");
			tableNames.add("PLUGIN_INFO");
			tableNames.add("SQLUCKY_USER");
			for (int i = 0; i < tableNames.size(); i++) {
				String tableName = tableNames.get(i);
				TransferDataUtils.cleanData(conn, "", tableName, AppDao::printLog);
				TransferDataUtils.dbTableDataMigration(sqluckyConn.getConn(), conn, tableName, "", "", 100, true,
						AppDao::printLog);

			}

			// 完成

//			for (int i = 0; i < tableNames.size(); i++) {
//				String tableName = tableNames.get(i);
//				String sql = "select   *   from  " + tableName;
//				SheetDataValue dvt = new SheetDataValue();
//				dvt.setDbConnection(sqluckyConn);
//				dvt.setSqlStr(sql);
//				dvt.setTabName(tableName);
//				try {
//					ResultSetPo rspo = SelectDao.selectSqlToRS(sql, sqluckyConn);
//					ObservableList<ResultSetRowPo> datas = rspo.getDatas();
//					if (datas != null) {
//						for (ResultSetRowPo resultSetRow : datas) {
//							ObservableList<ResultSetCellPo> cells = resultSetRow.getRowDatas();
//							InsertDao.execInsert(conn, tableName, cells);
//						}
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		} finally {
			if (sqluckyConn != null)
				sqluckyConn.closeConn();
			if (conn != null) {
				SqluckyAppDB.closeConn(conn);
			}
		}
	}

	// 获取目录下的旧db文件, 从旧文件中找一个最新的
	private static String oldDbFiles() {
		String rs = "";
		String path = DBTools.dbFilePath();
		File dir = new File(path);

		File[] files = dir.listFiles(name -> {
			return name.getName().startsWith(ConfigVal.H2_DB_NAME) && name.getName().endsWith(".mv.db");
		});
		if (files != null && files.length > 0) {
			long lastModifiedTime = 0;
			for (var fl : files) {
				String flName = fl.getName();
				if (!flName.startsWith(ConfigVal.H2_DB_NAME + ConfigVal.H2_DB_VERSION)) {
					long ltmp = fl.lastModified();
					if (ltmp > lastModifiedTime) {
						lastModifiedTime = ltmp;
						rs = path + flName.substring(0, flName.indexOf(".mv.db"));
					}
//					System.out.println(rs);
				}
			}
		}
		return rs;
	}

	// 获取目录下的旧db文件, 从旧文件中找一个最新的
	public static Optional<File> appOldDbFiles() {
		String rs = "";
		File rsFile = null;
		String path = DBTools.dbFilePath();
		File dir = new File(path);

		File[] files = dir.listFiles(name -> {
			String fnm = name.getName();
			return fnm.startsWith(ConfigVal.H2_DB_NAME) && fnm.endsWith("_sqlite.db")
					&& (!fnm.startsWith(ConfigVal.H2_DB_NAME + ConfigVal.H2_DB_VERSION));
		});
		if (files != null && files.length > 0) {
			long lastModifiedTime = 0;
			for (var fl : files) {
				String flName = fl.getName();
				if (!flName.startsWith(ConfigVal.H2_DB_NAME + ConfigVal.H2_DB_VERSION)) {
					long ltmp = fl.lastModified();
					if (ltmp > lastModifiedTime) {
						lastModifiedTime = ltmp;
						rs = flName;
						rsFile = new File(path, flName);
					}
//					System.out.println(rs);
				}
			}

			if (StrUtils.isNotNullOrEmpty(rs)) {
				for (var ftmp : files) {
					String ftmpName = ftmp.getName();
					if (!ftmpName.equals(rs)) {
						String archiveName = ftmpName.replace("_sqlite", "_archive");
						File renameFile = new File(path, archiveName);
						ftmp.renameTo(renameFile);
					}
				}
			}
		}
		Optional<File> rsOF = Optional.ofNullable(rsFile);
		return rsOF;
	}

	// 执行更新脚本
	public static void updateAppSql(Connection conn) {
		String UPDATE_SQL = AppDao.readConfig(conn, "UPDATE_SQL");
		if (UPDATE_SQL != null && UPDATE_SQL.length() > 0) {
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
			AppDao.saveConfig(conn, "UPDATE_SQL", "");
		}

		List<String> ls = updateSQL();
		for (String sql : ls) {
			try {
				if (sql.length() > 0) {
					DBTools.execDDL(conn, sql);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private static List<String> updateSQL() {
		List<String> ls = new ArrayList<>();
		String path = FileUtils.getUserDirectoryPath() + "/.sqlucky/updatesql.txt";
		File fl = new File(path);
		if (fl.exists()) {
			try {
				ls = FileUtils.readLines(fl, "UTF-8");
				FileUtils.forceDelete(fl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ls;
	}

}
