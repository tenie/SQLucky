package net.tenie.fx.main;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MyPreloaderGif extends Preloader {
	private static Stage preloaderStage; 
	private volatile static boolean isFinish = false;
	
	public static boolean getFinish() {
		return isFinish;
	}
	
	public static void  hiden() {
		if(preloaderStage!= null) {
			Thread th = new Thread() {
				public void run() {
					 var tf = getFinish();
					 while(!tf) {
						 tf = getFinish();
						 try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					 }
					 Platform.runLater(()->{
						 preloaderStage.close();
					 });
				}
			};
			th.start();
		}
		
	}
	
   private void stopTime() {
	   Thread th = new Thread() {
			public void run() {
				 try {
					Thread.sleep(4000);
					isFinish = true;
				} catch (InterruptedException e) { 
					e.printStackTrace();
				}
			}
		};
		th.start();
	}
    @Override
    public void start(Stage primaryStage) throws Exception {
       this.preloaderStage = primaryStage;
       double w = 550.0;
   	   double h = 319.0;
       VBox loading = new VBox();
       loading.setMaxWidth(Region.USE_PREF_SIZE);
       loading.setMaxHeight(Region.USE_PREF_SIZE);
       loading.setStyle("-fx-background-color: #000000;");  
//       Image i = new Image( MyPreloaderGif.class.getResourceAsStream("/image/sqlucky.gif"));
       var jpg = MyPreloaderGif.class.getResourceAsStream("/image/sqlucky_img.jpg");
       Image i = new Image( jpg); 
       ImageView  mediaView =  new ImageView(i);
       mediaView.setFitWidth(w);
       mediaView.setFitHeight( h); 

       loading.getChildren().add(  mediaView);   
       BorderPane root = new BorderPane(loading);
       Scene scene = new Scene(root);
       
       primaryStage.setWidth(w);
       primaryStage.setHeight( h);
       primaryStage.setMaximized(false);
       primaryStage.setResizable(false);
       primaryStage.initStyle(StageStyle.UNDECORATED);//设定窗口无边框
       primaryStage.setScene(scene);
//       primaryStage.setAlwaysOnTop(true);
       primaryStage.show();
       stopTime();
//		Thread th = new Thread() {
//			public void run() {
//				try {
//					Thread.sleep(5000); 
//					Platform.runLater(() -> {
//						preloaderStage.close();
//					});
//
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		th.start();
      
   }
 
   @Override
   public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
//      if (stateChangeNotification.getType() == Type.BEFORE_START) {
//         preloaderStage.hide();
//      }
   }
}
