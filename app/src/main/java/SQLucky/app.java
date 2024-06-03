package SQLucky;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.Log4jPrintStream;
import net.tenie.fx.Action.SettingKeyBinding;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;
import net.tenie.fx.component.UserAccount.UserAccountAction;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.component.container.AppWindowReStyleByWinOS;
import net.tenie.fx.dao.ConnectionDao;
import net.tenie.fx.factory.ServiceLoad;
import net.tenie.fx.main.MyPreloaderGif;
import net.tenie.fx.main.MyPreloaderMp4;
import net.tenie.fx.main.Restart;
import net.tenie.lib.db.h2.AppDao;
import net.tenie.lib.db.h2.UpdateScript;
import net.tenie.sdkImp.SqluckyAppComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 启动入口
 *
 * @author tenie
 *
 */
public class app extends Application {
    public static String sysOpenFile = "";
    public static List<String> argsList = new ArrayList<>();
    public static String userDir = "";
    public static Stage pStage;
    private AppWindow app;
    private Scene scene;
    private Image img;
    private String Theme;
    private boolean transferDB = false;
    private static Logger logger = LogManager.getLogger(app.class);

    private static boolean preloaderStatus = false;

//    public static boolean isPreloaderStatus() {
//        return preloaderStatus;
//    }
//
//    public static void setPreloaderStatus(boolean preloaderStatus) {
//        preloaderStatus = preloaderStatus;
//    }

    static {
        if (!CommonUtils.isDev()) {
            Log4jPrintStream.redirectSystemOut();
        }
    }

    @Override
    public void init() throws Exception {
        logger.info(ConfigVal.textLogo);
        transferDB = AppDao.checkTransferDB();
        Connection conn = SqluckyAppDB.getConn();
        // 查看新表是否存在
        AppDao.testDbTableExists(conn, "AUTO_COMPLETE_TEXT");
        // 插入更新sql
        UpdateScript.insertNewSQL();
        // 执行需要更新的sql
        UpdateScript.executeAppendSql();
        // 界面主题色， 没有设置过，默认黑色
        Theme = SqluckyAppDB.readConfig(conn, "THEME");
        if (StrUtils.isNullOrEmpty(Theme)) {
            SqluckyAppDB.saveConfig(conn, "THEME", "DARK");
            Theme = "DARK";
        }
        // 读自动补全文本
        AppDao.readAllAutoCompleteText(conn);

        SqluckyAppDB.closeConn(conn);
        ConfigVal.THEME = Theme;
        SqluckyAppComponent sqluckyComponent = new SqluckyAppComponent();
        ComponentGetter.appComponent = sqluckyComponent;
        // 注册
        ServiceLoad.callRegister();

        app = new AppWindow();
        img = ComponentGetter.LogoIcons;

        scene = app.getAppScene();

        AppCommonAction.setTheme(Theme);
        // 加载插件
        ServiceLoad.callLoad();
        logger.info("完成初始化");

    }

