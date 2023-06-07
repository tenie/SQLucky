package net.tenie.fx.window;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.TableViewUtil;
import net.tenie.Sqlucky.sdk.utility.TextFieldSetup;
import net.tenie.fx.component.UserAccount.UserAccountAction;
import net.tenie.fx.plugin.PluginManageAction;
import net.tenie.fx.plugin.PluginManageWindow;

/**
 * 快捷键绑定
 * @author tenie
 *
 */
public class KeysBindWindow {
	private VBox keysManageBox = new VBox();
	private FlowPane SearchPane = new FlowPane();
	private JFXButton searchBtn = new JFXButton("Search");
	private JFXTextField searchText = new JFXTextField();

	private VBox  keysBox = new VBox();
	
	private Stage stage;
	
	// 设置按钮
	JFXButton bindingBtn = new JFXButton("Binding");
	SheetTableData sheetDaV = null;
	FilteredTableView<ResultSetRowPo> allkeysTable = null;



	public KeysBindWindow() {
		searchBtn.setGraphic(IconGenerator.svgImageDefActive("search"));
		searchText.getStyleClass().add("myTextField");
		// 回车后触发查询按钮
		searchText.setOnKeyPressed(val->{
			 
		});
		searchBtn.setOnMouseClicked(e->{ 
		});
		
		SearchPane.getChildren().addAll(searchBtn, searchText );
		SearchPane.setMinHeight(35);
		SearchPane.setPrefHeight(35);
		SearchPane.getStyleClass().add("topPadding5");
		SearchPane.setHgap(10); // 横向间距
		
		
		// 绑定按钮
		bindingBtn.getStyleClass().add("myAlertBtn");
		bindingBtn.setDisable(true);
		
		
		HBox bindingBox = new HBox();
		bindingBox.getChildren().add(bindingBtn);
		HBox.setMargin(bindingBtn, new Insets(10));
		
		// 关闭按钮
		JFXButton closeBtn = new JFXButton("Close");
		closeBtn.getStyleClass().add("myAlertBtn");
		
		closeBtn.setOnAction(e->{
			stage.close();
		});
		
		AnchorPane bottomPane = new AnchorPane(); 
		bottomPane.getChildren().add(closeBtn);
		AnchorPane.setRightAnchor(closeBtn, 33.3);
		AnchorPane.setBottomAnchor(closeBtn, 10.0);
		
		keysManageBox.getChildren().addAll(SearchPane, keysBox, bindingBox, bottomPane);
		VBox.setVgrow(keysBox, Priority.ALWAYS);
		
		searchBtn.getStyleClass().add("myAlertBtn");

	}
	
   
	
	// 显示窗口
	public void show() {
		createTable( keysBox);
		stage = CreateModalWindow(keysManageBox);
		stage.show();
		searchText.requestFocus();
	}
	String sql = "select" 
			+ " ID , "
			+ " ACTION_NAME, "
			+ " BINDING" 
			+ " from KEYS_BINDING ";
	
	public   void createTable( VBox  keysBox) {
		Connection conn = SqluckyAppDB.getConn();
		
		try {
			List<String> hiddenCol = new ArrayList<>();
			hiddenCol.add("ID");
		    // 查询
			SheetTableData sheetDaV   = TableViewUtil.sqlToSheet(sql, conn, "KEYS_BINDING", null, hiddenCol );
			// 获取表
			FilteredTableView<ResultSetRowPo>  allkeysTable = sheetDaV.getInfoTable();
			  this.setSheetDaV(sheetDaV);
//			  this.allkeysTable(allkeysTable);
			// 表不可编辑
			allkeysTable.editableProperty().bind(new SimpleBooleanProperty(false));
			 
			// 表放入界面
			keysBox.getChildren().add(allkeysTable);
			
			
			// 行选中时间
			allkeysTable.getSelectionModel().selectedItemProperty().addListener((o, old, nnew)->{
				if(nnew !=null) {
					String idField = nnew.getValueByFieldName("ID");
					String bindingField = nnew.getValueByFieldName("BINDING");
					System.out.println("idField = " + idField);
					bindingBtn.setDisable(false);
					bindingBtn.setOnAction(e->{
						KeyBindingSubWindow.show(idField, bindingField, v->{
							 udateTable();
						});

						
					});
				}
			} );
			
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
	// 更新界面
	public void udateTable() {
		Connection conn = SqluckyAppDB.getConn();
		try {
			List<String> hiddenCol = new ArrayList<>();
			hiddenCol.add("ID");
			SheetTableData sheetDaV2 = TableViewUtil.sqlToSheet(sql, conn, "KEYS_BINDING", null, hiddenCol);
			this.setSheetDaV(sheetDaV2);
			FilteredTableView<ResultSetRowPo> allkeysTable2 = sheetDaV2.getInfoTable();
			allkeysTable2.editableProperty().bind(new SimpleBooleanProperty(false));
			keysBox.getChildren().clear();
			// 表放入界面
			keysBox.getChildren().add(allkeysTable2);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
	// 创建一个窗体
	public static Stage CreateModalWindow(VBox vb) {
		vb.getStyleClass().add("myPluginManager-vbox");
		vb.setPrefWidth(400);
		vb.maxWidth(400);

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

		stage.initModality(Modality.APPLICATION_MODAL);
		
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

	 
}
