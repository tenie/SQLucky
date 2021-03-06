package net.tenie.fx.component.container;

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
import net.tenie.fx.Action.DraggingTabPaneSupport;
import net.tenie.fx.Action.ShowTableRowDateDetailAction;
import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.PropertyPo.DataTabDataPo;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ButtonFactory;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;
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

	public static void showTableDate(DataTabDataPo rsval,   String time , String rows, String connName) {
		showTableDate(rsval, -1, true, time, rows, connName );
	}

	public static void showTableDate(DataTabDataPo rsval, int idx, boolean disable, String time , String rows, String connName) {
		Platform.runLater(() -> {
			List<FilteredTableView<ObservableList<StringProperty>>> allTable = rsval.getNewtables();
			List<String> names = rsval.getTableNames();
			// 只能在fx线程中操作控件
			TabPane dataTabPane = ComponentGetter.dataTab;
			for (int i = 0; i < allTable.size(); i++) {
				FilteredTableView<ObservableList<StringProperty>> table = allTable.get(i);
				// 添加一个新的tab页， 把view 放入其中
				String tn = names.get(i);
				String excInfoTitle = ConfigVal.EXEC_INFO_TITLE;
				// 如果是要添加一个info的Tab, 先看有没有旧的复用
				if( excInfoTitle.equals(tn) && ComponentGetter.dataTab.getTabs().size() > 0) { 
						Tab tab0 =ComponentGetter.dataTab.getTabs().get(0); 
						String title = CommonUtility.tabText(tab0); 
						if( excInfoTitle.equals(title) ) {
							// 新的table数据放入复用的table中
							VBox vb = (VBox) tab0.getContent();
							FilteredTableView<ObservableList<StringProperty>> vbt  = 
									(FilteredTableView<ObservableList<StringProperty>>) vb.getChildren().get(1);
							vbt.getItems().addAll( table.getItems()) ;
							dataTabPane.getSelectionModel().select(tab0);
						}else {
							addNewDateTab(dataTabPane, table, tn, 0, disable, time , rows, connName);
						}
						
				}else {
					addNewDateTab(dataTabPane, table, tn, idx, disable, time , rows, connName);
				}
				
			}
		});
	}

	// dataTab add content 添加一个tab页， 把TableView放如页中
	private static void addNewDateTab(TabPane dataTab, FilteredTableView<ObservableList<StringProperty>> tbv,
			String tabName, int idx, boolean disable , String time , String rows, String connName) {
		Tab nwTab = DataViewTab.createTab(dataTab, tabName);
		nwTab.setId(tbv.getId());
		CacheTableDate.saveTab(tbv.getId(), nwTab);
		// 构建数据Tab页中的表
		VBox vb = generateDataPane(tbv.getId(), disable, tbv ,   time ,   rows, connName);

		if (idx > -1) {
			dataTab.getTabs().add(idx, nwTab);
		} else {
			dataTab.getTabs().add(nwTab);
		}

		dataTab.getSelectionModel().select(nwTab);
		nwTab.setContent(vb);
	}

	// 数据tab中的组件
	public static VBox generateDataPane(String id, boolean disable, TableView<ObservableList<StringProperty>> tbv , String time , String rows, String connName) {
		VBox vb = new VBox();
		// 表格上面的按钮
		AnchorPane fp = ButtonFactory.getDataTableOptionBtnsPane(disable, time, rows, connName);
		fp.setId(id);
		vb.setId(id);
		vb.getChildren().add(fp);
		vb.getChildren().add(tbv);
		VBox.setVgrow(tbv, Priority.ALWAYS);
		return vb;
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
	     		               		  JFXButton btn = (JFXButton) ComponentGetter.dataFlowSaveBtn();
	     		               	      ShowTableRowDateDetailAction.show(btn); 
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
