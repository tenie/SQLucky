package net.tenie.fx.main;

import java.net.URL;

import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MyPreloaderMp4 extends Preloader {
	private static Stage preloaderStage;
	private volatile static boolean isFinish = false;
	private static StackPane loading;

	private MediaPlayer mediaPlayer;

	public static boolean getFinish() {
		return isFinish;
	}

	public static void hiden() {
		if (loading != null) {
			Thread th = new Thread() {
				@Override
				public void run() {
					var tf = getFinish();
					while (!tf) {
						tf = getFinish();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					Platform.runLater(() -> {
						preloaderStage.close();
					});

				}
			};
			th.start();

			// 动画有问题, 先注释了
//			 FadeTransition fadeTransition = CommonUtility.fadeTransitionHidden(loading, 1200, 0.8);
//			 fadeTransition.setOnFinished(e -> {
//				 preloaderStage.close();
//				  
//			  });

		}

	}

	private void stopTime() {
		Thread th = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
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
		double w = 550.0;
		double h = 319.0;
		URL valUrl2 = MyPreloaderMp4.class.getResource("/");
		URL valUrl = MyPreloaderMp4.class.getResource("/image/sqlucky_hd2.mp4");
		String filePath = valUrl.toExternalForm(); // MyPreloaderMp4.class.getResource("/image/sqlucky_hd2.mp4").toExternalForm();

		preloaderStage = primaryStage;

		loading = new StackPane();
		loading.setMaxWidth(Region.USE_PREF_SIZE);
		loading.setMaxHeight(Region.USE_PREF_SIZE);
		loading.setStyle("-fx-background-color: #000000;");

		Media media = new Media(filePath);
		mediaPlayer = new MediaPlayer(media);
		MediaView mediaView = new MediaView(mediaPlayer);

		mediaView.setFitWidth(w);
		mediaView.setFitHeight(h);
		mediaPlayer.play();

		loading.getChildren().add(mediaView);
		VBox.setVgrow(mediaView, Priority.ALWAYS);
		loading.setCursor(Cursor.WAIT);

		Scene scene = new Scene(loading);
		primaryStage.setWidth(w);
		primaryStage.setHeight(h);
		primaryStage.setMaximized(false);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.TRANSPARENT);// 设定窗口无边框
//       primaryStage.setAlwaysOnTop(true);
		primaryStage.setScene(scene);
		stopTime();
		primaryStage.show();

	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
		if (stateChangeNotification.getType() == Type.BEFORE_START) {
//			FadeTransition fadeTransition = CommonUtility.fadeTransitionHidden(loading, 2000);
//			fadeTransition.setOnFinished(e -> {
//				preloaderStage.hide();
//			});

		}
	}
}
