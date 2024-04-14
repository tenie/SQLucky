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


    public MyNoLineNumberEditor() {
        codeArea = new MyCodeArea(this);
        this.init(codeArea);
        codeArea.setMylineNumber(null); // 没有行号
        codeArea.getStyleClass().add("styled-text-area-no-line-number"); // 没有背景色
    }


    @Override
    public DocumentPo getDocumentPo() {
        return null;
    }

    @Override
    public void setDocumentPo(DocumentPo documentPo) {

    }

}
