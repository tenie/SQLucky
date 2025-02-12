package SQLucky;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.fx.component.container.AppWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TrayNotification {

    private static final Logger logger = LogManager.getLogger(TrayNotification.class);
    public static TrayIcon trayIcon;

    public static void setupSystemTray() {
        // 检查系统是否支持系统托盘
        if (!SystemTray.isSupported()) {
            logger.error("系统不支持系统托盘");
            return;
        }
        // 获取系统托盘
        SystemTray tray = SystemTray.getSystemTray();
        // 创建图标
        Image image = Toolkit.getDefaultToolkit().getImage(AppWindow.class.getResource(ConfigVal.appIcon));

        // 创建通知
        trayIcon = new TrayIcon(image, "Sqlucky");
        trayIcon.setImageAutoSize(true);
        // 显示窗口
        // 添加双击监听器以显示主窗口
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击
                    Platform.runLater(() -> {
                        var sg = ComponentGetter.primaryStage;
                        // 判断窗口是不是最小化状态
                        if (sg.iconifiedProperty().get()) {
                            sg.setIconified(false);
                            Platform.runLater(()->{
                                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                                sg.setX(primaryScreenBounds.getMinX());
                                sg.setY(primaryScreenBounds.getMinY());
                                sg.setWidth(primaryScreenBounds.getWidth());
                                sg.setHeight(primaryScreenBounds.getHeight());
                            });
                        }
                        sg.show();
                        sg.toFront();
                    });
                }
            }
        });

        // 注册右键菜单
        PopupMenu popup = new PopupMenu();

        MenuItem exitItem = new MenuItem("Close Sqlucky");
        exitItem.addActionListener(e -> app.saveApplicationStatusInfo());
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);

        try {
            // 添加托盘图标
            tray.add(trayIcon);
        } catch (AWTException e) {
            logger.error(e);
        }
    }

}
