package net.tenie.fx.component.container;

import javafx.application.Platform;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqluckyTitledPane;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;

/**
 * 
 * @author tenie
 */
public class DBinfoContainer extends  VBox{
	// TitledPane 容器
	private Accordion dbInfoAccordion;
	// 按钮面板
	private VBox dbInfoTreeBtnPane;
	private TreeView<TreeNodePo> dbInfoTreeView;
	public static DBinfoTree dbInfoTree;
	// 脚本
	public static  ScriptTabTree scriptTabTree;

	public DBinfoContainer() {
		super();
		// 数据库信息
		dbInfoTree = new DBinfoTree();
		dbInfoTreeView = DBinfoTree.DBinfoTreeView;
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
		
		ComponentGetter.leftNodeContainer = this;

		ComponentGetter.infoAccordion = dbInfoAccordion;
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
					this.getChildren().removeFirst();
					this.getChildren().addFirst(bx);
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
