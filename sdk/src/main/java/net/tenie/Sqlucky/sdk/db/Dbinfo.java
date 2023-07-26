package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.po.db.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.db.TableFieldPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.po.db.TablePrimaryKeysPo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 获取表的连接
 * 
 * @author tenie
 *
 */
public class Dbinfo {
	private static Logger logger = LogManager.getLogger(Dbinfo.class);

	private Connection conn = null;

	private String driver, url, us, ps;
//	private String sqliteDriver;

	// 加载 jar
	static {
//		URL u = new URL("jar:file:/path/to/pgjdbc2.jar!/");
////		String classname = "org.postgresql.Driver";
//		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
//		Driver d = (Driver)Class.forName(driver, true, ucl).newInstance();
//		DriverManager.registerDriver(new DriverShim(d));
//		DriverManager.getConnection("jdbc:postgresql://host/db", "user", "pw");
	}

//	public Dbinfo(String driver, String url, String us, String ps) {
//		this.driver = driver;
//		this.url = url;
//		this.us = us;
//		this.ps = ps;
//		this.sqliteDriver = "";
//	}
	public Dbinfo(String url, String us, String ps) {
		this.url = url;
		this.us = us;
		this.ps = ps;
//		this.sqliteDriver = "";
	}

	public Dbinfo() {
		this.driver = "";
		this.url = "";
		this.us = "";
		this.ps = "";
	}

