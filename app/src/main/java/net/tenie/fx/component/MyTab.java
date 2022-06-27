package net.tenie.fx.component;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.fxmisc.richtext.CodeArea;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyCodeAreaHolder;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.FindReplaceTextPanel;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.component.CodeArea.HighLightingCodeArea;
import net.tenie.fx.component.CodeArea.HighLightingSqlCodeAreaContextMenu;
import net.tenie.fx.component.CodeArea.MyAutoComplete;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.MainTabInfo;
import net.tenie.fx.config.MainTabs;
import net.tenie.lib.db.h2.AppDao;
/**
 * 
 * @author tenie
 *
 */
public class MyTab extends Tab implements SqluckyTab {
	private DocumentPo docPo;
	private HighLightingCodeArea sqlCodeArea;
	// 放查找面板, 文本area 的容器
	private VBox vbox;
	// 查找面板
	private FindReplaceTextPanel findReplacePanel;
	private Boolean savePo = true;
	
	private boolean isModify = false;
	
	public CodeArea getCodeArea() {
		return sqlCodeArea.getCodeArea();
	}
	
	public MyTab() {
		super();
	}
	public MyTab(boolean save) {
		super();
		this.savePo = save;
	}

	public MyTab(String TabName) {
		super();
		docPo = AppDao.scriptArchive(TabName, ""	, "", "UTF-8", 0);
		createMyTab();
	}
	
	public MyTab(DocumentPo po) {
		super();
		if(po.getId() == null ) { 
			docPo = AppDao.scriptArchive(po.getTitle(), po.getText()	, po.getFileFullName(),
					po.getEncode(), po.getParagraph());
		}else {
			docPo = po;
		}
		createMyTab();
	}
	
	public MyTab(DocumentPo po, boolean save) {
		super();
		this.savePo = save;
		if(save) {
			if(po.getId() == null ) { 
				docPo = AppDao.scriptArchive(po.getTitle(), po.getText()	, po.getFileFullName(),
						po.getEncode(), po.getParagraph());
			}else {
				docPo = po;
			}
			createMyTab();
		}else {
			docPo = po;
			createMyTab();
		}
	}
	
 
	
	private void createMyTab() {
		String TabName = docPo.getTitle();
		var myTabPane = ComponentGetter.mainTabPane;
		// 	名称
		CommonUtility.setTabName(this, TabName);
		// 添加到缓存
		MainTabs.add(this);
		MyAutoComplete myAuto = new MyAutoComplete();
		sqlCodeArea = new HighLightingCodeArea(myAuto);
//		右键菜单
		HighLightingSqlCodeAreaContextMenu cm = new  HighLightingSqlCodeAreaContextMenu(sqlCodeArea);  
		sqlCodeArea.setContextMenu(cm);
		
		StackPane pane = sqlCodeArea.getCodeAreaPane();
		vbox = new VBox();
		vbox.getChildren().add(pane);
		VBox.setVgrow(pane, Priority.ALWAYS);
		this.setContent(vbox);

		// 关闭前事件
		this.setOnCloseRequest(tabCloseReq(myTabPane));
		// 选中事件
		this.setOnSelectionChanged(value -> {
			MainTabInfo ti = MainTabs.get(this);
			if (ti != null) {
				DBConns.changeChoiceBox(ti.getTabConnIdx());
			}

		}); 
		
		// 设置sql 文本
		initTabSQLText( docPo.getText());
		
		// 右键菜单
		this.setContextMenu(MyTabMenu());
	}
	
	// 设置tab 中的 area 中的文本
	public   void initTabSQLText(String text) {
		var code = sqlCodeArea.getCodeArea();
		code.appendText(text);
		sqlCodeArea.highLighting(); 
	}
	
	// 设置tab 中的 area 中的文本, 并保存到数据库 
	public   void setTabSQLText(String text) {
		var code = sqlCodeArea.getCodeArea();
		code.appendText(text);
		sqlCodeArea.highLighting();
//		syncScriptPo();
	}
	public   void setTabStringText(String text) {
		var code = sqlCodeArea.getCodeArea();
		code.appendText(text);
	}
	
	
	@Override
	public String getAreaText() {
//		sqlCodeArea.getCodeArea().get
		CodeArea code = sqlCodeArea.getCodeArea();
		String sqlText = code.getText();
//		String sql = SqlEditor.getTabSQLText(this);
		return sqlText;
	}
	public String getTabTitle() {
		String title = CommonUtility.tabText(this);
		return title;
	}
	
