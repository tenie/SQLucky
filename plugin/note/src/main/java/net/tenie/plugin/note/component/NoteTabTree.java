package net.tenie.plugin.note.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.jfoenix.controls.JFXButton;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.SaveFile;
import net.tenie.Sqlucky.sdk.utility.StrUtils;


/**
 * 
 * @author tenie
 *
 */
public class NoteTabTree {

	public static TreeView<SqluckyTab> NoteTabTreeView;
//	private ContextMenu  menu;
	private static TreeItem<SqluckyTab> rootNode;
	
	public NoteTabTree() {
		 createScriptTreeView();
	}

	// db节点view
	public TreeView<SqluckyTab> createScriptTreeView() {
		SqluckyTab stab = ComponentGetter.appComponent.sqluckyTab();
		rootNode = new TreeItem<>(stab);
		TreeView<SqluckyTab> treeView = new TreeView<>(rootNode);
		treeView.getStyleClass().add("my-tag");
		treeView.setShowRoot(false); 
		// 展示连接
		if (rootNode.getChildren().size() > 0)
			treeView.getSelectionModel().select(rootNode.getChildren().get(0)); // 选中节点
		// 双击
		treeView.setOnMouseClicked(e -> {
			treeViewDoubleClick(e);
		});
		// 右键菜单
		ContextMenu contextMenu = createContextMenu();
//		menu = new ScriptTreeContextMenu(rootNode );
//		ContextMenu	contextMenu = menu.getContextMenu(); 
		treeView.setContextMenu(contextMenu);
		// 选中监听事件
//		treeView.getSelectionModel().selectedItemProperty().addListener(treeViewContextMenu(treeView));
		treeView.getSelectionModel().select(rootNode);

		NoteTabTreeView = treeView;

		// 显示设置
		treeView.setCellFactory(new NoteTabNodeCellFactory());
		 

//		recoverScriptNode(rootNode);
		return treeView;
	}
	
	
//	// 恢复数据中保存的连接数据
//	public static void recoverScriptNode(TreeItem<SqluckyTab> rootNode) {
//		List<DocumentPo> datas ;
//		List<H2SqlTextSavePo> ls;
//		String SELECT_PANE ;
//		try {
//			Connection H2conn = H2Db.getConn();
//			ls = SqlTextDao.read(H2conn);
//			SELECT_PANE = SqlTextDao.readConfig(H2conn, "SELECT_PANE");
//			datas = SqlTextDao.readScriptPo(H2conn);
//		} finally {
//			H2Db.closeConn();
//		}
//		
//		List<Integer> ids = new ArrayList<>();
//		for (H2SqlTextSavePo sqlpo : ls) {
//			ids.add(sqlpo.getScriptId());
//		}
//
//		if (datas != null && datas.size() > 0) {
//			ConfigVal.pageSize = datas.size();
//			for (DocumentPo po : datas) {
//				MyTab tb = new MyTab(po);
//				TreeItem<SqluckyTab> item = new TreeItem<>(tb);
//				rootNode.getChildren().add(item);
//				// 恢复代码编辑框
//				if (ids.contains(po.getId())) {
//					MyTab.myTabPaneAddMyTab(tb);
//				}
//			}
//
//		}
//
//		int ts = ComponentGetter.mainTabPane.getTabs().size();
//		// 初始化上次选中页面
//		if (StrUtils.isNotNullOrEmpty(SELECT_PANE)) { 
//			int sps = Integer.valueOf(SELECT_PANE);
//			if (ts > sps) {
//				ComponentGetter.mainTabPane.getSelectionModel().select(sps);
//			}  
//		}	
//		// 没有tab被添加, 添加一新的
//		if (ts == 0) {
//			MyTab.addCodeEmptyTabMethod();
//		}
//
//	}

	// 所有连接节点
	public static ObservableList<TreeItem<SqluckyTab>> allTreeItem() {
		ObservableList<TreeItem<SqluckyTab>> val = NoteTabTreeView.getRoot().getChildren();
		return val;
	}

	   

	// 获取当前选中的节点
	public static TreeItem<SqluckyTab> getScriptViewCurrentItem() {
		TreeItem<SqluckyTab> ctt = NoteTabTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	 

	// 给root节点加元素 
	public static void treeRootAddItem(TreeItem<SqluckyTab> item) { 
		TreeItem<SqluckyTab> rootNode = NoteTabTreeView.getRoot();
		rootNode.getChildren().add(item);		
	}
	// 给root节点加元素 
		public static void treeRootAddItem(SqluckyTab  mytab) {
			TreeItem<SqluckyTab> item = new TreeItem<SqluckyTab> (mytab); 
			treeRootAddItem(item);
		}

	// tree view 双击事件
	public void treeViewDoubleClick(MouseEvent mouseEvent) { 
		if (mouseEvent.getClickCount() == 2) {
			openMyTab();
		}
	}
	
	public static void openMyTab() {
		TreeItem<SqluckyTab> item = NoteTabTreeView.getSelectionModel().getSelectedItem();
		var mytab = item.getValue(); 
		if(mytab != null && mytab.getDocumentPo() != null) {
			mytab.mainTabPaneAddMyTab();
		}
	}
	 
	public static  List<DocumentPo>  allScriptPo() {
		 ObservableList<TreeItem<SqluckyTab>> ls = allTreeItem();
		 List<DocumentPo> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb.getDocumentPo());
		 }
		 
