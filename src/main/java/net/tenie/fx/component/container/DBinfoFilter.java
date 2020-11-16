package net.tenie.fx.component.container;

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
	 
	 public static AnchorPane createFilterPane(TreeView<TreeNodePo> treeView) {
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
			List<ConnItem> connItems =  ComponentGetter.dbInfoTree.getConnItems();
			
			if(StrUtils.isNullOrEmpty(newVal)  ) {
				for(int i = 0; i < connItems.size(); i++) {
					 ConnItem ci = connItems.get(i);  
					 ObservableList<TreeItem<TreeNodePo>>  val  =  ci.getTableItem();
					 ci.getTableNode().getChildren().setAll(val); 
					 
					 val  =   ci.getViewItem()  ;
					 ci.getViewNode().getChildren().setAll(val);
					 
					 val =   ci.getFuncItem() ;
					 ci.getFuncNode().getChildren().setAll(val);
					 
					 val =   ci.getProcItem() ;
					 ci.getProcNode().getChildren().setAll(val);
					 
				 }
			}
			
			if(StrUtils.isNotNullOrEmpty(newVal)) {
				for(int i = 0; i < connItems.size(); i++) {
					 ConnItem ci = connItems.get(i); 
					 
					 ObservableList<TreeItem<TreeNodePo>> val =  filter( ci.getTableItem() , newVal);
					 ci.getTableNode().getChildren().setAll(val);
					 
					 val =  filter( ci.getViewItem() , newVal);
					 ci.getViewNode().getChildren().setAll(val);
					 
					 val =  filter( ci.getFuncItem() , newVal);
					 ci.getFuncNode().getChildren().setAll(val);
					 
					 val =  filter( ci.getProcItem() , newVal);
					 ci.getProcNode().getChildren().setAll(val);
				 }
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
}
