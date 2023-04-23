package net.tenie.fx.plugin;

import org.controlsfx.control.tableview2.FilteredTableView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;

public class PluginManageWindow {
	private VBox pluginManageBox = new VBox();
	private FlowPane SearchPane = new FlowPane();
	private JFXButton searchBtn = new JFXButton("Search Plugin");
	private JFXTextField searchText = new JFXTextField();

	private VBox  pluginBox = new VBox();

	// 描述
	private MyCodeArea describe = new MyCodeArea();

	// 操作面板(下载, 退出)
	private FlowPane optionPane = new FlowPane();
	// 下载按钮
	private JFXButton download = new JFXButton("Download");
	private JFXButton disable = new JFXButton("Disable");
	private JFXButton enable = new JFXButton("Enable");

	private JFXButton delete = new JFXButton("Delete");
	// 同步服务器插件
	private JFXButton sync = new JFXButton(" 同步服务器插件");
	
	// 所有插件表
	SheetTableData sheetDaV = null;
	FilteredTableView<ResultSetRowPo> allPluginTable = null;



	public PluginManageWindow() {
		searchBtn.setGraphic(IconGenerator.svgImageDefActive("search"));
		searchText.getStyleClass().add("myTextField");
		// 回车后触发查询按钮
		searchText.setOnKeyPressed(val->{
			 if(val.getCode() == KeyCode.ENTER ){ 
				 PluginManageAction.queryAction(searchText.getText() , sheetDaV , allPluginTable);
			 }
		});
		searchBtn.setOnMouseClicked(e->{
			PluginManageAction.queryAction(searchText.getText(), sheetDaV , allPluginTable);
		});
		
		// 同步按钮
		sync.getStyleClass().add("myAlertBtn");
		sync.setOnAction(e->{
			PluginManageAction.queryServerPluginInfo(sheetDaV , allPluginTable);
		});
		SearchPane.getChildren().addAll(searchBtn, searchText , sync);
		SearchPane.setMinHeight(35);
		SearchPane.setPrefHeight(35);
		SearchPane.getStyleClass().add("topPadding5");
		SearchPane.setHgap(10); // 横向间距

		// 操作面板
		optionPane.setHgap(10); // 横向间距
		optionPane.getChildren().addAll(download, disable, enable, delete);
		optionPane.setMinHeight(35);
		optionPane.setPrefHeight(35);
		optionPane.getStyleClass().add("topPadding5");
		initBtn();
		
		describe.setPrefHeight(80);
		describe.setMinHeight(80);
		describe.setEditable(false);
		pluginManageBox.getChildren().addAll(SearchPane, pluginBox, describe, optionPane);
		VBox.setVgrow(pluginBox, Priority.ALWAYS);
		

		sync.getStyleClass().add("myAlertBtn");
		download.getStyleClass().add("myAlertBtn");
		disable.getStyleClass().add("myAlertBtn");
		enable.getStyleClass().add("myAlertBtn");
		delete.getStyleClass().add("myAlertBtn");
		searchBtn.getStyleClass().add("myAlertBtn");

	}
	
	public void initBtn() {
		download.setGraphic(IconGenerator.svgImageDefActive("cloud-download"));
		
		disable.setGraphic(IconGenerator.svgImageDefActive("toggle-off"));
		enable.setGraphic(IconGenerator.svgImageDefActive("toggle-on"));
		delete.setGraphic(IconGenerator.svgImageDefActive("trash"));
		
		disable.setOnAction(e->{
			PluginManageAction.enableOrDisableAction(false, allPluginTable);
		});

		enable.setOnAction(e->{
			PluginManageAction.enableOrDisableAction(true, allPluginTable);
		});
		
		download.setOnAction(e->{
			PluginManageAction.downloadPlugin(sheetDaV, allPluginTable);
		});
		
		delete.setOnAction(e->{
			PluginManageAction.deletePluginAction(sheetDaV, allPluginTable);
		});
		
		disable.setDisable(true);
		enable.setDisable(true);
		download.setDisable(true);
		delete.disableProperty().bind(download.disableProperty().not());
		delete.visibleProperty().bind(ConfigVal.SQLUCKY_VIP);
		 
	}
	
   
	
	// 显示窗口
	public void show() {
		PluginManageAction.createTable(this, pluginBox, describe, enable, disable, download);
		var stage = CreateModalWindow(pluginManageBox);
		stage.show();
		searchText.requestFocus();
	}
	
	// 创建一个窗体
	public static Stage CreateModalWindow(VBox vb) {
//		Stage	stage = new Stage();
		vb.getStyleClass().add("myPluginManager-vbox");

//		Scene scene = new Scene(vb);
		
		vb.setPrefWidth(720);
		vb.maxWidth(720);

		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		
		SqluckyStage sqlStage = new SqluckyStage(vb);
		Stage	stage = sqlStage.getStage();
		Scene scene = sqlStage.getScene();
		
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});

//		CommonUtility.loadCss(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
//		stage.setScene(scene);
		
//		stage.getIcons().add(ComponentGetter.LogoIcons);
		stage.setMaximized(false);
		stage.setResizable(false);
		stage.setOnHidden(e->{
		});
		return stage;
	}


	
	public SheetTableData getSheetDaV() {
		return sheetDaV;
	}

	public void setSheetDaV(SheetTableData sheetDaV) {
		this.sheetDaV = sheetDaV;
	}

	public FilteredTableView<ResultSetRowPo> getAllPluginTable() {
		return allPluginTable;
	}

	public void setAllPluginTable(FilteredTableView<ResultSetRowPo> allPluginTable) {
		this.allPluginTable = allPluginTable;
	}
}
