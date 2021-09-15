package net.tenie.fx.component;

import javafx.scene.Node;

import org.apache.commons.io.FileUtils;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import javafx.scene.layout.VBox;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Cache.CacheTabView;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.component.container.DBinfoTree;
import net.tenie.fx.component.container.DataViewContainer;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 
 * @author tenie
 *
 */
public final class ComponentGetter {
	public static PopupFilter<ObservableList<StringProperty>, String> popupFirstNameFilter;
	public static TabPane dataTabPane;
	public static TabPane mainTabPane;
	public static DataViewContainer dataView;
	
	public static TreeView<TreeNodePo> treeView;
	public static DBinfoTree dbInfoTree;
	public static TextField dbInfoFilter;

	public static MasterDetailPane masterDetailPane;
	public static MasterDetailPane treeAreaDetailPane;
	
	public static VBox mainWindow;
	public static FlowPane treeBtnPane;

	public static Scene primaryscene;
	public static Stage primaryStage;

	public static ComboBox<Label> connComboBox;
	public static volatile AppWindow app;
	
	
	public static Accordion infoAccordion;
	public static TitledPane scriptTitledPane;
	public static TitledPane dbTitledPane;
	
	// 数据同步界面
	public static Stage dataTransferStage;

	// 当前光标位置
	public static Long cursor;
	
	// 当前光标位置
	public static int codeAreaAnchor;
	
	// 拖动的对象名称
	public static String  dragTreeItemName;
	
	// file 编码
//	public static Map<String, String> fileEncode = new HashMap<>();
	
	
	// 数据tab集合
	public static ObservableList<Tab>  allDataTabs() {
		return ComponentGetter.dataTabPane.getTabs();
	}
	
//	public static String getFileEncode(String name) {
//		String val = fileEncode.get(name);
//		if(val == null) {
//			val = "UTF-8";
//		}
//		return val;
//	}
	
	// 获取打开文件的目录
	public static File getOpenfileDir() { 
//		return  FileUtils.getUserDirectory();
			if(StrUtils.isNullOrEmpty(ConfigVal.openfileDir)) {
				return  FileUtils.getUserDirectory();
			}else {
				 
				File f = new File(ConfigVal.openfileDir);
				if(f.isFile()) {
					String fp = f.getParent(); 
				    f =  new File(fp);
				} 
				return  f;
			} 
	}
	
	// 获取schema节点的 TreeNodePo
	public static TreeNodePo getSchemaTableNodePo(String schema) {
		Label lb = connComboBox.getValue();
		if( lb != null) {
			String str = lb.getText();
			TreeItem<TreeNodePo> tnp = getConnNode(str);
			if(StrUtils.isNullOrEmpty(schema)) {
				DbConnectionPo  dbpo = DBConns.get(str);
				schema =  dbpo.getDefaultSchema();
			} 

			if(tnp != null ) {
				if(tnp.getChildren().size() > 0) {
					ObservableList<TreeItem<TreeNodePo>> lsShc = tnp.getChildren().get(0).getChildren();
				    for(TreeItem<TreeNodePo> sche : lsShc) {
				    	if(sche.getValue().getName().equals(schema) ) {
				    		return sche.getValue();
				    	}
				    } 
				}
				
			}
		}
		return null;
	}
	
	// 当前选中的数据库连接名称
	public static String getCurrentConnectName() {
		Label lb = connComboBox.getValue();
		String str = "";
		if( lb != null) {
		   str = lb.getText(); 
		}
		return str;
	}
	// 
	public static DbConnectionPo getCurrentConnectPO() {
		String connName = getCurrentConnectName();
		return DBConns.get(connName);
	}
	
	// 根据链接名称,获取链接Node 
	public static TreeItem<TreeNodePo>  getConnNode(String dbName){
//		TreeItem<TreeNodePo> conn =
		TreeItem<TreeNodePo> root  = treeView.getRoot();
		 // 遍历tree root 找到对于的数据库节点
	    for(  TreeItem<TreeNodePo>  connNode : root.getChildren()) {
	    	if(connNode.getValue().getName().equals(dbName)) { 
	    		return connNode; 
	    	}
	    		
	    } 
	    return null;
	}
	
