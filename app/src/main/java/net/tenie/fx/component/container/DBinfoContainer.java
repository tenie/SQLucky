package net.tenie.fx.component.container;

import javafx.application.Platform;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.SqlcukyTitledPaneInfoPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.InfoTree.DBinfoTreeButtonFactory;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;

/**
 * 
 * @author tenie
 *
 */
public class DBinfoContainer {
	private VBox container;
	private Pane dbInfoTreeBtnPane; // 按钮面板
	private TreeView<TreeNodePo> dbInfoTreeView;
	private DBinfoTree dbInfoTree;
	private ScriptTabTree scriptTabTree; // 脚本

	public DBinfoContainer() {
		// 容器
		container = new VBox();
		// 数据库信息
		dbInfoTree = new DBinfoTree();
		dbInfoTreeView = dbInfoTree.DBinfoTreeView;
		// 按钮
		dbInfoTreeBtnPane = DBinfoTreeButtonFactory.createTreeViewbtn(dbInfoTreeView);
		TitledPane dbInfoTtPane = dbInfoTree.dbInfoTitledPane(dbInfoTreeBtnPane);

		// 脚本
		scriptTabTree = new ScriptTabTree();

		// 数据连接/脚本 切换窗口
		Accordion ad = createAccordion(scriptTabTree.scriptTitledPane(), dbInfoTtPane);

		container.getChildren().addAll(dbInfoTreeBtnPane, ad);
		VBox.setVgrow(ad, Priority.ALWAYS);

		AppWindow.treeView = dbInfoTreeView;
		AppWindow.dbInfoTree = dbInfoTree;
		ComponentGetter.leftNodeContainer = container;

//		ComponentGetter.treeBtnPane = treeBtnPane;

		ComponentGetter.infoAccordion = ad;

		CommonUtils.fadeTransition(dbInfoTreeBtnPane, 1000);
		CommonUtils.fadeTransition(ad, 1000);
		CommonUtils.fadeTransition(dbInfoTreeView, 1000);

	}

	private Accordion createAccordion(TitledPane scriptTitledPane, TitledPane dbTitledPane) {
		Accordion ad = new Accordion();

		ad.setExpandedPane(dbTitledPane);
		ad.getPanes().add(dbTitledPane);
		ad.getPanes().add(scriptTitledPane);

		ad.expandedPaneProperty().addListener((obj, o, n) -> {
			if (ad.getExpandedPane() == null && n == null) {
				Platform.runLater(() -> {
					if (ad.getExpandedPane() == null) {
						dbTitledPane.setExpanded(true);
					}
				});
			}
			if (n != null) {
				SqlcukyTitledPaneInfoPo info = (SqlcukyTitledPaneInfoPo) n.getUserData();
				if (info != null) {
					var bx = info.getBtnsBox();
					container.getChildren().remove(0);
					container.getChildren().add(0, bx);

				}
			}

		});
//		
//		dbTitledPane.expandedProperty().addListener((obs, oldValue, newValue) -> {
//			if(newValue == false) {
//				
//				if( scriptTitledPane.isExpanded() == false) {
//					Platform.runLater(() -> {scriptTitledPane.setExpanded(true); }); 
//				}
//			} 
//		});

//		scriptTitledPane.expandedProperty().addListener((obs, oldValue, newValue) -> {
//			if(newValue == false) {
//				if( scriptTitledPane.isExpanded() == false) {
//					Platform.runLater(() -> {dbTitledPane.setExpanded(true); }); 
//				}
//			} 
//		});

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

	public TreeView<TreeNodePo> getTreeView() {
		return dbInfoTreeView;
	}

	public void setTreeView(TreeView<TreeNodePo> treeView) {
		this.dbInfoTreeView = treeView;
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
