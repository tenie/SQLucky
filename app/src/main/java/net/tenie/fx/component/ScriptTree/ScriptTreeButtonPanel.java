package net.tenie.fx.component.ScriptTree;

import com.jfoenix.controls.JFXButton;

import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.component.MyTab;

public class ScriptTreeButtonPanel {

	private HBox optionHbox = new HBox();
	
	private JFXButton importBtn = new JFXButton();

	private JFXButton saveBtn = new JFXButton();
	private JFXButton newFile = new JFXButton();
	private JFXButton DeleteFile = new JFXButton();
	private JFXButton refresh = new JFXButton();

	private JFXButton showInFolder = new JFXButton();
	
	// 构造
	public ScriptTreeButtonPanel() {
		showInFolder.setGraphic(ComponentGetter.getIconDefActive("sign-in")   );
		showInFolder.setTooltip(CommonUtility.instanceTooltip("Show In System folder"));
		showInFolder.setOnMouseClicked(e->{
			ScriptTreeAction.showInFloder() ;
		});
		
		newFile.setGraphic(ComponentGetter.getIconDefActive("file-o"));
		newFile.setTooltip(CommonUtility.instanceTooltip("New file"));
		newFile.setOnMouseClicked(e->{
			MyTab.addCodeEmptyTabMethod();
		});
		
		importBtn.setGraphic(ComponentGetter.getIconDefActive("folder-open"));
		importBtn.setTooltip(CommonUtility.instanceTooltip("Open note folder "));
		importBtn.setOnMouseClicked(e->{
			CommonAction.openSqlFile("UTF-8");
		});
		
//		saveBtn.setText("Import");
//		saveBtn.getStyleClass().add("myTxtBtnBorder");
		saveBtn.setGraphic(ComponentGetter.getIconDefActive("save"));
		saveBtn.setTooltip(CommonUtility.instanceTooltip("Save file"));
		saveBtn.setOnMouseClicked(e->{
			ScriptTreeAction.saveAction();
		});
		
		
		optionHbox.getChildren().addAll(importBtn, newFile, saveBtn, showInFolder);
	}

	// 按钮面板和treeView 的vbox容器
	public VBox getScriptTitledPaneContent(TreeView<MyTab> scriptTreeView ) {
		VBox box  = new VBox();
		box.getChildren().addAll(optionHbox, scriptTreeView);
		
		box.getStyleClass().add("myTreeView-vbox");
		box.getStyleClass().add("myModalDialog");
		VBox.setVgrow(scriptTreeView, Priority.ALWAYS);
		return box;
	}

	public HBox getOptionHbox() {
		return optionHbox;
	}


	public void setOptionHbox(HBox optionHbox) {
		this.optionHbox = optionHbox;
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
