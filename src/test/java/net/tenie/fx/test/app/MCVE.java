package net.tenie.fx.test.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MCVE extends Application {

    public void start(Stage stage) {

        VBox view = new VBox();
        view.setPrefSize(600, 400);

        // Creating the root node
        final TreeItem<String> root = new TreeItem<>("Root node");
        root.setExpanded(true);

        // Creating the tree items that will be the first children of the root node
        // and the parent to the child nodes.
        final TreeItem<String> parentNode1 = new TreeItem<>("Parent node 1");
        final TreeItem<String> parentNode2 = new TreeItem<>("Parent node 2");
        final TreeItem<String> parentNode3 = new TreeItem<>("Parent node 3");

        // Creating the tree items that will be the children of the parent
        // nodes.
        final TreeItem<String> childNode1 = new TreeItem<>("Child Node 1");
        final TreeItem<String> childNode2 = new TreeItem<>("Child Node 2");
        final TreeItem<String> childNode3 = new TreeItem<>("Child Node 3");

        // Adding tree items to the root
        root.getChildren().setAll(parentNode1, parentNode2, parentNode3);

        // Add the child nodes to all children of the root
        for (TreeItem<String> parent : root.getChildren()) {
            parent.getChildren().addAll(childNode1, childNode2, childNode3);
        }

        // Creating a tree table view
        final TreeView<String> treeView = new TreeView<>(root);

        // We set show root to false. This will hide the root and only show it's children in the treeview.
        treeView.setShowRoot(false);

        treeView.setCellFactory(e -> new CustomCell());

        view.getChildren().add(treeView);

        Scene scene = new Scene(view);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * A custom cell that shows a checkbox, label and button in the
     * TreeCell.
     */
    class CustomCell extends TreeCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            // If the cell is empty we don't show anything.
            if (isEmpty()) {
                setGraphic(null);
                setText(null);
            } else {
                // We only show the custom cell if it is a leaf, meaning it has
                // no children.
                if (this.getTreeItem().isLeaf()) {

                    // A custom HBox that will contain your check box, label and
                    // button.
                    HBox cellBox = new HBox(10);

                    CheckBox checkBox = new CheckBox();
                    Label label = new Label(item);
                    Button button = new Button("Press!");
                    // Here we bind the pref height of the label to the height of the checkbox. This way the label and the checkbox will have the same size. 
                    label.prefHeightProperty().bind(checkBox.heightProperty());

                    cellBox.getChildren().addAll(checkBox, label, button);

                    // We set the cellBox as the graphic of the cell.
                    setGraphic(cellBox);
                    setText(null);
                } else {
                    // If this is the root we just display the text.
                    setGraphic(null);
                    setText(item);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}