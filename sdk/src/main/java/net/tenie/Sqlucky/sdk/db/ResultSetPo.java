package net.tenie.Sqlucky.sdk.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;

public class ResultSetPo {
	private ObservableList<SheetFieldPo> fields;
	private ObservableList<ResultSetRowPo> datas;
	
	// 新增数据
	private ObservableList<ResultSetRowPo> newDatas;
	// 待更新的数据
	private ObservableList<ResultSetRowPo> updateDatas;
	
	public void clean() {
		if(fields != null) {
			fields.clear();
			fields = null;
		}
	
		 
		if(datas != null) {
			datas.forEach(v->{ v.clean(); });
			datas.clear();
		}
		
		if(newDatas != null) {
			newDatas.forEach(v->{ v.clean(); });
			newDatas.clear();
		}
		
		if(updateDatas != null) {
			updateDatas.forEach(v->{ v.clean(); });
			updateDatas.clear();
		}
		
		
		 
	}
	public ResultSetPo() {
		fields = FXCollections.observableArrayList();
		datas =  FXCollections.observableArrayList();

		newDatas =  FXCollections.observableArrayList();
	    updateDatas =  FXCollections.observableArrayList();
		
	}

	public ObservableList<SheetFieldPo> getFields() {
		return fields;
	}

	public void setFields(ObservableList<SheetFieldPo> fields) {
		this.fields = fields;
	}

	public ObservableList<ResultSetRowPo> getDatas() {
		return datas;
	}

	public void setDatas(ObservableList<ResultSetRowPo> datas) {
		this.datas = datas;
	}
	
	public void addRow(ResultSetRowPo rowpo) {
		rowpo.setResultSet(this);
		datas.add(rowpo);
	}

	public int size() {
		return datas.size();
	}
	public ObservableList<ResultSetRowPo> getNewDatas() {
		return newDatas;
	}
	public void setNewDatas(ObservableList<ResultSetRowPo> newDatas) {
		this.newDatas = newDatas;
	}
	public ObservableList<ResultSetRowPo> getUpdateDatas() {
		return updateDatas;
	}
	public void setUpdateDatas(ObservableList<ResultSetRowPo> updateDatas) {
		this.updateDatas = updateDatas;
	}

	 
	
	
}
