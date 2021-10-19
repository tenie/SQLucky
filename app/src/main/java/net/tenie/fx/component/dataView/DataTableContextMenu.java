package net.tenie.fx.component.dataView;

import java.util.List;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.MenuAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.utility.MyPopupNumberFilter;
import net.tenie.lib.tools.IconGenerator;

public class DataTableContextMenu {
	
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
		miActive.setGraphic(IconGenerator.svgImageDefActive("clipboard"));
		miActive.setOnAction(e -> { // 粘贴板赋值
			CommonUtility.setClipboardVal(colname);
		});
		menuList.add(miActive);
		
		Menu copyColData = new Menu("Copy Column Data"); 
		copyColData.setGraphic(IconGenerator.svgImageDefActive("columns"));
		menuList.add(copyColData);
		
		
		MenuItem selectCopyColData = new MenuItem("Copy Select Column Data");
		selectCopyColData.setGraphic(IconGenerator.svgImageDefActive("clipboard"));
		selectCopyColData.setOnAction(CommonEventHandler.columnDataClipboard(true, false ,  colname)  );
		menuList.add(selectCopyColData);
		
		MenuItem AllCopyColData = new MenuItem("Copy All Column Data");
		AllCopyColData.setGraphic(IconGenerator.svgImageDefActive("clipboard"));
		AllCopyColData.setOnAction(CommonEventHandler.columnDataClipboard(false, false ,  colname)  );
		copyColData.getItems().addAll(selectCopyColData, AllCopyColData);
		menuList.add(AllCopyColData);
		
		MenuItem filter = new MenuItem("Filter");
		menuList.add(filter);
		filter.setGraphic(IconGenerator.svgImageDefActive("filter"));
		filter.setOnAction(e->{
			popupFilter.showPopup();
		});
		
		// drop column
		MenuItem dropCol = new MenuItem("Drop Column: "+ colname);
		menuList.add(dropCol);
//		dropCol.getStyleClass().add("myMenuItem");
		dropCol.setGraphic(IconGenerator.svgImageDefActive("eraser"));
		dropCol.setOnAction(e ->  {
			MenuAction.dropColumn(colname);
		});
		
		MenuItem alterColumn = new MenuItem("Alter Column Date Type"); 
		menuList.add(alterColumn);
		alterColumn.setGraphic(IconGenerator.svgImageDefActive("exchange"));
		alterColumn.setOnAction(e -> {
			MenuAction.alterColumn(colname);
		});
		
		MenuItem addColumn = new MenuItem("Add New Column"); 
		menuList.add(addColumn);
		addColumn.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));
		addColumn.setOnAction(e -> { 
			MenuAction.addNewColumn();
		}); 
		
		Menu updateMenu = new Menu("Update Column Data");
		menuList.add(updateMenu);
		updateMenu.setGraphic(IconGenerator.svgImageDefActive("edit"));
		MenuItem updateTableColumn = new MenuItem("Update: Table is  Column Value"); 
		menuList.add(updateTableColumn);
		updateTableColumn.setGraphic(IconGenerator.svgImageDefActive("table"));
		updateTableColumn.setOnAction(e -> {
			MenuAction.updateTableColumn(colname);
		});
		
		MenuItem updateCurrentPageColumn = new MenuItem("Update: Current All Data is  Column Value"); 
		menuList.add(updateCurrentPageColumn);
		updateCurrentPageColumn.setGraphic(IconGenerator.svgImageDefActive("file-text-o"));
		updateCurrentPageColumn.setOnAction(e -> { 
			MenuAction.updateCurrentColumn(colname, colIdx);
		});
		
		MenuItem updateSelectColumn = new MenuItem("Update: Selected Data is Column Value"); 
		menuList.add(updateSelectColumn);
		updateSelectColumn.setGraphic(IconGenerator.svgImageDefActive("indent"));
		updateSelectColumn.setOnAction(e -> {
			MenuAction.updateSelectColumn(colname, colIdx);
		});
		
		updateMenu.getItems().addAll(updateTableColumn, updateCurrentPageColumn, updateSelectColumn);
		
		cm.getItems().addAll(filter, miActive, copyColData,   dropCol, alterColumn, addColumn, updateMenu);
		cm.setOnShowing(e->{
			ObservableList<ObservableList<StringProperty>> alls = MyTabData.dataTableViewSelectedItems();
			if( alls.size() == 0) {
				updateSelectColumn.setDisable(true);
			}else {
				updateSelectColumn.setDisable(false);
			}
		});
		return cm;
	}
	
	
}