    @Override
    public void start(Stage primaryStage) {
        ComponentGetter.SQLucky = this;
        try {
            pStage = primaryStage;
            ComponentGetter.primaryStage = primaryStage;

            primaryStage.setScene(scene);
//			var sceneRoot = scene.getRoot();
//			CommonUtility.fadeTransition(sceneRoot, 2000);
//			app.fadeTransition();

//			primaryStage.setIconified(true);
            // 确保全屏显示
//			primaryStage.setMaximized(false);
//			primaryStage.setResizable(false);

            primaryStage.setOnCloseRequest(CommonEventHandler.mainCloseEvent());
//			CommonAction.setTheme(Theme);

            primaryStage.centerOnScreen(); // 居中
//			primaryStage.initStyle(StageStyle.UNDECORATED);//设定窗口无边框
//		    primaryStage.setIconified(true); //最小化窗口，任务栏可见图标
            if (CommonUtils.isLinuxOS()) {
                MyPreloaderGif.hiden();
            } else {
                MyPreloaderMp4.hiden();
            }

//             if(CommonUtility.isLinuxOS()) {
//				// 图标
//				primaryStage.getIcons().add(img);
//				primaryStage.setTitle("SQLucky");
//			}else if(CommonUtility.isMacOS()) {
//				primaryStage.setTitle("SQLucky");
//			}
            primaryStage.getIcons().add(img);
            primaryStage.setTitle("SQLucky");
            // macos 系统, 使用自己的关闭窗口
            if (!CommonUtils.isMacOS()) {
                AppWindowReStyleByWinOS winos = new AppWindowReStyleByWinOS();
                winos.setWindow(primaryStage, app.getHeadAnchorPane());
            }
            primaryStage.show();

            // 在stage show之后 需要初始化的内容, 如: 外观, 事件
            Platform.runLater(() -> {
                if (CommonUtils.isLinuxOS()) {
//					primaryStage.setAlwaysOnTop(true);
                    primaryStage.toFront();
                }
                if (!primaryStage.isFocused()) {
                    primaryStage.toFront();
                }
                primaryStage.toFront();
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                primaryStage.setX(primaryScreenBounds.getMinX());
                primaryStage.setY(primaryScreenBounds.getMinY());
                primaryStage.setWidth(primaryScreenBounds.getWidth());
                primaryStage.setHeight(primaryScreenBounds.getHeight());

                // 双击添加新codearea
                var mainTabPane = ComponentGetter.mainTabPane;
                Node tabHeader = mainTabPane.lookup(".tab-header-area");
                tabHeader.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getClickCount() == 2) {
                        MouseButton button = mouseEvent.getButton();
                        if(button == MouseButton.PRIMARY) { // 左键点击
                            MyEditorSheetHelper.addEmptyHighLightingEditor();
                        }
                    }
                });

            });

            ServiceLoad.callShowed();
            // 界面完成初始化后, 执行的回调函数
            Consumer<String> cr = v -> {
                Platform.runLater(() -> {
                    // 移除loading...
                    LoadingAnimation.rmLoading(ComponentGetter.primarySceneRoot);
                    // 账号恢复
                    UserAccountAction.appLanuchInitAccount();
                });
            };
            // 执行页面初始化好只会要执行的任务
            CommonUtils.executeInitTask(cr);

            Platform.runLater(() -> {
                SettingKeyBinding.setEscKeyBinding(ComponentGetter.primaryStage.getScene());
            });

            // 数据迁移
            if (transferDB) {
                moveDbData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据迁移, 如果数据库版本比当前的版本新就提示迁移
     */
    private  void moveDbData(){
        // 如果发现有新的数据库, 插入
        Optional<File> oldFile = AppDao.appOldDbFiles();
        if (oldFile.isPresent()) {
            Platform.runLater(() -> {
                boolean tf = MyAlert.myConfirmationShowAndWait("发现旧版本数据, 是否迁移");
                if (tf) {
                    // 数据库迁移
                    LoadingAnimation.primarySceneRootLoadingAnimation("Migrating", v -> {
                        boolean succeed = false;
                        File file = oldFile.get();
                        try {
                            AppDao.transferOldDbData(file);
                            succeed = true;
                            // 成功后归档原数据库
                            String ftmpName = file.getName();
                            String archiveName = ftmpName.replace("_sqlite", "_archive");
                            File renameFile = new File(file.getParent(), archiveName);
                            file.renameTo(renameFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MyAlert.errorAlert("迁移出错了!");
                        }

                        if (succeed) {
                            Platform.runLater(() -> {
                                MyAlert.myConfirmation("完成迁移, 重启APP, 加载迁移数据, ok ? ", x -> Restart.reboot(),
                                        System.out::println);
                            });
                        }
                    });

                }
            });
        }

    }
    // 退出程序, 保存app状态
    public static void saveApplicationStatusInfo() {
        // 载入动画
        LoadingAnimation.loadingAnimation("Saving....", v -> {
            Connection H2conn = SqluckyAppDB.getConn();
            try {
                ConnectionDao.refreshConnOrder();
                TabPane mainTabPane = ComponentGetter.mainTabPane;
                int activateTabPane = mainTabPane.getSelectionModel().getSelectedIndex();
                var alltabs = mainTabPane.getTabs();
                for (int i = 0; i < alltabs.size(); i++) {
                    Tab tab = alltabs.get(i);
                    if (tab instanceof MyEditorSheet mtab) {
                        mtab.saveScriptPo(H2conn);
                        var spo = mtab.getDocumentPo();
                        // 将打开状态设置为1, 之后根据这个状态来恢复
                        if (spo != null && spo.getId() != null) {
                            String sql = mtab.getAreaText();
                            if (StrUtils.isNotNullOrEmpty(sql) && sql.trim().length() > 0) {
                                spo.setOpenStatus(1);
                                // 当前激活的编辑页面
                                if (activateTabPane == i) {
                                    spo.setIsActivate(1);
                                } else {
                                    spo.setIsActivate(0);
                                }
                            } else {
                                spo.setOpenStatus(0);
                                spo.setIsActivate(0);
                            }
                        }
                    }
                }

                // 删除 script tree view 中的空内容tab
                List<TreeItem<MyEditorSheet>> scriptList = ScriptTabTree.ScriptTreeView.getRoot().getChildren();
                int idx = 1;
                for (int i = 0; i < scriptList.size(); i++) {
                    TreeItem<MyEditorSheet> treeItem = scriptList.get(i);
                    MyEditorSheet myEditorSheet = treeItem.getValue();
                    DocumentPo documentPo = myEditorSheet.getDocumentPo();
                    String sqlTxt = documentPo.getText();
                    // 删除内容已经为空的历史记录
                    if (sqlTxt == null || sqlTxt.trim().isEmpty()) {
                        AppDao.deleteScriptArchive(H2conn, documentPo);
                    } else {
                        String fp = documentPo.getFileFullName();
                        if (StrUtils.isNullOrEmpty(fp)) {
                            String titleName = documentPo.getTitle().get();
                            if (StrUtils.isNullOrEmpty(titleName) || titleName.startsWith("Untitled_")) {
                                documentPo.setTitle("Untitled_" + idx + "*");
                                idx++;
                            }
                        }
                        AppDao.updateScriptArchive(H2conn, documentPo);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SqluckyAppDB.closeConn(H2conn);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        logger.debug("main.args ==  " + Arrays.toString(args));
        if (args != null && args.length > 0) {
            sysOpenFile = args[0];
        }
        // 加载字体
        Font.loadFont(app.class.getResourceAsStream("/css/MonaspaceArgonVarVF.ttf"), 14);

        if(CommonUtils.isDev()){
            launch(args);
        }else{
            if (CommonUtils.isLinuxOS()) {
                LauncherImpl.launchApplication(app.class, MyPreloaderGif.class, args);
            } else {
                LauncherImpl.launchApplication(app.class, MyPreloaderMp4.class, args);
//			LauncherImpl.launchApplication(SQLucky.class, MyPreloaderGif.class, args);
//			LauncherImpl.launchApplication(SQLucky.class, MyPreloader.class, args);

            }
        }
    }
}
