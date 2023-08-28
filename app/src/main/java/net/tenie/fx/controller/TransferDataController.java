package net.tenie.fx.controller;

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckTreeView;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.MyTextEditor;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.ExportDBObjects;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.component.SqluckyTextField;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CodeRunTimeCalculate;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TransferDataUtils;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.component.InfoTree.DBinfoTree;

public class TransferDataController implements Initializable {
	private static Logger logger = LogManager.getLogger(TransferDataController.class);
	private static final String TABLE = "Table";
	private static final String VIEW = "View";
	private static final String FUNCTION = "Function";
	private static final String PROCEDURE = "Procedure";
	private static final String TRIGGER = "Trigger";
	private static final String INDEX = "Index";
	private static final String SEQUENCE = "Sequence";

	public static List<String> errorMsg = new ArrayList<>();

	@FXML
	private AnchorPane tpane;
	@FXML
	private AnchorPane optionPane;

	private static Thread currentThread;
	@FXML
	private Label title;
	@FXML
	private HBox treePane;

	@FXML
	private ComboBox<Label> soDB;
	@FXML
	private ComboBox<Label> soSC;

	@FXML
	private ComboBox<Label> taDB;
	@FXML
	private ComboBox<Label> taSC;

	@FXML
	private JFXCheckBox isIgnore;
	@FXML
	private JFXCheckBox isDel;

	@FXML
	private JFXCheckBox tabData;
	@FXML
	private JFXCheckBox tabStruct;

	@FXML
	private JFXCheckBox chView;
	@FXML
	private JFXCheckBox chFun;
	@FXML
	private JFXCheckBox chPro;
	@FXML
	private JFXCheckBox chTri;
	@FXML
	private JFXCheckBox chIndex;
	@FXML
	private JFXCheckBox chSeq;

	@FXML
	private Label queryLabel;
	@FXML
	private JFXButton execBtn;
	@FXML
	private JFXButton stopBtn;
	@FXML
	private JFXButton hideBtn;
	@FXML
	private JFXButton monBtn;

	@FXML
	private JFXButton upSelBtn;
	@FXML
	private JFXButton downSelBtn;

//	@FXML private TextField	filterTxt;
	private TextField filterTxt;
	// 同步数据行数最大行数
	@FXML
	private TextField amountTxt;

	// 提醒: 同步过程, 函数, 可能依赖序列和互相依赖
	@FXML
	private Label tipsLabel;

	private StackPane spCode;
	private MyCodeArea CodeArea;

	private CheckTreeView<String> checkTreeView;
	private CheckBoxTreeItem<String> root; // CheckBoxTreeItem<String>
//	private CheckBoxTreeItem<String> filterRoot;
	ObservableList<Label> empty = FXCollections.observableArrayList();

	// CheckBoxTreeItem 向上选和向下选中所有item
	private void selectAllObjByUpOrDown(boolean isUpSelect) {
		ObservableList<TreeItem<String>> rootChildren = root.getChildren();
		if (rootChildren != null && rootChildren.size() > 0) {
			for (var item : rootChildren) {
				// 遍历表节点
				var tabList = item.getChildren();
				int idx = 0;
				// 找到列表中选中的 对象 selectObj
				for (var tab : tabList) {
					CheckBoxTreeItem<String> tmpTab = (CheckBoxTreeItem<String>) tab;
					boolean tf = tmpTab.isSelected();
					if (tf) {
						break;
					}
					idx++;
				}

				// 根据selectObj的位置对其上面或下面的对象进行全选
				for (int i = 0; i < tabList.size(); i++) {
					CheckBoxTreeItem<String> tmpTab = (CheckBoxTreeItem<String>) tabList.get(i);
					if (isUpSelect) { // 向上全选
						if (i < idx) {
							tmpTab.setSelected(true);
						} else {
							break;
						}
					} else {// 向下全选
						if (i > idx) {
							tmpTab.setSelected(true);
						}
					}

				}

			}
		}

	}

