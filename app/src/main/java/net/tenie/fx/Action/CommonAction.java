package net.tenie.fx.Action;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.NotificationPane;
import org.fxmisc.richtext.CodeArea;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqlcukyEditor;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.AppWindowComponentGetter;
import net.tenie.fx.component.CommonButtons;
import net.tenie.fx.component.FindReplaceEditor;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemContainer;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;
import net.tenie.fx.component.dataView.MyTabData;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.ConnectionDao;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.main.Restart;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.SqlTextDao;
import net.tenie.lib.tools.IconGenerator;


/**
 *    @author tenie
 *    
 */
public class CommonAction {
	private static Logger logger = LogManager.getLogger(CommonAction.class);
	private static int windowsUiBugTag = 0;
	
	public static void openConn(TreeItem<TreeNodePo> item) {
		// 判断 节点是否已经有子节点
		if (item.getChildren().size() == 0) {
			CommonAction.backRunOpenConn(item);
		}
	}
	
	// 子线程打开db连接backRunOpenConn
	public static void backRunOpenConn(TreeItem<TreeNodePo> item) {
		Node nd = IconGenerator.svgImage("spinner", "red");
		CommonUtility.rotateTransition(nd);
		item.getValue().setIcon( nd);
		AppWindowComponentGetter.treeView.refresh();

		Thread t = new Thread() {
			public void run() {
				SqluckyConnector po1 = null;
				try {
					logger.info("backRunOpenConn()");
					String connName = item.getValue().getName();
					SqluckyConnector po = DBConns.get(connName);
					po1 = po;
					po1.setInitConnectionNodeStatus(true);

					var conntmp = po1.getConn();
//					if (po.isAlive()) {
					if ( conntmp != null) {
						ConnItemContainer connItemContainer = new  ConnItemContainer(po, item);
						TreeItem<TreeNodePo> s = connItemContainer.getSchemaNode();
						Platform.runLater(() -> {
							item.getChildren().add(s);
							item.getValue().setIcon(IconGenerator.svgImage("link", "#7CFC00"));							
							connItemContainer.selectTable(po.getDefaultSchema());
							DBConns.flushChoiceBox(connName);
						});
					} else {
						Platform.runLater(() -> {
							MyAlert.errorAlert( " Cannot connect ip:" + po.getHostOrFile() + " port:" + po.getPort() + "  !");
							item.getValue().setIcon(IconGenerator.svgImageUnactive("unlink"));
							AppWindowComponentGetter.treeView.refresh();

						});

					}
				} catch (Exception e) { 
					e.printStackTrace();
					logger.debug(e.getMessage());
					Platform.runLater(() -> {
						MyAlert.errorAlert( " Error !");
						item.getValue().setIcon(IconGenerator.svgImage("unlink", "red"));
						AppWindowComponentGetter.treeView.refresh();
					});
					
				} finally {
					DBConns.flushChoiceBoxGraphic();
					po1.setInitConnectionNodeStatus(false);
				}

			}
		};
		t.start();
	}
	
	// 控件移除样式
	public static void rmCssClass(Node nd, String css) {
		nd.getStyleClass().remove(css);
	}
	
	// 键盘ESC按下后: 查找表的输入框清空, 选中的文本取消选中, 查找替换面板关闭
	public static void pressBtnESC() {
		ComponentGetter.dbInfoFilter.setText("");
		
		// 代码编辑内容, 取消选中, 高亮恢复复原
		SqlcukyEditor.deselect(); 
		SqlcukyEditor.applyHighlighting();
		
		// 隐藏查找, 替换窗口
		hideFindReplaceWindow();
		
		// 提示窗口
		SqlcukyEditor.currentMyTab().getSqlCodeArea().hideAutoComplete();
//		MyAutoComplete.hide();
	}
 
