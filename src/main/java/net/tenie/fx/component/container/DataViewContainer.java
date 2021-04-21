package net.tenie.fx.component.container;

import java.util.HashMap;
import java.util.List;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.PropertyPo.CacheTabView;
//import net.tenie.fx.PropertyPo.CacheTableDate;
//import net.tenie.fx.PropertyPo.DataTabDataPo;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.utility.DraggingTabPaneSupport;
import net.tenie.fx.window.TableDataDetail;
import net.tenie.lib.tools.StrUtils;

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

		ComponentGetter.dataTab = dataView;
		DraggingTabPaneSupport support2 = new DraggingTabPaneSupport();
		support2.addSupport(dataView);
	}

	public static void showTableDate(DataViewTab dvt, String time , String rows) {
		showTableDate(dvt, -1, true, time, rows );
	}

	public static void showTableDate(DataViewTab dvt, int idx, boolean disable, String time , String rows) {
		Platform.runLater(() -> { 
			 dvt.createTab(idx, disable, time , rows); 
		});
	}

 





	// 数据展示tableView StringProperty
	public static FilteredTableView<ObservableList<StringProperty>> creatFilteredTableView() {
		FilteredTableView<ObservableList<StringProperty>> table = new FilteredTableView<ObservableList<StringProperty>>();

		table.rowHeaderVisibleProperty().bind(new SimpleBooleanProperty(true));

		// 可以选中多行
		table.getSelectionModel().selectionModeProperty().bind(Bindings.when(new SimpleBooleanProperty(true))
				.then(SelectionMode.MULTIPLE).otherwise(SelectionMode.SINGLE));

		// 选中监听
//		ListChangeListener<ObservableList<StringProperty>> indicesListener = new ListChangeListener<ObservableList<StringProperty>>() {
//			@Override
//			public void onChanged(Change<? extends ObservableList<StringProperty>> c) {
//				while (c.next()) {
//
//				}
//			}
//		};
//		table.getSelectionModel().getSelectedItems().addListener(indicesListener);

		int tableIdx = ConfigVal.tableIdx++;
		table.setId(tableIdx + "");
		table.getStyleClass().add("myTableTag");
		
		
	   FilteredTableColumn<ObservableList<StringProperty>, Number> tc = new FilteredTableColumn<>();
	   
//		tc.setCellValueFactory(cal -> { 
//			ObservableList<StringProperty> obs = cal.getValue(); 
//			int sz = obs.size();
//			StringProperty sp = obs.get(sz - 1); 
//			IntegerProperty sum = new SimpleIntegerProperty(); 
//			sum.setValue(Integer.valueOf(sp.get()) + 1);
//			return sum;
//		});
	   
	   // 点击 行号, 显示一个 当前行心的的窗口
	   tc.setCellFactory(col->{
			TableCell<ObservableList<StringProperty>, Number> cell = new TableCell<ObservableList<StringProperty>, Number>(){
			     
					@Override
	                public void updateItem(Number item, boolean empty) {
	                	super.updateItem(item, empty);
	                    this.setText(null);
	                    this.setGraphic(null);
	                    if (!empty) {
	                           int rowIndex = this.getIndex();
	                           this.setText((rowIndex+1)+"");
	                           this.setOnMouseClicked(e -> {
	     		               	  if (e.getClickCount() == 2) {
//	     		               		  JFXButton btn = (JFXButton) ComponentGetter.dataPaneSaveBtn();
	     		               	      TableDataDetail.show(); 
	     		               	  }
	     	                 } ); 
	                    }
	                }
			};
			return cell;
		}); 
	   
        table.setRowHeader(tc);
        //启用 隐藏列的控制按钮
        table.tableMenuButtonVisibleProperty().setValue(true);
        
        
		return table;
	}
	
	// 设置序号行的宽度
	public static void setTabRowWith(FilteredTableView<ObservableList<StringProperty>> table , int dataSize ) {
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
