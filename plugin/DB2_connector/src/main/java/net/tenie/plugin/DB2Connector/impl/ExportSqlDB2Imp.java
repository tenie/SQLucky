package net.tenie.plugin.DB2Connector.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import net.tenie.Sqlucky.sdk.db.ExportDDL;
import net.tenie.Sqlucky.sdk.po.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.TablePo;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
/* 
 *  @author tenie 
 *  
 */
public class ExportSqlDB2Imp implements ExportDDL {

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
			
//			if (vals != null && vals.size() > 0) {
//				vals.stream().forEach(v -> {
//					try {
//						// 表对象字段赋值
////						Dbinfo.fetchTableInfo(conn, v);
//						// 表对象 主键赋值
////						Dbinfo.fetchTablePrimaryKeys(conn, v);
//						// 表对象ddl语句
////						String ddl = fdb2.createTab(v);
////						v.setDdl(ddl);
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

	/**
	 * 视图对象
	 */
	@Override
	public List<TablePo> allViewObj(Connection conn, String schema) {
		try {
			// 获取视图名称
			List<TablePo> vals = Dbinfo.fetchAllViewName(conn, schema);
//			if (vals != null && vals.size() > 0) {
//				vals.stream().forEach(v -> {
//					// 视图ddl
//					String ddl = exportCreateView(conn, schema, v.getTableName());
//					v.setDdl(ddl);
//				});
//			}
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
//			if (vals != null && vals.size() > 0) {
//				vals.forEach(v -> {
//					String ddl = exportCreateFunction(conn, schema, v.getName());
//					v.setDdl(ddl);
//				});
//			}

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
//			if (vals != null && vals.size() > 0) {
//				vals.forEach(v -> {
//					String ddl = exportCreateProcedure(conn, schema, v.getName());
//					v.setDdl(ddl);
//				});
//			}

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
			 for(String name : names) {
				    FuncProcTriggerPo po = new FuncProcTriggerPo();
					po.setName(name);
					po.setSchema(schema);
					vals.add(po);
			 }
			
//			List<FuncProcTriggerPo> vals = Dbinfo.fetchAllTriggers(conn, schema);
//			if (vals != null && vals.size() > 0) {
//				vals.forEach(v -> {
//					String ddl = exportCreateTrigger(conn, schema, v.getName());
//					v.setDdl(ddl);
//				});
//			}

			return vals;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 索引
	 */
	@Override
	public List<FuncProcTriggerPo> allIndexObj(Connection conn, String schema) {
		try {
			// 名称
			List<String> names = fdb2.getIndexs(conn, schema);
			List<FuncProcTriggerPo> vals = new ArrayList<>();
			for(String name : names ) {
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
//			fdb2.getSeq(conn, schema)
			List<String> names =fdb2.getSeq(conn, schema);
			List<FuncProcTriggerPo> vals = new ArrayList<>();
			for(String name : names ) {
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


//	@Override
//	public List<String> allIndexName(Connection conn, String schema) {
//		return fdb2.getIndexs(conn, schema);
//	}
//
//	@Override
//	public List<String> allSequenceName(Connection conn, String schema) {
//		return fdb2.exportAllSeqs(conn, schema);
//	}
//
//	@Override
//	public List<String> allForeignKeyName(Connection conn, String schema) {
//		return fdb2.exportAllForeignKeys(conn, schema);
//	}
//
//	@Override
//	public List<String> allPrimaryKeyName(Connection conn, String schema) {
//		// TODO 先不需要
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

	@Override
	public String exportCreatePrimaryKey(Connection conn, String schema, String obj) {
		// TODO 暂时不用
		return "";
	}

	@Override
	public String exportCreateForeignKey(Connection conn, String schema, String obj) {
		String ddl = fdb2.exportForeignKey(conn, schema, obj);
		return ddl;
	}

	@Override
	public String exportAlterTableAddColumn(Connection conn, String schema, String tableName, String newCol) {
		String sql = "ALTER TABLE "+schema+"."+tableName+" ADD    " + newCol +";";
		sql += "CALL SYSPROC.ADMIN_CMD('reorg  TABLE " + schema + "." + tableName + " ') ;";
		return sql;
		 
	}

	@Override
	public String exportAlterTableDropColumn(Connection conn, String schema, String tableName, String col) {
		String sql = "ALTER TABLE "+schema+"."+tableName+" DROP COLUMN   " + col +";";
		sql += "CALL SYSPROC.ADMIN_CMD('reorg  TABLE " + schema + "." + tableName + " ') ;";
		return sql;
	}

	@Override 
	public String exportAlterTableModifyColumn(Connection conn, String schema, String tableName, String col) {

		String tmp = col.trim().replaceFirst(" ", "  SET DATA TYPE "); 
		String sql = "ALTER TABLE "+schema+"."+tableName+"  ALTER  " + tmp +";";
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
	public String exportDropIndex(String schema, String name) {
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
	
	@Override
	public String exportCallFuncSql(String funcStr) {
		String sql = "values "+funcStr;
		return sql;
	}

	

}
