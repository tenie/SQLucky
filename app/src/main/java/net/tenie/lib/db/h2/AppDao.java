package net.tenie.lib.db.h2;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.utility.DBTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.dao.InsertDao;

/**
 * 
 * @author tenie
 *
 */
public class AppDao {
	private static Logger logger = LogManager.getLogger(AppDao.class);
	public static final String CONNECTION_INFO = 
			"CREATE TABLE `CONNECTION_INFO` (\n" + 
//			"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" +  
					// sqlite 自增 AUTOINCREMENT
			"  `ID` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +  
			"  `CONN_NAME` VARCHAR(1000)   NOT NULL,\n" + 
			"  `USER` VARCHAR(1000)   NOT NULL,\n" + 
			"  `PASS_WORD` VARCHAR(1000)   NOT NULL,\n" + 
			"  `HOST` VARCHAR(200) ,\n" + 
			"  `PORT` VARCHAR(10) , \n" + 
			"  `JDBC_URL` VARCHAR(500) , \n" + 
			"  `DRIVER` VARCHAR(200) ,\n" + 
			"  `VENDOR` VARCHAR(100)  ,\n" + 
			"  `SCHEMA` VARCHAR(200)  ,\n" + 
			"  `DB_NAME` VARCHAR(200)  ,\n" + 
			"  `COMMENT` VARCHAR(200) DEFAULT NULL,\n" +  
			"  `AUTO_CONNECT` INT(1) DEFAULT '0',\n" + 
			"  `CREATED_AT` DATETIME DEFAULT NULL,\n" + 
			"  `UPDATED_AT` DATETIME DEFAULT NULL,\n" + 
			"  `RECORD_VERSION` INT(11) DEFAULT '0',\n" + 
			"  `ORDER_TAG` DOUBLE(11) DEFAULT '99'" + 
			// sqlite 不能建表的时候创建联合组件
//			+ ",\n" 
//			"  PRIMARY KEY (`ID`,`CONN_NAME`)\n" + 
			") ";
	public static  final String SQL_TEXT_SAVE = 
			"CREATE TABLE `SQL_TEXT_SAVE` (\n" +  
			"  `TITLE_NAME` VARCHAR(1000)   NOT NULL,\n" + 
			"  `SQL_TEXT` CLOB, \n" +
			"  `FILE_NAME` VARCHAR(1000) ,\n" + 
			"  `ENCODE` VARCHAR(100) ,\n" + 
			"  `PARAGRAPH` INT(11) DEFAULT '0',\n" + 
			"  `SCRIPT_ID` INT(11) NOT NULL ,\n" + 
			
			"  PRIMARY KEY (`TITLE_NAME`)\n" + 
			") ";
	
	public static final  String SCRIPT_ARCHIVE = 
			"CREATE TABLE `SCRIPT_ARCHIVE` (\n" +
//			"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `ID` INTEGER PRIMARY KEY AUTOINCREMENT,\n" + 
			"  `TITLE_NAME` VARCHAR(1000)   NOT NULL,\n" + 
			"  `SQL_TEXT` CLOB, \n" +
			"  `FILE_NAME` VARCHAR(1000) ,\n" + 
			"  `ENCODE` VARCHAR(100) ,\n" + 
			"  `PARAGRAPH` INT(11) DEFAULT '0'"+ 
//			+ ",\n" 
//			"  PRIMARY KEY ( `ID`, `TITLE_NAME`)\n" + 
			") ";
	
	
	public static final  String APP_CONFIG = 
					"CREATE TABLE `APP_CONFIG` (\n" +  
					"  `NAME` VARCHAR(1000)   NOT NULL,\n" + 
					"  `VAL`  VARCHAR(1000), \n" + 
					"  PRIMARY KEY (`NAME`)\n" + 
					") ";
	
	public static final  String DATA_MODEL_INFO = 
			"CREATE TABLE `DATA_MODEL_INFO` (\n" +
//			"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `ID` INTEGER PRIMARY KEY AUTOINCREMENT,\n" + 
			"  `NAME` VARCHAR(200)   NOT NULL,\n" + 
			"  `DESCRIBE` VARCHAR(300)  , \n" +
			"  `AVATAR` VARCHAR(200)   ,\n" + 
			"  `VERSION` VARCHAR(100)   ,\n" + 
			
			"  `CREATEDTIME` VARCHAR(100)    ,\n" + 
			"  `UPDATEDTIME` VARCHAR(100)    ,\n" + 
			
