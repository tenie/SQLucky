package net.tenie.Sqlucky.sdk;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DBNodeInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.TreeItemType;

public interface AppComponent {
	// 设置文件打开时候目录path, 便于二次打开可以直达该目录
	public void setOpenfileDir(String val);

	public void runSQL(SqluckyConnector sqlConn, String sqlv, boolean isCreateFunc);

	public SqluckyEditor createCodeArea();

	/*
	 * 查看table ddl界面 执行查询按钮, 不刷新底部tab
	 */
	public void runSelectSqlLockTabPane(SqluckyConnector sqlConn, String sqlv);

	void refreshDataTableView(String connName, String sql, String idx, boolean isLock);

	void addTitledPane(TitledPane tp);

	void addIconBySvg(String name, String svg);

	// 创建 SqluckyTab
//	SqluckyTab sqluckyTab();

//	SqluckyTab sqluckyTab(DocumentPo po);

	void tabPaneRemoveSqluckyTab(MyEditorSheet stb);

	public Region getIconUnactive(String name);

	public Region getIconDefActive(String name);

	public void saveData(String name, String key, String value);

	public String fetchData(String name, String key);

	// 注册数据库连接对象 DB DB2Connector
	public void registerDBConnector(SqluckyDbRegister ctr);

	// 让插件可以对数据库节点右键菜单增加插件的菜单按钮
	public void registerDBInfoMenu(List<Menu> ms, List<MenuItem> mis);

	// 获取选中的 dbInfo 节点的类型(表格, 视图, 等)
	public TreeItemType currentDBInfoNodeType();

	// 获取 dbInfo 上的数据库连接对象
	public DBNodeInfoPo currentDBInfoNode();

	// DBInfoMenu 显示的时候调用
	public void setDBInfoMenuOnShowing(Consumer<String> caller);

	public Consumer<String> getDBInfoMenuOnShowing();

	// 执行dml sql语句
	public boolean execDML(String sql);

	// TODO 创建数据tableview
//	public SqluckyBottomSheet sqlDataSheet(MyBottomSheet rs, SheetDataValue data, int idx, boolean disable);

//	public SqluckyBottomSheet tableViewSheet(SheetDataValue data, List<Node> nodeLs);
//	public SqluckyBottomSheet tableViewSheet(MyBottomSheet myBottomSheet, List<Node> btnLs);

	// 表, 视图 等 数据库对象的ddl语句
//	public SqluckyBottomSheet ddlSheet(SqluckyConnector sqluckyConn, String name, String ddl, boolean isRunFunc,
//			boolean isSelect);

//	public SqluckyBottomSheet tableInfoSheet(SqluckyConnector sqluckyConn, TablePo table);

//	public SqluckyBottomSheet ProcedureSheet(SqluckyConnector sqluckyConn, String name, String ddl, boolean isRunFunc);

//	public SqluckyBottomSheet EmptySheet(SqluckyConnector sqluckyConn, String name, String message);

	// 获取 DBconns 中保存的所有数据库链接信息
	public Map<String, SqluckyConnector> getAllConnector();

	// 获取 DBinfoTree 中链接的所有名称
	public List<String> getAllConnectorName();

	// 获取 DBinfoTree 中链接的所有激活的名称
	public List<String> getAllActiveConnectorName();

	// 登入窗口
	public void showSingInWindow(String title);

	/**
	 * 使用新数据重建数据库连接节点树
	 */
	public void recreateDBinfoTreeData(List<DBConnectorInfoPo> dbciPo);

	/**
	 * 使用新数据合并入数据库连接节点树
	 */
	public void mergeDBinfoTreeData(List<DBConnectorInfoPo> dbciPo);

	public void recreateScriptTreeData(List<DocumentPo> docs);

	public void mergeScriptTreeData(List<DocumentPo> docs);

	// 创建一个DocumentPo对象, 并保存在数据库
	public DocumentPo scriptArchive(String title, String txt, String filename, String encode, int paragraph);

	public void updateScriptArchive(Connection conn, DocumentPo po);

	// 给脚本treeView 添加子节点
	public void scriptTreeAddItem(MyEditorSheet sheet);

	// 刷新树
	public void scriptTreeRefresh();
}
