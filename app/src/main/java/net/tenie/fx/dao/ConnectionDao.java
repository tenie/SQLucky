package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.DBTools;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.RsData;
import net.tenie.Sqlucky.sdk.utility.DateUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.AppWindowComponentGetter;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;
import net.tenie.fx.config.DbVendor;

public class ConnectionDao {
	private static Logger logger = LogManager.getLogger(ConnectionDao.class);

	public static void delete(Connection conn, int id) {
		String sql = "delete FROM  CONNECTION_INFO where id = " + id;
		try {
			DBTools.execDDL(conn, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//	更新节点的
	public static void refreshConnOrder() {
		Connection conn = SqluckyAppDB.getConn();
		try {
			logger.info("refreshConnOrder");
			TreeView<TreeNodePo> treeView = AppWindowComponentGetter.treeView;
			TreeItem<TreeNodePo> root = treeView.getRoot();
			ObservableList<TreeItem<TreeNodePo>> ls = root.getChildren();
			int size = ls.size();
			for (int i = 0; i < size; i++) {
				TreeItem<TreeNodePo> nopo = ls.get(i);
				String name = nopo.getValue().getName();
				SqluckyConnector po = DBConns.get(name);
				int id = po.getId();
				updateDataOrder(conn, id, i);
			}
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

	}
	

	public static void updateDataOrder(Connection conn, int id, int order) {
		String sql = " UPDATE CONNECTION_INFO  set  ORDER_TAG = " + order + "  where ID = " + id;
		try {
			DBTools.execDML(conn, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询
	 */
	public static List<SqluckyConnector> recoverConnObj() {
		Connection conn = SqluckyAppDB.getConn();
		String sql = "SELECT * FROM  CONNECTION_INFO ORDER BY ORDER_TAG";
		List<SqluckyConnector> datas = new ArrayList<SqluckyConnector>();
		try {
			List<RsData> rs = DBTools.selectSql(conn, sql);
			for (RsData rd : rs) {
				// VENDOR , 没有注册jdbc的数据库驱动就跳过
				String vendor = rd.getString("VENDOR");
				SqluckyDbRegister reg = DbVendor.register(vendor);
				if (reg == null)
					continue;

				DBConnectorInfoPo connPo = new DBConnectorInfoPo(rd.getString("CONN_NAME"), rd.getString("DRIVER"), // DbVendor.getDriver(dbDriver.getValue()),
						rd.getString("HOST"), rd.getString("PORT"), rd.getString("USER"), rd.getString("PASS_WORD"),
						rd.getString("VENDOR"), // dbDriver.getValue(),
						rd.getString("SCHEMA"), // defaultSchema.getText()
						rd.getString("DB_NAME"), rd.getString("JDBC_URL"),
						1 == rd.getInteger("AUTO_CONNECT") ? true : false);
				// 设置使用jdbc url的flag
				if (StrUtils.isNullOrEmpty(connPo.getHostOrFile()) && StrUtils.isNullOrEmpty(connPo.getPort())
						&& StrUtils.isNotNullOrEmpty(connPo.getJdbcUrl())) {
					connPo.setJdbcUrlUse(true);
				} else {
					connPo.setJdbcUrlUse(false);
				}

				SqluckyConnector po = reg.createConnector(connPo);
				po.setId(rd.getInteger("ID"));
				po.setComment(rd.getString("COMMENT"));
				datas.add(po);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

		return datas;
	}

	// 通过DBConnectorInfoPo集合来创建需要恢复的数据库链接tree节点数据
	public static List<SqluckyConnector> recoverConnObj(List<DBConnectorInfoPo> dbciPo) {
		List<SqluckyConnector> datas = new ArrayList<SqluckyConnector>();
		try {
			for (DBConnectorInfoPo connPo : dbciPo) {
				// VENDOR connPo.getDbVendor
				String vendor = connPo.getDbVendor();
				SqluckyDbRegister reg = DbVendor.register(vendor);
				if (reg == null)
					continue;
				// 设置使用jdbc url的flag
				if (StrUtils.isNullOrEmpty(connPo.getHostOrFile()) && StrUtils.isNullOrEmpty(connPo.getPort())
						&& StrUtils.isNotNullOrEmpty(connPo.getJdbcUrl())) {
					connPo.setJdbcUrlUse(true);
				} else {
					connPo.setJdbcUrlUse(false);
				}

				SqluckyConnector po = reg.createConnector(connPo);
				datas.add(po);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return datas;
	}

	// 保存
	public static SqluckyConnector createOrUpdate(Connection conn, SqluckyConnector po) {
		Integer id = po.getId();
		String connName = po.getConnName();
		String user = po.getUser();
		String passWord = po.getPassWord();
		String hostOrFile = po.getHostOrFile();
		String port = po.getPort();
		String driver = po.getDriver();
		String dbVendor = po.getDbVendor();
		String defaultSchema = po.getDefaultSchema();
		String comment = po.getComment();
		String dbName = po.getDbName();
		String jdbcurl = po.getJdbcUrl();
		int autoC = po.getAutoConnect() ? 1 : 0;

//		    Date createdAt =      po.getCreatedAt();
//		    Date updatedAt =      po.getUpdatedAt();
//		    Integer recordVersion =  po.getRecordVersion(); 
		String sql = "";
		if (id != null && id > 0) {
			// 更新
			sql = " update CONNECTION_INFO  set " + " CONN_NAME = '" + connName + "' , " + " USER = '" + user + "' ,"
					+ " PASS_WORD = '" + passWord + "' ," + " HOST = '" + hostOrFile + "' ," + " PORT = '" + port
					+ "' , " + " DRIVER = '" + driver + "' , " + " VENDOR = '" + dbVendor + "' , "

					+ " SCHEMA = '" + defaultSchema + "', " + " DB_NAME = '" + dbName + "', " + " JDBC_URL = '"
					+ jdbcurl + "', " + " AUTO_CONNECT = " + autoC + ", "

					+ " UPDATED_AT = '" + DateUtils.dateToStrL(new Date()) + "'";
			if (StrUtils.isNotNullOrEmpty(comment)) {
				sql += ", COMMENT = '" + comment + "' ";
			}
			sql += " where ID = " + id;

		} else {
			// 插入
			sql = " INSERT INTO CONNECTION_INFO "
					+ "(CONN_NAME , USER, PASS_WORD, HOST, PORT, DRIVER,VENDOR, SCHEMA, DB_NAME, JDBC_URL, AUTO_CONNECT, CREATED_AT ";
			if (StrUtils.isNotNullOrEmpty(comment)) {
				sql += ", COMMENT ";
			}

			sql += ") values( " + "'" + connName + "' , " + "'" + user + "' ," + "'" + passWord + "' ," + "'"
					+ hostOrFile + "' ," + "'" + port + "' , " + "'" + driver + "' , " + "'" + dbVendor + "' , " + "'"
					+ defaultSchema + "', " + "'" + dbName + "', " + "'" + jdbcurl + "', " + autoC + ", "

					+ "'" + DateUtils.dateToStrL(new Date()) + "'";
			if (StrUtils.isNotNullOrEmpty(comment)) {
				sql += ", '" + comment + "' ";
			}
			sql += ")";
		}
		try {
			int val = DBTools.execInsertReturnId(conn, sql);

			if (val > 0) {
				logger.info("insert sql return id = " + val);
				po.setId(val);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		logger.info(po.toString());
		return po;
	}

	/**
	 * 删除 CONNECTION_INFO 表数据
	 * @param conn
	 */
	public static  void deleteConnectionInfo(Connection conn) {
		try {
			DBTools.execDelTab(conn, "CONNECTION_INFO");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除 SCRIPT_ARCHIVE 表数据
	 * @param conn
	 */
	public static  void deleteScript(Connection conn) {
		try {
			DBTools.execDelTab(conn, "SCRIPT_ARCHIVE");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 根据名称查找旧数据, 如果查询的名称已经存在就添加后缀查找, 直到名称为唯一的返回
	public static String queryConnectionInfoName(Connection conn, String qname) {
		String val = DBTools.selectOne(conn, "Select CONN_NAME from CONNECTION_INFO WHERE CONN_NAME = '"+qname+"'");
		if("".equals(val)) {
			return qname;
		}else {
			val = queryConnectionInfoName(conn, qname+"*");
		}
		return val; 
	}
	
	// 从新创建dbinfoTree的数据, 删除旧数据
	public static void DBInfoTreeReCreate(List<DBConnectorInfoPo> dbciPo) {
		if(dbciPo == null || dbciPo.size() == 0) return;
		Connection conn = SqluckyAppDB.getConn();
		try {
			// 删除表里的旧数据
			deleteConnectionInfo(conn);
			// 处理数据DBConnectorInfoPo 转为SqluckyConnector
			List<SqluckyConnector>  ls = recoverConnObj(dbciPo);
			// 将新数据插入到表里
			for(SqluckyConnector item : ls) {
				createOrUpdate(conn, item);
			}
			// 页面数据清空, 再加载新数据
			DBinfoTree.cleanRootRecoverNodeFromList(ls);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
	// 将DBConnectorInfoPo 的连接名称和数据库中已有的连接名称重复就重命名
	public static void renameOverlapDBinfoName(List<DBConnectorInfoPo> dbciPo) {
		Connection conn = SqluckyAppDB.getConn();
		try {
			if(dbciPo != null && dbciPo.size() > 0) {
				for(var po : dbciPo) {
					String name = po.getConnName();
					String val = queryConnectionInfoName(conn, name);
					po.setConnName(val);
				}
			}
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
		
	} 
	
	// 将新的数据库连接数据和旧数据合并起来
	public static void DBInfoTreeMerge(List<DBConnectorInfoPo> dbciPo) {
		if(dbciPo == null || dbciPo.size() == 0) return;
		Connection conn = SqluckyAppDB.getConn();
		try {
			// 判断链接名称是否重复, 重复添加后缀
			renameOverlapDBinfoName(dbciPo);
			
			// 处理数据DBConnectorInfoPo 转为SqluckyConnector
			List<SqluckyConnector>  ls = recoverConnObj(dbciPo);
			// 将新数据插入到表里
			for(SqluckyConnector item : ls) {
				createOrUpdate(conn, item);
			}
			// 从数据库从新读取数据
			List<SqluckyConnector> datas = ConnectionDao.recoverConnObj();
			// 页面数据清空, 再加载新数据
			DBinfoTree.cleanRootRecoverNodeFromList(datas);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	// 从新创建scriptTree的数据, 删除旧数据
	public static void scriptTreeReCreate(List<DocumentPo> docPo) {
		if(docPo == null || docPo.size() == 0) return;
		Connection conn = SqluckyAppDB.getConn();
		try {
			// 删除表里的旧数据
			deleteScript(conn);
			// 清空并还原
			ScriptTabTree.cleanOldAndRecover(docPo); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
	// 将新sript数据和旧数据合并起来
	public static void scriptTreeMerge(List<DocumentPo> docPo) {
		if(docPo == null || docPo.size() == 0) return;
		
		List<DocumentPo> tmpDocs = new ArrayList<>();
		Connection conn = SqluckyAppDB.getConn();
		try {
			// 相同内容跳过
			TreeItem<SqluckyTab>  root = ScriptTabTree.rootNode;
			ObservableList<TreeItem<SqluckyTab>> ls = root.getChildren();
			for(TreeItem<SqluckyTab> item : ls) {
				DocumentPo treeDoc = item.getValue().getDocumentPo();
				var treetitle = treeDoc.getTitle();
				var treetext = treeDoc.getText();
				for(var doc : docPo) {
					String title = doc.getTitle();
					String text = doc.getText();
					if(treetitle.equals(title) && treetext.equals(text) ) {
						logger.debug(treetitle+" : 找到相同的");
						tmpDocs.add(doc);
						break;
					}
					
				}
			}
			
			
			// 合并还原
			if(tmpDocs.size() > 0 ) {
				for(var tmpdoc : tmpDocs) {
					docPo.remove(tmpdoc);
				}
				ScriptTabTree.recoverFromDocumentPos(docPo); 
				ConfigVal.pageSize += tmpDocs.size();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
}
