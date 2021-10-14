package net.tenie.fx.component.dataView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.controlsfx.control.tableview2.FilteredTableView;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;


/**
 * 一个查询, 对应的一个数据表格, 对应的数据缓存 
 * @author tenie
 *
 */
public class MyTabDataValue {
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
	// 待insert的 数据
	private Map<String, ObservableList<StringProperty>> appendData = new HashMap<>();

	// 列的 menuItem
	private List<MenuItem> menuItems = new ArrayList<>();

	// 列
	private ObservableList<SqlFieldPo> colss;
	// 数据添加到表格 更简洁的api
	ObservableList<ObservableList<StringProperty>> rawData;


	// tab中的表格
	private FilteredTableView<ObservableList<StringProperty>> table;

	
	
	public void clean() {
		menuItems.clear();
		menuItems = null;
		table.getItems().clear();
		table = null;
		rawData.forEach(v -> {
			v.clear();
		});
		rawData.clear();
		rawData = null;
		colss.clear();
		colss = null; 
		appendData.clear();
		appendData = null;
		tabCol.clear();
		tabCol = null;
		oldval.clear();
		oldval = null;
		newLineDate.clear();
		newLineDate = null;

	}

 

	public MyTabDataValue(FilteredTableView<ObservableList<StringProperty>> table,  String tabName,
			String sqlStr, String connName, ObservableList<SqlFieldPo> colss,
			ObservableList<ObservableList<StringProperty>> rawData) {
		this.table = table;
		this.tabName = tabName;
		this.sqlStr = sqlStr;
		this.connName = connName;
		this.colss = colss;
		this.rawData = rawData;
	}

	public MyTabDataValue(FilteredTableView<ObservableList<StringProperty>> table, String tabName,
			ObservableList<SqlFieldPo> colss, ObservableList<ObservableList<StringProperty>> rawData) {
		this.table = table;
		this.tabName = tabName;
		this.colss = colss;
		this.rawData = rawData;
	}

	public MyTabDataValue() {

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

	public ObservableList<ObservableList<StringProperty>> getRawData() {
		return rawData;
	}

	public void setRawData(ObservableList<ObservableList<StringProperty>> rawData) {
		this.rawData = rawData;
	}

	public FilteredTableView<ObservableList<StringProperty>> getTable() {
		return table;
	}

	public void setTable(FilteredTableView<ObservableList<StringProperty>> table) {
		this.table = table;
	}

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
}
