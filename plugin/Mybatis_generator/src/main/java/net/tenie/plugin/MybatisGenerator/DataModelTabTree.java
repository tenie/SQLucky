package net.tenie.plugin.MybatisGenerator;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.MybatisGenerator.po.DataModelTablePo;
import net.tenie.plugin.MybatisGenerator.po.DataModelTreeNodePo;
import net.tenie.plugin.MybatisGenerator.tools.DataModelDAO;
import net.tenie.plugin.MybatisGenerator.tools.DataModelUtility;

/**
 * 
 * @author tenie
 *
 */
public class DataModelTabTree {

	public static TreeView<DataModelTreeNodePo> DataModelTreeView;
	public static TreeItem<DataModelTreeNodePo> treeRoot;
//	public static VBox vbox = new VBox();
	
//	public static TreeView<DataModelTreeNodePo> treeView ;
	private DataModelOption optionPanel ;
	private Pane btnsBox ; 
	String filePath = "";

	public DataModelTabTree() {
		createDataModelTreeView();
	}
	
	

	// 节点view
	public TreeView<DataModelTreeNodePo> createDataModelTreeView() {
		
		DataModelTreeNodePo treeNodePo = new DataModelTreeNodePo();
		treeRoot = new TreeItem<>(treeNodePo);
		DataModelTreeView = new TreeView<>(treeRoot);
		DataModelTreeView.getStyleClass().add("my-tag");
		DataModelTreeView.setShowRoot(false);
		// 展示连接
		if (treeRoot.getChildren().size() > 0)
			DataModelTreeView.getSelectionModel().select(treeRoot.getChildren().get(0)); // 选中节点
		// 右键菜单
		DataModelTabTreeContextMenu contextMenu = new DataModelTabTreeContextMenu(this);
		DataModelTreeView.setContextMenu(contextMenu.getContextMenu());
		// 选中监听事件
//		treeView.getSelectionModel().selectedItemProperty().addListener(treeViewContextMenu(treeView));
		DataModelTreeView.getSelectionModel().select(treeRoot);

//		DataModelTreeView = treeView;
		
		
		// 显示设置, 双击事件也在这里设置
		DataModelTreeView.setCellFactory(new DataModelNodeCellFactory());
		
		
	    optionPanel = new DataModelOption(); 
		btnsBox  = optionPanel.getOptionVbox();

//		vbox.getStyleClass().add("myTreeView-vbox");
//		vbox.getChildren().addAll( filterHbox, treeView);
//		vbox.getStyleClass().add("myModalDialog");
//		VBox.setVgrow(treeView, Priority.ALWAYS);
		
		// 恢复上次的数据
		DataModelUtility.recoverModelInfoNode(treeRoot);
		return DataModelTreeView;
	}

	
	/**
	 * 双击模型节点时， 查询数据库表， 添加表节点都模型节点下
	 * @param mdTreeNode
	 */
	public static void modelInfoTreeAddTableTreeNode(TreeItem<DataModelTreeNodePo>  mdTreeNode) {
		List<TreeItem<DataModelTreeNodePo>> nodels = new ArrayList<>();
		
		DataModelTreeNodePo dmpo = mdTreeNode.getValue();
		Long id = dmpo.getModelId();
		List<DataModelTablePo> tableLs = DataModelDAO.selectDMTable(id);
		
		for( var table : tableLs) {
			DataModelTreeNodePo ndpo = new DataModelTreeNodePo(table);
			TreeItem<DataModelTreeNodePo> treeNode = DataModelUtility.createItemNode(ndpo);
			nodels.add(treeNode);
		}
		if (nodels.size() > 0) {
			Platform.runLater(() -> {
				mdTreeNode.getChildren().addAll(nodels);
				DataModelTreeView.getSelectionModel().select(mdTreeNode.getChildren().get(0)); // 选中节点
			});
		}
		
	}
	
	
	static String tableName = "";
	/**
	 * 字段展示
	 * @return
	 */
	public static void showFields(Long tableId) {
		SdkComponent.addWaitingPane(-1);
		List<Node> rs = tableInfoToLabels(tableId);
		var conn = SqluckyAppDB.getConn();
		String sql = "select DEF_KEY as FIELD, DEF_NAME AS NAME , COMMENT, TYPE_FULL_NAME, PRIMARY_KEY, NOT_NULL, AUTO_INCREMENT, DEFAULT_VALUE,PRIMARY_KEY_NAME,NOT_NULL_NAME, AUTO_INCREMENT_NAME  from DATA_MODEL_TABLE_FIELDS where TABLE_ID = "+ tableId;
		try {
			SdkComponent.dataModelQueryFieldsShow(sql, conn , tableName, rs, DataModelOption.tableInfoColWidth);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
			SdkComponent.rmWaitingPane();
		}
		
		
	}
	
