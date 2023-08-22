package net.tenie.Sqlucky.sdk.component;

import java.io.File;
import java.sql.Connection;

import org.fxmisc.richtext.CodeArea;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.editor.HighLightingEditorUtils;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

public class MyEditorSheet {
	private Tab tab = new Tab();
	private SqluckyEditor sqluckyEditor; // 编辑器(比如高亮的文本编辑器)
	private DocumentPo documentPo; // 文本内容

	private Integer tabConnIdx ; // 与数据库链接的绑定, (切换tab的时候, 可以连着一起切换数据库链接)
	private boolean isInit = false; // 是否初始化
	private boolean isModify = false;
	// 放查找面板, 文本area 的容器
	private VBox vbox;
	// 查找面板
	private AnchorPane findAnchorPane;
	// 替换面板
	private AnchorPane replaceAnchorPane;
	private FindReplaceTextPanel findReplacePanel;

	public void clean() {
		if (tab != null) {
			tab.setUserData(null);
			tab.setContent(null);
			tab = null;
		}
		if (vbox != null) {
			vbox.getChildren().clear();
			vbox = null;
		}
		if (findAnchorPane != null) {
			findAnchorPane.getChildren().clear();
			findAnchorPane = null;
		}
		if (replaceAnchorPane != null) {
			replaceAnchorPane.getChildren().clear();
			replaceAnchorPane = null;
		}

		if (findReplacePanel != null) {
			findReplacePanel = null;
		}
		if (documentPo != null) {
			documentPo = null;
		}

	}

	public MyEditorSheet(DocumentPo valDocumentPo, SqluckyEditor sqluckyEditor) {
		if (valDocumentPo.getSaveToDB()) {
			if (valDocumentPo.getId() == null) {
				documentPo = ComponentGetter.appComponent.scriptArchive(valDocumentPo.getTitle(),
						valDocumentPo.getText(), valDocumentPo.getFileFullName(), valDocumentPo.getEncode(),
						valDocumentPo.getParagraph());
			} else {
				documentPo = valDocumentPo;
			}
		} else {
			documentPo = valDocumentPo;
		}

		delayInit(sqluckyEditor);
	}

	public MyEditorSheet(String TabName, SqluckyEditor sqluckyEditor) {
		documentPo = ComponentGetter.appComponent.scriptArchive(TabName, "", "", "UTF-8", 0);
		documentPo.setOpenStatus(1);
//		setTabProperty();
		delayInit(sqluckyEditor);
	}

	// 延迟初始化sheet, 如果 SqluckyEditor为空创建默认的SqluckyEditor对象
	public void delayInit(SqluckyEditor sqluckyEditor) {
		// Tab 其他属性设置
		setTabProperty();
		// 选择title的时候初始化tab内容
		tab.selectedProperty().addListener(l -> {
			boolean isSel = tab.isSelected();
			if (isSel && isInit == false) {

				if (sqluckyEditor == null) {
					if (documentPo.getType() == DocumentPo.IS_SQL) {
						setDefaultEditor();
					} else if (documentPo.getType() == DocumentPo.IS_TEXT) {
						createTextMyTab();
					}
				} else {
					setSqluckyEditor(sqluckyEditor);
				}

				isInit = true;

			}
		});
	}

	// 设置editor的时候 设置文本
	public void setSqluckyEditor(SqluckyEditor sqluckyEditor) {
		this.sqluckyEditor = sqluckyEditor;

		StackPane pane = sqluckyEditor.getCodeAreaPane();
		vbox = new VBox();
		vbox.getChildren().add(pane);
		VBox.setVgrow(pane, Priority.ALWAYS);
		tab.setContent(vbox);
		documentPo.setOpenStatus(1);
		initTabSQLText(documentPo.getText());
	}

	// 默认的SqluckyEditor
	public void setDefaultEditor() {
		SqluckyEditor sqlEditor = HighLightingEditorUtils.sqlEditor();
		this.setSqluckyEditor(sqlEditor);
	}

	// 创建一个纯文本的编辑器
	private void createTextMyTab() {
		SqluckyEditor sqlEditor = new MyTextEditor();
		this.setSqluckyEditor(sqlEditor);
	}

	// 删除查找替换面板
	public void delFindReplacePane() {
		if (findAnchorPane != null) {
			if (vbox.getChildren().contains(findAnchorPane)) {
				vbox.getChildren().remove(findAnchorPane);
			}
			findAnchorPane = null;

		}
		if (replaceAnchorPane != null) {
			if (vbox.getChildren().contains(replaceAnchorPane)) {
				vbox.getChildren().remove(replaceAnchorPane);
			}
			replaceAnchorPane = null;
		}
		if (findReplacePanel != null) {
			findReplacePanel = null;
		}
	}

