package net.tenie.sdkImp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.control.Accordion;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.editor.HighLightingEditor;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DBNodeInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.InfoTree.DBinfoTreeButtonFactory;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.dao.ConnectionDao;
import net.tenie.fx.dao.DmlDdlDao;
import net.tenie.fx.main.Restart;
import net.tenie.fx.window.SignInWindow;
import net.tenie.lib.db.h2.AppDao;

public class SqluckyAppComponent implements AppComponent {
	public List<Function<MyBottomSheet, MenuItem > > bottomSheetBtns = new ArrayList<>();

	private Consumer<String> dbInfoMenuOnShowingCaller;
	
	public void saveApplicationStatusInfo() {
		
	}
	
	
	// 重启app
	@Override
	public void reboot(){
		Restart.reboot();
	}
	@Override
	public   MyEditorSheet findMyTabByScriptPo(DocumentPo scpo) {
		MyEditorSheet sheet = ScriptTabTree.findMyTabByScriptPo(scpo);
		return sheet;
	}
	

	// 设置文件打开时候目录path, 便于二次打开可以直达该目录
	@Override
	public void setOpenfileDir(String val) {
		AppCommonAction.setOpenfileDir(val);
	}

	@Override
	public SqluckyEditor createCodeArea() {
		return new HighLightingEditor(null, null);
	}

	/**
	 * 根据自己提供的SqluckyConnector, 来执行sql
	 * 
	 * @param sqlConn
	 * @param sqlv
	 * @param isCreateFunc 执行create 语句
	 */
	@Override
	public void runSQL(SqluckyConnector sqlConn, String sqlv, boolean isCreateFunc) {
		RunSQLHelper.runSQL(sqlConn, sqlv, isCreateFunc);
	}

	/*
	 * 查看table ddl界面 执行查询按钮, 不刷新底部tab
	 */
	@Override
	public void runSelectSqlLockTabPane(SqluckyConnector sqlConn, String sqlv, Integer limit) {
		RunSQLHelper.runSelectSqlLockTabPane(sqlConn, sqlv, limit);
	}

	@Override
	public void addTitledPane(TitledPane tp) {
		Accordion ad = ComponentGetter.infoAccordion;
		if (ad != null) {
			ad.getPanes().add(tp);
		}
	}

	@Override
	public void addIconBySvg(String name, String svg) {
		IconGenerator.addSvgStr(name, svg);
	}

	/**
	 * 获取图标
	 */
	@Override
	public Region getIconUnactive(String name) {
		return IconGenerator.svgImageUnactive(name);
	}

	/**
	 * 获取图标
	 */
	@Override
	public Region getIconDefActive(String name) {
		return IconGenerator.svgImageDefActive(name);
	}

