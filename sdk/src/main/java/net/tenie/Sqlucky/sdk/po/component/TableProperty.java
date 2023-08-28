package net.tenie.Sqlucky.sdk.po.component;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/*   @author tenie */
public class TableProperty {
	private SimpleLongProperty tableId;
	private SimpleStringProperty tableName;
	private SimpleStringProperty tableRemarks;

	public TableProperty(Long tableId, String tableName, String tableRemarks) {
		this.tableId = new SimpleLongProperty(tableId);
		this.tableName = new SimpleStringProperty(tableName);
		this.tableRemarks = new SimpleStringProperty(tableRemarks);
	}

	public long getTableId() {
		return tableId.get();
	}

	public void setTableId(long id) {
		tableId.set(id);
	}

	public String getTableName() {
		return tableName.get();
	}

	public void setTableName(String tableName) {
		this.tableName.set(tableName);
		;
	}

	public String getTableRemarks() {
		return tableRemarks.get();
	}

	public void setTableRemarks(String tableRemarks) {
		this.tableRemarks.set(tableRemarks);
	}

}
