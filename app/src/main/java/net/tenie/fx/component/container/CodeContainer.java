package net.tenie.fx.component.container;

import org.controlsfx.control.NotificationPane;

import javafx.application.Platform;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.DraggingTabPaneSupport;
import net.tenie.fx.factory.ButtonFactory;

/*   @author tenie */
public class CodeContainer extends VBox{
	private AnchorPane operateBtnPane;
	private TabPane mainTabPane;
	// 通知信息的面板容器, 一些提示信息, 会在这个容器顶部显示
	private NotificationPane notificationPane = new NotificationPane();
	public CodeContainer() {
		super();
		operateBtnPane = ButtonFactory.codeAreabtnInit();
		
		mainTabPane = new TabPane();
		ComponentGetter.mainTabPane = mainTabPane;

		// 将主面板放入到"通知面板"的容器中
		notificationPane.setContent(mainTabPane); 
		VBox.setVgrow(notificationPane, Priority.ALWAYS);
		// 配置 notificationPane 组件
		configNotificationPane();
		
		this.getChildren().addAll(operateBtnPane, notificationPane);

		// tab 拖拽
		DraggingTabPaneSupport support1 = new DraggingTabPaneSupport();
		support1.addSupport(mainTabPane);
		
		
		CommonUtils.fadeTransition(operateBtnPane, 1000); 
		CommonUtils.fadeTransition(mainTabPane, 1000); 
		
	}

	// 顶部提示(通知)信息框的配置
	public void configNotificationPane() {
		ComponentGetter.notificationPane = notificationPane;
		notificationPane.setText(" ");
		notificationPane.setShowFromTop(true);
		notificationPane.setGraphic(IconGenerator.svgImage("info-circle", "#7CFC00"));
		
		notificationPane.setOnShown(e -> {
			new Thread(() -> {
				try {
					Thread.sleep(3000); // 延迟3秒后隐藏
					Platform.runLater(() -> {
						notificationPane.hide();
					});

				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

			}).start();

		});
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
