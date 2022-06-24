package net.tenie.Sqlucky.sdk.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.myEvent;

/**
 * 查找窗口
 * @author tenie
 *
 */
public class FindReplaceEditor {
	private static String f3Str = "";
	private static Logger logger = LogManager.getLogger(FindReplaceEditor.class);
	
	// 查询字符串输入框
	public	static TextField textField;
	private JFXButton down ;
	private JFXButton up;
	
	
	public static void findStrReplaceStr(TextField findtf, TextField tf, boolean sensitive) {
		CodeArea code = SqlcukyEditor.getCodeArea();
		int idx = code.getCaretPosition();
		String selTex = code.getSelectedText();
		String findStr = findtf.getText();
		if (StrUtils.isNotNullOrEmpty(selTex) && selTex.equals(findStr)) {
			idx = code.getSelection().getStart();
		}
		replaceString(findtf.getText(), tf.getText(), idx, sensitive, true);
	}

	public static void replaceString(String str, String strNew, int fromIndex, boolean sensitive, boolean forward) {
		CodeArea code = SqlcukyEditor.getCodeArea();
		String text = code.getText();
		if (sensitive) {
			str = str.toUpperCase();
			text = text.toUpperCase();
		}
		int start = -1;
		int length = str.length();
		if (text.indexOf(str) > -1) {
			if (forward) {
				start = text.indexOf(str, fromIndex);
				if (start < 0) {
					start = text.indexOf(str);
				}
			} else {
				start = text.lastIndexOf(str, (fromIndex - 1));
				int tempIdx = fromIndex - str.length();
				if (tempIdx == start) {
					start = text.lastIndexOf(str, tempIdx - 1);
				}
				if (start < 0) {
					start = text.lastIndexOf(str);
				}
			}
			if (start > -1) {
				// 开始替换
				// 将原文本删除
				code.deleteText(start, start + length);
				// 插入 注释过的文本
				code.insertText(start, strNew);
				// 选中
				selectRange(code, start, start + strNew.length());
			}
		}
	}

