package net.tenie.fx.utility.EventAndListener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ConnectionEditor;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.MainTabs;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class CommonListener {

	// 文本框只能输入ip字符串
	public static ChangeListener<String> textFieldIp(TextField rows) {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				String str = StrUtils.clearIpStr(newValue);
				rows.setText(str);
			}
		};
	}

	// 文本框只能输入数字
	public static ChangeListener<String> textFieldNumChange(TextField rows) {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				String str = StrUtils.clearString(newValue);
				if (str != null && str.length() > 0) {
					ConfigVal.MaxRows = Integer.valueOf(str);
				} else {
					ConfigVal.MaxRows = 0;
				}
				rows.setText(str);
			}
		};
	}

	// 文本框限制长度
	public static ChangeListener<Number> textFieldLimit(TextField textField, int LIMIT) {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue.intValue() > oldValue.intValue()) {
					// Check if the new character is greater than LIMIT
					if (textField.getText().length() >= LIMIT) {
						textField.setText(textField.getText().substring(0, LIMIT));
					}
				}
			}
		};
	}

// ChoiceBox change 
	public static ChangeListener choiceBoxChange() {
		return new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				// 给代码页面 设置 对应的连接名称, 切换代码页的时候可以自动转换链接
				MainTabs.getActTab();
				MainTabs.setBoxIdx(MainTabs.getActTab().getText(), newValue.toString());
			}
		};
	}

	// 选择框选择连接时, 如果未连接, 进行连接
	public static ChangeListener<Label> choiceBoxChange2() {
		return new ChangeListener<Label>() {
			@Override
			public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
				if (newValue != null) {
					DbConnectionPo cnnpo = DBConns.get(newValue.getText());
					if (cnnpo != null && !cnnpo.isAlive() && !cnnpo.isConnIng()) {
						ConnectionEditor.openConn(cnnpo.getConnName());
					}
				}

			}
		};
	}
	
	
	

}
