package net.tenie.fx.component.dataView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.RsVal;
import net.tenie.fx.Cache.CacheTabView;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.CodeArea.HighLightingCodeArea;
import net.tenie.fx.component.container.DataViewTab;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.window.TableDataDetail;
import net.tenie.lib.tools.IconGenerator;

public class MyTabData extends Tab{
	AnchorPane fp = new AnchorPane();
	private DataViewTab tableData ;
	public HighLightingCodeArea sqlArea;
	private boolean isLock = false; 
	private boolean isDDL = false;
	
	private String name;
	private int idx; 
	private boolean disable;

	

	public MyTabData(DataViewTab data, int idx, boolean disable) {
	    this(data.getTabName()); 
	    this.tableData = data;
	    this.name = data.getTabName();
	    this.idx = idx;
	    this.disable = disable;
//	    this.time = time;
//	    this.rows = rows;
//		// 构建数据Tab页中的表
//		VBox dataPane = generateDataPane(disable, time, rows);
//		this.setContent(dataPane); 
		 
	}
	
	private MyTabData(String tabName) {
		super(tabName);
		this.name = tabName; 	  
		this.setOnCloseRequest(CommonEventHandler.dataTabCloseReq(this));
		this.setContextMenu(tableViewMenu( )); 
		if(tableData == null) {
			tableData= new DataViewTab();
		}
	}
	// 数据
	public static MyTabData dtTab(DataViewTab data,  int idx, boolean disable) {	
		MyTabData rs = new MyTabData(data, idx, disable); 
		String time = rs.getTableData().getExecTime() == 0 ? "" : rs.getTableData().getExecTime()+ "";
		String rows = rs.getTableData().getRows()  == 0 ? "" : rs.getTableData().getRows() + "";
		VBox dataPane = dataBox(rs, disable, time, rows);
		rs.setContent(dataPane); 
		return rs;
	}
	
	//TODO 表, 视图 等 数据库对象的ddl语句
//		public void showDdlPanel(String title, String ddl) {
//			showDdlPanel(title, ddl, false); 
//			
//		}
		public static MyTabData ddlTab(String name, String ddl, boolean isRunFunc ) {
			var mtb = new MyTabData(name);
			mtb.setDDL(true);
			HighLightingCodeArea  sqlArea = new HighLightingCodeArea(null);
			mtb.setSqlArea(sqlArea); 
			VBox box = DDLBox(mtb, ddl, isRunFunc, false, name);
			mtb.setContent(box);  
			return mtb;
		}
		
		public static MyTabData  ProcedureTab(String name, String ddl, boolean isRunFunc ) {
			var mtb = new MyTabData(name);
			mtb.setDDL(true);
			HighLightingCodeArea  sqlArea = new HighLightingCodeArea(null);
			mtb.setSqlArea(sqlArea); 
			VBox box = DDLBox(mtb, ddl, isRunFunc, true, name);
			mtb.setContent(box);  
			return mtb;
		}
		

		public static MyTabData EmptyTab(String name, String message) {
			var mtb = new MyTabData(name);
			mtb.setDDL(true);
			HighLightingCodeArea  sqlArea = new HighLightingCodeArea(null);
			mtb.setSqlArea(sqlArea);  
			VBox box = DDLBox(mtb, message, false, false, name);
			mtb.setContent(box);  
			return mtb;
		}
		
//		
		public static Tab maskTab(String waittbName) {
			Tab waitTb = new Tab(waittbName);
			MaskerPane masker = new MaskerPane();
			waitTb.setContent(masker);
			return waitTb;
		}

//		public  void ifEmptyAddNewEmptyTab(TabPane dataTab, String tabName) {
//			if (dataTab.getTabs().size() == 0) {
//				addEmptyTab(dataTab, tabName);
//			}
//		}

//		public static Tab addEmptyTab(TabPane dataTab, String tabName) {
//			Tab tb = createTab(tabName);
//			dataTab.getTabs().add(tb);
//
//			return tb;
//		}

 
	
	// 数据tab中的组件
	public static VBox DDLBox(MyTabData mtb, String ddl, boolean isRunFunc, boolean isProc, String name) {
		VBox vb = new VBox();
		
		StackPane sp = mtb.getSqlArea().getCodeAreaPane(ddl, false);
		// 表格上面的按钮
		AnchorPane fp = new DdlOptionBtnsPane(mtb, ddl, isRunFunc, isProc, name); // ddlOptionBtnsPane(ddl, isRunFunc, isProc, name);
		vb.getChildren().add(fp);
		vb.getChildren().add(sp);
		VBox.setVgrow(sp, Priority.ALWAYS);
		return vb;
	}
	 
	
	// 数据tab中的组件
	public static  VBox dataBox( MyTabData mtb, boolean disable, String time, String rows) {
		var dataPane = new VBox();
		//TODO id???????  表格上面的按钮
		var fp = new DataTableOptionBtnsPane( mtb,  disable, time, rows, mtb.getTableData().getConnName(), mtb.getTableData().getBtns(), mtb.getTableData().isLock());	  
		dataPane.getChildren().add(fp);
		dataPane.getChildren().add( mtb.getTableData().getTable());
		VBox.setVgrow( mtb.getTableData().getTable(), Priority.ALWAYS);
		return dataPane;
	}
	
