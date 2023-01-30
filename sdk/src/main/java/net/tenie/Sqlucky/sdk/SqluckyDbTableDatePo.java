package net.tenie.Sqlucky.sdk;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;

/*   
 * 查询表的 字段 和 查询数据
 * @author tenie */
public interface SqluckyDbTableDatePo {
	public void clean() ;
	public ResultSetRowPo addRow() ;
	public void addData(ResultSetRowPo row, String data, SheetFieldPo field) ;
	public void addData(ResultSetRowPo row, StringProperty data, SheetFieldPo field) ;
	public void addField(SheetFieldPo data) ;
	public SheetFieldPo addField(String data) ;
	public SheetFieldPo addField(String data ,Double Width) ;
	public ObservableList<SheetFieldPo> getFields() ;
	public void setFields(ObservableList<SheetFieldPo> fields) ;
	public double getExecTime() ;
	public void setExecTime(double execTime) ;
	public ResultSetPo getResultSet() ;
	public void setResultSet(ResultSetPo resultSet) ;
	public void setRows(int rows) ;
}
