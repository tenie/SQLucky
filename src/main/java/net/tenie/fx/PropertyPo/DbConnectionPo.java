package net.tenie.fx.PropertyPo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.fx.component.TreeItem.ConnItemContainer;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.utility.ParseSQL;
import net.tenie.lib.db.ExportSqlDB2Imp;
import net.tenie.lib.db.Dbinfo;
import net.tenie.lib.db.ExportDefaultImp;
import net.tenie.lib.db.ExportDDL;
import net.tenie.lib.db.ExportSqlH2Imp;
import net.tenie.lib.db.ExportSqlMariadbImp;
import net.tenie.lib.db.ExportSqlMySqlImp;
import net.tenie.lib.db.ExportSqlSqliteImp;

/*   @author tenie */
public class DbConnectionPo {
	private static Logger logger = LogManager.getLogger(DbConnectionPo.class);
	private Integer id;
	private String connName; // 连接名称
	private String host;
	private String port;
	private String driver;
	private String dbVendor;
	private String defaultSchema;
	private String dbName;
	private String user;
	private String passWord;
	private String jdbcUrl;
	private String otherParameter; // 可以为空,
	private String comment; // 可以为空,

	private Date createdAt;
	private Date updatedAt;
	private Integer recordVersion;

	private Connection conn;
	private Map<String, DbSchemaPo> schemas;
	private ExportDDL exportDDL;
	
//	private ConnItemContainer itemContainer;
	
	private String SQLITE_DATABASE = "SQLITE DATABASE";

	// 正在连接中, 原子操作
	private AtomicBoolean connectionIng = new AtomicBoolean(false);

	public Boolean isConnIng() {
		return connectionIng.get();
	}

	public static DbConnectionPo copyObj(DbConnectionPo sopo, String schema) {
		DbConnectionPo val = new DbConnectionPo(  
				sopo.getConnName()+"Copy",
				sopo.getDriver(),
				sopo.getHost(),
				sopo.getPort(),
				sopo.getUser(),
				sopo.getPassWord(),
				sopo.getDbVendor(),
				schema,
				sopo.getDbName()
				);
		
		return val;
	}
	
	public void setConning(Boolean tf) {
		connectionIng.set(tf);
	}

//	public DbConnectionPo(){} 
	private ExportDDL setExportDDL(String dbvendor) {

		if (DbVendor.db2.toUpperCase().equals(dbVendor.toUpperCase())) {
			exportDDL = new ExportSqlDB2Imp();
		} else if (DbVendor.mysql.toUpperCase().equals(dbVendor.toUpperCase())) {
			exportDDL = new ExportSqlMySqlImp();
		} else if (DbVendor.mariadb.toUpperCase().equals(dbVendor.toUpperCase())) {
			exportDDL = new ExportSqlMariadbImp();
		}else if (DbVendor.h2.toUpperCase().equals(dbVendor.toUpperCase())) {
			exportDDL = new ExportSqlH2Imp();
		}else if (DbVendor.sqlite.toUpperCase().equals(dbVendor.toUpperCase())) {
			exportDDL = new ExportSqlSqliteImp();
		}else if (DbVendor.postgresql.toUpperCase().equals(dbVendor.toUpperCase())) {
//			this.dbName = this.defaultSchema;
//			this.defaultSchema = "public";
			
			exportDDL = new ExportDefaultImp();
		}   else {
			exportDDL = new ExportDefaultImp();
		}

		return exportDDL;
	}

	public DbConnectionPo(String connName, String driver, String host, String port, String user, String passWord,
			String dbVendor, String defaultSchema,String dbName

	) {
		super();
		this.connName = connName;
		this.host = host;
		this.port = port;
//		this.defaultSchema = defaultSchema.trim();
		this.dbVendor = dbVendor;
		if (DbVendor.postgresql.toUpperCase().equals(dbVendor.toUpperCase())) {
			this.defaultSchema = "public";
		} else {
			this.defaultSchema = defaultSchema.trim();
		}

		this.driver = driver;
		this.user = user;
		this.passWord = passWord;
		this.dbName = dbName;

		setExportDDL(dbVendor);
	}

	// 判断是否连接着
	public boolean isAlive() {
		boolean tf = false; 
		if (conn != null) {
			tf = true;
		}
		return tf;
	}

