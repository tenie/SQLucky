package net.tenie.Sqlucky.sdk.subwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTextEditor;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

public class MyAlert {
	/**
	 * 不会阻塞当前线程的 alert
	 * 
	 * @param title
	 * @param containTxt 要展示的内容
	 */
	public static void infoAlert(String containTxt) {
		Platform.runLater(() -> {
			showErrorMsg(containTxt, ComponentGetter.INFO);
		});

	}

	/**
	 * 会阻塞当前线程的alert, 关闭alert窗口才会执行之后的代码
	 * 
	 * @param title
	 * @param containTxt
	 */
	public static void alertWait(String containTxt) {
		showMsg(containTxt, ComponentGetter.INFO, true);
	}

	/**
	 * 不会阻塞当前线程的 错误信息 alert
	 * 
	 * @param containTxt
	 */
	public static void errorAlert(String containTxt) {
		Platform.runLater(() -> {
			showErrorMsg(containTxt, ComponentGetter.ERROR);
		});
	}

	/**
	 * 错误信息 alert , 通过isWait 可以阻塞ui线程, 等前台关闭警告窗口后执行后面的代码
	 * 
	 * @param containTxt
	 * @param isWait     是否阻塞参数
	 */
	public static void errorAlert(String containTxt, boolean isWait) {
		showMsg(containTxt, ComponentGetter.ERROR, isWait);
	}

	/**
	 * 不会阻塞当前线程的 错误信息 alert
	 * 
	 * @param containTxt 要展示的信息
	 * @param title      自定义tiele
	 */
	public static void showErrorMsg(String containTxt, Label title) {
		showMsg(containTxt, title, false);
	}

	/**
	 * alter 展示框的代码, 可以自定义 title ,信息, 是否阻塞ui
	 * 
	 * @param containTxt
	 * @param title
	 * @param iswait
	 */
	public static void showMsg(String containTxt, Label title, boolean iswait) {

		TextField tf1 = new TextField("");
		tf1.setEditable(false);
		tf1.setPrefWidth(500);
		tf1.setStyle("-fx-background-color: transparent;");
		tf1.setText(containTxt);
		tf1.setPrefHeight(40);
		tf1.setFocusTraversable(false);

		List<Node> nds = new ArrayList<>();

		nds.add(tf1);

		final Stage stage = new Stage();
		JFXButton btn = new JFXButton("Close(C) ");
		btn.getStyleClass().add("myAlertBtn");
		btn.setOnAction(value -> {
			stage.close();
		});
		List<Node> btns = new ArrayList<>();
		btns.add(btn);

		Node vb = DialogTools.setVboxShape(stage, title, nds, btns);
		Scene scene = new Scene((Parent) vb);
		setKeyPress(scene, btns);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		DialogTools.setSceneAndShow(scene, stage, iswait);
	}

	/**
	 * 确认对话框
	 * 
	 * @param promptInfo 提示信息
	 * @param caller     得到确认后的执行函数
	 */
	public static void myConfirmation(String promptInfo, Consumer<String> caller) {
		myConfirmation(promptInfo, caller, null);
	}

	/**
	 * 确认窗口, 返回 true/false
	 * 
	 * @param promptInfo
	 * @return
	 */
	public static boolean myConfirmationShowAndWait(String promptInfo) {
		final Stage stage = new Stage();
		final SimpleBooleanProperty rsval = new SimpleBooleanProperty(false);
		JFXButton btn = new JFXButton("Cancel(N)");
		btn.getStyleClass().add("myAlertBtn");
		btn.setOnAction(value -> {
			stage.close();
		});

		JFXButton okbtn = new JFXButton("Yes(Y)");
		okbtn.getStyleClass().add("myAlertOkBtn");
		okbtn.setOnAction(value -> {
			rsval.set(true);
			stage.close();
		});

		List<Node> btns = new ArrayList<>();
		btns.add(btn);
		btns.add(okbtn);

		myConfirmation(promptInfo, stage, btns, true);
		return rsval.get();
	}

