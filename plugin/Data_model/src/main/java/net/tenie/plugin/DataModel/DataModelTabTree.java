package net.tenie.plugin.DataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import net.tenie.Sqlucky.sdk.component.*;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheetAction;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.DataModel.po.DataModelTablePo;
import net.tenie.plugin.DataModel.po.DataModelTreeNodePo;
import net.tenie.plugin.DataModel.tools.DataModelDAO;
import net.tenie.plugin.DataModel.tools.DataModelUtility;

/**
 * 
 * @author tenie
 *
 */
public class DataModelTabTree extends SqluckyTitledPane{

	public static TreeView<DataModelTreeNodePo> DataModelTreeView;
	public static TreeItem<DataModelTreeNodePo> treeRoot;
	public	static DataModelOperate optionPanel;
	private Pane btnsBox;
	String filePath = "";

	public DataModelTabTree(String pluginName) {
		createDataModelTreeView();
//		this.setName(pluginName);
        this.setBtnsBox(btnsBox);
        this.setText(pluginName);
        CommonUtils.addCssClass(this, "titledPane-color");
        this.setContent(DataModelTreeView);
	}

	// 节点view
	public TreeView<DataModelTreeNodePo> createDataModelTreeView() {

		DataModelTreeNodePo treeNodePo = new DataModelTreeNodePo();
		treeRoot = new TreeItem<>(treeNodePo);
		DataModelTreeView = new TreeView<>(treeRoot);
		DataModelTreeView.getStyleClass().add("my-tag");
		DataModelTreeView.setShowRoot(false);
		// 展示连接
		if (!treeRoot.getChildren().isEmpty()) {
			// 选中节点
            DataModelTreeView.getSelectionModel().select(treeRoot.getChildren().getFirst());
        }
		// 右键菜单
		DataModelTabTreeContextMenu contextMenu = new DataModelTabTreeContextMenu(this);
		DataModelTreeView.setContextMenu(contextMenu.getContextMenu());
		// 选中监听事件
		DataModelTreeView.getSelectionModel().select(treeRoot);

		// 显示设置, 双击事件也在这里设置
		DataModelTreeView.setCellFactory(new DataModelNodeCellFactory());

		optionPanel = new DataModelOperate(this);

		// 设置查询插口的显示/隐藏
		this.setShowFinder(s->{
			CommonUtils.leftHideOrShowSecondOperateBox(optionPanel.getOptionVbox(), optionPanel.getFilterHbox(), optionPanel.getTxt());
		});
		this.setHideFinder(s->{
			optionPanel.getTxt().clear();
			CommonUtils.leftHideSecondOperateBox(optionPanel.getOptionVbox(), optionPanel.getFilterHbox());

		});
		btnsBox = optionPanel.getOptionVbox();

		// 恢复上次的数据
		DataModelUtility.recoverModelInfoNode(treeRoot);
		return DataModelTreeView;
	}

	/**
	 * 双击模型节点时， 查询数据库表， 添加表节点都模型节点下
	 * 
	 * @param mdTreeNode
	 */
	public static void modelInfoTreeAddTableTreeNode(TreeItem<DataModelTreeNodePo> mdTreeNode) {
		var queryField = optionPanel.getTxt();
		queryField.clear();
		List<TreeItem<DataModelTreeNodePo>> nodels = new ArrayList<>();

		DataModelTreeNodePo dmpo = mdTreeNode.getValue();
		Long id = dmpo.getModelId();
		List<DataModelTablePo> tableLs = DataModelDAO.selectDMTable(id);

		for (var table : tableLs) {
			DataModelTreeNodePo ndpo = new DataModelTreeNodePo(table);
			TreeItem<DataModelTreeNodePo> treeNode = DataModelUtility.createItemNode(ndpo);
			nodels.add(treeNode);
		}
		if (!nodels.isEmpty()) {
			mdTreeNode.getValue().setActive(true);
			Platform.runLater(() -> {
				mdTreeNode.getChildren().clear();
				mdTreeNode.getChildren().addAll(nodels);
				DataModelTreeView.getSelectionModel().select(mdTreeNode.getChildren().get(0)); // 选中节点
				
				// 将模型的子节点数据集放入缓存(搜索会用到)
				Map<String, ObservableList<TreeItem<DataModelTreeNodePo>>> rootMap = DataModelOperate.rootMap;
				// 模型名称
				var modelName = mdTreeNode.getValue().getName();
				 
				// 模型的孩子（表）， 添加到缓存集合中
				ObservableList<TreeItem<DataModelTreeNodePo>> tmps = FXCollections.observableArrayList();
				tmps.addAll(mdTreeNode.getChildren());
				rootMap.put(modelName, tmps);
			
			});
			
		} 

		
		 

	}

