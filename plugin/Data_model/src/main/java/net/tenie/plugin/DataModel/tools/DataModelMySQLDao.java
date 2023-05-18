package net.tenie.plugin.DataModel.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.tenie.plugin.DataModel.po.DataModelTableFieldsPo;
import net.tenie.plugin.DataModel.po.DataModelTablePo;

public class DataModelMySQLDao {
	
	public void queryTableInfo(Connection conn, String tableName) {
		String sql = "select  * from information_schema.columns where TABLE_NAME='"+tableName+"'";
		DataModelTablePo tablePo = new DataModelTablePo();
		DataModelTableFieldsPo tableFieldsPo = new DataModelTableFieldsPo();
		 
		ResultSet rs = null;
		String str = "";
		try {
			rs = conn.createStatement().executeQuery(sql);
			if (rs.next()) {
				str = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
}
