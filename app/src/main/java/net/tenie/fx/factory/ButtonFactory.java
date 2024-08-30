package net.tenie.fx.factory;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.tenie.Sqlucky.sdk.component.*;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.sql.SqlUtils;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TextFieldSetup;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.container.AppWindow;

/**
 * 
 * @author tenie
 *
 */
public class ButtonFactory {
	public static TextField rowsTextField;
	// 按钮box
	public   HBox operateBox = new HBox();

	// 选择框, limit输入框
	public   HBox dbinfoOperateBox = new HBox();

	// 代码区
	// codeArea 代码区域 按钮初始化
	@SuppressWarnings("unchecked")
	public  ButtonFactory() {
		JFXButton runbtn = new JFXButton();
		runbtn.setGraphic(IconGenerator.svgImageDefActive("play"));
		runbtn.setTooltip(MyTooltipTool.instance("Run SQL"));
		runbtn.setDisable(true);

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
			SqlUtils.formatSqlText();
		});
		formatSQL.setTooltip(MyTooltipTool.instance("Format"));

		// 查找
		JFXButton findSQlTxt = new JFXButton();
		findSQlTxt.setGraphic(IconGenerator.svgImageDefActive("search"));
		findSQlTxt.setId("runFunPro");
		findSQlTxt.setTooltip(MyTooltipTool.instance("Find"));
		findSQlTxt.setOnMouseClicked(e ->{
			CommonUtils.findReplaceByCurrentEditer(false);
		} );

		runbtn.setOnMouseClicked(e -> {
			RunSQLHelper.runAction();
		});

		runFunPro.setOnMouseClicked(e -> {
			RunSQLHelper.runCreateFuncSQLMethod();
		});
		stopbtn.setOnMouseClicked(e -> {
			RunSQLHelper.stopSQLMethod();
		});

//		JFXButton hideLeft = new JFXButton();
////		hideLeft.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-left"));
//		hideLeft.setGraphic(IconGenerator.mainTabPaneClose());
//		hideLeft.setOnMouseClicked(event ->  { AppCommonAction.hideLeft();});
//		hideLeft.setTooltip(MyTooltipTool.instance("hide or show connection panel "));
//
//		// TODO hideBottom
//		JFXButton hideBottom = new JFXButton();
////		hideBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-up"));
//		hideBottom.setGraphic(IconGenerator.bottomTabPaneOpen());
//		hideBottom.setOnMouseClicked(event -> {SdkComponent.hideBottom();});
//		hideBottom.setTooltip(MyTooltipTool.instance("hide or show data panel "));
//
//
//
//		JFXButton hideRight = new JFXButton();
////		var rightTabPaneOpen =IconGenerator.rightTabPaneOpen();
////		var rightTabPaneClose =IconGenerator.rightTabPaneClose();
////		hideBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-up"));
////		hideRight.setGraphic(rightTabPaneOpen);
////		hideRight.setOnMouseClicked(CommonEventHandler.hideBottom());
//		hideRight.setOnAction(event -> {
//			SdkComponent.showOrhideRight();
//		});
//		hideRight.setTooltip(MyTooltipTool.instance("Hide or Show Right Panel "));
////		Platform.runLater(SdkComponent::showOrhideRight);
//
//		btnsAnchorPane.getChildren().add(hideRight);
//		btnsAnchorPane.getChildren().add(hideLeft);
//		btnsAnchorPane.getChildren().add(hideBottom);
//		AnchorPane.setRightAnchor(hideRight, 0.0);
//		AnchorPane.setRightAnchor(hideBottom, 30.0);
//		AnchorPane.setRightAnchor(hideLeft, 60.0);






//		CommonButtons.hideLeft = hideLeft;
//		CommonButtons.hideBottom = hideBottom;
//		CommonButtons.hideRight = hideRight;

		// 选择sql在哪个连接上执行
		Label lbcnn = new Label("DB: ");
		JFXComboBox<Label> connsComboBox =  initConnsComboBox(runbtn , runFunPro);
		lbcnn.setLabelFor(connsComboBox);
		ComponentGetter.connComboBox = connsComboBox;

		// sql 执行读取行数
		Label lb = new Label("Limit: ");
		rowsTextField = initLimitTextField();
		lb.setLabelFor(rowsTextField);

		operateBox.getChildren().add(runbtn);
		operateBox.getChildren().add(stopbtn);
		operateBox.getChildren().add(addcodeArea);
		operateBox.getChildren().add(saveSQL);
		operateBox.getChildren().add(formatSQL);
		operateBox.getChildren().add(runFunPro);
		operateBox.getChildren().add(findSQlTxt);

		operateBox.setAlignment(Pos.CENTER_RIGHT);
		operateBox.setPadding(new Insets(3,0,3,0));
		CommonButtons.runbtn = runbtn;
		CommonButtons.stopbtn = stopbtn;
		CommonButtons.runFunPro = runFunPro;
		CommonButtons.addcodeArea = addcodeArea;

