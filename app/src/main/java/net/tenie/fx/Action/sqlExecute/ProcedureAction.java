package net.tenie.fx.Action.sqlExecute;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.component.DataViewContainer;
import net.tenie.Sqlucky.sdk.component.bottomSheet.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.db.ProcedureFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.DateUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtils;

public class ProcedureAction {

	private static Logger logger = LogManager.getLogger(ProcedureAction.class);
//	private static Thread staticThread = null;

	/**
	 * 存储过程执行,
	 * 
	 * @param sql
	 * @param dpo
	 * @param fields
	 * @param tidx
	 * @param isLock
	 * @throws Exception
	 */
	public static void procedureAction(String sql, SqluckyConnector dpo, List<ProcedureFieldPo> fields, int tidx,
			boolean isLock) throws Exception {
//		staticThread = thread;
		String msg = "";
		Connection conn = dpo.getConn();
		try {
			// 获取表名, 存储过程拿来做为表名
			String tableName = sql;
			MyBottomSheet myBottomSheet = new MyBottomSheet(tableName);
			FilteredTableView<ResultSetRowPo> table = SdkComponent.creatFilteredTableView(myBottomSheet);

			logger.info("tableName= " + tableName + "\n sql = " + sql);

			SheetDataValue dvt = myBottomSheet.getTableData();
			// 执行sql
			SelectDao.callProcedure(conn, sql, table.getId(), dvt, fields);

			DataViewContainer.setTabRowWith(table, dvt.getDataRs().getDatas().size());

			String connectName = DBConns.getCurrentConnectName();
			dvt.setSqlStr(sql);
			dvt.setTable(table);
			dvt.setTabName(tableName);
//			dvt.setConnName(connectName);
//			dvt.setDbconns(conn);
			dvt.setDbConnection(dpo);
			dvt.setLock(isLock);

			ObservableList<ResultSetRowPo> allRawData = dvt.getDataRs().getDatas();
			ObservableList<SheetFieldPo> colss = dvt.getColss();

			// 缓存
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true));

			// 根据表名获取tablepo对象
//			List<String> keys = findPrimaryKeys(conn, tableName);
			// table 添加列和数据
			// 表格添加列
			var ls = SdkComponent.createTableColForInfo(colss);
			// 设置 列的 右键菜单
			SqluckyAppDB.setDataTableContextMenu(myBottomSheet, ls, colss);
			table.getColumns().addAll(ls);
			table.setItems(allRawData);
			// 渲染界面

//			if (staticThread != null && !staticThread.isInterrupted()) {
			if (! Thread.currentThread().isInterrupted()) {
				if (hasOut(fields)) {
//					SqluckyBottomSheet mtd = ComponentGetter.appComponent.sqlDataSheet(dvt, tidx, true);
//					SqlExecuteOption.rmWaitingPane( isRefresh );
					TableViewUtils.rmWaitingPane(false);
					myBottomSheet.showSelectData(tidx, false);

				} else {
					msg = "ok. ";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "failed : " + e.getMessage();
//			if(dpo.getDbVendor().toUpperCase().equals( DbVendor.db2.toUpperCase())) {
//				msg += "\n"+Db2ErrorCode.translateErrMsg(msg);
//			}  
			msg += "\n" + dpo.translateErrMsg(msg);
		}

		if (StrUtils.isNotNullOrEmpty(msg)) {
			DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
			ObservableList<SheetFieldPo> fieldpols = ddlDmlpo.getFields();
			var row = ddlDmlpo.addRow();
			ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
					fieldpols.get(0));
			ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fieldpols.get(1));

			int endIdx = sql.length() > 100 ? 100 : sql.length();
			ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(
					"call procedure " + sql.subSequence(0, endIdx) + " ... "), fieldpols.get(2));
//			ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("" ),  fieldpols.get(3));

//			ObservableList<StringProperty> val = FXCollections.observableArrayList();
//			val.add(CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
//			val.add(CommonUtility.createReadOnlyStringProperty(msg)); 
//			int endIdx = sql.length() > 100 ? 100 : sql.length();
//			val.add(CommonUtility.createReadOnlyStringProperty("call procedure "+ sql.subSequence(0, endIdx) + " ... ")); 
//			val.add(CommonUtility.createReadOnlyStringProperty("" ));
//			ddlDmlpo.addData(val);
//			SqlExecuteOption.showExecuteSQLInfo(ddlDmlpo, thread);
			TableViewUtils.showInfo(ddlDmlpo);
		}
	}

	private static boolean hasOut(List<ProcedureFieldPo> fields) {
		if (fields != null && fields.size() > 0) {
			for (ProcedureFieldPo po : fields) {
				if (po.isOut()) {
					return true;
				}
			}
		}

		return false;
	}
}
