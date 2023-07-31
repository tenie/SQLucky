package net.tenie.Sqlucky.sdk.utility;

public class SqluckyBottomSheetUtility {

//	public static SheetDataValue myTabValue() {
//		SqluckyBottomSheet mtd = ComponentGetter.currentDataTab();
//		SheetDataValue dv = mtd.getTableData();
//		return dv;
//	}

//	public static ResultSetPo getResultSet(SheetDataValue dvt) {
//		if (dvt != null) {
//			ResultSetPo spo = dvt.getDataRs();
//			return spo;
//		}
//		return null;
//	}

//	// 获取字段
//	public static ObservableList<SheetFieldPo> getFields(SheetDataValue dvt) {
//		if (dvt != null) {
//			return dvt.getColss();
//		}
//		return null;
//	}

//	// 获取tableName
//	public static String getTableName(SheetDataValue dvt) {
//
//		if (dvt != null) {
//			return dvt.getTabName();
//		}
//		return "";
//	}

	// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
//	public static RsVal tableInfo(SheetDataValue dataObj) {
//		if (dataObj == null) {
//			return null;
//		}
//		String tableName = "";
//		Connection conn = null;
//		ObservableList<ResultSetRowPo> alldata = null;
//		SqluckyConnector cntor = null;
//		FilteredTableView<ResultSetRowPo> dataTableView = null;
//		if (dataObj != null) {
//			tableName = dataObj.getTabName();
//			cntor = dataObj.getDbConnection();
//			conn = cntor.getConn();
//
//			alldata = dataObj.getDataRs().getDatas();
//
//			dataTableView = dataObj.getTable();
//
//		}
//		RsVal rv = new RsVal();
//		rv.conn = conn;
//		rv.dbconnPo = cntor;
//		rv.tableName = tableName;
//		rv.alldata = alldata;
//		rv.dataTableView = dataTableView;
//		return rv;
//	}

//	// 获取当前table view 的保存按钮
//	public static Button dataPaneSaveBtn() {
//		if (ComponentGetter.currentDataTab() == null)
//			return null;
//		return ComponentGetter.currentDataTab().getSaveBtn();
//	}

	// 获取当前table view 的详细按钮
//	public static Button dataPaneDetailBtn() {
//		return ComponentGetter.currentDataTab().getDetailBtn();
//	}

//	public static void addData(SheetDataValue dvt, int rowNo, ResultSetRowPo newDate) {
//		addDataNewLine(dvt, rowNo, newDate);
//	}

//	public static String getSelectSQL(SheetDataValue dvt) {
//		if (dvt != null) {
//			return dvt.getSqlStr();
//		}
//		return "";
//	}

	// 添加一行新数据
//	public static void addDataNewLine(SheetDataValue dvt, int rowNo, ResultSetRowPo vals) {
//		if (dvt != null) {
//			dvt.getDataRs().getNewDatas().add(vals);
//		}
//	}

//	public static void addDataOldVal(SheetDataValue dvt, int rowNo, ResultSetRowPo vals) {
//		if (dvt != null) {
//			dvt.getDataRs().getUpdateDatas().add(vals);
//		}
//	}

//	public static ObservableList<ResultSetRowPo> getOldval(SheetDataValue dvt) {
//		if (dvt != null) {
//			var v = dvt.getDataRs().getUpdateDatas();
//			return v;
//		}
//		return null;
//	}

//	public static ObservableList<ResultSetRowPo> getModifyData(SheetDataValue dvt) {
//		if (dvt != null) {
//			var v = dvt.getDataRs().getUpdateDatas();
//			return v;
//		}
//		return null;
//
//	}

//	public static ObservableList<ResultSetRowPo> getNewLineDate(SheetDataValue dvt) {
//		if (dvt != null) {
//			var v = dvt.getDataRs().getNewDatas();
//			return v;
//		}
//		return null;
//	}

//	public static void rmAppendData(SheetDataValue dvt) {
//		if (dvt != null) {
//			dvt.getDataRs().getNewDatas().clear();
//
//		}
//	}

//	public static void appendDate(SheetDataValue dvt, ResultSetRowPo newDate) {
//		if (dvt != null) {
//			dvt.getDataRs().getNewDatas().add(newDate);
//			dvt.getDataRs().getDatas().add(newDate);
//		}
//	}

//	public static ObservableList<ResultSetRowPo> getAppendData(SheetDataValue dvt) {
//		if (dvt != null) {
//			return dvt.getDataRs().getNewDatas();
//		}
//		return null;
//	}

//	public static Connection getDbconn(SheetDataValue tableData) {
//		return tableData.getDbConnection().getConn();
////		return getDbConnection(tableData).getConn();
//	}

//	public static String getConnName(SheetDataValue dvt) {
//		if (dvt != null) {
//			return dvt.getConnName();
//		}
//		return "";
//	}

//	public static SqluckyConnector getDbConnection(SheetDataValue dvt) {
//
//		SheetDataValue dvt = tableData;
//		if (tableData == null) {
//			dvt = SqluckyBottomSheetUtility.myTabValue();
//		}
//		return dvt.getDbConnection();
//	}

