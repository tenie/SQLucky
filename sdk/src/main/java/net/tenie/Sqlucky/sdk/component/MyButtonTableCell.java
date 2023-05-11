package net.tenie.Sqlucky.sdk.component;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
/**
 * tabelView 按钮单元格
 * @author tenie 
 */
public class MyButtonTableCell<P, R> implements Callback<P,R>{
	private Button btn;
	public MyButtonTableCell(Button btnval ){
		this.btn = btnval;
	}
	@Override
	public Object call(Object param) {
        final TableCell<ResultSetRowPo, String> cell = new TableCell<>() {

//            final Button btn = new Button("Just Do It");

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                	btn.setOnMouseClicked(e->{
                		ObservableList<ResultSetRowPo> ls =	(ObservableList<ResultSetRowPo>) getTableView().getItems().get(getIndex());
                		ResultSetRowPo po = ls.get(0);
                		
                		MyAlert.infoAlert("", po.toString());
        			});
                    setGraphic(btn);
                    setText(null);
                }
            }
        };
        return cell;
    }
}
