package net.tenie.lib.db.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.utility.DBTools;

/**
 * 
 * @author tenie
 *
 */
public class SqlTextDao {
	private static Logger logger = LogManager.getLogger(SqlTextDao.class);
	public static final String CONNECTION_INFO = 
			"CREATE TABLE `CONNECTION_INFO` (\n" + 
			"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" + 
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
			"  `ORDER_TAG` DOUBLE(11) DEFAULT '99',\n" + 
			"  PRIMARY KEY (`ID`,`CONN_NAME`)\n" + 
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
			"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `TITLE_NAME` VARCHAR(1000)   NOT NULL,\n" + 
			"  `SQL_TEXT` CLOB, \n" +
			"  `FILE_NAME` VARCHAR(1000) ,\n" + 
			"  `ENCODE` VARCHAR(100) ,\n" + 
			"  `PARAGRAPH` INT(11) DEFAULT '0',\n" + 
			 
			"  PRIMARY KEY ( `ID`, `TITLE_NAME`)\n" + 
			") ";
	
	
	public static final  String APP_CONFIG = 
					"CREATE TABLE `APP_CONFIG` (\n" +  
					"  `NAME` VARCHAR(1000)   NOT NULL,\n" + 
					"  `VAL`  VARCHAR(1000), \n" + 
					"  PRIMARY KEY (`NAME`)\n" + 
					") ";
	
	public static final  String DATA_MODEL_INFO = 
			"CREATE TABLE `DATA_MODEL_INFO` (\n" +
			"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `NAME` VARCHAR(200)   NOT NULL,\n" + 
			"  `DESCRIBE` VARCHAR(300)  , \n" +
			"  `AVATAR` VARCHAR(200)   ,\n" + 
			"  `VERSION` VARCHAR(100)   ,\n" + 
			
			"  `CREATEDTIME` VARCHAR(100)    ,\n" + 
			"  `UPDATEDTIME` VARCHAR(100)    ,\n" + 
			
			"  `ORDER_TAG` INT(11) DEFAULT '99',\n" + 
			 
			"  PRIMARY KEY ( `ID`, `NAME`)\n" + 
			") ";
	
	
	public static final  String DATA_MODEL_TABLE = 
			"CREATE TABLE `DATA_MODEL_TABLE` (\n" +
			"  `ITEM_ID` INT(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `ID` VARCHAR(100) ,\n" + 
			"  `DEF_KEY` VARCHAR(200)   NOT NULL,\n" + 
			"  `DEF_NAME` VARCHAR(300)  , \n" +
			"  `COMMENT` VARCHAR(1000)  , \n" +
	 
			"  `CREATED_TIME` DATETIME  ,\n" + 
			"  `UPDATED_TIME` DATETIME  ,\n" + 
			
			"  PRIMARY KEY ( `ITEM_ID`, `DEF_KEY`)\n" + 
			") ";
	public static final  String DATA_MODEL_TABLE_FIELDS = 
			"CREATE TABLE `DATA_MODEL_TABLE_FIELDS` (\n" +
			"  `ITEM_ID` INT(11) NOT NULL AUTO_INCREMENT,\n" +
			"  `TABLE_ID` INT(11) NOT NULL ,\n" +
			"  `ID` VARCHAR(100)  , \n" + 
			"  `ROW_NO` INT(11) ,\n" + 
			"  `DEF_KEY` VARCHAR(200)    ,   \n" +  //字段名称
			"  `DEF_NAME` VARCHAR(300)  , \n" +
			"  `COMMENT` VARCHAR(1000)  , \n" +
			
			"  `DOMAIN` VARCHAR(200)  , \n" +
			"  `TYPE` VARCHAR(200)  , \n" +
			"  `LEN` INT(11) ,\n" + 
			"  `SCALE` VARCHAR(100)  , \n" +
			
			"  `PRIMARY_KEY` BOOLEAN ,\n" + 
			"  `NOT_NULL` BOOLEAN ,\n" + 
			"  `AUTO_INCREMENT` BOOLEAN ,\n" + 
			"  `DEFAULT_VALUE` VARCHAR(500)  , \n" + 
			"  `HIDE_IN_GRAPH` BOOLEAN ,\n" + 
			
			"  `TYPE_FULL_NAME` VARCHAR(500)  , \n" + 
			"  `PRIMARY_KEY_NAME` VARCHAR(500)  , \n" + 
			"  `NOT_NULL_NAME` VARCHAR(500)  , \n" + 
			"  `AUTO_INCREMENT_NAME` VARCHAR(500)  , \n" + 
			"  `REF_DICT` VARCHAR(500)  , \n" + 
			
			"  `CREATED_TIME` DATETIME ,\n" + 
			"  `UPDATED_TIME` DATETIME ,\n" + 
			
			"  PRIMARY KEY ( `ITEM_ID`, `TABLE_ID`,`DEF_KEY`)\n" + 
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
		DocumentPo po = null ;
		try{
			 po = scriptArchive(H2Db.getConn(),  title, txt, filename, encode, paragraph);
		}finally {
			H2Db.closeConn();
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
		var conn = H2Db.getConn();
		try {
			DBTools.execDDL(conn,  sql);
		} catch (SQLException e) { 
			e.printStackTrace();
		}finally {
			H2Db.closeConn();
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
	
	
	
}