		 return list;
	}
	public static  List<SqluckyTab>  allMyTab() {
		 ObservableList<TreeItem<SqluckyTab>> ls = allTreeItem();
		 List<SqluckyTab> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb);
		 } 
		 return list;
	}

	
	public static SqluckyTab findMyTabByScriptPo(DocumentPo scpo) {
		 ObservableList<TreeItem<SqluckyTab>> ls = allTreeItem();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 var tmp =  mytb.getDocumentPo();
			 if(tmp.equals(scpo)) {
				 return mytb;
			 }
					 
		 } 
		 return null;
	}
	
	// 关闭一个脚本 Node cell
	public static void closeAction(TreeItem<SqluckyTab> rootNode) {

		ObservableList<TreeItem<SqluckyTab>>  myTabItemList = rootNode.getChildren();
		TreeItem<SqluckyTab> ctt = NoteTabTree.NoteTabTreeView.getSelectionModel().getSelectedItem();
		SqluckyTab tb = ctt.getValue();
		
		String title = tb.getTitle(); //CommonUtility.tabText(tb);
		String sql = tb.getTabSqlText(); // SqlEditor.getTabSQLText(tb);
		if (title.endsWith("*") && sql.trim().length() > 0) {
			// 是否保存
			final Stage stage = new Stage();

			// 1 保存
			JFXButton okbtn = new JFXButton("Yes");
			okbtn.getStyleClass().add("myAlertBtn");
			okbtn.setOnAction(value -> {
//				CommonAction.saveSqlAction(tb);
//				removeNode(myTabItemList, ctt, tb);
				stage.close();
			});

			// 2 不保存
			JFXButton Nobtn = new JFXButton("No");
			Nobtn.setOnAction(value -> {
//				removeNode(myTabItemList, ctt, tb);
				stage.close();
			});
			// 取消
			JFXButton cancelbtn = new JFXButton("Cancel"); 
			cancelbtn.setOnAction(value -> { 
				stage.close();
			});

			List<Node> btns = new ArrayList<>();

			
			
			btns.add(cancelbtn);
			btns.add(Nobtn);
			btns.add(okbtn);

//			ModalDialog.myConfirmation("Save " + StrUtils.trimRightChar(title, "*") + "?", stage, btns);
		}else {
//			removeNode(myTabItemList, ctt, tb);
		}
		

	}
	
	 
	
	// treeView 右键菜单属性设置
//		public   ChangeListener<TreeItem<SqluckyTab>> treeViewContextMenu(TreeView<SqluckyTab> treeView) {
//			return new ChangeListener<TreeItem<SqluckyTab>>() {
//				@Override
//				public void changed(ObservableValue<? extends TreeItem<SqluckyTab>> observable,
//						TreeItem<SqluckyTab> oldValue, TreeItem<SqluckyTab> newValue) {
//					
//				 
//					 
//					
//					
////					if(! menu.getRefresh().isDisable()) {
////						TreeItem<TreeNodePo>  connItem = ConnItem(newValue);
////						menu.setRefreshAction(connItem);
////					}
//
//				}
//			};
//		}
 
	// 菜单
	public ContextMenu createContextMenu() { 

		ContextMenu contextMenu = new ContextMenu();  
		
		MenuItem close = new MenuItem("Close");
//		MenuItem close.setOnAction(e -> {
//			ScriptTabTree.closeAction(rootNode);
//			// closeAction(rootNode);
//		}); 
		 
		MenuItem Open = new MenuItem("Open Folder");
		Open.setOnAction(e -> {
//			openNoteDir();
			openNoteDir();
		}); 
		 
		
		MenuItem showInFolder = new MenuItem("Show In Folder");
		showInFolder.setOnAction(e -> {
			TreeItem<SqluckyTab> ctt = NoteTabTreeView.getSelectionModel().getSelectedItem();
			SqluckyTab tb = ctt.getValue(); 
			try {
				String fn = tb.getDocumentPo().getFileFullName();
				if(StrUtils.isNotNullOrEmpty(fn)) {
					File file = new File(fn); 
					CommonUtility.openExplorer(file.getParentFile());
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}); 
		
		contextMenu.getItems().addAll( 
				Open, 
				close  , 
				new SeparatorMenuItem(), 
				showInFolder
				);
	
		return contextMenu;
	}
	
	//TODO 打开sql文件
	public static void openNoteDir() {
		try {
			File f = FileOrDirectoryChooser.showDirChooser("Select Directory", ComponentGetter.primaryStage );
			if (f == null) return;
			System.out.println(f.getPath());
			String dirPath = f.getPath();
			
			
			String tabName = "";
			if (StrUtils.isNotNullOrEmpty(f.getPath())) {
				tabName = SaveFile.fileName(f.getPath());
			}
			DocumentPo scpo = new DocumentPo();
//			scpo.setEncode(encode);
			scpo.setFileFullName(f.getAbsolutePath());
//			scpo.setText(val);
			scpo.setTitle(tabName);
			scpo.setFile(f);
		    
			File[] files = f.listFiles();
			for(var file : files) {
				DocumentPo fileNode = new DocumentPo(); 
				fileNode.setFileFullName(file.getAbsolutePath());  
				fileNode.setTitle(file.getName());
				fileNode.setFile(file);
				
				
				SqluckyTab mtb = ComponentGetter.appComponent.sqluckyTab(fileNode); 
				TreeItem<SqluckyTab> item = new TreeItem<>(mtb);
				rootNode.getChildren().add(item);
			}
			
			
		} catch (Exception e) {
			MyAlert.errorAlert( e.getMessage());
			e.printStackTrace();
		}
	}
	
}
