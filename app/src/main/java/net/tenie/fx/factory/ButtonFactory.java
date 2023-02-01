package net.tenie.fx.factory;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.MainTabs;
/**
 * 
 * @author tenie
 *
 */
public class ButtonFactory {
	public static TextField rows ;

	
	// 代码区
	// codeArea 代码区域 按钮初始化
	@SuppressWarnings("unchecked")
	public static AnchorPane codeAreabtnInit() {
		    AnchorPane pn = new AnchorPane();
			JFXButton runbtn = new JFXButton();
			runbtn.setGraphic(IconGenerator.svgImageDefActive("play"));
			runbtn.setTooltip(MyTooltipTool.instance("run sql     (ctrl + Enter)"));
			runbtn.setDisable(true);
			
			JFXButton runLinebtn = new JFXButton();
			runLinebtn.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
			runLinebtn.setTooltip(MyTooltipTool.instance("run current line sql (Alt + R)"));
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
			addcodeArea.setTooltip(MyTooltipTool.instance("add new code area panel     ctrl + T "));			
			
			JFXButton saveSQL = new JFXButton();
			saveSQL.setGraphic(IconGenerator.svgImageDefActive("save"));
			saveSQL.setOnMouseClicked(CommonEventHandler.saveSQl());
			saveSQL.setTooltip(MyTooltipTool.instance("Save sql to file ctrl + S"));

			JFXButton formatSQL = new JFXButton();
			formatSQL.setGraphic(IconGenerator.svgImageDefActive("paragraph")); // i-cursor
			formatSQL.setOnMouseClicked(v -> {
				CommonAction.formatSqlText();
			});
			formatSQL.setTooltip(MyTooltipTool.instance("Format code   ctrl + shif + F  "));
			
			
		
			// 查找
			JFXButton findSQlTxt = new JFXButton();
			findSQlTxt.setGraphic(IconGenerator.svgImageDefActive("search"));
			findSQlTxt.setId("runFunPro");
			findSQlTxt.setTooltip(MyTooltipTool.instance("Find (Ctrl + F)"));
			findSQlTxt.setOnMouseClicked(e->CommonUtility.findReplace(false));

			
			runbtn.setOnMouseClicked(e->{
				RunSQLHelper.runSQLMethod();
			});
			runLinebtn.setOnMouseClicked(e->{ 
				RunSQLHelper.runCurrentLineSQLMethod();
			});
			
			
			runFunPro.setOnMouseClicked(e->{
				RunSQLHelper.runCreateFuncSQLMethod();
			});
			stopbtn.setOnMouseClicked(e->{
				RunSQLHelper.stopSQLMethod();
			});
			
			JFXButton hideLeft = new JFXButton();
			hideLeft.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-left"));
			hideLeft.setOnMouseClicked(CommonEventHandler.hideLift());
			hideLeft.setTooltip(MyTooltipTool.instance("hide or show connection panel "));
			
			//TODO hideBottom
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
			Label lbcnn = new Label("DB Connection: ");
			JFXComboBox<Label> connsComboBox = new JFXComboBox<Label>();
			lbcnn.setLabelFor(connsComboBox);
			connsComboBox.setPrefHeight(25);
			connsComboBox.setMinHeight(25);
			connsComboBox.setMaxWidth(150);
			connsComboBox.setMinWidth(150);
			connsComboBox.getStyleClass().add("myComboBox");
			connsComboBox.getStyleClass().add("my-tag");
			
			DBConns.flushChoiceBox(connsComboBox); // 填充内容
			// change 事件
			connsComboBox.getSelectionModel().selectedIndexProperty()
			.addListener((obj, ov, newValue)->{
				System.out.println("choiceBoxChange" + newValue);
				if(newValue!=null && newValue.intValue() > 0) {
					runbtn.setDisable(false);
					runLinebtn.setDisable(false);
					runFunPro.setDisable(false);
				}else {
					runbtn.setDisable(true);
					runLinebtn.setDisable(true);
					runFunPro.setDisable(true);
				}
				// 给代码页面 设置 对应的连接名称, 切换代码页的时候可以自动转换链接
				MainTabs.setBoxIdx( CommonUtility.tabText(  MainTabs.getActTab()), newValue.toString());
			});
			connsComboBox.getSelectionModel().selectedItemProperty().addListener(CommonListener.choiceBoxChange2());
			ComponentGetter.connComboBox = connsComboBox;
			
			
			// sql 执行读取行数
			Label lb = new Label("Max Rows: ");
			rows = new TextField();
			ComponentGetter.maxRowsTextField = rows;
			lb.setLabelFor(rows);
			rows.setPrefHeight(25);
			rows.setMinHeight(25);

//			rows.setLabelFloat(true);
//			rows.setPromptText("Max Rows");
			rows.getStyleClass().add("myTextField");
			rows.setMaxWidth(90);
			rows.setTooltip(MyTooltipTool.instance("Load query data rows, suggest <10000 "));
			rows.setText(ConfigVal.MaxRows + "");
			rows.lengthProperty().addListener(CommonListener.textFieldLimit(rows, 9));
			rows.textProperty().addListener(CommonListener.textFieldNumChange(rows));
			// 失去焦点, 如果没有输入值默认1
			rows.focusedProperty().addListener((observable,  oldValue,  newValu)->{
				if(newValu == false) {
					if(StrUtils.isNullOrEmpty(rows.getText())) {
						rows.setText("1");
					}
				}
			});

			int y = 0;
			int fix = 30;
			pn.getChildren().add(runbtn);
			runbtn.setLayoutX(0);
			runbtn.setLayoutY(0);

			// runLinebtn
			pn.getChildren().add(runLinebtn);
			runLinebtn.setLayoutY(0);
			y += fix;
			runLinebtn.setLayoutX(y);
			
			pn.getChildren().add(stopbtn);
			stopbtn.setLayoutY(0);
			y += fix;
			stopbtn.setLayoutX(y);

			pn.getChildren().add(addcodeArea);
			addcodeArea.setLayoutY(0);
			y += fix;
			addcodeArea.setLayoutX(y);

			pn.getChildren().add(saveSQL);
			saveSQL.setLayoutY(0);
			y += fix;
			saveSQL.setLayoutX(y);

			pn.getChildren().add(formatSQL);
			formatSQL.setLayoutY(0);
			y += fix;
			formatSQL.setLayoutX(y);

			//runFunPro
			pn.getChildren().add(runFunPro);
			runFunPro.setLayoutY(0);
			y += fix;
			runFunPro.setLayoutX(y);
			
			//findSQlTxt
			pn.getChildren().add(findSQlTxt);
			findSQlTxt.setLayoutY(0);
			y += fix;
			findSQlTxt.setLayoutX(y);
			
			pn.getChildren().add(lbcnn);
			lbcnn.setLayoutY(5);
			y += fix + 100;
			lbcnn.setLayoutX(y);
			pn.getChildren().add(connsComboBox);
			connsComboBox.setLayoutY(0);
			y += fix + 65;
			connsComboBox.setLayoutX(y);
			pn.getChildren().add(lb);
			lb.setLayoutY(5);
			y += fix + 140;
			lb.setLayoutX(y);
			pn.getChildren().add(rows);
			rows.setLayoutY(0);
			y += fix + 35;
			rows.setLayoutX(y);

//			AllButtons.btns.put("runbtn", runbtn);
//			AllButtons.btns.put("stopbtn", stopbtn);
//			AllButtons.btns.put("runFunPro", runFunPro);
//			AllButtons.btns.put("runLinebtn", runLinebtn); 
//			AllButtons.btns.put("addcodeArea", addcodeArea);
			
			CommonButtons.runbtn = runbtn;
			CommonButtons.stopbtn = stopbtn;
			CommonButtons.runFunPro = runFunPro;
			CommonButtons.runLinebtn = runLinebtn;
			CommonButtons.addcodeArea = addcodeArea;
			

			return pn;
		}
	


	
	
	
	// 锁住<锁按钮>
	public static void lockLockBtn(SqluckyBottomSheet mytb , JFXButton btn) {
		boolean islock =  mytb.getTableData().isLock();
		if( ! islock) { 
			mytb.getTableData().setLock(true);
			btn.setGraphic(IconGenerator.svgImageDefActive("lock"));
		}
	}
	
	
	
}
