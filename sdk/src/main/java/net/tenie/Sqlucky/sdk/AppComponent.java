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
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DBNodeInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.TreeItemType;

public interface AppComponent {
	
	void addTitledPane(TitledPane tp);
	void addIconBySvg(String name, String svg);
	SqluckyTab sqluckyTab();
//	SqluckyTab sqluckyTab(String TabName);
	SqluckyTab sqluckyTab(DocumentPo po);
	void tabPaneRemoveSqluckyTab(SqluckyTab stb);
	
	public Region getIconUnactive(String name);
	public Region getIconDefActive(String name);
	
	public void saveData(String name, String key, String value);
	public String fetchData(String name, String key);
	
	
	//DB DB2Connector 
	public void registerDBConnector(SqluckyDbRegister ctr);
	
	// DB info Node Menu register 注册插件的menu
	public void registerDBInfoMenu(List<Menu> ms, List<MenuItem> mis);
	// 获取选中的 dbInfo 节点的类型(表格, 视图, 等)
	public TreeItemType currentDBInfoNodeType();
	// 获取 dbInfo 上的数据库连接对象
	public DBNodeInfoPo currentDBInfoNode();
	
	// DBInfoMenu 显示的时候调用
	public void setDBInfoMenuOnShowing(Consumer< String >  caller);
	public Consumer< String >  getDBInfoMenuOnShowing();
	
	
	// 执行dml sql语句
	public boolean execDML(String sql);
	// 获取链接/断开链接
//	public void closeConn();
//	public void openConn();
	
    //创建数据tableview
	public  SqluckyBottomSheet sqlDataSheet(SheetDataValue data, int idx, boolean disable);
	public  SqluckyBottomSheet tableViewSheet(SheetDataValue data, List<Node> nodeLs);
	// 表, 视图 等 数据库对象的ddl语句
	public  SqluckyBottomSheet ddlSheet(String name, String ddl, boolean isRunFunc);
	public SqluckyBottomSheet ProcedureSheet(String name, String ddl, boolean isRunFunc);
	public SqluckyBottomSheet EmptySheet(String name, String message);
	
	// 获取 DBconns 中保存的所有数据库链接信息
	public Map<String, SqluckyConnector> getAllConnector();
	
	// 获取 DBinfoTree 中链接的所有名称
	public List<String> getAllConnectorName();
	
	// 登入窗口
	public void showSingInWindow() ;
	/**
	 * 使用新数据重建数据库连接节点树
	 */
	public void recreateDBinfoTreeData(List<DBConnectorInfoPo> dbciPo);
	
}
