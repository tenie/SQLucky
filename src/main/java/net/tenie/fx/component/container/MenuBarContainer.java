package net.tenie.fx.component.container;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ConnectionEditor;
import net.tenie.fx.component.DataTransferWindow;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.ModalDialog;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.config.CommonConst;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.utility.CommonUtility;

/*   @author tenie */
public class MenuBarContainer {
	private static Logger logger = LogManager.getLogger(MenuBarContainer.class);
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
	}

	// File 菜单创建
	Menu createFileMenu() {
		Menu mn = new Menu("File");

		MenuItem open = new MenuItem(MenuItemNameFormat("Open"));
		open.setGraphic(ImageViewGenerator.svgImageUnactive("folder-open"));
		open.setAccelerator(KeyCombination.keyCombination("shortcut+O"));
		open.setOnAction(value -> {
			CommonAction.openSqlFile("UTF-8");
		});
		
		Menu openEncoding = new Menu(MenuItemNameFormat("Open With Encoding "));
		openEncoding.setGraphic(ImageViewGenerator.svgImageUnactive("folder-open")); 
		 
		
		MenuItem openGBK = new MenuItem(MenuItemNameFormat("GBK"));
		openGBK.setGraphic(ImageViewGenerator.svgImageUnactive("folder-open")); 
		openGBK.setOnAction(value -> {
			CommonAction.openSqlFile("GBK");
		});
		openEncoding.getItems().addAll(openGBK );

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

		mn.getItems().addAll(open, openEncoding, Save, new SeparatorMenuItem(), exit);
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
//			SqlEditor.closeEditor();
//			关闭数据显示tab页
			Tab t = ComponentGetter.dataTab.getSelectionModel().getSelectedItem();
			if( t != null) {
				// tab 名称
				String title = CommonUtility.tabText(t);
				ComponentGetter.dataTab.getTabs().remove(t); 
				//都关闭页, 隐藏下半窗体
				int tabSize = ComponentGetter.dataTab.getTabs().size();
				if( tabSize == 0) {
					CommonAction.hideBottom();
				}else {
					//选择最后一个
					if( ConfigVal.EXEC_INFO_TITLE.equals(title) ) {
						ComponentGetter.dataTab.getSelectionModel().select(tabSize -1 );
					}
					 
				}
			}
			
			
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
		commentCode.setAccelerator(KeyCombination.keyCombination("shortcut+/"));
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
		// 数据迁移
		MenuItem dataTransfer = new MenuItem(MenuItemNameFormat("Data TransFer"));
		dataTransfer.setGraphic(ImageViewGenerator.svgImageUnactive("mfglabs-random"));
		dataTransfer.setOnAction(value -> {
			//TODO 
			if(dtw == null ) {
				 dtw = new DataTransferWindow();
			}
			dtw.show();
			
		});
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
		hideLeftBottom.setAccelerator(KeyCombination.keyCombination("shortcut+H"));
		hideLeftBottom.setGraphic(ImageViewGenerator.svgImageUnactive("arrows-alt"));
		hideLeftBottom.setOnAction(value -> {
			CommonAction.hideLeftBottom();
		});
		
		MenuItem EnCoding = new MenuItem(MenuItemNameFormat("EnCoding"));
		EnCoding.setGraphic(ImageViewGenerator.svgImageUnactive("mfglabs-random"));
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
		Menu Theme = new Menu(MenuItemNameFormat("Theme"));
		Theme.setGraphic(ImageViewGenerator.svgImageUnactive("icomoon-contrast")); 
		
		MenuItem themeDark = new MenuItem(MenuItemNameFormat("Dark")); 
		themeDark.setGraphic(ImageViewGenerator.svgImageUnactive("moon")); 
		themeDark.setOnAction(value -> {
			CommonAction.setTheme(CommonConst.THEME_DARK);
		});
		
		MenuItem themeLight = new MenuItem(MenuItemNameFormat("Light")); 
		themeLight.setGraphic(ImageViewGenerator.svgImageUnactive("sun")); 
		themeLight.setOnAction(value -> {
			CommonAction.setTheme(CommonConst.THEME_LIGHT);
		});
		
		MenuItem themeYellow = new MenuItem(MenuItemNameFormat("Yellow")); 
		themeYellow.setGraphic(ImageViewGenerator.svgImageUnactive("sun")); 
		themeYellow.setOnAction(value -> {
			CommonAction.setTheme(CommonConst.THEME_YELLOW);
		});
		
		Theme.getItems().addAll(themeDark , themeLight, themeYellow); 
		
		//TODO 字体大小
		Menu fontSize = new Menu(MenuItemNameFormat("Code Font Size"));
		fontSize.setGraphic(ImageViewGenerator.svgImageUnactive("text-height")); 
		
		MenuItem fontSizePlus = new MenuItem(MenuItemNameFormat("Code Font Size +")); 
//		KeyCodeCombination ctrlR = new KeyCodeCombination(KeyCode.EQUALS, KeyCodeCombination.SHORTCUT_DOWN);
		fontSizePlus.setAccelerator(KeyCombination.keyCombination( "shortcut+EQUALS") );
		fontSizePlus.setGraphic(ImageViewGenerator.svgImageUnactive("plus-circle")); 
		fontSizePlus.setOnAction(value -> {
			CommonAction.changeFontSize(true);
		});
		
		MenuItem fontSizeMinus = new MenuItem(MenuItemNameFormat("Code Font Size -")); 
		fontSizeMinus.setAccelerator(KeyCombination.keyCombination("shortcut+MINUS"));
		fontSizeMinus.setGraphic(ImageViewGenerator.svgImageUnactive("minus-circle")); 
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

		MenuItem about = new MenuItem(MenuItemNameFormat("About"));
		about.setGraphic(ImageViewGenerator.svgImageUnactive("info-circle"));
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

	static private String MenuItemNameFormat(String name) {
		String str = String.format("  %-30s", name);
		return str;
	}

	public static void main(String[] args) {
		   logger.info("Default Charset=" + Charset.defaultCharset());
	}
}
