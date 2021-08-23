package net.tenie.fx.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyCodeArea;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.TableDataDetail;
import net.tenie.lib.tools.StrUtils;

public class ButtonFactory {
	public static  Map<String, Boolean> lockObj = new HashMap<>();
	public static  List<ButtonBase> btns = new ArrayList<>();
	public static TextField rows ;

	// 连接区
	// 操作按钮
	public static FlowPane createTreeViewbtn() {
		FlowPane pn = new FlowPane();
		// 页面初始化: 添加组件
		JFXButton addConnbtn = new JFXButton();

		addConnbtn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
		addConnbtn.setOnMouseClicked(CommonEventHandler.addConnEvent());
		addConnbtn.setTooltip(MyTooltipTool.instance("Add new DB Connection"));
		btns.add(addConnbtn);

		// open连接
		JFXButton openConn = new JFXButton();
		openConn.setGraphic(ImageViewGenerator.svgImageDefActive("link"));
		openConn.setOnMouseClicked(e->{
			ConnectionEditor.openDbConn();
		});
		openConn.setTooltip(MyTooltipTool.instance("Open DB Connection"));
		btns.add(openConn);
		// 断开连接
		JFXButton closeConn = new JFXButton();
		closeConn.setGraphic(ImageViewGenerator.svgImageDefActive("unlink"));
		closeConn.setOnMouseClicked(e->{
			ConnectionEditor.closeDbConn();
		});
		closeConn.setTooltip(MyTooltipTool.instance("Close DB Connection"));
		btns.add(closeConn);

		JFXButton closeALlConn = new JFXButton();
		closeALlConn.setGraphic(ImageViewGenerator.svgImageDefActive("power-off"));
		closeALlConn.setOnMouseClicked(CommonEventHandler.closeAllConnEvent());
		closeALlConn.setTooltip(MyTooltipTool.instance("Close All DB Connection"));
		btns.add(closeALlConn);

		JFXButton editConn = new JFXButton();
		editConn.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
		editConn.setOnMouseClicked(e->{
			ConnectionEditor.closeDbConn();
			ConnectionEditor.editDbConn();
		});
		editConn.setTooltip(MyTooltipTool.instance("Edit DB Connection"));
		btns.add(editConn); 

		// 收缩树 zero-fitscreen-24
		JFXButton shrink = new JFXButton();
		btns.add(shrink);
		shrink.setGraphic(ImageViewGenerator.svgImageDefActive("zero-fitscreen-24"));
		shrink.setOnMouseClicked(e -> {
			// 如果有选中的字符串, 进行查询
			String str = SqlEditor.getCurrentCodeAreaSQLSelectedText();
			if (str.trim().length() > 0) {
				ComponentGetter.dbInfoFilter.setText(str.trim());
			} else {
				CommonAction.shrinkTreeView();
			}
		});
		shrink.setTooltip(MyTooltipTool.instance("Select Words Find It In DB Tree"));
		
		
		// 删除连接
		JFXButton deleteConn = new JFXButton();
		deleteConn.setGraphic(ImageViewGenerator.svgImageDefActive("trash"));
		deleteConn.setOnMouseClicked(CommonEventHandler.deleteConnEvent());
		deleteConn.setTooltip(MyTooltipTool.instance("Delete DB Connection"));
		btns.add(deleteConn);

		
		// 脚本
		JFXButton script = new JFXButton();
		script.setGraphic(ImageViewGenerator.svgImageDefActive("entypo-download"));
		script.setOnMouseClicked(e->{
		   CommonAction.archiveAllScript(); 
			
		});
		script.setTooltip(MyTooltipTool.instance("Archive Script "));
		btns.add(script);
		
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
	public static AnchorPane codeAreabtnInit() {
		    AnchorPane pn = new AnchorPane();
			JFXButton runbtn = new JFXButton();
			runbtn.setGraphic(ImageViewGenerator.svgImageDefActive("play"));
			runbtn.setTooltip(MyTooltipTool.instance("run sql      ctrl + Enter "));
			btns.add(runbtn);
			
			
			JFXButton stopbtn = new JFXButton();
			stopbtn.setGraphic(ImageViewGenerator.svgImage("stop", "red"));
			stopbtn.setTooltip(MyTooltipTool.instance("stop         ctrl + I "));
//			btns.add(stopbtn);
			
			stopbtn.setDisable(true);
			// add panel
			JFXButton addcodeArea = new JFXButton();
			addcodeArea.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square"));
			addcodeArea.setOnMouseClicked(CommonEventHandler.addCodeTab());
			addcodeArea.setTooltip(MyTooltipTool.instance("add new code area panel     ctrl + T "));
			btns.add(addcodeArea);
			
			
			JFXButton saveSQL = new JFXButton();
			saveSQL.setGraphic(ImageViewGenerator.svgImageDefActive("save"));
			saveSQL.setOnMouseClicked(CommonEventHandler.saveSQl());
			saveSQL.setTooltip(MyTooltipTool.instance("Save sql to file ctrl + S"));
			btns.add(saveSQL);

			JFXButton formatSQL = new JFXButton();
			formatSQL.setGraphic(ImageViewGenerator.svgImageDefActive("paragraph")); // i-cursor
			formatSQL.setOnMouseClicked(v -> {
				CommonAction.formatSqlText();
			});
			formatSQL.setTooltip(MyTooltipTool.instance("Format code   ctrl + shif + F  "));
			btns.add(formatSQL);
			
			
			// 执行存储过程
			JFXButton runFunPro = new JFXButton();
			runFunPro.setGraphic(ImageViewGenerator.svgImageDefActive("bolt"));
			runFunPro.setId("runFunPro");
			runFunPro.setTooltip(MyTooltipTool.instance("Execut Create Program DDL"));
			btns.add(runFunPro);

			
			runbtn.setOnMouseClicked(e->{
				RunSQLHelper.runSQLMethod();
			});
			runFunPro.setOnMouseClicked(e->{
				RunSQLHelper.runFuncSQLMethod();
			});
			stopbtn.setOnMouseClicked(e->{
				RunSQLHelper.stopSQLMethod();
			});
			
			JFXButton hideLeft = new JFXButton();
			hideLeft.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-left"));// fontImgName("caret-square-o-left",
																								// 16, Color.ROYALBLUE));
			hideLeft.setOnMouseClicked(CommonEventHandler.hideLift());
			hideLeft.setTooltip(MyTooltipTool.instance("hide or show connection panel "));
			btns.add(hideLeft);
			
			//TODO hideBottom
			JFXButton hideBottom = new JFXButton();
			hideBottom.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-up"));// fontImgName("caret-square-o-down",
																								// 16, Color.ROYALBLUE));
			hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom());
			hideBottom.setTooltip(MyTooltipTool.instance("hide or show data panel "));
			btns.add(hideBottom);
			
			
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
			AllButtons.btns.put("addcodeArea", addcodeArea);

			return pn;
		}
	// 数据区
	// 数据表格 操作按钮们
	public static AnchorPane getDataTableOptionBtnsPane(String id, boolean disable, String time , String rows, String connName, List<ButtonBase> optionBtns , boolean isLock) {

		AnchorPane fp = new AnchorPane(); 
		CommonAction.addCssClass(fp, "data-table-btn-anchor-pane");
		fp.prefHeight(25);
		JFXButton saveBtn = new JFXButton();
		saveBtn.setGraphic(ImageViewGenerator.svgImageDefActive("save"));
		saveBtn.setOnMouseClicked( e->{ 
				ButtonAction.dataSave();
		});
		saveBtn.setTooltip(MyTooltipTool.instance("Save data"));
		saveBtn.setDisable(true);
		saveBtn.setId(AllButtons.SAVE);
		optionBtns.add(saveBtn);
		

		JFXButton detailBtn = new JFXButton();
		detailBtn.setGraphic(ImageViewGenerator.svgImageDefActive("search-plus")); 
		detailBtn.setOnMouseClicked( e->{
			TableDataDetail.show();
		});
		detailBtn.setTooltip(MyTooltipTool.instance("current line detail "));
		detailBtn.setDisable(disable);
		optionBtns.add(detailBtn);
		
		
		JFXButton tableSQLBtn = new JFXButton();
		tableSQLBtn.setGraphic(ImageViewGenerator.svgImageDefActive("table")); 
		tableSQLBtn.setOnMouseClicked( e->{
			ButtonAction.findTable();
		});
		tableSQLBtn.setTooltip(MyTooltipTool.instance("Table SQL"));
		tableSQLBtn.setDisable(disable);
		optionBtns.add(tableSQLBtn);
		

		// refresh
		JFXButton refreshBtn = new JFXButton();
		refreshBtn.setGraphic(ImageViewGenerator.svgImageDefActive("refresh")); 
		refreshBtn.setOnMouseClicked( e->{ 
			Boolean islock = lockObj.get(id);
			ButtonAction.refreshData(islock) ;
		});
		refreshBtn.setTooltip(MyTooltipTool.instance("refresh table "));
		refreshBtn.setDisable(disable);
		optionBtns.add(refreshBtn);
		

		// 添加一行数据
		JFXButton addBtn = new JFXButton();
		addBtn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square"));

		addBtn.setOnMouseClicked(e->{ 
			ButtonAction.addData();
		});
		addBtn.setTooltip(MyTooltipTool.instance("add new data "));
		addBtn.setDisable(disable);
		optionBtns.add(addBtn);

		JFXButton minusBtn = new JFXButton();
		minusBtn.setGraphic(ImageViewGenerator.svgImage("minus-square", "#EC7774"));

		minusBtn.setOnMouseClicked( e->{ 
			ButtonAction.deleteData(); 
		});
		minusBtn.setTooltip(MyTooltipTool.instance("delete data "));
		minusBtn.setDisable(disable);
		optionBtns.add(minusBtn);

//	    	 files-o
		JFXButton copyBtn = new JFXButton();
		copyBtn.setGraphic(ImageViewGenerator.svgImageDefActive("files-o"));
		copyBtn.setOnMouseClicked( e->{ 
			ButtonAction.copyData();
		});
		copyBtn.setTooltip(MyTooltipTool.instance("copy selected row data "));
		copyBtn.setDisable(disable);
		optionBtns.add(copyBtn);
		

		MenuButton exportBtn = new MenuButton();
		exportBtn.setGraphic(ImageViewGenerator.svgImageDefActive("share-square-o"));
		exportBtn.setTooltip(MyTooltipTool.instance("Export data"));
		exportBtn.setDisable(disable);
		optionBtns.add(exportBtn);

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
		optionBtns.add(hideBottom);
		// 锁
		JFXButton lockbtn = createLockBtn(isLock, id);
//		JFXButton lockbtn = new JFXButton(); 
//		lockbtn.setDisable(disable);
//		if(isLock) {
//			lockbtn.setGraphic(ImageViewGenerator.svgImageDefActive("lock"));
//		}else {
//			lockbtn.setGraphic(ImageViewGenerator.svgImageDefActive("unlock"));
//		}
//		lockObj.put(id, isLock);
//		lockbtn.setOnMouseClicked(e->{
//			Boolean tf = lockObj.get(id);
//			if(tf) {
//				lockbtn.setGraphic(ImageViewGenerator.svgImageDefActive("unlock"));
//			}else {
//				lockbtn.setGraphic(ImageViewGenerator.svgImageDefActive("lock"));
//			}
//			lockObj.put(id, !tf);
//			
//		}); 
		
		optionBtns.add(lockbtn);
		
		//保存按钮监听 : 保存亮起, 锁住
		saveBtn.disableProperty().addListener(e->{
			if( ! saveBtn.disableProperty().getValue() ) {
				setLockBtn(id, true, lockbtn);
			}
		});
		
		
		//计时/查询行数
		String info = ""; //time+ " ms / "+rows+" rows";
		if(StrUtils.isNotNullOrEmpty(time)) {
			 info = connName+ " : "+ time+ " s / "+rows+" rows";
		}
		Label lb = new Label(info);
		
		
		
		fp.getChildren().addAll(saveBtn, detailBtn, tableSQLBtn, refreshBtn, addBtn, minusBtn, copyBtn, exportBtn, 
				hideBottom, lockbtn, lb);
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
		AnchorPane.setRightAnchor(lockbtn, 35.0);
		
		AnchorPane.setTopAnchor(lb, 3.0);
		AnchorPane.setRightAnchor(lb, 70.0);
		
		return fp;
	}
	
	

	
	
