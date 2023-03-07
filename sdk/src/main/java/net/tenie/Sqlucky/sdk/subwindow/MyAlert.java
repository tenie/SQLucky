package net.tenie.Sqlucky.sdk.subwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;

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
	// 显示警告, 会阻塞ui线程, 等前台关闭警告窗口后执行后面的代码
	public static void errorAlert( String containTxt, boolean isWait) {
		showErrorMsg( containTxt , ComponentGetter.ERROR, isWait);  
	}
	
	
	public static void showErrorMsg( String containTxt ,Label tit) {
		showErrorMsg(containTxt, tit, false);
	}
	
	public static void showErrorMsg( String containTxt ,Label tit, boolean iswait) {
		 
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
		DialogTools.setSceneAndShow(scene, stage, iswait);
	}
	

	/**
	 * 确认对话框
	 * @param promptInfo  提示信息
	 * @param caller      得到确认后的执行函数
	 */
	public static void myConfirmation(String promptInfo,  Consumer< String >  caller) {
		myConfirmation(promptInfo, caller, null);
	}
	
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
		btns.add( btn);
		btns.add( okbtn); 
		
		myConfirmation(promptInfo, stage, btns, true);
		return rsval.get();
	}
	
	
	public static void myConfirmation(String promptInfo,  Consumer< String >  okCaller, Consumer< String >  cancelCaller ) {
		final Stage stage = new Stage();
	
		JFXButton btn = new JFXButton("Cancel(N)");
		btn.getStyleClass().add("myAlertBtn");
		
		
		btn.setOnAction(value -> { 
			if(cancelCaller !=null) {
				cancelCaller.accept("");
			}
			stage.close();
		});
		
		JFXButton okbtn = new JFXButton("Yes(Y)");
		okbtn.getStyleClass().add("myAlertOkBtn");
		okbtn.setOnAction(value -> {
			if(okCaller !=null) {
				okCaller.accept("");
			} 
			stage.close(); 
		});
		
		List<Node> btns = new ArrayList<>();
		btns.add( btn);
		btns.add( okbtn); 
		
		myConfirmation(promptInfo, stage, btns, false);
	}
	
	
	public static void myConfirmation(String promptInfo, Stage stage  , List<Node> btns, boolean isWait ) {
		Label space = new Label(""); 
		Label tit = new Label(promptInfo); 
		
		List<Node> nds = new ArrayList<>();
		nds.add( space); 
		nds.add( tit); 

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
		for(var nd : btns) {
			if(nd instanceof Button ) {
				Button tmp = (Button) nd;
				if(tmp.getText().contains("(Y)")) {
					scene.getAccelerators().put(kcY, () -> {
						tmp.fire();
					});
				}
				if(tmp.getText().contains("(N)")) {
					scene.getAccelerators().put(kcN, () -> {
						tmp.fire();
					});
				}
				if(tmp.getText().contains("(C)")) {
					scene.getAccelerators().put(kcC, () -> {
						tmp.fire();
					});
				}
			}
		}
		
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
