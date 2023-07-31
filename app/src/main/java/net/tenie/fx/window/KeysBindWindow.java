package net.tenie.fx.window;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtils;

/**
 * 快捷键绑定
 * 
 * @author tenie
 *
 */
public class KeysBindWindow {
	private VBox keysManageBox = new VBox();
	private FlowPane SearchPane = new FlowPane();
	private JFXButton searchBtn = new JFXButton("Search");
	private JFXTextField searchText = new JFXTextField();
	private VBox keysBox = new VBox();

	private Stage stage;
	private static List<String> hiddenCol = new ArrayList<>();

	private static Map<String, Double> fieldWidthMap = new HashMap<>();

	static {
		hiddenCol.add("ID");
		fieldWidthMap.put("ACTION_NAME", 250.0);
		fieldWidthMap.put("BINDING", 125.0);
	}
	// 设置按钮
	JFXButton bindingBtn = new JFXButton("Binding");
	SheetTableData sheetDaV = null;
	FilteredTableView<ResultSetRowPo> allkeysTable = null;
	ObservableList<ResultSetRowPo> items;

	public KeysBindWindow() {
		searchBtn.setGraphic(IconGenerator.svgImageDefActive("search"));
		searchText.getStyleClass().add("myTextField");

		// 过滤功能
		searchText.textProperty().addListener((o, oldVal, newVal) -> {

			if (StrUtils.isNotNullOrEmpty(newVal)) {
				TableViewUtils.tableViewAllDataFilter(allkeysTable, items, newVal);
			} else {
				allkeysTable.setItems(items);
			}

		});

		searchBtn.setOnMouseClicked(e -> {
			searchText.requestFocus();
		});

		SearchPane.getChildren().addAll(searchBtn, searchText);
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

		closeBtn.setOnAction(e -> {
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
		createTable(keysBox);
		stage = CreateModalWindow(keysManageBox);
		stage.show();
		searchText.requestFocus();
	}

	String sql = "select" + " ID , " + " ACTION_NAME, " + " BINDING" + " from KEYS_BINDING ";

	public void createTable(VBox keysBox) {
		Connection conn = SqluckyAppDB.getConn();

		try {
			// 查询
			SheetTableData sheetDaV = TableViewUtils.sqlToSheet(sql, conn, "KEYS_BINDING", fieldWidthMap, hiddenCol);
			// 获取表
			allkeysTable = sheetDaV.getInfoTable();
			this.setSheetDaV(sheetDaV);
			// 表不可编辑
			allkeysTable.editableProperty().bind(new SimpleBooleanProperty(false));
			items = allkeysTable.getItems();
			// 表放入界面
			keysBox.getChildren().add(allkeysTable);

			// 行选中时间
			allkeysTable.getSelectionModel().selectedItemProperty().addListener((o, old, nnew) -> {
				if (nnew != null) {
					String idField = nnew.getValueByFieldName("ID");
					String bindingField = nnew.getValueByFieldName("BINDING");
					String actionNameField = nnew.getValueByFieldName("ACTION_NAME");
					System.out.println("idField = " + idField);
					bindingBtn.setDisable(false);
					bindingBtn.setOnAction(e -> {
						KeyBindingSubWindow.show(idField, bindingField, actionNameField, v -> {
							// 更新界面上绑定的值
							nnew.setValueByFieldName("BINDING", v);
							allkeysTable.refresh();
						});

					});
				}
			});

		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	// 创建一个窗体
	public static Stage CreateModalWindow(VBox vb) {
		vb.getStyleClass().add("myPluginManager-vbox");
		vb.setPrefWidth(420);
		vb.maxWidth(420);

		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);

		SqluckyStage sqlStage = new SqluckyStage(vb);
		Stage stage = sqlStage.getStage();
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
		stage.setOnHidden(e -> {
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
