package net.tenie.fx.component.container;

import SQLucky.app;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheetAction;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.KeyBindingCache;
import net.tenie.Sqlucky.sdk.sql.SqlUtils;
import net.tenie.Sqlucky.sdk.subwindow.DialogTools;
import net.tenie.Sqlucky.sdk.subwindow.ImportCsvWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportExcelWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportSQLWindow;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.plugin.PluginManageWindow;
import net.tenie.fx.window.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 顶部菜单栏
 * 
 * @author tenie
 *
 */
public class MenuBarContainer extends MenuBar{
	private static Logger logger = LogManager.getLogger(MenuBarContainer.class);
	private Menu mnfile;
	private Menu mnEdit;
	private Menu mnTools;
	private Menu mnplugin;

	private Menu mnHelp;
	private DataTransferWindow dtw;

	public MenuBarContainer() {
		super();
		mnfile = createFileMenu();
		mnEdit = createEditMenu();
		mnTools = createToolsMenu();
		mnHelp = createHelpMenu();
		mnplugin = createPluginMenu();

		this.getMenus().addAll(mnfile, mnEdit, mnTools, mnplugin, mnHelp);
		this.setUseSystemMenuBar(true);
	}

	// File 菜单创建
	Menu createFileMenu() {
		Menu mn = new Menu("File");

		MenuItem open = new MenuItem(StrUtils.MenuItemNameFormat("Open"));
		open.setGraphic(IconGenerator.svgImageDefActive("folder-open"));
		open.setOnAction(value -> {
			AppCommonAction.openSqlFile();
		});
		KeyBindingCache.menuItemBinding(open);

		MenuItem Save = new MenuItem(StrUtils.MenuItemNameFormat("Save"));
		Save.setGraphic(IconGenerator.svgImageDefActive("floppy-o"));
		Save.setOnAction(value -> {
			// 保存sql文本到硬盘
			MyEditorSheetHelper.saveSqlToFileAction();
		});

		KeyBindingCache.menuItemBinding(Save);

		MenuItem exit = new MenuItem(StrUtils.MenuItemNameFormat("Exit"));
		exit.setGraphic(IconGenerator.svgImageDefActive("power-off"));
		exit.setOnAction((ActionEvent t) -> {
			app.saveApplicationStatusInfo();
		});
		KeyBindingCache.menuItemBinding(exit);

		mn.getItems().addAll(open, Save, new SeparatorMenuItem(), exit);
		return mn;
	}

