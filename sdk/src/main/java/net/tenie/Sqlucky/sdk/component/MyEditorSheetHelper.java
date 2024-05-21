package net.tenie.Sqlucky.sdk.component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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
import org.fxmisc.richtext.model.Paragraph;

/**
 * 段落 code.getParagraphs();
 * 鼠标光标位置 code.getAnchor();
 */
public class MyEditorSheetHelper {

	// 将Tab 放入界面
	public static void mainTabPaneAddAllMyTabs(List<MyEditorSheet> ls) {
		if (ls != null && ls.size() > 0) {
			var myTabPane = ComponentGetter.mainTabPane;
			for (MyEditorSheet sheet : ls) {
				myTabPane.getTabs().add(sheet);
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
		if(selectionTab instanceof  MyEditorSheet myEditorSheet){
			return myEditorSheet;
		}
//		MyEditorSheet myEditorSheet = (MyEditorSheet) selectionTab.getUserData();
		return null;
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
			if( tab instanceof MyEditorSheet mtb){
//				MyEditorSheet mtb = (MyEditorSheet) tab.getUserData();
				mtb.getDocumentPo().setOpenStatus(0);
				mtb.syncScriptPo();
			}

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
			DocumentPo documentPo = sheet.getDocumentPo();
			String fileName = documentPo.getFileFullName();
			if (StrUtils.isNotNullOrEmpty(fileName)) {
				FileTools.saveByEncode(fileName, sql, documentPo.getEncode());
//				CommonUtility.setTabName(tb, FilenameUtils.getName(fileName));
				sheet.setTitle(FilenameUtils.getName(fileName));

			} else {
				String title = documentPo.getTitle().get();
				sheet.setModify(false);
				title = StrUtils.trimRightChar(title, "*");
				File file = FileOrDirectoryChooser.showSaveDefault("Save", title, ComponentGetter.primaryStage);
				if (file != null) {
					FileTools.save(file, sql);
					String name = FileTools.fileName(file.getPath());
//					CommonUtility.setTabName(tb, name);
					sheet.setTitle(name);
					documentPo.setFileFullName(file.getPath());
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
	public static MyCodeArea getCodeArea() {
		var sheet = MyEditorSheetHelper.getActivationEditorSheet();
		if (sheet != null){
			if( sheet.getSqluckyEditor() != null && sheet.getSqluckyEditor().getCodeArea() instanceof MyCodeArea  myCodeArea){
				return  myCodeArea;
			}
		}

		return null;
//		return sheet.getSqluckyEditor().getCodeArea();
	}

	// 获取当前在前台的文本框
	public static SqluckyEditor getSqluckyEditor() {
		var sheet = MyEditorSheetHelper.getActivationEditorSheet();
		if (sheet == null){
			return null;
		}
		if( sheet.getSqluckyEditor() != null ){
			return   sheet.getSqluckyEditor();
		}
		return null;
//		return sheet.getSqluckyEditor().getCodeArea();
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
		MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().highLighting(str);
	}

	// area中的所有文本
	public static String getCurrentCodeAreaSQLText() {
		CodeArea code = getCodeArea();
        String sqlText = null;
        if (code != null) {
            sqlText = code.getText();
			return sqlText;
        }
		return "";
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

	// 当前行的 字符串文本;
	public static String getCurrentLineText() {
		CodeArea code = getCodeArea();
		String rs = "";
		if(code ==  null){
			return rs;
		}
		String st = code.getSelectedText();

		if (StrUtils.isNotNullOrEmpty(st)) {
			rs = getSelectLineText();
		} else {
			int idx = code.getCurrentParagraph();
			Paragraph<Collection<String>, String, Collection<String>> val = code.getParagraph(idx);
			List<String> ls = val.getSegments();
			rs = ls.getFirst();

		}

		return rs;
	}


	// 光标所在行的首位置, 先获光标所在行, 然后移动到所在行的行首, 然后重新获取光标的位置返回
	public static int cursorCurrentLineAtAreaStart(CodeArea codeArea ){
		int paragraphIdx = codeArea.getCurrentParagraph();
		codeArea.moveTo(paragraphIdx, 0);
		int start = codeArea.getSelection().getStart();
		return start;
	}


	/**
	 * 选中行上的有效字符串, 行首行尾的空白符不要
	 */
	public static void selectCurrentLineTrimText(){
		var code = MyEditorSheetHelper.getCodeArea();
		if(code != null){

			// 行的坐标
			int paragraphIdx = code.getCurrentParagraph();
			// 通过行坐标获取行对象
			Paragraph pgh = code.getParagraph(paragraphIdx);
			// 获取行文本
			String curText = pgh.getText();
			// 移动光标到行首
			code.moveTo(paragraphIdx, 0);
			int anchorIdx = code.getAnchor(); // 获取光标的坐标值
			// 对空白的行首做计数, 后面不用选中空白的字符串
			if(!curText.isEmpty()){
				int count = 0;
				// 替换windows回车符为空
				curText = curText.replace("\r", "");
				// 空格制表符临时字符串
				String blankString = " \t\n";
				// 遍历字符串
				for(int i = 0 ; i < curText.length(); i++){
					String tmp = curText.substring(i, i + 1);
					// 判断子字符是不是空白符, 是的话计数加1, 不是就退出循环
					if(blankString.contains(tmp)){
						count++;
					}else{
						break;
					}

				}
				// 对字符串去除空格, 获取其长度
				String trimCurText = curText.trim();
				// 选中的开始坐标是, 光标在首行时的坐标加上空白字符的计数值
				int bengin = anchorIdx + count;
				// 选中结束的坐标是, 开始坐标 + 取出空格后字符串的长度
				int end  = bengin + trimCurText.length();
				// 执行字符串选中操作
				code.selectRange(bengin, end);

			}
		}
	}

	//选中光标所在行的数据
	public static void selectCurrentLine() {
		CodeArea code = getCodeArea();
		code.selectLine();
//		String st = code.getSelectedText();
//		if (StrUtils.isNullOrEmpty(st)) {
//			code.selectLine();
//		}

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
				if(tb instanceof  MyEditorSheet mtb){
//					MyEditorSheet mtb = (MyEditorSheet) tb.getUserData();
					// 修改代码编辑区域的样式
					if (mtb.getSqluckyEditor() != null)
						mtb.getSqluckyEditor().changeCodeAreaLineNoThemeHelper();
					// 修改查找替换的样式如果有的话
					changeFindReplacePaneBtnColor(tb);
				}

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
