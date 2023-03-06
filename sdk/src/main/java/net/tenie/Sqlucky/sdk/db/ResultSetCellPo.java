package net.tenie.Sqlucky.sdk.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
/**
 * 表格中某一个单元格的对象
 * 保存类单元格属于哪个字段, 值, 修改前的值等
 * @author tenie
 *
 */
public class ResultSetCellPo {
	

	private static Logger logger = LogManager.getLogger(ResultSetCellPo.class);
	private SheetFieldPo field;
	private StringProperty cellData; 
	private StringProperty oldCellData;  // 如果被更新, 旧的值放这个理
	private ResultSetRowPo currentRow;
	private int index = -1;
	private Boolean hasModify = false;
	
	public void clean() {
		field = null;
		cellData = null;
		oldCellData = null;
		currentRow = null;
	}
	
//	public ResultSetCellPo(ResultSetRowPo currentRow, StringProperty cellData, SheetFieldPo field) {
//		this.index = index;
//		this.cellData = cellData;
//		this.field = field; 
//		addStringPropertyChangeListener();
//	}
	
	protected ResultSetCellPo(ResultSetRowPo currentRow, StringProperty cellData, SheetFieldPo field) {
		this.index = currentRow.cellSize();
		this.cellData = cellData;
		this.field = field; 
		this.currentRow = currentRow;  
	}
	
	public StringProperty getCellData() {
		return cellData;
	}
	public void setCellData(StringProperty cellData) {
		
		this.cellData = cellData;
		
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	public SheetFieldPo getField() {
		return field;
	}

	public void setField(SheetFieldPo field) {
		this.field = field;
	}

	public StringProperty getOldCellData() {
		return oldCellData;
	}

	public void setOldCellData(StringProperty oldCellData) {
		this.oldCellData = oldCellData;
	}

	public ResultSetRowPo getCurrentRow() {
		return currentRow;
	}

	public void setCurrentRow(ResultSetRowPo currentRow) {
		this.currentRow = currentRow;
	}

	public Boolean getHasModify() {
		return hasModify;
	}

	public void setHasModify(Boolean hasModify) {
		this.hasModify = hasModify;
	}

	@Override
	public String toString() {
		return "ResultSetCellPo [field=" + field + ", cellData=" + cellData + ", oldCellData=" + oldCellData
				+ ", index=" + index + "]";
	}
	
	// 数据单元格添加监听
		// 字段修改事件
	public static void addStringPropertyChangeListener(ResultSetCellPo cell) {
		ChangeListener<String> cl = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				logger.info("add String Property Change Listener ：newValue：" + newValue + " | oldValue =" + oldValue);
//					logger.info("key ==" + tabId + "-" + rowNo);
				logger.info("observable = " + observable);
				int dbtype = cell.getField().getColumnType().get();

				// 如果类似是数字的, 新值不是数字, 还原
				if (CommonUtility.isNum(dbtype) && !StrUtils.isNumeric(newValue) && !"<null>".equals(newValue)) {
					Platform.runLater(() -> cell.getCellData().setValue(oldValue));
					return;
				}

				if (CommonUtility.isDateTime(dbtype) && "".equals(newValue)) {
					Platform.runLater(() ->  cell.getCellData().setValue("<null>"));
				}
				if (SqluckyBottomSheetUtility.dataPaneSaveBtn() != null) {
					SqluckyBottomSheetUtility.dataPaneSaveBtn().setDisable(false);
				}
//				setCellData();
				if(cell.getOldCellData() == null) {
					cell.setOldCellData(new SimpleStringProperty(oldValue));
					cell.setHasModify(true);  
					cell.getCurrentRow().setHasModify(true);
				}

//					ObservableList<StringProperty> oldDate = FXCollections.observableArrayList();
//					if (!SqluckyBottomSheetUtility.exist( rowNo)) {
//						for (int i = 0; i < rowDatas.size(); i++) {
//							if (i == idx) {
//								oldDate.add(new SimpleStringProperty(oldValue));
//							} else {
//								oldDate.add(rowDatas.get(i).getCellData());
//							}
//						}
//						SqluckyBottomSheetUtility.addData( rowNo, vals, oldDate); // 数据修改缓存, 用于之后更新
//					} else {
//						SqluckyBottomSheetUtility.addData( rowNo, vals);
//					}
			}
		};
		cell.getCellData().addListener(cl);
	}
}
