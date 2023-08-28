package net.tenie.fx.main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.javafx.application.LauncherImpl;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.Log4jPrintStream;
import net.tenie.fx.Action.SettingKeyBinding;
import net.tenie.fx.component.UserAccount.UserAccountAction;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.component.container.AppWindowReStyleByWinOS;
import net.tenie.fx.factory.ServiceLoad;
import net.tenie.lib.db.h2.AppDao;
import net.tenie.sdkImp.SqluckyAppComponent;

/**
 * 启动入口
 * 
 * @author tenie
 *
 */
public class SQLucky extends Application {
	public static String sysOpenFile = "";
	public static List<String> argsList = new ArrayList<>();
	public static String userDir = "";
	public static Stage pStage;
	private AppWindow app;
	private Scene scene;
	private Image img;
	private String Theme;
//	private boolean tableExists = true;
//	public static volatile boolean beginInit = false;
	private boolean transferDB = false;
	private static Logger logger = LogManager.getLogger(SQLucky.class);

	private static boolean preloaderStatus = false;

	public static boolean isPreloaderStatus() {
		return preloaderStatus;
	}

	public static void setPreloaderStatus(boolean preloaderStatus) {
		SQLucky.preloaderStatus = preloaderStatus;
	}

	static {
		if (!CommonUtils.isDev()) {
			Log4jPrintStream.redirectSystemOut();
		}
	}

	@Override
	public void init() throws Exception {
//		while (beginInit == false) {
//			Thread.sleep(1000);
//		}
		logger.info(ConfigVal.textLogo);
		transferDB = AppDao.checkTransferDB();

		Connection conn = SqluckyAppDB.getConn();

		// 数据库迁移
		AppDao.testDbTableExists(conn);

		// 界面主题色， 没有设置过，默认黑色
		Theme = SqluckyAppDB.readConfig(conn, "THEME");
		if (StrUtils.isNullOrEmpty(Theme)) {
			SqluckyAppDB.saveConfig(conn, "THEME", "DARK");
			Theme = "DARK";
		}

		ConfigVal.openfileDir = SqluckyAppDB.readConfig(conn, "OPEN_FILE_DIR");
		SqluckyAppDB.closeConn(conn);
		ConfigVal.THEME = Theme;
		SqluckyAppComponent sqluckyComponent = new SqluckyAppComponent();
		ComponentGetter.appComponent = sqluckyComponent;
		// 注册
		ServiceLoad.callRegister();

		app = new AppWindow();
		img = ComponentGetter.LogoIcons; // new Image(SQLucky.class.getResourceAsStream(ConfigVal.appIcon));

		scene = app.getAppScene();

		CommonAction.setTheme(Theme);
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
			// windows 系统, 使用自己的关闭窗口
			if (!CommonUtils.isMacOS()) {
				AppWindowReStyleByWinOS winos = new AppWindowReStyleByWinOS();
				winos.setWindow(primaryStage, app.getHeadAnchorPane());
			}
//			else if(CommonUtility.isLinuxOS()) {
//				// 图标 
//				primaryStage.getIcons().add(img);
//				primaryStage.setTitle("SQLucky"); 
//			}else if(CommonUtility.isMacOS()) {
//				primaryStage.setTitle("SQLucky"); 
//			}
			primaryStage.getIcons().add(img);
			primaryStage.setTitle("SQLucky");

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
//						MyAreaTab.addCodeEmptyTabMethod();
						MyEditorSheetHelper.addEmptyHighLightingEditor();
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
			Long mm = Runtime.getRuntime().maxMemory() / 1024;
			mm = mm / 1024;
			logger.info("Runtime.getRuntime().maxMemory = " + mm);
			SettingKeyBinding.setEscKeyBinding(scene);

			// 数据迁移
			if (transferDB) {
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public static void main(String[] args) throws IOException {
		logger.debug("main.args ==  " + Arrays.toString(args));
		if (args != null && args.length > 0) {
			sysOpenFile = args[0];
		}

//		String dir = CommonUtility.sqluckyAppModsPath();
//		File file = new File(dir , "text");
//		System.out.println(file.getAbsolutePath());
//		file.createNewFile();

//		String  v = System.getProperty("jdk.module.path");
//		logger.debug("jdk.module.path = "+ v);
//		try {
//			String str = UnlimitedCryptoPoliciesCheck.getInfo();
//			logger.debug("UnlimitedCryptoPoliciesCheck ==  "+ str); 
//			System.setProperty("https.protocols", "TLSv1.2");
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}

//		Properties  ps = System.getProperties();
//		ps.forEach((o1, o2)->{
//			System.out.println(o1 + " | "+ o2);
//			
//		});

//		var val =System.getProperty("jdk.module.path");
//		var val =System.getProperty("jdk.module.upgrade.path");
//		var val =System.getProperty("jdk.module.main");
//		logger.debug("jdk.module.path ==  "+ Arrays.toString(val.split(":")));

		if (CommonUtils.isLinuxOS()) {
			LauncherImpl.launchApplication(SQLucky.class, MyPreloaderGif.class, args);
		} else {
			LauncherImpl.launchApplication(SQLucky.class, MyPreloaderMp4.class, args);
//			LauncherImpl.launchApplication(SQLucky.class, MyPreloaderGif.class, args);
//			LauncherImpl.launchApplication(SQLucky.class, MyPreloader.class, args);

		}

//		LauncherImpl.launchApplication(SQLucky.class, MyPreloaderGif.class, args);
	}
}
