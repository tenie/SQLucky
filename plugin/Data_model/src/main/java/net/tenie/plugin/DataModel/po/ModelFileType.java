package net.tenie.plugin.DataModel.po;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ModelFileType {
	public static final String PDM = ".pdm";
	public static final String CDM = ".cdm";
	public static final String CHNR_JSON = ".chnr.json";
	
	
	static ObservableList<String> all = FXCollections.observableArrayList();
	public static ObservableList<String> allModeFileType() {
		if(all.size() == 0) {
			all.add(CHNR_JSON);
			all.add(PDM);
//			all.add(CDM);
		}
		
		return all;
	}
	
}
