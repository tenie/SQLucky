package net.tenie.fx.component.container;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.PropertyPo.ScriptPo;
import net.tenie.fx.PropertyPo.TreeItemType;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.component.TreeItem.ConnItemContainer;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.factory.DBInfoTreeContextMenu;
import net.tenie.fx.factory.ScriptTabNodeCellFactory;
import net.tenie.fx.factory.ScriptTreeContextMenu;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.H2SqlTextSavePo;
import net.tenie.lib.db.h2.SqlTextDao;
import net.tenie.lib.tools.StrUtils;

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
		List<ScriptPo> datas ;
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
			for (ScriptPo po : datas) {
				MyTab tb = new MyTab(po);
				TreeItem<MyTab> item = new TreeItem<>(tb);
				rootNode.getChildren().add(item);
				// 恢复代码编辑框
				if (ids.contains(po.getId())) {
					SqlEditor.myTabPaneAddMyTab(tb);
				}
			}

		}

		// 初始化上次选中页面
		if (StrUtils.isNotNullOrEmpty(SELECT_PANE)) {
			int ts = ComponentGetter.mainTabPane.getTabs().size();
			int sps = Integer.valueOf(SELECT_PANE);
			if (ts > sps) {
				ComponentGetter.mainTabPane.getSelectionModel().select(sps);
			}

			// 如果窗口中是空的情况下 , 增加一个 代码窗口
			if (ts == 0) {
				SqlEditor.addCodeEmptyTabMethod();
			}

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
//			TreeItem<MyTab> item = ScriptTreeView.getSelectionModel().getSelectedItem();
//			var mytab = item.getValue(); 
//			SqlEditor.myTabPaneAddMyTab(mytab);
			openMyTab();
		}
	}
	
	public static void openMyTab() {
		TreeItem<MyTab> item = ScriptTreeView.getSelectionModel().getSelectedItem();
		var mytab = item.getValue(); 
		SqlEditor.myTabPaneAddMyTab(mytab);
	}
	 
	public static  List<ScriptPo>  allScriptPo() {
		 ObservableList<TreeItem<MyTab>> ls = allTreeItem();
		 List<ScriptPo> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb.getScriptPo());
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

	
	public static MyTab findMyTabByScriptPo(ScriptPo scpo) {
		 ObservableList<TreeItem<MyTab>> ls = allTreeItem();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 var tmp =  mytb.getScriptPo();
			 if(tmp.equals(scpo)) {
				 return mytb;
			 }
					 
		 } 
		 return null;
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
