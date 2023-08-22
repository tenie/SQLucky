package net.tenie.Sqlucky.sdk.po;

import java.sql.Connection;
import java.util.List;

import org.controlsfx.control.tableview2.FilteredTableView;

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
public class SheetTableData {
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
	// 列
	private ObservableList<SheetFieldPo> colss;
	
	// 一般的信息展示表个数据
	private ResultSetPo infoTableVals; 
	private FilteredTableView<ResultSetRowPo> infoTable;

	// 操作数据的按钮
	private List<Node> btnLs ;
	
	
	public void clean() {
		if(colss!=null) {
			colss.clear();
		}
		colss = null;  
		
		if(infoTable != null) {
			infoTable = null;
		}
		
		if(infoTableVals != null) {
			infoTableVals.clean();
		}
		
		if(btnLs != null) btnLs.clear();
		btnLs = null;
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
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
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


	public FilteredTableView<ResultSetRowPo> getInfoTable() {
		return infoTable;
	}



	public void setInfoTable(FilteredTableView<ResultSetRowPo> table2) {
		this.infoTable = table2;
	}

	public ResultSetPo getInfoTableVals() {
		return infoTableVals;
	}



	public void setInfoTableVals(ResultSetPo infoTableVals) {
		this.infoTableVals = infoTableVals;
	}


 
	
	
}
