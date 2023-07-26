package net.tenie.Sqlucky.sdk;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.excel.ExcelDataPo;
import net.tenie.Sqlucky.sdk.excel.ExcelUtil;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;

public class SqluckyBottomSheetUtility {

	public static SheetDataValue myTabValue() {
		SqluckyBottomSheet mtd = ComponentGetter.currentDataTab();
		SheetDataValue dv = mtd.getTableData();
		return dv;
	}

	public static ResultSetPo getResultSet(SheetDataValue dvt) {
//		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		if (dvt == null) {
			dvt = SqluckyBottomSheetUtility.myTabValue();
		}
		if (dvt != null) {
			ResultSetPo spo = dvt.getDataRs();
			return spo;
		}
		return null;
	}

	// 获取所有数据
	public static ObservableList<ResultSetRowPo> getTabData(SheetDataValue tableData) {
		SheetDataValue dvt = tableData;
		if (tableData == null) {
			dvt = SqluckyBottomSheetUtility.myTabValue();
		}
//		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		if (dvt != null) {
//			return 
			ResultSetPo spo = dvt.getDataRs();
			ObservableList<ResultSetRowPo> val = spo.getDatas();
			return val;
		}
		return null;
	}

//	// 获取字段
	public static ObservableList<SheetFieldPo> getFields(SheetDataValue tableData) {
//		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		SheetDataValue dvt = tableData;
		if (tableData == null) {
			dvt = SqluckyBottomSheetUtility.myTabValue();
		}
		if (dvt != null) {
			return dvt.getColss();
		}
		return null;
	}

//	// 获取tableName
	public static String getTableName(SheetDataValue tableData) {
		SheetDataValue dvt = tableData;
		if (tableData == null) {
			dvt = SqluckyBottomSheetUtility.myTabValue();
		}

		if (dvt != null) {
			return dvt.getTabName();
		}
		return "";
	}

	// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
	public static RsVal tableInfo(SheetDataValue dataObj) {
		if (dataObj == null) {
			dataObj = SqluckyBottomSheetUtility.myTabValue();
		}
		String connName = "";
		String tableName = "";
		Connection conn = null;
		ObservableList<ResultSetRowPo> alldata = null;
		SqluckyConnector cntor = null;
		FilteredTableView<ResultSetRowPo> dataTableView = null;
		if (dataObj != null) {
			connName = dataObj.getConnName();
			tableName = dataObj.getTabName();
			cntor = dataObj.getDbConnection();
			conn = cntor.getConn();

			alldata = dataObj.getDataRs().getDatas();

			dataTableView = dataObj.getTable();

		}
		RsVal rv = new RsVal();
		rv.conn = conn;
		rv.dbconnPo = cntor;
		rv.tableName = tableName;
		rv.alldata = alldata;
		rv.dataTableView = dataTableView;
		return rv;
	}

