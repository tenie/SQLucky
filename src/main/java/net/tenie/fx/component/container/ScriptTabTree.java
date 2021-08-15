package net.tenie.fx.component.container;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import net.tenie.fx.PropertyPo.ScriptPo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.component.TreeItem.ConnItemContainer;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.factory.ScriptTabNodeCellFactory;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.H2SqlTextSavePo;
import net.tenie.lib.db.h2.SqlTextDao;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class ScriptTabTree {

	public static TreeView<MyTab> ScriptTreeView; 
	List<ConnItemContainer> connItemParent = new ArrayList<>(); 
	
	
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
//			menu = new ScriptTreeContextMenu();
//			ContextMenu	contextMenu = menu.getContextMenu(); 
//			treeView.setContextMenu(contextMenu);
		// 选中监听事件
//			treeView.getSelectionModel().selectedItemProperty().addListener(treeViewContextMenu(treeView));
		treeView.getSelectionModel().select(rootNode);

		ScriptTreeView = treeView;

		// 显示设置, 从TreeNodePo中的对象显示为 TreeItem 的名称和图标
		treeView.setCellFactory(new ScriptTabNodeCellFactory());
		 

		recoverScriptNode(rootNode);
		return treeView;
	}
	
	
	// 恢复数据中保存的连接数据
	public static void recoverScriptNode(TreeItem<MyTab> rootNode) {
		try {
			Connection H2conn = H2Db.getConn(); 
			List<H2SqlTextSavePo> ls = SqlTextDao.read(H2conn);
			List<Integer> ids = new ArrayList<>();
			for(H2SqlTextSavePo sqlpo : ls) {
				ids.add(sqlpo.getScriptId());
			}
			List<ScriptPo> datas = SqlTextDao.readScriptPo(H2conn);
			if (datas != null && datas.size() > 0) {
				for (ScriptPo po : datas) {
					MyTab tb = new MyTab(po);
					TreeItem<MyTab> item = new TreeItem<>(tb);
					rootNode.getChildren().add(item);
					// 恢复代码编辑框
					if(ids.contains(po.getId())) {
						SqlEditor.myTabPaneAddMyTab(tb);
					}
				} 
				// 初始化上次选中页面
				String SELECT_PANE = SqlTextDao.readConfig(H2conn, "SELECT_PANE");
				if(StrUtils.isNotNullOrEmpty(SELECT_PANE)) {
					ComponentGetter.mainTabPane.getSelectionModel().select(Integer.valueOf(SELECT_PANE));
				}
			} 
			if(ls == null || ls.size() ==0 ) {
				// 触发鼠标点击事件, 增加一个 代码窗口 , 如果窗口中是空的情况下
				SqlEditor.addCodeEmptyTabMethod();
			}
			
			
			
		} finally {
			H2Db.closeConn();
		}
	}

	// 所有连接节点
	public static ObservableList<TreeItem<MyTab>> allconnsItem() {
		ObservableList<TreeItem<MyTab>> val = ScriptTreeView.getRoot().getChildren();
		return val;
	}

	 

	// 删除 取连接节点 根据名字
//	public static void rmTreeItemByName(String name) {
//		ObservableList<TreeItem<String>> ls = allconnsItem();
//		ls.removeIf(item -> {
//			return item.getValue().getName().equals(name);
//		});
//	}

	 

	// 获取当前选中的节点
	public static TreeItem<MyTab> getScriptViewCurrentItem() {
		TreeItem<MyTab> ctt = ScriptTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	// 根据名称获取连接节点
//	public static TreeItem<String> getTreeItemByName(String name) {
//		ObservableList<TreeItem<String>> ls = allconnsItem();
//		for (TreeItem<String> item : ls) {
//			if (item.getValue().getName().equals(name)) {
//				return item;
//			}
//		}
//		return null;
//	}

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
			TreeItem<MyTab> item = ScriptTreeView.getSelectionModel().getSelectedItem();
			var mytab = item.getValue(); 
			SqlEditor.myTabPaneAddMyTab(mytab);
			// 删除节点
//			ScriptTreeView.getRoot().getChildren().remove(item);
//			SqlTextDao.delScriptPo(po.getId());
		}
	}

	// treeView 右键菜单属性设置
//	public   ChangeListener<TreeItem<TreeNodePo>> treeViewContextMenu(TreeView<TreeNodePo> treeView) { }
//	private TreeItem<TreeNodePo>   ConnItem(TreeItem<TreeNodePo> newValue) { }
 

 
 
}
