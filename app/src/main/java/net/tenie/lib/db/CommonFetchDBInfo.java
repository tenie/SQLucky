package net.tenie.lib.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.db.ExportDDL;
import net.tenie.Sqlucky.sdk.po.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.TablePo;
import net.tenie.fx.Action.SettingKeyCodeCombination;
/*   
 * @author tenie 
 * */
public class CommonFetchDBInfo {
	private static Logger logger = LogManager.getLogger(CommonFetchDBInfo.class);

	private ExportDDL exportDDL;

	public CommonFetchDBInfo(ExportDDL ddl) {
		this.exportDDL = ddl;
	}

	/**
	 * 获取所有表, 并包含字段信息
	 */
	public List<TablePo> getAllTables(Connection conn, String schema) {
		List<TablePo> ls = null;
		try {
			ls = Dbinfo.fetchAllTableName(conn, schema);
			for (TablePo tabpo : ls) {
				Dbinfo.fetchTableInfo(conn, tabpo);
				Dbinfo.fetchTablePrimaryKeys(conn, tabpo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("表个数" + ls.size());
		return ls;
	}

	// 建表语句生成
	public String exportCreateTable(Connection conn, String schema, String tabName) throws SQLException {
		String sql = exportDDL.exportCreateTable(conn, schema, tabName);
		return sql;
	}

	// 视图
	public String exportCreateView(Connection conn, String schema, String tabName) throws SQLException {
		String sql = exportDDL.exportCreateView(conn, schema, tabName);
		return sql;
	}

	// 函数
	public String exportCreateFunction(Connection conn, String schema, String tabName) throws SQLException {
		String sql = exportDDL.exportCreateFunction(conn, schema, tabName);
		return sql;
	}

	// 过程
	public String exportCreateProcedure(Connection conn, String schema, String tabName) throws SQLException {
		String sql = exportDDL.exportCreateProcedure(conn, schema, tabName);
		return sql;
	}

	// 索引
	public String exportCreateIndex(Connection conn, String schema, String tabName) throws SQLException {
		String sql = exportDDL.exportCreateIndex(conn, schema, tabName);
		return sql;
	}

	// 序列
	public String exportCreateSequence(Connection conn, String schema, String tabName) throws SQLException {
		String sql = exportDDL.exportCreateSequence(conn, schema, tabName);
		return sql;
	}

	// 触发器
	public String exportCreateTrigger(Connection conn, String schema, String tabName) throws SQLException {
		String sql = exportDDL.exportCreateTrigger(conn, schema, tabName);
		return sql;
	}

	// 主键
	public String exportCreatePrimaryKey(Connection conn, String schema, String tabName) throws SQLException {
		String sql = exportDDL.exportCreatePrimaryKey(conn, schema, tabName);
		return sql;
	}

	// 外键exportCreateForeignKey
	public String exportCreateForeignKey(Connection conn, String schema, String tabName) throws SQLException {
		String sql = exportDDL.exportCreateForeignKey(conn, schema, tabName);
		return sql;
	}

	// 表加字段ddl
	public String exportAlterTableAddColumn(Connection conn, String schema, String tabName, String newCol)
			throws SQLException {
		String sql = exportDDL.exportAlterTableAddColumn(conn, schema, tabName, newCol);
		return sql;
	}

	// 表删除字段
	public String exportAlterTableDropColumn(Connection conn, String schema, String tabName, String col)
			throws SQLException {
		String sql = exportDDL.exportAlterTableDropColumn(conn, schema, tabName, col);
		return sql;
	}

	// 表 修改字段 exportAlterTableModifyColumn
	public String exportAlterTableModifyColumn(Connection conn, String schema, String tabName, String col)
			throws SQLException {
		String sql = exportDDL.exportAlterTableDropColumn(conn, schema, tabName, col);
		return sql;
	}

	// 表添加主键
	public String exportAlterTableAddPrimaryKey(Connection conn, String schema, String tabName, String key)
			throws SQLException {
		String sql = exportDDL.exportAlterTableAddPrimaryKey(conn, schema, tabName, key);
		return sql;
	}

	// 外键
	public String exportAlterTableAddForeignKey(Connection conn, String schema, String tabName, String key)
			throws SQLException {
		String sql = exportDDL.exportAlterTableAddPrimaryKey(conn, schema, tabName, key);
		return sql;
	}

	// 所有序列 名称
//	public List<String> exportAllSequenceName(Connection conn, String schema) {
//		return exportDDL.allSequenceName(conn, schema);
//	}

//	// 所有触发器名称
	public List<FuncProcTriggerPo> exportAllTrigger(Connection conn, String schema) {
		return exportDDL.allTriggerObj(conn, schema);
	}

//    // 所有过程名称
	public List<FuncProcTriggerPo> exportAllProcedure(Connection conn, String schema) {
		return exportDDL.allProcedureObj(conn, schema);
	}

	// 所有函数名称
	public List<FuncProcTriggerPo> exportAllFunction(Connection conn, String schema) {
		return exportDDL.allFunctionObj(conn, schema);
	}

	// 所有主键名称
//	public List<String> exportAllPrimaryKeyName(Connection conn, String schema) {
//		return exportDDL.allPrimaryKeyName(conn, schema);
//	}

	// 所有外键名称
//	public List<String> exportAllForeignKeyName(Connection conn, String schema) {
//		return exportDDL.allPrimaryKeyName(conn, schema);
//	}

	// 删表
	public String exportDropTable(String schema, String name) throws SQLException {
		String sql = exportDDL.exportDropTable(schema, name);
		return sql;
	}

	// 删视图
	public String exportDropView(String schema, String name) {
		String sql = exportDDL.exportDropView(schema, name);
		return sql;
	}

	// 删 函数
	public String exportDropFcuntion(String schema, String name) {
		String sql = exportDDL.exportDropFunction(schema, name);
		return sql;
	}

	// 删过程
	public String exportDropProcedure(String schema, String name) {
		String sql = exportDDL.exportDropProcedure(schema, name);
		return sql;
	}

	// 删索引
	public String exportDropIndex(String schema, String name) {
		String sql = exportDDL.exportDropIndex(schema, name);
		return sql;
	}

	// 删序列
	public String exportDropSequence(String schema, String name) {
		String sql = exportDDL.exportDropSequence(schema, name);
		return sql;
	}

	// 删触发器
	public String exportDropTrigger(String schema, String name) {
		String sql = exportDDL.exportDropTrigger(schema, name);
		return sql;
	}

	// 删除主键
	public String exportDropPrimaryKey(String schema, String name) {
		String sql = exportDDL.exportDropPrimaryKey(schema, name);
		return sql;
	}

	// 删除外键
	public String exportDropForeignKey(String schema, String name) {
		String sql = exportDDL.exportDropForeignKey(schema, name);
		return sql;
	}

}
