package net.tenie.fx.Action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.TablePo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.sqlExecute.SqlExecuteOption;
import net.tenie.fx.component.InfoTree.TreeObjAction;
import net.tenie.fx.component.InfoTree.TreeItem.TreeObjCache;
import net.tenie.fx.dao.DeleteDao;
import net.tenie.fx.dao.InsertDao;
import net.tenie.fx.dao.UpdateDao;


public class ButtonAction {
	
	public static void dataSave() {
		Button saveBtn = SqluckyBottomSheetUtility.dataPaneSaveBtn();
		String tabName = SqluckyBottomSheetUtility.getTableName();
		Connection conn = SqluckyBottomSheetUtility.getDbconn();
		SqluckyConnector  dpo = SqluckyBottomSheetUtility.getDbConnection();
		if (tabName != null && tabName.length() > 0) {
			// 字段
			ObservableList<SheetFieldPo> fpos = SqluckyBottomSheetUtility.getFields();
			// 待保存数据
			 ObservableList<ResultSetRowPo> modifyData = SqluckyBottomSheetUtility.getModifyData();
			// 执行sql 后的信息 (主要是错误后显示到界面上)
			DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
			boolean btnDisable = true;
			if (!modifyData.isEmpty()) {
				for (ResultSetRowPo val : modifyData) {
					try {
						String msg = UpdateDao.execUpdate(conn, tabName, val);
						
						if(StrUtils.isNotNullOrEmpty(msg)) {
							var fds = ddlDmlpo.getFields();
							var row = ddlDmlpo.addRow();
							ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ), fds.get(0));
							ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fds.get(1));
							ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("success"), fds.get(2));
						}

					} catch (Exception e1) {
						e1.printStackTrace();
						btnDisable = false;
						String 	msg = "failed : " + e1.getMessage();
						msg += "\n"+dpo.translateErrMsg(msg);
						var fds = ddlDmlpo.getFields();
						var row = ddlDmlpo.addRow();
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ), fds.get(0));
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fds.get(1));
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("failed"), fds.get(2));
					}
				}
				SqluckyBottomSheetUtility.rmUpdateData();
			}

			// 插入操作
			ObservableList<ResultSetRowPo> dataList = SqluckyBottomSheetUtility.getAppendData();
			for (ResultSetRowPo os : dataList) {
				try {
					ObservableList<ResultSetCellPo> cells = os.getRowDatas();
					String msg = InsertDao.execInsert(conn, tabName, cells);
					var fds = ddlDmlpo.getFields();
					var row = ddlDmlpo.addRow();
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ), fds.get(0));
					ddlDmlpo.addData(row, new SimpleStringProperty(msg), fds.get(1));
					ddlDmlpo.addData(row, new SimpleStringProperty("success"), fds.get(2));

					// 对insert 的数据保存后 , 不能再修改
