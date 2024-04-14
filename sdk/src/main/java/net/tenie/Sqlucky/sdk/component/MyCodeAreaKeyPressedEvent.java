package net.tenie.Sqlucky.sdk.component;

import javafx.application.Platform;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.editor.HighLightingEditor;
import net.tenie.Sqlucky.sdk.component.editor.HighLightingEditorUtils;
import net.tenie.Sqlucky.sdk.component.editor.MyAutoComplete;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.Paragraph;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

public class MyCodeAreaKeyPressedEvent {

    private static Logger logger = LogManager.getLogger(MyCodeAreaKeyPressedEvent.class);
    public static void initKeyPressedEvent(SqluckyEditor sqluckyEditor)
        {
            MyEditorSheet sheet = sqluckyEditor.getSheet();
            MyCodeArea codeArea = sqluckyEditor.getCodeArea();
            MyAutoComplete myAuto =  sqluckyEditor.getMyAuto();
            // 事件KeyEvent
            codeArea.addEventFilter(KeyEvent.KEY_PRESSED, e -> 	{

                if (myAuto != null) {
                    // 提示框还在的情况下又有输入
                    if (myAuto.isShow()) {
                        // 输入的是退格键, 需要判断是否要隐藏提示框
                        if (e.getCode() == KeyCode.BACK_SPACE) {
                            myAuto.backSpaceHide(codeArea);
                        }
                        if (myAuto.isShow()) {
                            myAuto.hide();
                            sqluckyEditor.callPopup();
                        }
                    }
                } else if (e.getCode() == KeyCode.PERIOD) { // 按 "." 跳出补全提示框
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
                                sqluckyEditor.callPopup();
                            }
                        });
                    };
                    CommonUtils.runThread(caller);
                    return;
                }

                if (e.getCode() != KeyCode.SHIFT &&
                        e.getCode() != KeyCode.CONTROL &&
                        e.getCode() != KeyCode.ALT) {

                    // 添加新行
                    if (e.getCode() == KeyCode.ENTER) {
                        if (!(e.isControlDown() || e.isAltDown() || e.isShiftDown() || e.isShortcutDown())) {
                            addNewLine(e, codeArea);
                        }
                    }else if (e.getCode() == KeyCode.TAB) { // 文本缩进
                        codeAreaTab(e, codeArea);
                    } else if (e.isControlDown() && e.getCode() == KeyCode.A) {
                        codeArea.selectAll();
                        e.consume();
                    } else if ( (e.isControlDown()|| e.isMetaDown()) &&
                            e.getCode() == KeyCode.X) {
                        // 当没有选中文本的时候, 删除当前行
                        if (codeArea.getSelectedText().isEmpty()){
                            codeArea.selectLine();
                            var range =  codeArea.getSelection();
                            IndexRange delIndexRange = new IndexRange(range.getStart()-1, range.getEnd());
                            codeArea.deleteText(delIndexRange);
                            e.consume();
                        }
                    } else if (e.getCode() == KeyCode.A) {
                        codeAreaCtrlShiftA(e, sheet);
                    } else if (e.getCode() == KeyCode.E) {
                        codeAreaCtrlShiftE(e, sheet);
                    } else if (e.getCode() == KeyCode.W) {
                        codeAreaCtrlShiftW(e, sheet);
                    } else if (e.getCode() == KeyCode.U) {
                        codeAreaCtrlShiftU(e, sheet);
                    } else if (e.getCode() == KeyCode.K) {
                        codeAreaCtrlShiftK(e, sheet);
                    } else if (e.getCode() == KeyCode.D) {
                        codeAreaAltShiftD(e, sheet);
                        codeAreaCtrlShiftD(e, sheet);
                    } else if (e.getCode() == KeyCode.H) {
                        codeAreaCtrlShiftH(e, sheet);
                    } else if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE) {
//				codeAreaBackspaceDelete(e, cl);
                    } else if (e.getCode() == KeyCode.V) { // 黏贴的时候, 防止页面跳到自己黏贴
                        codeAreaCtrlV(e, codeArea);
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

                        delayHighLighting(val->{
                            textChangeAfterAction(val, sheet, myAuto);
                        }, 600, 0);
                    }
                }
            });
        }


    private static  ArrayBlockingQueue<Consumer<Integer>> queue = new ArrayBlockingQueue<>(1);
    /**
     * 延迟执行高亮, 如果有任务在队列中, 会抛弃任务不执行
     *
     * @param caller
     * @param milliseconds
     */
    public static  void delayHighLighting(Consumer<Integer> caller, int milliseconds, int lineNo) {
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


    /**
     * 文本缩进
     *
     * @param e
     * @param codeArea
     */
    public static void codeAreaTab(KeyEvent e, CodeArea codeArea) {
        if (codeArea.getSelectedText().contains("\n")) {
//            logger.info("文本缩进 : " + e.getCode());
//			e.consume();
            if (e.isShiftDown()) {
                HighLightingEditorUtils.minus4Space();
            } else {
                HighLightingEditorUtils.add4Space();
            }
        }else{
            HighLightingEditorUtils.onlyAdd4Space();
        }
        e.consume();
    }

    public static void addNewLine(KeyEvent e, MyCodeArea codeArea) {

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
                String strafter = paragraphPrefixBlankStr(anchor, codeArea);

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



    private static String paragraphPrefixBlankStr(int anchor, MyCodeArea codeArea) {
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
     * 移动光标到当前行的行首
     */
    public static  void codeAreaCtrlShiftA(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("光标移动到行首" + e.getCode());
            sheet.getSqluckyEditor().moveAnchorToLineBegin();
        }
    }
    // 移动光标到行开头
//    public  static void moveAnchorToLineBegin(MyCodeArea codeArea) {
//        if (codeArea.isFocused()) {
//            int idx = codeArea.getCurrentParagraph(); // 获取当前行号
//            codeArea.moveTo(idx, 0);
//        }
//
//    }

    /**
     * 移动光标到当前行的行尾
     *
     */
    public static void codeAreaCtrlShiftE(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("光标移动到行尾" + e.getCode());
            sheet.getSqluckyEditor().moveAnchorToLineEnd();
        }
    }


//    public static void moveAnchorToLineEnd(MyCodeArea codeArea) {
//        if (codeArea.isFocused()) {
//            int idx = codeArea.getCurrentParagraph(); // 获取当前行号
//            Paragraph<Collection<String>, String, Collection<String>> p = codeArea.getParagraph(idx);
//            String ptxt = p.getText();
//            codeArea.moveTo(idx, ptxt.length());
//
//        }
//    }

    /**
     * 操作当前行的光标之前的单词
     */
    public static void codeAreaCtrlShiftW(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("删除一个光标前的单词" + e.getCode());
            sheet.getSqluckyEditor().delAnchorBeforeWord();
        }
    }

