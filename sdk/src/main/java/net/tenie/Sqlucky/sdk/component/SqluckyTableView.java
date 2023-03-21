package net.tenie.Sqlucky.sdk.component;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
//import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;

public class SqluckyTableView<S> extends FilteredTableView<S>{
	
	public SqluckyTableView(String sql) {
		super();  
		init();
		
		
	}
	
	public SqluckyTableView() {
		init();
	}
	
	public void init() {

		this.rowHeaderVisibleProperty().bind(new SimpleBooleanProperty(true));
		this.setPlaceholder(new Label());
		// 可以选中多行
		this.getSelectionModel().selectionModeProperty().bind(Bindings.when(new SimpleBooleanProperty(true))
				.then(SelectionMode.MULTIPLE).otherwise(SelectionMode.SINGLE));

		String tableIdx = createTabId();
		this.setId(tableIdx);
		this.getStyleClass().add("myTableTag");

//		FilteredTableColumn<ResultSetRowPo, Number> tc = new FilteredTableColumn<>();

		// 点击 行号, 显示一个 当前行的明细窗口
//		tc.setCellFactory(col -> {
//			TableCell<ResultSetRowPo, Number> cell = new TableCell<>() {
//				@Override
//				public void updateItem(Number item, boolean empty) {
//					super.updateItem(item, empty);
//					this.setText(null);
//					this.setGraphic(null);
//					if (!empty) {
//						int rowIndex = this.getIndex();
//						this.setText((rowIndex + 1) + "");
//						this.setOnMouseClicked(e -> {
//							if (e.getClickCount() == 2) {
//								TableDataDetail.show();
//							}
//						});
//					}
//				}
//			};
//			return cell;
//		});

//		table.setRowHeader(tc);
		// 启用 隐藏列的控制按钮
		this.tableMenuButtonVisibleProperty().setValue(true);
 
	
	}
	
	public static String createTabId() {
		int tableIdx = ConfigVal.tableIdx++;
		return tableIdx + "";
	}

}
