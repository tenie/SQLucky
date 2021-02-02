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

import net.tenie.lib.db.DBTools;
import net.tenie.lib.db.MySqlExportDDLImp;
/*   @author tenie */
public class SqlTextDao {
	private static Logger logger = LogManager.getLogger(SqlTextDao.class);
	
	// 建表 
	public static void createTab(Connection conn) {
		String sql = 
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
				"  `COMMENT` VARCHAR(200) DEFAULT NULL,\n" +  
				"  `CREATED_AT` DATETIME DEFAULT NULL,\n" + 
				"  `UPDATED_AT` DATETIME DEFAULT NULL,\n" + 
				"  `RECORD_VERSION` INT(11) DEFAULT '0',\n" + 
				"  `ORDER_TAG` DOUBLE(11) DEFAULT '99',\n" + 
				"  PRIMARY KEY (`ID`,`CONN_NAME`)\n" + 
				") ";
		String sql2 = 
				"CREATE TABLE `SQL_TEXT_SAVE` (\n" +  
				"  `TITLE_NAME` VARCHAR(1000)   NOT NULL,\n" + 
				"  `SQL_TEXT` CLOB, \n" +
				"  `FILE_NAME` VARCHAR(1000) ,\n" + 
				"  `ENCODE` VARCHAR(100) ,\n" + 
				"  `PARAGRAPH` INT(11) DEFAULT '0',\n" + 
				 
				"  PRIMARY KEY (`TITLE_NAME`)\n" + 
				") ";
		String configTable = 
						"CREATE TABLE `APP_CONFIG` (\n" +  
						"  `NAME` VARCHAR(1000)   NOT NULL,\n" + 
						"  `VAL`  VARCHAR(1000), \n" + 
						"  PRIMARY KEY (`NAME`)\n" + 
						") ";
		try {
			DBTools.execDDL(conn, sql);
			DBTools.execDDL(conn, sql2);
			DBTools.execDDL(conn, configTable);
			saveConfig(conn, "THEME", "DARK");
		} catch (SQLException e) { 
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
	public static void save(Connection conn , String title, String txt, String filename, String encode, int paragraph) {
		String sql = "insert into SQL_TEXT_SAVE (TITLE_NAME, SQL_TEXT, FILE_NAME, ENCODE, PARAGRAPH) values ( ? , ?, ?, ?, ?)";
		int i = 0;
		PreparedStatement sm = null; 
		try { 
			sm = conn.prepareStatement(sql);
			sm.setString(1, title);
			sm.setString(2, txt);
			sm.setString(3, filename);
			sm.setString(4, encode);
			sm.setInt(5, paragraph);
		    i = sm.executeUpdate();
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
	
	public static void deleteAll(Connection conn ) {
		try {
			DBTools.execDDL(conn, "DELETE from SQL_TEXT_SAVE");
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}
	
	
	
}
