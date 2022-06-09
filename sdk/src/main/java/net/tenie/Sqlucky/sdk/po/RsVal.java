package net.tenie.Sqlucky.sdk.po;

import java.sql.Connection;

import org.controlsfx.control.tableview2.FilteredTableView;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;

public class RsVal {
	public String tableId;
	public String sql;
	public String tableName;
	public Connection conn;
	public SqluckyConnector dbconnPo;
	public ObservableList<ObservableList<StringProperty>> alldata;
	public FilteredTableView<ObservableList<StringProperty>> dataTableView;
}
