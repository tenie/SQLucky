package net.tenie.Sqlucky.sdk.component.editor;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.sql.SqlUtils;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

import java.util.ArrayList;
import java.util.List;

public class HighLightingEditorContextMenu extends ContextMenu {
	private static  List<MenuItem> itemList = new ArrayList<>();
	private static boolean isInit = false;
	public static List<MenuItem> extensionMenuItem( List<MenuItem> itemListVal){

		itemList.addAll(itemListVal);

		return  itemList;
	}
	private static HighLightingEditorContextMenu menu = null;
	public static HighLightingEditorContextMenu getHighLightingEditorContextMenu(){
		if(menu == null){
			menu = new HighLightingEditorContextMenu();
		}

		return menu;
	}


	private HighLightingEditorContextMenu() {
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
//			SqlEditor.deleteSelectionText()
			SqluckyEditor sqluckyEditor =MyEditorSheetHelper.getSqluckyEditor();

			 sqluckyEditor.delLineOrSelectTxt();
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

		MenuItem cleanEmptyLine = new MenuItem("Clean Empty Line");
		cleanEmptyLine.setOnAction(e -> {
			SqlUtils.cleanEmptyLine();
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


		// 收藏 字符串, 作为自动补全
		MenuItem favoritesStr = new MenuItem("Favorites Select Text For Auto Complete");// (ctrl+shift+K)
		favoritesStr.setGraphic(IconGenerator.svgImageDefActive("star"));
		favoritesStr.setOnAction(e -> {
//			codeArea.delAnchorAfterString();
			String str = MyEditorSheetHelper.getCurrentCodeAreaSQLSelectedText();
			ComponentGetter.appComponent.saveAutoCompleteText(str);
			MyAutoComplete.addKeyWords(str);
		});

		// 菜单
		this.getItems().addAll(copy, Paste, del, cut, new SeparatorMenuItem(), sqlFormat, formatAll, sqlUnformat,
				unformatAll, cleanEmptyLine, new SeparatorMenuItem(),
				find, replace, new SeparatorMenuItem(), favoritesStr ,new SeparatorMenuItem());




		// 菜单显示的时刻
		this.setOnShowing(e -> {
			if( ! isInit  &&  itemList.size() > 0){
				if(! this.getItems().contains(itemList.getFirst()) ){
					this.getItems().addAll(itemList);
					isInit = true;
				}
			}
			String str = MyEditorSheetHelper.getCurrentCodeAreaSQLSelectedText();
			if (StrUtils.isNotNullOrEmpty(str)) {
				copy.setDisable(false);
//				del.setDisable(false);
				cut.setDisable(false);
				sqlFormat.setDisable(false);
				formatAll.setDisable(true);
				sqlUnformat.setDisable(false);
				unformatAll.setDisable(true);
				cleanEmptyLine.setDisable(false);
				favoritesStr.setDisable(false);

			} else {
				copy.setDisable(true);
//				del.setDisable(true);
				cut.setDisable(true);
				sqlFormat.setDisable(true);
				formatAll.setDisable(false);
				sqlUnformat.setDisable(true);
				unformatAll.setDisable(false);
				cleanEmptyLine.setDisable(true);
				favoritesStr.setDisable(true);
			}

			boolean hasVal = CommonUtils.clipboardHasString();
			if (hasVal) {
				Paste.setDisable(false);
			} else {
				Paste.setDisable(true);
			}

		});
	}

	// 光标相关的按钮
	private Menu cursorBtn(){

//		cursor
		Menu cursorMenu = new Menu("Cursor");
		MenuItem mvB = new MenuItem("Move to begin of line"); // (ctrl+shift+A)
		mvB.setGraphic(IconGenerator.svgImageDefActive("step-backward"));
		mvB.setOnAction(e -> {
			SqluckyEditor sqluckyEditor = MyEditorSheetHelper.getSqluckyEditor();
			sqluckyEditor.moveAnchorToLineBegin();
		});

		MenuItem mvE = new MenuItem("Move to end of line"); // (ctrl+shift+E)
		mvE.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
		mvE.setOnAction(e -> {
			SqluckyEditor sqluckyEditor = MyEditorSheetHelper.getSqluckyEditor();
			sqluckyEditor.moveAnchorToLineEnd();
		});

		MenuItem selectLine = new MenuItem("Select Line"); // (ctrl+shift+E)
//		selectLine.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
		selectLine.setOnAction(e -> {
			MyEditorSheetHelper.selectCurrentLineTrimText();

		});

		cursorMenu.getItems().addAll(mvB, mvE, selectLine);
		return cursorMenu;
	}

	// 行编辑相关按钮
	private Menu editLineBtn (){

		Menu enditLine = new Menu("Edit Line");
		MenuItem delWord = new MenuItem("Delete the word before the cursor"); // (ctrl+shift+W)
		delWord.setOnAction(e -> {
			SqluckyEditor sqluckyEditor = MyEditorSheetHelper.getSqluckyEditor();
			sqluckyEditor.delAnchorBeforeWord();
		});

		MenuItem delChar = new MenuItem("Delete the character before the cursor"); // (ctrl+shift+H)
		delChar.setOnAction(e -> {
			SqluckyEditor sqluckyEditor = MyEditorSheetHelper.getSqluckyEditor();
			sqluckyEditor.delAnchorBeforeChar();
		});

		MenuItem delAllChar = new MenuItem("Delete all characters before the cursor");// (ctrl+shift+U)
		delAllChar.setOnAction(e -> {
			SqluckyEditor sqluckyEditor = MyEditorSheetHelper.getSqluckyEditor();
			sqluckyEditor.delAnchorBeforeString();
		});

		MenuItem delWordBackward = new MenuItem("Delete the word after the cursor");// (alt+shift+D)
		delWordBackward.setOnAction(e -> {
			SqluckyEditor sqluckyEditor = MyEditorSheetHelper.getSqluckyEditor();
			sqluckyEditor.delAnchorAfterWord();
		});

		MenuItem delCharBackward = new MenuItem("Delete the character after the cursor");// (ctrl+shift+D)
		delCharBackward.setOnAction(e -> {
			SqluckyEditor sqluckyEditor = MyEditorSheetHelper.getSqluckyEditor();
			sqluckyEditor.delAnchorAfterChar();
		});
		MenuItem delAllCharBackward = new MenuItem("Delete all characters after the cursor");// (ctrl+shift+K)
		delAllCharBackward.setOnAction(e -> {
			SqluckyEditor sqluckyEditor = MyEditorSheetHelper.getSqluckyEditor();
			sqluckyEditor.delAnchorAfterString();
		});
		enditLine.getItems().addAll(delWord, delChar, delAllChar, delWordBackward, delCharBackward, delAllCharBackward);
		return enditLine;
	}
}
