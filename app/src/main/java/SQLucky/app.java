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
import net.tenie.Sqlucky.sdk.component.SdkComponent;
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
import net.tenie.fx.component.container.AppHeadContainer;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.component.container.AppWindowReStyleByWinOS;
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
    private AppWindow app;
    private Scene scene;
    public static Image img;
    private String Theme;
    private boolean transferDB = false;
    private static Logger logger = LogManager.getLogger(app.class);

    private static boolean preloaderStatus = false;

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

    // 初始化一个Stage
    public static Stage initStage(Scene scene) {
        Stage primaryStage =new Stage();
        AppHeadContainer.SQLuckyStage = primaryStage;
        ComponentGetter.primaryStage = primaryStage;
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(CommonEventHandler.mainCloseEvent());
        primaryStage.centerOnScreen(); // 居中
        primaryStage.getIcons().add(img);
        primaryStage.setTitle("SQLucky");
        if (!CommonUtils.isMacOS()) {
            AppWindowReStyleByWinOS winos = new AppWindowReStyleByWinOS();
            // 添加关闭按钮
            AppWindow.appHeadContainer.addHiddenWindowResizeClose();
            try {
                winos.setupWindow(primaryStage, AppWindow.appHeadContainer.getHeadHbox());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
        });

        return primaryStage;
    }
    // 销毁 Stage
    public static void destroyStage(Stage stage){
        if(stage != null){
            stage.setScene(null);
            stage.setOnCloseRequest(null);
            stage.getIcons().clear();
            stage.setTitle(null);
            stage.close();
        }
    }

    @Override
    public void start(Stage pStage) {
        pStage.close();
        ComponentGetter.SQLucky = this;

        try {
            initStage(scene);
            if (CommonUtils.isLinuxOS()) {
                MyPreloaderGif.hiden();
            } else {
                MyPreloaderMp4.hiden();
            }

            Platform.runLater(() -> {
                 ServiceLoad.callShowed();

            // 界面完成初始化后, 执行的回调函数
            Consumer<String> cr = v -> {
                Platform.runLater(() -> {
                    // 移除loading...
                    LoadingAnimation.rmLoading(ComponentGetter.primarySceneRoot);
                    // 账号恢复
                    UserAccountAction.appLanuchInitAccount();
                    // 设置快捷键
                    Platform.runLater(() -> {
                        SettingKeyBinding.setEscKeyBinding(ComponentGetter.primaryStage.getScene());
                    });
                });

                Platform.runLater(()->{
                    // 双击添加新codearea
                    var mainTabPane = ComponentGetter.mainTabPane;
                    cleckDoubleAddTab(mainTabPane);
                    var rightTabPane = ComponentGetter.rightTabPane;
                    cleckDoubleAddTab(rightTabPane);
                    // 右侧代码框, 显示或隐藏
                    Platform.runLater(SdkComponent::intiShowOrhideRightByTabSize);

                });

            };
            // 执行页面初始化好只会要执行的任务
            CommonUtils.executeInitTask(cr);
            });
            // 数据迁移
            if (transferDB) {
                moveDbData();
            }

            // 界面获取到焦点检查文件是不被修改提示加载修改的内容
//            primaryStage.focusedProperty().addListener((a,b,c)->{
//                if(c){
//                    MyEditorSheet mes =   MyEditorSheetHelper.getActivationEditorSheet();
//                    if (mes != null) {
//                        //原文被其他程序修改后, 重新加载
//                        mes.reloadText();
//                    }
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // TabPane 头部 双击添加新Tab
    private static void cleckDoubleAddTab(TabPane tabPane){
        Node tabHeader = tabPane.lookup(".tab-header-area");
        if(tabHeader == null ){
            return;
        }
        tabHeader.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                MouseButton button = mouseEvent.getButton();
                if(button == MouseButton.PRIMARY) { // 左键点击
                    MyEditorSheetHelper.addEmptyHighLightingEditor();
                }
            }
        });
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
        if(ComponentGetter.dockSideTabPaneWindow != null ){
            ComponentGetter.dockSideTabPaneWindow.close();
        }
        // 载入动画
        LoadingAnimation.loadingAnimation("Saving....", v -> {
            Connection H2conn = SqluckyAppDB.getConn();
            try {
                // 更新数据库链接节点的顺序, 在拖动的时候直接保存
//                ConnectionDao.refreshConnOrder();

                TabPane mainTabPane = ComponentGetter.mainTabPane;
                TabPane rightTabPane = ComponentGetter.rightTabPane;
                int activateTabPane = mainTabPane.getSelectionModel().getSelectedIndex();
                var alltabs = mainTabPane.getTabs();
                if( !rightTabPane.getTabs().isEmpty()){
                    alltabs.addAll(rightTabPane.getTabs());
                }

                for (int i = 0; i < alltabs.size(); i++) {
                    Tab tab = alltabs.get(i);
                    if (tab instanceof MyEditorSheet mtab) {
                        // 文本保存到数据库
                        mtab.saveScriptPo(H2conn);

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
                        String fp = documentPo.getExistFileFullName();
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
                // 提示是否保存
                if(! MyEditorSheet.ConsumerLs.isEmpty()){
                    Platform.runLater(()->{
                        for (var call :  MyEditorSheet.ConsumerLs){
                            call.accept("");
                        }
                        SqluckyAppDB.closeConn(H2conn);
                        System.exit(0);
                    });

                }else {
                    SqluckyAppDB.closeConn(H2conn);
                    System.exit(0);
                }
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