//		btnsAnchorPane.getChildren().add(operateBox);
//		Platform.runLater(()->{
//			operateBox.setAlignment(Pos.CENTER_RIGHT);
//			AppWindow.SQLuckyAppWindow.getHeadAnchorPane().getChildren().add(operateBox);
//			AnchorPane.setRightAnchor(operateBox, 220.0);

//		});



		HBox.setMargin(lbcnn, new Insets(5, 2,0,20));
		dbinfoOperateBox.getChildren().add(lbcnn);
		dbinfoOperateBox.getChildren().add(connsComboBox);
		HBox.setMargin(lb, new Insets(5, 2,0,20));
		dbinfoOperateBox.getChildren().add(lb);
		dbinfoOperateBox.getChildren().add(rowsTextField);

//		dbinfoOperateBox.setAlignment(Pos.CENTER_RIGHT);
		dbinfoOperateBox.setPadding(new Insets(3,0,3,0));

	}


	private static TextField initLimitTextField(){
		TextField rowsTextField = new TextField();
		ComponentGetter.maxRowsTextField = rowsTextField;

		rowsTextField.setPrefHeight(25);
		rowsTextField.setMinHeight(25);

		rowsTextField.getStyleClass().add("myTextField");
		rowsTextField.setMaxWidth(90);
		rowsTextField.setTooltip(MyTooltipTool.instance("Load query data rows, suggest < 10000 "));
		rowsTextField.setText(ConfigVal.MaxRows + "");

		TextFieldSetup.setMaxLength(rowsTextField, 9);
		TextFieldSetup.maxRowsNumberOnly(rowsTextField);

		// 失去焦点, 如果没有输入值默认1
		rowsTextField.focusedProperty().addListener((observable, oldValue, newValu) -> {
			if (newValu == false) {
				if (StrUtils.isNullOrEmpty(rowsTextField.getText())) {
					rowsTextField.setText("1");
				}
			}
		});
		return rowsTextField;
	}
	private static JFXComboBox<Label> initConnsComboBox(JFXButton runbtn ,JFXButton runFunPro){
		JFXComboBox<Label> connsComboBox = new JFXComboBox<Label>();
		connsComboBox.setPrefHeight(25);
		connsComboBox.setMinHeight(25);
		connsComboBox.setMaxWidth(200);
		connsComboBox.setMinWidth(200);
		connsComboBox.getStyleClass().add("myComboBox");
		connsComboBox.getStyleClass().add("my-tag");

		DBConns.flushChoiceBox(connsComboBox); // 填充内容

		// change 事件
		connsComboBox.getSelectionModel().selectedIndexProperty().addListener((obj, ov, newValue) -> {
			if (newValue != null && newValue.intValue() > 0) {
				runbtn.setDisable(false);
				runFunPro.setDisable(false);

				connsComboBox.setTooltip(MyTooltipTool.instance(connsComboBox.getItems().get(newValue.intValue()).getText()));
			} else {
				runbtn.setDisable(true);
				runFunPro.setDisable(true);
				connsComboBox.setTooltip(MyTooltipTool.instance(connsComboBox.getItems().get(ov.intValue()).getText()));

			}
			// 给代码页面 设置 对应的连接名称, 切换代码页的时候可以自动转换链接
			MyEditorSheet sheet = MyEditorSheetHelper.getActivationEditorSheet();
			if(sheet != null ){
				sheet.setTabConnIdx(newValue.intValue());
			}

		});
		// 下拉选, 未连接的连接先打开数据库连接
		connsComboBox.getSelectionModel().selectedItemProperty().addListener(CommonListener.choiceBoxChange2());
//		点击的时候判断一下当前链接是不是链接状态
		connsComboBox.setOnMouseClicked(event -> {

			Label lb = connsComboBox.getSelectionModel().getSelectedItem();
			if(lb == null ){
				return;
			}
			String connName = lb.getText();
			SqluckyConnector sqluckyConnector = DBConns.get(connName);
			if( ! sqluckyConnector.isAlive()){
				DBinfoTree.silentOpenConn(connName);
			}
			// 刷新连接的排序
			DBConns.flushChoiceBox();
		});
		return connsComboBox;
	}

	public HBox getOperateBox() {
		return operateBox;
	}

	public void setOperateBox(HBox operateBox) {
		this.operateBox = operateBox;
	}

	public HBox getDbinfoOperateBox() {
		return dbinfoOperateBox;
	}

	public void setDbinfoOperateBox(HBox dbinfoOperateBox) {
		this.dbinfoOperateBox = dbinfoOperateBox;
	}

	public static TextField getRowsTextField() {
		return rowsTextField;
	}

	public static void setRowsTextField(TextField rowsTextField) {
		ButtonFactory.rowsTextField = rowsTextField;
	}
}
