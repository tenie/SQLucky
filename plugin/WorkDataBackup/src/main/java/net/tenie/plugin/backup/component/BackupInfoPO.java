package net.tenie.plugin.backup.component;

import com.jfoenix.controls.JFXCheckBox;
import javafx.scene.control.TextField;

public class BackupInfoPO {
	private String backupName ;
	private String privateKey  ;
	private Boolean saveDB  ;
	private Boolean saveScript  ;
	private Boolean saveModel  ;
	private Boolean usePrivateKey  ;
	
	public BackupInfoPO() {
		
	}
	
	public BackupInfoPO(TextField bakName, TextField privateKey, JFXCheckBox dbCB, JFXCheckBox scriptCB, JFXCheckBox modelCB, JFXCheckBox pkCB) {
		this.backupName = bakName.getText();
		this.privateKey = privateKey.getText();
		this.saveDB = dbCB.isSelected();
		this.saveScript = scriptCB.isSelected();
		this.saveModel = modelCB.isSelected();
		this.usePrivateKey = pkCB.isSelected();
	}

	public String getBackupName() {
		return backupName;
	}

	public void setBackupName(String backupName) {
		this.backupName = backupName;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public Boolean getSaveDB() {
		return saveDB;
	}

	public void setSaveDB(Boolean saveDB) {
		this.saveDB = saveDB;
	}

	public Boolean getSaveScript() {
		return saveScript;
	}

	public void setSaveScript(Boolean saveScript) {
		this.saveScript = saveScript;
	}

	public Boolean getSaveModel() {
		return saveModel;
	}

	public void setSaveModel(Boolean saveModel) {
		this.saveModel = saveModel;
	}

	public Boolean getUsePrivateKey() {
		return usePrivateKey;
	}

	public void setUsePrivateKey(Boolean usePrivateKey) {
		this.usePrivateKey = usePrivateKey;
	}

	@Override
	public String toString() {
		return "BackupInfoPO [backupName=" + backupName + ", privateKey=" + privateKey + ", saveDB=" + saveDB
				+ ", saveScript=" + saveScript + ", saveModel=" + saveModel + ", usePrivateKey=" + usePrivateKey + "]";
	}

	  
	
}
