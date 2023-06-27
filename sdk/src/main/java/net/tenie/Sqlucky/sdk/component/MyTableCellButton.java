package net.tenie.Sqlucky.sdk.component;

import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;

/**
 * tabelView 按钮单元格
 * 
 * @author tenie
 */
public class MyTableCellButton {
	private List<MyCellOperateButton> btns;

	public MyTableCellButton(List<MyCellOperateButton> btnvals) {
		this.btns = btnvals;

	}

	public Callback<TableColumn<ResultSetRowPo, String>, TableCell<ResultSetRowPo, String>> callback() {
		Callback<TableColumn<ResultSetRowPo, String>, TableCell<ResultSetRowPo, String>> cellFactory = //
				new Callback<TableColumn<ResultSetRowPo, String>, TableCell<ResultSetRowPo, String>>() {
					@Override
					public TableCell<ResultSetRowPo, String> call(final TableColumn<ResultSetRowPo, String> param) {
						final TableCell<ResultSetRowPo, String> cell = new TableCell<ResultSetRowPo, String>() {

							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
//									HBox btnBox = new HBox();
									FlowPane fpane = new FlowPane();
									if (btns != null) {
										for (MyCellOperateButton ob : btns) {
											var btnName = ob.getButtonName();
											Button btn = new Button(btnName);
											btn.getStyleClass().add("myAlertBtn");
//											btnBox.getChildren().add(btn);
											fpane.getChildren().add(btn);
											btn.setOnAction(event -> {
												ResultSetRowPo rowpo = getTableView().getItems().get(getIndex());
												ob.getBtnCaller().accept(rowpo);
											});
										}
									}
									fpane.setHgap(5); // 横向间距
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