//    public static void delAnchorBeforeWord(MyCodeArea codeArea) {
//        if (codeArea.isFocused()) {
//            int anchor = codeArea.getAnchor(); // 光标位置
//            String txt = codeArea.getText(0, anchor);
//
//            int[] a = { 0, 0, 0 };
//            a[0] = txt.lastIndexOf(" ");
//            a[1] = txt.lastIndexOf("\t");
//            a[2] = txt.lastIndexOf("\n") + 1;
//            int max = CommonUtils.getMax(a);
//            codeArea.deleteText(max, anchor);
//        }
//
//    }


//    /**
//     * 删除光标前的字符串
//     */
//    public static void codeAreaCtrlShiftU(KeyEvent e, MyCodeArea codeArea) {
//        if (e.isShiftDown() && e.isControlDown()) {
//            logger.info("删除光标前的字符串" + e.getCode());
//            delAnchorBeforeString(codeArea);
//        }
//    }

//    public static void delAnchorBeforeString( MyCodeArea codeArea) {
//        if (codeArea.isFocused()) {
//            int anchor = codeArea.getAnchor(); // 光标位置
//            String txt = codeArea.getText(0, anchor);
//
//            int idx = txt.lastIndexOf("\n");
//            if (idx == -1) {
//                idx = 0;
//            } else {
//                idx++;
//            }
//            codeArea.deleteText(idx, anchor);
//        }
//    }

    /**
     * 删除光标后的字符串
     */
    public static void codeAreaCtrlShiftK(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("删除光标后的字符串" + e.getCode());
            sheet.getSqluckyEditor().delAnchorAfterString();
        }
    }

    /**
     * 删除光标后一个单词
     */
    public static void codeAreaAltShiftD(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isAltDown()) {
            logger.info("删除一个光标后单词" + e.getCode());
            sheet.getSqluckyEditor().delAnchorAfterWord();
        }
    }
    /**
     * ctrl + d
     */
