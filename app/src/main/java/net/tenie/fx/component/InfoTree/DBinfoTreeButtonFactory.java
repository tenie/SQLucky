package net.tenie.fx.component.InfoTree;

import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;

import javafx.scene.Node;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.window.ConnectionEditor;

public class DBinfoTreeButtonFactory {

	public static VBox createTreeViewbtn(TreeView<TreeNodePo> treeView) {
		List<Node> btns = new ArrayList<>();
		VBox optionVbox = new VBox();

		// 查询过滤
		DBinfoTreeFilter dbf = new DBinfoTreeFilter();
		AnchorPane filter = dbf.createFilterPane(treeView);
		AppWindow.dbInfoTreeFilter = filter;

		HBox pn = new HBox();
		// 页面初始化: 添加组件
		JFXButton addConnbtn = new JFXButton();
		addConnbtn.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));
		addConnbtn.setOnMouseClicked(CommonEventHandler.addConnEvent());
		addConnbtn.setTooltip(MyTooltipTool.instance("Add new DB Connection"));
		btns.add(addConnbtn);
		// open连接
		JFXButton openConn = new JFXButton();
		openConn.setGraphic(IconGenerator.svgImageDefActive("link"));
		openConn.setOnMouseClicked(e -> {
			ConnectionEditor.openDbConn();
		});
		openConn.setTooltip(MyTooltipTool.instance("Open DB Connection"));

		btns.add(openConn);
		// 断开连接
		JFXButton closeConn = new JFXButton();
		closeConn.setGraphic(IconGenerator.svgImageDefActive("unlink"));
		closeConn.setOnMouseClicked(e -> {
			ConnectionEditor.closeDbConn();
		});
		closeConn.setTooltip(MyTooltipTool.instance("Close DB Connection"));
		btns.add(closeConn);

		JFXButton closeALlConn = new JFXButton();
		closeALlConn.setGraphic(IconGenerator.svgImageDefActive("power-off"));
		closeALlConn.setOnMouseClicked(CommonEventHandler.closeAllConnEvent());
		closeALlConn.setTooltip(MyTooltipTool.instance("Close All DB Connection"));
		btns.add(closeALlConn);

		JFXButton editConn = new JFXButton();
		editConn.setGraphic(IconGenerator.svgImageDefActive("edit"));
		editConn.setOnMouseClicked(e -> {
			ConnectionEditor.editDbConn();

		});
		editConn.setTooltip(MyTooltipTool.instance("Edit DB Connection"));
		btns.add(editConn);

		// 查找
		JFXButton queryTab = new JFXButton();
		queryTab.setGraphic(IconGenerator.svgImageDefActive("windows-magnify-browse"));
		queryTab.setOnMouseClicked(e -> {
			CommonAction.dbInfoTreeQuery(optionVbox, filter, btns);
		});

		// 收缩树 zero-fitscreen-24
		JFXButton shrink = new JFXButton();
		shrink.setGraphic(IconGenerator.svgImageDefActive("zero-fitscreen-24"));
		shrink.setOnMouseClicked(e -> {
			CommonAction.shrinkTreeView();
		});
		btns.add(shrink);

		// 删除连接
		JFXButton deleteConn = new JFXButton();
		deleteConn.setGraphic(IconGenerator.svgImageDefActive("trash"));
		deleteConn.setOnMouseClicked(CommonEventHandler.deleteConnEvent());
		deleteConn.setTooltip(MyTooltipTool.instance("Delete DB Connection"));
		btns.add(deleteConn);

		// 脚本
//			JFXButton script = new JFXButton();
//			script.setGraphic(IconGenerator.svgImageDefActive("entypo-download"));
//			script.setOnMouseClicked(e->{
//			   CommonAction.archiveAllScript(); 
//			});
//			script.setTooltip(MyTooltipTool.instance("Archive Script "));

		pn.getChildren().add(addConnbtn);
		pn.getChildren().add(editConn);
		pn.getChildren().add(queryTab);
		pn.getChildren().add(shrink);

		pn.getChildren().add(openConn);
		pn.getChildren().add(closeConn);
		pn.getChildren().add(closeALlConn);

		pn.getChildren().add(deleteConn);
//			pn.getChildren().add(script);

		optionVbox.getChildren().add(pn);
		return optionVbox;
	}

}
