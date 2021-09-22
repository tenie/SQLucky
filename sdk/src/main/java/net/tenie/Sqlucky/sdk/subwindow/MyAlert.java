package net.tenie.Sqlucky.sdk.subwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class MyAlert {
	public static void infoAlert(String title, String containTxt) {
		showErrorMsg(containTxt,ComponentGetter.INFO);
	}

	public static void errorAlert( String containTxt) {
		showErrorMsg( containTxt , ComponentGetter.ERROR);  
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
		
		Node vb = DialogTools.setVboxShape(stage, tit, nds, btns);
		Scene scene = new Scene((Parent) vb);
		
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		DialogTools.setSceneAndShow(scene, stage);
	}
	

	/**
	 * 确认对话框
	 * @param promptInfo  提示信息
	 * @param caller      得到确认后的执行函数
	 */
	public static void myConfirmation(String promptInfo,  Consumer< String >  caller) {
		myConfirmation(promptInfo, caller, null);
	}
	
	public static void myConfirmation(String promptInfo,  Consumer< String >  okCaller, Consumer< String >  cancelCaller ) {
		final Stage stage = new Stage();
	
		JFXButton btn = new JFXButton("Cancel");
		btn.getStyleClass().add("myAlertBtn");
		btn.setOnAction(value -> { 
			if(cancelCaller !=null) {
				cancelCaller.accept("");
			}
			stage.close();
		});

		JFXButton okbtn = new JFXButton("OK");
		okbtn.setOnAction(value -> {
			if(okCaller !=null) {
				okCaller.accept("");
			} 
			stage.close(); 
		});
		
		List<Node> btns = new ArrayList<>();
		btns.add( btn);
		btns.add( okbtn); 
		
		myConfirmation(promptInfo, stage, btns);
	}
	
	
	public static void myConfirmation(String promptInfo, Stage stage  , List<Node> btns ) {
		Label space = new Label(""); 
		Label tit = new Label(promptInfo); 
		
		List<Node> nds = new ArrayList<>();
		nds.add( space); 
		nds.add( tit); 

		Node vb = DialogTools.setVboxShape(stage, ComponentGetter.INFO, nds, btns);
		Scene scene = new Scene((Parent) vb);
		
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		DialogTools.setSceneAndShow(scene, stage);  
	}
	
}
