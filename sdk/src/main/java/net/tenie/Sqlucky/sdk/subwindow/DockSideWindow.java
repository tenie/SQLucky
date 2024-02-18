package net.tenie.Sqlucky.sdk.subwindow;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.bottomSheet.MyBottomSheet;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtils;

/**
 * 数据表单独窗口
 * 
 * @author tenie
 *
 */
public class DockSideWindow {

	private Stage stage;
	private MyBottomSheet myBottomSheet;

	public void showWindow(TableView<ResultSetRowPo> tableView, String tableName) {

		VBox subvb = new VBox();

		var topfp = topPane(tableView);
		subvb.getChildren().add(topfp);
		subvb.getChildren().add(tableView);
		VBox.setVgrow(tableView, Priority.ALWAYS);
		layout(subvb, tableName);

	}

	public void showWindow(MyBottomSheet myBottomSheet, VBox DataPaneVbox, String tableName) {
		this.myBottomSheet = myBottomSheet;
		VBox subvb = new VBox();
		subvb.getChildren().add(DataPaneVbox);
		VBox.setVgrow(DataPaneVbox, Priority.ALWAYS);
		layout(subvb, tableName);

	}

	// 界面顶部的操作按钮
	private FlowPane topPane(TableView<ResultSetRowPo> tableView) {
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

		ObservableList<ResultSetRowPo> items = tableView.getItems();

		// 添加过滤功能
		filterField.textProperty().addListener((o, oldVal, newVal) -> {
			if (StrUtils.isNotNullOrEmpty(newVal)) {
				TableViewUtils.tableViewAllDataFilter(tableView, items, newVal);
			} else {
				tableView.setItems(items);
			}

		});

		return topfp;
	}

	// 组件布局
	public void layout(VBox tbox, String tableName) {
		tbox.setPadding(new Insets(5));
		Stage stage = CreateWindow(tbox, tableName);

		stage.show();
	}

	public Stage CreateWindow(VBox vb, String title) {
		SqluckyStage sqluckyStatge = new SqluckyStage(vb);
		stage = sqluckyStatge.getStage();
		Scene scene = sqluckyStatge.getScene();

		vb.getStyleClass().add("connectionEditor");

		vb.setPrefWidth(1000);
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
		CommonUtils.loadCss(scene);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setScene(scene);
		stage.setOnCloseRequest(e -> myBottomSheet.clean());
		return stage;
	}

}
