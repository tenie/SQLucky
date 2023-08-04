package net.tenie.Sqlucky.sdk.component.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.ui.CodeAreaHighLightingHelper;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * sql文本编辑组件
 * 
 * @author tenie
 *
 */
public class HighLightingEditor implements SqluckyEditor {
	private static Logger logger = LogManager.getLogger(HighLightingEditor.class);
	private static final String sampleCode = String.join("\n", new String[] { "" });
	private StackPane codeAreaPane;
	private MyCodeArea codeArea;
	private ExecutorService executor;
	private CodeAreaHighLightingHelper highLightingHelper;
	private MyAutoComplete myAuto;
//	private MyEditorSheet myAreaTab;

	@Override
	public void hideAutoComplete() {
		myAuto.hide();
	}

	// 显示自动补全
	@Override
	public void showAutoComplete(double x, double y, String str) {
		myAuto.showPop(x, y + 7, str);
	}

	@Override
	public void nextBookmark(boolean tf) {
		codeArea.getMylineNumber().nextBookmark(tf);
	}

	@Override
	public void setContextMenu(ContextMenu cm) {
		if (cm != null) {
			codeArea.setContextMenu(cm);
		}
	}

	// 文本修改后title设置保存提示
	Consumer<Integer> caller = x -> {
		MyEditorSheet sheet = MyEditorSheetHelper.getActivationEditorSheet();
		if (sheet != null) {
			Platform.runLater(() -> {
				String title = sheet.getTitle(); // CommonUtility.tabText(tb);
				if (!title.endsWith("*")) {
					sheet.setTitle(title + "*");
					sheet.setModify(true);
				}
				this.highLighting(x);
			});
			// 缓存单词
			if (myAuto != null) {
				myAuto.cacheTextWord();
			}
		}
	};

//    HighLightingSqlCodeAreaContextMenu cm = new  HighLightingSqlCodeAreaContextMenu(this); 

