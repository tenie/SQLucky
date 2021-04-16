package net.tenie.fx.component.container;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import net.tenie.fx.PropertyPo.DataTabDataPo;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.SqlCodeAreaHighLighting;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.factory.ButtonFactory;

/*   @author tenie */
public class DataViewTab {
	
	private String tabId ;
	private String tabName ;
	private String sqlStr;
	private String connName;
	// table id + row num 组成key ,保存对于行的数据
	private  Map<String, ObservableList<StringProperty>> newLineDate = new HashMap<>();
	// table id + row num 组成key ,保存对于行的原始数据
	private  Map<String, ObservableList<StringProperty>> oldval = new HashMap<>();
	// 表字段的信息
	private   ObservableList<SqlFieldPo> tabCol = FXCollections.observableArrayList();
	// 表格数据
	private   ObservableList<ObservableList<StringProperty>> tabData =  FXCollections.observableArrayList();
	// 待insert的 数据
	private  Map<String, ObservableList<StringProperty>> appendData = new HashMap<>();
	
	// 数据连接对象
	private Connection  dbconns ;
	// 列
	private ObservableList<SqlFieldPo> colss;
	// 数据添加到表格 更简洁的api
	ObservableList<ObservableList<StringProperty>> rawData;
	
	//VBox
	private VBox dataPane;
	// tab
	private Tab tab ;
	// tab中的表格
	private FilteredTableView<ObservableList<StringProperty>> table ;
	
	//
	private DataTabDataPo tdpo ;
	private DbTableDatePo dpo;
	
	public DataViewTab(DbTableDatePo dpo, FilteredTableView<ObservableList<StringProperty>> table , String tabId, 
			            String tabName, String sqlStr,  Connection  dbconns,  String connName,
			            ObservableList<SqlFieldPo> colss, ObservableList<ObservableList<StringProperty>> rawData) {
		this.table = table;
		this.tabId = tabId;
		this.tabName = tabName;
		this.sqlStr = sqlStr;
		this.dbconns = dbconns;
		this.connName = connName;
		this.colss = colss;
		this.rawData = rawData;  
		this.dpo = dpo;
	}
	
	public DataViewTab( FilteredTableView<ObservableList<StringProperty>> table , String tabId, 
            String tabName,  ObservableList<SqlFieldPo> colss, ObservableList<ObservableList<StringProperty>> rawData) {
		this.table = table;
		this.tabId = tabId;
		this.tabName = tabName;
		this.colss = colss;
		this.rawData = rawData;  
		}
	
	public DataViewTab() {
		
	}
	
	public  Tab createTab(int idx,  boolean disable , String time , String rows) {
		 tab = createTab(tabName); 
		 tab.setId(tabId);
		// 构建数据Tab页中的表
		 VBox vb = generateDataPane( disable,   time ,   rows);
		 tab.setContent(vb);
		 var dataTab = ComponentGetter.dataTab ;
		 if (idx > -1) {
			 	
				dataTab.getTabs().add(idx, tab);
			} else {
				dataTab.getTabs().add(tab);
			}

			dataTab.getSelectionModel().select(tab);
		 return tab;
	}

	// 数据tab中的组件
	public  VBox generateDataPane(  boolean disable,   String time , String rows) {
		dataPane = new VBox();
		// 表格上面的按钮
		AnchorPane fp = ButtonFactory.getDataTableOptionBtnsPane(disable, time, rows, connName);
		fp.setId(tabId);
		dataPane.setId(tabId);
		dataPane.getChildren().add(fp);
		dataPane.getChildren().add(table);
		VBox.setVgrow(table, Priority.ALWAYS);
		return dataPane;
	}
	
	// 创建Tab
	public  Tab createTab(String tabName) {
	    tab = new Tab(tabName);
		tab.setOnCloseRequest(CommonEventHandler.dataTabCloseReq( tab));
		tab.setContextMenu(tableViewMenu(tab));
		return tab;
	}

