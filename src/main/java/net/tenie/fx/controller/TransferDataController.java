package net.tenie.fx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckTreeView;

import com.jfoenix.controls.JFXComboBox;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.MainTabs;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.tools.StrUtils;

public class TransferDataController implements Initializable {
	
	@FXML private HBox treePane;
	
	@FXML private JFXComboBox<Label>  soDB;
	@FXML private JFXComboBox<Label>  soSC;
	
	@FXML private JFXComboBox<Label>  taDB;
	
	@FXML private JFXComboBox<Label>  taSC;
	
	@FXML private CheckBox isIgnore; 
	@FXML private CheckBox isDel;
	
	@FXML private CheckBox tabData; 
	@FXML private CheckBox tabStruct; 
	
	@FXML private CheckBox chView;
	@FXML private CheckBox chFun;
	@FXML private CheckBox chPro;
	@FXML private CheckBox chTri;
	@FXML private CheckBox chIndex; 
	@FXML private CheckBox chSeq;
	
	
	private CheckBoxTreeItem<String> root;
	 ObservableList<Label > empty = FXCollections.observableArrayList();
	
	// 清除 check Box 
	private void cleanCheckBox() {
		isIgnore.setSelected(false);
		isDel.setSelected(false);
		
		tabData.setSelected(false);
		tabStruct.setSelected(false);
		
		chView.setSelected(false);
		chFun.setSelected(false);
		chPro.setSelected(false);
		chTri.setSelected(false);
		chIndex.setSelected(false);
		chSeq.setSelected(false); 
		
	}
	 

	
	// 初始化方法, 这边在初始化的时候添加按钮的点击事件
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		soDB.setItems(DBConns.getChoiceBoxItems());
		taDB.setItems(DBConns.getChoiceBoxItems());
//		soDB.setItems( getConnComboBoxList()); 
//		taDB.setItems( getConnComboBoxList());

		soDB.getSelectionModel().selectedItemProperty()
				.addListener((ChangeListener<? super Label>) (observable, oldValue, newValue) -> {
					soSC.setItems(empty);
					cleanCheckBox();
					String str = newValue.getText();
					soSC.setItems(getSchemaLabels(str));

					root.getChildren().removeAll(root.getChildren());
//					 soSC.getItems().get(0); 

				});
		soSC.getSelectionModel().selectedItemProperty()
				.addListener((ChangeListener<? super Label>) (observable, oldValue, newValue) -> {

					root.getChildren().removeAll(root.getChildren());
					cleanCheckBox();
				});

		taDB.getSelectionModel().selectedItemProperty()
				.addListener((ChangeListener<? super Label>) (observable, oldValue, newValue) -> {
					String str = newValue.getText();
					taSC.setItems(getSchemaLabels(str));
				});

		root = new CheckBoxTreeItem<String>("全选");
		root.setExpanded(true);

		CheckTreeView<String> checkTreeView = new CheckTreeView<>(root);
		checkTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		treePane.getChildren().addAll(checkTreeView);
		HBox.setHgrow(checkTreeView, Priority.ALWAYS);

