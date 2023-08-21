package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.utility.DateUtils;

public abstract class DbConnector implements SqluckyConnector { 

	private static Logger logger = LogManager.getLogger(DbConnector.class); 
	protected DBConnectorInfoPo connPo;
	protected TreeItem connTreeItem;
	protected SqluckyDbRegister dbRegister;
	
	@Override
	public SqluckyDbRegister getDbRegister() {
		return dbRegister;
	}
	
	public DbConnector(DBConnectorInfoPo c , SqluckyDbRegister dbRegister) {
		super();
		this.connPo = c;
		this.dbRegister = dbRegister;
	}
 
	
	//保存 树节点上显示的链接节点
	@Override
	public void setDbInfoTreeNode(TreeItem item) {
		this.connTreeItem = item;
	}
	@Override
	public TreeItem getDbInfoTreeNode() {
		return this.connTreeItem;
	}
	
	@Override
	public void setDBConnectorInfoPo(DBConnectorInfoPo po) {
		this.connPo = po;
	}
	@Override
	public DBConnectorInfoPo getDBConnectorInfoPo() {
		return this.connPo;
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
	@Override
	public boolean isAlive() {
		boolean tf = false; 
		if(finishInitNode( )) {
			if (this.connPo.getConn() != null) {
				boolean isClosed = true;
				try {
					isClosed = this.connPo.getConn().isClosed();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(! isClosed) {
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
	@Override
	public Set<String> settingSchema() {
		getConn(); // 先获取一下连接， 防止界面使用缓存数据
		Map<String, DbSchemaPo> map = getSchemas();
		return map.keySet();
	}

	@Override
	public String DBInfo(Connection conn) {
		String infoStr = Dbinfo.getDBInfo(conn);
		return infoStr;
	}
	
	@Override
	public abstract Connection getConn();

	// 关闭连接
	@Override
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

	@Override
	public String getDbVendor() {
		return this.connPo.getDbVendor();
	}

	public void setDbVendor(String dbVendor) {
		this.connPo.setDbVendor(dbVendor);
	}

	@Override
	public String getUser() {
		return this.connPo.getUser();
	}

	public void setUser(String user) {
		this.connPo.setUser(user);
	}

	@Override
	public Integer getId() {
		return this.connPo.getId();
	}

	@Override
	public void setId(Integer id) {
		this.connPo.setId(id);
	}

	@Override
	public String getPassWord() {
		return this.connPo.getPassWord();
	}

	public void setPassWord(String passWord) {
		this.connPo.setPassWord(passWord);
	}

	@Override
	public String getComment() {
		return this.connPo.getComment();
	}

	@Override
	public void setComment(String comment) {
//		this.comment = comment;
		this.connPo.setComment(comment);
	}
	
	@Override
	public String DateTimeToString(Object obj, int sqlFiledtype ) {
		
		String val = null; 
		if(obj instanceof LocalDateTime) {
			LocalDateTime ldt = (LocalDateTime) obj; 
			Date dv = Date.from( ldt.atZone( ZoneId.systemDefault()).toInstant());
			val = DateUtils.DateOrDateTimeToString(sqlFiledtype, dv);
		}
		else if(obj instanceof Date) {
			Date dv = (Date) obj;
			val = DateUtils.DateOrDateTimeToString(sqlFiledtype, dv);
		}else if(obj instanceof String) {
			val = (String) obj;
		}else if( obj instanceof Long) {
			Date date = new Date((long) obj);
			val = DateUtils.dateToStr(date, ConfigVal.dateFormateL); 
		}
	
		return val;
	}
	

	@Override
	public String getDriver() {
		return this.connPo.getDriver();
	}

	public void setDriver(String driver) {
		this.connPo.setDriver(driver);
	}

	@Override
	public String getConnName() {
		return this.connPo.getConnName();
	}

	@Override
	public String getHostOrFile() {
		return this.connPo.getHostOrFile();
	}

	@Override
	public String getPort() {
		return this.connPo.getPort();
	}


	@Override
	public String getDefaultSchema() {
		return this.connPo.getDefaultSchema();
	}
	
	public void setSchemas(Map<String, DbSchemaPo> schemas) {
		this.connPo.setSchemas(schemas);// = schemas;
	}

	@Override
	public ExportDBObjects getExportDDL() {
		return this.connPo.getExportDDL();
	}


	@Override
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
	
	@Override
	public boolean isJdbcUrlUse() {
		return this.connPo.isJdbcUrlUse();
	}
	
	@Override
	public boolean getAutoConnect() {
		return this.connPo.isAutoConnect();
	}
	
}