	/**
	 * 保持插件存储的key_value
	 */
	@Override
	public void saveData(String pluginName, String key, String value) {
		var conn = SqluckyAppDB.getConn();
		try {
			SqluckyAppDB.savePluginConfig(conn, pluginName, key, value);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	/**
	 * 删除插件存储的key_value
	 */
	@Override
	public void deleteData(String pluginName, String key){
		var conn = SqluckyAppDB.getConn();
		try {
			SqluckyAppDB.deletePluginConfigKey(conn, pluginName, key);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	/**
	 * 删除插件存储的key_value
	 */
	@Override
	public void deletePluginAllData(String pluginName){
		var conn = SqluckyAppDB.getConn();
		try {
			SqluckyAppDB.deletePluginAllConfig(conn, pluginName);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	/**
	 * 获取插件存储的key_value
	 */
	@Override
	public String fetchData(String pluginName, String key) {
		String val = "";
		var conn = SqluckyAppDB.getConn();
		try {
			val = SqluckyAppDB.readPluginConfig(conn, pluginName, key);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
		return val;
	}
	@Override
	public List<String> fetchAllData(String pluginName, String key){
		List<String>  vals;
		var conn = SqluckyAppDB.getConn();
		try {
			vals = SqluckyAppDB.readPluginAllConfig(conn, pluginName, key);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

		return vals;
	}


	@Override
	public List<String> fetchAllData(String pluginName){
		List<String>  vals;
		var conn = SqluckyAppDB.getConn();
		try {
			vals = SqluckyAppDB.readPluginAllConfig(conn, pluginName);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

		return vals;
	}

	@Override
	public void tabPaneRemoveSqluckyTab(MyEditorSheet stb) {
		var myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane.getTabs().contains(stb)) {
			myTabPane.getTabs().remove(stb);
		}else {
			myTabPane = ComponentGetter.rightTabPane;
			if (myTabPane.getTabs().contains(stb)) {
				myTabPane.getTabs().remove(stb);
			}
		}

	}

	@Override
	public void registerDBConnector(SqluckyDbRegister ctr) {
		DbVendor.registerDbConnection(ctr);
	}

	@Override
	public boolean execDML(String sql) {
		boolean succeed = false;
		var conn = SqluckyAppDB.getConn();
		try {
			DmlDdlDao.execDML(conn, sql);
			succeed = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return succeed;
	}

	// 注册db节点的右键菜单
	@Override
	public void registerDBInfoMenu(List<Menu> otherDBMenu, List<MenuItem> otherDBMenuItem) {
		var contextMenu = ComponentGetter.dbInfoTreeContextMenu;
		if (otherDBMenu != null && otherDBMenu.size() > 0) {
			contextMenu.getItems().add(new SeparatorMenuItem());
			for (var mn : otherDBMenu) {
				contextMenu.getItems().add(mn);
			}
		}
		if (otherDBMenuItem != null && otherDBMenuItem.size() > 0) {
			contextMenu.getItems().add(new SeparatorMenuItem());
			for (var mnitem : otherDBMenuItem) {
				contextMenu.getItems().add(mnitem);
			}
		}
	}

	/**
	 * 获取选中的 dbInfo 节点的类型(表格, 视图, 等)
	 */
	@Override
	public TreeItemType currentDBInfoNodeType() {
		TreeItem<TreeNodePo> item = DBinfoTree.DBinfoTreeView.getSelectionModel().getSelectedItem();
		TreeNodePo np = item.getValue();
		return np.getType();
	}

	@Override
	public DBNodeInfoPo currentDBInfoNode() {
		TreeItem<TreeNodePo> item = DBinfoTree.DBinfoTreeView.getSelectionModel().getSelectedItem();
		TreeNodePo np = item.getValue();
		return np.getDbNodeInfoPo();
	}

	@Override
	public void setDBInfoMenuOnShowing(Consumer<String> caller) {
		dbInfoMenuOnShowingCaller = caller;

	}

	@Override
	public Consumer<String> getDBInfoMenuOnShowing() {
		return dbInfoMenuOnShowingCaller;
	}

	@Override
	public Map<String, SqluckyConnector> getAllConnector() {
		return DBConns.getDbs();
	}

	/**
	 * 获取链接名称
	 */
	@Override
	public List<String> getAllConnectorName() {
		List<String> dbConnNames = new ArrayList<>();
		TreeView<TreeNodePo> tv = DBinfoTree.DBinfoTreeView;
		TreeItem<TreeNodePo> root = tv.getRoot();
		var childrenList = root.getChildren();
		if (childrenList != null && childrenList.size() > 0) {
			for (var item : childrenList) {
				TreeNodePo po = item.getValue();
				String name = po.getName();
				dbConnNames.add(name);
			}
		}
		return dbConnNames;
	}

	@Override
	public List<String> getAllActiveConnectorName() {
		List<String> dbConnNames = new ArrayList<>();
		TreeView<TreeNodePo> tv = DBinfoTree.DBinfoTreeView;
		TreeItem<TreeNodePo> root = tv.getRoot();
		var childrenList = root.getChildren();
		if (childrenList != null && childrenList.size() > 0) {
			for (var item : childrenList) {
				if (item.getChildren() != null && item.getChildren().size() > 0) {
					TreeNodePo po = item.getValue();
					String name = po.getName();
					dbConnNames.add(name);
				}
			}
		}
		return dbConnNames;
	}

	// 登入窗口
	@Override
	public void showSingInWindow(String title) {
		SignInWindow.show(title);
	}

	/**
	 * 使用新数据重建数据库连接节点树
	 */
	@Override
	public void recreateDBinfoTreeData(List<DBConnectorInfoPo> dbciPo) {
		ConnectionDao.DBInfoTreeReCreate(dbciPo);
	}

	/**
	 * 使用新数据合并入数据库连接节点树
	 */
	@Override
	public void mergeDBinfoTreeData(List<DBConnectorInfoPo> dbciPo) {
		ConnectionDao.DBInfoTreeMerge(dbciPo);
	}

	/**
	 * 使用新数据重建数据库连接节点树
	 */
	@Override
	public void recreateScriptTreeData(List<DocumentPo> docs) {
		ConnectionDao.scriptTreeReCreate(docs);

	}

	/**
	 * 使用新数据合并入数据库连接节点树
	 */
	@Override
	public void mergeScriptTreeData(List<DocumentPo> docs) {
		ConnectionDao.scriptTreeMerge(docs);
	}

	@Override
	public Long refreshDataTableView(String connName, String sql, String idx, boolean isLock) {

		return RunSQLHelper.refresh(DBConns.get(connName), sql, idx, isLock);
	}
	@Override
	public Long refreshDataTableView(SqluckyConnector dbconnPo, String sql, String idx, boolean isLock) {

		return RunSQLHelper.refresh(dbconnPo, sql, idx, isLock);
	}

	// 创建一个DocumentPo对象, 并保存在数据库
	@Override
	public DocumentPo scriptArchive(String title, String txt, String filename, String encode, int paragraph, int tabPosition) {
		return AppDao.scriptArchive(title, txt, filename, encode, paragraph, tabPosition);
	}

	@Override
	public void updateScriptArchive(Connection conn, DocumentPo po) {
		AppDao.updateScriptArchive(conn, po);
	}

	// 给脚本treeView 添加子节点
	@Override
	public void scriptTreeAddItem(MyEditorSheet sheet) {
		ScriptTabTree.treeRootAddItem(sheet);
	}

	@Override
	public void scriptTreeRefresh() {
		ScriptTabTree.ScriptTreeView.refresh();
	}
	
	// DBinfo 查询按钮
	@Override
	public void DBinfoTreeFilterHide() {
		DBinfoTreeButtonFactory.queryBtnHide();
	}


	// 注册 底部按钮
	@Override
	public void registerBottomSheetExportMenu(Function<MyBottomSheet, MenuItem > func) {
		bottomSheetBtns.add(func);
	}

	@Override
	public List<Function<MyBottomSheet, MenuItem>> getBottomSheetBtns() {
		return bottomSheetBtns;
	}

	@Override
	public List<MenuItem> getBottomSheetBtns(MyBottomSheet sheet) {
		List<MenuItem> ls = new ArrayList<>();
		for(var func :bottomSheetBtns){
			if(func != null){
				MenuItem tmp = func.apply(sheet);
				ls.add(tmp);
			}
		}

		return ls;
	}

	@Override
	public void saveAutoCompleteText(String val) {
		AppDao.saveAutoCompleteText(val);
	}
}
