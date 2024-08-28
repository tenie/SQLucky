package net.tenie.Sqlucky.sdk.component;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheet;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.DraggingTabPaneSupport;
import org.controlsfx.control.tableview2.FilteredTableView;

/*   
 * 展示(数据, ddl等)面板的容器
 * @author tenie 
 */
public class DataViewContainer extends HBox{
//	private HBox container;

	private VBox tabPanContainer;
	// 标签页根
	private TabPane dataViewTabPane;

	// top button pane
	private AnchorPane dataTabTopBtnPane;
	// 顶部信息显示label
	private AnchorPane topAPLabel = new AnchorPane();
//			new Label("");


	public DataViewContainer() {
		super();
//		container = new HBox();
		tabPanContainer = new VBox();
//		container.getChildren().add(TabPanContainer);
		this.getChildren().add(tabPanContainer);
		dataViewTabPane = new TabPane();
		dataTabTopBtnPane = new AnchorPane();
		dataTabTopBtnPane.setPrefHeight(35);
//		TabPanContainer.getChildren().add(ap);
		tabPanContainer.getChildren().add(dataViewTabPane);


		VBox.setVgrow(dataViewTabPane, Priority.ALWAYS);
		HBox.setHgrow(tabPanContainer, Priority.ALWAYS);

		ComponentGetter.dataTabPane = dataViewTabPane;
		ComponentGetter.tabPanContainer = tabPanContainer;
		ComponentGetter.dataViewContainer = this;

		DraggingTabPaneSupport support2 = new DraggingTabPaneSupport();
		support2.addSupport(dataViewTabPane);
		
		dataViewTabPane.getTabs().addListener((ListChangeListener<? super Tab>) c -> {
			var list = c.getList();
			if(list.size() == 0) {
//				SdkComponent.hideBottomPane(); 
				CommonUtils.delayRunThread(v -> {
					Platform.runLater(() -> {
						if (dataViewTabPane.getTabs().size() == 0) {
							SdkComponent.hideBottomPane();
						}
					});
				}, 200);
			}
					
		});

		// 鼠标进入 dataViewTabPane 显示 隐藏按钮
		dataViewTabPane.setOnMouseEntered(eh->{
			// 判断tabPane是不是单独出来了, 是的话不用 sideRightBottom了
			if(ComponentGetter.dockSideTabPaneWindow == null ){
				Tab t = dataViewTabPane.getSelectionModel().getSelectedItem();
				if( t != null){
					if( t instanceof MyBottomSheet myTab ){
						// 获取隐藏按钮
						JFXButton hideBottom = SheetDataValue.hideBottom;
						// 数据窗口在右边的时候, 添加隐藏按钮
						if (SheetDataValue.isSideRight) {
							if (!dataTabTopBtnPane.getChildren().contains(hideBottom)) {
								Platform.runLater(()->dataTabTopBtnPane.getChildren().add(hideBottom));
								AnchorPane.setTopAnchor(hideBottom, 0.0);
							}

						} else {
							if (!myTab.getButtonAnchorPane().getChildren().contains(hideBottom)) {
								myTab.getButtonAnchorPane().getChildren().add(hideBottom);
								AnchorPane.setRightAnchor(hideBottom, 0.0);
								AnchorPane.setTopAnchor(hideBottom, 6.0);
							}
						}
					}
				}
			}
		});
		// 鼠标离开 dataViewTabPane 隐藏 隐藏按钮
		dataViewTabPane.setOnMouseExited(eh-> {
			if (ComponentGetter.dockSideTabPaneWindow == null) {
				Tab t = dataViewTabPane.getSelectionModel().getSelectedItem();
				if (t != null) {
					if (t instanceof MyBottomSheet myTab) {
						// 获取隐藏按钮
						JFXButton hideBottom = SheetDataValue.hideBottom;
						// 数据窗口在右边的时候, 不移除隐藏按钮
						if (SheetDataValue.isSideRight) {
//							if (dataTabTopBtnPane.getChildren().contains(hideBottom)) {
//								Platform.runLater(()->dataTabTopBtnPane.getChildren().remove(hideBottom));
//							}
//
						} else {
							if (myTab.getButtonAnchorPane().getChildren().contains(hideBottom)) {
								myTab.getButtonAnchorPane().getChildren().remove(hideBottom);
							}
						}
					}
				}
			}
		});
		// 选中的tab 显示隐藏按钮
		dataViewTabPane.getSelectionModel().selectedItemProperty().addListener((a,b,c)-> {
			if (ComponentGetter.dockSideTabPaneWindow == null) {
				if (c instanceof MyBottomSheet myTab) {
					// 获取隐藏按钮
					JFXButton hideBottom = SheetDataValue.hideBottom;
					if (SheetDataValue.isSideRight) {
//						AnchorPane.setRightAnchor(hideBottom, 0.0);
						AnchorPane.setTopAnchor(hideBottom, 0.0);
						//执行 sql信息
						showLabelInfo();
//						if (!myTab.getBtnHbox().getChildren().contains(hideBottom)) {
//							Platform.runLater(() -> {
//								myTab.getBtnHbox().getChildren().add(0, hideBottom);
//							});
//
//						}
					} else {
						if (!myTab.getButtonAnchorPane().getChildren().contains(hideBottom)) {
							myTab.getButtonAnchorPane().getChildren().add(hideBottom);
							AnchorPane.setRightAnchor(hideBottom, 0.0);
							AnchorPane.setTopAnchor(hideBottom, 6.0);
							// 执行 sql信息
							myTab.showSqlInfo();
						}
					}
				}
			}
		});


	}


	// 显示顶部显示按钮的 TOP PANE
	public void showTopPane(){
//		this.dataTabTopBtnPane.getChildren().add(new Label("1111"));
		this.dataTabTopBtnPane.setPrefHeight(25);
		this.dataTabTopBtnPane.getChildren().add(this.topAPLabel);
		AnchorPane.setTopAnchor(topAPLabel, 6.0);


		this.tabPanContainer.getChildren().addFirst(this.dataTabTopBtnPane);
	}
	// sql执行信息展示
	public void dataTabTopBtnPaneAddText(Label info){
		Platform.runLater(()->{
			topAPLabel.getChildren().clear();
			topAPLabel.getChildren().add(info);

//			topPaneTextLabel.setText(info);
		});
	}

	public void showLabelInfo(){
		Tab tab =ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
		if(tab instanceof MyBottomSheet mbs){
			Label infoLb = mbs.getSqlLabel();
			ComponentGetter.dataViewContainer.dataTabTopBtnPaneAddText(infoLb);
		}
	}

	// 隐藏顶部显示按钮的 TOP PANE
	public void hideTopPane(){
		this.tabPanContainer.getChildren().remove(this.dataTabTopBtnPane);
		this.dataTabTopBtnPane.getChildren().clear();
	}

	// 设置序号行的宽度
	public static void setTabRowWith(FilteredTableView<ResultSetRowPo> table , int dataSize ) {
		if(dataSize > 1000) {
			table.setRowHeaderWidth(50);
		}else if(dataSize > 100000) {
			table.setRowHeaderWidth(60);
		}
	}

	public VBox getTabPancontainer() {
		return tabPanContainer;
	}

	public void setTabPancontainer(VBox tabPancontainer) {
		tabPanContainer = tabPancontainer;
	}

	public TabPane getDataViewTabPane() {
		return dataViewTabPane;
	}

	public void setDataViewTabPane(TabPane dataViewTabPane) {
		this.dataViewTabPane = dataViewTabPane;
	}

}
