package net.tenie.Sqlucky.sdk.utility;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.db.ProcedureFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author tenie
 */
public class CommonUtils {
    private static Logger logger = LogManager.getLogger(CommonUtils.class);
    private static ArrayBlockingQueue<Consumer<String>> queue = new ArrayBlockingQueue<>(1);

    public static String themeColor() {
        String color = "#1C94FF";
        if (ConfigVal.THEME.equals(CommonConst.THEME_YELLOW)) {
            color = "#FDA232";
        }
        return color;
    }

    // 加载css样式
    public static void loadCss(Scene scene) {
        scene.getStylesheets().clear();
        logger.info(ConfigVal.THEME);
        if (ConfigVal.THEME.equals(CommonConst.THEME_DARK)) {
            scene.getStylesheets().addAll(ConfigVal.cssList);
        } else if (ConfigVal.THEME.equals(CommonConst.THEME_LIGHT)) {
            scene.getStylesheets().addAll(ConfigVal.cssListLight);

        } else if (ConfigVal.THEME.equals(CommonConst.THEME_YELLOW)) {
            scene.getStylesheets().addAll(ConfigVal.cssListYellow);

        }

        // 加载自定义的css
        String path = FileUtils.getUserDirectoryPath() + "/.sqlucky/font-size.css";
        File cssf = new File(path);
        if (!cssf.exists()) {
            setFontSize(14);
        }
        String uri = Paths.get(path).toUri().toString();

        scene.getStylesheets().add(uri);

    }

