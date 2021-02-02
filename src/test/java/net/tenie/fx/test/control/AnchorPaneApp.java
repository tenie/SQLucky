package net.tenie.fx.test.control;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
 
/**
 * A simple example of an AnchorPane layout.
 */
public class AnchorPaneApp extends Application {
 
    public Parent createContent() {
        AnchorPane anchorPane = new AnchorPane();
 
        Label label1 = new Label("We are all in an AnchorPane.");
        String IMAGE = "SQL6.png";
        Image ICON_48 = new Image(getClass().getResourceAsStream(IMAGE));
        ImageView imageView = new ImageView(ICON_48);
        Button button1 = new Button("Submit");
 
        anchorPane.getChildren().addAll(label1, imageView, button1);
 
        AnchorPane.setTopAnchor(label1, Double.valueOf(2));
        AnchorPane.setLeftAnchor(label1, Double.valueOf(20));
//        AnchorPane.setTopAnchor(button1, Double.valueOf(40));
        AnchorPane.setLeftAnchor(button1, Double.valueOf(30));
        AnchorPane.setTopAnchor(imageView, Double.valueOf(75));
        AnchorPane.setLeftAnchor(imageView, Double.valueOf(20));
        return anchorPane;
    }
 
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }
 
    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}