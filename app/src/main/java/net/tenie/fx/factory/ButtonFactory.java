package net.tenie.fx.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.component.AllButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.dataView.MyTabData;
import net.tenie.Sqlucky.sdk.component.SqlcukyEditor;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.TableDataDetail;
import net.tenie.lib.tools.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class ButtonFactory {
	public static  Map<String, Boolean> lockObj = new HashMap<>();
	public static TextField rows ;

	// 连接区
	// 操作按钮
	public static FlowPane createTreeViewbtn() {
		FlowPane pn = new FlowPane();
		// 页面初始化: 添加组件
		JFXButton addConnbtn = new JFXButton();

		addConnbtn.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));
		addConnbtn.setOnMouseClicked(CommonEventHandler.addConnEvent());
		addConnbtn.setTooltip(MyTooltipTool.instance("Add new DB Connection"));
//		btns.add(addConnbtn);

		// open连接
		JFXButton openConn = new JFXButton();
		openConn.setGraphic(IconGenerator.svgImageDefActive("link"));
		openConn.setOnMouseClicked(e->{
			ConnectionEditor.openDbConn();
		});
		openConn.setTooltip(MyTooltipTool.instance("Open DB Connection"));
//		btns.add(openConn);
		// 断开连接
		JFXButton closeConn = new JFXButton();
		closeConn.setGraphic(IconGenerator.svgImageDefActive("unlink"));
		closeConn.setOnMouseClicked(e->{
			ConnectionEditor.closeDbConn();
		});
		closeConn.setTooltip(MyTooltipTool.instance("Close DB Connection"));
//		btns.add(closeConn);

		JFXButton closeALlConn = new JFXButton();
		closeALlConn.setGraphic(IconGenerator.svgImageDefActive("power-off"));
		closeALlConn.setOnMouseClicked(CommonEventHandler.closeAllConnEvent());
		closeALlConn.setTooltip(MyTooltipTool.instance("Close All DB Connection"));
//		btns.add(closeALlConn);

		JFXButton editConn = new JFXButton();
		editConn.setGraphic(IconGenerator.svgImageDefActive("edit"));
		editConn.setOnMouseClicked(e->{
			ConnectionEditor.closeDbConn();
			ConnectionEditor.editDbConn();
		});
		editConn.setTooltip(MyTooltipTool.instance("Edit DB Connection"));
//		btns.add(editConn); 

		// 收缩树 zero-fitscreen-24
		JFXButton shrink = new JFXButton();
//		btns.add(shrink);
		shrink.setGraphic(IconGenerator.svgImageDefActive("zero-fitscreen-24"));
		shrink.setOnMouseClicked(e -> {
			// 如果有选中的字符串, 进行查询
			String str = SqlcukyEditor.getCurrentCodeAreaSQLSelectedText();
			if (str.trim().length() > 0) {
				ComponentGetter.dbInfoFilter.setText(str.trim());
			} else {
				CommonAction.shrinkTreeView();
			}
		});
		shrink.setTooltip(MyTooltipTool.instance("Select Words Find It In DB Tree"));
		
		
		// 删除连接
		JFXButton deleteConn = new JFXButton();
		deleteConn.setGraphic(IconGenerator.svgImageDefActive("trash"));
		deleteConn.setOnMouseClicked(CommonEventHandler.deleteConnEvent());
		deleteConn.setTooltip(MyTooltipTool.instance("Delete DB Connection"));
//		btns.add(deleteConn);

		
		// 脚本
		JFXButton script = new JFXButton();
		script.setGraphic(IconGenerator.svgImageDefActive("entypo-download"));
		script.setOnMouseClicked(e->{
		   CommonAction.archiveAllScript(); 
			
		});
		script.setTooltip(MyTooltipTool.instance("Archive Script "));
