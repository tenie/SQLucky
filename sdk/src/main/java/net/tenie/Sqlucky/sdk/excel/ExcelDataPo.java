package net.tenie.Sqlucky.sdk.excel;

import java.util.List;

/**
 * excel 导出的时候, 使用的数据结构
 * @author tenie
 *
 */
public class ExcelDataPo {
	private String sheetName = "Sheet1";
	// 行首的列名
	private List<String> headerFields;
	// 数据集
	private List<List<String>> datas;

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public List<String> getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(List<String> headerFields) {
		this.headerFields = headerFields;
	}

	public List<List<String>> getDatas() {
		return datas;
	}

	public void setDatas(List<List<String>> datas) {
		this.datas = datas;
	}

}
