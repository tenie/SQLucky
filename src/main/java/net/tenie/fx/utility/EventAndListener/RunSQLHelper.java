package net.tenie.fx.utility.EventAndListener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.cell.TextField2TableCell;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;
import org.fxmisc.richtext.CodeArea;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.MyPopupNumberFilter;
import net.tenie.fx.PropertyPo.DataTabDataPo;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.ModalDialog;
import net.tenie.fx.component.MyTextField2TableCell;
import net.tenie.fx.component.SqlCodeAreaHighLightingHelper;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.component.StringPropertyListValueFactory;
import net.tenie.fx.component.container.ConnItemDbObjects;
import net.tenie.fx.component.container.DataViewContainer;
import net.tenie.fx.component.container.DataViewTab;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.DmlDdlDao;
import net.tenie.fx.dao.SelectDao;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.lib.db.Dbinfo;
import net.tenie.lib.db.sql.ParseSQL;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.po.TablePrimaryKeysPo;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class RunSQLHelper {
	private static Thread thread;
	private static JFXButton runbtn;
	private static JFXButton stopbtn;
	private static JFXButton otherbtn;
	private static final String WAITTB_NAME = "Loading...";

	@SuppressWarnings("restriction")
	private static void runMain(Map<String, Object> val) { // throws Exception
		if (val == null)
			return;
		String sqlstr = (String) val.get("sql");
		Connection conn = (Connection) val.get("conn");
		String tabIdx = (String) val.get("tabIdx");
		String btn = (String)val.get("btn");
		int tidx = -1;
		if (StrUtils.isNotNullOrEmpty(tabIdx)) {
			tidx = Integer.valueOf(tabIdx);
		}
		if (conn == null)
			return;
		// 等待加载动画
		Tab waitTb = DataViewTab.maskTab(WAITTB_NAME);
		addWaitingPane(waitTb, tidx);
		try {
			// 执行刷新数据的时候, (执行缓存的sql)
			if (StrUtils.isNotNullOrEmpty(sqlstr)) {
				selectAction(sqlstr, conn, tidx); // 执行刷新查询sql
			} else {
				// 获取文本编辑中选中的sql文本来执行sql
				// 获取sql 语句
				List<sqlData> allsqls = new ArrayList<>();
				if(StrUtils.isNotNullOrEmpty(btn)) {  //执行存储过程函数等
					String str = SqlEditor.getCurrentTabSQLText();
					sqlData sq = new sqlData(str, 0, str.length());
					allsqls.add(sq);
				}else {
					allsqls = willExecSql();
				} 
				// 执行sql
				execSqlList(allsqls, conn);
			}

		} catch (Exception e) {
			Platform.runLater(() -> { 
				ModalDialog.showErrorMsg("Sql Error", e.getMessage());
			});
			e.printStackTrace();
		} finally {
			rmWaitingPane(waitTb);
			settingBtn();
		}
		
	}

	// 执行查询sql 并拼装成一个表, 多个sql生成多个表
	private static void execSqlList(List<sqlData> allsqls, Connection conn) throws SQLException {
//    	List<String > allsqls = tdpo.getSql(); 
		String sqlstr;
		String sql;
		int sqllenght = allsqls.size();
		DbTableDatePo ddlDmlpo = new DbTableDatePo();

		ddlDmlpo.addField("Execute SQL Info");
		ddlDmlpo.addField("Execute SQL");

		for (int i = 0; i < sqllenght; i++) {
//			sqlstr = allsqls.get(i);
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
							System.out.println("add update sql: " + sql);
						} else if (type == ParseSQL.INSERT) {
							msg = DmlDdlDao.insertSql2(conn, sql);
							System.out.println("add insert sql: " + sql);
						} else if (type == ParseSQL.DELETE) {
							msg = DmlDdlDao.deleteSql2(conn, sql);
							System.out.println("add DELETE sql: " + sql);
						} else if (type == ParseSQL.DROP) {
							msg = DmlDdlDao.dropSql2(conn, sql);
							System.out.println("add DROP sql: " + sql);
						} else if (type == ParseSQL.ALTER) {
							msg = DmlDdlDao.alterSql2(conn, sql);
							System.out.println("add ALTER sql: " + sql);
						} else if (type == ParseSQL.CREATE) {
							msg = DmlDdlDao.createSql2(conn, sql);
							System.out.println("add CREATE sql: " + sql);
						} else {
							msg = DmlDdlDao.otherSql2(conn, sql);
							System.out.println("add OTEHR sql: " + sql);
						}
				}
			} catch (Exception e) {
				msg = "failed : " + e.getMessage();
				int bg = allsqls.get(i).begin;
				int len =  allsqls.get(i).length;
				Platform.runLater(() -> {  
					SqlCodeAreaHighLightingHelper.applyErrorHighlighting( bg, len);
				});
			}
			if(StrUtils.isNotNullOrEmpty(msg)) {
				ObservableList<StringProperty> val = FXCollections.observableArrayList();
				val.add(new SimpleStringProperty(msg));
				val.add(new SimpleStringProperty(sqlstr));
				val.add(new SimpleStringProperty("" + i));
				ddlDmlpo.addData(val);
			}

		}
		showExecuteSQLInfo(ddlDmlpo);
	}
	
	// 展示信息窗口,
	public static void showExecuteSQLInfo(DbTableDatePo ddlDmlpo) {
		// 有数据才展示
		if (ddlDmlpo.getAllDatas().size() > 0) {
			DataTabDataPo tdpo = new DataTabDataPo();
			tdpo.addTableName("Execute Info");
			FilteredTableView<ObservableList<StringProperty>> table = DataViewContainer.creatFilteredTableView();
			DataViewContainer.setTabRowWith(table, ddlDmlpo.getAllDatasSize());
			// table 添加列和数据
			TableAddVal(table, ddlDmlpo, new ArrayList<String>());
			tdpo.addTableView(table);
			// 渲染界面
			if (!thread.isInterrupted())
				DataViewContainer.showTableDate(tdpo);

		}
	}

	// select action 选中的sql执行
	private static void selectAction(String sql, Connection conn) throws Exception {
		selectAction(sql, conn, -1);
	}

	private static void selectAction(String sql, Connection conn, int tidx) throws Exception {
		try {
			DataTabDataPo tdpo = new DataTabDataPo();
			FilteredTableView<ObservableList<StringProperty>> table = DataViewContainer.creatFilteredTableView();
			// 获取表名
			String tableName = ParseSQL.tabName(sql);
			System.out.println("tableName= " + tableName + "\n sql = " + sql);
			tdpo.addTableName(tableName);
			DbTableDatePo dpo = SelectDao.selectSql(conn, sql, ConfigVal.MaxRows, table);
			DataViewContainer.setTabRowWith(table, dpo.getAllDatasSize());
			// 保存查询信息
			CacheTableDate.saveTableName(table.getId(), tableName);
			CacheTableDate.saveSelectSQl(table.getId(), sql);
			CacheTableDate.saveDBConn(table.getId(), conn);
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true));

			
			//根据表明获取tablepo对象
			List<String> keys = findPrimaryKeys(conn, tableName);
			// table 添加列和数据
			TableAddVal(table, dpo ,keys);
			tdpo.addTableView(table);
			// 渲染界面
			if (!thread.isInterrupted())
				DataViewContainer.showTableDate(tdpo, tidx, false);
		} catch (Exception e) {
			Platform.runLater(() -> {
				ModalDialog.showErrorMsg("Sql Error", e.getMessage());
			});
			e.printStackTrace();
			throw e;
		}
	}
	
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
		if(tnp != null) {
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

	private static void TableAddVal(FilteredTableView<ObservableList<StringProperty>> table, DbTableDatePo dpo, List<String> keys ) {
		ObservableList<SqlFieldPo> colss = dpo.getFields();
		// 表格添加列
		tableAddColumn(table, colss, keys);
		// 数据添加到表格 更简洁的api
		ObservableList<ObservableList<StringProperty>> rs = dpo.getAllDatas();
		CacheTableDate.addData(table.getId(), rs);
		table.setItems(rs); // FXCollections.observableArrayList(rs)
	}

	// 在子线程执行 运行sql 的任务
	public static Thread createThread(Connection conn, String sql, String tabIdx, JFXButton run,
			Consumer<Map<String, Object>> action) {
		return new Thread() {
			public void run() {
				System.out.println("线程启动了" + this.getName());
				Map<String, Object> val = new HashMap<>();
				val.put("sql", sql);
				val.put("conn", conn);
				val.put("tabIdx", tabIdx);
				val.put("btn", run.getId());
				action.accept(val);
				System.out.println("线程结束了" + this.getName());
				System.out.println(run.getId());
			}
		};
	}

	// 设置 按钮状态
	public static void settingBtn(JFXButton run, JFXButton stop,JFXButton btn) {
		if (runbtn == null) {
			runbtn = run;
			otherbtn = btn;
		}
		if (stopbtn == null) {
			stopbtn = stop;
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

	public static void settingBtn() {
		settingBtn(null, null , null);
	}

//    检查db连接状态
	private static boolean checkDBconn() {
		ComboBox<Label> conns = ComponentGetter.connComboBox;

		boolean warn = false;
//	    	System.out.println(conns.getValue() );
		if (conns.getValue() == null || StrUtils.isNullOrEmpty(conns.getValue().getText())) {
			warn = true;
			ModalDialog.errorAlert("Warn!", "please , choose alive DB connection!");
			return warn;
		} 
		String val = conns.getValue().getText();
		DbConnectionPo po = DBConns.get(val);
		if (po == null || !po.isAlive()) {
			warn = true;
			ModalDialog.errorAlert("Warn!", "please ,  connect DB !");
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
	public static void runSQLMethod(Connection conn, String sql, String tabIdx, JFXButton run) {
		settingBtn();
		CommonAction.showDetailPane();
		thread = createThread(conn, sql, tabIdx, run, RunSQLHelper::runMain);
		thread.start();
	}

	public static void runSQLMethod(JFXButton run, JFXButton stop, JFXButton btn) {
		runSQLMethod(run, stop, btn, null, null);
	}

	public static void runSQLMethod(JFXButton run, JFXButton stop, JFXButton btn, String sql, String tabIdx) {
		if (checkDBconn())
			return;
		DbConnectionPo dpo = DBConns.get(getComboBoxDbConnName());
		Connection conn = dpo.getConn(); // getComboBoxDbConn(); // 获取连接
		try {
			if (conn == null) {
				return;
			} else if (conn.isClosed()) {
				ModalDialog.errorAlert("Warn!", "Connect is Closed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		settingBtn(run, stop , btn );
		CommonAction.showDetailPane();

		thread = createThread(conn, sql, tabIdx, run, RunSQLHelper::runMain);
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
//   		 	 settingBtn(run, stop); 
			thread.interrupt();
			System.out.println("线程是否被中断：" + thread.isInterrupted());// true
			if (thread.isInterrupted()) {
				settingBtn(run, true, stop, false, btn);
			}

		}
	}

	// table 添加列
	private static void tableAddColumn(TableView<ObservableList<StringProperty>> table,
			ObservableList<SqlFieldPo> cols,  List<String> keys ) {

		CacheTableDate.addCols(table.getId(), cols); // 列名缓存
		int len = cols.size();
		for (int i = 0; i < len; i++) {
			String colname = cols.get(i).getColumnLabel().get();
			int type = cols.get(i).getColumnType().get();
			FilteredTableColumn col = null;
			String tyNa = cols.get(i).getColumnTypeName().get() + "(" + cols.get(i).getColumnDisplaySize().get();
			if (cols.get(i).getScale() != null && cols.get(i).getScale().get() > 0) {
				tyNa += ", " + cols.get(i).getScale().get();
			}
			tyNa += ")"; 

			boolean iskey = false;
			if(keys.contains(colname)) {
				iskey = true;
			}
			if (len == 2 && i == 1) {
				col = createColumn(colname, type, tyNa, i, table, true , iskey);
			} else {
				col = createColumn(colname, type, tyNa, i, table, false , iskey);
			}
			table.getColumns().add(col);
		}
	}

	// 创建列
	private static FilteredTableColumn<ObservableList<StringProperty>, String> createColumn(String colname, int type, String typeName,
			int i, TableView<ObservableList<StringProperty>> table, boolean augmentation, boolean iskey) {
		FilteredTableColumn<ObservableList<StringProperty>, String> col =
				new FilteredTableColumn<ObservableList<StringProperty>, String>();
		col.setCellFactory(MyTextField2TableCell.forTableColumn());
		col.setEditable(true);
		
		Label label  = new Label(colname);
		label.setTooltip(new Tooltip(typeName));
		if(iskey) {
			label.setGraphic(ImageViewGenerator.svgImage("material-vpn-key", 10, "#1C92FB")); 
		}
		col.setGraphic(label);
		
		int witdth;
		witdth = (colname.length() * 10) + 15;
		if (witdth < 90)
			witdth = 100;
		if (augmentation) {
			witdth = 200;
		}
		col.setMinWidth(witdth);
		col.setPrefWidth(witdth);
		col.setCellValueFactory(new StringPropertyListValueFactory(i, table));

		PopupFilter<ObservableList<StringProperty>, String> popupFilter ;
		if (CommonUtility.isNum(type)) {
			// 过滤框
			popupFilter = new MyPopupNumberFilter(col);
			col.setOnFilterAction(e -> popupFilter.showPopup());
			popupFilter.getStyleClass().add("mypopup");

		} else {
			// 过滤框
			popupFilter = new PopupStringFilter(col);
			col.setOnFilterAction(e -> popupFilter.showPopup());
			popupFilter.getStyleClass().add("mypopup");
		}

		// 右点菜单
		ContextMenu cm = new ContextMenu();
		MenuItem miActive = new MenuItem("Copy Column Name");
		miActive.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		miActive.setOnAction(e -> { // 粘贴板赋值
			CommonUtility.setClipboardVal(colname);
		});

		MenuItem filter = new MenuItem("Filter");
		filter.setGraphic(ImageViewGenerator.svgImageDefActive("filter"));
		filter.setOnAction(e->{
			popupFilter.showPopup();
		});


		col.setContextMenu(cm); 
		cm.getItems().addAll(miActive, filter);

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
			System.out.println("idVal = " + idVal);
			// 删除缓存
			if (idVal != null) {
				CacheTableDate.clear(idVal);
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
			if (dataTab.getTabs().size() == 1) {
				waitTb.setContent(null);
//		   			waitTb.setText("");
				CommonUtility.setTabName(waitTb, "");
			} else if (dataTab.getTabs().size() > 1) {
				dataTab.getTabs().remove(waitTb);
			}
		});

	}

	
	/**
	 * 获取要执行的sql, 去除无效的(如-- 开头的)
	 */
	public static List<sqlData> willExecSql() {
		List<sqlData> sds = new ArrayList<>();
		CodeArea code = SqlEditor.getCodeArea();
		String str = SqlEditor.getCurrentTabSQLTextSelected(); 
		int start = 0;
		if (str != null && str.length() > 0) {
		    start = code.getSelection().getStart();
		} else {
			str = SqlEditor.getCurrentTabSQLText();
		}
		
		// 去除注释
		str = StrUtils.trimCommentToSpace(str, "--");
		
		// 有多个 执行语句时
		if (str.contains(";")) {
			String[] all = str.split(";"); // 分割多个语句
			if (all != null && all.length > 0) {
				for (String s : all) { 
					String trimSql = s.trim();
					if (trimSql.length() > 1) {
						sqlData sq = new sqlData(trimSql, start, s.length());
						sds.add(sq);
						start +=  s.length()+1; 
					}
				}

			}
		}else {
			sqlData sq = new sqlData(str, start, str.length());
			sds.add(sq);
		} 

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

