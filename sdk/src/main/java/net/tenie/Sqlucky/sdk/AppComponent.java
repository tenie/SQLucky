package net.tenie.Sqlucky.sdk;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.BottomSheetDataValue;
import net.tenie.Sqlucky.sdk.po.DocumentPo;

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
	
	
	// 执行dml sql语句
	public boolean execDML(String sql);
	// 获取链接/断开链接
//	public void closeConn();
//	public void openConn();
	
    //创建数据tableview
	public  SqluckyBottomSheet sqlDataSheet(BottomSheetDataValue data, int idx, boolean disable);
	public  SqluckyBottomSheet tableViewSheet(BottomSheetDataValue data, List<Node> nodeLs);
	// 表, 视图 等 数据库对象的ddl语句
	public  SqluckyBottomSheet ddlSheet(String name, String ddl, boolean isRunFunc);
	public SqluckyBottomSheet ProcedureSheet(String name, String ddl, boolean isRunFunc);
	public SqluckyBottomSheet EmptySheet(String name, String message);
}
