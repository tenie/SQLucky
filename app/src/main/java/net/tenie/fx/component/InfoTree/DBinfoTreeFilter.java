package net.tenie.fx.component.InfoTree;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemContainer;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemDbObjects;
import net.tenie.fx.component.InfoTree.TreeItem.MyTreeItem;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.lib.tools.IconGenerator;

 
/**
 * 
 * @author tenie
 *
 */
public class DBinfoTreeFilter {
	 AnchorPane filter;
	 private  ObservableList<TreeItem<TreeNodePo>> temp  = FXCollections.observableArrayList();
	 private  ObservableList<TreeItem<TreeNodePo>>  filtList = FXCollections.observableArrayList();
	 private  TreeItem<TreeNodePo> rootNode ;
	 private TextField txt  ;
	 public DBinfoTreeFilter () {}
	 
	public TextField getTxtField() {
		return txt;
	}
	 
	 
	public AnchorPane createFilterPane(TreeView<TreeNodePo> treeView) {
		txt = new TextField();
		ComponentGetter.dbInfoFilter = txt;
		AnchorPane filter = new AnchorPane();
		filter.setPrefHeight(30);
		filter.setMinHeight(30);
		JFXButton query = new JFXButton();
		query.setGraphic(IconGenerator.svgImageDefActive("search"));
		query.setOnAction(e -> {
			txt.requestFocus();
		});
		 

		txt.setPrefWidth(200);
		txt.setPrefHeight(25);
		txt.setMaxHeight(25);
		txt.getStyleClass().add("myTextField");
		int x = 0;
		query.setLayoutX(x);
		query.setLayoutY(1);
		x += 35;
		txt.setLayoutX(x);
		txt.setLayoutY(1);
 
		Button clean = new Button(); 
		 
		AnchorPane.setLeftAnchor(clean, 210.0);
		AnchorPane.setTopAnchor(clean, 5.0); 
		clean.setMaxSize(12, 12);
		
		clean.setGraphic(IconGenerator.svgImageUnactive("times-circle" , 14));
		clean.getStyleClass().add("myCleanBtn");
		clean.setVisible(false); //clean 按钮默认不显示, 只有在鼠标进入搜索框才显示
		
		clean.setOnAction(e->{
			txt.clear();
		});
		
		filter.setOnMouseEntered(e->{
			clean.setVisible(true);
		});
		filter.setOnMouseExited(e->{
			clean.setVisible(false);
		});
		
		filter.getChildren().addAll(query, txt , clean);
		
		
		txt.textProperty().addListener((o, oldVal, newVal) -> {
			ComponentGetter.dbTitledPane.setExpanded(true);
			// 缓存
			ObservableList<TreeItem<TreeNodePo>> connNodes = treeView.getRoot().getChildren();
			if (temp.size() < connNodes.size()) {
				temp.clear();
				temp.addAll(connNodes);
				rootNode = treeView.getRoot();
			}

			// 恢复
			if (StrUtils.isNullOrEmpty(newVal)) {
				if (rootNode != null) {
					treeView.setRoot(rootNode);
				}
			}

			// 查询时
			if (StrUtils.isNotNullOrEmpty(newVal)) {
				filtList.clear();
				// 遍历每一个连接节点, 在节点下查找到了数据, 就会返回一个新节点对象, 最后使用新节点创建一个新的树
				for (int i = 0; i < temp.size(); i++) {
					TreeItem<TreeNodePo> connNode = temp.get(i);
					TreeItem<TreeNodePo> nConnNode = connNodeOption(connNode, newVal);
					// 新节点不是NULL 缓存
					if (nConnNode != null) {
						filtList.add(nConnNode);
					}
				}
				// 创建一个新的树根, 将查询数据挂在新的上面
				TreeItem<TreeNodePo> rootNode = new TreeItem<>(
						new TreeNodePo("Connections", IconGenerator.svgImageDefActive("windows-globe")));
				rootNode.getChildren().addAll(filtList);
				treeView.setRoot(rootNode); // 使用新的树根
				
				if(filtList.size() >0 ) {
					CommonAction.unfoldTreeView();
				}
				
			}

		});

		return filter;
	}
 
	
	/*
	 * 传递连接节点, 对其进行过滤
	 * 如果节点包含查询内容就返回一个新的节点, 否则返回null
	 */
	private TreeItem<TreeNodePo>  connNodeOption(TreeItem<TreeNodePo> conn, String queryStr) {
		// 1. 首先看节点是否激活的(有子节点?)
		if( conn.getChildren().size() > 0) {
			ConnItemContainer cip = conn.getValue().getConnItemContainer(); 
			if(cip != null &&  cip.getSchemaNode().getChildren().size() > 0) {
				// 获取Schema的节点集合
				ObservableList<TreeItem<TreeNodePo>> vals = cip.getSchemaNode().getChildren();
				
				ConnItemContainer tempcip = new ConnItemContainer(cip.getConnpo());
				SqluckyConnector connpo = tempcip.getConnpo();
				 
				// 2. 遍历每个schema节点
				for (int i = 0; i < vals.size(); i++) {
					TreeItem<TreeNodePo> sche = vals.get(i);
					int count = 0;
				    int sz =  0;
				    // 如果schema节点 是激活的
					if(sche.getChildren().size() > 0) {
						// 从schema节点下获取数据对象
						ConnItemDbObjects ci =	sche.getValue().getConnItem(); 
						// 创建一个新的数据对象, 来存储过滤后的数据
						ConnItemDbObjects cinew = new ConnItemDbObjects( ); 
						cinew.initConnItem(connpo,  ci.getSchemaName());
					
						
						// 开始查找, 从表开始
						count += filterHelper(ci.getTableNode(), queryStr, cinew.getTableNode(), cinew.getParentNode());
						
						count += filterHelper(ci.getViewNode(), queryStr, cinew.getViewNode(), cinew.getParentNode());

						count += filterHelper(ci.getFuncNode(), queryStr, cinew.getFuncNode(), cinew.getParentNode());

						count += filterHelper(ci.getProcNode(), queryStr, cinew.getProcNode(), cinew.getParentNode());

						count += filterHelper(ci.getTriggerNode(), queryStr, cinew.getTriggerNode(), cinew.getParentNode());
						
						count += filterHelper(ci.getIndexNode(), queryStr, cinew.getIndexNode(), cinew.getParentNode());

						count += filterHelper(ci.getSequenceNode(), queryStr, cinew.getSequenceNode(), cinew.getParentNode());
						 
						 // 如果找到了数据, 将新的数据对象, 放入schema数据对象
						 if(count > 0 ) {
							 tempcip.addChildren(cinew); 
						 } 
					}					
				}
				// schema数据对象有数据的 情况, 创建一个新的连接节点并返回它
				if(tempcip.getSchemaNode().getChildren().size() > 0) {
					MyTreeItem<TreeNodePo> newConn = new MyTreeItem<TreeNodePo>(
							new TreeNodePo(connpo.getConnName(), IconGenerator.svgImage("link", "#7CFC00")));
					
					newConn.getChildren().add(tempcip.getSchemaNode());
					newConn.getValue().setConnItemContainer(tempcip); 
					return newConn;
				}
			
			}
		}
		
		return null;
	}
	
	private static int filterHelper(TreeItem<TreeNodePo>  node, String queryStr, TreeItem<TreeNodePo> cnode, TreeItem<TreeNodePo> pnode) {
		int count = 0;
		if(node !=null) {
			ObservableList<TreeItem<TreeNodePo>>    val = filter(node.getChildren(), queryStr);
			int sz = val.size();
			if (sz > 0) {
				cnode.getChildren().setAll(val);
				pnode.getChildren().add(cnode);
				count += val.size();
			}
		}
		return count;
	}
	
	 
	private static ObservableList<TreeItem<TreeNodePo>> filter(ObservableList<TreeItem<TreeNodePo>> val, String str){
		ObservableList<TreeItem<TreeNodePo>> rs =  FXCollections.observableArrayList();
		String temp = str.toUpperCase();
		val.forEach(v ->{
			if(v.getValue().getName().toUpperCase().contains(temp)) {
				rs.add(v);
			}
		});
		return rs;
	}
	
}
