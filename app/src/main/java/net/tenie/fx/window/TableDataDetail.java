package net.tenie.fx.window;

import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;
import net.tenie.fx.component.MyTextField2ReadOnlyTableCell;
import net.tenie.fx.component.dataView.MyTabData;
import net.tenie.fx.component.dataView.MyTabDataValue;
import net.tenie.fx.dao.SelectDao;
import net.tenie.lib.tools.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/*   
 * 行数据 显示窗口
 * @author tenie 
 * */
public class TableDataDetail {

	public static void showTableFieldType(SqluckyConnector dbc, String schema, String tablename) {
		String sql = "SELECT * FROM " + tablename + " WHERE 1=2";
		try {
			DbTableDatePo DP = SelectDao.selectSqlField(dbc.getConn(), sql);
			ObservableList<SqlFieldPo> fields = DP.getFields();

			String fieldValue = "Field Type";
			for (int i = 0; i < fields.size(); i++) {
				SqlFieldPo p = fields.get(i);
				String tyNa = p.getColumnTypeName().get() + "(" + p.getColumnDisplaySize().get();
				if (p.getScale() != null && p.getScale().get() > 0) {
					tyNa += ", " + p.getScale().get();
				}
				tyNa += ")";
				StringProperty strp = new SimpleStringProperty(tyNa);
				p.setValue(strp);
			}
			showTableDetail(tablename, "Field Name", fieldValue, fields);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void show() {
		MyTabData mtd = MyTabData.currentDataTab();
		var tb = mtd.getTableData().getTable();
		int currentRowNo = tb.getSelectionModel().getSelectedIndex();

		MyTabDataValue dvt = mtd.getTableData();
		String tabName = dvt.getTabName();
		ObservableList<SqlFieldPo> fields = dvt.getColss();

		ObservableList<StringProperty> rowValues = null;

		if (currentRowNo < 0) {
			rowValues = FXCollections.observableArrayList();
		} else {
			rowValues = dvt.getRawData().get(currentRowNo);
		}

		String fieldValue = "Value";
		if (rowValues.size() > 0) {
			for (int i = 0; i < fields.size(); i++) {
				SqlFieldPo po = fields.get(i);
				StringProperty val = rowValues.get(i);
				po.setValue(val);
			}
		} else {
			fieldValue = "Field Type";
			for (int i = 0; i < fields.size(); i++) {
				SqlFieldPo p = fields.get(i);
				String tyNa = p.getColumnTypeName().get() + "(" + p.getColumnDisplaySize().get();
				if (p.getScale() != null && p.getScale().get() > 0) {
					tyNa += ", " + p.getScale().get();
				}
				tyNa += ")";
				StringProperty strp = new SimpleStringProperty(tyNa);// new SimpleStringProperty(val);
				p.setValue(strp);
			}
		}
		showTableDetail(tabName, "Field Name", fieldValue, fields);
	}

	private static void showTableDetail(String tableName ,String colName1, String colName2, ObservableList<SqlFieldPo> fields) {
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
		TableView<SqlFieldPo> tv = new TableView<>();
		tv.getStyleClass().add("myTableTag");
		tv.setEditable(true);
		tv.getSelectionModel().selectedItemProperty().addListener(// 选中某一行
				new ChangeListener<SqlFieldPo>() {
					@Override
					public void changed(ObservableValue<? extends SqlFieldPo> observableValue, SqlFieldPo oldItem,
							SqlFieldPo newItem) {
						SqlFieldPo p = newItem;
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
		TableColumn<SqlFieldPo, String> fieldNameCol = new TableColumn<>(colName1); // "Field Name"
		// 给单元格赋值
		fieldNameCol.setCellValueFactory(cellData -> {
			return cellData.getValue().getColumnLabel();
		});
		
		
		
		fieldNameCol.setCellFactory(MyTextField2ReadOnlyTableCell.forTableColumn());
//		fieldNameCol.setEditable(false);
		fieldNameCol.setPrefWidth(200);

		tv.getColumns().add(fieldNameCol);

		TableColumn<SqlFieldPo, String> valueCol = new TableColumn<>(colName2);
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
		topfp.getChildren().add(filterField);
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

		new ModalDialog(subvb, tv, tableName);
	}

	public static final void bindTableViewFilter(TableView<SqlFieldPo> tableView,
			ObservableList<SqlFieldPo> observableList, String newValue) {
		FilteredList<SqlFieldPo> filteredData = new FilteredList<>(observableList, p -> true);
		filteredData.setPredicate(entity -> {
			boolean tf1 = false;
			boolean tf2 = false;
			if (entity.getColumnLabel() != null) {
				tf1 = entity.getColumnLabel().get().toUpperCase().contains(newValue.toUpperCase());
			}
			if (entity.getValue() != null) {
				tf2 = entity.getValue().get().toUpperCase().contains(newValue.toUpperCase());
			}

			return tf1 || tf2;
		}

		);
		SortedList<SqlFieldPo> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);
	}

}
