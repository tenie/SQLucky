package net.tenie.fx.component;

import java.io.IOException;
import java.net.URL;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.main.MainMyDB;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;

public class DataTransferWindow {
	Stage stage;
	Scene scene;
	public DataTransferWindow() {
//		CreateModalWindow();
		if(stage == null ) {
			showFxml("/fxml/transferData.fxml");
		}else {
			stage.show();
		}
			
	}
	
	public void show() {
		if(stage == null ) {
			showFxml("/fxml/transferData.fxml");
		}else { 
			stage.show();
		}
		
		CommonAction.loadCss(scene);
//		if(ConfigVal.THEME.equals( "DARK")) {
//			scene.getStylesheets().addAll(ConfigVal.cssList);
//		}else { 
//			scene.getStylesheets().addAll(ConfigVal.cssListLight); 
//		}
		 
		
		
	}
	
	// 根据给定的fxml 创建 模态框
	public void showFxml(    String fxml) {

		try {
		    stage = new Stage();
		    ComponentGetter.dataTransferStage = stage;
			stage.initModality(Modality.APPLICATION_MODAL);
//			stage.initOwner(stg);
			stage.setTitle("Top Stage With Modality");

			URL url = getClass().getResource(fxml);
			Parent root = FXMLLoader.load(url);
		    scene = new Scene(root);
		    CommonAction.loadCss(scene);
//			if(ConfigVal.THEME.equals( "DARK")) {
//				scene.getStylesheets().addAll(ConfigVal.cssList);
//			}else { 
//				scene.getStylesheets().addAll(ConfigVal.cssListLight); 
//			}
//			scene.getStylesheets().addAll(ConfigVal.cssList);
			stage.setScene(scene);
			stage.show();
			
			Image	img = new Image(MainMyDB.class.getResourceAsStream(ConfigVal.appIcon));
			stage.getIcons().add(img);
			stage.setOnCloseRequest(ev->{
				stage.hide();
				ev.consume();
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public   Stage CreateModalWindow() {

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
}
