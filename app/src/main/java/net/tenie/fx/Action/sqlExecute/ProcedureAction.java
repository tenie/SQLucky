package net.tenie.fx.Action.sqlExecute;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.ProcedureFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.component.container.DataViewContainer;
import net.tenie.fx.config.DBConns;

public class ProcedureAction {

	private static Logger logger = LogManager.getLogger(ProcedureAction.class);
	private static Thread staticThread = null ;
	
	public static void procedureAction(String sql, SqluckyConnector dpo,
				List<ProcedureFieldPo> fields, int tidx, boolean isLock, 
				Thread thread, boolean isRefresh) throws Exception {
		staticThread = thread;
		String msg = "";
		Connection conn = dpo.getConn();
		DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
		try { 
			FilteredTableView<ObservableList<StringProperty>> table = SdkComponent.creatFilteredTableView();
			// 获取表名
			String tableName = sql; 
//			ParseSQL.tabName(sql);
			
			logger.info("tableName= " + tableName + "\n sql = " + sql);
			SheetDataValue dvt = new SheetDataValue();
			//TODO callProcedure
			SelectDao.callProcedure(conn, sql, table.getId(), dvt, fields );
			
			DataViewContainer.setTabRowWith(table, dvt.getRawData().size());
			
			String connectName = DBConns.getCurrentConnectName();
			dvt.setSqlStr(sql);
			dvt.setTable(table);
			dvt.setTabName(tableName);
			dvt.setConnName(connectName);
//			dvt.setDbconns(conn);
			dvt.setDbConnection(dpo);
			dvt.setLock(isLock);
			
			ObservableList<ObservableList<StringProperty>> allRawData = dvt.getRawData();
			ObservableList<SheetFieldPo> colss = dvt.getColss();
			  
			//缓存
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true)); 
			
			//根据表名获取tablepo对象
//			List<String> keys = findPrimaryKeys(conn, tableName);
			// table 添加列和数据 
			// 表格添加列
			var ls = SdkComponent.createTableColForInfo( colss);
			// 设置 列的 右键菜单
			SqlExecuteOption.setDataTableContextMenu(ls, colss);
			table.getColumns().addAll(ls);
			table.setItems(allRawData);   
			// 渲染界面
			if (staticThread != null && !staticThread.isInterrupted()) {
				if(hasOut(fields)) {
					SqluckyBottomSheet mtd = ComponentGetter.appComponent.sqlDataSheet(dvt, tidx, true);
					SqlExecuteOption.rmWaitingPane( isRefresh );
					mtd.show();
				
				}else {
					msg = "ok. ";
				}
			}
		} catch (Exception e) { 
			e.printStackTrace(); 
			msg = "failed : " + e.getMessage();
//			if(dpo.getDbVendor().toUpperCase().equals( DbVendor.db2.toUpperCase())) {
//				msg += "\n"+Db2ErrorCode.translateErrMsg(msg);
//			}  
			msg += "\n"+dpo.translateErrMsg(msg);
		}
		
		if(StrUtils.isNotNullOrEmpty(msg)) {
			ObservableList<StringProperty> val = FXCollections.observableArrayList();
			val.add(CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
			val.add(CommonUtility.createReadOnlyStringProperty(msg)); 
			int endIdx = sql.length() > 100 ? 100 : sql.length();
			val.add(CommonUtility.createReadOnlyStringProperty("call procedure "+ sql.subSequence(0, endIdx) + " ... ")); 
			val.add(CommonUtility.createReadOnlyStringProperty("" ));
			ddlDmlpo.addData(val);
			SqlExecuteOption.showExecuteSQLInfo(ddlDmlpo, thread);
		}
	}

	private static boolean hasOut(List<ProcedureFieldPo> fields) {
		if(fields != null && fields.size() > 0) {
			for(ProcedureFieldPo po : fields) {
				if(po.isOut()) {
					return true;
				}
			}
		}
		
		return false;
	}
}
