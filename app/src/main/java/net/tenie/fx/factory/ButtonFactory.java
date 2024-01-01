package net.tenie.fx.factory;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TextFieldSetup;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.Action.RunSQLHelper;

/**
 * 
 * @author tenie
 *
 */
public class ButtonFactory {
	public static TextField tfMaxRows;

	// 代码区
	// codeArea 代码区域 按钮初始化
	@SuppressWarnings("unchecked")
	public static AnchorPane codeAreabtnInit() {
		AnchorPane pn = new AnchorPane();
		JFXButton runbtn = new JFXButton();
		runbtn.setGraphic(IconGenerator.svgImageDefActive("play"));
		runbtn.setTooltip(MyTooltipTool.instance("Run SQL"));
		runbtn.setDisable(true);

		JFXButton runLinebtn = new JFXButton();
		runLinebtn.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
		runLinebtn.setTooltip(MyTooltipTool.instance("Run SQL Current Line"));
		runLinebtn.setDisable(true);

		// 执行存储过程
		JFXButton runFunPro = new JFXButton();
		runFunPro.setGraphic(IconGenerator.svgImageDefActive("bolt"));
		runFunPro.setId("runFunPro");
		runFunPro.setTooltip(MyTooltipTool.instance("Execut Create Program DDL"));
		runFunPro.setDisable(true);

		JFXButton stopbtn = new JFXButton();
		stopbtn.setGraphic(IconGenerator.svgImage("stop", "red"));
		stopbtn.setTooltip(MyTooltipTool.instance("stop         ctrl + I "));
		stopbtn.setDisable(true);

		// add panel
		JFXButton addcodeArea = new JFXButton();
		addcodeArea.setGraphic(IconGenerator.svgImageDefActive("plus-square"));
		addcodeArea.setOnMouseClicked(CommonEventHandler.addCodeTab());
		addcodeArea.setTooltip(MyTooltipTool.instance("Add New Edit Page"));

		JFXButton saveSQL = new JFXButton();
		saveSQL.setGraphic(IconGenerator.svgImageDefActive("save"));
		saveSQL.setOnMouseClicked(CommonEventHandler.saveSQl());
		saveSQL.setTooltip(MyTooltipTool.instance("Save"));

		JFXButton formatSQL = new JFXButton();
		formatSQL.setGraphic(IconGenerator.svgImageDefActive("paragraph"));
		formatSQL.setOnMouseClicked(v -> {
			CommonUtils.formatSqlText();
		});
		formatSQL.setTooltip(MyTooltipTool.instance("Format"));

		// 查找
		JFXButton findSQlTxt = new JFXButton();
		findSQlTxt.setGraphic(IconGenerator.svgImageDefActive("search"));
		findSQlTxt.setId("runFunPro");
		findSQlTxt.setTooltip(MyTooltipTool.instance("Find"));
		findSQlTxt.setOnMouseClicked(e -> CommonUtils.findReplace(false));

		runbtn.setOnMouseClicked(e -> {
			RunSQLHelper.runSQLMethod();
		});
		runLinebtn.setOnMouseClicked(e -> {
			RunSQLHelper.runCurrentLineSQLMethod();
		});

		runFunPro.setOnMouseClicked(e -> {
			RunSQLHelper.runCreateFuncSQLMethod();
		});
		stopbtn.setOnMouseClicked(e -> {
			RunSQLHelper.stopSQLMethod();
		});

		JFXButton hideLeft = new JFXButton();
		hideLeft.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-left"));
		hideLeft.setOnMouseClicked(CommonEventHandler.hideLift());
		hideLeft.setTooltip(MyTooltipTool.instance("hide or show connection panel "));

		// TODO hideBottom
		JFXButton hideBottom = new JFXButton();
		hideBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-up"));
		hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom());
		hideBottom.setTooltip(MyTooltipTool.instance("hide or show data panel "));

		pn.getChildren().add(hideLeft);
		pn.getChildren().add(hideBottom);
		AnchorPane.setRightAnchor(hideBottom, 0.0);
		AnchorPane.setRightAnchor(hideLeft, 30.0);

		CommonButtons.hideLeft = hideLeft;
		CommonButtons.hideBottom = hideBottom;

		// 选择sql在哪个连接上执行
		Label lbDBConnection = new Label("DB Connection: ");
		JFXComboBox<Label> connsComboBox = new JFXComboBox<Label>();
		ComponentGetter.connComboBox = connsComboBox;
		lbDBConnection.setLabelFor(connsComboBox);
		connsComboBox.setPrefHeight(25);
		connsComboBox.setMinHeight(25);
		connsComboBox.setMaxWidth(150);
		connsComboBox.setMinWidth(150);
		connsComboBox.getStyleClass().add("myComboBox");
		connsComboBox.getStyleClass().add("my-tag");

		DBConns.flushChoiceBox(connsComboBox); // 填充内容
		// change 事件
		connsComboBox.getSelectionModel().selectedIndexProperty().addListener((obj, ov, newValue) -> {
			if (newValue != null && newValue.intValue() > 0) {
				runbtn.setDisable(false);
				runLinebtn.setDisable(false);
				runFunPro.setDisable(false);
			} else {
				runbtn.setDisable(true);
				runLinebtn.setDisable(true);
				runFunPro.setDisable(true);
			}
			// 给代码页面 设置 对应的连接名称, 切换代码页的时候可以自动转换链接
			MyEditorSheet sheet = MyEditorSheetHelper.getActivationEditorSheet();
			sheet.setTabConnIdx(newValue.intValue());
		});
		