	// F3 字符串查找
	public static void findSelectedString() {
		CodeArea code = SqlcukyEditor.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text)) {
			if(StrUtils.isNotNullOrEmpty(f3Str)) {
				text = f3Str;
			}
		}else {
			f3Str = text;
		}
			
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();

		int idx = start + text.length();
		logger.info(idx);
		findString(text, idx, true, true);
	}

	 
	/**
	 * 字符串查找
	 * @param str 要查找的字符串
	 * @param forward  是否向前找
	 * @param sensitive 是否大小写敏感
	 */
	public static void findStringFromCodeArea(String str, boolean forward, boolean sensitive) {
		if (StrUtils.isNullOrEmpty(str))
			return;
		CodeArea code = SqlcukyEditor.getCodeArea();
		int idx = code.getCaretPosition();  // 光标位置
		findString(str, idx, sensitive, forward); 
	}

	public static String codeStr(boolean sensitive) {
		CodeArea code = SqlcukyEditor.getCodeArea();
		String text = code.getText();
		if (sensitive) {
			text = text.toUpperCase();
		}
		return text;
	}

	public static void findStrReplaceStrAll(TextField findtf, TextField tf, boolean sensitive) {
		replaceStringAll(findtf.getText(), tf.getText(), sensitive);
	}

	public static void replaceStringAll(String str, String strNew, boolean sensitive) {
		CodeArea code = SqlcukyEditor.getCodeArea();
		String text = code.getText();
		if (sensitive) {
			str = str.toUpperCase();
			text = text.toUpperCase();
		}
		int start = -1;
		int length = str.length();
		while (text.indexOf(str) > -1) {
			start = text.indexOf(str);
			if (start > -1) {
				// 开始替换
				// 将原文本删除
				code.deleteText(start, start + length);
				// 插入 注释过的文本
				code.insertText(start, strNew);
			}
			text = codeStr(sensitive);

		}
		// 选中
		selectRange(code, start, start + strNew.length());

	}

	static void selectRange(CodeArea code, int anchor, int caretPosition) {
		code.selectRange(anchor, caretPosition);
		code.requestFollowCaret();
	}

	/**
	 * 开始查找字符串
	 * @param str 要查找的字符串	
	 * @param fromIndex 从文本的哪个位置开始找
	 * @param sensitive 是否大小写敏感
	 * @param forward 向前还是向后找
	 */
	public static void findString(String str, int fromIndex, boolean sensitive, boolean forward) {
		CodeArea code = SqlcukyEditor.getCodeArea();
		// 获取文本
		String text = code.getText();
		if (sensitive) {
			str = str.toUpperCase();
			text = text.toUpperCase();
		}
		int start = -1;
		int length = str.length();
		if (text.indexOf(str) > -1) {
			if (forward) {
				start = text.indexOf(str, fromIndex);
				if (start < 0) {
					start = text.indexOf(str);
				}
			} else {
				start = text.lastIndexOf(str, (fromIndex - 1));
				int tempIdx = fromIndex - str.length();
				if (tempIdx == start) {
					start = text.lastIndexOf(str, tempIdx - 1);
				}
				if (start < 0) {
					start = text.lastIndexOf(str);
				}
			}
			if (start > -1) {
				selectRange(code, start, start + length);
			}
		} 
		SqlcukyEditor.currentSqlCodeAreaHighLighting(str);
	}

	public static void delFindReplacePane(SqluckyTab skTab) {
		VBox x = SqlcukyEditor.getTabVbox();
		while (x.getChildren().size() > 1) {
			x.getChildren().remove(0);
			skTab.cleanFindReplacePanel();
		}
	}

	public static AnchorPane createReplacePane(TextField findtf, JFXCheckBox cb) {
		AnchorPane replaceAnchorPane = new AnchorPane();
		CommonUtility.addCssClass(replaceAnchorPane, "myFindPane");
		replaceAnchorPane.prefHeight(30);
		JFXButton query = new JFXButton();
		query.setGraphic(IconGenerator.svgImageDefActive("refresh"));
		TextField tf = new TextField();
		tf.setPrefWidth(250);
		tf.setPrefHeight(15);
		tf.getStyleClass().add("myFindTextField");

		// "arrow-down"
		JFXButton replaceBtn = new JFXButton();
		replaceBtn.getStyleClass().add("myReplaceBtn");
		replaceBtn.setText("Replace");
		replaceBtn.setOnAction(v -> {
			findStrReplaceStr(findtf, tf, !cb.isSelected());
		});

		JFXButton replaceAllBtn = new JFXButton();
		replaceAllBtn.getStyleClass().add("myReplaceBtn");
		replaceAllBtn.setText("Replace All");
		replaceAllBtn.setOnAction(v -> {
			findStrReplaceStrAll(findtf, tf, !cb.isSelected());
		});

		int x = 0;
		query.setLayoutX(x);
		query.setLayoutY(0);
		x += 35;
		tf.setLayoutX(x);
		tf.setLayoutY(0);
		x += 255;
		replaceBtn.setLayoutX(x);
		replaceBtn.setLayoutY(0);
		x += 70;
		replaceAllBtn.setLayoutX(x);
		replaceAllBtn.setLayoutY(0);

		replaceAnchorPane.getChildren().add(query);
		replaceAnchorPane.getChildren().add(tf);
		replaceAnchorPane.getChildren().add(replaceBtn);
		replaceAnchorPane.getChildren().add(replaceAllBtn);
		return replaceAnchorPane;
	}
