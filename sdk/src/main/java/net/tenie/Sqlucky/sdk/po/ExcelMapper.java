package net.tenie.Sqlucky.sdk.po;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class ExcelMapper {
	private SimpleStringProperty tableName;
	private SimpleStringProperty fieldName;
	private SimpleStringProperty value;
	private SimpleBooleanProperty useExecl; // new SimpleBooleanProperty(false);
	private SimpleStringProperty excelRow; // new SimpleStringProperty("");

	public SimpleStringProperty getTableName() {
		return tableName;
	}

	public void setTableName(SimpleStringProperty tableName) {
		this.tableName = tableName;
	}

	public SimpleStringProperty getFieldName() {
		return fieldName;
	}

	public void setFieldName(SimpleStringProperty fieldName) {
		this.fieldName = fieldName;
	}

	public SimpleStringProperty getValue() {
		return value;
	}

	public void setValue(SimpleStringProperty value) {
		this.value = value;
	}

	public SimpleBooleanProperty getUseExecl() {
		return useExecl;
	}

	public void setUseExecl(SimpleBooleanProperty useExecl) {
		this.useExecl = useExecl;
	}

	public SimpleStringProperty getExcelRow() {
		return excelRow;
	}

	public void setExcelRow(SimpleStringProperty excelRow) {
		this.excelRow = excelRow;
	}

}
