package net.tenie.fx.test.app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TreeViewExample extends Application {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void start(Stage primaryStage) {

		TreeItem root = new TreeItem("root");
		TreeItem item1 = new TreeItem("Level1");
		TreeItem item2 = new TreeItem("Level1");
		TreeItem item11 = new TreeItem("Level2");
		TreeView tree = new TreeView();
		item1.getChildren().add(item11);
		tree.setRoot(root);
		tree.getRoot().getChildren().addAll(item1, item2);
		tree.getRoot().setExpanded(true);
		StackPane rootPane = new StackPane();
		tree.setEditable(true);
		tree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {

			@Override
			public TreeCell<String> call(TreeView<String> param) {
				return new TextFieldTreeCellImpl();
			}
		});

		rootPane.getChildren().add(tree);

		Button btn = new Button();
		btn.setText("Change Name to 'TEST'");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// CHANGE TEXT OF SELECTED TreeItem to "TEST"?
				item1.setValue("TEST");
			}
		});
		rootPane.getChildren().add(btn);

		Scene scene = new Scene(rootPane);

		primaryStage.setTitle("Hello World!");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	public class TextFieldTreeCellImpl extends TreeCell<String> {

		private TextField textField;

		public TextFieldTreeCellImpl() {
		}

		@Override
		public void startEdit() {
			super.startEdit();

			if (textField == null) {
				createTextField();
			}
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}

		public void cancelEdit() {
			super.cancelEdit();
			setText((String) getItem());
			setGraphic(getTreeItem().getGraphic());
		}

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(getTreeItem().getGraphic());
				}
			}
		};

		private void createTextField() {
			textField = new TextField(getString());

			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER) {
						commitEdit(textField.getText());
					} else if (t.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					}
				}
			});
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}
}