package net.tenie.plugin.DataModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtils;
import net.tenie.Sqlucky.sdk.utility.myEvent;
import net.tenie.plugin.DataModel.po.DataModelInfoPo;
import net.tenie.plugin.DataModel.po.DataModelTreeNodePo;
import net.tenie.plugin.DataModel.tools.DataModelUtility;

/**
 * button 的设置, 操作按钮的面板
 * 
 * @author tenie
 *
 */
public class DataModelOperate {

	private static Logger logger = LogManager.getLogger(DataModelOperate.class);
	private VBox optionVbox = new VBox();
	private HBox filterHbox = new HBox();
	private HBox btnHbox = new HBox();
	private TextField txt = new TextField();
	private JFXButton queryBtn = new JFXButton();
	private JFXButton queryExecBtn = new JFXButton();
	private MenuItem importFile = new MenuItem();
	private MenuItem Generate = new MenuItem();

	private JFXButton delBtn = new JFXButton();

	public static Map<String, Double> queryFieldColWidth = new HashMap<>();
	public static Map<String, Double> tableInfoColWidth = new HashMap<>();

	static {
		queryFieldColWidth.put("TABLE", 250.0);
		queryFieldColWidth.put("FIELD", 200.0);
		queryFieldColWidth.put("FIELD_NAME", 250.0);
		queryFieldColWidth.put("COMMENT", 300.0);

		tableInfoColWidth.put("FIELD", 180.0);
		tableInfoColWidth.put("NAME", 220.0);
		tableInfoColWidth.put("COMMENT", 250.0);
		tableInfoColWidth.put("PRIMARY_KEY", 80.0);
	}

	// 存放模型名称 对于的所有表的集合
	Map<String, ObservableList<TreeItem<DataModelTreeNodePo>>> rootMap = new HashMap<>();

	/**
	 * 构造函数
	 */
	public DataModelOperate() {
		// search
		queryBtn.setGraphic(ComponentGetter.getIconDefActive("windows-magnify-browse"));
		queryBtn.setTooltip(CommonUtils.instanceTooltip("Search table & field info "));
		queryBtn.setOnMouseClicked(e -> {
			CommonUtils.leftHideOrShowSecondOperateBox(optionVbox, filterHbox, txt);

		});
		queryExecBtn.setGraphic(ComponentGetter.getIconDefActive("search"));
		queryExecBtn.setTooltip(CommonUtils.instanceTooltip("Search table & field info "));
		queryExecBtn.setOnMouseClicked(e -> {
			queryAction();
		});
		// 查询文本输入框
		txt.setPrefWidth(230);
		txt.getStyleClass().add("myTextField");
		// 文本输入监听
		txt.textProperty().addListener((o, oldStr, newStr) -> {
			if (StrUtils.isNullOrEmpty(newStr)) {
				// 为空，还原
				for (var md : DataModelTabTree.treeRoot.getChildren()) {
					// 通过名称从缓存中获取表集合
					var tbs = rootMap.get(md.getValue().getName());
					if(tbs != null) {
						// 情况表集合
						md.getChildren().clear();
						// 恢复之前缓存的所有表
						md.getChildren().addAll(tbs);
						// 展开模型treeItem
						md.setExpanded(true);
					}
					
				}
			} else {
				delayQuery(v -> queryAction(), 800, txt.getText());

			}
		});
		// 回车后触发查询按钮
		txt.setOnKeyPressed(val -> {
			if (val.getCode() == KeyCode.ENTER) {
				myEvent.btnClick(queryExecBtn);
			}
		});

		AnchorPane txtAP = UiTools.textFieldAddCleanBtn(txt);
		// 导入文件按钮
		MenuButton menuButton = new MenuButton();
		menuButton.setGraphic(IconGenerator.svgImageDefActive("my-import"));

		importFile.setText("Import File Model");
		importFile.setGraphic(ComponentGetter.getIconDefActive("folder-open"));
		importFile.setOnAction(e -> {
			DataModelImportWindow.createModelImportWindow();
		});
		Generate.setText("Generate Model");
		Generate.setGraphic(IconGenerator.svgImage("database", "#7CFC00"));
		Generate.setOnAction(e -> {
			DataModelGenerateWindow.showWindow();
		});

		menuButton.getItems().add(importFile);
		menuButton.getItems().add(Generate);

		// 删除
		delBtn.setGraphic(ComponentGetter.getIconDefActive("trash"));
		delBtn.setTooltip(CommonUtils.instanceTooltip("Import Data Model Json File "));
		delBtn.setOnAction(e -> {
			DataModelUtility.delAction();
		});

		btnHbox.getChildren().addAll(queryBtn, menuButton, delBtn);
		filterHbox.getChildren().addAll(queryExecBtn, txtAP);
		optionVbox.getChildren().addAll(btnHbox);
	}

