package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;

/*   @author tenie */
public class DBinfoContainer {
	private VBox container;
	private FlowPane treeBtnPane;
	private TreeView<TreeNodePo> treeView;
	private AnchorPane filter;
	private DBinfoTree dbInfoTree;

	public DBinfoContainer() {
		container = new VBox();
		treeBtnPane = new FlowPane();

		dbInfoTree = new DBinfoTree();
		treeView = dbInfoTree.getTreeView();
		DBinfoFilter dbf  = new DBinfoFilter();
		filter = dbf.createFilterPane(treeView);
		container.getChildren().addAll(treeBtnPane, treeView, filter);
		VBox.setVgrow(treeView, Priority.ALWAYS);

		treeViewbtnInit(treeBtnPane);

		ComponentGetter.treeView = treeView;
		ComponentGetter.dbInfoTree = dbInfoTree;
	}

	// 操作按钮
	public static void treeViewbtnInit(FlowPane pn) {
		// 页面初始化: 添加组件
		JFXButton addConnbtn = new JFXButton();

		addConnbtn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
		addConnbtn.setOnMouseClicked(CommonEventHandler.addConnEvent());
		addConnbtn.setTooltip(MyTooltipTool.instance("Add new DB Connection"));

		// open连接
		JFXButton openConn = new JFXButton();
		openConn.setGraphic(ImageViewGenerator.svgImageDefActive("link"));
		openConn.setOnMouseClicked(CommonEventHandler.openConnEvent());
		openConn.setTooltip(MyTooltipTool.instance("Open DB Connection"));

		// 断开连接
		JFXButton closeConn = new JFXButton();
		closeConn.setGraphic(ImageViewGenerator.svgImageDefActive("unlink"));
		closeConn.setOnMouseClicked(CommonEventHandler.closeConnEvent());
		closeConn.setTooltip(MyTooltipTool.instance("Close DB Connection"));

		JFXButton closeALlConn = new JFXButton();
		closeALlConn.setGraphic(ImageViewGenerator.svgImageDefActive("power-off"));
		closeALlConn.setOnMouseClicked(CommonEventHandler.closeAllConnEvent());
		closeALlConn.setTooltip(MyTooltipTool.instance("Close All DB Connection"));

		JFXButton editConn = new JFXButton();
		editConn.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
		editConn.setOnMouseClicked(CommonEventHandler.editConnEvent());
		editConn.setTooltip(MyTooltipTool.instance("Edit DB Connection"));

		// 删除连接
		JFXButton deleteConn = new JFXButton();
		deleteConn.setGraphic(ImageViewGenerator.svgImageDefActive("trash"));
		deleteConn.setOnMouseClicked(CommonEventHandler.deleteConnEvent());
		deleteConn.setTooltip(MyTooltipTool.instance("Delete DB Connection"));

		pn.getChildren().add(addConnbtn);
		pn.getChildren().add(openConn);
		pn.getChildren().add(closeConn);
		pn.getChildren().add(closeALlConn);
		pn.getChildren().add(editConn);
		pn.getChildren().add(deleteConn);
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
