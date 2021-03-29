package net.tenie.fx.component;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;
import net.tenie.fx.utility.EventAndListener.CommonListener;
import net.tenie.fx.utility.EventAndListener.RunSQLHelper;
import net.tenie.lib.tools.StrUtils;

public class ButtonFactory {
	// 连接区
	// 操作按钮
	public static void treeViewbtnInit(FlowPane pn) {
		// 页面初始化: 添加组件
		JFXButton addConnbtn = new JFXButton();

		addConnbtn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
		addConnbtn.setOnMouseClicked(CommonEventHandler.addConnEvent());
		addConnbtn.setTooltip(MyTooltipTool.instance("Add new DB Connection"));

		// open连接
		JFXButton openConn = new JFXButton();
		openConn.setGraphic(ImageViewGenerator.svgImageDefActive("link"));
		openConn.setOnMouseClicked(CommonEventHandler.openConnEvent());
		openConn.setTooltip(MyTooltipTool.instance("Open DB Connection"));

		// 断开连接
		JFXButton closeConn = new JFXButton();
		closeConn.setGraphic(ImageViewGenerator.svgImageDefActive("unlink"));
		closeConn.setOnMouseClicked(CommonEventHandler.closeConnEvent());
		closeConn.setTooltip(MyTooltipTool.instance("Close DB Connection"));

		JFXButton closeALlConn = new JFXButton();
		closeALlConn.setGraphic(ImageViewGenerator.svgImageDefActive("power-off"));
		closeALlConn.setOnMouseClicked(CommonEventHandler.closeAllConnEvent());
		closeALlConn.setTooltip(MyTooltipTool.instance("Close All DB Connection"));

		JFXButton editConn = new JFXButton();
		editConn.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
		editConn.setOnMouseClicked(CommonEventHandler.editConnEvent());
		editConn.setTooltip(MyTooltipTool.instance("Edit DB Connection"));

		// 删除连接
		JFXButton deleteConn = new JFXButton();
		deleteConn.setGraphic(ImageViewGenerator.svgImageDefActive("trash"));
		deleteConn.setOnMouseClicked(CommonEventHandler.deleteConnEvent());
		deleteConn.setTooltip(MyTooltipTool.instance("Delete DB Connection"));

		// 收缩树 zero-fitscreen-24
		JFXButton shrink = new JFXButton();
		shrink.setGraphic(ImageViewGenerator.svgImageDefActive("zero-fitscreen-24"));
		shrink.setOnMouseClicked(e -> {
			// 如果有选中的字符串, 进行查询
			String str = SqlEditor.getCurrentCodeAreaSQLTextSelected();
			if (str.trim().length() > 0) {
				ComponentGetter.dbInfoFilter.setText(str.trim());
			} else {
				CommonAction.shrinkTreeView();
			}
		});
		shrink.setTooltip(MyTooltipTool.instance("Select Words Find It In DB Tree"));

		pn.getChildren().add(addConnbtn);
		pn.getChildren().add(editConn);
		pn.getChildren().add(shrink);

		pn.getChildren().add(openConn);
		pn.getChildren().add(closeConn);
		pn.getChildren().add(closeALlConn);

		pn.getChildren().add(deleteConn);

	}

	// 代码区
	// codeArea 代码区域 按钮初始化
	public static void codeAreabtnInit(AnchorPane pn) {
//			pn.prefHeight(35.0);
//			pn.setMinHeight(35.0);
			
			JFXButton runbtn = new JFXButton();
			runbtn.setGraphic(ImageViewGenerator.svgImageDefActive("play"));
			runbtn.setTooltip(MyTooltipTool.instance("run sql      ctrl + Enter "));

			JFXButton stopbtn = new JFXButton();
			stopbtn.setGraphic(ImageViewGenerator.svgImage("stop", "red"));
			stopbtn.setTooltip(MyTooltipTool.instance("stop         ctrl + I "));

			
			stopbtn.setDisable(true);
			// add panel
			JFXButton addcodeArea = new JFXButton();
			addcodeArea.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square"));
			addcodeArea.setOnMouseClicked(CommonEventHandler.addCodeTab());
			addcodeArea.setTooltip(MyTooltipTool.instance("add new code area panel     ctrl + T "));

			JFXButton saveSQL = new JFXButton();
			saveSQL.setGraphic(ImageViewGenerator.svgImageDefActive("save"));
			saveSQL.setOnMouseClicked(CommonEventHandler.saveSQl());
			saveSQL.setTooltip(MyTooltipTool.instance("Save sql to file ctrl + S"));

			JFXButton formatSQL = new JFXButton();
			formatSQL.setGraphic(ImageViewGenerator.svgImageDefActive("i-cursor"));
			formatSQL.setOnMouseClicked(v -> {
				CommonAction.formatSqlText();
			});
			formatSQL.setTooltip(MyTooltipTool.instance("Format code   ctrl + shif + F  "));
			
			// 执行存储过程
			JFXButton runFunPro = new JFXButton();
			runFunPro.setGraphic(ImageViewGenerator.svgImageDefActive("bolt"));
			runFunPro.setId("runFunPro");
			runFunPro.setTooltip(MyTooltipTool.instance("Execut Create Program DDL"));

			
			runbtn.setOnMouseClicked(RunSQLHelper.runSQL(runbtn, stopbtn, runFunPro));
			runFunPro.setOnMouseClicked(RunSQLHelper.runSQL(runFunPro, stopbtn, runbtn));
			stopbtn.setOnMouseClicked(RunSQLHelper.stopSQL(runbtn, stopbtn, runFunPro));
			
			JFXButton hideLeft = new JFXButton();
			hideLeft.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-left"));// fontImgName("caret-square-o-left",
																								// 16, Color.ROYALBLUE));
			hideLeft.setOnMouseClicked(CommonEventHandler.hideLift());
			hideLeft.setTooltip(MyTooltipTool.instance("hide or show connection panel "));

			JFXButton hideBottom = new JFXButton();
			hideBottom.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-up"));// fontImgName("caret-square-o-down",
																								// 16, Color.ROYALBLUE));
			hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom());
			hideBottom.setTooltip(MyTooltipTool.instance("hide or show data panel "));

