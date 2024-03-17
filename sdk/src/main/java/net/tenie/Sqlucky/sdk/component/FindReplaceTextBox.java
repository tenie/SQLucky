package net.tenie.Sqlucky.sdk.component;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.myEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;

import java.util.function.Consumer;

/**
 * 查找窗口
 * 
 * @author tenie
 *
 */
public class FindReplaceTextBox {
	private static String f3Str = "";
	private static Logger logger = LogManager.getLogger(FindReplaceTextBox.class);

	// 查询字符串输入框
	public static TextField textField;
	private JFXButton down;
	private JFXButton up;

	private JFXCheckBox sensitiveCheckBox ;
	private JFXButton countBtn ;
	private Label countLabel;
	// 替换字符串一次
	public static boolean findStrReplaceStr(TextField findtf, TextField tf, boolean sensitive) {
		CodeArea code = MyEditorSheetHelper.getCodeArea();
		int idx = code.getCaretPosition();
		String selTex = code.getSelectedText();
		String findStr = findtf.getText();
		if (StrUtils.isNotNullOrEmpty(selTex) && selTex.equals(findStr)) {
			idx = code.getSelection().getStart();
		}
		return replaceString(findtf.getText(), tf.getText(), idx, sensitive, true);
	}

	public static boolean replaceString(String str, String strNew, int fromIndex, boolean sensitive, boolean forward) {
		// 替换成功与否
		boolean tf = false;
		CodeArea code = MyEditorSheetHelper.getCodeArea();
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
				tf = true;
			}
		}
		return tf;
	}

	// F3 字符串查找
	public static void findSelectedString() {
		CodeArea code = MyEditorSheetHelper.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text)) {
			if (StrUtils.isNotNullOrEmpty(f3Str)) {
				text = f3Str;
			}
		} else {
			f3Str = text;
		}

		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();

		int idx = start + text.length();
//		logger.info(idx);
		findString(text, idx, true, true);
	}

	/**
	 * 字符串查找
	 *
	 * @param str       要查找的字符串
	 * @param forward   是否向前找
	 * @param sensitive 是否大小写敏感
	 */
	public static void findStringFromCodeArea(String str, boolean forward, boolean sensitive) {
		if (StrUtils.isNullOrEmpty(str))
			return;
		CodeArea code = MyEditorSheetHelper.getCodeArea();
		int idx = code.getCaretPosition(); // 光标位置
		findString(str, idx, sensitive, forward);
	}

	/**
	 * 找不到就停止查找
	 *
	 * @param str
	 * @param forward
	 * @param sensitive
	 * @return 返回false, 表示找不到
	 */
	public static boolean findStringStopFromCodeArea(String str, Integer position, boolean forward, boolean sensitive) {
		if (StrUtils.isNullOrEmpty(str))
			return false;

		Integer idx = 0;
		CodeArea code = MyEditorSheetHelper.getCodeArea();
		if (position == null) {
			idx = code.getCaretPosition(); // 光标位置
		} else if (position > -1) {
			idx = position;
		} else if (position < 0) { // 从后往前找
			int areaLength = code.getLength();
			idx = areaLength;
		}

		return findStringStop(str, idx, sensitive, forward);
	}

	static void selectRange(CodeArea code, int anchor, int caretPosition) {
		code.selectRange(anchor, caretPosition);
		code.requestFollowCaret();
	}

	/**
	 * 开始查找字符串
	 *
	 * @param str       要查找的字符串
	 * @param fromIndex 从文本的哪个位置开始找
	 * @param sensitive 是否大小写敏感
	 * @param forward   向前还是向后找
	 */
	public static void findString(String str, int fromIndex, boolean sensitive, boolean forward) {
		CodeArea code = MyEditorSheetHelper.getCodeArea();
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
		MyEditorSheetHelper.currentSqlCodeAreaHighLighting(str);
	}

	// 不循环找, 找不到下一个就不循环
	public static boolean findStringStop(String str, int fromIndex, boolean sensitive, boolean forward) {
		CodeArea code = MyEditorSheetHelper.getCodeArea();
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
			} else {
				start = text.lastIndexOf(str, (fromIndex - 1));
				int tempIdx = fromIndex - str.length();
				if (tempIdx == start) {
					start = text.lastIndexOf(str, tempIdx - 1);
				}
			}
			if (start > -1) {
				selectRange(code, start, start + length);
			}
		}
		if (start > -1) {
			MyEditorSheetHelper.currentSqlCodeAreaHighLighting(str);
			return true;
		}
		return false;

	}



	// 查找的计数action
	private void countAction() {

		String sqlTxt = MyEditorSheetHelper.getCurrentCodeAreaSQLText();
		int countVal = 0;
		if (textField.getText().length() == 0) {
			countLabel.setText("");
		} else {
			if (sensitiveCheckBox.isSelected()) {
				countVal = StrUtils.countSubString(sqlTxt, textField.getText());
			} else {
				countVal = StrUtils.countSubString(sqlTxt.toLowerCase(), textField.getText().toLowerCase());
			}
			countLabel.setText(countVal + "");
		}

	}


	VBox findReplaceBox = new VBox();
	HBox replaceBox ;
	public VBox getfindReplaceBox(){
		return findReplaceBox;
	}