	public HighLightingEditor(MyAutoComplete myAuto) {
//		this.myAreaTab = sheet;
		this.myAuto = myAuto;
		highLightingHelper = new CodeAreaHighLightingHelper();
		executor = Executors.newSingleThreadExecutor();
		codeArea = new MyCodeArea();

		// 行号主题色
		changeCodeAreaLineNoThemeHelper();

		// 自动补全对象不是null,就可以编辑文本
		if (myAuto == null) {
			codeArea.setEditable(false);
			return;

		}
		// 事件KeyEvent
		codeArea.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

			if (myAuto != null) {
				// 提示框还在的情况下又有输入
				if (myAuto.isShow()) {
					// 输入的是退格键, 需要判断是否要隐藏提示框
					if (e.getCode() == KeyCode.BACK_SPACE) {
						myAuto.backSpaceHide(codeArea);
					}
					if (myAuto.isShow()) {
						myAuto.hide();
						callPopup();
					}
				}
			}

			// 文本缩进
			if (e.getCode() == KeyCode.TAB) {
				codeAreaTab(e, codeArea);
			}
			// 按 "." 跳出补全提示框
			else if (e.getCode() == KeyCode.PERIOD) {
				int anchor = codeArea.getAnchor();
				Consumer<String> caller = x -> {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					Platform.runLater(() -> {
						int lateAnchor = codeArea.getAnchor();
						if ((lateAnchor - 1) == anchor) {
							callPopup();
						}
					});
				};
				CommonUtils.runThread(caller);

			} else if (e.getCode() == KeyCode.A) {
				codeAreaCtrlShiftA(e);
			} else if (e.getCode() == KeyCode.E) {
				codeAreaCtrlShiftE(e);
			} else if (e.getCode() == KeyCode.W) {
				codeAreaCtrlShiftW(e);
			} else if (e.getCode() == KeyCode.U) {
				codeAreaCtrlShiftU(e);
			} else if (e.getCode() == KeyCode.K) {
				codeAreaCtrlShiftK(e);
			} else if (e.getCode() == KeyCode.D) {
				codeAreaAltShiftD(e);
				codeAreaCtrlShiftD(e);
			} else if (e.getCode() == KeyCode.H) {
				codeAreaCtrlShiftH(e);
			} else if (e.getCode() == KeyCode.ENTER) {
				addNewLine(e);
			} else if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE) {
//				codeAreaBackspaceDelete(e, cl);
			} else if (e.getCode() == KeyCode.V) { // 黏贴的时候, 防止页面跳到自己黏贴
				codeAreaCtrlV(e);
			} else if (e.getCode() == KeyCode.Z) { // 文本的样式变化会导致页面跳动, 在撤销的时候去除文本变化监听事件
//				codeAreaCtrlZ(e, cl);
			} else {
//				Consumer<Integer> caller = x -> {
//					if (myAreaTab != null) {
//						Platform.runLater(() -> {
//							String title = myAreaTab.getTitle(); //CommonUtility.tabText(tb);
//							if (!title.endsWith("*")) {
//								myAreaTab.setTitle(title + "*");
//								myAreaTab.setModify(true);
//							}
//							this.highLighting(x);
//						});
//						// 缓存单词
//						if (myAuto != null) {
//							myAuto.cacheTextWord();
//						}
//					}
//				};
//				普通输入
//				System.out.println("普通输入"); 
//				var ecode = e.getCode();
//				ecode.isModifierKey();
//				ecode.isWhitespaceKey();
//				
//				if (!e.isShortcutDown() && !e.isAltDown() && !e.isControlDown() && !e.isShiftDown()
//						&& e.getCode() != KeyCode.CAPS && e.getCode() != KeyCode.CAPS 
//						&& (e.getCode().isLetterKey() || e.getCode().isDigitKey() || e.getCode().isKeypadKey())
//						) {
//
//					int currentLine = codeArea.getCurrentParagraph();
//					String text = codeArea.getText(0, 0, currentLine, 0);
//					int textLength = text.length();
//					if (textLength > 0) {
//						textLength--;
//					}
//					delayHighLighting(caller, 600, textLength);
//
//					var selection = codeArea.getSelection();
//					if (selection != null && selection.getLength() > 0) {
//						codeArea.deleteText(selection);
//					}
//
//				} else {
//					delayHighLighting(caller, 600, 0);
//				}

				delayHighLighting(caller, 600, 0);
			}

		});
		// TODO 输入事件
