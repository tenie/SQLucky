package net.tenie.fx.main;

import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MyPreloader extends Preloader {
	private Stage preloaderStage;
 
    @Override
    public void start(Stage primaryStage) throws Exception {
       this.preloaderStage = primaryStage;
       double w = 550.0;
   	   double h = 319.0;
       VBox loading = new VBox();
       loading.setMaxWidth(Region.USE_PREF_SIZE);
       loading.setMaxHeight(Region.USE_PREF_SIZE);
       loading.setStyle("-fx-background-color: #000000;");
//       Image i = new Image( MyPreloader.class.getResourceAsStream("/image/SQL6preLoad.png")); 
       Image i = new Image( MyPreloader.class.getResourceAsStream("/image/test2.gif")); 
       ImageView  mediaView =  new ImageView(i);
       mediaView.setFitWidth(w);
       mediaView.setFitHeight( h);
      
       ProgressBar prob = new ProgressBar();
       prob.setPrefWidth(300); 
       prob.getStyleClass().add("myProgressBar");  
       loading.getChildren().add(  mediaView);  
       loading.getChildren().add( prob);
       BorderPane root = new BorderPane(loading);
       Scene scene = new Scene(root);
       scene.getStylesheets().add(MyPreloader.class.getResource("/css/ProgressBar.css").toExternalForm());
       
//       primaryStage.setWidth(280);
//       primaryStage.setHeight(245);
       primaryStage.setWidth(w);
       primaryStage.setHeight( h);
       primaryStage.setMaximized(false);
       primaryStage.setResizable(false);
       primaryStage.initStyle(StageStyle.UNDECORATED);//设定窗口无边框
       primaryStage.setScene(scene);
       
       primaryStage.show();
       
      
   }
 
   @Override
   public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
      if (stateChangeNotification.getType() == Type.BEFORE_START) {
         preloaderStage.hide();
      }
   }
}
