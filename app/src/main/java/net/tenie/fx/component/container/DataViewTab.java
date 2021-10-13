package net.tenie.fx.component.container;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.tableview2.FilteredTableView;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.RsVal;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.Cache.CacheTabView;
import net.tenie.fx.component.AllButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.CodeArea.HighLightingCodeArea;
import net.tenie.fx.component.CodeArea.MyCodeArea;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.window.ModalDialog;
import net.tenie.fx.window.ProcedureExecuteWindow;
import net.tenie.lib.tools.IconGenerator;


/**
 * 一个查询, 对应的一个数据表格, 对应的数据缓存 
 * @author tenie
 *
 */
public class DataViewTab {

	private String tabId;
	private String tabName;
	private String sqlStr;
	private String connName;
	private boolean isLock = false;
	private SqluckyConnector dbConnection;
	// sql执行时间
	private double execTime = 0;
	// 行数
	private int rows = 0;

	// table id + row num 组成key ,保存对于行的数据
	private Map<String, ObservableList<StringProperty>> newLineDate = new HashMap<>();
	// table id + row num 组成key ,保存对于行的原始数据
	private Map<String, ObservableList<StringProperty>> oldval = new HashMap<>();
	// 表字段的信息
	private ObservableList<SqlFieldPo> tabCol = FXCollections.observableArrayList();
	// 表格数据
//	private ObservableList<ObservableList<StringProperty>> tabData = FXCollections.observableArrayList();
	// 待insert的 数据
	private Map<String, ObservableList<StringProperty>> appendData = new HashMap<>();

	// 操作按钮
	private List<ButtonBase> btns = new ArrayList<>();
	// 列的 menuItem
	private List<MenuItem> menuItems = new ArrayList<>();

	// 数据连接对象
	private Connection dbconns;
	// 列
	private ObservableList<SqlFieldPo> colss;
	// 数据添加到表格 更简洁的api
	ObservableList<ObservableList<StringProperty>> rawData;

	// VBox
	private VBox dataPane;
	// 按钮
	private AnchorPane fp;
	// tab
	private Tab tab;
	// tab中的表格
	private FilteredTableView<ObservableList<StringProperty>> table;

	
	
	public void clean() {
		menuItems.clear();
		menuItems = null;
		fp.getChildren().clear();
		dataPane.getChildren().clear();
		dataPane = null;
		fp = null;
		tab.setContent(null);
		tab = null;
		table.getItems().clear();
		table = null;
		rawData.forEach(v -> {
			v.clear();
		});
		rawData.clear();
		rawData = null;
		colss.clear();
		colss = null;
		dbconns = null;
		appendData.clear();
		appendData = null;
//		tabData.forEach(v -> {
//			v.clear();
//		});
//		tabData.clear();
//		tabData = null;
		tabCol.clear();
		tabCol = null;
		oldval.clear();
		oldval = null;
		newLineDate.clear();
		newLineDate = null;
		btns.clear();
		btns = null;

	}

	//
//	private DataTabDataPo tdpo ; 

	public AnchorPane getFp() {
		return fp;
	}

	public void setFp(AnchorPane fp) {
		this.fp = fp;
	}

	public DataViewTab(FilteredTableView<ObservableList<StringProperty>> table, String tabId, String tabName,
			String sqlStr, Connection dbconns, String connName, ObservableList<SqlFieldPo> colss,
			ObservableList<ObservableList<StringProperty>> rawData) {
		this.table = table;
		this.tabId = tabId;
		this.tabName = tabName;
		this.sqlStr = sqlStr;
		this.dbconns = dbconns;
		this.connName = connName;
		this.colss = colss;
		this.rawData = rawData;
	}

	public DataViewTab(FilteredTableView<ObservableList<StringProperty>> table, String tabId, String tabName,
			ObservableList<SqlFieldPo> colss, ObservableList<ObservableList<StringProperty>> rawData) {
		this.table = table;
		this.tabId = tabId;
		this.tabName = tabName;
		this.colss = colss;
		this.rawData = rawData;
	}

	public DataViewTab() {

	}
 

	

	
	public String getTabId() {
		return tabId;
	}

