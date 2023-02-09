package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;
import net.tenie.Sqlucky.sdk.po.TablePo;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;

public abstract class DbConnector implements SqluckyConnector { 

	private static Logger logger = LogManager.getLogger(DbConnector.class); 
	protected DBConnectorInfoPo connPo;
	
	
	

	public void setDBConnectorInfoPo(DBConnectorInfoPo po) {
		this.connPo = po;
	}
	public DBConnectorInfoPo getDBConnectorInfoPo() {
		return this.connPo;
	}
	
	public DbConnector(DBConnectorInfoPo c) {
		super();
		this.connPo = c;
	}
 

	//正在连接中, 原子操作
	private AtomicBoolean finishInitNodeStatus = new AtomicBoolean(true);
	
	//是不是正在连接
	public synchronized  Boolean finishInitNode() {
		return finishInitNodeStatus.get();
	}
	@Override
	public synchronized void setInitConnectionNodeStatus(Boolean tf) {
		finishInitNodeStatus.set( ! tf);
	}
	
	// 判断是否连接着
	public boolean isAlive() {
		boolean tf = false; 
		if(finishInitNode( )) {
			if (this.connPo.getConn() != null) {
				tf = true;
			}
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

	public abstract Connection getConn();
//	{
//		if (this.connPo.getConn() == null) {
//			logger.info(this.connPo.getDriver());
//			logger.info(getJdbcUrl());
//			logger.info(this.connPo.getUser());
////			logger.info(passWord);
////			if (DbVendor.sqlite.toUpperCase().equals(dbVendor.toUpperCase())) {
////				Dbinfo dbinfo = new Dbinfo(getJdbcUrl());
////				conn = dbinfo.getconn();
////			}else {
////				Dbinfo dbinfo = new Dbinfo(driver, getJdbcUrl(), user, passWord);
////				conn = dbinfo.getconn();
////			}			
//		}
//
//		return this.connPo.getConn();
//	}

	// 关闭连接
	public void closeConn() {
		try {
			if (this.connPo.getConn() != null) {
				this.connPo.getConn().close();
				this.connPo.setConn(null);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getDbVendor() {
		return this.connPo.getDbVendor();
	}

	public void setDbVendor(String dbVendor) {
//		this.dbVendor = dbVendor;
		this.connPo.setDbVendor(dbVendor);
	}

	public String getUser() {
		return this.connPo.getUser();
	}

	public void setUser(String user) {
//		this.user = user;
		this.connPo.setUser(user);
	}

	public Integer getId() {
		return this.connPo.getId();
	}

	public void setId(Integer id) {
//		this.id = id;
		this.connPo.setId(id);
	}

	public String getPassWord() {
		return this.connPo.getPassWord();
	}

	public void setPassWord(String passWord) {
//		this.passWord = passWord;
		this.connPo.setPassWord(passWord);
	}

	public String getComment() {
		return this.connPo.getComment();
	}

	public void setComment(String comment) {
//		this.comment = comment;
		this.connPo.setComment(comment);
	}

//	public abstract String getJdbcUrl();

//	public void setJdbcUrl(String jdbcUrl) {
////		this.jdbcUrl = jdbcUrl;
//		this.connPo.setJdbcUrl(jdbcUrl);
//	}

//	public String getOtherParameter() {
//		return otherParameter;
//	}
//
//	public void setOtherParameter(String otherParameter) {
//		this.otherParameter = otherParameter;
//	}

	public String getDriver() {
//		return driver;
		return this.connPo.getDriver();
	}

	public void setDriver(String driver) {
//		this.driver = driver;
		this.connPo.setDriver(driver);
	}

	public String getConnName() {
		return this.connPo.getConnName();
	}

//	public void setConnName(String connName) {
////		this.connName = connName;
//	}

	public String getHostOrFile() {
		return this.connPo.getHostOrFile();
	}

//	public void setHost(String host) {
//		this.host = host;
//	}

	public String getPort() {
		return this.connPo.getPort();
	}

//	public void setPort(String port) {
//		this.port = port;
//	}

	public String getDefaultSchema() {
		return this.connPo.getDefaultSchema();
	}

//	public void setDefaultSchema(String defaultSchema) {
//		this.defaultSchema = defaultSchema;
//	}

//	public Map<String, DbSchemaPo> getSchemas() {
//		try { 
//			if (schemas == null || schemas.isEmpty()) { 
//				if (DbVendor.sqlite.toUpperCase().equals(dbVendor.toUpperCase())) {
//					Map<String, DbSchemaPo> sch = new HashMap<>();
//					DbSchemaPo sp = new DbSchemaPo();
//					sp.setSchemaName(SQLITE_DATABASE);
//					sch.put(SQLITE_DATABASE, sp);
//					schemas = sch;
//				} else {
//					schemas = Dbinfo.fetchSchemasInfo(this);					
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return schemas;
//	}
	
//	private ExportDDL setExportDDL(String dbvendor) {
//
//		if (DbVendor.db2.toUpperCase().equals(dbVendor.toUpperCase())) {
//			exportDDL = new ExportSqlDB2Imp();
//		} else if (DbVendor.mysql.toUpperCase().equals(dbVendor.toUpperCase())) {
//			exportDDL = new ExportSqlMySqlImp();
//		} else if (DbVendor.mariadb.toUpperCase().equals(dbVendor.toUpperCase())) {
//			exportDDL = new ExportSqlMariadbImp();
//		}else if (DbVendor.h2.toUpperCase().equals(dbVendor.toUpperCase())) {
//			exportDDL = new ExportSqlH2Imp();
//		}else if (DbVendor.sqlite.toUpperCase().equals(dbVendor.toUpperCase())) {
//			exportDDL = new ExportSqlSqliteImp();
//		}else if (DbVendor.postgresql.toUpperCase().equals(dbVendor.toUpperCase())) {
//			exportDDL = new ExportDefaultImp();
//		}   else {
//			exportDDL = new ExportDefaultImp();
//		}
//
//		return exportDDL;
//	}
	

	public void setSchemas(Map<String, DbSchemaPo> schemas) {
		this.connPo.setSchemas(schemas);// = schemas;
	}

//	public Date getCreatedAt() {
//		return createdAt;
//	}
//
//	public void setCreatedAt(Date createdAt) {
//		this.createdAt = createdAt;
//	}
//
//	public Date getUpdatedAt() {
//		return updatedAt;
//	}

//	public void setUpdatedAt(Date updatedAt) {
//		this.updatedAt = updatedAt;
//	}

//	public Integer getRecordVersion() {
//		return recordVersion;
//	}
//
//	public void setRecordVersion(Integer recordVersion) {
//		this.recordVersion = recordVersion;
//	}

	public ExportDDL getExportDDL() {
		return this.connPo.getExportDDL();
	}

//	public void setExportDDL(ExportDDL exportDDL) {
//		this.exportDDL = exportDDL;
//	}

	
//	public ConnItemContainer getItemContainer() {
//		return itemContainer;
//	}
//
//	public void setItemContainer(ConnItemContainer itemContainer) {
//		this.itemContainer = itemContainer;
//	}

	public String getDbName() {
		return this.connPo.getDbName();
	}

//	public void setDbName(String dbName) {
//		this.dbName = dbName;
//	}

	@Override
	public String toString() {
		return this.connPo.toString();
	}


	public DBConnectorInfoPo getConnPo() {
		return connPo;
	}


	public void setConnPo(DBConnectorInfoPo connPo) {
		this.connPo = connPo;
	}
	
	public boolean isJdbcUrlUse() {
		return this.connPo.isJdbcUrlUse();
	}
	
	public boolean getAutoConnect() {
		return this.connPo.isAutoConnect();
	}
//	public boolean isSqlite() {
////		if (DbVendor.sqlite.toUpperCase().equals(dbVendor.toUpperCase())) {
////			return true;
////		}
//		return false;
//	}
	
//	public boolean isH2() {
////		if (DbVendor.h2.toUpperCase().equals(dbVendor.toUpperCase())) {
////			return true;
////		}
//		return false;
//	}
	
//	public boolean isPostgresql() {
////		if (DbVendor.postgresql.toUpperCase().equals(dbVendor.toUpperCase())) {
////			return true;
////		}
//		return false;
//	}

//	@Override
//	public SqluckyConnector copyObj(SqluckyConnector sopo, String schema) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
}
