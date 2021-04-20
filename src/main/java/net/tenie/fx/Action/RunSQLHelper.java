package net.tenie.fx.Action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.cell.TextField2TableCell;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;
import org.fxmisc.richtext.CodeArea;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
//import net.tenie.fx.PropertyPo.DataTabDataPo;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.PropertyPo.TablePrimaryKeysPo;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.PropertyPo.CacheTabView;
import net.tenie.fx.PropertyPo.DbConnectionPo;
//import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTextField2TableCell;
import net.tenie.fx.component.SqlCodeAreaHighLightingHelper;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.component.TreeItem.ConnItemDbObjects;
import net.tenie.fx.component.container.DataViewContainer;
import net.tenie.fx.component.container.DataViewTab;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.Db2ErrorCode;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.dao.DmlDdlDao;
import net.tenie.fx.dao.SelectDao;
import net.tenie.fx.factory.MenuFactory;
import net.tenie.fx.factory.StringPropertyListValueFactory;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.utility.MyPopupNumberFilter;
import net.tenie.fx.utility.ParseSQL;
import net.tenie.fx.window.ModalDialog;
import net.tenie.fx.window.MyAlert;
import net.tenie.lib.db.Dbinfo;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class RunSQLHelper {
	private static Logger logger = LogManager.getLogger(RunSQLHelper.class);
	private static Thread thread;
	private static JFXButton runbtn;
	private static JFXButton stopbtn;
	private static JFXButton otherbtn;
	private static final String WAITTB_NAME = "Loading...";
	private static String connName = "";
	// 新tab页插入的位置
	private static int tidx = -1;
	

	@SuppressWarnings("restriction")
	private static void runMain(Map<String, Object> val) { // throws Exception
		if (val == null)
			return;
		String sqlstr = (String) val.get("sql");
		Connection conn = (Connection) val.get("conn");
		String tabIdx = (String) val.get("tabIdx");
		String btn = (String)val.get("btn"); 
		DbConnectionPo dpo = (DbConnectionPo)val.get("dpo"); 
		 
		if (StrUtils.isNotNullOrEmpty(tabIdx)) {
			tidx = Integer.valueOf(tabIdx);
		}else {
			tidx = -1;
		}
		if (conn == null)
			return;
		// 等待加载动画
		Tab waitTb = new DataViewTab().maskTab(WAITTB_NAME);
		addWaitingPane(waitTb, tidx);
		List<sqlData> allsqls = new ArrayList<>();
		try {
			// 执行刷新数据的时候, (执行缓存的sql)
			if (StrUtils.isNotNullOrEmpty(sqlstr)) {
//				selectAction(sqlstr, conn); // 执行刷新查询sql
				allsqls = epurateSql(sqlstr);
			} else { 
				// 获取sql 语句 
				//执行存储过程函数等
				if(StrUtils.isNotNullOrEmpty(btn)) {  
					String str = SqlEditor.getCurrentCodeAreaSQLText();
					sqlData sq = new sqlData(str, 0, str.length());
					allsqls.add(sq);
				}else {
					// 获取编辑界面中的文本
					allsqls = willExecSql();
				}  
			}
			// 执行sql
			execSqlList(allsqls, conn, dpo);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rmWaitingPane(waitTb);
			settingBtn();
		}
		
	}

	// 执行查询sql 并拼装成一个表, 多个sql生成多个表
	private static void execSqlList(List<sqlData> allsqls, Connection conn, DbConnectionPo dpo) throws SQLException {
		String sqlstr;
		String sql;
		int sqllenght = allsqls.size();
		DbTableDatePo ddlDmlpo = DbTableDatePo.executeInfoPo();
		

		for (int i = 0; i < sqllenght; i++) { 
			sqlstr = allsqls.get(i).sql;
			sql = StrUtils.trimComment(sqlstr, "--");
			int type = ParseSQL.parseType(sql);
			String msg = "";
			try {
				if (type == ParseSQL.SELECT) {
					  selectAction(sql, conn); 
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
				int bg = allsqls.get(i).begin;
				int len =  allsqls.get(i).length;
				Platform.runLater(() -> {  
					SqlCodeAreaHighLightingHelper.applyErrorHighlighting( bg, len);
				});
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
			
			
			// 渲染界面
			if (!thread.isInterrupted())
				DataViewContainer.showTableDate(dvt , "", "");

		}
	}
 

	private static void selectAction(String sql, Connection conn) throws Exception {
		try {
//			DataTabDataPo tdpo = new DataTabDataPo();
			FilteredTableView<ObservableList<StringProperty>> table = DataViewContainer.creatFilteredTableView();
			// 获取表名
			String tableName = ParseSQL.tabName(sql);
			logger.info("tableName= " + tableName + "\n sql = " + sql);
//			tdpo.addTableName(tableName);
			DataViewTab dvt = new DataViewTab();
//			DbTableDatePo dpo = 
			SelectDao.selectSql(conn, sql, ConfigVal.MaxRows, table.getId(), dvt);
			
			DataViewContainer.setTabRowWith(table, dvt.getRawData().size()); //dpo.getAllDatasSize());
			
			//
			String connectName = ComponentGetter.getCurrentConnectName();
			dvt.setSqlStr(sql);
			dvt.setTable(table);
			dvt.setTabId( table.getId());
			dvt.setTabName(tableName);
			dvt.setConnName(connectName);
			dvt.setDbconns(conn); 
			
			ObservableList<ObservableList<StringProperty>> allRawData = dvt.getRawData();
			ObservableList<SqlFieldPo> colss = dvt.getColss();
			
//			DataViewTab dvt = new DataViewTab(dpo, table ,table.getId(), tableName,
//										      sql, conn, connectName, colss, rs ); 
			//缓存
			CacheTabView.addDataViewTab( dvt ,table.getId());
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
			if (!thread.isInterrupted())
				DataViewContainer.showTableDate(dvt, tidx, false, dvt.getExecTime()+"", dvt.getRows()+"");
//			dpo.clean();
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
		TreeNodePo tnp = ComponentGetter.getSchemaTableNodePo(schemaName);
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
	public static Thread createThread(DbConnectionPo dpo, Connection conn, String sql, String tabIdx, JFXButton run,
			Consumer<Map<String, Object>> action) {
		connName = ComponentGetter.getCurrentConnectName();
		return new Thread() {
			public void run() {
				logger.info("线程启动了" + this.getName());
				Map<String, Object> val = new HashMap<>();
				val.put("sql", sql);
				val.put("dpo", dpo);
				val.put("conn", conn);
				val.put("tabIdx", tabIdx);
				val.put("btn", run.getId());
				action.accept(val);
				logger.info("线程结束了" + this.getName());
				logger.info(run.getId());
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

	// 运行sql 事件
	public static EventHandler<Event> runSQL(JFXButton run, JFXButton stop, JFXButton btn) {
		return e -> {
			runSQLMethod(run, stop, btn);
		};

	}

	// 获取当前执行面板中的连接
	public static Connection getComboBoxDbConn() {
		String connboxVal = ComponentGetter.connComboBox.getValue().getText();
		if (StrUtils.isNullOrEmpty(connboxVal))
			return null;
		Connection conn = DBConns.get(connboxVal).getConn();
		return conn;
	}

	public static String getComboBoxDbConnName() {
		String connboxVal = ComponentGetter.connComboBox.getValue().getText();
		return connboxVal;
	}

	// 运行 sql 入口
	public static void runSQLMethod(DbConnectionPo dpo, Connection conn, String sql, String tabIdx, JFXButton run) {
		settingBtn();
		CommonAction.showDetailPane();
		thread = createThread(dpo, conn, sql, tabIdx, run, RunSQLHelper::runMain);
		thread.start();
	}

	public static void runSQLMethod(JFXButton run, JFXButton stop, JFXButton btn) {
		runSQLMethod(run, stop, btn, null, null);
	}

	public static void runSQLMethod(JFXButton run, JFXButton stop, JFXButton btn, String sql, String tabIdx) {
		if (checkDBconn())
			return;
		DbConnectionPo dpo = DBConns.get(getComboBoxDbConnName());
		Connection conn = dpo.getConn();
		try {
			if (conn == null) {
				return;
			} else if (conn.isClosed()) {
				MyAlert.errorAlert( "Connect is Closed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		settingBtn();
		CommonAction.showDetailPane();
		
		thread = createThread( dpo, conn, sql, tabIdx, run, RunSQLHelper::runMain);
		thread.start();
	}

	// stop 事件
	public static EventHandler<Event> stopSQL(JFXButton run, JFXButton stop, JFXButton btn) {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				stopSQLMethod(run, stop, btn);
			}
		};
	}

	// stop 入口
	public static void stopSQLMethod(JFXButton run, JFXButton stop, JFXButton btn) {
		if (thread != null && !stop.disabledProperty().getValue()) { 
			thread.interrupt();
			logger.info("线程是否被中断：" + thread.isInterrupted());// true
			if (thread.isInterrupted()) {
				settingBtn(run, true, stop, false, btn);
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
			String tyNa = cols.get(i).getColumnTypeName().get() + "(" + cols.get(i).getColumnDisplaySize().get();
			if (cols.get(i).getScale() != null && cols.get(i).getScale().get() > 0) {
				tyNa += ", " + cols.get(i).getScale().get();
			}
			tyNa += ")"; 
			if(isInfo) {
				col = createColumn(colname, type, tyNa, i,  true , false , isInfo, dvt);
			}else {
				boolean iskey = false;
				if(keys.contains(colname)) {
					iskey = true;
				}
				if (len == 2 && i == 1) {
					col = createColumn(colname, type, tyNa, i,  true , iskey , isInfo, dvt);
				} else {
					col = createColumn(colname, type, tyNa, i,  false , iskey , isInfo, dvt);
				} 
			}
			
			colList.add(col);
		}
		
		return colList;
		
	}

	// 创建列
	private static FilteredTableColumn<ObservableList<StringProperty>, String> createColumn(String colname, int type, String typeName,
			int colIdx,  boolean augmentation, boolean iskey, boolean isInfo, DataViewTab dvt ) {
		FilteredTableColumn<ObservableList<StringProperty>, String> col =
				new FilteredTableColumn<ObservableList<StringProperty>, String>();
//		col.setCellFactory(MyTextField2TableCell.forTableColumn());
		col.setCellFactory(TextField2TableCell.forTableColumn());
//		col.setEditable(true);
		col.setText(colname);
		Label label  = new Label(); 
		if(iskey) {
			label.setGraphic(ImageViewGenerator.svgImage("material-vpn-key", 10, "#1C92FB")); 
		}else {
			label.setGraphic(ImageViewGenerator.svgImage("sort", 10, "#1C92FB")); 
		}
		col.setGraphic(label);
		
		int witdth;
		if(colname.equals("Execute SQL Info")) {
			witdth = 550;
		}else if(colname.equals("Execute SQL")) {
			witdth = 600;
		}else {
			witdth = (colname.length() * 10) + 15;
			if (witdth < 90)
				witdth = 100;
			if (augmentation) {
				witdth = 200;
			}
		}
		
		col.setMinWidth(witdth);
		col.setPrefWidth(witdth);
		col.setCellValueFactory(new StringPropertyListValueFactory(colIdx));
		List<MenuItem> menuList = new ArrayList<>();
		// 右点菜单
		if(! isInfo) { 
			ContextMenu cm =MenuFactory.DataTableColumnContextMenu(colname, type, col, colIdx , menuList);
			col.setContextMenu(cm); 
			label.setTooltip(new Tooltip(typeName)); 
		}
		dvt.getMenuItems().addAll(menuList);
		return col;
	}

	// 删除空白页
	private static void deleteEmptyTab(TabPane dataTab) {
		if (dataTab.getTabs() != null) {
			dataTab.getTabs().removeIf(t -> dataTab.getTabs().size() > 0 && CommonUtility.tabText(t).equals(""));
		}
		// 判断是否已经到达最大tab显示页面
		// 删除旧的 tab
		while (dataTab.getTabs().size() > ConfigVal.maxDataTab) {

			Tab nd = dataTab.getTabs().get(0);
			String idVal = nd.getId();
			dataTab.getTabs().remove(0);
			logger.info("idVal = " + idVal);
			// 删除缓存
			if (idVal != null) {
//				CacheTabView.clear(idVal); //TODO clear?
			}
		}
	}

	// 等待加载动画 页面
	private static void addWaitingPane(Tab waitTb, int tabIdx) {
		Platform.runLater(() -> {
			TabPane dataTab = ComponentGetter.dataTab;

			if (tabIdx > -1) {
				dataTab.getTabs().add(tabIdx, waitTb);
			} else {
				dataTab.getTabs().add(waitTb);
			}
			dataTab.getSelectionModel().select(waitTb);
			// 删除空白tab
			deleteEmptyTab(dataTab);
		});

	}

	// 移除 等待加载动画 页面
	private static void rmWaitingPane(Tab waitTb) {
		Platform.runLater(() -> {
			TabPane dataTab = ComponentGetter.dataTab;
			dataTab.getTabs().remove(waitTb);
		});

	}
	
	private static List<sqlData> epurateSql(String str) {
		List<sqlData> sds = new ArrayList<>();
		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		List<String> sqls = StrUtils.findSQLFromTxt(str);
		
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
		List<String> sqls = StrUtils.findSQLFromTxt(str);
		
		if(sqls.size()> 0) {
			for (String s : sqls) { 
				String trimSql = s.trim();
				if (trimSql.length() > 1) {
					sqlData sq = new sqlData(trimSql, start, s.length());
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
		
		CodeArea code = SqlEditor.getCodeArea();
		String str = SqlEditor.getCurrentCodeAreaSQLTextSelected(); 
		int start = 0;
		if (str != null && str.length() > 0) {
		    start = code.getSelection().getStart();
		} else {
			str = SqlEditor.getCurrentCodeAreaSQLText();
		}
		// 去除注释, 包注释字符串转换为空白字符串
		str = StrUtils.trimCommentToSpace(str, "--");
//		// 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
		sds = epurateSql(str, start);
		return sds;
	}

}


class sqlData{
	String sql;
	int begin;
	int length;
	sqlData(String s, int i, int len){
		sql = s;
		begin = i;
		length = len;
	}
}