	// 刷新Schema中的tabs
	public List<TablePo> flushTabs(String schemasName) {
		Map<String, DbSchemaPo> map = getSchemas();
		DbSchemaPo spo = map.get(schemasName);
		List<TablePo> tbs = null;
		try {
			tbs = Dbinfo.fetchAllTableName(getConn(), schemasName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		spo.setTabs(tbs);
		return tbs;
	}

	// 获取sehema 列表
	public Set<String> settingSchema() {
		getConn(); // 先获取一下连接， 防止界面使用缓存数据
		Map<String, DbSchemaPo> map = getSchemas();
		return map.keySet();
	}

	public Connection getConn() {
		if (conn == null) {
			logger.info(driver);
			logger.info(getJdbcUrl());
			logger.info(user);
//			logger.info(passWord);
			if (DbVendor.sqlite.toUpperCase().equals(dbVendor.toUpperCase())) {
				Dbinfo dbinfo = new Dbinfo(getJdbcUrl());
				conn = dbinfo.getconn();
			}else {
				Dbinfo dbinfo = new Dbinfo(driver, getJdbcUrl(), user, passWord);
				conn = dbinfo.getconn();
			}			
		}

		return conn;
	}

	// 关闭连接
	public void closeConn() {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getDbVendor() {
		return dbVendor;
	}

	public void setDbVendor(String dbVendor) {
		this.dbVendor = dbVendor;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getJdbcUrl() {
		if (jdbcUrl == null || jdbcUrl.length() == 0) {
			if (this.isH2()) {
				jdbcUrl = "jdbc:h2:" + host;
				defaultSchema = "PUBLIC";
			}else if (this.isSqlite()) {
				jdbcUrl = "jdbc:sqlite:" + host;
				defaultSchema = SQLITE_DATABASE;
			} else if (this.isPostgresql()) {
				jdbcUrl = "jdbc:" + dbVendor + "://" + host + ":" + port + "/" + dbName;
			} else {
				jdbcUrl = "jdbc:" + dbVendor + "://" + host + ":" + port + "/" + defaultSchema;
				if (otherParameter != null && otherParameter.length() > 0) {
					jdbcUrl += "?" + getOtherParameter();
				}
			}

		}

		logger.info(jdbcUrl);

		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getOtherParameter() {
		return otherParameter;
	}

	public void setOtherParameter(String otherParameter) {
		this.otherParameter = otherParameter;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDefaultSchema() {
		return defaultSchema;
	}

	public void setDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
	}

	public Map<String, DbSchemaPo> getSchemas() {
		try { 
			if (schemas == null || schemas.isEmpty()) { 
				if (DbVendor.sqlite.toUpperCase().equals(dbVendor.toUpperCase())) {
					Map<String, DbSchemaPo> sch = new HashMap<>();
					DbSchemaPo sp = new DbSchemaPo();
					sp.setSchemaName(SQLITE_DATABASE);
					sch.put(SQLITE_DATABASE, sp);
					schemas = sch;
				} else {
					schemas = Dbinfo.fetchSchemasInfo(this);					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return schemas;
	}

	public void setSchemas(Map<String, DbSchemaPo> schemas) {
		this.schemas = schemas;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Integer getRecordVersion() {
		return recordVersion;
	}

	public void setRecordVersion(Integer recordVersion) {
		this.recordVersion = recordVersion;
	}

	public ExportDDL getExportDDL() {
		return exportDDL;
	}

	public void setExportDDL(ExportDDL exportDDL) {
		this.exportDDL = exportDDL;
	}

	
//	public ConnItemContainer getItemContainer() {
//		return itemContainer;
//	}
//
//	public void setItemContainer(ConnItemContainer itemContainer) {
//		this.itemContainer = itemContainer;
//	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public String toString() {
		return "DbConnectionPo [id=" + id + ", connName=" + connName + ", host=" + host + ", port=" + port + ", driver="
				+ driver + ", dbVendor=" + dbVendor + ", defaultSchema=" + defaultSchema + ", user=" + user
				+ ", passWord=" + passWord + ", jdbcUrl=" + jdbcUrl + ", otherParameter=" + otherParameter
				+ ", comment=" + comment + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", recordVersion="
				+ recordVersion + ", schemas=" + schemas + "]";
	}
	
	public boolean isSqlite() {
		if (DbVendor.sqlite.toUpperCase().equals(dbVendor.toUpperCase())) {
			return true;
		}
		return false;
	}
	
	public boolean isH2() {
		if (DbVendor.h2.toUpperCase().equals(dbVendor.toUpperCase())) {
			return true;
		}
		return false;
	}
	
	public boolean isPostgresql() {
		if (DbVendor.postgresql.toUpperCase().equals(dbVendor.toUpperCase())) {
			return true;
		}
		return false;
	}
}
