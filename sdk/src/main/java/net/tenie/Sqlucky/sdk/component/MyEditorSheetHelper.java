package net.tenie.Sqlucky.sdk.component;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.codeArea.HighLightingEditor;
import net.tenie.Sqlucky.sdk.component.codeArea.HighLightingEditorContextMenu;
import net.tenie.Sqlucky.sdk.component.codeArea.MyAutoComplete;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
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

	// 给主界面添加tab, 并选中
	public static void mainTabPaneAddAndSelect(Tab nTab, int idx) {
		var myTabPane = ComponentGetter.mainTabPane;
		myTabPane.getTabs().add(idx, nTab); // 在指定位置添加Tab
		myTabPane.getSelectionModel().select(idx);
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
//		MyAreaTab2 nwTab = new MyAreaTab2(labe);
		MyEditorSheet sheet = new MyEditorSheet(labe);
		// 设置 高亮编辑器
		createHighLightingEditor(sheet);
		mainTabPaneAddAndSelect(sheet.getTab(), size);
//		ScriptTabTree.treeRootAddItem(nwTab);
		ComponentGetter.appComponent.scriptTreeAddItem(sheet);
		return sheet;
	}

	// 通过documentpo 创建一个高亮的编辑器
	public static MyEditorSheet createHighLightingEditor(DocumentPo po) {
		MyEditorSheet myEditorSheet = new MyEditorSheet(po);
		createHighLightingEditor(po);
		return myEditorSheet;
	}

	/*
	 * 创建一个高亮的Editor, 创建自动补全, 右键菜单
	 */
	public static void createHighLightingEditor(MyEditorSheet myEditorSheet) {

		MyAutoComplete myAuto = new MyAutoComplete();
		SqluckyEditor sqlCodeAreaEditor = new HighLightingEditor(myAuto, myEditorSheet);
//		右键菜单
		HighLightingEditorContextMenu cm = new HighLightingEditorContextMenu(sqlCodeAreaEditor);
		sqlCodeAreaEditor.setContextMenu(cm);
		myEditorSheet.setSqluckyEditor(sqlCodeAreaEditor);

//		StackPane pane = sqlCodeAreaEditor.getCodeAreaPane();
//		VBox vbox = new VBox();
//		vbox.getChildren().add(pane);
//		VBox.setVgrow(pane, Priority.ALWAYS);
//		sheetTab.setContent(vbox);

//		// 关闭前事件
//		sheetTab.setOnCloseRequest(myEditorSheet.tabCloseReq(myTabPane, docPo));
//		// 选中事件
//		sheetTab.setOnSelectionChanged(value -> {
//			DBConns.changeChoiceBox(myEditorSheet.getTabConnIdx());
////			MainTabInfo ti = MainTabs.get(this);
//
////			if (ti != null) {
////				DBConns.changeChoiceBox(ti.getTabConnIdx());
//
////			}
//
//		});

		// 设置sql 文本
//		myEditorSheet.initTabSQLText(docPo.getText());

		// 右键菜单

	}

	// 获取当前tab中的EditorSheet
	public static MyEditorSheet getActivationEditorSheet() {
		TabPane myTabPane = ComponentGetter.mainTabPane;
		Tab selectionTab = myTabPane.getSelectionModel().getSelectedItem();
		MyEditorSheet myEditorSheet = (MyEditorSheet) selectionTab.getUserData();
		return myEditorSheet;
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
}
