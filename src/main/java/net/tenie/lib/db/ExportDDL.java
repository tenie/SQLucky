package net.tenie.lib.db;

import java.sql.Connection;
import java.util.List;

import net.tenie.lib.po.FuncProcTriggerPo;
import net.tenie.lib.po.TablePo;
/* 
 *  * @author tenie 
 *  
 */
public interface ExportDDL {

	List<TablePo> allTableName(Connection conn, String schema);

	List<TablePo> allTableObj(Connection conn, String schema);

	List<TablePo> allViewObj(Connection conn, String schema);

	List<TablePo> allViewName(Connection conn, String schema);

	List<FuncProcTriggerPo> allFunctionObj(Connection conn, String schema);

	List<FuncProcTriggerPo> allProcedureObj(Connection conn, String schema);

	List<FuncProcTriggerPo> allTriggerObj(Connection conn, String schema);

	List<String> allIndexName(Connection conn, String schema);

	List<String> allSequenceName(Connection conn, String schema);

	List<String> allForeignKeyName(Connection conn, String schema);

	List<String> allPrimaryKeyName(Connection conn, String schema);

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

}
