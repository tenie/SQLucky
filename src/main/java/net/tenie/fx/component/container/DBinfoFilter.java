package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.tools.StrUtils;
 
/*   @author tenie */
public class DBinfoFilter {
	 AnchorPane filter;
	 private  ObservableList<TreeItem<TreeNodePo>> temp  = FXCollections.observableArrayList();
	 private  ObservableList<TreeItem<TreeNodePo>>  filtList = FXCollections.observableArrayList();
	 private  TreeItem<TreeNodePo> rootNode ; 
	 public DBinfoFilter () {}
	 
	 
	 
	public AnchorPane createFilterPane(TreeView<TreeNodePo> treeView) {
		AnchorPane filter = new AnchorPane();
		filter.setPrefHeight(30);
		filter.setMinHeight(30);
		JFXButton query = new JFXButton();
		JFXTextField txt = new JFXTextField();
		query.setGraphic(ImageViewGenerator.svgImageUnactive("search"));
		query.setOnAction(e -> {
			txt.requestFocus();
		});

		txt.setPrefWidth(200);
		txt.setPrefHeight(20);
		txt.setMaxHeight(20);
		txt.getStyleClass().add("myTextField");
		int x = 0;
		query.setLayoutX(x);
		query.setLayoutY(1);
		x += 35;
		txt.setLayoutX(x);
		txt.setLayoutY(1);

		filter.getChildren().addAll(query, txt);
		txt.textProperty().addListener((o, oldVal, newVal) -> {

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
						new TreeNodePo("Connections", ImageViewGenerator.svgImageDefActive("windows-globe")));
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
				DbConnectionPo connpo = tempcip.getConnpo();
				 
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
						 ObservableList<TreeItem<TreeNodePo>> 
//						 val =  filter( ci.getTableItem() , queryStr); 
						 val =  filter(  ci.getTableNode().getChildren() , queryStr); 
						
						 sz =  val.size();
						// 如果找到来数据, 将数据放入到新的数据对象中
						if (sz > 0) {
							cinew.getTableNode().getChildren().setAll(val);
							cinew.getParentNode().getChildren().add(cinew.getTableNode());
							count += val.size();
						}

						val = filter(ci.getViewNode().getChildren(), queryStr);
						sz = val.size();
						if (sz > 0) {
							cinew.getViewNode().getChildren().setAll(val);
							cinew.getParentNode().getChildren().add(cinew.getViewNode());
							count += val.size();
						}

						val = filter(ci.getFuncNode().getChildren(), queryStr);
						sz = val.size();
						if (sz > 0) {
							cinew.getFuncNode().getChildren().setAll(val);
							cinew.getParentNode().getChildren().add(cinew.getFuncNode());
							count += val.size();
						}
						val = filter(ci.getProcNode().getChildren(), queryStr);
						sz = val.size();
						if (sz > 0) {
							cinew.getProcNode().getChildren().setAll(val);
							cinew.getParentNode().getChildren().add(cinew.getProcNode());
							count += val.size();
						}
						
						val = filter(ci.getTriggerNode().getChildren(), queryStr);
						sz = val.size();
						if (sz > 0) {
							cinew.getTriggerNode().getChildren().setAll(val);
							cinew.getParentNode().getChildren().add(cinew.getTriggerNode());
							count += val.size();
						}
						
						val = filter(ci.getIndexNode().getChildren(), queryStr);
						sz = val.size();
						if (sz > 0) {
							cinew.getIndexNode().getChildren().setAll(val);
							cinew.getParentNode().getChildren().add(cinew.getIndexNode());
							count += val.size();
						}
						
						val = filter(ci.getSequenceNode().getChildren(), queryStr);
						sz = val.size();
						if (sz > 0) {
							cinew.getSequenceNode().getChildren().setAll(val);
							cinew.getParentNode().getChildren().add(cinew.getSequenceNode());
							count += val.size();
						}
						 
						 
						 
						 
						 // 如果找到了数据, 将新的数据对象, 放入schema数据对象
						 if(count > 0 ) {
							 tempcip.addChildren(cinew); 
						 } 
					}					
				}
				// schema数据对象有数据的 情况, 创建一个新的连接节点并返回它
				if(tempcip.getSchemaNode().getChildren().size() > 0) {
					MyTreeItem<TreeNodePo> newConn = new MyTreeItem<TreeNodePo>(
							new TreeNodePo(connpo.getConnName(), ImageViewGenerator.svgImage("link", "#7CFC00")));
					
					newConn.getChildren().add(tempcip.getSchemaNode());
					newConn.getValue().setConnItemContainer(tempcip); 
					return newConn;
				}
			
			}
		}
		
		return null;
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
