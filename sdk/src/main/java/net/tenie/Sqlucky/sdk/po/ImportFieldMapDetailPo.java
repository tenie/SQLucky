package net.tenie.Sqlucky.sdk.po;

import java.util.Date;

/**
 * excel到处到表的字段数据结构
 * 
 * @author tenie
 *
 */
public class ImportFieldMapDetailPo {
	private Long tableId;
	private String tableFiledName;
	private Integer excelFiledIdx;
	private String fixedValue;

	private Date createdTime;
	private Date updatedTime;

	public ImportFieldMapDetailPo() {
	}

	public ImportFieldMapDetailPo(ImportFieldPo fieldpo, Long tableId) {
		this.tableFiledName = fieldpo.getColumnLabel().get();
		this.excelFiledIdx = fieldpo.getFieldIdx();
		this.fixedValue = fieldpo.getFixedValue().get();
		this.tableId = tableId;
	}

	public Integer getExcelFiledIdx() {
		return excelFiledIdx;
	}

	public void setExcelFiledIdx(Integer excelFiledIdx) {
		this.excelFiledIdx = excelFiledIdx;
	}

	public String getFixedValue() {
		return fixedValue;
	}

	public void setFixedValue(String fixedValue) {
		this.fixedValue = fixedValue;
	}

	public String getTableFiledName() {
		return tableFiledName;
	}

	public void setTableFiledName(String tableFiledName) {
		this.tableFiledName = tableFiledName;
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

}