	// 判断查找面板是否显示中
	public boolean findPaneIsShowing() {
		if (findAnchorPane != null) {
			return true;
		}
		return false;
	}

	// tab的属性设置, 名称, 右键菜单,
	public void setTabProperty() {
		tab.setUserData(this); // 将当前对象放入tab中
		// 右键菜单
		tab.setContextMenu(MyTabMenu());
		setTitleName();

		// 关闭前事件
		tab.setOnCloseRequest(tabCloseReq());
		// 选中事件
		tab.setOnSelectionChanged(value -> {
			boolean isSe = tab.isSelected();
			if(isSe) {
				Integer tmpIdx = this.getTabConnIdx() ;
				if(tmpIdx !=null) {
					DBConns.changeChoiceBox(this.getTabConnIdx());
				}else {
					tmpIdx = DBConns.choiceBoxIndex();
					this.setTabConnIdx(tmpIdx) ;
				}
			}
		});
	}

	// 设置title name
	public void setTitleName() {
		String TabName = documentPo.getTitle();
		// 名称
		CommonUtils.setTabName(tab, TabName);
	}

	public SqluckyEditor getSqluckyEditor() {
		return sqluckyEditor;
	}

	public void showEditor() {
		Platform.runLater(() -> {
			var myTabPane = ComponentGetter.mainTabPane;
			if (myTabPane.getTabs().contains(tab) == false) {
				myTabPane.getTabs().add(tab);// 在指定位置添加Tab
			}
			myTabPane.getSelectionModel().select(tab);
		});

	}

	public void showEditor(int idx) {
		Platform.runLater(() -> {
			var myTabPane = ComponentGetter.mainTabPane;
			if (myTabPane.getTabs().contains(tab) == false) {
				myTabPane.getTabs().add(idx, tab); // 在指定位置添加Tab
			}

			myTabPane.getSelectionModel().select(idx);

		});

	}

