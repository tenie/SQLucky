package net.tenie.Sqlucky.sdk.subwindow;

import java.io.File;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TextFieldSetup;

/**
 * excel导入
 * 
 * @author tenie
 *
 */
public class ImportExcelWindow {

	private static Stage stage;
	private static Logger logger = LogManager.getLogger(ImportExcelWindow.class);

	private static ChoiceBox<String> connNameChoiceBox;
	private static TextField tfTabName;
	private static TextField tfFilePath;

	// 按钮面板
	private static AnchorPane btnPane(String tableName) {
		AnchorPane btnPane = new AnchorPane();
		// 保存按钮
		Button nextbtn = nextBtn();

		nextbtn.disableProperty().bind(connNameChoiceBox.valueProperty().isNull()
				.or(tfTabName.textProperty().isEmpty().or(tfFilePath.textProperty().isEmpty())));

		Button cancel = cancelBtn();

		btnPane.getChildren().addAll(cancel, nextbtn);
		AnchorPane.setRightAnchor(nextbtn, 10.0);
		AnchorPane.setRightAnchor(cancel, 60.0);
		return btnPane;
	}

	// 组件布局
	public static void layout(List<Region> list, String tableName) {
		VBox vb = new VBox();
		Label title = new Label("Import Excel To DB Table");
		title.setPadding(new Insets(15));
		AppComponent appComponent = ComponentGetter.appComponent;
		title.setGraphic(appComponent.getIconDefActive("gears"));
		vb.getChildren().add(title);
		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
		vb.setPadding(new Insets(5));

		// 按钮
		AnchorPane btnsPane = btnPane(tableName);
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

	// 选择数据链接名称
	public static ChoiceBox<String> ChoiceBoxDbConnection(String selVal) {
		AppComponent appComponent = ComponentGetter.appComponent;
		List<String> connNames = appComponent.getAllActiveConnectorName();

		ObservableList<String> connNameVals = FXCollections.observableArrayList(connNames);

		ChoiceBox<String> connNameChoiceBox = new ChoiceBox<String>(connNameVals);

		if (StrUtils.isNotNullOrEmpty(selVal)) {
			connNameChoiceBox.getSelectionModel().select(selVal);
		}

		return connNameChoiceBox;
	}

	public static void showWindow(String tableName, String connName) {

		String str = "DB Connection";
		Label lbDBconn = new Label(str);
		connNameChoiceBox = ChoiceBoxDbConnection(connName);
		String tabNameStr = "Table Name";
		Label lbModelName = new Label(tabNameStr);

		tfTabName = new TextField();
		tfTabName.setPromptText(tabNameStr);
		tfTabName.setText(tableName);

		TextFieldSetup.setMaxLength(tfTabName, 100);

		// 选择文件
		String filePath = "File path";
		Label lbFilePath = new Label(filePath);

		tfFilePath = new TextField();
		tfFilePath.setPromptText(filePath);
		tfFilePath.setOnMouseClicked(v -> {
			String fileVal = tfFilePath.getText();
			if (StrUtils.isNullOrEmpty(fileVal)) {
				FileOrDirectoryChooser.getExcelFilePathAction(tfFilePath, stage);
			}

		});

		HBox fileBox = new HBox();
		Button selectFile = UiTools.openExcelFileBtn(tfFilePath, stage);
		fileBox.getChildren().addAll(tfFilePath, selectFile);

		List<Region> list = new ArrayList<>();

		list.add(lbDBconn);
		list.add(connNameChoiceBox);
		list.add(lbModelName);
		list.add(tfTabName);

		list.add(lbFilePath);
		list.add(fileBox);

		layout(list, tableName);

	}

//	// 选取文件按钮
//	public static Button openFileBtn(TextField tfFilePath) {
//		Button selectFileBtn = new Button("...");
//		selectFileBtn.setOnAction(e -> {
//			FileOrDirectoryChooser.getFilePathAction(tfFilePath, stage);
//		});
//		return selectFileBtn;
//	}

	// 下一步按钮
	public static Button nextBtn() {
		Button btn = new Button("next");
		btn.getStyleClass().add("myAlertBtn");
		btn.setOnAction(v -> {
			// 文件是否存在
			String fileval = tfFilePath.getText();
			File filePath = new File(fileval);
			if (!filePath.exists()) {
				MyAlert.errorAlert("文件不存在; " + filePath, true);
				return;
			}

			String connName = connNameChoiceBox.getValue();
			AppComponent appComponent = ComponentGetter.appComponent;
			Map<String, SqluckyConnector> sqluckyConnMap = appComponent.getAllConnector();
			SqluckyConnector sqluckyConn = sqluckyConnMap.get(connName);
			String tableName = tfTabName.getText();
			ImportExcelNextWindow.showWindow(sqluckyConn, tableName, tfFilePath.getText());
			stage.close();
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
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setScene(scene);

		stage.setMaximized(false);
		stage.setResizable(false);
		return stage;
	}

}
