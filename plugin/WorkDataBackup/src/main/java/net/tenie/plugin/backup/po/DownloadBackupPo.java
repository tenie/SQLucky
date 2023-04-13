package net.tenie.plugin.backup.po;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import net.tenie.plugin.backup.component.WorkDataBackupAction;

/**
 */
public class DownloadBackupPo {
	private SimpleStringProperty idVal = new SimpleStringProperty();
	private SimpleStringProperty nameVal = new SimpleStringProperty();
	private File bakFile;
	
	Map<String, File> allFile;
	
	public Map<String, File> fileDetail() {
		if(allFile != null) {
			return allFile;
		}
		allFile =  new HashMap<>();
		if(bakFile !=null && bakFile.exists()
		 && nameVal != null && nameVal.get() != null && !"".equals(nameVal.get())) {
			allFile = WorkDataBackupAction.unZipBackupFile(this.bakFile, nameVal.get());
			return   allFile;
		}
		return  this.allFile;
	}
	
	public SimpleStringProperty getIdVal() {
		return idVal;
	}
	public void setIdVal(SimpleStringProperty idVal) {
		this.idVal = idVal;
	}
	public SimpleStringProperty getNameVal() {
		return nameVal;
	}
	public void setNameVal(SimpleStringProperty nameVal) {
		this.nameVal = nameVal;
	}
	public File getBakFile() {
		return bakFile;
	}
	public void setBakFile(File bakFile) {
		this.bakFile = bakFile;
	}
	
	
}
