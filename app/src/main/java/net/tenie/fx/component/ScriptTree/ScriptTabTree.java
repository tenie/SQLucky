package net.tenie.fx.component.ScriptTree;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.SqlcukyTitledPaneInfoPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.component.MyAreaTab;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemContainer;
import net.tenie.fx.main.SQLucky;
import net.tenie.lib.db.h2.AppDao;


/**
 * 
 * @author tenie
 *
 */
public class ScriptTabTree {

	private static Logger logger = LogManager.getLogger(ScriptTabTree.class);
	
	public static TreeView<SqluckyTab> ScriptTreeView; 
	public static TreeItem<SqluckyTab> rootNode; 
	List<ConnItemContainer> connItemParent = new ArrayList<>(); 
	private  ScriptTreeContextMenu  menu;
	
	public ScriptTabTree() {
		 createScriptTreeView();
	}

	// db节点view
	public TreeView<SqluckyTab> createScriptTreeView() {
		rootNode = new TreeItem<>(new MyAreaTab());
		ComponentGetter.scriptTreeRoot = rootNode;
		ScriptTreeView = new TreeView<>(rootNode);
		ScriptTreeView.getStyleClass().add("my-tag");
		ScriptTreeView.setShowRoot(false); 
		// 展示连接
		if (rootNode.getChildren().size() > 0)
			ScriptTreeView.getSelectionModel().select(rootNode.getChildren().get(0)); // 选中节点
		// 双击
		ScriptTreeView.setOnMouseClicked(e -> {
			treeViewDoubleClick(e);
		});
		// 右键菜单
		menu = new ScriptTreeContextMenu(rootNode );
		ContextMenu	contextMenu = menu.getContextMenu(); 
		ScriptTreeView.setContextMenu(contextMenu);
		// 选中监听事件
//		treeView.getSelectionModel().selectedItemProperty().addListener(treeViewContextMenu(treeView));
		ScriptTreeView.getSelectionModel().select(rootNode);

//		ScriptTreeView = treeView;

		// 显示设置
		ScriptTreeView.setCellFactory(new ScriptTabNodeCellFactory());
		 
		// 恢复
		recoverScriptNode();
		return ScriptTreeView;
	}
	
	
	// 恢复数据中保存的连接数据
	public static void recoverScriptNode() {
		List<DocumentPo> scriptDatas ;
		// 上次的激活页面
		MyAreaTab activateMyTab = null;
		// 从系统中打开 .sql文件时, 大概这个sql的编辑页面
		MyAreaTab sysOpenFileTB = null;  
		Connection H2conn = SqluckyAppDB.getConn();
		try {
			// 读取 上次左侧script tree中的所有数据
			scriptDatas = AppDao.readScriptPo(H2conn);
		} finally {
			SqluckyAppDB.closeConn(H2conn);
		}
		//
		List<TreeItem<SqluckyTab>> treeItems = new ArrayList<>();
		List<MyAreaTab> myAreaTabs = new ArrayList<>();
		// 将DocumentPo 对象转换位tree的节点对象
		if (scriptDatas != null && scriptDatas.size() > 0) {
			ConfigVal.pageSize = scriptDatas.size();  
			for (DocumentPo po : scriptDatas) {
				MyAreaTab tb = new MyAreaTab(po, true); 
				TreeItem<SqluckyTab> item = new TreeItem<>(tb);
				treeItems.add(item);
				// 将需要恢复代码编辑框, 缓存到集合中
				if(po.getOpenStatus() !=null && po.getOpenStatus() == 1) {
					myAreaTabs.add(tb);
					// 设置上次激活的编辑页面
					if(po.getIsActivate() == 1) {
						activateMyTab = tb;
					}
					
					// 在操作系统中通过鼠标双击打开的文件, 如果再在以前打开过就直接选中
					if(StrUtils.isNotNullOrEmpty(SQLucky.sysOpenFile) ) {
						var filePath = po.getFileFullName();
						if(StrUtils.isNotNullOrEmpty(filePath) ) {
							if(SQLucky.sysOpenFile.equals(filePath)) {
								sysOpenFileTB = tb;
								logger.info("**** filePath = " + filePath);
							}
						}
					}
					
				}
			}
		}
		
		MyAreaTab tmpSysOpenFileTB = sysOpenFileTB;
		MyAreaTab activateTmpMyTab = activateMyTab;
		// 页面显示后 执行下吗
		Consumer< String > cr = v->{		
				if(treeItems.size() > 0 ) {
				Platform.runLater(() -> {
					rootNode.getChildren().addAll(treeItems);
					// 恢复代码编辑框
					if (myAreaTabs.size() > 0) {
						MyAreaTab.mainTabPaneAddAllMyTabs(myAreaTabs);
						
						// 系统打开文件触发启动APP时, 恢复历史中的文件
						if (tmpSysOpenFileTB != null) {
							logger.info("系统打开文件触发启动APP时, 恢复历史中的文件 " );
							ComponentGetter.mainTabPane.getSelectionModel().select(tmpSysOpenFileTB);
						}else if(StrUtils.isNotNullOrEmpty(SQLucky.sysOpenFile) ) { // 系统打开文件触发启动APP时, 新开一个 脚本文件
							logger.info("系统打开文件触发启动APP时, 新开一个 脚本文件 " );
							File sif = new File(SQLucky.sysOpenFile);
							CommonAction.openSqlFile(sif);
						}
						else if (activateTmpMyTab != null) {// 恢复选中上次选中页面
							logger.info(" 恢复选中上次选中页面" );
							ComponentGetter.mainTabPane.getSelectionModel().select(activateTmpMyTab);
						}
					}
					// 没有tab被添加, 添加一新的
					if (myAreaTabs.size() == 0) {
						MyAreaTab.addCodeEmptyTabMethod();
					}
				});
				}else {
					Platform.runLater(()->{
						MyAreaTab.addCodeEmptyTabMethod();
					});
				}
			 
		};
		CommonUtility.addInitTask(cr);
	}

