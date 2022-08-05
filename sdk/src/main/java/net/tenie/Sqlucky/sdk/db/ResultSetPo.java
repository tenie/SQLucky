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
	
	/**
	 * 首次查询数据后, 后期添加一行行的数据
	 */
	public ResultSetRowPo createAppendNewRow() {
		ResultSetRowPo appendNewpo = new ResultSetRowPo(this);
		newDatas.add(appendNewpo);
		datas.add(appendNewpo);
		return appendNewpo;
	}
	
	public ResultSetRowPo creatRow() {
		ResultSetRowPo po = new ResultSetRowPo(this);
		datas.add(po);
		return po;
	}
	
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
	public ResultSetPo(ObservableList<SheetFieldPo> fds) {
		fields = fds;// FXCollections.observableArrayList();
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
