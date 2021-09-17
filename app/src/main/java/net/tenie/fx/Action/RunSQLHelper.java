package net.tenie.fx.Action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.cell.TextField2TableCell;
import org.fxmisc.richtext.CodeArea;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.ProcedureFieldPo;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Cache.CacheDataTableViewShapeChange;
import net.tenie.fx.Cache.CacheTabView;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.component.AllButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqlcukyEditor;
import net.tenie.fx.component.TreeItem.ConnItemDbObjects;
import net.tenie.fx.component.container.DBinfoTree;
import net.tenie.fx.component.container.DataViewContainer;
import net.tenie.fx.component.container.DataViewTab;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.TablePrimaryKeysPo;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.Db2ErrorCode;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.dao.DmlDdlDao;
import net.tenie.fx.dao.SelectDao;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.factory.MenuFactory;
import net.tenie.fx.factory.StringPropertyListValueFactory;
import net.tenie.fx.utility.ParseSQL;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.lib.db.Dbinfo;
import net.tenie.lib.tools.IconGenerator;


/*   @author tenie */
public class RunSQLHelper {
	private static Logger logger = LogManager.getLogger(RunSQLHelper.class);
	private static Thread thread;
	private static JFXButton runbtn;
	private static JFXButton stopbtn;
	private static JFXButton otherbtn;
	private static final String WAITTB_NAME = "Loading...";

	// 新tab页插入的位置
	private static int tidx = -1;
	
	// 参数
	private static String sqlstr = null; 
	private static String tabIdx = null;
	private static Boolean isCreateFunc = null;
	private static DbConnectionPo dpo = null;
	private static Boolean isRefresh = false;
	private static boolean isLock =false;
	private static boolean isCallFunc = false;
	private static List<ProcedureFieldPo> callProcedureFields = null;
	
	

