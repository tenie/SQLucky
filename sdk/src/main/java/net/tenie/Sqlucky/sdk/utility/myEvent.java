package net.tenie.Sqlucky.sdk.utility;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * 自定义事件, 用于触发如鼠标点击的事件函数
 * 如果要触发 setOnAction() 函数设置的回调函数 可以直接调用 button的.fire()函数
 * @author tenie
 *
 */
public class myEvent {
    public  static MouseEvent  mouseEvent(EventType<? extends MouseEvent> eventType , Node node){
        MouseEvent me = new MouseEvent(eventType, node.getLayoutX(), node.getLayoutY(), node.getLayoutX(), node.getLayoutY(), MouseButton.PRIMARY, 1,
                true, true, true, true, true, true, true, true, true, true, null);
        return me;
    }
    
    
    
    /**
     * 触发button的点击事件 (button的setOnMouseClicked()函数设置的回调函数)
     * 如果要触发 setOnAction() 函数设置的回调函数 可以直接调用 button的.fire()函数
     * @param btn
     */
    public static void btnClick(Button btn) {
    	MouseEvent moEv = myEvent.mouseEvent(MouseEvent.MOUSE_CLICKED, btn);
		Event.fireEvent(btn, moEv);
    }
}
