package net.tenie.Sqlucky.sdk.subwindow;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.MyTableCellTextField2ReadOnly;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.ImportFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/*   
 * 行数据 显示窗口
 * @author tenie 
 * */
public class TableDataDetail {
	/**
	 * 双击当前行, 子窗口显示行的数据
	 */
	public static void show(MyBottomSheet mtd) {
		var tb = mtd.getTableData().getTable();
		if (tb == null)
			return;
//		int currentRowNo = tb.getSelectionModel().getSelectedIndex();
		ResultSetRowPo selectedItem = tb.getSelectionModel().getSelectedItem();
		Stage stage ;
		if(selectedItem != null ) {
			stage = selectedItem.getShowRowDataStage();
			if (stage != null) {
				stage.requestFocus();
				return;
			}
		}
		
		SheetDataValue dvt = mtd.getTableData();
		String tabName = dvt.getTabName();
		ObservableList<SheetFieldPo> fields = dvt.getColss();

		ObservableList<ResultSetCellPo> cells = null;

		if (selectedItem == null) {
			cells = FXCollections.observableArrayList();
		} else {
			cells = selectedItem.getRowDatas();
		}

		String fieldValue = "Value";
		if (cells.size() > 0) {
			for (int i = 0; i < fields.size(); i++) {
				SheetFieldPo po = fields.get(i);
				ResultSetCellPo cv = cells.get(i);
				StringProperty val = cv.getCellData();
				po.setValue(val);
			}
		} else {
			fieldValue = "Field Type";
			for (int i = 0; i < fields.size(); i++) {
				SheetFieldPo p = fields.get(i);
				String tyNa = p.getColumnTypeName().get() + "(" + p.getColumnDisplaySize().get();
				if (p.getScale() != null && p.getScale().get() > 0) {
					tyNa += ", " + p.getScale().get();
				}
				tyNa += ")";
				StringProperty strp = new SimpleStringProperty(tyNa);// new SimpleStringProperty(val);
				p.setValue(strp);
			}
		}
		stage = showTableDetail(tabName, "Field Name", fieldValue, fields);
		stage.setOnCloseRequest(e -> {
			selectedItem.setShowRowDataStage(null);
		});
		selectedItem.setShowRowDataStage(stage);
	}

