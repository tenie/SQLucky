package net.tenie.plugin.DataModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXButton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.DataModel.po.DataModelInfoPo;
import net.tenie.plugin.DataModel.po.DataModelTreeNodePo;
import net.tenie.plugin.DataModel.tools.AddModelFile;

public class DataModelOption {
	HBox FilterHbox = new HBox();
	ObservableList<TreeItem<DataModelTreeNodePo>>  filterTables ;
	// 存放模型名称 对于的所有表的集合
	Map<String, ObservableList<TreeItem<DataModelTreeNodePo> >>  rootMap = new HashMap<>();
	
	
	public DataModelOption(){
//		Label lb = new Label();
//		var icon = ComponentGetter.getIconDefActive("search");
//		lb.setGraphic(icon);
//		lb.getStyleClass().add("myIcon");
		
		JFXButton queryBtn = new JFXButton();
		queryBtn.setGraphic(ComponentGetter.getIconDefActive("search"));
//		addcodeArea.setOnMouseClicked(CommonEventHandler.addCodeTab());
//		queryBtn.setTooltip(CommonUtility.instanceTooltip("Add Data Model File "));
		
		
		
		TextField txt = new TextField("");
		txt.getStyleClass().add("myTextField");
		// 文本输入监听
		txt.textProperty().addListener((o, oldStr, newStr) -> {
			if(rootMap.isEmpty()) {
				for(var md: DataModelTabTree.treeRoot.getChildren()) {
					// 模型名称
					var modelName = md.getValue().getName();
					
					// 模型的孩子（表）， 添加到缓存集合中
					ObservableList<TreeItem<DataModelTreeNodePo> > tmps = FXCollections.observableArrayList();
					tmps.addAll( md.getChildren());
					rootMap.put(modelName, tmps);
					
				}
				
			}
			
			// 为空，还原
			for(var md: DataModelTabTree.treeRoot.getChildren()) {
				// 通过名称从缓存中获取表集合
				var tbs = rootMap.get(md.getValue().getName());
//				System.out.println("tbs = " + tbs.size());
				// 情况表集合
				md.getChildren().clear();
				// 恢复之前缓存的所有表
				md.getChildren().addAll(tbs);
			}
			// 如果输入的字符串有值， 进行查询
			if(StrUtils.isNotNullOrEmpty(newStr)) {
				queryTable( newStr,  DataModelTabTree.treeRoot.getChildren());
				 
			}
			DataModelTabTree.treeView.refresh();
		});
		
		// 添加按钮
		JFXButton addBtn = new JFXButton();
		addBtn.setGraphic(ComponentGetter.getIconDefActive("plus-square"));
//		addcodeArea.setOnMouseClicked(CommonEventHandler.addCodeTab());
		addBtn.setTooltip(CommonUtility.instanceTooltip("Add Data Model File "));
		addBtn.setOnAction(e->{
//			CommonUtility.openFileReadToString("UTF-8");
//			AddModelFile.test();
			readJosnModel("UTF-8");
		});
		
		FilterHbox.getChildren().addAll(queryBtn, txt, addBtn);
		HBox.setHgrow(txt, Priority.ALWAYS);
	}
	
	private void queryTable(String str, ObservableList<TreeItem<DataModelTreeNodePo>> allModels) {
		var upperCaseStr = str.toUpperCase();
		boolean exists = false;
		//遍历所有模型treeItem
		for(TreeItem<DataModelTreeNodePo> model : allModels ) {
			// treeItem 有子节点才继续
			if(model.getChildren().size()>0) {
				// 新表集合， 存放查找到的表
				ObservableList<TreeItem<DataModelTreeNodePo>> filterTable =  FXCollections.observableArrayList();
				var allTables = model.getChildren();
				allTables.forEach(v->{
					// 数据库里表名称
					var dbTabkey  = v.getValue().getName().toUpperCase();
					var tabpo = v.getValue().getTablepo();
					// 表的描述名称
					var tabName =  "";
					// 备注
					var comment =  "";
					if(tabpo!=null) {
						tabName = tabpo.getDefName().toUpperCase();
						comment = tabpo.getComment().toLowerCase();
					}
					
					// 表名， 表中文名， 表的备注根据查询字符串能匹配就加入到新表集合
					if( dbTabkey.contains( upperCaseStr ) ||
						tabName.contains(upperCaseStr) ||
						comment.contains(upperCaseStr)
							) {
						filterTable.add(v);
					}
				});
				// 情况原来的表集合
				model.getChildren().clear();
				// 加入新的表集合
				model.getChildren().addAll(filterTable);
				if(filterTable.size() > 0) {
					exists = true;
				}
			}
		}
		// 展开模型treeItem
		DataModelTabTree.treeRoot.getChildren().get(0).setExpanded(exists);
	}
	
	// 查询字符串 ObservableList<TreeItem<TreeNodePo>> rs =  FXCollections.observableArrayList();
	private static ObservableList<TreeItem<DataModelTreeNodePo>> filter(ObservableList<TreeItem<DataModelTreeNodePo>> val, String str){
		ObservableList<TreeItem<DataModelTreeNodePo>> rs =  FXCollections.observableArrayList();
		String temp = str.toUpperCase();
		val.forEach(v ->{
			if(v.getValue().getName().toUpperCase().contains(temp)) {
				rs.add(v);
			}
		});
		return rs;
	}
	
	
	

	public static void readJosnModel(String encode) {
		File f = FileOrDirectoryChooser.showOpenJsonFile("Open", ComponentGetter.primaryStage);
		if (f == null)
			return ;
		String val = "";
		try {
			val = FileUtils.readFileToString(f, encode);
			if(val != null && !"".equals(val) ) { 
				DataModelInfoPo DataModelPoVal = JSONObject.parseObject(val, DataModelInfoPo.class);
//				System.out.println(DataModelPoVal);
				AddModelFile.insertDataModel(DataModelPoVal);
			}
		} catch (IOException e) {
			e.printStackTrace();
			MyAlert.errorAlert( e.getMessage());
		} 
	}
	
	public static DataModelInfoPo readJosnModel(String fileName, String encode) {
		DataModelInfoPo DataModelPoVal =  null;
		File f =  new File(fileName);
		try {
			String val = FileUtils.readFileToString(f, encode);
			if(val != null && !"".equals(val) ) { 
			    DataModelPoVal = JSONObject.parseObject(val, DataModelInfoPo.class);
				System.out.println(DataModelPoVal);
				System.out.println("======================");
				System.out.println(DataModelPoVal.getEntities().get(0)); 
//				AddModelFile.insertDataModel(DataModelPoVal);
//				PoDao.insert(null, null);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			MyAlert.errorAlert( e.getMessage());
		} 
		
		return DataModelPoVal;
	}
	
	
//	public static void main(String[] args) {
//		readJosnModel("C:\\Users\\tenie\\Downloads\\infodms.chnr.json", "UTF-8");
//	}
	

	public HBox getFilterHbox() {
		return FilterHbox;
	}

	public void setFilterHbox(HBox filterHbox) {
		FilterHbox = filterHbox;
	} 

	 
	
	
}
