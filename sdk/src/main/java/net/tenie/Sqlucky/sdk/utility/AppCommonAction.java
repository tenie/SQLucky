package net.tenie.Sqlucky.sdk.utility;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.DBTools;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.RsVal;
import net.tenie.Sqlucky.sdk.po.component.ConnItemContainer;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.subwindow.DialogTools;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;

/**
 * @author tenie
 * 
 */
public class AppCommonAction {
	private static Logger logger = LogManager.getLogger(AppCommonAction.class);

	public static void openConn(TreeItem<TreeNodePo> item) {
		// 判断 节点是否已经有子节点
		if (item.getChildren().size() == 0) {
			AppCommonAction.backRunOpenConn(item);
		}
	}

	// 子线程打开db连接backRunOpenConn
	public static void backRunOpenConn(TreeItem<TreeNodePo> item) {
		Node nd = IconGenerator.svgImage("spinner", "red");
		CommonUtils.rotateTransition(nd);
		item.getValue().setIcon(nd);
		ComponentGetter.treeView.refresh();

		Thread t = new Thread() {
			@Override
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
					if (conntmp != null) {
						ConnItemContainer connItemContainer = new ConnItemContainer(po, item);
						TreeItem<TreeNodePo> s = connItemContainer.getSchemaNode();
						Platform.runLater(() -> {
							item.getChildren().add(s);
							item.getValue().setIcon(IconGenerator.svgImage("link", "#7CFC00"));
							connItemContainer.selectTable(po.getDefaultSchema());
							DBConns.flushChoiceBox(connName);
						});
					} else {
						Platform.runLater(() -> {
							MyAlert.errorAlert(
									" Cannot connect ip:" + po.getHostOrFile() + " port:" + po.getPort() + "  !");
							item.getValue().setIcon(IconGenerator.svgImageUnactive("unlink"));
							ComponentGetter.treeView.refresh();

						});

					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.debug(e.getMessage());
					Platform.runLater(() -> {
						MyAlert.errorAlert(" Error !");
						item.getValue().setIcon(IconGenerator.svgImage("unlink", "red"));
						ComponentGetter.treeView.refresh();
					});

				} finally {
					DBConns.flushChoiceBoxGraphic();
					po1.setInitConnectionNodeStatus(false);
				}

			}
		};
		t.start();
	}