//	public static VBox createFindReplaceTextBox(boolean isReplace, String findText, Consumer<VBox> hiddenBox){
//		FindReplaceTextBox box = new FindReplaceTextBox(isReplace, findText, hiddenBox);
//		return box.getfindReplaceBox();
//	}
	// 查找替换组件的面板
	public FindReplaceTextBox(boolean isReplace, String findText, Consumer<VBox> hiddenBox) { //, MyEditorSheet sheet
		AnchorPane findAnchorPane = new AnchorPane();
		VBox.setMargin(findReplaceBox, new Insets(3, 1, 3, 3));
		findReplaceBox.setSpacing(3);
		findReplaceBox.getChildren().add(findAnchorPane);
		CommonUtils.addCssClass(findAnchorPane, "myFindPane");
		JFXButton query = new JFXButton();
		sensitiveCheckBox = new JFXCheckBox("Sensitive");
		query.setGraphic(IconGenerator.svgImageDefActive("search"));

		textField = new TextField();
		query.setOnAction(v -> {
			findStringFromCodeArea(textField.getText(), true, !sensitiveCheckBox.isSelected());
		});

		if (StrUtils.isNullOrEmpty(findText)) {
			// 从编辑框中获取选中的文本
			CodeArea code = MyEditorSheetHelper.getCodeArea();
			findText = code.getSelectedText();
		}

		textField.setText(findText);
		textField.getStyleClass().add("myFindTextField");
		// 回车键出发查找下一个
		textField.setOnKeyPressed(val -> {
			if (val.getCode() == KeyCode.ENTER) {
				myEvent.btnClick(down);
			}
		});

		// "arrow-down"
		down = new JFXButton();
		down.setGraphic(IconGenerator.svgImageDefActive("arrow-down"));

		down.setOnMouseClicked(v -> {
			findStringFromCodeArea(textField.getText(), true, !sensitiveCheckBox.isSelected());
		});

		up = new JFXButton();
		up.setGraphic(IconGenerator.svgImageDefActive("arrow-up"));
		up.setOnMouseClicked(v -> {
			findStringFromCodeArea(textField.getText(), false, !sensitiveCheckBox.isSelected());
		});

		// 计算查询字符串出现次数 	JFXCheckBox cb = new JFXCheckBox("Sensitive");
		countBtn = new JFXButton("Count");
		countBtn.setGraphic(IconGenerator.svgImageDefActive("calculator"));
		countLabel = new Label("");
		countBtn.setOnAction(e -> {
			countAction();
		});
		textField.textProperty().addListener((obj, vOld, vNew) -> {
			if (StrUtils.isNotNullOrEmpty(vNew)) {
				countAction();
			} else {
				countLabel.setText("");
			}
		});

		AnchorPane textFieldPane = UiTools.textFieldAddCleanBtn(textField, 250.0);

		HBox findBox = new HBox();
		findBox.getChildren().addAll(query,textFieldPane,down, up, sensitiveCheckBox, countBtn, countLabel);
		HBox.setMargin(countLabel, new Insets(5, 3, 0, 3));
		HBox.setMargin(sensitiveCheckBox, new Insets(5, 0, 0, 3));
		findAnchorPane.getChildren().add(findBox);


		JFXButton hideBottom = new JFXButton();
		hideBottom.setGraphic(IconGenerator.svgImageDefActive("window-close"));
		findAnchorPane.getChildren().add(hideBottom);
		AnchorPane.setRightAnchor(hideBottom, 0.0);


		hideBottom.setOnAction(v -> {
//			sheet.delFindReplacePane();
//			findReplaceBox.setVisible(false);
			hiddenBox.accept(findReplaceBox);
		});
		// 加入到 代码编辑框上面
//		sheet.setFindAnchorPane(findAnchorPane);
		if (isReplace) {
			createReplacePane(textField, sensitiveCheckBox);

			findReplaceBox.getChildren().add(replaceBox);
		}
		Platform.runLater(() -> {
			textField.requestFocus();
			countAction();
		});
	}

	// 显示替换box
	public void showHiddenReplaceBox(boolean tf){
		if(tf){
			// 判断是为空， 空就创建
			if(replaceBox == null){
				createReplacePane(textField, sensitiveCheckBox);
			}
			// 判断是否已经显示， 不是就显示
			if( ! findReplaceBox.getChildren().contains(replaceBox)){
				findReplaceBox.getChildren().add(replaceBox);
			}

		}else { //隐藏
			if(  findReplaceBox.getChildren().contains(replaceBox)){
				findReplaceBox.getChildren().remove(replaceBox);
			}

		}
	}

	public void setText(String findText){
		textField.setText(findText);
	}

	public void createReplacePane(TextField findtf, JFXCheckBox cb) {
		replaceBox  = new HBox();
		CommonUtils.addCssClass(replaceBox, "myFindPane");
		replaceBox.prefHeight(30);
		JFXButton query = new JFXButton();
		query.setGraphic(IconGenerator.svgImageDefActive("refresh"));
		TextField replaceTextField = new TextField();
		replaceTextField.setPrefWidth(250);
		replaceTextField.setPrefHeight(15);
		replaceTextField.getStyleClass().add("myFindTextField");

		// "arrow-down"
		JFXButton replaceBtn = new JFXButton();
		replaceBtn.getStyleClass().add("myAlertBtn");
		replaceBtn.setText("Replace");
		replaceBtn.setOnAction(v -> {
			findStrReplaceStr(findtf, replaceTextField, !cb.isSelected());
			countAction();
		});

		JFXButton replaceAllBtn = new JFXButton();
		replaceAllBtn.getStyleClass().add("myAlertBtn");
		replaceAllBtn.setText("Replace All");
		replaceAllBtn.setOnAction(v -> {
			boolean tf = findStrReplaceStr(findtf, replaceTextField, !cb.isSelected());
			while(tf) {
				tf = findStrReplaceStr(findtf, replaceTextField, !cb.isSelected());
			}
			countAction();
		});

		AnchorPane replaceFieldPane = UiTools.textFieldAddCleanBtn(replaceTextField, 250.0);
		replaceBox.getChildren().add(query);
		replaceBox.getChildren().add(replaceFieldPane);

		replaceBox.getChildren().add(replaceBtn);
		replaceBox.getChildren().add(replaceAllBtn);
		HBox.setMargin(replaceBtn, new Insets(0, 0, 0, 3));
		HBox.setMargin(replaceAllBtn, new Insets(0, 0, 0, 3));
	}

}
