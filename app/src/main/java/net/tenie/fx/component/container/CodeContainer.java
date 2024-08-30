package net.tenie.fx.component.container;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.DraggingTabPaneSupport;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.NotificationPane;

/*   @author tenie */
public class CodeContainer extends VBox{
//	private AnchorPane operateBtnPane;
	// 主代码框
	private TabPane mainTabPane;
	// 右侧代码框
	private TabPane rightTabPane;
	// 通知信息的面板容器, 一些提示信息, 会在这个容器顶部显示
	private NotificationPane notificationPane = new NotificationPane();

	// 多窗口代码框
	private MasterDetailPane tabPaneMasterDetailPane;


	public CodeContainer() {
		super();

		// 按钮面板
//		operateBtnPane = ButtonFactory.codeAreabtnInit();
		
		mainTabPane = new TabPane();
		rightTabPane = new TabPane();
		ComponentGetter.mainTabPane = mainTabPane;
		ComponentGetter.rightTabPane = rightTabPane;

		// 当焦点在rightTabPane 就给currentActiveTabPane赋值
		rightTabPane.focusWithinProperty().addListener((a,b,c)->{
			if(c){
				ComponentGetter.currentActiveTabPane = rightTabPane;
			}
		});
        // 当焦点在mainTabPane 就给currentActiveTabPane赋值
		mainTabPane.focusWithinProperty().addListener((a,b,c)->{
			if(c){
				ComponentGetter.currentActiveTabPane = mainTabPane;
			}
		});


		tabPaneMasterDetailPane  = new MasterDetailPane(Side.RIGHT);
		tabPaneMasterDetailPane.setShowDetailNode(true);
		tabPaneMasterDetailPane.setMasterNode(mainTabPane);
		tabPaneMasterDetailPane.setDetailNode(rightTabPane);
		tabPaneMasterDetailPane.setDividerPosition(0.3);
		ComponentGetter.rightTabPaneMasterDetailPane = tabPaneMasterDetailPane;

		// 将主面板放入到"通知面板"的容器中
//		notificationPane.setContent(mainTabPane);
		notificationPane.setContent(tabPaneMasterDetailPane);
		VBox.setVgrow(notificationPane, Priority.ALWAYS);
		// 配置 notificationPane 组件
		configNotificationPane();
		
		this.getChildren().add(notificationPane);
//		this.getChildren().addAll(operateBtnPane, notificationPane);

		// 当表被拖拽进入到code editor , 将表名插入到 光标处
		this.setOnDragEntered(e -> {
			String val = ComponentGetter.dragTreeItemName;// 被拖拽的节点名称
			if (StrUtils.isNotNullOrEmpty(val)) {
				MyCodeArea ca = MyEditorSheetHelper.getCodeArea();
                int start = 0;
                if (ca != null) {
                    start = ca.getAnchor();
					ca.insertText(start, " " + val);
					ca.requestFocus();
                }

			}

		});

		// tab 拖拽
		DraggingTabPaneSupport support1 = new DraggingTabPaneSupport();
		support1.addSupport(mainTabPane);
		
		
//		CommonUtils.fadeTransition(operateBtnPane, 1000);
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
	




	public TabPane getMainTabPane() {
		return mainTabPane;
	}

	public void setMainTabPane(TabPane mainTabPane) {
		this.mainTabPane = mainTabPane;
	}

}
