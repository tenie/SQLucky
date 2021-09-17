package net.tenie.fx.component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.fxmisc.richtext.CodeArea;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.component.CodeArea.HighLightingCodeArea;
import net.tenie.fx.component.CodeArea.HighLightingSqlCodeAreaContextMenu;
import net.tenie.fx.component.CodeArea.MyAutoComplete;
import net.tenie.fx.component.container.ScriptTabTree;
import net.tenie.Sqlucky.sdk.SqluckyCodeAreaHolder;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.MainTabInfo;
import net.tenie.fx.config.MainTabs;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.SqlTextDao;

public class MyTab extends Tab implements SqluckyTab {
	private DocumentPo scriptPo;
	private HighLightingCodeArea sqlCodeArea;
	private VBox vbox;
	
	public CodeArea getCodeArea() {
		return sqlCodeArea.getCodeArea();
	}
	
	public MyTab() {
		super();
	}

	public MyTab(String TabName) {
		super();
		scriptPo = SqlTextDao.scriptArchive(TabName, ""	, "", "UTF-8", 0);
		createMyTab();
	}
	
	public MyTab(DocumentPo po) {
		super();
		if(po.getId() == null ) { 
			scriptPo = SqlTextDao.scriptArchive(po.getTitle(), po.getText()	, po.getFileName(),
					po.getEncode(), po.getParagraph());
		}else {
			scriptPo = po;
		}
		createMyTab();
	}
	
 
	
	private void createMyTab() {
		String TabName = scriptPo.getTitle();
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
		setTabSQLText( scriptPo.getText());
		
		// 右键菜单
		this.setContextMenu(MyTabMenu());
	}
	// 设置tab 中的 area 中的文本
	public   void setTabSQLText(String text) {
		var code = sqlCodeArea.getCodeArea();
		code.appendText(text);
		sqlCodeArea.highLighting();
		syncScriptPo();
	}
	
	public String getTabSqlText() {
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
		String sql = getTabSqlText();
		String title = getTabTitle();
		
		scriptPo.setText(sql);
		scriptPo.setTitle(title); 
		SqlTextDao.updateScriptArchive(conn , scriptPo); 
		ScriptTabTree.ScriptTreeView.refresh();
	}
	
	public void syncScriptPo() {
		try {
			syncScriptPo(H2Db.getConn());
		} finally {
			H2Db.closeConn();
		}
	 
	}
		
	
 
	
	public void saveScriptPo(Connection conn) {
		String sql = getTabSqlText();
		String title =   getTabTitle();
		
		scriptPo.setText(sql);
		scriptPo.setTitle(title); 
		SqlTextDao.updateScriptArchive(conn, scriptPo); 
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

	public DocumentPo getDocumentPo() {
		return scriptPo;
	}

	public void setScriptPo(DocumentPo scriptPo) {
		this.scriptPo = scriptPo;
	}

	public SqluckyCodeAreaHolder getSqlCodeArea() {
		return sqlCodeArea;
	}

	public void setSqlCodeArea(HighLightingCodeArea sqlCodeArea) {
		this.sqlCodeArea = sqlCodeArea;
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
	
	// 添加空文本的codeTab
	public  void mainTabPaneAddMyTab() { 
		var myTabPane = ComponentGetter.mainTabPane;
		if( myTabPane.getTabs().contains(this) == false ) {
			myTabPane.getTabs().add( this);// 在指定位置添加Tab 
		} 
		myTabPane.getSelectionModel().select(this);
	}

	public VBox getVbox() {
		return vbox;
	}

	@Override
	public String getTitle() {
		return CommonUtility.tabText(this);
	}

 
	// 设置tab 中的 area 中的文本
//	public static void setTabSQLText(MyTab tb, String text) {
//		CodeArea code = getCodeArea(tb);
//		code.appendText(text);
//		tb.getSqlCodeArea().highLighting();
//		tb.syncScriptPo();
//	}
	
}
