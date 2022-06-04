package net.tenie.Sqlucky.sdk.component;

import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;

public class SqlcukyComponent {
	public static String createTabId() {
		int tableIdx = ConfigVal.tableIdx++;
//		System.out.println(tableIdx);
		return tableIdx + "";
	}

	// 数据展示tableView StringProperty
	public static FilteredTableView<ObservableList<StringProperty>> creatFilteredTableView() {
		FilteredTableView<ObservableList<StringProperty>> table = new FilteredTableView<ObservableList<StringProperty>>();

		table.rowHeaderVisibleProperty().bind(new SimpleBooleanProperty(true));
		table.setPlaceholder(new Label());
		// 可以选中多行
		table.getSelectionModel().selectionModeProperty().bind(Bindings.when(new SimpleBooleanProperty(true))
				.then(SelectionMode.MULTIPLE).otherwise(SelectionMode.SINGLE));

		// 选中监听
//			ListChangeListener<ObservableList<StringProperty>> indicesListener = new ListChangeListener<ObservableList<StringProperty>>() {
//				@Override
//				public void onChanged(Change<? extends ObservableList<StringProperty>> c) {
//					while (c.next()) {
		//
//					}
//				}
//			};
//			table.getSelectionModel().getSelectedItems().addListener(indicesListener);

		String tableIdx = createTabId();
		table.setId(tableIdx);
		table.getStyleClass().add("myTableTag");

		FilteredTableColumn<ObservableList<StringProperty>, Number> tc = new FilteredTableColumn<>();

//			tc.setCellValueFactory(cal -> { 
//				ObservableList<StringProperty> obs = cal.getValue(); 
//				int sz = obs.size();
//				StringProperty sp = obs.get(sz - 1); 
//				IntegerProperty sum = new SimpleIntegerProperty(); 
//				sum.setValue(Integer.valueOf(sp.get()) + 1);
//				return sum;
//			});

		// 点击 行号, 显示一个 当前行的明细窗口
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

		table.setRowHeader(tc);
		// 启用 隐藏列的控制按钮
		table.tableMenuButtonVisibleProperty().setValue(true);

		return table;
	}
}