	// 清除 check Box
	private void cleanCheckBox() {
		isIgnore.setSelected(false);
		isDel.setSelected(false);

		tabData.setSelected(false);
		tabStruct.setSelected(false);

		chView.setSelected(false);
		chFun.setSelected(false);
		chPro.setSelected(false);
		chTri.setSelected(false);
		chIndex.setSelected(false);
		chSeq.setSelected(false);
		amountTxt.setDisable(true);
		amountTxt.setText("");
	}

	// 创建check tree
	public void creatCheckTree() {
		root = new CheckBoxTreeItem<String>("全选");
		root.setExpanded(true);
		checkTreeView = new CheckTreeView<>(root);
		checkTreeView.getStyleClass().add("transferDataTree");
		checkTreeView.getStyleClass().add("my-tag");
		checkTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		treePane.getChildren().addAll(checkTreeView);
		HBox.setHgrow(checkTreeView, Priority.ALWAYS);
	}

	// 显示 日志 输出窗口
	private void monitorShow() {
		Platform.runLater(() -> {
			treePane.getChildren().remove(0);
			treePane.getChildren().add(spCode);
			HBox.setHgrow(spCode, Priority.ALWAYS);
			monBtn.setText("Hide Monitor");
			monBtn.setStyle("-fx-background-color: red;");
		});
	}

	private void monitorHide() {
		treePane.getChildren().remove(0);
		treePane.getChildren().add(checkTreeView);
		monBtn.setText("Monitor");
		monBtn.setStyle("");
	}

	private int logLineSize = 0;

	// 输出log
	private void moniterAppendLog(String str) {
		Platform.runLater(() -> {
			if (logLineSize == 10000) {
				CodeArea.clear();
				logLineSize = 0;
			}
			CodeArea.appendText(str + "\n");
			CodeArea.scrollYToPixel(Double.MAX_VALUE);
			logLineSize++;
		});

	}

	private void moniterAppendErrorLog(List<String> logs) {
		Platform.runLater(() -> {
			for (String str : logs) {
				CodeArea.appendText(str + "\n");
			}

		});

	}

	// 清除log
	private void moniterCleanStr() {
		Platform.runLater(() -> {
			CodeArea.clear();
			logLineSize = 0;
		});
		errorMsg.clear();
	}

	// 按钮变化控制
	private void btnController(boolean tf) {
		Platform.runLater(() -> {
			if (tf) {
				execBtn.setDisable(true);
				execBtn.getStyleClass().add("transfer-run-btn");
				stopBtn.getStyleClass().add("transfer-stop-btn");
			} else {
				execBtn.setDisable(false);
				execBtn.getStyleClass().remove("transfer-run-btn");
				stopBtn.getStyleClass().remove("transfer-stop-btn");
			}
		});
	}

	private void runActionn() {
		btnController(true);
		monitorShow();
		moniterCleanStr();
		currentThread = new Thread() {
			@Override
			public void run() {
				CodeRunTimeCalculate rt = new CodeRunTimeCalculate();
				try {
					moniterAppendLog("........begin ......");
					runBtnAction();
					Platform.runLater(() -> {
						MyAlert.infoAlert("完成");
					});
				} catch (Exception e2) {
					e2.printStackTrace();
					logger.debug(e2.getMessage());
					Platform.runLater(() -> {
						MyAlert.errorAlert(e2.getMessage());
					});
				} finally {
					btnController(false);
					moniterAppendLog("........end ......");
					String rtValStr = rt.getSecond();
					moniterAppendLog("--------执行 时间 : " + rtValStr + " 秒 --------");
				}

			};
		};
		currentThread.start();

	}

	private void stopAction() {
		if (currentThread != null) {
			currentThread.stop();
			btnController(false);
		}
	}

	private void monBtnAction() {
		Object nd = treePane.getChildren().get(0);
		if (nd instanceof StackPane) {
			monitorHide();
		} else {
			monitorShow();
		}

	}

	// 设置 图标 css
	private void setGraphicAndCss() {
		CommonUtils.addCssClass(monBtn, "transfer-btn");
		CommonUtils.addCssClass(amountTxt, "myTextField");
		CommonUtils.addCssClass(upSelBtn, "transfer-btn");
		CommonUtils.addCssClass(downSelBtn, "transfer-btn");

		title.setGraphic(IconGenerator.svgImageDefActive("gears"));
		queryLabel.setGraphic(IconGenerator.svgImageDefActive("search"));
		execBtn.setGraphic(IconGenerator.svgImageDefActive("play"));
		stopBtn.setGraphic(IconGenerator.svgImage("stop", "red"));
		hideBtn.setGraphic(IconGenerator.svgImageUnactive("circle-o"));
		monBtn.setGraphic(IconGenerator.svgImageDefActive("laptop"));
	}

