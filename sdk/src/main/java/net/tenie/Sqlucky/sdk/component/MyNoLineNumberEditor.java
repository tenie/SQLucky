package net.tenie.Sqlucky.sdk.component;

import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.editor.FindReplaceTextBox;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.ui.CodeAreaHighLightingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 没有行号的 codeArea
 *
 * @author tenie
 */
public class MyNoLineNumberEditor extends SqluckyEditor {
    private static Logger logger = LogManager.getLogger(MyNoLineNumberEditor.class);
    private MyCodeArea codeArea;
    private CodeAreaHighLightingHelper highLightingHelper;

    public MyNoLineNumberEditor() {
        codeArea = new MyCodeArea(this);
        this.init(codeArea);
        codeArea.setMylineNumber(null); // 没有行号
        codeArea.getStyleClass().add("styled-text-area-no-line-number"); // 没有背景色
        this.getChildren().add(codeArea);
        VBox.setVgrow(codeArea, Priority.ALWAYS);
        this.getStyleClass().add("my-tag");
    }

    @Override
    public void hideAutoComplete() {
    }

    @Override
    public void showAutoComplete(double x, double y, String str) {
    }

    @Override
    public void nextBookmark(boolean tf) {
        codeArea.getMylineNumber().nextBookmark(tf);
    }

    //	@Override
    public void highLighting(int begin) {
        Platform.runLater(() -> {
            try {
                if (highLightingHelper == null) {
                    highLightingHelper = new CodeAreaHighLightingHelper();
                }

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
    public void highLighting(String str) {
        Platform.runLater(() -> {
            try {
                if (highLightingHelper == null) {
                    highLightingHelper = new CodeAreaHighLightingHelper();
                }
                highLightingHelper.applyFindWordHighlighting(codeArea, str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void errorHighLighting(int begin, String str) {
    }

    @Override
    public void changeCodeAreaLineNoThemeHelper() {

    }

    @Override
    public MyCodeArea getCodeArea() {
        return codeArea;
    }

    @Override
    public void callPopup() {
    }

    @Override
    public void codePopup(KeyEvent e) {
    }


    @Override
    public void setContextMenu(ContextMenu cm) {
    }

    @Override
    public void delLineOrSelectTxt() {
    }

    @Override
    public void moveAnchorToLineBegin() {
    }

    @Override
    public void moveAnchorToLineEnd() {
    }

    @Override
    public void delAnchorBeforeWord() {
    }

    @Override
    public void delAnchorBeforeChar() {
    }

    @Override
    public void delAnchorBeforeString() {
    }

    @Override
    public void delAnchorAfterWord() {
    }

    @Override
    public void delAnchorAfterChar() {
    }

    @Override
    public void delAnchorAfterString() {
    }


    @Override
    public DocumentPo getDocumentPo() {
        return null;
    }

    @Override
    public void setDocumentPo(DocumentPo documentPo) {

    }

    @Override
    public VBox getFdbox() {
        return null;
    }

    @Override
    public void setFdbox(VBox fdbox) {

    }

    @Override
    public FindReplaceTextBox getFindReplaceTextBox() {
        return null;
    }

    @Override
    public void setFindReplaceTextBox(FindReplaceTextBox findReplaceTextBox) {

    }

}
