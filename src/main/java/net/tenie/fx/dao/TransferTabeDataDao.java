package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.lib.tools.StrUtils;

public class TransferTabeDataDao {
	public static DbTableDatePo insertData(Connection conn, Connection toConn , String tableName,  String schename , String targetSchename) throws SQLException {
		String sql = "select   *   from   "+schename+"."+tableName+"    where   1=1  ";
		DbTableDatePo dpo = new DbTableDatePo();
		// DB对象
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			pstate = conn.prepareStatement(sql);
			// 处理结果集
			rs = pstate.executeQuery();
			// 获取元数据
			ResultSetMetaData mdata = rs.getMetaData();
			// 获取元数据列数
			Integer columnnums = Integer.valueOf(mdata.getColumnCount());
			// 迭代元数据
			for (int i = 1; i <= columnnums; i++) {
				SqlFieldPo po = new SqlFieldPo();
				po.setScale(mdata.getScale(i));
				po.setColumnName(mdata.getColumnName(i));
				po.setColumnClassName(mdata.getColumnClassName(i));
				po.setColumnDisplaySize(mdata.getColumnDisplaySize(i));
				po.setColumnLabel(mdata.getColumnLabel(i));
				po.setColumnType(mdata.getColumnType(i));
				po.setColumnTypeName(mdata.getColumnTypeName(i));
				dpo.addField(po);
			}


			execRs(toConn, rs, dpo, targetSchename+"."+tableName);
			
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
		}
		return dpo;
	} 
	
	private static void execRs( Connection toConn ,ResultSet rs, DbTableDatePo dpo, String tableName) throws SQLException {
		 
		Statement stmt = null;
		int execLine = 500;
		try {
			stmt = toConn.createStatement();
			int idx = 0 ; 
			ObservableList<SqlFieldPo> fpo = dpo.getFields();
			int columnnums = fpo.size();
			String  insertSql = "";
			while (rs.next()) {
				idx++;
				ObservableList<StringProperty> vals = FXCollections.observableArrayList();

				for (int i = 0; i < columnnums; i++) {
					int dbtype = fpo.get(i).getColumnType().get();
					StringProperty val;
					
					Object obj = rs.getObject(i + 1);
					if(obj == null) {
						val = new SimpleStringProperty("<null>");
					}else {
						if (CommonUtility.isDateTime(dbtype)) {
							java.sql.Timestamp ts = rs.getTimestamp(i + 1);
							Date d = new Date(ts.getTime());
							String v = StrUtils.dateToStr(d, ConfigVal.dateFormateL);
							val = new SimpleStringProperty(v);
						} else {
							String temp = rs.getString(i+1);
							val = new SimpleStringProperty(temp); 
						}
					}
					 vals.add(val);
				} 
			    insertSql = GenerateSQLString.insertSQL(tableName, vals, fpo);  
				
				stmt.addBatch(insertSql); 
				if( idx % execLine == 0 ) { 
					System.out.println(insertSql);
					int[] count = stmt.executeBatch();
					System.out.println("instert = "+count.length);
				}  
				 
			}
			
			if( idx % execLine >  0 ) { 
				System.out.println(insertSql);
				int[] count = stmt.executeBatch();
				System.out.println("instert = "+count.length);
			} 
		} catch (Exception e1) { 
			e1.printStackTrace();
		}finally {
			if (stmt != null)
				stmt.close();
		}
	}
	
}