	// 设置按钮 输入框 的action
	private void setAction() {

		// 监控显示隐藏
		monBtn.setOnAction(e -> {
			monBtnAction();
		});
		// 批处理 行数输入框 启用/禁用
		tabData.selectedProperty().addListener((a, old, nw) -> {
			amountTxt.setDisable(!nw);
		});
		// TODO 执行按钮
		execBtn.setOnAction(e -> {
			runActionn();
		});
		// 停止按钮
		stopBtn.setOnAction(e -> {
			stopAction();
		});
		// 隐藏界面
		hideBtn.setOnAction(e -> {
			ComponentGetter.dataTransferStage.hide();
		});

		// TODO 向上选表格
		upSelBtn.setOnAction(e -> {
			selectAllObjByUpOrDown(true);
		});
		// 向下选表格
		downSelBtn.setOnAction(e -> {
			selectAllObjByUpOrDown(false);
		});

		amountTxt.setDisable(true);
		amountTxt.lengthProperty().addListener(CommonListener.textFieldLimit(amountTxt, 4));
		amountTxt.textProperty().addListener(CommonListener.textFieldNumChange(amountTxt));

		AnchorPane.setRightAnchor(hideBtn, 3.0);
	}

	// 初始化方法, 这边在初始化的时候添加按钮的点击事件
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SqluckyTextField tf = new SqluckyTextField();
		filterTxt = tf.getTxt();
		filterTxtInitialize(filterTxt);

		tpane.getChildren().add(tf.getPane());

		setGraphicAndCss();
		tipsLabel.setText("Tips: 同步过程, 函数, 可能依赖序列或互相依赖");

		var sqlCodeArea = new MyTextEditor();
		spCode = sqlCodeArea.getCodeAreaPane();
		CodeArea = sqlCodeArea.getCodeArea();
		setAction();

		// 设置数据库下拉选的值
		TransferDataUtils.setupDBComboBox(soDB, taDB);

