package net.tenie.fx.Action;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * 自定义事件
 * @author tenie
 *
 */
public class myEvent {
    public  static MouseEvent  mouseEvent(EventType<? extends MouseEvent> eventType , Node node){
        MouseEvent me = new MouseEvent(eventType, node.getLayoutX(), node.getLayoutY(), node.getLayoutX(), node.getLayoutY(), MouseButton.PRIMARY, 1,
                true, true, true, true, true, true, true, true, true, true, null);
        return me;
    }
    
    
    public static void btnClick(Button btn) {
    	MouseEvent moEv = myEvent.mouseEvent(MouseEvent.MOUSE_CLICKED, btn);
		Event.fireEvent(btn, moEv);
    }
}