	@SuppressWarnings("restriction")
	private static void runMain(String s) {	 
		if (StrUtils.isNotNullOrEmpty(tabIdx)) {
			tidx = Integer.valueOf(tabIdx);
		}else {
			tidx = -1;
		}
		
		// 等待加载动画
		Tab waitTb =  addWaitingPane( tidx);
		List<sqlData> allsqls = new ArrayList<>();
		try {
			// 获取sql 语句 
			//执行创建存储过程函数, 触发器等
			if( isCreateFunc ) { 
				if (StrUtils.isNotNullOrEmpty(sqlstr)) {
					sqlData sq = new sqlData(sqlstr, 0, sqlstr.length());
					allsqls.add(sq );
				}else {
					String str = SqlcukyEditor.getCurrentCodeAreaSQLText();
					sqlData sq = new sqlData(str, 0, str.length());
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
			execSqlList(allsqls, dpo);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rmWaitingPane(waitTb);
			settingBtn();
			callProcedureFields = null;
			isCallFunc =false;
		}
		
	}

	// 执行查询sql 并拼装成一个表, 多个sql生成多个表
	private static void execSqlList(List<sqlData> allsqls,  DbConnectionPo dpo) throws SQLException {
		String sqlstr;
		String sql;
		Connection conn = dpo.getConn();
		int sqllenght = allsqls.size();
		DbTableDatePo ddlDmlpo = DbTableDatePo.executeInfoPo();
		List<sqlData> errObj = new ArrayList<>();
		
		for (int i = 0; i < sqllenght; i++) { 
			sqlstr = allsqls.get(i).sql;
//			boolean isCallfunc = allsqls.get(i).isCallfunc;
			sql = StrUtils.trimComment(sqlstr, "--");
			int type = ParseSQL.parseType(sql);
			String msg = "";
			try {
				if( isCallFunc ) { // 调用存储过程
//					msg = DmlDdlDao.callFunc(conn, sql);
//TODO					
					procedureAction(sql, conn ,  callProcedureFields);
				}else if (type == ParseSQL.SELECT) {
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
				if(dpo.getDbVendor().toUpperCase().equals( DbVendor.db2.toUpperCase())) {
					msg += "\n"+Db2ErrorCode.translateErrMsg(msg);
				}
				sqlData sd = 	allsqls.get(i);
				errObj.add(sd);
			}
			if(StrUtils.isNotNullOrEmpty(msg)) {
				ObservableList<StringProperty> val = FXCollections.observableArrayList();
				val.add(createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
				val.add(createReadOnlyStringProperty(msg)); 
				val.add(createReadOnlyStringProperty(sqlstr)); 
				val.add(createReadOnlyStringProperty("" + i));
				ddlDmlpo.addData(val);
			}

		}
		showExecuteSQLInfo(ddlDmlpo);
		// 如果是执行的界面上的sql, 那么对错误的sql渲染为红色
		if (StrUtils.isNullOrEmpty(RunSQLHelper.sqlstr)) {
			Platform.runLater(() -> { 
				if (errObj.size() > 0) {
					for (sqlData sd : errObj) {
						int bg = sd.begin;
						int len = sd.sql.length();
						SqlcukyEditor.ErrorHighlighting(bg, len, sd.sql);
					}
				}
			});
		}
		
	}
	
	
	
	// 展示信息窗口,
	public static void showExecuteSQLInfo(DbTableDatePo ddlDmlpo) {
		// 有数据才展示
		if (ddlDmlpo.getAllDatas().size() > 0) {  
			FilteredTableView<ObservableList<StringProperty>> table = DataViewContainer.creatFilteredTableView();
			// 表内容可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true));
			DataViewContainer.setTabRowWith(table, ddlDmlpo.getAllDatasSize());
			// table 添加列和数据 
			ObservableList<SqlFieldPo> colss = ddlDmlpo.getFields();
			ObservableList<ObservableList<StringProperty>> alldata = ddlDmlpo.getAllDatas();
			DataViewTab dvt = new DataViewTab(table ,table.getId(), ConfigVal.EXEC_INFO_TITLE,  colss, alldata); 
			CacheTabView.addDataViewTab( dvt ,table.getId());
			
			var cols = createTableCol( colss, new ArrayList<String>(), true, dvt);
			table.getColumns().addAll(cols);
			table.setItems(alldata); 
//			table.scrollToColumnIndex(15); 
//			table.scrollTo(30);

			 
			// 渲染界面
			if (!thread.isInterrupted()) {
				DataViewContainer.showTableDate(dvt , "", ""); 				
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

	private static void procedureAction(String sql, Connection conn, List<ProcedureFieldPo> fields) throws Exception {
		String msg = "";
		DbTableDatePo ddlDmlpo = DbTableDatePo.executeInfoPo();
		try { 
			FilteredTableView<ObservableList<StringProperty>> table = DataViewContainer.creatFilteredTableView();
			// 获取表名
			String tableName = sql; 
//			ParseSQL.tabName(sql);
			
			logger.info("tableName= " + tableName + "\n sql = " + sql);
			DataViewTab dvt = new DataViewTab();
			//TODO callProcedure
			SelectDao.callProcedure(conn, sql, table.getId(), dvt, fields );
			
			DataViewContainer.setTabRowWith(table, dvt.getRawData().size());
			
			String connectName = DBConns.getCurrentConnectName();
			dvt.setSqlStr(sql);
			dvt.setTable(table);
			dvt.setTabId( table.getId());
			dvt.setTabName(tableName);
			dvt.setConnName(connectName);
			dvt.setDbconns(conn); 
			dvt.setLock(isLock);
			
			ObservableList<ObservableList<StringProperty>> allRawData = dvt.getRawData();
			ObservableList<SqlFieldPo> colss = dvt.getColss();
			  
			//缓存
			CacheTabView.addDataViewTab( dvt , table.getId());
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true)); 
			
			//根据表名获取tablepo对象
			List<String> keys = findPrimaryKeys(conn, tableName);
			// table 添加列和数据 
			// 表格添加列
			var ls = createTableCol( colss, keys, false , dvt);
			table.getColumns().addAll(ls);
			table.setItems(allRawData);   
//			tdpo.addTableView(table);
			// 渲染界面
			if (!thread.isInterrupted()) {
				if(hasOut(fields)) {
					DataViewContainer.showTableDate(dvt, tidx, true, dvt.getExecTime()+"", dvt.getRows()+"");
				}else {
					msg = "ok. ";
				}
			}
		} catch (Exception e) { 
			e.printStackTrace(); 
			msg = "failed : " + e.getMessage();
			if(dpo.getDbVendor().toUpperCase().equals( DbVendor.db2.toUpperCase())) {
				msg += "\n"+Db2ErrorCode.translateErrMsg(msg);
			}   
		}
		
		if(StrUtils.isNotNullOrEmpty(msg)) {
			ObservableList<StringProperty> val = FXCollections.observableArrayList();
			val.add(createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
			val.add(createReadOnlyStringProperty(msg)); 
			val.add(createReadOnlyStringProperty("call procedure "+ sqlstr)); 
			val.add(createReadOnlyStringProperty("" ));
			ddlDmlpo.addData(val);
			showExecuteSQLInfo(ddlDmlpo);
		}
	}
	
	
	private static void selectAction(String sql, DbConnectionPo dpo ) throws Exception {
		try { 
		    Connection conn = dpo.getConn();
			FilteredTableView<ObservableList<StringProperty>> table = DataViewContainer.creatFilteredTableView();
			// 获取表名
			String tableName = ParseSQL.tabName(sql);
			if(StrUtils.isNullOrEmpty(tableName)) {
				tableName = "Table Name Not Finded";
			}
			logger.info("tableName= " + tableName + "\n sql = " + sql);
			DataViewTab dvt = new DataViewTab();

			SelectDao.selectSql(dpo, sql, ConfigVal.MaxRows, table.getId(), dvt);
			
			DataViewContainer.setTabRowWith(table, dvt.getRawData().size()); //dpo.getAllDatasSize());
			
			String connectName = DBConns.getCurrentConnectName();
			dvt.setSqlStr(sql);
			dvt.setTable(table);
			dvt.setTabId( table.getId());
			dvt.setTabName(tableName);
			dvt.setConnName(connectName);
			dvt.setDbconns(conn);
			dvt.setDbConnection(dpo);
			dvt.setLock(isLock);
			
			ObservableList<ObservableList<StringProperty>> allRawData = dvt.getRawData();
			ObservableList<SqlFieldPo> colss = dvt.getColss();
			  
			//缓存
			CacheTabView.addDataViewTab( dvt , table.getId());
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true)); 
			
			//根据表名获取tablepo对象
			List<String> keys = findPrimaryKeys(conn, tableName);
			// table 添加列和数据 
			// 表格添加列
			var tableColumns = createTableCol( colss, keys, false , dvt);  
			table.getColumns().addAll(tableColumns);
			table.setItems(allRawData);  

			// 列顺序重排
			CacheDataTableViewShapeChange.colReorder(dvt.getTabName(), colss, table);
			// 渲染界面
			if (!thread.isInterrupted()) {
				DataViewContainer.showTableDate(dvt, tidx, false, dvt.getExecTime()+"", dvt.getRows()+"");			
				// 水平滚顶条位置设置
				CacheDataTableViewShapeChange.setDataTableViewShapeCache(dvt.getTabName(), dvt.getTable(), colss); 		
					
				 
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
	public static Thread createThread(  Consumer<String> action) {
		return new Thread() {
			public void run() {
				logger.info("线程启动了" + this.getName()); 
				action.accept("");
				logger.info("线程结束了" + this.getName());
			}
		};
	}

	// 设置 按钮状态
	public static void settingBtn() { 
		if (runbtn == null) {
			runbtn =   AllButtons.btns.get("runbtn");
			otherbtn = AllButtons.btns.get("runFunPro");
		}
		if (stopbtn == null) {
			stopbtn = AllButtons.btns.get("stopbtn");
		}
		
		runbtn.setDisable(stopbtn.disabledProperty().getValue());
		otherbtn.setDisable(stopbtn.disabledProperty().getValue());
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
			MyAlert.errorAlert( "please , choose alive DB connection!");
			return warn;
		} 
		String val = conns.getValue().getText();
		DbConnectionPo po = DBConns.get(val);
		if (po == null || !po.isAlive()) {
			warn = true;
			MyAlert.errorAlert( "please ,  connect DB !");
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



	// 运行 sql 入口
	public static void runSQLMethodRefresh(DbConnectionPo dpov , String sqlv, String tabIdxv, boolean isLockv ) {
		settingBtn();
		CommonAction.showDetailPane();
		
	    sqlstr = sqlv;
	    dpo = dpov; 
	    tabIdx = tabIdxv;
	    isCreateFunc = false;
	    isRefresh = true;
	    isLock = isLockv;
	    isCallFunc = false;
	    
		thread = createThread( RunSQLHelper::runMain);
		thread.start();
	}

	public static void runSQLMethod( ) {
		runSQLMethod(  null, null, false);
	}

	public static void runFuncSQLMethod( ) {
		runSQLMethod(  null, null, true);
	}
	
	public static void callProcedure( String sqlv, DbConnectionPo dpov , List<ProcedureFieldPo> fields ) {
		if (checkDBconn())
			return; 
		Connection connv = dpov.getConn();
		try {
			if (connv == null) {
				return;
			} else if (connv.isClosed()) {
				MyAlert.errorAlert( "Connect is Closed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		settingBtn();
		CommonAction.showDetailPane();
		
	    sqlstr = sqlv;
	    dpo = dpov; 
	    tabIdx = null;
	    isCreateFunc = false;
	    isRefresh = false;
	    isLock = false;  
	    isCallFunc = true;
	    //TODO
	    callProcedureFields = fields;
	    
		thread = createThread( RunSQLHelper::runMain);
		thread.start();
	}
	
	public static void runSQLMethod( String sqlv, String tabIdxv, boolean isFuncv) {
		if (checkDBconn())
			return;
		DbConnectionPo dpov = CommonAction.getDbConnectionPoByComboBoxDbConnName();
		Connection connv = dpov.getConn();
		try {
			if (connv == null) {
				return;
			} else if (connv.isClosed()) {
				MyAlert.errorAlert( "Connect is Closed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		settingBtn();
		CommonAction.showDetailPane();
		
	    sqlstr = sqlv;
	    dpo = dpov; 
	    tabIdx = tabIdxv;
	    isCreateFunc = isFuncv;
	    isRefresh = false;
	    isLock = false; 
	    isCallFunc = false;
	    
		thread = createThread( RunSQLHelper::runMain);
		thread.start();
	}

 

	// stop 入口
	public static void stopSQLMethod() {
		if (thread != null && !stopbtn.disabledProperty().getValue()) { 
			thread.interrupt();
			logger.info("线程是否被中断：" + thread.isInterrupted());// true
			if (thread.isInterrupted()) {
				settingBtn(runbtn, true, stopbtn, false, otherbtn);
			} 
		}
	}

	// table 添加列
	private static ObservableList<FilteredTableColumn<ObservableList<StringProperty>, String>> createTableCol( ObservableList<SqlFieldPo> cols,  List<String> keys , boolean isInfo, DataViewTab dvt) {
		int len = cols.size();
		ObservableList<FilteredTableColumn<ObservableList<StringProperty>, String>> colList = FXCollections.observableArrayList();
		for (int i = 0; i < len; i++) {
			String colname = cols.get(i).getColumnLabel().get();
			int type = cols.get(i).getColumnType().get();
			FilteredTableColumn<ObservableList<StringProperty>, String> col = null;
			if(isInfo) {
				col = createColumn(colname, type, i,  true , false , isInfo, dvt);
			}else {
				boolean iskey = false;
				if(keys.contains(colname)) {
					iskey = true;
				}
				if (len == 2 && i == 1) {
					col = createColumn(colname, type, i,  true , iskey , isInfo, dvt);
				} else {
					col = createColumn(colname, type, i,  false , iskey , isInfo, dvt);
				} 
			}
			
			colList.add(col);
		}
		
		return colList;
		
	}

	// 创建列
	/**
	 * @param colname
	 * @param type
	 * @param typeName
	 * @param colIdx
	 * @param augmentation
	 * @param iskey
	 * @param isInfo
	 * @param dvt
	 * @return
	 */
	private static FilteredTableColumn<ObservableList<StringProperty>, String> createColumn(String colname, int type,
			int colIdx,  boolean augmentation, boolean iskey, boolean isInfo, DataViewTab dvt ) {
		FilteredTableColumn<ObservableList<StringProperty>, String> col =
				new FilteredTableColumn<ObservableList<StringProperty>, String>();
//		col.setCellFactory(MyTextField2TableCell.forTableColumn());
		col.setCellFactory(TextField2TableCell.forTableColumn());
//		col.setEditable(true);
		col.setText(colname);
		Label label  = new Label(); 
		if(iskey) {
			label.setGraphic(IconGenerator.svgImage("material-vpn-key", 10, "#1C92FB")); 
		}else {
			label.setGraphic(IconGenerator.svgImage("sort", 10, "#1C92FB")); 
		}
		col.setGraphic(label);
				
		String tableName = dvt.getTabName();
		//设置列宽
		CacheDataTableViewShapeChange.setColWidth(col, tableName, colname, augmentation); 
		
		
		col.setCellValueFactory(new StringPropertyListValueFactory(colIdx));
		List<MenuItem> menuList = new ArrayList<>();
		// 右点菜单
		if(! isInfo) { 
			ContextMenu cm =MenuFactory.DataTableColumnContextMenu(colname, type, col, colIdx , menuList);
			col.setContextMenu(cm);
		}
		dvt.getMenuItems().addAll(menuList); 		
		
		return col;
	}

	// 删除空白页
	private static void deleteEmptyTab(TabPane dataTab) {
//		if (dataTab.getTabs() != null) {
//			dataTab.getTabs().removeIf(t -> dataTab.getTabs().size() > 0 && CommonUtility.tabText(t).equals(""));
//		}
		if(isRefresh) return;
		// 判断是否已经到达最大tab显示页面
		// 删除旧的 tab
		List<Tab> ls = new ArrayList<>();
		for(int i = 0; i < dataTab.getTabs().size() ;i++) {
			Tab nd = dataTab.getTabs().get(i);
			String idVal = nd.getId();
			Boolean tf = ButtonFactory.lockObj.get(idVal);
			if(tf != null && tf) {
				logger.info("lock idVal = " + idVal);
			}else {
				ls.add(nd);
			}
		}
		ls.forEach(nd->{
			String idVal = nd.getId();
			dataTab.getTabs().remove(nd);
			CacheTabView.clear(idVal); //TODO clear?
			
		});
	}

	// 等待加载动画 页面
	private static Tab addWaitingPane( int tabIdx) {
		Tab waitTb = new DataViewTab().maskTab(WAITTB_NAME);
		Platform.runLater(() -> {
			TabPane dataTab = ComponentGetter.dataTabPane;
			// 删除b不要的tab
			deleteEmptyTab(dataTab);
			

			if (tabIdx > -1) {
				dataTab.getTabs().add(tabIdx, waitTb);
			} else {
				dataTab.getTabs().add(waitTb);
			}
			dataTab.getSelectionModel().select(waitTb);
			
		});
	    return waitTb;
	}

	// 移除 等待加载动画 页面
	private static void rmWaitingPane(Tab waitTb) {
		Platform.runLater(() -> {
			TabPane dataTab = ComponentGetter.dataTabPane;
			dataTab.getTabs().remove(waitTb);
		});

	}
	
	private static List<sqlData> epurateSql(String str) {
		List<sqlData> sds = new ArrayList<>();
		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		List<String> sqls = SqlcukyEditor.findSQLFromTxt(str);
		
		if(sqls.size()> 0) {
			for (String s : sqls) { 
				String trimSql = s.trim();
				if (trimSql.length() > 1) {
					sqlData sq = new sqlData(trimSql, 0, 0);
					sds.add(sq); 
				}
			}
		}else {
			sqlData sq = new sqlData(str, 0,0);
			sds.add(sq);
		}

		return sds;
	}

	// 将sql 字符串根据;分割成多个字符串 并计算其他信息
	private static List<sqlData> epurateSql(String str, int start) {
		List<sqlData> sds = new ArrayList<>();
		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		List<String> sqls = SqlcukyEditor.findSQLFromTxt(str);
		
		if(sqls.size()> 0) {
			for (String s : sqls) { 
				String trimSql = s.trim();
				if (trimSql.length() > 1) {
					sqlData sq = new sqlData(s, start, s.length());
					sds.add(sq);
					start +=  s.length()+1; 
				}
			}
		}else {
			sqlData sq = new sqlData(str, start, str.length());
			sds.add(sq);
		}

		return sds;
	}
	
	/**
	 * 获取要执行的sql, 去除无效的(如-- 开头的)
	 */
	public static List<sqlData> willExecSql() {
		List<sqlData> sds = new ArrayList<>();
		
		CodeArea code = SqlcukyEditor.getCodeArea();
		String str = SqlcukyEditor.getCurrentCodeAreaSQLSelectedText(); 
		int start = 0;
		if (str != null && str.length() > 0) {
		    start = code.getSelection().getStart();
		} else {
			str = SqlcukyEditor.getCurrentCodeAreaSQLText();
		}
		// 去除注释, 包注释字符串转换为空白字符串
		str = SqlcukyEditor.trimCommentToSpace(str, "--");
//		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		sds = epurateSql(str, start);
		return sds;
	}
	// 字段值被修改还原, 不允许修改
			public static   StringProperty createReadOnlyStringProperty(String val ) {
				StringProperty sp =  new StringProperty() {
					@Override
					public String get() { 
						return val;
					}
					
					@Override
					public void bind(ObservableValue<? extends String> arg0) { }
					@Override
					public boolean isBound() { 
						return false;
					}
					@Override
					public void unbind() { }

					@Override
					public Object getBean() { 
						return null;
					}
					@Override
					public String getName() { 
						return null;
					} 
					@Override
					public void addListener(ChangeListener<? super String> arg0) { } 
					@Override
					public void removeListener(ChangeListener<? super String> arg0) { } 
					@Override
					public void addListener(InvalidationListener arg0) { }
					@Override
					public void removeListener(InvalidationListener arg0) { } 		
					@Override
					public void set(String arg0) {}  
				}; 		
				return sp;
			}
//		public static void main(String[] args) {
//			String express = "(\\([\\w\\s\\,_]+\\))";
//			String st1 = "CREATE OR REPLACE PROCEDURE \"INFODMS\".\"P_CANCEL_BOOKINGORDER\"(IN AENTITY_CODE CHARACTER(80) ,\r\n"
//					+ "                                               OUT RETURN_CODE INTEGER,\r\n"
//					+ "                                               OUT RETURN_MSG VARCHAR(2) )"
//		//			+ "adas()sadas\r\n"
//					+ "    LANGUAGE SQL";
//			String str2 = "111(dsd  sa)";
//		//	Matcher match = Pattern.compile(express).matcher(st1);
//		//	         
//		//	while (match.find()) {
//		//	    System.out.println(match.group(1));
//		//	}
//			
//			String val = CommonAction.firstParenthesisInsideString(st1);
//			  System.out.println(val);
//		}
			
		
}


class sqlData{
	String sql;
	int begin;
	int length;
	boolean isCallfunc = false;
	sqlData(String s, int i, int len){
		sql = s;
		begin = i;
		length = len;
	}

}

