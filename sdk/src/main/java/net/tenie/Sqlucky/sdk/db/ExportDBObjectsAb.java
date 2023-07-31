package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.util.List;

import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/* 
 * 导出DB info
 *  * @author tenie 
 *  
 */
public abstract class ExportDBObjectsAb implements  ExportDBObjects {
 	// 所有表
	public List<TablePo> allTableObj(Connection conn, String schema) {
		try {
			List<TablePo> vals = Dbinfo.fetchAllTableName(conn, schema);
			return vals;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 	// 所有试图
	@Override
	public List<TablePo> allViewObj(Connection conn, String schema) {
		try {
			// 获取视图名称
			List<TablePo> vals = Dbinfo.fetchAllViewName(conn, schema);
			return vals;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	// 所有函数
//	List<FuncProcTriggerPo> allFunctionObj(Connection conn, String schema); 
//	// 所有过程
//	List<FuncProcTriggerPo> allProcedureObj(Connection conn, String schema); 
//	// 所有触发器
//	List<FuncProcTriggerPo> allTriggerObj(Connection conn, String schema);
//	// 所有索引
//	List<FuncProcTriggerPo> allIndexObj(Connection conn, String schema);
//	// 所有序列
//	List<FuncProcTriggerPo> allSequenceObj(Connection conn, String schema); 

	//  目前没用， 所有主键
//	List<FuncProcTriggerPo> allPrimaryKeyObj(Connection conn, String schema); 
//	//  目前没用， 所有外键
//	List<FuncProcTriggerPo> allForeignKeyObj(Connection conn, String schema); 
//	// 获取表的索引
//	List<TableIndexPo>  tableIndex(Connection conn, String schema, String tableName); 
//	// 获取表的外键
//	 List<TableForeignKeyPo> tableForeignKey(Connection conn, String schema, String tableName); 

//	String exportCreateTable(Connection conn, String schema, String obj);
//
//	String exportCreateView(Connection conn, String schema, String obj);
//
//	String exportCreateFunction(Connection conn, String schema, String obj);
//
//	String exportCreateProcedure(Connection conn, String schema, String obj);
//
//	String exportCreateIndex(Connection conn, String schema, String obj);
//
//	String exportCreateSequence(Connection conn, String schema, String obj);
//
//	String exportCreateTrigger(Connection conn, String schema, String obj);

	// 目前没用
//	String exportCreatePrimaryKey(Connection conn, String schema, String obj);
//
//	String exportCreateForeignKey(Connection conn, String schema, String obj);
//
//	String exportAlterTableAddColumn(Connection conn, String schema, String tableName, String newCol);
//
//	String exportAlterTableDropColumn(Connection conn, String schema, String tableName, String col);
//
//	String exportAlterTableModifyColumn(Connection conn, String schema, String tableName, String col);
//	// 目前没用
//	String exportAlterTableAddPrimaryKey(Connection conn, String schema, String tableName, String key);
//	// 目前没用
//	String exportAlterTableAddForeignKey(Connection conn, String schema, String tableName, String key);

	public String getObjName(String schema, String name) {
		String val = " ";
		if(StrUtils.isNotNullOrEmpty(schema)) {
			val += schema + "." + name.trim();
		}else {
			val += name.trim();
		}
		
		return val;
	}
	
	public String exportDropTable(String schema, String name) {
		String sql = "DROP TABLE " + getObjName(schema, name);
		return sql;
	}

	public String exportDropView(String schema, String name) {
		String sql = "DROP VIEW " + getObjName(schema, name);
		return sql;
	}

	public String exportDropFunction(String schema, String name) {
		String sql = "DROP  FUNCTION " + getObjName(schema, name);
		return sql;
	}

	public String exportDropProcedure(String schema, String name) {
		String sql = "DROP  PROCEDURE " + getObjName(schema, name);
		return sql;
	}
	
	// mySQL ：ALTER TABLE table_name DROP INDEX index_name
	public String exportDropIndex(String schema, String indexName, String tableName) {
		String sql = "DROP INDEX " + getObjName(schema, indexName);
		return sql;
	}

	public String exportDropSequence(String schema, String name) {
		String sql = "DROP SEQUENCE " +  getObjName(schema, name);
		return sql;
	}

	public String exportDropTrigger(String schema, String name) {
		String sql = "DROP TRIGGER " +  getObjName(schema, name);
		return sql;
	}
	
	// 目前没用
//	String exportDropPrimaryKey(String schema, String name);

//	String exportDropForeignKey(String schema, String foreignKeyName, String tableName);
	// 单纯调用函数的方法
//	String exportCallFuncSql(String funcStr);
}
