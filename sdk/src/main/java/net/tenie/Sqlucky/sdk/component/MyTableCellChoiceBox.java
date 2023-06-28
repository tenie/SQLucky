package net.tenie.Sqlucky.sdk.component;

import java.util.List;

import com.jfoenix.controls.JFXComboBox;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * tabelView 选择框组件
 * 
 * @author tenie
 */
public class MyTableCellChoiceBox<T> {
//	private ChoiceBox<S> excelRow;
	private List<String> vals;

	public MyTableCellChoiceBox(List<String> vals) {

		this.vals = vals;

	}

	public Callback<TableColumn<SheetFieldPo, String>, TableCell<SheetFieldPo, String>> callback() {
		Callback<TableColumn<SheetFieldPo, String>, TableCell<SheetFieldPo, String>> cellFactory = //
				new Callback<TableColumn<SheetFieldPo, String>, TableCell<SheetFieldPo, String>>() {
					@Override
					public TableCell<SheetFieldPo, String> call(final TableColumn<SheetFieldPo, String> param) {
						final TableCell<SheetFieldPo, String> cell = new TableCell<SheetFieldPo, String>() {

							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);

								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									FlowPane fpane = new FlowPane();
									ObservableList<String> obVals = FXCollections.observableArrayList();
									for (String v : vals) {
										String tmp = v;
										obVals.add(tmp);
									}
//									ChoiceBox<String> excelRow = new ChoiceBox<String>(obVals);
									JFXComboBox<String> connsComboBox = new JFXComboBox<String>();
									connsComboBox.setMaxWidth(10);
									connsComboBox.setPrefWidth(10);
									connsComboBox.setItems(obVals);
									connsComboBox.valueProperty().addListener((obj, oldv, newv) -> {
										MyAlert.alertWait(oldv);
										if (StrUtils.isNotNullOrEmpty(newv)) {
											TableRow<SheetFieldPo> tr = this.getTableRow();
											SheetFieldPo rowItem = tr.getItem();
											rowItem.setExcelRowVal(new SimpleStringProperty((String) newv));
											setText(newv);
										}
									});
//									fpane.getChildren().add(connsComboBox);
									setGraphic(connsComboBox);
									setText(null);
								}
							}
						};
						return cell;

					}
				};
		return cellFactory;
	}
}
