package net.tenie.Sqlucky.sdk.component;

import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.editor.FindReplaceTextBox;
import net.tenie.Sqlucky.sdk.component.editor.MyLineNumberNode;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.ui.CodeAreaHighLightingHelper;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 普通文本编辑组件
 *
 * @author tenie
 *
 */
public class MyTextEditor extends SqluckyEditor {
	private static Logger logger = LogManager.getLogger(MyTextEditor.class);
	private MyCodeArea codeArea;
	private CodeAreaHighLightingHelper highLightingHelper ;

	private VBox fdbox;

	private FindReplaceTextBox findReplaceTextBox;


	public MyTextEditor() {
		codeArea = new MyCodeArea(this);
		this.init(codeArea);
		this.getChildren().add(codeArea);
		VBox.setVgrow(codeArea, Priority.ALWAYS);
		this.getStyleClass().add("my-tag");
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
		codeArea.getMylineNumber().nextBookmark(tf);
	}

	//	@Override
	public void highLighting(int begin) {
		Platform.runLater(() -> {
			try {
				if(highLightingHelper == null) {
					highLightingHelper	= new CodeAreaHighLightingHelper();
				}

				highLightingHelper.applyHighlighting(codeArea, begin);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}
	@Override
	public void  highLighting() {
		highLighting(0);
	}
	@Override
	public void highLighting(String str) {
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

}
