package net.tenie.fx.Action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.fxmisc.richtext.CodeArea;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.component.CacheDataTableViewShapeChange;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.component.SqluckyEditor;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.ProcedureFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.TablePrimaryKeysPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.ParseSQL;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Po.SqlData;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemDbObjects;
import net.tenie.fx.component.container.DataViewContainer;
import net.tenie.fx.component.dataView.DataTableContextMenu;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.DmlDdlDao;

/**
 * 
 * @author tenie
 *
 */
public class RunSQLHelper {
	
	// 执行状态, 失败赋值为0， 成功为1
	public static Map<Long, Integer> RUN_STATUS = new HashMap<>();
//	RUN_STATUS = -1;
	
	private static Logger logger = LogManager.getLogger(RunSQLHelper.class);
	private static Thread thread;
	private static JFXButton runbtn;
	private static JFXButton runLinebtn;
	private static JFXButton stopbtn;
	private static JFXButton otherbtn;

	
	
	
	ExecutorService service = Executors.newFixedThreadPool(1);
	// 新tab页插入的位置
	private static int tidx = -1;
	
	// 参数
	private static String sqlstr = null; 
	private static String tabIdx = null;
	private static Boolean isCreateFunc = null;
	private static SqluckyConnector dpo = null;
	private static Boolean isRefresh = false;
	private static boolean isLock =false;
	private static boolean isCallFunc = false;
	private static List<ProcedureFieldPo> callProcedureFields = null;
	
	private static boolean isCurrentLine = false; 
	
	// 获取执行状态，成功或失败
	public static Integer runStatus(Long key) {
		Integer val = RUN_STATUS.get(key);
		if(val !=null) {
			if(val == 1 || val == 0) {
				RUN_STATUS.remove(key);
			}
		}
		return val;
	}
	
  
	
	
	
	@SuppressWarnings("restriction")
	private static void runMain(Long statusKey) {	 
		if (StrUtils.isNotNullOrEmpty(tabIdx)) {
			tidx = Integer.valueOf(tabIdx);
		}else {
			tidx = -1;
		}
		
		// 等待加载动画
		addWaitingPane( tidx, isRefresh);
		List<SqlData> allsqls = new ArrayList<>();
		try {
			// 获取sql 语句 
			//执行创建存储过程函数, 触发器等
			if( isCreateFunc ) { 
				if (StrUtils.isNotNullOrEmpty(sqlstr)) {
					SqlData sq = new SqlData(sqlstr, 0, sqlstr.length());
					allsqls.add(sq );
				}else {
					String str = SqluckyEditor.getCurrentCodeAreaSQLText();
					SqlData sq = new SqlData(str, 0, str.length());
					allsqls.add(sq);
				}
			// 执行传入的sql, 非界面上的sql
			}else if (StrUtils.isNotNullOrEmpty(sqlstr)) { // 执行指定sql
				allsqls = epurateSql(sqlstr);
			} else { 
				// 获取将要执行的sql 语句 , 如果有选中就获取选中的sql
				allsqls = willExecSql();
			}
			// 执行sql
			var rsVal = execSqlList(allsqls, dpo);
			
			// 执行sql的状态保存
			if(statusKey != null) RUN_STATUS.put(statusKey, rsVal);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			rmWaitingPane();
			settingBtn();
			callProcedureFields = null;
			isCallFunc =false;
		}
		
	}