	/**
	 * 对展示的代码, 进行确认
	 * 
	 * @param promptInfo
	 * @param code
	 * @return
	 */
	public static boolean myCodeAreaConfirmation(String promptInfo, String code) {
		final Stage stage = new Stage();

		// 按钮设置
		final SimpleBooleanProperty rsval = new SimpleBooleanProperty(false);
		JFXButton btn = new JFXButton("Cancel(N)");
		btn.getStyleClass().add("myAlertBtn");
		btn.setOnAction(value -> {
			rsval.set(false);
			stage.close();
		});

		JFXButton okbtn = new JFXButton("Yes(Y)");
		okbtn.getStyleClass().add("myAlertOkBtn");
		okbtn.setOnAction(value -> {
			rsval.set(true);
			stage.close();
		});

		List<Node> btns = new ArrayList<>();
		btns.add(btn);
		btns.add(okbtn);

		// 内容设置
		Label question = new Label(promptInfo);
		// code
		MyTextEditor myTextArea = new MyTextEditor();
		StackPane codeAreaPane = myTextArea.getCodeAreaPane(code, false);
		codeAreaPane.setStyle("-fx-background-color: transparent;");

		List<Node> nds = new ArrayList<>();
		nds.add(codeAreaPane);
		nds.add(question);

		Node vb = DialogTools.setVboxShape(500, 150, stage, ComponentGetter.INFO, nds, btns);
		Scene scene = new Scene((Parent) vb);
		setKeyPress(scene, btns);

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		DialogTools.setSceneAndShow(scene, stage, true);
//		myConfirmation(promptInfo, stage, btns, true);

		return rsval.get();
	}

	public static void myConfirmation(String promptInfo, Consumer<String> okCaller, Consumer<String> cancelCaller) {
		final Stage stage = new Stage();

		JFXButton btn = new JFXButton("Cancel(N)");
		btn.getStyleClass().add("myAlertBtn");

		btn.setOnAction(value -> {
			if (cancelCaller != null) {
				cancelCaller.accept("");
			}
			stage.close();
		});

		JFXButton okbtn = new JFXButton("Yes(Y)");
		okbtn.getStyleClass().add("myAlertOkBtn");
		okbtn.setOnAction(value -> {
			if (okCaller != null) {
				okCaller.accept("");
			}
			stage.close();
		});

		List<Node> btns = new ArrayList<>();
		btns.add(btn);
		btns.add(okbtn);

		myConfirmation(promptInfo, stage, btns, false);
	}

	public static void myConfirmation(String promptInfo, Stage stage, List<Node> btns, boolean isWait) {
		MyTextEditor myTextArea = new MyTextEditor();
		StackPane codeAreaPane = myTextArea.getCodeAreaPane(promptInfo, false);
		codeAreaPane.setStyle("-fx-background-color: transparent;");

		List<Node> nds = new ArrayList<>();
		nds.add(codeAreaPane);

		Node vb = DialogTools.setVboxShape(stage, ComponentGetter.INFO, nds, btns);
		Scene scene = new Scene((Parent) vb);
		setKeyPress(scene, btns);

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		DialogTools.setSceneAndShow(scene, stage, isWait);
	}

	// 设置键盘按钮按下触发button的action事件函数
	public static void setKeyPress(Scene scene, List<Node> btns) {
		KeyCodeCombination kcY = new KeyCodeCombination(KeyCode.Y);
		KeyCodeCombination kcN = new KeyCodeCombination(KeyCode.N);
		KeyCodeCombination kcC = new KeyCodeCombination(KeyCode.C);
		for (var nd : btns) {
			if (nd instanceof Button tmp) {
//				Button tmp = (Button) nd;
				if (tmp.getText().contains("(Y)")) {
					scene.getAccelerators().put(kcY, () -> {
						tmp.fire();
					});
				}
				if (tmp.getText().contains("(N)")) {
					scene.getAccelerators().put(kcN, () -> {
						tmp.fire();
					});
				}
				if (tmp.getText().contains("(C)")) {
					scene.getAccelerators().put(kcC, () -> {
						tmp.fire();
					});
				}
			}
		}

	}

