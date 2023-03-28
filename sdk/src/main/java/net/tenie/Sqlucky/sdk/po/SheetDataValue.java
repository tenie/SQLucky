package net.tenie.Sqlucky.sdk.po;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;


/**
 * 一个查询, 对应的一个数据表格, 对应的数据缓存 
 * @author tenie
 *
 */
public class SheetDataValue {
	private String tabName;
	private String sqlStr;
	private String connName;
	private boolean isLock = false;
	private SqluckyConnector dbConnection;
	private Connection conn;
	// sql执行时间
	private double execTime = 0;
	// 行数
	private int rowSize = 0;
	
	// 展示的数据集
	private ResultSetPo dataRs;
	// 列
	private ObservableList<SheetFieldPo> colss;
	// 数据添加到表格 更简洁的api   , 数据库查询结果的表格原始数据
	// tableView
	private FilteredTableView<ResultSetRowPo> dbValTable;
	
	// 操作数据的按钮
	private List<Node> btnLs ;
	
	
	public void clean() {
//		menuItems.clear();
//		menuItems = null;
		if(dbValTable != null) {
			dbValTable.getItems().clear();
		}
		dbValTable = null;
		
	 
		
		if(colss!=null) {
			colss.clear();
		}
		colss = null;  
		
		 
		
		if(btnLs != null) btnLs.clear();
		btnLs = null;
	}

 

	public SheetDataValue(FilteredTableView<ResultSetRowPo> table,  String tabName,
			String sqlStr, String connName, ObservableList<SheetFieldPo> colss,
			ResultSetPo  dataRs) {
		this.dbValTable = table;
		this.tabName = tabName;
		this.sqlStr = sqlStr;
		this.connName = connName;
		this.colss = colss;
		this.dataRs = dataRs;
	}

	public SheetDataValue(FilteredTableView<ResultSetRowPo> table, String tabName,
			ObservableList<SheetFieldPo> colss, 
			ResultSetPo  dataRs) {
		this.dbValTable = table;
		this.tabName = tabName;
		this.colss = colss;
		this.dataRs = dataRs;
	}

	public SheetDataValue() {

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



	public FilteredTableView<ResultSetRowPo> getTable() {
		return dbValTable;
	}

	public void setTable(FilteredTableView<ResultSetRowPo> table) {
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



	public double getExecTime() {
		return execTime;
	}

	public void setExecTime(double execTime) {
		this.execTime = execTime;
	}

	public int getRows() {
		return rowSize;
	}

	public void setRows(int rows) {
		this.rowSize = rows;
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



	public ResultSetPo getDataRs() {
		return dataRs;
	}



	public void setDataRs(ResultSetPo dataRs) {
		this.dataRs = dataRs;
	}



	public FilteredTableView<ResultSetRowPo> getDbValTable() {
		return dbValTable;
	}



	public void setDbValTable(FilteredTableView<ResultSetRowPo> dbValTable) {
		this.dbValTable = dbValTable;
	}

}
