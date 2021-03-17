package net.tenie.fx.Action;

import java.sql.Connection;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import net.tenie.lib.po.DbConnectionPo;

public class RsVal {
	public String tableId;
	public String sql;
	public String tableName;
	public Connection conn;
	public DbConnectionPo dbconnPo;
	public DbConnectionPo  dbc ;
	public ObservableList<ObservableList<StringProperty>> alldata;
	public Button saveBtn;
	public FilteredTableView<ObservableList<StringProperty>> dataTableView;
}
