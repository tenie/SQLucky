package net.tenie.Sqlucky.sdk.component.editor;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.sql.SqlUtils;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class HighLightingEditorContextMenu extends ContextMenu {

	public HighLightingEditorContextMenu(SqluckyEditor codeArea) {
		super();
		this.setPrefWidth(200);
		MenuItem copy = new MenuItem("Copy");
		copy.setGraphic(IconGenerator.svgImageDefActive("files-o"));
		copy.setOnAction(e -> {
			MyEditorSheetHelper.copySelectionText();
		});

		MenuItem Paste = new MenuItem("Paste");
		Paste.setGraphic(IconGenerator.svgImageDefActive("clipboard"));
		Paste.setOnAction(e -> {
			MyEditorSheetHelper.pasteTextToCodeArea();
		});

		MenuItem del = new MenuItem("Delete");
		del.setOnAction(e -> {
//			SqlEditor.deleteSelectionText(); 
			codeArea.delLineOrSelectTxt();
		});
		del.setGraphic(IconGenerator.svgImageDefActive("eraser"));

		MenuItem cut = new MenuItem("Cut");
		cut.setOnAction(e -> {
			MyEditorSheetHelper.cutSelectionText();
		});
		cut.setGraphic(IconGenerator.svgImageDefActive("scissors"));

		MenuItem sqlFormat = new MenuItem("Format  SQL");
		sqlFormat.setGraphic(IconGenerator.svgImageDefActive("paragraph"));
		sqlFormat.setOnAction(e -> {
			SqlUtils.formatSqlText();
		});

		MenuItem formatAll = new MenuItem("Format All SQL");
		formatAll.setOnAction(e -> {
			SqlUtils.formatSqlText();
		});

		MenuItem sqlUnformat = new MenuItem("Unformat SQL");
		sqlUnformat.setOnAction(e -> {
			SqlUtils.pressSqlText();
		});

		MenuItem unformatAll = new MenuItem("Unformat All SQL");
		unformatAll.setOnAction(e -> {
			SqlUtils.pressSqlText();
		});

		MenuItem myBatisXml	 = new MenuItem("Use Xml Conversion Element");
		myBatisXml.setOnAction(e -> {
			SqlUtils.myBatisXmlSql();
		});
		MenuItem trimMyBatisXml	 = new MenuItem("Trim Xml Conversion Element ");
		trimMyBatisXml.setOnAction(e -> {
			SqlUtils.trimMyBatisXml();
		});


		MenuItem find = new MenuItem("Find");
		find.setGraphic(IconGenerator.svgImageDefActive("search"));
		find.setOnAction(e -> {
//			CommonUtils.findReplace(false);
			CommonUtils.showFind(false, "");
		});

		MenuItem replace = new MenuItem("Replace");
		replace.setOnAction(e -> {
//			CommonUtils.findReplace(true);
			CommonUtils.showFind(true, "");
		});

//		cursor
		Menu cursorMenu = new Menu("Cursor");
		MenuItem mvB = new MenuItem("Move to begin of line"); // (ctrl+shift+A)
		mvB.setGraphic(IconGenerator.svgImageDefActive("step-backward"));
		mvB.setOnAction(e -> {
			codeArea.moveAnchorToLineBegin();
		});

		MenuItem mvE = new MenuItem("Move to end of line"); // (ctrl+shift+E)
		mvE.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
		mvE.setOnAction(e -> {
			codeArea.moveAnchorToLineEnd();
		});

		MenuItem selectLine = new MenuItem("Select Line"); // (ctrl+shift+E)
//		selectLine.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
		selectLine.setOnAction(e -> {
			MyEditorSheetHelper.selectCurrentLineTrimText();

		});

		cursorMenu.getItems().addAll(mvB, mvE, selectLine);

		Menu enditLine = new Menu("Edit Line");
		MenuItem delWord = new MenuItem("Delete the word before the cursor"); // (ctrl+shift+W)
		delWord.setOnAction(e -> {
			codeArea.delAnchorBeforeWord();
		});

		MenuItem delChar = new MenuItem("Delete the character before the cursor"); // (ctrl+shift+H)
		delChar.setOnAction(e -> {
			codeArea.delAnchorBeforeChar();
		});

		MenuItem delAllChar = new MenuItem("Delete all characters before the cursor");// (ctrl+shift+U)
		delAllChar.setOnAction(e -> {
			codeArea.delAnchorBeforeString();
		});

		MenuItem delWordBackward = new MenuItem("Delete the word after the cursor");// (alt+shift+D)
		delWordBackward.setOnAction(e -> {
			codeArea.delAnchorAfterWord();
		});

		MenuItem delCharBackward = new MenuItem("Delete the character after the cursor");// (ctrl+shift+D)
		delCharBackward.setOnAction(e -> {
			codeArea.delAnchorAfterChar();
		});
		MenuItem delAllCharBackward = new MenuItem("Delete all characters after the cursor");// (ctrl+shift+K)
		delAllCharBackward.setOnAction(e -> {
			codeArea.delAnchorAfterString();
		});

		this.getItems().addAll(copy, Paste, del, cut, new SeparatorMenuItem(), sqlFormat, formatAll, sqlUnformat,
				unformatAll, new SeparatorMenuItem(), myBatisXml, trimMyBatisXml, new SeparatorMenuItem(), find, replace, new SeparatorMenuItem(), cursorMenu, enditLine);
		enditLine.getItems().addAll(delWord, delChar, delAllChar, delWordBackward, delCharBackward, delAllCharBackward);

		// 菜单显示的时刻
		this.setOnShowing(e -> {
			String str = MyEditorSheetHelper.getCurrentCodeAreaSQLSelectedText();
			if (StrUtils.isNotNullOrEmpty(str)) {
				copy.setDisable(false);
//				del.setDisable(false);
				cut.setDisable(false);
				sqlFormat.setDisable(false);
				formatAll.setDisable(true);
				sqlUnformat.setDisable(false);
				unformatAll.setDisable(true);
				myBatisXml.setDisable(false);
				trimMyBatisXml.setDisable(false);

			} else {
				copy.setDisable(true);
//				del.setDisable(true);
				cut.setDisable(true);
				sqlFormat.setDisable(true);
				formatAll.setDisable(false);
				sqlUnformat.setDisable(true);
				unformatAll.setDisable(false);
				myBatisXml.setDisable(true);
				trimMyBatisXml.setDisable(true);
			}

			boolean hasVal = CommonUtils.clipboardHasString();
			if (hasVal) {
				Paste.setDisable(false);
			} else {
				Paste.setDisable(true);
			}

		});
	}

}