//		btns.add(script);
		
		pn.getChildren().add(addConnbtn);
		pn.getChildren().add(editConn);
		pn.getChildren().add(shrink);

		pn.getChildren().add(openConn);
		pn.getChildren().add(closeConn);
		pn.getChildren().add(closeALlConn);

		pn.getChildren().add(deleteConn);
		pn.getChildren().add(script);
		return pn;
	}

	// 代码区
	// codeArea 代码区域 按钮初始化
	@SuppressWarnings("unchecked")
	public static AnchorPane codeAreabtnInit() {
		    AnchorPane pn = new AnchorPane();
			JFXButton runbtn = new JFXButton();
			runbtn.setGraphic(IconGenerator.svgImageDefActive("play"));
			runbtn.setTooltip(MyTooltipTool.instance("run sql     (ctrl + Enter)"));
			
			JFXButton runLinebtn = new JFXButton();
			runLinebtn.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
			runLinebtn.setTooltip(MyTooltipTool.instance("run current line sql (ctrl + shift + Enter)"));
			
			JFXButton stopbtn = new JFXButton();
			stopbtn.setGraphic(IconGenerator.svgImage("stop", "red", false));
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
//			btns.add(saveSQL);

			JFXButton formatSQL = new JFXButton();
			formatSQL.setGraphic(IconGenerator.svgImageDefActive("paragraph")); // i-cursor
			formatSQL.setOnMouseClicked(v -> {
				CommonAction.formatSqlText();
			});
			formatSQL.setTooltip(MyTooltipTool.instance("Format code   ctrl + shif + F  "));
//			btns.add(formatSQL);
			
			
			// 执行存储过程
			JFXButton runFunPro = new JFXButton();
			runFunPro.setGraphic(IconGenerator.svgImageDefActive("bolt"));
			runFunPro.setId("runFunPro");
			runFunPro.setTooltip(MyTooltipTool.instance("Execut Create Program DDL"));
//			btns.add(runFunPro);

			
			runbtn.setOnMouseClicked(e->{
				RunSQLHelper.runSQLMethod();
			});
			runLinebtn.setOnMouseClicked(e->{ 
				RunSQLHelper.runCurrentLineSQLMethod();
			});
			
			
			runFunPro.setOnMouseClicked(e->{
				RunSQLHelper.runFuncSQLMethod();
			});
			stopbtn.setOnMouseClicked(e->{
				RunSQLHelper.stopSQLMethod();
			});
			
			JFXButton hideLeft = new JFXButton();
			hideLeft.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-left"));// fontImgName("caret-square-o-left",
																								// 16, Color.ROYALBLUE));
			hideLeft.setOnMouseClicked(CommonEventHandler.hideLift());
			hideLeft.setTooltip(MyTooltipTool.instance("hide or show connection panel "));
//			btns.add(hideLeft);
			
			//TODO hideBottom
			JFXButton hideBottom = new JFXButton();
			hideBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-up"));// fontImgName("caret-square-o-down",
																								// 16, Color.ROYALBLUE));
			hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom());
			hideBottom.setTooltip(MyTooltipTool.instance("hide or show data panel "));
//			btns.add(hideBottom);
			
			
			pn.getChildren().add(hideLeft);
			pn.getChildren().add(hideBottom);
			AnchorPane.setRightAnchor(hideBottom, 0.0);
			AnchorPane.setRightAnchor(hideLeft, 30.0);
			AllButtons.btns.put("hideLeft", hideLeft);
			AllButtons.btns.put("hideBottom", hideBottom);

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
			connsComboBox.getSelectionModel().selectedIndexProperty().addListener(CommonListener.choiceBoxChange());
			connsComboBox.getSelectionModel().selectedItemProperty().addListener(CommonListener.choiceBoxChange2());
			ComponentGetter.connComboBox = connsComboBox;
			
//			conns.getit
//			AnchorPane.setTopAnchor(conns, 0.0);
			
			// sql 执行读取行数
			Label lb = new Label("Max Rows: ");
			rows = new TextField();
//		    final TextField rows =  TextFieldFactory.numTextField();
			
			lb.setLabelFor(rows);
			rows.setPrefHeight(25);
			rows.setMinHeight(25);

//			rows.setLabelFloat(true);
//			rows.setPromptText("Max Rows");
			rows.getStyleClass().add("myTextField");
			rows.setMaxWidth(90);
			rows.setTooltip(MyTooltipTool.instance("Input 0 query all data."));
			rows.setText(ConfigVal.MaxRows + "");
			rows.lengthProperty().addListener(CommonListener.textFieldLimit(rows, 9));
			rows.textProperty().addListener(CommonListener.textFieldNumChange(rows));

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

			AllButtons.btns.put("runbtn", runbtn);
			AllButtons.btns.put("stopbtn", stopbtn);
			AllButtons.btns.put("runFunPro", runFunPro);
			AllButtons.btns.put("runLinebtn", runLinebtn);
			
			AllButtons.btns.put("addcodeArea", addcodeArea);

			return pn;
		}
	


	
	
	
	// 锁住<锁按钮>
	public static void lockLockBtn(MyTabData mytb , JFXButton btn) {
		boolean islock =  mytb.isLock(); //lockObj.get(id);
		if( ! islock) { 
//			lockObj.put(id, true);
			mytb.setLock(true);
			btn.setGraphic(IconGenerator.svgImageDefActive("lock"));
		}
	}
	
	
	public static  JFXButton createLockBtn(MyTabData mytb ) {
		// 锁
		JFXButton lockbtn = new JFXButton();
//		lockbtn.setDisable(disable);
		if (mytb.isLock()) {
			lockbtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
		} else {
			lockbtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));
		}
//		lockObj.put(id, isLock);
		lockbtn.setOnMouseClicked(e -> {
//			Boolean tf = lockObj.get(id);
			if (mytb.isLock()) {
				lockbtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));
			} else {
				lockbtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
			}
//			lockObj.put(id, !tf);

		});

		return lockbtn;
	}

}
