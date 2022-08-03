package net.tenie.Sqlucky.sdk.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;

public class ResultSetPo {
	private ObservableList<SheetFieldPo> fields;
	private ObservableList<ResultSetRowPo> datas;
	
	private ObservableList<ResultSetRowPo> newDatas;
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

	 
	
	
}
