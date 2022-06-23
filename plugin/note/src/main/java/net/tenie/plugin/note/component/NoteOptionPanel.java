package net.tenie.plugin.note.component;

import com.jfoenix.controls.JFXButton;

import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.myEvent;
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
	private VBox searchVbox2 = new VBox();
	
	private JFXButton openFolderBtn = new JFXButton();

//	private JFXButton cleanBtn = new JFXButton();
	private JFXButton newFile = new JFXButton();
	private JFXButton DeleteFile = new JFXButton();
//	private JFXButton refresh = new JFXButton();
	
	//
	private JFXButton query = new JFXButton();
	//
	public static  TextField txt  = new TextField();
	//
	private Label lbFT = new Label("Type");
	private TextField fileType  = new TextField();
	
	private JFXButton showInFolder = new JFXButton();
	
	
	
	// 搜索panel
	// 查询的上下按钮
	JFXButton up = new JFXButton();
	JFXButton down = new JFXButton();
	
	
	
	public NoteOptionPanel() {
		
		// 搜索面板初始化
		initSearchPanel();
		
		openFolderBtn.setGraphic(ComponentGetter.getIconDefActive("folder-open"));
		openFolderBtn.setTooltip(CommonUtility.instanceTooltip("Import note folder "));
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
		
		
//		cleanBtn.setGraphic(ComponentGetter.getIconDefActive("eraser")   );
//		cleanBtn.setTooltip(CommonUtility.instanceTooltip("Close note folder"));
//		cleanBtn.setOnMouseClicked(e->{
//			NoteTabTree.filePath = NoteUtility.cleanAction(NoteTabTree.rootNode);
//		});
		
		
		//refresh
//		refresh.setGraphic(ComponentGetter.getIconDefActive("refresh")   );
//		refresh.setTooltip(CommonUtility.instanceTooltip("Reload note folder"));
//		refresh.setOnMouseClicked(e->{
//			  NoteUtility.refreshAction(NoteTabTree.noteTabTreeView, NoteTabTree.rootNode, NoteTabTree.filePath);
//		});
		
		showInFolder.setGraphic(ComponentGetter.getIconDefActive("sign-in")   );
		showInFolder.setTooltip(CommonUtility.instanceTooltip("Show In System folder"));
		showInFolder.setOnMouseClicked(e->{
			NoteUtility.showInSystem(NoteTabTree.noteTabTreeView);
		});
		
//		search.setGraphic(ComponentGetter.getIconDefActive("search")   );
//		search.setOnMouseClicked(e->{
//			CommonUtility.leftHideOrShowSecondOptionBox(optionVbox, searchVbox, txt);
//		});
		
		MenuButton searchBtn = new MenuButton();
		searchBtn.setGraphic(IconGenerator.svgImageDefActive("search-plus"));
		searchBtn.setTooltip(MyTooltipTool.instance("Serarch"));
		// 文件名搜索
		MenuItem showQueryFileName = new MenuItem("Search file name ");
		showQueryFileName.setGraphic(IconGenerator.svgImageDefActive("search"));
		showQueryFileName.setOnAction(e->{
			showFileNameSearch();
		});
		
		// 文件内容搜索  
		MenuItem showQueryFileText = new MenuItem("Search file text ");
		showQueryFileText.setGraphic(IconGenerator.svgImageDefActive("file-text-o"));
		showQueryFileText.setOnAction(e -> {
			showFileTextSearch();
		});
		
		searchBtn.getItems().addAll(showQueryFileName, showQueryFileText);
		
		
		
		btnsBox.getChildren().addAll(openFolderBtn, newFile, DeleteFile, showInFolder, searchBtn);
		optionVbox.getChildren().add(btnsBox);
	}
	
	
	public void initSearchPanel() {
		// 查询btn
		query.setGraphic(ComponentGetter.getIconDefActive("search")   );
		query.setOnMouseClicked(e->{
			//TODO
			NoteUtility.searchAction(txt.getText().trim(), fileType.getText().trim());
		});
		// 查询文本
		txt.getStyleClass().add("myTextField");
		txt.textProperty().addListener((o, oldStr, newStr) -> {
			if(StrUtils.isNullOrEmpty(newStr)) {
				NoteTabTree.noteTabTreeView.setRoot(NoteUtility.rootCache);
			}
			
		});
		
		// 回车后触发查询按钮
		txt.setOnKeyPressed(val->{
			 if(val.getCode() == KeyCode.ENTER ){ 
				 myEvent.btnClick(query);
			 }
		});
		// 文件类型
		fileType.getStyleClass().add("myTextField");
		fileType.setText("*.*");  
		
		fileType.textProperty().addListener((o, oldStr, newStr) -> {
			if(NoteUtility.rootCache != null &&  !NoteTabTree.noteTabTreeView.getRoot().equals(NoteUtility.rootCache)) {
				NoteTabTree.noteTabTreeView.setRoot(NoteUtility.rootCache);
				NoteTabTree.noteTabTreeView.getSelectionModel().select(0);
				
			}
		});
		
		fileType.setOnKeyPressed(val->{
			 if(val.getCode() == KeyCode.ENTER ){ 
				 myEvent.btnClick(query);
			 }
		});
		
		
		// 上下查找btn
		down.setGraphic(IconGenerator.svgImageDefActive("arrow-down"));
		down.setOnAction(v -> {
			NoteUtility.downBtnChange();
		});

		up.setGraphic(IconGenerator.svgImageDefActive("arrow-up"));
		up.setOnAction(v -> {
			 
		});
	
	}

	
	// 文件名搜索展示
	public void showFileNameSearch() {
		if (optionVbox.getChildren().contains(searchVbox)) {
			optionVbox.getChildren().remove(searchVbox);
			NoteUtility.isFile = false;
			txt.clear();
			return ;
		}else if(optionVbox.getChildren().contains(searchVbox2)){
			optionVbox.getChildren().remove(searchVbox2);
			txt.clear();
		}
		GridPane grid = new GridPane();
		grid.add(query, 0, 0);
		grid.add(txt, 1, 0);
		searchVbox.getChildren().clear();
		searchVbox2.getChildren().clear();
		searchVbox.getChildren().add(grid);
		NoteUtility.isFile = true;
		NoteUtility.isText = false;
		CommonUtility.leftHideOrShowSecondOptionBox(optionVbox, searchVbox, txt);
	}
	
	// 文件内容搜索展示
	public void showFileTextSearch() {
		// 已经存在就隐藏
		if (optionVbox.getChildren().contains(searchVbox2)) {
			optionVbox.getChildren().remove(searchVbox2);
			NoteUtility.isText = false;
			txt.clear();
			return;

		} else if (optionVbox.getChildren().contains(searchVbox)) {
			optionVbox.getChildren().remove(searchVbox);
			txt.clear();
		}
		GridPane grid2 = new GridPane();
		grid2.add(query, 0, 0);
		grid2.add(txt, 1, 0);
		grid2.add(down, 2, 0);
		grid2.add(up, 3, 0);

		grid2.add(lbFT, 0, 1);
		grid2.add(fileType, 1, 1);
		searchVbox.getChildren().clear();
		searchVbox2.getChildren().clear();
		searchVbox2.getChildren().add(grid2);
		NoteUtility.isFile = false;
		NoteUtility.isText = true;
		CommonUtility.leftHideOrShowSecondOptionBox(optionVbox, searchVbox2, txt);

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
