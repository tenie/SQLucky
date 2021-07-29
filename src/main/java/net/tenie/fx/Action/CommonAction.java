package net.tenie.fx.Action;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.tenie.fx.window.ModalDialog;
import net.tenie.fx.window.MyAlert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.jfoenix.controls.JFXButton;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.tenie.fx.Cache.CacheTabView;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.PropertyPo.ProcedureFieldPo;
//import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.CommonFileChooser;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.FindReplaceEditor;
import net.tenie.fx.component.HighLightingSqlCodeAreaContextMenu;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.SqlCodeAreaHighLightingHelper;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.component.container.DBinfoTree;
import net.tenie.fx.component.container.MenuBarContainer;
import net.tenie.fx.config.CommonConst;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.ConnectionDao;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.factory.DBInfoTreeContextMenu;
import net.tenie.fx.main.Restart;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.utility.SaveFile;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.SqlTextDao;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class CommonAction {
	private static Logger logger = LogManager.getLogger(CommonAction.class);
	private static int windowsUiBugTag = 0;
	
	// 给控件加样式
	public static void addCssClass(Node nd, String css) {
		nd.getStyleClass().add(css);
	}
	
	// 控件移除样式
	public static void rmCssClass(Node nd, String css) {
		nd.getStyleClass().remove(css);
	}
	
	// 键盘ESC按下后: 查找表的输入框清空, 选中的文本取消选中, 查找替换面板关闭
	public static void pressBtnESC() {
		ComponentGetter.dbInfoFilter.setText("");
		
		// 代码编辑内容, 取消选中, 高亮恢复复原
		SqlEditor.deselect(); 
		SqlEditor.applyHighlighting();
		
		// 隐藏查找, 替换窗口
		hideFindReplaceWindow();
	}
	// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
	public static RsVal tableInfo() {
		String tableId = ComponentGetter.currentDataTabID();
		String connName =  CacheTabView.getConnName(tableId); // CacheTableDate.getConnName(tableId);
		String tableName = CacheTabView.getTableName(tableId); // CacheTableDate.getTableName(tableId);
		Connection conn =  CacheTabView.getDbConn(tableId); //  CacheTableDate.getDBConn(tableId);   
				
		var alldata =   CacheTabView.getTabData(tableId); //CacheTableDate.getData(tableId);
		DbConnectionPo  dbc = DBConns.get(connName); 
		
//		Button saveBtn = ComponentGetter.dataPaneSaveBtn();
		var dataTableView = ComponentGetter.dataTableView();
		RsVal rv = new RsVal();
		rv.conn = conn; 
		rv.dbconnPo = dbc; 
		rv.tableName = tableName;
//		rv.dbc =  dbc; 
		rv.alldata = alldata;
//		rv.saveBtn = saveBtn;
		rv.dataTableView = dataTableView;
		return rv;
	}
	
	public static RsVal tableInfo(String tableName, String connName, Connection conn ) {

		DbConnectionPo  dbc = DBConns.get(connName);  
		RsVal rv = new RsVal();
		rv.conn = conn; 
		rv.dbconnPo = dbc;   
		rv.tableName = tableName; 
		rv.alldata = null;
//		rv.saveBtn = null;
		rv.dataTableView = null;
		return rv;
	}
	
	
	// ctrl + S 按钮触发, 保存数据或sql文本
	public static void ctrlAndSAction() {
		boolean showStatus = ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		// 如果现在数据表格中的<保存按钮>是亮的(面板还要显示着), 就保存数据 
		if(showStatus) {
			Button btn = ComponentGetter.dataPaneSaveBtn();
			if(btn != null && ! btn.isDisable()) {
				ButtonAction.dataSave(); 
				return ;
			}
		}
		// 保存sql文本到硬盘
		saveSqlAction();
		
		
	}
	
	// 保存sql文本到硬盘
	public static void saveSqlAction() {
		try {
			String sql = SqlEditor.getCurrentCodeAreaSQLText();
			Tab tb = SqlEditor.mainTabPaneSelectedTab();
			logger.info(tb.getId());
			String tbid = tb.getId();
			String fileName = "";
			if (StrUtils.beginWith(tbid, ConfigVal.SAVE_TAG)) {
				fileName = tbid.substring(ConfigVal.SAVE_TAG.length());
				SaveFile.save(fileName, sql);
				CommonUtility.setTabName(SqlEditor.mainTabPaneSelectedTab(), FilenameUtils.getName(fileName));

			} else {
				File file = CommonFileChooser.showSaveDefault("Save", ComponentGetter.primaryStage);
				if (file != null) {
					SaveFile.save(file, sql);
					fileName = SaveFile.fileName(file.getPath());
					CommonUtility.setTabName(SqlEditor.mainTabPaneSelectedTab(), fileName);
					SqlEditor.mainTabPaneSelectedTab().setId(ConfigVal.SAVE_TAG + file.getPath());
					fileName = file.getPath();
				}
			}
			setOpenfileDir(fileName);

		} catch (Exception e1) {
			MyAlert.errorAlert( e1.getMessage());
			e1.printStackTrace();
		}

	}
	// 主窗口关闭事件处理逻辑
	public static void mainPageClose() {
		try {
			 saveApplicationStatusInfo();
		} finally {
			H2Db.closeConn();
			System.exit(0);
		}

	}
	
	// 保存app状态
	public static void saveApplicationStatusInfo() {
		ConnectionDao.refreshConnOrder(); 
		TabPane mainTabPane = ComponentGetter.mainTabPane;
		int SELECT_PANE = mainTabPane.getSelectionModel().getSelectedIndex();
		Connection H2conn = H2Db.getConn();
		SqlTextDao.deleteAll(H2conn);
		for (Tab t : mainTabPane.getTabs()) {
			String idval = t.getId();
			if (StrUtils.isNotNullOrEmpty(idval)) {
				String sql = SqlEditor.getTabSQLText(t);
				if (StrUtils.isNotNullOrEmpty(sql)) {
					CodeArea code = SqlEditor.getCodeArea(t);
					int paragraph = code.getCurrentParagraph() > 11 ? code.getCurrentParagraph() -10 : 0;
					if (StrUtils.beginWith(idval, ConfigVal.SAVE_TAG)) {
						idval = idval.substring(ConfigVal.SAVE_TAG.length());
					} else {
						idval = "";
					}

					String title = CommonUtility.tabText(t); 
					String encode = ComponentGetter.getFileEncode(idval);
					SqlTextDao.save(H2conn, title, sql, idval, encode, paragraph);
				}
			}
		}
		// 保存选择的pane 下标
		SqlTextDao.saveConfig(H2conn, "SELECT_PANE", SELECT_PANE+"");
	}

	// 代码格式化
	public static void formatSqlText() {
		CodeArea code = SqlEditor.getCodeArea();
		String txt = code.getSelectedText();
		if (StrUtils.isNotNullOrEmpty(txt)) {
			IndexRange i = code.getSelection();
			int start = i.getStart();
			int end = i.getEnd();

			String rs = SqlFormatter.format(txt);
			code.deleteText(start, end);
			code.insertText(start, rs);
		} else {
			txt = SqlEditor.getCurrentCodeAreaSQLText();
			String rs = SqlFormatter.format(txt);
			code.clear();
			code.appendText(rs);
		}
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}
	
	
	// sql 压缩
	public static void pressSqlText() {
		CodeArea code = SqlEditor.getCodeArea();
		String txt = code.getSelectedText();
		if (StrUtils.isNotNullOrEmpty(txt)) {
			IndexRange i = code.getSelection();
			int start = i.getStart();
			int end = i.getEnd();

			String rs = StrUtils.pressString(txt); // SqlFormatter.format(txt);
			code.deleteText(start, end);
			code.insertText(start, rs);
		} else {
			txt = SqlEditor.getCurrentCodeAreaSQLText();
			String rs = StrUtils.pressString(txt); //  SqlFormatter.format(txt);
			code.clear();
			code.appendText(rs);
		}
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}
	

	// 代码大写
	public static void UpperCaseSQLTextSelectText() {

		CodeArea code = SqlEditor.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		// 将原文本删除
		code.deleteText(start, end);
		 
		code.insertText(start, text.toUpperCase());
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}

	// 代码小写
	public static void LowerCaseSQLTextSelectText() {

		CodeArea code = SqlEditor.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		// 将原文本删除
		code.deleteText(start, end);
		 
		code.insertText(start, text.toLowerCase());
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}

	// 驼峰命名转下划线
	public static void CamelCaseUnderline() {

		CodeArea code = SqlEditor.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		// 将原文本删除
		code.deleteText(start, end);
	 
		text = StrUtils.CamelCaseUnderline(text);
		code.insertText(start, text);
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}

	// 下划线 轉 驼峰命名
	public static void underlineCaseCamel() {

		CodeArea code = SqlEditor.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		// 将原文本删除
		code.deleteText(start, end);
		// 插入 注释过的文本
		text = StrUtils.underlineCaseCamel(text);
		code.insertText(start, text);
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}

	public static void selectTextAddString() {
		CodeArea code = SqlEditor.getCodeArea();
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();

		// 修正开始下标 , 获取开始之前的字符串, 找到最接近start 的换行符
		String frontTxt = code.getText(0, start);
		int lidx = frontTxt.lastIndexOf('\n'); // 找到最后一个换行符
		if (lidx > 0) {
			lidx = frontTxt.length() - lidx - 1; // 获取换行符的位置, 不包括换行符自己
			start = start - lidx; // start的位置定位到最后一个换行符之后
		} else { // 如果没有找到换行符, 说明在第一行, 把start置为0
			start = 0;
		}
		// 获取文本
		String txt = code.getText(start, end);
		// 添加注释
		if (!StrUtils.beginWith(txt.trim(), "--")) {
			String temp = "";
			for (int t = 0; t < start; t++) {
				temp += " ";
			}
			txt = txt.replaceAll("\n", "\n-- ");
			txt = temp + "\n-- " + txt;
			logger.info(txt);
			int k = txt.indexOf('\n', 0);
			while (k >= 0) {
				code.insertText(k, "-- ");
				k = txt.indexOf('\n', k + 1);
			}
		} else {// 去除注释
			String valStr = "";

			String[] strArr = txt.split("\n");
			String endtxt = "";
			if (strArr.length > 0) {
				endtxt = txt.substring(txt.length() - 1);
				for (String val : strArr) {
					if (StrUtils.beginWith(val.trim(), "--")) {
						valStr += val.replaceFirst("-- ", "") + "\n";
					} else {
						valStr += val + "\n";
					}
				}
			}
			if (!"\n".equals(endtxt)) { // 去除最后一个换行符
				valStr = valStr.substring(0, valStr.length() - 1);
			}
			// 将原文本删除
			code.deleteText(start, end);
			// 插入 注释过的文本
			code.insertText(start, valStr);
		}
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}
	// 添加tab符号
	public static void add4Space() { 
		
		String replaceStr1 = "\n    ";
		String replaceStr2 = "    ";
		
		CodeArea code = SqlEditor.getCodeArea();
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		int begin = start;
		int over = end;
		
		// 修正开始下标 , 获取开始之前的字符串, 找到最接近start 的换行符
		String frontTxt = code.getText(0, start);
		int lidx = frontTxt.lastIndexOf('\n'); // 找到最后一个换行符
		if (lidx > 0) {
			lidx = frontTxt.length() - lidx - 1; // 获取换行符的位置, 不包括换行符自己
			start = start - lidx; // start的位置定位到最后一个换行符之后
		} else { // 如果没有找到换行符, 说明在第一行, 把start置为0
			start = 0;
		}
		// 获取文本
		String txt = code.getText(start, end);
		logger.info("txt = " + txt); 
			String temp = "";
			for (int t = 0; t < start; t++) {
				temp += " ";
			}
			txt = txt.replaceAll("\n", replaceStr1);
			txt = temp + replaceStr1 + txt;
			logger.info(txt);
			int k = txt.indexOf('\n', 0);
			int count  = 0;
			while (k >= 0) {
				count++;
				code.insertText(k, replaceStr2);
				k = txt.indexOf('\n', k + 1);
			} 
		code.selectRange(begin, over+ (count*4)); 
	}
	// 减少前置tab符号
	public static void minus4Space() {  
		CodeArea code = SqlEditor.getCodeArea();
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd(); 
		
		// 修正开始下标 , 获取开始之前的字符串, 找到最接近start 的换行符
		String frontTxt = code.getText(0, start);
		int lidx = frontTxt.lastIndexOf('\n'); // 找到最后一个换行符
		if (lidx > 0) {
			lidx = frontTxt.length() - lidx - 1; // 获取换行符的位置, 不包括换行符自己
			start = start - lidx; // start的位置定位到最后一个换行符之后
		} else { // 如果没有找到换行符, 说明在第一行, 把start置为0
			start = 0;
		}
		// 获取文本
		String txt = code.getText(start, end);
		
		String valStr = "";

		String[] strArr = txt.split("\n");
		String endtxt = "";
		if (strArr.length > 0) {
			endtxt = txt.substring(txt.length() - 1);
			// 遍历每一行
			for (String val : strArr) {
				// 获取没有空格的纯字符串
				String trimStr = val.trim();
				// 找到纯字符串, 在原本行里的下标位置
				int subscript  = val.indexOf(trimStr);
//				下标为0 就是没有必要去除空格
				if(subscript == 0) {
					valStr += val + "\n";
				}else { // 开始去除行的前4个空格
					String SpaceStr  = val.substring(0, subscript);
					// 如果有tab键就 换成4个空格
					if(SpaceStr.contains("\t")) {
						SpaceStr = SpaceStr.replaceAll("\t", "    ");
					}
					// 如果空格大于4个减去4个, 否则空格归零
					if( SpaceStr.length() > 4) {
						SpaceStr = SpaceStr.substring(3, SpaceStr.length());
					}else {
						SpaceStr = "";
					} 
					valStr += SpaceStr+ trimStr + "\n";
				}
			}
		}
		if (!"\n".equals(endtxt)) { // 去除最后一个换行符
			valStr = valStr.substring(0, valStr.length() - 1);
		}
		// 将原文本删除
		code.deleteText(start, end);
		// 插入 注释过的文本
		code.insertText(start, valStr);
		
		code.selectRange(start, start+valStr.length()); 
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}
	
	private static String paragraphPrefixBlankStr(CodeArea codeArea, int anchor) {
		int a = anchor;
		int b = anchor + 1;
		int len = codeArea.getText().length();
		
		StringBuilder strb2 = new StringBuilder("");
	
		while(true) {
				if(a >= len) break;
				 
			    String sc =  codeArea.getText(a, b);  
				if(" ".equals(sc) || "\t".equals(sc)) {
					strb2.append(sc);
				}else {
					break;
				} 
				a++;
				b++;
				
		}
		
		return strb2.toString();
	}
	
	public static void addNewLine(KeyEvent e, CodeArea codeArea) {

		// 换行缩进, 和当前行的缩进保持一致
		logger.info("换行缩进 : "+e.getCode() );
		String seltxt = codeArea.getSelectedText();
		int idx = codeArea.getCurrentParagraph(); // 获取当前行号
		int anchor =  codeArea.getAnchor(); //光标位置
		
		if(seltxt.length() == 0) {//没有选中文本, 存粹换行, 才进行缩进计算 
			// 根据行号获取该行的文本
			Paragraph<Collection<String>, String, Collection<String>>   p = codeArea.getParagraph(idx);
			String ptxt = p.getText();
			
			// 获取文本开头的空白字符串
			if(StrUtils.isNotNullOrEmpty(ptxt)) { 
				
				// 一行的前缀空白符
				String strb = StrUtils.prefixBlankStr(ptxt);
				int countSpace = strb.length();
				
				// 获取光标之后的空白符, 如果后面的字符包含空白符, 换行的时候需要修正前缀补充的字符, 补多了换行越来越长 
//				String afterAnchorText =  codeArea.getText(anchor,codeArea.getText().length());
//				String strafter = StrUtils.prefixBlankStr(afterAnchorText);
				String strafter = paragraphPrefixBlankStr(codeArea , anchor);
				
				String fstr = "";
				if(strafter.length() > 0 &&  strb.length() >  strafter.length()) {
					fstr = strb.substring(0 , strb.length() - strafter.length());
				}else {
					fstr = strb;
				}
				
				// 在新行插入空白字符串
				if(fstr.length() > 0) {
					e.consume();
					String addstr = "\n"+fstr; 
					codeArea.insertText(anchor , addstr);
					codeArea.moveTo(idx + 1, countSpace);
				}else {
					//如果光标在起始位, 那么回车后光标移动到起始再会到回车后的位置, 目的是防止页面不滚动
					if( anchor == 0) {
						Platform.runLater(() -> {
							codeArea.moveTo(0); // 光标移动到起始位置
							Platform.runLater(() -> {
							    codeArea.moveTo(1);
							});  
						});
					}else {
						e.consume();   
						codeArea.insertText(anchor , "\n");
					}
				}
				
			}
			
		}
		
	
	}
	
	// 代码添加注释-- 或去除注释
	public static void addAnnotationSQLTextSelectText() {

		CodeArea code = SqlEditor.getCodeArea();
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();

		// 修正开始下标 , 获取开始之前的字符串, 找到最接近start 的换行符
		String frontTxt = code.getText(0, start);
		int lidx = frontTxt.lastIndexOf('\n'); // 找到最后一个换行符
		if (lidx > 0) {
			lidx = frontTxt.length() - lidx - 1; // 获取换行符的位置, 不包括换行符自己
			start = start - lidx; // start的位置定位到最后一个换行符之后
		} else { // 如果没有找到换行符, 说明在第一行, 把start置为0
			start = 0;
		}
		// 获取文本
		String txt = code.getText(start, end);
		// 添加注释
		if (!StrUtils.beginWith(txt.trim(), "--")) {
			txt = txt.replaceAll("\n", "\n-- ");
			txt = "-- " + txt; 
			code.deleteText(start, end);
			code.insertText(start, txt);

			//TODO  
//			String temp = "";
//			for (int t = 0; t < start; t++) {
//				temp += " ";
//			}
//			txt = txt.replaceAll("\n", "\n-- ");
//			txt = temp + "\n-- " + txt;
//			logger.info(txt);
//			int k = txt.indexOf('\n', 0);
//			while (k >= 0) {
//				code.insertText(k, "-- ");
//				k = txt.indexOf('\n', k + 1);
//			}
		} else {// 去除注释
			String valStr = "";

			String[] strArr = txt.split("\n");
			String endtxt = "";
			if (strArr.length > 0) {
				endtxt = txt.substring(txt.length() - 1);
				for (String val : strArr) {
					if (StrUtils.beginWith(val.trim(), "--")) {
						valStr += val.replaceFirst("-- ", "") + "\n";
					} else {
						valStr += val + "\n";
					}
				}
			}
			if (!"\n".equals(endtxt)) { // 去除最后一个换行符
				valStr = valStr.substring(0, valStr.length() - 1);
			}
			// 将原文本删除
			code.deleteText(start, end);
			// 插入 注释过的文本
			code.insertText(start, valStr);
		}
		SqlCodeAreaHighLightingHelper.applyHighlighting(code);
	}

	// 打开sql文件
	public static void openSqlFile(String encode) {
		try {
			File f = CommonFileChooser.showOpenSqlFile("Open", ComponentGetter.primaryStage);
			if (f == null)
				return;
			String val = FileUtils.readFileToString(f, encode);
			String id = "";
			String tabName = "";
			ComponentGetter.fileEncode.put( f.getPath(), encode);
			if (StrUtils.isNotNullOrEmpty(f.getPath())) {
				id = ConfigVal.SAVE_TAG + f.getPath();
				tabName = SaveFile.fileName(f.getPath());
				setOpenfileDir(f.getPath());
			}
			SqlEditor.createTabFromSqlFile(val, tabName, id);
		} catch (IOException e) {
			MyAlert.errorAlert( e.getMessage());
			e.printStackTrace();
		}
	}

	// 查看表明细(一行数据) 快捷键
	public static void shortcutShowDataDatil() {
		AnchorPane fp = ComponentGetter.dataPane();
		Button btn = (Button) fp.getChildren().get(1);
		MouseEvent me = myEvent.mouseEvent(MouseEvent.MOUSE_CLICKED, btn);
		Event.fireEvent(btn, me);
	}

	public static void hideFindReplaceWindow() {
		VBox b = SqlEditor.getTabVbox();
		int bsize = b.getChildren().size();
		if (bsize > 1) { 
				FindReplaceEditor.delFindReplacePane();
		} 
		 
	}
	
	public static void findReplace(boolean isReplace) {
		VBox b = SqlEditor.getTabVbox();
		int bsize = b.getChildren().size();
		if (bsize > 1) {
			// 如果查找已经存在, 要打开替换, 就先关光再打开替换查找
			if (bsize == 2 && isReplace) {
				FindReplaceEditor.delFindReplacePane();
				findReplace(isReplace);
			} else // 如果替换已经存在, 要打开查找, 就先关光再打开查找
			if (bsize == 3 && !isReplace) {
				FindReplaceEditor.delFindReplacePane();
				findReplace(isReplace);
			} else {
				FindReplaceEditor.delFindReplacePane();
			}

		} else {
			FindReplaceEditor.createFindPane(isReplace);
		}
	}

	public static void hideLeftBottom() {
		JFXButton btnLeft = AllButtons.btns.get("hideLeft");
		JFXButton btnBottom = AllButtons.btns.get("hideBottom");
		boolean leftp = ComponentGetter.treeAreaDetailPane.showDetailNodeProperty().getValue();
		boolean bootp = ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		if (leftp || bootp) {
			ComponentGetter.treeAreaDetailPane.setShowDetailNode(false);
			btnLeft.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-right"));

			ComponentGetter.masterDetailPane.setShowDetailNode(false);
			btnBottom.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-up"));
		} else {
			ComponentGetter.treeAreaDetailPane.setShowDetailNode(true);
			btnLeft.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-left"));

			ComponentGetter.masterDetailPane.setShowDetailNode(true);
			btnBottom.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-down"));
		}

	}

	public static void hideLeft() {
		JFXButton btn = AllButtons.btns.get("hideLeft");
		if (ComponentGetter.treeAreaDetailPane.showDetailNodeProperty().getValue()) {
			ComponentGetter.treeAreaDetailPane.setShowDetailNode(false);
			btn.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-right"));
																							
		} else {
			ComponentGetter.treeAreaDetailPane.setShowDetailNode(true);
			btn.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-left"));
																					
		}
	}

	public static void hideBottom() {
		JFXButton btn = AllButtons.btns.get("hideBottom");
		boolean showStatus = !ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		hideShowBottomHelper(showStatus, btn);
		if(showStatus ) { 
			CommonAction.escapeWindowsUiBug(); 
			 
		}

	}
	
	//TODO 显示或隐藏 数据面板, 修改控制按钮图标
	public static void hideShowBottomHelper(boolean isShow, JFXButton btn) {
		ComponentGetter.masterDetailPane.setShowDetailNode(isShow);
		if (isShow) {
			btn.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-down"));
		} else {
			btn.setGraphic(ImageViewGenerator.svgImageDefActive("caret-square-o-up"));
		}

	}

	// 底部数据展示面板是否显示
	public static void showDetailPane() {
		JFXButton btn = AllButtons.btns.get("hideBottom");
		boolean showStatus = !ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		if (showStatus) {
			hideShowBottomHelper(true, btn); 
			escapeWindowsUiBug();
		}
		
	}

	// 连接测试
	public static boolean isAliveTestAlert(DbConnectionPo connpo, Button testBtn) {
		Thread t = new Thread() {
			public void run() {
				connpo.getConn();
				Platform.runLater(() -> {
					if (connpo.isAlive()) {
						MyAlert.infoAlert("Information!", "  Successfully  !");
						connpo.closeConn();
						testBtn.setStyle("-fx-background-color: green ");
					} else {
						MyAlert.errorAlert(" Cannot connect ip:" + connpo.getHost() + " port:" + connpo.getPort() + "  !");

					}

				});
			}
		};
		t.start();
		return false;
	}
	//收缩treeview
	public static void shrinkTreeView() {
		TreeItem<TreeNodePo>  root =ComponentGetter.treeView.getRoot();
		shrinkUnfoldTreeViewHelper(root, false);
	}
	 
	
	
	//展开treeview
	public static void unfoldTreeView() {
		TreeItem<TreeNodePo>  root =ComponentGetter.treeView.getRoot();
		root.setExpanded(true);
		shrinkUnfoldTreeViewHelper(root, true);
	}
	
	private  static void shrinkUnfoldTreeViewHelper(TreeItem<TreeNodePo>  node, boolean tf) {
		ObservableList<TreeItem<TreeNodePo>>  subNodes = node.getChildren();
		for (int i = 0; i < subNodes.size(); i++) {
			TreeItem<TreeNodePo>  subnode = subNodes.get(i);
			if(subnode.getChildren().size() > 0 ) {
				subnode.setExpanded(tf);
				shrinkUnfoldTreeViewHelper(subnode, tf);
			}
		}
	}
	// 保证theme状态
	public static void saveThemeStatus(String val) {
		ConfigVal.THEME = val;
		Connection conn =  H2Db.getConn();
		H2Db.setConfigVal(conn, "THEME", val) ;
		H2Db.closeConn();
	}
	
	// 设置整体样式
	public static void setTheme(String val ) {
		saveThemeStatus(val); 
		// 根据新状态加载新样式
		loadCss(ComponentGetter.primaryscene); 
		SqlEditor.changeThemeAllCodeArea() ; 
		changeSvgColor(); // 修改按钮颜色
	}
	// 设置整体样式
	public static void setThemeRestart(String val ) {
		
		// 询问是否重启app, 如果不重启再重新加载样式
		CommonAction.changeThemeRestartApp( val ) ;
		
	}
	
	
	public static void changeSvgColor() {
		String color = "#1C94FF";
		if(ConfigVal.THEME.equals(CommonConst.THEME_YELLOW)) {
			color = "#FDA232";
		}
		
		
		List<ButtonBase> allBtns = 	ButtonFactory.btns; // 
		allBtns.addAll(ComponentGetter.dataPaneBtns());  //数据面板中的按钮 
		for(ButtonBase reg :allBtns) {
			reg.getGraphic().setStyle("-fx-background-color: " + color + ";");
		}
		
		// 树右键菜单
		for(MenuItem it :DBInfoTreeContextMenu.menuItems) {
			it.getGraphic().setStyle("-fx-background-color: " + color + ";");
		}
		
		// datapane menuitem
		for(MenuItem it: ComponentGetter.dataPaneMenuItems()) {
			it.getGraphic().setStyle("-fx-background-color: " + color + ";");
		}
		
		for(MenuItem it: MenuBarContainer.barMenus) {
			if(it.getGraphic() != null)
				it.getGraphic().setStyle("-fx-background-color: " + color + ";");
		}
		// sql编辑页面的右键按钮
		for(MenuItem it: HighLightingSqlCodeAreaContextMenu.menuItems) {
			if(it.getGraphic() != null)
				it.getGraphic().setStyle("-fx-background-color: " + color + ";");
		}
		
		
		
		if( DBinfoTree.icon != null ) {
			DBinfoTree.icon.setStyle("-fx-background-color: " + color + ";");
			
		}
		
	}
	
	public static void setOpenfileDir(String val) {
		Connection conn =  H2Db.getConn();
		H2Db.setConfigVal(conn, "OPEN_FILE_DIR", val) ;
		H2Db.closeConn();
		ConfigVal.openfileDir = val;
	}
	
	
	// 根据括号( 寻找配对的 结束)括号所在的位置.
	public static int findBeginParenthesisRange(String text, int start, String pb , String pe) {
		String startStr = text.substring(start);
		int end = 0;
		int strSz =  startStr.length();
		if( strSz == 0) return end;
		if( ! startStr.contains(pe))  return end;
		int idx = 1;
		for(int i = 0; i < startStr.length(); i++ ) {
			if(idx == 0) break;
			String tmp = startStr.substring(i, i+1);
			
			if( pe.equals(tmp)) {
				idx--;
				end = i;
			}else if( pb.equals(tmp) ) {
				idx++;
			}
		} 
		return start + end;
	}
	
	// 用在存储过程, 第一个括号内的字符串, 
	public static String firstParenthesisInsideString(String text) {
		// 括号开始的位置, 不包括括号自己
		int begin = text.indexOf("(") + 1;
		int end = findBeginParenthesisRange(text, begin ,"(", ")");
		String str = text.substring(begin, end);
		return str;
	}
	
	//TODO 获取 IN 字段
	public static List<String> findInField(String sql){
		String pstr = firstParenthesisInsideString(sql);
		 List<String> list = new ArrayList<>();
		String[] sarr = pstr.split(",");
		for(String str: sarr) {
			str = str.trim();
			if(str.length() > 0) {
				 int idx = str.toUpperCase().indexOf("IN");
				 if(idx == 0) {
					 list.add(str);
				 }
			}
		} 
		return list;
	}
	
	// 判断是否是没有参数的存储过程, 没有参数 返回true
	public static boolean procedureIsNoParameter(String sqlddl) {
//		sqlddl = StrUtils.pressString(sqlddl).toUpperCase();
		if(sqlddl.indexOf("(") > -1) {
			String tmp = sqlddl.substring(0, sqlddl.indexOf("("));
			if(tmp.contains(" BEGIN ")) {
				return true;
			}else {
				return false;
			}
			
		}
		return true;
//		sqlddl = sqlddl.substring(0, sqlddl.indexOf(" BEGIN "));
		 
	}
	
	//TODO 从存储过程语句中提取参数
	public static List<ProcedureFieldPo> getProcedureFields(String ddl){
		 List<ProcedureFieldPo> rs = new ArrayList<>();
		 ddl = StrUtils.multiLineCommentToSpace(ddl);
		 ddl = StrUtils.trimCommentToSpace(ddl, "--");
		 // 给ddl分词, 找到过程名称后面的参数列表
		 ddl = StrUtils.pressString(ddl).toUpperCase();
		 if( procedureIsNoParameter(ddl) ) { // 没有参数直接返回
			 return rs;
		 }
		 
//		 ddl = ddl.substring(0, ddl.indexOf(" BEGIN "));
		 
		 String val = firstParenthesisInsideString(ddl);
		 val = val !=null ? val.trim() : "";
		 if(val.length() > 1) {
			String args[] =  val.split(",");
			for(int i=0; i<args.length; i++) {
				String str = args[i].trim();
			
				String fields[] = str.split(" ");
				String inout = fields[0].toUpperCase();
				boolean in = inout.contains("IN");
				boolean out = inout.contains("OUT");
				
				ProcedureFieldPo po = new ProcedureFieldPo();
				po.setName(str); 
				po.setIn(in);
				po.setOut(out); 
//				po.setType( fields[2]);
				rs.add(po);
			}
		 }
		 System.out.println(rs);
		 return rs;
	}
	
//	public static void main(String[] args) {
//		String sql = "CREATE PROCEDURE P_GEN_PART_MONREPORT (\r\n"
//				+ "  IN AENTITY_CODE CHARACTER(8),\r\n"
//				+ "  INOUT RETURN_CODE INTEGER,\r\n"
//				+ "  OUT RETURN_MSG VARCHAR(60)\r\n"
//				+ ") BEGIN DECLARE CURYEAR CHAR(4);aaa";
////		sql = "adasda()sddasd";
////		String val = firstParenthesisInsideString(sql);
////		System.out.println(val);
//		getProcedureFields(sql);
//		
//	}
	
	
	// 根据括号) 向前寻找配对的括号( 所在的位置.
	public static int findEndParenthesisRange(String text, int start, String pb , String pe) {
		String startStr = text.substring(0, start);
		int end = 0;
		int strSz =  startStr.length();
		if( strSz == 0) return end;
		if( ! startStr.contains(pe))  return end;
		int idx = 1;
		for(int i = start; i != 0; i--) {
			if(idx == 0) break;
			String tmp = startStr.substring(i-1, i);
			
			if( pe.equals(tmp)) {
				idx--;
				end = i;
			}else if( pb.equals(tmp) ) {
				idx++;
			}
		}		
		return  end;
	}
	
	
	

	// 根据括号( 寻找配对的 结束)括号所在的位置.
	public static int findBeginStringRange(String text, int start, String pb , String pe) {
		String startStr = text.substring(start).toUpperCase();
		int end = 0;
		int strSz =  startStr.length();
		if( strSz == 0) return end;
		if( ! startStr.contains(pe))  return end;
		int idx = 1;
		int peSz = pe.length();
		for(int i = 0; i < strSz; i++ ) {
			if(idx == 0) break;
			String tmp = startStr.substring(i, i+peSz);
			if( pb.equals(tmp) ) {
				idx++;
			}
			if( pe.equals(tmp)) {
				idx--;
				end = i;
			}
		} 
		return start + end;
	}
	
	// 根据括号) 向前寻找配对的括号( 所在的位置.
	public static int findEndStringRange(String text, int start, String pb , String pe) {
		String startStr = text.substring(0, start).toUpperCase();;
		int end = 0;
		int strSz =  startStr.length();
		if( strSz == 0) return end;
		if( ! startStr.contains(pe))  return end;
		int idx = 1;
		int peSz = pe.length();
		for(int i = start; i != 0; i--) {
			if(idx == 0) break;
			String tmp = startStr.substring(i-peSz, i);
			if( pb.equals(tmp) ) {
				idx++;
			}
			if( pe.equals(tmp)) {
				idx--;
				end = i;
			}
		}		
		return  end;
	}
	
	// 引号( ' " `) 之间的字符串区间
	public static IndexRange findStringRange(String text, int start, String pe) {
		IndexRange ir = new IndexRange(0, 0);
		int strSz =  text.length();
		if( strSz == 0) return ir;
		int idx = -1;
		for(int i = 0; i < strSz; i++ ) {
			String tmp = text.substring(i, i+1);
			if(tmp.equals(pe) ) {
				if(idx == -1) {
					idx = i;
				}else {
					if( idx == start || i == start) {
						ir =  new IndexRange(idx+1, i); 
						break;
					}  
					idx = -1;
					
					
				}
			}  
		} 
		return ir;
	}
	
	
	static Map<String, String> charMap = new HashMap<>();
	static Map<String, String> charMapPre = new HashMap<>(); 
	static List<String> charList = new ArrayList<>();
	
	
	static {
		charMap.put("(" , ")");
		charMap.put("[" , "]");
		charMap.put("{" , "}"); 
		
		charMapPre.put(")" , "("); 
		charMapPre.put("]" , "[");
		charMapPre.put("}" , "{");
		
		charList.add("\"");
		charList.add("'");
		charList.add("`");
		 
		
	}
	
	
	// 针对括号() {} []的双击, 选中括号内的文本
	 // 如果选中了内容, 就会返回false
	public static boolean selectSQLDoubleClicked( CodeArea codeArea) {
		boolean tf = true;
		String str  = codeArea.getSelectedText();
		String trimStr = str.trim();
		int strSz = trimStr.length();
		if(strSz > 0 ) {
			IndexRange i = codeArea.getSelection(); // 获取当前选中的区间 
    		int start = i.getStart();
    		Set<String> keys = charMap.keySet();
    		
    		for(String key : keys) { 
    			if(trimStr.endsWith(key)) { 
    				String val = charMap.get(key);
    	    		int endIdx = str.lastIndexOf(key);
    	    		int is = start + endIdx +1; 
    	    		int end = CommonAction.findBeginParenthesisRange(codeArea.getText(), is, key , val );
    	    		if( end != 0 && end > is) {
    	    			codeArea.selectRange(is, end);
    	    		}
    	    		tf = false;
    	    		break;
    			}
    		}
    		
    		if(tf) {
    			keys = charMapPre.keySet();
    			for(String key : keys) { 
        			if(trimStr.endsWith(key)) { 
        				String val = charMapPre.get(key);        	    		
        	    		int endIdx = str.lastIndexOf(key);
        	    		int end = start + endIdx ; 
        	    		int is = CommonAction.findEndParenthesisRange(codeArea.getText(), end, key , val);
        	    		if( end > is) {
        	    			codeArea.selectRange(is, end);
        	    		}
        	    		
        	    		tf = false;
        	    		break;
        			}
        		}
    		}
			if (tf) {
				for(String v: charList) {
					if (trimStr.endsWith( v)) {
						int endIdx = str.lastIndexOf(v);
						int end = start + endIdx;
						IndexRange ir = CommonAction.findStringRange(codeArea.getText(), end, v);
						if ( (ir.getStart() + ir.getEnd()) > 0) {
							codeArea.selectRange(ir.getStart(), ir.getEnd());
						}

						tf = false; 
						break;
					}
				} 
			} 
    		
    		if(tf) {
    			if(trimStr.toUpperCase().endsWith("SELECT")) {
    	    		int endIdx = str.toUpperCase().lastIndexOf("SELECT"); 
    	    		int is = start + endIdx + 6; 
    	    		int end = CommonAction.findBeginStringRange(codeArea.getText(), is, "SELECT", "FROM");
    	    		if( end != 0 && end > is) {
    	    			codeArea.selectRange(is - 6 , end + 4);
    	    		}
    	    		tf = false; 
    			}else if(trimStr.toUpperCase().endsWith("FROM")) {
    	    		int endIdx = str.toUpperCase().lastIndexOf("FROM");
    	    		int end = start + endIdx ; 
    	    		int is = CommonAction.findEndStringRange(codeArea.getText(), end, "FROM", "SELECT");
    	    		if( end > is) {
    	    			codeArea.selectRange(is - 6, end + 5);
    	    		}
    	    		tf = false; 
    			} 
    			
    		} 
    		
    		if(tf) {
    			if(trimStr.toUpperCase().endsWith("CASE")) {
    	    		int endIdx = str.toUpperCase().lastIndexOf("CASE");
    	    		int is = start + endIdx + 4; 
    	    		int end = CommonAction.findBeginStringRange(codeArea.getText(), is, "CASE", "END");
    	    		if( end != 0 && end > is) {
    	    			codeArea.selectRange(is - 4, end + 3);
    	    		}
    	    		tf = false; 
    			}else if(trimStr.toUpperCase().endsWith("END")) {
    	    		int endIdx = str.toUpperCase().lastIndexOf("END");
    	    		int end = start + endIdx ; 
    	    		int is = CommonAction.findEndStringRange(codeArea.getText(), end, "END", "CASE");
    	    		if( end > is) {
    	    			codeArea.selectRange(is - 4, end + 4);
    	    		}
    	    		tf = false; 
    			}   
    		} 
    		 
    	} 
		return tf;
	}
	
	
    public static void  setStyleSpans(CodeArea codeArea , int idx, int size) {
    	StyleSpansBuilder<Collection<String>> spansBuilder  = new StyleSpansBuilder<>();
		spansBuilder.add(Collections.emptyList(), 0);
        spansBuilder.add(Collections.singleton("findparenthesis"),  size);
        codeArea.setStyleSpans( idx , spansBuilder.create());
    }
	
	// 鼠标单击找到括号对, 标记一下
	public static boolean  oneClickedFindParenthesis( CodeArea codeArea) {
		boolean tf = true;
	
		int anchor = codeArea.getAnchor();
		int start = anchor == 0 ? anchor : anchor - 1 ; 
		int end = anchor +1;
		
		String text = codeArea.getText();
		if(text.length() == anchor) {
			return false;
		}
	 
		
		String str  = codeArea.getText(start, end);// codeArea.getSelectedText();
		String trimStr = str.trim();
		int strSz = trimStr.length();
//		logger.info("单击选中 |"+ trimStr+"|" );
		if(strSz > 0 ) { 
			logger.info("鼠标单击找到括号对, 标记一下 |"+ trimStr+"|" );
			
    		Set<String> keys = charMap.keySet();
    		
    		for(String key : keys) { 
    			if(trimStr.endsWith(key)) { 
    				String val = charMap.get(key);
    	    		int endIdx = str.lastIndexOf(key);
    	    		int is = start + endIdx +1; 
    	    		end = CommonAction.findBeginParenthesisRange(codeArea.getText(), is, key , val );
    	    		if( end != 0 && end > is) {
    	    			
    	    			setStyleSpans( codeArea, is -1, 1);
    	    			setStyleSpans( codeArea, end, 1);
    	    			
    	    			
    	    		}
    	    		tf = false;
    	    		break;
    			}
    		}
    		
    		if(tf) {
    			keys = charMapPre.keySet();
    			for(String key : keys) { 
        			if(trimStr.endsWith(key)) { 
        				String val = charMapPre.get(key);        	    		
        	    		int endIdx = str.lastIndexOf(key);
        	    	    end = start + endIdx ; 
        	    		int is = CommonAction.findEndParenthesisRange(codeArea.getText(), end, key , val);
        	    		if( end > is) {
        	    			setStyleSpans( codeArea, is -1, 1);
        	    			setStyleSpans( codeArea, end, 1);
        	    			
        	    		}
        	    		
        	    		tf = false;
        	    		break;
        			}
        		}
    		}
    		
    		 
    		
	   }
	return tf;
	}
	
	
	// 加载css样式
	public static void loadCss(Scene scene) {
//		if(scene ==null) return;
		scene.getStylesheets().clear();
		logger.info(ConfigVal.THEME);
		if(ConfigVal.THEME.equals( CommonConst.THEME_DARK )) {
			scene.getStylesheets().addAll(ConfigVal.cssList);
		}else if(ConfigVal.THEME.equals( CommonConst.THEME_LIGHT)) { 
			scene.getStylesheets().addAll(ConfigVal.cssListLight); 
			
		}else if(ConfigVal.THEME.equals( CommonConst.THEME_YELLOW)) { 
			scene.getStylesheets().addAll(ConfigVal.cssListYellow); 
			
		}
		
		// 加载自定义的css
		String path = FileUtils.getUserDirectoryPath() + "/.sqlucky/font-size.css"; 
		File cssf = new File(path); 
		if( ! cssf.exists() ) { 
			setFontSize(14);
		}
		String uri = Paths.get(path).toUri().toString();  
		 
		scene.getStylesheets().add(uri);
		
	     
	}
	
	// 设置字符大小
	static public void setFontSize(int i) { 
		String val = 
				"/*"+i+"*/ \n" +
				".myLineNumberlineno{ \n" + 
				"	-fx-font-size :	"+i+"; \n" + 
				"} \n" +
				".code-area{\n"+
				"	-fx-font-size :	"+i+"; \n" +
			    "} \n" +
				"";
		try {
			String path = FileUtils.getUserDirectoryPath() + "/.sqlucky/font-size.css";
			SaveFile.save( path , val);
			loadCss(ComponentGetter.primaryscene);  
			
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	//TODO 改变字体大小
	public static void changeFontSize(boolean isPlus) {
		// 获取当前的 size
		if(ConfigVal.FONT_SIZE == -1) {
			String path = FileUtils.getUserDirectoryPath() + "/.sqlucky/font-size.css";
			String str = SaveFile.read(path);
			String val = str.split("\n")[0];
			val = val.substring(2, val.lastIndexOf("*/"));
			System.out.println(val);
			ConfigVal.FONT_SIZE = Integer.valueOf(val);
		}
		int sz = ConfigVal.FONT_SIZE ;
		if(isPlus) {
			sz +=1;
		}else {
			sz -=1;
		}
		
		if(sz > 20) {
			sz = 20;
		}
		
		if(sz < 10) {
			sz = 10;
		}
		setFontSize(sz);
		for(CodeArea code : SqlEditor.getAllCodeArea() ) {
			logger.info(code.getStyle());
			String txt = code.getText();  
			code.replaceText(0, txt.length(), txt);
			SqlCodeAreaHighLightingHelper.applyHighlighting(code);
		}
		
		ConfigVal.FONT_SIZE = sz;
		
	}
	 
	
	// 关闭 数据页, 清理缓存
	public static void clearDataTable(Tab tb) {
		TabPane tabPane = ComponentGetter.dataTab; 
		long begintime = System.currentTimeMillis();
		String idVal = tb.getId();
		if (idVal != null) {
			CacheTabView.clear(idVal);
		}
		tb.setContent(null); 
		tabPane.getTabs().remove(tb);
		long endtime = System.currentTimeMillis();
		long costTime = (endtime - begintime);
		logger.info("关闭使用时间 = "+ costTime);
		
		if(tabPane.getTabs().size() == 0) {
			CommonAction.hideBottom(); 
		} 
	}
	
	public static void clearDataTable(int tbIdx) {
		TabPane tabPane = ComponentGetter.dataTab; 
		var tb = tabPane.getTabs().get(tbIdx);
		long begintime = System.currentTimeMillis();
		String idVal = tb.getId();
		if (idVal != null) {
			CacheTabView.clear(idVal);
		}
		tb.setContent(null); 
		tabPane.getTabs().remove(tb);
		long endtime = System.currentTimeMillis();
		long costTime = (endtime - begintime);
		logger.info("关闭使用时间 = "+ costTime);
		
		if(tabPane.getTabs().size() == 0) {
			CommonAction.hideBottom(); 
		} 
	}
	

	// 避免windows UI bug, 选择一下输入框
	public static void escapeWindowsUiBug() {
		if( windowsUiBugTag == 0 ){
			windowsUiBugTag = 1;
//			Platform.runLater(() -> {
//				ButtonFactory.rows.requestFocus();
//			 
//			});
			
			Thread th = new Thread() {
				public void run() {
					try {
						Thread.sleep(700);
						Platform.runLater(() -> {
							ButtonFactory.rows.requestFocus();
						 
						});
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			};
			th.start();
			
		    th = new Thread() {
				public void run() {
					try {
						Thread.sleep(900);
						Platform.runLater(() -> {
						    SqlEditor.getCodeArea().requestFocus(); 
						 
						});
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			};
			th.start();
		}
		
	}
	
	public static String createTabId() {
		int tableIdx = ConfigVal.tableIdx++; 
		System.out.println(tableIdx);
		return tableIdx + "";
	}
	
	// 从前应用
	public static void restartApp() {
		Consumer< String >  caller = x ->{ 
			saveApplicationStatusInfo();
			Restart.reboot();
		};
		ModalDialog.myConfirmation("Restart Application ? ", caller);
	}
	
	public static void changeThemeRestartApp(String val ) {
		Consumer< String >  ok = x ->{ 
			saveThemeStatus(val);  
			saveApplicationStatusInfo();
			Restart.reboot();
		};
		Consumer< String >  cancel = x ->{ 
			saveThemeStatus(val);  
			loadCss(ComponentGetter.primaryscene); 
			SqlEditor.changeThemeAllCodeArea() ;
			// 修改按钮颜色
			changeSvgColor();
		};
		ModalDialog.myConfirmation("Change Theme Restart Application Will Better, ok ? ", ok, cancel);
	}
	
	
	
	public static void demo() {
	
	}
	
	/**
	 * 	int tableIdx = ConfigVal.tableIdx++;
		table.setId(tableIdx + "");
		table.getStyleClass().add("myTableTag");
	 */
}
