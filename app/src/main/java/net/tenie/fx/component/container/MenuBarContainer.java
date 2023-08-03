package net.tenie.fx.component.container;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.KeyBindingCache;
import net.tenie.Sqlucky.sdk.subwindow.ImportCsvWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportExcelWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportSQLWindow;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.plugin.PluginManageWindow;
import net.tenie.fx.window.CheckUpdateWindow;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.DataTransferWindow;
import net.tenie.fx.window.KeysBindWindow;
import net.tenie.fx.window.SignInWindow;

/**
 * 顶部菜单栏
 * 
 * @author tenie
 *
 */
public class MenuBarContainer {
	private static Logger logger = LogManager.getLogger(MenuBarContainer.class);
	private MenuBar mainMenuBar;
	private Menu mnfile;
	private Menu mnEdit;
	private Menu mnTools;
	private Menu mnplugin;

	private Menu mnHelp;
	private DataTransferWindow dtw;

	public MenuBarContainer() {
		mainMenuBar = new MenuBar();
		mnfile = createFileMenu();
		mnEdit = createEditMenu();
		mnTools = createToolsMenu();
		mnHelp = createHelpMenu();
		mnplugin = createPluginMenu();

		mainMenuBar.getMenus().addAll(mnfile, mnEdit, mnTools, mnplugin, mnHelp);
		mainMenuBar.setUseSystemMenuBar(true);
	}

