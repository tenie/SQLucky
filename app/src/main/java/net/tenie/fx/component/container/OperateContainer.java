package net.tenie.fx.component.container;

import org.controlsfx.control.MasterDetailPane;
import javafx.geometry.Side;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqlcukyEditor;

/*   @author tenie */
public class OperateContainer {
	private HBox container;
	private MasterDetailPane treeAreaDetailPane;
	private DBinfoContainer dbinfoCtr; // 连接管理窗口
	private CodeContainer codeCtr; // 代码编辑窗口

	public OperateContainer() {
		container = new HBox();
		
		codeCtr = new CodeContainer();
		dbinfoCtr = new DBinfoContainer();

//		SqlEditor.codeAreaRecover(); // 还原上次的sql代码

		treeAreaDetailPane = new MasterDetailPane(Side.LEFT);
		treeAreaDetailPane.setShowDetailNode(true);
		treeAreaDetailPane.setMasterNode(codeCtr.getContainer());
		treeAreaDetailPane.setDetailNode(dbinfoCtr.getContainer());
//		treeAreaDetailPane.setDividerPosition(0.22);

		container.getChildren().add(treeAreaDetailPane);

		HBox.setHgrow(treeAreaDetailPane, Priority.ALWAYS);
		ComponentGetter.treeAreaDetailPane = treeAreaDetailPane;
	}

	public HBox getContainer() {
		return container;
	}

	public void setContainer(HBox container) {
		this.container = container;
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
