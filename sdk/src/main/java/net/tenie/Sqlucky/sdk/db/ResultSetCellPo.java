package net.tenie.Sqlucky.sdk.db;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;

public class ResultSetCellPo {
	private SheetFieldPo field;
	private StringProperty cellData; 
	private StringProperty oldCellData;  // 如果被更新, 旧的值放这个理
	private int index = -1;
	 
	public ResultSetCellPo(int index, StringProperty cellData, SheetFieldPo field) {
		this.index = index;
		this.cellData = cellData;
		this.field = field;
	}
	
	public StringProperty getCellData() {
		return cellData;
	}
	public void setCellData(StringProperty cellData) {
		this.oldCellData  = this.cellData;
		this.cellData = cellData;
		
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	public SheetFieldPo getField() {
		return field;
	}

	public void setField(SheetFieldPo field) {
		this.field = field;
	}

	public StringProperty getOldCellData() {
		return oldCellData;
	}

	public void setOldCellData(StringProperty oldCellData) {
		this.oldCellData = oldCellData;
	}

	@Override
	public String toString() {
		return "ResultSetCellPo [field=" + field + ", cellData=" + cellData + ", oldCellData=" + oldCellData
				+ ", index=" + index + "]";
	}
	
	
}
