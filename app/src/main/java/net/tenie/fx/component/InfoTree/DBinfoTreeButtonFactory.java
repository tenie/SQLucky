package net.tenie.fx.component.InfoTree;

import com.jfoenix.controls.JFXButton;

import javafx.scene.layout.FlowPane;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.window.ConnectionEditor;

public class DBinfoTreeButtonFactory {
	// 连接区
		// 操作按钮
		public static FlowPane createTreeViewbtn() {
			FlowPane pn = new FlowPane();
			// 页面初始化: 添加组件
			JFXButton addConnbtn = new JFXButton();
			addConnbtn.setGraphic(IconGenerator.svgImageDefActive("plus-square-o"));
			addConnbtn.setOnMouseClicked(CommonEventHandler.addConnEvent());
			addConnbtn.setTooltip(MyTooltipTool.instance("Add new DB Connection"));

			// open连接
			JFXButton openConn = new JFXButton();
			openConn.setGraphic(IconGenerator.svgImageDefActive("link"));
			openConn.setOnMouseClicked(e->{
				ConnectionEditor.openDbConn();
			});
			openConn.setTooltip(MyTooltipTool.instance("Open DB Connection"));

			// 断开连接
			JFXButton closeConn = new JFXButton();
			closeConn.setGraphic(IconGenerator.svgImageDefActive("unlink"));
			closeConn.setOnMouseClicked(e->{
				ConnectionEditor.closeDbConn();
			});
			closeConn.setTooltip(MyTooltipTool.instance("Close DB Connection"));

			JFXButton closeALlConn = new JFXButton();
			closeALlConn.setGraphic(IconGenerator.svgImageDefActive("power-off"));
			closeALlConn.setOnMouseClicked(CommonEventHandler.closeAllConnEvent());
			closeALlConn.setTooltip(MyTooltipTool.instance("Close All DB Connection"));

			JFXButton editConn = new JFXButton();
			editConn.setGraphic(IconGenerator.svgImageDefActive("edit"));
			editConn.setOnMouseClicked(e->{
//				TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem(); 
//				val = ConnectionEditor.getConnNodeRoot(val);
//				boolean tf = ConnectionEditor.treeItemIsLink(val);
//				if(tf) { 
//					ConnectionEditor.closeDbConn();
//					ConnectionEditor.editLinkStatus = true;
//				}else {
//					ConnectionEditor.editLinkStatus = false;
//				}
				
				ConnectionEditor.editDbConn();
				
			});
			editConn.setTooltip(MyTooltipTool.instance("Edit DB Connection"));

			// 查找
			JFXButton queryTab = new JFXButton();
			queryTab.setGraphic(IconGenerator.svgImageDefActive("windows-magnify-browse"));
			queryTab.setOnMouseClicked(e->{
				CommonAction.dbInfoTreeQuery();
			});
			queryTab.setTooltip(MyTooltipTool.instance("Find Table  (F4)"));
			
			
			
			// 收缩树 zero-fitscreen-24
			JFXButton shrink = new JFXButton();
//			btns.add(shrink);
			shrink.setGraphic(IconGenerator.svgImageDefActive("zero-fitscreen-24"));
			shrink.setOnMouseClicked(e -> {
				CommonAction.shrinkTreeView();
			});
			shrink.setTooltip(MyTooltipTool.instance("Shrink Tree"));
			
			
			// 删除连接
			JFXButton deleteConn = new JFXButton();
			deleteConn.setGraphic(IconGenerator.svgImageDefActive("trash"));
			deleteConn.setOnMouseClicked(CommonEventHandler.deleteConnEvent());
			deleteConn.setTooltip(MyTooltipTool.instance("Delete DB Connection"));

			
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
			
			
			return pn;
		}

}
