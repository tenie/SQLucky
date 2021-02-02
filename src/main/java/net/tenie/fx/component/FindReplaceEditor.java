package net.tenie.fx.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import javafx.application.Platform;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class FindReplaceEditor {
	private static Logger logger = LogManager.getLogger(FindReplaceEditor.class);
	public static void findStrReplaceStr(TextField findtf, TextField tf, boolean sensitive) {
		CodeArea code = SqlEditor.getCodeArea();
		int idx = code.getCaretPosition();
		String selTex = code.getSelectedText();
		String findStr = findtf.getText();
		if (StrUtils.isNotNullOrEmpty(selTex) && selTex.equals(findStr)) {
			idx = code.getSelection().getStart();
		}
		replaceString(findtf.getText(), tf.getText(), idx, sensitive, true);
	}

	public static void replaceString(String str, String strNew, int fromIndex, boolean sensitive, boolean forward) {
		CodeArea code = SqlEditor.getCodeArea();
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
		CodeArea code = SqlEditor.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();

		int idx = start + text.length();
		logger.info(idx);
		findString(text, idx, true, true);
	}

	// 字符串查找
	public static void findStringFromCodeArea(String str, boolean forward, boolean sensitive) {
		if (StrUtils.isNullOrEmpty(str))
			return;
		CodeArea code = SqlEditor.getCodeArea();
		int idx = code.getCaretPosition();
		findString(str, idx, sensitive, forward); 
	}

	public static String codeStr(boolean sensitive) {
		CodeArea code = SqlEditor.getCodeArea();
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
		CodeArea code = SqlEditor.getCodeArea();
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

	// 查找
	public static void findString(String str, int fromIndex, boolean sensitive, boolean forward) {
		CodeArea code = SqlEditor.getCodeArea();
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
		SqlCodeAreaHighLightingHelper.applyFindWordHighlighting(code, str);
	}

	public static void delFindReplacePane() {
		VBox x = SqlEditor.getTabVbox();
		while (x.getChildren().size() > 1) {
			x.getChildren().remove(0);
		}
	}

	public static AnchorPane createReplacePane(TextField findtf, JFXCheckBox cb) {
		AnchorPane replaceAnchorPane = new AnchorPane();
		replaceAnchorPane.prefHeight(30);
		JFXButton query = new JFXButton();
		query.setGraphic(ImageViewGenerator.svgImageUnactive("refresh"));
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

	public static AnchorPane createFindPane(boolean isReplace) {
		AnchorPane findAnchorPane = new AnchorPane();
		findAnchorPane.prefHeight(30);
		JFXButton query = new JFXButton();
		JFXCheckBox cb = new JFXCheckBox("Sensitive");
		query.setGraphic(ImageViewGenerator.svgImageUnactive("search"));

		TextField tf = new TextField();
		query.setOnAction(v -> {
			findStringFromCodeArea(tf.getText(), true, !cb.isSelected());
		});
		CodeArea code = SqlEditor.getCodeArea();
		tf.setText(code.getSelectedText());
		tf.setPrefWidth(250);
		tf.setPrefHeight(15);
		tf.getStyleClass().add("myFindTextField");
		tf.textProperty().addListener((o, oldVal, newVal) -> {
			findStringFromCodeArea(newVal, true, !cb.isSelected());
		});

		// "arrow-down"
		JFXButton down = new JFXButton();
		down.setGraphic(ImageViewGenerator.svgImageUnactive("arrow-down"));
		down.setOnAction(v -> {
			findStringFromCodeArea(tf.getText(), true, !cb.isSelected());
		});

		JFXButton up = new JFXButton();
		up.setGraphic(ImageViewGenerator.svgImageUnactive("arrow-up"));
		up.setOnAction(v -> {
			findStringFromCodeArea(tf.getText(), false, !cb.isSelected());
		});

		int x = 0;
		query.setLayoutX(x);
		query.setLayoutY(0);
		x += 35;
		tf.setLayoutX(x);
		tf.setLayoutY(0);
		x += 250;
		down.setLayoutX(x);
		down.setLayoutY(0);
		x += 35;
		up.setLayoutX(x);
		up.setLayoutY(0);

		x += 35;
		cb.setLayoutX(x);
		cb.setLayoutY(5);

		findAnchorPane.getChildren().add(query);
		findAnchorPane.getChildren().add(tf);
		findAnchorPane.getChildren().add(down);
		findAnchorPane.getChildren().add(up);
		findAnchorPane.getChildren().add(cb);

		JFXButton hideBottom = new JFXButton();
		hideBottom.setGraphic(ImageViewGenerator.svgImageUnactive("window-close"));// fontImgName("caret-square-o-down",
																					// 16, Color.ROYALBLUE));
		findAnchorPane.getChildren().add(hideBottom);
		AnchorPane.setRightAnchor(hideBottom, 0.0);

		VBox b = SqlEditor.getTabVbox();
		hideBottom.setOnAction(v -> {
			delFindReplacePane();
		});
		b.getChildren().add(0, findAnchorPane);
		if (isReplace) {
			b.getChildren().add(1, createReplacePane(tf, cb));
		}
		Platform.runLater(() -> {
			tf.requestFocus();
		});
		return findAnchorPane;
	}
}
