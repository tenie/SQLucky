package net.tenie.fx.utility;

import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class CommonUtility {
	public static void runThread(Function<Object, Object> fun) {
		Thread t = new Thread() {
			public void run() {
				fun.apply(null);
			}
		};
		t.start();
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
		if (type == java.sql.Types.BIGINT || type == java.sql.Types.BIT || type == java.sql.Types.DECIMAL
				|| type == java.sql.Types.DOUBLE || type == java.sql.Types.FLOAT || type == java.sql.Types.NUMERIC
				|| type == java.sql.Types.REAL || type == java.sql.Types.TINYINT || type == java.sql.Types.SMALLINT
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

	// 数据单元格添加监听
	// 字段修改事件
	public static void addStringPropertyChangeListener(StringProperty val, int rowNo, String tabId, int idx,
			ObservableList<StringProperty> vals, int dbtype) {
		ChangeListener<String> cl = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				System.out
						.println("addStringPropertyChangeListener ：newValue：" + newValue + " | oldValue =" + oldValue);
				System.out.println("key ==" + tabId + "-" + rowNo);
				System.out.println("observable = " + observable);
				if (CommonUtility.isNum(dbtype) && !StrUtils.isNumeric(newValue) && !"<null>".equals(newValue)) {
					Platform.runLater(() -> val.setValue(oldValue));
					return;
				}
				ComponentGetter.dataFlowSaveBtn().setDisable(false);

				ObservableList<StringProperty> oldDate = FXCollections.observableArrayList();
				if (!CacheTableDate.exist(tabId, rowNo)) {
					for (int i = 0; i < vals.size(); i++) {
						if (i == idx) {
							oldDate.add(new SimpleStringProperty(oldValue));
						} else {
							oldDate.add(new SimpleStringProperty(vals.get(i).get()));
						}
					}
					CacheTableDate.addData(tabId, rowNo, vals, oldDate); // 数据修改缓存, 用于之后更新
				} else {
					CacheTableDate.addData(tabId, rowNo, vals);
				}
			}
		};
		val.addListener(cl);
	}

	public static void newStringPropertyChangeListener(StringProperty val, int dbtype) {
		ChangeListener<String> cl = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (CommonUtility.isNum(dbtype) && !StrUtils.isNumeric(newValue) && !"<null>".equals(newValue)) {
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

}
