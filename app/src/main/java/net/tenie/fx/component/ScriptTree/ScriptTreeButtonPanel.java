package net.tenie.fx.component.ScriptTree;

import com.jfoenix.controls.JFXButton;

import javafx.scene.layout.HBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

public class ScriptTreeButtonPanel extends HBox{
	private JFXButton importBtn = new JFXButton();

	private JFXButton saveBtn = new JFXButton();
	private JFXButton newFile = new JFXButton();
	private JFXButton DeleteFile = new JFXButton();
	private JFXButton refresh = new JFXButton();

	private JFXButton showInFolder = new JFXButton();

	// 构造
	public ScriptTreeButtonPanel() {
		showInFolder.setGraphic(ComponentGetter.getIconDefActive("sign-in"));
		showInFolder.setTooltip(CommonUtils.instanceTooltip("Show In System folder"));
		showInFolder.setOnMouseClicked(e -> {
			ScriptTreeAction.showInFloder();
		});

		newFile.setGraphic(ComponentGetter.getIconDefActive("file-o"));
		newFile.setTooltip(CommonUtils.instanceTooltip("New file"));
		newFile.setOnMouseClicked(e -> {
//			MyAreaTab.addCodeEmptyTabMethod();
			MyEditorSheetHelper.addEmptyHighLightingEditor();
		});

		importBtn.setGraphic(ComponentGetter.getIconDefActive("folder-open"));
		importBtn.setTooltip(CommonUtils.instanceTooltip("Open note folder "));
		importBtn.setOnMouseClicked(e -> {
			AppCommonAction.openSqlFile();
		});

//		saveBtn.setText("Import");
//		saveBtn.getStyleClass().add("myTxtBtnBorder");
		saveBtn.setGraphic(ComponentGetter.getIconDefActive("save"));
		saveBtn.setTooltip(CommonUtils.instanceTooltip("Save file"));
		saveBtn.setOnMouseClicked(e -> {
			ScriptTreeAction.saveAction();
		});

		this.getChildren().addAll(importBtn, newFile, saveBtn, showInFolder);
	}

	public JFXButton getImportBtn() {
		return importBtn;
	}

	public void setImportBtn(JFXButton importBtn) {
		this.importBtn = importBtn;
	}

	public JFXButton getNewFile() {
		return newFile;
	}

	public void setNewFile(JFXButton newFile) {
		this.newFile = newFile;
	}

	public JFXButton getDeleteFile() {
		return DeleteFile;
	}

	public void setDeleteFile(JFXButton deleteFile) {
		DeleteFile = deleteFile;
	}

	public JFXButton getRefresh() {
		return refresh;
	}

	public void setRefresh(JFXButton refresh) {
		this.refresh = refresh;
	}

	public JFXButton getShowInFolder() {
		return showInFolder;
	}

	public void setShowInFolder(JFXButton showInFolder) {
		this.showInFolder = showInFolder;
	}

}
