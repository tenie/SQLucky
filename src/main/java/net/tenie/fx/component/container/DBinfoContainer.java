package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ButtonFactory;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;

/*   @author tenie */
public class DBinfoContainer {
	private VBox container;
	private FlowPane treeBtnPane;
	private TreeView<TreeNodePo> treeView;
	private AnchorPane filter;
	private DBinfoTree dbInfoTree;
	private DBinfoFilter dbf;
	
	public DBinfoContainer() {
		container = new VBox();
		treeBtnPane = new FlowPane();

		dbInfoTree = new DBinfoTree();
		treeView = dbInfoTree.getTreeView();
		dbf  = new DBinfoFilter(); 		
		filter = dbf.createFilterPane(treeView);
		container.getChildren().addAll(treeBtnPane, treeView, filter);
		VBox.setVgrow(treeView, Priority.ALWAYS);

		ButtonFactory.treeViewbtnInit(treeBtnPane);

		ComponentGetter.treeView = treeView;
		ComponentGetter.dbInfoTree = dbInfoTree;
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
