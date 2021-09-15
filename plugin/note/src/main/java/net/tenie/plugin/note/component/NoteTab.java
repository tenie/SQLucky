package net.tenie.plugin.note.component;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.plugin.note.impl.NoteDelegateImpl;

public class NoteTab extends Tab {
	private FileInfoPo FileInfoPo; 

	public NoteTab(String TabName) {
		super(); 
		createMyTab();
	}
	
	public NoteTab(FileInfoPo po) {
		super(); 
		createMyTab();
	}
	
 
	
	private void createMyTab() {
		String TabName = FileInfoPo.getFileName();
		TabPane myTabPane = NoteDelegateImpl.appCom.mainTabPane();
		// 	名称
		CommonUtility.setTabName(this, TabName);
		// 添加到缓存
//		MainTabs.add(this);

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
//		SqlEditor.setTabSQLText(this, FileInfoPo.getText());
		
		// 右键菜单
//		this.setContextMenu(MyTabMenu());
	}
	
	
	 
	public String getTabTitle() {
		String title = CommonUtility.tabText(this);
		return title;
	}
	
 
	 
		
	
 
	
 
	/**
	 * tab 关闭时：阻止关闭最后一个
	 */
	public  EventHandler<Event> tabCloseReq(TabPane myTabPane) {
		return new EventHandler<Event>() {
			public void handle(Event e) {
//				syncFileInfoPo();   
//				 // 如果只有一个窗口就不能关闭 
//				if (myTabPane.getTabs().size() == 1) {
//  					e.consume();
//				}
				
			}
		};
	}
	
	// 删除 TabPane中的所有 MyTab
	private void closeAll() {
//		CommonAction.archiveAllScript();
	}
	
	// 右键菜单
//	public ContextMenu MyTabMenu() {
//		ContextMenu contextMenu = new ContextMenu();
//		MenuItem closeAll = new MenuItem("Close ALl");
//		closeAll.setOnAction(e -> { 
//			closeAll();
//		});
//
//		MenuItem closeOther = new MenuItem("Close Other");
//		closeOther.setOnAction(e -> { 
//			closeAll();
//			SqlEditor.myTabPane.getTabs().add(this);
//
//		});
//		
//		MenuItem closeRight = new MenuItem("Close Tabs To The Right");
//		closeRight.setOnAction(e -> { 
//			var tabs = SqlEditor.myTabPane.getTabs();
//			int idx = tabs.indexOf(this);
//			int tsize = tabs.size();
//			if( (idx+1) < tsize ) {
//				for(int i = idx+1 ; i < tsize; i++) {
//					var t = tabs.get(i);
//					NoteTab mt = (NoteTab) t;
//					mt.syncFileInfoPo();
//				}
//				
//				tabs.remove(idx + 1, tsize);
//				
//			}
//			
//			
//
//		});
//		
//		MenuItem closeLeft = new MenuItem("Close Tabs To The Left");
//		closeLeft.setOnAction(e -> { 
//			var tabs = SqlEditor.myTabPane.getTabs();
//			int idx = tabs.indexOf(this); 
//			if(  idx  > 0 ) {
//				for(int i = 0  ; i < idx; i++) {
//					var t = tabs.get(i);
//					NoteTab mt = (NoteTab) t;
//					mt.syncFileInfoPo();
//				}
//				
//				tabs.remove(0, idx);
//				
//			}
//
//		});
//
//		contextMenu.getItems().addAll(closeAll, closeOther, closeRight, closeLeft );
//		contextMenu.setOnShowing(e->{
//			int idx = SqlEditor.myTabPane.getTabs().indexOf(this);
//			int size = SqlEditor.myTabPane.getTabs().size();
//			if(idx == 0) {
//				closeLeft.setDisable(true);
//			}else {
//				closeLeft.setDisable(false);
//			}
//			
//			if(idx == (size -1 ) ) {
//				closeRight.setDisable(true);
//			}else {
//				closeRight.setDisable(false);
//			}
//			
//			
//			if(size == 1) {
//				closeOther.setDisable(true);
//			}else {
//				closeOther.setDisable(false);
//			}
//			
//			
//		});
//		return contextMenu;
//	}

	public FileInfoPo getFileInfoPo() {
		return FileInfoPo;
	}

	public void setFileInfoPo(FileInfoPo FileInfoPo) {
		this.FileInfoPo = FileInfoPo;
	}


}
