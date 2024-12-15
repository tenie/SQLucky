package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.utility.DateUtils;
//DbConnector
public abstract class SqluckyConnector {

	private static Logger logger = LogManager.getLogger(SqluckyConnector.class);
	
	protected DBConnectorInfoPo dbConnectorInfoPo;
	protected TreeItem connTreeItem;
	protected SqluckyDbRegister dbRegister;
 
	public SqluckyDbRegister getDbRegister() {
		return dbRegister;
	}

	public SqluckyConnector(DBConnectorInfoPo c, SqluckyDbRegister dbRegister) {
		this.dbConnectorInfoPo = c;
		this.dbRegister = dbRegister;
	}

	// 保存 树节点上显示的链接节点
	public void setDbInfoTreeNode(TreeItem item) {
		this.connTreeItem = item;
	}
 
	public TreeItem getDbInfoTreeNode() {
		return this.connTreeItem;
	}

	public void setDBConnectorInfoPo(DBConnectorInfoPo po) {
		this.dbConnectorInfoPo = po;
	}

	public DBConnectorInfoPo getDBConnectorInfoPo() {
		return this.dbConnectorInfoPo;
	}

	// 正在连接中, 原子操作
	private AtomicBoolean finishInitNodeStatus = new AtomicBoolean(true);

	// 是不是正在连接
	public synchronized Boolean finishInitNode() {
		return finishInitNodeStatus.get();
	}

	public synchronized void setInitConnectionNodeStatus(Boolean tf) {
		finishInitNodeStatus.set(!tf);
	}

