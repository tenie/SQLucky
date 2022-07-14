package net.tenie.fx.component.CodeArea;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import net.tenie.Sqlucky.sdk.SqluckyCodeAreaHolder;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;

/**
 * 普通文本编辑组件
 * 
 * @author tenie
 *
 */
public class MyTextArea implements SqluckyCodeAreaHolder {
	private static Logger logger = LogManager.getLogger(MyTextArea.class);
	private StackPane codeAreaPane;
	private MyCodeArea codeArea;

	public MyTextArea() {
		codeArea = new MyCodeArea();
		// 行号主题色
		changeCodeAreaLineNoThemeHelper();
	}

	@Override
	public void hideAutoComplete() {
	}

	@Override
	public void showAutoComplete(double x, double y, String str) {
	}

	@Override
	public void nextBookmark(boolean tf) {
	}

	@Override
	public void highLighting(String str) {
		
	}

	@Override
	public void highLighting() {
	}

	@Override
	public void errorHighLighting(int begin, String str) {
	}

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
	public StackPane getCodeAreaPane() {
		if( codeAreaPane == null) { 
			return getCodeAreaPane(null, true);
		}else {
			return codeAreaPane;
		}
	}

	@Override
	public StackPane getCodeAreaPane(String text, boolean editable) {
		if( codeAreaPane == null) { 
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
}
