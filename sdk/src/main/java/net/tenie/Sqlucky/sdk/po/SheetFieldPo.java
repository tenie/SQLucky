package net.tenie.Sqlucky.sdk.po;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 数据库表的字段数据结构
 * 
 * @author tenie
 *
 */
public class SheetFieldPo {

	public static final String TEXT_TYPE = "TEXT_TYPE";
	public static final String CHECK_BOX_TYPE = "CHECK_BOX_TYPE";

	// 字段名称, 这个可能会没有
	private StringProperty columnName;
	// 这个也是字段名称
	private StringProperty columnLabel;

	private StringProperty columnClassName;
	private IntegerProperty columnDisplaySize;
	private IntegerProperty columnType;
	private StringProperty columnTypeName;
	private IntegerProperty scale;
	private StringProperty value;
	private Double columnWidth;
	private LongProperty dateValue; // 如果是时间, 保存为long

	// excel数据导入表需要使用下面2个字段
	private StringProperty excelRowVal = new SimpleStringProperty(""); // excel 对应列
	private StringProperty fixedValue = new SimpleStringProperty("");; // 不使用excel对应的列, 使用固定值

	// 类型, 在界面上显示的时候, 默认文本类型
	private String Type = TEXT_TYPE;

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

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
		if (columnName == null) {
			return columnLabel;
		}
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
		if (columnLabel == null) {
			return columnName;
		}
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

	public LongProperty getDateValue() {
		return dateValue;
	}

	public void setDateValue(LongProperty dateValue) {
		this.dateValue = dateValue;
	}

	public StringProperty getExcelRowVal() {
		return excelRowVal;
	}

	public void setExcelRowVal(StringProperty excelRowVal) {
		this.excelRowVal = excelRowVal;
	}

	public StringProperty getFixedValue() {
		return fixedValue;
	}

	public void setFixedValue(StringProperty fixedValue) {
		this.fixedValue = fixedValue;
	}

	@Override
	public String toString() {
		return "SheetFieldPo [columnName=" + columnName + ", columnLabel=" + columnLabel + ", columnClassName="
				+ columnClassName + ", columnDisplaySize=" + columnDisplaySize + ", columnType=" + columnType
				+ ", columnTypeName=" + columnTypeName + ", scale=" + scale + ", value=" + value + ", columnWidth="
				+ columnWidth + ", dateValue=" + dateValue + ", Type=" + Type + "]";
	}

}
