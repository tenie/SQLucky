package net.tenie.plugin.note.component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.jfoenix.controls.JFXButton;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
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
	private Label lbFT = new Label("Type");
	private TextField fileType  = new TextField();
	
	private JFXButton showInFolder = new JFXButton();
	
	private boolean isFile = false;
	private boolean isText = false;
	
	// 搜索panel
//	GridPane grid = new GridPane();
	// 查询的上下按钮
	JFXButton up = new JFXButton();
	JFXButton down = new JFXButton();
	
	Map<String, ObservableList<TreeItem<SqluckyTab> >>  rootMap = new HashMap<>();
	
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
		
		
		
		btnsBox.getChildren().addAll(openFolderBtn, newFile, DeleteFile, cleanBtn, refresh, showInFolder, searchBtn);
		optionVbox.getChildren().add(btnsBox);
	}
	
	
	public void initSearchPanel() {
		// 查询btn
		query.setGraphic(ComponentGetter.getIconDefActive("search")   );
		query.setOnMouseClicked(e->{
			String ft = fileType.getText().trim();
			File file = NoteUtility.currentTreeItemFile();
			if( file.isDirectory()) {
				TreeItem<SqluckyTab> treeItem = NoteUtility.currentTreeItem();
				treeItem.getChildren();
			}
			
			FileTools.getFileFromDir(file);
		
		});
		// 查询文本
		txt.getStyleClass().add("myTextField");
		// 上下查找btn
		down.setGraphic(IconGenerator.svgImageDefActive("arrow-down"));
		down.setOnAction(v -> {
		 
		});

		up.setGraphic(IconGenerator.svgImageDefActive("arrow-up"));
		up.setOnAction(v -> {
			 
		});
		
		
		
		fileType.getStyleClass().add("myTextField");
		fileType.setText("*.*");
		
		
	
	}

	
	// 文件名搜索展示
	public void showFileNameSearch() {
		if (optionVbox.getChildren().contains(searchVbox)) {
			optionVbox.getChildren().remove(searchVbox);
			isFile = false;
			return ;
		}else if(optionVbox.getChildren().contains(searchVbox2)){
			optionVbox.getChildren().remove(searchVbox2);
		}
		GridPane grid = new GridPane();
		grid.add(query, 0, 0);
		grid.add(txt, 1, 0);
		searchVbox.getChildren().clear();
		searchVbox2.getChildren().clear();
		searchVbox.getChildren().add(grid);
		isFile = true;
		isText = false;
		CommonUtility.leftHideOrShowSecondOptionBox(optionVbox, searchVbox, txt);
	}
	
	// 文件内容搜索展示
	public void showFileTextSearch() {
		// 已经存在就隐藏
		if (optionVbox.getChildren().contains(searchVbox2)) {
			optionVbox.getChildren().remove(searchVbox2);
			isText = false;
			return;

		} else if (optionVbox.getChildren().contains(searchVbox)) {
			optionVbox.getChildren().remove(searchVbox);
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
		isFile = false;
		isText = true;
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