	public void syncScriptPo(Connection conn) {
		String sql = getAreaText();
		String title = getTabTitle();
		
		docPo.setText(sql);
		docPo.setTitle(title); 
		if(savePo) { 
			AppDao.updateScriptArchive(conn , docPo); 
		}
		ScriptTabTree.ScriptTreeView.refresh();
	}
	
	public void syncScriptPo() {
		var conn = SqluckyAppDB.getConn();
		try {
			syncScriptPo(conn);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	 
	}
		
	
 
	
	public void saveScriptPo(Connection conn) {
		String sql = getAreaText();
		String title =   getTabTitle();
		
		docPo.setText(sql);
		docPo.setTitle(title); 
		AppDao.updateScriptArchive(conn, docPo); 
	}
	
	
	/**
	 * tab 关闭时：阻止关闭最后一个
	 */
	public  EventHandler<Event> tabCloseReq(TabPane myTabPane) {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				syncScriptPo();   
//				 // 如果只有一个窗口就不能关闭 
//				if (myTabPane.getTabs().size() == 1) {
//  					e.consume();
//				}
				
			}
		};
	}
	
	// 删除 TabPane中的所有 MyTab
	private void closeAll() {
		CommonAction.archiveAllScript();
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
			if( (idx+1) < tsize ) {
				for(int i = idx+1 ; i < tsize; i++) {
					var t = tabs.get(i);
					MyTab mt = (MyTab) t;
					mt.syncScriptPo();
				}
				
				tabs.remove(idx + 1, tsize);
				
			}
			
			

		});
		
		MenuItem closeLeft = new MenuItem("Close Tabs To The Left");
		closeLeft.setOnAction(e -> { 
			var myTabPane = ComponentGetter.mainTabPane;
			var tabs = myTabPane.getTabs();
			int idx = tabs.indexOf(this); 
			if(  idx  > 0 ) {
				for(int i = 0  ; i < idx; i++) {
					var t = tabs.get(i);
					MyTab mt = (MyTab) t;
					mt.syncScriptPo();
				}
				
				tabs.remove(0, idx);
				
			}

		});

		contextMenu.getItems().addAll(closeAll, closeOther, closeRight, closeLeft );
		contextMenu.setOnShowing(e->{
			var myTabPane = ComponentGetter.mainTabPane;
			int idx = myTabPane.getTabs().indexOf(this);
			int size = myTabPane.getTabs().size();
			if(idx == 0) {
				closeLeft.setDisable(true);
			}else {
				closeLeft.setDisable(false);
			}
			
			if(idx == (size -1 ) ) {
				closeRight.setDisable(true);
			}else {
				closeRight.setDisable(false);
			}
			
			
			if(size == 1) {
				closeOther.setDisable(true);
			}else {
				closeOther.setDisable(false);
			}
			
			
		});
		return contextMenu;
	}

	// 添加空文本的codeTab
	public static MyTab addCodeEmptyTabMethod() {
		var myTabPane = ComponentGetter.mainTabPane;
		int size = myTabPane.getTabs().size();
		if (ConfigVal.pageSize < 0) {
			ConfigVal.pageSize = size;
		}
		ConfigVal.pageSize++;
		String labe = "Untitled_" + ConfigVal.pageSize + "*";
		MyTab nwTab = new MyTab(labe); 
		myTabPane.getTabs().add(size, nwTab);// 在指定位置添加Tab
		myTabPane.getSelectionModel().select(size);
		ScriptTabTree.treeRootAddItem(nwTab);
		return nwTab;
	}

	// 添加空文本的codeTab
	public static MyTab addMyTabByScriptPo(DocumentPo scpo) {
		var myTabPane = ComponentGetter.mainTabPane;
		int size = myTabPane.getTabs().size(); 
		ConfigVal.pageSize++; 
		MyTab nwTab = new MyTab(scpo); 
		myTabPane.getTabs().add(size, nwTab);// 在指定位置添加Tab
		myTabPane.getSelectionModel().select(size);
		ScriptTabTree.treeRootAddItem(nwTab);
		return nwTab;
	}
	public static void createTabFromSqlFile(DocumentPo scpo) {
		addMyTabByScriptPo(scpo);
	}
	
	//TODO 添加空文本的codeTab
	public  void mainTabPaneAddSqlTab() { 
		Platform.runLater(()->{
			var myTabPane = ComponentGetter.mainTabPane;
			if( myTabPane.getTabs().contains(this) == false ) {
				var code = sqlCodeArea.getCodeArea();
				if( StrUtils.isNullOrEmpty(code.getText().trim() ) ) {
					setTabSQLText( docPo.getText());
				}
				myTabPane.getTabs().add( this);// 在指定位置添加Tab 
			} 
			myTabPane.getSelectionModel().select(this);
		});
		
	}
	public  void mainTabPaneAddTextTab() { 
		Platform.runLater(()->{
			var myTabPane = ComponentGetter.mainTabPane;
			if( myTabPane.getTabs().contains(this) == false ) {
				var code = sqlCodeArea.getCodeArea();
				if( StrUtils.isNullOrEmpty(code.getText().trim() ) ) {
					setTabStringText( docPo.getText());
				}
				myTabPane.getTabs().add( this);// 在指定位置添加Tab 
			} 
			myTabPane.getSelectionModel().select(this);
		});
		
	}
	// 主界面上存在否
	@Override
	public boolean existTab() {
		var myTabPane = ComponentGetter.mainTabPane;
		return myTabPane.getTabs().contains(this);
	}
	
	// 存在 就显示出来
	@Override
	public boolean existTabShow() {
		
		var myTabPane = ComponentGetter.mainTabPane;
		if(myTabPane.getTabs().contains(this)) {
			myTabPane.getSelectionModel().select(this);
			return true;
		}
		return false;
	}
	
	//TODO 添加空文本的codeTab
	public static  void mainTabPaneAddAllMyTabs(List<MyTab> ls) { 
		if(ls != null && ls.size() > 0) {
			var myTabPane = ComponentGetter.mainTabPane;
			for(var mtb : ls) {
				var code = mtb.getSqlCodeArea().getCodeArea();
				if( StrUtils.isNullOrEmpty(code.getText().trim() ) ) {
					mtb.setTabSQLText( mtb.getDocumentPo().getText());
				}
			}
			
			myTabPane.getTabs().addAll(ls);
		}
		
//		if( myTabPane.getTabs().contains(this) == false ) {
//			var code = sqlCodeArea.getCodeArea();
//			if( StrUtils.isNullOrEmpty(code.getText().trim() ) ) {
//				setTabSQLText( docPo.getText());
//			}
//			myTabPane.getTabs().add( this);// 在指定位置添加Tab 
//		} 
//		myTabPane.getSelectionModel().select(this);
	}

	public VBox getVbox() {
		return vbox;
	}

	public DocumentPo getDocumentPo() {
		return docPo;
	}

	public void setScriptPo(DocumentPo scriptPo) {
		this.docPo = scriptPo;
	}

	public SqluckyCodeAreaHolder getSqlCodeArea() {
		return sqlCodeArea;
	}

	public void setSqlCodeArea(HighLightingCodeArea sqlCodeArea) {
		this.sqlCodeArea = sqlCodeArea;
	}

	
	@Override
	public String getTitle() {
		return CommonUtility.tabText(this);
	}
	
	public void setFile(File file) {
		docPo.setFile(file);
	}
	
	public File getFile() {
		if(docPo == null ) return null;
		return docPo.getFile();
	}
	
	public Region getIcon() {
		if(docPo == null ) return null;
		return docPo.getIcon();
	}

	public void setIcon(Region icon) {
		docPo.setIcon(icon);
	}
	
	public String getFileText() {
		if(docPo == null ) return "";
		return docPo.getText();
	}
	public void setFileText(String text) { 
		docPo.setText(text);
	}

	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean isModify) {
		this.isModify = isModify;
	}
 
	public void saveTextAction() {
		CommonAction.saveSqlAction(this);
	}

	
	
	 
	// 清空查找面板
	@Override
	public void cleanFindReplacePanel() {
		findReplacePanel = null;
		
	}
	@Override
	public FindReplaceTextPanel getFindReplacePanel() {
		return findReplacePanel;
	}
	// 保存查找面板
	@Override
	public void setFindReplacePanel(FindReplaceTextPanel findReplacePanel) {
		this.findReplacePanel = findReplacePanel;
	}

	@Override
	public boolean isShowing() {
		var myTabPane = ComponentGetter.mainTabPane;
		if(myTabPane.getTabs().contains(this)) {
			int idxThis = myTabPane.getTabs().indexOf(this);
			int  currentSelect = myTabPane.getSelectionModel().getSelectedIndex();
			if(idxThis == currentSelect) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	// 设置tab 中的 area 中的文本
//	public static void setTabSQLText(MyTab tb, String text) {
//		CodeArea code = getCodeArea(tb);
//		code.appendText(text);
//		tb.getSqlCodeArea().highLighting();
//		tb.syncScriptPo();
//	}
	
}
