package net.tenie.fx.Action;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.component.SqlcukyEditor;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.MainTabs;
import net.tenie.fx.window.ConnectionEditor;


/**
 * 
 * @author tenie
 *
 */
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
	@SuppressWarnings("rawtypes")
	public static ChangeListener choiceBoxChange() {
		return new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				System.out.println("choiceBoxChange");
				// 给代码页面 设置 对应的连接名称, 切换代码页的时候可以自动转换链接
				MainTabs.setBoxIdx( CommonUtility.tabText(  MainTabs.getActTab()), newValue.toString());
			}
		};
	}

	// 选择框选择连接时, 如果未连接, 进行连接
	public static ChangeListener<Label> choiceBoxChange2() {
		return new ChangeListener<Label>() {
			@Override
			public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
				System.out.println("choiceBoxChange2");
				if (newValue != null) {
					SqluckyConnector cnnpo = DBConns.get(newValue.getText());
					if (cnnpo != null && !cnnpo.isAlive() && !cnnpo.isConnIng()) {
						//清除查找字符串
						IndexRange ir = SqlcukyEditor.getSelection();
						CommonAction.pressBtnESC();	
						CommonAction.shrinkTreeView();
						ConnectionEditor.openConn(cnnpo.getConnName());
						SqlcukyEditor.selectRange(ir);
					}
				}
			    

			}
		};
	}
	
	
	

}
