package net.tenie.plugin.DataModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXButton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.myEvent;
import net.tenie.plugin.DataModel.po.DataModelInfoPo;
import net.tenie.plugin.DataModel.po.DataModelTreeNodePo;
import net.tenie.plugin.DataModel.tools.AddModelFile;

public class DataModelOption {
	private HBox FilterHbox = new HBox();
	private TextField txt  = new TextField();
	private JFXButton queryBtn  = new JFXButton();
	private JFXButton addBtn = new JFXButton();
	private ObservableList<TreeItem<DataModelTreeNodePo>>  filterTables ;
	public static Map<String, Double> queryFieldColWidth = new HashMap<>();
	public static Map<String, Double> tableInfoColWidth = new HashMap<>();
	
	static {
		queryFieldColWidth.put("TABLE", 250.0);
		queryFieldColWidth.put("FIELD", 200.0);
		queryFieldColWidth.put("FIELD_NAME", 250.0);
		queryFieldColWidth.put("COMMENT", 300.0);
		
		tableInfoColWidth.put("FIELD", 180.0);
		tableInfoColWidth.put("NAME", 220.0);
		tableInfoColWidth.put("COMMENT", 250.0);
		tableInfoColWidth.put("PRIMARY_KEY", 80.0);
	}
	
	// 存放模型名称 对于的所有表的集合
	Map<String, ObservableList<TreeItem<DataModelTreeNodePo> >>  rootMap = new HashMap<>();
	
	
	public DataModelOption(){
//		txt = new TextField("");
		txt.getStyleClass().add("myTextField");
		
//	    queryBtn = new JFXButton();
		queryBtn.setGraphic(ComponentGetter.getIconDefActive("search"));
		queryBtn.setTooltip(CommonUtility.instanceTooltip("Search table $ field info "));
		queryBtn.setOnMouseClicked(e->{
			String txtVal = txt.getText();
			if(StrUtils.isNotNullOrEmpty(txtVal)) {
				exeQueryTable(txtVal);
				exeQueryTableFields(txtVal);
			}else {
				txt.requestFocus();
			}
			
		});
		
		// 文本输入监听
		txt.textProperty().addListener((o, oldStr, newStr) -> {
			if(StrUtils.isNullOrEmpty(newStr)) {
				// 为空，还原
				for(var md: DataModelTabTree.treeRoot.getChildren()) {
					// 通过名称从缓存中获取表集合
					var tbs = rootMap.get(md.getValue().getName());
//					System.out.println("tbs = " + tbs.size());
					// 情况表集合
					md.getChildren().clear();
					// 恢复之前缓存的所有表
					md.getChildren().addAll(tbs);
				}
			}
		});
		// 回车后触发查询按钮
		txt.setOnKeyPressed(val->{
			 if(val.getCode() == KeyCode.ENTER ){ 
				 myEvent.btnClick(queryBtn);
			 }
		});
		
		// 导入文件按钮
//	    addBtn = new JFXButton();
		addBtn.setGraphic(ComponentGetter.getIconDefActive("folder-open"));
//		addcodeArea.setOnMouseClicked(CommonEventHandler.addCodeTab());
		addBtn.setTooltip(CommonUtility.instanceTooltip("Import Data Model Json File "));
		addBtn.setOnAction(e->{
			readJosnModel("UTF-8");
		});
		
		FilterHbox.getChildren().addAll(queryBtn, txt, addBtn);
		HBox.setHgrow(txt, Priority.ALWAYS);
	}
	
	
	// 通过字符串， 查询字段表
	private void exeQueryTableFields(String queryStr) {
		
		
		queryStr = queryStr.toUpperCase();
				
		var conn = SqluckyAppDB.getConn();
		String sql =  "SELECT b.DEF_KEY AS TABLE, a.DEF_KEY AS  FIELD,  a.DEF_NAME AS FIELD_NAME , a.COMMENT FROM DATA_MODEL_TABLE_FIELDS  a\n"
				+ "left join DATA_MODEL_TABLE b on b.ITEM_ID = a.TABLE_ID\n"
				+ "where  a.DEF_KEY like '%"+queryStr+"%' or  a.DEF_NAME  like '%"+queryStr+"%' or a.COMMENT like '%"+queryStr+"%'";
		try {
			SdkComponent.dataModelQueryFieldsShow(sql, conn , queryStr, new ArrayList(), queryFieldColWidth);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
	private void exeQueryTable(String queryStr) {
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
//			System.out.println("tbs = " + tbs.size());
			// 情况表集合
			md.getChildren().clear();
			// 恢复之前缓存的所有表
			md.getChildren().addAll(tbs);
		}
		// 如果输入的字符串有值， 进行查询
		if(StrUtils.isNotNullOrEmpty(queryStr)) {
			queryTable( queryStr,  DataModelTabTree.treeRoot.getChildren());
			 
		}
		DataModelTabTree.treeView.refresh();
	
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