	// 使用外部数据还原script tree节点, 清空节点和 清空tabpane打开的编辑tab
	public static void cleanOldAndRecover(List<DocumentPo> scriptDatas) {
		rootNode.getChildren().clear();
		var myTabPane = ComponentGetter.mainTabPane;
		myTabPane.getTabs().clear();
		
		MyAreaTab activateMyTab = null;
		List<TreeItem<SqluckyTab>> treeItems = new ArrayList<>();
		List<MyAreaTab> myAreaTabs = new ArrayList<>();
		// 将DocumentPo 对象转换位tree的节点对象
		if (scriptDatas != null && scriptDatas.size() > 0) {
			ConfigVal.pageSize = scriptDatas.size();  
			for (DocumentPo po : scriptDatas) {
				// 使用外部数据, 还原将数据保存到数据库, 
				//只要确保DocumentPo id为null, new MyAreaTab时会保存到数据库 
				po.setId(null);
				MyAreaTab tb = new MyAreaTab(po, true); 
				TreeItem<SqluckyTab> item = new TreeItem<>(tb);
				treeItems.add(item);
				// 将需要恢复代码编辑框, 缓存到集合中
				if(po.getOpenStatus() !=null && po.getOpenStatus() == 1) {
					myAreaTabs.add(tb);
					// 设置上次激活的编辑页面
					if(po.getIsActivate() == 1) {
						activateMyTab = tb;
					}
				}
				
			}
		}
		
		MyAreaTab activateTmpMyTab = activateMyTab;
		// 页面显示后 执行下吗
		if (treeItems.size() > 0) {
			Platform.runLater(() -> {
				rootNode.getChildren().addAll(treeItems);
				// 恢复代码编辑框
				if (myAreaTabs.size() > 0) {
					 MyAreaTab.mainTabPaneAddAllMyTabs(myAreaTabs); 
					 if (activateTmpMyTab != null ) {// 恢复选中上次选中页面
						logger.info(" 恢复选中上次选中页面");
						ComponentGetter.mainTabPane.getSelectionModel().select(activateTmpMyTab);
					}
				}
				// 没有tab被添加, 添加一新的
				if (myAreaTabs.size() == 0) {
					MyAreaTab.addCodeEmptyTabMethod();
				}
			});
		}else {
			Platform.runLater(()->{
				MyAreaTab.addCodeEmptyTabMethod();
			});
		}
			 
	}
	
	// 所有连接节点
	public static ObservableList<TreeItem<SqluckyTab>> allTreeItem() {
		ObservableList<TreeItem<SqluckyTab>> val = ScriptTreeView.getRoot().getChildren();
		return val;
	}

	   

	// 获取当前选中的节点
	public static TreeItem<SqluckyTab> getScriptViewCurrentItem() {
		TreeItem<SqluckyTab> ctt = ScriptTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	 

	// 给root节点加元素 
	public static void treeRootAddItem(TreeItem<SqluckyTab> item) { 
		TreeItem<SqluckyTab> rootNode = ScriptTreeView.getRoot();
		rootNode.getChildren().add(item);		
	}
	// 给root节点加元素 
		public static void treeRootAddItem(SqluckyTab  mytab) {
			TreeItem<SqluckyTab> item = new TreeItem<> (mytab); 
			treeRootAddItem(item);
		}

	// tree view 双击事件
	public void treeViewDoubleClick(MouseEvent mouseEvent) { 
		if (mouseEvent.getClickCount() == 2) {
			openMyTab();
		}
	}
	
	public static void openMyTab() {
		TreeItem<SqluckyTab> item = ScriptTreeView.getSelectionModel().getSelectedItem();
		var mytab = item.getValue(); 
		if(mytab != null && mytab.getDocumentPo() != null) {
			mytab.showMyTab();
		}
	}
	 
	public static  List<DocumentPo>  allScriptPo() {
		 ObservableList<TreeItem<SqluckyTab>> ls = allTreeItem();
		 List<DocumentPo> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb.getDocumentPo());
		 }
		 