			pn.getChildren().add(hideLeft);
			pn.getChildren().add(hideBottom);
			AnchorPane.setRightAnchor(hideBottom, 0.0);
			AnchorPane.setRightAnchor(hideLeft, 30.0);
			AllButtons.btns.put("hideLeft", hideLeft);
			AllButtons.btns.put("hideBottom", hideBottom);

			// 选择sql在哪个连接上执行
			Label lbcnn = new Label("DB Connection: ");
			ComboBox<Label> conns = new ComboBox<Label>();
			lbcnn.setLabelFor(conns);
			conns.setPrefHeight(25);
			conns.setMinHeight(25);
			conns.setMaxWidth(150);
			conns.setMinWidth(150);
			conns.getStyleClass().add("myComboBox");
			conns.getStyleClass().add("my-tag");
			
			DBConns.flushChoiceBox(conns); // 填充内容
			// change 事件
			conns.getSelectionModel().selectedIndexProperty().addListener(CommonListener.choiceBoxChange());
			conns.getSelectionModel().selectedItemProperty().addListener(CommonListener.choiceBoxChange2());
			ComponentGetter.connComboBox = conns;
//			conns
//			AnchorPane.setTopAnchor(conns, 0.0);
			
			// sql 执行读取行数
			Label lb = new Label("Max Rows: ");
			TextField rows = new TextField();
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
			pn.getChildren().add(conns);
			conns.setLayoutY(0);
			y += fix + 65;
			conns.setLayoutX(y);
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
			AllButtons.btns.put("addcodeArea", addcodeArea);

		}
	// 数据区
	// 数据表格 操作按钮们
	public static AnchorPane getDataTableOptionBtnsPane(boolean disable, String time , String rows, String connName) {
//		FlowPane fp = new FlowPane();
		AnchorPane fp = new AnchorPane();
		fp.prefHeight(25);
		JFXButton saveBtn = new JFXButton();
		saveBtn.setGraphic(ImageViewGenerator.svgImageDefActive("save"));
		saveBtn.setOnMouseClicked(CommonEventHandler.saveDate(saveBtn));
		saveBtn.setTooltip(MyTooltipTool.instance("Save data"));
		saveBtn.setDisable(true);
		saveBtn.setId(AllButtons.SAVE);

		JFXButton detailBtn = new JFXButton();
		detailBtn.setGraphic(ImageViewGenerator.svgImageDefActive("search-plus")); 
		detailBtn.setOnMouseClicked(CommonEventHandler.showLineDetail(detailBtn));
		detailBtn.setTooltip(MyTooltipTool.instance("current line detail "));
		detailBtn.setDisable(disable);
		
		JFXButton tableSQLBtn = new JFXButton();
		tableSQLBtn.setGraphic(ImageViewGenerator.svgImageDefActive("table")); 
//		tableSQLBtn.setOnMouseClicked(CommonEventHandler.showLineDetail(detailBtn));
		tableSQLBtn.setOnMouseClicked( e->{
			ButtonAction.findTable();
		});
		tableSQLBtn.setTooltip(MyTooltipTool.instance("current line detail "));
		tableSQLBtn.setDisable(disable);
		

		// refresh
		JFXButton refreshBtn = new JFXButton();
		refreshBtn.setGraphic(ImageViewGenerator.svgImageDefActive("refresh"));

		refreshBtn.setOnMouseClicked(CommonEventHandler.refreshData(refreshBtn));
		refreshBtn.setTooltip(MyTooltipTool.instance("refresh table "));
		refreshBtn.setDisable(disable);

		// 添加一行数据
		JFXButton addBtn = new JFXButton();
		addBtn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square"));

		addBtn.setOnMouseClicked(CommonEventHandler.addData(addBtn));
		addBtn.setTooltip(MyTooltipTool.instance("add new data "));
		addBtn.setDisable(disable);

		JFXButton minusBtn = new JFXButton();
		minusBtn.setGraphic(ImageViewGenerator.svgImage("minus-square", "#EC7774"));

		minusBtn.setOnMouseClicked(CommonEventHandler.deleteData(minusBtn));
		minusBtn.setTooltip(MyTooltipTool.instance("delete data "));
		minusBtn.setDisable(disable);

//	    	 files-o
		JFXButton copyBtn = new JFXButton();
		copyBtn.setGraphic(ImageViewGenerator.svgImageDefActive("files-o"));

		copyBtn.setOnMouseClicked(CommonEventHandler.copyData(copyBtn));
		copyBtn.setTooltip(MyTooltipTool.instance("copy selected row data "));
		copyBtn.setDisable(disable);

		MenuButton exportBtn = new MenuButton();
		exportBtn.setGraphic(ImageViewGenerator.svgImageDefActive("share-square-o"));
		exportBtn.setTooltip(MyTooltipTool.instance("Export data"));
		exportBtn.setDisable(disable);

		Menu insertSQL = new Menu("Export Insert SQL Format ");
		MenuItem selected = new MenuItem("Selected Data to Clipboard ");
		selected.setOnAction(CommonEventHandler.InsertSQLClipboard(true, false));
		MenuItem selectedfile = new MenuItem("Selected Data to file");
		selectedfile.setOnAction(CommonEventHandler.InsertSQLClipboard(true, true));

		MenuItem all = new MenuItem("ALl Data to Clipboard ");
		all.setOnAction(CommonEventHandler.InsertSQLClipboard(false, false));
		MenuItem allfile = new MenuItem("ALl Data to file");
		allfile.setOnAction(CommonEventHandler.InsertSQLClipboard(false, true));

		insertSQL.getItems().addAll(selected, selectedfile, all, allfile);

		Menu csv = new Menu("Export CSV Format ");
		MenuItem csvselected = new MenuItem("Selected Data to Clipboard ");
		csvselected.setOnAction(CommonEventHandler.csvStrClipboard(true, false));
		MenuItem csvselectedfile = new MenuItem("Selected Data to file");
		csvselectedfile.setOnAction(CommonEventHandler.csvStrClipboard(true, true));

		MenuItem csvall = new MenuItem("ALl Data to Clipboard ");
		csvall.setOnAction(CommonEventHandler.csvStrClipboard(false, false));
		MenuItem csvallfile = new MenuItem("ALl Data to file");
		csvallfile.setOnAction(CommonEventHandler.csvStrClipboard(false, true));

		csv.getItems().addAll(csvselected, csvselectedfile, csvall, csvallfile);

		Menu txt = new Menu("Export TXT Format ");
		MenuItem txtselected = new MenuItem("Selected Data to Clipboard ");
		txtselected.setOnAction(CommonEventHandler.txtStrClipboard(true, false));
		MenuItem txtselectedfile = new MenuItem("Selected Data to file");
		txtselectedfile.setOnAction(CommonEventHandler.txtStrClipboard(true, true));

		MenuItem txtall = new MenuItem("ALl Data to Clipboard ");
		txtall.setOnAction(CommonEventHandler.txtStrClipboard(false, false));
		MenuItem txtallfile = new MenuItem("ALl Data to file");
		txtallfile.setOnAction(CommonEventHandler.txtStrClipboard(false, true));

		txt.getItems().addAll(txtselected, txtselectedfile, txtall, txtallfile);

		exportBtn.getItems().addAll(insertSQL, csv, txt);
		
		//隐藏按钮
		JFXButton hideBottom = new JFXButton(); 
		hideBottom.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-down"));
		hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom()); 
		
		//计时/查询行数
		String info = ""; //time+ " ms / "+rows+" rows";
		if(StrUtils.isNotNullOrEmpty(time)) {
			 info = connName+ " : "+ time+ " s / "+rows+" rows";
		}
		Label lb = new Label(info);
		
		
		
		fp.getChildren().addAll(saveBtn, detailBtn, tableSQLBtn, refreshBtn, addBtn, minusBtn, copyBtn, exportBtn, 
				hideBottom, lb);
		Double fix = 30.0;
		int i = 0;
		AnchorPane.setLeftAnchor(detailBtn , fix * ++i ) ;
		AnchorPane.setLeftAnchor(tableSQLBtn , fix * ++i ) ;
		AnchorPane.setLeftAnchor(refreshBtn , fix * ++i);
		
		AnchorPane.setLeftAnchor(addBtn , fix * ++i ) ;
		AnchorPane.setLeftAnchor(minusBtn , fix * ++i);
		AnchorPane.setLeftAnchor(copyBtn , fix * ++i ) ;
		AnchorPane.setLeftAnchor(exportBtn , fix * ++i);
		
		AnchorPane.setRightAnchor(hideBottom, 0.0);
		AnchorPane.setTopAnchor(lb, 3.0);
		AnchorPane.setRightAnchor(lb, 35.0);
		
		return fp;
	}
}
