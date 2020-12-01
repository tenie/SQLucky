package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.DraggingTabPaneSupport;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;
import net.tenie.fx.utility.EventAndListener.CommonListener;
import net.tenie.fx.utility.EventAndListener.RunSQLHelper;

/*   @author tenie */
public class CodeContainer {
	private VBox container;
	private AnchorPane operateBtnPane;
	private TabPane mainTabPane;
	private DraggingTabPaneSupport dtps = new DraggingTabPaneSupport();

	public CodeContainer() {
		container = new VBox();
		operateBtnPane = new AnchorPane();
		codeAreabtnInit(operateBtnPane);
		mainTabPane = new TabPane();
		ComponentGetter.mainTabPane = mainTabPane;

		SqlEditor.myTabPane = mainTabPane;
		SqlEditor.codeAreaRecover(); // 还原上次的sql代码
		dtps.addSupport(mainTabPane);

		VBox.setVgrow(mainTabPane, Priority.ALWAYS);
		container.getChildren().addAll(operateBtnPane, mainTabPane);

		// tab 拖拽
		DraggingTabPaneSupport support1 = new DraggingTabPaneSupport();
		support1.addSupport(mainTabPane);
	}

	// codeArea 代码区域 按钮初始化
	public static void codeAreabtnInit(AnchorPane pn) {
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
		formatSQL.setTooltip(MyTooltipTool.instance("Format code   ctrl + shif + T   "));
		
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
		JFXComboBox<Label> conns = new JFXComboBox<Label>();
		lbcnn.setLabelFor(conns);
		conns.setPrefHeight(25);
		conns.setMinHeight(25);
		conns.setMaxWidth(120);
		conns.setMinWidth(120);
		conns.getStyleClass().add("myComboBox");
		DBConns.flushChoiceBox(conns); // 填充内容
		// change 事件
		conns.getSelectionModel().selectedIndexProperty().addListener(CommonListener.choiceBoxChange());
		conns.getSelectionModel().selectedItemProperty().addListener(CommonListener.choiceBoxChange2());
		ComponentGetter.connComboBox = conns;

		// sql 执行读取行数
		Label lb = new Label("Max Rows: ");
		JFXTextField rows = new JFXTextField();
		lb.setLabelFor(rows);
		rows.setPrefHeight(25);
		rows.setMinHeight(25);

		rows.setLabelFloat(true);
		rows.setPromptText("Max Rows");
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
		y += fix + 110;
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

	public VBox getContainer() {
		return container;
	}

	public void setContainer(VBox container) {
		this.container = container;
	}

	public AnchorPane getOperateBtnPane() {
		return operateBtnPane;
	}

	public void setOperateBtnPane(AnchorPane operateBtnPane) {
		this.operateBtnPane = operateBtnPane;
	}

	public TabPane getMainTabPane() {
		return mainTabPane;
	}

	public void setMainTabPane(TabPane mainTabPane) {
		this.mainTabPane = mainTabPane;
	}

}
