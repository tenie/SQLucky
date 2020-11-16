package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.tenie.fx.config.DbVendor;
import net.tenie.lib.db.DBTools;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.po.RsData;
import net.tenie.lib.tools.StrUtils;

public class ConnectionDao {
	
	public static void delete(Connection conn, int id) {
		String sql = "delete FROM  CONNECTION_INFO where id = "+id;
		try {
			DBTools.execDDL(conn, sql);
		} catch (SQLException e) { 
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询
	 */
	public static  List<DbConnectionPo> selectData(Connection conn) {
		String sql = "SELECT * FROM  CONNECTION_INFO";
		List<DbConnectionPo> datas =  new ArrayList<DbConnectionPo>();
		try {
		    List<RsData>  rs = DBTools.selectSql(conn, sql);
		    for(RsData rd: rs) {
//		    	DbConnectionPo po = new DbConnectionPo(); 
//		    	po.setId(rd.getInteger("ID"));
//		    	po.setConnName(rd.getString("CONN_NAME"));
//		    	po.setHost(rd.getString("HOST"));
//		    	po.setPort(rd.getString("PORT"));
//		    	po.setDriver(rd.getString("DRIVER"));
//		    	po.setDbVendor(rd.getString("VENDOR"));
//		    	po.setDefaultSchema(rd.getString("SCHEMA"));
//		    	po.setUser(rd.getString("USER"));
//		    	po.setPassWord(rd.getString("PASS_WORD"));
//		    	po.setComment(rd.getString("COMMENT"));
		    	
		    	
		    	  DbConnectionPo po = new DbConnectionPo(
		    			  	rd.getString("CONN_NAME"),
		    			  	rd.getString("DRIVER"), //DbVendor.getDriver(dbDriver.getValue()),
							rd.getString("HOST"),
							rd.getString("PORT"),
							rd.getString("USER"),
							rd.getString("PASS_WORD"),
							rd.getString("VENDOR"), //dbDriver.getValue(),
							rd.getString("SCHEMA") //defaultSchema.getText()													    
							);
		    	  po.setId(rd.getInteger("ID"));
		    	  po.setComment( rd.getString("COMMENT"));
		    	
		    	
		    	datas.add(po);
		    }
		} catch (SQLException e) { 
			e.printStackTrace();
		}
		
		return datas; 
	}
	
	// 保存
		public static DbConnectionPo createOrUpdate(Connection conn ,  DbConnectionPo po) {
			Integer id =          po.getId(); 
		    String connName =     po.getConnName();
		    String user =         po.getUser();
		    String passWord =     po.getPassWord();
		    String host =         po.getHost();
		    String port =         po.getPort();
		    String driver =       po.getDriver();
		    String dbVendor =     po.getDbVendor();
		    String defaultSchema =po.getDefaultSchema();
		    String comment =      po.getComment();
//		    Date createdAt =      po.getCreatedAt();
//		    Date updatedAt =      po.getUpdatedAt();
//		    Integer recordVersion =  po.getRecordVersion(); 
			String sql  = "";
			if(id !=null && id > 0) {
				// 更新
				  sql  = " update CONNECTION_INFO  set "
							+ " CONN_NAME = '"+ connName +"' , "
							+ " USER = '"+ user +"' ,"
							+ " PASS_WORD = '"+ passWord +"' ,"
							+ " HOST = '"+ host +"' ,"
							+ " PORT = '"+ port +"' , "
							+ " DRIVER = '"+ driver +"' , "
							+ " VENDOR = '"+ dbVendor +"' , "
							
							+ " SCHEMA = '"+ defaultSchema +"', "
							+ " UPDATED_AT = '"+StrUtils.dateToStrL(new Date()) +"'";  
							if(  StrUtils.isNotNullOrEmpty(comment)) {
								sql  += ", COMMENT = '"+comment+"' ";
							}  
								sql  += " where ID = " +id;  
				
			}else {
				// 插入
			    sql  = " INSERT INTO CONNECTION_INFO "
						+ "(CONN_NAME , USER, PASS_WORD, HOST, PORT, DRIVER,VENDOR, SCHEMA, CREATED_AT ";
						if(  StrUtils.isNotNullOrEmpty(comment)) {
							sql  += ", COMMENT ";
						}
						 
				sql  +=  ") values( "
						+"'"+ connName +"' , "
						+"'"+ user +"' ,"
						+"'"+ passWord +"' ,"
						+"'"+ host +"' ,"
						+"'"+ port +"' , "
						+"'"+ driver +"' , "
						+"'"+ dbVendor +"' , "
						+"'"+ defaultSchema +"', "
						+"'"+StrUtils.dateToStrL(new Date()) +"'";  
						if(  StrUtils.isNotNullOrEmpty(comment)) {
							sql  += ", '"+comment+"' ";
						}
							sql  += ")";  
			}
			try {
				int val = DBTools.execInsertReturnId(conn, sql);
				
				if(val>0) {
					System.out.println("insert sql return id = " + val);
					po.setId(val);
				}
					
			} catch (SQLException e) { 
				e.printStackTrace();
			}
			System.out.println( po.toString() );
			return po;
		}
	
	 
}
