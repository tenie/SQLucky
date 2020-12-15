package net.tenie.fx.utility;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestMain extends Application {
    private CssToColorHelper helper = new CssToColorHelper();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
      Scene scene = new Scene(new Group(), 450, 250);

      scene.getStylesheets().add("colors.css");

      TextField textfield = new TextField();      

      Group root = (Group) scene.getRoot();
      root.getChildren().add(textfield);
      root.getChildren().add(helper);
      stage.setScene(scene);       

      Color blue = getNamedColor("my-blue");

      Background bgf = new Background(new BackgroundFill(blue, null, null));
      textfield.setBackground(bgf);  

      stage.show();        
    }  

    Color getNamedColor(String name) {
      helper.setStyle("-named-color: " + name + ";");
      helper.applyCss();

      return helper.getNamedColor();      
    }
}
