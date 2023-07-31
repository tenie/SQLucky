package net.tenie.plugin.DB2Connector.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.tenie.Sqlucky.sdk.db.Dbinfo;
import net.tenie.Sqlucky.sdk.db.ExportDBObjects;
import net.tenie.Sqlucky.sdk.po.db.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.db.TableForeignKeyPo;
import net.tenie.Sqlucky.sdk.po.db.TableIndexPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;

/* 
 *  @author tenie 
 *  
 */
public class ExportSqlDB2Imp implements ExportDBObjects {

	private FetchDB2InfoImp fdb2;

	public ExportSqlDB2Imp() {
		fdb2 = new FetchDB2InfoImp();
	}

	/**
	 * 导出所有表对象, 属性: 表名, 字段, 主键, ddl
	 */
	@Override
	public List<TablePo> allTableObj(Connection conn, String schema) {
		try {
			List<TablePo> vals = Dbinfo.fetchAllTableName(conn, schema);
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

	/**
	 * 函数对象
	 */
	@Override
	public List<FuncProcTriggerPo> allFunctionObj(Connection conn, String schema) {
		try {
			// 函数名称
			List<FuncProcTriggerPo> vals = Dbinfo.fetchAllFunctions(conn, schema);
			return vals;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 过程对象
	 */
	@Override
	public List<FuncProcTriggerPo> allProcedureObj(Connection conn, String schema) {
		try {
			// 名称
			List<FuncProcTriggerPo> vals = Dbinfo.fetchAllProcedures(conn, schema);

			return vals;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 触发器对象
	 */
	@Override
	public List<FuncProcTriggerPo> allTriggerObj(Connection conn, String schema) {
		try {
			// 名称
			List<String> names = fdb2.getTriggers(conn, schema);
			List<FuncProcTriggerPo> vals = new ArrayList<>();
			for (String name : names) {
				FuncProcTriggerPo po = new FuncProcTriggerPo();
				po.setName(name);
				po.setSchema(schema);
				vals.add(po);
			}

//			List<FuncProcTriggerPo> vals = Dbinfo.fetchAllTriggers(conn, schema);

			return vals;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 所有索引
	 */
	@Override
	public List<FuncProcTriggerPo> allIndexObj(Connection conn, String schema) {
		try {
			// 名称
			List<String> names = fdb2.getIndexs(conn, schema);
			List<FuncProcTriggerPo> vals = new ArrayList<>();
			for (String name : names) {
				FuncProcTriggerPo po = new FuncProcTriggerPo();
				po.setName(name);
				po.setSchema(schema);
				vals.add(po);
			}

			return vals;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 序列
	 */
	@Override
	public List<FuncProcTriggerPo> allSequenceObj(Connection conn, String schema) {
		try {
			// 名称
			List<String> names = fdb2.getSeq(conn, schema);
			List<FuncProcTriggerPo> vals = new ArrayList<>();
			for (String name : names) {
				FuncProcTriggerPo po = new FuncProcTriggerPo();
				po.setName(name);
				po.setSchema(schema);
				vals.add(po);
			}

			return vals;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 目前没有
	@Override
	public List<FuncProcTriggerPo> allPrimaryKeyObj(Connection conn, String schema) {
		String sql = "select * from syscat.references where TYPE  ='P' and OWNER = '" + schema + "' ";
		return null;
	}

	// 目前没有
	@Override
	public List<FuncProcTriggerPo> allForeignKeyObj(Connection conn, String schema) {
		String sql = "select * from syscat.references where TYPE  ='F' and OWNER = '" + schema + "' ";
		return null;
	}

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
			ddl = fdb2.createTab(schema, v);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ddl;
	}

	@Override
	public String exportCreateView(Connection conn, String schema, String obj) {
		String ddl = fdb2.exportView(conn, schema, obj);
		return ddl;
	}

	@Override
	public String exportCreateFunction(Connection conn, String schema, String obj) {
		String ddl = fdb2.exportFunction(conn, schema, obj);
		return ddl;
	}

	@Override
	public String exportCreateProcedure(Connection conn, String schema, String obj) {
		String ddl = fdb2.exportProcedure(conn, schema, obj);
		return ddl;
	}

	@Override
	public String exportCreateIndex(Connection conn, String schema, String obj) {
		String ddl = fdb2.exportIndex(conn, schema, obj);
		return ddl;
	}

	@Override
	public String exportCreateSequence(Connection conn, String schema, String obj) {
		String ddl = fdb2.exportSeq(conn, schema, obj);
		return ddl;
	}

	@Override
	public String exportCreateTrigger(Connection conn, String schema, String obj) {
		String ddl = fdb2.exportTrigger(conn, schema, obj);
		return ddl;
	}

	// 目前没用
	@Override
	public String exportCreatePrimaryKey(Connection conn, String schema, String obj) {

		return "";
	}

	@Override
	public String exportCreateForeignKey(Connection conn, String schema, String obj) {
		String ddl = fdb2.exportForeignKey(conn, schema, obj);
		return ddl;
	}

	@Override
	public String exportAlterTableAddColumn(Connection conn, String schema, String tableName, String newCol) {
		String sql = "ALTER TABLE " + schema + "." + tableName + " ADD    " + newCol + ";";
		sql += "CALL SYSPROC.ADMIN_CMD('reorg  TABLE " + schema + "." + tableName + " ') ;";
		return sql;

	}

	@Override
	public String exportAlterTableDropColumn(Connection conn, String schema, String tableName, String col) {
		String sql = "ALTER TABLE " + schema + "." + tableName + " DROP COLUMN   " + col + ";";
		sql += "CALL SYSPROC.ADMIN_CMD('reorg  TABLE " + schema + "." + tableName + " ') ;";
		return sql;
	}

	@Override
	public String exportAlterTableModifyColumn(Connection conn, String schema, String tableName, String col) {

		String tmp = col.trim().replaceFirst(" ", "  SET DATA TYPE ");
		String sql = "ALTER TABLE " + schema + "." + tableName + "  ALTER  " + tmp + ";";
		sql += "\n CALL SYSPROC.ADMIN_CMD('reorg  TABLE " + schema + "." + tableName + " ') ;";
		return sql;
	}

	// 目前没用
	@Override
	public String exportAlterTableAddPrimaryKey(Connection conn, String schema, String tableName, String key) {
		return null;
	}

	// 目前没用
	@Override
	public String exportAlterTableAddForeignKey(Connection conn, String schema, String tableName, String key) {
		return null;
	}

	@Override
	public String exportDropTable(String schema, String name) {
		String sql = "DROP TABLE " + schema + "." + name.trim();
		return sql;
	}

	@Override
	public String exportDropView(String schema, String name) {
		String sql = "DROP VIEW " + schema + "." + name.trim();
		return sql;
	}

	@Override
	public String exportDropFunction(String schema, String name) {
		String sql = "DROP  FUNCTION " + schema + "." + name.trim();
		return sql;
	}

	@Override
	public String exportDropProcedure(String schema, String name) {
		String sql = "DROP  PROCEDURE " + schema + "." + name.trim();
		return sql;
	}

	@Override
	public String exportDropIndex(String schema, String name, String tableName) {
		String sql = "DROP INDEX " + schema + "." + name.trim();
		return sql;
	}

	@Override
	public String exportDropSequence(String schema, String name) {
		String sql = "DROP SEQUENCE " + schema + "." + name.trim() + " RESTRICT";
		return sql;
	}

	@Override
	public String exportDropTrigger(String schema, String name) {
		String sql = "DROP TRIGGER " + schema + "." + name.trim();
		return sql;
	}

	// 目前没用
	@Override
	public String exportDropPrimaryKey(String schema, String name) {
		return null;
	}

	@Override
	public String exportDropForeignKey(String schema, String foreignKeyName, String tableName) {
//		ALTER TABLE 子表  DROP CONSTRAINT 外键名称 ;
		String sql = "ALTER TABLE " + tableName + " DROP CONSTRAINT " + foreignKeyName;

//		sql += "\n CALL SYSPROC.ADMIN_CMD('reorg  TABLE " + schema + "." + tableName + " ') ;";
		return sql;
	}

	@Override
	public String exportCallFuncSql(String funcStr) {
		String sql = "values " + funcStr;
		return sql;
	}

	// TODO 表格索引
	@Override
	public List<TableIndexPo> tableIndex(Connection conn, String schema, String tableName) {
		String sql = "select *  from syscat.indexes where   INDSCHEMA = '" + schema + "'   " + "	 and TABNAME = '"
				+ tableName + "' " + "	 and UNIQUERULE <> 'P'";
		ResultSet rs = null;
		Statement sm = null;
		List<TableIndexPo> ls = new ArrayList<>();

		try {
			sm = conn.createStatement();
			rs = sm.executeQuery(sql);

			while (rs.next()) {
				TableIndexPo po = new TableIndexPo();
//				private String indname; // INDNAME 索引名称
//				private String tabname;  // TABNAME 表名
//				private String indschema; // INDSCHEMA 索引schema
//				private String colnames; // COLNAMES 索引的列
				po.setIndname(rs.getString("INDNAME"));
				po.setTabname(rs.getString("TABNAME"));
				po.setIndschema(rs.getString("INDSCHEMA"));
				po.setColnames(rs.getString("COLNAMES"));
				ls.add(po);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ls;
	}

	@Override
	public List<TableForeignKeyPo> tableForeignKey(Connection conn, String schema, String tableName) {

		String sql = "select *  from syscat.references where   TABSCHEMA = '" + schema + "'   " + "	 and TABNAME = '"
				+ tableName + "' \n" + " and TABSCHEMA = '" + schema + "' \n";
		ResultSet rs = null;
		Statement sm = null;
		List<TableForeignKeyPo> ls = new ArrayList<>();

		try {
			sm = conn.createStatement();
			rs = sm.executeQuery(sql);

			while (rs.next()) {
				TableForeignKeyPo po = new TableForeignKeyPo();

//				private String tabName; // TABNAME
//				private String constname; // CONSTNAME 	约束名称 外键名称
//				private String fkColnames; // FK_COLNAMES	外键字段
//				private String refTabname; // REFTABNAME 引用表名称 (主表)
//				private String pkColnames; //	PK_COLNAMES	引用表字段名称(就是主表的主键)
//				private String refKeyname;  // REFKEYNAME 主表的主键名称

				po.setTabName(rs.getString("TABNAME"));
				po.setConstname(rs.getString("CONSTNAME"));
				po.setFkColnames(rs.getString("FK_COLNAMES"));
				po.setRefTabname(rs.getString("REFTABNAME"));

				po.setPkColnames(rs.getString("PK_COLNAMES"));
				po.setRefKeyname(rs.getString("REFKEYNAME"));
				ls.add(po);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ls;

	}

}
