package net.tenie.Sqlucky.sdk.po;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.tenie.Sqlucky.sdk.db.Dbinfo;
import net.tenie.Sqlucky.sdk.po.db.TableFieldPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.sql.SqlParser;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;

import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;

/**
 * 一个查询, 对应的一个数据表格, 对应的数据缓存
 * 
 * @author tenie
 *
 */
public class SheetDataValue {
	private String tabName;
	private String sqlStr; // 执行是的sql

	// sql语句中 表的信息对象
	private List<TablePo> sqlTableInfoList;
//	private String connName;
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
//	private Map<String, Button> btnMap;

	private JFXButton saveBtn = new JFXButton();
	private JFXButton lockBtn = new JFXButton();
	private JFXButton hideBottom = new JFXButton();
//	JFXButton detailBtn = new JFXButton();;
//	JFXButton tableSQLBtn = new JFXButton();
//	JFXButton refreshBtn = new JFXButton();
//	JFXButton addBtn = new JFXButton();
//	JFXButton minusBtn = new JFXButton();
//	JFXButton copyBtn = new JFXButton();
//	JFXButton dockSideBtn = new JFXButton();

	public void clean() {
		if (dbValTable != null) {
			dbValTable.getItems().clear();
		}
		dbValTable = null;

		if (colss != null) {
			colss.clear();
		}
		colss = null;

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
		lockBtn = null;
		hideBottom = null;
//		detailBtn = null;
//		tableSQLBtn = null;
//		refreshBtn = null;
//		addBtn = null;
//		minusBtn = null;
//		copyBtn = null;
//		dockSideBtn = null;
	}

//	public SheetDataValue(FilteredTableView<ResultSetRowPo> table, String tabName, String sqlStr, String connName,
//			ObservableList<SheetFieldPo> colss, ResultSetPo dataRs) {
//		this.dbValTable = table;
//		this.tabName = tabName;
//		this.sqlStr = sqlStr;
//		this.connName = connName;
//		this.colss = colss;
//		this.dataRs = dataRs;
//		this.dataRs.setSheetDataValue(this);
//	}

//	public SheetDataValue() {
//
//	}

	public void setSheetDataValue(FilteredTableView<ResultSetRowPo> table, String tabName,
			ObservableList<SheetFieldPo> colss, ResultSetPo dataRs) {
		this.dbValTable = table;
		this.tabName = tabName;
		this.colss = colss;
		this.dataRs = dataRs;
		this.dataRs.setSheetDataValue(this);
	}

