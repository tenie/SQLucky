package net.tenie.Sqlucky.sdk.subwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
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
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.TextFieldSetup;

/**
 * excel导入
 * 
 * @author tenie
 *
 */
public class ImportExcelWindow {
	// 编辑连接时记录连接状态
	public static boolean editLinkStatus = false;
	public static Stage stage;
	private static Logger logger = LogManager.getLogger(ImportExcelWindow.class);

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

	// 组件布局
	public static void layout(List<Region> list) {
		VBox vb = new VBox();
		Label title = new Label("Generate Model From DB");
		title.setPadding(new Insets(15));
		AppComponent appComponent = ComponentGetter.appComponent;
		title.setGraphic(appComponent.getIconDefActive("gears"));
		vb.getChildren().add(title);
		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
		vb.setPadding(new Insets(5));
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

	// 选择数据链接名称
	public static ChoiceBox<String> ChoiceBoxDbConnection() {
		AppComponent appComponent = ComponentGetter.appComponent;
		List<String> connNames = appComponent.getAllConnectorName();

		ObservableList<String> connNameVals = FXCollections.observableArrayList(connNames);

		ChoiceBox<String> connNameChoiceBox = new ChoiceBox<String>(connNameVals);
		return connNameChoiceBox;
	}

	static ChoiceBox<String> connNameChoiceBox;

	public static void showWindow(String tableName) {

		String str = "DB Connection";
		Label lbDBconn = new Label(str);
		connNameChoiceBox = ChoiceBoxDbConnection();

		String tabNameStr = "Table Name";
		Label lbModelName = new Label(tabNameStr);
		TextField tfTabName = new TextField();
		tfTabName.setPromptText(tabNameStr);
		tfTabName.setDisable(true);
		tfTabName.setText(tableName);

		TextFieldSetup.setMaxLength(tfTabName, 60);
		connNameChoiceBox.valueProperty().addListener((obj, ol, ne) -> {
			if (ne != null && !"".equals(ne)) {
				tfTabName.setText(ne + "_");
				tfTabName.requestFocus();
			}
		});

		// 保存按钮
		var savebtn = saveBtn(tfTabName);
		savebtn.disableProperty().bind(tfTabName.textProperty().isEmpty());
		var cancel = cancelBtn();

		List<Region> list = new ArrayList<>();

		list.add(lbDBconn);
		list.add(connNameChoiceBox);
		list.add(lbModelName);
		list.add(tfTabName);

		list.add(savebtn);
		list.add(cancel);

		layout(list);

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

	public static Button saveBtn(TextField tfModelName) {
		Button saveBtn = new Button("Save");
		saveBtn.getStyleClass().add("myAlertBtn");
		saveBtn.setOnMouseClicked(e -> {

		});
		return saveBtn;
	}

	public static Button cancelBtn() {
		Button cancelBtn = new Button("Cancel");
		cancelBtn.getStyleClass().add("myAlertBtn");
		cancelBtn.setOnMouseClicked(e -> {
			stage.close();

		});
		return cancelBtn;
	}
}
