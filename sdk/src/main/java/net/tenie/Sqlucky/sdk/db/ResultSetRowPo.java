package net.tenie.Sqlucky.sdk.db;

import java.util.Date;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
/**
 * 一行的数据的数据结构
 * 
 * @author tenie
 *
 */
public class ResultSetRowPo {
	// 对整个结果集的应用
	private ResultSetPo resultSet ;
	// 所有单元格
	private ObservableList<ResultSetCellPo> rowDatas; 
	// 是否修改过的标记
	private Boolean hasModify = false;
	// 是后期手动添加的新行
	private Boolean isNewAdd = false;
	
	// 当前行所在的位置(可以理解为下标)
	private int rowIndex = -1;
	
	public void clean() {
		if(resultSet != null ) {
			resultSet.getFields().clear();
			resultSet = null;
		}
		rowDatas.forEach(v->{ v.clean(); });
		hasModify = null;
	}
	
	
	protected ResultSetRowPo(ResultSetPo rs) {
		resultSet = rs;
		rowIndex = resultSet.getDatas().size();
		rowDatas = FXCollections.observableArrayList();
	}
	/**
	 * 给一行数据, 添加一个cell
	 * @param cellData
	 * @param field
	 */
	public void addCell(StringProperty cellData, Object dbValObj, SheetFieldPo field) {
		ResultSetCellPo cell = new ResultSetCellPo(this, cellData, dbValObj, field);
		rowDatas.add(cell);
	}
	public void addCell(String cellstr,  Date cellValByDate, SheetFieldPo field) {
		StringProperty sp = new SimpleStringProperty(cellstr);
		addCell(sp,cellValByDate, field);
	}
	/**
	 * 一行中有多少个cell
	 * @return
	 */
	public int cellSize() {
		if(rowDatas != null) {
			return rowDatas.size();
		}
		return 0;
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
	
	// cell 添加事件, 当单元格被修改做一些处理
	public void cellAddChangeListener(List<Button> btns) {
		// 手动添加 不用给cell添加监听
		if(isNewAdd ) return;
		if( rowDatas != null && rowDatas.size() > 0) {
			for(ResultSetCellPo cell : rowDatas) {
				ResultSetCellPo.addStringPropertyChangeListener(cell , btns);
			}
		}
		
	}
	
	public ResultSetPo getResultSet() {
		return resultSet;
	}


	public void setResultSet(ResultSetPo resultSet) {
		this.resultSet = resultSet;
	}


	public ObservableList<ResultSetCellPo> getRowDatas() {
		return rowDatas;
	}

//	public void setRowDatas(ObservableList<ResultSetCellPo> rowDatas) {
//		this.rowDatas = rowDatas;
//	}
	public int getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	public ObservableList<SheetFieldPo> getFields() {
		return resultSet.getFields();
	}
	 


//	public ObservableList<ResultSetCellPo> getOldCellVal() {
//		return oldCellVal;
//	}
//
//
//	public void setOldCellVal(ObservableList<ResultSetCellPo> oldCellVal) {
//		this.oldCellVal = oldCellVal;
//	}


	public Boolean getHasModify() {
		return hasModify;
	}


	public void setHasModify(Boolean hasModify) {
		if(this.hasModify == false) {
			this.hasModify = hasModify;
			if(hasModify) {
				resultSet.getUpdateDatas().add(this);
			}
		} 
	}


	public Boolean getIsNewAdd() {
		return isNewAdd;
	}


	public void setIsNewAdd(Boolean isNewAdd) {
		this.isNewAdd = isNewAdd;
	}


	@Override
	public String toString() {
		return "ResultSetRowPo [resultSet=" + resultSet + ", rowDatas=" + rowDatas + ", hasModify=" + hasModify
				+ ", isNewAdd=" + isNewAdd + ", rowIndex=" + rowIndex + "]";
	}
	
	
	
}