			"  `ORDER_TAG` INT(11) DEFAULT '99'" + 
//			+ ",\n" +
//			"  PRIMARY KEY ( `ID`, `NAME`)\n" + 
			") ";
	
	
	public static final  String DATA_MODEL_TABLE = 
			"CREATE TABLE `DATA_MODEL_TABLE` (\n" +
//			"  `ITEM_ID` INT(11) NOT NULL AUTO_INCREMENT,\n" +
			"  `ITEM_ID` INTEGER PRIMARY KEY AUTOINCREMENT,\n" + 
			"  `MODEL_ID` INT(11)   ,\n" + 		
			"  `ID` VARCHAR(100) ,\n" + 
			"  `DEF_KEY` VARCHAR(200)   NOT NULL,\n" + 
			"  `DEF_NAME` VARCHAR(300)  , \n" +
			"  `COMMENT` VARCHAR(1000)  , \n" +
	 
			"  `CREATED_TIME` DATETIME  ,\n" + 
			"  `UPDATED_TIME` DATETIME  " +
//			+ ",\n" + 
//			"  PRIMARY KEY ( `ITEM_ID`, `DEF_KEY`)\n" + 
			") ";
	public static final  String DATA_MODEL_TABLE_FIELDS = 
			"CREATE TABLE `DATA_MODEL_TABLE_FIELDS` (\n" +
//			"  `ITEM_ID` INT(11) NOT NULL AUTO_INCREMENT,\n" +
			"  `ITEM_ID` INTEGER PRIMARY KEY AUTOINCREMENT,\n" + 
			"  `TABLE_ID` INT(11) NOT NULL ,\n" +
			"  `MODEL_ID` INT(11)   ,\n" + 	
			"  `ID` VARCHAR(100)  , \n" + 
			"  `ROW_NO` INT(11) ,\n" + 
			"  `DEF_KEY` VARCHAR(200)    ,   \n" +  //字段名称
			"  `DEF_NAME` VARCHAR(300)  , \n" +
			"  `COMMENT` VARCHAR(1000)  , \n" +
			
			"  `DOMAIN` VARCHAR(200)  , \n" +
			"  `TYPE` VARCHAR(200)  , \n" +
			"  `LEN` INT(11) ,\n" + 
			"  `SCALE` VARCHAR(100)  , \n" +
			
			"  `PRIMARY_KEY` VARCHAR(10) ,\n" + 
			"  `NOT_NULL` VARCHAR(10) ,\n" + 
			"  `AUTO_INCREMENT` VARCHAR(10) ,\n" + 
			"  `DEFAULT_VALUE` VARCHAR(500)  , \n" + 
			"  `HIDE_IN_GRAPH` VARCHAR(10) ,\n" + 
			
			"  `TYPE_FULL_NAME` VARCHAR(500)  , \n" + 
			"  `PRIMARY_KEY_NAME` VARCHAR(500)  , \n" + 
			"  `NOT_NULL_NAME` VARCHAR(500)  , \n" + 
			"  `AUTO_INCREMENT_NAME` VARCHAR(500)  , \n" + 
			"  `REF_DICT` VARCHAR(500)  , \n" + 
			
			"  `CREATED_TIME` DATETIME ,\n" + 
			"  `UPDATED_TIME` DATETIME " +
//			+ ",\n" + 
//			"  PRIMARY KEY ( `ITEM_ID`, `TABLE_ID`,`DEF_KEY`)\n" + 
			") ";
	
	public static final  String PLUGIN_INFO = 
			"CREATE TABLE `PLUGIN_INFO` (\n" +
//			"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" +
			"  `ID` INTEGER PRIMARY KEY AUTOINCREMENT, \n" + 
			"  `PLUGIN_NAME` VARCHAR(200)    ,   \n" +  //字段名称
			"  `PLUGIN_CODE` VARCHAR(200)    ,   \n" +  //字段名称
			"  `PLUGIN_DESCRIBE` VARCHAR(1000)  , \n" +
			"  `COMMENT` VARCHAR(1000)  , \n" +
			"  `DOWNLOAD_STATUS` INT(1) ,\n" +            //下载状态, 0:未安装, 1: 以安装
			"  `RELOAD_STATUS` INT(1) DEFAULT '1',\n" +   // 是否需要加载, 0: 不加载, 1: 加载
			"  `VERSION` VARCHAR(30)    , "+       // 版本
			