	// File 菜单创建
	Menu createFileMenu() {
		Menu mn = new Menu("File");

		MenuItem open = new MenuItem(StrUtils.MenuItemNameFormat("Open"));
		open.setGraphic(IconGenerator.svgImageDefActive("folder-open"));
		open.setOnAction(value -> {
			CommonAction.openSqlFile();
		});
		KeyBindingCache.menuItemBinding(open);

//		Menu openEncoding = new Menu(StrUtils.MenuItemNameFormat("Open With Encoding "));
//		openEncoding.setGraphic(IconGenerator.svgImageDefActive("folder-open")); 

//		MenuItem openGBK = new MenuItem(StrUtils.MenuItemNameFormat("GBK"));
//		openGBK.setGraphic(IconGenerator.svgImageDefActive("folder-open")); 
//		openGBK.setOnAction(value -> {
//			CommonAction.openSqlFile("GBK");
//		});

//		openEncoding.getItems().addAll(openGBK );

		MenuItem Save = new MenuItem(StrUtils.MenuItemNameFormat("Save"));
		Save.setGraphic(IconGenerator.svgImageDefActive("floppy-o"));
		Save.setOnAction(value -> {
			// 保存sql文本到硬盘
//			CommonAction.saveSqlAction();
			MyEditorSheetHelper.saveSqlAction();
		});

		KeyBindingCache.menuItemBinding(Save);

		MenuItem exit = new MenuItem(StrUtils.MenuItemNameFormat("Exit"));
		exit.setGraphic(IconGenerator.svgImageDefActive("power-off"));
		exit.setOnAction((ActionEvent t) -> {
			CommonAction.mainPageClose();
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
			RunSQLHelper.runSQLMethod();
		});
		MenuItem runCurrentMenu = new MenuItem(StrUtils.MenuItemNameFormat("Run SQL Current Line"));
		runCurrentMenu.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
		runCurrentMenu.setOnAction(value -> {
			RunSQLHelper.runCurrentLineSQLMethod();
		});

		MenuItem codeAutocompletionMenu = new MenuItem(StrUtils.MenuItemNameFormat("Code Autocompletion"));
		codeAutocompletionMenu.setOnAction(value -> {
//			SqluckyEditorUtils.currentMyTab().getSqlCodeArea().callPopup();
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().callPopup();
		});

		MenuItem nce = new MenuItem(StrUtils.MenuItemNameFormat("Add New Edit Page"));
		nce.setGraphic(IconGenerator.svgImageDefActive("plus-square"));
		nce.setOnAction(value -> {
//			MyAreaTab.addCodeEmptyTabMethod();
			MyEditorSheetHelper.addEmptyHighLightingEditor();
		});

		MenuItem cce = new MenuItem(StrUtils.MenuItemNameFormat("Close Data Table"));
		cce.setOnAction(value -> {
			CommonAction.closeDataTable();
		});

		MenuItem Find = new MenuItem(StrUtils.MenuItemNameFormat("Find"));
		Find.setGraphic(IconGenerator.svgImageDefActive("search"));
		Find.setOnAction(value -> {
			CommonUtils.findReplace(false);
		});

		MenuItem FindReplace = new MenuItem(StrUtils.MenuItemNameFormat("Replace"));
		FindReplace.setOnAction(value -> {
			CommonUtils.findReplace(true);
		});

		MenuItem Format = new MenuItem(StrUtils.MenuItemNameFormat("Format"));
		Format.setGraphic(IconGenerator.svgImageDefActive("paragraph"));
		Format.setOnAction(value -> {
			CommonUtils.formatSqlText();
		});

		MenuItem commentCode = new MenuItem(StrUtils.MenuItemNameFormat("Line Comment"));
		commentCode.setOnAction(value -> {
			CommonAction.addAnnotationSQLTextSelectText();
		});

		// 大写
		MenuItem UpperCase = new MenuItem(StrUtils.MenuItemNameFormat("Upper Case"));
		UpperCase.setOnAction(value -> {
			CommonAction.UpperCaseSQLTextSelectText();
		});

		MenuItem LowerCase = new MenuItem(StrUtils.MenuItemNameFormat("Lower Case"));
		LowerCase.setOnAction(value -> {
			CommonAction.LowerCaseSQLTextSelectText();
		});

		// Underscore to hump
		MenuItem underscore = new MenuItem(StrUtils.MenuItemNameFormat("Underscore To Hump"));
		underscore.setOnAction(value -> {
			CommonAction.underlineCaseCamel();
		});

		MenuItem Hump = new MenuItem(StrUtils.MenuItemNameFormat("Hump To Underscore"));
		Hump.setOnAction(value -> {
			CommonAction.CamelCaseUnderline();
		});

		Menu cursorMenu = new Menu("Cursor");
		MenuItem mvB = new MenuItem(StrUtils.MenuItemNameFormat("Move to begin of line"));
		mvB.setGraphic(IconGenerator.svgImageDefActive("step-backward"));
		mvB.setOnAction(e -> {
//			SqluckyEditorUtils.currentMyTab().getSqlCodeArea().moveAnchorToLineBegin();
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().moveAnchorToLineBegin();
		});

		MenuItem mvE = new MenuItem(StrUtils.MenuItemNameFormat("Move to end of line"));
		mvE.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
		mvE.setOnAction(e -> {
//			SqluckyEditorUtils.currentMyTab().getSqlCodeArea().moveAnchorToLineEnd();
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().moveAnchorToLineEnd();
		});

		cursorMenu.getItems().addAll(mvB, mvE);

		Menu enditLine = new Menu("Edit Line");
		MenuItem delWord = new MenuItem(StrUtils.MenuItemNameFormat("Delete the word before the cursor")); // (ctrl+shift+W)
		delWord.setOnAction(e -> {
//			SqluckyEditorUtils.currentMyTab().getSqlCodeArea().delAnchorBeforeWord();
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorBeforeWord();
		});

		MenuItem delChar = new MenuItem(StrUtils.MenuItemNameFormat("Delete the character before the cursor")); // (ctrl+shift+H)
		delChar.setOnAction(e -> {
//			SqluckyEditorUtils.currentMyTab().getSqlCodeArea().delAnchorBeforeChar();
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorBeforeChar();
		});

		MenuItem delAllChar = new MenuItem(StrUtils.MenuItemNameFormat("Delete all characters before the cursor"));// (ctrl+shift+U)
		delAllChar.setOnAction(e -> {
//			SqluckyEditorUtils.currentMyTab().getSqlCodeArea().delAnchorBeforeString();
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorBeforeString();
		});

		MenuItem delWordBackward = new MenuItem(StrUtils.MenuItemNameFormat("Delete the word after the cursor"));// (alt+shift+D)
		delWordBackward.setOnAction(e -> {
//			SqluckyEditorUtils.currentMyTab().getSqlCodeArea().delAnchorAfterWord();
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorAfterWord();
		});

		MenuItem delCharBackward = new MenuItem(StrUtils.MenuItemNameFormat("Delete the character after the cursor"));// (ctrl+shift+D)
		delCharBackward.setOnAction(e -> {
//			SqluckyEditorUtils.currentMyTab().getSqlCodeArea().delAnchorAfterChar();
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorAfterChar();
		});
		MenuItem delAllCharBackward = new MenuItem(
				StrUtils.MenuItemNameFormat("Delete all characters after the cursor"));
		delAllCharBackward.setOnAction(e -> {
//			SqluckyEditorUtils.currentMyTab().getSqlCodeArea().delAnchorAfterString();
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().delAnchorAfterString();
		});
		enditLine.getItems().addAll(delWord, delChar, delAllChar, delWordBackward, delCharBackward, delAllCharBackward);

		mn.getItems().addAll(runMenu, runCurrentMenu, codeAutocompletionMenu, nce, cce, new SeparatorMenuItem(), Find,
				FindReplace, new SeparatorMenuItem(), Format, commentCode, new SeparatorMenuItem(), UpperCase,
				LowerCase, underscore, Hump, new SeparatorMenuItem(), cursorMenu, enditLine);

		// 给菜单按钮绑定快捷
		KeyBindingCache.menuItemBinding(runMenu);
		KeyBindingCache.menuItemBinding(runCurrentMenu);
		KeyBindingCache.menuItemBinding(codeAutocompletionMenu);

		KeyBindingCache.menuItemBinding(nce);
		KeyBindingCache.menuItemBinding(cce);
		KeyBindingCache.menuItemBinding(Find);
		KeyBindingCache.menuItemBinding(FindReplace);
		KeyBindingCache.menuItemBinding(Format);
		KeyBindingCache.menuItemBinding(commentCode);
		KeyBindingCache.menuItemBinding(UpperCase);
		KeyBindingCache.menuItemBinding(LowerCase);
		KeyBindingCache.menuItemBinding(underscore);
		KeyBindingCache.menuItemBinding(Hump);

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
			ConnectionEditor.ConnectionInfoSetting();
		});
		addDB.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));

		MenuItem editConn = new MenuItem(StrUtils.MenuItemNameFormat("Edit DB Connection"));
		editConn.setOnAction(value -> {
			ConnectionEditor.editDbConn();
		});
		editConn.setGraphic(IconGenerator.svgImageDefActive("edit"));

		MenuItem openConn = new MenuItem(StrUtils.MenuItemNameFormat("Open DB Connection"));
		openConn.setOnAction(value -> {
			ConnectionEditor.openDbConn();
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
			CommonAction.hideLeft();
		});

		MenuItem hideBottom = new MenuItem(StrUtils.MenuItemNameFormat("Hide/Show Data View Panel"));
		hideBottom.setOnAction(value -> {
			SdkComponent.hideBottom();
		});

		MenuItem hideLeftBottom = new MenuItem(StrUtils.MenuItemNameFormat("Hide/Show All Panels"));
		hideLeftBottom.setGraphic(IconGenerator.svgImageDefActive("arrows-alt"));
		hideLeftBottom.setOnAction(value -> {
			CommonAction.hideLeftBottom();
		});

		// 主题变化
		Menu Theme = new Menu(StrUtils.MenuItemNameFormat("Theme"));
		Theme.setGraphic(IconGenerator.svgImageDefActive("icomoon-contrast"));

		MenuItem themeDark = new MenuItem(StrUtils.MenuItemNameFormat("Dark"));
		themeDark.setGraphic(IconGenerator.svgImageDefActive("moon"));
		themeDark.setOnAction(value -> {
			CommonAction.setThemeRestart(CommonConst.THEME_DARK);

		});

		MenuItem themeLight = new MenuItem(StrUtils.MenuItemNameFormat("Light"));
		themeLight.setGraphic(IconGenerator.svgImageDefActive("sun"));
		themeLight.setOnAction(value -> {
			CommonAction.setThemeRestart(CommonConst.THEME_LIGHT);
		});

		MenuItem themeYellow = new MenuItem(StrUtils.MenuItemNameFormat("Yellow"));
		themeYellow.setGraphic(IconGenerator.svgImageDefActive("adjust"));
		themeYellow.setOnAction(value -> {
			CommonAction.setThemeRestart(CommonConst.THEME_YELLOW);
		});

		Theme.getItems().addAll(themeDark, themeLight, themeYellow);

		// TODO 字体大小
		Menu fontSize = new Menu(StrUtils.MenuItemNameFormat("Font Size"));
		fontSize.setGraphic(IconGenerator.svgImageDefActive("text-height"));

		MenuItem fontSizePlus = new MenuItem(StrUtils.MenuItemNameFormat("Font Size +"));
		fontSizePlus.setGraphic(IconGenerator.svgImageDefActive("plus-circle"));
		fontSizePlus.setOnAction(value -> {
			CommonAction.changeFontSize(true);
		});

		MenuItem fontSizeMinus = new MenuItem(StrUtils.MenuItemNameFormat("Font Size -"));
		fontSizeMinus.setGraphic(IconGenerator.svgImageDefActive("minus-circle"));
		fontSizeMinus.setOnAction(value -> {
			CommonAction.changeFontSize(false);
		});

		fontSize.getItems().addAll(fontSizePlus, fontSizeMinus);

		// 快捷键设置
		MenuItem keysBind = new MenuItem(StrUtils.MenuItemNameFormat("Keys Binding"));
		keysBind.setGraphic(IconGenerator.svgImageDefActive("keyboard-o"));
		keysBind.setOnAction(value -> {
			KeysBindWindow kbw = new KeysBindWindow();
			kbw.show();

		});

		mn.getItems().addAll(dataTransfer, importData, new SeparatorMenuItem(), addDB, editConn, openConn, closeConn,
				closeALlConn, deleteConn, new SeparatorMenuItem(), hideLeft, hideBottom, hideLeftBottom,
				new SeparatorMenuItem(), Theme, new SeparatorMenuItem(), fontSize, keysBind);

		KeyBindingCache.menuItemBinding(fontSizeMinus);
		KeyBindingCache.menuItemBinding(fontSizePlus);
		KeyBindingCache.menuItemBinding(hideLeftBottom);

		return mn;
	}

	Menu createHelpMenu() {
		Menu mn = new Menu("Help");
		MenuItem about = new MenuItem(StrUtils.MenuItemNameFormat("About"));
		about.setGraphic(IconGenerator.svgImageDefActive("info-circle"));
		about.setOnAction(value -> {
			ModalDialog.showAbout();
		});

		MenuItem SignInMenuItem = new MenuItem(StrUtils.MenuItemNameFormat("Sign In"));
		SignInMenuItem.setGraphic(IconGenerator.svgImageDefActive("sign-in"));
		SignInMenuItem.setOnAction(value -> {
			SignInWindow.show("");
		});

		MenuItem SignUpMenuItem = new MenuItem(StrUtils.MenuItemNameFormat("Sign Up"));
		SignUpMenuItem.setGraphic(IconGenerator.svgImageDefActive("windows-clipboard-variant-edit"));
		SignUpMenuItem.setOnAction(value -> {
			CommonUtils.OpenURLInBrowser("https://app.sqlucky.com/");
		});

		MenuItem checkForUpdates = new MenuItem(StrUtils.MenuItemNameFormat("Check For Updates"));
		var svg = IconGenerator.sqluckyLogoSVG();
		checkForUpdates.setGraphic(svg);
		checkForUpdates.setOnAction(value -> {
			CheckUpdateWindow.show("");
		});

		mn.getItems().addAll(SignInMenuItem, SignUpMenuItem, checkForUpdates, new SeparatorMenuItem(), about);
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

	public MenuBar getMainMenuBar() {
		return mainMenuBar;
	}

	public void setMainMenuBar(MenuBar mainMenuBar) {
		this.mainMenuBar = mainMenuBar;
	}

	public Menu getMnfile() {
		return mnfile;
	}

	public void setMnfile(Menu mnfile) {
		this.mnfile = mnfile;
	}

}