//    public  static void codeAreaCtrlD(KeyEvent e, MyCodeArea codeArea) {
//        if (e.isControlDown()) {
//            logger.info("删除选中的内容或删除光标所在的行" + e.getCode());
//            delLineOrSelectTxt(codeArea);
//        }
//    }


    /**
     * 删除一个光标后字符
     */
    public static void codeAreaCtrlShiftD(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("删除一个光标后字符" + e.getCode());
            sheet.getSqluckyEditor().delAnchorAfterChar();
        }
    }



    /**
     * 删除光标前的字符串
     */
    public static void codeAreaCtrlShiftU(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("删除光标前的字符串" + e.getCode());
            sheet.getSqluckyEditor().delAnchorBeforeString();
        }
    }


    /**
     * 删除光标前一个字符
     */
    public static  void codeAreaCtrlShiftH(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("删除一个光标前字符" + e.getCode());
            sheet.getSqluckyEditor().delAnchorBeforeChar();
        }
    }


    /**
     * 黏贴的时候, 防止页面跳到自己黏贴
     */
    public static void codeAreaCtrlV(KeyEvent e, MyCodeArea codeArea) {
        if (e.isShortcutDown()) {
            String val = CommonUtils.getClipboardVal();
//			logger.info("黏贴值==" + val);
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

    // 文本修改后title设置保存提示
    public static void textChangeAfterAction(Integer x, MyEditorSheet sheet1, MyAutoComplete myAuto) {
        if (sheet1 == null) {
            sheet1 = MyEditorSheetHelper.getActivationEditorSheet();
        }
        MyEditorSheet sheet = sheet1;
        if (sheet != null) {

            Platform.runLater(() -> {
                String title = sheet.getTitle();
                if (!title.endsWith("*")) {
                    sheet.setTitle(title + "*");
                    sheet.setModify(true);
                }
                sheet.getSqluckyEditor().highLighting(x);
//                sheet.highLighting(x);
            });
            // 缓存单词
            if (myAuto != null) {
                myAuto.cacheTextWord();
            }
        }

    }


    /**
     * 触发删除按钮
     */
//	public void codeAreaBackspaceDelete(KeyEvent e, ChangeListener<String> cl) {
//		// 删除选中字符串防止页面滚动, 自己删
//		codeArea.textProperty().removeListener(cl);
//		Platform.runLater(() -> {
//			codeArea.textProperty().addListener(cl);
//		});
//	}


    /**
     * 文本的样式变化会导致页面跳动, 在撤销的时候去除文本变化监听事件
     */
//	public void codeAreaCtrlZ(KeyEvent e, ChangeListener<String> cl) {
//
//		if (e.isShortcutDown()) {
//			codeArea.textProperty().removeListener(cl);
//			Platform.runLater(() -> {
//				codeArea.textProperty().addListener(cl);
//			});
//		}
//	}
}
