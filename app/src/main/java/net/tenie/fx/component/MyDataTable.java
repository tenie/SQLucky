package net.tenie.fx.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Cache.CacheTabView;
import net.tenie.fx.component.container.DataViewContainer;
import net.tenie.fx.component.container.DataViewTab;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.window.TableDataDetail;

public class MyDataTable extends FilteredTableView<ObservableList<StringProperty>> {
 	private DataViewTab tableData ; 
	//FilteredTableView<ObservableList<StringProperty>> table = DataViewContainer.creatFilteredTableView();
	public MyDataTable() {
		super();

		this.rowHeaderVisibleProperty().bind(new SimpleBooleanProperty(true));
		this.setPlaceholder(new Label());
		// 可以选中多行
		this.getSelectionModel().selectionModeProperty().bind(Bindings.when(new SimpleBooleanProperty(true))
				.then(SelectionMode.MULTIPLE).otherwise(SelectionMode.SINGLE));

		String tableIdx = CommonAction.createTabId(); // ConfigVal.tableIdx++;
		this.setId(tableIdx);
		this.getStyleClass().add("myTableTag");

		FilteredTableColumn<ObservableList<StringProperty>, Number> tc = new FilteredTableColumn<>();

		// 点击 行号, 显示一个 当前行心的的窗口
		tc.setCellFactory(col -> {
			TableCell<ObservableList<StringProperty>, Number> cell = new TableCell<ObservableList<StringProperty>, Number>() {

				@Override
				public void updateItem(Number item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);
					if (!empty) {
						int rowIndex = this.getIndex();
						this.setText((rowIndex + 1) + "");
						this.setOnMouseClicked(e -> {
							if (e.getClickCount() == 2) {
								TableDataDetail.show();
							}
						});
					}
				}
			};
			return cell;
		});

		this.setRowHeader(tc);
		// 启用 隐藏列的控制按钮
		this.tableMenuButtonVisibleProperty().setValue(true);
 

	}

	

//	public Tab createTab(int idx, boolean disable, String time, String rows) {
//		var tab = createTab(tableData.getTabName()); 
//		// 构建数据Tab页中的表
//		VBox dataPane = generateDataPane(disable, time, rows);
//		tab.setContent(dataPane); 
//		return tab;
//	}
	
	// 创建Tab
//	public Tab createTab(String tabName) {
//		var tab = new Tab(tabName);
//		  
//		tab.setOnCloseRequest(CommonEventHandler.dataTabCloseReq(tab));
//		tab.setContextMenu(tableViewMenu(tab));
//		return tab;
//	}
//	


	

	
//	public DataViewTab getTableData() {
//		return tableData;
//	}
//
//	public void setTableData(DataViewTab tableData) {
//		this.tableData = tableData;
//	}
	
	
}
