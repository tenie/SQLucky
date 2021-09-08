package net.tenie.fx.factory;


import javafx.beans.NamedArg;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
/*   @author tenie */
public class StringPropertyListValueFactory implements Callback<CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>{
	 private final int idx;
	 
	 public StringPropertyListValueFactory(final @NamedArg("idx") int idx) {
	        this.idx = idx;
	    }
	@Override
	public ObservableValue<String> call(CellDataFeatures<ObservableList<StringProperty>, String> param) {
		ObservableList<StringProperty> ls = param.getValue();
		StringProperty value  = ls.get(idx);
		StringProperty sp = value; 
		return sp; 
	}

}
