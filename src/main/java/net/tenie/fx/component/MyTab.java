package net.tenie.fx.component;

import java.sql.Connection;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.PropertyPo.ScriptPo;
import net.tenie.fx.component.container.ScriptTabTree;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.MainTabInfo;
import net.tenie.fx.config.MainTabs;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.SqlTextDao;

public class MyTab extends Tab {
	private ScriptPo scriptPo;
	
	public MyTab() {
		super();
	}

	public MyTab(String TabName) {
		super();
//		scriptPo = new ScriptPo();
//		scriptPo.setTitle(TabName);  
		
//		scriptPo 保存
//		Connection conn , String title, String txt, String filename, String encode, int paragraph) {
		
		scriptPo = SqlTextDao.scriptArchive(TabName, ""	, "", "UTF-8", 0);
		createMyTab();
	}
	
	public MyTab(ScriptPo po) {
		super();
		scriptPo = po; 
		createMyTab();
	}
	
 
	
	private void createMyTab() {
		String TabName = scriptPo.getTitle();
		TabPane myTabPane = SqlEditor.myTabPane;
		// 	名称
		CommonUtility.setTabName(this, TabName);
		// 添加到缓存
		MainTabs.add(this);

		StackPane pane = SqlEditor.SqlCodeArea();
		VBox vbox = new VBox();
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
		SqlEditor.setTabSQLText(this, scriptPo.getText(), scriptPo.getParagraph());
	}
	
	
	public String getTabSqlText() {
		String sql = SqlEditor.getTabSQLText(this);
		return sql;
	}
	public String getTabTitle() {
		String title = CommonUtility.tabText(this);
		return title;
	}
	
	public void syncScriptPo(Connection conn) {
		String sql = getTabSqlText();
		String title =   getTabTitle();
		
		scriptPo.setText(sql);
		scriptPo.setTitle(title); 
		SqlTextDao.updateScriptArchive(conn , scriptPo); 
		ScriptTabTree.ScriptTreeView.refresh();
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
				try {
					syncScriptPo(H2Db.getConn());
				} finally {
					H2Db.closeConn();
				}
				
				
				
//				 // 如果只有一个窗口就不能关闭 
//				if (myTabPane.getTabs().size() == 1) {
//  					e.consume();
//				}
				
			}
		};
	}

	

	public ScriptPo getScriptPo() {
		return scriptPo;
	}

	public void setScriptPo(ScriptPo scriptPo) {
		this.scriptPo = scriptPo;
	}


}
