package net.tenie.fx.component;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class MyTooltipTool {
	
	public static Tooltip instance(String msg){
		Tooltip tt = new Tooltip(msg); 
//		setTipTime(tt, 100000); 
		return tt;
	}
	
//	  public static void setTipTime(Tooltip tooltip,int time){
//	        try {
//	            Class tipClass = tooltip.getClass();
//	            Field f = tipClass.getDeclaredField("BEHAVIOR");
//	            f.setAccessible(true);
//	            Class behavior = Class.forName("javafx.scene.control.Tooltip$TooltipBehavior");
//	            Constructor constructor = behavior.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
//	            constructor.setAccessible(true);
//	            f.set(behavior, constructor.newInstance(new Duration(100),  // 鼠标进入的时间
//	            										new Duration(time), // 从出现时到消失的时间
//	            										new Duration(200), // 鼠标离开后消失的时间
//	            										false));
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	    }
}