		// schemas
		Label lbSchemas = new Label("Schemas: ");
		JFXComboBox<String> cbSchemas = new JFXComboBox<String>(); 
		// 数据库连接下拉选，值改变事件, 未连接的连接先打开数据库连接， 修改schema 列表
		connsComboBox.getSelectionModel().
					selectedItemProperty().addListener(CommonListener.choiceBoxChange2(cbSchemas,lbSchemas ));
		// 默认隐藏， 只要设置为可见的才会动态显示
		cbSchemas.setVisible(false);
		lbSchemas.setVisible(false);
		
		cbSchemas.setPrefHeight(25);
		cbSchemas.setMinHeight(25);
		cbSchemas.setMaxWidth(120);
		cbSchemas.setMinWidth(120);
		
		// schemas 改变保存在临时TmpSchema中， 
		cbSchemas.getSelectionModel().
				selectedItemProperty().addListener((a , b , c)->{
					if(c != null) {
						String connName = connsComboBox.getValue().getText();
//						System.out.println(connName);
						SqluckyConnector sqluckyConnector = DBConns.get(connName);
						String tmpSchema = sqluckyConnector.getDBConnectorInfoPo().getTmpSchema();
//						System.out.println(" cbSchemas.getSelectionModel()= " + c + " | " + tmpSchema);
						if(! tmpSchema.equals(c)) {
							 sqluckyConnector.getDBConnectorInfoPo().setTmpSchema(c);
						}
					}
				});
		
		

		// sql 执行读取行数
		Label lbMaxRows = new Label("Max Rows: ");
		tfMaxRows = new TextField();
		ComponentGetter.maxRowsTextField = tfMaxRows;
		lbMaxRows.setLabelFor(tfMaxRows);
		tfMaxRows.setPrefHeight(25);
		tfMaxRows.setMinHeight(25);

//			rows.setLabelFloat(true);
//			rows.setPromptText("Max Rows");
		tfMaxRows.getStyleClass().add("myTextField");
		tfMaxRows.setMaxWidth(50);
		tfMaxRows.setTooltip(MyTooltipTool.instance("Load query data rows, suggest <10000 "));
		tfMaxRows.setText(ConfigVal.MaxRows + "");

		TextFieldSetup.setMaxLength(tfMaxRows, 9);
		TextFieldSetup.maxRowsNumberOnly(tfMaxRows);

		// 失去焦点, 如果没有输入值默认1
		tfMaxRows.focusedProperty().addListener((observable, oldValue, newValu) -> {
			if (newValu == false) {
				if (StrUtils.isNullOrEmpty(tfMaxRows.getText())) {
					tfMaxRows.setText("1");
				}
			}
		});

		int x = 0;
		int fix = 30;
		pn.getChildren().add(runbtn);
		runbtn.setLayoutX(0);
		runbtn.setLayoutY(0);

		// runLinebtn
		pn.getChildren().add(runLinebtn);
		runLinebtn.setLayoutY(0);
		x += fix;
		runLinebtn.setLayoutX(x);

		pn.getChildren().add(stopbtn);
		stopbtn.setLayoutY(0);
		x += fix;
		stopbtn.setLayoutX(x);

		pn.getChildren().add(addcodeArea);
		addcodeArea.setLayoutY(0);
		x += fix;
		addcodeArea.setLayoutX(x);

		pn.getChildren().add(saveSQL);
		saveSQL.setLayoutY(0);
		x += fix;
		saveSQL.setLayoutX(x);

		pn.getChildren().add(formatSQL);
		formatSQL.setLayoutY(0);
		x += fix;
		formatSQL.setLayoutX(x);

		// runFunPro
		pn.getChildren().add(runFunPro);
		runFunPro.setLayoutY(0);
		x += fix;
		runFunPro.setLayoutX(x);

		// findSQlTxt
		pn.getChildren().add(findSQlTxt);
		findSQlTxt.setLayoutY(0);
		x += fix;
		findSQlTxt.setLayoutX(x);

		
		
		pn.getChildren().add(lbMaxRows);
		lbMaxRows.setLayoutY(5);
		x += fix + 15;
		lbMaxRows.setLayoutX(x);
		pn.getChildren().add(tfMaxRows);
		tfMaxRows.setLayoutY(0);
		x += fix + 35;
		tfMaxRows.setLayoutX(x);
		
		pn.getChildren().add(lbDBConnection);
		lbDBConnection.setLayoutY(5);
		x += fix + 30;
		lbDBConnection.setLayoutX(x);
		pn.getChildren().add(connsComboBox);
		connsComboBox.setLayoutY(0);
		x += fix + 65;
		connsComboBox.setLayoutX(x);
		
		// Schemas
		
		pn.getChildren().add(lbSchemas);
		lbSchemas.setLayoutY(5);
		x += fix + 125;
		lbSchemas.setLayoutX(x);
		pn.getChildren().add(cbSchemas);
		cbSchemas.setLayoutY(0);
		x += fix + 30;
		cbSchemas.setLayoutX(x);

		CommonButtons.runbtn = runbtn;
		CommonButtons.stopbtn = stopbtn;
		CommonButtons.runFunPro = runFunPro;
		CommonButtons.runLinebtn = runLinebtn;
		CommonButtons.addcodeArea = addcodeArea;

		return pn;
	}

}
