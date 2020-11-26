package net.tenie.fx.component.container;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.component.ConnectionEditor;
import net.tenie.fx.component.DataTransferWindow;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.ModalDialog;
import net.tenie.fx.component.SqlEditor;

/*   @author tenie */
public class MenuBarContainer {
	private MenuBar mainMenuBar;
	private Menu mnfile;
	private Menu mnEdit;
	private Menu mnTools;
	private Menu mnHelp;

	public MenuBarContainer() {
		mainMenuBar = new MenuBar();
		mnfile = createFileMenu();
		mnEdit = createEditMenu();
		mnTools = createToolsMenu();
		mnHelp = createHelpMenu();

		mainMenuBar.getMenus().addAll(mnfile, mnEdit, mnTools, mnHelp);
	}

	// File 菜单创建
	Menu createFileMenu() {
		Menu mn = new Menu("File");

		MenuItem open = new MenuItem(MenuItemNameFormat("Open"));
		open.setGraphic(ImageViewGenerator.svgImageUnactive("folder-open"));
		open.setAccelerator(KeyCombination.keyCombination("shortcut+O"));
		open.setOnAction(value -> {
			CommonAction.openSqlFile();
		});

		MenuItem Save = new MenuItem(MenuItemNameFormat("Save"));
		Save.setGraphic(ImageViewGenerator.svgImageUnactive("floppy-o"));
		Save.setAccelerator(KeyCombination.keyCombination("shortcut+S"));
		Save.setOnAction(value -> {
			CommonAction.saveSqlAction();
		});

		MenuItem exit = new MenuItem("Exit");
		exit.setGraphic(ImageViewGenerator.svgImageUnactive("power-off"));
		exit.setAccelerator(KeyCombination.keyCombination("shortcut+Q"));
		exit.setOnAction((ActionEvent t) -> {
			CommonAction.mainPageClose();
		});

		mn.getItems().addAll(open, Save, new SeparatorMenuItem(), exit);
		return mn;
	}

	Menu createEditMenu() {
		Menu mn = new Menu("Edit");

		MenuItem nce = new MenuItem(MenuItemNameFormat("Add Code Editer"));
		nce.setAccelerator(KeyCombination.keyCombination("shortcut+T"));
		nce.setOnAction(value -> {
			SqlEditor.addCodeEmptyTabMethod();
		});

		MenuItem cce = new MenuItem(MenuItemNameFormat("Close Code Editer"));
		cce.setAccelerator(KeyCombination.keyCombination("shortcut+W"));
		cce.setOnAction(value -> {
			SqlEditor.closeEditor();
		});

		MenuItem Find = new MenuItem(MenuItemNameFormat("Find"));
		Find.setGraphic(ImageViewGenerator.svgImageUnactive("search"));
		Find.setAccelerator(KeyCombination.keyCombination("shortcut+F"));
		Find.setOnAction(value -> {
			CommonAction.findReplace(false);
		});

		MenuItem FindReplace = new MenuItem(MenuItemNameFormat("Replace"));
		FindReplace.setAccelerator(KeyCombination.keyCombination("shortcut+R"));
		FindReplace.setOnAction(value -> {
			CommonAction.findReplace(true);
		});

		MenuItem Format = new MenuItem(MenuItemNameFormat("Format Text"));
		Format.setAccelerator(KeyCombination.keyCombination("shortcut+shift+F"));
		Format.setOnAction(value -> {
			CommonAction.formatSqlText();
		});

		MenuItem commentCode = new MenuItem(MenuItemNameFormat("Comment Code"));
		commentCode.setAccelerator(KeyCombination.keyCombination("shortcut+T"));
		commentCode.setOnAction(value -> {
			CommonAction.addAnnotationSQLTextSelectText();
		});

		// 大写
		MenuItem UpperCase = new MenuItem(MenuItemNameFormat("Upper Case"));
		UpperCase.setAccelerator(KeyCombination.keyCombination("shortcut+shift+X"));
		UpperCase.setOnAction(value -> {
			CommonAction.UpperCaseSQLTextSelectText();
		});

		MenuItem LowerCase = new MenuItem(MenuItemNameFormat("Lower Case"));
		LowerCase.setAccelerator(KeyCombination.keyCombination("shortcut+shift+Y"));
		LowerCase.setOnAction(value -> {
			CommonAction.LowerCaseSQLTextSelectText();
		});

		// Underscore to hump
		MenuItem underscore = new MenuItem(MenuItemNameFormat("Underscore To Hump"));
		underscore.setAccelerator(KeyCombination.keyCombination("shortcut+shift+R"));
		underscore.setOnAction(value -> {
			CommonAction.underlineCaseCamel();
		});

		MenuItem Hump = new MenuItem(MenuItemNameFormat("Hump To Underscore"));
		Hump.setAccelerator(KeyCombination.keyCombination("shortcut+shift+T"));
		Hump.setOnAction(value -> {
			CommonAction.CamelCaseUnderline();
		});

		mn.getItems().addAll(nce, cce, new SeparatorMenuItem(), Find, FindReplace, new SeparatorMenuItem(), Format,
				commentCode, new SeparatorMenuItem(), UpperCase, LowerCase, underscore, Hump, new SeparatorMenuItem());
		return mn;
	}

