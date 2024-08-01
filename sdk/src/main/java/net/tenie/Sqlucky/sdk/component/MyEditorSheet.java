package net.tenie.Sqlucky.sdk.component;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.editor.HighLightingEditorUtils;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.DialogTools;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MyEditorSheet extends Tab {
	// 在脚本树中的节点
	TreeItem<MyEditorSheet> treeItem ;


	private SqluckyEditor sqluckyEditor; // 编辑器(比如高亮的文本编辑器)
	private DocumentPo documentPo; // 文本内容

	private Integer tabConnIdx ; // 与数据库链接的绑定, (切换tab的时候, 可以连着一起切换数据库链接)
	private boolean isInit = false; // 是否初始化
	private boolean isModify = false;
	// 放查找面板, 文本area 的容器
	private VBox vbox;

	private boolean needReload = true;


	// 脚本树上的label
	private Label scriptTreeLabel = new Label();

	public void clean() {
		this.setContent(null);
		if (vbox != null) {
			vbox.getChildren().clear();
			vbox = null;
		}

		if (documentPo != null) {
			documentPo = null;
		}

	}

	public MyEditorSheet(DocumentPo valDocumentPo, SqluckyEditor sqluckyEditor) {
		if (valDocumentPo.getSaveToDB()) {
			if (valDocumentPo.getId() == null) {
				documentPo = ComponentGetter.appComponent.scriptArchive(valDocumentPo.getTitle().get(),
						valDocumentPo.getText(), valDocumentPo.getExistFileFullName(), valDocumentPo.getEncode(),
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
		scriptTreeLabel.textProperty().bind(this.getDocumentPo().getTitle());
		treeItem = new TreeItem<>(this);
		setTabTitleName();
		// 选中事件
		this.setOnSelectionChanged(value -> {
			boolean isSelected = this.isSelected();
			if(isSelected) {
				Integer tmpIdx = this.getTabConnIdx() ;
				if(tmpIdx !=null) {
					DBConns.changeChoiceBox(this.getTabConnIdx());
				}else {
					tmpIdx = DBConns.choiceBoxIndex();
					this.setTabConnIdx(tmpIdx) ;
				}
				// 脚本树中的节点选中
				if(ComponentGetter.scriptTreeView.getRoot().getChildren().contains(treeItem)){
					ComponentGetter.scriptTreeView.getSelectionModel().select(treeItem);
				}

				reloadText();
			}
		});
		// 选择title的时候初始化tab内容
		this.selectedProperty().addListener(l -> {
			boolean isSel = this.isSelected();
			if (isSel && !isInit) {
				// 右键菜单
				this.setContextMenu(createTabContextMenu());

				// 关闭前事件
				this.setOnCloseRequest(tabCloseReq());

				if (sqluckyEditor == null) {
					if (documentPo.getType() == DocumentPo.IS_SQL) {
						setDefaultEditor();
					} else if (documentPo.getType() == DocumentPo.IS_TEXT) {
						createTextMyTab();
					}
				} else {
					setSqluckyEditor(sqluckyEditor);

				}
                if (sqluckyEditor != null) {
                    sqluckyEditor.setSheet(this);
                }
                isInit = true;

			}
//			if(isSel){
//				ComponentGetter.focusedSqluckyEditor = this.sqluckyEditor;
//			}
		});
	}

	/**
	 * 界面上的文本保存到文件
	 */
	public void saveAreaTextToDocumentFile(){
		String sql = this.getAreaText();// Sql
		String filePath = documentPo.getExistFileFullName();
		if(StrUtils.isNotNullOrEmpty(filePath)){
			File file = new File(filePath);
			if(file.exists() && file.isFile()){
				try {
					FileTools.saveByEncode(filePath, sql, documentPo.getEncode());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}


    }

	/**
	 * 判断文件是否存在
	 */
	public boolean documentFileExists(){
		boolean tf = false;
		String filePath = documentPo.getExistFileFullName();
		if(StrUtils.isNotNullOrEmpty(filePath)){
			File file = new File(filePath);
			if(file.exists() && file.isFile()){
				tf = true;
			}
		}

		return tf;
	}

	/**
	 * 读取文件文本
	 * @return
	 */
	public String readDocumentFileText(){
		String val = "";
		String filePath = documentPo.getExistFileFullName();
		if(StrUtils.isNullOrEmpty(filePath)) return val;
		File file = new File(filePath);
		if(file.exists() && file.isFile()){
			String encode = documentPo.getEncode();
			val = FileTools.read(file, encode);
		}
		return val;
	}

	/**
	 * 文件中的文本和界面上的内容是否相等
	 * @return
	 */
	public boolean documentTextEqualsCodeAreaText(){
		boolean tf = true;
		String fileText = this.readDocumentFileText();
		if(fileText.contains("\r")){
			fileText = fileText.replaceAll("\r", "");
		}
		String codeText = sqluckyEditor.getCodeArea().getText();
		if(codeText.contains("\r")){
			codeText = codeText.replaceAll("\r", "");
		}

		int codeSize = codeText.length();
		int fileSize = fileText.length();
		if (codeSize != fileSize) {
			tf = false;
		} else {
			if (!fileText.equals(codeText)) {
				tf = false;
			}
		}

		return tf;
	}
	/**
	 * 原文被其他程序修改后, 重新加载
	 */
	public void reloadText(){
		if(sqluckyEditor == null ) return;
		if(!documentFileExists()) return;
		if(! needReload ) return;

		// 内容不同时提示
		if(! documentTextEqualsCodeAreaText()){
			int idx = sqluckyEditor.getCodeArea().getAnchor();
			CommonUtils.threadAwait(1);
			Platform.runLater(() -> {
				boolean confVal = MyAlert.myConfirmationShowAndWait("文本发生改变是否重新加载?");
				if (confVal) {
					sqluckyEditor.getCodeArea().clear();
					String fileTexttmp = this.readDocumentFileText();
					sqluckyEditor.getCodeArea().appendText(fileTexttmp);
					Platform.runLater(() -> {
						sqluckyEditor.getCodeArea().moveTo(idx);
					});
				} else {
					needReload = false;
//						CommonUtils.delayRunThread(this::setNeedReload, 5000);
				}
			});

		}

	}




	private void setNeedReload(){
		needReload = true;
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
		String text = documentPo.getText();
		if(documentFileExists()){
			text = readDocumentFileText();
			documentPo.setText(text);
		}

		initTabSQLText(text);
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

	// 设置title name
	public void setTabTitleName() {
//		String TabName = documentPo.getTitle().get();
		// 名称
		CommonUtils.setTabName(this, documentPo.getTitle());
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
			}
		};
	}

	public void syncScriptPo() {
		Connection conn = SqluckyAppDB.getConn();
		try {
			syncScriptPo(conn);
			ComponentGetter.appComponent.scriptTreeRefresh();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

	}

	public void syncScriptPo(Connection conn) {
		syncScriptPo(conn, null);
	}
	/**
	 * 保存到数据库
	 * @param conn
	 */
	public void syncScriptPo(Connection conn, String sql) {
		if(StrUtils.isNullOrEmpty(sql)){
			sql = getAreaText();
		}
		String title = getTitle();
		documentPo.setTitle(title);
		if (sql != null) {
			documentPo.setText(sql);
			int p = getParagraph();
			documentPo.setParagraph(p);
		}


		if (documentPo.getSaveToDB()) {
			ComponentGetter.appComponent.updateScriptArchive(conn, documentPo);

		}

	}

	public static List<Consumer<String>> ConsumerLs = new ArrayList<>();

	/**
	 * 界面上的文本保存到数据库
	 * @param conn
	 */
	public void saveScriptPo(Connection conn) {
		if( this.sqluckyEditor == null ) return;
		if( documentFileExists()){
			if( !documentTextEqualsCodeAreaText() ){
				ConsumerLs.add(stc->{
					boolean confVal = MyAlert.myConfirmationShowAndWait("文本发生改变是否保存?");
					if (confVal) {
						// 界面文本保存到文件
						saveAreaTextToDocumentFile();
						syncScriptPo(conn, "");
					}
//					else {
//						不保存的情况
//						String fileTexttmp = this.readDocumentFileText();
//						syncScriptPo(conn);
//					}
				});

//				Platform.runLater(()->{
//					boolean confVal = MyAlert.myConfirmationShowAndWait("文本发生改变是否保存?");
//					if (confVal) {
//						saveAreaTextToDocumentFile();
//					}else {
//						String fileTexttmp = this.readDocumentFileText();
//						syncScriptPo(conn, fileTexttmp);
//					}
//				});

			}
		}else{
			syncScriptPo(conn);
		}

//		var spo = this.getDocumentPo();
		// 将打开状态设置为1, 之后根据这个状态来恢复
		if (documentPo != null && documentPo.getId() != null) {
			String sql = this.getAreaText();
			if (StrUtils.isNotNullOrEmpty(sql) && sql.trim().length() > 0) {
				documentPo.setOpenStatus(1);
				// 当前激活的编辑页面
				if( this.isSelected()){
					documentPo.setIsActivate(1);
				}else {
					documentPo.setIsActivate(0);
				}

			} else {
				documentPo.setOpenStatus(0);
				documentPo.setIsActivate(0);
			}
		}
//		documentPo.getFileFullName()


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

	/**
	 *
	 * 光标所在段落
	 * @return
	 */
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

	private boolean checkSqlFileExist(){
		String fileName = this.documentPo.getExistFileFullName();
        return StrUtils.isNotNullOrEmpty(fileName);
    }

	/**
	 * 带确认的销毁Tab
	 */
	public void destroyTabConfirmation(){
		String fileName = this.documentPo.getExistFileFullName();
		if (StrUtils.isNotNullOrEmpty(fileName)) {
			MyAlert.myConfirmation("是否保存到文件:" + fileName,
					ok->{saveDiskAndDestroyTab();},
					no->{destroyTab();});
		}else{
			destroyTab();
		}
	}

	/**
	 * 从页面上关闭 tab, 但不做其他操作, 数据还在内存中, 可以从tree上重新打开
 	 */
	public void closeTab(){
		var myTabPane = ComponentGetter.mainTabPane;
		var tabs = myTabPane.getTabs();
		if (tabs.contains(this)) {
			syncScriptPo();
			documentPo.setOpenStatus(0);
			tabs.remove(this);
		}
	}

	// 销毁, 从界面上移除tab,并清空属性的引用
	public void destroyTab() {
		this.closeTab();
		this.clean();
		List<TreeItem<MyEditorSheet>> scriptList = ComponentGetter.scriptTreeView.getRoot().getChildren();
		scriptList =  scriptList.stream().filter(item-> item.getValue().equals(this)).collect(Collectors.toList());
		if(!scriptList.isEmpty()){
			for(TreeItem<MyEditorSheet> item : scriptList ){
				if(ComponentGetter.scriptTreeView.getRoot().getChildren().contains(item)){
					ComponentGetter.scriptTreeView.getRoot().getChildren().remove(item);
				}

			}
			ComponentGetter.scriptTreeView.refresh();
		}
	}
	// 同步到数据库, 然后销毁tab
	public void syncAndDestroyTab() {
		this.getDocumentPo().setOpenStatus(0);
		this.syncScriptPo();
		this.destroyTab();
	}
	// 保存到文件, 同步到数据库, 然后销毁Tab
	public void saveDiskAndDestroyTab() {
		MyEditorSheetHelper.saveSqlToFileAction(this);
		this.getDocumentPo().setOpenStatus(0);
		this.syncScriptPo();
		this.destroyTab();
	}

	// 右键菜单
	public ContextMenu createTabContextMenu() {
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

		//
		MenuItem rename = new MenuItem("Rename");
		rename.setDisable(true);
		rename.setOnAction(e -> {
			Consumer<String> caller = x -> {
				if (StrUtils.isNullOrEmpty(x.trim()))
					return;
				this.setTitle(x);
				this.documentPo.setTitle(x);
			};
			DialogTools.showExecWindow("New Name", this.documentPo.getTitle().get(), caller);
		});

		contextMenu.getItems().addAll(closeAll, closeOther, closeRight, closeLeft, new SeparatorMenuItem(), rename);
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


			String fileFullName =  this.getDocumentPo().getExistFileFullName();
			if (StrUtils.isNullOrEmpty(fileFullName)){
				rename.setDisable(false);
			}else {
				rename.setDisable(true);
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
		this.documentPo.setTitle(val);
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

	public File getFile() {
		if (documentPo == null)
			return null;
		return documentPo.getFile();
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

	public Label getScriptTreeLabel() {
		return scriptTreeLabel;
	}

	public void setScriptTreeLabel(Label scriptTreeLabel) {
		this.scriptTreeLabel = scriptTreeLabel;
	}

	public TreeItem<MyEditorSheet> getTreeItem() {
		return treeItem;
	}

	public void setTreeItem(TreeItem<MyEditorSheet> treeItem) {
		this.treeItem = treeItem;
	}
}
