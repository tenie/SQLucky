package net.tenie.Sqlucky.sdk.db;

import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
/**
 * 行的数
 * @author tenie
 *
 */
public class ResultSetRowPo {
	private	ObservableList<SheetFieldPo> fields;
	private ObservableList<ResultSetCellPo> rowDatas;
	private int rowIndex = -1;
	
	public ResultSetRowPo(int idx, ObservableList<ResultSetCellPo> val, ObservableList<SheetFieldPo> fields) {
		rowIndex = idx;
		rowDatas = val;
		this.fields = fields;
	}
	public ObservableList<ResultSetCellPo> getRowDatas() {
		return rowDatas;
	}

	public void setRowDatas(ObservableList<ResultSetCellPo> rowDatas) {
		this.rowDatas = rowDatas;
	}
	public int getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	public ObservableList<SheetFieldPo> getFields() {
		return fields;
	}
	public void setFields(ObservableList<SheetFieldPo> fields) {
		this.fields = fields;
	}
	@Override
	public String toString() {
		return "ResultSetRowPo [fields=" + fields + ", rowDatas=" + rowDatas + ", rowIndex=" + rowIndex + "]";
	}
	
	
}
