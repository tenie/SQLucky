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
public class ExcelFieldPo extends SheetFieldPo {

	// excel数据导入表需要使用下面2个字段
	private StringProperty excelRowVal = new SimpleStringProperty(""); // excel 对应列
	private SimpleStringProperty excelRowIdx = new SimpleStringProperty(""); // excel 对应列号
	private StringProperty fixedValue = new SimpleStringProperty("");; // 不使用excel对应的列, 使用固定值

	// 可选的值
	private List<String> excelRowInfo;

	public ExcelFieldPo(SheetFieldPo sheetField) {
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
						String[] excelInfo = valNew.split(" - ");
						excelRowIdx.setValue(excelInfo[0]);
					} else {
						excelRowIdx.setValue("");
						excelRowVal.set(valOld);

					}
				}

			} else {
				excelRowIdx.setValue("");
			}
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

	public SimpleStringProperty getExcelRowIdx() {
		return excelRowIdx;
	}

	public void setExcelRowIdx(SimpleStringProperty excelRowIdx) {
		this.excelRowIdx = excelRowIdx;
	}

	public List<String> getExcelRowInfo() {
		return excelRowInfo;
	}

	public void setExcelRowInfo(List<String> excelRowInfo) {
		this.excelRowInfo = excelRowInfo;
	}

	@Override
	public String toString() {
		return super.toString() + "ExcelFieldPo [excelRowVal=" + excelRowVal + ", excelRowIdx=" + excelRowIdx
				+ ", fixedValue=" + fixedValue + "]";
	}

	public static void main(String[] args) {
		SheetFieldPo sheetField = new SheetFieldPo();
		sheetField.setColumnLabel("1111");
		sheetField.setColumnName("222");
		System.out.println(sheetField);

		ExcelFieldPo po = new ExcelFieldPo(sheetField);
		System.out.println(po);
	}

}
