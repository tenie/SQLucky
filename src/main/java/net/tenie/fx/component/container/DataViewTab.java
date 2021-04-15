package net.tenie.fx.component.container;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.controlsfx.control.MaskerPane;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.SqlCodeAreaHighLighting;
import net.tenie.fx.component.SqlEditor;

/*   @author tenie */
public class DataViewTab {
	
	private String tabId ;
	private String sqlStr;
	private String connName;
	// table id + row num 组成key ,保存对于行的数据
	private static Map<String, ObservableList<StringProperty>> newLineDate = new HashMap<>();
	// table id + row num 组成key ,保存对于行的原始数据
	private static Map<String, ObservableList<StringProperty>> oldval = new HashMap<>();
	// 表字段的信息
	private static  ObservableList<SqlFieldPo> tabCol = FXCollections.observableArrayList();
	// 表格数据
	private static  ObservableList<ObservableList<StringProperty>> tabData =  FXCollections.observableArrayList();
	// 待insert的 数据
	private static Map<String, ObservableList<StringProperty>> appendData = new HashMap<>();
	
	// 数据连接对象
	private static  Connection  dbconns ;
	

	// 创建Tab
	public static Tab createTab(TabPane dataTab, String tabName) {
		Tab tb = new Tab(tabName);
		tb.setOnCloseRequest(CommonEventHandler.dataTabCloseReq(dataTab, tb));
		tb.setContextMenu(tableViewMenu(tb));
		return tb;
	}

	public static Tab createTab(String tabName) {
		Tab tb = new Tab(tabName);
		tb.setOnCloseRequest(CommonEventHandler.dataTabCloseReq(ComponentGetter.dataTab, tb));
		tb.setContextMenu(tableViewMenu(tb));
		return tb;
	}

	// 表, 视图 等 数据库对象的ddl语句
	public static void showDdlPanel(String title, String ddl) {
		Tab tb = createTab(title); 
		VBox box = CreateDDLBox(ddl);
		tb.setContent(box);

		ComponentGetter.dataTab.getTabs().add(tb);
		CommonAction.showDetailPane();
		ComponentGetter.dataTab.getSelectionModel().select(tb);
	}
	
	public static void showEmptyPanel(String title, String message) {
		Tab tb = createTab(title); 
		VBox box = CreateDDLBox(message);
		tb.setContent(box);

		ComponentGetter.dataTab.getTabs().add(tb);
		CommonAction.showDetailPane();
//		ComponentGetter.dataTab.getSelectionModel().select(tb);
	}

	// 数据tab中的组件
		public static VBox CreateDDLBox(String ddl) {
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
		public static AnchorPane ddlOptionBtnsPane(String ddl) {
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
	public static ContextMenu tableViewMenu(Tab tb) {
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

	public static Tab maskTab(String waittbName) {
		Tab waitTb = createTab(ComponentGetter.dataTab, waittbName);
		MaskerPane masker = new MaskerPane();
		waitTb.setContent(masker);
		return waitTb;
	}

	public static void ifEmptyAddNewEmptyTab(TabPane dataTab, String tabName) {
		if (dataTab.getTabs().size() == 0) {
			addEmptyTab(dataTab, tabName);
		}
	}

	public static Tab addEmptyTab(TabPane dataTab, String tabName) {
		Tab tb = createTab(dataTab, tabName);
		dataTab.getTabs().add(tb);

		return tb;
	}

}
