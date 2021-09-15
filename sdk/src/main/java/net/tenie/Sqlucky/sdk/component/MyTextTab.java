package net.tenie.Sqlucky.sdk.component;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority; 
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
 

public class MyTextTab extends Tab {
	private String tabName; 
	private AppComponent appCom;
	public MyTextTab(AppComponent app, String TabName , Node content) {
		super(); 
		tabName = TabName;
		appCom = app;
		createMyTab(app, content);
	}
	
	private void createMyTab(AppComponent app, Node content ) { 
		TabPane myTabPane = app.mainTabPane();
		// 	名称
		CommonUtility.setTabName(this, tabName);
		// 添加到缓存
//		MainTabs.add(this);

//		StackPane pane = SqlEditor.SqlCodeArea();
		VBox vbox = new VBox();
		vbox.getChildren().add(content);
		VBox.setVgrow(content, Priority.ALWAYS);
		this.setContent(vbox);

		// 关闭前事件
//		this.setOnCloseRequest(tabCloseReq(myTabPane));
//		// 选中事件
//		this.setOnSelectionChanged(value -> {
//			MainTabInfo ti = MainTabs.get(this);
//			if (ti != null) {
//				DBConns.changeChoiceBox(ti.getTabConnIdx());
//			}
//
//		});  
	}
	
	
 
	public String getTabTitle() {
		String title = CommonUtility.tabText(this);
		return title;
	}
	
    


}
