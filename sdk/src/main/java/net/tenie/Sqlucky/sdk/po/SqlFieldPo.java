package net.tenie.Sqlucky.sdk.po;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/*   @author tenie */
public class SqlFieldPo {

	private StringProperty columnName;
	private StringProperty columnClassName;
	private IntegerProperty columnDisplaySize;
	private StringProperty columnLabel;
	private IntegerProperty columnType;
	private StringProperty columnTypeName;
	private IntegerProperty scale;

	private StringProperty value;
	
	
	private Double columnWidth;

	public IntegerProperty getScale() {
		return scale;
	}

	public void setScale(IntegerProperty scale) {
		this.scale = scale;
	}

	public void setScale(Integer scale) {
		this.scale = new SimpleIntegerProperty(scale);
	}

	public StringProperty getValue() {
		return value;
	}

	public void setValue(StringProperty value) {
		this.value = value;
	}

	public StringProperty getColumnName() {
		return columnName;
	}

	public void setColumnName(StringProperty columnName) {
		this.columnName = columnName;
	}

	public void setColumnName(String columnName) {
		if (StrUtils.isNotNullOrEmpty(columnName))
			this.columnName = new SimpleStringProperty(columnName);
	}

	public StringProperty getColumnClassName() {
		return columnClassName;
	}

	public void setColumnClassName(StringProperty columnClassName) {
		this.columnClassName = columnClassName;
	}

	public void setColumnClassName(String columnClassName) {
		if (StrUtils.isNotNullOrEmpty(columnClassName))
			this.columnClassName = new SimpleStringProperty(columnClassName);
	}

	public IntegerProperty getColumnDisplaySize() {
		return columnDisplaySize;
	}

	public void setColumnDisplaySize(IntegerProperty columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
	}

	public void setColumnDisplaySize(Integer columnDisplaySize) {
		if (columnDisplaySize != null)
			this.columnDisplaySize = new SimpleIntegerProperty(columnDisplaySize);
	}

	public StringProperty getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(StringProperty columnLabel) {
		this.columnLabel = columnLabel;
	}

	public void setColumnLabel(String columnLabel) {
		if (StrUtils.isNotNullOrEmpty(columnLabel))
			this.columnLabel = new SimpleStringProperty(columnLabel);
	}

	public IntegerProperty getColumnType() {
		return columnType;
	}

	public void setColumnType(IntegerProperty columnType) {
		this.columnType = columnType;
	}

	public void setColumnType(Integer columnType) {
		if (columnType != null)
			this.columnType = new SimpleIntegerProperty(columnType);
	}

	public StringProperty getColumnTypeName() {
		return columnTypeName;
	}

	public void setColumnTypeName(StringProperty columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	public void setColumnTypeName(String columnTypeName) {
		if (StrUtils.isNotNullOrEmpty(columnTypeName))
			this.columnTypeName = new SimpleStringProperty(columnTypeName);
	}

	public Double getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(Double columnWidth) {
		this.columnWidth = columnWidth;
	}

 
}
