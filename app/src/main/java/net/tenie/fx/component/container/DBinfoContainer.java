package net.tenie.fx.component.container;

import javafx.application.Platform;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqluckyTitledPane;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.InfoTree.DBinfoTreeButtonFactory;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;

/**
 * 
 * @author tenie
 *
 */
public class DBinfoContainer extends  VBox{
	private Accordion dbInfoAccordion; // TitledPane 容器
	private VBox dbInfoTreeBtnPane; // 按钮面板
	private TreeView<TreeNodePo> dbInfoTreeView;
	private DBinfoTree dbInfoTree;
	private ScriptTabTree scriptTabTree; // 脚本

	public DBinfoContainer() {
		super();
		// 数据库信息
		dbInfoTree = new DBinfoTree();
		dbInfoTreeView = dbInfoTree.DBinfoTreeView;
		// 按钮
		dbInfoTreeBtnPane = dbInfoTree.getDbInfoTreeBtnPane();
		// 脚本
		scriptTabTree = new ScriptTabTree();

		// TitledPane容器: 放入 数据连接 脚本 ...
		dbInfoAccordion = createAccordion(scriptTabTree, dbInfoTree);

		this.getChildren().addAll(dbInfoTreeBtnPane, dbInfoAccordion);
		VBox.setVgrow(dbInfoAccordion, Priority.ALWAYS);

		AppWindow.treeView = dbInfoTreeView;

		ComponentGetter.treeView = dbInfoTreeView;
		
		AppWindow.dbInfoTree = dbInfoTree;
		ComponentGetter.leftNodeContainer = this;

		ComponentGetter.infoAccordion = dbInfoAccordion;

		CommonUtils.fadeTransition(dbInfoTreeBtnPane, 1000);
		CommonUtils.fadeTransition(dbInfoAccordion, 1000);
		CommonUtils.fadeTransition(dbInfoTreeView, 1000);

	}

	/**
	 * 创建 TitledPane 容器
	 * @param scriptTitledPane
	 * @param dbTitledPane
	 * @return
	 */
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
			if (n != null && n instanceof SqluckyTitledPane sqluckyTitledPane) {
					var bx = sqluckyTitledPane.getBtnsBox();
					this.getChildren().remove(0);
					this.getChildren().add(0, bx);
			}

		});

		ComponentGetter.dbTitledPane = dbTitledPane;
		ComponentGetter.scriptTitledPane = scriptTitledPane;

		return ad;
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
