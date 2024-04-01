package net.tenie.Sqlucky.sdk.component;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.editor.HighLightingEditorUtils;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.sql.Connection;

public class MyEditorSheet extends Tab {
//	private Tab tab = new Tab();
	private SqluckyEditor sqluckyEditor; // 编辑器(比如高亮的文本编辑器)
	private DocumentPo documentPo; // 文本内容

	private Integer tabConnIdx ; // 与数据库链接的绑定, (切换tab的时候, 可以连着一起切换数据库链接)
	private boolean isInit = false; // 是否初始化
	private boolean isModify = false;
	// 放查找面板, 文本area 的容器
	private VBox vbox;
	// 查找面板
//	private AnchorPane findAnchorPane;
	// 替换面板
	private HBox replaceAnchorPane;
//	private FindReplaceTextPanel findReplacePanel;

	public void clean() {
//		this.setUserData(null);
		this.setContent(null);
		if (vbox != null) {
			vbox.getChildren().clear();
			vbox = null;
		}
//		if (findAnchorPane != null) {
//			findAnchorPane.getChildren().clear();
//			findAnchorPane = null;
//		}
		if (replaceAnchorPane != null) {
			replaceAnchorPane.getChildren().clear();
			replaceAnchorPane = null;
		}

//		if (findReplacePanel != null) {
//			findReplacePanel = null;
//		}
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
		this.selectedProperty().addListener(l -> {
			boolean isSel = this.isSelected();
			if (isSel && !isInit) {
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
//			if(isSel){
//				ComponentGetter.focusedSqluckyEditor = this.sqluckyEditor;
//			}
		});
	}

	// 设置editor的时候 设置文本
	public void setSqluckyEditor(SqluckyEditor sqluckyEditor) {
		this.sqluckyEditor = sqluckyEditor;
		this.sqluckyEditor.setDocumentPo(documentPo);
//		VBox pane = sqluckyEditor.getCodeAreaPane();
		vbox = new VBox();
		vbox.setSpacing(3);
		vbox.getChildren().add(sqluckyEditor);
		VBox.setVgrow(sqluckyEditor, Priority.ALWAYS);
		this.setContent(vbox);
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
		SqluckyEditor sqlEditor = HighLightingEditorUtils.noKeyWordEditor();
		this.setSqluckyEditor(sqlEditor);
	}

	// tab的属性设置, 名称, 右键菜单,
	public void setTabProperty() {
		// 右键菜单
		this.setContextMenu(MyTabMenu());
		setTitleName();

		// 关闭前事件
		this.setOnCloseRequest(tabCloseReq());
		// 选中事件
		this.setOnSelectionChanged(value -> {
			boolean isSe = this.isSelected();
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
		CommonUtils.setTabName(this, TabName);
	}

	public SqluckyEditor getSqluckyEditor() {
		return sqluckyEditor;
	}

	public void showEditor() {
		Platform.runLater(() -> {
			var myTabPane = ComponentGetter.mainTabPane;
			if (!myTabPane.getTabs().contains(this)) {
				myTabPane.getTabs().add(this);// 在指定位置添加Tab
			}
			myTabPane.getSelectionModel().select(this);
		});

	}

	public void showEditor(int idx) {
		Platform.runLater(() -> {
			var myTabPane = ComponentGetter.mainTabPane;
			if (!myTabPane.getTabs().contains(this)) {
				myTabPane.getTabs().add(idx, this); // 在指定位置添加Tab
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
		if( documentPo.getSaveToDB()){
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
		if (tabs.contains(this)) {
			tabs.remove(this);
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
			myTabPane.getTabs().add(this);

		});

		MenuItem closeRight = new MenuItem("Close Tabs To The Right");
		closeRight.setOnAction(e -> {
			var myTabPane = ComponentGetter.mainTabPane;
			var tabs = myTabPane.getTabs();
			int idx = tabs.indexOf(this);
			int tsize = tabs.size();
			if ((idx + 1) < tsize) {
				for (int i = idx + 1; i < tsize; i++) {
					Tab t = tabs.get(i);
					if(t instanceof  MyEditorSheet mt){
//						MyEditorSheet mt = (MyEditorSheet) t.getUserData();
						mt.getDocumentPo().setOpenStatus(0);
						mt.syncScriptPo();
					}

				}
				tabs.remove(idx + 1, tsize);
			}
		});

		MenuItem closeLeft = new MenuItem("Close Tabs To The Left");
		closeLeft.setOnAction(e -> {
			var myTabPane = ComponentGetter.mainTabPane;
			var tabs = myTabPane.getTabs();
			int idx = tabs.indexOf(this);
			if (idx > 0) {
				for (int i = 0; i < idx; i++) {
					Tab t = tabs.get(i);
					if(t instanceof  MyEditorSheet mt){
//						MyEditorSheet mt = (MyEditorSheet) t.getUserData();
						mt.getDocumentPo().setOpenStatus(0);
						mt.syncScriptPo();
					}

				}
				tabs.remove(0, idx);
			}
		});

		contextMenu.getItems().addAll(closeAll, closeOther, closeRight, closeLeft);
		contextMenu.setOnShowing(e -> {
			var myTabPane = ComponentGetter.mainTabPane;
			int idx = myTabPane.getTabs().indexOf(this);
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

	public DocumentPo getDocumentPo() {
		return documentPo;
	}

	public void setDocumentPo(DocumentPo documentPo) {
		this.documentPo = documentPo;
	}

	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean isModify) {
		this.isModify = isModify;
	}

	// 得到 tab的显示名称
	public String getTitle() {
		return CommonUtils.tabText(this);
	}

	// 设置 tab的显示名称
	public void setTitle(String val) {
		CommonUtils.setTabName(this, val);
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

//	public FindReplaceTextPanel getFindReplacePanel() {
//		return findReplacePanel;
//	}

//	public void setFindReplacePanel(FindReplaceTextPanel findReplacePanel) {
//		this.findReplacePanel = findReplacePanel;
//	}

	public File getFile() {
		if (documentPo == null)
			return null;
		return documentPo.getFile();
	}

//	public AnchorPane getFindAnchorPane() {
//		return findAnchorPane;
//	}
//
//	public void setFindAnchorPane(AnchorPane findAnchorPane) {
//		this.findAnchorPane = findAnchorPane;
//		vbox.getChildren().add(0, findAnchorPane);
//	}

	public HBox getReplaceAnchorPane() {
		return replaceAnchorPane;
	}

	public void setReplaceAnchorPane(HBox replaceAnchorPane) {
		this.replaceAnchorPane = replaceAnchorPane;
		vbox.getChildren().add(1, replaceAnchorPane);
	}

	// 存在 就显示出来
	public boolean existTabShow() {
		var myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane.getTabs().contains(this)) {
			myTabPane.getSelectionModel().select(this);
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

	/**
	 * 判断是否选择的状态
     */
	public boolean isSelecting() {
		var myTabPane = ComponentGetter.mainTabPane;
		if (myTabPane.getTabs().contains(this)) {
			int idxThis = myTabPane.getTabs().indexOf(this);
			int currentSelect = myTabPane.getSelectionModel().getSelectedIndex();
            return idxThis == currentSelect;
		}
		return false;
	}
}
