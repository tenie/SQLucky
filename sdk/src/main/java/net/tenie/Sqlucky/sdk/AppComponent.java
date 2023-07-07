package net.tenie.Sqlucky.sdk;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DBNodeInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.po.db.TablePo;

public interface AppComponent {

	void addTitledPane(TitledPane tp);

	void addIconBySvg(String name, String svg);

	// 创建 SqluckyTab
	SqluckyTab sqluckyTab();

	SqluckyTab sqluckyTab(DocumentPo po);

	void tabPaneRemoveSqluckyTab(SqluckyTab stb);

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

	// 创建数据tableview
	public SqluckyBottomSheet sqlDataSheet(SheetDataValue data, int idx, boolean disable);

	public SqluckyBottomSheet tableViewSheet(SheetDataValue data, List<Node> nodeLs);

	// 表, 视图 等 数据库对象的ddl语句
	public SqluckyBottomSheet ddlSheet(SqluckyConnector sqluckyConn, String name, String ddl, boolean isRunFunc,
			boolean isSelect);

	public SqluckyBottomSheet tableInfoSheet(SqluckyConnector sqluckyConn, TablePo table);

	public SqluckyBottomSheet ProcedureSheet(SqluckyConnector sqluckyConn, String name, String ddl, boolean isRunFunc);

	public SqluckyBottomSheet EmptySheet(SqluckyConnector sqluckyConn, String name, String message);

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
}
