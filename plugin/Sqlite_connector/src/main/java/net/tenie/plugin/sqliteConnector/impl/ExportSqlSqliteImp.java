package net.tenie.plugin.sqliteConnector.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
public class ExportSqlSqliteImp implements ExportDBObjects {
 
	private	FetchDBInfoCommonTools fdbtool;
	public ExportSqlSqliteImp() { 
		fdbtool  =new FetchDBInfoCommonTools(); 
	}
	 
	/**
	 * 导出所有表对象, 属性: 表名, 字段, 主键, ddl
	 */
	@Override
	public List<TablePo> allTableObj(Connection conn, String schema ){
		try {
			List<TablePo>  vals = Dbinfo.fetchAllTableName(conn);
			return vals;
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return null; 
	}
	
	/**
	 * 视图对象
	 */
	@Override
	public List<TablePo> allViewObj(Connection conn, String schema ){
		try {
			// 获取视图名称
			List<TablePo> vals =  Dbinfo.fetchAllViewName(conn);
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
			v = Dbinfo.fetchTableObjByName(conn, "", tab);
			// 表对象字段赋值
			Dbinfo.fetchTableInfo(conn, v);
			// 表对象 主键赋值
			Dbinfo.fetchTablePrimaryKeys(conn, v);
			// 表对象ddl语句
		    ddl = createTab(v); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ddl;
	}
	
	private String createTab(TablePo tab) {
		Set<TableFieldPo> fls = tab.getFields();
		String tableName = tab.getTableName();  
		String sql = "CREATE TABLE " +tableName + " ( \n";
		String keysql = "";
		// 字段
		for (TableFieldPo po : fls) {
			// not null
			String notnull = "N".equals(po.getIsNullable()) ? "not null " : " ";
			// default
			String defVal = StrUtils.isNullOrEmpty(po.getDefaultVal()) ? "" : " default " + po.getDefaultVal();

			String fieldName = po.getFieldName();
			sql += "	" + fieldName + " " + po.getType()  + notnull + " " + defVal + " ,\n";
 
		}
		// 获取主键
		List<TablePrimaryKeysPo> ls = tab.getPrimaryKeys();
		String pkn = "";
		if (ls.size() > 0) {
			for (TablePrimaryKeysPo kp : ls) {
				keysql += kp.getColumnName() + ",";
				pkn = kp.getPkName();
			}
		}
		// 有主键就加上
		if (!StrUtils.isNullOrEmpty(keysql)) { 
			keysql =  " PRIMARY KEY ( " + keysql.substring(0, keysql.length() - 1) + " )";
			
			if(StrUtils.isNotNullOrEmpty(pkn)) {
				keysql =  " CONSTRAINT " + pkn +  keysql;
			}
			sql += keysql + " \n" + ") \n";
			
			
		} else {
			sql = sql.substring(0, sql.length() - 2) + " \n )";
		}
		  
		
		return sql;
	}
	

	@Override
	public String exportCreateView(Connection conn, String schema, String obj) {
		String ddl = fdbtool.exportView(conn, schema, obj);
		return ddl;
	}

	@Override
	public String exportCreateFunction(Connection conn, String schema, String obj) {
		String ddl = fdbtool.exportFunction(conn,  schema, obj);
		return ddl;
	}

	@Override
	public String exportCreateProcedure(Connection conn, String schema, String obj) {
		String ddl = fdbtool.exportProcedure(conn,  schema, obj);
		return ddl;
	}

	@Override
	public String exportCreateIndex(Connection conn, String schema, String obj) { 
		String ddl = fdbtool.exportIndex(conn,  schema, obj); 
		return ddl;
	}

	@Override
	public String exportCreateSequence(Connection conn, String schema, String obj) {
		String ddl = fdbtool.exportSeq(conn, schema,  obj);
		return ddl;
	}

	@Override
	public String exportCreateTrigger(Connection conn, String schema, String obj) { 
		String ddl = fdbtool.exportTrigger(conn,  schema, obj);
		return ddl;
	}

	@Override
	public String exportCreatePrimaryKey(Connection conn, String schema, String obj) {
		//TODO 暂时不用
		return "";
	}

	@Override
	public String exportCreateForeignKey(Connection conn, String schema, String obj) {
		String ddl = fdbtool.exportForeignKey(conn,  schema, obj);
		return ddl;
	}

	@Override
	public String exportAlterTableAddColumn(Connection conn, String schema, String tableName, String newCol) {
		String sql = "ALTER TABLE "+tableName+" ADD    " + newCol +";";
		return sql;
	}

	@Override
	public String exportAlterTableDropColumn(Connection conn, String schema, String tableName, String col) {
		throw new RuntimeException("Sqlite nonsupport Drop Column");
	}

	@Override
	public String exportAlterTableModifyColumn(Connection conn, String schema, String tableName, String col) {
		throw new RuntimeException("Sqlite nonsupport Modify Column");
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
		String sql = "DROP TABLE " +  name.trim();
		return sql;
	}

	@Override
	public String exportDropView(String schema, String name) {
		String sql = "DROP VIEW " +   name.trim();
		return sql;
	}

	@Override
	public String exportDropFunction(String schema, String name) {
		String sql = "DROP  FUNCTION " +  name.trim();
		return sql;
	}

	@Override
	public String exportDropProcedure(String schema, String name) {
		String sql = "DROP  PROCEDURE " +   name.trim();
		return sql;
	}

	@Override
	public String exportDropIndex(String schema, String name) {
		String sql = "DROP INDEX " +  name.trim();
		return sql;
	}
 
	@Override
	public String exportDropSequence(String schema, String name) {
		String sql = "DROP sequence " +  name.trim() ;
		return sql;
	}

	@Override
	public String exportDropTrigger(String schema, String name) {
		String sql = "DROP TRIGGER " +  name.trim();
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