    // 设置字符大小
    static public void setFontSize(int i) {
        String val = "/*" + i + "*/ \n" + ".myLineNumberlineno{ \n" + "	-fx-font-size :	" + i + "; \n" + "} \n"
                + ".code-area{\n" + "	-fx-font-size :	" + i + "; \n" + "} \n" + "";
        try {
            String path = FileUtils.getUserDirectoryPath() + "/.sqlucky/font-size.css";
            FileTools.saveByEncode(path, val, "UTF-8");
            // 加载自定义的css
            File cssf = new File(path);
            if (!cssf.exists()) {
                setFontSize(14);
            }
            String uri = Paths.get(path).toUri().toString();
            if (ComponentGetter.primaryscene.getStylesheets().contains(uri)) {
                ComponentGetter.primaryscene.getStylesheets().remove(uri);
            }
            Platform.runLater(() -> {
                ComponentGetter.primaryscene.getStylesheets().add(uri);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 延迟执行, 如果有任务在队列中, 会抛弃任务不执行
     *
     * @param caller
     * @param milliseconds
     */
    public static void delayRunThread(Consumer<String> caller, int milliseconds) {
        if (queue.isEmpty()) {
            queue.offer(caller); // 队列尾部插入元素, 如果队列满了, 返回false, 插入失败

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(milliseconds);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    var cl = queue.poll(); // 从队列取出一个元素
                    if (cl != null) {
                        cl.accept("");
                    }
                }
            };
            t.start();
        }
    }

    public static void runThread(Function<Object, Object> fun) {
        Thread t = new Thread() {
            @Override
            public void run() {
                fun.apply(null);
            }
        };
        t.start();
    }

    /**
     * 子线程执行任务
     * @param runObj
     */
    public static void runThread(Runnable runObj) {
        Thread t = new Thread(() -> runObj.run());
        t.start();
    }

    /**
     * 延迟执行
     * @param runObj
     * @param millis 毫秒
     */
    public static void delayRunThread(Runnable runObj, long millis) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            runObj.run();
        });
        t.start();
    }

    public static void runThread(Consumer<String> caller) {
        Thread t = new Thread() {
            @Override
            public void run() {
                caller.accept("");
            }
        };
        t.start();
    }
    public static void runThread(Consumer<String> caller, String val) {
        Thread t = new Thread() {
            @Override
            public void run() {
                caller.accept(val);
            }
        };
        t.start();
    }

    // 检测文件是否存在
    public static boolean checkFileExist(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            return true;
        }
        return false;
    }

    // 获取Tab 中的文本
    public static String tabText(Tab tb) {
        String title = tb.getText();
        if (StrUtils.isNullOrEmpty(title)) {
            Label lb = (Label) tb.getGraphic();
            if (lb != null)
                title = lb.getText();
            else
                title = "";
        }
        return title;
    }

    public static void setTabName(Tab tb, String val) {
        Label lb = (Label) tb.getGraphic();
        if (lb != null) {
            lb.setText(val);
            tb.setText("");
        } else {
            tb.setText(val);
        }
    }

    public static void setTabName(Tab tb, SimpleStringProperty val) {
        Label lb = (Label) tb.getGraphic();
        if (lb != null) {
            lb.textProperty().bind(val);
            tb.setText("");
        } else {
            tb.setText(val.getValue());
        }
    }

    // 判断数据库字段是否是数字类型
    public static boolean isNum(int type) {
        if (type == java.sql.Types.BIGINT || type == java.sql.Types.BIT || type == java.sql.Types.DECIMAL
                || type == java.sql.Types.DOUBLE || type == java.sql.Types.FLOAT || type == java.sql.Types.NUMERIC
                || type == java.sql.Types.REAL || type == java.sql.Types.TINYINT || type == java.sql.Types.SMALLINT
                || type == java.sql.Types.INTEGER) {
            return true;
        }
        return false;
    }

    public static boolean isInteger(int type) {
        if (type == java.sql.Types.BIGINT
                || type == java.sql.Types.BIT
                || type == java.sql.Types.REAL
                || type == java.sql.Types.TINYINT
                || type == java.sql.Types.SMALLINT
                || type == java.sql.Types.INTEGER) {
            return true;
        }
        return false;
    }

    public static boolean isFloat(int type) {
        if (type == java.sql.Types.DECIMAL
                || type == java.sql.Types.DOUBLE
                || type == java.sql.Types.FLOAT
                || type == java.sql.Types.NUMERIC) {
            return true;
        }
        return false;
    }



    public static boolean isDateAndDateTime(int type) {
        if (type == java.sql.Types.DATE || type == java.sql.Types.TIME || type == java.sql.Types.TIMESTAMP) {
            return true;
        }
        return false;
    }

    // 时间类型判断, 时分秒
    public static boolean isTime(int type) {
        if (type == java.sql.Types.TIME) {
            return true;
        }
        return false;
    }

    // 时间类型判断, 时分秒
    public static boolean isDateTime(int type) {
        if (type == java.sql.Types.TIMESTAMP) {
            return true;
        }
        return false;
    }

    // 时间类型判断, 没有时分秒
    public static boolean isDate(int type) {
        if (type == java.sql.Types.DATE) {
            return true;
        }
        return false;
    }

    // 字符串类型判断
    public static boolean isString(int type) {
        if (type == java.sql.Types.CHAR || type == java.sql.Types.VARCHAR || type == java.sql.Types.LONGVARCHAR
                || type == java.sql.Types.CLOB

        ) {
            return true;
        }
        return false;
    }

    public static void newStringPropertyChangeListener(StringProperty val, int dbtype) {
        ChangeListener<String> cl = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (CommonUtils.isNum(dbtype) && !StrUtils.isNumeric(newValue) && !"<null>".equals(newValue)) {
                    logger.info("newStringPropertyChangeListener() : newValue= " + newValue + "set fail");
                    Platform.runLater(() -> val.setValue(oldValue));
                }
            }
        };
        val.addListener(cl);
    }

    // 添加的新行数据, 保存后, 不允许再修改, (要修改刷新页面)
    public static void prohibitChangeListener(StringProperty val, String original) {
        ChangeListener<String> cl = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (original == null) {
                    if (newValue != null) {
                        Platform.runLater(() -> val.setValue(original));
                    }
                } else {
                    if (newValue != null && !newValue.equals(original)) {
                        Platform.runLater(() -> val.setValue(original));
                    }
                }

            }
        };
        val.addListener(cl);
    }

    // 剪贴板 赋值 (复制, 拷贝)
    public static void setClipboardVal(String val) {
        Platform.runLater(() -> {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent clipboardContent = new javafx.scene.input.ClipboardContent();
            clipboardContent.putString(val);
            clipboard.setContent(clipboardContent);
        });

    }

    // 获取剪贴板的值
    public static String getClipboardVal() {
        var cbd = Clipboard.getSystemClipboard();
        if (cbd != null && cbd.hasString()) {
            return cbd.getString();
        }
        return "";
    }

    public static boolean clipboardHasString() {
        var cbd = Clipboard.getSystemClipboard();
        if (cbd != null) {
            return cbd.hasString();
        }
        return false;
    }

    /**
     * 等待正在执行的Platform队列执行完毕 在 Platform.runLater中放入一个计数器, 等执行到这个计数器完成说明在它前面的任务已经完成了
     */
    public static void platformAwait() {
        threadAwait(1);
    }

    /**
     * 线程阻塞函数, 使用sleep实现
     * 使用计数器, 在子线程里sleep后, 计数器-1
     * 在子线程外面等待计数器为0, 实现在线程等待
     * @param second
     */
    public static void threadAwait(int second) {
        // 设置一次计数
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 子线程中睡眠和减计数
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    // 在子线程里 睡眠
                    Thread.sleep(second * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 睡眠完成后 计数减一
                    countDownLatch.countDown();
                }
            }
        };
        t.start();

        try {
            // countDownLatch 如果不为0 会在这阻塞.
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int getMax(int[] arr) {
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        logger.debug(max);
        return max;
    }

    public static int getMin(int[] arr) {
        int min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        logger.debug(min);
        return min;
    }

    /**
     * node 进行旋转的动画, 设置旋转9999次
     *
     * @param pointer
     */
    public static void rotateTransition(Node pointer) {
        // 播放持续时间
        double play_time = 3.0;
        // 开始角度
        double fromAngle = 0.0;
        // 结束角度
        double toAngle = 720.0;
        // 根据旋转角度大小计算动画播放持续时间
//        play_time =Math.abs(toAngle-fromAngle)*0.05;
        // Duration.seconds(3)设置动画持续时间
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(play_time), pointer);
        // 设置旋转角度
        rotateTransition.setFromAngle(fromAngle);
        rotateTransition.setToAngle(toAngle);
        // 只播放次数
        rotateTransition.setCycleCount(9999);
        // 每次旋转后是否改变旋转方向
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
    }

    /**
     * 添加显示动画, 动画有问题先注释了
     *
     * @param node
     * @param ms
     */
    public static void fadeTransition(Node node, double ms) {
        // 从下面语句创建一个淡入淡出效果对象并设置持续事件为2S
//        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ms));
//        fadeTransition.setFromValue(0.0);   // 设置起始透明度为1.0，表示不透明
//        fadeTransition.setToValue(1.0);     // 设置结束透明度为0.0，表示透明
//        fadeTransition.setCycleCount(1);     // 设置循环周期为无限
//        fadeTransition.setAutoReverse(true);    // 设置自动反转
//        fadeTransition.setNode(node);         // 设置动画应用的节点
//        fadeTransition.play();                  // 播放动画 
    }

    // 对节点实验隐藏动画, 动画有问题, 先注释了
    public static FadeTransition fadeTransitionHidden(Node node, double ms) {
        // 从下面语句创建一个淡入淡出效果对象并设置持续事件为2S
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ms));
//     fadeTransition.setFromValue(1);   // 设置起始透明度为1.0，表示不透明
//     fadeTransition.setToValue(0);     // 设置结束透明度为0.0，表示透明
//     fadeTransition.setCycleCount(1);     // 设置循环周期为无限
//     fadeTransition.setAutoReverse(true);    // 设置自动反转
//     fadeTransition.setNode(node);         // 设置动画应用的节点
//     fadeTransition.play();                  // 播放动画 
        return fadeTransition;
    }

    // 对节点实验隐藏动画, 动画有问题, 先注释了
    public static FadeTransition fadeTransitionHidden(Node node, double ms, double val) {
        // 从下面语句创建一个淡入淡出效果对象并设置持续事件为2S
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ms));
//		  fadeTransition.setFromValue(1);   // 设置起始透明度为1.0，表示不透明
//		  fadeTransition.setToValue(val);     // 设置结束透明度为0.0，表示透明
//		  fadeTransition.setCycleCount(1);     // 设置循环周期为无限
//		  fadeTransition.setAutoReverse(true);    // 设置自动反转
//		  fadeTransition.setNode(node);         // 设置动画应用的节点
//		  fadeTransition.play();                  // 播放动画 
        return fadeTransition;
    }

    public static boolean isMacOS() {
        String os_name = System.getProperty("os.name");
        if (os_name.toLowerCase().startsWith("mac")) {
            return true;
        }
        return false;
    }

    public static boolean isWinOS() {
        String os_name = System.getProperty("os.name");
        if (os_name.toLowerCase().startsWith("win")) {
            return true;
        }
        return false;
    }

    public static boolean isLinuxOS() {
        String os_name = System.getProperty("os.name");
        if (os_name.toLowerCase().startsWith("linux")) {
            return true;
        }
        return false;
    }

    public static String osName() {
        String os_name = System.getProperty("os.name");
        if (os_name.toLowerCase().startsWith("linux")) {
            return "linux";
        } else if (os_name.toLowerCase().startsWith("win")) {
            return "win";
        } else if (os_name.toLowerCase().startsWith("mac")) {
            return "mac";
        }
        return "";
    }

    // 给控件加样式
    public static void addCssClass(Node nd, String css) {
        nd.getStyleClass().add(css);
    }

    // 打开系统的文件窗口
    public static void openExplorer(File file) {
        EventQueue.invokeLater(() -> {
            try {
                if (file != null && file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    MyAlert.errorAlert("File Not Exists ");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    //
    public static String readFileText(File file, String encode) {
        String val = "";
        try {
            if (!file.exists())
                return val;
            val = FileUtils.readFileToString(file, encode);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return val;
    }

    // 用在存储过程, 第一个括号内的字符串,
    public static String firstParenthesisInsideString(String text) {
        // 括号开始的位置, 不包括括号自己
        int begin = text.indexOf("(") + 1;
        int end = findBeginParenthesisRange(text, begin, "(", ")");
        String str = text.substring(begin, end);
        return str;
    }

    // 获取 IN 字段
    public static List<String> findInField(String sql) {
        String pstr = firstParenthesisInsideString(sql);
        List<String> list = new ArrayList<>();
        String[] sarr = pstr.split(",");
        for (String str : sarr) {
            str = str.trim();
            if (str.length() > 0) {
                int idx = str.toUpperCase().indexOf("IN");
                if (idx == 0) {
                    list.add(str);
                }
            }
        }
        return list;
    }

    // 判断是否是没有参数的存储过程, 没有参数 返回true
    public static boolean procedureIsNoParameter(String sqlddl) {
        if (sqlddl.indexOf("(") > -1) {
            String tmp = sqlddl.substring(0, sqlddl.indexOf("("));
            if (tmp.contains(" BEGIN ")) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    // TODO 从存储过程语句中提取参数
    public static List<ProcedureFieldPo> getProcedureFields(String ddl) {
        List<ProcedureFieldPo> rs = new ArrayList<>();
//        ddl = StrUtils.multiLineCommentToSpace(ddl);
//        ddl = StrUtils.trimCommentToSpace(ddl, "--");
        ddl = StrUtils.replaceAllCommentToSpace(ddl);
        // 给ddl分词, 找到过程名称后面的参数列表
        ddl = StrUtils.pressString(ddl).toUpperCase();
        if (procedureIsNoParameter(ddl)) { // 没有参数直接返回
            return rs;
        }

        String val = firstParenthesisInsideString(ddl);
        val = val != null ? val.trim() : "";
        if (val.length() > 1) {
            String args[] = val.split(",");
            for (int i = 0; i < args.length; i++) {
                String str = args[i].trim();

                String fields[] = str.split(" ");
                String inout = fields[0].toUpperCase();
                boolean in = inout.contains("IN");
                boolean out = inout.contains("OUT");

                ProcedureFieldPo po = new ProcedureFieldPo();
                po.setName(str);
                po.setIn(in);
                po.setOut(out);
                rs.add(po);
            }
        }
        logger.debug(rs);
        return rs;
    }

    // 根据括号( 寻找配对的 结束)括号所在的位置.
    public static int findBeginParenthesisRange(String text, int start, String pb, String pe) {
        String startStr = text.substring(start);
        int end = 0;
        int strSz = startStr.length();
        if (strSz == 0)
            return end;
        if (!startStr.contains(pe))
            return end;
        int idx = 1;
        for (int i = 0; i < startStr.length(); i++) {
            if (idx == 0)
                break;
            String tmp = startStr.substring(i, i + 1);

            if (pe.equals(tmp)) {
                idx--;
                end = i;
            } else if (pb.equals(tmp)) {
                idx++;
            }
        }
        return start + end;
    }

    // 鼠标等待
    public static void setCursor(Cursor cursorVal) {
        Platform.runLater(() -> {
            ComponentGetter.primaryscene.addEventHandler(MouseEvent.ANY, e -> {
                ComponentGetter.primaryscene.setCursor(cursorVal);
            });
        });

    }

    // 应用创建完后, 执行一些初始化的任务
    private static List<Consumer<String>> initTasks = new ArrayList<>();
    private static volatile int tasksCount = 0;

    private static synchronized int getTaskCount() {
        return tasksCount;
    }

    private static synchronized int addTaskCount() {
        return tasksCount++;
    }

    private static synchronized int minusTaskCount() {
        return tasksCount--;
    }

    public static void addInitTask(Consumer<String> v) {
        initTasks.add(v);
        addTaskCount();
        logger.debug("addTaskCount == getTaskCount()  = " + CommonUtils.getTaskCount());

    }

    public static synchronized int countTask() {
        return initTasks.size();
    }

    // 子线程执行初始化任务
    public static void executeInitTask(Consumer<String> Callback) {
        for (Consumer<String> caller : initTasks) {
            try {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            caller.accept("");
                        } finally {
                            minusTaskCount();
                            logger.debug("minusTaskCount == getTaskCount()  = " + CommonUtils.getTaskCount());
                        }
                    }
                };
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        InitFinishCall(Callback);

    }

    /**
     * 初始化完成后的回调函数
     *
     * @param caller
     */
    public static void InitFinishCall(Consumer<String> caller) {
        try {
            Thread t = new Thread() {
                @Override
                public void run() {
                    while (CommonUtils.getTaskCount() > 0) {
                        try {
                            Thread.sleep(500);
                            logger.debug("getTaskCount()  = " + CommonUtils.getTaskCount());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    caller.accept("");
                }
            };
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 组件的悬停提示
    public static Tooltip instanceTooltip(String msg) {
        Tooltip tt = new Tooltip(msg);
        tt.setShowDelay(new Duration(100));
        return tt;
    }

    // 读选择系统中的文件读取为字符串 UTF-8
    public static String openFileReadToString(String encode) {
        File f = FileOrDirectoryChooser.showOpenSqlFile("Open", ComponentGetter.primaryStage);
        if (f == null)
            return "";
        String val = "";
        try {
            val = FileUtils.readFileToString(f, encode);
        } catch (IOException e) {
            e.printStackTrace();
            MyAlert.errorAlert(e.getMessage());
        }
        return val;
    }

    public static int RandomInt() {
        Random r = new Random();
        return r.nextInt();
    }

    public static Long dateTime() {
        Date d = new Date();
        return d.getTime();
    }

    public static File getFileHelper(boolean isFile) {
        File file = null;
        if (isFile) {
            file = FileOrDirectoryChooser.showSaveDefault("Save", new Stage());
        }
        return file;
    }

    /**
     * 创建文件事, 让用户现在文件的绝对路径和文件名
     */
    public static File getFilePathHelper(String fileType) {
        FileChooser fileChooser = new FileChooser();
        File dir = FileOrDirectoryChooser.getOpenfileDir();

        fileChooser.setInitialDirectory(dir);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(fileType, "*." + fileType));
        File file = fileChooser.showSaveDialog(new Stage());
        return file;

    }

    /**
     * 选择文件
     *
     * @return
     */
    public static File getFileHelper() {
        File file = FileOrDirectoryChooser.showOpenAllFile("Select File", ComponentGetter.primaryStage);
        return file;
    }


    // 字段值被修改还原, 不允许修改
    public static StringProperty createReadOnlyStringProperty(String val) {
        StringProperty sp = new StringProperty() {
            @Override
            public String get() {
                return val;
            }

            @Override
            public void bind(ObservableValue<? extends String> arg0) {
            }

            @Override
            public boolean isBound() {
                return false;
            }

            @Override
            public void unbind() {
            }

            @Override
            public Object getBean() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void addListener(ChangeListener<? super String> arg0) {
            }

            @Override
            public void removeListener(ChangeListener<? super String> arg0) {
            }

            @Override
            public void addListener(InvalidationListener arg0) {
            }

            @Override
            public void removeListener(InvalidationListener arg0) {
            }

            @Override
            public void set(String arg0) {
            }
        };
        return sp;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 左侧添加隐藏操作按钮/查询框
    public static void leftHideOrShowSecondOperateBox(Pane container, Node box, List<Node> btnList) {
        if (container.getChildren().contains(box)) {
            container.getChildren().remove(box);
            for (var btn : btnList) {
                btn.setDisable(false);
            }
        } else {
            container.getChildren().add(1, box);
            for (var btn : btnList) {
                btn.setDisable(true);
            }
        }

    }

    // 左侧添加隐藏操作按钮/查询框
    public static void leftHideOrShowSecondOperateBox(Pane container, Node box, TextField txt) {
        if (container.getChildren().contains(box)) {
            txt.clear();
            container.getChildren().remove(box);

        } else {
            container.getChildren().add(1, box);
            if (txt != null) {
                txt.requestFocus();
            }

        }
    }

    public static void leftHideSecondOperateBox(Pane container, Node box) {
        if (container.getChildren().contains(box)) {
            container.getChildren().remove(box);
        }

    }


    // 左侧折叠面板的图标切换
    public static void leftPaneChangeIcon(TitledPane NotePane, StackPane Node, Region icon, Region uaicon) {
        if (NotePane.isExpanded()) {
            Node.getChildren().add(icon);
            NotePane.getStyleClass().add("myTitleTxtActiveColor");
        } else {
            Node.getChildren().add(uaicon);
            NotePane.getStyleClass().remove("myTitleTxtActiveColor");
        }
    }

    public static void setLeftPaneIcon(TitledPane NotePane, Region icon, Region uaicon) {
        // 图标切换
        final StackPane Node = (StackPane) NotePane.lookup(".arrow-button");
        Node.getChildren().clear();
        CommonUtils.leftPaneChangeIcon(NotePane, Node, icon, uaicon);
        // 监听展开状态
        NotePane.expandedProperty().addListener((obs, ov, nv) -> {
            Node.getChildren().clear();
            NotePane.getStyleClass().remove("myTitleTxtActiveColor");
            if (nv) {
                Node.getChildren().add(icon);
                NotePane.getStyleClass().add("myTitleTxtActiveColor");
            } else {
                Node.getChildren().add(uaicon);
//				NotePane.getStyleClass().remove("myTitleTxtActiveColor");
            }
        });

        var title = NotePane.lookup(".title");
        // 鼠标进入后， 切换图标
        title.setOnMouseEntered(e -> {
            Node.getChildren().clear();
            NotePane.getStyleClass().remove("myTitleTxtActiveColor");
            NotePane.getStyleClass().add("myTitleTxtActiveColor");
            if (NotePane.isExpanded()) {
                Node.getChildren().add(ComponentGetter.iconLeft);
            } else {
                Node.getChildren().add(ComponentGetter.iconRight);
            }

        });
        // 鼠标离开后使用原来的图标
        title.setOnMouseExited(e -> {
            Node.getChildren().clear();
            NotePane.getStyleClass().remove("myTitleTxtActiveColor");
            CommonUtils.leftPaneChangeIcon(NotePane, Node, icon, uaicon);
        });

    }


    // 在浏览器中打开 URL
    public static void OpenURLInBrowser(String url) {
        Application application = ComponentGetter.SQLucky;
        HostServices host = application.getHostServices();
        host.showDocument(url);
    }

    // 获取运行时app的可以执行程序(文件)
    public static String sqluckyAppPath() {
        String os_name = System.getProperty("os.name");
        String java_home = System.getProperty("java.home");
        logger.info("os_name = " + os_name);
        logger.info("java_home = " + java_home);
        String app = "";
        if (os_name.toLowerCase().startsWith("win")) {
            app = java_home.replace("runtime", "SQLucky.exe");
            logger.info("win app = " + app);
        } else if (os_name.toLowerCase().startsWith("mac")) {
            app = java_home.replace("runtime/Contents/Home", "MacOS/SQLucky");
            logger.info("mac app = " + app);
        } else if (os_name.toLowerCase().startsWith("linux")) {
            app = java_home.replace("lib/runtime", "bin/SQLucky");
            logger.info(" linux app = " + app);
        }

        return app;
    }

    public static String getFirstJdkModulePath() {
        String modulePath = System.getProperty("jdk.module.path");
        String strSplit = ":";
        if (CommonUtils.isWinOS()) {
            strSplit = ";";
        }
        String[] ls = modulePath.split(strSplit);
        return ls[0];
    }

    // 运行时判断是不是开发环境
    public static boolean isDev() {
        String modulePath = System.getProperty("jdk.module.path");
        String strSplit = ":";
        if (CommonUtils.isWinOS()) {
            strSplit = ";";
        }
        String[] ls = modulePath.split(strSplit);
        if (ls.length > 1) {
            return true;
        }
        return false;
    }

    public static String sqluckyWorkDirPath() {
        String dir = "/.sqlucky/";
        if (CommonUtils.isDev()) {
            dir = "/.sqlucky_dev/";
        }
        String path = FileUtils.getUserDirectoryPath() + dir;
        File file = new File(path);
        if (file.exists() == false) {
            file.mkdir();
        }
        return path;
    }

    // 获取运行时app的模块路径
    public static String sqluckyAppPluginModsPath() {
        String path = sqluckyWorkDirPath() + "plugins";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    // 判断是否登过, 没有就跳出登入窗口
    public static boolean isLogin(String title) {
        // 登入校验
        if (ConfigVal.SQLUCKY_LOGIN_STATUS.get() == false) {
            Platform.runLater(() -> {
                AppComponent app = ComponentGetter.appComponent;
                app.showSingInWindow(title);
            });

            return false;
        }
        return true;
    }


    // 键盘ESC按下后: 查找表的输入框清空, 选中的文本取消选中, 查找替换面板关闭
    public static void pressBtnESC() {
        ComponentGetter.dbInfoFilter.setText("");

        // 代码编辑内容, 取消选中, 高亮恢复复原
        MyEditorSheetHelper.deselect();
        MyEditorSheetHelper.applyHighlighting();

        // 隐藏查找, 替换窗口
        hideFindReplaceWindow();
        // 提示窗口
        MyEditorSheetHelper.getActivationEditorSheet().getSqluckyEditor().hideAutoComplete();
    }

    // 隐藏查找, 替换窗口
    public static void hideFindReplaceWindow() {
        if (!FinderAction.hideMainTabPaneFindReplace()) {
            if (!FinderAction.hideDataTabPaneFindReplace()) {
                if (!FinderAction.hideTitledPaneFind()) {
                    FinderAction.hideTitledPane();
                }
            }

        }
    }

    /**
     * 递归判断是否是参数的组件子节点是焦点对象
     */
    public static boolean isChildFocused(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node.isFocused()) {
                return true;
            } else if (node instanceof Parent subParent) {
                if (isChildFocused(subParent)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取焦点对象
     *
     * @param parent
     * @return
     */
    public static Node getFocusedChildNode(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node.isFocused()) {
                return node;
            } else if (node instanceof Parent) {
                var subNode = getFocusedChildNode((Parent) node);
                if (subNode != null) {
                    return subNode;
                }
            }
        }
        return null;
    }


    public static void showFind(boolean isReplace, String findStr) {
        boolean isShow = FinderAction.showMainTabPaneFindReplace(isReplace, findStr);
        if (!isShow) {
            isShow = FinderAction.showDataTabPaneFindReplace(isReplace, findStr);
            if (!isShow) {
                isShow = FinderAction.showTitledPaneFind("");
                if (!isShow) {
                    FinderAction.showTitledPane("");
                }
            }
        }
    }


    // 查找替换
    public static void findReplace(boolean isReplace, String findStr, MyEditorSheet sheet) {
        sheet.getSqluckyEditor().getCodeArea().showFindReplaceTextBox(isReplace, findStr);
    }

    // 查找替换, 当前的编辑器
    public static void findReplaceByCurrentEditer(boolean isReplace) {
        var sheet = MyEditorSheetHelper.getActivationEditorSheet();
        findReplace(isReplace, "", sheet);
    }

    // 获取当前连接下拉选中的连接名称
    public static String getComboBoxDbConnName() {
        var v = ComponentGetter.connComboBox.getValue();
        if (v != null) {
            String connboxVal = ComponentGetter.connComboBox.getValue().getText();
            return connboxVal;
        }

        return null;
    }

    // 获取当前连接下拉选值的对应连接对象
    public static SqluckyConnector getDbConnectionPoByComboBoxDbConnName() {
        var name = getComboBoxDbConnName();
        if (StrUtils.isNotNullOrEmpty(name)) {
            SqluckyConnector dpov = DBConns.get(name);
            return dpov;
        }
        return null;
    }

    public static String dbTypeToJavaType(SheetFieldPo field) {
        int type = field.getColumnType().get();
        String val = "";
        if (CommonUtils.isDateAndDateTime(type)) {
            val = "Date";
        } else if (type == java.sql.Types.BIGINT) {
            val = "Long";
        } else if (type == java.sql.Types.BIT) {
            val = "Boolean";
        } else if (type == java.sql.Types.DECIMAL || type == java.sql.Types.NUMERIC) {

            if (field.getScale() != null && field.getScale().get() > 0) {
                val = "Double";
            } else {
                val = "Long";
            }

        } else if (type == java.sql.Types.DOUBLE || type == java.sql.Types.FLOAT || type == java.sql.Types.REAL) {
            val = "Double";
        } else if (type == java.sql.Types.TINYINT || type == java.sql.Types.SMALLINT
                || type == java.sql.Types.INTEGER) {
            val = "Integer";
        } else {
            //  CHAR、 VARCHAR 和 LONGVARCHAR
            val = "String";
        }

        return val;
    }
}
