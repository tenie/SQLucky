package net.tenie.plugin.MariadbConnector.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.db.ExportDBObjects;
import net.tenie.Sqlucky.sdk.po.db.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.db.TableForeignKeyPo;
import net.tenie.Sqlucky.sdk.po.db.TableIndexPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
import net.tenie.Sqlucky.sdk.utility.FetchDBInfoCommonTools;


/**
 * 
 * @author tenie
 *
 */
public class ExportSqlMariadbImp implements ExportDBObjects {
	private static Logger logger = LogManager.getLogger(ExportSqlMariadbImp.class);
	private FetchDBInfoCommonTools fdb2;
	public ExportSqlMariadbImp() {
		fdb2 = new FetchDBInfoCommonTools();

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
//						Dbinfo.fetchTableInfo(conn, v);
//						// 表对象 主键赋值
//						Dbinfo.fetchTablePrimaryKeys(conn, v);
//						// 表对象ddl语句
//						String ddl = fdb2.createTab(v);
//						v.setDdl(ddl);
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
			if (vals != null && vals.size() > 0) {
				vals.stream().forEach(v -> {
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
	public List<FuncProcTriggerPo> allFunctionObj(Connection conn, String schema) {
		try {
			// 函数名称
			List<FuncProcTriggerPo> vals = Dbinfo.fetchAllFunctions(conn, schema);
			if (vals != null && vals.size() > 0) {
				vals.forEach(v -> {
					String ddl = exportCreateFunction(conn, schema, v.getName());
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
	 * 过程对象
	 */
	@Override
	public List<FuncProcTriggerPo> allProcedureObj(Connection conn, String schema) {
		try {
			// 函数名称
			String sql = "  select  name,  comment  from mysql.proc where db = '" + schema
					+ "' and `type` = 'PROCEDURE'";
			List<FuncProcTriggerPo> vals = fetchAllProcedures(conn, schema, sql);
			if (vals != null && vals.size() > 0) {
				vals.forEach(v -> {
					String ddl = exportCreateProcedure(conn, schema, v.getName());
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
	 * 触发器对象
	 */
	@Override
	public List<FuncProcTriggerPo> allTriggerObj(Connection conn, String schema) {
		try {
			// 函数名称
			List<FuncProcTriggerPo> vals = Dbinfo.fetchAllTriggers(conn, schema);
			if (vals != null && vals.size() > 0) {
				vals.forEach(v -> {
					String ddl = exportCreateTrigger(conn, schema, v.getName());
					v.setDdl(ddl);
				});
			}

			return vals;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
//
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
		String sql = "SHOW CREATE TABLE " + schema + "." + tab;
		String ddl = getddlHelper(conn, sql, 2);
		return ddl;
	}

	@Override
	public String exportCreateView(Connection conn, String schema, String obj) {
		String sql = "SHOW CREATE VIEW  " + schema + "." + obj;
		String ddl = getddlHelper(conn, sql, 2);
		return ddl;
	}

	@Override
	public String exportCreateFunction(Connection conn, String schema, String obj) {
		String sql = "SHOW CREATE FUNCTION  " + schema + "." + obj;
		String ddl = getddlHelper(conn, sql, 3);
		return ddl;
	}

	@Override
	public String exportCreateProcedure(Connection conn, String schema, String obj) {
		String sql = "SHOW CREATE PROCEDURE  " + schema + "." + obj;
		String ddl = getddlHelper(conn, sql, 3);
		logger.info(ddl);
		return ddl;
	}

	@Override
	public String exportCreateIndex(Connection conn, String schema, String obj) { 
		String sql = "SELECT \n" + 
				"CONCAT('ALTER TABLE `',TABLE_NAME,'` ', 'ADD ', \n" + 
				"IF( any_value(NON_UNIQUE )= 1, \n" + 
				"CASE UPPER(any_value(INDEX_TYPE)) \n" + 
				"WHEN 'FULLTEXT' THEN 'FULLTEXT INDEX' \n" + 
				"WHEN 'SPATIAL' THEN 'SPATIAL INDEX' \n" + 
				"ELSE CONCAT('INDEX `', \n" + 
				"INDEX_NAME, \n" + 
				"'` USING ', \n" + 
				"any_value(INDEX_TYPE) \n" + 
				") \n" + 
				"END, \n" + 
				"IF(UPPER(INDEX_NAME) = 'PRIMARY', \n" + 
				"CONCAT('PRIMARY KEY USING ', \n" + 
				"any_value(INDEX_TYPE) \n" + 
				"), \n" + 
				"CONCAT('UNIQUE INDEX `', \n" + 
				"INDEX_NAME, \n" + 
				"'` USING ', \n" + 
				"any_value(INDEX_TYPE) \n" + 
				") \n" + 
				") \n" + 
				"),'(', GROUP_CONCAT(DISTINCT CONCAT('`', COLUMN_NAME, '`') ORDER BY SEQ_IN_INDEX ASC SEPARATOR ', '), ');') AS 'Show_Add_Indexes' \n" + 
				"FROM information_schema.STATISTICS \n" + 
				"WHERE  \n" + 
				"      TABLE_SCHEMA = '"+schema+"'  \n" + 
				"  AND INDEX_NAME='"+obj+"' \n" + 
				"  AND any_value(NON_UNIQUE )= 1 \n"+
				"GROUP BY TABLE_NAME, INDEX_NAME \n" + 
				"ORDER BY TABLE_NAME ASC, INDEX_NAME ASC";
		
		String ddl = getddlHelper(conn, sql, 1);
		return ddl;
	}

	@Override
	public String exportCreateSequence(Connection conn, String schema, String obj) {
		 
		return "";
	}

	@Override
	public String exportCreateTrigger(Connection conn, String schema, String obj) {
		String sql = "SHOW CREATE TRIGGER  " + schema + "." + obj;
		String ddl = getddlHelper(conn, sql, 3);
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
		return sql;
	}

	@Override
	public String exportAlterTableDropColumn(Connection conn, String schema, String tableName, String col) {
		String sql = "ALTER TABLE "+schema+"."+tableName+" DROP COLUMN   " + col +";";
		return sql;
	}

	@Override
	public String exportAlterTableModifyColumn(Connection conn, String schema, String tableName, String col) {
		String sql = "ALTER TABLE "+schema+"."+tableName+" MODIFY COLUMN  " + col +";";
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
		String sql = "DROP sequence " + schema + "." + name.trim() ;
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
	public String exportDropForeignKey(String schema, String foreignKeyName, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getddlHelper(Connection conn, String sql, int i) {

		String ddl = "";
		logger.info(sql);
		ResultSet rs = null;
		try {
			rs = conn.createStatement().executeQuery(sql);
			if (rs.next()) {
				ddl = rs.getString(i);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return ddl;
	}
	
	private List<String> getAllddlHelper(Connection conn, String sql, int i) {
		List<String> allDDl = new ArrayList<String>();
		String ddl = "";
		logger.info(sql);
		ResultSet rs = null;
		try {
			rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				ddl = rs.getString(i);
				allDDl.add(ddl);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return allDDl;
	}
	

	public static List<FuncProcTriggerPo> fetchAllProcedures(Connection conn, String schemaOrCatalog, String sql)
			throws Exception {
		List<FuncProcTriggerPo> ls = new ArrayList<FuncProcTriggerPo>();
		ResultSet rs = null;
		try {
			rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				String remarks = rs.getString("comment");
				FuncProcTriggerPo po = new FuncProcTriggerPo();
				po.setName(name);
				po.setRemarks(remarks);
				po.setSchema(schemaOrCatalog);
				ls.add(po);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return ls;
	}

	@Override
	public List<FuncProcTriggerPo> allIndexObj(Connection conn, String schema) {
		try {
			String str = 
					"select  INDEX_NAME from information_schema.STATISTICS  \n" + 
					"where TABLE_SCHEMA = '"+schema+"' \n" + 
					"and NON_UNIQUE = 1 \n" ;
			// 获取名称
			List<String> allDDLs = getAllddlHelper(conn, str, 1);
			List<FuncProcTriggerPo> vals =  new ArrayList<>();   // getAllddlHelper(conn, schema, 1); //Dbinfo.fetchAllViewName(conn, schema);
			if (allDDLs != null && allDDLs.size() > 0) {
				allDDLs.stream().forEach(v -> {
					FuncProcTriggerPo po = new FuncProcTriggerPo();
					po.setName(v);
					po.setSchema(schema);
					vals.add(po);
					
					// 视图ddl
//					String ddl = exportCreateView(conn, schema, v.getTableName());
//					v.setDdl(ddl);
				});
			}
			 
			return vals;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<FuncProcTriggerPo> allSequenceObj(Connection conn, String schema) {
		try {
			String str = 
					"select  INDEX_NAME from information_schema.STATISTICS  \n" + 
					"where TABLE_SCHEMA = '"+schema+"' \n" + 
					"and NON_UNIQUE = 1 \n" ;
			// 获取名称
			List<String> allDDLs = getAllddlHelper(conn, str, 1);
			List<FuncProcTriggerPo> vals =  new ArrayList<>();   // getAllddlHelper(conn, schema, 1); //Dbinfo.fetchAllViewName(conn, schema);
			if (allDDLs != null && allDDLs.size() > 0) {
				allDDLs.stream().forEach(v -> {
					FuncProcTriggerPo po = new FuncProcTriggerPo();
					po.setName(v);
					po.setSchema(schema);
					vals.add(po);
					// 视图ddl
//					String ddl = exportCreateView(conn, schema, v.getTableName());
//					v.setDdl(ddl);
				});
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
