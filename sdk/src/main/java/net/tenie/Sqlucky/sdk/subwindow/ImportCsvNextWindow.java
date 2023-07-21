package net.tenie.Sqlucky.sdk.subwindow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.cell.ComboBox2TableCell;

import com.jfoenix.controls.JFXCheckBox;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.MyTableCellTextField2ReadOnly;
import net.tenie.Sqlucky.sdk.db.DaoTools;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.excel.CsvToDB;
import net.tenie.Sqlucky.sdk.excel.CsvUtil;
import net.tenie.Sqlucky.sdk.excel.ExcelHeadCellInfo;
import net.tenie.Sqlucky.sdk.po.ExcelFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * Csv导入
 * 
 * @author tenie
 *
 */
public class ImportCsvNextWindow {

	private Stage stage;
	private static Logger logger = LogManager.getLogger(ImportCsvNextWindow.class);

	private TextField beginIdTF;
	private TextField conuntTF;
	private TextField tfFilePath;
	private JFXCheckBox saveSqlCheckBox;
	private JFXCheckBox onlySave;
	private String csvFile;
	private String tableName;
	private ObservableList<ExcelFieldPo> csvFields;
	private SqluckyConnector sqluckyConn;
	private String splitSymbol = "";

	public void showWindow(SqluckyConnector dbc, String tableNameVal, String csvFilePath, Stage parentStage,
			String sSymbol) {
		sqluckyConn = dbc;
		csvFile = csvFilePath;
		tableName = tableNameVal;
		splitSymbol = sSymbol;

		LoadingAnimation.loadingAnimation("Loading....", v -> {

			VBox tbox = tableBox(dbc, tableName, csvFile);
			if (tbox == null) {
				MyAlert.errorAlert("没有表<" + tableNameVal + ">的信息, 请确保表存在!");
				return;
			}
			Platform.runLater(() -> {
				layout(tbox, tableName);
				parentStage.close();
			});

		});

	}

