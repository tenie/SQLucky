package net.tenie.fx.component.container;

import javafx.application.Platform;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.SqlcukyTitledPaneInfoPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.AppWindowComponentGetter;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.InfoTree.DBinfoTreeButtonFactory;
import net.tenie.fx.component.InfoTree.DBinfoTreeFilter;
import net.tenie.fx.component.ScriptTree.ScriptTabTree;
import net.tenie.fx.component.ScriptTree.ScriptTreeButtonPanel;

/**
 * 
 * @author tenie
 *
 */
public class DBinfoContainer {
	private VBox container;
	private HBox treeBtnPane;	// 按钮面板
	private TreeView<TreeNodePo> treeView;
	private AnchorPane filter;
	private DBinfoTree dbInfoTree;
	private ScriptTabTree scriptTabTree;   //脚本
	private DBinfoTreeFilter dbf;
	
	
	public DBinfoContainer() {
		// 容器
		container = new VBox();
		// 按钮
//		treeBtnPane = DBinfoTreeButtonFactory.createTreeViewbtn();  
		treeBtnPane = DBinfoTreeButtonFactory.createTreeViewbtn2();  
		// 数据库信息
		dbInfoTree = new DBinfoTree();
		treeView =  DBinfoTree.DBinfoTreeView ;  
		// 脚本
		scriptTabTree = new ScriptTabTree();
		var scriptTreeView =  ScriptTabTree.ScriptTreeView ;
		// 查询过滤
		dbf  = new DBinfoTreeFilter(); 		
		filter = dbf.createFilterPane(treeView);
		
//		VBox vboxTreeView = new VBox();
//		vboxTreeView.getStyleClass().add("myDataModel-vbox");
//		vboxTreeView.getChildren().addAll(filter, treeView );
//		VBox.setVgrow(treeView, Priority.ALWAYS);
		
		// 数据连接/脚本 切换窗口
		Accordion ad = createAccordion(scriptTreeView, treeView);
		
//		container.getChildren().addAll(treeBtnPane, ad , filter);
		container.getChildren().addAll(treeBtnPane, ad );
		VBox.setVgrow(ad, Priority.ALWAYS);
 

		AppWindowComponentGetter.treeView = treeView;
		AppWindowComponentGetter.dbInfoTree = dbInfoTree;
		AppWindowComponentGetter.DBinfoContainer = container;
		AppWindowComponentGetter.dbInfoTreeFilter = filter;
		
//		ComponentGetter.treeBtnPane = treeBtnPane;
		
		
		ComponentGetter.infoAccordion = ad;
		
		CommonUtility.fadeTransition(treeBtnPane, 1000); 
		CommonUtility.fadeTransition(ad, 1000);  
		CommonUtility.fadeTransition(treeView, 1000); 
		CommonUtility.fadeTransition(filter, 1000); 
		
		
	}

	
	private Accordion createAccordion(TreeView<MyTab> scriptTreeView ,TreeView<TreeNodePo> DBtreeView) { 
		Accordion ad = new Accordion();
		// 数据库连接信息
		TitledPane dbTitledPane = new TitledPane();
		dbTitledPane.setText("DB Connection"); 
		dbTitledPane.setUserData( new SqlcukyTitledPaneInfoPo("Sqlucky DB Connection", treeBtnPane));
//		dbTitledPane.setGraphic( ImageViewGenerator.svgImageDefActive("info-circle", 14)); 
		CommonUtility.addCssClass(dbTitledPane, "titledPane-color");
		dbTitledPane.setContent( DBtreeView);
		
		

		// 脚本文件
		ScriptTreeButtonPanel sbtnPanel  = new ScriptTreeButtonPanel();
//		sbtnPanel.getOptionHbox();
		var scriptVbox = sbtnPanel.getScriptTitledPaneContent(scriptTreeView);
		TitledPane scriptTitledPane = new TitledPane();
		scriptTitledPane.setText("Script");
		scriptTitledPane.setUserData(new SqlcukyTitledPaneInfoPo("Script", sbtnPanel.getOptionHbox()));
		
		
		CommonUtility.addCssClass(scriptTitledPane, "titledPane-color");
		scriptTitledPane.setContent(scriptTreeView);
//		scriptTitledPane.setContent(scriptVbox);
		
		ad.setExpandedPane(dbTitledPane);
		ad.getPanes().add(dbTitledPane);
		ad.getPanes().add(scriptTitledPane);
		
		
		ad.expandedPaneProperty().addListener((obj, o, n )->{
//			System.out.println(o);
//			System.out.println(n);
//			System.out.println(ad.getExpandedPane());
//			System.out.println("----------------------\n");
			if(ad.getExpandedPane() == null && n == null) { 
				Platform.runLater(() -> { 
					if(ad.getExpandedPane() == null) { 
						dbTitledPane.setExpanded(true); 
					}
				}); 
			}
			
			SqlcukyTitledPaneInfoPo info = (SqlcukyTitledPaneInfoPo) n.getUserData();
			if(info !=null) {
				var bx = info.getBtnsBox();
				container.getChildren().remove(0);
				container.getChildren().add(0, bx);
			}
		});
//		
//		dbTitledPane.expandedProperty().addListener((obs, oldValue, newValue) -> {
//			if(newValue == false) {
//				
//				if( scriptTitledPane.isExpanded() == false) {
//					Platform.runLater(() -> {scriptTitledPane.setExpanded(true); }); 
//				}
//			} 
//		});
		
//		scriptTitledPane.expandedProperty().addListener((obs, oldValue, newValue) -> {
//			if(newValue == false) {
//				if( scriptTitledPane.isExpanded() == false) {
//					Platform.runLater(() -> {dbTitledPane.setExpanded(true); }); 
//				}
//			} 
//		});
		
		ComponentGetter.dbTitledPane = dbTitledPane;
		ComponentGetter.scriptTitledPane = scriptTitledPane;
		   
		return ad;
	}
	
	
	public VBox getContainer() {
		return container;
	}

	public void setContainer(VBox container) {
		this.container = container;
	}


	public TreeView<TreeNodePo> getTreeView() {
		return treeView;
	}

	public void setTreeView(TreeView<TreeNodePo> treeView) {
		this.treeView = treeView;
	}

	public DBinfoTree getDbInfoTree() {
		return dbInfoTree;
	}

	public void setDbInfoTree(DBinfoTree dbInfoTree) {
		this.dbInfoTree = dbInfoTree;
	}


	public ScriptTabTree getScriptTabTree() {
		return scriptTabTree;
	}


	public void setScriptTabTree(ScriptTabTree scriptTabTree) {
		this.scriptTabTree = scriptTabTree;
	}

}
