package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.util.List;

import net.tenie.Sqlucky.sdk.po.db.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.db.TableForeignKeyPo;
import net.tenie.Sqlucky.sdk.po.db.TableIndexPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
/* 
 * 导出DB info
 *  * @author tenie 
 *  
 */
public interface ExportDBObjects {
	// 所有表
	List<TablePo> allTableObj(Connection conn, String schema); 
	// 所有试图
	List<TablePo> allViewObj(Connection conn, String schema); 
	// 所有函数
	List<FuncProcTriggerPo> allFunctionObj(Connection conn, String schema); 
	// 所有过程
	List<FuncProcTriggerPo> allProcedureObj(Connection conn, String schema); 
	// 所有触发器
	List<FuncProcTriggerPo> allTriggerObj(Connection conn, String schema);
	// 所有索引
	List<FuncProcTriggerPo> allIndexObj(Connection conn, String schema);
	// 所有序列
	List<FuncProcTriggerPo> allSequenceObj(Connection conn, String schema); 

	// 所有主键
	List<FuncProcTriggerPo> allPrimaryKeyObj(Connection conn, String schema); 
	// 所有外键
	List<FuncProcTriggerPo> allForeignKeyObj(Connection conn, String schema); 
	// 获取表的索引
	List<TableIndexPo>  tableIndex(Connection conn, String schema, String tableName); 
	// 获取表的外键
	 List<TableForeignKeyPo> tableForeignKey(Connection conn, String schema, String tableName); 

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
