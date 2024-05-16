package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
	
	protected DBConnectorInfoPo connPo;
	protected TreeItem connTreeItem;
	protected SqluckyDbRegister dbRegister;
 
	public SqluckyDbRegister getDbRegister() {
		return dbRegister;
	}

	public SqluckyConnector(DBConnectorInfoPo c, SqluckyDbRegister dbRegister) {
//		super();
		this.connPo = c;
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
		this.connPo = po;
	}

	public DBConnectorInfoPo getDBConnectorInfoPo() {
		return this.connPo;
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
			if (this.connPo.getConn() != null) {
				boolean isClosed = true;
				try {
					isClosed = this.connPo.getConn().isClosed();
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
		this.connPo.setDbVendor(dbVendor);
	}

	public String getUser() {
		return this.connPo.getUser();
	}

	public void setUser(String user) {
		this.connPo.setUser(user);
	}

	public Integer getId() {
		return this.connPo.getId();
	}

	public void setId(Integer id) {
		this.connPo.setId(id);
	}

	public String getPassWord() {
		return this.connPo.getPassWord();
	}

	public void setPassWord(String passWord) {
		this.connPo.setPassWord(passWord);
	}

	public String getComment() {
		return this.connPo.getComment();
	}

	public void setComment(String comment) {
		this.connPo.setComment(comment);
	}

	// 自定义需要展示的时间格式, 转换成字符串
	public String DateTimeToString(Object obj, int sqlFiledtype) {
		return DateUtils.DbDateTimeToString(obj, sqlFiledtype);
	}

	public String getDriver() {
		return this.connPo.getDriver();
	}

	public void setDriver(String driver) {
		this.connPo.setDriver(driver);
	}

	public String getConnName() {
		return this.connPo.getConnName();
	}

	public String getHostOrFile() {
		return this.connPo.getHostOrFile();
	}

	public String getPort() {
		return this.connPo.getPort();
	}

	public String getDefaultSchema() {
		return this.connPo.getDefaultSchema();
	}

	public void setSchemas(Map<String, DbSchemaPo> schemas) {
		this.connPo.setSchemas(schemas);// = schemas;
	}

	public ExportDBObjects getExportDDL() {
		return this.connPo.getExportDDL();
	}

	public String getDbName() {
		return this.connPo.getDbName();
	}

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
	public abstract String dbRootNodeName();
	public abstract String translateErrMsg(String errString);
	public abstract Map<String, DbSchemaPo> getSchemas();
	public abstract Connection getConn();
	public abstract SqluckyConnector copyObj( String schema);
	public abstract String getRealDefaultSchema();
	public abstract String getJdbcUrl();
	
	/**
	 * 设置最后一次使用时间, 避免超时被另一个线程释放掉
	 */
	public void setConnectionLastUseTime() {
		if( connPo.getConn() == null) {
			Date time = new Date();
			connPo.setLastConnectTime(time);
		}
	}
	/**
	 * 获取缓存的连接
	 * @return
	 */
	public  Connection getCacheConn() {
		Date lastTime = connPo.getLastConnectTime();
		Date time = new Date();

		connPo.setLastConnectTime(time);
		if ( lastTime == null || connPo.getConn() == null ) {
			return null;
		}
		long interval = time.getTime() - lastTime.getTime() ;
		if( interval > 600_000L ) {
			logger.info("链接超时600秒, 需要重新链接");
			closeConn();
			return null;
		}
		return connPo.getConn();
	}
	
//	public static void main(String[] args) throws InterruptedException {
//		Date time = new Date();
//		Thread.sleep(2000);
//		Date time2= new Date();
//		System.out.println(time2.getTime() - time.getTime()) ;
//	}
}
