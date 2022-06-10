package net.tenie.plugin.note.component;

import com.jfoenix.controls.JFXButton;

import javafx.scene.layout.HBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.plugin.note.utility.NoteUtility;

public class NoteOptionPanel {

	private HBox optionHbox = new HBox();
	private JFXButton openFolderBtn = new JFXButton();

	private JFXButton cleanBtn = new JFXButton();
	private JFXButton newFile = new JFXButton();
	private JFXButton DeleteFile = new JFXButton();
	private JFXButton refresh = new JFXButton();
	
	
	public NoteOptionPanel() {
		openFolderBtn.setGraphic(ComponentGetter.getIconDefActive("folder-open"));
		openFolderBtn.setTooltip(CommonUtility.instanceTooltip("Open note folder "));
		openFolderBtn.setOnMouseClicked(e->{
			NoteTabTree.filePath =  NoteUtility.openFolder( NoteTabTree.rootNode);
		});
		
		
		newFile.setGraphic(ComponentGetter.getIconDefActive("file-o"));
		newFile.setTooltip(CommonUtility.instanceTooltip("New  file "));
		newFile.setOnMouseClicked(e->{
			NoteUtility.newFile(NoteTabTree.NoteTabTreeView, NoteTabTree.rootNode, NoteTabTree.filePath);
		});
		
		
		DeleteFile.setGraphic(ComponentGetter.getIconDefActive("trash"));
		DeleteFile.setTooltip(CommonUtility.instanceTooltip("Delete  file "));
		DeleteFile.setOnMouseClicked(e->{
			NoteUtility.deleteFile(NoteTabTree.NoteTabTreeView);
		});
		
		
		cleanBtn.setGraphic(ComponentGetter.getIconDefActive("eraser")   );
		cleanBtn.setTooltip(CommonUtility.instanceTooltip("Close note folder"));
		cleanBtn.setOnMouseClicked(e->{
			NoteTabTree.filePath = NoteUtility.cleanAction(NoteTabTree.rootNode);
		});
		
		
		//refresh
		refresh.setGraphic(ComponentGetter.getIconDefActive("refresh")   );
		refresh.setTooltip(CommonUtility.instanceTooltip("Reload note folder"));
		refresh.setOnMouseClicked(e->{
			  NoteUtility.refreshAction(NoteTabTree.NoteTabTreeView, NoteTabTree.rootNode, NoteTabTree.filePath);
		});
		
		
		optionHbox.getChildren().addAll(openFolderBtn, newFile, DeleteFile, cleanBtn, refresh);
	}


	public HBox getOptionHbox() {
		return optionHbox;
	}


	public void setOptionHbox(HBox optionHbox) {
		this.optionHbox = optionHbox;
	}


	public JFXButton getOpenFolderBtn() {
		return openFolderBtn;
	}


	public void setOpenFolderBtn(JFXButton openFolderBtn) {
		this.openFolderBtn = openFolderBtn;
	}


	public JFXButton getNewFile() {
		return newFile;
	}


	public void setNewFile(JFXButton newFile) {
		this.newFile = newFile;
	}
	
	
	
	
}