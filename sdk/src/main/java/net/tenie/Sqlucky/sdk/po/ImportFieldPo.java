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
	private StringProperty excelFieldVal = new SimpleStringProperty(""); // excel 对应列
	private StringProperty fixedValue = new SimpleStringProperty("");; // 不使用excel对应的列, 使用固定值

	private Integer fieldIdx = null; // 对应列的下标, 默认null , 如果excelFieldVal的值为空就赋值为-1

	// 可选的值
	private List<String> excelFieldInfo;

	public ImportFieldPo(SheetFieldPo sheetField) {
		try {
			BeanUtils.copyProperties(this, sheetField);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		excelFieldValListener();
	}

	public void excelFieldValListener() {
		excelFieldVal.addListener((obj, valOld, valNew) -> {
			if (StrUtils.isNotNullOrEmpty(valNew)) {
				// 如果是手动修改了下拉选的值
				if (excelFieldInfo != null) {
					if (excelFieldInfo.contains(valNew)) {
					} else {
						excelFieldVal.set(valOld);

					}
				}

			}
		});
	}

	public StringProperty getExcelFieldVal() {
		return excelFieldVal;
	}

	public StringProperty getFixedValue() {
		return fixedValue;
	}

	public void setFixedValue(StringProperty fixedValue) {
		this.fixedValue = fixedValue;
	}

	public List<String> getExcelFieldInfo() {
		return excelFieldInfo;
	}

	public void setExcelFieldInfo(List<String> excelInfo) {
		this.excelFieldInfo = excelInfo;
	}

	@Override
	public String toString() {
		return super.toString() + "ExcelFieldPo [excelFieldVal=" + excelFieldVal + ", fixedValue=" + fixedValue + "]";
	}

	public Integer getFieldIdx() {
		if(StrUtils.isNotNullOrEmpty(this.getExcelFieldVal().get())) {
			if (fieldIdx == null) {
				// 匹配到excel的列
				String fieldIdxStr = "";
				String fieldVal = this.getExcelFieldVal().get();
				if (StrUtils.isNotNullOrEmpty(fieldVal)) {
					String[] excelInfo = fieldVal.split(" - ");
					fieldIdxStr = excelInfo[0];
				}

				if (StrUtils.isNotNullOrEmpty(fieldIdxStr)) { // 空表示没有匹配
					Integer fieldidx = Integer.valueOf(fieldIdxStr);
					fieldIdx = fieldidx - 1; // 下标从0开始, 需要减1
				} else {
					fieldIdx = -1;
				}

			}
		}else {
			fieldIdx = -1;
		}
		
		return fieldIdx;
	}

	public void setFieldIdx(Integer fieldIdx) {
		this.fieldIdx = fieldIdx;
	}

}
