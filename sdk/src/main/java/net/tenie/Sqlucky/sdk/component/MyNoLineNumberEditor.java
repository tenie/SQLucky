package net.tenie.Sqlucky.sdk.component;

import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
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
        // 没有行号
        codeArea.setMylineNumber(null);
        // 没有背景色
        codeArea.getStyleClass().add("styled-text-area-no-line-number");
    }


    @Override
    public DocumentPo getDocumentPo() {
        return null;
    }

    @Override
    public void setDocumentPo(DocumentPo documentPo) {

    }

}
