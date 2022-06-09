package net.tenie.Sqlucky.sdk.subwindow;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;

/**
 * 
 * @author tenie
 *
 */
public class ModalDialog {
	private static Logger logger = LogManager.getLogger(ModalDialog.class);
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
	public ModalDialog(Parent node, TableView<SqlFieldPo>  tv ,String title) {
		try {
			
			final Stage stage = new Stage();
			Scene scene = new Scene(node);
			 
			CommonUtility.loadCss(scene);
			Image img = ComponentGetter.LogoIcons; //new Image(ModalDialog.class.getResourceAsStream(ConfigVal.appIcon));
			stage.getIcons().add(img);
			

			stage.initModality(Modality.WINDOW_MODAL);
			stage.setTitle(title);

			stage.setScene(scene);

			stage.show();
			tv.getSelectionModel().select(0);
			stage.setOnCloseRequest(v -> {

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 根据给定的node 创建 模态框
		public ModalDialog(Parent node, String title) {
			try {
				
				final Stage stage = new Stage();
				Scene scene = new Scene(node);
				 
				CommonUtility.loadCss(scene);
				Image img = ComponentGetter.LogoIcons; //new Image(ModalDialog.class.getResourceAsStream(ConfigVal.appIcon));
				stage.getIcons().add(img);

				stage.initModality(Modality.WINDOW_MODAL);
				stage.setTitle(title);

				stage.setScene(scene);

				stage.show();
//				tv.getSelectionModel().select(0);
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
		hideBottom.setGraphic(IconGenerator.svgImageUnactive("window-close"));
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

//		scene.getStylesheets().addAll(ConfigVal.cssList);
		CommonUtility.loadCss(scene);

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
	

	// 在提示框里执行
	public static void ModalDialogAppCallConsumer(VBox node, String title, Consumer< String >  caller) {
		try {
			node.getStyleClass().add("myAlert");
			final Stage stage = new Stage();
			Scene scene = new Scene(node);
			JFXButton btn = new JFXButton("No");
			btn.setOnAction(value -> {
				stage.close();
			});
			
			JFXButton okbtn = new JFXButton("YES");
			okbtn.setOnAction(value -> {
				caller.accept("");
				stage.close();
			});
			
			
			AnchorPane pn = new AnchorPane();
			pn.getChildren().addAll(okbtn, btn);
			AnchorPane.setRightAnchor(btn, 0.0);
			AnchorPane.setRightAnchor(okbtn, 60.0);
			node.getChildren().add(pn);
 
			

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle(title);
			stage.setScene(scene);
			DialogTools.setSceneAndShow(scene, stage);
 
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
			logger.info(listdata);
		});
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

	
	
	
	
	public static void showComfirmExec(String title, String containTxt ,  Consumer< String >  caller) {
		VBox vb = new VBox();
		TextField tf1 = new TextField("");
		tf1.setEditable(false);
		tf1.setPrefWidth(500);
		tf1.setStyle("-fx-background-color: transparent;");
		tf1.setText(containTxt);
		tf1.setPrefHeight(40);
		tf1.setFocusTraversable(false);
		Label tit = new Label(title);
		
		
		vb.getChildren().add(tit);
		vb.getChildren().add(tf1);
		vb.setPrefWidth(500);
		vb.setPadding(new Insets(20));
		vb.setPrefHeight(100);
		vb.maxHeight(100);
		vb.maxWidth(500);
		ModalDialog.ModalDialogAppCallConsumer(vb, title, caller); 
	}
	
	//TODO
	public static void showExecWindow2(String title, String containTxt ,  Consumer< String >  caller) {
		VBox vb = new VBox();
		TextField tf1 = new TextField("");
		tf1.getStyleClass().add("myFindTextField");
		tf1.setEditable(true);
		tf1.setPrefWidth(500);
		//tf1.setStyle("-fx-background-color: transparent;");
		tf1.setText(containTxt);
		tf1.setPrefHeight(40);
		tf1.setFocusTraversable(false);
		
		Label tit = new Label(title);
		vb.getChildren().add(tit);
		vb.getChildren().add(tf1);
		vb.setPrefWidth(500);
//		vb.setPadding(new Insets(0,20,20,20));
		vb.setPrefHeight(100);
		vb.maxHeight(100);
		vb.maxWidth(500);
//		ModalDialog.ModalDialogAppCallConsumer(vb, title, caller); 
		vb.getStyleClass().add("myAlert");
		final Stage stage = new Stage();
		Scene scene = new Scene(vb);
		JFXButton btn = new JFXButton("Cancel");
		btn.setOnAction(value -> {
			stage.close();
		});
		
		JFXButton okbtn = new JFXButton("OK");
		okbtn.setOnAction(value -> {
			String val = tf1.getText();
			caller.accept(val);
			stage.close();
		});
		
		
		AnchorPane pn = new AnchorPane();
		pn.getChildren().addAll(okbtn, btn);
		AnchorPane.setRightAnchor(btn, 0.0);
		AnchorPane.setRightAnchor(okbtn, 60.0);
		vb.getChildren().add(pn);

//		setVboxShape(vb, stage);

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle(title);
		stage.setScene(scene);
		DialogTools.setSceneAndShow(scene, stage);
	}
	
	
	public static void showExecWindow(String promptInfo, String containTxt ,  Consumer< String >  caller) {
		TextField tf1 = new TextField("");
		tf1.getStyleClass().add("myFindTextField");
		tf1.setEditable(true);
		tf1.setPrefWidth(500);
		tf1.setText(containTxt);
		tf1.setPrefHeight(40);
		tf1.setFocusTraversable(false);
		
		Label tit = new Label(promptInfo);  
		final Stage stage = new Stage();
		
		JFXButton btn = new JFXButton("Cancel");
		btn.getStyleClass().add("myAlertBtn");
		btn.setOnAction(value -> {
			stage.close();
		});
		
		JFXButton okbtn = new JFXButton("OK");
		okbtn.setOnAction(value -> {
			String val = tf1.getText();
			caller.accept(val);
			stage.close();
		});
		
		List<Node> nds = new ArrayList<>();
		
		nds.add( tit);
		nds.add( tf1);
		
		List<Node> btns = new ArrayList<>();
		btns.add( btn);
		btns.add( okbtn);
		
		
		Node vb = DialogTools.setVboxShape(stage, ComponentGetter.WARN, nds, btns);
		Scene scene = new Scene((Parent) vb);
		
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		DialogTools.setSceneAndShow(scene, stage);  
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
	

	// about window
	public static void showAbout() { 
		
		Label tit = new Label("Version: 2.0.0");  
		Label text0 = new Label("#DataBase Tool #Open Source  #JAVA16 #JAVAFX ");  
		Label text1 = new Label("Author: tenie  Email: tenie@tenie.net");  
		Label text2 = new Label("Github: https://github.com/tenie/SQLucky");  
		
		 
		final Stage stage = new Stage(); 
		
		JFXButton okbtn = new JFXButton("OK");
		CommonUtility.addCssClass(okbtn, "myAlertBtn");
		okbtn.setOnAction(value -> {
			stage.close();
		}); 
		List<Node> nds = new ArrayList<>(); 
		nds.add( tit); 
		nds.add( text0); 
		nds.add( text1); 
		nds.add( text2);  
		
		List<Node> btns = new ArrayList<>(); 
		btns.add( okbtn);
		
		
		Node vb = DialogTools.setVboxShape(stage, ComponentGetter.ABOUT, nds, btns);
		Scene scene = new Scene((Parent) vb);
		
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		DialogTools.setSceneAndShow(scene, stage);  
	}
	
	
}
