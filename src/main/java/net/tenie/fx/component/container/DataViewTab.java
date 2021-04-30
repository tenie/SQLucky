package net.tenie.fx.component.container;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.MenuAction;
import net.tenie.fx.Action.RsVal;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.Action.myEvent;
import net.tenie.fx.PropertyPo.DbConnectionPo;
//import net.tenie.fx.PropertyPo.DataTabDataPo;
//import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyCodeArea;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.SqlCodeAreaHighLighting;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.window.ModalDialog;
import net.tenie.fx.window.ProcedureExecuteWindow;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class DataViewTab {

	private String tabId;
	private String tabName;
	private String sqlStr;
	private String connName;
	private boolean isLock = false;

	// sql执行时间
	private double execTime = 0;
	// 行数
	private int rows = 0;

	// table id + row num 组成key ,保存对于行的数据
	private Map<String, ObservableList<StringProperty>> newLineDate = new HashMap<>();
	// table id + row num 组成key ,保存对于行的原始数据
	private Map<String, ObservableList<StringProperty>> oldval = new HashMap<>();
	// 表字段的信息
	private ObservableList<SqlFieldPo> tabCol = FXCollections.observableArrayList();
	// 表格数据
	private ObservableList<ObservableList<StringProperty>> tabData = FXCollections.observableArrayList();
	// 待insert的 数据
	private Map<String, ObservableList<StringProperty>> appendData = new HashMap<>();

	// 操作按钮
	private List<ButtonBase> btns = new ArrayList<>();
	// 列的 menuItem
	private List<MenuItem> menuItems = new ArrayList<>();

	// 数据连接对象
	private Connection dbconns;
	// 列
	private ObservableList<SqlFieldPo> colss;
	// 数据添加到表格 更简洁的api
	ObservableList<ObservableList<StringProperty>> rawData;

	// VBox
	private VBox dataPane;
	// 按钮
	private AnchorPane fp;
	// tab
	private Tab tab;
	// tab中的表格
	private FilteredTableView<ObservableList<StringProperty>> table;

	public void clean() {
		menuItems.clear();
		menuItems = null;
		fp.getChildren().clear();
		dataPane.getChildren().clear();
		dataPane = null;
		fp = null;
		tab.setContent(null);
		tab = null;
		table.getItems().clear();
		table = null;
		rawData.forEach(v -> {
			v.clear();
		});
		rawData.clear();
		rawData = null;
		colss.clear();
		colss = null;
		dbconns = null;
		appendData.clear();
		appendData = null;
		tabData.forEach(v -> {
			v.clear();
		});
		tabData.clear();
		tabData = null;
		tabCol.clear();
		tabCol = null;
		oldval.clear();
		oldval = null;
		newLineDate.clear();
		newLineDate = null;
		btns.clear();
		btns = null;

	}

	//
//	private DataTabDataPo tdpo ; 

	public DataViewTab(FilteredTableView<ObservableList<StringProperty>> table, String tabId, String tabName,
			String sqlStr, Connection dbconns, String connName, ObservableList<SqlFieldPo> colss,
			ObservableList<ObservableList<StringProperty>> rawData) {
		this.table = table;
		this.tabId = tabId;
		this.tabName = tabName;
		this.sqlStr = sqlStr;
		this.dbconns = dbconns;
		this.connName = connName;
		this.colss = colss;
		this.rawData = rawData;
	}

	public DataViewTab(FilteredTableView<ObservableList<StringProperty>> table, String tabId, String tabName,
			ObservableList<SqlFieldPo> colss, ObservableList<ObservableList<StringProperty>> rawData) {
		this.table = table;
		this.tabId = tabId;
		this.tabName = tabName;
		this.colss = colss;
		this.rawData = rawData;
	}

	public DataViewTab() {

	}

	public Tab createTab(int idx, boolean disable, String time, String rows) {
		tab = createTab(tabName);
		tab.setId(tabId);
		// 构建数据Tab页中的表
		generateDataPane(disable, time, rows);
		tab.setContent(dataPane);
		var dataTab = ComponentGetter.dataTab;
		if (idx > -1) {

			dataTab.getTabs().add(idx, tab);
		} else {
			dataTab.getTabs().add(tab);
		}

		dataTab.getSelectionModel().select(tab);
		return tab;
	}

	// 数据tab中的组件
	public VBox generateDataPane(boolean disable, String time, String rows) {
		dataPane = new VBox();
		// 表格上面的按钮
		fp = ButtonFactory.getDataTableOptionBtnsPane(tabId, disable, time, rows, connName, btns, isLock);
		fp.setId(tabId);
		dataPane.setId(tabId);
		dataPane.getChildren().add(fp);
		dataPane.getChildren().add(table);
		VBox.setVgrow(table, Priority.ALWAYS);
		return dataPane;
	}

	// 创建Tab
	public Tab createTab(String tabName) {
		tab = new Tab(tabName);
		tab.setOnCloseRequest(CommonEventHandler.dataTabCloseReq(tab));
		tab.setContextMenu(tableViewMenu(tab));
		return tab;
	}

	// 表, 视图 等 数据库对象的ddl语句
	public void showDdlPanel(String title, String ddl) {
		showDdlPanel(title, ddl, false);
	}
	public void showDdlPanel(String title, String ddl, boolean isRunFunc ) {
		tab = createTab(title);
		tabName = title;
		VBox box = CreateDDLBox(ddl, isRunFunc, false, title);
		tab.setContent(box);

		ComponentGetter.dataTab.getTabs().add(tab);
		CommonAction.showDetailPane();
		ComponentGetter.dataTab.getSelectionModel().select(tab);
	}
	
	public void showProcedurePanel(String title, String ddl, boolean isRunFunc ) {
		tab = createTab(title);
		tabName = title;
		VBox box = CreateDDLBox(ddl, isRunFunc, true,title );
		tab.setContent(box);

		ComponentGetter.dataTab.getTabs().add(tab);
		CommonAction.showDetailPane();
		ComponentGetter.dataTab.getSelectionModel().select(tab);
	}
	

	public void showEmptyPanel(String title, String message) {
		Tab tb = createTab(title);
		VBox box = CreateDDLBox(message, false, false, title);
		tb.setContent(box);

		ComponentGetter.dataTab.getTabs().add(tb);
		CommonAction.showDetailPane();
	}

	public SqlCodeAreaHighLighting sqlArea;
	// 数据tab中的组件
	public VBox CreateDDLBox(String ddl, boolean isRunFunc, boolean isProc, String name) {
		VBox vb = new VBox();
	    sqlArea = new SqlCodeAreaHighLighting();
		StackPane sp = sqlArea.getObj(ddl, false);
		// 表格上面的按钮
		AnchorPane fp = ddlOptionBtnsPane(ddl, isRunFunc, isProc, name);
		vb.getChildren().add(fp);
		vb.getChildren().add(sp);
		VBox.setVgrow(sp, Priority.ALWAYS);
		return vb;
	}


	// TODO 数据表格 操作按钮们
	public AnchorPane ddlOptionBtnsPane(String ddl, boolean isRunFunc ,boolean isProc, String name ) {
		AnchorPane fp = new AnchorPane();
		fp.prefHeight(25);
//		JFXButton editBtn ;
		// 锁 
		if(StrUtils.isNullOrEmpty(tabId)) {
			tabId = CommonAction.createTabId();
			tab.setId(tabId);
		} 
		JFXButton lockbtn =ButtonFactory.createLockBtn(false, false, tabId);
				
		// 保存
		JFXButton saveBtn = new JFXButton();
		saveBtn.setGraphic(ImageViewGenerator.svgImageDefActive("save"));
		saveBtn.setOnMouseClicked(e -> { 
			//TODO 保存存储过程
			RunSQLHelper.runSQLMethod(sqlArea.getCodeArea().getText(), null, true);
			saveBtn.setDisable(true);
			
		});
		saveBtn.setTooltip(MyTooltipTool.instance("save"));
		saveBtn.setDisable(true);
		btns.add(saveBtn);
		
		//编辑
		JFXButton editBtn = new JFXButton();
		editBtn.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
		editBtn.setOnMouseClicked(e -> {
//			SqlEditor.createTabFromSqlFile(ddl, "", "");
			if (sqlArea != null) {
				MyCodeArea codeArea = sqlArea.getCodeArea();
				codeArea.setEditable(true);
				saveBtn.setDisable(false);
//				myEvent.btnClick( lockbtn);
				ButtonFactory.lockLockBtn(tabId, lockbtn);

			}
		});
		editBtn.setTooltip(MyTooltipTool.instance("Edit"));
		editBtn.setId(AllButtons.SAVE);
		btns.add(editBtn);
		
		// 隐藏按钮
		JFXButton hideBottom = new JFXButton();
		hideBottom.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-down"));
		hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom());

		fp.getChildren().addAll(saveBtn, editBtn, hideBottom , lockbtn);
		AnchorPane.setRightAnchor(hideBottom, 0.0);
		AnchorPane.setRightAnchor(lockbtn, 30.0);
		
		
		double offset = 30.0;
		AnchorPane.setLeftAnchor(editBtn, offset);
		
		// 运行按钮
		if (isRunFunc) {
			JFXButton runFuncBtn = new JFXButton();
			runFuncBtn.setGraphic(ImageViewGenerator.svgImageDefActive("play"));
			runFuncBtn.setOnMouseClicked(e -> {
				Consumer< String >  caller;
				if(isProc) {
					new ProcedureExecuteWindow(ddl, name);
					
					caller = x -> {
						
//						DbConnectionPo dpo = ComponentGetter.getCurrentConnectPO();
//						String sql = dpo.getExportDDL().exportCallFuncSql(x);
						
//						RunSQLHelper.callFuncMethod(sql);
//						RunSQLHelper.runSQLMethodRefresh(dpo, dpo.getConn(), sql, null, false);
					};
				}else {
					caller = x -> {
						DbConnectionPo dpo = ComponentGetter.getCurrentConnectPO();
						String sql = dpo.getExportDDL().exportCallFuncSql(x);
						RunSQLHelper.runSQLMethodRefresh(dpo, dpo.getConn(), sql, null, false);
					};
					ModalDialog.showExecWindow("Run function", tabName+"()", caller);
				}
			   
				
			
			});
			runFuncBtn.setTooltip(MyTooltipTool.instance("Run"));
			btns.add(runFuncBtn);
			fp.getChildren().add(runFuncBtn);
			AnchorPane.setLeftAnchor(runFuncBtn, offset + 30.0);
		}

		return fp;
	}

	// 右键菜单
	public ContextMenu tableViewMenu(Tab tb) {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeAll = new MenuItem("Close ALl");
		closeAll.setOnAction(e -> {
			List<Tab> ls = new ArrayList<>();
			for (Tab tab : ComponentGetter.dataTab.getTabs()) {
				ls.add(tab);
			}
			ls.forEach(tab -> {
				CommonAction.clearDataTable(tab);
			});
			ComponentGetter.dataTab.getTabs().clear();
		});

		MenuItem closeOther = new MenuItem("Close Other");
		closeOther.setOnAction(e -> {
			int size = ComponentGetter.dataTab.getTabs().size();
			if (size > 1) {
				List<Tab> ls = new ArrayList<>();
				for (Tab tab : ComponentGetter.dataTab.getTabs()) {

					if (!Objects.equals(tab, tb)) {
						ls.add(tab);
					}
				}
				ls.forEach(tab -> {
					CommonAction.clearDataTable(tab);
				});

				ComponentGetter.dataTab.getTabs().clear();
				ComponentGetter.dataTab.getTabs().add(tb);

			}

		});

		contextMenu.getItems().addAll(closeAll, closeOther);
		return contextMenu;
	}

	public Tab maskTab(String waittbName) {
		Tab waitTb = new Tab(waittbName);
		MaskerPane masker = new MaskerPane();
		waitTb.setContent(masker);
		return waitTb;
	}

	public void ifEmptyAddNewEmptyTab(TabPane dataTab, String tabName) {
		if (dataTab.getTabs().size() == 0) {
			addEmptyTab(dataTab, tabName);
		}
	}

	public Tab addEmptyTab(TabPane dataTab, String tabName) {
		Tab tb = createTab(tabName);
		dataTab.getTabs().add(tb);

		return tb;
	}

	public String getTabId() {
		return tabId;
	}

	public void setTabId(String tabId) {
		this.tabId = tabId;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getSqlStr() {
		return sqlStr;
	}

	public void setSqlStr(String sqlStr) {
		this.sqlStr = sqlStr;
	}

	public ObservableList<SqlFieldPo> getTabCol() {
		return tabCol;
	}

	public void setTabCol(ObservableList<SqlFieldPo> tabCol) {
		this.tabCol = tabCol;
	}

	public ObservableList<ObservableList<StringProperty>> getTabData() {
		return tabData;
	}

	public void setTabData(ObservableList<ObservableList<StringProperty>> tabData) {
		this.tabData = tabData;
	}

	public ObservableList<ObservableList<StringProperty>> getRawData() {
		return rawData;
	}

	public void setRawData(ObservableList<ObservableList<StringProperty>> rawData) {
		this.rawData = rawData;
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tb) {
		this.tab = tb;
	}

	public FilteredTableView<ObservableList<StringProperty>> getTable() {
		return table;
	}

	public void setTable(FilteredTableView<ObservableList<StringProperty>> table) {
		this.table = table;
	}
//	public DataTabDataPo getTdpo() {
//		return tdpo;
//	}
//	public void setTdpo(DataTabDataPo tdpo) {
//		this.tdpo = tdpo;
//	}

	public ObservableList<SqlFieldPo> getColss() {
		return colss;
	}

	public void setColss(ObservableList<SqlFieldPo> colss) {
		this.colss = colss;
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public Map<String, ObservableList<StringProperty>> getNewLineDate() {
		return newLineDate;
	}

	public void setNewLineDate(Map<String, ObservableList<StringProperty>> newLineDate) {
		this.newLineDate = newLineDate;
	}

	public Map<String, ObservableList<StringProperty>> getOldval() {
		return oldval;
	}

	public void setOldval(Map<String, ObservableList<StringProperty>> oldval) {
		this.oldval = oldval;
	}

	public Map<String, ObservableList<StringProperty>> getAppendData() {
		return appendData;
	}

	public void setAppendData(Map<String, ObservableList<StringProperty>> appendData) {
		this.appendData = appendData;
	}

	public Connection getDbconns() {
		return dbconns;
	}

	public void setDbconns(Connection dbconns) {
		this.dbconns = dbconns;
	}

	public VBox getDataPane() {
		return dataPane;
	}

	public void setDataPane(VBox dataPane) {
		this.dataPane = dataPane;
	}

	public double getExecTime() {
		return execTime;
	}

	public void setExecTime(double execTime) {
		this.execTime = execTime;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public List<ButtonBase> getBtns() {
		return btns;
	}

	public void setBtns(List<ButtonBase> btns) {
		this.btns = btns;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public boolean isLock() {
		return isLock;
	}

	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}

}
