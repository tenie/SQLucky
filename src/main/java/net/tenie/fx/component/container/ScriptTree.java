package net.tenie.fx.component.container;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.fxmisc.richtext.Caret.CaretVisibility;
import org.fxmisc.richtext.CodeArea;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.MenuAction;
import net.tenie.fx.Action.TreeObjAction;
import net.tenie.fx.PropertyPo.DBOptionHelper;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.PropertyPo.FuncProcTriggerPo;
import net.tenie.fx.PropertyPo.ScriptPo;
import net.tenie.fx.PropertyPo.TablePo;
import net.tenie.fx.PropertyPo.TreeItemType;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.component.TreeItem.ConnItemContainer;
import net.tenie.fx.component.TreeItem.ConnItemDbObjects;
import net.tenie.fx.component.TreeItem.MyTreeItem;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.ConnectionDao;
import net.tenie.fx.factory.MenuFactory;
import net.tenie.fx.factory.ScriptNodeCellFactory;
import net.tenie.fx.factory.TreeNodeCellFactory;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.factory.DBInfoTreeContextMenu;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.SqlTextDao;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class ScriptTree {

	public static TreeView<ScriptPo> ScriptTreeView; 
	List<ConnItemContainer> connItemParent = new ArrayList<>(); 
	
	
	public ScriptTree() {
		 createScriptTreeView();
	}

	// db节点view
	public TreeView<ScriptPo> createScriptTreeView() {
		var rootNode = new TreeItem<>(new ScriptPo());
		TreeView<ScriptPo> treeView = new TreeView<>(rootNode);
		treeView.getStyleClass().add("my-tag");
		treeView.setShowRoot(false);
		
		recoverScriptNode(rootNode);
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
		treeView.setCellFactory(new ScriptNodeCellFactory());
		return treeView;
	}
	
	
	// 恢复数据中保存的连接数据
	public static void recoverScriptNode(TreeItem<ScriptPo> rootNode) {
		try {
			Connection H2conn = H2Db.getConn();
			List<ScriptPo> datas = SqlTextDao.readScriptPo(H2conn);
			if (datas != null && datas.size() > 0) {
				for (ScriptPo po : datas) {
					TreeItem<ScriptPo> item = new TreeItem<>(po);
					rootNode.getChildren().add(item);
				}
			}
		} finally {
			H2Db.closeConn();
		}
	}

	// 所有连接节点
	public static ObservableList<TreeItem<ScriptPo>> allconnsItem() {
		ObservableList<TreeItem<ScriptPo>> val = ScriptTreeView.getRoot().getChildren();
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
	public static TreeItem<ScriptPo> getScriptViewCurrentItem() {
		TreeItem<ScriptPo> ctt = ScriptTreeView.getSelectionModel().getSelectedItem();
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
	public static void treeRootAddItem(TreeItem<ScriptPo> item) {
//		TreeView<ScriptPo> treeView = ScriptTreeView;
		TreeItem<ScriptPo> rootNode = ScriptTreeView.getRoot();
		rootNode.getChildren().add(item);
//		ScriptTreeView.getSelectionModel().select(item); // 选择新加的节点
		
	}

	// tree view 双击事件
	public void treeViewDoubleClick(MouseEvent mouseEvent) { 
		if (mouseEvent.getClickCount() == 2) {
			TreeItem<ScriptPo> item = ScriptTreeView.getSelectionModel().getSelectedItem();
			var po = item.getValue();
			Tab tab = SqlEditor.addCodeEmptyTabMethod();
			SqlEditor.setTabSQLText(tab, po.getText(), po.getParagraph());
			CommonUtility.setTabName(tab, po.getTitle());
			
			if (StrUtils.isNotNullOrEmpty(po.getFileName())) { ;
				tab.setId(ConfigVal.SAVE_TAG + po.getFileName()); 
//				CommonUtility.setTabName(tab, po.getTitle());
				ComponentGetter.fileEncode.put(  po.getFileName(), po.getEncode());
			}
			// 删除节点
			ScriptTreeView.getRoot().getChildren().remove(item);
			SqlTextDao.delScriptPo(po.getId());
		}
	}

	// treeView 右键菜单属性设置
//	public   ChangeListener<TreeItem<TreeNodePo>> treeViewContextMenu(TreeView<TreeNodePo> treeView) { }
//	private TreeItem<TreeNodePo>   ConnItem(TreeItem<TreeNodePo> newValue) { }
 

 
 
}
