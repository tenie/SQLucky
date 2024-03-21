package net.tenie.Sqlucky.sdk;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.po.DocumentPo;

/**
 * 文本编辑器
 *
 * @author tenie
 */
public interface SqluckyEditor {
    void highLighting(String str);

    void highLighting();

    void errorHighLighting(int begin, String str);

    void changeCodeAreaLineNoThemeHelper();

    MyCodeArea getCodeArea();

    void callPopup();

    void codePopup(KeyEvent e);

    //	隐藏自动补全
    void hideAutoComplete();

    //	显示自动补全
    void showAutoComplete(double x, double y, String str);

    void nextBookmark(boolean tf);

    VBox getCodeAreaPane();

    VBox getCodeAreaPane(String text, boolean editable);

    void setContextMenu(ContextMenu cm);

    void delLineOrSelectTxt();

    void moveAnchorToLineBegin();

    void moveAnchorToLineEnd();

    void delAnchorBeforeWord();

    void delAnchorBeforeChar();

    void delAnchorBeforeString();

    void delAnchorAfterWord();

    void delAnchorAfterChar();

    void delAnchorAfterString();

    void showFindReplaceTextBox(boolean showReplace, String findText);

    void hiddenFindReplaceBox();

    // 设置 codeArea的焦点, 让查找替换, 可以知道在哪个codeArea中出去
    void codeAreaSetFocusedSqluckyEditor();

    DocumentPo getDocumentPo();

    void setDocumentPo(DocumentPo documentPo);
}
