package net.tenie.Sqlucky.sdk.subwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.myEvent;

public class MyAlert {
	public static void infoAlert(String title, String containTxt) {
		Platform.runLater(()->{
			showErrorMsg(containTxt,ComponentGetter.INFO);
		});
		
	}

	public static void errorAlert( String containTxt) {
		Platform.runLater(()->{
			showErrorMsg( containTxt , ComponentGetter.ERROR);  
		});
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

		JFXButton okbtn = new JFXButton("Yes");
		okbtn.setOnAction(value -> {
			if(okCaller !=null) {
				okCaller.accept("");
			} 
			stage.close(); 
		});
		okbtn.setOnMouseClicked(value -> { 
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
		KeyCodeCombination kcY = new KeyCodeCombination(KeyCode.Y);
		scene.getAccelerators().put(kcY, () -> {
			for(var nd : btns) {
				if(nd instanceof Button ) {
					Button tmp = (Button) nd;
					if(tmp.getText().equals("Yes")) {
						myEvent.btnClick(tmp);
//						tmp.getOnAction()
					}
				}
			}
		});
		
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		DialogTools.setSceneAndShow(scene, stage);  
	}
	
	
	 public static void notification(String title, String str, NotificationType type ) {
		 
		 Notifications notificationBuilder = Notifications.create()
	                .title(title)
	                .text(str)
//	                .graphic(graphic)
	                .hideAfter(Duration.seconds(2.5))
	                .position(Pos.TOP_RIGHT)
//	                .onAction(e -> System.out.println("Notification clicked on!"))
//	                .threshold((int) thresholdSlider.getValue(),
//	                        Notifications.create().title("Threshold Notification"))
	                ;
		if(! CommonConst.THEME_LIGHT.equals(ConfigVal.THEME)) {
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
	 
	 public  enum NotificationType{
		 Error,
		 Warning,
		 Information,
		 Confirm,
		 show;
		 
	 }
}