		soDB.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			soSC.setItems(empty);
			cleanCheckBox();
			if (newValue != null) {
				String str = newValue.getText();
				soSC.setItems(getSchemaLabels(str));

				root.getChildren().removeAll(root.getChildren());
				filterTxt.clear();
			}

		});
		soSC.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			root.getChildren().removeAll(root.getChildren());
			cleanCheckBox();
			filterTxt.clear();
		});

		taDB.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				String str = newValue.getText();
				taSC.setItems(getSchemaLabels(str));
			}
		});

		creatCheckTree();

		tabData.selectedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue && !tabStruct.isSelected() && soDB.getValue() != null && soSC.getValue() != null) {
				String dbname = soDB.getValue().getText();
				String schename = "";
				if (soSC.getValue() != null) {
					schename = soSC.getValue().getText();
				}

				if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
					TreeItem<TreeNodePo> schemaNode = DBinfoTree.getSchemaNode(dbname, schename);

					addNodeHelper(schemaNode, TABLE);
				}
			}

			removeNode(!newValue && !tabStruct.isSelected(), TABLE);
		});

		tabStruct.selectedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue && !tabData.isSelected() && soDB.getValue() != null && soSC.getValue() != null) {
				String dbname = soDB.getValue().getText();
				String schename = soSC.getValue().getText();
				if (soSC.getValue() != null) {
					schename = soSC.getValue().getText();
				}
				if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
					TreeItem<TreeNodePo> schemaNode = DBinfoTree.getSchemaNode(dbname, schename);

					addNodeHelper(schemaNode, TABLE);
				}
			}

			removeNode(!newValue && !tabData.isSelected(), TABLE);
		});

		chView.selectedProperty().addListener(CheckListener(VIEW));
		chFun.selectedProperty().addListener(CheckListener(FUNCTION));
		chPro.selectedProperty().addListener(CheckListener(PROCEDURE));
		chTri.selectedProperty().addListener(CheckListener(TRIGGER));
		chIndex.selectedProperty().addListener(CheckListener(INDEX));
		chSeq.selectedProperty().addListener(CheckListener(SEQUENCE));

	}

	private boolean checkDbConn() {
		if (soSC.getValue() != null && soDB.getValue() != null && taDB.getValue() != null && taSC.getValue() != null) {
			String dbname = soDB.getValue().getText();
			String schename = soSC.getValue().getText();
			String targetDBName = taDB.getValue().getText();
			String targetSchename = taSC.getValue().getText();

			if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)
					&& StrUtils.isNotNullOrEmpty(targetDBName) && StrUtils.isNotNullOrEmpty(targetSchename)) {
				return true;
			}
		}

		return false;
	}

	private SqluckyConnector soDbpo;
	private SqluckyConnector tarDbpo;

	private void runBtnAction() throws Exception {
		// isThrow 表示出错是否继续执行
		boolean isThrow = !isIgnore.isSelected();
		try {
			if (checkDbConn()) {

				String dbname = soDB.getValue().getText();
				String schename = soSC.getValue().getText();
				String targetDBName = taDB.getValue().getText();
				String targetSchename = taSC.getValue().getText();

				// 复制一个新的连接对象, 避免和 主界面上的连接发生冲突
				SqluckyConnector soDbpo1 = DBConns.get(dbname);
				var soDefSch = soDbpo1.getRealDefaultSchema();
				if (StrUtils.isNullOrEmpty(soDefSch)) {
					schename = "";
				}
				soDbpo = soDbpo1.copyObj(schename);
				Connection soConn = soDbpo.getConn();

				SqluckyConnector tarDbpo1 = DBConns.get(targetDBName);
				var tarDefSch = tarDbpo1.getRealDefaultSchema();
				if (StrUtils.isNullOrEmpty(tarDefSch)) {
					targetSchename = "";
				}
				tarDbpo = tarDbpo1.copyObj(targetSchename);
				Connection tarConn = tarDbpo.getConn();

				moniterAppendLog("-------- 获取 ddl --------");
				List<String> sqls = generateDDL(soConn, schename, targetSchename);

				moniterAppendLog("--------准备执行 sql--------");
				// 执行ddl
				TransferDataUtils.execListSQL(sqls, tarConn, isThrow, this::moniterAppendLog);

				moniterAppendLog("--------准备执行 insert --------");
				synTabData(soConn, tarConn, schename, targetSchename, isThrow);
				moniterAppendLog("--------END insert  --------");
			} else {
				if (isThrow)
					throw new RuntimeException("数据库链接错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			errorMsg.add(e.getMessage());
			moniterAppendLog(e.getMessage());
			if (isThrow)
				throw e;
		}
		if (errorMsg.size() > 0) {
			moniterAppendErrorLog(errorMsg);
		}

	}

	// 获取数据库对象的 创建语句
	private List<String> generateDDL(Connection soConn, String schename, String targetSchename) {
		// 将要执行的sql集合
		List<String> sqls = new ArrayList<>();
		// 表结构
		createSynSql(tabStruct.isSelected(), sqls, soConn, schename, TABLE, targetSchename);
		// 视图同步
		createSynSql(chView.isSelected(), sqls, soConn, schename, VIEW, targetSchename);
		// 函数同步
		createSynSql(chFun.isSelected(), sqls, soConn, schename, FUNCTION, targetSchename);
		// 过程同步
		createSynSql(chPro.isSelected(), sqls, soConn, schename, PROCEDURE, targetSchename);
		// 触发器同步
		createSynSql(chTri.isSelected(), sqls, soConn, schename, TRIGGER, targetSchename);
		// 索引同步
		createSynSql(chIndex.isSelected(), sqls, soConn, schename, INDEX, targetSchename);
		// 序列同步
		createSynSql(chSeq.isSelected(), sqls, soConn, schename, SEQUENCE, targetSchename);

		return sqls;
	}

	// 同步表数据
	private void synTabData(Connection soConn, Connection toConn, String schename, String targetSchename,
			boolean isThrow) throws Exception {
		// 数据同步 执行insert
		if (!tabData.isSelected()) {
			return;
		}

		int amount = -1;
		if (amountTxt.getText().length() > 0) {
			amount = Integer.valueOf(amountTxt.getText());
		}
		boolean delObj = isDel.isSelected();
		TreeItem<String> table = rootSubNode(TABLE);
		try {
			if (table != null) {
				ObservableList<CheckBoxTreeItem<String>> selectNodes = selectNode(table);
				for (CheckBoxTreeItem<String> cb : selectNodes) {
					String tabName = cb.getValue();
					// 删语句
					if (delObj) {
						TransferDataUtils.cleanData(toConn, targetSchename, tabName, this::moniterAppendLog);

					}
//					TransferDataUtils.cleanData(delObj, toConn, targetSchename, tabName, this::moniterAppendLog);
					TransferDataUtils.dbTableDataMigration(soConn, toConn, tabName, schename, targetSchename, amount,
							isThrow, this::moniterAppendLog);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			errorMsg.add(e.getMessage());
			moniterAppendLog(e.getMessage());
			if (isThrow)
				throw e;
		}
	}

	// 试图, 函数, 索引等
	private void createSynSql(boolean tf, List<String> sqls, Connection soConn, String schename, String nodeType,
			String targetSchename) {
		if (tf) {
			boolean delObj = isDel.isSelected();
			TreeItem<String> table = rootSubNode(nodeType);
			if (table != null) {
				ObservableList<CheckBoxTreeItem<String>> selectNodes = selectNode(table);
				for (CheckBoxTreeItem<String> cb : selectNodes) {
					String checkBoxName = cb.getValue();
					// drop语句
					if (delObj) {
						String drop = getDropDDL(nodeType, schename, checkBoxName, targetSchename);
						logger.info(drop);
						sqls.add(drop);
						moniterAppendLog(drop);
					}
					// create语句
					String create = getCreateDDL(soConn, nodeType, schename, checkBoxName, targetSchename);
					logger.info(create);
					sqls.add(create);
					moniterAppendLog(create);

				}
			}

		}
	}

	// 获取drop 语句
	private String getDropDDL(String type, String schename, String objName, String tarSchename) {
		String drop = "";
		ExportDBObjects export = soDbpo.getExportDDL();
		if (type.equals(TABLE)) {
			drop = export.exportDropTable(schename, objName);
		} else if (type.equals(VIEW)) {
			drop = export.exportDropView(schename, objName);
		} else if (type.equals(FUNCTION)) {
			drop = export.exportDropFunction(schename, objName);
		} else if (type.equals(PROCEDURE)) {
			drop = export.exportDropProcedure(schename, objName);
		} else if (type.equals(TRIGGER)) {
			drop = export.exportDropTrigger(schename, objName);
		} else if (type.equals(INDEX)) {
			drop = export.exportDropIndex(schename, objName, null);
		} else if (type.equals(SEQUENCE)) {
			drop = export.exportDropSequence(schename, objName);
		}

		String sorName = TransferDataUtils.getTableName(schename, objName);
		String tarName = TransferDataUtils.getTableName(tarSchename, objName);
		drop = drop.replaceAll(sorName, tarName);
		return drop;
	}

	// 获取Create 语句
	private String getCreateDDL(Connection conn, String type, String schename, String objName, String tarSchename) {
		String createDDL = "";
		ExportDBObjects export = soDbpo.getExportDDL();
		if (type.equals(TABLE)) {
			createDDL = export.exportCreateTable(conn, schename, objName);
		} else if (type.equals(VIEW)) {
			createDDL = export.exportCreateView(conn, schename, objName);
		} else if (type.equals(FUNCTION)) {
			createDDL = export.exportCreateFunction(conn, schename, objName);
		} else if (type.equals(PROCEDURE)) {
			createDDL = export.exportCreateProcedure(conn, schename, objName);
		} else if (type.equals(TRIGGER)) {
			createDDL = export.exportCreateTrigger(conn, schename, objName);
		} else if (type.equals(INDEX)) {
			createDDL = export.exportCreateIndex(conn, schename, objName);
		} else if (type.equals(SEQUENCE)) {
			createDDL = export.exportCreateSequence(conn, schename, objName);
		}

		String sorName = TransferDataUtils.getTableName(schename, objName);
		String tarName = TransferDataUtils.getTableName(tarSchename, objName);
		createDDL = createDDL.replaceAll(sorName, tarName);

		return createDDL;
	}

	// 根据名称获取root 中的子节点
	private TreeItem<String> rootSubNode(String name) {
		ObservableList<TreeItem<String>> childs = root.getChildren();
		for (int i = 0; i < childs.size(); i++) {
			TreeItem<String> val = childs.get(i);
			if (val.getValue().equals(name)) {
				return val;
			}
		}
		return null;
	}

	// 获取节点下选中的子节点
	private ObservableList<CheckBoxTreeItem<String>> selectNode(TreeItem<String> node) {
		ObservableList<CheckBoxTreeItem<String>> selectNodes = FXCollections.observableArrayList();
		ObservableList<TreeItem<String>> nodeSub = node.getChildren();
		for (int i = 0; i < nodeSub.size(); i++) {
			CheckBoxTreeItem<String> sub = (CheckBoxTreeItem<String>) nodeSub.get(i);
			if (sub.isSelected()) {
				selectNodes.add(sub);
			}
		}

		return selectNodes;
	}

	// 获取schema名称列表
	private ObservableList<TreeItem<TreeNodePo>> getSchemaComboBoxList(String dbName) {

		ObservableList<TreeItem<TreeNodePo>> temp = FXCollections.observableArrayList();
		ObservableList<TreeItem<TreeNodePo>> newVal = FXCollections.observableArrayList();

		TreeItem<TreeNodePo> connNode = DBinfoTree.getConnNode(dbName);
		if (connNode != null) {
			if (connNode.getChildren().size() > 0) {
				temp = connNode.getChildren().get(0).getChildren();
				if (temp.size() > 0) {
					for (TreeItem<TreeNodePo> tnp : temp) {
						if (tnp.getChildren().size() > 0) {
							newVal.add(tnp);
						}
					}
				}
			}

		}
		return newVal;
	}

	// 获取schema的下拉的数据
	private ObservableList<Label> getSchemaLabels(String dbName) {
		ObservableList<TreeItem<TreeNodePo>> vals = getSchemaComboBoxList(dbName);
		ObservableList<Label> newVal = FXCollections.observableArrayList();
		newVal.add(new Label(""));
		for (TreeItem<TreeNodePo> val : vals) {
			Label la = new Label(val.getValue().getName());

			newVal.add(la);
		}
		return newVal;
	}

	// 生成数据checkTreeView
	private void addNode(TreeItem<TreeNodePo> item) {
		CheckBoxTreeItem<String> pi = new CheckBoxTreeItem<>(item.getValue().getName());
		ObservableList<CheckBoxTreeItem<String>> newVal = FXCollections.observableArrayList();
		root.getChildren().add(pi);
		ObservableList<TreeItem<TreeNodePo>> subItem = item.getChildren();

		for (TreeItem<TreeNodePo> subNode : subItem) {
			String name = subNode.getValue().getName();
			CheckBoxTreeItem<String> SubCbt = new CheckBoxTreeItem<>(name);

			newVal.add(SubCbt);
		}
		pi.getChildren().addAll(newVal);
	}

	// 数据同步界面
	private ChangeListener<Boolean> CheckListener(String nodeType) {
		return (observable, oldValue, newValue) -> {
			if (newValue && soDB.getValue() != null && soSC.getValue() != null) {
				String dbname = soDB.getValue().getText();
				String schename = soSC.getValue().getText();
				if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
					TreeItem<TreeNodePo> schemaNode = DBinfoTree.getSchemaNode(dbname, schename);

					addNodeHelper(schemaNode, nodeType);
				}
			}
			removeNode(!newValue, nodeType);
		};

	}

	private void addNodeHelper(TreeItem<TreeNodePo> schemaNode, String nodeType) {
		TreeItem<TreeNodePo> val = getSourceNode(schemaNode, nodeType);
		if (val != null) {
			addNode(val);
		}
	}

	private void removeNode(boolean tf, String name) {
		if (tf) {
			for (int i = 0; i < root.getChildren().size(); i++) {
				TreeItem<String> ch = root.getChildren().get(i);
				if (ch.getValue().equals(name)) {
					root.getChildren().remove(i);
					break;
				}
			}
		}
	}

	// 从schema中获取节点, 根据名称
	private TreeItem<TreeNodePo> getSourceNode(TreeItem<TreeNodePo> schemaNode, String name) {
		for (int i = 0; i < schemaNode.getChildren().size(); i++) {
			TreeItem<TreeNodePo> ch = schemaNode.getChildren().get(i);
			if (ch.getValue().getName().equals(name)) {
				return ch;
			}
		}
		return null;
	}

	private ObservableList<TreeItem<String>> temp = FXCollections.observableArrayList();
	private ObservableList<CheckBoxTreeItem<String>> filtList = FXCollections.observableArrayList();

	private void filterTxtInitialize(TextField textField) {
		textField.textProperty().addListener((o, oldVal, newVal) -> {
			// 缓存
			ObservableList<TreeItem<String>> connNodes = root.getChildren();
			temp.clear();
			temp.addAll(connNodes);

			// 恢复
			if (StrUtils.isNullOrEmpty(newVal)) {
				if (root != null) {
					checkTreeView.setRoot(root);
					root.setExpanded(true);
				}
			}

			// 查询时
			if (StrUtils.isNotNullOrEmpty(newVal)) {
				filtList.clear();
				// 遍历每一个连接节点, 在节点下查找到了数据, 就会返回一个新节点对象, 最后使用新节点创建一个新的树
				for (int i = 0; i < temp.size(); i++) {
					CheckBoxTreeItem<String> connNode = (CheckBoxTreeItem<String>) temp.get(i);
					// 查找
					CheckBoxTreeItem<String> nConnNode = connNodeOption(connNode, newVal);
					// 新节点不是NULL 缓存
					if (nConnNode != null) {
						filtList.add(nConnNode);
					}
				}
				// 创建一个新的树根, 将查询数据挂在新的上面
				CheckBoxTreeItem<String> rootNode = new CheckBoxTreeItem<String>("全选");
				rootNode.getChildren().addAll(filtList);
				checkTreeView.setRoot(rootNode); // 使用新的树根
				rootNode.setExpanded(true);
				for (int i = 0; i < rootNode.getChildren().size(); i++) {
					CheckBoxTreeItem<String> subNode = (CheckBoxTreeItem<String>) rootNode.getChildren().get(i);
					subNode.setExpanded(true);
				}
			}

		});
	}

	/*
	 * 传递连接节点, 对其进行过滤 如果节点包含查询内容就返回一个新的节点, 否则返回null
	 */
	private CheckBoxTreeItem<String> connNodeOption(CheckBoxTreeItem<String> node, String queryStr) {
		// 1. 首先看节点是否激活的(有子节点?)
		if (node.getChildren().size() > 0) {
			CheckBoxTreeItem<String> nnode = new CheckBoxTreeItem<String>(node.getValue());
			int count = 0;
			int sz = 0;
			// 开始查找
			ObservableList<CheckBoxTreeItem<String>> val = filter(node.getChildren(), queryStr);
			sz = val.size();
			// 如果找到来数据, 将数据放入到新的数据对象中
			if (sz > 0) {
				nnode.getChildren().setAll(val);
				count += val.size();
			}

			// 如果找到了数据, 将新的数据对象, 放入schema数据对象
			if (count > 0) {
				return nnode;
			}

		}

		return null;
	}

	private static ObservableList<CheckBoxTreeItem<String>> filter(ObservableList<TreeItem<String>> observableList,
			String str) {
		ObservableList<CheckBoxTreeItem<String>> rs = FXCollections.observableArrayList();
		String temp = str.toUpperCase();
		observableList.forEach(v -> {
			if (v.getValue().toUpperCase().contains(temp)) {
				rs.add((CheckBoxTreeItem<String>) v);
			}
		});
		return rs;
	}

	// 创建一行数据
	private ResultSetRowPo createRow(ObservableList<SheetFieldPo> fields) {
		ResultSetPo resultSet = new ResultSetPo(fields);
		ResultSetRowPo row = resultSet.creatRow();
		return row;
	}

}
