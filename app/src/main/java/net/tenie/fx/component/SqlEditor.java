package net.tenie.fx.component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.Paragraph;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.PropertyPo.MyRange;
import net.tenie.fx.PropertyPo.ScriptPo;
import net.tenie.fx.component.container.ScriptTabTree;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.H2SqlTextSavePo;
import net.tenie.lib.db.h2.SqlTextDao;


/*   @author tenie */
public class SqlEditor {
	public static List<CodeArea> allCodeArea = new ArrayList<>();
	public static TabPane myTabPane;
	private static Logger logger = LogManager.getLogger(SqlEditor.class);

	// 当前文本框中文本重新高亮
	public static void applyHighlighting() {
		currentSqlCodeAreaHighLighting();
	}
	
	// 获取当前选中的区间
	public static IndexRange getSelection() {
		var codeArea = SqlEditor.getCodeArea();
		return codeArea.getSelection();
	}
	
	
	// 设置选中
	public static void selectRange( IndexRange  ir) {
		var codeArea = SqlEditor.getCodeArea();
		codeArea.selectRange(ir.getStart(), ir.getEnd());
	}
	// 添加空文本的codeTab
	public static MyTab addCodeEmptyTabMethod() {

		int size = myTabPane.getTabs().size();
		if (ConfigVal.pageSize < 0) {
			ConfigVal.pageSize = size;
		}
		ConfigVal.pageSize++;
		String labe = "Untitled_" + ConfigVal.pageSize + "*";
//		String tabId = ConfigVal.SQL_AREA_TAG + ConfigVal.pageSize;
//		Tab nwTab = new Tab();
		MyTab nwTab = new MyTab(labe);
//		CommonUtility.setTabName(nwTab, labe);
//		// 添加到缓存
//		MainTabs.add(nwTab);

//		StackPane pane = SqlCodeArea();
//		VBox vbox = new VBox();
//		vbox.getChildren().add(pane);
//		VBox.setVgrow(pane, Priority.ALWAYS);
//		nwTab.setContent(vbox);

		// 关闭前事件
//		nwTab.setOnCloseRequest(CommonEventHandler.tabCloseReq(myTabPane));
//		// 选中事件
//		nwTab.setOnSelectionChanged(value -> {
//			MainTabInfo ti = MainTabs.get(nwTab);
//			if (ti != null) {
//				DBConns.changeChoiceBox(ti.getTabConnIdx());
//			}
//
//		});

//		nwTab.setId(ConfigVal.SQL_AREA_TAG + ConfigVal.pageSize);
		myTabPane.getTabs().add(size, nwTab);// 在指定位置添加Tab
		myTabPane.getSelectionModel().select(size);
		ScriptTabTree.treeRootAddItem(nwTab);
		return nwTab;
	}
	
	// 添加空文本的codeTab
		public static MyTab addMyTabByScriptPo(ScriptPo scpo) {

			int size = myTabPane.getTabs().size(); 
			ConfigVal.pageSize++; 
			MyTab nwTab = new MyTab(scpo); 
			myTabPane.getTabs().add(size, nwTab);// 在指定位置添加Tab
			myTabPane.getSelectionModel().select(size);
			ScriptTabTree.treeRootAddItem(nwTab);
			return nwTab;
		}
	
	
	// 添加空文本的codeTab
		public static void myTabPaneAddMyTab(MyTab nwTab) { 
			if( myTabPane.getTabs().contains(nwTab) == false ) {
				myTabPane.getTabs().add( nwTab);// 在指定位置添加Tab 
			} 
			myTabPane.getSelectionModel().select(nwTab);
		}
	
	