//					ObservableList<ResultSetCellPo> cells = os.getRowDatas();
					for (int i = 0; i < cells.size(); i++) {
						var cellpo = cells.get(i);
						StringProperty sp = cellpo.getCellData();
						CommonUtility.prohibitChangeListener(sp, sp.get());
					}

				} catch (Exception e1) {
					e1.printStackTrace();
					btnDisable = false;
					var fs = ddlDmlpo.getFields();
					var row = ddlDmlpo.addRow();
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ), fs.get(0));
					ddlDmlpo.addData(row, new SimpleStringProperty(e1.getMessage()), fs.get(1));
					ddlDmlpo.addData(row, new SimpleStringProperty("failed"), fs.get(2));
				}
			}
			// 删除缓存数据
			SqluckyBottomSheetUtility.rmAppendData();

			// 保存按钮禁用
			saveBtn.setDisable(btnDisable);
			SqlExecuteOption.showExecuteSQLInfo(ddlDmlpo, null);

		}

	} 
	
	public static void deleteData() {
		

			// 获取当前的table view
			FilteredTableView<ResultSetRowPo> table = SqluckyBottomSheetUtility.dataTableView();
			String tabName = SqluckyBottomSheetUtility.getTableName();
			Connection conn = SqluckyBottomSheetUtility.getDbconn();
			ObservableList<SheetFieldPo> fpos = SqluckyBottomSheetUtility.getFields();

			ObservableList<ResultSetRowPo> vals = table.getSelectionModel().getSelectedItems();

			// 行号集合
			List<String> temp = new ArrayList<>();

			// 执行sql 后的信息 (主要是错误后显示到界面上)
			DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
			Consumer<String> caller = x -> {
				try {
					for (int i = 0; i < vals.size(); i++) {
						ResultSetRowPo sps = vals.get(i);
						String msg = DeleteDao.execDelete(conn, tabName, sps);
						var rs = sps.getResultSet();
						rs.getDatas().remove(sps);
						var fs = ddlDmlpo.getFields();
						var row = ddlDmlpo.addRow();
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL(new Date())), fs.get(0));
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fs.get(1));
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("success"), fs.get(2));

					}

				} catch (Exception e1) {
					var fs = ddlDmlpo.getFields();
					var row = ddlDmlpo.addRow();
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL(new Date())), fs.get(0));
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(e1.getMessage()), fs.get(1));
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("fail."), fs.get(2));
				} finally {
					SqlExecuteOption.showExecuteSQLInfo(ddlDmlpo, null);
				}
			};
		if(vals.size() >0 ) {
			MyAlert.myConfirmation("Sure to delete selected rows?", caller);
		}
		

	}
	// 复制选择的 行数据 插入到表格末尾
	public static void copyData() {
		// 获取当前的table view
		FilteredTableView<ResultSetRowPo> table = SqluckyBottomSheetUtility.dataTableView();
		// 获取字段属性信息
		ObservableList<SheetFieldPo> fs = SqluckyBottomSheetUtility.getFields();
		// 选中的行数据
		ObservableList<ResultSetRowPo> vals = SqluckyBottomSheetUtility.dataTableViewSelectedItems();
		try {
			// 遍历选中的行
			for (int i = 0; i < vals.size(); i++) {
				// 一行数据, 提醒: 最后一列是行号
				ResultSetRowPo rowPo = vals.get(i);
				var rs = rowPo.getResultSet();
				ResultSetRowPo appendRow = rs.createAppendNewRow();
				ObservableList<ResultSetCellPo> cells = rowPo.getRowDatas();
				// copy 一行
				ObservableList<StringProperty> item = FXCollections.observableArrayList();
//				int newLineidx = ConfigVal.newLineIdx++;
				for (int j = 0 ; j < cells.size(); j++) {
					ResultSetCellPo cellPo = cells.get(j);
					
					StringProperty newsp = new SimpleStringProperty(cellPo.getCellData().get());
					appendRow.addCell(newsp, cellPo.getField());
					int dataType = fs.get(j).getColumnType().get();
					CommonUtility.newStringPropertyChangeListener(newsp, dataType);
					item.add(newsp);
				}

			}
			table.scrollTo(table.getItems().size() - 1);

			// 保存按钮亮起
			SqluckyBottomSheetUtility.dataPaneSaveBtn().setDisable(false);
		} catch (Exception e2) {
			MyAlert.errorAlert( e2.getMessage());
		}
	
	}
	
	
	
	
	
	// 判断是否需要删除两边的单引号'
	private static String needTrimChar(String value) {
		String rs = value;
		char c1 = value.charAt(0);
		char c2 = value.charAt(value.length() - 1 );
		if( c1 == c2 && c1 == '\'') {
			rs =  StrUtils.trimChar(value, "'");
		}
		return rs;
	}
	
	// 更新查询结果中所有数据对应列的值
	public static void updateAllColumn(int colIdx,String value) {
		RsVal rv = SqluckyBottomSheetUtility.tableInfo();
		value = needTrimChar(value);
		if("null".equals(value)) {
			value = "<null>";
		}
		FilteredTableView<ResultSetRowPo> dataTableView = rv.dataTableView;
		ObservableList<ResultSetRowPo> alls = dataTableView.getItems();
		for(ResultSetRowPo ls : alls) {
			StringProperty  tmp = ls.getRowDatas().get(colIdx).getCellData();
//			StringProperty  tmp = ls.get(colIdx);
			tmp.setValue(value);
		}
		dataSave();
	}
	
	// 更新查询结果中选中的数据 对应列的值
	 public static void updateSelectedDataColumn(int colIdx,String value) {
		value = needTrimChar(value);
		if("null".equals(value)) {
			value = "<null>";
		}
		 
		ObservableList<ResultSetRowPo> alls = SqluckyBottomSheetUtility.dataTableViewSelectedItems();
		for(ResultSetRowPo ls : alls) {
			StringProperty  tmp = ls.getRowDatas().get(colIdx).getCellData();
//			StringProperty  tmp = ls.get(colIdx);
			tmp.setValue(value);
		}
		dataSave();
	}
	
	
	// 获取tree 节点中的 table 的sql
	public static void findTable() {
		RsVal rv = SqluckyBottomSheetUtility.tableInfo();
		SqluckyConnector dbcp = rv.dbconnPo;
		if(dbcp == null ) {
			return ;
		}
		String tbn = rv.tableName;
		String key = dbcp.getConnName() + "_" +dbcp.getDefaultSchema();
		// 从表格缓存中查找表
		List<TablePo> tbs = TreeObjCache.tableCache.get(key);
		 
		TablePo tbrs = null; 
		for(TablePo po: tbs) {
			if( po.getTableName().equals(tbn) ){
				tbrs = po;
				break;
			}
		}
		// 从试图缓存中查找
		if(tbrs == null ) {
			 tbs = TreeObjCache.viewCache.get(key);
			 for(TablePo po: tbs) {
					if( po.getTableName().equals(tbn) ){
						tbrs = po;
						break;
					}
				}
		}
		
		if( tbrs != null)
		TreeObjAction.showTableSql(dbcp, tbrs, tbn);
		
	}
	
	
	
	
}
