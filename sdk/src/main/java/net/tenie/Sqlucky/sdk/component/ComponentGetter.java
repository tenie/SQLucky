package net.tenie.Sqlucky.sdk.component;

import javafx.scene.layout.VBox;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;

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
	public static FlowPane treeBtnPane;

	public static Scene primaryscene;
	public static Stage primaryStage;

	public static ComboBox<Label> connComboBox;
	
	
	public static Accordion infoAccordion;
	public static TitledPane scriptTitledPane;
	public static TitledPane dbTitledPane;
	
	public static NotificationPane notificationPane ;
	
	// 数据同步界面
	public static Stage dataTransferStage;

	// 当前光标位置
	public static Long cursor;
	
	// 当前光标位置
	public static int codeAreaAnchor;
	
	// 拖动的对象名称
	public static String  dragTreeItemName;
	
	public static AppComponent appComponent;
	
	
	public static Label INFO ;
	public static Label ABOUT ;
	public static Label WARN ;
	public static Label ERROR ;
	public static Label EMPTY ;
	
	
	public static   Region iconInfo;
	public static   Region iconScript;
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
	
	
	
	

	



}
