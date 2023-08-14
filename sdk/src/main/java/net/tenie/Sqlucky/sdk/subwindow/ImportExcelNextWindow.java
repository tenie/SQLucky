package net.tenie.Sqlucky.sdk.subwindow;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
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
import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.excel.ExcelHeadCellInfo;
import net.tenie.Sqlucky.sdk.excel.ExcelToDB;
import net.tenie.Sqlucky.sdk.excel.ExcelUtil;
import net.tenie.Sqlucky.sdk.excel.ReadExcel;
import net.tenie.Sqlucky.sdk.po.ImportFieldMapDetailPo;
import net.tenie.Sqlucky.sdk.po.ImportFieldMapPo;
import net.tenie.Sqlucky.sdk.po.ImportFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TextFieldSetup;

/**
 * excel导入
 * 
 * @author tenie
 *
 */
public class ImportExcelNextWindow {

	private Stage stage;
	private Logger logger = LogManager.getLogger(ImportExcelNextWindow.class);
	private TextField sheetTF;
	private TextField beginIdTF;
	private TextField conuntTF;
	private TextField tfFilePath;
	private JFXCheckBox saveSqlCheckBox;
	private JFXCheckBox onlySave;
	private String excelFile;
	private String tableName;
	private ObservableList<ImportFieldPo> excelFields;
	private SqluckyConnector sqluckyConn;
	private Workbook workbook;
	List<String> selectVal = new ArrayList<>();
	// 过滤输入框
	TextField filterField = new TextField();

