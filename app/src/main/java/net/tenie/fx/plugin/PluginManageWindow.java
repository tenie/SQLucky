package net.tenie.fx.plugin;

import java.sql.Connection;

import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.component.InfoTree.DBinfoTree;

public class PluginManageWindow {
	private VBox pluginManageBox = new VBox();
	private FlowPane SearchPane = new FlowPane();
	private Label searchLb = new Label("Search Plugin");
	private JFXTextField searchText = new JFXTextField();
	private JFXButton searchBtn = new JFXButton("Search");

	// 插件表格面板
	private TabPane pluginTabPane = new TabPane();
	// 所有插件面板
	private Tab allPluginTab = new Tab();
	private Tab installedPluginTab = new Tab();

	// 表

	// 描述
	private MyCodeArea describe = new MyCodeArea();

	// 操作面板(下载, 退出)
	private FlowPane optionPane = new FlowPane();
	// 下载按钮
	private JFXButton download = new JFXButton("Download");

//	private JFXButton close = new JFXButton("Close");

	public PluginManageWindow() {
		searchLb.setGraphic(IconGenerator.svgImageDefActive("search"));
		searchBtn.setGraphic(IconGenerator.svgImageDefActive("search"));
		searchText.getStyleClass().add("myTextField");
		SearchPane.getChildren().addAll(searchLb, searchText, searchBtn);
		

		// 插件表格
		allPluginTab.setText("All plugin ");
		installedPluginTab.setText("Installed plugin");
		pluginTabPane.getTabs().addAll(allPluginTab, installedPluginTab);
		FilteredTableView<ObservableList<StringProperty>> allTable = SdkComponent.creatFilteredTableView();
		FilteredTableView<ObservableList<StringProperty>> installedTable = SdkComponent.creatFilteredTableView();

		allPluginTab.setContent(allTable);
		installedPluginTab.setContent(installedTable);

		// 操作面板
		optionPane.getChildren().addAll(download);

		pluginManageBox.getChildren().addAll(SearchPane, pluginTabPane, describe, optionPane);
		VBox.setVgrow(pluginTabPane, Priority.ALWAYS);

	}
	
	public static Stage CreateModalWindow(VBox vb) {
		Stage	stage = new Stage();
		vb.getStyleClass().add("connectionEditor");

		Scene scene = new Scene(vb);
		
		vb.setPrefWidth(450);
		vb.maxWidth(450);
		AnchorPane bottomPane = new AnchorPane();
		bottomPane.setPadding(new Insets(10));

		vb.getChildren().add(bottomPane);
		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});

		CommonUtility.loadCss(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		
		stage.getIcons().add(ComponentGetter.LogoIcons);
		stage.setMaximized(false);
		stage.setResizable(false);
		stage.setOnHidden(e->{
			//TODO 打开连接
//			if( editLinkStatus) {
//				Platform.runLater(()->{
//					var item = DBinfoTree.getTrewViewCurrentItem();
//					CommonAction.openConn(item);
//				});
//			}
		});
		return stage;
	}
	public void show() {
		var stage = CreateModalWindow(pluginManageBox);
		stage.show();
		String sql = "select * from PLUGIN_INFO";
		Connection conn = SqluckyAppDB.getConn();
		try {
			SdkComponent.sqlToSheet(sql, conn, "PLUGIN_INFO", null);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	
	}
}
