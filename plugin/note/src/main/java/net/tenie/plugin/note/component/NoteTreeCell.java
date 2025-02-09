package net.tenie.plugin.note.component;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.plugin.note.utility.NoteUtility;

public class NoteTreeCell extends TreeCell<MyNoteEditorSheet>{
    private   Button  clean;
    private boolean isRootItem = false;
    private TreeView<MyNoteEditorSheet> treeView;
    private Button showInFolder;

    public NoteTreeCell( TreeView<MyNoteEditorSheet> treeView, Button showInFolder){
        this.treeView = treeView;
        this.showInFolder = showInFolder;
    }
    @Override
    protected void updateItem(MyNoteEditorSheet item, boolean empty) {
        super.updateItem(item, empty);
        // 给cell 内容添加 button
        // If the cell is empty we don't show anything.
        if (isEmpty()) {
            setGraphic(null);
            setText(null);
        } else {
            isRootItem = item.getIsRootItem();
            DocumentPo po = item.getDocumentPo();
            Region icon = po.getIcon();
            Label label = new Label();
            label.textProperty().bind( po.getTitle());

            label.setGraphic(icon);
            AnchorPane pn = new AnchorPane();
            pn.getChildren().add(label);
            if(isRootItem){
                this.clean =  new Button();
                clean.setMaxSize(12, 12);
                clean.setGraphic(IconGenerator.svgImageUnactive("times-circle", 14));
                clean.getStyleClass().add("myCleanBtn");
                clean.setVisible(false); // clean 按钮默认不显示, 只有在鼠标进入搜索框才显示
                clean.setOnAction(e -> {
                    TreeItem<MyNoteEditorSheet>  treeItem = item.getFileRootitem();
                    boolean tf = treeView.getRoot().getChildren().remove(treeItem);
                    if(tf){
                        NoteUtility.rmSavePath(treeItem);
                    }
                });

                this.setOnMouseEntered(e -> {
                    if (this.isSelected()) {
                        clean.setVisible(true);
                    }

                });

                this.setOnMouseExited(e -> {
                    clean.setVisible(false);
                });
                pn.getChildren().add(clean);
                AnchorPane.setRightAnchor(clean, 5.0);
            }

            // 点击事件, 显示文件到tab
            this.setOnMouseClicked(e -> {
                var selectedItem = treeView.getSelectionModel().getSelectedItem();
                showInFolder.setDisable(selectedItem == null);
                NoteUtility.clickItem(this.getTreeItem());

                if(isRootItem){
                    if (this.isSelected()) {
                        clean.setVisible(true);
                    }
                }

            });

//					pn.accessibleRoleProperty().set(AccessibleRole.PARENT);
            setGraphic(pn);

            setText(null);
        }
    }

}
