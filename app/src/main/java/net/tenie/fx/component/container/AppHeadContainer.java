package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.fx.factory.ButtonFactory;

public class AppHeadContainer {
    public static volatile Stage SQLuckyStage;

    // 头部底层
    private HBox headHbox = new HBox();

    // 头部按钮容器
    private AnchorPane rightAnchorPane = new AnchorPane();

    // 头部 logo 菜单容器
    private HBox leftBox = new HBox();

    // 菜单
    private MenuBarContainer mainMenuBar;
    //控制菜单显示按钮
    JFXButton showMenuBarBtn;


    // sql操作按钮box
    public static HBox buttonBox;
    public static HBox DbinfoOperateBox;

    // 关闭, 最小化, 还原的图标
    private JFXButton windowResize = new JFXButton();
    private	JFXButton hidden = new JFXButton();
    private JFXButton close = new JFXButton();

    private JFXButton hideLeft = new JFXButton();
    private JFXButton hideBottom = new JFXButton();
    private JFXButton hideRight = new JFXButton();


    public AppHeadContainer(){
        // 内边距
        headHbox.setPadding(new Insets(5,0, 2, 10));

        headHbox.getChildren().add(leftBox);
        headHbox.getChildren().add(rightAnchorPane);
        // 头部底层 靠右居中
        headHbox.setAlignment(Pos.CENTER_RIGHT);

        // logo 菜单靠左居中
        leftBox.setAlignment(Pos.CENTER_LEFT);
        // 子空间之间的间距
        leftBox.setSpacing(10);
        HBox.setHgrow(leftBox , Priority.ALWAYS);


        // 添加图标
        Image i = ComponentGetter.LogoIcons;
        ImageView LogoImageView = new ImageView(i);
        LogoImageView.setFitWidth(22.0);
        LogoImageView.setFitHeight(22.0);


        // 菜单
        mainMenuBar = new MenuBarContainer();
        // 初始化 显示菜单按钮
        initShowMenuBarBtn();

        // sql查询等操作按钮
        ButtonFactory buttonFactory = new ButtonFactory();
        buttonBox = buttonFactory.getOperateBox();
        // 界面上的子窗口的隐藏和显示按钮
        initSubWindowCtrlBtn(buttonBox);

        // 初始化按钮, 关闭, 还原, 最小化, 但不添加到主界面上
        initTopButtonPane();

        rightAnchorPane.getChildren().add(buttonBox);

        // 数据库连接切换box
        DbinfoOperateBox = buttonFactory.getDbinfoOperateBox();

        // 加入左边box
        leftBox.getChildren().add(LogoImageView);
        if(!CommonUtils.isMacOS()){
            leftBox.getChildren().add(showMenuBarBtn);
            mainMenuBar.setVisible(false);
        }
        leftBox.getChildren().add(DbinfoOperateBox);
        // 菜单, 如果mac os, 不显示菜单按钮
        leftBox.getChildren().add(mainMenuBar);
    }

    // 初始化 显示菜单按钮
    private JFXButton initShowMenuBarBtn(){
        showMenuBarBtn = new JFXButton();
        showMenuBarBtn.setGraphic(IconGenerator.menuBarIcon());

        if(!CommonUtils.isMacOS()){
            showMenuBarBtn.setOnAction(event -> {
                leftBox.getChildren().remove(showMenuBarBtn);
                leftBox.getChildren().remove(DbinfoOperateBox);
                mainMenuBar.setVisible(true);
                Platform.runLater(()->{
                    addMouseEventFilter(SQLuckyStage);
                });

            });
        }


        return showMenuBarBtn;
    }