//	// 获取当前table view 的保存按钮
//	public static Button dataPaneSaveBtn() {
//		if (ComponentGetter.currentDataTab() == null)
//			return null;
//		return ComponentGetter.currentDataTab().getSaveBtn();
//	}

	// 获取当前table view 的详细按钮
	public static Button dataPaneDetailBtn() {
		return ComponentGetter.currentDataTab().getDetailBtn();
	}

	public static void addData(int rowNo, ResultSetRowPo newDate) {
		addDataNewLine(rowNo, newDate);
	}

	public static String getSelectSQL(SheetDataValue dvt) {
		if (dvt == null) {
			dvt = SqluckyBottomSheetUtility.myTabValue();
		}
		if (dvt != null) {
			return dvt.getSqlStr();
		}
		return "";
	}

	// 添加一行新数据
	public static void addDataNewLine(int rowNo, ResultSetRowPo vals) {
		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		if (dvt != null) {
			dvt.getDataRs().getNewDatas().add(vals);
		}
	}

	public static void addDataOldVal(int rowNo, ResultSetRowPo vals) {
		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		if (dvt != null) {
			dvt.getDataRs().getUpdateDatas().add(vals);
		}
	}

	public static ObservableList<ResultSetRowPo> getOldval() {
		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		if (dvt != null) {
			var v = dvt.getDataRs().getUpdateDatas();
			return v;
		}
		return null;
	}

	public static ObservableList<ResultSetRowPo> getModifyData(SheetDataValue tableData) {
		SheetDataValue dvt = tableData;
		if (tableData == null) {
			dvt = SqluckyBottomSheetUtility.myTabValue();
		}

		if (dvt != null) {
			var v = dvt.getDataRs().getUpdateDatas();
			return v;
		}
		return null;

//			return getNewLineDate();
	}

	public static ObservableList<ResultSetRowPo> getNewLineDate() {
		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		if (dvt != null) {
			var v = dvt.getDataRs().getNewDatas();
			return v;
		}
		return null;
	}

	public static void rmUpdateData(SheetDataValue tableData) {
		SheetDataValue dvt = tableData;
		if (tableData == null) {
			dvt = SqluckyBottomSheetUtility.myTabValue();
		}
		if (dvt != null) {
			dvt.getDataRs().getNewDatas().clear();
			dvt.getDataRs().getUpdateDatas().clear();
		}
	}

	public static void rmAppendData(SheetDataValue dvt) {
		if (dvt == null) {
			SqluckyBottomSheet mtd = ComponentGetter.currentDataTab();
			dvt = mtd.getTableData();
		}
		if (dvt != null) {
			dvt.getDataRs().getNewDatas().clear();

		}
	}

	public static void appendDate(ResultSetRowPo newDate) {
		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		if (dvt != null) {
			dvt.getDataRs().getNewDatas().add(newDate);
			dvt.getDataRs().getDatas().add(newDate);
		}
	}

	public static ObservableList<ResultSetRowPo> getAppendData(SheetDataValue dvt) {
		if (dvt == null) {
			dvt = SqluckyBottomSheetUtility.myTabValue();
		}
		if (dvt != null) {
			return dvt.getDataRs().getNewDatas();
		}
		return null;
	}

	public static Connection getDbconn(SheetDataValue tableData) {
		return getDbConnection(tableData).getConn();
	}

	public static String getConnName(SheetDataValue dvt) {
		if (dvt == null) {
			dvt = SqluckyBottomSheetUtility.myTabValue();
		}
		if (dvt != null) {
			return dvt.getConnName();
		}
		return "";
	}

	public static SqluckyConnector getDbConnection(SheetDataValue tableData) {

		SheetDataValue dvt = tableData;
		if (tableData == null) {
			dvt = SqluckyBottomSheetUtility.myTabValue();
		}
		return dvt.getDbConnection();
	}

	// 获取当前的表格
	@SuppressWarnings("unchecked")
	public static FilteredTableView<ResultSetRowPo> dataTableView(SqluckyBottomSheet mtd) {
		if (mtd == null) {
			mtd = ComponentGetter.currentDataTab();
		}

		var table = mtd.getTableData().getTable();
		return table;
	}

	// 获取当前表格选择的数据
	public static ObservableList<ResultSetRowPo> dataTableViewSelectedItems(SqluckyBottomSheet mtd) {
		ObservableList<ResultSetRowPo> vals = dataTableView(mtd).getSelectionModel().getSelectedItems();
		return vals;
	}

	public static ObservableList<ResultSetRowPo> getValsHelper(boolean isSelected, SqluckyBottomSheet mtd,
			SheetDataValue tableData) {
		ObservableList<ResultSetRowPo> vals = null;
		if (isSelected) {
			vals = SqluckyBottomSheetUtility.dataTableViewSelectedItems(mtd);
		} else {
			vals = SqluckyBottomSheetUtility.getTabData(tableData);
		}
		return vals;
	}

	// TODO table view 数据转换为excel导出的数据结构
	public static ExcelDataPo tableValueToExcelDataPo(boolean isSelect, SqluckyBottomSheet mtd,
			SheetDataValue tableData) {

		String tabName = SqluckyBottomSheetUtility.getTableName(tableData);
		ObservableList<SheetFieldPo> fpos = SqluckyBottomSheetUtility.getFields(tableData);

		ObservableList<ResultSetRowPo> rows = getValsHelper(isSelect, mtd, tableData);// valpo.getDatas();

		ExcelDataPo po = new ExcelDataPo();

		// 表头字段
		List<String> fields = new ArrayList<>();
		for (var fpo : fpos) {
			fields.add(fpo.getColumnLabel().get());
		}
		// 数据
		List<List<String>> datas = new ArrayList<>();
		for (var rowpo : rows) {
			List<String> rowlist = new ArrayList<>();
			ObservableList<ResultSetCellPo> cells = rowpo.getRowDatas();
			for (ResultSetCellPo cell : cells) {
				var cellval = cell.getCellData().get();
				if (cellval != null && "<null>".equals(cellval)) {
					cellval = null;
				}
				rowlist.add(cellval);
			}
			datas.add(rowlist);

		}

		po.setSheetName(tabName);
		po.setHeaderFields(fields);
		po.setDatas(datas);

		return po;
	}

	/**
	 * 表格数据导出到excel
	 * 
	 * @param isSelect true 导出选中行的数据, fasle 全部导出
	 */
	public static void exportExcelAction(boolean isSelect, SqluckyBottomSheet mtd, SheetDataValue tableData) {
		File ff = CommonUtility.getFilePathHelper("xls");
		if (ff == null)
			return;
		if (ff.exists()) {
			MyAlert.errorAlert("File Name Exist. Need A New File Name, Please!");
			return;
		}
		LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
			ExcelDataPo po = SqluckyBottomSheetUtility.tableValueToExcelDataPo(isSelect, mtd, tableData);
			try {
				ExcelUtil.createExcel(po, ff);
			} catch (Exception e1) {
				e1.printStackTrace();
				MyAlert.errorAlert("Error");
			}

		});
	}
}
