package net.tenie.Sqlucky.sdk.po;

import java.sql.Connection;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;

public class RsVal {
	public String tableId;
	public String sql;
	public String tableName;
	public Connection conn;
	public SqluckyConnector dbconnPo;
	public ObservableList<ResultSetRowPo> alldata;
	public FilteredTableView<ResultSetRowPo> dataTableView;

	// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
	public RsVal(SheetDataValue tableData) {
//		String connName = tableData.getConnName();
		String tableName = tableData.getTabName();
		SqluckyConnector cntor = tableData.getDbConnection();
		Connection conn = tableData.getConn();

		ObservableList<ResultSetRowPo> alldata = tableData.getDataRs().getDatas();

		FilteredTableView<ResultSetRowPo> dataTableView = tableData.getTable();

		this.conn = conn;
		this.dbconnPo = cntor;
		this.tableName = tableName;
		this.alldata = alldata;
		this.dataTableView = dataTableView;
	}

}
