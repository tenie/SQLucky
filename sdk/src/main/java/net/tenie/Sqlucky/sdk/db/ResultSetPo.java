package net.tenie.Sqlucky.sdk.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;

/*
 * sql查询结果最终转换为一个数据结构, 包含如下内容:
 *    所有的字段
 *    所有的行数据
 *    新增行的数据
 *    有过修改的行数据
 */
public class ResultSetPo {
	// 所有的字段
	private ObservableList<SheetFieldPo> fields;
	// 所有行的数据
	private ObservableList<ResultSetRowPo> datas;

	// 新增数据
	private ObservableList<ResultSetRowPo> newDatas;
	// 待更新的数据
	private ObservableList<ResultSetRowPo> updateDatas;

	// tab的数据
	private SheetDataValue sheetDataValue;

	public void clean() {
		if (fields != null) {
			fields.clear();
			fields = null;
		}

		if (datas != null) {
			datas.forEach(v -> {
				v.clean();
			});
			datas.clear();
			datas = null;
		}

		if (newDatas != null) {
			newDatas.forEach(v -> {
				v.clean();
			});
			newDatas.clear();
			newDatas = null;
		}

		if (updateDatas != null) {
			updateDatas.forEach(v -> {
				v.clean();
			});
			updateDatas.clear();
			updateDatas = null;
		}
		sheetDataValue = null;

	}

	// 手动添加一行的数据, 在指定下标位置
	public ResultSetRowPo manualAppendNewRow(int idx) {
		ResultSetRowPo appendNewpo = new ResultSetRowPo(this);
		newDatas.add(appendNewpo);
		datas.add(idx, appendNewpo);
		appendNewpo.setIsNewAdd(true);
		return appendNewpo;
	}

//	//后期添加一行行的数据
//	public ResultSetRowPo createAppendNewRow() {
//		ResultSetRowPo appendNewpo = new ResultSetRowPo(this);
//		newDatas.add(appendNewpo);
//		datas.add(appendNewpo);
//		return appendNewpo;
//	}
//	
	// 手动添加一行的数据
	public ResultSetRowPo manualAppendNewRow() {
		ResultSetRowPo appendNewpo = new ResultSetRowPo(this);
		newDatas.add(appendNewpo);
		datas.add(appendNewpo);
		appendNewpo.setIsNewAdd(true);
		return appendNewpo;
	}

	public ResultSetRowPo creatRow() {
		ResultSetRowPo po = new ResultSetRowPo(this);
		datas.add(po);
		return po;
	}

	public ResultSetPo(ObservableList<SheetFieldPo> fds) {
		fields = fds;// FXCollections.observableArrayList();
		datas = FXCollections.observableArrayList();

		newDatas = FXCollections.observableArrayList();
		updateDatas = FXCollections.observableArrayList();
//		this.sheetDataValue = sheetDataValue;
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

	public SheetDataValue getSheetDataValue() {
		return sheetDataValue;
	}

	public void setSheetDataValue(SheetDataValue sheetDataValue) {
		this.sheetDataValue = sheetDataValue;
	}

}
