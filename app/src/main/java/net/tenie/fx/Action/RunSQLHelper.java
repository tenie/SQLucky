package net.tenie.fx.Action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
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
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqlcukyEditor;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.ProcedureFieldPo;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;
import net.tenie.Sqlucky.sdk.po.TablePrimaryKeysPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Cache.CacheDataTableViewShapeChange;
import net.tenie.fx.PropertyPo.SqlData;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.CommonButtons;
import net.tenie.fx.component.MyTextField2TableCell2;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemDbObjects;
import net.tenie.fx.component.container.DataViewContainer;
import net.tenie.fx.component.dataView.DataTableContextMenu;
import net.tenie.fx.component.dataView.MyTabData;
import net.tenie.fx.component.dataView.MyTabDataValue;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.DmlDdlDao;
import net.tenie.fx.dao.SelectDao;
import net.tenie.fx.factory.StringPropertyListValueFactory;
import net.tenie.fx.utility.ParseSQL;
import net.tenie.lib.tools.IconGenerator;

/**
 * 
 * @author tenie
 *
 */
public class RunSQLHelper {
	private static Logger logger = LogManager.getLogger(RunSQLHelper.class);
	private static Thread thread;
	private static JFXButton runbtn;
	private static JFXButton runLinebtn;
	private static JFXButton stopbtn;
	private static JFXButton otherbtn;
	private static final String WAITTB_NAME = "Loading...";
	
