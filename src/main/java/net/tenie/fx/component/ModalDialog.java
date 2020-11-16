package net.tenie.fx.component;

import javafx.stage.*;
import net.tenie.fx.config.ConfigVal;
import javafx.scene.*;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;

/*   @author tenie */
public class ModalDialog {
	private static void resize(SVGPath svg, double width, double height) {

		double originalWidth = svg.prefWidth(-1);
		double originalHeight = svg.prefHeight(originalWidth);

		double scaleX = width / originalWidth;
		double scaleY = height / originalHeight;

		svg.setScaleX(scaleX);
		svg.setScaleY(scaleY);
	}

	// 根据给定的fxml 创建 模态框
	public static void test() {
		try {

			SplitPane spane = new SplitPane();

			final Stage stage = new Stage();
			VBox vb = new VBox();

			Text text = new Text();
			text.setFont(new Font(20));
			text.setStyle("-fx-fill: #214283;");
			text.setText("First row\nSecond row");
			spane.getItems().add(text);

			text = new Text();
			text.setFont(new Font(33));
			text.setStyle("-fx-fill: #214283;");
			text.setText("First row\nSecond row");

			SVGPath p = new SVGPath();
			p.setContent(
					"M1344 800v64q0 14-9 23t-23 9h-352v352q0 14-9 23t-23 9h-64q-14 0-23-9t-9-23v-352h-352q-14 0-23-9t-9-23v-64q0-14 9-23t23-9h352v-352q0-14 9-23t23-9h64q14 0 23 9t9 23v352h352q14 0 23 9t9 23zm128 448v-832q0-66-47-113t-113-47h-832q-66 0-113 47t-47 113v832q0 66 47 113t113 47h832q66 0 113-47t47-113zm128-832v832q0 119-84.5 203.5t-203.5 84.5h-832q-119 0-203.5-84.5t-84.5-203.5v-832q0-119 84.5-203.5t203.5-84.5h832q119 0 203.5 84.5t84.5 203.5z");

			Region svgShape = new Region();
			svgShape.setShape(p);
			svgShape.setMinSize(20, 20);
			svgShape.setPrefSize(20, 20);
			svgShape.setMaxSize(20, 20);
			svgShape.setStyle("-fx-background-color: red;");

			Label r = new Label();

			r.setGraphic(svgShape);
			spane.getItems().add(r);
			vb.getChildren().add(spane);

			Scene scene = new Scene(vb);

			stage.initModality(Modality.WINDOW_MODAL);
			stage.setTitle("Top Stage With Modality");

			stage.setScene(scene);

			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 根据给定的fxml 创建 模态框
	public ModalDialog(final Stage stg, String fxml) {

		try {
			final Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(stg);
			stage.setTitle("Top Stage With Modality");

			URL url = getClass().getResource(fxml);
			Parent root = FXMLLoader.load(url);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 根据给定的fxml 创建 模态框
	public ModalDialog(String fxml) {

		try {
			final Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Top Stage With Modality");

			URL url = getClass().getResource(fxml);
			Parent root = FXMLLoader.load(url);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 根据给定的node 创建 模态框
	public ModalDialog(Parent node, String title) {
		try {

			final Stage stage = new Stage();
			Scene scene = new Scene(node);
			scene.getStylesheets().addAll(ConfigVal.cssList);

			stage.initModality(Modality.WINDOW_MODAL);
			stage.setTitle(title);

			stage.setScene(scene);

			stage.show();
			stage.setOnCloseRequest(v -> {

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Stage CreateModalWindow() {

		final Stage stage = new Stage();
		VBox vb = new VBox();
		vb.getStyleClass().add("myAlert");
		Scene scene = new Scene(vb);

		vb.setPrefWidth(500);
		vb.maxWidth(500);

		AnchorPane closepane = new AnchorPane();
		JFXButton hideBottom = new JFXButton();
		hideBottom.setGraphic(ImageViewGenerator.svgImageUnactive("window-close"));
		closepane.getChildren().add(hideBottom);
		AnchorPane.setRightAnchor(hideBottom, 0.0);
		hideBottom.setOnAction(v -> {
			stage.close();
		});
		vb.getChildren().add(closepane);

		AnchorPane bottomPane = new AnchorPane();
		bottomPane.setPadding(new Insets(10));
		JFXButton btn = new JFXButton("Close");
		btn.setOnAction(value -> {
			stage.close();
		});
		bottomPane.getChildren().add(btn);
		AnchorPane.setRightAnchor(btn, 0.0);

		vb.getChildren().add(bottomPane);
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

		scene.getStylesheets().addAll(ConfigVal.cssList);

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);

		stage.setMaximized(false);
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);// 设定窗口无边框

		stage.show();
		stage.setOnCloseRequest(v -> {

		});
		return stage;
	}

	public static void ModalDialogApp(VBox node, String title) {
		try {
			node.getStyleClass().add("myAlert");
			final Stage stage = new Stage();
			Scene scene = new Scene(node);
			Button btn = new Button("Close");
			btn.setOnAction(value -> {
				stage.close();
			});
			AnchorPane pn = new AnchorPane();
			pn.getChildren().add(btn);
			AnchorPane.setRightAnchor(btn, 0.0);
			node.getChildren().add(pn);

			scene.getStylesheets().addAll(ConfigVal.cssList);

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle(title);
			stage.setScene(scene);

			stage.setMaximized(false);
			stage.setResizable(false);
			stage.initStyle(StageStyle.UNDECORATED);// 设定窗口无边框

			stage.show();
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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 根据给定的fxml 创建 模态框
	public ModalDialog(final Stage stg, String fxml, Modality modality) {

		try {
			final Stage stage = new Stage();
			stage.initModality(modality);
			stage.initOwner(stg);
			stage.setTitle("Top Stage With Modality");

			URL url = getClass().getResource(fxml);
			Parent root = FXMLLoader.load(url);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void CommonDialog(Parent node) {
		Dialog<List<String>> dialog = new Dialog<>();
		dialog.initModality(Modality.WINDOW_MODAL);
		ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType);
		dialog.getDialogPane().setContent(node);

		// 界面消失后的事件, 点击ok/取消 会触发改方法
		dialog.setResultConverter(dialogButton -> {
			// 判断是否是按了 ok 后消失
			if (dialogButton == okButtonType) {

			}
			return null;
		});

		// show 界面
		Optional<List<String>> result = dialog.showAndWait();

		// 界面关闭后进入
		result.ifPresent(listdata -> {
			System.out.println(listdata);
		});
	}

	public static void errorAlert(String title, String containTxt) {
		showErrorMsg(title, containTxt);
	}

	public static void infoAlert(String title, String containTxt) {
		showErrorMsg(title, containTxt);
	}

	public static void showErrorMsg2(String title, String containTxt) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Info");
		alert.setHeaderText(title);
		String exceptionText = containTxt;

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

	public static void showErrorMsg(String title, String containTxt) {
		VBox vb = new VBox();
		TextField tf1 = new TextField("");
		tf1.setEditable(false);
		tf1.setPrefWidth(500);
		tf1.setStyle("-fx-background-color: transparent;");
		tf1.setText(containTxt);
		tf1.setPrefHeight(40);
		tf1.setFocusTraversable(false);

		vb.getChildren().add(tf1);
		vb.setPrefWidth(500);
		vb.setPadding(new Insets(20));
		vb.setPrefHeight(100);
		vb.maxHeight(100);
		vb.maxWidth(500);
		ModalDialog.ModalDialogApp(vb, title);

	}

	// 确认对话框
	public static boolean Confirmation(String msg) {
		boolean tf = false;
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(msg);
		alert.setGraphic(ImageViewGenerator.svgImageDefActive("question-circle", 25));
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