	public void setTabId(String tabId) {
		this.tabId = tabId;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getSqlStr() {
		return sqlStr;
	}

	public void setSqlStr(String sqlStr) {
		this.sqlStr = sqlStr;
	}

	public ObservableList<SqlFieldPo> getTabCol() {
		return tabCol;
	}

	public void setTabCol(ObservableList<SqlFieldPo> tabCol) {
		this.tabCol = tabCol;
	}

//	public ObservableList<ObservableList<StringProperty>> getTabData() {
//		return tabData;
//	}
//
//	public void setTabData(ObservableList<ObservableList<StringProperty>> tabData) {
//		this.tabData = tabData;
//	}

	public ObservableList<ObservableList<StringProperty>> getRawData() {
		return rawData;
	}

	public void setRawData(ObservableList<ObservableList<StringProperty>> rawData) {
		this.rawData = rawData;
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tb) {
		this.tab = tb;
	}

	public FilteredTableView<ObservableList<StringProperty>> getTable() {
		return table;
	}

	public void setTable(FilteredTableView<ObservableList<StringProperty>> table) {
		this.table = table;
	}
//	public DataTabDataPo getTdpo() {
//		return tdpo;
//	}
//	public void setTdpo(DataTabDataPo tdpo) {
//		this.tdpo = tdpo;
//	}

	public ObservableList<SqlFieldPo> getColss() {
		return colss;
	}

	public void setColss(ObservableList<SqlFieldPo> colss) {
		this.colss = colss;
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public Map<String, ObservableList<StringProperty>> getNewLineDate() {
		return newLineDate;
	}

	public void setNewLineDate(Map<String, ObservableList<StringProperty>> newLineDate) {
		this.newLineDate = newLineDate;
	}

	public Map<String, ObservableList<StringProperty>> getOldval() {
		return oldval;
	}

	public void setOldval(Map<String, ObservableList<StringProperty>> oldval) {
		this.oldval = oldval;
	}

	public Map<String, ObservableList<StringProperty>> getAppendData() {
		return appendData;
	}

	public void setAppendData(Map<String, ObservableList<StringProperty>> appendData) {
		this.appendData = appendData;
	}

	public Connection getDbconns() {
		return dbconns;
	}

	public void setDbconns(Connection dbconns) {
		this.dbconns = dbconns;
	}

	public VBox getDataPane() {
		return dataPane;
	}

	public void setDataPane(VBox dataPane) {
		this.dataPane = dataPane;
	}

	public double getExecTime() {
		return execTime;
	}

	public void setExecTime(double execTime) {
		this.execTime = execTime;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public List<ButtonBase> getBtns() {
		return btns;
	}

	public void setBtns(List<ButtonBase> btns) {
		this.btns = btns;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public boolean isLock() {
		return isLock;
	}

	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}

	public SqluckyConnector getDbConnection() {
		return dbConnection;
	}

	public void setDbConnection(SqluckyConnector dbConnection) {
		this.dbConnection = dbConnection;
	}
	
	
	
	
	// 获取数据页的id
//	public static String currentDataTabID() {
//		Tab tab = currentDataTab();
//		String id = tab.getId();
//		
//		return id;
//	}
	
	
	
	public static RsVal tableInfo(String tableName, String connName, Connection conn ) {

		SqluckyConnector  dbc = DBConns.get(connName);  
		RsVal rv = new RsVal();
		rv.conn = conn; 
		rv.dbconnPo = dbc;   
		rv.tableName = tableName; 
		rv.alldata = null;
//		rv.saveBtn = null;
		rv.dataTableView = null;
		return rv;
	}
	
//  获取当前数据表的Tab 中的 vbox
	public static VBox currentDataVbox() {
		Tab tab = ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
		VBox vb = (VBox) tab.getContent();
		return vb;
	}
	

	
	
	// 获取当前的表格
	@SuppressWarnings("unchecked")
	public static FilteredTableView<ObservableList<StringProperty>> dataTableView() {
		VBox vb = (VBox) ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem().getContent();
		FilteredTableView<ObservableList<StringProperty>> table = (FilteredTableView<ObservableList<StringProperty>>) vb
				.getChildren().get(1);
		return table;
	}

	// 获取当前表格选择的数据
	public static ObservableList<ObservableList<StringProperty>> dataTableViewSelectedItems() {
		ObservableList<ObservableList<StringProperty>> vals = dataTableView().getSelectionModel().getSelectedItems();
		return vals;
	}

	// 获取当前表格id
	public static String dataTableViewID() {
		return dataTableView().getId();
	}

	
	
	// 获取所有按钮
//	public static List<ButtonBase>  dataPaneBtns() {
//	 
//		List<ButtonBase> ls = new ArrayList<>();
//		for( String key :CacheTabView.getKey()) {
//			ls.addAll(CacheTabView.optionBtns(key) );
//		} 
//		return ls;
//	}
	// 获取数据面板中所有右键按钮
//	public static List<MenuItem>  dataPaneMenuItems() {
//		 
//		List<MenuItem> ls = new ArrayList<>();
//		for( String key :CacheTabView.getKey()) {
//			ls.addAll(CacheTabView.MenuItems(key) );
//		} 
//		return ls;
//	}
}