	/**
	 * 表信息， 转换为label
	 * @return
	 */
	public static List<Node>  tableInfoToLabels(Long tableId ) {
		DataModelTablePo tbpo = DataModelDAO.selectTableById(tableId);
		String table = tbpo.getDefKey();
		tableName = tbpo.getDefName();
		String tableComment = tbpo.getComment();
		Label t = new Label(table + "   ");
		t.setGraphic(IconGenerator.svgImageUnactive("table"));
		Label tN = new Label(tableName  + "   ");
		tN.setGraphic(IconGenerator.svgImageUnactive("table"));
		Label tC = new Label(tableComment  + "   ");
		tC.setGraphic(IconGenerator.svgImageUnactive("table"));
		List<Node> rs = new ArrayList<>();
		
		if(StrUtils.isNullOrEmpty(tableName)) {
			tableName = table;
		}
		
		rs.add(t);
		rs.add(tN);
		rs.add(tC);
		return rs;
	}
	
	// 所有连接节点
	public static ObservableList<TreeItem<DataModelTreeNodePo>> allTreeItem() {
		ObservableList<TreeItem<DataModelTreeNodePo>> val = DataModelTreeView.getRoot().getChildren();
		return val;
	}

	// 获取当前选中的节点
	public static TreeItem<DataModelTreeNodePo> getScriptViewCurrentItem() {
		TreeItem<DataModelTreeNodePo> ctt = DataModelTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	// 给root节点加元素
	public static void treeRootAddItem(TreeItem<DataModelTreeNodePo> item) {
		TreeItem<DataModelTreeNodePo> rootNode = DataModelTreeView.getRoot();
		rootNode.getChildren().add(item);
	}

	// 给root节点加元素
	public static void treeRootAddItem(DataModelTreeNodePo mytab) {
		TreeItem<DataModelTreeNodePo> item = new TreeItem<DataModelTreeNodePo>(mytab);
		treeRootAddItem(item);
	}





	public Pane getBtnsBox() {
		return btnsBox;
	}



	public void setBtnsBox(Pane btnsBox) {
		this.btnsBox = btnsBox;
	}



	public static TreeView<DataModelTreeNodePo> getDataModelTreeView() {
		return DataModelTreeView;
	}



	public static void setDataModelTreeView(TreeView<DataModelTreeNodePo> dataModelTreeView) {
		DataModelTreeView = dataModelTreeView;
	}
	
	
	public static TreeItem<DataModelTreeNodePo> currentSelectItem() {
		TreeItem<DataModelTreeNodePo> item = DataModelTabTree.DataModelTreeView.getSelectionModel().getSelectedItem();
		return item;
	}



	public DataModelOption getOptionPanel() {
		return optionPanel;
	}



	public void setOptionPanel(DataModelOption optionPanel) {
		this.optionPanel = optionPanel;
	}

	// 菜单
//	public ContextMenu createContextMenu() {
//
//		ContextMenu contextMenu = new ContextMenu();
//
//		MenuItem Clean = new MenuItem("Clean ");
//		Clean.setOnAction(e -> {
//			treeRoot.getChildren().clear();
//			filePath = "";
//			ComponentGetter.appComponent.saveData(DataModelDelegateImpl.pluginName, "dir_path", "");
//		});
//
//		MenuItem Refresh = new MenuItem("Refresh ");
//		Refresh.setOnAction(e -> {
//			var itm = DataModelTreeView.getSelectionModel().getSelectedItem();
//			var ParentNode = itm.getParent();
//			if (Objects.equals(treeRoot, ParentNode)) {
//				if (treeRoot.getChildren().size() > 0) {
//					treeRoot.getChildren().clear();
//				}
//				openNoteDir(ParentNode, new File(filePath));
//			} else {
//				if (ParentNode.getChildren().size() > 0) {
//					ParentNode.getChildren().clear();
//				}
////				var parentFile = ParentNode.getValue().getFile();
////				openNoteDir(ParentNode, parentFile);
//			}
//		});
//
//		MenuItem newFile = new MenuItem("New File ");
//		newFile.setOnAction(e -> {
//			var itm = DataModelTreeView.getSelectionModel().getSelectedItem();
//			var ParentNode = itm.getParent();
//			if (Objects.equals(treeRoot, ParentNode)) {
//				newFileNode(treeRoot, filePath);
//			} else {
////				var parentFile = ParentNode.getValue().getFile();
////				newFileNode(ParentNode, parentFile.getAbsolutePath());
//			}
//		});
//
//		MenuItem Open = new MenuItem("Open Folder");
//		Open.setOnAction(e -> {
//			File f = FileOrDirectoryChooser.showDirChooser("Select Directory", ComponentGetter.primaryStage);
//			if (f != null && f.exists()) {
//				filePath = f.getAbsolutePath();
//				if (treeRoot.getChildren().size() >= 0) {
//					treeRoot.getChildren().clear();
//				}
//				openNoteDir(treeRoot, f);
//				ComponentGetter.appComponent.saveData(DataModelDelegateImpl.pluginName, "dir_path", filePath);
//
//			}
//		});
//
//		MenuItem close = new MenuItem("Close");
//		close.setOnAction(e -> {
//			var itm = DataModelTreeView.getSelectionModel().getSelectedItem();
//			DataModelTabTree.closeAction(itm);
//		});
//
//		MenuItem deleteFile = new MenuItem("Delete File");
//		deleteFile.setOnAction(e -> {
//			var itm = DataModelTreeView.getSelectionModel().getSelectedItem();
//			File file = itm.getValue().getFile();
//			String fileTyep = "File";
//			if (file.isDirectory()) {
//				fileTyep = "Folder";
//			}
//
//			List<Node> btns = new ArrayList<>();
//			final Stage stage = new Stage();
//			JFXButton okbtn = new JFXButton("Yes");
//			okbtn.getStyleClass().add("myAlertBtn");
//			okbtn.setOnAction(value -> {
//				file.delete();
//				var pa = itm.getParent();
//				pa.getChildren().remove(itm);
//				stage.close();
//			});
//
//			// 取消
//			JFXButton cancelbtn = new JFXButton("Cancel");
//			cancelbtn.setOnAction(value -> {
//				stage.close();
//			});
//
//			btns.add(cancelbtn);
//			btns.add(okbtn);
//			MyAlert.myConfirmation("Delete  " + fileTyep + ": " + file.getAbsolutePath() + " ? ", stage, btns);
//
//		});
//
//		MenuItem showInFolder = new MenuItem("Show In Folder");
//		showInFolder.setOnAction(e -> {
//			TreeItem<DataModelTreeNodePo> ctt = DataModelTreeView.getSelectionModel().getSelectedItem();
//			SqluckyTab tb = ctt.getValue();
//			try {
//				String fn = tb.getDocumentPo().getFileFullName();
//				if (StrUtils.isNotNullOrEmpty(fn)) {
//					File file = new File(fn);
//					CommonUtility.openExplorer(file.getParentFile());
//				}
//
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		});
//
//		contextMenu.getItems().addAll(Open, close, new SeparatorMenuItem(), newFile, deleteFile, Refresh, Clean,
//				new SeparatorMenuItem(), showInFolder);
//		contextMenu.setOnShowing(e -> {
//			var itm = DataModelTreeView.getSelectionModel().getSelectedItem();
//
//			if (itm != null && itm.getValue() != null && itm.getValue().getFile() != null
//					&& itm.getValue().getFile().isFile()) {
//				deleteFile.setDisable(false);
//			} else {
//				deleteFile.setDisable(true);
//			}
//
//			if (itm != null && itm.getValue() != null && itm.getValue().getFile() != null) {
//				showInFolder.setDisable(false);
//			} else {
//				showInFolder.setDisable(true);
//			}
//
//		});
//		return contextMenu;
//	}

	

}
