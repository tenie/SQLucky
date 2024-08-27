package net.tenie.Sqlucky.sdk.component;

import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;

import java.sql.Statement;

/**
 * 
 * @author tenie
 *
 */
public final class ComponentGetter {
	public static Application SQLucky;
	public static PopupFilter<ObservableList<StringProperty>, String> popupFirstNameFilter;

	public static DataViewContainer dataViewContainer;
	public static TabPane dataTabPane;
	public static TabPane mainTabPane;
	public static TabPane rightTabPane;

	public static TabPane currentActiveTabPane;

	public static VBox tabPanContainer;
	public static Stage dockSideTabPaneWindow;

	public static TextField dbInfoFilter;

	public static MasterDetailPane masterDetailPane;
	public static MasterDetailPane treeAreaDetailPane;
	public static MasterDetailPane rightTabPaneMasterDetailPane;

	public static VBox mainWindow;
	public static TreeView<TreeNodePo> treeView;
	
//	public static FlowPane treeBtnPane;

	// 主界面 Stage
	public static Stage primaryStage;
	// 主界面 scene
	public static Scene primaryscene;

	// 主界面 scene root
	public static StackPane primarySceneRoot;

	// 当前激活的 stage
	public static StackPane currentStackPane;

	public static ComboBox<Label> connComboBox;

	public static Accordion infoAccordion;
	public static TitledPane scriptTitledPane;

	// 查询结果limit 的 text field
//	public static maxRowsTextField
	public static TextField maxRowsTextField;
	// 数据库链接信息tree 的容器面板
	public static TitledPane dbTitledPane;
	// 数据库链接信息tree 的右键菜单
	public static ContextMenu dbInfoTreeContextMenu;

	// 链接信息 treeView root
//	public static TreeItem<TreeNodePo> dbInfoTreeRoot;
	// 脚本treeView root
	public static TreeItem<MyEditorSheet> scriptTreeRoot;
	public static TreeView<MyEditorSheet> scriptTreeView;
//	public static TreeItem<SqluckyTab> scriptTreeRoot;

	public static NotificationPane notificationPane;
	public static Menu pluginMenu;

	// 数据同步界面
	public static Stage dataTransferStage;

	// 当前光标位置
	public static Long cursor;

	// 当前光标位置
//	public static int codeAreaAnchor;

	// 拖动的对象名称
	public static String dragTreeItemName;

	public static AppComponent appComponent;

	public static VBox leftNodeContainer;
	// 窗口上的logo图
	public static Image LogoIcons;

	public static Label INFO;
	public static Label ABOUT;
	public static Label WARN;
	public static Label ERROR;
	public static Label EMPTY;

	public static Region iconInfo;
	public static Region uaIconInfo;
	public static Region iconScript;
	public static Region uaIconScript;
	public static Region iconRight;
	public static Region iconLeft;



	public static Statement sqlStatement;
	public static Region getIconUnActive(String name) {
		var rs = appComponent.getIconUnactive(name);
		return rs;
	}

	public static Region getIconDefActive(String name) {
		var rs = appComponent.getIconDefActive(name);
		return rs;
	}
	public  static void setCurrentSqlStatement(Statement stm){
		ComponentGetter.sqlStatement = stm;
	}

	/**
	 * 返回当前编辑界面的TabPane
	 * @return
	 */
	public static TabPane getEditTabPane(){
		if(currentActiveTabPane != null){
			return currentActiveTabPane;
		}

		TabPane myTabPane = ComponentGetter.mainTabPane;
		if( myTabPane.getTabs().size() > 0 ){
		 	if(isFocused(myTabPane)){
				return  myTabPane;
			}
		}else {
			if(myTabPane.isFocused()){
				return  myTabPane;
			}
		}
		myTabPane = ComponentGetter.rightTabPane;
		if( myTabPane.getTabs().size() > 0 ){
			if(isFocused(myTabPane)){
				return  myTabPane;
			}
		}else {
			if(myTabPane.isFocused()){
				return  myTabPane;
			}
		}


		return ComponentGetter.mainTabPane;
	}

	private static boolean isFocused(TabPane myTabPane){
		Tab tab = myTabPane.getSelectionModel().getSelectedItem();
		var val = CommonUtils.getFocusedChildNode((Parent) tab.getContent());
		if(val == null ){
			return false;
		}
		return true;
	}
}