//		codeArea.textProperty().addListener(cl);
		codeArea.replaceText(0, 0, sampleCode);

		// 中午输入法显示问题
		codeArea.setInputMethodRequests(new InputMethodRequestsObject(codeArea));
		codeArea.setOnInputMethodTextChanged(e -> {
			if (e.getCommitted() != "") {
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
				codeArea.selectRange(start + 1, start + 1 + val.length());
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

					if (trimStr.length() == 0) {
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
						// 双击没有选择的情况下, 重新刷新一下高亮
						if (clickCount == 2) {
							highLighting();
						}
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

	@Override
	public StackPane getCodeAreaPane() {
		if (codeAreaPane == null) {
			return getCodeAreaPane(null, true);
		} else {
			return codeAreaPane;
		}
	}

	@Override
	public StackPane getCodeAreaPane(String text, boolean editable) {
		if (codeAreaPane == null) {
			codeAreaPane = new StackPane(new VirtualizedScrollPane<>(codeArea));
			codeAreaPane.getStyleClass().add("my-tag");
		}

		if (text != null) {
			codeArea.appendText(text);
			highLighting();
		}
		codeArea.setEditable(editable);

		return codeAreaPane;
	}

	@Override
	public void highLighting(String str) {
		Platform.runLater(() -> {
			try {
				highLightingHelper.applyFindWordHighlighting(codeArea, str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	public void highLighting(int begin) {
		Platform.runLater(() -> {
			try {
				highLightingHelper.applyHighlighting(codeArea, begin);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	@Override
	public void highLighting() {
		highLighting(0);
	}

	@Override
	public void errorHighLighting(int begin, String str) {
		Platform.runLater(() -> {
			try {
				highLightingHelper.applyErrorHighlighting(codeArea, begin, str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void stop() {
		executor.shutdown();
	}

	@Override
	public MyCodeArea getCodeArea() {
		return codeArea;
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

	// 改变样式
	@Override
	public void changeCodeAreaLineNoThemeHelper() {
		MyLineNumberNode nbf = null;
		List<String> lines = null;
		if (codeArea.getMylineNumber() != null) {
			lines = codeArea.getMylineNumber().getLineNoList();
		}

		if (ConfigVal.THEME.equals(CommonConst.THEME_DARK)) {
			nbf = MyLineNumberNode.get(codeArea, "#606366", "#313335", lines);
		} else if (ConfigVal.THEME.equals(CommonConst.THEME_YELLOW)) {
			nbf = MyLineNumberNode.get(codeArea, "#ffffff", "#000000", lines);
		} else if (ConfigVal.THEME.equals(CommonConst.THEME_LIGHT)) {
			nbf = MyLineNumberNode.get(codeArea, "#666", "#ddd", lines);
		}

		codeArea.setParagraphGraphicFactory(nbf);
		codeArea.setMylineNumber(nbf);

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
//			logger.info("单击选中 |"+ trimStr+"|" );
		if (strSz > 0) {
			logger.info("鼠标单击找到括号对, 标记一下 |" + trimStr + "|");

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

	/**
	 * 自动补全提示
	 * 
	 * @param e
	 * @param codeArea
	 */
	@Override
	public void codePopup(KeyEvent e) {
		if (myAuto == null)
			return;
		if (e.isAltDown()) {
			callPopup();

		}
	}

	/**
	 * 自动补全
	 * 
	 */
	@Override
	public void callPopup() {
		if (codeArea.isFocused()) {
			if (CommonUtils.isMacOS()) {
				Platform.runLater(() -> {
					int ar = codeArea.getAnchor();
					String str = codeArea.getText(ar - 1, ar);
					if (str.equals("÷")) {
						codeArea.deleteText(ar - 1, ar);
					}
				});
			} else if (CommonUtils.isLinuxOS()) {
				Platform.runLater(() -> {
					int ar = codeArea.getAnchor();
					String str = codeArea.getText(ar - 1, ar);
					if (str.equals("/")) {
						codeArea.deleteText(ar - 1, ar);
					}
				});
			}
			if (myAuto == null)
				return;
			Platform.runLater(() -> {
				Bounds bd = codeArea.caretBoundsProperty().getValue().get();
				double x = bd.getCenterX();
				double y = bd.getCenterY();
				int anchor = codeArea.getAnchor();
				String str = "";
				for (int i = 1; anchor - i >= 0; i++) {
					var tmp = codeArea.getText(anchor - i, anchor);
					int tmplen = tmp.length();
					int idx = anchor - tmplen;
					if (tmp.startsWith(" ") || tmp.startsWith("\t") || tmp.startsWith("\n") || idx <= 0) {
						str = tmp;
						break;
					}
				}
				myAuto.showPop(x, y + 9, str);
			});
//			SqluckyEditor.currentMyTab().getSqlCodeArea().callPopup();

		}

	}

	/**
	 * 移动光标到当前行的行首
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaCtrlShiftA(KeyEvent e) {
		if (e.isShiftDown() && e.isControlDown()) {
			logger.info("光标移动到行首" + e.getCode());
			moveAnchorToLineBegin();
		}
	}

	// 移动光标到行开头
	@Override
	public void moveAnchorToLineBegin() {
		if (codeArea.isFocused()) {
			int idx = codeArea.getCurrentParagraph(); // 获取当前行号
			codeArea.moveTo(idx, 0);
		}

	}

	/**
	 * 移动光标到当前行的行尾
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaCtrlShiftE(KeyEvent e) {
		if (e.isShiftDown() && e.isControlDown()) {
			logger.info("光标移动到行尾" + e.getCode());
			moveAnchorToLineEnd();
		}
	}

	@Override
	public void moveAnchorToLineEnd() {
		if (codeArea.isFocused()) {
			int idx = codeArea.getCurrentParagraph(); // 获取当前行号
			Paragraph<Collection<String>, String, Collection<String>> p = codeArea.getParagraph(idx);
			String ptxt = p.getText();
			codeArea.moveTo(idx, ptxt.length());

		}
	}

	/**
	 * 操作当前行的光标之前的单词
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaCtrlShiftW(KeyEvent e) {
		if (e.isShiftDown() && e.isControlDown()) {
			logger.info("删除一个光标前的单词" + e.getCode());
			delAnchorBeforeWord();
		}
	}

	@Override
	public void delAnchorBeforeWord() {
		if (codeArea.isFocused()) {
			int anchor = codeArea.getAnchor(); // 光标位置
			String txt = codeArea.getText(0, anchor);

			int[] a = { 0, 0, 0 };
			a[0] = txt.lastIndexOf(" ");
			a[1] = txt.lastIndexOf("\t");
			a[2] = txt.lastIndexOf("\n") + 1;
			int max = CommonUtils.getMax(a);
			codeArea.deleteText(max, anchor);
		}

	}

	/**
	 * 删除光标前一个字符
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaCtrlShiftH(KeyEvent e) {
		if (e.isShiftDown() && e.isControlDown()) {
			logger.info("删除一个光标前字符" + e.getCode());
			delAnchorBeforeChar();
		}
	}

	@Override
	public void delAnchorBeforeChar() {
		if (codeArea.isFocused()) {
			int anchor = codeArea.getAnchor(); // 光标位置
			String txt = codeArea.getText(anchor - 1, anchor);
			if (!txt.equals("\n"))
				codeArea.deleteText(anchor - 1, anchor);

		}
	}

	/**
	 * 删除光标后一个单词
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaAltShiftD(KeyEvent e) {
		if (e.isShiftDown() && e.isAltDown()) {
			logger.info("删除一个光标后单词" + e.getCode());
			delAnchorAfterWord();
		}
	}

	@Override
	public void delAnchorAfterWord() {
		if (codeArea.isFocused()) {
			int anchor = codeArea.getAnchor(); // 光标位置
			String txt = codeArea.getText();
			int txtLen = txt.length();
			int[] a = { 0, 0, 0 };
			int val = 0;
			val = txt.indexOf(" ", anchor);
			a[0] = val == -1 ? txtLen : val + 1;
			val = txt.indexOf("\t", anchor);
			a[1] = val == -1 ? txtLen : val + 1;
			val = txt.indexOf("\n", anchor);
			a[2] = val == -1 ? txtLen : val;
			int min = CommonUtils.getMin(a);
			codeArea.deleteText(anchor, min);
		}
	}

	/**
	 * 删除一个光标后字符
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaCtrlShiftD(KeyEvent e) {
		if (e.isShiftDown() && e.isControlDown()) {
			logger.info("删除一个光标后字符" + e.getCode());
			delAnchorAfterChar();
		}
	}

	@Override
	public void delAnchorAfterChar() {
		if (codeArea.isFocused()) {
			int anchor = codeArea.getAnchor(); // 光标位置
			String txt = codeArea.getText(anchor, anchor + 1);
			if (!txt.equals("\n"))
				codeArea.deleteText(anchor, anchor + 1);
		}
	}

	/**
	 * 删除光标前的字符串
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaCtrlShiftU(KeyEvent e) {
		if (e.isShiftDown() && e.isControlDown()) {
			logger.info("删除光标前的字符串" + e.getCode());
			delAnchorBeforeString();
		}
	}

	@Override
	public void delAnchorBeforeString() {
		if (codeArea.isFocused()) {
			int anchor = codeArea.getAnchor(); // 光标位置
			String txt = codeArea.getText(0, anchor);

			int idx = txt.lastIndexOf("\n");
			if (idx == -1) {
				idx = 0;
			} else {
				idx++;
			}
			codeArea.deleteText(idx, anchor);
		}
	}

	/**
	 * 删除光标后的字符串
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaCtrlShiftK(KeyEvent e) {
		if (e.isShiftDown() && e.isControlDown()) {
			logger.info("删除光标后的字符串" + e.getCode());
			delAnchorAfterString();
		}
	}

	@Override
	public void delAnchorAfterString() {
		if (codeArea.isFocused()) {
			int anchor = codeArea.getAnchor(); // 光标位置
			String txt = codeArea.getText();

			int idx = txt.indexOf("\n", anchor);
			if (idx == -1) {
				idx = 0;
			} else {
				idx++;
			}
			codeArea.deleteText(anchor, idx - 1);
		}
	}

	/**
	 * ctrl + d
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaCtrlD(KeyEvent e) {
		if (e.isControlDown()) {
			logger.info("删除选中的内容或删除光标所在的行" + e.getCode());
			delLineOrSelectTxt();
		}
	}

	/**
	 * 删除选中的内容或删除光标所在的行
	 * 
	 * @param codeArea
	 */
	@Override
	public void delLineOrSelectTxt() {
		var selectTxt = codeArea.getSelectedText();
		if (StrUtils.isNullOrEmpty(selectTxt)) {
			moveAnchorToLineEnd();
			delAnchorBeforeString();
		} else {
			// 删除选中的内容
			codeArea.deleteText(codeArea.getSelection());
		}
	}

	public void addNewLine(KeyEvent e) {

		// 换行缩进, 和当前行的缩进保持一致
		logger.info("换行缩进 : " + e.getCode());
		String seltxt = codeArea.getSelectedText();
		int idx = codeArea.getCurrentParagraph(); // 获取当前行号
		int anchor = codeArea.getAnchor(); // 光标位置

		if (seltxt.length() == 0) {// 没有选中文本, 存粹换行, 才进行缩进计算
			// 根据行号获取该行的文本
			Paragraph<Collection<String>, String, Collection<String>> p = codeArea.getParagraph(idx);
			String ptxt = p.getText();

			// 获取文本开头的空白字符串
			if (StrUtils.isNotNullOrEmpty(ptxt)) {

				// 一行的前缀空白符
				String strb = StrUtils.prefixBlankStr(ptxt);
				int countSpace = strb.length();

				// 获取光标之后的空白符, 如果后面的字符包含空白符, 换行的时候需要修正前缀补充的字符, 补多了换行越来越长
//					String afterAnchorText =  codeArea.getText(anchor,codeArea.getText().length());
//					String strafter = StrUtils.prefixBlankStr(afterAnchorText);
				String strafter = paragraphPrefixBlankStr(anchor);

				String fstr = "";
				if (strafter.length() > 0 && strb.length() > strafter.length()) {
					fstr = strb.substring(0, strb.length() - strafter.length());
				} else {
					fstr = strb;
				}

				// 当前行的前缀时空白符, 回车后在新行前面填入相同数量的空白符
				if (fstr.length() > 0) {
//					e.consume();
					String addstr = fstr;
					// 回车后, 在回车那行补上前缀空白符
					Platform.runLater(() -> {
						codeArea.insertText(idx + 1, 0, addstr);
					});

				} else {
					// 如果光标在起始位, 那么回车后光标移动到起始再会到回车后的位置, 目的是防止页面不滚动
					if (anchor == 0) {
						Platform.runLater(() -> {
							codeArea.moveTo(0); // 光标移动到起始位置
							Platform.runLater(() -> {
								codeArea.moveTo(1);
							});
						});
					} else {
						// 在最后一行
//						e.consume();
//						codeArea.insertText(anchor, "\n");
//						codeArea.scrollYToPixel(Double.MAX_VALUE);
					}
				}

			}

		}
	}

	/**
	 * 触发删除按钮
	 * 
	 * @param e
	 * @param codeArea
	 * @param cl
	 */
	public void codeAreaBackspaceDelete(KeyEvent e, ChangeListener<String> cl) {
		// 删除选中字符串防止页面滚动, 自己删
		codeArea.textProperty().removeListener(cl);
		Platform.runLater(() -> {
			codeArea.textProperty().addListener(cl);
		});
	}

	/**
	 * 黏贴的时候, 防止页面跳到自己黏贴
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaCtrlV(KeyEvent e) {
		if (e.isShortcutDown()) {
			String val = CommonUtils.getClipboardVal();
			logger.info("黏贴值==" + val);
			if (val.length() > 0) {
				String seltxt = codeArea.getSelectedText();
				if (seltxt.length() > 0) {
					IndexRange idx = codeArea.getSelection();
					codeArea.deleteText(idx);
					codeArea.insertText(codeArea.getAnchor(), val);
					e.consume();
				}
			}
		}
	}

	/**
	 * 文本的样式变化会导致页面跳动, 在撤销的时候去除文本变化监听事件
	 * 
	 * @param e
	 * @param codeArea
	 */
	public void codeAreaCtrlZ(KeyEvent e, ChangeListener<String> cl) {

		if (e.isShortcutDown()) {
			codeArea.textProperty().removeListener(cl);
			Platform.runLater(() -> {
				codeArea.textProperty().addListener(cl);
			});
		}
	}

	private String paragraphPrefixBlankStr(int anchor) {
		int a = anchor;
		int b = anchor + 1;
		int len = codeArea.getText().length();

		StringBuilder strb2 = new StringBuilder("");

		while (true) {
			if (a >= len)
				break;

			String sc = codeArea.getText(a, b);
			if (" ".equals(sc) || "\t".equals(sc)) {
				strb2.append(sc);
			} else {
				break;
			}
			a++;
			b++;

		}

		return strb2.toString();
	}

	/**
	 * 文本缩进
	 * 
	 * @param e
	 * @param codeArea
	 */
	public static void codeAreaTab(KeyEvent e, CodeArea codeArea) {
		if (codeArea.getSelectedText().contains("\n")) {
			logger.info("文本缩进 : " + e.getCode());
			e.consume();
			if (e.isShiftDown()) {
				HighLightingEditorUtils.minus4Space();
			} else {
				HighLightingEditorUtils.add4Space();
			}
		}
	}

	private ArrayBlockingQueue<Consumer<Integer>> queue = new ArrayBlockingQueue<>(1);

	/**
	 * 延迟执行高亮, 如果有任务在队列中, 会抛弃任务不执行
	 * 
	 * @param caller
	 * @param milliseconds
	 */
	public void delayHighLighting(Consumer<Integer> caller, int milliseconds, int lineNo) {
		if (queue.isEmpty()) {
			queue.offer(caller); // 队列尾部插入元素, 如果队列满了, 返回false, 插入失败

			Thread t = new Thread() {
				@Override
				public void run() {

					try {
						Thread.sleep(milliseconds);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					var cl = queue.poll(); // 从队列取出一个元素
					if (cl != null) {
						cl.accept(lineNo);
					}
				}
			};
			t.start();

		} else {
			logger.debug("delayRunThread");
			return;

		}

	}
}

class InputMethodRequestsObject implements InputMethodRequests {
	private static Logger logger = LogManager.getLogger(InputMethodRequestsObject.class);
	private CodeArea area;

	public InputMethodRequestsObject(CodeArea area) {
		this.area = area;
	}

	@Override
	public String getSelectedText() {
		return "";
	}

	@Override
	public int getLocationOffset(int x, int y) {
		return 0;
	}

	@Override
	public void cancelLatestCommittedText() {

	}

	@Override
	public Point2D getTextLocation(int offset) {
		logger.info("输入法软件展示");
		// a very rough example, only tested under macOS
		Optional<Bounds> caretPositionBounds = area.getCaretBounds();
		if (caretPositionBounds.isPresent()) {
			Bounds bounds = caretPositionBounds.get();
			return new Point2D(bounds.getMaxX() - 5, bounds.getMaxY());
		}
		throw new NullPointerException();
	}

}