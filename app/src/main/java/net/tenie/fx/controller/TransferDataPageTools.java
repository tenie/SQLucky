package net.tenie.fx.controller;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import net.tenie.fx.config.DBConns;

public class TransferDataPageTools {
	
	// 设置数据库下拉选的值
	public static void setupDBComboBox(ComboBox<Label> cbox1, ComboBox<Label> cbox2) {
		cbox1.setOnMouseClicked(e->{
			var items = DBConns.getChoiceBoxItems();
			if(items == null || items.size() == 0 ) return;
			if( cbox2.getValue() != null ) {
				String 	taDBVal = cbox2.getValue().getText();
				if(taDBVal != null && taDBVal.length() > 0) {
					for(int i = 0; i < items.size(); i++ ) {
						var itm = items.get(i);
						if( taDBVal.equals(itm.getText() ) ){
							items.remove(i);
							break;
						}
					}
				}
			} 
			
			cbox1.setItems(items);
		});
		
		
		cbox2.setOnMouseClicked(e->{
			var items = DBConns.getChoiceBoxItems();
			if(items == null || items.size() == 0 ) return;
			if( cbox1.getValue() != null ) {
				String 	taDBVal = cbox1.getValue().getText();
				if(taDBVal != null && taDBVal.length() > 0) {
					for(int i = 0; i < items.size(); i++ ) {
						var itm = items.get(i);
						if( taDBVal.equals(itm.getText() ) ){
							items.remove(i);
							break;
						}
					}
				}
			} 
			
			cbox2.setItems(items);
		});
		
		
		
	}
}
