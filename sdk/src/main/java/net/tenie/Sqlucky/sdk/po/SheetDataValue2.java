package net.tenie.Sqlucky.sdk.po;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;


/**
 * 一个查询, 对应的一个数据表格, 对应的数据缓存 
 * @author tenie
 *
 */
public class SheetDataValue2 {
	private String tabName;
	private String sqlStr;
	private String connName;
	private boolean isLock = false;
	private SqluckyConnector dbConnection;
	private Connection conn;
	// sql执行时间
	private double execTime = 0;
	// 行数
	private int rows = 0;

	// table id + row num 组成key ,保存对于行的数据
	private Map<String, ObservableList<StringProperty>> newLineDate;
	// table id + row num 组成key ,保存对于行的原始数据
	private Map<String, ObservableList<StringProperty>> oldval ;
	// 待insert的 数据
	private Map<String, ObservableList<StringProperty>> appendData ;

	// 列的右键菜单 menuItem
//	private List<MenuItem> menuItems = new ArrayList<>();

	// 列
	private ObservableList<SheetFieldPo> colss;
	// 数据添加到表格 更简洁的api   , 数据库查询结果的表格原始数据
	private ObservableList<ObservableList<StringProperty>> rawData;
	// tab中的表格
	private FilteredTableView<ObservableList<StringProperty>> dbValTable;
	
	// 一般的信息展示表个数据
	private ObservableList<ResultSetRowPo> infoTableVals;
	private FilteredTableView<ResultSetRowPo> infoTable;

	// 操作数据的按钮
	private List<Node> btnLs ;
	
	
	public void clean() {
//		menuItems.clear();
//		menuItems = null;
		if(dbValTable != null) {
			dbValTable.getItems().clear();
		}
		dbValTable = null;
		
		if(rawData!=null) {
			rawData.forEach(v -> {
				v.clear();
			});
			rawData.clear();
		}		
		rawData = null;
		
		if(colss!=null) {
			colss.clear();
		}
		colss = null;  
		
		if(appendData != null) appendData.clear();
		appendData = null;
		
		if(oldval != null) oldval.clear();
		oldval = null;
		
		if(newLineDate != null) newLineDate.clear();
		newLineDate = null;
		
		if(btnLs != null) btnLs.clear();
		btnLs = null;
	}

 

	public SheetDataValue2(FilteredTableView<ObservableList<StringProperty>> table,  String tabName,
			String sqlStr, String connName, ObservableList<SheetFieldPo> colss,
			ObservableList<ObservableList<StringProperty>> rawData) {
		this.dbValTable = table;
		this.tabName = tabName;
		this.sqlStr = sqlStr;
		this.connName = connName;
		this.colss = colss;
		this.rawData = rawData;
	}

	public SheetDataValue2(FilteredTableView<ObservableList<StringProperty>> table, String tabName,
			ObservableList<SheetFieldPo> colss, ObservableList<ObservableList<StringProperty>> rawData) {
		this.dbValTable = table;
		this.tabName = tabName;
		this.colss = colss;
		this.rawData = rawData;
	}

	public SheetDataValue2() {

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

//	public ObservableList<SqlFieldPo> getTabCol() {
//		return tabCol;
//	}

//	public void setTabCol(ObservableList<SqlFieldPo> tabCol) {
//		this.tabCol = tabCol;
//	}

	public ObservableList<ObservableList<StringProperty>> getRawData() {
		return rawData;
	}

	public void setRawData(ObservableList<ObservableList<StringProperty>> rawData) {
		this.rawData = rawData;
	}

	public FilteredTableView<ObservableList<StringProperty>> getTable() {
		return dbValTable;
	}

	public void setTable(FilteredTableView<ObservableList<StringProperty>> table) {
		this.dbValTable = table;
	}

	public ObservableList<SheetFieldPo> getColss() {
		return colss;
	}

	public void setColss(ObservableList<SheetFieldPo> colss) {
		this.colss = colss;
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public Map<String, ObservableList<StringProperty>> getNewLineDate() {
		if(newLineDate == null) {
			newLineDate = new HashMap<>();
		}
		return newLineDate;
	}

	public void setNewLineDate(Map<String, ObservableList<StringProperty>> newLineDate) {
		this.newLineDate = newLineDate;
	}

	public Map<String, ObservableList<StringProperty>> getOldval() {
		if(oldval == null) {
			oldval = new HashMap<>();
		}
		return oldval;
	}

	public void setOldval(Map<String, ObservableList<StringProperty>> oldval) {
		this.oldval = oldval;
	}

	public Map<String, ObservableList<StringProperty>> getAppendData() {
		if(appendData == null) {
			appendData = new HashMap<>();
		}
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

//	public List<MenuItem> getMenuItems() {
//		return menuItems;
//	}
//
//	public void setMenuItems(List<MenuItem> menuItems) {
//		this.menuItems = menuItems;
//	}

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



	public List<Node> getBtnLs() {
		return btnLs;
	}



	public void setBtnLs(List<Node> btnLs) {
		this.btnLs = btnLs;
	}



	public Connection getConn() {
		return conn;
	}



	public void setConn(Connection conn) {
		this.conn = conn;
	}



	public ObservableList<ResultSetRowPo> getInfoTableVals() {
		return infoTableVals;
	}



	public void setInfoTableVals(ObservableList<ResultSetRowPo> infoTableVals) {
		this.infoTableVals = infoTableVals;
	}



	public FilteredTableView<ResultSetRowPo> getInfoTable() {
		return infoTable;
	}



	public void setInfoTable(FilteredTableView<ResultSetRowPo> table2) {
		this.infoTable = table2;
	}


 
	
	
}