	// 获取连接
	public Connection getconn() {
		if (conn == null) {
			try {
//				if(StrUtils.isNotNullOrEmpty(sqliteDriver)) {
//					conn = DriverManager.getConnection(sqliteDriver);
//				}else if( StrUtils.isNullOrEmpty(driver) )  {
//					conn = DriverManager.getConnection(url, us, ps);
//				}else {
//					Class.forName(driver).newInstance();
//					conn = DriverManager.getConnection(url, us, ps);
//				} 
				conn = DriverManager.getConnection(url, us, ps);

			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return conn;
	}

	public Connection getconn(String jdbcurl) {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection(jdbcurl);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return conn;
	}

	public static Connection getConnByJdbc(String jdbcurl) {
		var dbinfo = new Dbinfo();
		return dbinfo.getconn(jdbcurl);
	}

	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// jdbc方式: 获取主键
	public static void fetchTablePrimaryKeys(Connection conn, TablePo tbpo) throws Exception {
		ResultSet rs = null;
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			rs = dmd.getPrimaryKeys(null, tbpo.getTableSchema(), tbpo.getTableName());
			while (rs.next()) {
				TablePrimaryKeysPo po = new TablePrimaryKeysPo();
				String v1 = rs.getString("TABLE_CAT");
				po.setTableCat(v1);
				String v2 = rs.getString("TABLE_SCHEM");
				po.setTableSchem(v2);
				String v3 = rs.getString("TABLE_NAME");
				po.setTableName(v3);
				String v4 = rs.getString("COLUMN_NAME");
				po.setColumnName(v4);
				int v5 = rs.getInt("KEY_SEQ");
				po.setKeySeq(v5);
				String v6 = rs.getString("PK_NAME");
				po.setPkName(v6);
				tbpo.getPrimaryKeys().add(po);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	// jdbc方式: 获取schemas
//	public static Map<String, DbSchemaPo> fetchSchemasInfo(SqluckyConnector dbpo) throws Exception {
//		return fetchSchemasInfo(dbpo.getConn(), dbpo.getDbVendor());
//	}
//
//	public static Map<String, DbSchemaPo> fetchSchemasInfo(Connection conn) throws Exception {
//		return fetchSchemasInfo(conn, "");
//	}

	// jdbc方式: 获取schemas Connection conn ,String DbVendor
//	public static Map<String, DbSchemaPo> fetchSchemasInfo(Connection conn, String dbVendor) throws Exception {
//		ResultSet rs = null;
//		Map<String, DbSchemaPo> pos = new HashMap<String, DbSchemaPo>();
//		try {
//			DatabaseMetaData dmd = conn.getMetaData();
//			if (    DbVendor.mysql.toUpperCase().equals(dbVendor.toUpperCase())
//				||  DbVendor.mariadb.toUpperCase().equals(dbVendor.toUpperCase())
//					) {
//				rs = dmd.getCatalogs();
//			} else {
//				rs = dmd.getSchemas(); // 默认 db2
//			}
//
//			while (rs.next()) {
//				DbSchemaPo po = new DbSchemaPo();
//				String schema = rs.getString(1);
//				logger.info("fetchSchemasInfo(); schema=" + schema);
//				po.setSchemaName(schema);
//				pos.put(schema, po);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (rs != null)
//				rs.close();
//		}
//
//		return pos;
//	}

	// jdbc方式: 获取表的字段信息
	public static void fetchTableInfo(Connection conn, TablePo tbpo) throws Exception {
		ResultSet rs = null;
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			rs = dmd.getColumns(null, tbpo.getTableSchema(), tbpo.getTableName(), "%");
			while (rs.next()) {
				TableFieldPo fpo = new TableFieldPo();
				String v1 = rs.getString("TABLE_CAT");
				fpo.setTableCat(v1);
				String v2 = rs.getString("TABLE_SCHEM");
				fpo.setTableSchem(v2);
				String v3 = rs.getString("TABLE_NAME");
				fpo.setTableName(v3);
				String v4 = rs.getString("COLUMN_NAME");
				fpo.setColumnName(v4);
				fpo.setFieldName(v4);

				int v5 = rs.getInt("DATA_TYPE");
				fpo.setDataType(v5);

				String v6 = rs.getString("TYPE_NAME");
				fpo.setTypeName(v6);
				fpo.setType(v6);

				int v7 = rs.getInt("COLUMN_SIZE");
				fpo.setColumnSize(v7);
				fpo.setLength(Integer.valueOf(v7));

				String v8 = rs.getString("BUFFER_LENGTH");
				fpo.setBufferLength(v8);

				int v9 = rs.getInt("DECIMAL_DIGITS");
				fpo.setDecimalDigits(v9);
				fpo.setScale(v9);

				int v10 = rs.getInt("NUM_PREC_RADIX");
				fpo.setNumPrecRadix(v10);

				int v11 = rs.getInt("NULLABLE");
				fpo.setNullable(v11);
//					fpo.setIsNullable("0".equals(v11)? "N":"Y");

				String v12 = rs.getString("REMARKS");
				fpo.setRemarks(v12);

				String v13 = rs.getString("COLUMN_DEF"); // 默认值
				fpo.setColumnDef(v13);
				fpo.setDefaultVal(v13);

				int v14 = rs.getInt("SQL_DATA_TYPE");
				fpo.setSqlDataType(v14);
				int v15 = rs.getInt("SQL_DATETIME_SUB");
				fpo.setSqlDatetimeSub(v15);
				int v16 = rs.getInt("CHAR_OCTET_LENGTH");
				fpo.setCharOctetLength(v16);
				int v17 = rs.getInt("ORDINAL_POSITION");
				fpo.setOrdinalPosition(v17);

				String v18 = rs.getString("IS_NULLABLE");
				fpo.setIsNullable("NO".equals(v18) ? "N" : "Y");

//					String v19 = rs.getString("SCOPE_CATALOG"); 
				String v20 = rs.getString("SCOPE_SCHEMA");
				fpo.setScopeSchema(v20);

				String v21 = rs.getString("SCOPE_TABLE");
				fpo.setScopeTable(v21);
				int v22 = rs.getInt("SOURCE_DATA_TYPE");
				fpo.setSourceDataType(v22);
				String v23 = rs.getString("IS_AUTOINCREMENT");
				fpo.setIsAutoincrement(v23);
//					String v24 = rs.getString("IS_GENERATEDCOLUMN"); 
				tbpo.getFields().add(fpo);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	public static List<TablePo> fetchAllViewName(Connection conn, String schemaOrCatalog) throws Exception {
		return fetchAllTableViewName(conn, schemaOrCatalog, false);
	}

	public static List<TablePo> fetchAllViewName(Connection conn) throws Exception {
		return fetchAllTableViewName(conn, false);
	}

	public static List<TablePo> fetchAllTableName(Connection conn, String schemaOrCatalog) throws Exception {
		return fetchAllTableViewName(conn, schemaOrCatalog, true);
	}

	public static List<TablePo> fetchAllTableName(Connection conn) throws Exception {
		return fetchAllTableViewName(conn, true);
	}

	public static String getSchema(Connection conn) {
		String val = null;
		try {
			val = conn.getSchema();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return val;
	}

	public static String getCatalog(Connection conn) {
		String val = null;
		try {
			val = conn.getCatalog();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}

	// jdbc方式: 获取视图, 名称,
	public static List<TablePo> fetchAllTableViewName(Connection conn, String schemaOrCatalog, boolean istable)
			throws Exception {
		ResultSet tablesResultSet = null;
//		ResultSet rs = null;
		Statement sm = null;
		List<TablePo> tbls = new ArrayList<TablePo>();
		try {
			DatabaseMetaData dbMetaData = conn.getMetaData();
			String catalog = getCatalog(conn);// conn.getCatalog();
			String schema = getSchema(conn); // conn.getSchema();
			if (StrUtils.isNotNullOrEmpty(schemaOrCatalog)) {
				if (catalog == null) {
					schema = schemaOrCatalog;
				} else if (schema == null) {
					catalog = schemaOrCatalog;
				}
			}
			tablesResultSet = dbMetaData.getTables(catalog, schema, null, null);
			while (tablesResultSet.next()) {
				String tableType = tablesResultSet.getString("TABLE_TYPE");
				if (tableType == null) {
					tableType = "";
				}
				if (istable) {
					if (tableType.contains("VIEW")) {
						continue;
					}
				} else {
					if (!tableType.contains("VIEW")) {
						continue;
					}
				}

				String tableName = tablesResultSet.getString("TABLE_NAME");
				String remarks = tablesResultSet.getString("REMARKS");
				TablePo po = new TablePo();
				po.setTableName(tableName);
				po.setTableRemarks(remarks);
				po.setTableSchema(schemaOrCatalog);
				po.setTableType(tableType);
				LinkedHashSet<TableFieldPo> fields = new LinkedHashSet<TableFieldPo>();
				po.setFields(fields);
				ArrayList<TablePrimaryKeysPo> tpks = new ArrayList<TablePrimaryKeysPo>();
				po.setPrimaryKeys(tpks);
				tbls.add(po);
			}

		} finally {
			if (tablesResultSet != null)
				tablesResultSet.close();
			if (sm != null)
				sm.close();
		}
		return tbls;
	}

	public static List<TablePo> fetchAllTableViewName(Connection conn, boolean istable) throws Exception {
		ResultSet tablesResultSet = null;
//		ResultSet rs = null;
		Statement sm = null;
		List<TablePo> tbls = new ArrayList<TablePo>();
		try {
			DatabaseMetaData dbMetaData = conn.getMetaData();

			tablesResultSet = dbMetaData.getTables(null, null, null, null);
			while (tablesResultSet.next()) {
				String tableType = tablesResultSet.getString("TABLE_TYPE");
				if (tableType == null) {
					tableType = "";
				}
				if (istable) {
					if (tableType.contains("VIEW")) {
						continue;
					}
				} else {
					if (!tableType.contains("VIEW")) {
						continue;
					}
				}

				String tableName = tablesResultSet.getString("TABLE_NAME");
				String remarks = tablesResultSet.getString("REMARKS");
//				System.out.println("tableName = "+ tableName);
//				System.out.println("REMARKS = "+ remarks);
				TablePo po = new TablePo();
				po.setTableName(tableName);
				po.setTableRemarks(remarks);
				po.setTableSchema("");
				po.setTableType(tableType);
				LinkedHashSet<TableFieldPo> fields = new LinkedHashSet<TableFieldPo>();
				po.setFields(fields);
				ArrayList<TablePrimaryKeysPo> tpks = new ArrayList<TablePrimaryKeysPo>();
				po.setPrimaryKeys(tpks);
				tbls.add(po);
			}

		} finally {
			if (tablesResultSet != null)
				tablesResultSet.close();
//			if (rs != null)
//				rs.close();
			if (sm != null)
				sm.close();
		}
		return tbls;
	}

	public static TablePo fetchTableObjByName(Connection conn, String schemaOrCatalog, String tabName)
			throws Exception {
		ResultSet tablesResultSet = null;
//		ResultSet rs = null;
		Statement sm = null;
		TablePo po = new TablePo();
		try {
			DatabaseMetaData dbMetaData = conn.getMetaData();
			String catalog = getCatalog(conn);// conn.getCatalog();
			String schema = getSchema(conn); // conn.getSchema();
			if (StrUtils.isNotNullOrEmpty(schemaOrCatalog)) {
				if (catalog == null) {
					schema = schemaOrCatalog;
				} else if (schema == null) {
					catalog = schemaOrCatalog;
				}
			}
			tablesResultSet = dbMetaData.getTables(null, null, tabName, null);
			if (tablesResultSet.next()) {
				String tableType = tablesResultSet.getString("TABLE_TYPE");
				if (tableType.contains("TABLE")) {
					String tableName = tablesResultSet.getString("TABLE_NAME");
					String remarks = tablesResultSet.getString("REMARKS");

					po.setTableName(tableName);
					po.setTableRemarks(remarks);
					po.setTableSchema(schemaOrCatalog);
					po.setTableType(tableType);
					LinkedHashSet<TableFieldPo> fields = new LinkedHashSet<TableFieldPo>();
					po.setFields(fields);
					ArrayList<TablePrimaryKeysPo> tpks = new ArrayList<TablePrimaryKeysPo>();
					po.setPrimaryKeys(tpks);
				}

			}

		} finally {
			if (tablesResultSet != null)
				tablesResultSet.close();
//			if (rs != null)
//				rs.close();
			if (sm != null)
				sm.close();
		}
		return po;
	}

	// 函数
	public static List<FuncProcTriggerPo> fetchAllFunctions(Connection conn, String schemaOrCatalog) throws Exception {
		ResultSet funRs = null;
//		ResultSet rs = null;
		Statement sm = null;
		List<FuncProcTriggerPo> ls = new ArrayList<FuncProcTriggerPo>();
		try {
			DatabaseMetaData dbMetaData = conn.getMetaData();
			String catalog = conn.getCatalog();
			String schema = conn.getSchema();
			if (StrUtils.isNotNullOrEmpty(schemaOrCatalog)) {
				if (catalog == null) {
					schema = schemaOrCatalog;
				} else if (schema == null) {
					catalog = schemaOrCatalog;
				}
			}

			funRs = dbMetaData.getFunctions(catalog, schema, null);
			while (funRs.next()) {

				String FUNCTION_NAME = funRs.getString("FUNCTION_NAME");
				String REMARKS = funRs.getString("REMARKS");
				FuncProcTriggerPo po = new FuncProcTriggerPo();
				po.setName(FUNCTION_NAME);
				po.setRemarks(REMARKS);
				po.setSchema(schemaOrCatalog);

				ls.add(po);
			}

		} finally {
			if (funRs != null)
				funRs.close();
//			if (rs != null)
//				rs.close();
			if (sm != null)
				sm.close();
		}
		return ls;
	}

	// procedure
	public static List<FuncProcTriggerPo> fetchAllProcedures(Connection conn, String schemaOrCatalog) throws Exception {
		ResultSet proRs = null;
		Statement sm = null;
		List<FuncProcTriggerPo> ls = new ArrayList<FuncProcTriggerPo>();
		try {
			DatabaseMetaData dbMetaData = conn.getMetaData();
			String catalog = conn.getCatalog();
			String schema = conn.getSchema();
			if (StrUtils.isNotNullOrEmpty(schemaOrCatalog)) {
				if (catalog == null) {
					schema = schemaOrCatalog;
				} else if (schema == null) {
					catalog = schemaOrCatalog;
				}
			}
			proRs = dbMetaData.getProcedures(catalog, schema, null);
			while (proRs.next()) {
				String name = proRs.getString("PROCEDURE_NAME");
				String remarks = proRs.getString("REMARKS");

				FuncProcTriggerPo po = new FuncProcTriggerPo();
				po.setName(name);
				po.setRemarks(remarks);
				po.setSchema(schemaOrCatalog);
				po.setProcedure(true);
				ls.add(po);
			}

		} finally {
			if (proRs != null)
				proRs.close();
			if (sm != null)
				sm.close();
		}
		return ls;
	}

	// triggers
	public static List<FuncProcTriggerPo> fetchAllTriggers(Connection conn, String schemaOrCatalog) throws Exception {
		ResultSet proRs = null;
		Statement sm = null;
		List<FuncProcTriggerPo> ls = new ArrayList<FuncProcTriggerPo>();
		try {
			DatabaseMetaData dbMetaData = conn.getMetaData();
			String catalog = conn.getCatalog();
			String schema = conn.getSchema();
			if (StrUtils.isNotNullOrEmpty(schemaOrCatalog)) {
				if (catalog == null) {
					schema = schemaOrCatalog;
				} else if (schema == null) {
					catalog = schemaOrCatalog;
				}
			}

			proRs = dbMetaData.getTables(catalog, schema, null, new String[] { "TRIGGER" });
			while (proRs.next()) {
				String name = proRs.getString("PROCEDURE_NAME");
				String remarks = proRs.getString("REMARKS");

				FuncProcTriggerPo po = new FuncProcTriggerPo();
				po.setName(name);
				po.setRemarks(remarks);

				ls.add(po);
			}

		} finally {
			if (proRs != null)
				proRs.close();
			if (sm != null)
				sm.close();
		}
		return ls;
	}

	/**
	 * mysql : 获取数据库中的表的list
	 */
	public List<TablePo> getTableName(Connection conn) throws Exception {
		ResultSet tablesResultSet = null;
		ResultSet rs = null;
		Statement sm = null;

		try {
			List<TablePo> tables = new ArrayList<>();
			DatabaseMetaData dbMetaData = conn.getMetaData();
			String catalog = conn.getCatalog(); // catalog 其实也就是数据库名
			tablesResultSet = dbMetaData.getTables(catalog, null, null, new String[] { "TABLE" });

			while (tablesResultSet.next()) {
				String sql = "SHOW create table ";
				sm = conn.createStatement();
				TablePo po = new TablePo();
				String tableName = tablesResultSet.getString("TABLE_NAME");
				String REMARKS = tablesResultSet.getString("REMARKS");
				sql = sql + tableName;
				String commentstr = "";
				rs = sm.executeQuery(sql);
				if (rs.next()) {
					String t = rs.getString(1);
					String c = rs.getString(2);
					commentstr = getCommentFromSql(c);
				}
				if (REMARKS == null || "".equals(REMARKS)) {
					REMARKS = commentstr;
				}
				po.setTableName(tableName);
				po.setTableRemarks(REMARKS);
				tables.add(po);
				sm.close();
				rs.close();
			}
			return tables;
		} finally {
			if (tablesResultSet != null)
				tablesResultSet.close();
			if (rs != null)
				rs.close();
			if (sm != null)
				sm.close();
		}

	}

	/**
	 * 根据表名获取表中字段的信息
	 */
	public TablePo getTableField(TablePo tbpo, Connection conn) throws Exception {
		ResultSet resultSet = null;
		try {
			DatabaseMetaData databaseMetaData = conn.getMetaData();
			resultSet = databaseMetaData.getColumns(null, null, tbpo.getTableName().toUpperCase(), "%");

			LinkedHashSet<TableFieldPo> list = new LinkedHashSet<TableFieldPo>();
			while (resultSet.next()) {
				TableFieldPo tfp = new TableFieldPo();
				tfp.setFieldName(resultSet.getString("COLUMN_NAME"));
				tfp.setType(resultSet.getString("TYPE_NAME"));
				tfp.setIsNullable(resultSet.getString("IS_NULLABLE"));
				tfp.setRemarks(resultSet.getString("REMARKS"));
				list.add(tfp);
			}
			tbpo.setFields(list);
			return tbpo;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}

	}

	/**
	 * 获取create sql语句中的 COMMENT的值
	 * 
	 * @param sql
	 * @return
	 */
	private String getCommentFromSql(String sql) {
		if (sql == null && "".equals(sql)) {
			return "";
		}
		String sr[] = sql.split("\n");
		if (sr.length == 0) {
			return "";
		}
		String srs = sr[sr.length - 1];
		String comment[] = srs.split("COMMENT");
		if (comment.length == 0) {
			return "";
		}

		String commentstr = "";
		if (comment.length > 1) {
			commentstr = comment[1];
			int start = (commentstr.indexOf("'") + 1);
			commentstr = commentstr.substring(start, commentstr.lastIndexOf("'"));
			logger.info(" 表注释名:" + commentstr);
		}

		return commentstr;
	}

	/**
	 * 将数据保存到h2数据库中
	 * 
	 * @param tbList
	 */
//	public static void initializationH2DateBase(TablePo po, String h2Url, String h2User, String h2Password) {
//		Connection conn = null;
//		PreparedStatement sm = null;
//		PreparedStatement sm2 = null;
//		try {
//			conn = ConnectionPool.getDirectConn(h2Url, h2User, h2Password);
//			String tableSQl = "insert into mytables (table_name, table_comment) values ( ?,?)";
//			String fieldSQl = "insert into mytables_field (table_id, field_name,field_comment,TYPE_NAME,IS_NULLABLE) "
//					+ "values (?, ?, ?, ?, ?)";
//			sm = conn.prepareStatement(tableSQl, Statement.RETURN_GENERATED_KEYS); // 插入数据,返回主键id
//			sm.setString(1, po.getTableName());
//			sm.setString(2, po.getTableRemarks());
//			sm.execute();
//			ResultSet generatedKeys = sm.getGeneratedKeys();
//			long id = 0;
//			if (generatedKeys.next()) {
//				id = generatedKeys.getLong(1);
//			}
//			sm.close();
//			po.setId(id);
//			// 字段的值插入
//			Set<TableFieldPo> fieldList = po.getFields();
//			sm2 = conn.prepareStatement(fieldSQl);
//			for (TableFieldPo fpo : fieldList) {
//				sm2.setLong(1, po.getId());
//				sm2.setString(2, fpo.getFieldName());
//				sm2.setString(3, fpo.getRemarks());
//				sm2.setString(4, fpo.getType());
//				sm2.setString(5, fpo.getIsNullable());
//				sm2.execute();
//			}
//			sm2.close();
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public static String getDBInfo(Connection connection) {
		// 2.获取元数据
		DatabaseMetaData metaData;
		StringBuilder infoStr = new StringBuilder("");
		try {
			metaData = connection.getMetaData();

			// 3.获取数据库基本信息
//	  infoStr.append(metaData.getUserName());

//	  infoStr.append(metaData.supportsTransactions());//是否支持事务

			infoStr.append("Database Name: " + metaData.getDatabaseProductName() + " ");
			infoStr.append("Database Version: " + metaData.getDatabaseProductVersion());

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return infoStr.toString();
	}

	public static Connection createConnection(String jdbcUrl, String user, String pw) {
		Dbinfo dbinfo = new Dbinfo(jdbcUrl, user, pw);
		Connection connection = dbinfo.getconn();
		return connection;
	}

}
