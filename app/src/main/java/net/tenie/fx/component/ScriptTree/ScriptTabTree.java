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
import net.tenie.lib.db.h2.H2SqlTextSavePo;


/**
 * 
 * @author tenie
 *
 */
public class ScriptTabTree {

	private static Logger logger = LogManager.getLogger(ScriptTabTree.class);
	
	public static TreeView<MyAreaTab> ScriptTreeView; 
	List<ConnItemContainer> connItemParent = new ArrayList<>(); 
	private  ScriptTreeContextMenu  menu;
	
	public ScriptTabTree() {
		 createScriptTreeView();
	}

	// db节点view
	public TreeView<MyAreaTab> createScriptTreeView() {
		var rootNode = new TreeItem<>(new MyAreaTab());
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
		 

		recoverScriptNode(rootNode);
		return ScriptTreeView;
	}
	
	
	// 恢复数据中保存的连接数据
	public static void recoverScriptNode(TreeItem<MyAreaTab> rootNode) {
		List<DocumentPo> datas ;
		List<H2SqlTextSavePo> ls;
		String SELECT_PANE ;
		MyAreaTab sysOpenFileTB = null;
		Connection H2conn = SqluckyAppDB.getConn();
		try {
			ls = AppDao.read(H2conn);
			SELECT_PANE = AppDao.readConfig(H2conn, "SELECT_PANE");
			datas = AppDao.readScriptPo(H2conn);
		} finally {
			SqluckyAppDB.closeConn(H2conn);
		}
		List<Integer> ids = new ArrayList<>();
		for (H2SqlTextSavePo sqlpo : ls) {
			ids.add(sqlpo.getScriptId());
		}
		
		List<TreeItem<MyAreaTab>> itemList = new ArrayList<>();
		List<MyAreaTab> mtbs = new ArrayList<>();
		if (datas != null && datas.size() > 0) {
			ConfigVal.pageSize = datas.size();  
			for (DocumentPo po : datas) {
				MyAreaTab tb = new MyAreaTab(po, true);
				
				TreeItem<MyAreaTab> item = new TreeItem<>(tb);
//				rootNode.getChildren().add(item);
				itemList.add(item);
				// 恢复代码编辑框
				if (ids.contains(po.getId())) {
					mtbs.add(tb);
					// 再操作系统中通过鼠标双击打开的文件, 如果再在以前打开过就直接选中
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
		// 页面显示后 执行下吗
		Consumer< String > cr = v->{		
				if(itemList.size() > 0 ) {
				Platform.runLater(() -> {
					rootNode.getChildren().addAll(itemList);
					// 恢复代码编辑框
					if (mtbs.size() > 0) {
						MyAreaTab.mainTabPaneAddAllMyTabs(mtbs);
						
						// 系统打开文件触发启动APP时, 恢复历史中的文件
						if (tmpSysOpenFileTB != null) {
							logger.info("系统打开文件触发启动APP时, 恢复历史中的文件 " );
							ComponentGetter.mainTabPane.getSelectionModel().select(tmpSysOpenFileTB);
						}else if(StrUtils.isNotNullOrEmpty(SQLucky.sysOpenFile) ) { // 系统打开文件触发启动APP时, 新开一个 脚本文件
							logger.info("系统打开文件触发启动APP时, 新开一个 脚本文件 " );
							File sif = new File(SQLucky.sysOpenFile);
							CommonAction.openSqlFile(sif);
						}
						else if (StrUtils.isNotNullOrEmpty(SELECT_PANE)) {// 恢复选中上次选中页面
							logger.info(" 恢复选中上次选中页面" );
							int sps = Integer.valueOf(SELECT_PANE);
							if (mtbs.size() > sps) {
								ComponentGetter.mainTabPane.getSelectionModel().select(sps);
							}
						}
					}
					// 没有tab被添加, 添加一新的
					if (mtbs.size() == 0) {
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

	// 所有连接节点
	public static ObservableList<TreeItem<MyAreaTab>> allTreeItem() {
		ObservableList<TreeItem<MyAreaTab>> val = ScriptTreeView.getRoot().getChildren();
		return val;
	}

	   

	// 获取当前选中的节点
	public static TreeItem<MyAreaTab> getScriptViewCurrentItem() {
		TreeItem<MyAreaTab> ctt = ScriptTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	 

	// 给root节点加元素 
	public static void treeRootAddItem(TreeItem<MyAreaTab> item) { 
		TreeItem<MyAreaTab> rootNode = ScriptTreeView.getRoot();
		rootNode.getChildren().add(item);		
	}
	// 给root节点加元素 
		public static void treeRootAddItem(MyAreaTab  mytab) {
			TreeItem<MyAreaTab> item = new TreeItem<MyAreaTab> (mytab); 
			treeRootAddItem(item);
		}

	// tree view 双击事件
	public void treeViewDoubleClick(MouseEvent mouseEvent) { 
		if (mouseEvent.getClickCount() == 2) {
			openMyTab();
		}
	}
	
	public static void openMyTab() {
		TreeItem<MyAreaTab> item = ScriptTreeView.getSelectionModel().getSelectedItem();
		var mytab = item.getValue(); 
		if(mytab != null && mytab.getDocumentPo() != null) {
			mytab.showMyTab();
		}
	}
	 
	public static  List<DocumentPo>  allScriptPo() {
		 ObservableList<TreeItem<MyAreaTab>> ls = allTreeItem();
		 List<DocumentPo> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb.getDocumentPo());
		 }
		 
		 return list;
	}
	public static  List<MyAreaTab>  allMyTab() {
		 ObservableList<TreeItem<MyAreaTab>> ls = allTreeItem();
		 List<MyAreaTab> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb);
		 } 
		 return list;
	}

	
	public static MyAreaTab findMyTabByScriptPo(DocumentPo scpo) {
		 ObservableList<TreeItem<MyAreaTab>> ls = allTreeItem();
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
	public static void closeAction(TreeItem<MyAreaTab> rootNode) {

		ObservableList<TreeItem<MyAreaTab>>  myTabItemList = rootNode.getChildren();
		TreeItem<MyAreaTab> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
		MyAreaTab tb = ctt.getValue();
		
		String title = CommonUtility.tabText(tb);
		String sql = tb.getAreaText(); // SqlEditor.getTabSQLText(tb);
		if (title.endsWith("*") && sql != null && sql.trim().length() > 0) {
			// 是否保存
			final Stage stage = new Stage();

			// 1 保存
			JFXButton okbtn = new JFXButton("Yes");
			okbtn.getStyleClass().add("myAlertBtn");
			okbtn.setOnAction(value -> {
				CommonAction.saveSqlAction(tb);
				removeNode(myTabItemList, ctt, tb);
				stage.close();
			});

			// 2 不保存
			JFXButton Nobtn = new JFXButton("No");
			Nobtn.setOnAction(value -> {
				removeNode(myTabItemList, ctt, tb);
				stage.close();
			});
			// 取消
			JFXButton cancelbtn = new JFXButton("Cancel"); 
			cancelbtn.setOnAction(value -> { 
				stage.close();
			});

			List<Node> btns = new ArrayList<>();

			
			
			btns.add(cancelbtn);
			btns.add(Nobtn);
			btns.add(okbtn);

			MyAlert.myConfirmation("Save " + StrUtils.trimRightChar(title, "*") + "?", stage, btns);
		}else {
			removeNode(myTabItemList, ctt, tb);
		}
		

	}
	
	// 从ScriptTabTree 中移除一个节点
	public static void removeNode(ObservableList<TreeItem<MyAreaTab>>  myTabItemList, TreeItem<MyAreaTab> ctt, MyAreaTab tb ) {
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
