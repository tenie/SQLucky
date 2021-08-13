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

import net.tenie.fx.PropertyPo.ScriptPo;
import net.tenie.lib.db.DBTools;
import net.tenie.lib.db.ExportSqlMySqlImp;
/*   @author tenie */
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
			"  `DRIVER` VARCHAR(200) ,\n" + 
			"  `VENDOR` VARCHAR(100)  ,\n" + 
			"  `SCHEMA` VARCHAR(200)  ,\n" + 
			"  `DB_NAME` VARCHAR(200)  ,\n" + 
			"  `COMMENT` VARCHAR(200) DEFAULT NULL,\n" +  
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
	// 建表 
	public static void createTab(Connection conn) {
		try {
			DBTools.execDDLNoErr(conn, CONNECTION_INFO);
			DBTools.execDDLNoErr(conn, SQL_TEXT_SAVE);
			DBTools.execDDLNoErr(conn, SCRIPT_ARCHIVE); 
			DBTools.execDDLNoErr(conn, APP_CONFIG);
			saveConfig(conn, "THEME", "DARK");
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
//	int val = DBTools.execInsertReturnId(conn, sql);
	public static ScriptPo scriptArchive(Connection conn , String title, String txt, String filename, String encode, int paragraph) {
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
		ScriptPo po = new ScriptPo();
		po.setId(id);
		po.setTitle(title);
		po.setFileName(filename);
		po.setEncode(encode);
		po.setParagraph(paragraph);
		po.setText(txt);
		
		return po;
	}
	
	public static ScriptPo scriptArchive( String title, String txt, String filename, String encode, int paragraph) {
		ScriptPo po = null ;
		try{
			 po = scriptArchive(H2Db.getConn(),  title, txt, filename, encode, paragraph);
		}finally {
			H2Db.closeConn();
		}
		if( po == null) {
			po = new ScriptPo();
		}
		return  po;
	}
	
	public static void updateScriptArchive(ScriptPo po) {
		PreparedStatement sm = null; 
		String sql = "update SCRIPT_ARCHIVE set TITLE_NAME = ?, "
				   + " SQL_TEXT = ?, FILE_NAME = ? , ENCODE = ?, PARAGRAPH = ? where id = ?";
		try {  
			var conn = H2Db.getConn();
			sm = conn.prepareStatement(sql);
			sm.setString(1, po.getTitle());
			sm.setString(2, po.getText());
			sm.setString(3, po.getFileName());
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
			H2Db.closeConn();
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
	
	public static List<ScriptPo> readScriptPo(Connection conn) {
		String sql = "select   *   from   SCRIPT_ARCHIVE " ;
		List<ScriptPo> vals = new ArrayList<>();
		
		Statement sm = null; 
		ResultSet rs = null;
		try { 
			sm = conn.createStatement();
			logger.info("执行   "+ sql);
		    rs =  sm.executeQuery(sql);  
		    while(rs.next()) {
		    	ScriptPo po = new ScriptPo();
		    	po.setId(rs.getInt("ID"));
		    	po.setTitle( rs.getString("TITLE_NAME"));
		    	po.setText( rs.getString("SQL_TEXT"));
		    	po.setFileName( rs.getString("FILE_NAME"));
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
