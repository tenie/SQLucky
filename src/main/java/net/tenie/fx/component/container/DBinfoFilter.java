package net.tenie.fx.component.container;

import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.lib.tools.StrUtils;
 
/*   @author tenie */
public class DBinfoFilter {
	 AnchorPane filter;
	 private  ObservableList<TreeItem<TreeNodePo>> temp  = FXCollections.observableArrayList();
	 private  ObservableList<TreeItem<TreeNodePo>>  filtList = FXCollections.observableArrayList();
	 
	 public DBinfoFilter () {}
	 public   AnchorPane createFilterPane(TreeView<TreeNodePo> treeView) {
		 AnchorPane filter = new AnchorPane();
		 filter.setPrefHeight(20);
		 JFXButton query = new JFXButton();
		 JFXTextField txt = new JFXTextField();
		 query.setGraphic(ImageViewGenerator.svgImageUnactive("search"));
		 query.setOnAction(e->{
			 txt.requestFocus();
		 });
		 
		 txt.setPrefWidth(200);
		 txt.getStyleClass().add("myTextField");
		 txt.textProperty().addListener((o, oldVal, newVal) -> {		
			 
			 // 获取连接节点集合
			 ObservableList<TreeItem<TreeNodePo>> connNodes =   treeView.getRoot().getChildren();
			 if(temp.size() < connNodes.size()) {
				 temp.addAll( connNodes);
//				 treeView.getRoot().getChildren().clear();
//				 treeView.getRoot().getChildren().addAll(temp);
//				 return;
			 }
			 
			if(StrUtils.isNullOrEmpty(newVal)  ) {
				if(temp != null ) {
//					for (int i = 0; i < temp.size(); i++) {
//						TreeItem<TreeNodePo> np = temp.get(i);
//						np.
//					}
//					treeView.getRoot().getChildren().setAll(temp);
					 treeView.getRoot().getChildren().clear();
					 treeView.getRoot().getChildren().addAll(temp);
				}
			}
			
			if(StrUtils.isNotNullOrEmpty(newVal)) {
				 
				filtList.clear();
				for(int i = 0; i < temp.size() ; i++) {
					TreeItem<TreeNodePo> connRoot  = temp.get(i);
					if( connRoot.getChildren().size() > 0 ) {
						TreeNodePo rootPo = connRoot.getValue();
						ConnItemParent cip = rootPo.getConnItemParent();
						if(cip != null) {
							 ObservableList<TreeItem<TreeNodePo>> schemas = cip.getSchemaNode().getChildren();
							 ObservableList<TreeItem<TreeNodePo>> schemasTemp = cip.getSchemaNode().getChildren();
							 List<ConnItem> cs = new ArrayList<>();
							 for (int j = 0; j < schemas.size(); j++) {
								int count = 0;
								 // schema 有子节点时
								if( schemas.get(j).getChildren().size() > 0) { 
									
									 ConnItem ci = cip.getConnItems().get( schemas.get(j).getValue().getName());
									 ObservableList<TreeItem<TreeNodePo>> val =  filter( ci.getTableItem() , newVal);
									 ci.getTableNode().getChildren().setAll(val);
									 count += val.size();
									 
									 val =  filter( ci.getViewItem() , newVal);
									 ci.getViewNode().getChildren().setAll(val);
									 count += val.size();
									 
									 val =  filter( ci.getFuncItem() , newVal);
									 ci.getFuncNode().getChildren().setAll(val);
									 count += val.size();
									 
									 val =  filter( ci.getProcItem() , newVal);
									 ci.getProcNode().getChildren().setAll(val); 
									 count += val.size();
									 if(count > 0 ) {
										cs.add(ci);
									 }
								}
							}
							if(cs.size() > 0) {
								for (int j = 0; j < cs.size(); j++) {
									ConnItem ci = cs.get(j); 
									connRoot.getChildren().clear();
									connRoot.getChildren().add(ci.getParentNode());
								}
							}
							 
						}
					}
					
				}
				
				
//				for(int i = 0; i < cips.size(); i++) {
//					ConnItemParent cip = cips.get(i);  
//					TreeItem<TreeNodePo>  cipRoot = cip.getRoot();
//					 if( cipRoot.getChildren().size() > 0 ) {
//						 List<ConnItem> connItems = cip.getConnItem();
//						 for(int j = 0 ; j < connItems.size() ; j++) {
//							 ConnItem ci =  connItems.get(j);
//							 ObservableList<TreeItem<TreeNodePo>> val =  filter( ci.getTableItem() , newVal);
//							 ci.getTableNode().getChildren().setAll(val);
//							 
//							 val =  filter( ci.getViewItem() , newVal);
//							 ci.getViewNode().getChildren().setAll(val);
//							 
//							 val =  filter( ci.getFuncItem() , newVal);
//							 ci.getFuncNode().getChildren().setAll(val);
//							 
//							 val =  filter( ci.getProcItem() , newVal);
//							 ci.getProcNode().getChildren().setAll(val); 
//							 
//						 }
//						 
//					 }else {
////						 System.out.println(ci.getSchemaNode().getChildren().get(i).getValue().getName());
//						 rmRoot(cipRoot);
//						 
//					 }
//				 }
			} 
		 	
			 
		 });
		 
		 	int x = 0;
			query.setLayoutX(x);
			query.setLayoutY(1);
			x += 35;
			txt.setLayoutX(x);
			txt.setLayoutY(1);
		 
		 filter.getChildren().addAll(query, txt);  
		 return filter;
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
	
	
	private static void  rmRoot(TreeItem<TreeNodePo>  cipRoot) {
		TreeItem<TreeNodePo> rootParent = cipRoot.getParent(); 
		String rootName = cipRoot.getValue().getName();
		ObservableList<TreeItem<TreeNodePo>> os =  rootParent.getChildren();
		for(int i = 0 ; i < os.size() ; i ++ ) {
			if(rootName.equals(  os.get(i).getValue().getName() )) {
				os.remove(i);
			}
		}
		
	}
}