	/*
	 * 对数据库表字段转换为一个表格, 返回null表示没有找到表的信息
	 */
	public VBox tableBox(SqluckyConnector dbc, String tablename, String CsvFile) {
		try {
			ObservableList<SheetFieldPo> fields = DaoTools.tableFields(dbc.getConn(), tablename);
			if (fields == null || fields.size() == 0) {
				return null;
			}
			for (int i = 0; i < fields.size(); i++) {
				SheetFieldPo p = fields.get(i);
				StringProperty strp = new SimpleStringProperty("");
				p.setValue(strp);
			}

			csvFields = FXCollections.observableArrayList();
			for (var fd : fields) {
				ExcelFieldPo excelpo = new ExcelFieldPo(fd);
				csvFields.add(excelpo);
			}

			VBox tableBox = tableFiledMapCsvRowBox(tablename, csvFields, csvFile);
			return tableBox;
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return new VBox();
	}

	/**
	 * csv 文件头信息
	 * 
	 * @param excelFile
	 * @return
	 */
	private String[] csvHeadArray(String csvFile, ObservableList<ExcelFieldPo> fields) {
		List<ExcelHeadCellInfo> row1 = CsvUtil.readCsvHeadInfo(csvFile, splitSymbol);

		if (row1 != null) {

			String[] headArr = new String[row1.size()];
			List<String> selectVal = new ArrayList<>();
			for (int i = 0; i < row1.size(); i++) {
				ExcelHeadCellInfo cell = row1.get(i);
				String val = (cell.getCellIdx() + 1) + " - " + cell.getCellVal();
				headArr[i] = val;
				selectVal.add(val);
			}

			for (int j = 0; (j < selectVal.size()) && (j < fields.size()); j++) {
				var tmp = fields.get(j);
				tmp.setExcelRowInfo(selectVal);
				tmp.getExcelRowVal().set(selectVal.get(j));
			}

			return headArr;
		}

		return new String[0];
	}

	public VBox tableFiledMapCsvRowBox(String tableName, ObservableList<ExcelFieldPo> fields, String csvFile) {

		Label tf1 = new Label();
		tf1.setPrefWidth(500);
		tf1.setPadding(new Insets(8));

		// table
		var tableView = createTableView(fields, tf1);
		VBox subvb = new VBox();
		subvb.setPrefHeight(400);

		FlowPane topfp = topPane(tableView, fields);
		subvb.getChildren().add(topfp);

		subvb.getChildren().add(tableView);
		VBox.setVgrow(tableView, Priority.ALWAYS);
		subvb.getChildren().add(tf1);

		return subvb;
	}

	private TableView<ExcelFieldPo> createTableView(ObservableList<ExcelFieldPo> fields, Label tf1) {
		TableView<ExcelFieldPo> tableView = new TableView<>();
		tableView.getStyleClass().add("myTableTag");
		tableView.setEditable(true);
		tableView.getSelectionModel().selectedItemProperty().addListener(// 选中某一行
				new ChangeListener<ExcelFieldPo>() {
					@Override
					public void changed(ObservableValue<? extends ExcelFieldPo> observableValue, ExcelFieldPo oldItem,
							ExcelFieldPo newItem) {
						ExcelFieldPo p = newItem;
						if (p == null)
							return;
						String str1 = p.getColumnLabel().get();
						String tyNa = p.getColumnTypeName().get() + "(" + p.getColumnDisplaySize().get();
						if (p.getScale() != null && p.getScale().get() > 0) {
							tyNa += ", " + p.getScale().get();
						}
						tyNa += ")";

						str1 += "  : " + tyNa + "  : " + p.getColumnClassName().get();
						tf1.setText(str1);
					}
				});

		String colName1 = "字段名称";
		String colName2 = "Csv列";
		String colName3 = "自定义值";
		// 第一列
		TableColumn<ExcelFieldPo, String> fieldNameCol = new TableColumn<>(colName1); // "Field Name"
		fieldNameCol.setEditable(false);
		// 给单元格赋值
		fieldNameCol.setCellValueFactory(cellData -> {
			return cellData.getValue().getColumnLabel();
		});

		fieldNameCol.setCellFactory(MyTableCellTextField2ReadOnly.forTableColumn());
		fieldNameCol.setPrefWidth(180);

		tableView.getColumns().add(fieldNameCol);

		// 第二列
		TableColumn<ExcelFieldPo, String> valueCol = new TableColumn<>(colName2);
		valueCol.setPrefWidth(180);

		String[] headArr = csvHeadArray(csvFile, fields);
		valueCol.setCellValueFactory(p -> p.getValue().getExcelRowVal());
		valueCol.setCellFactory(ComboBox2TableCell.forTableColumn(headArr));

		tableView.getColumns().add(valueCol);

		// 第三列
		TableColumn<ExcelFieldPo, String> valueCol3 = new TableColumn<>(colName3);
		valueCol3.setPrefWidth(180);

		valueCol3.setCellValueFactory(p -> p.getValue().getFixedValue());
		valueCol3.setCellFactory(TextFieldTableCell.forTableColumn());

		tableView.getColumns().add(valueCol3);

		tableView.getItems().addAll(fields);
		return tableView;
	}

	// 界面顶部的操作按钮
	private FlowPane topPane(TableView<ExcelFieldPo> tv, ObservableList<ExcelFieldPo> fields) {
		FlowPane topfp = new FlowPane();
		topfp.setPadding(new Insets(5));
		Label lb = new Label();
		lb.setGraphic(IconGenerator.svgImageDefActive("search"));
		TextField filterField = new TextField();

		filterField.getStyleClass().add("myTextField");
		topfp.getChildren().add(lb);
		FlowPane.setMargin(lb, new Insets(0, 10, 0, 5));
		topfp.getChildren().add(filterField);
		topfp.setMinHeight(35);
		topfp.prefHeight(35);
		filterField.setPrefWidth(200);
		// 过滤功能
		filterField.textProperty().addListener((o, oldVal, newVal) -> {
			if (StrUtils.isNotNullOrEmpty(newVal)) {
				TableDataDetail.bindTableViewExcelFieldFilter(tv, fields, newVal);
			} else {
				tv.setItems(fields);
			}

		});

		// 清空列的值
		Button cleanBtn = new Button("清空列值");
		cleanBtn.setPadding(new Insets(5));
		cleanBtn.getStyleClass().add("myAlertBtn");
		cleanBtn.setOnAction(e -> {
			for (var tmp : fields) {
				tmp.getExcelRowVal().set("");
			}
		});

		topfp.getChildren().add(cleanBtn);

		FlowPane.setMargin(cleanBtn, new Insets(0, 10, 0, 5));
		return topfp;
	}

	// 按钮面板
	private AnchorPane btnPane() {
		AnchorPane btnPane = new AnchorPane();
		// 保存按钮
		Button nextbtn = saveBtnSetup();

		Button cancel = cancelBtn();

		btnPane.getChildren().addAll(cancel, nextbtn);
		AnchorPane.setRightAnchor(nextbtn, 10.0);
		AnchorPane.setRightAnchor(cancel, 60.0);
		return btnPane;
	}

	// 其他设置
	public List<Region> otherSet() {

		Label lb1 = new Label("起始行号");
		beginIdTF = new TextField();
		beginIdTF.setPromptText("默认第一行开始");

		Label lb2 = new Label("导入行数");
		conuntTF = new TextField();
		conuntTF.setPromptText("默认全部");

		saveSqlCheckBox = new JFXCheckBox("导入SQL保存到文件");
		tfFilePath = new TextField();
		tfFilePath.disableProperty().bind(saveSqlCheckBox.selectedProperty().not());
		Button selectFile = UiTools.openFileBtn(tfFilePath, stage);
		selectFile.disableProperty().bind(saveSqlCheckBox.selectedProperty().not());

		onlySave = new JFXCheckBox("只保存SQL到文件, 不用插入到数据库");
		onlySave.disableProperty().bind(saveSqlCheckBox.selectedProperty().not());
		HBox b2 = new HBox();
		b2.getChildren().addAll(tfFilePath, selectFile);

		List<Region> nds = new ArrayList<>();

		nds.add(lb1);
		nds.add(beginIdTF);

		nds.add(lb2);
		nds.add(conuntTF);

		nds.add(saveSqlCheckBox);
		nds.add(b2);

		nds.add(null);
		nds.add(onlySave);
		return nds;
	}

	// 组件布局
	public void layout(VBox tbox, String table) {
		VBox vb = new VBox();
		vb.maxHeight(500);
		vb.getChildren().add(tbox);

		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
		vb.setPadding(new Insets(5));

		// 按钮
		AnchorPane btnsPane = btnPane();
		vb.getChildren().add(btnsPane);

		stage = CreateWindow(vb, "CSV Row Map " + table + " Field");
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 5, 5, 5));