	//TODO 从h2中获取上次的code area val
	public static void codeAreaRecover() {
		try {
			Connection H2conn = H2Db.getConn();
			List<H2SqlTextSavePo> ls = SqlTextDao.read(H2conn);
			if (ls != null && ls.size() > 0) {
				for (H2SqlTextSavePo po : ls) {
//					Tab tab = addCodeEmptyTabMethod();
					ScriptPo spo = new ScriptPo();
					spo.setEncode(po.getEncode());
					spo.setFileName(po.getFileName());
					spo.setId(po.getScriptId());
					spo.setParagraph(po.getParagraph());
					spo.setText(po.getText());
					spo.setTitle(po.getTitle());
					MyTab tab = new MyTab(spo);
					myTabPaneAddMyTab(tab);
//					setTabSQLText(tab, po.getText(), po.getParagraph());
//					if (StrUtils.isNotNullOrEmpty(po.getFileName())) {
						// String file = FilenameUtils.getName(po.getFileName());
//						tab.setId(ConfigVal.SAVE_TAG + po.getFileName());
//							tab.setText(po.getTitle()); 
//						CommonUtility.setTabName(tab, po.getTitle());
//						ComponentGetter.fileEncode.put(  po.getFileName(), po.getEncode());
//					}

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

//	public static void createTabFromSqlFile(String val, String tabName) {
//		MyTab tab = SqlEditor.addCodeEmptyTabMethod();
//		if (StrUtils.isNotNullOrEmpty(val))
//			setTabSQLText(tab, val);
//		if (StrUtils.isNotNullOrEmpty(tabName))
//			CommonUtility.setTabName(tab, tabName);  
//	}
	
	public static void createTabFromSqlFile(ScriptPo scpo) {
		addMyTabByScriptPo(scpo);
	}
	

	// 设置tab 中的 area 中的文本
	public static void setTabSQLText(MyTab tb, String text) {
		CodeArea code = getCodeArea(tb);
		code.appendText(text);
		tb.getSqlCodeArea().highLighting();
		tb.syncScriptPo();
	}
	
 
	
	// 获取当前在前台的文本框
	public static CodeArea getCodeArea() {
		var mtb = currentMyTab();
		return mtb.getSqlCodeArea().getCodeArea();
	}
	
//	public static CodeArea getCodeArea(StackPane p) {
//			@SuppressWarnings("rawtypes")
//			VirtualizedScrollPane v = (VirtualizedScrollPane) p.getChildren().get(0);
//			CodeArea code = (CodeArea) v.getContent();
//			return code;  
//	}
	// 当前文本框中, 取消选中的文本
	public static void deselect() {
		getCodeArea().deselect(); 
	}

   //  获取Tab中的的code area
	public static CodeArea getCodeArea(MyTab tb) {
//		StackPane p = getTabStackPane(tb);
//		@SuppressWarnings("rawtypes")
//		VirtualizedScrollPane v = (VirtualizedScrollPane) p.getChildren().get(0);
//		CodeArea code = (CodeArea) v.getContent();
		var code = tb.getSqlCodeArea().getCodeArea();
		return code;
	}

    // 获取area 中的文本
	public static String getCurrentTabSQLText(CodeArea code) {
		String sqlText = code.getText();
		return sqlText;
	}

    // 获取tab 中的  area 中的文本
	public static String getTabSQLText(MyTab tb) {
		CodeArea code = getCodeArea(tb);
		String sqlText = code.getText();
		return sqlText;
	}

	// 	get sql text
	public static String getCurrentCodeAreaSQLText() {
		CodeArea code = getCodeArea();
		String sqlText = code.getText();
		return sqlText;
	}  
	// append txt
//	public static void appendStr(StackPane spCode  ,String str) {
//		if(str !=null) {
//			CodeArea code = SqlEditor.getCodeArea(spCode);
//			code.appendText(str);
//		}
//		
//	}
	// 清除所有文本
//	public static void cleanStr(StackPane spCode) {
//		CodeArea code = SqlEditor.getCodeArea(spCode);
//		code.clear();
//	}
	

   // get select text
	public static String getCurrentCodeAreaSQLSelectedText() {
		CodeArea code = getCodeArea();
		return code.getSelectedText();
	}
	
	// 复制当前选中的文本
	public static void copySelectionText() {
		String txt = getCurrentCodeAreaSQLSelectedText();
		CommonUtility.setClipboardVal(txt);
	}
	
	public static void pasteTextToCodeArea(){
		String val = CommonUtility.getClipboardVal();
		if(StrUtils.isNotNullOrEmpty(val)) {
			var codeArea = SqlEditor.getCodeArea();
			int i = codeArea.getAnchor();
			codeArea.insertText(i, val);
			SqlEditor.currentMyTab().getSqlCodeArea().highLighting();
		}
	}
	
	// 删除选中文本
	public static void deleteSelectionText() { 
		var codeArea = SqlEditor.getCodeArea();
		IndexRange ir = codeArea.getSelection();
		codeArea.deleteText(ir);
	}
	
	// 剪切选中文本
	public static void cutSelectionText() { 
		 copySelectionText();
		 deleteSelectionText(); 
	}
		

	// 代码的容器
	public static StackPane getTabStackPane(MyTab tb) {
		VBox vb = (VBox) tb.getContent();
		StackPane sp = null;
		if (vb.getChildren().size() > 1) {
			int sz = vb.getChildren().size() - 1;
			sp = (StackPane) vb.getChildren().get(sz);
			
		} else {
			sp = (StackPane) vb.getChildren().get(0);
		}
		return sp;
	}

//	public static StackPane getTabStackPane() {
//		Tab tb = mainTabPaneSelectedTab();
//		VBox vb = (VBox) tb.getContent();
//		StackPane sp = null;
//		int chsz = vb.getChildren().size();
//		sp = (StackPane) vb.getChildren().get(chsz - 1);
//		return sp;
//	}

	// 获取当前选中的代码Tab
	public static Tab mainTabPaneSelectedTab() { 
		return myTabPane.getSelectionModel().getSelectedItem();
	}
	
	public static MyTab currentMyTab() { 
		Tab currentTab = myTabPane.getSelectionModel().getSelectedItem();
		MyTab rs = (MyTab) currentTab;
		return rs;
	}
	
	public static void currentSqlCodeAreaHighLighting() {
		MyTab mtb = currentMyTab();
		var area = mtb.getSqlCodeArea();
		area.highLighting();
	} 
	
	public static void ErrorHighlighting( int begin , int length , String str) {
		MyTab mtb = currentMyTab();
		var area = mtb.getSqlCodeArea();
		area.errorHighLighting(begin, length, str);
	}
	
	public static void currentSqlCodeAreaHighLighting(String str) {
		MyTab mtb = currentMyTab();
		var area = mtb.getSqlCodeArea();
		area.highLighting(str);
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
//	public static StackPane SqlCodeArea() {
//		StackPane sp = new HighLightingSqlCodeArea().getObj();
//		return sp;
//	}
	
 
	
	// 获取所有的CodeArea
	public static List<MyTab> getAllgetMyTabs() {
		List<MyTab> cas = new ArrayList<>();
		TabPane myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane.getTabs().size() > 1) {
			ObservableList<Tab> tabs = myTabPane.getTabs();
			for(Tab tb : tabs) { 
				MyTab mtb = (MyTab) tb;
//				CodeArea ac = getCodeArea(mtb);
				cas.add(mtb);
			}
		}
		return cas;
	}
	
//	public static List<CodeArea> getAllCodeArea() {
//		List<CodeArea> cas = new ArrayList<>();
//		TabPane myTabPane = ComponentGetter.mainTabPane;
//		if (myTabPane.getTabs().size() > 1) {
//			ObservableList<Tab> tabs = myTabPane.getTabs();
//			for(Tab tb : tabs) { 
//				MyTab mtb = (MyTab) tb;
//				CodeArea ac = getCodeArea(mtb);
//				cas.add(ac);
//			}
//		}
//		return cas;
//	}
	
	
	// 改变样式
	public static void changeThemeAllCodeArea() { 
		TabPane myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane != null && myTabPane.getTabs().size() > 0) {
			ObservableList<Tab> tabs = myTabPane.getTabs();
			for(Tab tb : tabs) { 
				MyTab mtb = (MyTab) tb;
				// 修改代码编辑区域的样式
//				MyCodeArea ac = (MyCodeArea) getCodeArea(mtb); 
				mtb.getSqlCodeArea().changeCodeAreaLineNoThemeHelper();
//				changeCodeAreaLineNoThemeHelper(ac);
				// 修改查找替换的样式如果有的话
				changeFindReplacePaneBtnColor(tb);
			}
		}
	}
	
//	修改查找替换的样式如果有的话
	private static void changeFindReplacePaneBtnColor(Tab tb) {
		VBox vbx = (VBox) tb.getContent();
		if(vbx.getChildren().size() > 1) {
			String color = CommonAction.themeColor();
			for(int i = 0 ; i< vbx.getChildren().size() -1 ; i++) {
				Node nd  = vbx.getChildren().get(i);
				if( nd instanceof AnchorPane) {
					AnchorPane ap =  (AnchorPane) nd;
					var apchs = ap.getChildren();
					for(Node apnd : apchs ) {
						if( apnd instanceof JFXButton) {
							JFXButton btn = (JFXButton) apnd;
							if(btn.getGraphic() != null)
								btn.getGraphic().setStyle("-fx-background-color: " +  color + ";");
						}
					}
				}
				
			}
		}
	}
	
	
	
	

	
//	/**
//	 * 自动补全提示
//	 * @param e
//	 * @param codeArea
//	 */
//	public static void codePopup(KeyEvent e, CodeArea codeArea) { 
//		if(e.isAltDown()) { 
//			callPopup(codeArea);
//			
//		} 
//	}
//	
//	public static void callPopup(CodeArea codeArea) {
//		Platform.runLater(()->{
//			Bounds  bd = codeArea.caretBoundsProperty().getValue().get();
//			double x = bd.getCenterX();
//			double y = bd.getCenterY(); 
//			int anchor = codeArea.getAnchor();
//			String str = "";
//			for(int i = 1 ; anchor-i >= 0 ; i++) {
//				var tmp = codeArea.getText(anchor-i, anchor);
//				int tmplen = tmp.length();
//				int idx =    anchor - tmplen ;
////				System.out.println(tmp 
////						+ tmp.startsWith(" ") + 
////						tmp.startsWith("\t") + 
////						tmp.startsWith("\n") +
////						(idx <= 0) +" ==="
////						);
//				
//				if(tmp.startsWith(" ") || tmp.startsWith("\t") || tmp.startsWith("\n") ||   idx <= 0   ) {
//					str = tmp;
//					break;
//				}
//			}
//			MyAutoComplete.showPop(x, y+9 , str);
//		});
//	}
	
	
	
	
	
	
	/*
	 * 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况 1. 先在原始文本中找到sql的字符串, 替换为空白字符串,
	 * 得到一个新文本 2. 在新文本中根据 ; 分割字符串, 得到每个分割出来的子串在文本中的区间 3. 根据区间, 在原始文本中 提炼出sql语句
	 */
	public static List<String> findSQLFromTxt(String text) {
		String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
		String patternString = "(?<STRING>" + STRING_PATTERN + ")";
		Pattern PATTERN = Pattern.compile(patternString);
		Matcher matcher = PATTERN.matcher(text);
		String txtTmp = "";
		int lastKwEnd = 0;
		// 把匹配到的sql的字符串替换为对应长度的空白字符串, 得到一个和原始文本一样长度的新字符串
		while (matcher.find()) {
//			 String styleClass = matcher.group("STRING") != null ? "string" : null;
			int start = matcher.start();
			int end = matcher.end();
			int len = end - start;
			String space = createSpaceStr(len);
			String tmp = text.substring(start, end);
//			 logger.info("len = "+len+" ; tmp = " + tmp); 
			txtTmp += text.substring(lastKwEnd, start) + space;
			lastKwEnd = end;
		}
		if (lastKwEnd > 0) {
			String txtEnd = text.substring(lastKwEnd, text.length());
			txtTmp += txtEnd;
		} else {
			txtTmp = text;
		}
//		logger.info("txtTmp = " + txtTmp);

		// TODO 在新字符上面, 提取字sql语句的区间
		String str = txtTmp;
		// 根据区间提炼出真正要执行的sql语句
		List<String> sqls = new ArrayList<>();
		if (str.contains(";")) {
			List<MyRange> idxs = new ArrayList<>();
			String[] all = str.split(";"); // 分割多个语句
			if (all != null && all.length > 0) {
				int ss = 0;
				for (int i = 0; i < all.length; i++) {
					String s = all[i];
					int end = ss + s.length();
					if (end > str.length()) {
						end--;
					}
					MyRange mr = new MyRange(ss, end);
					ss = end + 1;
					idxs.add(mr);
				}
			}
			for (MyRange mr : idxs) {
				int s = mr.getStart();
				int e = mr.getEnd();
				String tmps = text.substring(s, e);
				sqls.add(tmps);
			}
		} else {
			sqls.add(text);
		}

		return sqls;
	}
	
	private static String createSpaceStr(int len) {
		String space = "";
		for (int j = 0; j < len; j++) {
			space += " ";
		}
		return space;
	}
	
	
	// 将注释部分转换为空格字符,保持字符串的长度
		public static String trimCommentToSpace(String sql, String symbol) {
			if (!sql.contains(symbol))
				return sql;
			// 在symbol前插入换行符, 之后就是对行的处理
			String str = sql.replaceAll(symbol, "\n" + symbol);
			if (str.contains("\r")) {
				str = str.replace("\r", "");
			}

			String[] sa = str.split("\n");
			String nstr = "";
			if (sa != null && sa.length > 1) {
				// 遍历行
				for (int i = 0; i < sa.length; i++) {
					String temp = sa[i];
					// 如果不是以symbol开头的字符串就保持到nstr字符串
					if (! StrUtils.beginWith(temp, symbol)) {
						nstr += temp + "\n";
					} else {
						// 生成空白行的字符串
//							String space = ""; 
//							for(int j = 0 ; j < temp.length(); j++){
//								space += " ";
//							}
						String space = createSpaceStr(temp.length());

						nstr = nstr.substring(0, nstr.length() - 1);
						nstr += space + "\n";
					}
				}
			}
			if ("".equals(nstr)) {
				nstr = sql;
			}
//				return nstr.trim();
			return nstr;
		}

	
//	public static void codeAreaCtrlShiftLeft(KeyEvent e, CodeArea codeArea) {
//		logger.info("向左移动光标"+e.getCode() ); 
//		if(e.isShiftDown() && e.isControlDown()) {
//			int anchor =  codeArea.getAnchor(); //光标位置
//			String txt = codeArea.getText(0, anchor); 
//			
//			int[] a = {0, 0, 0};
//			a[0] = txt.lastIndexOf(" ");
//			a[1] = txt.lastIndexOf("\t");
//			a[2] = txt.lastIndexOf("\n");
//			int max = CommonUtility.getMax(a);
//			codeArea.moveTo(max); 
//		}
//	}
	
//	public static void codeAreaCtrlShiftRight(KeyEvent e, CodeArea codeArea) {
//		logger.info("向左移动光标"+e.getCode() ); 
//		if(e.isShiftDown() && e.isControlDown()) {
//			int anchor =  codeArea.getAnchor(); //光标位置
//			String txt = codeArea.getText(anchor); 
//			
//			int[] a = {0, 0, 0};
//			a[0] = txt.indexOf(" ");
//			a[1] = txt.indexOf("\t");
//			a[2] = txt.indexOf("\n");
//			int max = CommonUtility.getMin(a);
//			codeArea.moveTo(anchor+max); 
//		}
//	}

}
