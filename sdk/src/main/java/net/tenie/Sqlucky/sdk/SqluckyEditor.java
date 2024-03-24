package net.tenie.Sqlucky.sdk;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.editor.FindReplaceTextBox;
import net.tenie.Sqlucky.sdk.po.DocumentPo;

/**
 * 文本编辑器
 *
 * @author tenie
 */
public abstract  class SqluckyEditor  extends VBox  {
    public void setCodeArea(String text, boolean editable){
        if (text != null) {
            getCodeArea().appendText(text);
            highLighting();
            if(getDocumentPo() == null){
                DocumentPo documentPo = DocumentPo.createTmpDocumentPo(text);
                setDocumentPo(documentPo);
            }
        }
        getCodeArea().setEditable(editable);
    }
    abstract public void highLighting(String str);

    abstract public void highLighting();

    abstract public  void errorHighLighting(int begin, String str);

    abstract public void changeCodeAreaLineNoThemeHelper();

    abstract public MyCodeArea getCodeArea();

    abstract public  void callPopup();

    abstract public void codePopup(KeyEvent e);

    //	隐藏自动补全
    abstract public void hideAutoComplete();

    //	显示自动补全
    abstract public void showAutoComplete(double x, double y, String str);

    abstract public void nextBookmark(boolean tf);

//    abstract public  VBox getCodeAreaPane();

//    abstract public  VBox getCodeAreaPane(String text, boolean editable);
//    public abstract void setCodeArea(String text, boolean editable);
    abstract public void setContextMenu(ContextMenu cm);

    abstract public void delLineOrSelectTxt();

    abstract public void moveAnchorToLineBegin();

    abstract public void moveAnchorToLineEnd();

    abstract public void delAnchorBeforeWord();

    abstract public  void delAnchorBeforeChar();

    abstract public void delAnchorBeforeString();

    abstract public void delAnchorAfterWord();

    abstract public void delAnchorAfterChar();

    abstract public void delAnchorAfterString();

    abstract public  DocumentPo getDocumentPo();

    abstract public void setDocumentPo(DocumentPo documentPo);
    abstract public VBox getFdbox();
    abstract public void setFdbox(VBox fdbox);
    abstract public  FindReplaceTextBox getFindReplaceTextBox();
    abstract public void setFindReplaceTextBox(FindReplaceTextBox findReplaceTextBox);
}
