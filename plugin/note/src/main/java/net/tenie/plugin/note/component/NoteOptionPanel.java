package net.tenie.plugin.note.component;

import com.jfoenix.controls.JFXButton;

import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.SqluckyTitledPane;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.myEvent;
import net.tenie.plugin.note.utility.NoteSearchAction;
import net.tenie.plugin.note.utility.NoteUtility;

/**
 * button 的设置
 * 
 * @author tenie
 *
 */
public class NoteOptionPanel extends VBox{
	private HBox btnsBox = new HBox();

	private VBox searchVbox = new VBox();
//	private VBox searchVbox2 = new VBox();
	private JFXButton newFile = new JFXButton();
	private JFXButton DeleteFile = new JFXButton();

	private JFXButton query = new JFXButton();
	public static TextField txt = new TextField();
	private Label lbFT = new Label("Type");
	private TextField fileType = new TextField();

	private JFXButton showInFolder = new JFXButton();
	private JFXButton stopbtn = new JFXButton();
	private JFXButton hideBtn = new JFXButton();

	// 当前显示的 GridPane
	private GridPane currentShowGridPane;
	NoteSearchAction noteSearchAction = new NoteSearchAction();
	// 搜索panel
	// 查询的上下按钮
	JFXButton up = new JFXButton();
	JFXButton down = new JFXButton();
	AnchorPane txtAnchorPane ;
	AnchorPane fileTypeAnchorPane;
	public NoteOptionPanel(SqluckyTitledPane sqluckyTitledPane) {
		// 搜索面板初始化
//		initSearchPanel();
		query.setGraphic(ComponentGetter.getIconDefActive("search"));
		query.setOnMouseClicked(e -> {
			noteSearchAction.searchAction(txt.getText().trim(), fileType.getText().trim(), down, up, stopbtn);
		});
		// 查询文本
		txt.getStyleClass().add("myTextField");
		txtAnchorPane = UiTools.textFieldAddCleanBtn(txt, 200.0);
		txt.textProperty().addListener((o, oldStr, newStr) -> {
			if (noteSearchAction.rootCache != null && !NoteTabTree.noteTabTreeView.getRoot().equals(noteSearchAction.rootCache)) {
				NoteTabTree.noteTabTreeView.setRoot(noteSearchAction.rootCache);
				NoteTabTree.noteTabTreeView.getSelectionModel().select(0);
				down.setDisable(true);
				up.setDisable(true);
				stopbtn.setDisable(true);
			}

			if (StrUtils.isNullOrEmpty(newStr)) {
				NoteTabTree.noteTabTreeView.setRoot(noteSearchAction.rootCache);
				down.setDisable(true);
				up.setDisable(true);
				stopbtn.setDisable(true);
			}

		});

		// 回车后触发查询按钮
		txt.setOnKeyPressed(val -> {
			if (val.getCode() == KeyCode.ENTER) {
				myEvent.btnClick(query);
			}
		});
		// 文件类型
		fileType.getStyleClass().add("myTextField");
		fileType.setText("*.*");
		fileTypeAnchorPane = UiTools.textFieldAddCleanBtn(fileType, 200.0);
		fileType.textProperty().addListener((o, oldStr, newStr) -> {
			if (noteSearchAction.rootCache != null && !NoteTabTree.noteTabTreeView.getRoot().equals(noteSearchAction.rootCache)) {
				NoteTabTree.noteTabTreeView.setRoot(noteSearchAction.rootCache);
				NoteTabTree.noteTabTreeView.getSelectionModel().select(0);
				down.setDisable(true);
				up.setDisable(true);
				stopbtn.setDisable(true);

			}
		});

		fileType.setOnKeyPressed(val -> {
			if (val.getCode() == KeyCode.ENTER) {
				myEvent.btnClick(query);
			}
		});

		// 上下查找btn
		down.setGraphic(IconGenerator.svgImageDefActive("arrow-down"));
		down.setDisable(true);
		down.setTooltip(MyTooltipTool.instance("Search next"));
		down.setOnAction(v -> {
			NoteUtility.downUpBtnChange(false, txt.getText());
		});

		up.setGraphic(IconGenerator.svgImageDefActive("arrow-up"));
		up.setDisable(true);
		up.setTooltip(MyTooltipTool.instance("Search previous"));
		up.setOnAction(v -> {
			NoteUtility.downUpBtnChange(true, txt.getText());
		});

		stopbtn.setGraphic(IconGenerator.svgImage("stop", "red"));
		stopbtn.setDisable(true);
		stopbtn.setTooltip(MyTooltipTool.instance("Stop search"));
		stopbtn.setOnAction(v -> {
			noteSearchAction.stopSearch();
		});

		hideBtn.setGraphic(IconGenerator.svgImageDefActive("window-close"));
		hideBtn.setTooltip(MyTooltipTool.instance("Close"));
		hideBtn.setOnAction(v -> {
			hideSearchBox();
		});
		MenuButton openBtn = new MenuButton();
		openBtn.setGraphic(ComponentGetter.getIconDefActive("folder-open"));

		MenuItem openFolderBtn = new MenuItem("Import Folder");
		openFolderBtn.setGraphic(IconGenerator.svgImageDefActive("folder-open"));
		openFolderBtn.setOnAction(e -> {
			NoteTabTree.filePath = NoteUtility.openFolder(NoteTabTree.rootNode);
		});
		MenuItem openFileBtn = new MenuItem("Import File");
		openFileBtn.setGraphic(IconGenerator.svgImageDefActive("file-text-o"));
		openFileBtn.setOnAction(e -> {
			NoteTabTree.filePath = NoteUtility.openFile();
		});
		openBtn.getItems().addAll(openFolderBtn, openFileBtn);

//		openFolderBtn.setGraphic(ComponentGetter.getIconDefActive("folder-open"));
//		openFolderBtn.setTooltip(CommonUtils.instanceTooltip("Import Note Folder "));
//		openFolderBtn.setOnMouseClicked(e -> {
//			NoteTabTree.filePath = NoteUtility.openFolder(NoteTabTree.rootNode);
//		});
//
//		openFileBtn.setGraphic(ComponentGetter.getIconDefActive("folder-open"));
//		openFileBtn.setTooltip(CommonUtils.instanceTooltip("Import Note File "));
//		openFileBtn.setOnMouseClicked(e -> {
//			NoteTabTree.filePath = NoteUtility.openFile();
//		});

		newFile.setGraphic(ComponentGetter.getIconDefActive("file-o"));
		newFile.setTooltip(CommonUtils.instanceTooltip("New  File "));
		newFile.setOnMouseClicked(e -> {
			NoteUtility.newFile(NoteTabTree.noteTabTreeView, NoteTabTree.rootNode, NoteTabTree.filePath);
		});

		DeleteFile.setGraphic(ComponentGetter.getIconDefActive("trash"));
		DeleteFile.setTooltip(CommonUtils.instanceTooltip("Delete  File "));
		DeleteFile.setOnMouseClicked(e -> {
			NoteUtility.deleteFile(NoteTabTree.noteTabTreeView);
		});

		showInFolder.setGraphic(ComponentGetter.getIconDefActive("sign-in"));
		showInFolder.setTooltip(CommonUtils.instanceTooltip("Show In System Folder"));
		showInFolder.setOnMouseClicked(e -> {
			NoteUtility.showInSystem(NoteTabTree.noteTabTreeView);
		});
		showInFolder.setDisable(true);

		MenuButton searchBtn = new MenuButton();
		searchBtn.setGraphic(IconGenerator.svgImageDefActive("search-plus"));
		searchBtn.setTooltip(MyTooltipTool.instance("Serarch"));
		// 文件名搜索
		MenuItem showQueryFileName = new MenuItem("Search file name ");
		showQueryFileName.setGraphic(IconGenerator.svgImageDefActive("search"));
		showQueryFileName.setOnAction(e -> {
			hideSearchBox();
			showFileNameSearch();
		});

		// 全局快捷键显示
		sqluckyTitledPane.setShowFinder(s -> {
			hideSearchBox();
			showFileNameSearch();
		});

		// 全局的快捷键隐藏
		sqluckyTitledPane.setHideFinder(s -> {
			hideSearchBox();
		});

		// 文件内容搜索
		MenuItem showQueryFileText = new MenuItem("Search file text ");
		showQueryFileText.setGraphic(IconGenerator.svgImageDefActive("file-text-o"));
		showQueryFileText.setOnAction(e -> {
			hideSearchBox();
			showFileTextSearch();
		});

		searchBtn.getItems().addAll(showQueryFileName, showQueryFileText);

		btnsBox.getChildren().addAll(openBtn, newFile, DeleteFile, showInFolder, searchBtn);
		this.getChildren().add(btnsBox);
	}


