package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
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