	// 获取输入框内容, 进行查询
	private void queryAction() {
		String txtVal = txt.getText();
		Platform.runLater(() -> {
			if (StrUtils.isNotNullOrEmpty(txtVal)) {
				SdkComponent.addWaitingPane(-1);
				try {
					exeQueryTable(txtVal);
					// 查询字段
					exeQueryTableFields(txtVal);
				} finally {
					SdkComponent.rmWaitingPane();
				}

			} else {
				txt.requestFocus();
			}

		});
	}

	private ArrayBlockingQueue<Consumer<String>> queue = new ArrayBlockingQueue<>(1);

	// 延迟执行函数
	public void delayQuery(Consumer<String> caller, int milliseconds, String val) {
		if (queue.isEmpty()) {
			queue.offer(caller); // 队列尾部插入元素, 如果队列满了, 返回false, 插入失败

			Thread t = new Thread() {
				@Override
				public void run() {

					try {
						Thread.sleep(milliseconds);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					var cl = queue.poll(); // 从队列取出一个元素
					if (cl != null) {
						cl.accept(val);
					}
				}
			};
			t.start();

		} else {
			logger.debug("delay  Query Thread");
			return;

		}

	}

	// 通过字符串， 查询字段表
	private void exeQueryTableFields(String queryStr) {
		Long modelId = 0L;
		String modelIds = "0";
		var allmodels = DataModelTabTree.treeRoot.getChildren();
		for (Iterator iterator = allmodels.iterator(); iterator.hasNext();) {
			TreeItem<DataModelTreeNodePo> treeItem = (TreeItem<DataModelTreeNodePo>) iterator.next();
			// 模型激活状态有子节点才获取模型的ID
			if (treeItem.getChildren().size() > 0) {
				DataModelTreeNodePo modelepo = treeItem.getValue();
				modelId = modelepo.getModelId();
				modelIds += "," + modelId;
			}
		}

		queryStr = queryStr.toUpperCase();

		var SqluckyConn = SqluckyAppDB.getSqluckyConnector();
		String sql = "SELECT b.DEF_KEY AS TABLE_NAME, a.DEF_KEY AS  FIELD,  a.DEF_NAME AS FIELD_NAME , a.COMMENT FROM DATA_MODEL_TABLE_FIELDS  a\n"
				+ "left join DATA_MODEL_TABLE b on b.ITEM_ID = a.TABLE_ID\n" + "where b.MODEL_ID in ( " + modelIds
				+ ") and ( a.DEF_KEY like '%" + queryStr + "%' or  a.DEF_NAME  like '%" + queryStr
				+ "%' or a.COMMENT like '%" + queryStr + "%' )";
		try {
			// TODO
			List<Node> btns = new ArrayList();
			// 导出excel
			JFXButton exportExcel = new JFXButton();
			exportExcel.setGraphic(IconGenerator.svgImageDefActive("share-square-o"));
			// 独立窗口
			JFXButton dockSideBtn = new JFXButton();
			dockSideBtn.setGraphic(IconGenerator.svgImageDefActive("material-filter-none"));
			dockSideBtn.setTooltip(MyTooltipTool.instance("Dock side"));

			// 查询框
			TextField textField = new TextField();
			textField.getStyleClass().add("myTextField");
			AnchorPane txtAP = UiTools.textFieldAddCleanBtn(textField);
			txtAP.setVisible(false);

			JFXButton query = new JFXButton();
			query.setGraphic(ComponentGetter.getIconDefActive("search"));
			query.setOnAction(e -> {
				txtAP.setVisible(!txtAP.isVisible());
			});

			btns.add(dockSideBtn);
			btns.add(exportExcel);
			btns.add(query);
			btns.add(txtAP);

			MyBottomSheet myBottomSheet = DataModelUtility.dataModelQueryFieldsShow(sql, SqluckyConn, queryStr, btns,
					queryFieldColWidth);
			exportExcel.setOnAction(e -> {
				if (myBottomSheet != null) {
					myBottomSheet.exportExcelAction(false);
				}
			});

			dockSideBtn.setOnMouseClicked(e -> {
				myBottomSheet.dockSide();

			});
			// 添加过滤功能

			// tableView 处理
			SheetDataValue sheetDaV = myBottomSheet.getTableData();
			TableView<ResultSetRowPo> tableView = sheetDaV.getTable();
			ObservableList<ResultSetRowPo> items = tableView.getItems();
			textField.textProperty().addListener((o, oldVal, newVal) -> {
				if (StrUtils.isNotNullOrEmpty(newVal)) {
					TableViewUtils.tableViewAllDataFilter(tableView, items, newVal);
				} else {
					tableView.setItems(items);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeSqluckyConnector(SqluckyConn);
		}
	}

	// 查询表
	private void exeQueryTable(String queryStr) {
		if (rootMap.isEmpty()) {
			for (var md : DataModelTabTree.treeRoot.getChildren()) {
				// 模型下面没有节点跳过
				if (md.getChildren().size() == 0)
					continue;

				// 模型名称
				var modelName = md.getValue().getName();

				// 模型的孩子（表）， 添加到缓存集合中
				ObservableList<TreeItem<DataModelTreeNodePo>> tmps = FXCollections.observableArrayList();
				tmps.addAll(md.getChildren());
				rootMap.put(modelName, tmps);

			}

		}

		// 为空，还原
		for (var md : DataModelTabTree.treeRoot.getChildren()) {
			// 模型下面没有节点跳过
			if (md.getChildren().size() == 0)
				continue;

			// 通过名称从缓存中获取表集合
			var tbs = rootMap.get(md.getValue().getName());
			// 情况表集合
			md.getChildren().clear();
			// 恢复之前缓存的所有表
			md.getChildren().addAll(tbs);
		}
		// 如果输入的字符串有值， 进行查询
		if (StrUtils.isNotNullOrEmpty(queryStr)) {
			queryTable(queryStr, DataModelTabTree.treeRoot.getChildren());

		}
		DataModelTabTree.DataModelTreeView.refresh();

	}

	private void queryTable(String str, ObservableList<TreeItem<DataModelTreeNodePo>> allModels) {
		var upperCaseStr = str.toUpperCase();
		boolean exists = false;
		// 遍历所有模型treeItem
		for (TreeItem<DataModelTreeNodePo> model : allModels) {
			// treeItem 有子节点才继续
			if (model.getChildren().size() > 0) {
				// 新表集合， 存放查找到的表
				ObservableList<TreeItem<DataModelTreeNodePo>> filterTable = FXCollections.observableArrayList();
				var allTables = model.getChildren();
				allTables.forEach(v -> {
					// 数据库里表名称
					var dbTabkey = v.getValue().getName().toUpperCase();
					var tabpo = v.getValue().getTablepo();
					// 表的描述名称
					var tabName = "";
					// 备注
					var comment = "";
					if (tabpo != null) {
						if (tabpo.getDefName() != null) {
							tabName = tabpo.getDefName().toUpperCase();
						}
						if (tabpo.getComment() != null) {
							comment = tabpo.getComment().toLowerCase();
						}

					}

					// 表名， 表中文名， 表的备注根据查询字符串能匹配就加入到新表集合
					if (dbTabkey.contains(upperCaseStr) || tabName.contains(upperCaseStr)
							|| comment.contains(upperCaseStr)) {
						filterTable.add(v);
					}
				});

				if (filterTable.size() > 0) {
					exists = true;
					// 清空原来的表集合
					model.getChildren().clear();
					// 加入新的表集合
					model.getChildren().addAll(filterTable);
				} else { // 没找到
					// 清空原来的表集合
					model.getChildren().clear();
				}
				if (model.getChildren().size() > 0) {
					model.setExpanded(exists);
				}
			}
		}
		// 展开模型treeItem
		if (DataModelTabTree.treeRoot.getChildren().size() > 0)
			DataModelTabTree.treeRoot.getChildren().get(0).setExpanded(exists);
	}

	// 查询字符串 ObservableList<TreeItem<TreeNodePo>> rs =
	// FXCollections.observableArrayList();
	private static ObservableList<TreeItem<DataModelTreeNodePo>> filter(
			ObservableList<TreeItem<DataModelTreeNodePo>> val, String str) {
		ObservableList<TreeItem<DataModelTreeNodePo>> rs = FXCollections.observableArrayList();
		String temp = str.toUpperCase();
		val.forEach(v -> {
			if (v.getValue().getName().toUpperCase().contains(temp)) {
				rs.add(v);
			}
		});
		return rs;
	}

	public static DataModelInfoPo readJosnModel(String fileName, String encode) {
		DataModelInfoPo DataModelPoVal = null;
		File f = new File(fileName);
		try {
			String val = FileUtils.readFileToString(f, encode);
			if (val != null && !"".equals(val)) {
				DataModelPoVal = JSONObject.parseObject(val, DataModelInfoPo.class);
			}
		} catch (IOException e) {
			e.printStackTrace();
			MyAlert.errorAlert(e.getMessage());
		}

		return DataModelPoVal;
	}

	public HBox getBtnHbox() {
		return btnHbox;
	}

	public VBox getOptionVbox() {
		return optionVbox;
	}

	public HBox getFilterHbox() {
		return filterHbox;
	}

	public TextField getTxt() {
		return txt;
	}

}
