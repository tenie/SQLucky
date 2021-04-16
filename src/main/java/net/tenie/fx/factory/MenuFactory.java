package net.tenie.fx.factory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.MenuAction;
import net.tenie.fx.Action.RsVal;
import net.tenie.fx.Action.RunSQLHelper;
//import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.GenerateSQLString;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.utility.MyPopupNumberFilter;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.ModalDialog;
import net.tenie.fx.window.MyAlert;
import net.tenie.lib.db.DBTools;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.tools.StrUtils;

public class MenuFactory {
	
	/**
	 * 数据表中列的右键弹出菜单
	 * @param colname
	 * @param type
	 * @param col
	 * @return
	 */
	public static ContextMenu DataTableColumnContextMenu(String colname, int type, FilteredTableColumn<ObservableList<StringProperty>, String> col, int colIdx) {
		
		// 过滤框
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
//		miActive.getStyleClass().add("myMenuItem");
		miActive.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		miActive.setOnAction(e -> { // 粘贴板赋值
			CommonUtility.setClipboardVal(colname);
		});
		Menu copyColData = new Menu("Copy Column Data");
		copyColData.setGraphic(ImageViewGenerator.svgImageDefActive("columns"));
		
		MenuItem selectCopyColData = new MenuItem("Copy Select Column Data");
		selectCopyColData.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		selectCopyColData.setOnAction(CommonEventHandler.columnDataClipboard(true, false ,  colname)  );
		
		MenuItem AllCopyColData = new MenuItem("Copy All Column Data");
		AllCopyColData.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		AllCopyColData.setOnAction(CommonEventHandler.columnDataClipboard(false, false ,  colname)  );
		copyColData.getItems().addAll(selectCopyColData, AllCopyColData);
		
		MenuItem filter = new MenuItem("Filter");
		filter.setGraphic(ImageViewGenerator.svgImageDefActive("filter"));
		filter.setOnAction(e->{
			popupFilter.showPopup();
		});
		
		// drop column
		MenuItem dropCol = new MenuItem("Drop Column: "+ colname);
//		dropCol.getStyleClass().add("myMenuItem");
		dropCol.setGraphic(ImageViewGenerator.svgImageDefActive("eraser"));
		dropCol.setOnAction(e ->  {
			MenuAction.dropColumn(colname);
		});
		
		MenuItem alterColumn = new MenuItem("Alter Column Date Type"); 
		alterColumn.setGraphic(ImageViewGenerator.svgImageDefActive("exchange"));
		alterColumn.setOnAction(e -> {
			MenuAction.alterColumn(colname);
		});
		
		MenuItem addColumn = new MenuItem("Add New Column"); 
		addColumn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
		addColumn.setOnAction(e -> { 
			MenuAction.addNewColumn();
		}); 
		
		Menu updateMenu = new Menu("Update Column Data");
		updateMenu.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
		MenuItem updateTableColumn = new MenuItem("Update: Table is  Column Value"); 
		updateTableColumn.setGraphic(ImageViewGenerator.svgImageDefActive("table"));
		updateTableColumn.setOnAction(e -> {
			MenuAction.updateTableColumn(colname);
		});
		
		MenuItem updateCurrentPageColumn = new MenuItem("Update: Current All Data is  Column Value"); 
		updateCurrentPageColumn.setGraphic(ImageViewGenerator.svgImageDefActive("file-text-o"));
		updateCurrentPageColumn.setOnAction(e -> { 
			MenuAction.updateCurrentColumn(colname, colIdx);
		});
		
		MenuItem updateSelectColumn = new MenuItem("Update: Selected Data is Column Value"); 
		updateSelectColumn.setGraphic(ImageViewGenerator.svgImageDefActive("indent"));
		updateSelectColumn.setOnAction(e -> {
			MenuAction.updateSelectColumn(colname, colIdx);
		});
		
		updateMenu.getItems().addAll(updateTableColumn, updateCurrentPageColumn, updateSelectColumn);
		
		cm.getItems().addAll(filter, miActive, copyColData,   dropCol, alterColumn, addColumn, updateMenu);
		cm.setOnShowing(e->{
			ObservableList<ObservableList<StringProperty>> alls = ComponentGetter.dataTableViewSelectedItems();
			if( alls.size() == 0) {
				updateSelectColumn.setDisable(true);
			}else {
				updateSelectColumn.setDisable(false);
			}
		});
		return cm;
	}
	
 
	
	
	
	
//	treeView 右键菜单
	public static ContextMenu CreateTreeViewConnMenu() {
			ContextMenu contextMenu = new ContextMenu();

			MenuItem add = new MenuItem("Add Connection");
			add.setOnAction(e -> {
				ConnectionEditor.ConnectionInfoSetting();
			});
			add.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
			
			MenuItem addtabNewCol = new MenuItem("Table Add New Column");
			addtabNewCol.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
			addtabNewCol.setId("tableAddNewCol");
			
//			addtabNewCol.setOnAction(e -> {
////				ConnectionEditor.ConnectionInfoSetting();
//				addNewColumn();
//			});

			MenuItem link = new MenuItem("Open Connection");
			link.setOnAction(CommonEventHandler.openConnEvent());
			link.setGraphic(ImageViewGenerator.svgImageDefActive("link"));

			MenuItem unlink = new MenuItem("Close Connection");
			unlink.setOnAction(CommonEventHandler.closeConnEvent());
			unlink.setGraphic(ImageViewGenerator.svgImageDefActive("unlink"));

			MenuItem Edit = new MenuItem("Edit Connection");
			Edit.setOnAction(CommonEventHandler.editConnEvent());
			Edit.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));

			MenuItem delete = new MenuItem("Delete Connection");
			delete.setOnAction(e -> {
				ConnectionEditor.ConnectionInfoSetting();
			});
			delete.setGraphic(ImageViewGenerator.svgImageDefActive("trash"));

			contextMenu.getItems().addAll(add, link, unlink, Edit, delete, new SeparatorMenuItem(), addtabNewCol);

			return contextMenu;
		}
	
	
	//行号 右键菜单
	public static ContextMenu CreateLineNoMenu(List<String> lineNoList , Label lineNo) {
			ContextMenu contextMenu = new ContextMenu();

			MenuItem add = new MenuItem("Add Bookmark");
			add.setOnAction(e -> { 
				if(lineNoList.contains(lineNo.getText())) {
					lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("NULL",12));
					lineNoList.remove(lineNo.getText());
				}else {
					lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-right", 12));
					lineNoList.add(lineNo.getText());
				} 
				
			});
			add.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-right"));

			MenuItem next = new MenuItem("Next");
			next.setOnAction(e->{
				ButtonAction.nextBookmark(true);
			});
			next.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-down"));

			MenuItem previous = new MenuItem("Previous");
			previous.setOnAction(e->{
				ButtonAction.nextBookmark(false);
			});
			previous.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-up"));

			 

			contextMenu.getItems().addAll(add, next, previous);

			return contextMenu;
		}
	
	
	
	
	
	
}




