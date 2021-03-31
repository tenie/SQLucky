package net.tenie.fx.Action;

import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class myEvent {
    public  static MouseEvent  mouseEvent(EventType<? extends MouseEvent> eventType , Node node){
        MouseEvent me = new MouseEvent(eventType, node.getLayoutX(), node.getLayoutY(), node.getLayoutX(), node.getLayoutY(), MouseButton.PRIMARY, 1,
                true, true, true, true, true, true, true, true, true, true, null);
        return me;
    }
}