	/**
	 * 表字段明细展示
	 * 
	 * @param tableName
	 * @param colName1
	 * @param colName2
	 * @param fields
	 */
	public static Stage showTableDetail(String tableName, String colName1, String colName2,
			ObservableList<SheetFieldPo> fields) {
		FlowPane fp = new FlowPane();

		TextField tf1 = new TextField("");
		tf1.setEditable(false);
		tf1.setPrefWidth(150);
		tf1.setStyle("-fx-background-color: transparent;");
		TextField tf2 = new TextField("");
		tf2.setEditable(false);
		tf2.setPrefWidth(150);
		tf2.setStyle("-fx-background-color: transparent;");
		TextField tf3 = new TextField("");
		tf3.setEditable(false);
		tf3.setPrefWidth(150);
		tf3.setStyle("-fx-background-color: transparent;");

		fp.getChildren().add(tf1);
		fp.getChildren().add(tf2);
		fp.getChildren().add(tf3);
		fp.setPadding(new Insets(8, 0, 0, 0));
		Insets is = new Insets(0, 8, 8, 8);
		FlowPane.setMargin(tf1, is);
		FlowPane.setMargin(tf2, is);
		FlowPane.setMargin(tf3, is);

		// table
		TableView<SheetFieldPo> tv = new TableView<>();
		tv.getStyleClass().add("myTableTag");
		tv.setEditable(true);
		tv.getSelectionModel().selectedItemProperty().addListener(// 选中某一行
				new ChangeListener<SheetFieldPo>() {
					@Override
					public void changed(ObservableValue<? extends SheetFieldPo> observableValue, SheetFieldPo oldItem,
							SheetFieldPo newItem) {
						SheetFieldPo p = newItem;
						if (p == null)
							return;
						tf1.setText(p.getColumnLabel().get());
						String tyNa = p.getColumnTypeName().get() + "(" + p.getColumnDisplaySize().get();
						if (p.getScale() != null && p.getScale().get() > 0) {
							tyNa += ", " + p.getScale().get();
						}
						tyNa += ")";
						tf2.setText(tyNa);
						tf3.setText(p.getColumnClassName().get());
					}
				});
		// 第一列
		TableColumn<SheetFieldPo, String> fieldNameCol = new TableColumn<>(colName1); // "Field Name"
		// 给单元格赋值
		fieldNameCol.setCellValueFactory(cellData -> {
			return cellData.getValue().getColumnLabel();
		});

		fieldNameCol.setCellFactory(MyTableCellTextField2ReadOnly.forTableColumn());
		fieldNameCol.setPrefWidth(200);

		tv.getColumns().add(fieldNameCol);

		// 第二列
		TableColumn<SheetFieldPo, String> valueCol = new TableColumn<>(colName2);
		valueCol.setPrefWidth(200);
		valueCol.setCellValueFactory(cellData -> {
			return cellData.getValue().getValue();
		});
		valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
		tv.getColumns().add(valueCol);

		tv.getItems().addAll(fields);

		VBox subvb = new VBox();
		FlowPane topfp = new FlowPane();
		topfp.setPadding(new Insets(8, 5, 8, 8));
		Label lb = new Label();
		lb.setGraphic(IconGenerator.svgImageDefActive("search"));
		TextField filterField = new TextField();

		filterField.getStyleClass().add("myTextField");
		topfp.getChildren().add(lb);
		FlowPane.setMargin(lb, new Insets(0, 10, 0, 5));
		
		AnchorPane filterFieldPane = UiTools.textFieldAddCleanBtn(filterField);
//		topfp.getChildren().add(filterField);
		topfp.getChildren().add(filterFieldPane);
		topfp.setMinHeight(30);
		topfp.prefHeight(30);
		filterField.setPrefWidth(200);

		subvb.getChildren().add(topfp);

		subvb.getChildren().add(tv);
		VBox.setVgrow(tv, Priority.ALWAYS);
		subvb.getChildren().add(fp);

		// 过滤功能
		filterField.textProperty().addListener((o, oldVal, newVal) -> {
			if (StrUtils.isNotNullOrEmpty(newVal)) {
				bindTableViewFilter(tv, fields, newVal);
			} else {
				tv.setItems(fields);
			}

		});

		
		SqluckyStage sqlstage = new SqluckyStage(subvb, tableName);
		
		Scene scene = sqlstage.getScene();
		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		scene.getAccelerators().put(escbtn, () -> {
			filterField.clear();
		});
		
		Stage stage = sqlstage.getStage();
		stage.show();
		tv.getSelectionModel().select(0);
		return stage;
	}

	/**
	 * 过滤
	 * 
	 * @param tableView
	 * @param observableList
	 * @param newValue
	 */
	public static final void bindTableViewFilter(TableView<SheetFieldPo> tableView,
			ObservableList<SheetFieldPo> observableList, String newValue) {
		FilteredList<SheetFieldPo> filteredData = new FilteredList<>(observableList, p -> true);
		filteredData.setPredicate(entity -> {

			boolean tf1 = false;
			boolean tf2 = false;
			if (entity != null) {
				if (entity.getColumnLabel() != null && entity.getColumnLabel().get() != null) {
					tf1 = entity.getColumnLabel().get().toUpperCase().contains(newValue.toUpperCase());
				}
				if (entity.getValue() != null && entity.getValue().get() != null) {
					tf2 = entity.getValue().get().toUpperCase().contains(newValue.toUpperCase());
				}
			}
			return tf1 || tf2;
		}

		);
		SortedList<SheetFieldPo> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);
	}

	/**
	 * 过滤
	 * 
	 * @param tableView
	 * @param observableList
	 * @param newValue
	 */
	public static final void bindTableViewExcelFieldFilter(TableView<ImportFieldPo> tableView,
			ObservableList<ImportFieldPo> observableList, String newValue) {
		FilteredList<ImportFieldPo> filteredData = new FilteredList<>(observableList, p -> true);
		filteredData.setPredicate(entity -> {

			boolean tf1 = false;
			boolean tf2 = false;
			if (entity != null) {
				if (entity.getColumnLabel() != null && entity.getColumnLabel().get() != null) {
					tf1 = entity.getColumnLabel().get().toUpperCase().contains(newValue.toUpperCase());
				}
				if (entity.getValue() != null && entity.getValue().get() != null) {
					tf2 = entity.getValue().get().toUpperCase().contains(newValue.toUpperCase());
				}
			}
			return tf1 || tf2;
		}

		);
		SortedList<ImportFieldPo> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);
	}

}