	Menu createToolsMenu() {
		Menu mn = new Menu("Tools");

		MenuItem addDB = new MenuItem(MenuItemNameFormat("Add New DB Connection"));
		addDB.setOnAction(value -> {
			ConnectionEditor.ConnectionInfoSetting();
		});
		addDB.setGraphic(ImageViewGenerator.svgImageUnactive("plus-square-o"));

		MenuItem editConn = new MenuItem(MenuItemNameFormat("Edit DB Connection"));
		editConn.setOnAction(value -> {
			ConnectionEditor.editDbConn();
		});
		editConn.setGraphic(ImageViewGenerator.svgImageUnactive("edit"));

		MenuItem openConn = new MenuItem(MenuItemNameFormat("Open DB Connection"));
		openConn.setOnAction(value -> {
			ConnectionEditor.openDbConn();
		});
		openConn.setGraphic(ImageViewGenerator.svgImageUnactive("link"));

		MenuItem closeConn = new MenuItem(MenuItemNameFormat("Close DB Connection"));
		closeConn.setOnAction(value -> {
			ConnectionEditor.closeDbConn();
		});
		closeConn.setGraphic(ImageViewGenerator.svgImageUnactive("unlink"));

		MenuItem closeALlConn = new MenuItem(MenuItemNameFormat("Close All DB Connections"));
		closeALlConn.setOnAction(value -> {
			ConnectionEditor.closeAllDbConn();
		});
		closeALlConn.setGraphic(ImageViewGenerator.svgImageUnactive("power-off"));

		MenuItem deleteConn = new MenuItem(MenuItemNameFormat("Delete DB Connection"));
		deleteConn.setOnAction(value -> {
			ConnectionEditor.deleteDbConn();
		});
		deleteConn.setGraphic(ImageViewGenerator.svgImageUnactive("trash"));

		MenuItem hideLeft = new MenuItem(MenuItemNameFormat("Hide/Show DB Info Panel"));
		hideLeft.setOnAction(value -> {
			CommonAction.hideLeft();
		});

		MenuItem hideBottom = new MenuItem(MenuItemNameFormat("Hide/Show Data View Panel"));
		hideBottom.setOnAction(value -> {
			CommonAction.hideBottom();
		});

		MenuItem hideLeftBottom = new MenuItem(MenuItemNameFormat("Hide/Show All Panels"));
		hideLeftBottom.setGraphic(ImageViewGenerator.svgImageUnactive("arrows-alt"));
		hideLeftBottom.setOnAction(value -> {
			CommonAction.hideLeftBottom();
		});
		
		// 数据迁移
		MenuItem dataTransfer = new MenuItem(MenuItemNameFormat("Data TransFer"));
		dataTransfer.setGraphic(ImageViewGenerator.svgImageUnactive("mfglabs-random"));
		dataTransfer.setOnAction(value -> {
			DataTransferWindow dtw = new DataTransferWindow();
		});
		mn.getItems().addAll(dataTransfer, new SeparatorMenuItem(), addDB, editConn, openConn, closeConn, closeALlConn, deleteConn, new SeparatorMenuItem(),
				hideLeft, hideBottom, hideLeftBottom, new SeparatorMenuItem());
		return mn;
	}

	Menu createHelpMenu() {
		Menu mn = new Menu("Help");

		MenuItem about = new MenuItem(MenuItemNameFormat("About"));
		about.setGraphic(ImageViewGenerator.svgImageUnactive("info-circle"));

		mn.getItems().addAll(about);
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

	static private String MenuItemNameFormat(String name) {
		String str = String.format("  %-30s", name);
		return str;
	}

}