	// 表, 视图 等 数据库对象的ddl语句
	public  void showDdlPanel(String title, String ddl) {
	    tab = createTab(title); 
		VBox box = CreateDDLBox(ddl);
		tab.setContent(box);

		ComponentGetter.dataTab.getTabs().add(tab);
		CommonAction.showDetailPane();
		ComponentGetter.dataTab.getSelectionModel().select(tab);
	}
	
	public  void showEmptyPanel(String title, String message) {
		Tab tb = createTab(title); 
		VBox box = CreateDDLBox(message);
		tb.setContent(box);

		ComponentGetter.dataTab.getTabs().add(tb);
		CommonAction.showDetailPane(); 
	}

	// 数据tab中的组件
		public  VBox CreateDDLBox(String ddl) {
			VBox vb = new VBox();
			StackPane sp = new SqlCodeAreaHighLighting().getObj(ddl, false);
			// 表格上面的按钮
			AnchorPane fp = ddlOptionBtnsPane(ddl);
			vb.getChildren().add(fp);
			vb.getChildren().add(sp);
			VBox.setVgrow(sp, Priority.ALWAYS);
			return vb;
		}
		
		//TODO 数据表格 操作按钮们
		public  AnchorPane ddlOptionBtnsPane(String ddl) {
			AnchorPane fp = new AnchorPane();
			fp.prefHeight(25);
			JFXButton editBtn = new JFXButton();
			editBtn.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
			editBtn.setOnMouseClicked(e->{
				SqlEditor.createTabFromSqlFile(ddl, "", "");
			});
			editBtn.setTooltip(MyTooltipTool.instance("Edit"));
			editBtn.setId(AllButtons.SAVE);
			//隐藏按钮
			JFXButton hideBottom = new JFXButton(); 
			hideBottom.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-down"));
			hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom()); 
			
 

			fp.getChildren().addAll(editBtn , hideBottom);
			AnchorPane.setRightAnchor(hideBottom, 0.0);
			return fp;
		}
	
	// 右键菜单
	public  ContextMenu tableViewMenu(Tab tb) {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeAll = new MenuItem("Close ALl");
		closeAll.setOnAction(e -> {
				// 清空缓存
				for(Tab tab: ComponentGetter.dataTab.getTabs()) {
					CommonAction.clearDataTable(ComponentGetter.dataTab, tab);
				}
				ComponentGetter.dataTab.getTabs().clear(); 
				CommonAction.hideBottom();
		});

		MenuItem closeOther = new MenuItem("Close Other");
		closeOther.setOnAction(e -> {
			int size = ComponentGetter.dataTab.getTabs().size();
			if (size > 1) {
				// 清空缓存
				for(Tab tab: ComponentGetter.dataTab.getTabs()) {
					if( ! Objects.equals(tab, tb)) {
						CommonAction.clearDataTable(ComponentGetter.dataTab, tab);
					}
					
				}
				ComponentGetter.dataTab.getTabs().clear(); 
				ComponentGetter.dataTab.getTabs().add(tb);
				
			}

		});

		contextMenu.getItems().addAll(closeAll, closeOther);
		return contextMenu;
	}

	public  Tab maskTab(String waittbName) {
		Tab waitTb = createTab(waittbName);
		MaskerPane masker = new MaskerPane();
		waitTb.setContent(masker);
		return waitTb;
	}

	public  void ifEmptyAddNewEmptyTab(TabPane dataTab, String tabName) {
		if (dataTab.getTabs().size() == 0) {
			addEmptyTab(dataTab, tabName);
		}
	}

	public  Tab addEmptyTab(TabPane dataTab, String tabName) {
		Tab tb = createTab( tabName);
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
	public DataTabDataPo getTdpo() {
		return tdpo;
	}
	public void setTdpo(DataTabDataPo tdpo) {
		this.tdpo = tdpo;
	}

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

	public DbTableDatePo getDpo() {
		return dpo;
	}

	public void setDpo(DbTableDatePo dpo) {
		this.dpo = dpo;
	}

}
