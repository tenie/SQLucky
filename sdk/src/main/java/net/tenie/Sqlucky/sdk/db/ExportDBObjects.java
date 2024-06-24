package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.util.List;

import net.sf.jsqlparser.statement.select.Limit;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.db.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.db.TableForeignKeyPo;
import net.tenie.Sqlucky.sdk.po.db.TableIndexPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/*
 * 导出DB info
 *  * @author tenie 
 *  
 */
public abstract class ExportDBObjects {
	//查询表的schema
	public abstract List<String> tableSchema(Connection conn, String table);
	// 所有表
	public abstract List<TablePo> allTableObj(Connection conn, String schema);
	// 所有试图
	public abstract List<TablePo> allViewObj(Connection conn, String schema);
	// 所有函数
	public abstract List<FuncProcTriggerPo> allFunctionObj(Connection conn, String schema);
	// 所有过程
	public abstract List<FuncProcTriggerPo> allProcedureObj(Connection conn, String schema);
	// 所有触发器
	public abstract List<FuncProcTriggerPo> allTriggerObj(Connection conn, String schema);
	// 所有索引
	public abstract List<FuncProcTriggerPo> allIndexObj(Connection conn, String schema);
	// 所有序列
	public abstract List<FuncProcTriggerPo> allSequenceObj(Connection conn, String schema);

	//  目前没用， 所有主键
	public abstract List<FuncProcTriggerPo> allPrimaryKeyObj(Connection conn, String schema);
	//  目前没用， 所有外键
	public abstract List<FuncProcTriggerPo> allForeignKeyObj(Connection conn, String schema);
	// 获取表的索引
	public abstract List<TableIndexPo>  tableIndex(Connection conn, String schema, String tableName);
	// 获取表的外键
	public abstract List<TableForeignKeyPo> tableForeignKey(Connection conn, String schema, String tableName);

	public abstract String exportCreateTable(Connection conn, String schema, String obj);

	public abstract String exportCreateView(Connection conn, String schema, String obj);

	public abstract String exportCreateFunction(Connection conn, String schema, String obj);

	public abstract String exportCreateProcedure(Connection conn, String schema, String obj);

	public abstract String exportCreateIndex(Connection conn, String schema, String obj);

	public abstract String exportCreateSequence(Connection conn, String schema, String obj);

	public abstract String exportCreateTrigger(Connection conn, String schema, String obj);

	/**
	 * 设置表字段的注释
	 * @param conn
	 * @param schema
	 * @param table
	 */
	public  void  setTableFieldComment(Connection conn, String schema, String table,List<SheetFieldPo>  fieldPoList ) {
        try {
            Dbinfo.fetchTableFieldInfo(conn, fieldPoList, schema, table);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

	/**
	 * 导出表的查询sql 语句, 查询20条, 这里默认全部, 需要各个数据库实现
	 */
	public  String select20(String tabSchema, String tablename ) {
			String sql = "";
			if (StrUtils.isNotNullOrEmpty(tabSchema)) {
				sql = "SELECT * FROM " + tabSchema + "." + tablename;
			} else {
				sql = "SELECT * FROM " + tablename;
			}
			return  sql;
	}

	/**
	 * 导出limit sql
	 * @param sql
	 * @param limit
	 * @return
	 */
	public String limitSelectSql(String sql, int  limit){
		String tmpsql = sql.toLowerCase();
	    boolean tf = StrUtils.hasKeyWord(tmpsql,"limit");
		if(tf){
			return sql;
		}else {
			String limitSql =String.format("select * from ( %s ) a limit %d ", sql, limit);

			return limitSql;
		}
	}

    // 目前没用
	public abstract String exportCreatePrimaryKey(Connection conn, String schema, String obj);

	public abstract String exportCreateForeignKey(Connection conn, String schema, String obj);

	public abstract String exportAlterTableAddColumn(Connection conn, String schema, String tableName, String newCol);

	public abstract String exportAlterTableDropColumn(Connection conn, String schema, String tableName, String col);

	public abstract String exportAlterTableModifyColumn(Connection conn, String schema, String tableName, String col);
	// 目前没用
	public abstract String exportAlterTableAddPrimaryKey(Connection conn, String schema, String tableName, String key);
	// 目前没用
	public abstract String exportAlterTableAddForeignKey(Connection conn, String schema, String tableName, String key);

	public abstract String exportDropTable(String schema, String name);

	public abstract String exportDropView(String schema, String name);

	public abstract String exportDropFunction(String schema, String name);

	public abstract String exportDropProcedure(String schema, String name);

	public abstract String exportDropIndex(String schema, String indexName, String tableName);

	public abstract String exportDropSequence(String schema, String name);

	public abstract String exportDropTrigger(String schema, String name);
	
	// 目前没用
	public abstract String exportDropPrimaryKey(String schema, String name);

	public abstract String exportDropForeignKey(String schema, String foreignKeyName, String tableName);
	// 单纯调用函数的方法
	public abstract String exportCallFuncSql(String funcStr);
}
