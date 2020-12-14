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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.fx.Action.DraggingTabPaneSupport;
import net.tenie.fx.Action.ShowTableRowDateDetailAction;
import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.PropertyPo.DataTabDataPo;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;

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

	public static void showTableDate(DataTabDataPo rsval) {
		showTableDate(rsval, -1, true);
	}

	public static void showTableDate(DataTabDataPo rsval, int idx, boolean disable) {
		Platform.runLater(() -> {
			List<FilteredTableView<ObservableList<StringProperty>>> allTable = rsval.getNewtables();
			List<String> names = rsval.getTableNames();
			// 只能在fx线程中操作控件
			TabPane dataTabPane = ComponentGetter.dataTab;
			for (int i = 0; i < allTable.size(); i++) {
				FilteredTableView<ObservableList<StringProperty>> table = allTable.get(i);
				// 添加一个新的tab页， 把view 放图其中
				String tn = names.get(i);
				addNewDateTab(dataTabPane, table, tn, idx, disable);
			}
		});
	}

	// dataTab add content 添加一个tab页， 把TableView放如页中
	private static void addNewDateTab(TabPane dataTab, FilteredTableView<ObservableList<StringProperty>> tbv,
			String tabName, int idx, boolean disable) {
		Tab nwTab = DataViewTab.createTab(dataTab, tabName);
		nwTab.setId(tbv.getId());
		CacheTableDate.saveTab(tbv.getId(), nwTab);
		VBox vb = generateDataPane(tbv.getId(), disable, tbv);

		if (idx > -1) {
			dataTab.getTabs().add(idx, nwTab);
		} else {
			dataTab.getTabs().add(nwTab);
		}

		dataTab.getSelectionModel().select(nwTab);
		nwTab.setContent(vb);
	}

	// 数据tab中的组件
	public static VBox generateDataPane(String id, boolean disable, TableView<ObservableList<StringProperty>> tbv) {
		VBox vb = new VBox();
		// 表格上面的按钮
		FlowPane fp = getDataTableOptionBtnsPane(disable);
		fp.setId(id);
		vb.setId(id);
		vb.getChildren().add(fp);
		vb.getChildren().add(tbv);
		VBox.setVgrow(tbv, Priority.ALWAYS);
		return vb;
	}

	// 数据表格 操作按钮们
	public static FlowPane getDataTableOptionBtnsPane(boolean disable) {
		FlowPane fp = new FlowPane();
		fp.prefHeight(25);
		JFXButton saveBtn = new JFXButton();
		saveBtn.setGraphic(ImageViewGenerator.svgImageDefActive("save"));
		saveBtn.setOnMouseClicked(CommonEventHandler.saveDate(saveBtn));
		saveBtn.setTooltip(MyTooltipTool.instance("Save data"));
		saveBtn.setDisable(true);
		saveBtn.setId(AllButtons.SAVE);

		JFXButton detailBtn = new JFXButton();
		detailBtn.setGraphic(ImageViewGenerator.svgImageDefActive("search-plus"));

		detailBtn.setOnMouseClicked(CommonEventHandler.showLineDetail(detailBtn));
		detailBtn.setTooltip(MyTooltipTool.instance("current line detail "));

		// refresh
		JFXButton refreshBtn = new JFXButton();
		refreshBtn.setGraphic(ImageViewGenerator.svgImageDefActive("refresh"));

		refreshBtn.setOnMouseClicked(CommonEventHandler.refreshData(refreshBtn));
		refreshBtn.setTooltip(MyTooltipTool.instance("refresh table "));
		refreshBtn.setDisable(disable);

		// 添加一行数据
		JFXButton addBtn = new JFXButton();
		addBtn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square"));

		addBtn.setOnMouseClicked(CommonEventHandler.addData(addBtn));
		addBtn.setTooltip(MyTooltipTool.instance("add new data "));
		addBtn.setDisable(disable);

		JFXButton minusBtn = new JFXButton();
		minusBtn.setGraphic(ImageViewGenerator.svgImage("minus-square", "#EC7774"));

		minusBtn.setOnMouseClicked(CommonEventHandler.deleteData(minusBtn));
		minusBtn.setTooltip(MyTooltipTool.instance("delete data "));
		minusBtn.setDisable(disable);

//	    	 files-o
		JFXButton copyBtn = new JFXButton();
		copyBtn.setGraphic(ImageViewGenerator.svgImageDefActive("files-o"));

		copyBtn.setOnMouseClicked(CommonEventHandler.copyData(copyBtn));
		copyBtn.setTooltip(MyTooltipTool.instance("copy selected row data "));
		copyBtn.setDisable(disable);

		MenuButton exportBtn = new MenuButton();
		exportBtn.setGraphic(ImageViewGenerator.svgImageDefActive("share-square-o"));
		exportBtn.setTooltip(MyTooltipTool.instance("Export data"));

		Menu insertSQL = new Menu("Export Insert SQL Format ");
		MenuItem selected = new MenuItem("Selected Data to Clipboard ");
		selected.setOnAction(CommonEventHandler.InsertSQLClipboard(true, false));
		MenuItem selectedfile = new MenuItem("Selected Data to file");
		selectedfile.setOnAction(CommonEventHandler.InsertSQLClipboard(true, true));

		MenuItem all = new MenuItem("ALl Data to Clipboard ");
		all.setOnAction(CommonEventHandler.InsertSQLClipboard(false, false));
		MenuItem allfile = new MenuItem("ALl Data to file");
		allfile.setOnAction(CommonEventHandler.InsertSQLClipboard(false, true));

		insertSQL.getItems().addAll(selected, selectedfile, all, allfile);

		Menu csv = new Menu("Export CSV Format ");
		MenuItem csvselected = new MenuItem("Selected Data to Clipboard ");
		csvselected.setOnAction(CommonEventHandler.csvStrClipboard(true, false));
		MenuItem csvselectedfile = new MenuItem("Selected Data to file");
		csvselectedfile.setOnAction(CommonEventHandler.csvStrClipboard(true, true));

		MenuItem csvall = new MenuItem("ALl Data to Clipboard ");
		csvall.setOnAction(CommonEventHandler.csvStrClipboard(false, false));
		MenuItem csvallfile = new MenuItem("ALl Data to file");
		csvallfile.setOnAction(CommonEventHandler.csvStrClipboard(false, true));

		csv.getItems().addAll(csvselected, csvselectedfile, csvall, csvallfile);

		Menu txt = new Menu("Export TXT Format ");
		MenuItem txtselected = new MenuItem("Selected Data to Clipboard ");
		txtselected.setOnAction(CommonEventHandler.txtStrClipboard(true, false));
		MenuItem txtselectedfile = new MenuItem("Selected Data to file");
		txtselectedfile.setOnAction(CommonEventHandler.txtStrClipboard(true, true));

		MenuItem txtall = new MenuItem("ALl Data to Clipboard ");
		txtall.setOnAction(CommonEventHandler.txtStrClipboard(false, false));
		MenuItem txtallfile = new MenuItem("ALl Data to file");
		txtallfile.setOnAction(CommonEventHandler.txtStrClipboard(false, true));

		txt.getItems().addAll(txtselected, txtselectedfile, txtall, txtallfile);

		exportBtn.getItems().addAll(insertSQL, csv, txt);

		fp.getChildren().addAll(saveBtn, detailBtn, refreshBtn, addBtn, minusBtn, copyBtn, exportBtn);
		return fp;
	}

	// 数据展示tableView StringProperty
	public static FilteredTableView<ObservableList<StringProperty>> creatFilteredTableView() {
		FilteredTableView<ObservableList<StringProperty>> table = new FilteredTableView<ObservableList<StringProperty>>();

		table.rowHeaderVisibleProperty().bind(new SimpleBooleanProperty(true));

		// 可以选中多行
		table.getSelectionModel().selectionModeProperty().bind(Bindings.when(new SimpleBooleanProperty(true))
				.then(SelectionMode.MULTIPLE).otherwise(SelectionMode.SINGLE));

		// 选中监听
		ListChangeListener<ObservableList<StringProperty>> indicesListener = new ListChangeListener<ObservableList<StringProperty>>() {
			@Override
			public void onChanged(Change<? extends ObservableList<StringProperty>> c) {
				while (c.next()) {

				}
			}
		};
		table.getSelectionModel().getSelectedItems().addListener(indicesListener);

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
