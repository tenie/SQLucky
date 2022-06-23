package net.tenie.Sqlucky.sdk.component;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;

public class LoadingAnimation {
	private static Label lb  ;
	private static Region  Animation ;
	
	public static void ChangeLabelText(String val) {
		if(lb !=null) {
			Platform.runLater(() -> {
				lb.setText(val);
			});
		}
	}
	
	// 添加loading... 动画
	public static  void addLoading(StackPane root) {
		Platform.runLater(() -> {
			lb = new Label("Loading.....");
			Animation = IconGenerator.svgImageUnactive("icomoon-spinner3", 30);
			CommonUtility.rotateTransition(Animation);
			lb.setGraphic(Animation);
			lb.setFont(new Font(30));
			StackPane.setAlignment(lb, Pos.CENTER);
			root.setCursor(Cursor.WAIT);
			root.getChildren().add(lb);
		});

	}
	
	public static  void addLoading(StackPane root, String loadingString) {
		Platform.runLater(() -> {
			lb = new Label(loadingString);
			Animation = IconGenerator.svgImageUnactive("icomoon-spinner3", 30);
			CommonUtility.rotateTransition(Animation);
			lb.setGraphic(Animation);
			lb.setFont(new Font(30));
			StackPane.setAlignment(lb, Pos.CENTER);
			root.setCursor(Cursor.WAIT);
			root.getChildren().add(lb);
		});

	}
	
	//	移除loading...
	public static  void rmLoading(StackPane root) {
		Platform.runLater(()->{
			 FadeTransition fadeTransition = CommonUtility.fadeTransitionHidden(lb, 1500);
			 fadeTransition.setOnFinished(e ->{
				 root.getChildren().remove(lb);
				 root.setCursor(Cursor.DEFAULT);
				 
				 lb = null;
				 Animation = null;
			 });
		});
		 
	}
	
}
