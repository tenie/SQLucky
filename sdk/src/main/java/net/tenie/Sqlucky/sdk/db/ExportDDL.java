package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.util.List;

import net.tenie.Sqlucky.sdk.po.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.TablePo;
/* 
 *  * @author tenie 
 *  
 */
public interface ExportDDL {
 
    String getDbVendor();
	List<TablePo> allTableObj(Connection conn, String schema); 
	List<TablePo> allViewObj(Connection conn, String schema); 
	List<FuncProcTriggerPo> allFunctionObj(Connection conn, String schema); 
	List<FuncProcTriggerPo> allProcedureObj(Connection conn, String schema); 
	List<FuncProcTriggerPo> allTriggerObj(Connection conn, String schema);
	List<FuncProcTriggerPo> allIndexObj(Connection conn, String schema);
	List<FuncProcTriggerPo> allSequenceObj(Connection conn, String schema); 

	List<FuncProcTriggerPo> allPrimaryKeyObj(Connection conn, String schema); 
	List<FuncProcTriggerPo> allForeignKeyObj(Connection conn, String schema); 
 
 

	String exportCreateTable(Connection conn, String schema, String obj);

	String exportCreateView(Connection conn, String schema, String obj);

	String exportCreateFunction(Connection conn, String schema, String obj);

	String exportCreateProcedure(Connection conn, String schema, String obj);

	String exportCreateIndex(Connection conn, String schema, String obj);

	String exportCreateSequence(Connection conn, String schema, String obj);

	String exportCreateTrigger(Connection conn, String schema, String obj);

	String exportCreatePrimaryKey(Connection conn, String schema, String obj);

	String exportCreateForeignKey(Connection conn, String schema, String obj);

	String exportAlterTableAddColumn(Connection conn, String schema, String tableName, String newCol);

	String exportAlterTableDropColumn(Connection conn, String schema, String tableName, String col);

	String exportAlterTableModifyColumn(Connection conn, String schema, String tableName, String col);

	String exportAlterTableAddPrimaryKey(Connection conn, String schema, String tableName, String key);

	String exportAlterTableAddForeignKey(Connection conn, String schema, String tableName, String key);

	String exportDropTable(String schema, String name);

	String exportDropView(String schema, String name);

	String exportDropFunction(String schema, String name);

	String exportDropProcedure(String schema, String name);

	String exportDropIndex(String schema, String name);

	String exportDropSequence(String schema, String name);

	String exportDropTrigger(String schema, String name);

	String exportDropPrimaryKey(String schema, String name);

	String exportDropForeignKey(String schema, String name);
	
	String exportCallFuncSql(String funcStr);
}
