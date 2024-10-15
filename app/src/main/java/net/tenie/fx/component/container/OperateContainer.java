package net.tenie.fx.component.container;

import org.controlsfx.control.MasterDetailPane;

import javafx.geometry.Side;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

/**
 * 操作容器, 左侧窗口, 代码编辑窗口
 */
public class OperateContainer extends HBox {
	private MasterDetailPane treeAreaDetailPane;
	private DBinfoContainer dbinfoCtr; // 连接管理窗口
	private CodeContainer codeCtr; // 代码编辑窗口

	public OperateContainer() {
		super();

		codeCtr = new CodeContainer();
		dbinfoCtr = new DBinfoContainer();

		treeAreaDetailPane = new MasterDetailPane(Side.LEFT);
		treeAreaDetailPane.setShowDetailNode(true);
		treeAreaDetailPane.setMasterNode(codeCtr);
		treeAreaDetailPane.setDetailNode(dbinfoCtr);

		this.getChildren().add(treeAreaDetailPane);

		HBox.setHgrow(treeAreaDetailPane, Priority.ALWAYS);
		ComponentGetter.treeAreaDetailPane = treeAreaDetailPane;

//		CommonUtils.fadeTransition(codeCtr, 1000);
//		CommonUtils.fadeTransition(dbinfoCtr, 1000);
	}

	public MasterDetailPane getTreeAreaDetailPane() {
		return treeAreaDetailPane;
	}

	public void setTreeAreaDetailPane(MasterDetailPane treeAreaDetailPane) {
		this.treeAreaDetailPane = treeAreaDetailPane;
	}

	public DBinfoContainer getDbinfoCtr() {
		return dbinfoCtr;
	}

	public void setDbinfoCtr(DBinfoContainer dbinfoCtr) {
		this.dbinfoCtr = dbinfoCtr;
	}

	public CodeContainer getCodeCtr() {
		return codeCtr;
	}

	public void setCodeCtr(CodeContainer codeCtr) {
		this.codeCtr = codeCtr;
	}

}
