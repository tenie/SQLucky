package net.tenie.fx.component.container;

import org.controlsfx.control.NotificationPane;

import javafx.application.Platform;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.utility.DraggingTabPaneSupport;

/*   @author tenie */
public class CodeContainer {
	private VBox container;
	private AnchorPane operateBtnPane;
	private TabPane mainTabPane;
	private DraggingTabPaneSupport dtps = new DraggingTabPaneSupport();
	private NotificationPane notificationPane = new NotificationPane();
	public CodeContainer() {
		container = new VBox();
		operateBtnPane = ButtonFactory.codeAreabtnInit();
		
		mainTabPane = new TabPane();
		ComponentGetter.mainTabPane = mainTabPane;

		dtps.addSupport(mainTabPane);

//		VBox.setVgrow(mainTabPane, Priority.ALWAYS);
		notificationPane.setContent(mainTabPane); 
		VBox.setVgrow(notificationPane, Priority.ALWAYS);
		// 配置 notificationPane 组件
		configNotificationPane();
		
		container.getChildren().addAll(operateBtnPane, notificationPane);
//		container.getChildren().addAll(operateBtnPane, mainTabPane);

		// tab 拖拽
		DraggingTabPaneSupport support1 = new DraggingTabPaneSupport();
		support1.addSupport(mainTabPane);
		
		
		CommonUtility.fadeTransition(operateBtnPane, 1000); 
		CommonUtility.fadeTransition(mainTabPane, 1000); 
		
	}

	public void configNotificationPane() {
		ComponentGetter.notificationPane = notificationPane;
//	    notificationPane.setText("Hello World! Using the dark theme");
		notificationPane.setText(" ");
		notificationPane.setShowFromTop(true);
		notificationPane.setGraphic(IconGenerator.svgImage("info-circle", "#7CFC00"));
		
	    notificationPane.setOnShown(e->{
	    	new Thread(() -> { 
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					Platform.runLater(()->{
						notificationPane.hide();
					});
					
				 
			}).start();
	    	
	    });
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
