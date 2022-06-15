package net.tenie.Sqlucky.sdk.component;

import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;

/**
 * 
 * @author tenie
 *
 */
public final class ComponentGetter {
	public static PopupFilter<ObservableList<StringProperty>, String> popupFirstNameFilter;
	public static TabPane dataTabPane;
	public static TabPane mainTabPane;

	public static TextField dbInfoFilter;

	public static MasterDetailPane masterDetailPane;
	public static MasterDetailPane treeAreaDetailPane;
	
	public static VBox mainWindow;
//	public static FlowPane treeBtnPane;

	
	// 主界面 Stage
	public static Stage primaryStage;
	// 主界面 scene
	public static Scene primaryscene;
	
	// 主界面 scene root
	public static StackPane primarySceneRoot;
		
	

	public static ComboBox<Label> connComboBox;
	
	
	public static Accordion infoAccordion;
	public static TitledPane scriptTitledPane;
	
	// 查询结果limit 的 text field
//	public static maxRowsTextField
	public static TextField maxRowsTextField ;
	// 数据库链接信息tree 的容器面板
	public static TitledPane dbTitledPane;
	// 数据库链接信息tree 的右键菜单
	public static ContextMenu	dbInfoTreeContextMenu;
	
	public static NotificationPane notificationPane ;
	public static Menu pluginMenu ;
	
	// 数据同步界面
	public static Stage dataTransferStage;

	// 当前光标位置
	public static Long cursor;
	
	// 当前光标位置
	public static int codeAreaAnchor;
	
	// 拖动的对象名称
	public static String  dragTreeItemName;
	
	public static AppComponent appComponent;
	
	public static VBox leftNodeContainer;
	// 窗口上的logo图
	public static Image LogoIcons;
	
	
	public static Label INFO ;
	public static Label ABOUT ;
	public static Label WARN ;
	public static Label ERROR ;
	public static Label EMPTY ;
	
	
	public static   Region iconInfo;
	public static   Region uaIconInfo;
	public static   Region iconScript;
	public static   Region uaIconScript;
	public static   Region iconRight;
	public static   Region iconLeft;
	
	public static Region getIconUnActive(String name) {
		var rs = appComponent.getIconUnactive(name);
		return rs;
	}
	 
	public static Region getIconDefActive(String name) {
		var rs = appComponent.getIconDefActive(name);
		return rs;
	}
	
//	public static SqluckyBottomSheet currentDataTab() {
//		SqluckyBottomSheet tab = (MyTabData) ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
//		return tab;
//	}
	
	// 获取当前数据表的Tab
	public static MyTabData currentDataTab() {
		Tab tab =  ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
		MyTabData mtb =	(MyTabData) tab.getUserData();
//		SqluckyBottomSheet sheet = (SqluckyBottomSheet) tab;
		return mtb;
	}

	
	
	
	

	



}
