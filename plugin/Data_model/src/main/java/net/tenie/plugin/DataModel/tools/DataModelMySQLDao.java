package net.tenie.plugin.DataModel.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.plugin.DataModel.po.DataModelInfoPo;
import net.tenie.plugin.DataModel.po.DataModelTableFieldsPo;
import net.tenie.plugin.DataModel.po.DataModelTablePo;
/**
 * 通过mysql数据库链接 生成模型
 * @author tenie
 *
 */
public class DataModelMySQLDao {
	
	public static  DataModelInfoPo generateMySqlModel(SqluckyConnector sqluckyConn, String modelName) {
		Connection dbconn =  sqluckyConn.getConn();
		var conn = SqluckyAppDB.getConn();
//		String connName = sqluckyConn.getConnName();
		DataModelInfoPo modelPo = new DataModelInfoPo();
		modelPo.setName(modelName); 
		List<DataModelTablePo> entities = new ArrayList<>();
		modelPo.setEntities(entities);
		try {
			Long modelID = PoDao.insertReturnID(conn, modelPo);
			modelPo.setId(modelID);
			String schema = sqluckyConn.getDefaultSchema();
			List<TablePo> tbs = sqluckyConn.getExportDDL().allTableObj(dbconn, sqluckyConn.getDefaultSchema());
			
			
			for(var tb : tbs) {
				String tableName = tb.getTableName();
				DataModelTablePo mtbpo = queryTableInfo(conn, dbconn, modelID, tableName, schema);
				
				entities.add(mtbpo);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
			sqluckyConn.closeConn();
		}
		 
		return modelPo;
	}
	
	
	public static DataModelTablePo queryTableInfo(Connection conn, Connection sqlConn ,Long modelId, String tableName, String schema) {
		ResultSet rs = null;
		String str = "";
		DataModelTablePo tablePo = new DataModelTablePo();
		try {
			// 表信息
			String  tableSql =  "select  TABLE_COMMENT from information_schema.tables where TABLE_SCHEMA= '"+schema+"' and TABLE_NAME='"+tableName+"'";
			String TABLE_COMMENT = SelectDao.selectOne(sqlConn, tableSql);
			
			tablePo.setDefKey(tableName);
			tablePo.setDefName(TABLE_COMMENT);
			tablePo.setModelId(modelId);
			tablePo.setComment("");

			List<DataModelTableFieldsPo> fields = new ArrayList<>();
			tablePo.setFields(fields);
			Long tableId = PoDao.insertReturnID(conn, tablePo);
			
			// 字段信息
			String sql = "select  * from information_schema.columns where TABLE_NAME='"+tableName+"'";
		
			rs = sqlConn.createStatement().executeQuery(sql);
			while (rs.next()) {
				DataModelTableFieldsPo tableFieldsPo = new DataModelTableFieldsPo();
				tableFieldsPo.setTableId(tableId);
				tableFieldsPo.setModelId(modelId);
				tableFieldsPo.setRowNo( rs.getInt("ORDINAL_POSITION")  );
				tableFieldsPo.setDefKey(rs.getString("COLUMN_NAME"));
				tableFieldsPo.setDefName(rs.getString("COLUMN_COMMENT"));
				tableFieldsPo.setComment(rs.getString("COLUMN_COMMENT"));
				
				tableFieldsPo.setDefaultValue(rs.getString("COLUMN_DEFAULT"));
				tableFieldsPo.setTypeFullName(rs.getString("COLUMN_TYPE"));
				tableFieldsPo.setPrimaryKeyName(rs.getString("COLUMN_KEY")); 
				tableFieldsPo.setPrimaryKey(rs.getString("COLUMN_KEY"));
				String isNullable =   rs.getString("IS_NULLABLE");
				if(isNullable !=null && "NO".equals(isNullable)) {
					tableFieldsPo.setNotNull("YES");
				}else {
					tableFieldsPo.setNotNull("NO");
				}
				tableFieldsPo.setAutoIncrement(rs.getString("EXTRA"));
				PoDao.insert(conn, tableFieldsPo);
				fields.add(tableFieldsPo);
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
		return tablePo;
	}
}
