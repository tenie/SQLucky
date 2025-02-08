package net.tenie.fx.component.container;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.DataViewContainer;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import org.controlsfx.control.MasterDetailPane;

import java.util.Objects;

/*   @author tenie */
public class AppWindow extends VBox {
    public static AppHeadContainer appHeadContainer;
    private MasterDetailPane masterDetailPane;
    // 操作区域, 数据库链接, sql编辑
    private OperateContainer operateContainer;

    // 数据展示区域
    public static DataViewContainer dataViewContainer;
    private Scene appScene;
    private StackPane root;

    // 全局组件
    public static TreeView<TreeNodePo> treeView;

    public AppWindow() {
        super();
        CommonUtils.addCssClass(this, "main-background");
        ComponentGetter.mainWindow = this;

        masterDetailPane = new MasterDetailPane(Side.BOTTOM);
        //  操作容器, 左侧窗口, 代码编辑窗口
        operateContainer = new OperateContainer();
        // 下面窗口
        dataViewContainer = new DataViewContainer();

        masterDetailPane.getStyleClass().addFirst("masterDetailPane");
        masterDetailPane.setShowDetailNode(false);
        masterDetailPane.setMasterNode(operateContainer);
        masterDetailPane.setDetailNode(dataViewContainer);
        masterDetailPane.setDividerPosition(0.6);
        VBox.setVgrow(masterDetailPane, Priority.ALWAYS);

        ComponentGetter.masterDetailPane = masterDetailPane;
        // 设置tree 面板的显示比例
        masterDetailPane.widthProperty().addListener((ob, ov, nv) -> {
            if (nv.doubleValue() > 1) {
                double wi = ComponentGetter.masterDetailPane.getWidth();
                double tbp = 300.0;
                double val = tbp / wi;
                ComponentGetter.treeAreaDetailPane.setDividerPosition(val);
            }
        });
        root = new StackPane(this);
        appScene = new Scene(root);
        ComponentGetter.primaryscene = appScene;
        ComponentGetter.primarySceneRoot = root;


//		headAnchorPane.getStyleClass().add("window-head-pane");
        // 顶部容器, 菜单, 操作按钮
        appHeadContainer = new AppHeadContainer();
        var HeadHbox = appHeadContainer.getHeadHbox();
        Platform.runLater(() -> {
            this.getChildren().addAll(HeadHbox, masterDetailPane);
            VBox.setMargin(masterDetailPane, new Insets(3, 3, 3, 3));
        });
        ComponentGetter.treeView = treeView;
    }

    static {
        ConfigVal.cssList.add(AppWindow.class.getResource("/css/application.css").toExternalForm());
        ConfigVal.cssList.add(AppWindow.class.getResource("/css/dark/common.css").toExternalForm());
        ConfigVal.cssList.add(AppWindow.class.getResource("/css/dark/sql-keywords.css").toExternalForm());
        ConfigVal.cssList.add(AppWindow.class.getResource("/css/dark/treeView.css").toExternalForm());
        ConfigVal.cssList.add(AppWindow.class.getResource("/css/dark/TableView.css").toExternalForm());
        ConfigVal.cssList.add(AppWindow.class.getResource("/css/dark/tabPane.css").toExternalForm());

        ConfigVal.cssListLight.add(AppWindow.class.getResource("/css/application.css").toExternalForm());
        ConfigVal.cssListLight.add(AppWindow.class.getResource("/css/light/common-light.css").toExternalForm());
        ConfigVal.cssListLight.add(AppWindow.class.getResource("/css/light/sql-keywords-light.css").toExternalForm());
        ConfigVal.cssListLight.add(AppWindow.class.getResource("/css/light/tabPane-light.css").toExternalForm());

        ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/application.css").toExternalForm());
        ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/common-yellow.css").toExternalForm());
        ConfigVal.cssListYellow
                .add(AppWindow.class.getResource("/css/yellow/sql-keywords-yellow.css").toExternalForm());
        ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/treeView-yellow.css").toExternalForm());
        ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/TableView-yellow.css").toExternalForm());
        ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/tabPane-yellow.css").toExternalForm());

        ComponentGetter.INFO = new Label("");
        ComponentGetter.INFO.setGraphic(IconGenerator.svgImage("info-circle", "#7CFC00"));

        ComponentGetter.ABOUT = new Label("");
        ComponentGetter.ABOUT.setGraphic(IconGenerator.svgImage("info-circle", "#7CFC00"));

        ComponentGetter.WARN = new Label("");
        ComponentGetter.WARN.setGraphic(IconGenerator.svgImage("info-circle", "#FFD700"));
        ComponentGetter.ERROR = new Label("");
        ComponentGetter.ERROR.setGraphic(IconGenerator.svgImage("info-circle", "red"));
        ComponentGetter.EMPTY = new Label("");

        ComponentGetter.iconInfo = IconGenerator.svgImageDefActive("info-circle");
        ComponentGetter.uaIconInfo = IconGenerator.svgImageUnactive("info-circle");
        ComponentGetter.iconScript = IconGenerator.svgImageDefActive("icomoon-files-empty");
        ComponentGetter.uaIconScript = IconGenerator.svgImageUnactive("icomoon-files-empty");

        ComponentGetter.iconRight = IconGenerator.svgImageDefActive("chevron-circle-right", 14);
        ComponentGetter.iconLeft = IconGenerator.svgImageDefActive("chevron-circle-down", 14);

        ComponentGetter.LogoIcons = new Image(Objects.requireNonNull(AppWindow.class.getResourceAsStream(ConfigVal.appIcon)));

    }






    public Scene getAppScene() {
        return appScene;
    }

    public void setAppScene(Scene appScene) {
        this.appScene = appScene;
    }

}
