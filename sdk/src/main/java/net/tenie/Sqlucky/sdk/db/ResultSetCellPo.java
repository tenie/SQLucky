package net.tenie.Sqlucky.sdk.db;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;

public class ResultSetCellPo {
	private SheetFieldPo field;
	private StringProperty cellData; 
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
		this.cellData = cellData;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	
}
