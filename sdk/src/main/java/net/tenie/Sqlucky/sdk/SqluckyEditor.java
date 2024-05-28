package net.tenie.Sqlucky.sdk;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.editor.MyAutoComplete;
import net.tenie.Sqlucky.sdk.component.editor.MyLineNumberNode;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.ui.CodeAreaHighLightingHelper;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.model.Paragraph;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 文本编辑器
 *
 * @author tenie
 */
public abstract  class SqluckyEditor  extends VBox  {
    protected MyCodeArea codeArea;
    protected MyAutoComplete myAuto;
    protected MyEditorSheet sheet;
    protected CodeAreaHighLightingHelper highLightingHelper;

    /**
     *
     * 将MyCodeArea 放入到Vbox中， 让MyCodeArea有滚动条
     * @param codeArea
     */
    public void init(MyCodeArea codeArea){
        VirtualizedScrollPane<MyCodeArea> vp = new VirtualizedScrollPane<>(codeArea);
        this.getChildren().add(vp);
        VBox.setVgrow(vp, Priority.ALWAYS);
        this.getStyleClass().add("my-tag");
    }
    public void initCodeArea(String text, boolean editable){
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


    private final int HIGH_LIGHT_MAX_STRING_LENGTH =1000_000;
    public void highLighting(int begin) {
        var codeArea = getCodeArea();
        if (codeArea.getText().length() < HIGH_LIGHT_MAX_STRING_LENGTH) {
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
    }
//    abstract public void highLighting(String str);
public void highLighting(String str) {
    if (codeArea.getText().length() < HIGH_LIGHT_MAX_STRING_LENGTH) {
        Platform.runLater(() -> {
            try {
                if(highLightingHelper == null) {
                    highLightingHelper	= new CodeAreaHighLightingHelper();
                }
                highLightingHelper.applyFindWordHighlighting(codeArea, str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


}


    public void highLighting() {
        highLighting(0);
    }
    public void errorHighLighting(int begin, String str) {
        if (codeArea.getText().length() < HIGH_LIGHT_MAX_STRING_LENGTH) {
            Platform.runLater(() -> {
                try {
                    if (highLightingHelper == null) {
                        highLightingHelper = new CodeAreaHighLightingHelper();
                    }
                    highLightingHelper.applyErrorHighlighting(codeArea, begin, str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

    // 改变样式
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
  public MyCodeArea getCodeArea(){
      return codeArea;
  }

    /**
     * 显示自动补全窗口, 如果是输入触发的, 使用 preAnchor 做为按钮按下是的光标位置, 和执行时光标的位置是1个字符差就显示
     * 如果没有preAnchor 说明是主动触发
     * @param preAnchor
     */
    public void callPopup(Integer preAnchor) {
        var codeArea = getCodeArea();
        if (codeArea.isFocused()) {
                // TODO 屏蔽触发自动补全时多余输入
//            if (CommonUtils.isMacOS()) {
//                Platform.runLater(() -> {
//                    int ar = codeArea.getAnchor();
//                    if(ar == 0) return;
//                    String str = codeArea.getText(ar - 1, ar);
//                    if (str.equals("÷")) {
//                        codeArea.deleteText(ar - 1, ar);
//                    }
//                });
//            } else if (CommonUtils.isLinuxOS()) {
//                Platform.runLater(() -> {
//                    int ar = codeArea.getAnchor();
//                    if(ar == 0) return;
//                    String str = codeArea.getText(ar - 1, ar);
//                    if (str.equals("/")) {
//                        codeArea.deleteText(ar - 1, ar);
//                    }
//                });
//            }
            if (myAuto == null)
                return;
            Platform.runLater(() -> {
                // 光标的坐标系
                Bounds bd = codeArea.caretBoundsProperty().getValue().get();
                double x = bd.getCenterX();
                double y = bd.getCenterY();
                int anchor = codeArea.getAnchor();
                if(preAnchor != null ){
                    if((anchor -1 ) != preAnchor ){
                        return;
                    }
                }
//                String  str = "";
                // 获取光标向前方向的单词
                String str = getAnchorWord();
//                for (int i = 1; anchor - i >= 0; i++) {
//                    var tmp = codeArea.getText(anchor - i, anchor);
//                    int tmplen = tmp.length();
//                    int idx = anchor - tmplen;
//                    if (tmp.startsWith(" ") || tmp.startsWith("\t") || tmp.startsWith("\n") || idx <= 0) {
//                        str = tmp;
//                        break;
//                    }
//                }
                // 子线程创建pop
                CommonUtils.runThread( strVal -> {
                    myAuto.showPop(x, y + 9, strVal);
                }, str);
              ;
            });
        }

    }

    public String getAnchorWord(){
        String str = "";
        int anchor = codeArea.getAnchor();
        // 获取光标向前方向的单词
        for (int i = 1; anchor - i >= 0; i++) {
            var tmp = codeArea.getText(anchor - i, anchor);
            int tmplen = tmp.length();
            int idx = anchor - tmplen;
            if (tmp.startsWith(" ") || tmp.startsWith("\t") || tmp.startsWith("\n") || idx <= 0) {
                str = tmp.trim();
                break;
            }
        }
        return str;
    }

    //	隐藏自动补全
    public void hideAutoComplete() {
        myAuto.hide();
    }

    //	显示自动补全
    public void showAutoComplete(double x, double y, String str) {
        myAuto.showPop(x, y + 7, str);
    }

    public void nextBookmark(boolean tf) {
        getCodeArea().getMylineNumber().nextBookmark(tf);
    }

    public void setContextMenu(ContextMenu cm) {
        if (cm != null) {
            codeArea.setContextMenu(cm);
        }
    }
    /**
     * 移动光标到行开头
     */
    public void moveAnchorToLineBegin() {
        var codeArea = getCodeArea();
        if (codeArea.isFocused()) {
            int idx = codeArea.getCurrentParagraph(); // 获取当前行号
            codeArea.moveTo(idx, 0);
        }

    }

    /**
     * 移动光标去行尾
     */
    public void moveAnchorToLineEnd() {
        var codeArea = getCodeArea();
        if (codeArea.isFocused()) {
            int idx = codeArea.getCurrentParagraph(); // 获取当前行号
            Paragraph<Collection<String>, String, Collection<String>> p = codeArea.getParagraph(idx);
            String ptxt = p.getText();
            codeArea.moveTo(idx, ptxt.length());
        }
    }
//    abstract public void moveAnchorToLineEnd();



    /**
     * 删除光标前的单词
     */
    public void delAnchorBeforeWord() {
        var codeArea = getCodeArea();
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
     * 删除光标后的字符
     */
    public void delAnchorBeforeChar() {
        var codeArea = getCodeArea();
        if (codeArea.isFocused()) {
            int anchor = codeArea.getAnchor(); // 光标位置
            String txt = codeArea.getText(anchor - 1, anchor);
            if (!txt.equals("\n"))
                codeArea.deleteText(anchor - 1, anchor);

        }
    }


    /**
     * 删除光标后的单词
     */
    public void delAnchorAfterWord() {
        var codeArea = getCodeArea();
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
     * 删除光标后的字符
     */
    public void delAnchorAfterChar() {
        var codeArea = getCodeArea();
        if (codeArea.isFocused()) {
            int anchor = codeArea.getAnchor(); // 光标位置
            String txt = codeArea.getText(anchor, anchor + 1);
            if (!txt.equals("\n"))
                codeArea.deleteText(anchor, anchor + 1);
        }
    }


    /**
     * 删除光标前的字符串
     */
    public void delAnchorBeforeString() {
        var codeArea = getCodeArea();
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
     */
    public void delAnchorAfterString() {
        var codeArea = getCodeArea();
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
     * 删除选中的内容或删除光标所在的行
     */
    public void delLineOrSelectTxt() {
        var codeArea = getCodeArea();
        var selectTxt = codeArea.getSelectedText();
        if (StrUtils.isNullOrEmpty(selectTxt)) {
            moveAnchorToLineEnd();
            delAnchorBeforeString();
        } else {
            // 删除选中的内容
            codeArea.deleteText(codeArea.getSelection());
        }
    }

//    abstract public void delAnchorBeforeWord();

//    abstract public  void delAnchorBeforeChar();

//    abstract public void delAnchorBeforeString();

//    abstract public void delAnchorAfterWord();

//    abstract public void delAnchorAfterChar();

//    abstract public void delAnchorAfterString();

    abstract public  DocumentPo getDocumentPo();

    abstract public void setDocumentPo(DocumentPo documentPo);
//    abstract public VBox getFdbox();
//    abstract public void setFdbox(VBox fdbox);
//    abstract public  FindReplaceTextBoTextBox(FindReplaceTextBox findReplaceTextBox);

    public MyAutoComplete getMyAuto() {
        return myAuto;
    }

    public MyEditorSheet getSheet() {
        return sheet;
    }

    public void setSheet(MyEditorSheet sheet) {
        this.sheet = sheet;
    }
}
