package net.tenie.Sqlucky.sdk.component.editor;

import javafx.application.Platform;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.Paragraph;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

/**
 * codeArea 按键输入事件
 * @author tenie
 */
public class MyCodeAreaKeyPressedEvent {

    private static final Logger logger = LogManager.getLogger(MyCodeAreaKeyPressedEvent.class);

    private static final ArrayBlockingQueue<Consumer<Integer>> queue = new ArrayBlockingQueue<>(1);

    public static void initKeyPressedEvent(SqluckyEditor sqluckyEditor) {
        MyEditorSheet sheet = sqluckyEditor.getSheet();
        MyCodeArea codeArea = sqluckyEditor.getCodeArea();
        MyAutoComplete myAuto = sqluckyEditor.getMyAuto();
        // 事件KeyEvent
        codeArea.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

            if (myAuto != null) {
                if (!myAuto.isShow()) {
                    if(!(e.isControlDown() || e.isAltDown() || e.isShiftDown() || e.isShortcutDown())){
                        if (e.getCode().isLetterKey()
                                || e.getCode() == KeyCode.PERIOD
                                || e.getCode().isDigitKey()) { // 按 "." 跳出补全提示框
                            // 当前光标位置
                            int anchor = codeArea.getAnchor();
                            // 子线程里执行
                            CommonUtils.runThread(x -> {
                                sqluckyEditor.callPopup(anchor);
                            });
                        }
                    }
                }

            }

            if (e.getCode() != KeyCode.SHIFT &&
                    e.getCode() != KeyCode.CONTROL &&
                    e.getCode() != KeyCode.ALT) {

                // 添加新行
                if (e.getCode() == KeyCode.ENTER) {
                    // shift + enter , 光标移动到行位, 在添加新行
                    if (e.isShiftDown()) {
                        // 当前行
                        var currentParagraph = codeArea.getCurrentParagraph();
                        var currentParagraphText = codeArea.getText(currentParagraph);
                        // 移动到行尾
                        codeArea.moveTo(currentParagraph, currentParagraphText.length());
                        addNewLine(e, codeArea);
                        // 最后添加一个换行符
                        codeArea.insertText(codeArea.getAnchor(), "\n");
                    } else if (!(e.isControlDown() || e.isAltDown() || e.isShortcutDown())) {
                        addNewLine(e, codeArea);
                    }
                } else if (e.getCode() == KeyCode.TAB) { // 文本缩进
                    codeAreaTab(e, codeArea);
                } else if (e.isControlDown() && e.getCode() == KeyCode.A) {
                    codeArea.selectAll();
                    e.consume();
                } else if ((e.isControlDown() || e.isMetaDown()) &&
                        e.getCode() == KeyCode.X) {
                    // 当没有选中文本的时候, 删除当前行
                    if (codeArea.getSelectedText().isEmpty()) {
                        codeArea.selectLine();
                        var range = codeArea.getSelection();
                        IndexRange delIndexRange = new IndexRange(range.getStart() - 1, range.getEnd());
                        codeArea.deleteText(delIndexRange);
                        e.consume();
                    }
                } else if ((e.isControlDown() || e.isMetaDown()) && e.getCode() == KeyCode.C) {
                    selectLineAtCtrlC(e, codeArea);
                    // 复制选中的内容, 避免页面跳动
                    CommonUtils.setClipboardVal(codeArea.getSelectedText());
                    e.consume();
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
                    copyLineAtCtrlD(e, codeArea, sqluckyEditor);


                } else if (e.getCode() == KeyCode.H) {
                    codeAreaCtrlShiftH(e, sheet);
                } else if (e.getCode() == KeyCode.V) { // 黏贴的时候, 防止页面跳到自己黏贴
                    codeAreaCtrlV(e, codeArea, sqluckyEditor);
                } else {
                    delayHighLighting(val -> textChangeAfterAction(val, sheet, myAuto), 600, 0);
                }
            }
        });
    }



    /**
     * 延迟执行高亮, 如果有任务在队列中, 会抛弃任务不执行
     */
    public static void delayHighLighting(Consumer<Integer> caller, int milliseconds, int lineNo) {
        if (queue.isEmpty()) {
            // 队列尾部插入元素, 如果队列满了, 返回false, 插入失败
            queue.offer(caller);

            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(milliseconds);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
                var cl = queue.poll(); // 从队列取出一个元素
                if (cl != null) {
                    cl.accept(lineNo);
                }
            });
            t.start();

        } else {
            logger.debug("delayRunThread");
        }

    }


    /**
     * 文本缩进
     */
    public static void codeAreaTab(KeyEvent e, CodeArea codeArea) {
        if (codeArea.getSelectedText().contains("\n")) {
            if (e.isShiftDown()) {
                HighLightingEditorUtils.minus4Space();
            } else {
                HighLightingEditorUtils.add4Space();
            }
        } else {
            HighLightingEditorUtils.onlyAdd4Space();
        }
        e.consume();
    }

    public static void addNewLine(KeyEvent e, MyCodeArea codeArea) {

        // 换行缩进, 和当前行的缩进保持一致
        logger.info("换行缩进 : {}", e.getCode());
        String selectedText = codeArea.getSelectedText();
        // 获取当前行号
        int idx = codeArea.getCurrentParagraph();
        // 光标位置
        int anchor = codeArea.getAnchor();
        // 没有选中文本, 存粹换行, 才进行缩进计算
        if (selectedText.isEmpty()) {
            // 根据行号获取该行的文本
            Paragraph<Collection<String>, String, Collection<String>> p = codeArea.getParagraph(idx);
            String paragraphText = p.getText();

            // 获取文本开头的空白字符串
            if (StrUtils.isNotNullOrEmpty(paragraphText)) {

                // 一行的前缀空白符
                String strBlank = StrUtils.prefixBlankStr(paragraphText);

                // 获取光标之后的空白符, 如果后面的字符包含空白符, 换行的时候需要修正前缀补充的字符, 补多了换行越来越长
                String strAfter = paragraphPrefixBlankStr(anchor, codeArea);

                String prefixStr ;
                if (!strAfter.isEmpty() && strBlank.length() > strAfter.length()) {
                    prefixStr = strBlank.substring(0, strBlank.length() - strAfter.length());
                } else {
                    prefixStr = strBlank;
                }

                // 当前行的前缀时空白符, 回车后在新行前面填入相同数量的空白符
                if (!prefixStr.isEmpty()) {
                    // 回车后, 在回车那行补上前缀空白符
                    Platform.runLater(() -> codeArea.insertText(idx + 1, 0, prefixStr));

                } else {
                    // 如果光标在起始位, 那么回车后光标移动到起始再会到回车后的位置, 目的是防止页面不滚动
                    if (anchor == 0) {
                        Platform.runLater(() -> {
                            // 光标移动到起始位置
                            codeArea.moveTo(0);
                            Platform.runLater(() ->  codeArea.moveTo(1));
                        });
                    }
                }

            }

        }
    }


    private static String paragraphPrefixBlankStr(int anchor, MyCodeArea codeArea) {
        int a = anchor;
        int b = anchor + 1;
        int len = codeArea.getText().length();

        StringBuilder stringBuilder = new StringBuilder();

        while (true) {
            if (a >= len) {
                break;
            }

            String sc = codeArea.getText(a, b);
            if (" ".equals(sc) || "\t".equals(sc)) {
                stringBuilder.append(sc);
            } else {
                break;
            }
            a++;
            b++;

        }

        return stringBuilder.toString();
    }

    /**
     * 移动光标到当前行的行尾
     */
    public static void codeAreaCtrlShiftE(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("光标移动到行尾{}", e.getCode());
            sheet.getSqluckyEditor().moveAnchorToLineEnd();
        }
    }

    /**
     * 操作当前行的光标之前的单词
     */
    public static void codeAreaCtrlShiftW(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("删除一个光标前的单词{}", e.getCode());
            sheet.getSqluckyEditor().delAnchorBeforeWord();
        }
    }

    /**
     * 删除光标后的字符串
     */
    public static void codeAreaCtrlShiftK(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("删除光标后的字符串{}", e.getCode());
            sheet.getSqluckyEditor().delAnchorAfterString();
        }
    }

    /**
     * 删除光标后一个单词
     */
    public static void codeAreaAltShiftD(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isAltDown()) {
            logger.info("删除一个光标后单词{}", e.getCode());
            sheet.getSqluckyEditor().delAnchorAfterWord();
        }
    }

    /**
     * 删除一个光标后字符
     */
    public static void codeAreaCtrlShiftD(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("删除一个光标后字符{}", e.getCode());
            sheet.getSqluckyEditor().delAnchorAfterChar();
        }
    }

    /**
     * 当前行的字符串, 插入到下一行 / 选中的字符串, 在后面插入
     */
    public static void copyLineAtCtrlD(KeyEvent e, MyCodeArea codeArea, SqluckyEditor sqluckyEditor) {
        // 复制当前行到下一行
        if (e.isControlDown()) {
            if (codeArea.getSelectedText().isEmpty()) {
                int currentAnchor = codeArea.getAnchor();
                codeArea.selectLine();
                var range = codeArea.getSelection();
                String selectText = codeArea.getSelectedText();
                int allTxtLen = codeArea.getText().length();
                // 最后一行的情况, 直接append
                if (range.getEnd() + 1 > allTxtLen) {
                    codeArea.appendText("\n" + codeArea.getSelectedText());
                } else {
                    codeArea.insertText(range.getEnd() + 1, codeArea.getSelectedText() + "\n");
                }

                // 光标移动到下一行的位置
                codeArea.moveTo(currentAnchor + selectText.length() + 1);
                sqluckyEditor.highLighting();
            } else {
                // 插入选择的内容
                var range = codeArea.getSelection();
                codeArea.insertText(range.getEnd(), codeArea.getSelectedText());
                sqluckyEditor.highLighting();
            }

        }
    }

    /**
     * 当按下 ctrl + c 没有选择任何内容的情况下, 选中当前行
     */
    public static void selectLineAtCtrlC(KeyEvent e, MyCodeArea codeArea) {
        if (e.isControlDown() || e.isMetaDown()) {
            if (codeArea.getSelectedText().isEmpty()) {
                codeArea.selectLine();
            }
        }
    }


    /**
     * 删除光标前的字符串
     */
    public static void codeAreaCtrlShiftU(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("删除光标前的字符串{}", e.getCode());
            sheet.getSqluckyEditor().delAnchorBeforeString();
        }
    }


    /**
     * 删除光标前一个字符
     */
    public static void codeAreaCtrlShiftH(KeyEvent e, MyEditorSheet sheet) {
        if (e.isShiftDown() && e.isControlDown()) {
            logger.info("删除一个光标前字符{}", e.getCode());
            sheet.getSqluckyEditor().delAnchorBeforeChar();
        }
    }


    /**
     * 黏贴的时候, 防止页面跳到自己黏贴
     */
    public static void codeAreaCtrlV(KeyEvent e, MyCodeArea codeArea, SqluckyEditor sqluckyEditor) {
        if (e.isShortcutDown()) {
            String val = CommonUtils.getClipboardVal();
            if (!val.isEmpty()) {
                // 自己粘贴, 否则粘贴的行会跳到窗口顶部(windows上会出现)
                String selectedText = codeArea.getSelectedText();
                if (!selectedText.isEmpty()) {
                    IndexRange idx = codeArea.getSelection();
                    codeArea.deleteText(idx);
                }
                codeArea.insertText(codeArea.getAnchor(), val);
                e.consume();
                Platform.runLater(sqluckyEditor::highLighting);
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
//                String title = sheet.getTitle();
//                if (!title.endsWith("*")) {
//                    sheet.setTitle(title + "*");
//                    sheet.setModify(true);
//                }
                sheet.getSqluckyEditor().highLighting(x);
            });
            // 缓存单词
            if (myAuto != null) {
                myAuto.cacheTextWord();
            }
        }

    }

}
