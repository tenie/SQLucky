package net.tenie.plugin.note.component;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;

import java.sql.Connection;

public class MyNoteEditorSheet extends MyEditorSheet {
    private Boolean isRootItem = false;
    TreeItem<MyNoteEditorSheet> fileRootitem;
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
    /**
     * tab 关闭时：阻止关闭最后一个
     */
    @Override
    public EventHandler<Event> tabCloseReq() {
        return new EventHandler<Event>() {
            @Override
            public void handle(Event e) {
            }
        };
    }

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
