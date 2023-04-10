package net.tenie.Sqlucky.sdk.po;

import java.sql.Types;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.SqluckyDbTableDatePo;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;

/*   
 * 查询表的 字段 和 查询数据
 * @author tenie */
public class DbTableDatePo implements SqluckyDbTableDatePo {
	// 字段
	private ObservableList<SheetFieldPo> fields;
 
	private ResultSetPo resultSet ;
	
	// sql执行时间
	private double execTime = 0;
	// 行数
	private int rows = 0;
	
	public void clean() {
		fields.clear();
		resultSet.clean();
		
		fields = null;
		resultSet = null;
	}
	public DbTableDatePo() {
		fields = FXCollections.observableArrayList();
		resultSet = new ResultSetPo(fields);
	}
	
	public static DbTableDatePo setExecuteInfoPo() {
		DbTableDatePo ddlDmlpo = new DbTableDatePo(); 
		ddlDmlpo.addField("Current Time", 140.0);
		ddlDmlpo.addField("Execute SQL Info", 500.0);
		ddlDmlpo.addField("Execute SQL", 550.0);
		return ddlDmlpo;
	}

	// 生成一个错误的对象
	public static DbTableDatePo errObj(String errorMessage) {
		DbTableDatePo errdpo = new DbTableDatePo();

		SheetFieldPo p = new SheetFieldPo();
		p.setColumnLabel(new SimpleStringProperty("Error Message Info"));
		errdpo.addField(p);

		ResultSetRowPo  rowpo = errdpo.getResultSet().creatRow();
		rowpo.addCell(new SimpleStringProperty(errorMessage), null, p);
		return errdpo;
	}
	public ResultSetRowPo addRow() {
		ResultSetRowPo row = resultSet.creatRow();
		return row;
	}
	
	public void addData(ResultSetRowPo row, String data, SheetFieldPo field) {
		row.addCell(data, null, field);
	}
	public void addData(ResultSetRowPo row, StringProperty data, SheetFieldPo field) {
		row.addCell(data, null, field);
	}

	public void addField(SheetFieldPo data) {
		fields.add(data);
	}

	public SheetFieldPo addField(String data) {
		SheetFieldPo po = new SheetFieldPo();
		po.setColumnLabel(data);
		po.setColumnName(data);
		po.setColumnTypeName("String");
		po.setColumnClassName("String");
		po.setColumnType(Types.VARCHAR);
		po.setScale(0);
		po.setColumnDisplaySize(0); 
		fields.add(po);
		return po;
	}
	public SheetFieldPo addField(String data ,Double Width) {
		SheetFieldPo po = new SheetFieldPo();
		po.setColumnLabel(data);
		po.setColumnName(data);
		po.setColumnTypeName("String");
		po.setColumnClassName("String");
		po.setColumnType(Types.VARCHAR);
		po.setScale(0);
		po.setColumnDisplaySize(0); 
		po.setColumnWidth(Width);
		fields.add(po);
		return po;
	}

	public ObservableList<SheetFieldPo> getFields() {
		return fields;
	}

	public void setFields(ObservableList<SheetFieldPo> fields) {
		this.fields = fields;
	}

	public double getExecTime() {
		return execTime;
	}

	public void setExecTime(double execTime) {
		this.execTime = execTime;
	}

	public ResultSetPo getResultSet() {
		return resultSet;
	}
	public void setResultSet(ResultSetPo resultSet) {
		this.resultSet = resultSet;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	
}