	static String tableName = "";

	private static void LabelcontextMenu(Label lb){
		ContextMenu contextMenu = new ContextMenu();
		MenuItem copyText = new MenuItem("Copy Text");
		copyText.setGraphic(IconGenerator.svgImageDefActive("clipboard"));
		copyText.setOnAction(e -> {
			CommonUtils.setClipboardVal(lb.getText());
		});
		contextMenu.getItems().add(copyText);
		lb.setContextMenu(contextMenu);
	}
	/**
	 * 界面上展示, 字段数据的表格
	 * 
	 * @return
	 */
	public static void showFields(Long tableId) {
		SqluckyConnector SqluckyConn = null;
		try {
			SdkComponent.addWaitingPane(-1);
//			MyBottomSheet myBottomSheet = null;
			// 操作区, 控件
			DataModelTablePo tbpo = DataModelDAO.selectTableById(tableId);
			if (tbpo != null) {
			}
			String table = tbpo.getDefKey();
			tableName = tbpo.getDefName();
			String tableComment = tbpo.getComment();
			Label tabNameLabel = new Label(table + "   ");
			tabNameLabel.setGraphic(IconGenerator.svgImageUnactive("table"));
			LabelcontextMenu(tabNameLabel);
			Label tabNameLabel2 = new Label(tableName + "   ");
			tabNameLabel2.setGraphic(IconGenerator.svgImageUnactive("table"));
			LabelcontextMenu(tabNameLabel2);
			Label commentLabel = new Label(tableComment + "   ");
			commentLabel.setGraphic(IconGenerator.svgImageUnactive("table"));
			LabelcontextMenu(commentLabel);

			if (StrUtils.isNullOrEmpty(tableName)) {
				tableName = table;
			}
			// 查询框
			TextField textField = new TextField();
			textField.getStyleClass().add("myTextField");
			textField.setMinWidth(150.0);
			AnchorPane txtAP = UiTools.textFieldAddCleanBtn(textField);
//			txtAP.setVisible(false);

			JFXButton query = new JFXButton();
			query.setGraphic(ComponentGetter.getIconDefActive("search"));
			query.setOnAction(e -> {
				txtAP.setVisible(!txtAP.isVisible());
			});
			// 导出excel
			JFXButton exportExcel = new JFXButton();
			exportExcel.setTooltip(MyTooltipTool.instance("Export Excel"));
			exportExcel.setGraphic(IconGenerator.svgImageDefActive("share-square-o"));

			// 字段信息保存按钮
			JFXButton saveBtn = new JFXButton();
			saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
			saveBtn.setDisable(true);

			// 独立窗口
			JFXButton dockSideBtn = new JFXButton();
			dockSideBtn.setGraphic(IconGenerator.svgImageDefActive("material-filter-none"));

			dockSideBtn.setTooltip(MyTooltipTool.instance("Dock side"));
//			dockSideBtn.setDisable(disable);

			List<Node> tableHeadOptionNode = new ArrayList<>();
			tableHeadOptionNode.add(tabNameLabel);
			tableHeadOptionNode.add(tabNameLabel2);
			tableHeadOptionNode.add(commentLabel);
			tableHeadOptionNode.add(saveBtn);

			tableHeadOptionNode.add(exportExcel);

			tableHeadOptionNode.add(dockSideBtn);

			tableHeadOptionNode.add(query);
			tableHeadOptionNode.add(txtAP);

			// 查询数据库, 获取字段信息
//			conn = SqluckyAppDB.getConn();
			SqluckyConn = SqluckyAppDB.getSqluckyConnector();
			String sql = "select DEF_KEY as FIELD," + " DEF_NAME AS NAME , " + "COMMENT, " + "TYPE_FULL_NAME, "
					+ "PRIMARY_KEY, " + "NOT_NULL, " + "AUTO_INCREMENT, " + "DEFAULT_VALUE, " + "PRIMARY_KEY_NAME, "
					+ "NOT_NULL_NAME, " + "AUTO_INCREMENT_NAME  " + "from DATA_MODEL_TABLE_FIELDS where TABLE_ID = "
					+ tableId;

//			
			MyBottomSheet myBottomSheet = DataModelUtility.dataModelQueryFieldsShow(sql, SqluckyConn, tableName,
					tableHeadOptionNode, DataModelOperate.tableInfoColWidth);
			SheetDataValue sheetDaV = myBottomSheet.getTableData();
			sheetDaV.setSaveBtn(saveBtn);
			dockSideBtn.setOnMouseClicked(e -> {
				MyBottomSheetAction.dockSide(myBottomSheet);

			});

			// 保存按钮处理
			ResultSetPo resultSetPo = sheetDaV.getDataRs();
			saveBtn.setOnAction(e -> {
				var connObj = SqluckyAppDB.getConn();
				try {
					DataModelDAO.saveTableInfo(myBottomSheet, saveBtn, resultSetPo, tableId, connObj);
				} finally {
					SqluckyAppDB.closeConn(connObj);
				}

			});
			exportExcel.setOnAction(e -> {
				MyBottomSheetAction.exportExcelAction(myBottomSheet,false);
			});

			// tableView 处理
			TableView<ResultSetRowPo> tableView = sheetDaV.getTable();
			ObservableList<ResultSetRowPo> items = tableView.getItems();

			// 添加过滤功能
			textField.textProperty().addListener((o, oldVal, newVal) -> {
				if (StrUtils.isNotNullOrEmpty(newVal)) {
					bindTableViewFilter(tableView, items, newVal);
				} else {
					tableView.setItems(items);
				}

			});

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (SqluckyConn != null) {
                SqluckyAppDB.closeSqluckyConnector(SqluckyConn);
            }
			SdkComponent.rmWaitingPane();
		}

	}

