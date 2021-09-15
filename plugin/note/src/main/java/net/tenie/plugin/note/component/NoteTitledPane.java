package net.tenie.plugin.note.component;

import javafx.scene.control.TitledPane;

public class NoteTitledPane {
	public static TitledPane createTitledPane() {
		TitledPane dbTitledPane = new TitledPane();
		dbTitledPane.setText("DB Config"); 
//		dbTitledPane.setGraphic( ImageViewGenerator.svgImageDefActive("info-circle", 14)); 
//		CommonAction.addCssClass(dbTitledPane, "titledPane-color");
		dbTitledPane.getStyleClass().add("titledPane-color");
//		dbTitledPane.setContent(  );
		
		
		return dbTitledPane;
	}
	
	 
}
