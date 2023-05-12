package net.tenie.Sqlucky.sdk.component;

import java.util.List;
import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;

/**
 * tabelView 按钮单元格
 * 
 * @author tenie
 */
public class MyTableCellButton {
	private List<MyCellOperateButton>  btns;
	HBox btnBox = new HBox(); 
	public MyTableCellButton(List<MyCellOperateButton> btnvals) {
		this.btns = btnvals;
		if(btns != null ) {
			for(var ob : btns) {
				btnBox.getChildren().add(ob.getBtn());
			}
		}
		
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
									if(btns != null ) {
										for(MyCellOperateButton ob : btns) {
											var btn = ob.getBtn();
											btn.setOnAction(event -> {
												ResultSetRowPo rowpo = getTableView().getItems().get(getIndex());
												ob.getBtnCaller().accept(rowpo);
											});
										}
									}
									 
									setGraphic(btnBox);
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
