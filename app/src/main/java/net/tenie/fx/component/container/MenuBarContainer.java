package net.tenie.fx.component.container;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.component.SqluckyEditor;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.KeyBindingCache;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.component.MyAreaTab;
import net.tenie.fx.plugin.PluginManageWindow;
import net.tenie.fx.window.CheckUpdateWindow;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.DataTransferWindow;
import net.tenie.fx.window.KeysBindWindow;
import net.tenie.fx.window.SignInWindow;

/*   @author tenie */
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
//		open.setAccelerator(KeyCombination.keyCombination("shortcut + u"));
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
			CommonAction.saveSqlAction();
		});

		KeyBindingCache.menuItemBinding(Save);

		MenuItem exit = new MenuItem(StrUtils.MenuItemNameFormat("Exit"));
		exit.setGraphic(IconGenerator.svgImageDefActive("power-off"));
//		exit.setAccelerator(KeyCombination.keyCombination("shortcut+Q"));
		exit.setOnAction((ActionEvent t) -> {
			CommonAction.mainPageClose();
		});
		KeyBindingCache.menuItemBinding(exit);

		mn.getItems().addAll(open,
//				openEncoding, 
				Save, new SeparatorMenuItem(), exit);
		return mn;
	}

	Menu createEditMenu() {
		Menu mn = new Menu("Edit");

		MenuItem runMenu = new MenuItem(StrUtils.MenuItemNameFormat("Run SQL"));
		runMenu.setOnAction(value -> {
			RunSQLHelper.runSQLMethod();
		});
		MenuItem runCurrentMenu = new MenuItem(StrUtils.MenuItemNameFormat("Run SQL Current Line"));
		runCurrentMenu.setOnAction(value -> {
			RunSQLHelper.runCurrentLineSQLMethod();
		});

		MenuItem codeAutocompletionMenu = new MenuItem(StrUtils.MenuItemNameFormat("Code Autocompletion"));
		codeAutocompletionMenu.setOnAction(value -> {
			SqluckyEditor.currentMyTab().getSqlCodeArea().callPopup();
		});

		MenuItem nce = new MenuItem(StrUtils.MenuItemNameFormat("Add New Edit Page"));
//		nce.setAccelerator(KeyCombination.keyCombination("shortcut+T"));
		nce.setOnAction(value -> {
			MyAreaTab.addCodeEmptyTabMethod();
		});

		MenuItem cce = new MenuItem(StrUtils.MenuItemNameFormat("Close Data Table"));
//		cce.setAccelerator(KeyCombination.keyCombination("alt+W"));
		cce.setOnAction(value -> {
			CommonAction.closeDataTable();
		});

		MenuItem Find = new MenuItem(StrUtils.MenuItemNameFormat("Find"));
		Find.setGraphic(IconGenerator.svgImageDefActive("search"));
//		Find.setAccelerator(KeyCombination.keyCombination("shortcut+F"));
		Find.setOnAction(value -> {
			CommonUtility.findReplace(false);
		});

		MenuItem FindReplace = new MenuItem(StrUtils.MenuItemNameFormat("Replace"));
//		FindReplace.setAccelerator(KeyCombination.keyCombination("shortcut+R"));
		FindReplace.setOnAction(value -> {
			CommonUtility.findReplace(true);
		});

		MenuItem Format = new MenuItem(StrUtils.MenuItemNameFormat("Format"));
//		Format.setAccelerator(KeyCombination.keyCombination("shortcut+shift+F"));
		Format.setOnAction(value -> {
			CommonAction.formatSqlText();
		});

		MenuItem commentCode = new MenuItem(StrUtils.MenuItemNameFormat("Line Comment"));
//		commentCode.setAccelerator(KeyCombination.keyCombination("shortcut+/"));
		commentCode.setOnAction(value -> {
			CommonAction.addAnnotationSQLTextSelectText();
		});

		// 大写
		MenuItem UpperCase = new MenuItem(StrUtils.MenuItemNameFormat("Upper Case"));
//		UpperCase.setAccelerator(KeyCombination.keyCombination("shortcut+shift+X"));
		UpperCase.setOnAction(value -> {
			CommonAction.UpperCaseSQLTextSelectText();
		});

		MenuItem LowerCase = new MenuItem(StrUtils.MenuItemNameFormat("Lower Case"));
//		LowerCase.setAccelerator(KeyCombination.keyCombination("shortcut+shift+Y"));
		LowerCase.setOnAction(value -> {
			CommonAction.LowerCaseSQLTextSelectText();
		});

		// Underscore to hump
		MenuItem underscore = new MenuItem(StrUtils.MenuItemNameFormat("Underscore To Hump"));
//		underscore.setAccelerator(KeyCombination.keyCombination("shortcut+shift+R"));
		underscore.setOnAction(value -> {
			CommonAction.underlineCaseCamel();
		});

		MenuItem Hump = new MenuItem(StrUtils.MenuItemNameFormat("Hump To Underscore"));
//		Hump.setAccelerator(KeyCombination.keyCombination("shortcut+shift+T"));
		Hump.setOnAction(value -> {
			CommonAction.CamelCaseUnderline();
		});

		Menu cursorMenu = new Menu("Cursor");
		MenuItem mvB = new MenuItem(StrUtils.MenuItemNameFormat("Move to begin of line")); // (ctrl+shift+A)
		mvB.setGraphic(IconGenerator.svgImageDefActive("step-backward"));
		mvB.setOnAction(e -> {
			SqluckyEditor.currentMyTab().getSqlCodeArea().moveAnchorToLineBegin();
		});

		MenuItem mvE = new MenuItem(StrUtils.MenuItemNameFormat("Move to end of line")); // (ctrl+shift+E)
		mvE.setGraphic(IconGenerator.svgImageDefActive("step-forward"));
		mvE.setOnAction(e -> {
			SqluckyEditor.currentMyTab().getSqlCodeArea().moveAnchorToLineEnd();
		});

		cursorMenu.getItems().addAll(mvB, mvE);

		Menu enditLine = new Menu("Edit Line");
		MenuItem delWord = new MenuItem(StrUtils.MenuItemNameFormat("Delete the word before the cursor")); // (ctrl+shift+W)
		delWord.setOnAction(e -> {
			SqluckyEditor.currentMyTab().getSqlCodeArea().delAnchorBeforeWord();
		});

		MenuItem delChar = new MenuItem(StrUtils.MenuItemNameFormat("Delete the character before the cursor")); // (ctrl+shift+H)
		delChar.setOnAction(e -> {
			SqluckyEditor.currentMyTab().getSqlCodeArea().delAnchorBeforeChar();
		});

		MenuItem delAllChar = new MenuItem(StrUtils.MenuItemNameFormat("Delete all characters before the cursor"));// (ctrl+shift+U)
		delAllChar.setOnAction(e -> {
			SqluckyEditor.currentMyTab().getSqlCodeArea().delAnchorBeforeString();
		});

		MenuItem delWordBackward = new MenuItem(StrUtils.MenuItemNameFormat("Delete the word after the cursor"));// (alt+shift+D)
		delWordBackward.setOnAction(e -> {
			SqluckyEditor.currentMyTab().getSqlCodeArea().delAnchorAfterWord();
		});

		MenuItem delCharBackward = new MenuItem(StrUtils.MenuItemNameFormat("Delete the character after the cursor"));// (ctrl+shift+D)
		delCharBackward.setOnAction(e -> {
			SqluckyEditor.currentMyTab().getSqlCodeArea().delAnchorAfterChar();
		});
		MenuItem delAllCharBackward = new MenuItem(
				StrUtils.MenuItemNameFormat("Delete all characters after the cursor"));// (ctrl+shift+K)
		delAllCharBackward.setOnAction(e -> {
			SqluckyEditor.currentMyTab().getSqlCodeArea().delAnchorAfterString();
		});
		enditLine.getItems().addAll(delWord, delChar, delAllChar, delWordBackward, delCharBackward, delAllCharBackward);

		mn.getItems().addAll(runMenu, runCurrentMenu, codeAutocompletionMenu, nce, cce, new SeparatorMenuItem(), Find,
				FindReplace, new SeparatorMenuItem(), Format, commentCode, new SeparatorMenuItem(), UpperCase,
				LowerCase, underscore, Hump, new SeparatorMenuItem(), cursorMenu, enditLine);

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
		MenuItem dataTransfer = new MenuItem(StrUtils.MenuItemNameFormat("Data TransFer"));
		dataTransfer.setGraphic(IconGenerator.svgImageDefActive("mfglabs-random"));
		dataTransfer.setOnAction(value -> {
			// TODO
			if (dtw == null) {
				dtw = new DataTransferWindow();
			}
			dtw.show();

		});
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
//		hideLeftBottom.setAccelerator(KeyCombination.keyCombination("shortcut+H"));
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
//		fontSizePlus.setAccelerator(KeyCombination.keyCombination("shortcut+EQUALS"));
		fontSizePlus.setGraphic(IconGenerator.svgImageDefActive("plus-circle"));
		fontSizePlus.setOnAction(value -> {
			CommonAction.changeFontSize(true);
		});

		MenuItem fontSizeMinus = new MenuItem(StrUtils.MenuItemNameFormat("Font Size -"));
//		fontSizeMinus.setAccelerator(KeyCombination.keyCombination("shortcut+MINUS"));
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

		mn.getItems().addAll(dataTransfer, new SeparatorMenuItem(), addDB, editConn, openConn, closeConn, closeALlConn,
				deleteConn, new SeparatorMenuItem(), hideLeft, hideBottom, hideLeftBottom, new SeparatorMenuItem()
//				, EnCoding
				, Theme, new SeparatorMenuItem(), fontSize, keysBind);

		KeyBindingCache.menuItemBinding(fontSizeMinus);
		KeyBindingCache.menuItemBinding(fontSizePlus);
		KeyBindingCache.menuItemBinding(hideLeftBottom);

		return mn;
	}

	Menu createHelpMenu() {
		Menu mn = new Menu("Help");
//		mn.setGraphic(IconGenerator.svgImageDefActive("info-circle"));
		MenuItem about = new MenuItem(StrUtils.MenuItemNameFormat("About"));
		about.setGraphic(IconGenerator.svgImageDefActive("info-circle"));
		about.setOnAction(value -> {
			ModalDialog.showAbout();
		});

		MenuItem SignInMenuItem = new MenuItem(StrUtils.MenuItemNameFormat("Sign In"));
		SignInMenuItem.setGraphic(IconGenerator.svgImageDefActive("sign-in"));
		SignInMenuItem.setOnAction(value -> {
//			SignInWindow.createWorkspaceConfigWindow();
			SignInWindow.show("");
		});

		MenuItem SignUpMenuItem = new MenuItem(StrUtils.MenuItemNameFormat("Sign Up"));
		SignUpMenuItem.setGraphic(IconGenerator.svgImageDefActive("windows-clipboard-variant-edit"));
		SignUpMenuItem.setOnAction(value -> {
//			SignUpWindow.createWorkspaceConfigWindow();
			CommonUtility.OpenURLInBrowser("https://app.sqlucky.com/");
		});

		MenuItem checkForUpdates = new MenuItem(StrUtils.MenuItemNameFormat("Check For Updates"));
//		checkForUpdates.setGraphic(IconGenerator.svgImageDefActive("zero-app-pai"));
		var svg = IconGenerator.sqluckyLogoSVG();
		checkForUpdates.setGraphic(svg);
		checkForUpdates.setOnAction(value -> {

			CheckUpdateWindow.show("");
//			String version = HttpUtil.get("http://127.0.0.1:8088/sqlucky/version");
//			if(ConfigVal.version.equals(version)) {
//				MyAlert.alertWait("已经是最新版本!");
//			}else {
//				
//			}

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
