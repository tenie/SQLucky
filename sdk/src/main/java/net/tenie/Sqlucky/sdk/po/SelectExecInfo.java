package net.tenie.Sqlucky.sdk.po;

import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;


/**
 * 执行select sql 时候一些数据
 * @author tenie
 *
 */
public class SelectExecInfo {

	// sql执行时间
	private double execTime = 0;

	// 展示的数据集
	private ResultSetPo dataRs;

	// 列
	private ObservableList<SheetFieldPo> colss;
	
	// 行数
	private int rowSize = 0;

	public double getExecTime() {
		return execTime;
	}

	public void setExecTime(double execTime) {
		this.execTime = execTime;
	}

	public ResultSetPo getDataRs() {
		return dataRs;
	}

	public void setDataRs(ResultSetPo dataRs) {
		this.dataRs = dataRs;
	}

	public ObservableList<SheetFieldPo> getColss() {
		return colss;
	}

	public void setColss(ObservableList<SheetFieldPo> colss) {
		this.colss = colss;
	}

	public int getRowSize() {
		return rowSize;
	}

	public void setRowSize(int rowSize) {
		this.rowSize = rowSize;
	}
	  

  

}
