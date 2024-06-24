package net.tenie.fx.Action.sqlExecute;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.component.CacheDataTableViewShapeChange;
import net.tenie.Sqlucky.sdk.component.DataViewContainer;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheet;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SelectExecInfo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.ParseSQL;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;

import java.sql.Connection;
import java.util.List;

/**
 * select sql execute
 * 
 * @author tenie
 *
 */
public class SelectAction {

	private static Logger logger = LogManager.getLogger(SelectAction.class);


	public static void selectAction(String sql, SqluckyConnector sqluckyConn, int tidx, boolean isLock, Integer selectLimit, int type) throws Exception {
		MyBottomSheet myBottomSheet  = null;
		try {
			// 获取表名
			String tableName = ParseSQL.tabName(sql, ParseSQL.SELECT);
			myBottomSheet = new MyBottomSheet(tableName);

			SheetDataValue sheetDaV = myBottomSheet.getTableData();

			FilteredTableView<ResultSetRowPo> table = sheetDaV.getTable();

			if (StrUtils.isNullOrEmpty(tableName)) {
				tableName = "Table Name Not Finded";
			}
//			logger.info("tableName= " + tableName + "\n sql = " + sql);
			sheetDaV.setDbConnection(sqluckyConn);
			sheetDaV.setSqlStr(sql);
			sheetDaV.setTabName(tableName);
			sheetDaV.setLock(isLock);

			int limit = ConfigVal.MaxRows;
			if(selectLimit != null && selectLimit > 0){
				limit = selectLimit;
			}

			SelectExecInfo execInfo = SelectDao.selectSql2(sql, limit, sqluckyConn);

			sheetDaV.setSelectExecInfo(execInfo);
			// 设置行号显示宽度
			DataViewContainer.setTabRowWith(table, sheetDaV.getDataRs().getDatas().size());

			ObservableList<ResultSetRowPo> allRawData = sheetDaV.getDataRs().getDatas();
			ObservableList<SheetFieldPo> colss = sheetDaV.getColss();


			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true));

			// 根据表名获取tablepo对象 
			Connection conn = sqluckyConn.getConn();
			List<String> keys = SqluckyAppDB.findPrimaryKeys(conn, tableName);
			// table 添加列和数据
			// 表格添加列
			var tableColumns = SqluckyAppDB.createTableColForSqlData(colss, keys, sheetDaV);
			// 设置 列的 右键菜单
			SqluckyAppDB.setDataTableContextMenu(myBottomSheet, tableColumns, colss);
			table.getColumns().addAll(tableColumns);
			table.setItems(allRawData);
			// 表格选中事件, 对表格中的字段添加修改监听
			table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
				//
				if (newValue != null) {
					newValue.cellAddChangeListener();
				}
			});

			// 列顺序重排
			CacheDataTableViewShapeChange.colReorder(sheetDaV.getTabName(), colss, table);
			// 渲染界面
			if (! Thread.currentThread().isInterrupted()) {
//			if (thread != null && !thread.isInterrupted()) {
//				SqluckyBottomSheet mtd = ComponentGetter.appComponent.sqlDataSheet(myBottomSheet, sheetDaV, tidx,
//						false);
//				TableViewUtils.rmWaitingPane(isRefresh);
				String tableNameTmp = tableName;
				myBottomSheet.showSelectData(tidx, false);
				CommonUtils.runThread(()->{
					// 设置表格的外形(根据缓存)
					// 给表的字段赋值注解
					sqluckyConn.getExportDDL().setTableFieldComment(sqluckyConn.getConn(),
							sqluckyConn.getDefaultSchema(),
							tableNameTmp, colss);
					// 设置: 水平滚顶条位置设置, 字段类型, 主表的字段名称
					CacheDataTableViewShapeChange.setDataTableViewShapeCache(sheetDaV.getTabName(),
							sheetDaV.getTable(),
							colss);

				});

			}
		} catch (Exception e) {
			if(myBottomSheet!= null){
				myBottomSheet.clean();
			}
			e.printStackTrace();
			throw e;
		}
	}
	


}