	/**
	 * tab 关闭时：阻止关闭最后一个
	 */
	public EventHandler<Event> tabCloseReq() {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				syncScriptPo();
				documentPo.setOpenStatus(0);
//				 // 如果只有一个窗口就不能关闭 
//				TabPane myTabPane
//				if (myTabPane.getTabs().size() == 1) {
//  					e.consume();
//				}

			}
		};
	}

	public void syncScriptPo() {
		Connection conn = SqluckyAppDB.getConn();
		try {
			syncScriptPo(conn);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

	}

	public void syncScriptPo(Connection conn) {
		String sql = getAreaText();
		String title = getTitle();
		if (sql != null) {
			documentPo.setText(sql);
		}

		documentPo.setTitle(title);
		if (documentPo.getSaveToDB()) {
			ComponentGetter.appComponent.updateScriptArchive(conn, documentPo);

		}
		ComponentGetter.appComponent.scriptTreeRefresh();
	}

	public void saveScriptPo(Connection conn) {
		String sql = getAreaText();
		String title = getTitle();
		if (sql != null) {
			documentPo.setText(sql);
		}
		int p = getParagraph();
		documentPo.setTitle(title);
		documentPo.setParagraph(p);
//		AppDao.updateScriptArchive(conn, documentPo);
		ComponentGetter.appComponent.updateScriptArchive(conn, documentPo);
	}

	// 设置tab 中的 area 中的文本
	public void initTabSQLText(String text) {
		var code = sqluckyEditor.getCodeArea();
		code.appendText(text);
		sqluckyEditor.highLighting();
	}

	public String getAreaText() {
		if (sqluckyEditor != null) {
			CodeArea code = sqluckyEditor.getCodeArea();
			String sqlText = code.getText();
			return sqlText;
		}

		return documentPo.getText();
	}

	public int getParagraph() {
		if (sqluckyEditor != null) {
			CodeArea code = sqluckyEditor.getCodeArea();
			int paragraph = code.getCurrentParagraph() > 11 ? code.getCurrentParagraph() - 10 : 0;
			return paragraph;
		}
		return 0;
	}

	// 删除 TabPane中的所有 MyTab, 不删除treeView中的节点
	private void closeAll() {
		MyEditorSheetHelper.archiveAllScript();

	}

	// 销毁, 从界面上移除tab,并清空属性的引用
	public void destroySheet() {
		var myTabPane = ComponentGetter.mainTabPane;
		var tabs = myTabPane.getTabs();
		if (tabs.contains(tab)) {
			tabs.remove(tab);
		}
		this.clean();

	}

	// 右键菜单
	public ContextMenu MyTabMenu() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeAll = new MenuItem("Close ALl");
		closeAll.setOnAction(e -> {
			closeAll();
		});

		MenuItem closeOther = new MenuItem("Close Other");
		closeOther.setOnAction(e -> {
			closeAll();
			var myTabPane = ComponentGetter.mainTabPane;
			myTabPane.getTabs().add(this.tab);

		});

		MenuItem closeRight = new MenuItem("Close Tabs To The Right");
		closeRight.setOnAction(e -> {
			var myTabPane = ComponentGetter.mainTabPane;
			var tabs = myTabPane.getTabs();
			int idx = tabs.indexOf(this.tab);
			int tsize = tabs.size();
			if ((idx + 1) < tsize) {
				for (int i = idx + 1; i < tsize; i++) {
					Tab t = tabs.get(i);
					MyEditorSheet mt = (MyEditorSheet) t.getUserData();
					mt.getDocumentPo().setOpenStatus(0);
					mt.syncScriptPo();
				}
				tabs.remove(idx + 1, tsize);
			}
		});

		MenuItem closeLeft = new MenuItem("Close Tabs To The Left");
		closeLeft.setOnAction(e -> {
			var myTabPane = ComponentGetter.mainTabPane;
			var tabs = myTabPane.getTabs();
			int idx = tabs.indexOf(this.tab);
			if (idx > 0) {
				for (int i = 0; i < idx; i++) {
					Tab t = tabs.get(i);
					MyEditorSheet mt = (MyEditorSheet) t.getUserData();
					mt.getDocumentPo().setOpenStatus(0);
					mt.syncScriptPo();
				}
				tabs.remove(0, idx);
			}
		});

		contextMenu.getItems().addAll(closeAll, closeOther, closeRight, closeLeft);
		contextMenu.setOnShowing(e -> {
			var myTabPane = ComponentGetter.mainTabPane;
			int idx = myTabPane.getTabs().indexOf(this.tab);
			int size = myTabPane.getTabs().size();
			if (idx == 0) {
				closeLeft.setDisable(true);
			} else {
				closeLeft.setDisable(false);
			}

			if (idx == (size - 1)) {
				closeRight.setDisable(true);
			} else {
				closeRight.setDisable(false);
			}

			if (size == 1) {
				closeOther.setDisable(true);
			} else {
				closeOther.setDisable(false);
			}

		});
		return contextMenu;
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

	public DocumentPo getDocumentPo() {
		return documentPo;
	}

	public void setDocumentPo(DocumentPo documentPo) {
		this.documentPo = documentPo;
	}

	// Tab的名称(也是文件的名称)
//	public String getTabName() {
//		return documentPo.getTitle();
//	}

	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean isModify) {
		this.isModify = isModify;
	}

	// 得到 tab的显示名称
	public String getTitle() {
		return CommonUtils.tabText(this.tab);
	}

	// 设置 tab的显示名称
	public void setTitle(String val) {
		CommonUtils.setTabName(this.tab, val);
	}

	public Integer getTabConnIdx() {
		return tabConnIdx;
	}

	public void setTabConnIdx(Integer tabConnIdx) {
		this.tabConnIdx = tabConnIdx;
	}

	public VBox getVbox() {
		return vbox;
	}

	public void setVbox(VBox vbox) {
		this.vbox = vbox;
	}

	public FindReplaceTextPanel getFindReplacePanel() {
		return findReplacePanel;
	}

	public void setFindReplacePanel(FindReplaceTextPanel findReplacePanel) {
		this.findReplacePanel = findReplacePanel;
	}

	public File getFile() {
		if (documentPo == null)
			return null;
		return documentPo.getFile();
	}

	public AnchorPane getFindAnchorPane() {
		return findAnchorPane;
	}

	public void setFindAnchorPane(AnchorPane findAnchorPane) {
		this.findAnchorPane = findAnchorPane;
		vbox.getChildren().add(0, findAnchorPane);
	}

	public AnchorPane getReplaceAnchorPane() {
		return replaceAnchorPane;
	}

	public void setReplaceAnchorPane(AnchorPane replaceAnchorPane) {
		this.replaceAnchorPane = replaceAnchorPane;
		vbox.getChildren().add(1, replaceAnchorPane);
	}

	// 存在 就显示出来
	public boolean existTabShow() {
		var myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane.getTabs().contains(tab)) {
			myTabPane.getSelectionModel().select(tab);
			return true;
		}
		return false;
	}

	public void setFileText(String text) {
		documentPo.setText(text);
	}

	public void setFile(File file) {
		documentPo.setFile(file);
	}

	public void setIcon(Region icon) {
		documentPo.setIcon(icon);
	}

	public boolean isShowing() {
		var myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane.getTabs().contains(tab)) {
			int idxThis = myTabPane.getTabs().indexOf(tab);
			int currentSelect = myTabPane.getSelectionModel().getSelectedIndex();
			if (idxThis == currentSelect) {
				return true;
			}
		}
		return false;
	}
}
