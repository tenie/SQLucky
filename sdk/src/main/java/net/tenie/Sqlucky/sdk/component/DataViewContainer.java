package net.tenie.Sqlucky.sdk.component;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
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

	private VBox TabPanContainer;
	// 标签页根
	private TabPane dataViewTabPane;

	public DataViewContainer() {
		super();
//		container = new HBox();
		TabPanContainer = new VBox();
//		container.getChildren().add(TabPanContainer);
		this.getChildren().add(TabPanContainer);
		dataViewTabPane = new TabPane();
		TabPanContainer.getChildren().add(dataViewTabPane);

		VBox.setVgrow(dataViewTabPane, Priority.ALWAYS);
		HBox.setHgrow(TabPanContainer, Priority.ALWAYS);

		ComponentGetter.dataTabPane = dataViewTabPane;
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
			Tab t = dataViewTabPane.getSelectionModel().getSelectedItem();
			if( t != null){
				if( t instanceof MyBottomSheet myTab ){
					// 获取隐藏按钮
					JFXButton hideBottom = SheetDataValue.hideBottom;
					if (SheetDataValue.isSideRight) {
						if (!myTab.getBtnHbox().getChildren().contains(hideBottom)) {
							myTab.getBtnHbox().getChildren().add(0, hideBottom);
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

		});
		// 鼠标离开 dataViewTabPane 隐藏 隐藏按钮
		dataViewTabPane.setOnMouseExited(eh->{
			Tab t = dataViewTabPane.getSelectionModel().getSelectedItem();
			if( t != null) {
				if (t instanceof MyBottomSheet myTab) {
					// 获取隐藏按钮
					JFXButton hideBottom = SheetDataValue.hideBottom;

					if (SheetDataValue.isSideRight) {
						if (myTab.getBtnHbox().getChildren().contains(hideBottom)) {
							myTab.getBtnHbox().getChildren().remove(hideBottom);
						}
					} else {
						if (myTab.getButtonAnchorPane().getChildren().contains(hideBottom)) {
							myTab.getButtonAnchorPane().getChildren().remove(hideBottom);
						}
					}
				}
			}

		});
		// 选中的tab 显示隐藏按钮
		dataViewTabPane.getSelectionModel().selectedItemProperty().addListener((a,b,c)->{
			if (c instanceof MyBottomSheet myTab) {
				// 获取隐藏按钮
				JFXButton hideBottom = SheetDataValue.hideBottom;
				if (SheetDataValue.isSideRight) {
					if (!myTab.getBtnHbox().getChildren().contains(hideBottom)) {
						Platform.runLater(()->{
							myTab.getBtnHbox().getChildren().add(0, hideBottom);
						});

					}
				} else {
					if (!myTab.getButtonAnchorPane().getChildren().contains(hideBottom)) {
						myTab.getButtonAnchorPane().getChildren().add(hideBottom);
						AnchorPane.setRightAnchor(hideBottom, 0.0);
						AnchorPane.setTopAnchor(hideBottom, 6.0);
					}
				}
			}
		});


	}

//	public static void showTableDate(DataViewTab dvt, String time , String rows) {
//		showTableDate(dvt, -1, true, time, rows );
//	}
//
//	public static void showTableDate(DataViewTab dvt, int idx, boolean disable, String time , String rows) {
//		Platform.runLater(() -> { 
//			dvt.createTab(idx, disable, time , rows); 
//		});
//	}

 





	
	
	// 设置序号行的宽度
	public static void setTabRowWith(FilteredTableView<ResultSetRowPo> table , int dataSize ) {
		if(dataSize > 1000) {
			table.setRowHeaderWidth(50);
		}else if(dataSize > 100000) {
			table.setRowHeaderWidth(60);
		}
	}
	
//	public HBox getContainer() {
//		return container;
//	}
//
//	public void setContainer(HBox container) {
//		this.container = container;
//	}

	public VBox getTabPancontainer() {
		return TabPanContainer;
	}

	public void setTabPancontainer(VBox tabPancontainer) {
		TabPanContainer = tabPancontainer;
	}

	public TabPane getDataViewTabPane() {
		return dataViewTabPane;
	}

	public void setDataViewTabPane(TabPane dataViewTabPane) {
		this.dataViewTabPane = dataViewTabPane;
	}

}