	private static Tab waitTb ;
	
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
	
	
   // 查询时等待画面
	public static Tab maskTab(String waittbName) {
		Tab waitTb = new Tab(waittbName);
		MaskerPane masker = new MaskerPane();
		waitTb.setContent(masker);
		return waitTb;
	}
	static {
		 waitTb = maskTab(WAITTB_NAME);
	}
	
	
	@SuppressWarnings("restriction")
	private static void runMain(String s) {	 
		if (StrUtils.isNotNullOrEmpty(tabIdx)) {
			tidx = Integer.valueOf(tabIdx);
		}else {
			tidx = -1;
		}
		
		// 等待加载动画
		addWaitingPane( tidx);
		List<SqlData> allsqls = new ArrayList<>();
		try {
			// 获取sql 语句 
			//执行创建存储过程函数, 触发器等
			if( isCreateFunc ) { 
				if (StrUtils.isNotNullOrEmpty(sqlstr)) {
					SqlData sq = new SqlData(sqlstr, 0, sqlstr.length());
					allsqls.add(sq );
				}else {
					String str = SqlcukyEditor.getCurrentCodeAreaSQLText();
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
			execSqlList(allsqls, dpo);

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
	private static void execSqlList(List<SqlData> allsqls,  SqluckyConnector dpo) throws SQLException {
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
//				if(dpo.getDbVendor().toUpperCase().equals( DbVendor.db2.toUpperCase())) {
//					msg += "\n"+Db2ErrorCode.translateErrMsg(msg);
//				}
				msg += "\n"+dpo.translateErrMsg(msg);
				SqlData sd = 	allsqls.get(i);
				errObj.add(sd);
			}
			if(StrUtils.isNotNullOrEmpty(msg)) {
				// 如果只有一行ddl执行
				if(sqllenght == 1  && !msg.startsWith("failed :")) {
					CommonAction.showNotifiaction(msg);
//					rmWaitingPane();
					rmWaitingPaneHold();
				}else {
					// 显示字段是只读的
					ObservableList<StringProperty> val = FXCollections.observableArrayList();
					val.add(createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
					val.add(createReadOnlyStringProperty(msg)); 
					int endIdx = sqlstr.length() > 100 ? 100 : sqlstr.length();
					val.add(createReadOnlyStringProperty(sqlstr.substring(0, endIdx) + " ... ")); 
					val.add(createReadOnlyStringProperty("" + i));
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
						SqlcukyEditor.ErrorHighlighting(bg, sd.sql);
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
			MyTabDataValue dvt = new MyTabDataValue(table , ConfigVal.EXEC_INFO_TITLE,  colss, alldata); 
			
			var cols = createTableCol( colss, new ArrayList<String>(), true, dvt);
			table.getColumns().addAll(cols);
			table.setItems(alldata); 

			 
			// 渲染界面
			if (!thread.isInterrupted()) {
				MyTabData mtd = MyTabData.dtTab(dvt, -1, true);
				rmWaitingPane();
				mtd.show();
			
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
			FilteredTableView<ObservableList<StringProperty>> table = DataViewContainer.creatFilteredTableView();
			// 获取表名
			String tableName = sql; 
//			ParseSQL.tabName(sql);
			
			logger.info("tableName= " + tableName + "\n sql = " + sql);
			MyTabDataValue dvt = new MyTabDataValue();
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
			ObservableList<SqlFieldPo> colss = dvt.getColss();
			  
			//缓存
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true)); 
			
			//根据表名获取tablepo对象
			List<String> keys = findPrimaryKeys(conn, tableName);
			// table 添加列和数据 
			// 表格添加列
			var ls = createTableCol( colss, keys, false , dvt);
			table.getColumns().addAll(ls);
			table.setItems(allRawData);   
			// 渲染界面
			if (!thread.isInterrupted()) {
				if(hasOut(fields)) {
					MyTabData mtd = MyTabData.dtTab(dvt, tidx, true);
					rmWaitingPane();
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
			val.add(createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
			val.add(createReadOnlyStringProperty(msg)); 
			int endIdx = sqlstr.length() > 100 ? 100 : sqlstr.length();
			val.add(createReadOnlyStringProperty("call procedure "+ sqlstr.subSequence(0, endIdx) + " ... ")); 
			val.add(createReadOnlyStringProperty("" ));
			ddlDmlpo.addData(val);
			showExecuteSQLInfo(ddlDmlpo);
		}
	}
	
	
	private static void selectAction(String sql, SqluckyConnector dpo ) throws Exception {
		try { 
		    Connection conn = dpo.getConn();
			FilteredTableView<ObservableList<StringProperty>> table = DataViewContainer.creatFilteredTableView();
			
		    // 获取表名
			String tableName = ParseSQL.tabName(sql);
			if(StrUtils.isNullOrEmpty(tableName)) {
				tableName = "Table Name Not Finded";
			}
			logger.info("tableName= " + tableName + "\n sql = " + sql);
			MyTabDataValue dvt = new MyTabDataValue();
			dvt.setDbConnection(dpo); 
			String connectName = DBConns.getCurrentConnectName();
			dvt.setSqlStr(sql);
			dvt.setTable(table);
			dvt.setTabName(tableName);
			dvt.setConnName(connectName);
			dvt.setLock(isLock);

			SelectDao.selectSql(sql, ConfigVal.MaxRows, dvt); 
			DataViewContainer.setTabRowWith(table, dvt.getRawData().size()); 
			
			ObservableList<ObservableList<StringProperty>> allRawData = dvt.getRawData();
			ObservableList<SqlFieldPo> colss = dvt.getColss();
			  
			//缓存
			dvt.setTable(table);
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
				MyTabData mtd = MyTabData.dtTab(dvt, tidx, false);
				rmWaitingPane();
				mtd.show();
				// 水平滚顶条位置设置和字段类型
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
			MyAlert.errorAlert( "please , choose alive DB connection!");
			return warn;
		} 
		String val = conns.getValue().getText();
		SqluckyConnector po = DBConns.get(val);
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
	public static void runSQLMethodRefresh(SqluckyConnector dpov , String sqlv, String tabIdxv, boolean isLockv ) {
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
		SqluckyConnector dpov = CommonAction.getDbConnectionPoByComboBoxDbConnName();
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
	private static ObservableList<FilteredTableColumn<ObservableList<StringProperty>, String>> createTableCol( ObservableList<SqlFieldPo> cols,  List<String> keys , boolean isInfo, MyTabDataValue dvt) {
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
			int colIdx,  boolean augmentation, boolean iskey, boolean isInfo, MyTabDataValue dvt ) {
		FilteredTableColumn<ObservableList<StringProperty>, String> col =
				new FilteredTableColumn<ObservableList<StringProperty>, String>();
		col.setCellFactory(MyTextField2TableCell2.forTableColumn());
//		col.setCellFactory(TextField2TableCell.forTableColumn());
//		col.setEditable(true);
		col.setText(colname);
		Label label  = new Label(); 
		if(iskey) {// #F0F0F0    1C92FB ##6EB842  #7CFC00
			label.setGraphic(IconGenerator.svgImage("material-vpn-key", 10, "#FF6600")); 
		}else {
//			label.setGraphic(IconGenerator.svgImage("sort", 10, "blue" , false)); 
		}
		col.setGraphic(label);
				
		String tableName = dvt.getTabName();
		//设置列宽
		CacheDataTableViewShapeChange.setColWidth(col, tableName, colname, augmentation); 
		
		
		col.setCellValueFactory(new StringPropertyListValueFactory(colIdx));
		List<MenuItem> menuList = new ArrayList<>();
		// 右点菜单
		if(! isInfo) { 
			ContextMenu cm = DataTableContextMenu.DataTableColumnContextMenu(colname, type, col, colIdx , menuList);
			col.setContextMenu(cm);
		}
		dvt.getMenuItems().addAll(menuList); 		
		
		return col;
	}

	// 删除空白页, 保留锁定页
	private static void deleteEmptyTab(TabPane dataTab) {
		if(isRefresh) return;
		// 判断是否已经到达最大tab显示页面
		// 删除旧的 tab
		List<Tab> ls = new ArrayList<>();
		for(int i = 0; i < dataTab.getTabs().size() ;i++) { 
			MyTabData nd = (MyTabData) dataTab.getTabs().get(i);
//			String idVal = nd.getId();
//			Boolean tf = ButtonFactory.lockObj.get(idVal);
			Boolean tf = nd.getTableData().isLock();
			if(tf != null && tf) {
				logger.info("lock  "  );
			}else {
				ls.add(nd);
			}
		}
		ls.forEach(nd->{
			String idVal = nd.getId();
			dataTab.getTabs().remove(nd);
//			CacheTabView.clear(idVal); //TODO clear?
			
		});
	}
	
	// 等待加载动画 页面, 删除不要的页面, 保留 锁定的页面
	private static Tab addWaitingPane( int tabIdx) {
//		Tab waitTb = MyTabData.maskTab(WAITTB_NAME);
		Platform.runLater(() -> {
			TabPane dataTab = ComponentGetter.dataTabPane;
			// 删除不要的tab, 保留 锁定的tab
//			deleteEmptyTab(dataTab);
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
	private static void rmWaitingPane() {
		Platform.runLater(() -> {
			TabPane dataTab = ComponentGetter.dataTabPane;
			if( dataTab.getTabs().contains(waitTb) ) {
				dataTab.getTabs().remove(waitTb);
			}
			if(dataTab.getTabs().size() == 0) {
				CommonAction.hideBottom();
			}
			deleteEmptyTab(dataTab);
			
		});

	}
	// 移除 等待加载动画 页面, 不删除任何界面
	private static void rmWaitingPaneHold() {
		Platform.runLater(() -> {
			TabPane dataTab = ComponentGetter.dataTabPane;
			if( dataTab.getTabs().contains(waitTb) ) {
				dataTab.getTabs().remove(waitTb);
			}
			if(dataTab.getTabs().size() == 0) {
				CommonAction.hideBottom();
			}
			
			
		});

	}
	
	private static List<SqlData> epurateSql(String str) {
		List<SqlData> sds = new ArrayList<>();
		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		List<String> sqls = SqlcukyEditor.findSQLFromTxt(str);
		
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
		List<String> sqls = SqlcukyEditor.findSQLFromTxt(str);
		
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
		CodeArea code = SqlcukyEditor.getCodeArea();
		// 如果是执行当前行
		if(isCurrentLine) {
			try {
				str = SqlcukyEditor.getCurrentLineText();
			} finally {
				isCurrentLine = false;
			} 			
		}else {
			str = SqlcukyEditor.getCurrentCodeAreaSQLSelectedText(); 
		}
		  
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
			
		
}
 

