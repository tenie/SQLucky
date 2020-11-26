package net.tenie.fx.component;

import com.jfoenix.controls.JFXButton;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.tenie.fx.config.ConfigVal;

public class DataTransferWindow {
	
	public DataTransferWindow() {
		CreateModalWindow();
	}
	
	public   Stage CreateModalWindow() {

		final Stage stage = new Stage();
		VBox vb = new VBox();
		vb.getStyleClass().add("myAlert");
		Scene scene = new Scene(vb);

		vb.setPrefWidth(500);
		vb.maxWidth(500);

		AnchorPane closepane = new AnchorPane();
		JFXButton hideBottom = new JFXButton();
		hideBottom.setGraphic(ImageViewGenerator.svgImageUnactive("window-close"));
		closepane.getChildren().add(hideBottom);
		AnchorPane.setRightAnchor(hideBottom, 0.0);
		hideBottom.setOnAction(v -> {
			stage.close();
		});
		vb.getChildren().add(closepane);

		AnchorPane bottomPane = new AnchorPane();
		bottomPane.setPadding(new Insets(10));
		JFXButton btn = new JFXButton("Close");
		btn.setOnAction(value -> {
			stage.close();
		});
		bottomPane.getChildren().add(btn);
		AnchorPane.setRightAnchor(btn, 0.0);

		vb.getChildren().add(bottomPane);
		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination enterbtn = new KeyCodeCombination(KeyCode.ENTER);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(enterbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});

		scene.getStylesheets().addAll(ConfigVal.cssList);

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);

		stage.setMaximized(false);
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);// 设定窗口无边框

		stage.show();
		stage.setOnCloseRequest(v -> {

		});
		return stage;
	}
}
