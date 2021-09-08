package net.tenie.fx.window;

import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.tenie.fx.Action.CommonAction;

public class MyAlert {
	public static void infoAlert(String title, String containTxt) {
		showErrorMsg(containTxt, ModalDialog.INFO);
	}

	public static void errorAlert( String containTxt) {
		showErrorMsg( containTxt , ModalDialog.ERROR);  
	}
	
	
	public static void showErrorMsg( String containTxt ,Label tit) {
 
		TextField tf1 = new TextField("");
		tf1.setEditable(false);
		tf1.setPrefWidth(500);
		tf1.setStyle("-fx-background-color: transparent;");
		tf1.setText(containTxt);
		tf1.setPrefHeight(40);
		tf1.setFocusTraversable(false); 
		
        List<Node> nds = new ArrayList<>();
		 
		nds.add( tf1);
		
		final Stage stage = new Stage(); 
		JFXButton btn = new JFXButton("Cancel");
		btn.getStyleClass().add("myAlertBtn");
		btn.setOnAction(value -> {
			stage.close();
		});
		List<Node> btns = new ArrayList<>();
		btns.add( btn); 
		
		Node vb = ModalDialog.setVboxShape(stage, tit, nds, btns);
		Scene scene = new Scene((Parent) vb);
		
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		ModalDialog.setSceneAndShow(scene, stage);
	}
	
//	public static void ModalDialogApp(VBox node) {
//		try {
//			node.getStyleClass().add("myAlert");
//			final Stage stage = new Stage();
//			Scene scene = new Scene(node);
//			JFXButton btn = new JFXButton("Close");
//			btn.getStyleClass().add("myAlertBtn");
//			btn.setOnAction(value -> {
//				stage.close();
//			});
//			AnchorPane pn = new AnchorPane();
//			pn.getChildren().add(btn);
//			AnchorPane.setRightAnchor(btn, 0.0);
//			node.getChildren().add(pn);
//
////			scene.getStylesheets().addAll(ConfigVal.cssList);
//			CommonAction.loadCss(scene);
//
//			stage.initModality(Modality.APPLICATION_MODAL);
////			stage.setTitle(title);
//			stage.setScene(scene);
//
//			stage.setMaximized(false);
//			stage.setResizable(false);
//			stage.initStyle(StageStyle.UNDECORATED);// 设定窗口无边框
//
//			stage.show();
//			stage.setOnCloseRequest(v -> {
//
//			});
//
//			KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
//			KeyCodeCombination enterbtn = new KeyCodeCombination(KeyCode.ENTER);
//			KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
//			scene.getAccelerators().put(escbtn, () -> {
//				stage.close();
//			});
//			scene.getAccelerators().put(enterbtn, () -> {
//				stage.close();
//			});
//			scene.getAccelerators().put(spacebtn, () -> {
//				stage.close();
//			});
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
