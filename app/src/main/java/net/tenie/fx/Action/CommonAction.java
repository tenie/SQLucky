package net.tenie.fx.Action;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
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
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.FindReplaceTextPanel;
import net.tenie.Sqlucky.sdk.component.SqluckyEditor;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.myEvent;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.AppWindowComponentGetter;
import net.tenie.fx.component.MyAreaTab;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemContainer;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.ConnectionDao;
import net.tenie.fx.main.Restart;
import net.tenie.lib.db.h2.AppDao;


/**
 *    @author tenie
 *    
 */
public class CommonAction {
	private static Logger logger = LogManager.getLogger(CommonAction.class);
	
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
		SqluckyEditor.deselect(); 
		SqluckyEditor.applyHighlighting();
		
		// 隐藏查找, 替换窗口
		hideFindReplaceWindow();
		
		// 提示窗口
		SqluckyEditor.currentMyTab().getSqlCodeArea().hideAutoComplete();
	}
	
	// 隐藏查找, 替换窗口
	public static void hideFindReplaceWindow() {
		VBox b = SqluckyEditor.getTabVbox();
		var sltb = SqluckyEditor.currentMyTab();
		int bsize = b.getChildren().size();
		if (bsize > 1) { 
			FindReplaceTextPanel.delFindReplacePane(sltb);
		} 
		 
	}
 
	// ctrl + S 按钮触发, 保存数据或sql文本
	public static void ctrlAndSAction() {
		boolean showStatus = ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		// 如果现在数据表格中的<保存按钮>是亮的(面板还要显示着), 就保存数据库数据 
		if(showStatus) {
			Button btn = SqluckyBottomSheetUtility.dataPaneSaveBtn();
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
			MyAreaTab tb = (MyAreaTab) SqluckyEditor.mainTabPaneSelectedTab();
			saveSqlAction(tb);
	}
	
	// 保存sql文本到硬盘
	public static void saveSqlAction(SqluckyTab tb) {
		var conn = SqluckyAppDB.getConn();
		try {			
			String sql   =  tb.getAreaText();// SqlEditor.getTabSQLText(tb); 
			var scriptPo = tb.getDocumentPo();
			String fileName = scriptPo.getFileFullName();
			if (StrUtils.isNotNullOrEmpty(fileName)) {
				FileTools.saveByEncode(fileName, sql, scriptPo.getEncode());
//				CommonUtility.setTabName(tb, FilenameUtils.getName(fileName));
				tb.setTitle(FilenameUtils.getName(fileName));

			} else {
				String title = scriptPo.getTitle();
				tb.setModify(false);
				title = StrUtils.trimRightChar(title, "*");
				File file = FileOrDirectoryChooser.showSaveDefault("Save", title, ComponentGetter.primaryStage);
				if (file != null) {
					FileTools.save(file, sql);
					String name = FileTools.fileName(file.getPath());
//					CommonUtility.setTabName(tb, name);
					tb.setTitle( name);
					scriptPo.setFileFullName(file.getPath());
					fileName = file.getPath();
				}
			}
			tb.syncScriptPo(conn);
			setOpenfileDir(fileName);

		} catch (Exception e1) {
			MyAlert.errorAlert( e1.getMessage());
			e1.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		} 
	}
	
	
	
	
	// 主窗口关闭事件处理逻辑
	public static void mainPageClose() {
		try {
			 saveApplicationStatusInfo();
		} finally {
//			SqluckyAppDB.closeConn();
			System.exit(0);
		}

	}
	
	// 保存app状态
	public static void saveApplicationStatusInfo() {
		Connection H2conn = SqluckyAppDB.getConn();
		try {
			ConnectionDao.refreshConnOrder();
			TabPane mainTabPane = ComponentGetter.mainTabPane;
			int activateTabPane = mainTabPane.getSelectionModel().getSelectedIndex();
			var alltabs = mainTabPane.getTabs();
			for (int i = 0 ; i < alltabs.size(); i++ ) {
				Tab tab = alltabs.get(i);
				//TODO close save
				MyAreaTab mtab = (MyAreaTab) tab;
				mtab.saveScriptPo(H2conn);
				var spo = mtab.getDocumentPo();
				
				// 将打开状态设置为1, 之后根据这个状态来恢复
				if (spo != null && spo.getId() != null ) {
					String sql = mtab.getAreaText() ;
					if (StrUtils.isNotNullOrEmpty(sql) && sql.trim().length() > 0) {
						spo.setOpenStatus(1); 
						// 当前激活的编辑页面
						if(activateTabPane == i) {
							spo.setIsActivate(1); 
						}else {
							spo.setIsActivate(0);
						}
					}else {
						spo.setOpenStatus(0);
						spo.setIsActivate(0);
					}
				}
			}
			// 保存选择的pane 下标
//			AppDao.saveConfig(H2conn, "SELECT_PANE", activateTabPane + "");
			
			// 删除 script tree view 中的空内容tab
			var childs = ScriptTabTree.ScriptTreeView.getRoot().getChildren();
			int idx = 1;
			for(int i = 0; i < childs.size() ; i++) {
				var tv = childs.get(i);
				var mytab = tv.getValue();
				var scpo = mytab.getDocumentPo();
				var sqltxt = scpo.getText();
				if(sqltxt == null || sqltxt.trim().length() == 0) {
					AppDao.deleteScriptArchive(H2conn, scpo); 
				}else {
					String fp = scpo.getFileFullName();
					if(StrUtils.isNullOrEmpty(fp)) {
						scpo.setTitle("Untitled_"+ idx +"*");
						idx++;
					}
					AppDao.updateScriptArchive(H2conn , scpo);  
					
				}				
			}

		} finally {
			SqluckyAppDB.closeConn(H2conn);
		}

	}
	
	//TODO archive script
	public static void archiveAllScript() {
		TabPane mainTabPane = ComponentGetter.mainTabPane;
		var tabs = mainTabPane.getTabs(); 
		for(var tab : tabs) { 
			MyAreaTab mtb = (MyAreaTab) tab;
			mtb.getDocumentPo().setOpenStatus(0);
			mtb.syncScriptPo();
		} 
		tabs.clear();
		var stp = ComponentGetter.scriptTitledPane;
		stp.setExpanded(true);
	}
	

	// 代码格式化
	public static void formatSqlText() {
		CodeArea code = SqluckyEditor.getCodeArea();
		String txt = code.getSelectedText();
		if (StrUtils.isNotNullOrEmpty(txt)) {
			IndexRange i = code.getSelection();
			int start = i.getStart();
			int end = i.getEnd();

			String rs = SqlFormatter.format(txt);
			code.deleteText(start, end);
			code.insertText(start, rs);
		} else {
			txt = SqluckyEditor.getCurrentCodeAreaSQLText();
			String rs = SqlFormatter.format(txt);
			code.clear();
			code.appendText(rs);
		}
		SqluckyEditor.currentSqlCodeAreaHighLighting();
	}
	
	
	// sql 压缩
	public static void pressSqlText() {
		CodeArea code = SqluckyEditor.getCodeArea();
		String txt = code.getSelectedText();
		if (StrUtils.isNotNullOrEmpty(txt)) {
			IndexRange i = code.getSelection();
			int start = i.getStart();
			int end = i.getEnd();

			String rs = StrUtils.pressString(txt); // SqlFormatter.format(txt);
			code.deleteText(start, end);
			code.insertText(start, rs);
		} else {
			txt = SqluckyEditor.getCurrentCodeAreaSQLText();
			String rs = StrUtils.pressString(txt); //  SqlFormatter.format(txt);
			code.clear();
			code.appendText(rs);
		} 
		SqluckyEditor.currentSqlCodeAreaHighLighting();
	}
	

	// 代码大写
	public static void UpperCaseSQLTextSelectText() {

		CodeArea code = SqluckyEditor.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		// 将原文本删除
		code.deleteText(start, end);
		 
		code.insertText(start, text.toUpperCase());
		SqluckyEditor.currentSqlCodeAreaHighLighting();
	}

	// 代码小写
	public static void LowerCaseSQLTextSelectText() {

		CodeArea code = SqluckyEditor.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		// 将原文本删除
		code.deleteText(start, end);
		 
		code.insertText(start, text.toLowerCase()); 
		SqluckyEditor.currentSqlCodeAreaHighLighting();
	}

	// 驼峰命名转下划线
	public static void CamelCaseUnderline() {

		CodeArea code = SqluckyEditor.getCodeArea();
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
		SqluckyEditor.currentSqlCodeAreaHighLighting();
	}

	// 下划线 轉 驼峰命名
	public static void underlineCaseCamel() {

		CodeArea code = SqluckyEditor.getCodeArea();
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
		SqluckyEditor.currentSqlCodeAreaHighLighting();
	}

	public static void selectTextAddString() {
		CodeArea code = SqluckyEditor.getCodeArea();
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
		SqluckyEditor.currentSqlCodeAreaHighLighting();
	}
	


	// 代码添加注释-- 或去除注释
	public static void addAnnotationSQLTextSelectText() {

		CodeArea code = SqluckyEditor.getCodeArea();
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
		SqluckyEditor.currentSqlCodeAreaHighLighting();
	}

	//TODO 打开sql文件
	public static void openSqlFile() {
		try {
			File f = FileOrDirectoryChooser.showOpenSqlFile("Open", ComponentGetter.primaryStage);
			if (f == null)
				return;
			String charset = FileTools.detectFileCharset(f);
			if(charset == null ) {
				 new RuntimeException("Open failed!");
			}
			String val = FileUtils.readFileToString(f, charset); 
			String tabName = "";
//			ComponentGetter.fileEncode.put( f.getPath(), encode);
			if (StrUtils.isNotNullOrEmpty(f.getPath())) {
//				id = ConfigVal.SAVE_TAG + f.getPath();
				tabName = FileTools.fileName(f.getPath());
				setOpenfileDir(f.getPath());
			}
			DocumentPo scpo = new DocumentPo();
			scpo.setEncode(charset);
			scpo.setFileFullName(f.getAbsolutePath());
			scpo.setText(val);
			scpo.setTitle(tabName);
			SqluckyTab mt = ScriptTabTree.findMyTabByScriptPo(scpo);
			if(mt != null) { // 如果已经存在就不用重新打开
				mt.showMyTab();
			}else {
				MyAreaTab.createTabFromSqlFile(scpo);
			}
			
		} catch (IOException e) {
			MyAlert.errorAlert( e.getMessage());
			e.printStackTrace();
		}
	}
	// 打开系统中的一个sql文件, 并在主界面显示
	public static void openSqlFile(	File f) {
		try {
			if (f == null)
				return;
			String charset = FileTools.detectFileCharset(f);
			if(charset == null ) {
				 new RuntimeException("Open failed!");
			}
			String val = FileUtils.readFileToString(f, charset); 
			String tabName = "";
//			ComponentGetter.fileEncode.put( f.getPath(), encode);
			if (StrUtils.isNotNullOrEmpty(f.getPath())) {
//				id = ConfigVal.SAVE_TAG + f.getPath();
				tabName = FileTools.fileName(f.getPath());
				setOpenfileDir(f.getPath());
			}
			DocumentPo scpo = new DocumentPo();
			scpo.setEncode(charset);
			scpo.setFileFullName(f.getAbsolutePath());
			scpo.setText(val);
			scpo.setTitle(tabName);
			SqluckyTab mt = ScriptTabTree.findMyTabByScriptPo(scpo);
			if(mt != null) { // 如果已经存在就不用重新打开
				mt.showMyTab();
			}else {
				MyAreaTab.createTabFromSqlFile(scpo);
			}
			
		} catch (IOException e) {
			MyAlert.errorAlert( e.getMessage());
			e.printStackTrace();
		}
	}

	// 查看表明细(一行数据) 快捷键
	public static void shortcutShowDataDatil() {
		Button btn = 	SqluckyBottomSheetUtility.dataPaneDetailBtn();
		if(btn != null ) {
			MouseEvent me = myEvent.mouseEvent(MouseEvent.MOUSE_CLICKED, btn);
			Event.fireEvent(btn, me);
		}
	
	}

	
	
//	public static void findReplace(boolean isReplace) {
//		VBox b = SqlcukyEditor.getTabVbox();
//		int bsize = b.getChildren().size();
//		if (bsize > 1) {
//			// 如果查找已经存在, 要打开替换, 就先关光再打开替换查找
//			if (bsize == 2 && isReplace) {
//				FindReplaceEditor.delFindReplacePane();
//				findReplace(isReplace);
//			} else // 如果替换已经存在, 要打开查找, 就先关光再打开查找
//			if (bsize == 3 && !isReplace) {
//				FindReplaceEditor.delFindReplacePane();
//				findReplace(isReplace);
//			} else {
//				FindReplaceEditor.delFindReplacePane();
//			}
//
//		} else {
//			FindReplaceEditor.createFindPane(isReplace);
//		}
//	}

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

	

	// 连接测试
	public static boolean isAliveTestAlert(SqluckyConnector connpo, Button testBtn) {
		Thread t = new Thread() {
			public void run() {
				connpo.getConn();
				Platform.runLater(() -> {
					if (connpo.isAlive()) {
						String infoStr = connpo.DBInfo(connpo.getConn()); //Dbinfo.getDBInfo(connpo.getConn());
						MyAlert.infoAlert("  Successfully  ! \n" + infoStr);
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
		Connection conn =  SqluckyAppDB.getConn();
		AppDao.saveConfig(conn, "THEME", val) ;
		SqluckyAppDB.closeConn(conn);
	}
	
	// 设置整体样式
	public static void setTheme(String val ) {
		saveThemeStatus(val); 
		// 根据新状态加载新样式
		CommonUtility.loadCss(ComponentGetter.primaryscene); 
		SqluckyEditor.changeThemeAllCodeArea() ; 
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
		Connection conn =  SqluckyAppDB.getConn();
		AppDao.saveConfig(conn, "OPEN_FILE_DIR", val) ;
		SqluckyAppDB.closeConn(conn);
		ConfigVal.openfileDir = val;
	}	
	
	// 改变字体大小
	public static void changeFontSize(boolean isPlus) {
		// 获取当前的 size
		if(ConfigVal.FONT_SIZE == -1) {
			String path = FileUtils.getUserDirectoryPath() + "/.sqlucky/font-size.css";
			String str = FileTools.read(path);
			String val = str.split("\n")[0];
			val = val.substring(2, val.lastIndexOf("*/"));
//			System.out.println(val);
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
		for(SqluckyTab mtb : SqluckyEditor.getAllgetMyTabs() ) {
			var obj = mtb.getSqlCodeArea();
			var code = obj.getCodeArea();
			logger.info(code.getStyle());
			String txt = code.getText();  
			code.replaceText(0, txt.length(), txt);
			obj.highLighting();
		}
		
		ConfigVal.FONT_SIZE = sz;
		
	}
	 
	

//	
//	public static void clearDataTable(int tbIdx) {
//		TabPane tabPane = ComponentGetter.dataTabPane; 
//		var tb = tabPane.getTabs().get(tbIdx);
//		long begintime = System.currentTimeMillis();
//		tb.setContent(null); 
//		tabPane.getTabs().remove(tb);
//		long endtime = System.currentTimeMillis();
//		long costTime = (endtime - begintime);
//		logger.info("关闭使用时间 = "+ costTime);
//		
//		if(tabPane.getTabs().size() == 0) {
//			SdkComponent.hideBottom(); 
//		} 
//	}
//	

	

	
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
			SqluckyEditor.changeThemeAllCodeArea() ;
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
	public static void dbInfoTreeQuery(Pane container, Node filter, List<Node>  btnList) {
		
//		var container = ComponentGetter.leftNodeContainer;

		CommonUtility.leftHideOrShowSecondOptionBox(container, filter, btnList);
//		//显示或隐藏 filter
////		var filter = AppWindowComponentGetter.dbInfoTreeFilter;
//		if (container.getChildren().contains(filter)) {
//			container.getChildren().remove(filter);
//		} else {
//			container.getChildren().add(1, filter);
//		}

		// 如果有选中的字符串, 进行查询
		String str = SqluckyEditor.getCurrentCodeAreaSQLSelectedText();
		if (str.trim().length() > 0) {
			ComponentGetter.dbInfoFilter.setText(str.trim());
			if (!container.getChildren().contains(filter)) {
				container.getChildren().add(1, filter);
			}
		}
	}

	
	
		
		public static final int DROP_COLUMN = 1;
		public static final int ALTER_COLUMN = 2;
		public static final int ADD_COLUMN = 3;
	// 执行导出的sql
	public static Long execExportSql(String sql, Connection conn, SqluckyConnector dbconnPo) {
		Long key = RunSQLHelper.refresh(dbconnPo, sql, "", false);
		return key;
	}
	
	// 导出SQL
	public static RsVal exportSQL(int ty, String colname, RsVal rv) {
		try {
			// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
			String sql = "";
			if (DROP_COLUMN == ty) {
				sql = rv.dbconnPo.getExportDDL().exportAlterTableDropColumn(rv.conn, rv.dbconnPo.getDefaultSchema(),
						rv.tableName, colname);
			} else if (ALTER_COLUMN == ty) {
				sql = rv.dbconnPo.getExportDDL().exportAlterTableModifyColumn(rv.conn, rv.dbconnPo.getDefaultSchema(),
						rv.tableName, colname);
			} else if (ADD_COLUMN == ty) {
				sql = rv.dbconnPo.getExportDDL().exportAlterTableAddColumn(rv.conn, rv.dbconnPo.getDefaultSchema(),
						rv.tableName, colname);
			}

			rv.sql = sql;
		} catch (Exception e) {
			MyAlert.errorAlert(e.getMessage());

		}
		return rv;

	}

	public static RsVal exportSQL(int ty, String colname) {
		RsVal rv = SqluckyBottomSheetUtility.tableInfo();
		return exportSQL(ty, colname, rv);
	}
	// 添加新字段
	public static void addNewColumn() {
		RsVal rv = SqluckyBottomSheetUtility.tableInfo();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			RsVal rv2 = exportSQL(ADD_COLUMN, x);
			CommonAction.execExportSql(rv2.sql, rv2.conn, rv.dbconnPo);
		};
		ModalDialog.showExecWindow(rv.tableName + " add column : input words like 'MY_COL CHAR(10)'", "", caller);

	}
	// 添加新字段
	public static void addNewColumn(SqluckyConnector dbc, String schema, String tablename) {

		Connection conn = dbc.getConn();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			String colname = x.trim();
			String sql = dbc.getExportDDL().exportAlterTableAddColumn(conn, schema, tablename, colname);
			CommonAction.execExportSql(sql, conn, dbc);
		};
		ModalDialog.showExecWindow(tablename + " add column : input words like 'MY_COL CHAR(10)'", "", caller);

	}
	public static void demo() {}
	
}
