package net.tenie.Sqlucky.sdk.utility;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

public class FinderAction {
    /**
     * 代码编辑区域的, find
     * @param isRep
     * @param findStr
     * @return
     */
    public static boolean   showMainTabPaneFindReplace(boolean isRep, String findStr) {
        return  showTabPaneFindReplace(ComponentGetter.mainTabPane, isRep, findStr);
//        boolean isShow = false;
//        var sheet = MyEditorSheetHelper.getActivationEditorSheet();
//        if(sheet != null ){
//            var editor = sheet.getSqluckyEditor();
//            boolean isChildFoucde = isChildFocused(editor);
//            if (isChildFoucde) {
//                editor.getCodeArea().showFindReplaceTextBox(isRep, findStr);
//                isShow = true;
//            }
//        }
//
//
//        return isShow;
    }
    public static boolean hideMainTabPaneFindReplace() {
        return  hideTabPaneFindReplace(ComponentGetter.mainTabPane);
    }


    /**
     * 数据显示区域的find
     * @param isRep
     * @param findStr
     * @return
     */
    public static boolean  showDataTabPaneFindReplace(boolean isRep, String findStr) {
        return  showTabPaneFindReplace(ComponentGetter.dataTabPane, isRep, findStr);
//        boolean isShow = false;
//        TabPane myTabPane = ComponentGetter.dataTabPane;
//        Tab selectionTab = myTabPane.getSelectionModel().getSelectedItem();
//        if(selectionTab != null ){
//            Node tabConntent = selectionTab.getContent();
//            SqluckyEditor sqluckyEditor = findFocusedSqluckyEditor(tabConntent);
//            if (sqluckyEditor != null) {
//                sqluckyEditor.getCodeArea().showFindReplaceTextBox(isRep, findStr);
//                isShow = true;
//            }
//        }
//        return isShow;
    }
    public static boolean  hideDataTabPaneFindReplace() {
        return  hideTabPaneFindReplace(ComponentGetter.dataTabPane);
    }


    /**
     * 显示TabPane中选中的tab中焦点的sqluckyEditor
     * @param myTabPane
     * @param isRep
     * @param findStr
     * @return
     */
    private static boolean showTabPaneFindReplace(TabPane myTabPane, boolean isRep, String findStr) {
        boolean isShow = false;
        Tab selectionTab = myTabPane.getSelectionModel().getSelectedItem();
        if(selectionTab != null ){
            Node tabConntent = selectionTab.getContent();
            SqluckyEditor sqluckyEditor = findFocusedSqluckyEditor(tabConntent);
            if (sqluckyEditor != null) {
                sqluckyEditor.getCodeArea().showFindReplaceTextBox(isRep, findStr);
                isShow = true;
            }
        }
        return isShow;
    }

    /**
     * 隐藏TabPane中选中的tab中焦点的sqluckyEditor
     * @param myTabPane
     * @return
     */
    private static boolean hideTabPaneFindReplace(TabPane myTabPane) {
        boolean isShow = false;
        Tab selectionTab = myTabPane.getSelectionModel().getSelectedItem();
        if(selectionTab != null ){
            Node tabConntent = selectionTab.getContent();
            SqluckyEditor sqluckyEditor = findFocusedSqluckyEditor(tabConntent);
            if (sqluckyEditor != null) {
                sqluckyEditor.getCodeArea().hiddenFindReplaceBox();
                isShow = true;
            }
        }
        return isShow;
    }

    /**
     * tab的内容中找到焦点对象, 通过焦点对象并找到SqluckyEditor
     * @param tabConntent
     * @return
     */
    private static SqluckyEditor findFocusedSqluckyEditor( Node tabConntent ){
        if (tabConntent instanceof Parent pNode) {
            Node focusedNode = getFocusedChildNode(pNode);
            if (focusedNode != null) {
                SqluckyEditor sqluckyEditor = focusedSqluckyEditor(focusedNode);
                if (sqluckyEditor != null) {
                    return  sqluckyEditor;
                }
            }
        }
        return null;
    }


    public static void  showMainTabPaneFindReplace2(boolean isRep, String findStr){
        TabPane tabPane = ComponentGetter.mainTabPane;
        for(Tab tab : tabPane.getTabs()){
            Node node =  tab.getContent();
            if( node instanceof  Parent parent){
                Node  focusedNode  =  getFocusedChildNode(parent);
                if(focusedNode != null){
                    SqluckyEditor sqluckyEditor = focusedSqluckyEditor(focusedNode);
                    if(sqluckyEditor!= null ){
                        sqluckyEditor.getCodeArea().showFindReplaceTextBox(isRep, findStr);
                    }
                    break;
                }

            }
        }

    }

    /**
     * 判断对象是不是SqluckyEditor, 不是就循环父节点, 找不到就返回null
     * @param focusedNode
     * @return
     */
    private static SqluckyEditor focusedSqluckyEditor(Node focusedNode){
        if(focusedNode instanceof SqluckyEditor sqluckyEditor){
            return sqluckyEditor;
        }else{
            Parent tmpParent = focusedNode.getParent();
            while (tmpParent != null){
                if(tmpParent instanceof SqluckyEditor sqluckyEditor){
                    return sqluckyEditor;
                }else {
                    tmpParent = tmpParent.getParent();
                }

            }
        }

        return null;
    }



    // 递归判断是否是参数的组件子节点是焦点对象
    private static boolean isChildFocused(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node.isFocused()) {
                return true;
            } else if (node instanceof Parent subParent) {
                if (isChildFocused( subParent)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取焦点对象, 从参数parent的子对象中递归的查找焦点的子对象, 没有就返回null,
     * @param parent
     * @return
     */
    private static Node getFocusedChildNode(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node.isFocused()) {
                return node;
            } else if (node instanceof Parent ) {
                var subNode = getFocusedChildNode((Parent) node);
                if (subNode != null ) {
                    return subNode;
                }
            }
        }
        return null;
    }
}
