package net.tenie.lib.po;

/*   @author tenie */
public class SqlFieldPo {

	private String columnName;
	private String columnClassName;
	private int columnDisplaySize;
	private String columnLabel;
	private int columnType;
	private String columnTypeName;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnClassName() {
		return columnClassName;
	}

	public void setColumnClassName(String columnClassName) {
		this.columnClassName = columnClassName;
	}

	public int getColumnDisplaySize() {
		return columnDisplaySize;
	}

	public void setColumnDisplaySize(int columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	public int getColumnType() {
		return columnType;
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}

	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	@Override
	public String toString() {
		return "SqlFieldPo [columnName=" + columnName + ", columnClassName=" + columnClassName + ", columnDisplaySize="
				+ columnDisplaySize + ", columnLabel=" + columnLabel + ", columnType=" + columnType
				+ ", columnTypeName=" + columnTypeName + "]";
	}

}
