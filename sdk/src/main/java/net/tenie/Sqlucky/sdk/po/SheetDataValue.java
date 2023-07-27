package net.tenie.Sqlucky.sdk.po;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;

/**
 * 一个查询, 对应的一个数据表格, 对应的数据缓存
 * 
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
	// 数据添加到表格 更简洁的api , 数据库查询结果的表格原始数据
	// tableView
	private FilteredTableView<ResultSetRowPo> dbValTable;

	// 操作数据的按钮, 按钮名称和控件对象方式保存
	private Map<String, Button> btnMap;

	JFXButton saveBtn = new JFXButton();
	JFXButton detailBtn = new JFXButton();;
	JFXButton tableSQLBtn = new JFXButton();
	JFXButton refreshBtn = new JFXButton();
	JFXButton addBtn = new JFXButton();
	JFXButton minusBtn = new JFXButton();
	JFXButton copyBtn = new JFXButton();
	JFXButton dockSideBtn = new JFXButton();

	public void clean() {
		if (dbValTable != null) {
			dbValTable.getItems().clear();
		}
		dbValTable = null;

		if (colss != null) {
			colss.clear();
		}
		colss = null;

		if (btnMap != null)
			btnMap.clear();
		btnMap = null;

		if (dataRs != null) {
			dataRs.clean();
			dataRs = null;
		}
		if (dbConnection != null) {
			dbConnection = null;
		}
		if (conn != null) {
			conn = null;
		}

		saveBtn = null;
		detailBtn = null;
		tableSQLBtn = null;
		refreshBtn = null;
		addBtn = null;
		minusBtn = null;
		copyBtn = null;
		dockSideBtn = null;
	}

	public SheetDataValue(FilteredTableView<ResultSetRowPo> table, String tabName, String sqlStr, String connName,
			ObservableList<SheetFieldPo> colss, ResultSetPo dataRs) {
		this.dbValTable = table;
		this.tabName = tabName;
		this.sqlStr = sqlStr;
		this.connName = connName;
		this.colss = colss;
		this.dataRs = dataRs;
		this.dataRs.setSheetDataValue(this);
	}

	public SheetDataValue(FilteredTableView<ResultSetRowPo> table, String tabName, ObservableList<SheetFieldPo> colss,
			ResultSetPo dataRs) {
		this.dbValTable = table;
		this.tabName = tabName;
		this.colss = colss;
		this.dataRs = dataRs;
		this.dataRs.setSheetDataValue(this);
	}

	public SheetDataValue() {

	}

	public Map<String, Button> getBtnMap() {
		return btnMap;
	}

	// 将select sql 执行的结果信息复制给当前对象
	public void setSelectExecInfo(SelectExecInfo execInfo) {
		this.setColss(execInfo.getColss());
		this.setDataRs(execInfo.getDataRs());

		this.setExecTime(execInfo.getExecTime());
		this.setRows(execInfo.getRowSize());
	}

	public void setBtnMap(Map<String, Button> btnMap) {
		this.btnMap = btnMap;
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

	/**
	 * 添加按钮
	 * 
	 * @param btnName
	 * @param btn
	 */
	public void addBtn(String btnName, Button btn) {
		if (btnMap == null) {
			btnMap = new HashMap<String, Button>();
		}
		btnMap.put(btnName, btn);
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

	public int getRowSize() {
		return rowSize;
	}

	public void setRowSize(int rowSize) {
		this.rowSize = rowSize;
	}

	public JFXButton getSaveBtn() {
		return saveBtn;
	}

	public void setSaveBtn(JFXButton saveBtn) {
		this.saveBtn = saveBtn;
	}

	public JFXButton getDetailBtn() {
		return detailBtn;
	}

	public void setDetailBtn(JFXButton detailBtn) {
		this.detailBtn = detailBtn;
	}

	public JFXButton getTableSQLBtn() {
		return tableSQLBtn;
	}

	public void setTableSQLBtn(JFXButton tableSQLBtn) {
		this.tableSQLBtn = tableSQLBtn;
	}

	public JFXButton getRefreshBtn() {
		return refreshBtn;
	}

	public void setRefreshBtn(JFXButton refreshBtn) {
		this.refreshBtn = refreshBtn;
	}

	public JFXButton getAddBtn() {
		return addBtn;
	}

	public void setAddBtn(JFXButton addBtn) {
		this.addBtn = addBtn;
	}

	public JFXButton getMinusBtn() {
		return minusBtn;
	}

	public void setMinusBtn(JFXButton minusBtn) {
		this.minusBtn = minusBtn;
	}

	public JFXButton getCopyBtn() {
		return copyBtn;
	}

	public void setCopyBtn(JFXButton copyBtn) {
		this.copyBtn = copyBtn;
	}

	public JFXButton getDockSideBtn() {
		return dockSideBtn;
	}

	public void setDockSideBtn(JFXButton dockSideBtn) {
		this.dockSideBtn = dockSideBtn;
	}

}