//  public static  void createFindPane(boolean isReplace, String findText, VBox b ) {
	public  FindReplaceEditor(boolean isReplace, String findText, SqluckyTab skTab ) {
		AnchorPane findAnchorPane = new AnchorPane();
		CommonUtility.addCssClass(findAnchorPane, "myFindPane");
		findAnchorPane.prefHeight(30);
		JFXButton query = new JFXButton();
		JFXCheckBox cb = new JFXCheckBox("Sensitive");  
		query.setGraphic(IconGenerator.svgImageDefActive("search"));

	    textField = new TextField();
		query.setOnAction(v -> {
			findStringFromCodeArea(textField.getText(), true, !cb.isSelected());
		});
		
		if(StrUtils.isNullOrEmpty(findText)) {
			// 从编辑框中获取选中的文本
			CodeArea code = SqlcukyEditor.getCodeArea();
			findText = code.getSelectedText();
		}
		
		textField.setText(findText);
		textField.setPrefWidth(250);
		textField.setPrefHeight(15);
		textField.getStyleClass().add("myFindTextField");
		textField.textProperty().addListener((o, oldVal, newVal) -> {
			findStringFromCodeArea(newVal, true, !cb.isSelected());
		});
		// 回车键出发查找下一个
		textField.setOnKeyPressed(val->{
			 if(val.getCode() == KeyCode.ENTER ){ 
				 myEvent.btnClick(down);
			 }
		});
		// "arrow-down"
		down = new JFXButton();
		down.setGraphic(IconGenerator.svgImageDefActive("arrow-down"));
		down.setOnAction(v -> {
			findStringFromCodeArea(textField.getText(), true, !cb.isSelected());
		});
		
		down.setOnMouseClicked(v -> {
			findStringFromCodeArea(textField.getText(), true, !cb.isSelected());
		});
		
		

		up = new JFXButton();
		up.setGraphic(IconGenerator.svgImageDefActive("arrow-up"));
		up.setOnAction(v -> {
			findStringFromCodeArea(textField.getText(), false, !cb.isSelected());
		});
		up.setOnMouseClicked(v -> {
			findStringFromCodeArea(textField.getText(), false, !cb.isSelected());
		});
		
		// 计算查询字符串出现次数
		JFXButton count = new JFXButton("Count");
		count.setGraphic(IconGenerator.svgImageDefActive("calculator")); 
		Label countLabel = new Label(""); 
		count.setOnAction(e->{
			String sqlTxt = SqlcukyEditor.getCurrentCodeAreaSQLText();
			int countVal = 0;
			if(textField.getText().length() == 0) {
				countLabel.setText("");
			}else {
				if(cb.isSelected()) {
					countVal = StrUtils.countSubString(sqlTxt, textField.getText());
				}else {
					countVal = StrUtils.countSubString(sqlTxt.toLowerCase() , textField.getText().toLowerCase());
				}
				countLabel.setText(countVal + "");
			}			
		});

		int x = 0;
		query.setLayoutX(x);
		query.setLayoutY(0);
		x += 35;
		textField.setLayoutX(x);
		textField.setLayoutY(0);
		x += 250;
		down.setLayoutX(x);
		down.setLayoutY(0);
		x += 35;
		up.setLayoutX(x);
		up.setLayoutY(0);

		x += 35;
		cb.setLayoutX(x);
		cb.setLayoutY(3);
		
		x += 90;
		count.setLayoutX(x);
		count.setLayoutY(0);
		x += 80;
		countLabel.setLayoutX(x);
		countLabel.setLayoutY(5);
		

		findAnchorPane.getChildren().add(query);
		findAnchorPane.getChildren().add(textField);
		findAnchorPane.getChildren().add(down);
		findAnchorPane.getChildren().add(up);
		findAnchorPane.getChildren().add(cb);
		findAnchorPane.getChildren().add(count);
		findAnchorPane.getChildren().add(countLabel);

		JFXButton hideBottom = new JFXButton();
		hideBottom.setGraphic(IconGenerator.svgImageDefActive("window-close"));// fontImgName("caret-square-o-down",
																					// 16, Color.ROYALBLUE));
		findAnchorPane.getChildren().add(hideBottom);
		AnchorPane.setRightAnchor(hideBottom, 0.0);
		
//		VBox b = SqlcukyEditor.getTabVbox();
		hideBottom.setOnAction(v -> {
			delFindReplacePane(skTab);
		});
		// 加入到 代码编辑框上面
		VBox b =  skTab.getVbox();
		b.getChildren().add(0, findAnchorPane);
		if (isReplace) {
			b.getChildren().add(1, createReplacePane(textField, cb));
		}
		Platform.runLater(() -> {
			textField.requestFocus();
		});
//		return findAnchorPane;
	}
}
