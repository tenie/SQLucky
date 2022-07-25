package net.tenie.Sqlucky.sdk.db;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
	
	
	// 根据字段名称找到对应的值
	public String getValueByFieldName(String fieldName) {
		if(rowDatas != null && rowDatas.size()>0) {
			for(ResultSetCellPo cellpo : rowDatas) {
				StringProperty tmpName = cellpo.getField().getColumnName();
				String strName = tmpName.get();
				if(fieldName.equals(strName)) {
					return cellpo.getCellData().get();
				}
			}
		}
		
		return null;
	}
	
	public void setValueByFieldName(String fieldName, String val) {
		if(rowDatas != null && rowDatas.size()>0) {
			for(ResultSetCellPo cellpo : rowDatas) {
				StringProperty tmpName = cellpo.getField().getColumnName();
				String strName = tmpName.get();
				if(fieldName.equals(strName)) { 
					cellpo.setCellData(new SimpleStringProperty(val));
				}
			}
		}
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
	
	
}