		 return list;
	}
	public static  List<SqluckyTab>  allMyTab() {
		 ObservableList<TreeItem<SqluckyTab>> ls = allTreeItem();
		 List<SqluckyTab> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb);
		 } 
		 return list;
	}

	
	public static SqluckyTab findMyTabByScriptPo(DocumentPo scpo) {
		 ObservableList<TreeItem<SqluckyTab>> ls = allTreeItem();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 var tmp =  mytb.getDocumentPo();
			 if(tmp.equals(scpo)) {
				 return mytb;
			 }
					 
		 } 
		 return null;
	}
	
	// 关闭一个脚本 Node cell
	public static void closeAction(TreeItem<SqluckyTab> rootNode) {

		ObservableList<TreeItem<SqluckyTab>>  myTabItemList = rootNode.getChildren();
		TreeItem<SqluckyTab> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
		SqluckyTab tb = ctt.getValue();
		
		String title = tb.getTitle();// CommonUtility.tabText(tb);
		String sql = tb.getAreaText(); // SqlEditor.getTabSQLText(tb);
		if (title.endsWith("*") && sql != null && sql.trim().length() > 0) {
			// 是否保存
			final Stage stage = new Stage();

			// 1 保存
			JFXButton okbtn = new JFXButton("Yes(Y)");
			okbtn.getStyleClass().add("myAlertBtn");
			okbtn.setOnAction(value -> {
				CommonAction.saveSqlAction(tb);
				removeNode(myTabItemList, ctt, tb);
				stage.close();
			});

			// 2 不保存
			JFXButton Nobtn = new JFXButton("No(N)");
			Nobtn.setOnAction(value -> {
				removeNode(myTabItemList, ctt, tb);
				stage.close();
			});
			// 取消
			JFXButton cancelbtn = new JFXButton("Cancel(C)"); 
			cancelbtn.setOnAction(value -> { 
				stage.close();
			});

			List<Node> btns = new ArrayList<>();

			
			
			btns.add(cancelbtn);
			btns.add(Nobtn);
			btns.add(okbtn);

			MyAlert.myConfirmation("Save " + StrUtils.trimRightChar(title, "*") + "?", stage, btns, false);
		}else {
			removeNode(myTabItemList, ctt, tb);
		}
		

	}
	
	// 从ScriptTabTree 中移除一个节点
	public static void removeNode(ObservableList<TreeItem<SqluckyTab>>  myTabItemList, TreeItem<SqluckyTab> ctt, SqluckyTab tb ) {
		var conn = SqluckyAppDB.getConn();
		try { 
			var myTabPane = ComponentGetter.mainTabPane;
			if (myTabPane.getTabs().contains(tb)) {
				myTabPane.getTabs().remove(tb);
			}
			myTabItemList.remove(ctt);

			var scpo = tb.getDocumentPo();
			AppDao.deleteScriptArchive(conn, scpo);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
	// TitledPane
	public TitledPane scriptTitledPane() {
 
		ScriptTreeButtonPanel sbtnPanel  = new ScriptTreeButtonPanel();
		
		TitledPane scriptTitledPane = new TitledPane();
		scriptTitledPane.setText("Script");
		scriptTitledPane.setUserData(new SqlcukyTitledPaneInfoPo("Script", sbtnPanel.getOptionHbox()));
		
		
		CommonUtility.addCssClass(scriptTitledPane, "titledPane-color");
		scriptTitledPane.setContent(ScriptTreeView);
		
		
		// 图标切换
		CommonUtility.addInitTask(v->{
			Platform.runLater(()->{
				CommonUtility.setLeftPaneIcon(scriptTitledPane , ComponentGetter.iconScript, ComponentGetter.uaIconScript);
			});
			
		});
		
		return scriptTitledPane;
	}
	
	// treeView 右键菜单属性设置
//		public   ChangeListener<TreeItem<MyTab>> treeViewContextMenu(TreeView<MyTab> treeView) {
//			return new ChangeListener<TreeItem<MyTab>>() {
//				@Override
//				public void changed(ObservableValue<? extends TreeItem<MyTab>> observable,
//						TreeItem<MyTab> oldValue, TreeItem<MyTab> newValue) {
//					
//				 
//					 
//					
//					
////					if(! menu.getRefresh().isDisable()) {
////						TreeItem<TreeNodePo>  connItem = ConnItem(newValue);
////						menu.setRefreshAction(connItem);
////					}
//
//				}
//			};
//		}
 
}
