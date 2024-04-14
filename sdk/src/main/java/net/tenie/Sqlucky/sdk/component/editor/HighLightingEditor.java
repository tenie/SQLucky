package net.tenie.Sqlucky.sdk.component.editor;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.*;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.ui.CodeAreaHighLightingHelper;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

/**
 * sql文本编辑组件
 * 
 * @author tenie
 *
 */
public class HighLightingEditor extends SqluckyEditor {
	private static Logger logger = LogManager.getLogger(HighLightingEditor.class);
	private static final String sampleCode = String.join("\n", new String[] { "" });

	private DocumentPo documentPo;



	public HighLightingEditor(MyAutoComplete myAuto, CodeAreaHighLightingHelper helper) {
		this.myAuto = myAuto;
		if(helper == null ){
			highLightingHelper = new CodeAreaHighLightingHelper();
		}else {
			highLightingHelper = helper;
		}

		codeArea = new MyCodeArea(this);
		this.init(codeArea);

		codeArea.textProperty().addListener((a,b,c)->{
			if(c.isEmpty() &&  documentPo != null
					&& codeArea.getCodeArea().getUndoManager().getNextUndo() == null){
				codeArea.insertText(0, documentPo.getText());
				this.highLighting();
			}
		});
		// 行号主题色
		changeCodeAreaLineNoThemeHelper();

		// 自动补全对象不是null,就可以编辑文本
		if (myAuto == null) {
			codeArea.setEditable(false);
			return;

		}
		// 事件KeyEvent
		MyCodeAreaKeyPressedEvent.initKeyPressedEvent(this);
		// TODO 输入事件
//		codeArea.textProperty().addListener(cl);
		codeArea.replaceText(0, 0, sampleCode);

		// 中午输入法显示问题
		codeArea.setInputMethodRequests(new InputMethodRequestsObject(codeArea));
		codeArea.setOnInputMethodTextChanged(e -> {
			if (!Objects.equals(e.getCommitted(), "")) {
				codeArea.insertText(codeArea.getCaretPosition(), e.getCommitted());
			}
		});

		// 当表被拖拽进入到code editor , 将表名插入到 光标处
		codeArea.setOnDragEntered(e -> {
			String val = ComponentGetter.dragTreeItemName;
			if (StrUtils.isNotNullOrEmpty(val)) {
				int start = ComponentGetter.codeAreaAnchor; // codeArea.getAnchor();
				logger.debug("ComponentGetter.codeAreaAnchor = " + start);
				codeArea.insertText(start, " " + val);
//				codeArea.selectRange(start + 1, start + 1 + val.length());
				codeArea.requestFocus();
			}

		});

		// 鼠标退出界面, 记录光标位置
		codeArea.setOnMouseExited(mouseEvent -> {
			ComponentGetter.codeAreaAnchor = codeArea.getAnchor();
		});

		// 当鼠标释放, 判断是否为双击, 是双击选中对应的内容, 在判断有没有选择的文本, 有的话就修改所有相同的文本
		codeArea.setOnMouseReleased(mouseEvent -> {
			if (mouseEvent.getButton() == MouseButton.PRIMARY) { // 鼠标左键
				int clickCount = mouseEvent.getClickCount();
				String str = codeArea.getSelectedText();
				String trimStr = str.trim();
				int strSz = trimStr.length();
				boolean isContinue = true;
				if (clickCount == 2) {

					if (trimStr.isEmpty()) {
						// 选中的内容为空白符, 就选中当前行
						codeArea.selectLine();
					} else {
						// 针对括号() {} []的双击, 选中括号内的文本
						isContinue = selectSQLDoubleClicked(codeArea); // 如果选中了内容, 就会返回false
					}

				} else if (clickCount == 1) { // 鼠标单击
					// 单击 括号() {} []的双击, 找到下一个括号, 对括号添加选中样式
					isContinue = oneClickedFindParenthesis(codeArea);

					// 隐藏自动补全提示框
					if (myAuto != null) {
						myAuto.hide();
					}

				}

				// 上面已经选中了东西这里就不继续往下走了
				if (isContinue) {
					if (strSz > 0 && !"*".equals(trimStr)) {
						// 查找选中的字符
						highLighting(str);
					} else {
						highLighting();
//						// 双击没有选择的情况下, 重新刷新一下高亮
//						if (clickCount == 2) {
//							highLighting();
//						}else {
//							highLighting();
//						}
					}
				}

			}

		});

	}

