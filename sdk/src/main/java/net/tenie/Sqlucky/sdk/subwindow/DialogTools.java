package net.tenie.Sqlucky.sdk.subwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

public class DialogTools {
	private static Logger logger = LogManager.getLogger(DialogTools.class);

	public static VBox getSceneVbox(Stage stage, Node title, List<Node> nds, List<Node> btns) {
		return getSceneVbox(800, 160, stage, title, nds, btns);
	}

	// TODO 设置子窗口的外形
	public static VBox getSceneVbox(double width, double height, Stage stage, Node title, List<Node> nds,
									List<Node> btns) {
		VBox windowVbox = getWindowShell(stage,title, "myAlert");
		if (width > 0  ) {
			windowVbox.setPrefWidth(width);
			windowVbox.setPrefHeight(height);
		}
		
		if(height > 0) {
			windowVbox.maxHeight(height);
			windowVbox.maxWidth(width);
		}

		// 内容
		VBox textSubVbox = new VBox();
		textSubVbox.setPadding(new Insets(0, 5, 5, 5));
		windowVbox.getChildren().add(textSubVbox);
		for (Node nd : nds) {
			textSubVbox.getChildren().add(nd);
			VBox.setMargin(nd, new Insets(0, 0, 5, 0));
		}
		// 最后的按钮
		AnchorPane foot = new AnchorPane();
		HBox hBoxBtn = new HBox();
		hBoxBtn.setSpacing(10);
		if (btns != null) {
			for (Node bn : btns) {
				hBoxBtn.getChildren().add(bn);
			}
			foot.getChildren().add(hBoxBtn);
			AnchorPane.setRightAnchor(hBoxBtn, 0.0);
		} else {
			JFXButton cancelbtn = new JFXButton("Cancel");
			cancelbtn.getStyleClass().add("myAlertBtn");
			foot.getChildren().add(cancelbtn);
			AnchorPane.setRightAnchor(cancelbtn, 0.0);
			cancelbtn.setOnAction(e -> {
				stage.close();
			});
		}

		VBox.setMargin(foot, new Insets(5, 5, 5, 5));
		windowVbox.getChildren().add(foot);
		VBox.setVgrow(textSubVbox, Priority.ALWAYS);

		return windowVbox;
	}


	// 给一个窗口加一个外壳, 包含一个头部的关闭按钮
	public static VBox getWindowShell(Stage stage, Node title, String css) {
		VBox subWindow = new VBox();
//		subWindow.getStyleClass().add("myShellWindow");
		subWindow.getStyleClass().add(css);

		AnchorPane anchorPane = new AnchorPane();
		JFXButton closeWindowBtn = new JFXButton();
		closeWindowBtn.setGraphic(ComponentGetter.getIconUnActive("window-close"));
		AnchorPane.setRightAnchor(closeWindowBtn, 0.0);
		AnchorPane.setTopAnchor(title, 4.0);
		AnchorPane.setLeftAnchor(title, 4.0);
		anchorPane.getChildren().addAll(closeWindowBtn, title);
		anchorPane.getStyleClass().add("subWindowClose");
		subWindow.getChildren().add(anchorPane);

		VBox.setMargin(anchorPane, new Insets(0, 0, 5, 0));
		closeWindowBtn.setOnAction(e -> {
			stage.close();
		});

		return subWindow;
	}



	public static void setSceneAndShow(Scene scene, Stage stage, boolean isWait) {

		stage.setMaximized(false);
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);// 设定窗口无边框
		CommonUtils.loadCss(scene);

		stage.setOnCloseRequest(v -> {

		});

		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination enterbtn = new KeyCodeCombination(KeyCode.ENTER);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(enterbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});
		if (isWait) {
			stage.showAndWait();
		} else {
			stage.show();
		}
	}

	public static void showAbout() {

		Label tit = new Label("Version: " + ConfigVal.version);
		Label text0 = new Label("DataBase Tool ");
		Label text1 = new Label("Author: tenie  Email: tenie@tenie.net");
		Label text2 = new Label("Github: https://github.com/tenie/SQLucky");

		final Stage stage = new Stage();

		JFXButton okbtn = new JFXButton("OK");
		CommonUtils.addCssClass(okbtn, "myAlertBtn");
		okbtn.setOnAction(value -> {
			stage.close();
		});
		List<Node> nds = new ArrayList<>();
		nds.add(tit);
		nds.add(text0);
		nds.add(text1);
		nds.add(text2);

		List<Node> btns = new ArrayList<>();
		btns.add(okbtn);

		VBox vb = DialogTools.getSceneVbox(stage, ComponentGetter.ABOUT, nds, btns);
		Scene scene = new Scene( vb);

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		DialogTools.setSceneAndShow(scene, stage, false);
	}

	// TODO
	public static void showExecWindow(String title, String containTxt, Consumer<String> caller) {
		TextField tf1 = new TextField("");
		tf1.getStyleClass().add("myFindTextField");
		tf1.setEditable(true);
		tf1.setPrefWidth(500);
		tf1.setText(containTxt);
		tf1.setPrefHeight(40);
		tf1.setFocusTraversable(true);

		Label tit = new Label(title);

		Stage stage = new Stage();
		
		JFXButton btn = new JFXButton("Cancel");
		btn.getStyleClass().add("myAlertBtn");
		btn.setOnAction(value -> {
			stage.close();
		});

		JFXButton okbtn = new JFXButton("OK");
		okbtn.getStyleClass().add("myAlertOkBtn");
		okbtn.setOnAction(value -> {
			String val = tf1.getText();
			caller.accept(val);
			stage.close();
		});


		List<Node> nds = new ArrayList<>();
		nds.add(tit);
		nds.add(tf1);
		
		List<Node> btns = new ArrayList<>();
		btns.add(btn);
		btns.add(okbtn);
		
		
		VBox vb = DialogTools.getSceneVbox(stage, ComponentGetter.INFO, nds, btns);
		Scene scene = new Scene(vb);
		stage.setOnShowing(e->{
			tf1.requestFocus();
		});
		stage.setTitle(title);
		stage.setScene(scene);

		stage.setMaximized(false);
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);// 设定窗口无边框
		CommonUtils.loadCss(scene);

		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination enterbtn = new KeyCodeCombination(KeyCode.ENTER);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(enterbtn, () -> {
			okbtn.fire();
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});
		stage.show();
	}

	// javafx 默认的确认对话框
	public static boolean Confirmation(String msg) {
		boolean tf = false;
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(msg);
		alert.setGraphic(IconGenerator.svgImageDefActive("question-circle", 25));
		ButtonType buttonTypeOne = new ButtonType("Continue", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeOne);
		alert.initOwner(ComponentGetter.primaryStage.getOwner());
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			tf = true;
		}
		return tf;
	}
}