	Menu createEditMenu() {
		Menu mn = new Menu("Edit");

		MenuItem runMenu = new MenuItem(StrUtils.MenuItemNameFormat("Run SQL"));
		runMenu.setGraphic(IconGenerator.svgImageDefActive("play"));
		runMenu.setOnAction(value -> {
			RunSQLHelper.runAction();
		});
		MenuItem runCurrentMenu = new MenuItem(StrUtils.MenuItemNameFormat("Run SQL Current Line"));
		runCurrentMenu.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
		runCurrentMenu.setOnAction(value -> {
			RunSQLHelper.runActionCurrentLine();
		});

		MenuItem codeAutocompletionMenu = new MenuItem(StrUtils.MenuItemNameFormat("Code Autocompletion"));
		codeAutocompletionMenu.setOnAction(value -> {
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().callPopup(null);
		});

		MenuItem nce = new MenuItem(StrUtils.MenuItemNameFormat("Add New Edit Page"));
		nce.setGraphic(IconGenerator.svgImageDefActive("plus-square"));
		nce.setOnAction(value -> {
			MyEditorSheetHelper.addEmptyHighLightingEditor();
		});

		MenuItem previousTab = new MenuItem(StrUtils.MenuItemNameFormat("Previous  Edit Page"));
		previousTab.setGraphic(IconGenerator.svgImageDefActive("plus-square"));
		previousTab.setOnAction(value -> {
			MyEditorSheetHelper.addEmptyHighLightingEditor();
		});
		MenuItem closeEditPage = new MenuItem(StrUtils.MenuItemNameFormat("Close Edit Page"));
		closeEditPage.setOnAction(value -> {
			AppCommonAction.closeEditPage();
		});


		MenuItem cce = new MenuItem(StrUtils.MenuItemNameFormat("Close Data Table Page"));
		cce.setOnAction(value -> {
			AppCommonAction.closeDataTable();
		});

		MenuItem Find = new MenuItem(StrUtils.MenuItemNameFormat("Find"));
		Find.setGraphic(IconGenerator.svgImageDefActive("search"));
		Find.setOnAction(value -> {
			CommonUtils.showFind(false, "");
		});

		MenuItem FindReplace = new MenuItem(StrUtils.MenuItemNameFormat("Replace"));
		FindReplace.setOnAction(value -> {
			CommonUtils.showFind(true, "");
		});

		MenuItem Format = new MenuItem(StrUtils.MenuItemNameFormat("Format"));
		Format.setGraphic(IconGenerator.svgImageDefActive("paragraph"));
		Format.setOnAction(value -> {
			SqlUtils.formatSqlText();
		});
		MenuItem unFormat = new MenuItem(StrUtils.MenuItemNameFormat("unformat"));
		unFormat.setOnAction(value -> {
			SqlUtils.pressSqlText();
		});

		MenuItem commentCode = new MenuItem(StrUtils.MenuItemNameFormat("Line Comment"));
		commentCode.setOnAction(value -> {
			AppCommonAction.addAnnotationSqlTextSelectText();
		});

		// 大写
		MenuItem UpperCase = new MenuItem(StrUtils.MenuItemNameFormat("Upper Case"));
		UpperCase.setOnAction(value -> {
			AppCommonAction.UpperCaseSQLTextSelectText();
		});

		MenuItem LowerCase = new MenuItem(StrUtils.MenuItemNameFormat("Lower Case"));
		LowerCase.setOnAction(value -> {
			AppCommonAction.lowerCaseSQLTextSelectText();
		});

		MenuItem underscore = new MenuItem(StrUtils.MenuItemNameFormat("Underline To Hump"));
		underscore.setOnAction(value -> {
			Platform.runLater(()->{
				AppCommonAction.underlineCaseCamel();
			});

		});

		MenuItem Hump = new MenuItem(StrUtils.MenuItemNameFormat("Hump To Underline"));
		Hump.setOnAction(value -> {
			Platform.runLater(AppCommonAction::camelCaseUnderline);
		});

		Menu cursorMenu = new Menu("Cursor");
		MenuItem mvB = new MenuItem(StrUtils.MenuItemNameFormat("Move to begin of line"));
		mvB.setGraphic(IconGenerator.svgImageDefActive("step-backward"));
		mvB.setOnAction(e -> {
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().moveAnchorToLineBegin();
		});

		MenuItem mvE = new MenuItem(StrUtils.MenuItemNameFormat("Move to end of line"));
		mvE.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
		mvE.setOnAction(e -> {
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().moveAnchorToLineEnd();
		});
		MenuItem selectLine = new MenuItem("Select Line Text");
		selectLine.setOnAction(e -> {
			MyEditorSheetHelper.selectCurrentLineTrimText();

		});
		KeyBindingCache.menuItemBinding(selectLine);

		cursorMenu.getItems().addAll(mvB, mvE, selectLine);

		Menu enditLine = new Menu("Edit Line");
		MenuItem delWord = new MenuItem(StrUtils.MenuItemNameFormat("Delete the word before the cursor"));
		delWord.setOnAction(e -> {
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorBeforeWord();
		});

		MenuItem delChar = new MenuItem(StrUtils.MenuItemNameFormat("Delete the character before the cursor"));
		delChar.setOnAction(e -> {
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorBeforeChar();
		});

		MenuItem delAllChar = new MenuItem(StrUtils.MenuItemNameFormat("Delete all characters before the cursor"));
		delAllChar.setOnAction(e -> {
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorBeforeString();
		});

		MenuItem delWordBackward = new MenuItem(StrUtils.MenuItemNameFormat("Delete the word after the cursor"));
		delWordBackward.setOnAction(e -> {
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorAfterWord();
		});

		MenuItem delCharBackward = new MenuItem(StrUtils.MenuItemNameFormat("Delete the character after the cursor"));
		delCharBackward.setOnAction(e -> {
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorAfterChar();
		});
		MenuItem delAllCharBackward = new MenuItem(
				StrUtils.MenuItemNameFormat("Delete all characters after the cursor"));
		delAllCharBackward.setOnAction(e -> {
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorAfterString();
		});
		enditLine.getItems().addAll(delWord, delChar, delAllChar, delWordBackward, delCharBackward, delAllCharBackward);

		// 导航, 后退/前进, Tab
		MenuItem back = new MenuItem(StrUtils.MenuItemNameFormat("Back"));
		back.setOnAction(value -> {
			var stack = MyEditorSheet.backMyEditorSheet;
			if(!stack.isEmpty()){
				MyEditorSheet sheet = stack.pop();
				// 设置back的全局状态
				MyEditorSheet.isBack = true;
				sheet.existTabShow();
			}
		});

		MenuItem forward = new MenuItem(StrUtils.MenuItemNameFormat("Forward"));
		forward.setOnAction(value -> {
			var stack = MyEditorSheet.forwardMyEditorSheet;
			if(!stack.isEmpty()){
				MyEditorSheet sheet = stack.pop();
				MyEditorSheet.isForward = true;
				sheet.existTabShow();
			}
		});
		mn.getItems().addAll(runMenu,runCurrentMenu , codeAutocompletionMenu, nce, closeEditPage, cce,
							new SeparatorMenuItem(),
							Find, FindReplace,
							new SeparatorMenuItem(),
							Format, unFormat, commentCode,
							new SeparatorMenuItem(), UpperCase,	LowerCase, underscore, Hump,
							new SeparatorMenuItem(), cursorMenu, enditLine,
							new SeparatorMenuItem(), back , forward);

		// 给菜单按钮绑定快捷
		KeyBindingCache.menuItemBinding(runMenu);
		KeyBindingCache.menuItemBinding(runCurrentMenu);
		KeyBindingCache.menuItemBinding(codeAutocompletionMenu);

		KeyBindingCache.menuItemBinding(nce);
		KeyBindingCache.menuItemBinding(closeEditPage);
		KeyBindingCache.menuItemBinding(cce);
		KeyBindingCache.menuItemBinding(Find);
		KeyBindingCache.menuItemBinding(FindReplace);
		KeyBindingCache.menuItemBinding(Format);
		KeyBindingCache.menuItemBinding(unFormat);
		KeyBindingCache.menuItemBinding(commentCode);
		KeyBindingCache.menuItemBinding(UpperCase);
		KeyBindingCache.menuItemBinding(LowerCase);
		KeyBindingCache.menuItemBinding(underscore);
		KeyBindingCache.menuItemBinding(Hump);
		KeyBindingCache.menuItemBinding(back);
		KeyBindingCache.menuItemBinding(forward);


		KeyBindingCache.allMenuItemBinding(delWord, delChar, delAllChar, delWordBackward, delCharBackward,
				delAllCharBackward);
		KeyBindingCache.allMenuItemBinding(mvB, mvE);

		return mn;
	}