	/**
	 * 查询过滤
	 * 
	 * @param tableView
	 * @param observableList
	 * @param newValue
	 */
	public static final void bindTableViewFilter(TableView<ResultSetRowPo> tableView,
			ObservableList<ResultSetRowPo> observableList, String newValue) {
		FilteredList<ResultSetRowPo> filteredData = new FilteredList<>(observableList, p -> true);
		filteredData.setPredicate(entity -> {
			String upperCaseVal = newValue.toUpperCase();

			ObservableList<ResultSetCellPo> rowDatas = entity.getRowDatas();
			for (var cell : rowDatas) {
				String cellFieldName = cell.getField().getColumnLabel().get();
				if ("FIELD".equals(cellFieldName) || "NAME".equals(cellFieldName) || "COMMENT".equals(cellFieldName)) {
					String cellVal = cell.getCellData().get();
					if (cellVal.toUpperCase().contains(upperCaseVal)) {
						return true;
					}
				}

			}

			return false;
		});
		SortedList<ResultSetRowPo> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);
	}

	/**
	 * 表头option区域
	 * 
	 * @return 返回表信息的Label, 查询框
	 */
	public static List<Node> tableHeadOptionNodes2(Long tableId) {
		DataModelTablePo tbpo = DataModelDAO.selectTableById(tableId);
		String table = tbpo.getDefKey();
		tableName = tbpo.getDefName();
		String tableComment = tbpo.getComment();
		Label t = new Label(table + "   ");
		t.setGraphic(IconGenerator.svgImageUnactive("table"));
		Label tN = new Label(tableName + "   ");
		tN.setGraphic(IconGenerator.svgImageUnactive("table"));
		Label tC = new Label(tableComment + "   ");
		tC.setGraphic(IconGenerator.svgImageUnactive("table"));
		List<Node> rs = new ArrayList<>();

		if (StrUtils.isNullOrEmpty(tableName)) {
			tableName = table;
		}

		// 查询框
		JFXButton query = new JFXButton();
		query.setGraphic(ComponentGetter.getIconDefActive("search"));
		TextField textField = new TextField();

		textField.getStyleClass().add("myTextField");

		rs.add(t);
		rs.add(tN);
		rs.add(tC);
		rs.add(query);
		rs.add(textField);

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

	@Override
    public Pane getBtnsBox() {
		return btnsBox;
	}

	@Override
    public void setBtnsBox(Pane btnsBox) {
		this.btnsBox = btnsBox;
	}


	public static TreeItem<DataModelTreeNodePo> currentSelectItem() {
		TreeItem<DataModelTreeNodePo> item = DataModelTabTree.DataModelTreeView.getSelectionModel().getSelectedItem();
		return item;
	}

	public DataModelOperate getOptionPanel() {
		return optionPanel;
	}

}
