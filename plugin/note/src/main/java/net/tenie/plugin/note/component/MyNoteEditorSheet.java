package net.tenie.plugin.note.component;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.po.DocumentPo;

import java.sql.Connection;

public class MyNoteEditorSheet extends MyEditorSheet {
    private Boolean isRootItem = false;
    private  TreeItem<MyNoteEditorSheet> fileRootitem;

    public MyNoteEditorSheet(DocumentPo valDocumentPo, SqluckyEditor sqluckyEditor) {
        super(valDocumentPo, sqluckyEditor);
    }
    public MyNoteEditorSheet(String tabName, SqluckyEditor sqluckyEditor) {
        super(tabName, sqluckyEditor);
    }

    @Override// 保存逻辑单独实现
    public void saveScriptPo(Connection conn) {}

    @Override
    public void syncScriptPo() {}
    @Override
    public void syncScriptPo(Connection conn) {}


    public Boolean getIsRootItem() {
        return isRootItem;
    }

    public void setIsRootItem(Boolean rootItem, TreeItem<MyNoteEditorSheet> fileRootitem) {
        isRootItem = rootItem;
        this.fileRootitem =fileRootitem;
    }

    public TreeItem<MyNoteEditorSheet> getFileRootitem() {
        return fileRootitem;
    }

    public void setFileRootitem(TreeItem<MyNoteEditorSheet> fileRootitem) {
        this.fileRootitem = fileRootitem;
    }




}