	public static JFXButton createLockBtn( boolean isLock , String id) {
		// 锁
		JFXButton lockbtn = new JFXButton();
//		lockbtn.setDisable(disable);
		if (isLock) {
			lockbtn.setGraphic(ImageViewGenerator.svgImageDefActive("lock"));
		} else {
			lockbtn.setGraphic(ImageViewGenerator.svgImageDefActive("unlock"));
		}
		lockObj.put(id, isLock);
		lockbtn.setOnMouseClicked(e -> {
			Boolean tf = lockObj.get(id);
			if (tf) {
				lockbtn.setGraphic(ImageViewGenerator.svgImageDefActive("unlock"));
			} else {
				lockbtn.setGraphic(ImageViewGenerator.svgImageDefActive("lock"));
			}
			lockObj.put(id, !tf);

		});
		
		return lockbtn;
	}
	
	// 锁住<锁按钮>
	public static void lockLockBtn(String id, JFXButton btn) {
		boolean islock = lockObj.get(id);
		if( ! islock) { 
			lockObj.put(id, true);
			btn.setGraphic(ImageViewGenerator.svgImageDefActive("lock"));
		}
	}
	
	private static  void setLockBtn(String id, Boolean islock , Button lockbtn) { 
		if(islock) {
			lockbtn.setGraphic(ImageViewGenerator.svgImageDefActive("lock"));
		}else {
			lockbtn.setGraphic(ImageViewGenerator.svgImageDefActive("unlock"));
			
		}
		lockObj.put(id, islock);
	}
}
