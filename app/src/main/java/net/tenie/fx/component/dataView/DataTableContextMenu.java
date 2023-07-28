package net.tenie.fx.component.dataView;

import java.io.File;
import java.io.IOException;

import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.MyPopupNumberFilter;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.GenerateSQLString;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;

public class DataTableContextMenu {

	/**
	 * 数据表中列的右键弹出菜单
	 * 
	 * @param colname
	 * @param type
	 * @param col
	 * @return
	 */
	public static ContextMenu DataTableColumnContextMenu(MyBottomSheet myBottomSheet, String colname, int type,
			FilteredTableColumn<ResultSetRowPo, String> col, int colIdx) {

		// 过滤框
		PopupFilter<ResultSetRowPo, String> popupFilter;
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

		Menu copyColData = new Menu("Copy Column Data");
		copyColData.setGraphic(IconGenerator.svgImageDefActive("columns"));

		MenuItem selectCopyColData = new MenuItem("Copy Select Column Data");
		selectCopyColData.setGraphic(IconGenerator.svgImageDefActive("clipboard"));
		selectCopyColData.setOnAction(columnDataClipboard(myBottomSheet, true, false, colname));

		MenuItem AllCopyColData = new MenuItem("Copy All Column Data");
		AllCopyColData.setGraphic(IconGenerator.svgImageDefActive("clipboard"));
		AllCopyColData.setOnAction(columnDataClipboard(myBottomSheet, false, false, colname));
		copyColData.getItems().addAll(selectCopyColData, AllCopyColData);

		MenuItem filter = new MenuItem("Filter");
		filter.setGraphic(IconGenerator.svgImageDefActive("filter"));
		filter.setOnAction(e -> {
			popupFilter.showPopup();
		});

		// drop column
		MenuItem dropCol = new MenuItem("Drop Column: " + colname);
//		dropCol.getStyleClass().add("myMenuItem");
		dropCol.setGraphic(IconGenerator.svgImageDefActive("eraser"));
		dropCol.setOnAction(e -> {
			DataTableContextMenuAction.dropColumn(myBottomSheet, colname);
		});

		MenuItem alterColumn = new MenuItem("Alter Column Date Type");
		alterColumn.setGraphic(IconGenerator.svgImageDefActive("exchange"));
		alterColumn.setOnAction(e -> {
			DataTableContextMenuAction.alterColumn(myBottomSheet, colname);
		});

		MenuItem addColumn = new MenuItem("Add New Column");
		addColumn.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));
		addColumn.setOnAction(e -> {
			CommonAction.addNewColumn(myBottomSheet);
		});

		Menu updateMenu = new Menu("Update Column Data");
		updateMenu.setGraphic(IconGenerator.svgImageDefActive("edit"));
		MenuItem updateTableColumn = new MenuItem("Update: Table is  Column Value");
		updateTableColumn.setGraphic(IconGenerator.svgImageDefActive("table"));
		updateTableColumn.setOnAction(e -> {
			DataTableContextMenuAction.updateTableColumn(myBottomSheet.getTableData(), colname);
		});

		MenuItem updateCurrentPageColumn = new MenuItem("Update: Current All Data is  Column Value");
		updateCurrentPageColumn.setGraphic(IconGenerator.svgImageDefActive("file-text-o"));
		updateCurrentPageColumn.setOnAction(e -> {
			DataTableContextMenuAction.updateCurrentColumn(myBottomSheet, colname, colIdx);
		});

		MenuItem updateSelectColumn = new MenuItem("Update: Selected Data is Column Value");
		updateSelectColumn.setGraphic(IconGenerator.svgImageDefActive("indent"));
		updateSelectColumn.setOnAction(e -> {
			DataTableContextMenuAction.updateSelectColumn(myBottomSheet, colname, colIdx);
		});

		updateMenu.getItems().addAll(updateTableColumn, updateCurrentPageColumn, updateSelectColumn);

		cm.getItems().addAll(filter, miActive, copyColData, dropCol, alterColumn, addColumn, updateMenu);
		cm.setOnShowing(e -> {
			ObservableList<ResultSetRowPo> alls = myBottomSheet.getTableData().getTable().getSelectionModel()
					.getSelectedItems(); // SqluckyBottomSheetUtility.dataTableViewSelectedItems(myBottomSheet);
			if (alls.size() == 0) {
				updateSelectColumn.setDisable(true);
			} else {
				updateSelectColumn.setDisable(false);
			}
		});
		return cm;
	}

	public static EventHandler<ActionEvent> columnDataClipboard(MyBottomSheet myBottomSheet, boolean isSelected,
			boolean isFile, String colName) {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				ObservableList<ResultSetRowPo> vals = myBottomSheet.getValsHelper(isSelected);
				final File ff = CommonUtility.getFileHelper(isFile);
				Thread t = new Thread() {
					@Override
					public void run() {
						String sql = GenerateSQLString.columnStrHelper(vals, colName);
						if (StrUtils.isNotNullOrEmpty(sql)) {
							if (isFile) {
								if (ff != null) {
									try {
										FileTools.save(ff, sql);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							} else {
								CommonUtility.setClipboardVal(sql);
							}
						}
					}
				};
				t.start();

			}

		};
	}

}
