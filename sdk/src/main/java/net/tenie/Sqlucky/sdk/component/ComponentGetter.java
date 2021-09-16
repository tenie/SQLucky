package net.tenie.Sqlucky.sdk.component;

import javafx.scene.Node;
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
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.controlsfx.control.MasterDetailPane;
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
	
	// 数据同步界面
	public static Stage dataTransferStage;

	// 当前光标位置
	public static Long cursor;
	
	// 当前光标位置
	public static int codeAreaAnchor;
	
	// 拖动的对象名称
	public static String  dragTreeItemName;
	
 
	
 



	
	
	
	
	

	



}