	// 将select sql 执行的结果信息复制给当前对象
	public void setSelectExecInfo(SelectExecInfo execInfo) {
		this.setColss(execInfo.getColss());
		this.setDataRs(execInfo.getDataRs());

		this.setExecTime(execInfo.getExecTime());
		this.setRows(execInfo.getRowSize());
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
		return dbConnection.getConnName();
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
		this.dataRs.setSheetDataValue(this);
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

	public JFXButton getLockBtn() {
		initLockBtn();
		return lockBtn;
	}

	public JFXButton getHideBottom() {
		if (hideBottom.getGraphic() == null) {
			hideBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-down"));
			hideBottom.setOnMouseClicked(e -> {
				SdkComponent.hideBottom();
			});
		}

		return hideBottom;
	}

	/**
	 * 锁btn
	 *
	 * @return
	 */
	private void initLockBtn() {
		if (lockBtn.getGraphic() == null) {
			// 锁
			if (this.isLock()) {
				lockBtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
			} else {
				lockBtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));
			}
			lockBtn.setOnMouseClicked(e -> {
				if (this.isLock()) {
					lockBtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));
					this.setLock(false);
				} else {
					lockBtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
					this.setLock(true);
				}

			});
		}

	}


	// 获取sql中所有表的 表信息对象
	public List<TablePo> getSqlTableInfoList() {
		if(sqlTableInfoList != null ){
			return  sqlTableInfoList;
		}
		sqlTableInfoList = new ArrayList<>();
		// 找到sql中的表
		Set<String> names = SqlParser.selectSqlTableNames(this.sqlStr);
		if(! names.isEmpty()){
			for(String tabName : names){
				TablePo tbpo = new TablePo();
				if(tabName.contains(".")){
					String tmpArr[] = tabName.split("\\.");
					tbpo.setTableSchema(tmpArr[0]);
					tbpo.setTableName(tmpArr[1]);
				}
				String defaultSchema = this.dbConnection.getDefaultSchema();
				if(StrUtils.isNotNullOrEmpty(defaultSchema)){
					tbpo.setTableSchema(defaultSchema);
					tbpo.setTableName(tabName);
				}
                try {
                    Dbinfo.fetchTableInfo( this.dbConnection.getConn(), tbpo);
					sqlTableInfoList.add(tbpo);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
		}

		return sqlTableInfoList;
	}

	/**
	 *  使用主表表名创建一个新的TablePo实例
	 * @return
	 */
	private TablePo createEmptyTablePo(){
		TablePo tbpo = new TablePo();
		if(tabName.contains(".")){
			String tmpArr[] = tabName.split("\\.");
			tbpo.setTableSchema(tmpArr[0]);
			tbpo.setTableName(tmpArr[1]);
		}
		String defaultSchema = this.dbConnection.getDefaultSchema();
		if(StrUtils.isNotNullOrEmpty(defaultSchema)){
			tbpo.setTableSchema(defaultSchema);
			tbpo.setTableName(tabName);
		}
		return tbpo;
	}
	// 获取主表的详细信息
	public TablePo getSqlTableInfo() {
		TablePo tablePo = createEmptyTablePo();
        try {
			// 给tablePo 赋值
            Dbinfo.fetchTableInfo( this.dbConnection.getConn(), tablePo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tablePo;
	}

	public List<SheetFieldPo> getMainTableFields() {
		List<SheetFieldPo> ls = new ArrayList<>();
//
		TablePo tablePo = getSqlTableInfo();
		LinkedHashSet<TableFieldPo> fieldList = tablePo.getFields();
		for (var tfp  : fieldList){
			var javatype = tfp.getDataType();
			var fieldName = tfp.getColumnName();
			var remarks = tfp.getRemarks();
			SheetFieldPo sfpo = new SheetFieldPo();
			//TODO
			sfpo.setDbinfoRemark(remarks);
			// 字段名称
			sfpo.setDbinfoFieldName(fieldName);
			// 数据库的类型转换为java的类型(字符串)
			sfpo.setColumnType(javatype);
			sfpo.setScale(tfp.getScale());
			String typeStr = CommonUtils.dbTypeToJavaType(sfpo);
			sfpo.setJavaType(typeStr);
			if("Date".equals(typeStr)){
				sfpo.setDbinfoIsDateType(true);
			}
			ls.add(sfpo);
		}
		return  ls;
	}

	/**
	 * where 后面的字段转换为 fieldpo对象
	 * @param fields
	 * @return
	 */
	public List<SheetFieldPo>  whereFieldInfo(List<String> fields){
		List<SheetFieldPo> rs = new ArrayList<>();
		LinkedHashSet<TableFieldPo> allFields = allTableAllFields();
		for(String tmp : fields){
			for (TableFieldPo  tfp: allFields){
				var fn = tfp.getFieldName();
				if(fn.equals(tmp)){
					SheetFieldPo sfpo = new SheetFieldPo();
					String remarkStr = tfp.getRemarks();
					int javatype = tfp.getDataType();
					// 备注
					sfpo.setDbinfoRemark(remarkStr);
					// 字段名称
					sfpo.setDbinfoFieldName(tmp);
					// 字符是不是时间类型
					boolean isDateType = CommonUtils.isDateAndDateTime(javatype);
					sfpo.setDbinfoIsDateType(isDateType);
					rs.add(sfpo);
					break;
				}
			}
		}

		return rs;

	}

	LinkedHashSet<TableFieldPo> allFields = null;
	/**
	 * 所有表的所有字段
	 * @return
	 */
	private LinkedHashSet<TableFieldPo> allTableAllFields(){
		if(allFields != null ){
			return  allFields;
		}
		allFields = new LinkedHashSet<>();
		List<TablePo> allTabs = getSqlTableInfoList();
		for(TablePo tp : allTabs){
			LinkedHashSet<TableFieldPo> tmpSet = 	tp.getFields();
			allFields.addAll(tmpSet);
		}
		return allFields;
	}

	private boolean fetchTableFieldInfo = false;
	// 给列的 备注 赋值(数据库中的备注)
	public List<SheetFieldPo> getColssInfos(){
		List<SheetFieldPo>  fls  = this.getColss();
		if(fetchTableFieldInfo){
			return fls;
		}

		List<TablePo> ls = getSqlTableInfoList();
		// 所有表的字段集中起来
		LinkedHashSet<TableFieldPo> allFields = allTableAllFields();


		for(var sfpo: fls){
			String colName = sfpo.getColumnLabel().get();
			for( var tfp : allFields){
				var fn = tfp.getFieldName();
				if(fn.equals(colName)){
					int javatype = tfp.getDataType();
					String remarkStr = tfp.getRemarks();
					// 备注
					sfpo.setDbinfoRemark(remarkStr);
					// 字段名称
					sfpo.setDbinfoFieldName(colName);
					// 字符是不是时间类型
					boolean isDateType = CommonUtils.isDateAndDateTime(javatype);
					sfpo.setDbinfoIsDateType(isDateType);
					break;
				}
			}
		}

		fetchTableFieldInfo = true;
		return fls;
	}

}
