package net.tenie.Sqlucky.sdk;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheet;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DBNodeInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.TreeItemType;

public interface AppComponent {
	// 保存app 状态(关闭app时)
	void saveApplicationStatusInfo();
	// 重启app
	void reboot();
	   MyEditorSheet findMyTabByScriptPo(DocumentPo scpo);
	// 设置文件打开时候目录path, 便于二次打开可以直达该目录
	 void setOpenfileDir(String val);

	 void runSQL(SqluckyConnector sqlConn, String sqlv, boolean isCreateFunc);

	 void runSQL(String sqlv);

	 SqluckyEditor createCodeArea();


	/**
	 *  查看table ddl界面 执行查询按钮, 不刷新底部tab
	 * @param sqlConn
	 * @param sqlv
	 * @param limit 限制查询的行数, 目前给的是20
	 */
	 void runSelectSqlLockTabPane(SqluckyConnector sqlConn, String sqlv, Integer limit);

	Long refreshDataTableView(String connName, String sql, String idx, boolean isLock);
	Long refreshDataTableView(SqluckyConnector dbconnPo, String sql, String idx, boolean isLock);
	void addTitledPane(TitledPane tp);

	void addIconBySvg(String name, String svg);

	// 创建 SqluckyTab
//	SqluckyTab sqluckyTab();

//	SqluckyTab sqluckyTab(DocumentPo po);

	void tabPaneRemoveSqluckyTab(MyEditorSheet stb);

	 Region getIconUnactive(String name);

	 Region getIconDefActive(String name);

	 void saveData(String name, String key, String value);


	 void deleteData(String name, String key);
	 void deletePluginAllData(String pluginName);
	 String fetchData(String name, String key);

	 List<String> fetchAllData(String pluginName, String key);

	 List<String> fetchAllData(String pluginName);

	// 注册数据库连接对象 DB DB2Connector
	 void registerDBConnector(SqluckyDbRegister ctr);

	// 让插件可以对数据库节点右键菜单增加插件的菜单按钮
	 void registerDBInfoMenu(List<Menu> ms, List<MenuItem> mis);

	// 获取选中的 dbInfo 节点的类型(表格, 视图, 等)
	 TreeItemType currentDBInfoNodeType();

	// 获取 dbInfo 上的数据库连接对象
	 DBNodeInfoPo currentDBInfoNode();

	// DBInfoMenu 显示的时候调用
	 void setDBInfoMenuOnShowing(Consumer<String> caller);

	 Consumer<String> getDBInfoMenuOnShowing();

	// 执行dml sql语句
	 boolean execDML(String sql);

	// TODO 创建数据tableview
//	 SqluckyBottomSheet sqlDataSheet(MyBottomSheet rs, SheetDataValue data, int idx, boolean disable);

//	 SqluckyBottomSheet tableViewSheet(SheetDataValue data, List<Node> nodeLs);
//	 SqluckyBottomSheet tableViewSheet(MyBottomSheet myBottomSheet, List<Node> btnLs);

	// 表, 视图 等 数据库对象的ddl语句
//	 SqluckyBottomSheet ddlSheet(SqluckyConnector sqluckyConn, String name, String ddl, boolean isRunFunc,
//			boolean isSelect);

//	 SqluckyBottomSheet tableInfoSheet(SqluckyConnector sqluckyConn, TablePo table);

//	 SqluckyBottomSheet ProcedureSheet(SqluckyConnector sqluckyConn, String name, String ddl, boolean isRunFunc);

//	 SqluckyBottomSheet EmptySheet(SqluckyConnector sqluckyConn, String name, String message);

	// 获取 DBconns 中保存的所有数据库链接信息
	 Map<String, SqluckyConnector> getAllConnector();

	// 获取 DBinfoTree 中链接的所有名称
	 List<String> getAllConnectorName();

	// 获取 DBinfoTree 中链接的所有激活的名称
	 List<String> getAllActiveConnectorName();

	// 登入窗口
	 void showSingInWindow(String title);

	/**
	 * 使用新数据重建数据库连接节点树
	 */
	 void recreateDBinfoTreeData(List<DBConnectorInfoPo> dbciPo);

	/**
	 * 使用新数据合并入数据库连接节点树
	 */
	 void mergeDBinfoTreeData(List<DBConnectorInfoPo> dbciPo);

	 void recreateScriptTreeData(List<DocumentPo> docs);

	 void mergeScriptTreeData(List<DocumentPo> docs);

	// 创建一个DocumentPo对象, 并保存在数据库
	 DocumentPo scriptArchive(String title, String txt, String filename, String encode, int paragraph, int tabPosition);

	 void updateScriptArchive(Connection conn, DocumentPo po);

	// 给脚本treeView 添加子节点
	 void scriptTreeAddItem(MyEditorSheet sheet);

	// 刷新树
	 void scriptTreeRefresh();
	
	// DBinfo 查询按钮
	 void DBinfoTreeFilterHide();

	 void registerBottomSheetExportMenu(Function<MyBottomSheet, MenuItem > func);
	 List<Function<MyBottomSheet, MenuItem>> getBottomSheetBtns();
	 List<MenuItem> getBottomSheetBtns(MyBottomSheet sheet);
	// 保存 自动补全文本
	 void saveAutoCompleteText(String val);

}
