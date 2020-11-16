package net.tenie.lib.db;

import java.sql.Connection;
import java.util.List;
import net.tenie.lib.po.FuncProcTriggerPo;
import net.tenie.lib.po.TablePo;
/* 
 *  * @author tenie 
 *  
 */
public class EmptyExportDDLImp implements ExportDDL {

	@Override
	public List<TablePo> allTableName(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TablePo> allTableObj(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TablePo> allViewObj(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TablePo> allViewName(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FuncProcTriggerPo> allFunctionObj(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FuncProcTriggerPo> allProcedureObj(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FuncProcTriggerPo> allTriggerObj(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> allIndexName(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> allSequenceName(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> allForeignKeyName(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> allPrimaryKeyName(Connection conn, String schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportCreateTable(Connection conn, String schema, String obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportCreateView(Connection conn, String schema, String obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportCreateFunction(Connection conn, String schema, String obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportCreateProcedure(Connection conn, String schema, String obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportCreateIndex(Connection conn, String schema, String obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportCreateSequence(Connection conn, String schema, String obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportCreateTrigger(Connection conn, String schema, String obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportCreatePrimaryKey(Connection conn, String schema, String obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportCreateForeignKey(Connection conn, String schema, String obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportAlterTableAddColumn(Connection conn, String schema, String tableName, String newCol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportAlterTableDropColumn(Connection conn, String schema, String tableName, String col) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportAlterTableModifyColumn(Connection conn, String schema, String tableName, String col) {
		// TODO Auto-generated method stub
		return null;
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
	public String exportDropTable(String schema, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportDropView(String schema, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportDropFcuntion(String schema, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportDropProcedure(String schema, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportDropIndex(String schema, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportDropSequence(String schema, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportDropTrigger(String schema, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportDropPrimaryKey(String schema, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportDropForeignKey(String schema, String name) {
		// TODO Auto-generated method stub
		return null;
	}

  
	 

}