		List<Region> list = otherSet();

		int i = 0;
		int j = 0;

		for (int k = 0; k < list.size(); k += 2) {
			var node1 = list.get(k);
			var node2 = list.get(k + 1);
			int idxi = i++;
			int idxj = j++;
			if (node1 != null)
				grid.add(node1, 0, idxi);
			if (node2 != null)
				grid.add(node2, 1, idxj);
		}

		stage.show();
	}

	// 保存按钮
	public Button saveBtnSetup() {
		Button btn = new Button("Save");
		btn.getStyleClass().add("myAlertBtn");
		btn.setOnAction(e -> {

			if (saveSqlCheckBox.isSelected()) {
				String filePath = tfFilePath.getText();
				if (StrUtils.isNullOrEmpty(filePath)) {
					MyAlert.errorAlert("保存Sql的文件路径不能为空!");
					tfFilePath.requestFocus();
					return;
				}
			}
			List<ExcelFieldPo> vals = new ArrayList<>();
			// 提取有被映射的字段
			for (ExcelFieldPo fieldpo : csvFields) {
				fieldpo.getExcelRowVal().getValue();
				if (StrUtils.isNotNullOrEmpty(fieldpo.getExcelRowVal())
						|| StrUtils.isNotNullOrEmpty(fieldpo.getFixedValue())) {
					vals.add(fieldpo);
				}

			}
			String beginInt = beginIdTF.getText();
			String countInt = conuntTF.getText();
			Integer beginval = null;
			Integer countval = null;
			if (StrUtils.isNotNullOrEmpty(beginInt)) {
				beginval = Integer.valueOf(beginInt);
				// 程序的下标是从0开始, 需要减 1
				beginval = beginval - 1;
			}
			if (StrUtils.isNotNullOrEmpty(countInt)) {
				countval = Integer.valueOf(countInt);
			}
			btn.setDisable(true);

			Integer tmpBeginval = beginval;
			Integer tmpCountval = countval;
			LoadingAnimation.loadingAnimation("Saving....", v -> {
				try {

					CsvToDB.toTable(sqluckyConn, tableName, csvFile, tfFilePath.getText(), vals, tmpBeginval,
							tmpCountval, onlySave.isSelected(), saveSqlCheckBox.isSelected(), splitSymbol);
					MyAlert.infoAlert("导入成功!");
				} catch (Exception e1) {
					MyAlert.showTextArea("Error", "导入失败 ! \n" + e1.getMessage());
				}
				btn.setDisable(false);
			});

		});
		return btn;
	}

	public Button cancelBtn() {
		Button cancelBtn = new Button("Cancel");
		cancelBtn.getStyleClass().add("myAlertBtn");
		cancelBtn.setOnAction(e -> {
			stage.close();
		});
		return cancelBtn;
	}

	public Stage CreateWindow(VBox vb, String title) {
		SqluckyStage sqluckyStatge = new SqluckyStage(vb);
		stage = sqluckyStatge.getStage();
		Scene scene = sqluckyStatge.getScene();

		vb.getStyleClass().add("connectionEditor");

		vb.setPrefWidth(600);
		vb.maxWidth(600);
		AnchorPane bottomPane = new AnchorPane();
		bottomPane.setPadding(new Insets(10));

		vb.getChildren().add(bottomPane);
		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});

		stage.setTitle(title);
		CommonUtility.loadCss(scene);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setScene(scene);

		stage.setMaximized(false);
		stage.setResizable(false);
		return stage;
	}

}
