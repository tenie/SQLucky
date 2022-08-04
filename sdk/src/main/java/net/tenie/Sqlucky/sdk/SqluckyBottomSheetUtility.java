package net.tenie.Sqlucky.sdk;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;

public class SqluckyBottomSheetUtility {

	public static SheetDataValue myTabValue() {
		SqluckyBottomSheet mtd = ComponentGetter.currentDataTab();
		SheetDataValue dv = mtd.getTableData();
		return dv;
	}
	
	// 获取所有数据
	public static ObservableList<ResultSetRowPo> getTabData() {
		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		if (dvt != null) {
//			return 
			ResultSetPo spo = dvt.getDataRs();
			 ObservableList<ResultSetRowPo> val = spo.getDatas();
			 return val;
		}
		return null;
	}
//	// 获取字段
	public static ObservableList<SheetFieldPo> getFields() {
		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		if (dvt != null) {
			return dvt.getColss();
		}
		return null;
	}
	
//	// 获取tableName
	public static String getTableName() {
		SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
		if (dvt != null) {
			return dvt.getTabName();
		}
		return "";
	}
	// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
		public static RsVal tableInfo() {
			SheetDataValue dataObj = SqluckyBottomSheetUtility.myTabValue();
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
		// 获取当前table view 的保存按钮
		public static Button dataPaneSaveBtn() {
			return ComponentGetter.currentDataTab().getSaveBtn();
		}

		// 获取当前table view 的详细按钮
		public static Button dataPaneDetailBtn() {
			return ComponentGetter.currentDataTab().getDetailBtn();
		}

		public static boolean exist(int row) {
			//TODO 后期重构
//			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
//			if (dvt != null) {
//				Map<String, ObservableList<StringProperty>> oldval = dvt.getNewLineDate();
//				if (null != oldval.get(row + "")) {
//					return true;
//				}
//			}
			return false;
		}

		public static void addData(int rowNo,ResultSetRowPo newDate,
				ResultSetRowPo oldDate) {
			if (!exist(rowNo)) {
				addDataOldVal(rowNo, oldDate);
			}
			addDataNewLine(rowNo, newDate);

		}

		public static void addData(int rowNo, ResultSetRowPo newDate) {
			addDataNewLine(rowNo, newDate);
		}

		public static String getSelectSQL() {
			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
			if (dvt != null) {
				return dvt.getSqlStr();
			}
			return "";
		}

//		// 添加一行新数据
		public static void addDataNewLine(int rowNo, ResultSetRowPo  vals) {
			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
			if (dvt != null) {
				dvt.getDataRs().getNewDatas().add(vals);
//				Map<String, ObservableList<StringProperty>> map = dvt.getNewLineDate();
//				map.put(rowNo + "", vals);
			}
		}

		
		public static void addDataOldVal(int rowNo, ResultSetRowPo vals) {
			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
			if (dvt != null) {
				dvt.getDataRs().getUpdateDatas().add(vals);
//				Map<String, ObservableList<StringProperty>> map = dvt.getOldval();
//				map.put(rowNo + "", vals);
			}
		}

		public static ObservableList<StringProperty> getold2(String row) {
//			var ov = getOldval();
//			return ov.get(row);
			return null;
		}

		public static ObservableList<ResultSetRowPo> getOldval() {
			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
			if (dvt != null) {
				var v = dvt.getDataRs().getUpdateDatas();
				return v;
			}
			return null;
		}

		public static ObservableList<ResultSetRowPo>  getModifyData() {
			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
			if (dvt != null) {
				var v = dvt.getDataRs().getUpdateDatas();
				return v;
			}
			return null;
			
//			return getNewLineDate();
		}

		public static ObservableList<ResultSetRowPo>  getNewLineDate() {
			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
			if (dvt != null) {
				var v = dvt.getDataRs().getNewDatas();
				return v;
			}
			return null;
		}

		public static void rmUpdateData() {
			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
			if (dvt != null) {
				dvt.getDataRs().getNewDatas().clear();
				dvt.getDataRs().getUpdateDatas().clear();
			}
		}

		public static void rmAppendData() {
			SqluckyBottomSheet mtd = ComponentGetter.currentDataTab();
			SheetDataValue dvt = mtd.getTableData();
			if (dvt != null) {
				dvt.getDataRs().getNewDatas().clear();

			}
		}

		public static void appendDate(int rowNo, ResultSetRowPo newDate) {
			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
			if (dvt != null) {
				dvt.getDataRs().getNewDatas().add(newDate);
//				Map<String, ObservableList<StringProperty>> map = dvt.getAppendData();
//				map.put(rowNo + "", newDate);
			}
		}

		public static List<ObservableList<StringProperty>> getAppendData2() {
			//TODO 后期重构
//			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
//			if (dvt != null) {
//				List<ObservableList<StringProperty>> dataList = new ArrayList<>();
//
//				var map = dvt.getAppendData();
//				for (String key : map.keySet()) {
//					dataList.add(map.get(key));
//				}
//				return dataList;
//			}
			return null;

		}

		public static void deleteTabDataRowNo2(String no) {
			//TODO 后期重构
//			ObservableList<ObservableList<StringProperty>> ol = getTabData();
//			if (ol == null)
//				return;
//			for (int i = 0; i < ol.size(); i++) {
//				ObservableList<StringProperty> sps = ol.get(i);
//				int len = sps.size();
//				String dro = sps.get(len - 1).get();
//				if (dro.equals(no)) {
//					ol.remove(i);
//					break;
//				}
//			}

		}

		public static Connection getDbconn() {
			return getDbConnection().getConn();
		}

		public static String getConnName() {
			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
			if (dvt != null) {
				return dvt.getConnName();
			}
			return "";
		}

		public static SqluckyConnector getDbConnection() {
			SheetDataValue dvt = SqluckyBottomSheetUtility.myTabValue();
			return dvt.getDbConnection();
		}

		// 获取当前的表格
		@SuppressWarnings("unchecked")
		public static FilteredTableView<ResultSetRowPo> dataTableView() {
			SqluckyBottomSheet mtd = ComponentGetter.currentDataTab();
			var table = mtd.getTableData().getTable();
			return table;
		}

		// 获取当前表格选择的数据
		public static ObservableList<ResultSetRowPo> dataTableViewSelectedItems() {
			ObservableList<ResultSetRowPo> vals = 
					dataTableView().getSelectionModel().getSelectedItems();
			return vals;
		}

		// 获取 当前table view 的控制面板
//		public static AnchorPane optionPane() {
//			if (ComponentGetter.dataTabPane == null || ComponentGetter.dataTabPane.getSelectionModel() == null
//					|| ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem() == null)
//				return null;
//			Node vb = ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem().getContent();
//			if (vb != null) {
//				VBox vbx = (VBox) vb;
//				AnchorPane fp = (AnchorPane) vbx.getChildren().get(0);
//				return fp;
//			}
//			return null;
//		}

		// 获取当前数据页面 中的 某个按钮
//		public static Button getDataOptionBtn(String btnName) {
//			AnchorPane fp = optionPane();
//			if (fp == null)
//				return null;
//			Optional<Node> fn = fp.getChildren().stream().filter(v -> {
//				return v.getId().equals(btnName);
//			}).findFirst();
//			Button btn = (Button) fn.get();
//
//			return btn;
//		}

		
		public static ObservableList<ResultSetRowPo> getValsHelper(boolean isSelected) {
			ObservableList<ResultSetRowPo> vals = null;
			if (isSelected) {
				vals = SqluckyBottomSheetUtility.dataTableViewSelectedItems();
			} else {
				vals =  SqluckyBottomSheetUtility.getTabData();
			}
			return vals;
		}
}
