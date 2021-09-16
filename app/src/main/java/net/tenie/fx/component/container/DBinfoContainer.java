package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.PropertyPo.ScriptPo;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.fx.component.AppWindowComponentGetter;
import net.tenie.Sqlucky.sdk.component.ImageViewGenerator;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.factory.ButtonFactory;

/*   @author tenie */
public class DBinfoContainer {
	private VBox container;
	private FlowPane treeBtnPane;
	private TreeView<TreeNodePo> treeView;
	private AnchorPane filter;
	private DBinfoTree dbInfoTree;
	private ScriptTabTree scriptTabTree;   //脚本
	private DBinfoTreeFilter dbf;
	
	
	public DBinfoContainer() {
		// 容器
		container = new VBox();
		// 按钮
		treeBtnPane = ButtonFactory.createTreeViewbtn();  
		// 数据库
		dbInfoTree = new DBinfoTree();
		treeView =  DBinfoTree.DBinfoTreeView ;  
		// 脚本
		scriptTabTree = new ScriptTabTree();
		var scriptTreeView =  ScriptTabTree.ScriptTreeView ;
		// 查询过滤
		dbf  = new DBinfoTreeFilter(); 		
		filter = dbf.createFilterPane(treeView);
		
		// 数据连接/脚本 切换窗口
		Accordion ad = createAccordion(scriptTreeView, treeView);
		
		container.getChildren().addAll(treeBtnPane, ad , filter);
		
		VBox.setVgrow(ad, Priority.ALWAYS);
 

		AppWindowComponentGetter.treeView = treeView;
		AppWindowComponentGetter.dbInfoTree = dbInfoTree;
		ComponentGetter.treeBtnPane = treeBtnPane;
		
		
		ComponentGetter.infoAccordion = ad;
		
	}

	
	private Accordion createAccordion(TreeView<MyTab> scriptTreeView ,TreeView<TreeNodePo> DBtreeView) { 
		Accordion ad = new Accordion();
		// 数据库连接信息
		TitledPane dbTitledPane = new TitledPane();
		dbTitledPane.setText("DB Config"); 
//		dbTitledPane.setGraphic( ImageViewGenerator.svgImageDefActive("info-circle", 14)); 
		CommonAction.addCssClass(dbTitledPane, "titledPane-color");
		dbTitledPane.setContent( DBtreeView);
		
		

		// 脚本文件
		TitledPane scriptTitledPane = new TitledPane();
		scriptTitledPane.setText("Script");
//		scriptTitledPane.setGraphic( ImageViewGenerator.svgImageDefActive("icomoon-files-empty", 14));
		CommonAction.addCssClass(scriptTitledPane, "titledPane-color");
		scriptTitledPane.setContent(scriptTreeView);
		
		ad.setExpandedPane(dbTitledPane);
		ad.getPanes().add(dbTitledPane);
		ad.getPanes().add(scriptTitledPane);
		
		dbTitledPane.expandedProperty().addListener((obs, oldValue, newValue) -> {
			if(newValue == false) {
				if( scriptTitledPane.isExpanded() == false) {
					Platform.runLater(() -> {scriptTitledPane.setExpanded(true); }); 
				}
			} 
		});
		
		scriptTitledPane.expandedProperty().addListener((obs, oldValue, newValue) -> {
			if(newValue == false) {
				if( scriptTitledPane.isExpanded() == false) {
					Platform.runLater(() -> {dbTitledPane.setExpanded(true); }); 
				}
			} 
		});
		
		ComponentGetter.dbTitledPane = dbTitledPane;
		ComponentGetter.scriptTitledPane = scriptTitledPane;
		   
		return ad;
	}
	
	
	public VBox getContainer() {
		return container;
	}

	public void setContainer(VBox container) {
		this.container = container;
	}

	public FlowPane getTreeBtnPane() {
		return treeBtnPane;
	}

	public void setTreeBtnPane(FlowPane treeBtnPane) {
		this.treeBtnPane = treeBtnPane;
	}

	public TreeView<TreeNodePo> getTreeView() {
		return treeView;
	}

	public void setTreeView(TreeView<TreeNodePo> treeView) {
		this.treeView = treeView;
	}

	public DBinfoTree getDbInfoTree() {
		return dbInfoTree;
	}

	public void setDbInfoTree(DBinfoTree dbInfoTree) {
		this.dbInfoTree = dbInfoTree;
	}


	public ScriptTabTree getScriptTabTree() {
		return scriptTabTree;
	}


	public void setScriptTabTree(ScriptTabTree scriptTabTree) {
		this.scriptTabTree = scriptTabTree;
	}

}
