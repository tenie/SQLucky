package net.tenie.plugin.note.utility;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.note.component.MyNoteEditorSheet;
import net.tenie.plugin.note.component.NoteTabTree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索
 */
public class NoteSearchAction {
    public   boolean isFile = false;
    public   boolean isText = false;
    private  volatile boolean stopTag = false;
    public  TreeItem<MyNoteEditorSheet> rootCache = null;
    // 搜索
    public void searchAction(String queryStr, String fileType, Button down, Button up, Button stopbtn) {
        if (StrUtils.isNullOrEmpty(queryStr)) {
            return;
        } else {
            queryStr = queryStr.toLowerCase();
        }
        rootCache = NoteTabTree.rootNode;
        beginSearch();
        down.setDisable(true);
        up.setDisable(true);
        stopbtn.setDisable(false);
        var windowSceneRoot = NoteTabTree.noteStackPane;
        LoadingAnimation.addLoading(windowSceneRoot, "Search....", 14);

        String searchStr = queryStr;
        CommonUtils.runThread(v -> {
            List<File> searchDirs = new ArrayList<>();
            List<File> searchFiles = new ArrayList<>();
            try {
                var nodels = NoteTabTree.noteTabTreeView.getRoot().getChildren();//NoteTabTree.rootNode.getChildren();
                for (var subNd : nodels) {
                    var tmpfile = subNd.getValue().getFile();
                    if (tmpfile.isDirectory()) {
                        searchDirs.add(tmpfile);
                    }else {
                        searchFiles.add(tmpfile);
                    }

                }

                MyNoteEditorSheet stab = new MyNoteEditorSheet("", null);// ComponentGetter.appComponent.MyNoteEditorSheet();
                TreeItem<MyNoteEditorSheet> tmpRoot = new TreeItem<>(stab);

                for (var dir : searchDirs) {
                    List<File>  fileList = FileTools.getAllFileFromDirIncludeSubdirectory(dir, false);
                    searchFiles.addAll(fileList);
                }
                Platform.runLater(() -> {
                    NoteTabTree.noteTabTreeView.setRoot(tmpRoot);
                });
                // 搜索文件名
                if (isFile) {
                    searchFileName(searchFiles, searchStr, tmpRoot);
                }else {
                    // 搜索文本
                    searchTextFromFile(searchFiles, searchStr, fileType, tmpRoot);
                }
            } finally {
                NoteTabTree.noteTabTreeView.getSelectionModel().select(0);
                NoteTabTree.noteTabTreeView.refresh();
                LoadingAnimation.rmLoading(windowSceneRoot);
                down.setDisable(false);
                up.setDisable(false);
                stopbtn.setDisable(true);
                stopSearch();
            }

        });
    }

    /**
     * 对文件list中的文件名称进行比对, 名称包含查询字符串, 就加入到TreeItem中
     * @param fileList
     * @param searchStr
     * @param tmpRoot
     */
    public void searchFileName(List<File>  fileList , String searchStr, TreeItem<MyNoteEditorSheet> tmpRoot){
        for(File file : fileList){
            String fileName = file.getName().toLowerCase();
            if (fileName.contains(searchStr)) {
                TreeItem<MyNoteEditorSheet> fileRootitem = NoteUtility.createItemNode(file);
                Platform.runLater(() -> {
                    tmpRoot.getChildren().add(fileRootitem);
                });

            }
            if(isStopSearch()){
                return;
            }
        }
    }

    /**
     * 搜索文本
     * @param fileList
     * @param searchStr
     * @param fileType
     * @param tmpRoot
     */
    public void searchTextFromFile(List<File>  fileList , String searchStr, String fileType, TreeItem<MyNoteEditorSheet> tmpRoot) {
        if(StrUtils.isNullOrEmpty(fileType.trim())){
            fileType = "*.*";
        }
        for(File tmpfile : fileList){
            if (fileType.endsWith("*")) { // 如果结尾是* , 就规避二进制文件
                if (FileTools.isBinaryFile(tmpfile)) {
                   continue;
                }
            }
            boolean match = StrUtils.matchFileName(fileType, tmpfile);
            if (match) {
                LoadingAnimation.ChangeLabelText("Search: \n" + tmpfile.getName());
                String charset = FileTools.detectFileCharset(tmpfile);
                if (charset != null) {
                    String textStr = FileTools.read(tmpfile, charset);
                    if (textStr.toLowerCase().contains(searchStr)) {
                        TreeItem<MyNoteEditorSheet> fileRootitem = NoteUtility.createItemNode(tmpfile);

                        Platform.runLater(() -> {
                            tmpRoot.getChildren().add(fileRootitem);
                        });
                    }
                }
            }
            if(isStopSearch()){
                return;
            }
        }
    }




    public   boolean isStopSearch() {
        return stopTag;
    }

    public   void beginSearch() {
        stopTag = false;
    }

    public   void stopSearch() {
        stopTag = true;
    }
}
