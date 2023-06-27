package net.tenie.Sqlucky.sdk.component;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * tabelView 选择框组件
 * 
 * @author tenie
 */
public class MyTableCellChoiceBox<T, S> {
//	private ChoiceBox<S> excelRow;
	private List<S> vals;

	public MyTableCellChoiceBox(List<S> vals) {

		this.vals = vals;

	}

	public Callback<TableColumn<T, S>, TableCell<T, S>> callback() {
		Callback<TableColumn<T, S>, TableCell<T, S>> cellFactory = //
				new Callback<TableColumn<T, S>, TableCell<T, S>>() {
					@Override
					public TableCell<T, S> call(final TableColumn<T, S> param) {
						final TableCell<T, S> cell = new TableCell<T, S>() {

							@Override
							public void updateItem(S item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									FlowPane fpane = new FlowPane();
									ObservableList<S> obVals = FXCollections.observableArrayList(vals);

									ChoiceBox<S> excelRow = new ChoiceBox<S>(obVals);
									excelRow.valueProperty().addListener((obj, oldv, newv) -> {
										if (StrUtils.isNotNullOrEmpty(newv)) {
											TableRow<T> tr = this.getTableRow();
											SheetFieldPo rowItem = (SheetFieldPo) tr.getItem();
											rowItem.setExcelRowVal(new SimpleStringProperty((String) newv));

										}
									});
									fpane.getChildren().add(excelRow);
									setGraphic(fpane);
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
