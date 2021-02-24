package net.tenie.fx.component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.MainTabInfo;
import net.tenie.fx.config.MainTabs;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.H2SqlTextSavePo;
import net.tenie.lib.db.h2.SqlTextDao;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class SqlEditor {
	public static List<CodeArea> allCodeArea = new ArrayList<>();
	public static TabPane myTabPane;

	// 添加空文本的codeTab
	public static Tab addCodeEmptyTabMethod() {

		int size = myTabPane.getTabs().size();
		if (ConfigVal.pageSize < 0) {
			ConfigVal.pageSize = size;
		}
		ConfigVal.pageSize++;
		String labe = "Untitled" + ConfigVal.pageSize + "*";
		Tab nwTab = new Tab();
		CommonUtility.setTabName(nwTab, labe);
		// 添加到缓存
		MainTabs.add(nwTab);

		StackPane pane = SqlCodeArea();
		VBox vbox = new VBox();
		vbox.getChildren().add(pane);
		VBox.setVgrow(pane, Priority.ALWAYS);
		nwTab.setContent(vbox);

//			关闭前事件
		nwTab.setOnCloseRequest(CommonEventHandler.tabCloseReq(myTabPane));
		// 选中事件
		nwTab.setOnSelectionChanged(value -> {
			MainTabInfo ti = MainTabs.get(nwTab);
			if (ti != null) {
				DBConns.changeChoiceBox(ti.getTabConnIdx());
			}

		});

		nwTab.setId(ConfigVal.SQL_AREA_TAG + ConfigVal.pageSize);
		myTabPane.getTabs().add(size, nwTab);// 在指定位置添加Tab
		myTabPane.getSelectionModel().select(size);
		return nwTab;
	}

	// 从h2中获取上次的code area val
	public static void codeAreaRecover() {
		try {
			Connection H2conn = H2Db.getConn();
			List<H2SqlTextSavePo> ls = SqlTextDao.read(H2conn);
			if (ls != null && ls.size() > 0) {
				for (H2SqlTextSavePo po : ls) {
					Tab tab = addCodeEmptyTabMethod();
					setTabSQLText(tab, po.getText(), po.getParagraph());
					if (StrUtils.isNotNullOrEmpty(po.getFileName())) {
						// String file = FilenameUtils.getName(po.getFileName());
						tab.setId(ConfigVal.SAVE_TAG + po.getFileName());
//							tab.setText(po.getTitle()); 
						CommonUtility.setTabName(tab, po.getTitle());
						ComponentGetter.fileEncode.put(  po.getFileName(), po.getEncode());
					}

				}
				// 初始化上次选中页面
				String SELECT_PANE = SqlTextDao.readConfig(H2conn, "SELECT_PANE");
				if(StrUtils.isNotNullOrEmpty(SELECT_PANE)) {
					ComponentGetter.mainTabPane.getSelectionModel().select(Integer.valueOf(SELECT_PANE));
				}
				
			} else {
				// 触发鼠标点击事件, 增加一个 代码窗口 , 如果窗口中是空的情况下
				addCodeEmptyTabMethod();
			}
			 

		} finally {
			H2Db.closeConn();
		}
	}

	public static void createTabFromSqlFile(String val, String tabName, String id) {
		Tab tab = SqlEditor.addCodeEmptyTabMethod();
		if (StrUtils.isNotNullOrEmpty(val))
			setTabSQLText(tab, val);
		if (StrUtils.isNotNullOrEmpty(tabName))
			CommonUtility.setTabName(tab, tabName);
		if (StrUtils.isNotNullOrEmpty(id))
			tab.setId(id);
	}

	// 设置tab 中的 area 中的文本
	public static void setTabSQLText(Tab tb, String text) {
		CodeArea code = getCodeArea(tb);
		code.appendText(text);
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}
	
	public static void setTabSQLText(Tab tb, String text, int paragraph) {
		CodeArea code = getCodeArea(tb);
		code.appendText(text);
		code.showParagraphAtTop(paragraph);
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}
	

	public static CodeArea getCodeArea() {
		StackPane p = getTabStackPane();
		@SuppressWarnings("rawtypes")
		VirtualizedScrollPane v = (VirtualizedScrollPane) p.getChildren().get(0);
		CodeArea code = (CodeArea) v.getContent();
		return code;
	}

//  获取Tab中的的code area
	public static CodeArea getCodeArea(Tab tb) {
		StackPane p = getTabStackPane(tb);
		@SuppressWarnings("rawtypes")
		VirtualizedScrollPane v = (VirtualizedScrollPane) p.getChildren().get(0);
		CodeArea code = (CodeArea) v.getContent();
		return code;
	}

// 获取area 中的文本
	public static String getCurrentTabSQLText(CodeArea code) {
		String sqlText = code.getText();
		return sqlText;
	}

// 获取tab 中的  area 中的文本
	public static String getTabSQLText(Tab tb) {
		CodeArea code = getCodeArea(tb);
		String sqlText = code.getText();
		return sqlText;
	}

// get sql text
	public static String getCurrentCodeAreaSQLText() {
		CodeArea code = getCodeArea();
		String sqlText = code.getText();
		return sqlText;
	}

// get select text
	public static String getCurrentCodeAreaSQLTextSelected() {
		CodeArea code = getCodeArea();
		return code.getSelectedText();
	}

	// 代码的容器
	public static StackPane getTabStackPane(Tab tb) {
		VBox vb = (VBox) tb.getContent();
		StackPane sp = null;
		if (vb.getChildren().size() > 1) {
			sp = (StackPane) vb.getChildren().get(1);
		} else {
			sp = (StackPane) vb.getChildren().get(0);
		}
		return sp;
	}

	public static StackPane getTabStackPane() {
		Tab tb = mainTabPaneSelectedTab();
		VBox vb = (VBox) tb.getContent();
		StackPane sp = null;
		int chsz = vb.getChildren().size();
		sp = (StackPane) vb.getChildren().get(chsz - 1);
		return sp;
	}

	// 获取当前选中的代码Tab
	public static Tab mainTabPaneSelectedTab() {
		return myTabPane.getSelectionModel().getSelectedItem();
	}

	// 获取tab的内容 VBox
	public static VBox getTabVbox(Tab tb) {
		return (VBox) tb.getContent();
	}

	public static VBox getTabVbox() {
		Tab tb = mainTabPaneSelectedTab();
		return (VBox) tb.getContent();
	}

	public static void closeEditor() {
		TabPane myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane.getTabs().size() > 1) {
			myTabPane.getTabs().remove(myTabPane.getSelectionModel().getSelectedIndex());
		}
	}

