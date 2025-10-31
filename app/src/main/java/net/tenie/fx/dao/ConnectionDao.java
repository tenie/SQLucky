package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.DBTools;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.RsData;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.utility.DateUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;
import net.tenie.fx.component.container.AppWindow;
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

	// 更新数据库链接节点的顺序
	public static void refreshConnOrder(Connection conn) {
		logger.info("refreshConnOrder");
		TreeView<TreeNodePo> treeView = AppWindow.treeView;
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
	}

	// 更新数据库链接节点的顺序
	public static void refreshConnOrderByNewItem(Integer addId) {
		List<RsData>  ls = selectConnectionInfo();
		List<RsData> tmpList = new ArrayList<>();
		RsData addRsData  = null;
		if(ls !=null && ls.size()> 0){
			for(var data : ls){
				if(data.getInteger("ID").equals(addId)){
					addRsData = data;
				}else {
					tmpList.add(data);
				}
			}
		}
		tmpList.add(0, addRsData);
		// 更新报错
		refreshConnOrder(tmpList);
	}
	// 根据提供给的列表来更新顺序
	public static void refreshConnOrder(List<RsData> ls) {
		Connection conn = SqluckyAppDB.getConn();
		try {
			logger.info("refreshConnOrder");
			int size = ls.size();
			for (int i = 0; i < size; i++) {
				RsData data = ls.get(i);
				int id = data.getInteger("ID");
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
	 * 更新位置
	 */
	public static void updatePosition(String target, String nextSib){
		List<RsData>  ls = selectConnectionInfo();
		List<RsData> tmpList = new ArrayList<>();
		List<RsData> tmpList2 = new ArrayList<>();
		RsData tgDa = null;
		// 第一次循环找出 target ,
		for(var da : ls ){
			var name = da.getString("CONN_NAME");
			if(name.equals(target)){
				tgDa = da;
			}else {
				tmpList.add(da);
			}
		}
		if(tgDa == null){
			return;
		}

		// 如果nextSib是空, 就把找到的target 放最后
		if(StrUtils.isNullOrEmpty(nextSib)){
			tmpList.add(tgDa);
			tmpList2 = tmpList;

			// 找到 nextSib , 在他之前加入target
		}else {
			for(var data : tmpList ){
				var name = data.getString("CONN_NAME");
				if(name.equals(nextSib)){
					tmpList2.add(tgDa);
				}
				tmpList2.add(data);
			}
		}

		// 新的顺序插入数据库
		ConnectionDao.refreshConnOrder(tmpList2);

	}


	/**
	 * 查询
	 */
	public static  List<RsData> selectConnectionInfo(){
		Connection conn = SqluckyAppDB.getConn();
		String sql = "SELECT * FROM  CONNECTION_INFO ORDER BY ORDER_TAG";
		try {
			return DBTools.selectSql(conn, sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}finally {
			SqluckyAppDB.closeConn(conn);
		}

	}

	/**
	 * 从数据库恢复数据
	 * @return
	 */
	public static List<SqluckyConnector> recoverConnObj() {
		String sql = "SELECT * FROM  CONNECTION_INFO ORDER BY ORDER_TAG";
		List<SqluckyConnector> datas = new ArrayList<SqluckyConnector>();

		List<RsData> rs = selectConnectionInfo();
		for (RsData rd : rs) {
			// VENDOR , 没有注册jdbc的数据库驱动就跳过
			String vendor = rd.getString("VENDOR");
			SqluckyDbRegister reg = DbVendor.register(vendor);
			if (reg == null) {
				continue;
			}

			DBConnectorInfoPo connPo = new DBConnectorInfoPo(rd.getString("CONN_NAME"), rd.getString("DRIVER"),
					rd.getString("HOST"), rd.getString("PORT"), rd.getString("USER"), rd.getString("PASS_WORD"),
					rd.getString("VENDOR"),
					rd.getString("SCHEMA"),
					rd.getString("DB_NAME"), rd.getString("JDBC_URL"),
					1 == rd.getInteger("AUTO_CONNECT"));
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
				if (reg == null) {
					continue;
				}
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
		}

		return datas;
	}

	// 保存
	public static void createOrUpdate(Connection conn, SqluckyConnector po) {
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
	}

	/**
	 * 删除 CONNECTION_INFO 表数据
	 *
	 * @param conn
	 */
	public static void deleteConnectionInfo(Connection conn) {
		try {
			DBTools.execDelTab(conn, "CONNECTION_INFO");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除 SCRIPT_ARCHIVE 表数据
	 *
	 * @param conn
	 */
	public static void deleteScript(Connection conn) {
		try {
			DBTools.execDelTab(conn, "SCRIPT_ARCHIVE");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 根据名称查找旧数据, 如果查询的名称已经存在就添加后缀查找, 直到名称为唯一的返回
	public static String queryConnectionInfoName(Connection conn, String qname) {
		String val = DBTools.selectOne(conn, "Select CONN_NAME from CONNECTION_INFO WHERE CONN_NAME = '" + qname + "'");
		if ("".equals(val)) {
			return qname;
		} else {
			val = queryConnectionInfoName(conn, qname + "*");
		}
		return val;
	}

	// 从新创建dbinfoTree的数据, 删除旧数据
	public static void DBInfoTreeReCreate(List<DBConnectorInfoPo> dbciPo) {
		if (dbciPo == null || dbciPo.isEmpty()) {
			return;
		}
		Connection conn = SqluckyAppDB.getConn();
		try {
			// 删除表里的旧数据
			deleteConnectionInfo(conn);
			// 处理数据DBConnectorInfoPo 转为SqluckyConnector
			List<SqluckyConnector> ls = recoverConnObj(dbciPo);
			// 将新数据插入到表里
			for (SqluckyConnector item : ls) {
				createOrUpdate(conn, item);
			}
			// 页面数据清空, 再加载新数据
			DBinfoTree.cleanRootRecoverNodeFromList(ls);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	// 将DBConnectorInfoPo 的连接名称和数据库中已有的连接名称重复就重命名
	public static void renameOverlapDBinfoName(List<DBConnectorInfoPo> dbciPo) {
		Connection conn = SqluckyAppDB.getConn();
		try {
			if (dbciPo != null && dbciPo.size() > 0) {
				for (var po : dbciPo) {
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
		if (dbciPo == null || dbciPo.isEmpty()) {
			return;
		}
		Connection conn = SqluckyAppDB.getConn();
		try {
			// 判断链接名称是否重复, 重复添加后缀
			renameOverlapDBinfoName(dbciPo);

			// 处理数据DBConnectorInfoPo 转为SqluckyConnector
			List<SqluckyConnector> ls = recoverConnObj(dbciPo);
			// 将新数据插入到表里
			for (SqluckyConnector item : ls) {
				createOrUpdate(conn, item);
			}
			// 从数据库从新读取数据
			List<SqluckyConnector> datas = ConnectionDao.recoverConnObj();
			// 页面数据清空, 再加载新数据
			DBinfoTree.cleanRootRecoverNodeFromList(datas);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	// 从新创建scriptTree的数据, 删除旧数据
	public static void scriptTreeReCreate(List<DocumentPo> docPo) {
		if (docPo == null || docPo.isEmpty()) {
			return;
		}
		Connection conn = SqluckyAppDB.getConn();
		try {
			// 删除表里的旧数据
			deleteScript(conn);
			// 清空并还原
			ScriptTabTree.cleanOldAndRecover(docPo);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	// 将新sript数据和旧数据合并起来
	public static void scriptTreeMerge(List<DocumentPo> docPo) {
		if (docPo == null || docPo.isEmpty()) {
			return;
		}

		List<DocumentPo> tmpDocs = new ArrayList<>();
		Connection conn = SqluckyAppDB.getConn();
		try {
			// 相同内容跳过
			TreeItem<MyEditorSheet> root = ScriptTabTree.rootNode;
			ObservableList<TreeItem<MyEditorSheet>> ls = root.getChildren();
			for (TreeItem<MyEditorSheet> item : ls) {
				DocumentPo treeDoc = item.getValue().getDocumentPo();
				var treetitle = treeDoc.getTitle();
				var treetext = treeDoc.getText();
				for (var doc : docPo) {
					String title = doc.getTitle().get();
					String text = doc.getText();
					if (treetitle.equals(title) && treetext.equals(text)) {
						logger.debug(treetitle + " : 找到相同的");
						tmpDocs.add(doc);
						break;
					}

				}
			}

			// 合并还原
			if (!tmpDocs.isEmpty()) {
				for (var tmpdoc : tmpDocs) {
					docPo.remove(tmpdoc);
				}
				ScriptTabTree.recoverFromDocumentPos(docPo);
				ConfigVal.pageSize += tmpDocs.size();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	/**
	 * 导入链接
	 * @param po
	 */
	public static void dbInfoImport(DBConnectorInfoPo po  ){
		// 判断链接名称是否重复, 重复添加后缀
		renameOverlapDBinfoName(List.of(po));
		String connectionName = po.getConnName();
		SqluckyDbRegister dbRegister = DbVendor.register(po.getDbVendor());
		SqluckyConnector sqluckyConnnector = dbRegister.createConnector(po);

		TreeNodePo tnpo = new TreeNodePo(connectionName,
				IconGenerator.svgImageUnactive("unlink"));
		tnpo.setType(TreeItemType.CONNECT_INFO);
		TreeItem<TreeNodePo> item = new TreeItem<>(tnpo);
		DBinfoTree.treeRootAddItem(item);

		// 缓存数据
		DBConns.add(connectionName, sqluckyConnnector);
		var conn = SqluckyAppDB.getConn();
		try{
			ConnectionDao.createOrUpdate(conn, sqluckyConnnector);
		}finally {
			SqluckyAppDB.closeConn(conn);
		}

	}
}