//	更新节点的
	public static void refreshConnOrder() {
		Connection conn = SqluckyAppDB.getConn();
		try {
			logger.info("refreshConnOrder");
			TreeView<TreeNodePo> treeView = ComponentGetter.treeView;
			TreeItem<TreeNodePo> root = treeView.getRoot();
			ObservableList<TreeItem<TreeNodePo>> ls = root.getChildren();
			int size = ls.size();
			for (int i = 0; i < size; i++) {
				TreeItem<TreeNodePo> nopo = ls.get(i);
				String name = nopo.getValue().getName();
				SqluckyConnector po = DBConns.get(name);
				int id = po.getId();
				updateDataOrder(conn, id, i);
			}
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

	}

	public static void updateDataOrder(Connection conn, int id, int order) {
		String sql = " UPDATE CONNECTION_INFO  set  ORDER_TAG = " + order + "  where ID = " + id;
		try {
			DBTools.execDML(conn, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 代码大写
	public static void UpperCaseSQLTextSelectText() {

		CodeArea code = MyEditorSheetHelper.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		// 将原文本删除
		code.deleteText(start, end);

		code.insertText(start, text.toUpperCase());
		MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
	}

	// 代码小写
	public static void LowerCaseSQLTextSelectText() {

		CodeArea code = MyEditorSheetHelper.getCodeArea();
		String text = code.getSelectedText();
		if (StrUtils.isNullOrEmpty(text))
			return;
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		// 将原文本删除
		code.deleteText(start, end);

		code.insertText(start, text.toLowerCase());
		MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
	}

	// 驼峰命名转下划线
	public static void CamelCaseUnderline() {

		CodeArea code = MyEditorSheetHelper.getCodeArea();
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
		MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
	}

	// 下划线 轉 驼峰命名
	public static void underlineCaseCamel() {

		CodeArea code = MyEditorSheetHelper.getCodeArea();
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
		MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
	}

	public static void selectTextAddString() {
		CodeArea code = MyEditorSheetHelper.getCodeArea();
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
		MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
	}

	// 代码添加注释-- 或去除注释
	public static void addAnnotationSQLTextSelectText() {

		CodeArea code = MyEditorSheetHelper.getCodeArea();
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
		MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
	}
 


	public static void hideLeft() {
		JFXButton btn = CommonButtons.hideLeft;
		if (ComponentGetter.treeAreaDetailPane.showDetailNodeProperty().getValue()) {
			ComponentGetter.treeAreaDetailPane.setShowDetailNode(false);
			btn.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-right"));

		} else {
			double dpval = ComponentGetter.treeAreaDetailPane.getDividerPosition();
			if (dpval < 0.1) {
				double wi = ComponentGetter.masterDetailPane.getWidth();
				double tbp = 275.0;
				double val = tbp / wi;
				ComponentGetter.treeAreaDetailPane.setDividerPosition(val);
			}
			ComponentGetter.treeAreaDetailPane.setShowDetailNode(true);
			btn.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-left"));

		}
	}
	public static void hideLeftBottom() {
		JFXButton btnLeft = CommonButtons.hideLeft; // AllButtons.btns.get("hideLeft");
		JFXButton btnBottom = CommonButtons.hideBottom; // AllButtons.btns.get("hideBottom");
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

	// 连接测试
	public static boolean isAliveTestAlert(SqluckyConnector connpo, Button testBtn) {
		Thread t = new Thread() {
			@Override
			public void run() {
				connpo.getConn();
				Platform.runLater(() -> {
					if (connpo.isAlive()) {
						String infoStr = connpo.DBInfo(connpo.getConn()); // Dbinfo.getDBInfo(connpo.getConn());
						MyAlert.infoAlert("  Successfully  ! \n" + infoStr);
						connpo.closeConn();
						testBtn.setStyle("-fx-background-color: green ");
					} else {
						MyAlert.errorAlert(" Connect fail !");

					}
					testBtn.setGraphic(null);

				});
			}
		};
		t.start();
		return false;
	}

	// 收缩treeview
	public static void shrinkTreeView() {
		TreeItem<TreeNodePo> root = ComponentGetter.treeView.getRoot();
		shrinkUnfoldTreeViewHelper(root, false);
	}

	// 展开treeview
	public static void unfoldTreeView() {
		TreeItem<TreeNodePo> root = ComponentGetter.treeView.getRoot();
		root.setExpanded(true);
		shrinkUnfoldTreeViewHelper(root, true);
	}

	private static void shrinkUnfoldTreeViewHelper(TreeItem<TreeNodePo> node, boolean tf) {
		ObservableList<TreeItem<TreeNodePo>> subNodes = node.getChildren();
		for (int i = 0; i < subNodes.size(); i++) {
			TreeItem<TreeNodePo> subnode = subNodes.get(i);
			if (subnode.getChildren().size() > 0) {
				subnode.setExpanded(tf);
				shrinkUnfoldTreeViewHelper(subnode, tf);
			}
		}

		ComponentGetter.treeView.refresh();
	}

	// 保证theme状态
	public static void saveThemeStatus(String val) {
		ConfigVal.THEME = val;
		Connection conn = SqluckyAppDB.getConn();
		SqluckyAppDB.saveConfig(conn, "THEME", val);
		SqluckyAppDB.closeConn(conn);
	}

	// 设置整体样式
	public static void setTheme(String val) {
		saveThemeStatus(val);
		// 根据新状态加载新样式
		CommonUtils.loadCss(ComponentGetter.primaryscene);
		MyEditorSheetHelper.changeThemeAllCodeArea();
//		changeSvgColor(); // 修改按钮颜色
	}

 
	// 设置文件打开时候目录path, 便于二次打开可以直达该目录
	public static void setOpenfileDir(String val) {
		Connection conn = SqluckyAppDB.getConn();
		SqluckyAppDB.saveConfig(conn, "OPEN_FILE_DIR", val);
		SqluckyAppDB.closeConn(conn);
		ConfigVal.openfileDir = val;
	}

	 
		// TODO 打开sql文件
		public static void openSqlFile() {
			try {
				File f = FileOrDirectoryChooser.showOpenSqlFile("Open", ComponentGetter.primaryStage);
				if (f == null)
					return;
				String charset = FileTools.detectFileCharset(f);
				if (charset == null) {
					new RuntimeException("Open failed!");
				}
				String val = FileUtils.readFileToString(f, charset);
				String tabName = "";
//				ComponentGetter.fileEncode.put( f.getPath(), encode);
				if (StrUtils.isNotNullOrEmpty(f.getPath())) {
//					id = ConfigVal.SAVE_TAG + f.getPath();
					tabName = FileTools.fileName(f.getPath());
					setOpenfileDir(f.getPath());
				}
				DocumentPo scpo = new DocumentPo();
				scpo.setEncode(charset);
				scpo.setFileFullName(f.getAbsolutePath());
				scpo.setText(val);
				scpo.setTitle(tabName);
//				MyEditorSheet sheet = ScriptTabTree.findMyTabByScriptPo(scpo);
				MyEditorSheet sheet =  ComponentGetter.appComponent.findMyTabByScriptPo(scpo);

				if (sheet != null) { // 如果已经存在就不用重新打开
					sheet.showEditor();
				} else {
					MyEditorSheetHelper.createTabFromSqlFile(scpo);
				}

			} catch (IOException e) {
				MyAlert.errorAlert(e.getMessage());
				e.printStackTrace();
			}
		}
		


		// 打开系统中的一个sql文件, 并在主界面显示
		public static void openSqlFile(File f) {
			try {
				if (f == null)
					return;
				String charset = FileTools.detectFileCharset(f);
				if (charset == null) {
					new RuntimeException("Open failed!");
				}
				String val = FileUtils.readFileToString(f, charset);
				String tabName = "";
//				ComponentGetter.fileEncode.put( f.getPath(), encode);
				if (StrUtils.isNotNullOrEmpty(f.getPath())) {
//					id = ConfigVal.SAVE_TAG + f.getPath();
					tabName = FileTools.fileName(f.getPath());
					AppCommonAction.setOpenfileDir(f.getPath());
				}
				DocumentPo scpo = new DocumentPo();
				scpo.setEncode(charset);
				scpo.setFileFullName(f.getAbsolutePath());
				scpo.setText(val);
				scpo.setTitle(tabName);

//				MyEditorSheet sheet = ScriptTabTree.findMyTabByScriptPo(scpo);
//				MyEditorSheet sheet = ScriptTabTree.findMyTabByScriptPo(scpo);
				MyEditorSheet sheet =  ComponentGetter.appComponent.findMyTabByScriptPo(scpo);
				if (sheet != null) { // 如果已经存在就不用重新打开
					sheet.showEditor();
				} else {
					MyEditorSheetHelper.createTabFromSqlFile(scpo);
				}

			} catch (IOException e) {
				MyAlert.errorAlert(e.getMessage());
				e.printStackTrace();
			}
		}
	
	// 改变字体大小
	public static void changeFontSize(boolean isPlus) {
		// 获取当前的 size
		if (ConfigVal.FONT_SIZE == -1) {
			String path = FileUtils.getUserDirectoryPath() + "/.sqlucky/font-size.css";
			String str = FileTools.read(path);
			String val = str.split("\n")[0];
			val = val.substring(2, val.lastIndexOf("*/"));
			ConfigVal.FONT_SIZE = Integer.valueOf(val);
		}
		int sz = ConfigVal.FONT_SIZE;
		if (isPlus) {
			sz += 1;
		} else {
			sz -= 1;
		}

		if (sz > 20) {
			sz = 20;
		}

		if (sz < 10) {
			sz = 10;
		}
		CommonUtils.setFontSize(sz);

		ConfigVal.FONT_SIZE = sz;

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
		var name = AppCommonAction.getComboBoxDbConnName();
		if (StrUtils.isNotNullOrEmpty(name)) {
			SqluckyConnector dpov = DBConns.get(name);
			return dpov;
		}
		return null;
	}

	public static final int DROP_COLUMN = 1;
	public static final int ALTER_COLUMN = 2;
	public static final int ADD_COLUMN = 3;

 
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

	public static RsVal exportSQL(MyBottomSheet myBottomSheet, int ty, String colname) {
		RsVal rv = myBottomSheet.tableInfo();
		return exportSQL(ty, colname, rv);
	}

	// 添加新字段
	public static void addNewColumn(MyBottomSheet myBottomSheet) {
		RsVal rv = myBottomSheet.tableInfo();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			RsVal rv2 = exportSQL(myBottomSheet, ADD_COLUMN, x);
			AppCommonAction.execExportSql(rv2.sql, rv2.conn, rv.dbconnPo);
		};
		DialogTools.showExecWindow(rv.tableName + " add column : input words like 'MY_COL CHAR(10)'", "", caller);

	}

	// 执行导出的sql
	public static Long execExportSql(String sql, Connection conn, SqluckyConnector dbconnPo) {
		Long key = ComponentGetter.appComponent.refreshDataTableView(dbconnPo, sql, "", false); 
		return key;
	}
	// 添加新字段
	public static void addNewColumn(SqluckyConnector dbc, String schema, String tablename) {

		Connection conn = dbc.getConn();
		Consumer<String> caller = x -> {
			if (StrUtils.isNullOrEmpty(x.trim()))
				return;
			String colname = x.trim();
			String sql = dbc.getExportDDL().exportAlterTableAddColumn(conn, schema, tablename, colname);
			AppCommonAction.execExportSql(sql, conn, dbc);
		};
		DialogTools.showExecWindow(tablename + " add column : input words like 'MY_COL CHAR(10)'", "", caller);

	}

	// 关闭数据展示表格
	public static void closeDataTable() {
//		关闭数据显示tab页
		Tab t = ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
		if (t != null) {
			// tab 名称
			String title = CommonUtils.tabText(t);
			ComponentGetter.dataTabPane.getTabs().remove(t);
			// 都关闭页, 隐藏下半窗体
			int tabSize = ComponentGetter.dataTabPane.getTabs().size();
			if (tabSize == 0) {
//				SdkComponent.hideBottom();
			} else {
				// 选择最后一个
				if (ConfigVal.EXEC_INFO_TITLE.equals(title)) {
					ComponentGetter.dataTabPane.getSelectionModel().select(tabSize - 1);
				}

			}
		}

	}
	 
	// 重启应用
	public static void restartApp() {
		Consumer<String> caller = x -> {
			ComponentGetter.appComponent.saveApplicationStatusInfo();
			ComponentGetter.appComponent.reboot();
//			Restart.reboot();
		};
		MyAlert.myConfirmation("Restart Application ? ", caller);
	}
 
	// 设置整体样式
		public static void setThemeRestart(String val) {
			// 询问是否重启app, 如果不重启再重新加载样式
			AppCommonAction.changeThemeRestartApp(val);
		}



		public static void changeThemeRestartApp(String val) {
			Consumer<String> ok = x -> {
				AppCommonAction.saveThemeStatus(val);
				ComponentGetter.appComponent.saveApplicationStatusInfo();
				ComponentGetter.appComponent.reboot();
				//				Restart.reboot();
			};
			Consumer<String> cancel = x -> {
				AppCommonAction.saveThemeStatus(val);
				CommonUtils.loadCss(ComponentGetter.primaryscene);
				MyEditorSheetHelper.changeThemeAllCodeArea();
			};
			MyAlert.myConfirmation("Change Theme Restart Application Will Better, ok ? ", ok, cancel);
		}
}
