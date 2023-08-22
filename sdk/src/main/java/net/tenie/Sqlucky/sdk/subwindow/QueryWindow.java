package net.tenie.Sqlucky.sdk.subwindow;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

public class QueryWindow {

	private Stage stage;

	public void showWindow(TableView<ResultSetRowPo> tableView, List<Node> nodes, String title) {

		VBox subvb = new VBox();

		var topfp = topPane(nodes);
		subvb.getChildren().add(topfp);
		subvb.getChildren().add(tableView);
		VBox.setVgrow(tableView, Priority.ALWAYS);
		layout(subvb, title);

	}

	// 界面顶部的操作按钮
	private FlowPane topPane(List<Node> nodes) {
		FlowPane topfp = new FlowPane();
		topfp.setPadding(new Insets(5));
		topfp.setMinHeight(35);
		topfp.prefHeight(35);
		
		for(var tmpNode : nodes) {
			topfp.getChildren().add(tmpNode);
		}

		return topfp;
	}

	// 组件布局
	public void layout(VBox tbox, String title) {
		tbox.setPadding(new Insets(5));
		Stage stage = CreateWindow(tbox, title);

		stage.showAndWait();
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
		stage.requestFocus();
		return stage;
	}

}
