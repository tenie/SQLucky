package net.tenie.lib.po;

import java.util.ArrayList;
import java.util.List;

/*   @author tenie */
public class DbTableDatePo {
	// 字段
	private List<SqlFieldPo> fields = new ArrayList<SqlFieldPo>();
	// 数据
	private List<List<String>> allDatas = new ArrayList<List<String>>();

	public DbTableDatePo() {
	}

	// 生成一个错误的对象
	public static DbTableDatePo errObj(String errorMessage) {
		DbTableDatePo errdpo = new DbTableDatePo();

		SqlFieldPo p = new SqlFieldPo();
		p.setColumnLabel("Error Message Info");
		errdpo.addField(p);

		List<String> valsErr = new ArrayList<String>();
		valsErr.add(errorMessage);
		errdpo.addData(valsErr);
		return errdpo;
	}

	public List<SqlFieldPo> getFields() {
		return fields;
	}

	public void setFields(List<SqlFieldPo> fields) {
		this.fields = fields;
	}

	public List<List<String>> getAllDatas() {
		return allDatas;
	}

	public void setAllDatas(List<List<String>> allDatas) {
		this.allDatas = allDatas;
	}

	public void addData(List<String> data) {
		allDatas.add(data);
	}

	public void addField(SqlFieldPo data) {
		fields.add(data);
	}

	public void addData(String str) {
		List<String> val = new ArrayList<>();
		val.add(str);
		allDatas.add(val);
	}

	public void addField(String data) {
		SqlFieldPo po = new SqlFieldPo();
		po.setColumnLabel(data);
		po.setColumnName(data);
		po.setColumnTypeName("String");
		fields.add(po);
	}

}