	// 执行查询sql 并拼装成一个表, 多个sql生成多个表
	private static Integer execSqlList(List<SqlData> allsqls,  SqluckyConnector dpo) throws SQLException {
		Integer rsVal = 1;
		String sqlstr;
		String sql;
		Connection conn = dpo.getConn();
		int sqllenght = allsqls.size();
		DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
		List<SqlData> errObj = new ArrayList<>();
		
		for (int i = 0; i < sqllenght; i++) {
			sqlstr = allsqls.get(i).sql; 
			sql = StrUtils.trimComment(sqlstr, "--");
			int type = ParseSQL.parseType(sql);
			String msg = "";
			try {
				if( isCallFunc ) { // 调用存储过程  
					procedureAction(sql, dpo ,  callProcedureFields);
				}else if (type == ParseSQL.SELECT) { // 调用查询
					  selectAction(sql, dpo); 
				} else {
						if (type == ParseSQL.UPDATE) {
							msg = DmlDdlDao.updateSql2(conn, sql);
							logger.info("add update sql: " + sql);
						} else if (type == ParseSQL.INSERT) {
							msg = DmlDdlDao.insertSql2(conn, sql);
							logger.info("add insert sql: " + sql);
						} else if (type == ParseSQL.DELETE) {
							msg = DmlDdlDao.deleteSql2(conn, sql);
							logger.info("add DELETE sql: " + sql);
						} else if (type == ParseSQL.DROP) {
							msg = DmlDdlDao.dropSql2(conn, sql);
							logger.info("add DROP sql: " + sql);
						} else if (type == ParseSQL.ALTER) {
							msg = DmlDdlDao.alterSql2(conn, sql);
							logger.info("add ALTER sql: " + sql);
						} else if (type == ParseSQL.CREATE) {
							msg = DmlDdlDao.createSql2(conn, sql);
							logger.info("add CREATE sql: " + sql);
						} else {
							msg = DmlDdlDao.otherSql2(conn, sql);
							logger.info("add OTEHR sql: " + sql);
						}
						
				}
			} catch (Exception e) {
				msg = "failed : " + e.getMessage();
//				if(dpo.getDbVendor().toUpperCase().equals( DbVendor.db2.toUpperCase())) {
//					msg += "\n"+Db2ErrorCode.translateErrMsg(msg);
//				}
				msg += "\n"+dpo.translateErrMsg(msg);
				SqlData sd = 	allsqls.get(i);
				errObj.add(sd);
				rsVal = 0;
			}
			if(StrUtils.isNotNullOrEmpty(msg)) {
				// 如果只有一行ddl执行
				if(sqllenght == 1  && !msg.startsWith("failed")) {
					final String msgVal = msg; 
					Platform.runLater(()->{
						CommonAction.showNotifiaction(msgVal);
						rmWaitingPane(true);
//						rmWaitingPaneHold();
					});
					
				}else {
					// 显示字段是只读的
					ObservableList<StringProperty> val = FXCollections.observableArrayList();
					val.add(CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
					val.add(CommonUtility.createReadOnlyStringProperty(msg)); 
					int endIdx = sqlstr.length() > 100 ? 100 : sqlstr.length();
					val.add(CommonUtility.createReadOnlyStringProperty(sqlstr.substring(0, endIdx) + " ... ")); 
					val.add(CommonUtility.createReadOnlyStringProperty("" + i));
					ddlDmlpo.addData(val);

				}
			}

		}
		// 如果 ddlDmlpo 中有 msg的信息 就会显示到界面上
		showExecuteSQLInfo(ddlDmlpo);
		// 如果是执行的界面上的sql, 那么对错误的sql渲染为红色
		if (StrUtils.isNullOrEmpty(RunSQLHelper.sqlstr)) {
			Platform.runLater(() -> { 
				if (errObj.size() > 0) {
					for (SqlData sd : errObj) {
						int bg = sd.begin;
						SqluckyEditor.ErrorHighlighting(bg, sd.sql);
					}
				}
			});
		}
		return rsVal;
	}
	
	
	
	// 展示信息窗口,
	public static void showExecuteSQLInfo(DbTableDatePo ddlDmlpo) {
		// 有数据才展示
		if (ddlDmlpo.getAllDatas().size() > 0) {
			FilteredTableView<ObservableList<StringProperty>> table = SdkComponent.creatFilteredTableView();
			// 表内容可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true));
			DataViewContainer.setTabRowWith(table, ddlDmlpo.getAllDatasSize());
			// table 添加列和数据 
			ObservableList<SheetFieldPo> colss = ddlDmlpo.getFields();
			ObservableList<ObservableList<StringProperty>> alldata = ddlDmlpo.getAllDatas();
			SheetDataValue dvt = new SheetDataValue(table , ConfigVal.EXEC_INFO_TITLE,  colss, alldata); 
			
			var cols = SdkComponent.createTableColForInfo( colss);
			table.getColumns().addAll(cols);
			table.setItems(alldata); 

//			rmWaitingPaneHold();
			rmWaitingPane(true);
			// 渲染界面
			if (!thread.isInterrupted()) {
				boolean showtab = true;
				if(ddlDmlpo.getAllDatas().size() == 1) {
					var list = ddlDmlpo.getAllDatas().get(0);
					var strfield = list.get(1).get();
					if(!strfield.startsWith("failed")){
						CommonAction.showNotifiaction(strfield);
						showtab = false;
					}
				}
				if(showtab){
					SqluckyBottomSheet mtd = ComponentGetter.appComponent.sqlDataSheet(dvt, -1, true);
//					rmWaitingPane(); 
					
					mtd.show();
				}

			
			}

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

	private static void procedureAction(String sql, SqluckyConnector dpo, List<ProcedureFieldPo> fields) throws Exception {
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
			setDataTableContextMenu(ls, colss);
			table.getColumns().addAll(ls);
			table.setItems(allRawData);   
			// 渲染界面
			if (!thread.isInterrupted()) {
				if(hasOut(fields)) {
					SqluckyBottomSheet mtd = ComponentGetter.appComponent.sqlDataSheet(dvt, tidx, true);
					rmWaitingPane( isRefresh );
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
			int endIdx = sqlstr.length() > 100 ? 100 : sqlstr.length();
			val.add(CommonUtility.createReadOnlyStringProperty("call procedure "+ sqlstr.subSequence(0, endIdx) + " ... ")); 
			val.add(CommonUtility.createReadOnlyStringProperty("" ));
			ddlDmlpo.addData(val);
			showExecuteSQLInfo(ddlDmlpo);
		}
	}
	
	
	private static void selectAction(String sql, SqluckyConnector dpo ) throws Exception {
		try { 
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
			List<String> keys = findPrimaryKeys(conn, tableName);
			// table 添加列和数据 
			// 表格添加列
			var tableColumns = createTableColForSqlData( colss, keys , sheetDaV); 
			// 设置 列的 右键菜单
			setDataTableContextMenu(tableColumns, colss);
			table.getColumns().addAll(tableColumns);
			table.setItems(allRawData);  

			// 列顺序重排
			CacheDataTableViewShapeChange.colReorder(sheetDaV.getTabName(), colss, table);
			// 渲染界面
			if (!thread.isInterrupted()) {
				SqluckyBottomSheet mtd = ComponentGetter.appComponent.sqlDataSheet(sheetDaV, tidx, false);
				rmWaitingPane( isRefresh);
				mtd.show();
				// 水平滚顶条位置设置和字段类型
				CacheDataTableViewShapeChange.setDataTableViewShapeCache(sheetDaV.getTabName(), sheetDaV.getTable(), colss); 		
			}
		} catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	
	
	// 根据表名获取表的主键字段名称集合
	private static List<String> findPrimaryKeys(Connection conn, String tableName ){
		
		String schemaName = "";
		List<String> keys = new ArrayList<>();
		String tempTableName = tableName;
		if(tableName.contains(".")) {
			String[] arrs = tableName.split("\\.");
			schemaName = arrs[0];
			tempTableName = arrs[1];
		}
		TreeNodePo tnp = DBinfoTree.getSchemaTableNodePo(schemaName);
		if(tnp != null && tnp.getConnItem() != null && tnp.getConnItem().getTableNode() !=null) {
			ConnItemDbObjects ci =tnp.getConnItem(); 
			ObservableList<TreeItem<TreeNodePo>>  tabs = ci.getTableNode().getChildren(); 
			for(TreeItem<TreeNodePo> node: tabs) {
				if(node.getValue().getName().toUpperCase().equals(tempTableName.toUpperCase())) { 
					keys = getKeys(conn, node);
				}
			}
		} 
		return keys;
	}
	
	private static List<String>  getKeys( Connection conn, TreeItem<TreeNodePo> node){
		List<String> keys = new ArrayList<>(); 
		try {
			ArrayList<TablePrimaryKeysPo> pks = node.getValue().getTable().getPrimaryKeys();
			if(pks == null || pks.size() == 0) {
				Dbinfo.fetchTablePrimaryKeys(conn, node.getValue().getTable()); 
			} 
			pks = node.getValue().getTable().getPrimaryKeys();
			if(pks !=null ) {
				for(TablePrimaryKeysPo kp : pks ) {
					keys.add(kp.getColumnName());
				} 
			} 
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		return keys;
	}
 
	// 在子线程执行 运行sql 的任务
	public static Thread createThread(Consumer<Long> action , Long statusKey) {
		return new Thread() {
			public void run() {
				logger.info("线程启动了" + this.getName()); 
				action.accept(statusKey);
				logger.info("线程结束了" + this.getName());
			}
		};
	}

	// 设置 按钮状态
	public static void settingBtn() { 
		if (runbtn == null) {
			runbtn =   CommonButtons.runbtn; //  AllButtons.btns.get("runbtn");
			otherbtn = CommonButtons.runFunPro; //  AllButtons.btns.get("runFunPro");
			runLinebtn = CommonButtons.runLinebtn; //  AllButtons.btns.get("runLinebtn");
		}
		if (stopbtn == null) {
			stopbtn = CommonButtons.stopbtn; // AllButtons.btns.get("stopbtn");
		}
		
		runbtn.setDisable(stopbtn.disabledProperty().getValue());
		otherbtn.setDisable(stopbtn.disabledProperty().getValue());
		runLinebtn.setDisable(stopbtn.disabledProperty().getValue());
		stopbtn.setDisable(!runbtn.disabledProperty().getValue());
		ComponentGetter.connComboBox.setDisable( runbtn.disabledProperty().getValue());
	}

	public static void settingBtn(JFXButton run, boolean runt, JFXButton stop, boolean stopt, JFXButton btn) {
		run.setDisable(runt);
		btn.setDisable(runt);
		stop.setDisable(stopt);
	} 

//    检查db连接状态
	private static boolean checkDBconn() {
		ComboBox<Label> conns = ComponentGetter.connComboBox;

		boolean warn = false;
//	    	logger.info(conns.getValue() );
		if (conns.getValue() == null || StrUtils.isNullOrEmpty(conns.getValue().getText())) {
			warn = true;
//			MyAlert.errorAlert( "please , choose alive DB connection!");
			MyAlert.notification("Error", "Please , choose alive DB connection!", MyAlert.NotificationType.Error);
			return warn;
		} 
		String val = conns.getValue().getText();
		SqluckyConnector po = DBConns.get(val);
		if (po == null || !po.isAlive()) {
			warn = true;
//			MyAlert.errorAlert( "please ,  connect DB !");
			MyAlert.notification("Error", "Please ,  connect DB !", MyAlert.NotificationType.Error);
			
		}
		return warn;
	}

 

	// 获取当前执行面板中的连接
	public static Connection getComboBoxDbConn() {
		String connboxVal = ComponentGetter.connComboBox.getValue().getText();
		if (StrUtils.isNullOrEmpty(connboxVal))
			return null;
		Connection conn = DBConns.get(connboxVal).getConn();
		return conn;
	}
	

	public static Long runSQLMethodRefresh(SqluckyConnector dpov , String sqlv, String tabIdxv, boolean isLockv ) {
		Long statusKey =  CommonUtility.dateTime();
		 
		RUN_STATUS.put(statusKey, -1);
		 
		settingBtn();
		SdkComponent.showDetailPane();
		
	    sqlstr = sqlv;
	    dpo = dpov; 
	    tabIdx = tabIdxv;
	    isCreateFunc = false;
	    isRefresh = true;
	    isLock = isLockv;
	    isCallFunc = false;
	    
		thread = createThread( RunSQLHelper::runMain, statusKey);
		thread.start();
		return statusKey;
	}


	// 运行 sql 入口
//	public static void runSQLMethodRefresh(SqluckyConnector dpov , String sqlv, String tabIdxv, boolean isLockv ) {
//		runSQLMethodRefreshByStatus(dpov, sqlv, tabIdxv, isLockv, null );
//	}
	//TODO runCurrentLineSQLMethod
	public static void runCurrentLineSQLMethod() {
		isCurrentLine = true;
		runSQLMethod(  null, null, false);
	}
	
	public static void runSQLMethod() {
		runSQLMethod(  null, null, false);
	}

	public static void runFuncSQLMethod( ) {
		runSQLMethod(  null, null, true);
	}
	
	public static void callProcedure( String sqlv, SqluckyConnector dpov , List<ProcedureFieldPo> fields ) {
		if (checkDBconn())
			return; 
		Connection connv = dpov.getConn();
		try {
			if (connv == null) {
				return;
			} else if (connv.isClosed()) {
//				MyAlert.errorAlert( "Connect is Closed!");
				MyAlert.notification("Error", "Connect is Closed!", MyAlert.NotificationType.Error);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		settingBtn();
		SdkComponent.showDetailPane();
		
	    sqlstr = sqlv;
	    dpo = dpov; 
	    tabIdx = null;
	    isCreateFunc = false;
	    isRefresh = false;
	    isLock = false;  
	    isCallFunc = true;
	    //TODO
	    callProcedureFields = fields;
	    
		thread = createThread( RunSQLHelper::runMain, null);
		thread.start();
	}
	
	public static void runSQLMethod( String sqlv, String tabIdxv, boolean isFuncv) {
		if (checkDBconn())
			return;
		SqluckyConnector dpov = CommonAction.getDbConnectionPoByComboBoxDbConnName();
		Connection connv = dpov.getConn();
		try {
			if (connv == null) {
				return;
			} else if (connv.isClosed()) {
//				MyAlert.errorAlert("Connect is Closed!" );
				MyAlert.notification("Error", "Connect is Closed!", MyAlert.NotificationType.Error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		settingBtn();
		SdkComponent.showDetailPane();
		
	    sqlstr = sqlv;
	    dpo = dpov; 
	    tabIdx = tabIdxv;
	    isCreateFunc = isFuncv;
	    isRefresh = false;
	    isLock = false; 
	    isCallFunc = false;
	    
		thread = createThread( RunSQLHelper::runMain, null);
		thread.start();
	}

 

	// stop 入口
	public static void stopSQLMethod() {
		if (thread != null && !stopbtn.disabledProperty().getValue()) { 
			thread.interrupt();
			logger.info("线程是否被中断：" + thread.isInterrupted());// true
			dpo.closeConn();
			dpo.getConn();
//			settingBtn(runbtn, true, stopbtn, false, otherbtn);
//		
//			if (thread.isInterrupted()) {
//				settingBtn(runbtn, true, stopbtn, false, otherbtn);
//			}else {
//				dpo.closeConn();
//				dpo.getConn();
//			}
		}
	}

	// table 添加列
	private static ObservableList<FilteredTableColumn<ObservableList<StringProperty>, String>> createTableColForSqlData( ObservableList<SheetFieldPo> cols,  List<String> keys  , SheetDataValue dvt) {
		int len = cols.size();
		ObservableList<FilteredTableColumn<ObservableList<StringProperty>, String>> colList = FXCollections.observableArrayList();
		for (int i = 0; i < len; i++) {
			String colname = cols.get(i).getColumnLabel().get();
			FilteredTableColumn<ObservableList<StringProperty>, String> col = null;
			 
			boolean iskey = false;
			if(keys != null) {
				if(keys.contains(colname)) {
					iskey = true;
				}
			}
			col = createColumnForSqlData(colname, i , iskey , dvt);
			 
			colList.add(col);
		}
		
		return colList;
	}
	// 设置 列的 右键菜单
	private static void setDataTableContextMenu(ObservableList<FilteredTableColumn<ObservableList<StringProperty>, String>>  colList , ObservableList<SheetFieldPo> cols) {
		int len = cols.size();
		for (int i = 0; i < len; i++) {
			FilteredTableColumn<ObservableList<StringProperty>, String> col = colList.get(i);
			String colname = cols.get(i).getColumnLabel().get();
			int type = cols.get(i).getColumnType().get();
			// 右点菜单
			ContextMenu cm = DataTableContextMenu.DataTableColumnContextMenu(colname, type, col, i );
			col.setContextMenu(cm);
		}
	}
	
	
	/** 创建列
	 */
	private static FilteredTableColumn<ObservableList<StringProperty>, String> createColumnForSqlData(
			String colname, int colIdx, boolean iskey, SheetDataValue dvt ) {
		FilteredTableColumn<ObservableList<StringProperty>, String> col = SdkComponent.createColumn(colname, colIdx);
		Label label  =  (Label) col.getGraphic() ;//new Label(); 
		if(iskey) {// #F0F0F0    1C92FB ##6EB842  #7CFC00
			label.setGraphic(IconGenerator.svgImage("material-vpn-key", 10, "#FF6600")); 
		} 
				
		String tableName = dvt.getTabName();
		//设置列宽
		CacheDataTableViewShapeChange.setColWidthByCache(col, tableName, colname); 
		return col;
	}
	

	


	
	// 等待加载动画 页面, 删除不要的页面, 保留 锁定的页面
//	private static Tab addWaitingPane( int tabIdx) {
//		Platform.runLater(() -> {
//			TabPane dataTab = ComponentGetter.dataTabPane;
//			if (tabIdx > -1) {
//				dataTab.getTabs().add(tabIdx, waitTb);
//			} else {
//				dataTab.getTabs().add(waitTb);
//			}
//			dataTab.getSelectionModel().select(waitTb);
//			
//		});
//	    return waitTb;
//	}

	
	
	private static List<SqlData> epurateSql(String str) {
		List<SqlData> sds = new ArrayList<>();
		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		List<String> sqls = SqluckyEditor.findSQLFromTxt(str);
		
		if(sqls.size()> 0) {
			for (String s : sqls) { 
				String trimSql = s.trim();
				if (trimSql.length() > 1) {
					SqlData sq = new SqlData(trimSql, 0, 0);
					sds.add(sq); 
				}
			}
		}else {
			SqlData sq = new SqlData(str, 0,0);
			sds.add(sq);
		}

		return sds;
	}

	// 将sql 字符串根据;分割成多个字符串 并计算其他信息
	private static List<SqlData> epurateSql(String str, int start) {
		List<SqlData> sds = new ArrayList<>();
		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		List<String> sqls = SqluckyEditor.findSQLFromTxt(str);
		
		if(sqls.size()> 0) {
			for (String s : sqls) { 
				String trimSql = s.trim();
				if (trimSql.length() > 1) {
					SqlData sq = new SqlData(s, start, s.length());
					sds.add(sq);
					start +=  s.length()+1; 
				}
			}
		}else {
			SqlData sq = new SqlData(str, start, str.length());
			sds.add(sq);
		}

		return sds;
	}
	
	/**
	 * 获取要执行的sql, 去除无效的(如-- 开头的)
	 */
	public static List<SqlData> willExecSql() {
		List<SqlData> sds = new ArrayList<>();
		String str = "";
		CodeArea code = SqluckyEditor.getCodeArea();
		// 如果是执行当前行
		if(isCurrentLine) {
			try {
				str = SqluckyEditor.getCurrentLineText();
			} finally {
				isCurrentLine = false;
			} 			
		}else {
			str = SqluckyEditor.getCurrentCodeAreaSQLSelectedText(); 
		}
		  
		int start = 0;
		if (str != null && str.length() > 0) {
		    start = code.getSelection().getStart();
		} else {
			str = SqluckyEditor.getCurrentCodeAreaSQLText();
		}
		// 去除注释, 包注释字符串转换为空白字符串
		str = SqluckyEditor.trimCommentToSpace(str, "--");
//		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		sds = epurateSql(str, start);
		return sds;
	}
	
	private static void rmWaitingPane(boolean holdSheet) {
		SdkComponent.rmWaitingPane();
		Platform.runLater(()->{
			if( holdSheet == false) { // 非刷新的， 删除多余的页
				TabPane dataTab = ComponentGetter.dataTabPane;
				SdkComponent.deleteEmptyTab(dataTab);
			}
		});
		
	}
	private static Tab addWaitingPane(int tabIdx, boolean holdSheet) {
		Tab v =	SdkComponent.addWaitingPane(tabIdx);
		Platform.runLater(()->{
			if( holdSheet == false) { // 非刷新的， 删除多余的页
				TabPane dataTab = ComponentGetter.dataTabPane;
				SdkComponent.deleteEmptyTab(dataTab);
			}
		});
		
		return v;
	}
		
}
 

