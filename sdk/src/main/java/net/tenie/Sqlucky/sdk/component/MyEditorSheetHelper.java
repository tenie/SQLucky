package net.tenie.Sqlucky.sdk.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.fxmisc.richtext.CodeArea;

import com.jfoenix.controls.JFXButton;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.editor.HighLightingEditorUtils;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.MyRange;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class MyEditorSheetHelper {

	// 将Tab 放入界面
	public static void mainTabPaneAddAllMyTabs(List<MyEditorSheet> ls) {
		if (ls != null && ls.size() > 0) {
			var myTabPane = ComponentGetter.mainTabPane;
			for (MyEditorSheet sheet : ls) {
				myTabPane.getTabs().add(sheet.getTab());
			}
		}

	}

	// 添加空文本的codeTab
	public static MyEditorSheet addEmptyHighLightingEditor() {
		var myTabPane = ComponentGetter.mainTabPane;
		int size = myTabPane.getTabs().size();
		if (ConfigVal.pageSize < 0) {
			ConfigVal.pageSize = size;
		}
		ConfigVal.pageSize++;
		String labe = "Untitled_" + ConfigVal.pageSize + "*";

		MyEditorSheet sheet = new MyEditorSheet(labe, null);
		sheet.showEditor(size);
		ComponentGetter.appComponent.scriptTreeAddItem(sheet);
		return sheet;
	}

	// 通过documentpo 创建一个高亮的编辑器
	public static MyEditorSheet createHighLightingEditor(DocumentPo po) {
		SqluckyEditor sqlEditor = HighLightingEditorUtils.sqlEditor();
		MyEditorSheet myEditorSheet = new MyEditorSheet(po, sqlEditor);
		return myEditorSheet;
	}

	public static void createTabFromSqlFile(DocumentPo scpo) {
		addMyTabByScriptPo(scpo);
	}

	// 添加空文本的codeTab
	public static MyEditorSheet addMyTabByScriptPo(DocumentPo scpo) {
		var myTabPane = ComponentGetter.mainTabPane;
		int size = myTabPane.getTabs().size();
		ConfigVal.pageSize++;

		MyEditorSheet sheet = new MyEditorSheet(scpo, null);
		sheet.showEditor(size);
		ComponentGetter.appComponent.scriptTreeAddItem(sheet);
		return sheet;
	}

	// 获取当前tab中的EditorSheet
	public static MyEditorSheet getActivationEditorSheet() {
		TabPane myTabPane = ComponentGetter.mainTabPane;
		Tab selectionTab = myTabPane.getSelectionModel().getSelectedItem();
		if (selectionTab == null) {
			return null;
		}
		MyEditorSheet myEditorSheet = (MyEditorSheet) selectionTab.getUserData();
		return myEditorSheet;
	}

	// 获取当前tab中的EditorSheet
	public static String getActivationEditorText() {
		MyEditorSheet myEditorSheet = getActivationEditorSheet();
		if (myEditorSheet == null) {
			return "";
		}
		String val = myEditorSheet.getSqluckyEditor().getCodeArea().getText();
		return val;
	}

	// TODO archive script
	public static void archiveAllScript() {
		TabPane mainTabPane = ComponentGetter.mainTabPane;
		var tabs = mainTabPane.getTabs();
		for (var tab : tabs) {
			MyEditorSheet mtb = (MyEditorSheet) tab.getUserData();
			mtb.getDocumentPo().setOpenStatus(0);
			mtb.syncScriptPo();
		}
		tabs.clear();
		var stp = ComponentGetter.scriptTitledPane;
		stp.setExpanded(true);
	}

	// 保存sql文本到硬盘
	public static void saveSqlAction() {
//		MyAreaTab tb = (MyAreaTab) SqluckyEditorUtils.mainTabPaneSelectedTab();
		MyEditorSheet sheet = getActivationEditorSheet();
		saveSqlAction(sheet);
	}

	// 保存sql文本到硬盘
	public static void saveSqlAction(MyEditorSheet sheet) {
		var conn = SqluckyAppDB.getConn();
		try {
			String sql = sheet.getAreaText();// SqlEditor.getTabSQLText(tb);
			var scriptPo = sheet.getDocumentPo();
			String fileName = scriptPo.getFileFullName();
			if (StrUtils.isNotNullOrEmpty(fileName)) {
				FileTools.saveByEncode(fileName, sql, scriptPo.getEncode());
//				CommonUtility.setTabName(tb, FilenameUtils.getName(fileName));
				sheet.setTitle(FilenameUtils.getName(fileName));

			} else {
				String title = scriptPo.getTitle();
				sheet.setModify(false);
				title = StrUtils.trimRightChar(title, "*");
				File file = FileOrDirectoryChooser.showSaveDefault("Save", title, ComponentGetter.primaryStage);
				if (file != null) {
					FileTools.save(file, sql);
					String name = FileTools.fileName(file.getPath());
//					CommonUtility.setTabName(tb, name);
					sheet.setTitle(name);
					scriptPo.setFileFullName(file.getPath());
					fileName = file.getPath();
				}
			}
			sheet.syncScriptPo(conn);
			ComponentGetter.appComponent.setOpenfileDir(fileName);

		} catch (Exception e1) {
			MyAlert.errorAlert(e1.getMessage());
			e1.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	// 当前文本框中, 取消选中的文本
	public static void deselect() {
		getCodeArea().deselect();
	}

	// 获取当前在前台的文本框
	public static CodeArea getCodeArea() {
		var sheet = MyEditorSheetHelper.getActivationEditorSheet();
		if (sheet == null)
			return null;
		return sheet.getSqluckyEditor().getCodeArea();
	}

	// 获取tab的内容 VBox
	public static VBox getTabVbox(Tab tb) {
		return (VBox) tb.getContent();
	}

	public static VBox getTabVbox() {
//		Tab tb = mainTabPaneSelectedTab();
//		return (VBox) tb.getContent();
		var sheet = MyEditorSheetHelper.getActivationEditorSheet();
		var vbox = sheet.getVbox();
		return vbox;
	}

	// 对文本进行高亮设置
	public static void currentSqlCodeAreaHighLighting(String str) {
//		SqluckyTab mtb = currentMyTab();
//		var area = mtb.getSqlCodeArea();
//		area.highLighting(str);
		MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().highLighting(str);
	}

	// area中的所有文本
	public static String getCurrentCodeAreaSQLText() {
		CodeArea code = getCodeArea();
		String sqlText = code.getText();
		return sqlText;
	}

	// 选中的文本
	public static String getCurrentCodeAreaSQLSelectedText() {
		CodeArea code = getCodeArea();
		return code.getSelectedText();
	}

	// 复制当前选中的文本
	public static void copySelectionText() {
		String txt = getCurrentCodeAreaSQLSelectedText();
		CommonUtils.setClipboardVal(txt);
	}

	public static void pasteTextToCodeArea() {
		String val = CommonUtils.getClipboardVal();
		if (StrUtils.isNotNullOrEmpty(val)) {
			var codeArea = MyEditorSheetHelper.getCodeArea();
			int i = codeArea.getAnchor();
			codeArea.insertText(i, val);
//			SqluckyEditorUtils.currentMyTab().getSqlCodeArea().highLighting();
			MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().highLighting();
		}
	}

	// 剪切选中文本
	public static void cutSelectionText() {
		copySelectionText();
		deleteSelectionText();
	}

	// 删除选中文本
	public static void deleteSelectionText() {
		var codeArea = MyEditorSheetHelper.getCodeArea();
		IndexRange ir = codeArea.getSelection();
		codeArea.deleteText(ir);
	}

	public static void currentSqlCodeAreaHighLighting() {

		MyEditorSheet sheet = MyEditorSheetHelper.getActivationEditorSheet();
		sheet.getSqluckyEditor().highLighting();
	}

	// 当前文本框中文本重新高亮
	public static void applyHighlighting() {
		currentSqlCodeAreaHighLighting();
	}
	
	public record matherString(Matcher matcherObj, String newString, List<String> replaceStr ) {}
	// 正则匹配字符串
	public static matherString getStringMatcher(String valStr) {
		String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'|`([^`\\\\]|\\\\.)*`";
		Pattern patn = Pattern.compile(STRING_PATTERN);
		Matcher matr = patn.matcher(valStr);
		List<String> tmpStrLs = new ArrayList<>();
		while (matr.find()) {
			String cutstr = valStr.substring(matr.start(), matr.end());
//			System.out.println("cutstr=" + cutstr);
			tmpStrLs.add(cutstr);
			
		}
		String str2 = matr.replaceAll(" __P_h__ ");
		var rs = new matherString(matr, str2, tmpStrLs);
		return rs;
	}
	
	public static String recoverStringMatcher(matherString msval, String strVal) {
		List<String> ls = msval.replaceStr();
		for(String str : ls) {
			strVal = strVal.replaceFirst(" __P_h__ ", str);
		}
		return strVal;
	}
	
	// 将注释部分转换为空格字符,保持字符串的长度
	public static String trimCommentToSpace(String sql, String symbol) {
		if (!sql.contains(symbol))
			return sql;
		
		// 对包含在字符串中的 symbol 字符串不做处理, 用正则把字符串使用占位符替换掉
		matherString msVal = getStringMatcher(sql);
		String sqlNew = msVal.newString();
		// 在symbol前插入换行符, 之后就是对行的处理
		String str = sqlNew.replaceAll(symbol, "\n" + symbol);
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
				if (!StrUtils.beginWith(temp, symbol)) {
					nstr += temp + "\n";
				} else {
					// 生成空白行的字符串
					String space = createSpaceStr(temp.length());

					nstr = nstr.substring(0, nstr.length() - 1);
					nstr += space + "\n";
				}
			}
		}
		if ("".equals(nstr)) { 
			nstr = sql;
		}else {
			nstr = recoverStringMatcher(msVal, nstr);
		}
		
		return nstr;
	}

	private static String createSpaceStr(int len) {
		String space = "";
		for (int j = 0; j < len; j++) {
			space += " ";
		}
		return space;
	}

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

	// 当前行的 字符串文本;
	public static String getCurrentLineText() {
		CodeArea code = getCodeArea();
		String st = code.getSelectedText();
		String rs = "";
		if (StrUtils.isNotNullOrEmpty(st)) {
			rs = getSelectLineText();
		} else {
			int idx = code.getCurrentParagraph();
			var val = code.getParagraph(idx);
			List<String> ls = val.getSegments();
			rs = ls.get(0);
		}

		return rs;
	}
	
	//选中光标所在行的数据
	public static void selectCurrentLine() {
		CodeArea code = getCodeArea();
		String st = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(st)) {
			code.selectLine();
		}  

	}
	

	// 获取选中行的所有字符,
	public static String getSelectLineText() {
		CodeArea code = MyEditorSheetHelper.getCodeArea();
		var pgs = code.getParagraphs();
		String tmp = "";
		for (int i = 0; i < pgs.size(); i++) {
			var val = code.getParagraphSelection(i);
			if (val.getStart() > 0 || val.getEnd() > 0) {
				tmp += pgs.get(i).getText() + "\n";
			}
		}
		return tmp;
	}

	// 改变样式
	public static void changeThemeAllCodeArea() {
		TabPane myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane != null && myTabPane.getTabs().size() > 0) {
			ObservableList<Tab> tabs = myTabPane.getTabs();
			for (Tab tb : tabs) {
				MyEditorSheet mtb = (MyEditorSheet) tb.getUserData();
				// 修改代码编辑区域的样式
				if (mtb.getSqluckyEditor() != null)
					mtb.getSqluckyEditor().changeCodeAreaLineNoThemeHelper();
				// 修改查找替换的样式如果有的话
				changeFindReplacePaneBtnColor(tb);
			}
		}
	}

	// 修改查找替换的样式如果有的话
	private static void changeFindReplacePaneBtnColor(Tab tb) {
		VBox vbx = (VBox) tb.getContent();
		if (vbx != null && vbx.getChildren().size() > 1) {
			String color = CommonUtils.themeColor();
			for (int i = 0; i < vbx.getChildren().size() - 1; i++) {
				Node nd = vbx.getChildren().get(i);
				if (nd instanceof AnchorPane ap) {
//					AnchorPane ap = (AnchorPane) nd;
					var apchs = ap.getChildren();
					for (Node apnd : apchs) {
						if (apnd instanceof JFXButton btn) {
//							JFXButton btn = (JFXButton) apnd;
							if (btn.getGraphic() != null)
								btn.getGraphic().setStyle("-fx-background-color: " + color + ";");
						}
					}
				}

			}
		}
	}

	// 获取当前选中的区间
	public static IndexRange getSelection() {
		var codeArea = MyEditorSheetHelper.getCodeArea();
		return codeArea.getSelection();
	}

	// 设置选中
	public static void selectRange(IndexRange ir) {
		var codeArea = MyEditorSheetHelper.getCodeArea();
		codeArea.selectRange(ir.getStart(), ir.getEnd());
	}

	public static void ErrorHighlighting(int begin, String str) {
		MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().errorHighLighting(begin, str);
	}
}
