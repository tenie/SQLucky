package net.tenie.Sqlucky.sdk.subwindow;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.ExcelMapper;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;

/**
 * excel导入
 * 
 * @author tenie
 *
 */
public class ImportExcelNextWindow {

	private static Stage stage;
	private static Logger logger = LogManager.getLogger(ImportExcelNextWindow.class);

	private static ChoiceBox<String> connNameChoiceBox;
	private static TextField tfTabName;
	private static TextField tfFilePath;

	public static void showWindow(SqluckyConnector dbc, String tableName) {
		ObservableList<SheetFieldPo> fieldPos = showTableFieldType(dbc, tableName);
		List<ExcelMapper> ls = FieldToList(fieldPos, tableName);
		List<Region> nodes = fieldListToComponents(ls);

		layout(nodes);

	}

	public static void test(SqluckyConnector dbc, String tablename) {
		String sql = "SELECT * FROM " + tablename + " WHERE 1=2";
		try {
			DbTableDatePo DP = SelectDao.selectSqlField(dbc.getConn(), sql);
			ObservableList<SheetFieldPo> fields = DP.getFields();

			for (int i = 0; i < fields.size(); i++) {
				SheetFieldPo p = fields.get(i);
				String tyNa = p.getColumnTypeName().get() + "(" + p.getColumnDisplaySize().get();
				if (p.getScale() != null && p.getScale().get() > 0) {
					tyNa += ", " + p.getScale().get();
				}
				tyNa += ")";
				StringProperty strp = new SimpleStringProperty("");
				p.setValue(strp);
			}
			TableDataDetail.tableFiledMapExcelRow(tablename, fields);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// 按钮面板
	private static AnchorPane btnPane() {
		AnchorPane btnPane = new AnchorPane();
		// 保存按钮
		Button nextbtn = nextBtn();

		Button cancel = cancelBtn();

		btnPane.getChildren().addAll(cancel, nextbtn);
		AnchorPane.setRightAnchor(nextbtn, 10.0);
		AnchorPane.setRightAnchor(cancel, 60.0);
		return btnPane;
	}

	public static ObservableList<SheetFieldPo> showTableFieldType(SqluckyConnector dbc, String tablename) {
		String sql = "SELECT * FROM " + tablename + " WHERE 1=2";
		ObservableList<SheetFieldPo> fields = null;
		try {
			DbTableDatePo DP = SelectDao.selectSqlField(dbc.getConn(), sql);
			fields = DP.getFields();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fields;
	}

	// 将表字段对象转换为列表
	public static List<ExcelMapper> FieldToList(ObservableList<SheetFieldPo> fields, String tableName) {
		List<ExcelMapper> ls = new ArrayList<>();
		for (SheetFieldPo po : fields) {
			ExcelMapper em = new ExcelMapper();
			em.setTableName(new SimpleStringProperty(tableName));
			em.setFieldName(new SimpleStringProperty(po.getColumnLabel().get()));
			em.setExcelRow(new SimpleStringProperty(""));
			em.setValue(new SimpleStringProperty(""));
			ls.add(em);
		}
		return ls;
	}

	//
	public static List<Region> fieldListToComponents(List<ExcelMapper> fields) {

		List<Region> list = new ArrayList<>();
		for (ExcelMapper em : fields) {
			Label fieldLabel = new Label(em.getFieldName().get());

			TextField fieldVal = new TextField();
			fieldVal.textProperty().bind(em.getValue());
			list.add(fieldLabel);
			list.add(fieldVal);
		}

		return list;
	}

	// 组件布局
	public static void layout(List<Region> list) {
		VBox vb = new VBox();
		vb.maxHeight(600);
		Label title = new Label("Import Excel To DB Table");
		title.setPadding(new Insets(15));
		AppComponent appComponent = ComponentGetter.appComponent;
		title.setGraphic(appComponent.getIconDefActive("gears"));
		vb.getChildren().add(title);
		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
		vb.setPadding(new Insets(5));

		// 按钮
		AnchorPane btnsPane = btnPane();
		vb.getChildren().add(btnsPane);

		Stage stage = CreateWindow(vb);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));

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

	// 选取文件按钮
	public static Button openFileBtn(TextField tfFilePath) {
		Button selectFileBtn = new Button("...");
		selectFileBtn.setOnAction(e -> {
			// 获取文件
			File file = FileOrDirectoryChooser.selectExcelFile(ComponentGetter.primaryStage);
			if (file != null) {
				tfFilePath.setText(file.getAbsolutePath());
			}

		});
		return selectFileBtn;
	}

	// 通过数据库的链接 生成数据模型
	public static SqluckyConnector generateModelData(TextField tfModelName) {
		AppComponent appComponent = ComponentGetter.appComponent;
		Map<String, SqluckyConnector> sqluckyConnMap = appComponent.getAllConnector();
		String selectConnName = connNameChoiceBox.getValue();
		SqluckyConnector sqluckyConn = sqluckyConnMap.get(selectConnName);
		String modelNameStr = tfModelName.getText();

		// 载入动画
//		LoadingAnimation.loadingAnimation("Saving....", v -> {
//			try {
//				// 生成数据
//				var tmpDataModelPoVal = DataModelMySQLDao.generateMySqlModel(sqluckyConn, modelNameStr);
//				var mid = tmpDataModelPoVal.getId();
//				// 数据插入到数据库
////					Long mid = DataModelUtility.insertDataModel(tmpDataModelPoVal);
//				// 插入模型节点
//				DataModelUtility.addModelItem(mid, DataModelTabTree.treeRoot);
//				Platform.runLater(() -> {
//					stage.close();
//				});
//			} catch (Exception e1) {
//				e1.printStackTrace();
//				MyAlert.errorAlert(e1.getMessage());
//			}
//		});

		return sqluckyConn;
	}

	// 下一步按钮
	public static Button nextBtn() {
		Button btn = new Button("next");
		btn.getStyleClass().add("myAlertBtn");
		btn.setOnMouseClicked(e -> {

		});
		return btn;
	}

	public static Button cancelBtn() {
		Button cancelBtn = new Button("Cancel");
		cancelBtn.getStyleClass().add("myAlertBtn");
		cancelBtn.setOnMouseClicked(e -> {
			stage.close();

		});
		return cancelBtn;
	}

	public static Stage CreateWindow(VBox vb) {
		SqluckyStage sqluckyStatge = new SqluckyStage(vb);
		stage = sqluckyStatge.getStage();
		Scene scene = sqluckyStatge.getScene();

		vb.getStyleClass().add("connectionEditor");

		vb.setPrefWidth(400);
		vb.maxWidth(400);
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

		CommonUtility.loadCss(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);

		stage.setMaximized(false);
		stage.setResizable(false);
		return stage;
	}

}
