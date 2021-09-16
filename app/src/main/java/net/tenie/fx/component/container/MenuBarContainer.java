package net.tenie.fx.component.container;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCombination;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.ImageViewGenerator;
import net.tenie.fx.component.SqlEditor;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.DataTransferWindow;
import net.tenie.fx.window.ModalDialog;


/*   @author tenie */
public class MenuBarContainer {
	private static Logger logger = LogManager.getLogger(MenuBarContainer.class);
	public static List<MenuItem> barMenus = new ArrayList<>();
	private MenuBar mainMenuBar;
	private Menu mnfile;
	private Menu mnEdit;
	private Menu mnTools;
	private Menu mnHelp;
	private DataTransferWindow dtw;
	public MenuBarContainer() {
		mainMenuBar = new MenuBar();
		mnfile = createFileMenu();
		mnEdit = createEditMenu();
		mnTools = createToolsMenu();
		mnHelp = createHelpMenu();

		mainMenuBar.getMenus().addAll(mnfile, mnEdit, mnTools, mnHelp);
		mainMenuBar.setUseSystemMenuBar(true);
	}

	// File 菜单创建
	Menu createFileMenu() {
		Menu mn = new Menu("File");

		MenuItem open = new MenuItem(StrUtils.MenuItemNameFormat("Open"));
		barMenus.add(open);
		open.setGraphic(ImageViewGenerator.svgImageDefActive("folder-open"));
		open.setAccelerator(KeyCombination.keyCombination("shortcut+O"));
		open.setOnAction(value -> {
			CommonAction.openSqlFile("UTF-8");
		});
		
		Menu openEncoding = new Menu(StrUtils.MenuItemNameFormat("Open With Encoding "));
		barMenus.add(openEncoding);
		openEncoding.setGraphic(ImageViewGenerator.svgImageDefActive("folder-open")); 
		 
		
		MenuItem openGBK = new MenuItem(StrUtils.MenuItemNameFormat("GBK"));
		barMenus.add(openGBK);
		openGBK.setGraphic(ImageViewGenerator.svgImageDefActive("folder-open")); 
		openGBK.setOnAction(value -> {
			CommonAction.openSqlFile("GBK");
		});
		openEncoding.getItems().addAll(openGBK );

		MenuItem Save = new MenuItem(StrUtils.MenuItemNameFormat("Save"));
		barMenus.add(Save);
		Save.setGraphic(ImageViewGenerator.svgImageDefActive("floppy-o"));
		//Save.setAccelerator(KeyCombination.keyCombination("shortcut+S"));
		Save.setOnAction(value -> { 
			// 保存sql文本到硬盘
			CommonAction.saveSqlAction();
		});

		MenuItem exit = new MenuItem("Exit");
		barMenus.add(exit);
		exit.setGraphic(ImageViewGenerator.svgImageDefActive("power-off"));
		exit.setAccelerator(KeyCombination.keyCombination("shortcut+Q"));
		exit.setOnAction((ActionEvent t) -> {
			CommonAction.mainPageClose();
		});

		mn.getItems().addAll(open, openEncoding, Save, new SeparatorMenuItem(), exit);
		return mn;
	}

