package net.tenie.Sqlucky.sdk.sql;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import javafx.scene.control.IndexRange;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.fxmisc.richtext.CodeArea;

public class SqlUtils {
    // 代码格式化
    public static void formatSqlText() {
        CodeArea code = MyEditorSheetHelper.getCodeArea();
        String txt = code.getSelectedText();
        if (StrUtils.isNotNullOrEmpty(txt)) {
            IndexRange i = code.getSelection();
            int start = i.getStart();
            int end = i.getEnd();

            String rs = SqlFormatter.format(txt);
            code.deleteText(start, end);
            code.insertText(start, rs);
        } else {
            txt = MyEditorSheetHelper.getCurrentCodeAreaSQLText();
            String rs = SqlFormatter.format(txt);
            code.clear();
            code.appendText(rs);
        }
        MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
    }


    // sql 压缩
    public static void pressSqlText() {
        CodeArea code = MyEditorSheetHelper.getCodeArea();
        String txt = code.getSelectedText();
        if (StrUtils.isNotNullOrEmpty(txt)) {
            IndexRange i = code.getSelection();
            int start = i.getStart();
            int end = i.getEnd();

            String rs = StrUtils.pressString(txt);
            code.deleteText(start, end);
            code.insertText(start, rs);
        } else {
            txt = MyEditorSheetHelper.getCurrentCodeAreaSQLText();
            String rs = StrUtils.pressString(txt);
            code.clear();
            code.appendText(rs);
        }
        MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
    }
}
