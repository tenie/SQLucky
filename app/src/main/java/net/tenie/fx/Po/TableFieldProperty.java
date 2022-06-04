package net.tenie.fx.Po;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/*   @author tenie */
public class TableFieldProperty {

	private SimpleLongProperty tableFieldId;
	private SimpleLongProperty tableId;
	private SimpleStringProperty fieldName;
	private SimpleStringProperty type;
	private SimpleStringProperty isNullable;
	private SimpleStringProperty remarks;

	public TableFieldProperty(Long id, Long tbid, String fieldName, String type, String isNullable, String remarks) {
		this.tableFieldId = new SimpleLongProperty(id);
		this.tableId = new SimpleLongProperty(tbid);
		this.fieldName = new SimpleStringProperty(fieldName);
		this.type = new SimpleStringProperty(type);
		this.isNullable = new SimpleStringProperty(isNullable);
		this.remarks = new SimpleStringProperty(remarks);
	}

	public long getTableFieldId() {
		return tableFieldId.get();
	}

	public void setTableFieldId(Long id) {
		tableFieldId.set(id);
	}

	public long getTableId() {
		return tableId.get();
	}

	public void setTableId(Long id) {
		tableId.set(id);
	}

	public String getFieldName() {
		return fieldName.get();
	}

	public void setFieldName(String fieldName) {
		this.fieldName.set(fieldName);
		;
	}

	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type.set(type);
	}
 
	public String getIsNullable() {
		return isNullable.get();
	}

	public void setIsNullable(String isNullable) {
		this.isNullable.set(isNullable);
	}

	// remarks
	public String getRemarks() {
		return remarks.get();
	}

	public void setRemarks(String remarks) {
		this.remarks.set(remarks);
	}

}