	Menu createEditMenu() {
		Menu mn = new Menu("Edit");

		MenuItem nce = new MenuItem(StrUtils.MenuItemNameFormat("Add Code Editer"));
		barMenus.add(nce);
		nce.setAccelerator(KeyCombination.keyCombination("shortcut+T"));
		nce.setOnAction(value -> {
			SqlEditor.addCodeEmptyTabMethod();
		});

		MenuItem cce = new MenuItem(StrUtils.MenuItemNameFormat("Close Data Table"));
		barMenus.add(cce);
		cce.setAccelerator(KeyCombination.keyCombination("alt+W"));
		cce.setOnAction(value -> {
//			SqlEditor.closeEditor();
//			关闭数据显示tab页
			Tab t = ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
			if( t != null) {
				// tab 名称
				String title = CommonUtility.tabText(t);
				ComponentGetter.dataTabPane.getTabs().remove(t); 
				//都关闭页, 隐藏下半窗体
				int tabSize = ComponentGetter.dataTabPane.getTabs().size();
				if( tabSize == 0) {
					CommonAction.hideBottom();
				}else {
					//选择最后一个
					if( ConfigVal.EXEC_INFO_TITLE.equals(title) ) {
						ComponentGetter.dataTabPane.getSelectionModel().select(tabSize -1 );
					}
					 
				}
			}
			
			
		});

		MenuItem Find = new MenuItem(StrUtils.MenuItemNameFormat("Find"));
		barMenus.add(Find);
		Find.setGraphic(ImageViewGenerator.svgImageDefActive("search"));
		Find.setAccelerator(KeyCombination.keyCombination("shortcut+F"));
		Find.setOnAction(value -> {
			CommonAction.findReplace(false);
		});

		MenuItem FindReplace = new MenuItem(StrUtils.MenuItemNameFormat("Replace"));
		barMenus.add(FindReplace);
		FindReplace.setAccelerator(KeyCombination.keyCombination("shortcut+R"));
		FindReplace.setOnAction(value -> {
			CommonAction.findReplace(true);
		});

		MenuItem Format = new MenuItem(StrUtils.MenuItemNameFormat("Format Text"));
		barMenus.add(Format);
		Format.setAccelerator(KeyCombination.keyCombination("shortcut+shift+F"));
		Format.setOnAction(value -> {
			CommonAction.formatSqlText();
		});

		MenuItem commentCode = new MenuItem(StrUtils.MenuItemNameFormat("Comment Code"));
		barMenus.add(commentCode);
		commentCode.setAccelerator(KeyCombination.keyCombination("shortcut+/"));
		commentCode.setOnAction(value -> {
			CommonAction.addAnnotationSQLTextSelectText();
		});

		// 大写
		MenuItem UpperCase = new MenuItem(StrUtils.MenuItemNameFormat("Upper Case"));
		barMenus.add(UpperCase);
		UpperCase.setAccelerator(KeyCombination.keyCombination("shortcut+shift+X"));
		UpperCase.setOnAction(value -> {
			CommonAction.UpperCaseSQLTextSelectText();
		});

		MenuItem LowerCase = new MenuItem(StrUtils.MenuItemNameFormat("Lower Case"));
		barMenus.add(LowerCase);
		LowerCase.setAccelerator(KeyCombination.keyCombination("shortcut+shift+Y"));
		LowerCase.setOnAction(value -> {
			CommonAction.LowerCaseSQLTextSelectText();
		});

		// Underscore to hump
		MenuItem underscore = new MenuItem(StrUtils.MenuItemNameFormat("Underscore To Hump"));
		barMenus.add(underscore);
		underscore.setAccelerator(KeyCombination.keyCombination("shortcut+shift+R"));
		underscore.setOnAction(value -> {
			CommonAction.underlineCaseCamel();
		});

		MenuItem Hump = new MenuItem(StrUtils.MenuItemNameFormat("Hump To Underscore"));
		barMenus.add(Hump);
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
		// 数据迁移
		MenuItem dataTransfer = new MenuItem(StrUtils.MenuItemNameFormat("Data TransFer"));
		barMenus.add(dataTransfer);
		dataTransfer.setGraphic(ImageViewGenerator.svgImageDefActive("mfglabs-random"));
		dataTransfer.setOnAction(value -> {
			//TODO 
			if(dtw == null ) {
				 dtw = new DataTransferWindow();
			}
			dtw.show();
			
		});
		MenuItem addDB = new MenuItem(StrUtils.MenuItemNameFormat("Add New DB Connection"));
		barMenus.add(addDB);
		addDB.setOnAction(value -> {
			ConnectionEditor.ConnectionInfoSetting();
		});
		addDB.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));

		MenuItem editConn = new MenuItem(StrUtils.MenuItemNameFormat("Edit DB Connection"));
		barMenus.add(editConn);
		editConn.setOnAction(value -> {
			ConnectionEditor.editDbConn();
		});
		editConn.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));

		MenuItem openConn = new MenuItem(StrUtils.MenuItemNameFormat("Open DB Connection"));
		barMenus.add(openConn);
		openConn.setOnAction(value -> {
			ConnectionEditor.openDbConn();
		});
		openConn.setGraphic(ImageViewGenerator.svgImageDefActive("link"));

		MenuItem closeConn = new MenuItem(StrUtils.MenuItemNameFormat("Close DB Connection"));
		barMenus.add(closeConn);
		closeConn.setOnAction(value -> {
			ConnectionEditor.closeDbConn();
		});
		closeConn.setGraphic(ImageViewGenerator.svgImageDefActive("unlink"));

		MenuItem closeALlConn = new MenuItem(StrUtils.MenuItemNameFormat("Close All DB Connections"));
		barMenus.add(closeALlConn);
		closeALlConn.setOnAction(value -> {
			ConnectionEditor.closeAllDbConn();
		});
		closeALlConn.setGraphic(ImageViewGenerator.svgImageDefActive("power-off"));

		MenuItem deleteConn = new MenuItem(StrUtils.MenuItemNameFormat("Delete DB Connection"));
		barMenus.add(deleteConn);
		deleteConn.setOnAction(value -> {
			ConnectionEditor.deleteDbConn();
		});
		deleteConn.setGraphic(ImageViewGenerator.svgImageDefActive("trash"));

		MenuItem hideLeft = new MenuItem(StrUtils.MenuItemNameFormat("Hide/Show DB Info Panel"));
		barMenus.add(hideLeft);
		hideLeft.setOnAction(value -> {
			CommonAction.hideLeft();
		});

		MenuItem hideBottom = new MenuItem(StrUtils.MenuItemNameFormat("Hide/Show Data View Panel"));
		barMenus.add(hideBottom);
		hideBottom.setOnAction(value -> {
			CommonAction.hideBottom();
		});

		MenuItem hideLeftBottom = new MenuItem(StrUtils.MenuItemNameFormat("Hide/Show All Panels"));
		barMenus.add(hideLeftBottom);
		hideLeftBottom.setAccelerator(KeyCombination.keyCombination("shortcut+H"));
		hideLeftBottom.setGraphic(ImageViewGenerator.svgImageDefActive("arrows-alt"));
		hideLeftBottom.setOnAction(value -> {
			CommonAction.hideLeftBottom();
		});
		
		MenuItem EnCoding = new MenuItem(StrUtils.MenuItemNameFormat("EnCoding"));
		barMenus.add(EnCoding);
		EnCoding.setGraphic(ImageViewGenerator.svgImageDefActive("mfglabs-random"));
		EnCoding.setOnAction(value -> {
			String txt = SqlEditor.getCurrentCodeAreaSQLText();
//			logger.info(txt);
		    try {
				String unicode = new String(txt.getBytes(""),"GBK");
				logger.info(unicode);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		   
//			String txt
		});
		
		// 主题变化
		Menu Theme = new Menu(StrUtils.MenuItemNameFormat("Theme"));
		barMenus.add(Theme);
		Theme.setGraphic(ImageViewGenerator.svgImageDefActive("icomoon-contrast")); 
		
		MenuItem themeDark = new MenuItem(StrUtils.MenuItemNameFormat("Dark")); 
		barMenus.add(themeDark);
		themeDark.setGraphic(ImageViewGenerator.svgImageDefActive("moon")); 
		themeDark.setOnAction(value -> {
			CommonAction.setThemeRestart(CommonConst.THEME_DARK);
			
		});
		
		MenuItem themeLight = new MenuItem(StrUtils.MenuItemNameFormat("Light"));
		barMenus.add(themeLight);
		themeLight.setGraphic(ImageViewGenerator.svgImageDefActive("sun")); 
		themeLight.setOnAction(value -> {
			CommonAction.setThemeRestart(CommonConst.THEME_LIGHT);
		});
		
		MenuItem themeYellow = new MenuItem(StrUtils.MenuItemNameFormat("Yellow")); 
		barMenus.add(themeYellow);
		themeYellow.setGraphic(ImageViewGenerator.svgImageDefActive("adjust")); 
		themeYellow.setOnAction(value -> {
			CommonAction.setThemeRestart(CommonConst.THEME_YELLOW);
		});
		
		Theme.getItems().addAll(themeDark , themeLight, themeYellow); 
		
		//TODO 字体大小
		Menu fontSize = new Menu(StrUtils.MenuItemNameFormat("Code Font Size"));
		barMenus.add(fontSize);
		fontSize.setGraphic(ImageViewGenerator.svgImageDefActive("text-height")); 
		
		MenuItem fontSizePlus = new MenuItem(StrUtils.MenuItemNameFormat("Code Font Size +")); 
		barMenus.add(fontSizePlus);
//		KeyCodeCombination ctrlR = new KeyCodeCombination(KeyCode.EQUALS, KeyCodeCombination.SHORTCUT_DOWN);
		fontSizePlus.setAccelerator(KeyCombination.keyCombination( "shortcut+EQUALS") );
		fontSizePlus.setGraphic(ImageViewGenerator.svgImageDefActive("plus-circle")); 
		fontSizePlus.setOnAction(value -> {
			CommonAction.changeFontSize(true);
		});
		
		MenuItem fontSizeMinus = new MenuItem(StrUtils.MenuItemNameFormat("Code Font Size -")); 
		barMenus.add(fontSizeMinus);
		fontSizeMinus.setAccelerator(KeyCombination.keyCombination("shortcut+MINUS"));
		fontSizeMinus.setGraphic(ImageViewGenerator.svgImageDefActive("minus-circle")); 
		fontSizeMinus.setOnAction(value -> {
			CommonAction.changeFontSize(false);
		});
		
		fontSize.getItems().addAll(fontSizePlus , fontSizeMinus); 
		
		
		mn.getItems().addAll(dataTransfer, new SeparatorMenuItem(), addDB, editConn, openConn, closeConn, closeALlConn, deleteConn, new SeparatorMenuItem(),
				hideLeft, hideBottom, hideLeftBottom, new SeparatorMenuItem()
//				, EnCoding
				,Theme
				, new SeparatorMenuItem()
				,fontSize
				
				); 
		return mn;
	}

	Menu createHelpMenu() {
		Menu mn = new Menu("Help");

		MenuItem about = new MenuItem(StrUtils.MenuItemNameFormat("About"));
		barMenus.add(about);
		about.setGraphic(ImageViewGenerator.svgImageDefActive("info-circle"));
		about.setOnAction(value -> {
			ModalDialog.showAbout();
		});

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

	

}
