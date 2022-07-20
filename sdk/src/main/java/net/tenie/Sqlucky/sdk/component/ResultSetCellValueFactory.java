package net.tenie.Sqlucky.sdk.component;

import javafx.beans.NamedArg;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;

/**
 * 
 * @author tenie
 *
 */
public class ResultSetCellValueFactory implements Callback<CellDataFeatures<ResultSetRowPo, String>, ObservableValue<String>>{
	 private final int idx;
	 
	 public ResultSetCellValueFactory(final @NamedArg("idx") int idx) {
	        this.idx = idx;
	    }
	@Override
	public ObservableValue<String> call(CellDataFeatures<ResultSetRowPo, String> param) {
		ResultSetRowPo ls = param.getValue();
		var rpo =  ls.getRowDatas();
		ResultSetCellPo celpo =	rpo.get(idx);
		StringProperty sp = celpo.getCellData(); 
		return sp; 
	}

}
