package net.tenie.fx.component.container;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.utility.DraggingTabPaneSupport;

/*   
 * 展示(数据, ddl等)面板的容器
 * @author tenie 
 */
public class DataViewContainer {
	private HBox container;
	private VBox TabPanContainer;
	private TabPane dataView;

	public DataViewContainer() {
		container = new HBox();
		TabPanContainer = new VBox();
		container.getChildren().add(TabPanContainer);
		dataView = new TabPane();
		TabPanContainer.getChildren().add(dataView);

		VBox.setVgrow(dataView, Priority.ALWAYS);
		HBox.setHgrow(TabPanContainer, Priority.ALWAYS);

		ComponentGetter.dataTabPane = dataView;
		DraggingTabPaneSupport support2 = new DraggingTabPaneSupport();
		support2.addSupport(dataView);
		
		dataView.getTabs().addListener((ListChangeListener<? super Tab>) c -> { 
			var list = c.getList();
			if(list.size() == 0) {
//				SdkComponent.hideBottomPane(); 
				CommonUtility.delayRunThread(v -> {
					Platform.runLater(() -> {
						if (dataView.getTabs().size() == 0) {
							SdkComponent.hideBottomPane();
						}
					});
				}, 200);
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
	
	public HBox getContainer() {
		return container;
	}

	public void setContainer(HBox container) {
		this.container = container;
	}

	public VBox getTabPancontainer() {
		return TabPanContainer;
	}

	public void setTabPancontainer(VBox tabPancontainer) {
		TabPanContainer = tabPancontainer;
	}

	public TabPane getDataView() {
		return dataView;
	}

	public void setDataView(TabPane dataView) {
		this.dataView = dataView;
	}

}
