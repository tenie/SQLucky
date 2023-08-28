package net.tenie.plugin.DB2Connector.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.db.DBTools;
import net.tenie.Sqlucky.sdk.db.Dbinfo;
import net.tenie.Sqlucky.sdk.po.RsData;
import net.tenie.Sqlucky.sdk.po.myEntry;
import net.tenie.Sqlucky.sdk.po.db.TableFieldPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.po.db.TablePrimaryKeysPo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
/* 
 *  * @author tenie 
 *  
 */
public class FetchDB2InfoImp {
	private static Logger logger = LogManager.getLogger(FetchDB2InfoImp.class);

	// 执行sql只返回第一个字段的list
	private List<String> execSQL(Connection conn, String sql) {
		ResultSet rs = null;
		List<String> ls = new ArrayList<String>();
		try {
			rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				String sequence = rs.getString(1);
				ls.add(sequence);
			}
		} catch (Exception e) {
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

	// 执行sql只返回第2个字段的list
	private List<myEntry<String, String>> execSQL2(Connection conn, String sql) {

		ResultSet rs = null;
		List<myEntry<String, String>> ls = new ArrayList<myEntry<String, String>>();
		try {
			rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {

				String v1 = rs.getString(1);
				String v2 = rs.getString(2);
				myEntry<String, String> e = new myEntry<>(v1, v2);
				ls.add(e);
			}
		} catch (Exception e) {
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

	// 执行sql只返回第一个字段的list
	private String selectOne(Connection conn, String sql) {
		ResultSet rs = null;
		String str = "";
		try {
			rs = conn.createStatement().executeQuery(sql);
			if (rs.next()) {
				str = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return str;
	}

	/**
	 * 获取所有表, 并包含字段信息
	 */

	public List<TablePo> getTables(Connection db2conn, String schema) {
		List<TablePo> ls = null;
		try {
			ls = Dbinfo.fetchAllTableName(db2conn, schema);
			for (TablePo tabpo : ls) {
				Dbinfo.fetchTableInfo(db2conn, tabpo);
				Dbinfo.fetchTablePrimaryKeys(db2conn, tabpo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ls;
	}

	private String getTypeLength(TableFieldPo po) {
		// type长度
		Integer length = +po.getLength();
		Integer scale = +po.getScale();
		String typeLength = " (" + length;
		//
		if ("BIGINT".equals(po.getType()) || "SMALLINT".equals(po.getType()) || "REAL".equals(po.getType())
				|| "DATE".equals(po.getType()) || "TIMESTAMP".equals(po.getType()) || "INTEGER".equals(po.getType())
				|| "DOUBLE".equals(po.getType())) {
			typeLength = " ";
		} else {
			if (scale != null && scale > 0) {
				typeLength += "," + scale + ")";
			} else {
				typeLength += ") ";
			}
		}
		return typeLength;
	}

	// 建表语句生成
	public String createTab(Connection conn, TablePo tab) throws SQLException {
		String sql = createTab(tab);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public String createTab(String schema, TablePo tab) {
		Set<TableFieldPo> fls = tab.getFields();
		String tableName = tab.getTableName();
 

		String sql = "CREATE TABLE " + schema+"."+tableName + " ( \n";
		String keysql = "";
		// 字段
		for (TableFieldPo po : fls) {
			// not null
			String notnull = "N".equals(po.getIsNullable()) ? "not null " : " ";
			// default
			String defVal = StrUtils.isNullOrEmpty(po.getDefaultVal()) ? "" : " default " + po.getDefaultVal();

			// 字段类型type长度
			String typeLength = getTypeLength(po);

			String fieldName = po.getFieldName();
			sql += "	" + fieldName + " " + po.getType() + typeLength + notnull + " " + defVal + " ,\n";

		}
		// 获取主键
		List<TablePrimaryKeysPo> ls = tab.getPrimaryKeys();
		String pkn = "";
		if (ls.size() > 0) {
			for (TablePrimaryKeysPo kp : ls) {
				keysql += kp.getColumnName() + ",";
				pkn = kp.getPkName();
			}
		}
		// 有主键就加上
		if (!StrUtils.isNullOrEmpty(keysql)) {
//			String keyName = pkn; 
			keysql = " PRIMARY KEY ( " + keysql.substring(0, keysql.length() - 1) + " )";
			
			if(StrUtils.isNotNullOrEmpty(pkn)) {
				keysql =  " CONSTRAINT " + pkn +  keysql;
			}
			 
			sql += keysql + " \n" + ") \n";
		} else {
			sql = sql.substring(0, sql.length() - 2) + " \n )";
		}
		 
		return sql;
	} 
	public String createTab(TablePo tab) {
		Set<TableFieldPo> fls = tab.getFields();
		String tableName = tab.getTableName();  
		String sql = "CREATE TABLE " + tableName + " ( \n";
		String keysql = "";
		// 字段
		for (TableFieldPo po : fls) {
			// not null
			String notnull = "N".equals(po.getIsNullable()) ? "not null " : " ";
			// default
			String defVal = StrUtils.isNullOrEmpty(po.getDefaultVal()) ? "" : " default " + po.getDefaultVal();

			// 字段类型type长度
			String typeLength = getTypeLength(po);

			String fieldName = po.getFieldName();
			sql += "	" + fieldName + " " + po.getType() + typeLength + notnull + " " + defVal + " ,\n";
  
		}
		// 获取主键
		List<TablePrimaryKeysPo> ls = tab.getPrimaryKeys();
		String pkn = "";
		if (ls.size() > 0) {
			for (TablePrimaryKeysPo kp : ls) {
				keysql += kp.getColumnName() + ",";
				pkn = kp.getPkName();
			}
		}
		// 有主键就加上
		if (!StrUtils.isNullOrEmpty(keysql)) {
			String keyName = pkn; // "P"+ tableName.substring(tableName.indexOf("_") );
			keysql = " CONSTRAINT " + keyName + " PRIMARY KEY ( " + keysql.substring(0, keysql.length() - 1) + " )";
			sql += keysql + " \n" + ") \n";
		} else {
			sql = sql.substring(0, sql.length() - 2) + " \n )";
		}
		String seq = ";";
		 

		return sql + seq;
	}
	public String createTab_ft(TablePo tab) {
		Set<TableFieldPo> fls = tab.getFields();
		String tableName = tab.getTableName();

		Boolean hasItemID = false;

		String sql = "CREATE TABLE " + tableName + " ( \n";
		String keysql = "";
		// 字段
		for (TableFieldPo po : fls) {
			// not null
			String notnull = "N".equals(po.getIsNullable()) ? "not null " : " ";
			// default
			String defVal = StrUtils.isNullOrEmpty(po.getDefaultVal()) ? "" : " default " + po.getDefaultVal();

			// 字段类型type长度
			String typeLength = getTypeLength(po);

			String fieldName = po.getFieldName();
			sql += "	" + fieldName + " " + po.getType() + typeLength + notnull + " " + defVal + " ,\n";

			if ("ITEM_ID".equals(fieldName.toUpperCase())) {
				hasItemID = true;
			}

		}
		// 获取主键
		List<TablePrimaryKeysPo> ls = tab.getPrimaryKeys();
		String pkn = "";
		if (ls.size() > 0) {
			for (TablePrimaryKeysPo kp : ls) {
				keysql += kp.getColumnName() + ",";
				pkn = kp.getPkName();
			}
		}
		// 有主键就加上
		if (!StrUtils.isNullOrEmpty(keysql)) {
			String keyName = pkn; // "P"+ tableName.substring(tableName.indexOf("_") );
			keysql = " CONSTRAINT " + keyName + " PRIMARY KEY ( " + keysql.substring(0, keysql.length() - 1) + " )";
			sql += keysql + " \n" + ") \n";
		} else {
			sql = sql.substring(0, sql.length() - 2) + " \n )";
		}
		String seq = ";";
		if (hasItemID) {
			// 生成seq
			// CREATE SEQUENCE SEQ_CUSTOMER_SERIES_TYPE_DET AS BIGINT INCREMENT BY 1 START
			// WITH 1 MAXVALUE 9999999999
			String seqName = "SEQ" + tableName.substring(tableName.indexOf("_"));
			seq = "; \n CREATE SEQUENCE " + seqName + " 	AS BIGINT INCREMENT BY 1 START WITH 1 MAXVALUE 9999999999;";
		}

		return sql + seq;
	}

	// 添加字段

	public String alterTabAddColumn(Connection conn, TableFieldPo po) throws SQLException {
		String sql = alterTabAddColumn(conn, po);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public String alterTabAddColumn(TableFieldPo po) {
		// default
		String notnull = "N".equals(po.getIsNullable()) ? "not null " : " ";
		String defVal = StrUtils.isNullOrEmpty(po.getDefaultVal()) ? ""
				: " " + notnull + " default " + po.getDefaultVal();

		// type长度
		String typeLength = getTypeLength(po);

		String sql = "alter table " + po.getTableName() + " add column " + po.getFieldName() + " " + po.getType()
				+ typeLength + defVal + " ;";
		return sql;
	}

	// 删除字段

	public String alterTabDropColumn(Connection conn, TableFieldPo po) throws SQLException {
		String Sql = alterTabDropColumn(po);
		DBTools.execDDL(conn, Sql);
		return Sql;
	}

	public String alterTabDropColumn(TableFieldPo po) {
		String sql = "alter table " + po.getTableName() + " drop column " + po.getFieldName() + " ;";
		return sql;
	}

	// 修改字段

	public String alterTabModifyColumn(Connection conn, TableFieldPo po) throws SQLException {
		String Sql = alterTabModifyColumn(po);
		DBTools.execDDL(conn, Sql);
		return Sql;
	}

	public String alterTabModifyColumn(TableFieldPo po) {
		// type长度
		String typeLength = getTypeLength(po);
		String sql = "ALTER TABLE " + po.getTableName() + " ALTER COLUMN " + po.getFieldName() + "  set data type "
				+ po.getType() + typeLength + " ;";

		return sql;
	}
	// 添加组件

	public String alterTabAddPriMaryKey(String schema, String table, String kn, String fields) {
		String Sql = "alter table " + schema + "." + table + "aaa add constraint " + kn + " primary key(" + fields
				+ ")";
		return Sql;
	}

	public String alterTabAddPriMaryKey(Connection conn, String schema, String table, String kn, String fields)
			throws SQLException {
		String Sql = alterTabAddPriMaryKey(schema, table, kn, fields);
		DBTools.execDDL(conn, Sql);
		return Sql;
	}

	/**
	 * 找到目标数据库中没有的表
	 */

	public List<TablePo> findNewTab(List<TablePo> source, List<TablePo> tag) {
		List<TablePo> tabNames = new ArrayList<TablePo>();
		for (TablePo soTab : source) {
			if (!tag.contains(soTab)) {
				tabNames.add(soTab);
			}
			;
		}
		return tabNames;
	}

	public List<TablePo> findMyTab(List<TablePo> source, List<TablePo> tag) {
		List<TablePo> tabNames = new ArrayList<TablePo>();
		for (TablePo soTab : tag) {
			if (!source.contains(soTab)) {
				tabNames.add(soTab);
			}
			;
		}
		return tabNames;
	}

	/**
	 * 字段判断, 两组表, 返回有差异的字段集合, 返回的虽然是字段, 但TableFieldPo带有表信息
	 */

	public List<TableFieldPo> findNewField(List<TablePo> source, List<TablePo> tag) {
		List<TableFieldPo> newFiled = new ArrayList<TableFieldPo>();

		int tidx = -1;
		for (TablePo tp : source) {
			tidx = tag.indexOf(tp); // 找到目标集中的表
			if (tidx >= 0) {
				TablePo tagpo = tag.get(tidx);
				// 获取2个表中的字段
				Set<TableFieldPo> sof = tp.getFields();
				Set<TableFieldPo> taf = tagpo.getFields();
				sof.removeAll(taf);
				// 取差值后 比较, 如果 目标表有字段就做标记字段要修改,
				if (sof != null && sof.size() > 0) {

					List<TableFieldPo> rml = new ArrayList<TableFieldPo>();
					for (TableFieldPo sfieldPO : sof) {
						String fieldN = sfieldPO.getFieldName();
						for (TableFieldPo tfp : taf) {
							if (tfp.getFieldName().contentEquals(fieldN)) {
								if (tfp.getType().toUpperCase().contains("CHAR")) {
									if (tfp.getLength().equals(sfieldPO.getLength())) {
										rml.add(sfieldPO);
									}
								}
								sfieldPO.setShort(true);

							}
						}
					}
					sof.removeAll(rml);
					newFiled.addAll(sof);
				}

			} else {

			}
		}

		return newFiled;
	}

 

	/**
	 * 批处理插入数据 INSERT
	 */

	public void moveData(Connection soconn, Connection tagconn, TablePo soTab) {

		String fields = " ";
		String fieldName = "";
		String ty = "";
		String tabName = soTab.getTableName();
		Set<TableFieldPo> fs = soTab.getFields();
		for (TableFieldPo fpo : fs) {
			fields += fpo.getFieldName() + ",";
		}
		fields = fields.substring(0, fields.length() - 1);

		// SQL select
		String select = "select " + fields + " from " + tabName;
		String inster = "";
		ResultSet rs = null;
		Statement stmt = null;
		try {
			// tag清空数据
			DBTools.execDelTab(tagconn, tabName);
			rs = soconn.createStatement().executeQuery(select);
			stmt = tagconn.createStatement();
			int idx = 0;
			while (rs.next()) {
				idx++;
				inster = "INSERT INTO " + tabName + " ( " + fields + " ) VALUES ( ";
				for (TableFieldPo fpo : fs) {
					fieldName = fpo.getFieldName();
					ty = fpo.getType();
					if (rs.getObject(fieldName) == null) {
						inster += rs.getObject(fieldName) + ",";
					} else if (ty.contains("BLOB") || ty.contains("CLOB") || ty.contains("CHAR") || ty.contains("DATE")
							|| ty.contains("TIMESTAMP")) {
						inster += "'" + rs.getObject(fieldName) + "',";
					} else {
						inster += rs.getObject(fieldName) + ",";
					}
				}
				inster = inster.substring(0, inster.length() - 1) + " )";

				stmt.addBatch(inster);
				if (idx % 2500 == 0) {
					logger.info(inster);
					int[] count = stmt.executeBatch();
					logger.info("instert = " + count.length);
				}
			}

			if (idx % 2500 > 0) {
				logger.info(inster);
				int[] count = stmt.executeBatch();
				logger.info("instert = " + count.length);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Integer statisticRow(Connection tagconn, TablePo soTab) {
		ResultSet rs = null;
		Integer retI = -1;
		try {
			String select = " select count(*) from " + soTab.getTableName();
			rs = tagconn.createStatement().executeQuery(select);
			if (rs.next()) {
				retI = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return retI;
	}

	public List<String> exportAllSeqs(Connection tagconn, String schema) {
		String select = "  SELECT  \n" + "    CONCAT(  \n" + "    CONCAT(  \n" + "    CONCAT(  \n" + "    CONCAT( \n"
				+ "    CONCAT(  \n" + "    CONCAT(      \n" + "    CONCAT(   \n" + "    CONCAT( \n" + "    CONCAT( \n"
				+ "    CONCAT(  \n" + "    CONCAT( \n" + "        'CREATE OR REPLACE SEQUENCE ' \n"
				+ "     ,  SEQNAME) \n" + "     ,  ' AS BIGINT  START WITH ') \n"
				+ "     ,  CASE WHEN LASTASSIGNEDVAL IS NULL  THEN CHAR(BIGINT(START)+1)  ELSE CHAR(BIGINT(LASTASSIGNEDVAL)+1)  END ) \n"
				+ "     ,  ' INCREMENT  BY ' )  \n" + "     ,  CHAR(BIGINT(INCREMENT)) ) \n"
				+ "     , ' MINVALUE ' ) \n" + "     ,  CHAR(BIGINT( MINVALUE)) ) \n" + "     ,' MAXVALUE ' ) \n"
				+ "     ,CHAR(BIGINT(  MAXVALUE )) ) \n" + "     ,  ' ORDER ' ) \n" + "     , ';') \n" + "   FROM    \n"
				+ "     SYSIBM.SYSSEQUENCES \n" + " WHERE \n" + "     SEQSCHEMA = '" + schema + "'  \n";
		return execSQL(tagconn, select);
	}

	public String exportSeq(Connection tagconn, String schema, String seq) {
		String select = "  SELECT  \n" + "    CONCAT(  \n" + "    CONCAT(  \n" + "    CONCAT(  \n" + "    CONCAT( \n"
				+ "    CONCAT(  \n" + "    CONCAT(      \n" + "    CONCAT(   \n" + "    CONCAT( \n" + "    CONCAT( \n"
				+ "    CONCAT(  \n" + "    CONCAT( \n" + "        'CREATE OR REPLACE SEQUENCE ' \n"
				+ "     ,  SEQNAME) \n" + "     ,  ' AS BIGINT  START WITH ') \n"
				+ "     ,  CASE WHEN LASTASSIGNEDVAL IS NULL  THEN CHAR(BIGINT(START)+1)  ELSE CHAR(BIGINT(LASTASSIGNEDVAL)+1)  END ) \n"
				+ "     ,  ' INCREMENT  BY ' )  \n" + "     ,  CHAR(BIGINT(INCREMENT)) ) \n"
				+ "     , ' MINVALUE ' ) \n" + "     ,  CHAR(BIGINT( MINVALUE)) ) \n" + "     ,' MAXVALUE ' ) \n"
				+ "     ,CHAR(BIGINT(  MAXVALUE )) ) \n" + "     ,  ' ORDER ' ) \n" + "     , ';') \n" + "   FROM    \n"
				+ "     SYSIBM.SYSSEQUENCES \n" + " WHERE \n" + "     SEQSCHEMA = '" + schema + "'  \n"
				+ "     and SEQNAME = '" + seq + "' ";

		return selectOne(tagconn, select);
	}

	public List<String> exportAllTriggers(Connection tagconn, String schema) {
		String select = "select TEXT  \n" + "FROM  SYSIBM.SYSTRIGGERS    \n" + "WHERE					   \n"
				+ "    SCHEMA = '" + schema + "'";
		return execSQL(tagconn, select);
	}

	public String exportTrigger(Connection tagconn, String schema, String tri) {
		String select = "select TEXT  \n" + "FROM  SYSIBM.SYSTRIGGERS    \n" + "WHERE					   \n"
				+ "    SCHEMA = '" + schema + "' " + "    and NAME = '" + tri + "' ";
		return selectOne(tagconn, select);
	}

	public List<String> exportAllProcedures(Connection tagconn, String schema) {
		String select = "select TEXT  \n" + "FROM    SYSCAT.ROUTINES  \n" + "WHERE					   \n"
				+ "     OWNER = '" + schema + "' \n" + "     and ROUTINETYPE = 'P' ";
		return execSQL(tagconn, select);
	}

	public String exportProcedure(Connection tagconn, String schema, String name) {
		String select = "select TEXT  \n" + "FROM    SYSCAT.ROUTINES  \n" + "WHERE					   \n"
				+ "     OWNER = '" + schema + "' " + "     and ROUTINETYPE = 'P' " + "     and ROUTINENAME = '" + name
				+ "' ";
		return selectOne(tagconn, select);
	}

	public List<String> exportAllFunctions(Connection tagconn, String schema) {
		String select = "select TEXT  \n" + "FROM    SYSCAT.ROUTINES  \n" + "WHERE					   \n"
				+ "     OWNER = '" + schema + "' \n" + "     and ROUTINETYPE = 'F' ";
		return execSQL(tagconn, select);
	}

	public String exportFunction(Connection tagconn, String schema, String name) {
		String select = "select TEXT  \n" + "FROM    SYSCAT.ROUTINES  \n" + "WHERE					   \n"
				+ "     OWNER = '" + schema + "' " + "     and ROUTINETYPE = 'F' " + "     and ROUTINENAME = '" + name
				+ "' ";
		return selectOne(tagconn, select);
	}

	public String dropTab(Connection conn, String schema, String tab) throws java.sql.SQLException {
		String sql = dropTab(schema, tab);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public String dropTab(String tab, String schema) {
		String sql = "DROP TABLE " + schema + "." + tab.trim();
		return sql;
	}

	public String dropView(String view, String schema) {
		String sql = "DROP VIEW " + schema + "." + view.trim();
		return sql;
	}

	public String dropView(Connection conn, String schema, String view) throws java.sql.SQLException {
		String sql = dropView(schema, view);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public String dropSeq(Connection conn, String schema, String Seq) throws java.sql.SQLException {
		String sql = dropSeq(schema, Seq);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public String dropSeq(String Seq, String schema) {
		String sql = "DROP sequence " + schema + "." + Seq.trim() + " RESTRICT";
		return sql;
	}

	public String dropTirgger(Connection conn, String schema, String Tirgger) throws java.sql.SQLException {
		String sql = dropTirgger(schema, Tirgger);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public String dropTirgger(String schema, String Tirgger) {
		String sql = "DROP TRIGGER " + schema + "." + Tirgger.trim();
		return sql;
	}

	public String dropProcedure(Connection conn, String schema, String procedure) throws java.sql.SQLException {
		String sql = dropProcedure(schema, procedure);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public String dropProcedure(String schema, String procedure) {
		String sql = "DROP  PROCEDURE " + schema + "." + procedure.trim();
		return sql;
	}

	public String dropFunction(String schema, String fun) {
		String sql = "DROP  FUNCTION " + schema + "." + fun.trim();
		return sql;
	}

	public String dropFunction(Connection conn, String schema, String fun) throws java.sql.SQLException {
		String sql = dropFunction(schema, fun);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public String dropIndex(Connection conn, String schema, String name) throws java.sql.SQLException {
		String sql = dropIndex(schema, name);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public String dropIndex(String schema, String name) {
		String sql = "DROP INDEX " + schema + "." + name.trim();
		return sql;
	}

	public String dropForeignKey(String schema, String table, String name) {
		String sql = "ALTER TABLE " + schema + "." + table.trim() + " DROP CONSTRAINT  " + name;
		return sql;
	}

	public String dropForeignKey(Connection conn, String schema, String table, String name) throws SQLException {
		String sql = dropForeignKey(schema, table, name);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public String execDropForeignKey(Connection conn, String schema, String name) throws SQLException {
		String sql = "select TABNAME " + "  from syscat.references where TABSCHEMA = '" + schema + "' and CONSTNAME = '"
				+ name + "' ";
		String tab = selectOne(conn, sql);
		sql = dropForeignKey(conn, schema, tab, name);
		return sql;
	}

	public String dropPrimaryKey(String schema, String table) {
		String sql = "ALTER TABLE " + schema + "." + table.trim() + " DROP PRIMARY KEY ";
		return sql;
	}

	public String dropPrimaryKey(Connection conn, String schema, String table) throws SQLException {
		String sql = dropPrimaryKey(schema, table);
		DBTools.execDDL(conn, sql);
		return sql;
	}

	public List<String> getSeq(Connection conn, String schema) {
		String select = "SELECT SEQNAME  FROM    \n" + " SYSIBM.SYSSEQUENCES \n" + " WHERE \n" + "     SEQSCHEMA = '"
				+ schema + "'  \n";
		List<String> ls = execSQL(conn, select);
		return ls;
	}

	public List<String> getTriggers(Connection conn, String schema) {
		String select = "select NAME  \n" 
				+ "FROM  SYSIBM.SYSTRIGGERS    \n" 
				+ "WHERE					   \n"
				+ "    SCHEMA = '" + schema + "'";
		List<String> ls = execSQL(conn, select);
		return ls;
	}

	public List<String> getProcedure(Connection conn, String schema) {
		String select = "select ROUTINENAME  \n" + "FROM   SYSCAT.ROUTINES    \n" + "WHERE					   \n"
				+ "    OWNER = '" + schema + "'" + "    and ROUTINETYPE = 'P' ";
		List<String> ls = execSQL(conn, select);
		return ls;
	}

	public List<String> getFunctions(Connection conn, String schema) {
		String select = "select ROUTINENAME  \n" + "FROM   SYSCAT.ROUTINES     \n" + "WHERE					   \n"
				+ "    OWNER = '" + schema + "'" + "    and ROUTINETYPE = 'F' ";
		List<String> ls = execSQL(conn, select);
		return ls;
	}

	// 根据类型导出index  , 3种类型
	// P(主键建表时候指定的)Implements primary key, 
	// U (类似主键,保证数据唯一性)Unique ; 
	// D 允许重复 Permits duplicates
	public List<String> getIndexs(Connection conn, String schema, String type) {
		String sql = "select INDNAME   " + " from syscat.indexes" + " where  INDSCHEMA = '" + schema + "' "
				+ "    and UNIQUERULE ='" + type + "'"; // db2类型有 P(主键建表时候指定的), U (类似主键,保证数据唯一性) D 普通索引
		List<String> ls = execSQL(conn, sql);
		return ls;
	}

	// 导出非主键的索引
	public List<String> getIndexs(Connection conn, String schema) {
		String sql = "select INDNAME   " + " from syscat.indexes" + " where  INDSCHEMA = '" + schema + "' "
				+ "    and UNIQUERULE <> 'P'"; // db2类型有 P(主键建表时候指定的)Implements primary key, U (类似主键,保证数据唯一性)Unique ;  D 允许重复 Permits duplicates
		List<String> ls = execSQL(conn, sql);
		return ls;
	}

	public List<String> exportAllIndexs(Connection conn, String schema) {
		String sql = "select UNIQUERULE, INDNAME, TABNAME, COLNAMES , INDEXTYPE, REVERSE_SCANS "
				+ " from syscat.indexes" + " where  INDSCHEMA = '" + schema + "' " + "    and UNIQUERULE <> 'P'";
		return createIndexSQL(conn, sql);

	}

	public String exportIndex(Connection conn, String schema, String name) {
		String sql = "select UNIQUERULE, INDNAME, TABNAME, COLNAMES , INDEXTYPE, REVERSE_SCANS "
				+ " from syscat.indexes" + " where  INDSCHEMA = '" + schema + "' " + "    and UNIQUERULE <> 'P'"
				+ "    and  INDNAME = '" + name + "'";
		String createSql = "";
		List<String> ls = createIndexSQL(conn, sql);
		if (ls != null && ls.size() > 0) {
			createSql = ls.get(0);
		}
		return createSql;
	}

	private List<String> createIndexSQL(Connection conn, String sql) {
		List<String> ls = new ArrayList<String>();
		try {
			List<RsData> rs = DBTools.selectSql(conn, sql);
			if (rs != null && rs.size() > 0)
				for (RsData dt : rs) {
					String UNIQUERULE = dt.getString("UNIQUERULE");
					String INDNAME = dt.getString("INDNAME");
					String TABNAME = dt.getString("TABNAME");
					String COLNAMES = dt.getString("COLNAMES");
					String INDEXTYPE = dt.getString("INDEXTYPE");
					String REVERSE_SCANS = dt.getString("REVERSE_SCANS");

					String createSql = "CREATE " + ("U".equals(UNIQUERULE) ? "UNIQUE" : " ") + " INDEX " + INDNAME
							+ " on " + TABNAME + " ( " + COLNAMES.replace("+", ",").substring(1) + " ) "
							+ ("CLUS".equals(INDEXTYPE) ? " cluster " : " ")
							+ ("N".equals(REVERSE_SCANS) ? " DISALLOW REVERSE SCANS " : "");
					ls.add(createSql);
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ls;

	}

	public List<String> getViews(Connection conn, String schema) {
		String sql = "select VIEWNAME  from SYSCAT.Views where VIEWSCHEMA = '" + schema + "'";
		List<String> ls = execSQL(conn, sql);
		return ls;
	}

	public List<String> exportAllViews(Connection conn, String schema) {
		String sql = "select TEXT  from SYSCAT.Views where VIEWSCHEMA = '" + schema + "'";
		List<String> ls = execSQL(conn, sql);
		return ls;
	}

	public String exportView(Connection conn, String schema, String viewname) {
		String sql = "select TEXT  from SYSCAT.Views where VIEWSCHEMA = '" + schema + "' " + " and VIEWNAME = '"
				+ viewname + "'";
		String ls = selectOne(conn, sql);
		return ls;
	}

	public List<myEntry<String, String>> getForeignKeys(Connection conn, String schema) {
		String sql = "select TABNAME, CONSTNAME   from syscat.references where TABSCHEMA = '" + schema + "' ";
		List<myEntry<String, String>> ls = execSQL2(conn, sql);
//		setForeignKey(ls);
		return ls;
	}

	private List<String> exportFk(Connection conn, String schema, String sql) {
		List<String> ls = new ArrayList<String>();
		List<RsData> rs;
		try {
			rs = DBTools.selectSql(conn, sql);
			if (rs != null && rs.size() > 0)
				for (RsData dt : rs) {
					String str = "ALTER TABLE " + schema + "." + dt.getString("TABNAME") + " ADD CONSTRAINT  "
							+ dt.getString("CONSTNAME") + " FOREIGN KEY ("
							+ StrUtils.StrPlitJoin(dt.getString("FK_COLNAMES"), " ", ",") + ") REFERENCES "
							+ dt.getString("REFTABNAME") + "("
							+ StrUtils.StrPlitJoin(dt.getString("PK_COLNAMES"), " ", ",") + ") " + "";
					if ("R".equals(dt.getString("DELETERULE"))) {
						str += " ON DELETE  RESTRICT ";
					}

					if ("C".equals(dt.getString("DELETERULE"))) {
						str += " ON DELETE  CASCADE ";
					}
					if ("A".equals(dt.getString("DELETERULE"))) {
						str += "  ";
					}

					if ("R".equals(dt.getString("UPDATERULE"))) {
						str += " ON UPDATE  RESTRICT ";
					}

					if ("C".equals(dt.getString("UPDATERULE"))) {
						str += " ON UPDATE  CASCADE ";
					}
					if ("A".equals(dt.getString("UPDATERULE"))) {
						str += "  ";
					}
					ls.add(str);
				}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ls;
	}

	public List<String> exportAllForeignKeys(Connection conn, String schema) {
		String sql = "select TABNAME, CONSTNAME, FK_COLNAMES, PK_COLNAMES, REFTABNAME, DELETERULE, UPDATERULE"
				+ "  from syscat.references where TABSCHEMA = '" + schema + "' ";

		return exportFk(conn, schema, sql);
	}

	public String exportForeignKey(Connection conn, String schema, String name) {
		String sql = "select TABNAME, CONSTNAME, FK_COLNAMES, PK_COLNAMES, REFTABNAME, DELETERULE, UPDATERULE, FK_COLNAMES,PK_COLNAMES"
				+ "  from syscat.references where TABSCHEMA = '" + schema + "' and CONSTNAME = '" + name + "' ";
		String str = "";
		List<String> ls = exportFk(conn, schema, sql);
		if (ls != null && ls.size() > 0) {
			str = ls.get(0);
		}
		return str;
	}

}