	// 隐藏查询面板
	public void hideSearchBox(){
		if (this.getChildren().contains(searchVbox)) {
			this.getChildren().remove(searchVbox);
			txt.clear();
		}
	}

	// 文件名搜索展示
	public void showFileNameSearch() {
		currentShowGridPane = new GridPane();
		currentShowGridPane.add(query, 0, 0);
		currentShowGridPane.add(txtAnchorPane, 1, 0);
		currentShowGridPane.add(stopbtn, 2, 0);
		currentShowGridPane.add(hideBtn, 3, 0);


		searchVbox.getChildren().clear();
		searchVbox.getChildren().add(currentShowGridPane);
		noteSearchAction.isFile = true;
		noteSearchAction.isText = false;
		CommonUtils.leftHideOrShowSecondOperateBox(this, searchVbox, txt);
	}

	// 文件内容搜索展示
	public void showFileTextSearch() {
		currentShowGridPane= new GridPane();
		currentShowGridPane.add(query, 0, 0);
		currentShowGridPane.add(txtAnchorPane, 1, 0);
		currentShowGridPane.add(stopbtn, 2, 0);
		currentShowGridPane.add(hideBtn, 3, 0);

		currentShowGridPane.add(lbFT, 0, 1);
		currentShowGridPane.add(fileTypeAnchorPane, 1, 1);
		currentShowGridPane.add(down, 2, 1);
		currentShowGridPane.add(up, 3, 1);
		searchVbox.getChildren().clear();
		searchVbox.getChildren().add(currentShowGridPane);
		noteSearchAction.isFile = false;
		noteSearchAction.isText = true;
		CommonUtils.leftHideOrShowSecondOperateBox(this, searchVbox, txt);
	}


	public JFXButton getShowInFolder() {
		return showInFolder;
	}

}
