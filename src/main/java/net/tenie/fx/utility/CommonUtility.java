package net.tenie.fx.utility;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.util.Duration;
import net.tenie.lib.tools.StrUtils;

/**
 * 
 * @author tenie
 *
 */
public class CommonUtility {
	private static Logger logger = LogManager.getLogger(CommonUtility.class);
	
	public static void runThread(Function<Object, Object> fun) {
		Thread t = new Thread() {
			public void run() {
				fun.apply(null);
			}
		};
		t.start();
	}
	
	public static void runThread(Consumer< String >  caller) {
		Thread t = new Thread() {
			public void run() {
				caller.accept("");
			}
		};
		t.start();
	}
	
	// 检测文件是否存在
	public static boolean checkFileExist(String fileName) {
		File file = new File(fileName); 
		if (file.exists() && file.isFile()) {
			return true;
		}
		return false;
	}
	
	// 获取Tab 中的文本
	public static String tabText(Tab tb) {
		String title = tb.getText();
		if (StrUtils.isNullOrEmpty(title)) {
			Label lb = (Label) tb.getGraphic();
			if (lb != null)
				title = lb.getText();
			else
				title = "";
		}
		return title;
	}

	public static void setTabName(Tab tb, String val) {
		Label lb = (Label) tb.getGraphic();
		if (lb != null) {
			lb.setText(val);
			tb.setText("");
		} else {
			tb.setText(val);
		}
	}

	// 判断数据库字段是否是数字类型
	public static boolean isNum(int type) {
		if (type == java.sql.Types.BIGINT 
				|| type == java.sql.Types.BIT 
				|| type == java.sql.Types.DECIMAL
				|| type == java.sql.Types.DOUBLE 
				|| type == java.sql.Types.FLOAT
				|| type == java.sql.Types.NUMERIC
				|| type == java.sql.Types.REAL 
				|| type == java.sql.Types.TINYINT
				|| type == java.sql.Types.SMALLINT
				|| type == java.sql.Types.INTEGER) {
			return true;
		}
		return false;
	}

	// 时间类型判断
	public static boolean isDateTime(int type) {
		if (type == java.sql.Types.DATE || type == java.sql.Types.TIME || type == java.sql.Types.TIMESTAMP) {
			return true;
		}
		return false;
	}

	// 字符串类型判断
	public static boolean isString(int type) {
		if (type == java.sql.Types.CHAR || type == java.sql.Types.VARCHAR || type == java.sql.Types.LONGVARCHAR) {
			return true;
		}
		return false;
	}

	 

	public static void newStringPropertyChangeListener(StringProperty val, int dbtype) {
		ChangeListener<String> cl = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (CommonUtility.isNum(dbtype) 
				&& !StrUtils.isNumeric(newValue) 
				&& !"<null>".equals(newValue)) {
					logger.info("newStringPropertyChangeListener() : newValue= "+newValue+"set fail" );
					Platform.runLater(() -> val.setValue(oldValue));
					return;
				}
			}
		};
		val.addListener(cl);
	}

	public static void prohibitChangeListener(StringProperty val, String original) {
		ChangeListener<String> cl = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (original == null) {
					if (newValue != null) {
						Platform.runLater(() -> val.setValue(original));
					}
				} else {
					if (newValue != null && !newValue.equals(original)) {
						Platform.runLater(() -> val.setValue(original));
					}
				}

			}
		};

	}

	// 剪贴板 赋值
	public static void setClipboardVal(String val) {
		Platform.runLater(() -> {
			javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
			javafx.scene.input.ClipboardContent clipboardContent = new javafx.scene.input.ClipboardContent();
			clipboardContent.putString(val);
			clipboard.setContent(clipboardContent);
		});

	}
	
	// 获取剪贴板的值
	public static String getClipboardVal() {
		return  javafx.scene.input.Clipboard.getSystemClipboard().getString();
	}
	
	/**
	 *  等待正在执行的Platform队列执行完毕 
	 *  在 Platform.runLater中放入一个计数器, 等执行到这个计数器完成说明在它前面的任务已经完成了
	 */
	public static void platformAwait(){
		CountDownLatch countDownLatch = new CountDownLatch(1);
	    Platform.runLater(countDownLatch::countDown);
	    try {
			countDownLatch.await();
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
	}

	public static int getMax(int[] arr) {
		int max = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
			}
		}
		System.out.println(max);
		return max;
	}
	
	public static int getMin(int[] arr) {
		int min = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < min) {
				min = arr[i];
			}
		}
		System.out.println(min);
		return min;
	}
	/**
	 * node 进行旋转的动画, 设置旋转100次
	 * @param pointer
	 */
	public static void rotateTransition(Node pointer) {
		// 播放持续时间
		double play_time = 2.0;
		// 开始角度
		double fromAngle = 0.0;
		// 结束角度
		double toAngle = 720.0;
		// 根据旋转角度大小计算动画播放持续时间
//        play_time =Math.abs(toAngle-fromAngle)*0.05;
		// Duration.seconds(3)设置动画持续时间
		RotateTransition rotateTransition = new RotateTransition(Duration.seconds(play_time), pointer);
		// 设置旋转角度
		rotateTransition.setFromAngle(fromAngle);
		rotateTransition.setToAngle(toAngle);
		// 只播放次数
		rotateTransition.setCycleCount(100);
		// 每次旋转后是否改变旋转方向
		rotateTransition.setAutoReverse(false);
		rotateTransition.play();
	}
	
	public static  boolean isMacOS() {
		String os_name = System.getProperty("os.name");
		if (os_name.toLowerCase().startsWith("mac")) {
			return true;
		}
		return false;
	}

	public static   boolean isWinOS() {
		String os_name = System.getProperty("os.name");
		if (os_name.toLowerCase().startsWith("win")) {
			return true;
		}
		return false;
	}
	
	public static   boolean isLinuxOS() {
		String os_name = System.getProperty("os.name");
		if (os_name.toLowerCase().startsWith("linux")) {
			return true;
		}
		return false;
	}

}
