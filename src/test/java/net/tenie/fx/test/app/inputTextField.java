package net.tenie.fx.test.app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class inputTextField extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        HBox root = new HBox();

        EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {

            private boolean willConsume = true;

            @Override
            public void handle(KeyEvent event) {


                if (event.getCode()  == KeyCode.SLASH) {
                    if (event.getEventType() == KeyEvent.KEY_PRESSED) {
                        willConsume = true;
                    } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
                        willConsume = false;
                    }
                }

                if (willConsume) {
                    event.consume();
                }
            }

        };

        TextField textField = new TextField();
        textField.addEventFilter(KeyEvent.ANY, handler);

        // logging
        textField.addEventFilter(KeyEvent.ANY, e -> System.out.println(e));

        root.getChildren().addAll(textField);

        Scene scene = new Scene(root, 300, 100);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

}