    // 顶部按钮面板, 添加 最小化, 重置大小, 关闭按钮
    private void initTopButtonPane() {
        // 最小化
        hidden.setGraphic(IconGenerator.svgImageCss("my-minus-square", 12.0, 1.0, "top-btn-Icon-color"));
        hidden.getStyleClass().add("window-other-btn");

        // 最大化, 非最大化(还原)
        Region windowResizeSvg = IconGenerator.svgImageCss("my-window-restore", 12, 12, "top-btn-Icon-color");
        windowResize.setGraphic(windowResizeSvg);
        windowResize.getStyleClass().add("window-other-btn");

        // 关闭
        var closeSvg = IconGenerator.svgImageCss("my-window-close", 12, 12, "top-btn-Icon-color");
        close.getStyleClass().add("window-close-btn");
        close.setGraphic(closeSvg);

        CommonButtons.hidden = hidden;
        CommonButtons.windowResize = windowResize;
        CommonButtons.close = close;
    }

    // 添加自定义的 关闭, 最小化, 重置大小按钮
    public void addHiddenWindowResizeClose(){
        if(!CommonUtils.isMacOS() && !buttonBox.getChildren().contains(hidden)){
            buttonBox.getChildren().addAll(hidden, windowResize, close);
        }
    }
    // 移除自定义的 关闭, 最小化, 重置大小按钮
    public void removeHiddenWindowResizeClose(){
        buttonBox.getChildren().removeAll(hidden, windowResize, close);

        AnchorPane.setRightAnchor(buttonBox, 0.0);
    }

    /**
     * 界面上的子窗口的隐藏和显示按钮
     */
    private void initSubWindowCtrlBtn( HBox buttonBox){
        CommonButtons.hideLeft = hideLeft;
        CommonButtons.hideBottom = hideBottom;
        CommonButtons.hideRight = hideRight;

        hideLeft.setGraphic(IconGenerator.mainTabPaneClose());
        hideLeft.setOnMouseClicked(event ->  { AppCommonAction.hideLeft();});
        hideLeft.setTooltip(MyTooltipTool.instance("hide or show connection panel "));

        // hideBottom
        hideBottom.setGraphic(IconGenerator.bottomTabPaneOpen());
        hideBottom.setOnMouseClicked(event -> { SdkComponent.hideBottom();});
        hideBottom.setTooltip(MyTooltipTool.instance("hide or show data panel "));

        hideRight.setGraphic(IconGenerator.rightTabPaneOpen());
        hideRight.setOnAction(event -> {
            SdkComponent.showOrhideRight();
        });
        hideRight.setTooltip(MyTooltipTool.instance("Hide or Show Right Panel "));

        buttonBox.getChildren().addAll(hideLeft, hideBottom, hideRight);
    }

    // 控制菜单显示
    public void ctrlMenuBarShow(){
        if( leftBox.getChildren().contains(mainMenuBar) && !subMenuIsHover()){
            mainMenuBar.setVisible(false);
            if(!leftBox.getChildren().contains(showMenuBarBtn)){
                leftBox.getChildren().add(1,showMenuBarBtn);
                leftBox.getChildren().add(2,DbinfoOperateBox);
            }
            removeMouseEventFilter(AppHeadContainer.SQLuckyStage);
        }
    }

    //重写EventHandler接口实现方法
    private final EventHandler<MouseEvent> appMouseEvent = event -> ctrlMenuBarShow();
    public void addMouseEventFilter(Stage pStage){
        pStage.addEventFilter(MouseEvent.MOUSE_CLICKED, appMouseEvent);
    }
    public void removeMouseEventFilter(Stage pStage){
        pStage.removeEventFilter(MouseEvent.MOUSE_CLICKED, appMouseEvent);
    }

    private boolean subMenuIsHover(){
        var menusList = mainMenuBar.getMenus();
        boolean tf = false;
        for(var menu : menusList){
            tf = menu.isShowing();
            if(tf){
                return tf;
            }
        }

        return tf;
    }

    public HBox getHeadHbox() {
        return headHbox;
    }

    public void setHeadHbox(HBox headHbox) {
        this.headHbox = headHbox;
    }
}
