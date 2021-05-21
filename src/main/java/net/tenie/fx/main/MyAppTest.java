package net.tenie.fx.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MyAppTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        MenuBar menuBar = new MenuBar(new Menu("TEST"));
        menuBar.setUseSystemMenuBar(true);
        Scene scene = new Scene(new VBox(menuBar, new StackPane(l)), 640, 480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void runApp(String[] args) {
        launch(args);
    }
    
    public static void main(String[] args) {
    	MyAppTest.runApp(args);
	}
}