	// ctrl + S 按钮触发, 保存数据或sql文本
	public static void ctrlAndSAction() {
		boolean showStatus = ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		// 如果现在数据表格中的<保存按钮>是亮的(面板还要显示着), 就保存数据库数据 
		if(showStatus) {
			Button btn = MyTabData.dataPaneSaveBtn();
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
			MyTab tb = (MyTab) SqlcukyEditor.mainTabPaneSelectedTab();
			saveSqlAction(tb);
	}
	
	// 保存sql文本到硬盘
	public static void saveSqlAction(MyTab tb) {
		try {			
			String sql   =  tb.getTabSqlText();// SqlEditor.getTabSQLText(tb); 
			var scriptPo = tb.getDocumentPo();
			String fileName = scriptPo.getFileFullName();
			if (StrUtils.isNotNullOrEmpty(fileName)) {
				FileTools.saveByEncode(fileName, sql, scriptPo.getEncode());
				CommonUtility.setTabName(tb, FilenameUtils.getName(fileName));

			} else {
				String title = scriptPo.getTitle();
				tb.setModify(false);
				title = StrUtils.trimRightChar(title, "*");
				File file = FileOrDirectoryChooser.showSaveDefault("Save", title, ComponentGetter.primaryStage);
				if (file != null) {
					FileTools.save(file, sql);
					String name = FileTools.fileName(file.getPath());
					CommonUtility.setTabName(tb, name);
					scriptPo.setFileFullName(file.getPath());
					fileName = file.getPath();
				}
			}
			tb.syncScriptPo(H2Db.getConn());
			setOpenfileDir(fileName);

		} catch (Exception e1) {
			MyAlert.errorAlert( e1.getMessage());
			e1.printStackTrace();
		}finally {
			H2Db.closeConn();
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
		try {
			ConnectionDao.refreshConnOrder();
			TabPane mainTabPane = ComponentGetter.mainTabPane;
			int SELECT_PANE = mainTabPane.getSelectionModel().getSelectedIndex();
			Connection H2conn = H2Db.getConn();
			SqlTextDao.deleteAll(H2conn);
			for (Tab t : mainTabPane.getTabs()) {
				//TODO close save
				MyTab mtab = (MyTab) t;
				mtab.saveScriptPo(H2conn);
				var spo = mtab.getDocumentPo();
				String fp = spo.getFileFullName(); 
				if (spo != null && spo.getId() != null ) {
					String sql = mtab.getTabSqlText() ;// SqlEditor.getTabSQLText(mtab);
					if (StrUtils.isNotNullOrEmpty(sql) && sql.trim().length() > 0) {
						CodeArea code = mtab.getCodeArea(); //SqlEditor.getCodeArea(mtab);
						int paragraph = code.getCurrentParagraph() > 11 ? code.getCurrentParagraph() - 10 : 0;
						String title =  spo.getTitle();
						String encode = spo.getEncode();
						SqlTextDao.save(H2conn, title, sql, fp, encode, paragraph, spo.getId());
					}

				}
			}
			// 保存选择的pane 下标
			SqlTextDao.saveConfig(H2conn, "SELECT_PANE", SELECT_PANE + "");
			
			// 删除 script tree view 中的空内容tab
			var childs = ScriptTabTree.ScriptTreeView.getRoot().getChildren();
			int idx = 1;
			for(int i = 0; i < childs.size() ; i++) {
//			for(var tv :childs) {
				var tv = childs.get(i);
				var mytab = tv.getValue();
				var scpo = mytab.getDocumentPo();
				var sqltxt = scpo.getText();
				if(sqltxt == null || sqltxt.trim().length() == 0) {
					SqlTextDao.deleteScriptArchive(H2conn, scpo); 
				}else {
					String fp = scpo.getFileFullName();
					if(StrUtils.isNullOrEmpty(fp)) {
						scpo.setTitle("Untitled_"+ idx +"*");
						SqlTextDao.updateScriptArchive(H2conn , scpo);  
						idx++;
					}
				}				
			}

		} finally {
			H2Db.closeConn();
		}

	}
		
	// 保存脚本文件内容
//	public static List<ScriptPo>  saveScriptArchive() {
//		List<ScriptPo> val = new ArrayList<>();
//		try {
//			TabPane mainTabPane = ComponentGetter.mainTabPane;
//			Connection H2conn = H2Db.getConn();
//			for (Tab t : mainTabPane.getTabs()) {
////				String idval = t.getId();
//				MyTab mtab = (MyTab) t;
//				var 
//				if (StrUtils.isNotNullOrEmpty(idval)) {
//					String sql = SqlEditor.getTabSQLText(t);
//					if (StrUtils.isNotNullOrEmpty(sql)) {
//						CodeArea code = SqlEditor.getCodeArea(t);
//						int paragraph = code.getCurrentParagraph() > 11 ? code.getCurrentParagraph() - 10 : 0;
//						if (StrUtils.beginWith(idval, ConfigVal.SAVE_TAG)) {
//							idval = idval.substring(ConfigVal.SAVE_TAG.length());
//						} else {
//							idval = "";
//						}
//
//						String title = CommonUtility.tabText(t); 
//						String encode = ComponentGetter.getFileEncode(idval);
//						ScriptPo po= SqlTextDao.scriptArchive(H2conn, title, sql, idval, encode, paragraph);
//						if(po != null && po.getId() > -1) { 
//							val.add(po);
//						}
//					}
//				}
//			}
//		} finally {
//		}
//		return val;
//	}
	
	//TODO archive script
	public static void archiveAllScript() {
		TabPane mainTabPane = ComponentGetter.mainTabPane;
		var tabs = mainTabPane.getTabs(); 
		for(var tab : tabs) { 
			MyTab mtb = (MyTab) tab;
			mtb.syncScriptPo();
		} 
		tabs.clear();
		var stp = ComponentGetter.scriptTitledPane;
		stp.setExpanded(true);
	}
	

	// 代码格式化
	public static void formatSqlText() {
		CodeArea code = SqlcukyEditor.getCodeArea();
		String txt = code.getSelectedText();
		if (StrUtils.isNotNullOrEmpty(txt)) {
			IndexRange i = code.getSelection();
			int start = i.getStart();
			int end = i.getEnd();

			String rs = SqlFormatter.format(txt);
			code.deleteText(start, end);
			code.insertText(start, rs);
		} else {
			txt = SqlcukyEditor.getCurrentCodeAreaSQLText();
			String rs = SqlFormatter.format(txt);
			code.clear();
			code.appendText(rs);
		}
		SqlcukyEditor.currentSqlCodeAreaHighLighting();
	}
	
	
	// sql 压缩
	public static void pressSqlText() {
		CodeArea code = SqlcukyEditor.getCodeArea();
		String txt = code.getSelectedText();
		if (StrUtils.isNotNullOrEmpty(txt)) {
			IndexRange i = code.getSelection();
			int start = i.getStart();
			int end = i.getEnd();

			String rs = StrUtils.pressString(txt); // SqlFormatter.format(txt);
			code.deleteText(start, end);
			code.insertText(start, rs);
		} else {
			txt = SqlcukyEditor.getCurrentCodeAreaSQLText();
			String rs = StrUtils.pressString(txt); //  SqlFormatter.format(txt);
			code.clear();
			code.appendText(rs);
		} 
		SqlcukyEditor.currentSqlCodeAreaHighLighting();
	}
	

	// 代码大写
	public static void UpperCaseSQLTextSelectText() {

		CodeArea code = SqlcukyEditor.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		// 将原文本删除
		code.deleteText(start, end);
		 
		code.insertText(start, text.toUpperCase());
		SqlcukyEditor.currentSqlCodeAreaHighLighting();
	}

	// 代码小写
	public static void LowerCaseSQLTextSelectText() {

		CodeArea code = SqlcukyEditor.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		// 将原文本删除
		code.deleteText(start, end);
		 
		code.insertText(start, text.toLowerCase()); 
		SqlcukyEditor.currentSqlCodeAreaHighLighting();
	}

	// 驼峰命名转下划线
	public static void CamelCaseUnderline() {

		CodeArea code = SqlcukyEditor.getCodeArea();
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
		SqlcukyEditor.currentSqlCodeAreaHighLighting();
	}

	// 下划线 轉 驼峰命名
	public static void underlineCaseCamel() {

		CodeArea code = SqlcukyEditor.getCodeArea();
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
		SqlcukyEditor.currentSqlCodeAreaHighLighting();
	}

	public static void selectTextAddString() {
		CodeArea code = SqlcukyEditor.getCodeArea();
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
		SqlcukyEditor.currentSqlCodeAreaHighLighting();
	}
	// 添加tab符号
	public static void add4Space() { 
		
		String replaceStr1 = "\n    ";
		String replaceStr2 = "    ";
		
		CodeArea code = SqlcukyEditor.getCodeArea();
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
		CodeArea code = SqlcukyEditor.getCodeArea();
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
		SqlcukyEditor.currentSqlCodeAreaHighLighting();
	}
	


	// 代码添加注释-- 或去除注释
	public static void addAnnotationSQLTextSelectText() {

		CodeArea code = SqlcukyEditor.getCodeArea();
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
		SqlcukyEditor.currentSqlCodeAreaHighLighting();
	}

	//TODO 打开sql文件
	public static void openSqlFile(String encode) {
		try {
			File f = FileOrDirectoryChooser.showOpenSqlFile("Open", ComponentGetter.primaryStage);
			if (f == null)
				return;
			String val = FileUtils.readFileToString(f, encode); 
			String tabName = "";
//			ComponentGetter.fileEncode.put( f.getPath(), encode);
			if (StrUtils.isNotNullOrEmpty(f.getPath())) {
//				id = ConfigVal.SAVE_TAG + f.getPath();
				tabName = FileTools.fileName(f.getPath());
				setOpenfileDir(f.getPath());
			}
			DocumentPo scpo = new DocumentPo();
			scpo.setEncode(encode);
			scpo.setFileFullName(f.getAbsolutePath());
			scpo.setText(val);
			scpo.setTitle(tabName);
			MyTab mt = ScriptTabTree.findMyTabByScriptPo(scpo);
			if(mt != null) { // 如果已经存在就不用重新打开
				mt.mainTabPaneAddMyTab();
			}else {
				MyTab.createTabFromSqlFile(scpo);
			}
			
		} catch (IOException e) {
			MyAlert.errorAlert( e.getMessage());
			e.printStackTrace();
		}
	}

	// 查看表明细(一行数据) 快捷键
	public static void shortcutShowDataDatil() {
		AnchorPane fp = MyTabData.optionPane();
		Button btn = (Button) fp.getChildren().get(1);
		MouseEvent me = myEvent.mouseEvent(MouseEvent.MOUSE_CLICKED, btn);
		Event.fireEvent(btn, me);
	}

	public static void hideFindReplaceWindow() {
		VBox b = SqlcukyEditor.getTabVbox();
		int bsize = b.getChildren().size();
		if (bsize > 1) { 
				FindReplaceEditor.delFindReplacePane();
		} 
		 
	}
	
	public static void findReplace(boolean isReplace) {
		VBox b = SqlcukyEditor.getTabVbox();
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
		JFXButton btnLeft =  CommonButtons.hideLeft; // AllButtons.btns.get("hideLeft");
		JFXButton btnBottom = CommonButtons.hideBottom; //  AllButtons.btns.get("hideBottom");
		boolean leftp = ComponentGetter.treeAreaDetailPane.showDetailNodeProperty().getValue();
		boolean bootp = ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		if (leftp || bootp) {
			ComponentGetter.treeAreaDetailPane.setShowDetailNode(false);
			btnLeft.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-right"));

			ComponentGetter.masterDetailPane.setShowDetailNode(false);
			btnBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-up"));
		} else {
			ComponentGetter.treeAreaDetailPane.setShowDetailNode(true);
			btnLeft.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-left"));

			ComponentGetter.masterDetailPane.setShowDetailNode(true);
			btnBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-down"));
		}

	}

	public static void hideLeft() {
		JFXButton btn =  CommonButtons.hideLeft; //  AllButtons.btns.get("hideLeft");
		if (ComponentGetter.treeAreaDetailPane.showDetailNodeProperty().getValue()) {
			ComponentGetter.treeAreaDetailPane.setShowDetailNode(false);
			btn.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-right"));
																							
		} else {
			ComponentGetter.treeAreaDetailPane.setShowDetailNode(true);
			btn.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-left"));
																					
		}
	}

	public static void hideBottom() {
		JFXButton btn =   CommonButtons.hideBottom; //   AllButtons.btns.get("hideBottom");
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
			btn.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-down"));
		} else {
			btn.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-up"));
		}

	}

	// 底部数据展示面板是否显示
	public static void showDetailPane() {
		JFXButton btn =    CommonButtons.hideBottom; //  AllButtons.btns.get("hideBottom");
		boolean showStatus = !ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		if (showStatus) {
			hideShowBottomHelper(true, btn); 
			escapeWindowsUiBug();
		}
		
	}

	// 连接测试
	public static boolean isAliveTestAlert(SqluckyConnector connpo, Button testBtn) {
		Thread t = new Thread() {
			public void run() {
				connpo.getConn();
				Platform.runLater(() -> {
					if (connpo.isAlive()) {
						MyAlert.infoAlert("Information!", "  Successfully  !");
						connpo.closeConn();
						testBtn.setStyle("-fx-background-color: green ");
					} else {
						MyAlert.errorAlert(" Connect fail !");

					}

				});
			}
		};
		t.start();
		return false;
	}
	//收缩treeview
	public static void shrinkTreeView() {
		TreeItem<TreeNodePo>  root = AppWindowComponentGetter.treeView.getRoot();
		shrinkUnfoldTreeViewHelper(root, false);
	}
	 
	
	
	//展开treeview
	public static void unfoldTreeView() {
		TreeItem<TreeNodePo>  root = AppWindowComponentGetter.treeView.getRoot();
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
		CommonUtility.loadCss(ComponentGetter.primaryscene); 
		SqlcukyEditor.changeThemeAllCodeArea() ; 
//		changeSvgColor(); // 修改按钮颜色
	}
	// 设置整体样式
	public static void setThemeRestart(String val ) {
		
		// 询问是否重启app, 如果不重启再重新加载样式
		CommonAction.changeThemeRestartApp( val ) ;
		
	}
		
//	public static void changeSvgColor() {
//		String color =  CommonUtility.themeColor();
//		for(var icon : IconGenerator.icons) {
//			icon.setStyle("-fx-background-color: " + color + ";");
//		}
//		
//	}
	
	public static void setOpenfileDir(String val) {
		Connection conn =  H2Db.getConn();
		H2Db.setConfigVal(conn, "OPEN_FILE_DIR", val) ;
		H2Db.closeConn();
		ConfigVal.openfileDir = val;
	}	
	
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
	
	
	
	
	

	
	
	
	
	//TODO 改变字体大小
	public static void changeFontSize(boolean isPlus) {
		// 获取当前的 size
		if(ConfigVal.FONT_SIZE == -1) {
			String path = FileUtils.getUserDirectoryPath() + "/.sqlucky/font-size.css";
			String str = FileTools.read(path);
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
		CommonUtility.setFontSize(sz);
		for(SqluckyTab mtb : SqlcukyEditor.getAllgetMyTabs() ) {
			var obj = mtb.getSqlCodeArea();
			var code = obj.getCodeArea();
			logger.info(code.getStyle());
			String txt = code.getText();  
			code.replaceText(0, txt.length(), txt);
			obj.highLighting();
		}
		
		ConfigVal.FONT_SIZE = sz;
		
	}
	 
	
	// 关闭 数据页, 清理缓存
	public static void clearDataTable(Tab tb) {
		TabPane tabPane = ComponentGetter.dataTabPane; 
		long begintime = System.currentTimeMillis();
//		String idVal = tb.getId();
//		if (idVal != null) {
//			CacheTabView.clear(idVal);
//		}
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
		TabPane tabPane = ComponentGetter.dataTabPane; 
		var tb = tabPane.getTabs().get(tbIdx);
		long begintime = System.currentTimeMillis();
//		String idVal = tb.getId();
//		if (idVal != null) {
//			CacheTabView.clear(idVal);
//		}
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
						    SqlcukyEditor.getCodeArea().requestFocus(); 
						 
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
//		System.out.println(tableIdx);
		return tableIdx + "";
	}
	
	// 重启应用
	public static void restartApp() {
		Consumer< String >  caller = x ->{ 
			saveApplicationStatusInfo();
			Restart.reboot();
		};
		MyAlert.myConfirmation("Restart Application ? ", caller);
	}
	
	public static void changeThemeRestartApp(String val ) {
		Consumer< String >  ok = x ->{ 
			saveThemeStatus(val);  
			saveApplicationStatusInfo();
			Restart.reboot();
		};
		Consumer< String >  cancel = x ->{ 
			saveThemeStatus(val);  
			CommonUtility.loadCss(ComponentGetter.primaryscene); 
			SqlcukyEditor.changeThemeAllCodeArea() ;
			// 修改按钮颜色
//			changeSvgColor();
		};
		MyAlert.myConfirmation("Change Theme Restart Application Will Better, ok ? ", ok, cancel);
	}
	// 获取当前连接下拉选中的连接名称
	public static String getComboBoxDbConnName() {
		var v = ComponentGetter.connComboBox.getValue();
		if (v != null) {
			String connboxVal = ComponentGetter.connComboBox.getValue().getText();
			return connboxVal;
		}

		return null;
	}
	
	
	// 获取当前连接下拉选值的对应连接对象
	public static SqluckyConnector getDbConnectionPoByComboBoxDbConnName() {
		var name = CommonAction.getComboBoxDbConnName();
		if(StrUtils.isNotNullOrEmpty(name)) {
			SqluckyConnector dpov = DBConns.get(name);
			return dpov;
		}
		return null; 
	}
	


	// 数据库表名的查询输入框
	public static void dbInfoTreeQuery() {
		 var container =AppWindowComponentGetter.DBinfoContainer;
		   var filter = AppWindowComponentGetter.dbInfoTreeFilter;
		   if(  container.getChildren().contains(filter)) {
			   container.getChildren().remove(filter);
		   }else {
			   container.getChildren().add(1,filter);
		   }
		   
		    // 如果有选中的字符串, 进行查询
			String str = SqlcukyEditor.getCurrentCodeAreaSQLSelectedText();
			if (str.trim().length() > 0) {
				ComponentGetter.dbInfoFilter.setText(str.trim());
				if(  ! container.getChildren().contains(filter)) {
					container.getChildren().add(1,filter);
			   }
			}
	}
	
	public static void showNotifiaction(String title) {
		var notificationPane =  ComponentGetter.notificationPane;
		notificationPane.setText(title);
		if(! CommonConst.THEME_LIGHT.equals(ConfigVal.THEME)) { 
			if( ! notificationPane.getStyleClass().contains(NotificationPane.STYLE_CLASS_DARK)) {
				notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
			} 
		}else {
			if( notificationPane.getStyleClass().contains(NotificationPane.STYLE_CLASS_DARK)) {
				notificationPane.getStyleClass().remove(NotificationPane.STYLE_CLASS_DARK);
			} 
		}
		 
		if (notificationPane.isShowing()) {
            notificationPane.hide();
        } else {
            notificationPane.show();
        }
	}
	
	// 字段值被修改还原, 不允许修改
		public static   StringProperty createReadOnlyStringProperty(String val ) {
			StringProperty sp =  new StringProperty() {
				@Override
				public String get() { 
					return val;
				}
				
				@Override
				public void bind(ObservableValue<? extends String> arg0) { }
				@Override
				public boolean isBound() { 
					return false;
				}
				@Override
				public void unbind() { }

				@Override
				public Object getBean() { 
					return null;
				}
				@Override
				public String getName() { 
					return null;
				} 
				@Override
				public void addListener(ChangeListener<? super String> arg0) { } 
				@Override
				public void removeListener(ChangeListener<? super String> arg0) { } 
				@Override
				public void addListener(InvalidationListener arg0) { }
				@Override
				public void removeListener(InvalidationListener arg0) { } 		
				@Override
				public void set(String arg0) {}  
			}; 		
			return sp;
		}
			
	
	public static void demo() {}
	
}
