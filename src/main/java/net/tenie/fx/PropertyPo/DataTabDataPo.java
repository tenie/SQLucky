package net.tenie.fx.PropertyPo;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*   @author tenie */
/**
 * 可以保持多个数据表
 */
public class DataTabDataPo {
	// 表的集合
	List<FilteredTableView<ObservableList<StringProperty>>> newtables = FXCollections.observableArrayList();
	// 表名的集合
	List<String> tableNames = new ArrayList<String>();
// 执行sql 集合
	List<String> sql = new ArrayList<String>();

	public static DataTabDataPo getErrObj(String str) {
		DataTabDataPo po = new DataTabDataPo();
		po.addTableName("Warn!");
		po.addSql(str);
		return po;
	}

	public boolean isEmpty() {
		if (newtables.size() == 0) {
			return true;
		}
		return false;
	}

	public List<String> getSql() {
		return sql;
	}

	public void setSql(List<String> sql) {
		this.sql = sql;
	}

	public List<FilteredTableView<ObservableList<StringProperty>>> getNewtables() {
		return newtables;
	}

	public void setNewtables(List<FilteredTableView<ObservableList<StringProperty>>> newtables) {
		this.newtables = newtables;
	}

	public List<String> getTableNames() {
		return tableNames;
	}

	public void setTableNames(List<String> tableNames) {
		this.tableNames = tableNames;
	}

	public void addTableName(String name) {
		tableNames.add(name);
	}

	public void addSql(String str) {
		sql.add(str);
	}

	public void addAllSqls(List<String> ls) {
		sql.addAll(ls);
	}

	public void addTableView(FilteredTableView<ObservableList<StringProperty>> v) {
		newtables.add(v);
	}

}
