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

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.model.Paragraph;

/**
 * 普通文本编辑组件
 *
 * @author tenie
 *
 */
public class MyTextEditor extends SqluckyEditor {
	private static Logger logger = LogManager.getLogger(MyTextEditor.class);

	public MyTextEditor() {
		codeArea = new MyCodeArea(this);
		this.init(codeArea);
		// 行号主题色
		changeCodeAreaLineNoThemeHelper();
	}


	@Override
	public DocumentPo getDocumentPo() {
		return null;
	}

	@Override
	public void setDocumentPo(DocumentPo documentPo) {

	}

}