	/**
	 * 横条行提示, 在代码编辑框上面线索, 3秒后自动关闭
	 * 
	 * @param title
	 */
	public static void showNotifiaction(String title) {
		var notificationPane = ComponentGetter.notificationPane;
		notificationPane.setText(title);
		if (!CommonConst.THEME_LIGHT.equals(ConfigVal.THEME)) {
			if (!notificationPane.getStyleClass().contains(NotificationPane.STYLE_CLASS_DARK)) {
				notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
			}
		} else {
			if (notificationPane.getStyleClass().contains(NotificationPane.STYLE_CLASS_DARK)) {
				notificationPane.getStyleClass().remove(NotificationPane.STYLE_CLASS_DARK);
			}
		}

		if (notificationPane.isShowing()) {
			notificationPane.hide();
		} else {
			notificationPane.show();
		}
	}

	// 右上角提示框, 3秒后自动关闭
	public static void notification(String title, String str, NotificationType type) {

		Notifications notificationBuilder = Notifications.create().title(title).text(str)
//	                .graphic(graphic)
				.hideAfter(Duration.seconds(3)).position(Pos.TOP_RIGHT)
//	                .onAction(e -> System.out.println("Notification clicked on!"))
//	                .threshold((int) thresholdSlider.getValue(),
//	                        Notifications.create().title("Threshold Notification"))
		;
		if (!CommonConst.THEME_LIGHT.equals(ConfigVal.THEME)) {
			notificationBuilder.darkStyle();
		}
		switch (type) {
		case Error:
//			 notificationBuilder.graphic( ComponentGetter.ERROR);
			notificationBuilder.showError();
			break;

		case Warning:
//			 notificationBuilder.graphic( ComponentGetter.WARN);
			notificationBuilder.showWarning();
			break;

		case Information:
//			 notificationBuilder.graphic( ComponentGetter.INFO);
			notificationBuilder.showInformation();
			break;

		case Confirm:
//			 notificationBuilder.graphic( ComponentGetter.INFO);
			notificationBuilder.showConfirm();
			break;

		default:
//			notificationBuilder.graphic( ComponentGetter.INFO);
			notificationBuilder.show();
			break;

		}

	}

	public enum NotificationType {
		Error, Warning, Information, Confirm, show;

	}

	/**
	 * 弹出 TextArea 的窗口
	 */
	public static void showTextArea(String title, String text) {
		Platform.runLater(() -> {
			Label titleLabel = new Label(title);
			titleLabel.setGraphic(IconGenerator.svgImage("info-circle", "#7CFC00"));
			MyTextEditor myTextArea = new MyTextEditor();
			StackPane codeAreaPane = myTextArea.getCodeAreaPane(text, false);
			codeAreaPane.setStyle("-fx-background-color: transparent;");

			List<Node> nds = new ArrayList<>();

			nds.add(codeAreaPane);

			final Stage stage = new Stage();
			JFXButton btn = new JFXButton("Close(C) ");
			btn.getStyleClass().add("myAlertBtn");
			btn.setOnAction(value -> {
				stage.close();
			});

			JFXButton copyText = new JFXButton("Copy");
			copyText.getStyleClass().add("myAlertBtn");
			copyText.setOnAction(value -> {
				CommonUtils.setClipboardVal(text);
//					stage.close();
			});

			List<Node> btns = new ArrayList<>();
			btns.add(btn);
			btns.add(copyText);
			Node vb = DialogTools.setVboxShape(500, 180, stage, titleLabel, nds, btns);
			Scene scene = new Scene((Parent) vb);
			setKeyPress(scene, btns);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);
			DialogTools.setSceneAndShow(scene, stage, true);
		});
	}
}
