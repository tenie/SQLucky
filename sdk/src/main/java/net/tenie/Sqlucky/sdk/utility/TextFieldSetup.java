package net.tenie.Sqlucky.sdk.utility;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
/**
 * 最大输入的 文本输入框
 * @author tenie
 *
 */
public class TextFieldSetup {
	// 长度设置
	public static void setMaxLength(TextField tf, int maxLength) {
		tf.lengthProperty().addListener(textFieldLimit(tf, maxLength));
	}
	// 限制只能输入数字
	public static void numberOnly(TextField tf) {
		tf.textProperty().addListener(textFieldNumChange(tf));
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
		
		// 文本框只能输入数字
		public static ChangeListener<String> textFieldNumChange(TextField rows) {
			return new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if(StrUtils.isNotNullOrEmpty(newValue)) {
						String str = StrUtils.clearString(newValue);
						if (str != null && str.length() > 0) {
							ConfigVal.MaxRows = Integer.valueOf(str);
						} else {
							ConfigVal.MaxRows = 1;
						}
						rows.setText(str);
					}
				}
			};
		}

}