	// 针对括号() {} []的双击, 选中括号内的文本
	// 如果选中了内容, 就会返回false
	public static boolean selectSQLDoubleClicked(CodeArea codeArea) {
		boolean tf = true;
		String str = codeArea.getSelectedText();
		String trimStr = str.trim();
		int strSz = trimStr.length();
		if (strSz > 0) {
			IndexRange i = codeArea.getSelection(); // 获取当前选中的区间
			int start = i.getStart();
			Set<String> keys = charMap.keySet();

			for (String key : keys) {
				if (trimStr.endsWith(key)) {
					String val = charMap.get(key);
					int endIdx = str.lastIndexOf(key);
					int is = start + endIdx + 1;
					int end = CommonUtils.findBeginParenthesisRange(codeArea.getText(), is, key, val);
					if (end != 0 && end > is) {
						codeArea.selectRange(is, end);
					}
					tf = false;
					break;
				}
			}

			if (tf) {
				keys = charMapPre.keySet();
				for (String key : keys) {
					if (trimStr.endsWith(key)) {
						String val = charMapPre.get(key);
						int endIdx = str.lastIndexOf(key);
						int end = start + endIdx;
						int is = HighLightingEditorUtils.findEndParenthesisRange(codeArea.getText(), end, key, val);
						if (end > is) {
							codeArea.selectRange(is, end);
						}

						tf = false;
						break;
					}
				}
			}
			if (tf) {
				for (String v : charList) {
					if (trimStr.endsWith(v)) {
						int endIdx = str.lastIndexOf(v);
						int end = start + endIdx;
						IndexRange ir = HighLightingEditorUtils.findStringRange(codeArea.getText(), end, v);
						if ((ir.getStart() + ir.getEnd()) > 0) {
							int st = ir.getStart();
							int en = ir.getEnd();
//							codeArea.selectRange(ir.getStart(), ir.getEnd());
							String tmpcheck = codeArea.getText(ir.getStart(), ir.getEnd());
							if (tmpcheck.endsWith(v)) {
								en--;
							}
							if (tmpcheck.startsWith(v)) {
								st++;
							}
							codeArea.selectRange(st, en);

						}

						tf = false;
						break;
					}
				}
			}

			if (tf) {
				if (trimStr.toUpperCase().endsWith("SELECT")) {
					int endIdx = str.toUpperCase().lastIndexOf("SELECT");
					int is = start + endIdx + 6;
					int end = HighLightingEditorUtils.findBeginStringRange(codeArea.getText(), is, "SELECT", "FROM");
					if (end != 0 && end > is) {
						codeArea.selectRange(is - 6, end + 4);
					}
					tf = false;
				} else if (trimStr.toUpperCase().endsWith("FROM")) {
					int endIdx = str.toUpperCase().lastIndexOf("FROM");
					int end = start + endIdx;
					int is = HighLightingEditorUtils.findEndStringRange(codeArea.getText(), end, "FROM", "SELECT");
					if (end > is) {
						codeArea.selectRange(is - 6, end + 5);
					}
					tf = false;
				}

			}

			if (tf) {
				if (trimStr.toUpperCase().endsWith("CASE")) {
					int endIdx = str.toUpperCase().lastIndexOf("CASE");
					int is = start + endIdx + 4;
					int end = HighLightingEditorUtils.findBeginStringRange(codeArea.getText(), is, "CASE", "END");
					if (end != 0 && end > is) {
						codeArea.selectRange(is - 4, end + 3);
					}
					tf = false;
				} else if (trimStr.toUpperCase().endsWith("END")) {
					int endIdx = str.toUpperCase().lastIndexOf("END");
					int end = start + endIdx;
					int is = HighLightingEditorUtils.findEndStringRange(codeArea.getText(), end, "END", "CASE");
					if (end > is) {
						codeArea.selectRange(is - 4, end + 4);
					}
					tf = false;
				}
			}

		}
		return tf;
	}









	static Map<String, String> charMap = new HashMap<>();
	static Map<String, String> charMapPre = new HashMap<>();
	static List<String> charList = new ArrayList<>();

	static {
		charMap.put("(", ")");
		charMap.put("[", "]");
		charMap.put("{", "}");

		charMapPre.put(")", "(");
		charMapPre.put("]", "[");
		charMapPre.put("}", "{");

		charList.add("\"");
		charList.add("'");
		charList.add("`");
		charList.add("%");

	}



	// 鼠标单击找到括号对, 标记一下
	public static boolean oneClickedFindParenthesis(CodeArea codeArea) {
		boolean tf = true;

		int anchor = codeArea.getAnchor();
		int start = anchor == 0 ? anchor : anchor - 1;
		int end = anchor + 1;

		String text = codeArea.getText();
		if (text.length() == anchor) {
			return false;
		}

		String str = codeArea.getText(start, end);// codeArea.getSelectedText();
		String trimStr = str.trim();
		int strSz = trimStr.length();
		if (strSz > 0) {
//			logger.info("鼠标单击找到括号对, 标记一下 |" + trimStr + "|");

			Set<String> keys = charMap.keySet();

			for (String key : keys) {
				if (trimStr.endsWith(key)) {
					String val = charMap.get(key);
					int endIdx = str.lastIndexOf(key);
					int is = start + endIdx + 1;
					end = CommonUtils.findBeginParenthesisRange(codeArea.getText(), is, key, val);
					if (end != 0 && end > is) {

						setStyleSpans(codeArea, is - 1, 1);
						setStyleSpans(codeArea, end, 1);

					}
					tf = false;
					break;
				}
			}

			if (tf) {
				keys = charMapPre.keySet();
				for (String key : keys) {
					if (trimStr.endsWith(key)) {
						String val = charMapPre.get(key);
						int endIdx = str.lastIndexOf(key);
						end = start + endIdx;
						int is = HighLightingEditorUtils.findEndParenthesisRange(codeArea.getText(), end, key, val);
						if (end > is) {
							setStyleSpans(codeArea, is - 1, 1);
							setStyleSpans(codeArea, end, 1);

						}

						tf = false;
						break;
					}
				}
			}

		}
		return tf;
	}

	public static void setStyleSpans(CodeArea codeArea, int idx, int size) {
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		spansBuilder.add(Collections.emptyList(), 0);
		spansBuilder.add(Collections.singleton("findparenthesis"), size);
		codeArea.setStyleSpans(idx, spansBuilder.create());
	}












	public DocumentPo getDocumentPo() {
		return documentPo;
	}

	public void setDocumentPo(DocumentPo documentPo) {
		this.documentPo = documentPo;
	}

}


