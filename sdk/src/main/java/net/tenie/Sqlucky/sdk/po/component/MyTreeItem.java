package net.tenie.fx.component.InfoTree.TreeItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;

/*   @author tenie */
public class MyTreeItem<T> extends TreeItem<T> {
	private ObservableList<T> subTreeItems = FXCollections.observableArrayList();
	private SqluckyConnector sqluckyConn;
	public MyTreeItem(T value, SqluckyConnector sconn) {
		super(value);
		this.sqluckyConn = sconn;
	}
	

	public ObservableList<T> getSubTreeItems() {
		return subTreeItems;
	}

	public void setSubTreeItems(ObservableList<T> subTreeItems) {
		this.subTreeItems = subTreeItems;
	}


	public SqluckyConnector getSqluckyConn() {
		return sqluckyConn;
	}


	public void setSqluckyConn(SqluckyConnector sqluckyConn) {
		this.sqluckyConn = sqluckyConn;
	}

}
