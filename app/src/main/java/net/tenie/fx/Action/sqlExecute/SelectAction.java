package net.tenie.fx.Action.sqlExecute;

import java.sql.Connection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.component.CacheDataTableViewShapeChange;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.utility.ParseSQL;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.component.container.DataViewContainer;
import net.tenie.fx.config.DBConns;


/**
 * select sql execute
 * @author tenie
 *
 */
public class SelectAction {

	private static Logger logger = LogManager.getLogger(SelectAction.class);
//	private static Thread staticThread;
	
	public static void selectAction(String sql, SqluckyConnector dpo , int tidx, boolean isLock, Thread thread, boolean isRefresh) throws Exception {
		try { 
//			staticThread = thread;
		    Connection conn = dpo.getConn();
			FilteredTableView<ObservableList<StringProperty>> table = SdkComponent.creatFilteredTableView();
			
		    // 获取表名
			String tableName = ParseSQL.tabName(sql);
			if(StrUtils.isNullOrEmpty(tableName)) {
				tableName = "Table Name Not Finded";
			}
			logger.info("tableName= " + tableName + "\n sql = " + sql);
			SheetDataValue sheetDaV = new SheetDataValue();
			sheetDaV.setDbConnection(dpo); 
			String connectName = DBConns.getCurrentConnectName();
			sheetDaV.setSqlStr(sql);
			sheetDaV.setTable(table);
			sheetDaV.setTabName(tableName);
			sheetDaV.setConnName(connectName);
			sheetDaV.setLock(isLock);

			SelectDao.selectSql(sql, ConfigVal.MaxRows, sheetDaV); 
			DataViewContainer.setTabRowWith(table, sheetDaV.getRawData().size()); 
			
			ObservableList<ObservableList<StringProperty>> allRawData = sheetDaV.getRawData();
			ObservableList<SheetFieldPo> colss = sheetDaV.getColss();
			  
			//缓存
			sheetDaV.setTable(table);
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true)); 
			
			//根据表名获取tablepo对象
			List<String> keys = SqlExecuteOption.findPrimaryKeys(conn, tableName);
			// table 添加列和数据 
			// 表格添加列
			var tableColumns = SqlExecuteOption.createTableColForSqlData( colss, keys , sheetDaV); 
			// 设置 列的 右键菜单
			SqlExecuteOption.setDataTableContextMenu(tableColumns, colss);
			table.getColumns().addAll(tableColumns);
			table.setItems(allRawData);  

			// 列顺序重排
			CacheDataTableViewShapeChange.colReorder(sheetDaV.getTabName(), colss, table);
			// 渲染界面
			if (thread != null && !thread.isInterrupted()) {
				SqluckyBottomSheet mtd = ComponentGetter.appComponent.sqlDataSheet(sheetDaV, tidx, false);
				SqlExecuteOption.rmWaitingPane( isRefresh);
				mtd.show();
				// 水平滚顶条位置设置和字段类型
				CacheDataTableViewShapeChange.setDataTableViewShapeCache(sheetDaV.getTabName(), sheetDaV.getTable(), colss); 		
			}
		} catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	
}