	// 判断是否连接着
	public boolean isAlive() {
		boolean tf = false;
		if (finishInitNode()) {
			if (this.dbConnectorInfoPo.getConn() != null) {
				boolean isClosed = true;
				try {
					isClosed = this.dbConnectorInfoPo.getConn().isClosed();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
				if (!isClosed) {
					tf = true;
				}

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

	public String DBInfo(Connection conn) {
		String infoStr = Dbinfo.getDBInfo(conn);
		return infoStr;
	}


	// 关闭连接
	public void closeConn() {
		try {
			if (this.dbConnectorInfoPo.getConn() != null) {
				this.dbConnectorInfoPo.getConn().close();
				this.dbConnectorInfoPo.setConn(null);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getDbVendor() {
		return this.dbConnectorInfoPo.getDbVendor();
	}

	public void setDbVendor(String dbVendor) {
		this.dbConnectorInfoPo.setDbVendor(dbVendor);
	}

	public String getUser() {
		return this.dbConnectorInfoPo.getUser();
	}

	public void setUser(String user) {
		this.dbConnectorInfoPo.setUser(user);
	}

	public Integer getId() {
		return this.dbConnectorInfoPo.getId();
	}

	public void setId(Integer id) {
		this.dbConnectorInfoPo.setId(id);
	}

	public String getPassWord() {
		return this.dbConnectorInfoPo.getPassWord();
	}

	public void setPassWord(String passWord) {
		this.dbConnectorInfoPo.setPassWord(passWord);
	}

	public String getComment() {
		return this.dbConnectorInfoPo.getComment();
	}

	public void setComment(String comment) {
		this.dbConnectorInfoPo.setComment(comment);
	}

	// 自定义需要展示的时间格式, 转换成字符串
	public String DateTimeToString(Object obj, int sqlFiledtype) {
		return DateUtils.DbDateTimeToString(obj, sqlFiledtype);
	}

	public String getDriver() {
		return this.dbConnectorInfoPo.getDriver();
	}

	public void setDriver(String driver) {
		this.dbConnectorInfoPo.setDriver(driver);
	}

	public String getConnName() {
		return this.dbConnectorInfoPo.getConnName();
	}

	public String getHostOrFile() {
		return this.dbConnectorInfoPo.getHostOrFile();
	}

	public String getPort() {
		return this.dbConnectorInfoPo.getPort();
	}

	public String getDefaultSchema() {
		return this.dbConnectorInfoPo.getDefaultSchema();
	}

	public void setSchemas(Map<String, DbSchemaPo> schemas) {
		this.dbConnectorInfoPo.setSchemas(schemas);// = schemas;
	}

	public ExportDBObjects getExportDDL() {
		return this.dbConnectorInfoPo.getExportDDL();
	}

	public String getDbName() {
		return this.dbConnectorInfoPo.getDbName();
	}

	@Override
	public String toString() {
		return this.dbConnectorInfoPo.toString();
	}

	public DBConnectorInfoPo getDbConnectorInfoPo() {
		return dbConnectorInfoPo;
	}

	public void setDbConnectorInfoPo(DBConnectorInfoPo dbConnectorInfoPo) {
		this.dbConnectorInfoPo = dbConnectorInfoPo;
	}

	public boolean isJdbcUrlUse() {
		return this.dbConnectorInfoPo.isJdbcUrlUse();
	}

	public boolean getAutoConnect() {
		return this.dbConnectorInfoPo.isAutoConnect();
	}
	public abstract String dbRootNodeName();
	public abstract String translateErrMsg(String errString);
	public abstract Map<String, DbSchemaPo> getSchemas();
	public abstract SqluckyConnector copyObj( String schema);
	public abstract String getRealDefaultSchema();
	public abstract String getJdbcUrl();

	// 使用新的数据库名, 重新设置连接
	public void resetJdbcUrlStr(String dataBaseName){
		String jdbcUrlstr = dbConnectorInfoPo.getJdbcUrl();
		if(StrUtils.isNotNullOrEmpty(jdbcUrlstr)) {
			String jdbcUrlstrTmp = jdbcUrlstr;
			jdbcUrlstrTmp = jdbcUrlstrTmp.replaceFirst("://", "   ");
			int idx = jdbcUrlstrTmp.indexOf("/");
			if(idx > 0 ){
				String beginStr = jdbcUrlstr.substring(0, idx+1);
				beginStr += dataBaseName;
				int idx2 = jdbcUrlstr.indexOf("?");
				if(idx2 > idx){
					String endStr = jdbcUrlstr.substring(idx2);
					// 设置jdbc url
					dbConnectorInfoPo.setJdbcUrl(beginStr + endStr);
					// 重新连接
					reConnection();
				}
			}else{
				int idx2 = jdbcUrlstr.indexOf("?");
				if(idx2 > 0){
					String beginStr = jdbcUrlstr.substring(0, idx2);
					String endStr = jdbcUrlstr.substring(idx2);
					// 设置jdbc url
					dbConnectorInfoPo.setJdbcUrl(beginStr + "/" + dataBaseName + endStr);
					// 重新连接
					reConnection();
				}else{
					// 设置jdbc url
					dbConnectorInfoPo.setJdbcUrl(jdbcUrlstr + "/" + dataBaseName);
					// 重新连接
					reConnection();
				}
			}
		}

	}


	/**
	 * 获取 Connection
	 * @return
	 */
	public Connection getConn() {
		if (getDbConnectorInfoPo().getConn() == null) {
			Dbinfo dbinfo = new Dbinfo( getJdbcUrl(), getUser(), getPassWord());
			var conn = dbinfo.getconn();
			getDbConnectorInfoPo().setConn(conn);
		}
		return getDbConnectorInfoPo().getConn();
	}

	/**
	 * 重新连接获取 Connection
	 * @return
	 */
	public void reConnection() {
		if (getDbConnectorInfoPo().getConn() != null) {
			try {
				getDbConnectorInfoPo().getConn().close();
				getDbConnectorInfoPo().setConn(null);
				getConn();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * 设置最后一次使用时间, 避免超时被另一个线程释放掉
	 */
	public void setConnectionLastUseTime() {
		if( dbConnectorInfoPo.getConn() == null) {
			Date time = new Date();
			dbConnectorInfoPo.setLastConnectTime(time);
		}
	}
	/**
	 * 获取缓存的连接
	 * @return
	 */
	public  Connection getCacheConn() {
		Date lastTime = dbConnectorInfoPo.getLastConnectTime();
		Date time = new Date();

		dbConnectorInfoPo.setLastConnectTime(time);
		if ( lastTime == null || dbConnectorInfoPo.getConn() == null ) {
			return null;
		}
		long interval = time.getTime() - lastTime.getTime() ;
		if( interval > 600_000L ) {
			logger.info("链接超时600秒, 需要重新链接");
			closeConn();
			return null;
		}
		return dbConnectorInfoPo.getConn();
	}
	
}
