package net.tenie.lib.db.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.tenie.lib.db.DBTools;
/*   @author tenie */
public class SqlTextDao {
	
	// 建表 
	public static void createTab(Connection conn) {
		String sql = "CREATE TABLE `CONNECTION_INFO` (\n" + 
				"  `ID` INT(11) NOT NULL AUTO_INCREMENT,\n" + 
				"  `CONN_NAME` VARCHAR(1000)   NOT NULL,\n" + 
				"  `USER` VARCHAR(1000)   NOT NULL,\n" + 
				"  `PASS_WORD` VARCHAR(1000)   NOT NULL,\n" + 
				"  `HOST` VARCHAR(200) NOT NULL,\n" + 
				"  `PORT` VARCHAR(10) NOT NULL, \n" + 
				"  `DRIVER` VARCHAR(200) NOT NULL ,\n" + 
				"  `VENDOR` VARCHAR(100)   NOT NULL,\n" + 
				"  `SCHEMA` VARCHAR(200) NOT NULL ,\n" + 
				"  `COMMENT` VARCHAR(200) DEFAULT NULL,\n" +  
				"  `CREATED_AT` DATETIME DEFAULT NULL,\n" + 
				"  `UPDATED_AT` DATETIME DEFAULT NULL,\n" + 
				"  `RECORD_VERSION` INT(11) DEFAULT '0',\n" + 
				"  PRIMARY KEY (`ID`,`CONN_NAME`)\n" + 
				") ";
		String sql2 = "CREATE TABLE `SQL_TEXT_SAVE` (\n" +  
				"  `TITLE_NAME` VARCHAR(1000)   NOT NULL,\n" + 
				"  `SQL_TEXT` CLOB, \n" +
				"  `FILE_NAME` VARCHAR(1000) ,\n" + 
				"  PRIMARY KEY (`TITLE_NAME`)\n" + 
				") ";
		try {
			DBTools.execDDL(conn, sql);
			DBTools.execDDL(conn, sql2);
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}
	public static void save(Connection conn , String title, String txt, String filename) {
		String sql = "insert into SQL_TEXT_SAVE (TITLE_NAME, SQL_TEXT, FILE_NAME) values ( ? , ?, ? )";
		int i = 0;
		PreparedStatement sm = null; 
		try { 
			sm = conn.prepareStatement(sql);
			sm.setString(1, title);
			sm.setString(2, txt);
			sm.setString(3, filename);
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
	
	
	public static List<H2SqlTextSavePo> read(Connection conn) {
		String sql = "select   *   from   SQL_TEXT_SAVE   ";
		List<H2SqlTextSavePo> vals = new ArrayList<H2SqlTextSavePo>();
		Statement sm = null; 
		ResultSet rs = null;
		try { 
			sm = conn.createStatement();
			System.out.println("执行   "+ sql);
		    rs =  sm.executeQuery(sql);  
		    while(rs.next()) { 
		    	H2SqlTextSavePo po = new H2SqlTextSavePo();
		    	po.setTitle( rs.getString("TITLE_NAME"));
		    	po.setText( rs.getString("SQL_TEXT"));
		    	po.setFileName( rs.getString("FILE_NAME"));
		    	
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
