package net.tenie.fx.component.container;

import org.controlsfx.control.MasterDetailPane;

import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.MyTab;

/*   @author tenie */
public class AppWindow {
	private VBox mainWindow;
	private MenuBarContainer mainMenuBar;
	private MasterDetailPane masterDetailPane;
	private OperateContainer operate;
	private DataViewContainer dataView;

	public AppWindow() {
		mainWindow = new VBox();
		ComponentGetter.mainWindow = mainWindow;

		mainMenuBar = new MenuBarContainer();
		masterDetailPane = new MasterDetailPane(Side.BOTTOM);
		masterDetailPane = new MasterDetailPane(Side.BOTTOM);
		operate = new OperateContainer();
		dataView = new DataViewContainer();

		masterDetailPane.getStyleClass().add(0, "masterDetailPane");
		masterDetailPane.setShowDetailNode(false);
		masterDetailPane.setMasterNode(operate.getContainer());
		masterDetailPane.setDetailNode(dataView.getContainer());
		masterDetailPane.setDividerPosition(0.6);
		VBox.setVgrow(masterDetailPane, Priority.ALWAYS);

		ComponentGetter.masterDetailPane = masterDetailPane;
		ComponentGetter.dataView = dataView;
		mainWindow.getChildren().addAll(mainMenuBar.getMainMenuBar(), masterDetailPane);
		// 设置tree 面板的显示比例
		masterDetailPane.widthProperty().addListener((ob, ov ,nv)->{
				if (nv.doubleValue() > 1) {
					double wi = ComponentGetter.masterDetailPane.getWidth();
					double tbp = 270.0;
					double val =  tbp / wi;  
					System.out.println("设置窗口比例 :" + val);
					ComponentGetter.treeAreaDetailPane.setDividerPosition(val);
				}
		}); 
		
		// tab 被拖拽后, script tree 跟着一起变换位置
		ComponentGetter.mainTabPane.getTabs().addListener((Change<? extends Tab> tmpTab) -> {
			    boolean tf = false;
	            while (tmpTab.next()) {
	                if (tmpTab.wasAdded()) {
	                	tf = true;
	                	System.out.println("\n=========== mainTabPane.setOnDragDone =========\n ");
	                } 
	            }
	            if(tf) {//TODO
	            	Platform.runLater(()->{
	            		ObservableList<Tab> tabs = ComponentGetter.mainTabPane.getTabs();
		            	ObservableList<TreeItem<MyTab>> treeItems = ScriptTabTree.ScriptTreeView.getRoot().getChildren();
		            	for(int i = 0 ; i < tabs.size(); i++) {
		            		MyTab mt = (MyTab) tabs.get(i);
		            		TreeItem<MyTab> itm = treeItems.get(i);
		            		MyTab imtMt = itm.getValue();
		            		
		            		
		            	}
	            	}); 
	            	
	            }
	            
	        });

	}

	public VBox getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(VBox mainWindow) {
		this.mainWindow = mainWindow;
	}

	public MenuBarContainer getMainMenuBar() {
		return mainMenuBar;
	}

	public void setMainMenuBar(MenuBarContainer mainMenuBar) {
		this.mainMenuBar = mainMenuBar;
	}

	public MasterDetailPane getMasterDetailPane() {
		return masterDetailPane;
	}

	public void setMasterDetailPane(MasterDetailPane masterDetailPane) {
		this.masterDetailPane = masterDetailPane;
	}

	public OperateContainer getOperate() {
		return operate;
	}

	public void setOperate(OperateContainer operate) {
		this.operate = operate;
	}

	public DataViewContainer getDataView() {
		return dataView;
	}

	public void setDataView(DataViewContainer dataView) {
		this.dataView = dataView;
	}

}