//	代码框添加
	public static StackPane SqlCodeArea() {
		StackPane sp = new SqlCodeAreaHighLighting().getObj();
		return sp;
	}
	
	// 获取所有的CodeArea
//	public static List<CodeArea> getAllCodeArea() {
//		List<CodeArea> cas = new ArrayList<>();
//		TabPane myTabPane = ComponentGetter.mainTabPane;
//		if (myTabPane.getTabs().size() > 1) {
//			ObservableList<Tab> tabs = myTabPane.getTabs();
//			for(Tab tb : tabs) { 
//				CodeArea ac = getCodeArea(tb);
//				cas.add(ac);
//				if(ConfigVal.THEME.equals("DARK")) {
//					ac.setParagraphGraphicFactory(MyLineNumberFactory.get(ac ,"#606366" , "#313335"));
//				}else {
//					ac.setParagraphGraphicFactory(MyLineNumberFactory.get(ac, "#666", "#ddd"));
//				} 
//			}
//		}
//		return cas;
//	}
	
	// 获取所有的CodeArea
	public static List<CodeArea> getAllCodeArea() {
		List<CodeArea> cas = new ArrayList<>();
		TabPane myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane.getTabs().size() > 1) {
			ObservableList<Tab> tabs = myTabPane.getTabs();
			for(Tab tb : tabs) { 
				CodeArea ac = getCodeArea(tb);
				cas.add(ac);
			}
		}
		return cas;
	}
	
	public static void changeThemeAllCodeArea() { 
		TabPane myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane.getTabs().size() > 0) {
			ObservableList<Tab> tabs = myTabPane.getTabs();
			for(Tab tb : tabs) { 
				CodeArea ac = getCodeArea(tb); 
				if(ConfigVal.THEME.equals("DARK")) {
					ac.setParagraphGraphicFactory(MyLineNumberFactory.get(ac ,"#606366" , "#313335"));
				}else {
					ac.setParagraphGraphicFactory(MyLineNumberFactory.get(ac, "#666" , "#ddd"));
				} 
			}
		}
	}

}
