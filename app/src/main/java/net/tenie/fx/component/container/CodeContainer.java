package net.tenie.fx.component.container;

import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.utility.DraggingTabPaneSupport;

/*   @author tenie */
public class CodeContainer {
	private VBox container;
	private AnchorPane operateBtnPane;
	private TabPane mainTabPane;
	private DraggingTabPaneSupport dtps = new DraggingTabPaneSupport();

	public CodeContainer() {
		container = new VBox();
		operateBtnPane = ButtonFactory.codeAreabtnInit();
		
		mainTabPane = new TabPane();
		ComponentGetter.mainTabPane = mainTabPane;

		SqlEditor.myTabPane = mainTabPane;
		dtps.addSupport(mainTabPane);

		VBox.setVgrow(mainTabPane, Priority.ALWAYS);
		container.getChildren().addAll(operateBtnPane, mainTabPane);

		// tab 拖拽
		DraggingTabPaneSupport support1 = new DraggingTabPaneSupport();
		support1.addSupport(mainTabPane);
	}

	

	public VBox getContainer() {
		return container;
	}

	public void setContainer(VBox container) {
		this.container = container;
	}

	public AnchorPane getOperateBtnPane() {
		return operateBtnPane;
	}

	public void setOperateBtnPane(AnchorPane operateBtnPane) {
		this.operateBtnPane = operateBtnPane;
	}

	public TabPane getMainTabPane() {
		return mainTabPane;
	}

	public void setMainTabPane(TabPane mainTabPane) {
		this.mainTabPane = mainTabPane;
	}

}
