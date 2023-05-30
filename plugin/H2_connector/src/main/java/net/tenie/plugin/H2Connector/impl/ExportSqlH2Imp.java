package net.tenie.plugin.H2Connector.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import net.tenie.Sqlucky.sdk.db.ExportDBObjects;
import net.tenie.Sqlucky.sdk.po.db.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.db.TableFieldPo;
import net.tenie.Sqlucky.sdk.po.db.TableForeignKeyPo;
import net.tenie.Sqlucky.sdk.po.db.TableIndexPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.po.db.TablePrimaryKeysPo;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
import net.tenie.Sqlucky.sdk.utility.FetchDBInfoCommonTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 
 * @author tenie
 *
 */
public class ExportSqlH2Imp implements ExportDBObjects {
 
	private FetchDBInfoCommonTools fdbtool;
	
	public ExportSqlH2Imp() { 
		fdbtool  =new FetchDBInfoCommonTools(); 
	}
	 
	/**
	 * 导出所有表对象, 属性: 表名, 字段, 主键, ddl
	 */
	@Override
	public List<TablePo> allTableObj(Connection conn, String schema ){
		try {
			List<TablePo>  vals = fetchAllTableViewNameForH2(conn, null, true); // Dbinfo.fetchAllTableName(conn, null);
//			if(vals !=null && vals.size() > 0) {
//				vals.stream().forEach(v ->{
//					try {
//						// 表对象字段赋值
//						Dbinfo.fetchTableInfo(conn, v);
//						// 表对象 主键赋值
//						Dbinfo.fetchTablePrimaryKeys(conn, v);
//						// 表对象ddl语句
//						String ddl = fdb2.createTab( v);
//						v.setDdl(ddl);
//					} catch (Exception e) { 
//						e.printStackTrace();
//					}
//					
//				});
//			}
			return vals;
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return null; 
	}
	
	// jdbc方式: 获取视图, 名称,
	public static List<TablePo> fetchAllTableViewNameForH2(Connection conn, String schemaOrCatalog, boolean istable)
			throws Exception {
		ResultSet tablesResultSet = null;
		ResultSet rs = null;
		Statement sm = null;
		List<TablePo> tbls = new ArrayList<TablePo>();
		try {
			DatabaseMetaData dbMetaData = conn.getMetaData();
			String catalog = Dbinfo.getCatalog(conn);// conn.getCatalog();
			String schema = Dbinfo.getSchema(conn); // conn.getSchema();
			if (StrUtils.isNotNullOrEmpty(schemaOrCatalog)) {
				if (catalog == null) {
					schema = schemaOrCatalog;
				} else if (schema == null) {
					catalog = schemaOrCatalog;
				}
			}
			tablesResultSet = dbMetaData.getTables(catalog, schema, null, null);
			while (tablesResultSet.next()) {
				String tableType = tablesResultSet.getString("TABLE_TYPE");
				if (tableType == null) {
					tableType = "";
				}
				if (istable) {
					if (tableType.contains("VIEW")) {
						continue;
					}
					if (tableType.contains("SYSTEM")) {
						continue;
					}
					
				} else {
					if (!tableType.contains("VIEW")) {
						continue;
					}
				}

				String tableName = tablesResultSet.getString("TABLE_NAME");
				String remarks = tablesResultSet.getString("REMARKS");
				TablePo po = new TablePo();
				po.setTableName(tableName);
				po.setTableRemarks(remarks);
				po.setTableSchema(schemaOrCatalog);
				po.setTableType(tableType);
				LinkedHashSet<TableFieldPo> fields = new LinkedHashSet<TableFieldPo>();
				po.setFields(fields);
				ArrayList<TablePrimaryKeysPo> tpks = new ArrayList<TablePrimaryKeysPo>();
				po.setPrimaryKeys(tpks);
				tbls.add(po);
			}

		} finally {
			if (tablesResultSet != null)
				tablesResultSet.close();
			if (rs != null)
				rs.close();
			if (sm != null)
				sm.close();
		}
		return tbls;
	}
	
	/**
	 * 视图对象
	 */
	@Override
	public List<TablePo> allViewObj(Connection conn, String schema ){
		try {
			// 获取视图名称
			List<TablePo> vals =  Dbinfo.fetchAllViewName(conn, schema);
			if(vals !=null && vals.size() > 0) {
				vals.stream().forEach(v ->{
					// 视图ddl
					String ddl = exportCreateView(conn, schema, v.getTableName());
					v.setDdl(ddl);
				}); 
			}
			return vals;
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return null; 
	}
	
	
	/**
	 * 函数对象
	 */
	@Override
	public List<FuncProcTriggerPo> allFunctionObj(Connection conn, String schema ){
		List<FuncProcTriggerPo> val = new ArrayList<>(); 
		return val;
	}
	/**
	 * 过程对象
	 */
	@Override
	public List<FuncProcTriggerPo> allProcedureObj(Connection conn, String schema) {
		List<FuncProcTriggerPo> val = new ArrayList<>(); 
		return val;
	}
	
	/**
	 * 触发器对象
	 */
	@Override
	public List<FuncProcTriggerPo> allTriggerObj(Connection conn, String schema) {
		List<FuncProcTriggerPo> val = new ArrayList<>(); 
		return val;
	}
 

//	@Override
//	public List<String> allIndexName(Connection conn, String schema) {
//		return fdb2.exportAllIndexs(conn, schema); 
//	}
//
//	@Override
//	public List<String> allSequenceName(Connection conn, String schema) {
//		return fdb2.exportAllSeqs(conn, schema); 
//	}
//
//	
//	@Override
//	public List<String> allForeignKeyName(Connection conn, String schema) {
//		return fdb2.exportAllForeignKeys(conn, schema); 
//	}
//
//	@Override
//	public List<String> allPrimaryKeyName(Connection conn, String schema) {
//		//TODO 先不需要
//		List<String> vals = new ArrayList<String>();
//		return vals;
//	}

	
	
	// 表对象ddl语句
	@Override 
	public String exportCreateTable(Connection conn, String schema, String tab) {
		String ddl = "";
		try {

			TablePo v;
			v = Dbinfo.fetchTableObjByName(conn, schema, tab);
			// 表对象字段赋值
			Dbinfo.fetchTableInfo(conn, v);
			// 表对象 主键赋值
			Dbinfo.fetchTablePrimaryKeys(conn, v);
			// 表对象ddl语句
		    ddl = fdbtool.createTab(v); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ddl;
	}

	@Override
	public String exportCreateView(Connection conn, String schema, String obj) {
//		String ddl = fdbtool.exportView(conn, schema, obj);
		String ddl = "";
		return ddl;
	}

	@Override
	public String exportCreateFunction(Connection conn, String schema, String obj) {
//		String ddl = fdbtool.exportFunction(conn,  schema, obj);
		String ddl = ""; 
		return ddl;
	}

	@Override
	public String exportCreateProcedure(Connection conn, String schema, String obj) {
//		String ddl = fdbtool.exportProcedure(conn,  schema, obj);
		String ddl = "";
		return ddl;
	}

	@Override
	public String exportCreateIndex(Connection conn, String schema, String obj) { 
//		String ddl = fdbtool.exportIndex(conn,  schema, obj); 
		String ddl = "";
		return ddl;
	}

	@Override
	public String exportCreateSequence(Connection conn, String schema, String obj) {
//		String ddl = fdbtool.exportSeq(conn, schema,  obj);
		String ddl = "";
		return ddl;
	}

	@Override
	public String exportCreateTrigger(Connection conn, String schema, String obj) { 
//		String ddl = fdbtool.exportTrigger(conn,  schema, obj);
		String ddl = "";
		return ddl;
	}

	@Override
	public String exportCreatePrimaryKey(Connection conn, String schema, String obj) {
		//TODO 暂时不用
		return "";
	}

	@Override
	public String exportCreateForeignKey(Connection conn, String schema, String obj) {
//		String ddl = fdbtool.exportForeignKey(conn,  schema, obj);
		String ddl = "";
		return ddl;
	}

	@Override
	public String exportAlterTableAddColumn(Connection conn, String schema, String tableName, String newCol) {
		String sql = "ALTER TABLE "+tableName+" ADD    " + newCol +";";
		return sql;
	}

	@Override
	public String exportAlterTableDropColumn(Connection conn, String schema, String tableName, String col) {
		String sql = "ALTER TABLE "+tableName+" DROP COLUMN   " + col +";";
		return sql;
	}

	@Override
	public String exportAlterTableModifyColumn(Connection conn, String schema, String tableName, String col) {
		String sql = "ALTER TABLE "+tableName+" MODIFY COLUMN  " + col +";";
		return sql;
	}

	@Override
	public String exportAlterTableAddPrimaryKey(Connection conn, String schema, String tableName, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportAlterTableAddForeignKey(Connection conn, String schema, String tableName, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportDropTable(String schema , String name) {
		String sql = "DROP TABLE "  + name.trim();
		return sql;
	}

	@Override
	public String exportDropView(String schema, String name) {
		String sql = "DROP VIEW "  + name.trim();
		return sql;
	}

	@Override
	public String exportDropFunction(String schema, String name) {
		String sql = "DROP  FUNCTION "  + name.trim();
		return sql;
	}

	@Override
	public String exportDropProcedure(String schema, String name) {
		String sql = "DROP  PROCEDURE "  + name.trim();
		return sql;
	}

	@Override
	public String exportDropIndex(String schema, String name, String tableName) {
		String sql = "DROP INDEX "  + name.trim();
		return sql;
	}
 
	@Override
	public String exportDropSequence(String schema, String name) {
		String sql = "DROP sequence "  + name.trim() ;
		return sql;
	}

	@Override
	public String exportDropTrigger(String schema, String name) {
		String sql = "DROP TRIGGER "  + name.trim();
		return sql;
	}

	@Override
	public String exportDropPrimaryKey(String schema , String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportDropForeignKey(String schema , String foreignKeyName, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FuncProcTriggerPo> allIndexObj(Connection conn, String schema) {
		List<FuncProcTriggerPo>  vals = new ArrayList<>();
		return vals;
	}

	@Override
	public List<FuncProcTriggerPo> allSequenceObj(Connection conn, String schema) {
		List<FuncProcTriggerPo>  vals = new ArrayList<>();
		return vals;
	}

	@Override
	public List<FuncProcTriggerPo> allPrimaryKeyObj(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FuncProcTriggerPo> allForeignKeyObj(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportCallFuncSql(String funcStr) {
		String sql = "select "+funcStr+" from dual";
		return sql;
	}

	@Override
	public List<TableIndexPo> tableIndex(Connection conn, String schema, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TableForeignKeyPo> tableForeignKey(Connection conn, String schema, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

}
