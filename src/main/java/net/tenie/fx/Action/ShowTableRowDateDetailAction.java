package net.tenie.fx.Action;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

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
import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.ModalDialog;
import net.tenie.lib.tools.StrUtils;

/*   
 * 行数据 显示窗口
 * @author tenie 
 * */
public class ShowTableRowDateDetailAction {
	public static void show(JFXButton saveBtn) {

		VBox vb = (VBox) saveBtn.getParent().getParent();
		@SuppressWarnings("unchecked")
		TableView<ObservableList<StringProperty>> tb = (TableView<ObservableList<StringProperty>>) vb.getChildren()
				.get(1);
		int currentRowNo = tb.getSelectionModel().getSelectedIndex();

		String id = saveBtn.getParent().getId();
		ObservableList<SqlFieldPo> fields = CacheTableDate.getCols(id);
		ObservableList<StringProperty> rowValues = null;

		if (currentRowNo < 0) {
			rowValues = FXCollections.observableArrayList();
		} else {
			rowValues = CacheTableDate.getData(id).get(currentRowNo);
		}

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
		fp.setMargin(tf1, is);
		fp.setMargin(tf2, is);
		fp.setMargin(tf3, is);
		if (rowValues.size() > 0) {
			for (int i = 0; i < fields.size(); i++) {
				SqlFieldPo po = fields.get(i);
				StringProperty val = rowValues.get(i);
				po.setValue(val);
			}
		}

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
						if(p == null ) return;
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
		TableColumn<SqlFieldPo, String> col = new TableColumn<>("Field Name");
		col.setCellValueFactory(cellData -> {
			return cellData.getValue().getColumnLabel();
		});
		col.setCellFactory(TextFieldTableCell.forTableColumn());
		col.setEditable(false);
		col.setPrefWidth(200);

		tv.getColumns().add(col);

		col = new TableColumn<>("Value");
		col.setPrefWidth(200);
		col.setCellValueFactory(cellData -> {
			return cellData.getValue().getValue();
		});
		col.setCellFactory(TextFieldTableCell.forTableColumn());
		tv.getColumns().add(col);

		tv.getItems().addAll(fields);

		VBox subvb = new VBox();
		FlowPane topfp = new FlowPane();
		topfp.setPadding(new Insets(8, 5, 8, 8));
		Label lb = new Label();
		lb.setGraphic(ImageViewGenerator.svgImageDefActive("search"));
		TextField filterField = new TextField();

		filterField.getStyleClass().add("myTextField");
		topfp.getChildren().add(lb);
		topfp.setMargin(lb, new Insets(0, 10, 0, 5));
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

		new ModalDialog(subvb, tv , "Table Info");

	}

	public static final void bindTableViewFilter(
			TableView<SqlFieldPo> tableView,
			ObservableList<SqlFieldPo> observableList,
			String newValue) {
		FilteredList<SqlFieldPo> filteredData = new FilteredList<>(observableList, p -> true);
		filteredData.setPredicate(
				   entity -> {
					 boolean tf1 = false;
					 boolean tf2 = false;
					 if( entity.getColumnLabel() != null) {
						 tf1 = entity.getColumnLabel().get().toUpperCase().contains(newValue.toUpperCase());
					 }
					 if( entity.getValue() != null) {
						 tf2 = entity.getValue().get().toUpperCase().contains(newValue.toUpperCase());
					 }
					  
				       
				     return tf1 || tf2 ;
				   }
				 
				
				);
		SortedList<SqlFieldPo> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);
	}

}