	public void showWindow(SqluckyConnector dbc, String tableNameVal, String excelFilePath, Stage parentStage) {

		sqluckyConn = dbc;
		excelFile = excelFilePath;
		tableName = tableNameVal;
		LoadingAnimation.loadingAnimation("Loading....", v -> {

			VBox tbox = tableBox(dbc, tableName, excelFile);
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
	public VBox tableBox(SqluckyConnector dbc, String tablename, String excelFile) {
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

			excelFields = FXCollections.observableArrayList();
			for (var fd : fields) {
				ImportFieldPo excelpo = new ImportFieldPo(fd);
				excelFields.add(excelpo);
			}

			VBox tableBox = tableFiledMapExcelRowBox(tablename, excelFields, excelFile);
			return tableBox;
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return new VBox();
	}

	/**
	 * excel 文件头信息
	 * 
	 * @param excelFile
	 * @return
	 */
	private String[] excelHeadArray(String excelFile, ObservableList<ImportFieldPo> fields) {

		try {
			workbook = ExcelUtil.readFileToWorkbok(excelFile);
			if (workbook == null)
				return new String[0];
			List<ExcelHeadCellInfo> row1 = ReadExcel.readHeadInfo(workbook);

			if (row1 != null) {

				String[] headArr = new String[row1.size()];
//				List<String> selectVal = new ArrayList<>();
				for (int i = 0; i < row1.size(); i++) {
					ExcelHeadCellInfo cell = row1.get(i);
					String val = (cell.getCellIdx() + 1) + " - " + cell.getCellAddress() + " - " + cell.getCellVal();
					headArr[i] = val;
					selectVal.add(val);
				}

				for (int j = 0; (j < selectVal.size()) && (j < fields.size()); j++) {
					var tmp = fields.get(j);
					tmp.setExcelFieldInfo(selectVal);
					tmp.getExcelFieldVal().set(selectVal.get(j));
				}

				return headArr;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new String[0];
	}

	public VBox tableFiledMapExcelRowBox(String tableName, ObservableList<ImportFieldPo> fields, String excelFile) {

		Label tf1 = new Label();
		tf1.setPrefWidth(450);
		tf1.setPadding(new Insets(8, 0, 0, 0));

		// table
		var tableView = createTableView(fields, tf1);
		VBox subvb = new VBox();

		var topfp = topPane(tableView, fields);
		subvb.getChildren().add(topfp);

		subvb.getChildren().add(tableView);
		VBox.setVgrow(tableView, Priority.ALWAYS);
		subvb.getChildren().add(tf1);

		return subvb;
	}

	// 界面顶部的操作按钮
	private FlowPane topPane(TableView<ImportFieldPo> tableView, ObservableList<ImportFieldPo> fields) {
		FlowPane topfp = new FlowPane();
		topfp.setPadding(new Insets(5));
		Label lb = new Label();
		lb.setGraphic(IconGenerator.svgImageDefActive("search"));

		filterField.getStyleClass().add("myTextField");
		FlowPane.setMargin(lb, new Insets(0, 10, 0, 5));
		topfp.setMinHeight(35);
		topfp.prefHeight(35);
		filterField.setPrefWidth(200);
		// 过滤功能
		filterField.textProperty().addListener((o, oldVal, newVal) -> {
			if (StrUtils.isNotNullOrEmpty(newVal)) {
				TableDataDetail.bindTableViewExcelFieldFilter(tableView, fields, newVal);
			} else {
				tableView.setItems(fields);
			}

		});
		var textFieldPane = UiTools.textFieldAddCleanBtn(filterField);

		// 清空列的值
		Button cleanBtn = new Button("清空列值");
//		cleanBtn.setPadding(new Insets(5));
		cleanBtn.getStyleClass().add("myAlertBtn");
		cleanBtn.setOnAction(e -> {
			for (var tmp : fields) {
				tmp.getExcelFieldVal().set("");
			}
		});

		Button autoBtn = new Button("自动匹配");
//		cleanBtn.setPadding(new Insets(5));
		autoBtn.getStyleClass().add("myAlertBtn");
		autoBtn.setOnAction(e -> {
			ImportFieldMapPo tmp = new ImportFieldMapPo();
			tmp.setTableName(tableName);
			Connection conn = SqluckyAppDB.getConn();
			try {
				List<ImportFieldMapPo> ls = PoDao.select(conn, tmp);
				if (ls != null && ls.size() > 0) {
					ImportFieldMapPo mpo = ls.get(0);
					ImportFieldMapDetailPo dpo = new ImportFieldMapDetailPo();
					dpo.setTableId(mpo.getId());
					List<ImportFieldMapDetailPo> dls = PoDao.select(conn, dpo);
					if (dls != null && dls.size() > 0) {
						for (ImportFieldMapDetailPo mdpo : dls) {
							String fname = mdpo.getTableFiledName();
							int idx = mdpo.getExcelFiledIdx();
							for (var tmp1 : fields) {
								if (tmp1.getColumnLabel().get().equals(fname)) {
									String selectValtmp = selectVal.get(idx);
									tmp1.getExcelFieldVal().set(selectValtmp);

								}
//								tmp.getExcelFieldVal().set("");
							}
						}

					}
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				SqluckyAppDB.closeConn(conn);
			}

		});

		topfp.getChildren().add(lb);
		topfp.getChildren().add(textFieldPane); // filterField
		topfp.getChildren().add(cleanBtn);
		topfp.getChildren().add(autoBtn);
		FlowPane.setMargin(cleanBtn, new Insets(0, 10, 0, 5));
		return topfp;
	}

	private TableView<ImportFieldPo> createTableView(ObservableList<ImportFieldPo> fields, Label tf1) {
		TableView<ImportFieldPo> tv = new TableView<>();
		tv.getStyleClass().add("myTableTag");
		tv.setEditable(true);
		tv.getSelectionModel().selectedItemProperty().addListener(// 选中某一行
				new ChangeListener<ImportFieldPo>() {
					@Override
					public void changed(ObservableValue<? extends ImportFieldPo> observableValue, ImportFieldPo oldItem,
							ImportFieldPo newItem) {
						ImportFieldPo p = newItem;
						if (p == null)
							return;
						String tyNa = p.getColumnTypeName().get() + "(" + p.getColumnDisplaySize().get();
						if (p.getScale() != null && p.getScale().get() > 0) {
							tyNa += ", " + p.getScale().get();
						}
						tyNa += ")";

						tf1.setText(p.getColumnLabel().get() + "  :  " + tyNa + "  :  " + p.getColumnClassName().get());
					}
				});

		String colName1 = "字段名称";
		String colName2 = "Excel列";
		String colName3 = "自定义值";
		// 第一列
		TableColumn<ImportFieldPo, String> fieldNameCol = new TableColumn<>(colName1); // "Field Name"
		fieldNameCol.setEditable(false);
		// 给单元格赋值
		fieldNameCol.setCellValueFactory(cellData -> {
			return cellData.getValue().getColumnLabel();
		});

		fieldNameCol.setCellFactory(MyTableCellTextField2ReadOnly.forTableColumn());
		fieldNameCol.setPrefWidth(180);

		tv.getColumns().add(fieldNameCol);

		// 第二列
		TableColumn<ImportFieldPo, String> valueCol = new TableColumn<>(colName2);
		valueCol.setPrefWidth(180);

		String[] headArr = excelHeadArray(excelFile, fields);
		valueCol.setCellValueFactory(p -> p.getValue().getExcelFieldVal());
		valueCol.setCellFactory(ComboBox2TableCell.forTableColumn(headArr));

		tv.getColumns().add(valueCol);

		// 第三列
		TableColumn<ImportFieldPo, String> valueCol3 = new TableColumn<>(colName3);
		valueCol3.setPrefWidth(180);

		valueCol3.setCellValueFactory(p -> p.getValue().getFixedValue());
		valueCol3.setCellFactory(TextFieldTableCell.forTableColumn());

		tv.getColumns().add(valueCol3);

		tv.getItems().addAll(fields);
		return tv;
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
		Label lb0 = new Label("第几个sheet");
		sheetTF = new TextField("1");
		sheetTF.setPromptText("为空表示所有sheet的数据");
		TextFieldSetup.numberOnly(sheetTF);

		Label lb1 = new Label("起始行号");
		beginIdTF = new TextField();
		beginIdTF.setPromptText("默认第一行开始");
		TextFieldSetup.numberOnly(beginIdTF);

		Label lb2 = new Label("导入行数");
		conuntTF = new TextField();
		conuntTF.setPromptText("默认全部");
		TextFieldSetup.numberOnly(conuntTF);

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

		nds.add(lb0);
		nds.add(sheetTF);

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
		vb.maxHeight(600);
		vb.getChildren().add(tbox);

		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
		vb.setPadding(new Insets(5));

		// 按钮
		AnchorPane btnsPane = btnPane();
		vb.getChildren().add(btnsPane);

		Stage stage = CreateWindow(vb, "Excel Row Map " + table + " Field");
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));

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
			List<ImportFieldPo> vals = new ArrayList<>();
			// 提取有被映射的字段
			for (ImportFieldPo fieldpo : excelFields) {
				fieldpo.getExcelFieldVal().getValue();
				if (StrUtils.isNotNullOrEmpty(fieldpo.getExcelFieldVal())
						|| StrUtils.isNotNullOrEmpty(fieldpo.getFixedValue())) {
					vals.add(fieldpo);
				}

			}
			//
			if (vals.size() == 0) {
				MyAlert.errorAlert("字段还没有做关联!");
				return;
			} else {
				// 保存映射
				ImportFieldMapPo.save(tableName, "Excel", vals);
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
					Integer sheetNo = null;
					String sheetNoStr = sheetTF.getText();
					if (StrUtils.isNotNullOrEmpty(sheetNoStr.trim())) {
						sheetNo = Integer.valueOf(sheetNoStr);
					}

					ExcelToDB.toTable(sqluckyConn, tableName, workbook,
//							excelFile,
							tfFilePath.getText(), vals, sheetNo, tmpBeginval, tmpCountval, onlySave.isSelected(),
							saveSqlCheckBox.isSelected());
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
//		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			filterField.clear();
//			stage.close();
		});
//		scene.getAccelerators().put(spacebtn, () -> {
//			stage.close();
//		});

		stage.setTitle(title);
		CommonUtils.loadCss(scene);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setScene(scene);

		stage.setMaximized(false);
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> {
			if (workbook != null) {
				try {
					workbook.close();
					workbook = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		});
		return stage;
	}

}
