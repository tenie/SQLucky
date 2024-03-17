package net.tenie.Sqlucky.sdk;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.ui.CodeAreaHighLightingHelper;

/**
 * 文本编辑器
 * 
 * 
 * @author tenie
 *
 */
public interface SqluckyEditor {
	public void highLighting(String str);

	public void highLighting();

	public void errorHighLighting(int begin, String str);

	public void changeCodeAreaLineNoThemeHelper();

	public MyCodeArea getCodeArea();

	public void callPopup();

	public void codePopup(KeyEvent e);

//	隐藏自动补全
	public void hideAutoComplete();

//	显示自动补全
	public void showAutoComplete(double x, double y, String str);

	public void nextBookmark(boolean tf);

	public VBox getCodeAreaPane();

	public VBox getCodeAreaPane(String text, boolean editable);

	public void setContextMenu(ContextMenu cm);

	public void delLineOrSelectTxt();

	public void moveAnchorToLineBegin();

	public void moveAnchorToLineEnd();

	public void delAnchorBeforeWord();

	public void delAnchorBeforeChar();

	public void delAnchorBeforeString();

	public void delAnchorAfterWord();

	public void delAnchorAfterChar();

	public void delAnchorAfterString();
	public  void  showFindReplaceTextBox(boolean showReplace, String findText);

	public   void  hiddenFindReplaceBox();

	// 设置 codeArea的焦点, 让查找替换, 可以知道在哪个codeArea中出去
	public void codeAreaSetFocusedSqluckyEditor();
}
