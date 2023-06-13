package net.tenie.Sqlucky.sdk.subwindow;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;

public class DialogTools {
	private static Logger logger = LogManager.getLogger(DialogTools.class);
		
	public static Node setVboxShape(Stage stage , Node title, List<Node> nds, List<Node> btns ) {
		return setVboxShape(500, 80, stage, title, nds, btns);
	}
	//TODO 设置子窗口的外形
	public static Node setVboxShape(double  width , double height, Stage stage , Node title, List<Node> nds, List<Node> btns ) {
		
		VBox subWindow = new VBox(); 
		if(width > 0 && height > 0) {
			subWindow.setPrefWidth(width); 
			subWindow.setPrefHeight(height);
			subWindow.maxHeight(height);
			subWindow.maxWidth(width);
		}
		
		
		// 内容
		for(Node nd : nds) {
			subWindow.getChildren().add(nd);
			VBox.setMargin(nd, new Insets(0, 0, 5, 0));
		}
		VBox.setVgrow(nds.get(0), Priority.ALWAYS);
		
		// 最后的按钮
		AnchorPane foot = new AnchorPane();  
		JFXButton cancelbtn = new JFXButton("Cancel");
		
		if(btns != null) {
			double i = 0.0;
			for(Node bn : btns) {
				foot.getChildren().add(bn);
				AnchorPane.setRightAnchor(bn, i);
				i +=80;
			}
			
		}else {
			foot.getChildren().add(cancelbtn);
			AnchorPane.setRightAnchor(cancelbtn, 0.0);
		}
		
		VBox.setMargin(foot, new Insets(0,0,5,0));
		
		subWindow.getChildren().add(foot);
		
		cancelbtn.setOnAction(e->{
			stage.close();
		});
		 
		
		
		subWindow.setPadding(new Insets(0,5,5,5));
		Node  subw = windowShell(stage, subWindow, title, "myAlert"); 
		
		return subw;
	}
	

	public static  void windowShell(Stage stage, Node title) {
		Scene scene  = stage.getScene();
		scene.getRoot();
		Node n = windowShell(stage, scene.getRoot() , title,  "myAlert" );
		scene.setRoot((Parent) n);
		
		stage.initModality(Modality.APPLICATION_MODAL);
//		stage.setScene(scene);
		setSceneAndShow(scene, stage, false);
	}
	
	
	// 给一个窗口加一个外壳, 包含一个头部的关闭按钮
	public static  Node windowShell(Stage stage, Node subNode, Node title, String css) {
		VBox subWindow = new VBox();
		subWindow.getStyleClass().add("myShellWindow");
		subWindow.getStyleClass().add(css);
		
		
		AnchorPane pn = new AnchorPane();  
		JFXButton btn = new JFXButton(); 
		btn.setGraphic(ComponentGetter.getIconUnActive("window-close"));
		AnchorPane.setRightAnchor(btn, 0.0);
		AnchorPane.setTopAnchor(title, 4.0);
		AnchorPane.setLeftAnchor(title, 4.0);
		pn.getChildren().addAll(btn, title );
		pn.getStyleClass().add("subWindowClose");
		
		subWindow.getChildren().add(pn);
		subWindow.getChildren().add(subNode);
		
		VBox.setMargin(pn, new Insets(0, 0, 5, 0));
		
		btn.setOnAction(e->{
			stage.close();
		});
		
		return subWindow;
	}
 
	 
	public static void setSceneAndShow(Scene scene , Stage stage , boolean isWait) {
		 
		stage.setMaximized(false);
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);// 设定窗口无边框
		CommonUtility.loadCss(scene);
		
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
		if(isWait) {
			stage.showAndWait();
		}else {
			stage.show();
		}
	}
	 
	 
	
}
