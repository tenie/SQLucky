package net.tenie.lib.po;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import net.tenie.lib.db.DB2ExportDDLImp;
import net.tenie.lib.db.Dbinfo;
import net.tenie.lib.db.DefaultExportDDLImp;
import net.tenie.lib.db.ExportDDL;
import net.tenie.lib.db.H2ExportDDLImp;
import net.tenie.lib.db.MySqlExportDDLImp;

/*   @author tenie */
public class DbConnectionPo {
	private Integer id;
	private String connName; // 连接名称
	private String host;
	private String port;
	private String driver;
	private String dbVendor;
	private String defaultSchema;
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

	// 正在连接中, 原子操作
	private AtomicBoolean connectionIng = new AtomicBoolean(false);

	public Boolean isConnIng() {
		return connectionIng.get();
	}

	public void setConning(Boolean tf) {
		connectionIng.set(tf);
	}

//	public DbConnectionPo(){} 
	private ExportDDL setExportDDL(String dbvendor) {

		if ("DB2".equals(dbVendor.toUpperCase())) {
			exportDDL = new DB2ExportDDLImp();
		} else if ("MYSQL".equals(dbVendor.toUpperCase())) {
			exportDDL = new MySqlExportDDLImp();
		} else if ("H2".equals(dbVendor.toUpperCase())) {
			exportDDL = new H2ExportDDLImp();
		} else {
			exportDDL = new DefaultExportDDLImp();
		}

		return exportDDL;
	}

	public DbConnectionPo(String connName, String driver, String host, String port, String user, String passWord,
			String dbVendor, String defaultSchema

	) {
		super();
		this.connName = connName;
		this.host = host;
		this.port = port;
		this.defaultSchema = defaultSchema;
		this.dbVendor = dbVendor;
		this.driver = driver;
		this.user = user;
		this.passWord = passWord;

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
			System.out.println(driver);
			System.out.println(getJdbcUrl());
			System.out.println(user);
			System.out.println(passWord);
			Dbinfo dbinfo = new Dbinfo(driver, getJdbcUrl(), user, passWord);

			conn = dbinfo.getconn();
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
			if ("H2".equals(dbVendor.toUpperCase())) {
				jdbcUrl = "jdbc:h2:" + host;
				defaultSchema = "PUBLIC";
			} else {
				jdbcUrl = "jdbc:" + dbVendor + "://" + host + ":" + port + "/" + defaultSchema;
				if (otherParameter != null && otherParameter.length() > 0) {
					jdbcUrl += "?" + getOtherParameter();
				}
			}

		}

		System.out.println(jdbcUrl);

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
		if (schemas == null) {
			try {
				schemas = Dbinfo.fetchSchemasInfo(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	@Override
	public String toString() {
		return "DbConnectionPo [id=" + id + ", connName=" + connName + ", host=" + host + ", port=" + port + ", driver="
				+ driver + ", dbVendor=" + dbVendor + ", defaultSchema=" + defaultSchema + ", user=" + user
				+ ", passWord=" + passWord + ", jdbcUrl=" + jdbcUrl + ", otherParameter=" + otherParameter
				+ ", comment=" + comment + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", recordVersion="
				+ recordVersion + ", schemas=" + schemas + "]";
	}

}
