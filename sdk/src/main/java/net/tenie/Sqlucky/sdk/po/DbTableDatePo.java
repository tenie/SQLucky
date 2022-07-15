package net.tenie.Sqlucky.sdk.po;

import java.sql.Types;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*   
 * 查询表的 字段 和 查询数据
 * @author tenie */
public class DbTableDatePo {
	// 字段
	private ObservableList<SheetFieldPo> fields;
	// 数据
	private ObservableList<ObservableList<StringProperty>> allDatas;
	// sql执行时间
	private double execTime = 0;
	// 行数
	private int rows = 0;
	
	public void clean() {
		fields.clear();
		allDatas.forEach(val ->{
			val.clear();
		});
		
		allDatas.clear();
		
		fields = null;
		allDatas = null;
	}
	public DbTableDatePo() {
		fields = FXCollections.observableArrayList();
		allDatas = FXCollections.observableArrayList();
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

		ObservableList<StringProperty> valsErr = FXCollections.observableArrayList();
		valsErr.add(new SimpleStringProperty(errorMessage));
		errdpo.addData(valsErr);
		return errdpo;
	}

	public void addData(ObservableList<StringProperty> data) {
		allDatas.add(data);
	}

	public void addField(SheetFieldPo data) {
		fields.add(data);
	}

	public void addData(String str) {
		ObservableList<StringProperty> val = FXCollections.observableArrayList();
		val.add(new SimpleStringProperty(str));
		allDatas.add(val);
	}

	public void addField(String data) {
		SheetFieldPo po = new SheetFieldPo();
		po.setColumnLabel(data);
		po.setColumnName(data);
		po.setColumnTypeName("String");
		po.setColumnClassName("String");
		po.setColumnType(Types.VARCHAR);
		po.setScale(0);
		po.setColumnDisplaySize(0); 
		fields.add(po);
	}
	public void addField(String data ,Double Width) {
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
	}

	public ObservableList<SheetFieldPo> getFields() {
		return fields;
	}

	public void setFields(ObservableList<SheetFieldPo> fields) {
		this.fields = fields;
	}

	public ObservableList<ObservableList<StringProperty>> getAllDatas() {
		return allDatas;
	}

	public void setAllDatas(ObservableList<ObservableList<StringProperty>> allDatas) {
		this.allDatas = allDatas;
	}

	public int getAllDatasSize() {
		return allDatas.size();
	}

	public double getExecTime() {
		return execTime;
	}

	public void setExecTime(double execTime) {
		this.execTime = execTime;
	}

	public int getRows() {
		if( allDatas != null) {
			this.rows =  allDatas.size();
		}
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
	
}
