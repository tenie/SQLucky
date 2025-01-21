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

import net.tenie.Sqlucky.sdk.component.editor.MyAutoComplete;
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
import net.tenie.Sqlucky.sdk.utility.TransferDataUtils;

/**
 * 
 * @author tenie
 *
 */
public class AppDao {
	private static Logger logger = LogManager.getLogger(AppDao.class);

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
			insertShortcutKeys(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 插入 快捷键
	public static void insertShortcutKeys(Connection conn) {
		try {
			String sql = readSqlFile("/db/keysBinding.sql");
			if (CommonUtils.isMacOS()) {
				sql = macKeyChange(sql);
			}
			execSqlFileString(conn, sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除快捷键
	public static void deleteShortcutKeys(Connection conn) {
		try {
			String sql = "delete from KEYS_BINDING";
			execSqlFileString(conn, sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// 重置 快捷键
	public static void restoreShortcutKeys(){
		Connection conn = SqluckyAppDB.getConn();
		deleteShortcutKeys(conn);
		insertShortcutKeys(conn);
		SqluckyAppDB.closeConn(conn);
	}

	public static DocumentPo scriptArchive(Connection conn, String title, String txt, String filename, String encode,
			int paragraph, int tabPosition) {
		String sql = "insert into SCRIPT_ARCHIVE (TITLE_NAME, SQL_TEXT, FILE_NAME, ENCODE, PARAGRAPH, TAB_POSITION) values ( ? , ?, ?, ?, ?, ?)";
		PreparedStatement sm = null;
		Integer id = -1;
		try {
			sm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			sm.setString(1, title);
			sm.setString(2, txt);
			sm.setString(3, filename);
			sm.setString(4, encode);
			sm.setInt(5, paragraph);
			sm.setInt(6, tabPosition);


			id = DBTools.execInsertReturnId(sm);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (sm != null) {
                try {
                    sm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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

	public static DocumentPo scriptArchive(String title, String txt, String filename, String encode, int paragraph , int tabPosition) {
		var conn = SqluckyAppDB.getConn();
		DocumentPo po = null;
		try {
			po = scriptArchive(conn, title, txt, filename, encode, paragraph, tabPosition);
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
		if(po.getId() == null ) {
			return;
		}
		String sql = "update SCRIPT_ARCHIVE set TITLE_NAME = ?, "
				+ " SQL_TEXT = ?, FILE_NAME = ? , ENCODE = ?, PARAGRAPH = ? , OPEN_STATUS = ?, IS_ACTIVATE = ?, " +
				" TAB_POSITION = ?  where id = ?";
		try {
			sm = conn.prepareStatement(sql);
			sm.setString(1, po.getTitle().get());
			sm.setString(2, po.getText());
			sm.setString(3, po.getExistFileFullName());
			sm.setString(4, po.getEncode());
			sm.setInt(5, po.getParagraph());
			sm.setInt(6, po.getOpenStatus());
			sm.setInt(7, po.getIsActivate());
			sm.setInt(8, po.getTabPosition());
			sm.setInt(9, po.getId());

			// 位置 position
			sm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (sm != null) {
                try {
                    sm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
			if (sm != null) {
                try {
                    sm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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
//			logger.info("执行   " + sql);
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
				po.setTabPosition(rs.getInt("TAB_POSITION"));
				vals.add(po);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
                    rs.close();
                }
				if (sm != null) {
                    sm.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return vals;
	}

	// 判断表是否存在
	public static void testDbTableExists(Connection conn, String dbTableName) {
		// 第一次启动
		if (!tabExist(conn, dbTableName)) {
			AppDao.createTab(conn);

		}
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

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		} finally {
			if (sqluckyConn != null) {
                sqluckyConn.closeConn();
            }
			if (conn != null) {
				SqluckyAppDB.closeConn(conn);
			}
		}
	}

	// 获取目录下的旧db文件, 从旧文件中找一个最新的
	private static String oldDbFiles() {
		String rs = "";
		String path = CommonUtils.sqluckyWorkDirPath();
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
				}
			}
		}
		return rs;
	}

	// 检查是否需要迁移数据, 如果最新版本的数据库已经存在, 就不需要迁移
	public static boolean checkTransferDB() {
		File dbFile = new File(SqluckyAppDB.getSqliteFilePath());
		if (dbFile.exists()) {
			return false;
		}
		return true;
	}

	// 获取目录下的旧db文件, 从旧文件中找一个最新的
	public static Optional<File> appOldDbFiles() {
		String rs = "";
		File rsFile = null;
		String path = CommonUtils.sqluckyWorkDirPath();
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
		String UPDATE_SQL = SqluckyAppDB.readConfig(conn, "UPDATE_SQL");
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
			SqluckyAppDB.saveConfig(conn, "UPDATE_SQL", "");
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

	// 自动补全要用的文本
	public static void readAllAutoCompleteText(Connection conn){
		String sql = "select TEXT  from AUTO_COMPLETE_TEXT";
		List<String>  list = DBTools.selectOneColList(conn, sql);
		for(String tmp : list){
			MyAutoComplete.addKeyWords(tmp);
		}
	}
	// 保存 自动补全文本
	public static void saveAutoCompleteText(String val){
		Connection conn = SqluckyAppDB.getConn();
		try{
			String sql = " INSERT INTO AUTO_COMPLETE_TEXT (TEXT ) VALUES ('"+val+"' )";
			DBTools.execDDLNoErr(conn, sql);
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
	}


}
