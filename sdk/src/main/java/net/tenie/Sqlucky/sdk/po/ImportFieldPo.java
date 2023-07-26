package net.tenie.Sqlucky.sdk.po;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * excel到处到表的字段数据结构
 * 
 * @author tenie
 *
 */
public class ImportFieldPo extends SheetFieldPo {

	// excel数据导入表需要使用下面2个字段
	private StringProperty excelRowVal = new SimpleStringProperty(""); // excel 对应列
	private StringProperty fixedValue = new SimpleStringProperty("");; // 不使用excel对应的列, 使用固定值

	private Integer rowIdx = null; // 对应列的下标, 默认null , 如果excelRowVal的值为空就赋值为-1

	// 可选的值
	private List<String> excelRowInfo;

	public ImportFieldPo(SheetFieldPo sheetField) {
		try {
			BeanUtils.copyProperties(this, sheetField);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		excelRowValListener();
	}

	public void excelRowValListener() {
		excelRowVal.addListener((obj, valOld, valNew) -> {
			if (StrUtils.isNotNullOrEmpty(valNew)) {
				// 如果是手动修改了下拉选的值
				if (excelRowInfo != null) {
					if (excelRowInfo.contains(valNew)) {
//						String[] excelInfo = valNew.split(" - ");
//						excelRowIdx.setValue(excelInfo[0]);
					} else {
//						excelRowIdx.setValue("");
						excelRowVal.set(valOld);

					}
				}

			}
//			else {
//				excelRowIdx.setValue("");
//			}
		});
	}

	public StringProperty getExcelRowVal() {
		return excelRowVal;
	}

//	public void setExcelRowVal(StringProperty excelRowVal) {
//		this.excelRowVal = excelRowVal;
//	}

	public StringProperty getFixedValue() {
		return fixedValue;
	}

	public void setFixedValue(StringProperty fixedValue) {
		this.fixedValue = fixedValue;
	}

	public List<String> getExcelRowInfo() {
		return excelRowInfo;
	}

	public void setExcelRowInfo(List<String> excelRowInfo) {
		this.excelRowInfo = excelRowInfo;
	}

	@Override
	public String toString() {
		return super.toString() + "ExcelFieldPo [excelRowVal=" + excelRowVal + ", fixedValue=" + fixedValue + "]";
	}

	public Integer getRowIdx() {
		if (rowIdx == null) {
			// 匹配到excel的列
			String rowIdxStr = "";
			String rowVal = this.getExcelRowVal().get();
			if (StrUtils.isNotNullOrEmpty(rowVal)) {
				String[] excelInfo = rowVal.split(" - ");
				rowIdxStr = excelInfo[0];
			}

			if (StrUtils.isNotNullOrEmpty(rowIdxStr)) { // 空表示没有匹配
				Integer rowidx = Integer.valueOf(rowIdxStr);
				rowIdx = rowidx - 1; // 下标从0开始, 需要减1
			} else {
				rowIdx = -1;
			}

		}
		return rowIdx;
	}

	public void setRowIdx(Integer rowIdx) {
		this.rowIdx = rowIdx;
	}

}
