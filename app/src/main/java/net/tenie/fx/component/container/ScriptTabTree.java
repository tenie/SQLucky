package net.tenie.fx.component.container;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.jfoenix.controls.JFXButton;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.fx.component.MyTab;
import net.tenie.Sqlucky.sdk.component.SqlcukyEditor;
import net.tenie.fx.component.TreeItem.ConnItemContainer;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.fx.factory.ScriptTabNodeCellFactory;
import net.tenie.fx.factory.ScriptTreeContextMenu;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.window.ModalDialog;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.H2SqlTextSavePo;
import net.tenie.lib.db.h2.SqlTextDao;


/*   @author tenie */
public class ScriptTabTree {

	public static TreeView<MyTab> ScriptTreeView; 
	List<ConnItemContainer> connItemParent = new ArrayList<>(); 
	private  ScriptTreeContextMenu  menu;
	
	public ScriptTabTree() {
		 createScriptTreeView();
	}

	// db节点view
	public TreeView<MyTab> createScriptTreeView() {
		var rootNode = new TreeItem<>(new MyTab());
		TreeView<MyTab> treeView = new TreeView<>(rootNode);
		treeView.getStyleClass().add("my-tag");
		treeView.setShowRoot(false); 
		// 展示连接
		if (rootNode.getChildren().size() > 0)
			treeView.getSelectionModel().select(rootNode.getChildren().get(0)); // 选中节点
		// 双击
		treeView.setOnMouseClicked(e -> {
			treeViewDoubleClick(e);
		});
		// 右键菜单
		menu = new ScriptTreeContextMenu(rootNode );
		ContextMenu	contextMenu = menu.getContextMenu(); 
		treeView.setContextMenu(contextMenu);
		// 选中监听事件
//		treeView.getSelectionModel().selectedItemProperty().addListener(treeViewContextMenu(treeView));
		treeView.getSelectionModel().select(rootNode);

		ScriptTreeView = treeView;

		// 显示设置
		treeView.setCellFactory(new ScriptTabNodeCellFactory());
		 

		recoverScriptNode(rootNode);
		return treeView;
	}
	
	
	// 恢复数据中保存的连接数据
	public static void recoverScriptNode(TreeItem<MyTab> rootNode) {
		List<DocumentPo> datas ;
		List<H2SqlTextSavePo> ls;
		String SELECT_PANE ;
		try {
			Connection H2conn = H2Db.getConn();
			ls = SqlTextDao.read(H2conn);
			SELECT_PANE = SqlTextDao.readConfig(H2conn, "SELECT_PANE");
			datas = SqlTextDao.readScriptPo(H2conn);
		} finally {
			H2Db.closeConn();
		}
		
		List<Integer> ids = new ArrayList<>();
		for (H2SqlTextSavePo sqlpo : ls) {
			ids.add(sqlpo.getScriptId());
		}

		if (datas != null && datas.size() > 0) {
			ConfigVal.pageSize = datas.size();
			for (DocumentPo po : datas) {
				MyTab tb = new MyTab(po);
				TreeItem<MyTab> item = new TreeItem<>(tb);
				rootNode.getChildren().add(item);
				// 恢复代码编辑框
				if (ids.contains(po.getId())) {
					tb.mainTabPaneAddMyTab();
				}
			}

		}

		int ts = ComponentGetter.mainTabPane.getTabs().size();
		// 初始化上次选中页面
		if (StrUtils.isNotNullOrEmpty(SELECT_PANE)) { 
			int sps = Integer.valueOf(SELECT_PANE);
			if (ts > sps) {
				ComponentGetter.mainTabPane.getSelectionModel().select(sps);
			}  
		}	
		// 没有tab被添加, 添加一新的
		if (ts == 0) {
			MyTab.addCodeEmptyTabMethod();
		}

	}

	// 所有连接节点
	public static ObservableList<TreeItem<MyTab>> allTreeItem() {
		ObservableList<TreeItem<MyTab>> val = ScriptTreeView.getRoot().getChildren();
		return val;
	}

	   

	// 获取当前选中的节点
	public static TreeItem<MyTab> getScriptViewCurrentItem() {
		TreeItem<MyTab> ctt = ScriptTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	 

	// 给root节点加元素 
	public static void treeRootAddItem(TreeItem<MyTab> item) { 
		TreeItem<MyTab> rootNode = ScriptTreeView.getRoot();
		rootNode.getChildren().add(item);		
	}
	// 给root节点加元素 
		public static void treeRootAddItem(MyTab  mytab) {
			TreeItem<MyTab> item = new TreeItem<MyTab> (mytab); 
			treeRootAddItem(item);
		}

	// tree view 双击事件
	public void treeViewDoubleClick(MouseEvent mouseEvent) { 
		if (mouseEvent.getClickCount() == 2) {
			openMyTab();
		}
	}
	
	public static void openMyTab() {
		TreeItem<MyTab> item = ScriptTreeView.getSelectionModel().getSelectedItem();
		var mytab = item.getValue(); 
		if(mytab != null && mytab.getDocumentPo() != null) {
			mytab.mainTabPaneAddMyTab();
		}
	}
	 
	public static  List<DocumentPo>  allScriptPo() {
		 ObservableList<TreeItem<MyTab>> ls = allTreeItem();
		 List<DocumentPo> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb.getDocumentPo());
		 }
		 
		 return list;
	}
	public static  List<MyTab>  allMyTab() {
		 ObservableList<TreeItem<MyTab>> ls = allTreeItem();
		 List<MyTab> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb);
		 } 
		 return list;
	}

	
	public static MyTab findMyTabByScriptPo(DocumentPo scpo) {
		 ObservableList<TreeItem<MyTab>> ls = allTreeItem();
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
	public static void closeAction(TreeItem<MyTab> rootNode) {

		ObservableList<TreeItem<MyTab>>  myTabItemList = rootNode.getChildren();
		TreeItem<MyTab> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
		MyTab tb = ctt.getValue();
		
		String title = CommonUtility.tabText(tb);
		String sql = tb.getTabSqlText(); // SqlEditor.getTabSQLText(tb);
		if (title.endsWith("*") && sql.trim().length() > 0) {
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
	public static void removeNode(ObservableList<TreeItem<MyTab>>  myTabItemList, TreeItem<MyTab> ctt, MyTab tb ) {
		try { 
			var myTabPane = ComponentGetter.mainTabPane;
			var conn = H2Db.getConn();
			if (myTabPane.getTabs().contains(tb)) {
				myTabPane.getTabs().remove(tb);
			}
			myTabItemList.remove(ctt);

			var scpo = tb.getDocumentPo();
			SqlTextDao.deleteScriptArchive(conn, scpo);
		} finally {
			H2Db.closeConn();
		}
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