			"  `CREATED_TIME` DATETIME ,\n" + 
			"  `UPDATED_TIME` DATETIME "+ 
//			",\n" + 
//			"  PRIMARY KEY ( `ID`, `PLUGIN_NAME`)\n" + 
			") ";
 
	
	// 建表 
	public static void createTab(Connection conn) {
		try {
			DBTools.execDDLNoErr(conn, CONNECTION_INFO);
			DBTools.execDDLNoErr(conn, SQL_TEXT_SAVE);
			DBTools.execDDLNoErr(conn, SCRIPT_ARCHIVE); 
			DBTools.execDDLNoErr(conn, APP_CONFIG);
			
			DBTools.execDDLNoErr(conn, DATA_MODEL_INFO);
			DBTools.execDDLNoErr(conn, DATA_MODEL_TABLE);
			DBTools.execDDLNoErr(conn, DATA_MODEL_TABLE_FIELDS);
			DBTools.execDDLNoErr(conn, PLUGIN_INFO);
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
 
	
//	public static void save(Connection conn , String title, String txt, String filename) {
//		String sql = "insert into SQL_TEXT_SAVE (TITLE_NAME, SQL_TEXT, FILE_NAME) values ( '"+title+"' , '"+txt+"', '"+filename+"' )";
//		try {
//			DBTools.execDML(conn, sql);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} 
//	}
	public static void save(Connection conn , String title, String txt, String filename, String encode, int paragraph, int scriptId) {
		String sql = "insert into SQL_TEXT_SAVE (TITLE_NAME, SQL_TEXT, FILE_NAME, ENCODE, PARAGRAPH ,SCRIPT_ID) values ( ?, ? , ?, ?, ?, ?)";
		PreparedStatement sm = null; 
		try { 
			sm = conn.prepareStatement(sql);
			sm.setString(1, title);
			sm.setString(2, txt);
			sm.setString(3, filename);
			sm.setString(4, encode);
			sm.setInt(5, paragraph);
			sm.setInt(6, scriptId);
		    sm.executeUpdate();
		} catch (SQLException e) { 
			e.printStackTrace(); 
		}finally { 
			if(sm!=null)
				try {
					sm.close();
				} catch (SQLException e) { 
					e.printStackTrace();
				}
		}
	}
	
	
	public static DocumentPo scriptArchive(Connection conn , String title, String txt, String filename, String encode, int paragraph) {
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
		}finally { 
			if(sm!=null)
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
	
	public static DocumentPo scriptArchive( String title, String txt, String filename, String encode, int paragraph) {
		var conn = SqluckyAppDB.getConn();
		DocumentPo po = null ;
		try{
			 po = scriptArchive(SqluckyAppDB.getConn(),  title, txt, filename, encode, paragraph);
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		if( po == null) {
			po = new DocumentPo();
		}
		return  po;
	}
	
	public static void updateScriptArchive(Connection conn, DocumentPo po) {
		PreparedStatement sm = null; 
		String sql = "update SCRIPT_ARCHIVE set TITLE_NAME = ?, "
				   + " SQL_TEXT = ?, FILE_NAME = ? , ENCODE = ?, PARAGRAPH = ? where id = ?";
		try {
			sm = conn.prepareStatement(sql);
			sm.setString(1, po.getTitle());
			sm.setString(2, po.getText());
			sm.setString(3, po.getFileFullName());
			sm.setString(4, po.getEncode());
			sm.setInt(5, po.getParagraph());
			sm.setInt(6, po.getId());
			
			sm.execute(); 
		} catch (SQLException e) { 
			e.printStackTrace(); 
		}finally { 
			if(sm!=null)
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
		}finally { 
			if(sm!=null)
				try {
					sm.close();
				} catch (SQLException e) { 
					e.printStackTrace();
				}
		}
		
	}
		
	
	
	public static String readConfig(Connection conn, String name) {
		String sql = "select   VAL   from   APP_CONFIG   where name = '"+name+"' ";
		String vals = DBTools.selectOne(conn, sql);
		return vals;
	}
	
	public static void deleteConfigKey(Connection conn , String key) {
		try {
			DBTools.execDDL(conn, "DELETE from APP_CONFIG where name = '"+key+"' ");
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}
		
	public static void saveConfig(Connection conn , String key, String val) {
		
//		String kv = readConfig(conn, key);
//		if(kv !=null && kv.length() > 0) { 
//			deleteConfigKey(conn, key);
//		}
		deleteConfigKey(conn, key);
		String sql = "insert into APP_CONFIG (NAME, VAL) values ( '"+key+"' , '"+val+"' )"; 
		try {
			DBTools.execDML(conn, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static List<H2SqlTextSavePo> read(Connection conn) {
		String sql = "select   *   from   SQL_TEXT_SAVE   ";
		List<H2SqlTextSavePo> vals = new ArrayList<H2SqlTextSavePo>();
		Statement sm = null; 
		ResultSet rs = null;
		try { 
			sm = conn.createStatement();
			logger.info("执行   "+ sql);
		    rs =  sm.executeQuery(sql);  
		    while(rs.next()) { 
		    	H2SqlTextSavePo po = new H2SqlTextSavePo();
		    	po.setTitle( rs.getString("TITLE_NAME"));
		    	po.setText( rs.getString("SQL_TEXT"));
		    	po.setFileName( rs.getString("FILE_NAME"));
		    	po.setEncode( rs.getString("ENCODE"));
		    	po.setParagraph(rs.getInt("PARAGRAPH"));
		    	po.setScriptId(rs.getInt("SCRIPT_ID"));
		    	vals.add(po);
		    }
		} catch (SQLException e) { 
			e.printStackTrace(); 
		}finally { 
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e1) { 
					e1.printStackTrace();
				}
			if(sm!=null)
				try {
					sm.close();
				} catch (SQLException e) { 
					e.printStackTrace();
				}
		}
		return vals;
	}
	
	public static void delScriptPo(Integer id) {
		String sql = "delete   from   SCRIPT_ARCHIVE  where id = " + id ;
		var conn = SqluckyAppDB.getConn();
		try {
			DBTools.execDDL(conn,  sql);
		} catch (SQLException e) { 
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
	public static List<DocumentPo> readScriptPo(Connection conn) {
		String sql = "select   *   from   SCRIPT_ARCHIVE " ;
		List<DocumentPo> vals = new ArrayList<>();
		
		Statement sm = null; 
		ResultSet rs = null;
		try { 
			sm = conn.createStatement();
			logger.info("执行   "+ sql);
		    rs =  sm.executeQuery(sql);  
		    while(rs.next()) {
		    	DocumentPo po = new DocumentPo();
		    	po.setId(rs.getInt("ID"));
		    	po.setTitle( rs.getString("TITLE_NAME"));
		    	po.setText( rs.getString("SQL_TEXT"));
		    	po.setFileFullName( rs.getString("FILE_NAME"));
		    	po.setEncode( rs.getString("ENCODE"));
		    	po.setParagraph(rs.getInt("PARAGRAPH"));
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
	
	
	public static void deleteAll(Connection conn ) {
		try {
			DBTools.execDDL(conn, "DELETE from SQL_TEXT_SAVE");
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}
	
	public static void testDbTableExists(Connection conn) {
		// 第一次启动
		if (!tabExist(conn, "PLUGIN_INFO")) {
			AppDao.createTab(conn);
			// 数据库迁移
			transferOldDbData(conn);
		} else {// 之后的启动, 更新脚本
//			UpdateScript.execUpdate(conn);

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
	// 旧的数据 转移 到新的 表里
	private static void transferOldDbData(Connection conn) {
		String path = oldDbFiles();
		if (StrUtils.isNotNullOrEmpty(path)) {
			DBConnectorInfoPo connPo = new DBConnectorInfoPo("CONN_NAME",  
					"", // rd.getString("DRIVER"),
					"", // rd.getString("HOST"),
					"", // rd.getString("PORT"),
					ConfigVal.USER, 
					ConfigVal.PASSWD, 
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
				SheetDataValue dvt = new SheetDataValue();
				dvt.setDbConnection(cnor);
				dvt.setSqlStr(sql);
				dvt.setTabName(tableName);
				try {
					SelectDao.selectSql(sql, -1, dvt);
//					var datas = dvt.getRawData();
					ResultSetPo rspo = dvt.getDataRs();
					ObservableList<ResultSetRowPo> datas = rspo.getDatas();
					var fs = dvt.getColss();
					if (datas != null) {
						for (ResultSetRowPo data : datas) {
							InsertDao.execInsert(conn, tableName, data);
						}
					}

				} catch (Exception e) {
					e.printStackTrace(); 
				}

			}
			cnor.closeConn();

		}
	}
	// 获取目录下的旧db文件, 从旧文件中找一个最新的
	private static String oldDbFiles(){
		String rs = "";
		String path = DBTools.dbFilePath() ;
		File dir = new File(path);
		
		File[] files = dir.listFiles(name->{
				return name.getName().startsWith(ConfigVal.H2_DB_NAME) && name.getName().endsWith(".mv.db");
			});
		if(files != null && files.length > 0) {
			long lastModifiedTime = 0;
			for(var fl : files) {
				String flName = fl.getName(); 
				if(!flName.startsWith(ConfigVal.H2_DB_NAME + ConfigVal.H2_DB_VERSION) ) {					
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

	// 执行更新脚本
	public static void updateAppSql(Connection conn) {
//		 setConfigVal(conn,  "UPDATE_SQL", "ALTER TABLE SQL_TEXT_SAVE ADD PARAGRAPH  INT(11);");
		 String  UPDATE_SQL =  AppDao.readConfig(conn , "UPDATE_SQL"); 
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
				AppDao.saveConfig(conn,  "UPDATE_SQL", "");
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
    /**
     *  
     * @return
     */
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
	
	
}
