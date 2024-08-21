package net.tenie.Sqlucky.sdk.utility;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqluckyTitledPane;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据不同的焦点, ctrl + f 显示不同的查找组件
 */
public class FinderAction {

    public static Map<String, SqluckyTitledPane> regSqluckyTitledPane = new HashMap<>();

    public static void putSqluckyTitledPane(String name, SqluckyTitledPane sqluckyTitledPane) {
        regSqluckyTitledPane.put(name, sqluckyTitledPane);
    }

    public static boolean showTitledPane(String findStr) {
        boolean isShow = false;
        for (var entry : regSqluckyTitledPane.entrySet()) {
            SqluckyTitledPane sqluckyTitledPane = entry.getValue();
            boolean isChild = isChildFocused(sqluckyTitledPane);
            if (isChild) {
                sqluckyTitledPane.showFinder(findStr);
                return true;
            }
        }
        return isShow;
    }

    public static boolean hideTitledPane() {
        boolean isShow = false;
        for (var entry : regSqluckyTitledPane.entrySet()) {
            SqluckyTitledPane sqluckyTitledPane = entry.getValue();
            boolean isChild = isChildFocused(sqluckyTitledPane);
            if (isChild) {
                sqluckyTitledPane.hideFinder();
                return true;
            }
        }
        return isShow;
    }

    public static boolean showTitledPaneFind(String findStr) {
        boolean isShow = false;
        VBox vBox = ComponentGetter.leftNodeContainer;
        SqluckyTitledPane sqluckyTitledPane = findFocusedSqluckyTitledPane(vBox);
        if (sqluckyTitledPane != null) {
            sqluckyTitledPane.showFinder(findStr);
        }

        return isShow;
    }

    public static boolean hideTitledPaneFind() {
        boolean succeed = false;
        VBox vBox = ComponentGetter.leftNodeContainer;
        SqluckyTitledPane sqluckyTitledPane = findFocusedSqluckyTitledPane(vBox);
        if (sqluckyTitledPane != null) {
            sqluckyTitledPane.hideFinder();
            succeed = true;
        }

        return succeed;
    }

    /**
     * 代码编辑区域的, find
     *
     * @param isRep
     * @param findStr
     * @return
     */
    public static boolean showMainTabPaneFindReplace(boolean isRep, String findStr) {
        return showTabPaneFindReplace(ComponentGetter.getEditTabPane(), isRep, findStr);
    }

    public static boolean hideMainTabPaneFindReplace() {
        return hideTabPaneFindReplace(ComponentGetter.getEditTabPane());
    }


    /**
     * 数据显示区域的find
     *
     * @param isRep
     * @param findStr
     * @return
     */
    public static boolean showDataTabPaneFindReplace(boolean isRep, String findStr) {
        return showTabPaneFindReplace(ComponentGetter.dataTabPane, isRep, findStr);
    }

    public static boolean hideDataTabPaneFindReplace() {
        return hideTabPaneFindReplace(ComponentGetter.dataTabPane);
    }


    /**
     * 显示TabPane中选中的tab中焦点的sqluckyEditor
     *
     * @param myTabPane
     * @param isRep
     * @param findStr
     * @return
     */
    private static boolean showTabPaneFindReplace(TabPane myTabPane, boolean isRep, String findStr) {
        boolean isShow = false;
        Tab selectionTab = myTabPane.getSelectionModel().getSelectedItem();
        if (selectionTab != null) {
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
     *
     * @param myTabPane
     * @return
     */
    private static boolean hideTabPaneFindReplace(TabPane myTabPane) {
        boolean succeed = false;
        Tab selectionTab = myTabPane.getSelectionModel().getSelectedItem();
        if (selectionTab != null) {
            Node tabConntent = selectionTab.getContent();
            SqluckyEditor sqluckyEditor = findFocusedSqluckyEditor(tabConntent);
            if (sqluckyEditor != null) {
                sqluckyEditor.getCodeArea().hiddenFindReplaceBox();
                succeed = true;
            }
        }
        return succeed;
    }

    /**
     * tab的内容中找到焦点对象, 通过焦点对象并找到SqluckyEditor
     *
     * @param tabConntent
     * @return
     */
    private static SqluckyEditor findFocusedSqluckyEditor(Node tabConntent) {
        if (tabConntent instanceof Parent pNode) {
            Node focusedNode = CommonUtils.getFocusedChildNode(pNode);
            if (focusedNode != null) {
                SqluckyEditor sqluckyEditor = focusedSqluckyEditor(focusedNode);
                if (sqluckyEditor != null) {
                    return sqluckyEditor;
                }
            }
        }
        return null;
    }

    private static SqluckyTitledPane findFocusedSqluckyTitledPane(Node tabConntent) {
        if (tabConntent instanceof Parent pNode) {
            Node focusedNode =  CommonUtils.getFocusedChildNode(pNode);
            if (focusedNode != null) {
                SqluckyTitledPane sqluckyTitledPane = focusedSqluckyTitledPane(focusedNode);
                if (sqluckyTitledPane != null) {
                    return sqluckyTitledPane;
                }
            }
        }
        return null;
    }

    /**
     * 判断对象是不是SqluckyEditor, 不是就循环父节点, 找不到就返回null
     *
     * @param focusedNode
     * @return
     */
    private static SqluckyEditor focusedSqluckyEditor(Node focusedNode) {
        if (focusedNode instanceof SqluckyEditor sqluckyEditor) {
            return sqluckyEditor;
        } else {
            Parent tmpParent = focusedNode.getParent();
            while (tmpParent != null) {
                if (tmpParent instanceof SqluckyEditor sqluckyEditor) {
                    return sqluckyEditor;
                } else {
                    tmpParent = tmpParent.getParent();
                }

            }
        }

        return null;
    }

    private static SqluckyTitledPane focusedSqluckyTitledPane(Node focusedNode) {
        if (focusedNode instanceof SqluckyTitledPane sqluckyTitledPane) {
            return sqluckyTitledPane;
        } else {
            Parent tmpParent = focusedNode.getParent();
            while (tmpParent != null) {
                if (tmpParent instanceof SqluckyTitledPane sqluckyTitledPane) {
                    return sqluckyTitledPane;
                } else {
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
                if (isChildFocused(subParent)) {
                    return true;
                }
            }
        }
        return false;
    }

//    /**
//     * 获取焦点对象, 从参数parent的子对象中递归的查找焦点的子对象, 没有就返回null,
//     *
//     * @param parent
//     * @return
//     */
//    private static Node getFocusedChildNode(Parent parent) {
//        for (Node node : parent.getChildrenUnmodifiable()) {
//            if (node.isFocused()) {
//                return node;
//            } else if (node instanceof Parent) {
//                var subNode = getFocusedChildNode((Parent) node);
//                if (subNode != null) {
//                    return subNode;
//                }
//            }
//        }
//        return null;
//    }
}
