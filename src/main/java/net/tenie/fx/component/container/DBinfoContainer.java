package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
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
	private ScriptTree scriptTree;   //脚本
	private DBinfoTreeFilter dbf;
	
	public DBinfoContainer() {
		container = new VBox();
		treeBtnPane = ButtonFactory.createTreeViewbtn();  
		dbInfoTree = new DBinfoTree();
		treeView =  DBinfoTree.DBinfoTreeView ; 

		scriptTree = new ScriptTree();
		var ScripttreeView =  ScriptTree.ScriptTreeView ;  
		
		dbf  = new DBinfoTreeFilter(); 		
		filter = dbf.createFilterPane(treeView);
		
		// 
		Accordion ad = new Accordion();
		TitledPane dbTitledPane = new TitledPane();
		ad.setExpandedPane(dbTitledPane);
		CommonAction.addCssClass(dbTitledPane, "titledPane-color");
		dbTitledPane.setText("DB Config");
		TitledPane scriptTitledPane = new TitledPane();
		CommonAction.addCssClass(scriptTitledPane, "titledPane-color");
		scriptTitledPane.setText("Script");
		ad.getPanes().add(dbTitledPane);
		ad.getPanes().add(scriptTitledPane);
		 
		scriptTitledPane.setContent(ScripttreeView);
	
		dbTitledPane.setContent( treeView);
		
		container.getChildren().addAll(treeBtnPane, ad , filter);
//		container.getChildren().addAll(treeBtnPane, treeView, filter);
		 
		
		VBox.setVgrow(ad, Priority.ALWAYS);
 

		ComponentGetter.treeView = treeView;
		ComponentGetter.dbInfoTree = dbInfoTree;
		ComponentGetter.treeBtnPane = treeBtnPane;
		
		ComponentGetter.dbTitledPane = dbTitledPane;
		ComponentGetter.scriptTitledPane = scriptTitledPane;
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

}
