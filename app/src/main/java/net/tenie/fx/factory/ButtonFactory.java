package net.tenie.fx.factory;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import net.tenie.Sqlucky.sdk.component.*;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.sql.SqlUtils;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TextFieldSetup;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.Action.SettingKeyBinding;
import net.tenie.fx.component.InfoTree.DBinfoTree;

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
		addcodeArea.setOnMouseClicked(e -> {
			if(!ComponentGetter.rightTabPaneMasterDetailPane.isShowDetailNode()){
				MyEditorSheetHelper.addEmptyHighLightingEditor(ComponentGetter.mainTabPane);
			}else{
				// 按住control, 再按添加窗口按钮, 可以在rightTabPane中添加
				var kc = KeyCode.CONTROL;
				if(SettingKeyBinding.keyCode != null && SettingKeyBinding.keyCode.equals(kc)){
					MyEditorSheetHelper.addEmptyHighLightingEditor(ComponentGetter.rightTabPane);
				}else {
					MyEditorSheetHelper.addEmptyHighLightingEditor(ComponentGetter.mainTabPane);
				}
			}
		});
		addcodeArea.setTooltip(MyTooltipTool.instance("Add New Edit Page"));

		JFXButton saveSQL = new JFXButton();
		saveSQL.setGraphic(IconGenerator.svgImageDefActive("save"));
		saveSQL.setOnMouseClicked(e->MyEditorSheetHelper.saveSqlToFileAction());
		saveSQL.setTooltip(MyTooltipTool.instance("Save"));

		JFXButton formatSQL = new JFXButton();
		formatSQL.setGraphic(IconGenerator.svgImageDefActive("paragraph"));
		formatSQL.setOnMouseClicked(v -> {
			SqlUtils.formatSqlText();
		});
		formatSQL.setTooltip(MyTooltipTool.instance("Format"));

		// 查找
		JFXButton findSqlTxt = new JFXButton();
		findSqlTxt.setGraphic(IconGenerator.svgImageDefActive("search"));
		findSqlTxt.setId("runFunPro");
		findSqlTxt.setTooltip(MyTooltipTool.instance("Find"));
		findSqlTxt.setOnMouseClicked(e ->{
			CommonUtils.findReplaceByCurrentEditer(false);
		} );

		runbtn.setOnMouseClicked(e -> {
			RunSQLHelper.runAction();
		});

		runFunPro.setOnMouseClicked(e -> {
			RunSQLHelper.runCreateFuncSqlMethod();
		});
		stopbtn.setOnMouseClicked(e -> {
			RunSQLHelper.stopSqlMethod();
		});

		// 选择sql在哪个连接上执行
		Label lbcnn = new Label("DB");
		lbcnn.getStyleClass().add("myToplabel");
		JFXComboBox<Label> connsComboBox =  initConnsComboBox(runbtn , runFunPro);
		lbcnn.setLabelFor(connsComboBox);
		ComponentGetter.connComboBox = connsComboBox;

		// sql 执行读取行数
		Label lb = new Label("Limit");
		lb.getStyleClass().add("myToplabel");
		rowsTextField = initLimitTextField();
		lb.setLabelFor(rowsTextField);

		operateBox.getChildren().add(runbtn);
		operateBox.getChildren().add(stopbtn);
		operateBox.getChildren().add(addcodeArea);
		operateBox.getChildren().add(saveSQL);
		operateBox.getChildren().add(formatSQL);
		operateBox.getChildren().add(runFunPro);
		operateBox.getChildren().add(findSqlTxt);

		operateBox.setAlignment(Pos.CENTER_RIGHT);
		operateBox.setPadding(new Insets(3,0,3,0));
		CommonButtons.runbtn = runbtn;
		CommonButtons.stopbtn = stopbtn;
		CommonButtons.runFunPro = runFunPro;
		CommonButtons.addcodeArea = addcodeArea;

		HBox.setMargin(lbcnn, new Insets(0, 2,0,5));
		dbinfoOperateBox.getChildren().add(lbcnn);
		dbinfoOperateBox.getChildren().add(connsComboBox);
		HBox.setMargin(lb, new Insets(0, 0,0,10));
		HBox.setMargin(rowsTextField, new Insets(0, 0,0,1));
		dbinfoOperateBox.getChildren().add(lb);
		dbinfoOperateBox.getChildren().add(rowsTextField);

//		dbinfoOperateBox.setAlignment(Pos.CENTER_RIGHT);
		dbinfoOperateBox.setPadding(new Insets(3,0,0,0));

	}


	private static TextField initLimitTextField(){
		TextField rowsTextField = new TextField();
		ComponentGetter.maxRowsTextField = rowsTextField;

		rowsTextField.getStyleClass().add("myTopTextField");
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
		connsComboBox.setMaxWidth(240);
		connsComboBox.setMinWidth(240);
		connsComboBox.getStyleClass().add("myComboBox");
		// 填充内容
		DBConns.flushChoiceBox(connsComboBox);

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