	// 右键菜单
	public ContextMenu tableViewMenu() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeAll = new MenuItem("Close ALl");
		closeAll.setOnAction(e -> {
			List<Tab> ls = new ArrayList<>();
			for (Tab tab : ComponentGetter.dataTabPane.getTabs()) {
				ls.add(tab);
			}
			ls.forEach(tab -> {
				CommonAction.clearDataTable(tab);
			});
			ComponentGetter.dataTabPane.getTabs().clear();
		});

		MenuItem closeOther = new MenuItem("Close Other");
		closeOther.setOnAction(e -> {
			int size = ComponentGetter.dataTabPane.getTabs().size();
			if (size > 1) {
				List<Tab> ls = new ArrayList<>();
				for (Tab tab : ComponentGetter.dataTabPane.getTabs()) {

					if (!Objects.equals(tab, this)) {
						ls.add(tab);
					}
				}
				ls.forEach(tab -> {
					CommonAction.clearDataTable(tab);
				});

				ComponentGetter.dataTabPane.getTabs().clear();
				ComponentGetter.dataTabPane.getTabs().add(this);

			}

		});

		contextMenu.getItems().addAll(closeAll, closeOther);
		return contextMenu;
	}
	 

	public  void show() {
		Platform.runLater(() -> {  
			var dataTab = ComponentGetter.dataTabPane;
			if(isDDL) {
				dataTab.getTabs().add(this); 
			}else {
				if (idx > -1) { 
					dataTab.getTabs().add(idx, this);
				} else {
					dataTab.getTabs().add(this);
				}
			} 
			
			CommonAction.showDetailPane(); 
			dataTab.getSelectionModel().select(this);
		});
	}
	
	
	// 获取当前数据表的Tab
	public static MyTabData currentDataTab() {
		MyTabData tab = (MyTabData) ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
		return tab;
	}
	
	// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
		public static RsVal tableInfo() {
			MyTabData  mtd = currentDataTab() ;
			var dataObj = mtd.getTableData();
			String connName = "";
			String tableName = "";
			Connection conn = null;
			ObservableList<ObservableList<StringProperty>>  alldata = null; 
			SqluckyConnector cntor = null;
			FilteredTableView<ObservableList<StringProperty>> dataTableView = null;
			if(dataObj != null ) {
			    connName = dataObj.getConnName();
				tableName = dataObj.getTabName();
			    cntor =  dataObj.getDbConnection();
				conn = cntor.getConn();
				
				alldata =  dataObj.getRawData();
				
			    dataTableView =  dataObj.getTable(); 
				
			}
//			String tableId = DataViewTab.currentDataTabID();
//			String connName =  CacheTabView.getConnName(tableId); // CacheTableDate.getConnName(tableId);
//			String tableName = CacheTabView.getTableName(tableId); // CacheTableDate.getTableName(tableId);
//			Connection conn =  CacheTabView.getDbConn(tableId); //  CacheTableDate.getDBConn(tableId);   
					
//		    alldata =  dataObj.getRawData(); // CacheTabView.getTabData( ); //CacheTableDate.getData(tableId);
//			SqluckyConnector  dbc = DBConns.get(connName); 
			
//			Button saveBtn = ComponentGetter.dataPaneSaveBtn();
//			 dataTableView =  dataObj.getTab(); //dataTableView();
			RsVal rv = new RsVal();
			rv.conn = conn; 
			rv.dbconnPo = cntor; 
			rv.tableName = tableName;
//			rv.dbc =  dbc; 
			rv.alldata = alldata;
//			rv.saveBtn = saveBtn;
			rv.dataTableView = dataTableView;
			return rv;
		}
	
		// 获取 当前table view 的控制面板
		public static AnchorPane optionPane() {
			if (ComponentGetter.dataTabPane == null || ComponentGetter.dataTabPane.getSelectionModel() == null
					|| ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem() == null)
				return null;
			Node vb = ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem().getContent();
			if (vb != null) {
				VBox vbx = (VBox) vb;
				AnchorPane fp = (AnchorPane) vbx.getChildren().get(0);
				return fp;
			}
			return null;
		}

		// 获取当前数据页面 中的 某个按钮
		public static Button getDataOptionBtn(String btnName) {
			AnchorPane fp = optionPane();
			if (fp == null)
				return null;
			Optional<Node> fn = fp.getChildren().stream().filter(v -> {
				return v.getId().equals(btnName);
			}).findFirst();
			Button btn = (Button) fn.get();

			return btn;
		}

		// 获取当前table view 的保存按钮
		public static Button dataPaneSaveBtn() {
			AnchorPane fp = optionPane();
			if(fp == null ) return null;
			return (Button) fp.getChildren().get(0);
		}
		
		// 获取当前table view 的详细按钮
		public static Button dataPaneDetailBtn() {
			AnchorPane fp = optionPane();
			if(fp == null ) return null;
			return (Button) fp.getChildren().get(1);
		}
	
	public boolean isLock() {
		return isLock;
	}

	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}

	public DataViewTab getTableData() {
		return tableData;
	}

	public void setTableData(DataViewTab tableData) {
		this.tableData = tableData;
	}

	public HighLightingCodeArea getSqlArea() {
		return sqlArea;
	}

	public void setSqlArea(HighLightingCodeArea sqlArea) {
		this.sqlArea = sqlArea;
	}

	public boolean isDDL() {
		return isDDL;
	}

	public void setDDL(boolean isDDL) {
		this.isDDL = isDDL;
	}
	
	
}
