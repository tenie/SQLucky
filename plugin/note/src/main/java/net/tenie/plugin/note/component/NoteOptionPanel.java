package net.tenie.plugin.note.component;

import com.jfoenix.controls.JFXButton;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.note.utility.NoteUtility;

/**
 * button 的设置
 * @author tenie
 *
 */
public class NoteOptionPanel {

	private VBox optionVbox = new VBox();
	private HBox btnsBox = new HBox();
	
	private VBox searchVbox = new VBox();
	private JFXButton openFolderBtn = new JFXButton();

	private JFXButton cleanBtn = new JFXButton();
	private JFXButton newFile = new JFXButton();
	private JFXButton DeleteFile = new JFXButton();
	private JFXButton refresh = new JFXButton();
	private JFXButton search = new JFXButton();
	
	//
	private JFXButton query = new JFXButton();
	//
	private TextField txt  = new TextField();
	//
	private Label lbFT = new Label("File Type");
	private TextField fileType  = new TextField();
	
	private JFXButton showInFolder = new JFXButton();
	
	
	
	public NoteOptionPanel() {
		
		// 搜索面板初始化
		initSearchPanel();
		
		openFolderBtn.setGraphic(ComponentGetter.getIconDefActive("folder-open"));
		openFolderBtn.setTooltip(CommonUtility.instanceTooltip("Open note folder "));
		openFolderBtn.setOnMouseClicked(e->{
			NoteTabTree.filePath =  NoteUtility.openFolder( NoteTabTree.rootNode);
		});
		
		
		newFile.setGraphic(ComponentGetter.getIconDefActive("file-o"));
		newFile.setTooltip(CommonUtility.instanceTooltip("New  file "));
		newFile.setOnMouseClicked(e->{
			NoteUtility.newFile(NoteTabTree.noteTabTreeView, NoteTabTree.rootNode, NoteTabTree.filePath);
		});
		
		
		DeleteFile.setGraphic(ComponentGetter.getIconDefActive("trash"));
		DeleteFile.setTooltip(CommonUtility.instanceTooltip("Delete  file "));
		DeleteFile.setOnMouseClicked(e->{
			NoteUtility.deleteFile(NoteTabTree.noteTabTreeView);
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
			  NoteUtility.refreshAction(NoteTabTree.noteTabTreeView, NoteTabTree.rootNode, NoteTabTree.filePath);
		});
		
		showInFolder.setGraphic(ComponentGetter.getIconDefActive("sign-in")   );
		showInFolder.setTooltip(CommonUtility.instanceTooltip("Show In System folder"));
		showInFolder.setOnMouseClicked(e->{
			NoteUtility.showInSystem(NoteTabTree.noteTabTreeView);
		});
		
		search.setGraphic(ComponentGetter.getIconDefActive("search")   );
		search.setOnMouseClicked(e->{
			CommonUtility.leftHideOrShowSecondOptionBox(optionVbox, searchVbox, txt);
		});
		
		
		
		
		
		btnsBox.getChildren().addAll(openFolderBtn, newFile, DeleteFile, cleanBtn, refresh, search, showInFolder);
		optionVbox.getChildren().add(btnsBox);
	}
	
	
	public void initSearchPanel() {
		// 查询
		query.setGraphic(ComponentGetter.getIconDefActive("search")   );
		query.setOnMouseClicked(e->{
			String ft = fileType.getText().trim();
		
		});
		
		txt.getStyleClass().add("myTextField");
		
		fileType.getStyleClass().add("myTextField");
		fileType.setText("*.*");
		
		GridPane grid = new GridPane();
		grid.add(query, 0, 0);
		grid.add(txt, 1, 0);
		
		grid.add(lbFT, 0, 1);
		grid.add(fileType, 1, 1);
		
		
		searchVbox.getChildren().add(grid);
	}

	public static void main(String[] args) {
//		String fileTyleStr = "*ss.sss";
		String fileTyleStr = "bbbb*ss.sss";
//		String queryStr = fileTyleStr.substring(0, fileTyleStr.lastIndexOf("*"));
//		String queryStr = fileTyleStr.substring(1);
//		System.out.println(queryStr);
		
		String arrStr[] = fileTyleStr.split("\\*");
		String qStr1 = arrStr[0];
		String qStr2 = arrStr[1];
		System.out.println(qStr1 + " | " + qStr2);
		
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



	public VBox getOptionVbox() {
		return optionVbox;
	}


 
	
	
	
}