	// 获取当前的表格
//	@SuppressWarnings("unchecked")
//	public static FilteredTableView<ResultSetRowPo> dataTableView(SqluckyBottomSheet mtd) {
//		if (mtd == null) {
//			return null;
//		}
//		var table = mtd.getTableData().getTable();
//		return table;
//	}

	// 获取当前表格选择的数据
//	public static ObservableList<ResultSetRowPo> dataTableViewSelectedItems(SqluckyBottomSheet mtd) {
//		
//		ObservableList<ResultSetRowPo> vals = dataTableView(mtd).getSelectionModel().getSelectedItems();
//		return vals;
//	}

//	public static ObservableList<ResultSetRowPo> getValsHelper(boolean isSelected, SqluckyBottomSheet mtd) {
//		ObservableList<ResultSetRowPo> vals = null;
//		if (isSelected) {
//			vals = mtd.getTableData().getTable().getSelectionModel().getSelectedItems(); // SqluckyBottomSheetUtility.dataTableViewSelectedItems(mtd);
//		} else {
//			SheetDataValue tableData = mtd.getTableData();
//			vals = SqluckyBottomSheetUtility.getTabData(tableData);
//		}
//		return vals;
//	}

	// TODO table view 数据转换为excel导出的数据结构
//	public static ExcelDataPo tableValueToExcelDataPo(boolean isSelect, SqluckyBottomSheet mtd,
//			SheetDataValue tableData) {
//
//		String tabName = tableData.getTabName();// SqluckyBottomSheetUtility.getTableName(tableData);
//		ObservableList<SheetFieldPo> fpos = tableData.getColss();// SqluckyBottomSheetUtility.getFields(tableData);
//
//		ObservableList<ResultSetRowPo> rows = getValsHelper(isSelect, mtd);// valpo.getDatas();
//
//		ExcelDataPo po = new ExcelDataPo();
//
//		// 表头字段
//		List<String> fields = new ArrayList<>();
//		for (var fpo : fpos) {
//			fields.add(fpo.getColumnLabel().get());
//		}
//		// 数据
//		List<List<String>> datas = new ArrayList<>();
//		for (var rowpo : rows) {
//			List<String> rowlist = new ArrayList<>();
//			ObservableList<ResultSetCellPo> cells = rowpo.getRowDatas();
//			for (ResultSetCellPo cell : cells) {
//				var cellval = cell.getCellData().get();
//				if (cellval != null && "<null>".equals(cellval)) {
//					cellval = null;
//				}
//				rowlist.add(cellval);
//			}
//			datas.add(rowlist);
//
//		}
//
//		po.setSheetName(tabName);
//		po.setHeaderFields(fields);
//		po.setDatas(datas);
//
//		return po;
//	}

//	/**
//	 * 表格数据导出到excel
//	 * 
//	 * @param isSelect  true 导出选中行的数据, fasle 全部导出
//	 * @param mtd       可以输入null , null时从当前tabpane中查找对象
//	 * @param tableData 可以输入null , null时从当前tabpane中查找对象
//	 */
//	public static void exportExcelAction(boolean isSelect, SqluckyBottomSheet mtd, SheetDataValue tableData) {
//		File ff = CommonUtility.getFilePathHelper("xls");
//		if (ff == null)
//			return;
//		if (ff.exists()) {
//			MyAlert.errorAlert("File Name Exist. Need A New File Name, Please!");
//			return;
//		}
//		LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
//			ExcelDataPo po = SqluckyBottomSheetUtility.tableValueToExcelDataPo(isSelect, mtd, tableData);
//			try {
//				ExcelUtil.createExcel(po, ff);
//			} catch (Exception e1) {
//				e1.printStackTrace();
//				MyAlert.errorAlert("Error");
//			}
//
//		});
//	}
}