	// 根据链接名称,获取链接Node 
	public static TreeItem<TreeNodePo>  getSchemaNode(String dbName, String SchemaName){
		TreeItem<TreeNodePo> connNode = getConnNode(dbName);
		if(connNode!=null ) {
			TreeItem<TreeNodePo> schemaParent =	connNode.getChildren().get(0);
			for(TreeItem<TreeNodePo> schNode : schemaParent.getChildren()) {
				if(schNode.getValue().getName().equals(SchemaName)) {
					return schNode;
				}
			}
		}
		
		return null;
	}
	
	
	// 获取当前代码Tab中的

	// 获取库的连接对象
	public static DbConnectionPo getSchameIsConnObj(TreeItem<TreeNodePo> item) {
		String connName = item.getParent().getParent().getValue().getName();
		return DBConns.get(connName);
	}

	// 获取数据表的 控制按钮列表
	public static AnchorPane dataAnchorPane(FilteredTableView<ObservableList<StringProperty>> table) {
		VBox vb = (VBox) table.getParent();
		AnchorPane fp = (AnchorPane) vb.getChildren().get(0);
		return fp;
	}
	
	// 获取当前数据表的Tab
	public static Tab currentDataTab() {
		Tab tab = dataTabPane.getSelectionModel().getSelectedItem();
		return tab;
	}
	
	// 获取数据页的id
	public static String currentDataTabID() {
		Tab tab = currentDataTab();
		String id = tab.getId();
		
		return id;
	}
	//  获取当前数据表的Tab 中的 vbox
	public static VBox currentDataVbox() {
		Tab tab = dataTabPane.getSelectionModel().getSelectedItem();
		VBox vb = (VBox) tab.getContent();
		return vb;
	}
	
	// 获取 当前table view 的控制面板
	public static AnchorPane dataPane() {
		if(   dataTabPane == null  
		   || dataTabPane.getSelectionModel() == null
		   || dataTabPane.getSelectionModel().getSelectedItem() == null) return null;  
		Node vb =  dataTabPane.getSelectionModel().getSelectedItem().getContent();
		if(vb != null) { 
			VBox vbx = (VBox) vb;
			AnchorPane fp = (AnchorPane) vbx.getChildren().get(0);
			return fp;
		}
		return null;
	}

	// 获取当前table view 的保存按钮
	public static Button dataPaneSaveBtn() {
		AnchorPane fp = ComponentGetter.dataPane();
		if(fp == null ) return null;
		return (Button) fp.getChildren().get(0);
	}
	
	// 获取当前table view 的详细按钮
	public static Button dataPaneDetailBtn() {
		AnchorPane fp = ComponentGetter.dataPane();
		if(fp == null ) return null;
		return (Button) fp.getChildren().get(1);
	}

	// 获取当前的表格
	@SuppressWarnings("unchecked")
	public static FilteredTableView<ObservableList<StringProperty>> dataTableView() {
		VBox vb = (VBox) dataTabPane.getSelectionModel().getSelectedItem().getContent();
		FilteredTableView<ObservableList<StringProperty>> table = (FilteredTableView<ObservableList<StringProperty>>) vb
				.getChildren().get(1);
		return table;
	}

	// 获取当前表格选择的数据
	public static ObservableList<ObservableList<StringProperty>> dataTableViewSelectedItems() {
		ObservableList<ObservableList<StringProperty>> vals = dataTableView().getSelectionModel().getSelectedItems();
		return vals;
	}

	// 获取当前表格id
	public static String dataTableViewID() {
		return dataTableView().getId();
	}

	// 获取当前数据页面 中的 某个按钮
	public static Button getDataOptionBtn(String btnName) {
		AnchorPane fp = ComponentGetter.dataPane();
		if(fp == null ) return null;
		Optional<Node> fn = fp.getChildren().stream().filter(v -> {
			return v.getId().equals(btnName);
		}).findFirst();
		Button btn = (Button) fn.get();

		return btn;
	}
	
	// 获取所有按钮
	public static List<ButtonBase>  dataPaneBtns() {
	 
		List<ButtonBase> ls = new ArrayList<>();
		for( String key :CacheTabView.getKey()) {
			ls.addAll(CacheTabView.optionBtns(key) );
		} 
		return ls;
	}
	// 获取数据面板中所有右键按钮
	public static List<MenuItem>  dataPaneMenuItems() {
		 
		List<MenuItem> ls = new ArrayList<>();
		for( String key :CacheTabView.getKey()) {
			ls.addAll(CacheTabView.MenuItems(key) );
		} 
		return ls;
	}

}