	Menu createToolsMenu() {
		Menu mn = new Menu("Tools");
		// 数据迁移
		MenuItem dataTransfer = new MenuItem(StrUtils.MenuItemNameFormat("Data Transfer"));
		dataTransfer.setGraphic(IconGenerator.svgImageDefActive("mfglabs-random"));
		dataTransfer.setOnAction(value -> {
			if (dtw == null) {
				dtw = new DataTransferWindow();
			}
			dtw.show();

		});

		// 导入数据
		Menu importData = new Menu(StrUtils.MenuItemNameFormat("Import Data"));
		importData.setGraphic(IconGenerator.svgImageDefActive("bootstrap-save-file"));

		MenuItem importExcelFile = new MenuItem(StrUtils.MenuItemNameFormat("Import Excel"));
		importExcelFile.setGraphic(IconGenerator.svgImageDefActive("EXCEL"));
		importExcelFile.setOnAction(value -> {
			ImportExcelWindow.showWindow("", "");
		});

		MenuItem importCsv = new MenuItem(StrUtils.MenuItemNameFormat("Import CSV"));
		importCsv.setGraphic(IconGenerator.svgImageDefActive("CSV"));
		importCsv.setOnAction(value -> {
			ImportCsvWindow.showWindow("", "");
		});

		MenuItem importSqlFile = new MenuItem(StrUtils.MenuItemNameFormat("Import Sql File"));
		importSqlFile.setGraphic(IconGenerator.svgImageDefActive("SQL"));
		importSqlFile.setOnAction(value -> {
			ImportSQLWindow.showWindow("", "");
		});
		importData.getItems().addAll(importExcelFile, importCsv, importSqlFile);

		MenuItem addDB = new MenuItem(StrUtils.MenuItemNameFormat("Add New DB Connection"));
		addDB.setOnAction(value -> {
			new ConnectionEditor();
		});
		addDB.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));

		MenuItem editConn = new MenuItem(StrUtils.MenuItemNameFormat("Edit DB Connection"));
		editConn.setOnAction(value -> {
			ConnectionEditor.editDbConn();
		});
		editConn.setGraphic(IconGenerator.svgImageDefActive("edit"));

		MenuItem openConn = new MenuItem(StrUtils.MenuItemNameFormat("Open DB Connection"));
		openConn.setOnAction(value -> {
			DBinfoTree.openDbConn();
		});
		openConn.setGraphic(IconGenerator.svgImageDefActive("link"));

		MenuItem closeConn = new MenuItem(StrUtils.MenuItemNameFormat("Close DB Connection"));
		closeConn.setOnAction(value -> {
			ConnectionEditor.closeDbConn();
		});
		closeConn.setGraphic(IconGenerator.svgImageDefActive("unlink"));

		MenuItem closeALlConn = new MenuItem(StrUtils.MenuItemNameFormat("Close All DB Connections"));
		closeALlConn.setOnAction(value -> {
			ConnectionEditor.closeAllDbConn();
		});
		closeALlConn.setGraphic(IconGenerator.svgImageDefActive("power-off"));

		MenuItem deleteConn = new MenuItem(StrUtils.MenuItemNameFormat("Delete DB Connection"));
		deleteConn.setOnAction(value -> {
			ConnectionEditor.deleteDbConn();
		});
		deleteConn.setGraphic(IconGenerator.svgImageDefActive("trash"));

		MenuItem hideLeft = new MenuItem(StrUtils.MenuItemNameFormat("Hide/Show DB Info Panel"));
		hideLeft.setOnAction(value -> {
			AppCommonAction.hideLeft();
		});

		MenuItem hideBottom = new MenuItem(StrUtils.MenuItemNameFormat("Hide/Show Data View Panel"));
		hideBottom.setOnAction(value -> {
			SdkComponent.hideBottom();
		});

		MenuItem hideLeftBottom = new MenuItem(StrUtils.MenuItemNameFormat("Hide/Show All Panels"));
		hideLeftBottom.setGraphic(IconGenerator.svgImageDefActive("arrows-alt"));
		hideLeftBottom.setOnAction(value -> {
			AppCommonAction.hideLeftBottom();
		});

		// 主题变化
		Menu Theme = new Menu(StrUtils.MenuItemNameFormat("Theme"));
		Theme.setGraphic(IconGenerator.svgImageDefActive("icomoon-contrast"));

		MenuItem themeDark = new MenuItem(StrUtils.MenuItemNameFormat("Dark"));
		themeDark.setGraphic(IconGenerator.svgImageDefActive("moon"));
		themeDark.setOnAction(value -> {
			AppCommonAction.setThemeRestart(CommonConst.THEME_DARK);

		});

		MenuItem themeLight = new MenuItem(StrUtils.MenuItemNameFormat("Light"));
		themeLight.setGraphic(IconGenerator.svgImageDefActive("sun"));
		themeLight.setOnAction(value -> {
			AppCommonAction.setThemeRestart(CommonConst.THEME_LIGHT);
		});

		MenuItem themeYellow = new MenuItem(StrUtils.MenuItemNameFormat("Yellow"));
		themeYellow.setGraphic(IconGenerator.svgImageDefActive("adjust"));
		themeYellow.setOnAction(value -> {
			AppCommonAction.setThemeRestart(CommonConst.THEME_YELLOW);
		});

		Theme.getItems().addAll(themeDark, themeLight, themeYellow);

		// 字体大小
		Menu fontSize = new Menu(StrUtils.MenuItemNameFormat("Font Size"));
		fontSize.setGraphic(IconGenerator.svgImageDefActive("text-height"));

		MenuItem fontSizePlus = new MenuItem(StrUtils.MenuItemNameFormat("Font Size +"));
		fontSizePlus.setGraphic(IconGenerator.svgImageDefActive("plus-circle"));
		fontSizePlus.setOnAction(value -> {
			AppCommonAction.changeFontSize(true);
		});

		MenuItem fontSizeMinus = new MenuItem(StrUtils.MenuItemNameFormat("Font Size -"));
		fontSizeMinus.setGraphic(IconGenerator.svgImageDefActive("minus-circle"));
		fontSizeMinus.setOnAction(value -> {
			AppCommonAction.changeFontSize(false);
		});

		fontSize.getItems().addAll(fontSizePlus, fontSizeMinus);

		// 快捷键设置
		MenuItem keysBind = new MenuItem(StrUtils.MenuItemNameFormat("Keys Binding"));
		keysBind.setGraphic(IconGenerator.svgImageDefActive("keyboard-o"));
		keysBind.setOnAction(value -> {
			KeysBindWindow kbw = new KeysBindWindow();
			kbw.show();

		});
		MenuItem dockSideTabPane = new MenuItem(StrUtils.MenuItemNameFormat("Separate Date Window"));
		dockSideTabPane.setOnAction(value -> {
			MyBottomSheetAction.dockSideTabPane();
		});

		mn.getItems().addAll(dataTransfer, importData, new SeparatorMenuItem(), addDB, editConn, openConn, closeConn,
				closeALlConn, deleteConn, new SeparatorMenuItem(), hideLeft, hideBottom, hideLeftBottom,
				new SeparatorMenuItem(), Theme, new SeparatorMenuItem(), fontSize, keysBind, dockSideTabPane);

		KeyBindingCache.menuItemBinding(fontSizeMinus);
		KeyBindingCache.menuItemBinding(fontSizePlus);
		KeyBindingCache.menuItemBinding(hideLeftBottom);
		KeyBindingCache.menuItemBinding(hideBottom);
		KeyBindingCache.menuItemBinding(hideLeft);
		KeyBindingCache.menuItemBinding(dockSideTabPane);
		return mn;
	}

	Menu createHelpMenu() {
		Menu mn = new Menu("Help");
		MenuItem about = new MenuItem(StrUtils.MenuItemNameFormat("About"));
		about.setGraphic(IconGenerator.svgImageDefActive("info-circle"));
		about.setOnAction(value -> {
			DialogTools.showAbout();
		});

		MenuItem SignInMenuItem = new MenuItem(StrUtils.MenuItemNameFormat("Sign In"));
		SignInMenuItem.setGraphic(IconGenerator.svgImageDefActive("sign-in"));
		SignInMenuItem.setOnAction(value -> {
			SignInWindow.show("");
		});
// 		注册
//		MenuItem SignUpMenuItem = new MenuItem(StrUtils.MenuItemNameFormat("Sign Up"));
//		SignUpMenuItem.setGraphic(IconGenerator.svgImageDefActive("windows-clipboard-variant-edit"));
//		SignUpMenuItem.setOnAction(value -> {
//			CommonUtils.OpenURLInBrowser("https://app.sqlucky.com/");
//		});

		MenuItem checkForUpdates = new MenuItem(StrUtils.MenuItemNameFormat("Check For Updates"));
		var svg = IconGenerator.sqluckyLogoSVG();
		checkForUpdates.setGraphic(svg);
		checkForUpdates.setOnAction(value -> {
			CheckUpdateWindow.show("");
		});

		mn.getItems().addAll(SignInMenuItem,  checkForUpdates, new SeparatorMenuItem(), about);
		return mn;
	}

	Menu createPluginMenu() {
		Menu mn = new Menu("Plugin");

		MenuItem plugin = new MenuItem(StrUtils.MenuItemNameFormat("Plugin Manage "));
		plugin.setGraphic(IconGenerator.svgImageDefActive("metro-power-cord"));

		plugin.setOnAction(value -> {
			PluginManageWindow pw = new PluginManageWindow();
			pw.show();
		});
		mn.getItems().addAll(plugin);
		ComponentGetter.pluginMenu = mn;
		return mn;
	}

	public Menu getMnfile() {
		return mnfile;
	}

	public void setMnfile(Menu mnfile) {
		this.mnfile = mnfile;
	}

}