		// check box
		tabData.selectedProperty().addListener((ChangeListener<? super Boolean>) (observable, oldValue, newValue) -> {
			if (newValue && !tabStruct.isSelected()) {
				String dbname = soDB.getValue().getText();
				String schename = soSC.getValue().getText();
				if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
					TreeItem<TreeNodePo> schemaNode = ComponentGetter.getSchemaNode(dbname, schename);
					addNode(schemaNode.getChildren().get(0));
				}
			}
			if (!newValue && !tabStruct.isSelected()) { //
				removeNode("Table");
			}

		});
		tabStruct.selectedProperty().addListener((ChangeListener<? super Boolean>) (observable, oldValue, newValue) -> {
			if (newValue && !tabData.isSelected()) {
				String dbname = soDB.getValue().getText();
				String schename = soSC.getValue().getText();
				if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
					TreeItem<TreeNodePo> schemaNode = ComponentGetter.getSchemaNode(dbname, schename);
					addNode(schemaNode.getChildren().get(0));
				}
			}
			if (!newValue && !tabData.isSelected()) { //
				removeNode("Table");
			}

		});
		chView.selectedProperty().addListener((ChangeListener<? super Boolean>) (observable, oldValue, newValue) -> {
			if (newValue) {
				String dbname = soDB.getValue().getText();
				String schename = soSC.getValue().getText();
				if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
					TreeItem<TreeNodePo> schemaNode = ComponentGetter.getSchemaNode(dbname, schename);
					addNode(schemaNode.getChildren().get(1));
				}
			}
			if (!newValue ) { //
				removeNode("View");
			}

		});
		
		chFun.selectedProperty().addListener((ChangeListener<? super Boolean>) (observable, oldValue, newValue) -> {
			if (newValue) {
				String dbname = soDB.getValue().getText();
				String schename = soSC.getValue().getText();
				if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
					TreeItem<TreeNodePo> schemaNode = ComponentGetter.getSchemaNode(dbname, schename);
					addNode(schemaNode.getChildren().get(2));
				}
			}
			if (!newValue ) { //
				removeNode("Function");
			}

		});
		
		chPro.selectedProperty().addListener((ChangeListener<? super Boolean>) (observable, oldValue, newValue) -> {
			if (newValue) {
				String dbname = soDB.getValue().getText();
				String schename = soSC.getValue().getText();
				if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
					TreeItem<TreeNodePo> schemaNode = ComponentGetter.getSchemaNode(dbname, schename);
					addNode(schemaNode.getChildren().get(3));
				}
			}
			if (!newValue ) { //
				removeNode("Procedure");
			}

		});

	}
	
	// 获取连接名称list
	private ObservableList<Label> getConnComboBoxList() {
		ComboBox<Label> connComboBox = ComponentGetter.connComboBox;
		ObservableList<Label> sos = connComboBox.getItems();
		ObservableList<Label> newVal = FXCollections.observableArrayList();
		for(Label label : sos) {
			Label la = new Label(label.getText());
			
			newVal.add(la);
		}
		return newVal;
	}
	
	// 获取schema名称列表
	private ObservableList<TreeItem<TreeNodePo>> getSchemaComboBoxList(String dbName) {
		 
	    ObservableList<TreeItem<TreeNodePo> > temp = FXCollections.observableArrayList();
	    ObservableList<TreeItem<TreeNodePo> > newVal = FXCollections.observableArrayList();
	    
	    TreeItem<TreeNodePo>  connNode = ComponentGetter.getConnNode(dbName);
	    if(connNode !=null) {
	    	if(connNode.getChildren().size() > 0) {
	    		temp = connNode.getChildren().get(0).getChildren();
		    	if(temp.size() > 0 ) {
	    			for(TreeItem<TreeNodePo> tnp : temp) {
	    				if(tnp.getChildren().size()> 0) {
	    					newVal.add(tnp);
	    				}
	    			}
	    		}
	    	}
	    	
	    }
		return newVal;
	}
	// 获取schema的下拉的数据
	private ObservableList<Label> getSchemaLabels(String dbName ) { 
		ObservableList<TreeItem<TreeNodePo>>  vals = getSchemaComboBoxList(dbName);
		ObservableList<Label> newVal = FXCollections.observableArrayList();
		newVal.add(new  Label(""));
		for(TreeItem<TreeNodePo> val : vals) {
			Label la = new Label(val.getValue().getName());
			
			newVal.add(la);
		}
		return newVal;
	}
	
	// 生成数据checkTreeView
	private void addNode(TreeItem<TreeNodePo> item) {
		CheckBoxTreeItem<String>  pi  = new CheckBoxTreeItem(item.getValue().getName());
		ObservableList<CheckBoxTreeItem<String>> newVal = FXCollections.observableArrayList();
		root.getChildren().add(pi);
		ObservableList<TreeItem<TreeNodePo>>  subItem = item.getChildren();
		
		for(TreeItem<TreeNodePo> subNode : subItem) {
			String name = subNode.getValue().getName();
			CheckBoxTreeItem<String> SubCbt  = new CheckBoxTreeItem(name);
			
			newVal.add(SubCbt);
		}
		pi.getChildren().addAll( newVal);
	}
	
	private void removeNode(String name) {
		root.getChildren();
		for(int i = 0; i < root.getChildren().size() ; i++) { 
			TreeItem<String> ch = root.getChildren().get(i);
			if(ch.getValue().equals(name)) {
				root.getChildren().remove(i);
				break;
			}
		}
	}
	
	
}
