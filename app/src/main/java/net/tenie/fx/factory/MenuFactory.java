package net.tenie.fx.factory;

import java.util.List;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.MenuAction;
import net.tenie.Sqlucky.sdk.component.ImageViewGenerator;
import net.tenie.fx.component.container.DataViewTab;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.utility.MyPopupNumberFilter;

public class MenuFactory {
	
	/**
	 * 数据表中列的右键弹出菜单
	 * @param colname
	 * @param type
	 * @param col
	 * @return
	 */
	public static ContextMenu DataTableColumnContextMenu(String colname, int type, FilteredTableColumn<ObservableList<StringProperty>, String> col, int colIdx, List<MenuItem> menuList) {
		
		// 过滤框
		PopupFilter<ObservableList<StringProperty>, String> popupFilter ;
		if (CommonUtility.isNum(type)) {
			// 过滤框
			popupFilter = new MyPopupNumberFilter<>(col);
			col.setOnFilterAction(e -> popupFilter.showPopup());
			popupFilter.getStyleClass().add("mypopup");

		} else {
			// 过滤框
			popupFilter = new PopupStringFilter<>(col);
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
		menuList.add(miActive);
		
		Menu copyColData = new Menu("Copy Column Data"); 
		copyColData.setGraphic(ImageViewGenerator.svgImageDefActive("columns"));
		menuList.add(copyColData);
		
		
		MenuItem selectCopyColData = new MenuItem("Copy Select Column Data");
		selectCopyColData.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		selectCopyColData.setOnAction(CommonEventHandler.columnDataClipboard(true, false ,  colname)  );
		menuList.add(selectCopyColData);
		
		MenuItem AllCopyColData = new MenuItem("Copy All Column Data");
		AllCopyColData.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		AllCopyColData.setOnAction(CommonEventHandler.columnDataClipboard(false, false ,  colname)  );
		copyColData.getItems().addAll(selectCopyColData, AllCopyColData);
		menuList.add(AllCopyColData);
		
		MenuItem filter = new MenuItem("Filter");
		menuList.add(filter);
		filter.setGraphic(ImageViewGenerator.svgImageDefActive("filter"));
		filter.setOnAction(e->{
			popupFilter.showPopup();
		});
		
		// drop column
		MenuItem dropCol = new MenuItem("Drop Column: "+ colname);
		menuList.add(dropCol);
//		dropCol.getStyleClass().add("myMenuItem");
		dropCol.setGraphic(ImageViewGenerator.svgImageDefActive("eraser"));
		dropCol.setOnAction(e ->  {
			MenuAction.dropColumn(colname);
		});
		
		MenuItem alterColumn = new MenuItem("Alter Column Date Type"); 
		menuList.add(alterColumn);
		alterColumn.setGraphic(ImageViewGenerator.svgImageDefActive("exchange"));
		alterColumn.setOnAction(e -> {
			MenuAction.alterColumn(colname);
		});
		
		MenuItem addColumn = new MenuItem("Add New Column"); 
		menuList.add(addColumn);
		addColumn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
		addColumn.setOnAction(e -> { 
			MenuAction.addNewColumn();
		}); 
		
		Menu updateMenu = new Menu("Update Column Data");
		menuList.add(updateMenu);
		updateMenu.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
		MenuItem updateTableColumn = new MenuItem("Update: Table is  Column Value"); 
		menuList.add(updateTableColumn);
		updateTableColumn.setGraphic(ImageViewGenerator.svgImageDefActive("table"));
		updateTableColumn.setOnAction(e -> {
			MenuAction.updateTableColumn(colname);
		});
		
		MenuItem updateCurrentPageColumn = new MenuItem("Update: Current All Data is  Column Value"); 
		menuList.add(updateCurrentPageColumn);
		updateCurrentPageColumn.setGraphic(ImageViewGenerator.svgImageDefActive("file-text-o"));
		updateCurrentPageColumn.setOnAction(e -> { 
			MenuAction.updateCurrentColumn(colname, colIdx);
		});
		
		MenuItem updateSelectColumn = new MenuItem("Update: Selected Data is Column Value"); 
		menuList.add(updateSelectColumn);
		updateSelectColumn.setGraphic(ImageViewGenerator.svgImageDefActive("indent"));
		updateSelectColumn.setOnAction(e -> {
			MenuAction.updateSelectColumn(colname, colIdx);
		});
		
		updateMenu.getItems().addAll(updateTableColumn, updateCurrentPageColumn, updateSelectColumn);
		
		cm.getItems().addAll(filter, miActive, copyColData,   dropCol, alterColumn, addColumn, updateMenu);
		cm.setOnShowing(e->{
			ObservableList<ObservableList<StringProperty>> alls = DataViewTab.dataTableViewSelectedItems();
			if( alls.size() == 0) {
				updateSelectColumn.setDisable(true);
			}else {
				updateSelectColumn.setDisable(false);
			}
		});
		return cm;
	}
	
 
	
	
	
	
//	treeView 右键菜单 TreeMenu
//	@Deprecated 
//	public static ContextMenu CreateTreeViewConnMenu() {
//			ContextMenu contextMenu = new ContextMenu();
////
////			MenuItem add = new MenuItem("Add Connection");
////			add.setOnAction(e -> {
////				ConnectionEditor.ConnectionInfoSetting();
////			});
////			add.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
//			
//			MenuItem addtabNewCol = new MenuItem("Table Add New Column");
//			addtabNewCol.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
//			addtabNewCol.setId("tableAddNewCol");
//			addtabNewCol.setDisable(true);
//			
////			addtabNewCol.setOnAction(e -> {
//////				ConnectionEditor.ConnectionInfoSetting();
////				addNewColumn();
////			});
//
//			MenuItem link = new MenuItem("Open Connection");
//			link.setOnAction(CommonEventHandler.openConnEvent());
//			link.setGraphic(ImageViewGenerator.svgImageDefActive("link"));
//			link.setDisable(true);
//			link.setId("OpenConnection");
//			
//			MenuItem unlink = new MenuItem("Close Connection");
//			unlink.setOnAction(CommonEventHandler.closeConnEvent());
//			unlink.setGraphic(ImageViewGenerator.svgImageDefActive("unlink"));
//			unlink.setDisable(true);
//			unlink.setId("CloseConnection");
//			
//			MenuItem Edit = new MenuItem("Edit Connection");
//			Edit.setOnAction(CommonEventHandler.editConnEvent());
//			Edit.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
//			Edit.setDisable(true);
//			Edit.setId("EditConnection");
//			
//			MenuItem delete = new MenuItem("Delete Connection");
//			delete.setOnAction(e -> {
//				ConnectionEditor.ConnectionInfoSetting();
//			});
//			delete.setGraphic(ImageViewGenerator.svgImageDefActive("trash"));
//			delete.setDisable(true);
//			delete.setId("DeleteConnection");
//			
//			contextMenu.getItems().addAll(
////					add,
//					link, unlink, Edit, delete, new SeparatorMenuItem(), addtabNewCol);
//
//			return contextMenu;
//		}
	
	

	
	
	
	
	
	
}




