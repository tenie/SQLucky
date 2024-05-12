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

    /**
     * myBatis xml 中的sql , 转换 <> <= <![CDATA[ <= ]]>
     */
    public static void myBatisXmlSql() {
        CodeArea code = MyEditorSheetHelper.getCodeArea();
        String txt = code.getSelectedText();
        if (StrUtils.isNotNullOrEmpty(txt)) {
            IndexRange i = code.getSelection();
            int start = i.getStart();
            int end = i.getEnd();

            String rs = useXmlConversionElement(txt);
            code.deleteText(start, end);
            code.insertText(start, rs);
        } else {
            txt = MyEditorSheetHelper.getCurrentCodeAreaSQLText();
            String rs = useXmlConversionElement(txt);
            code.clear();
            code.appendText(rs);
        }
        MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
    }

    /**
     * myBatis xml的大于小于符号 , 转为正常符号
     */
    public static void trimMyBatisXml() {
        CodeArea code = MyEditorSheetHelper.getCodeArea();
        String txt = code.getSelectedText();
        if (StrUtils.isNotNullOrEmpty(txt)) {
            IndexRange i = code.getSelection();
            int start = i.getStart();
            int end = i.getEnd();

            String rs = trimXmlConversionElement(txt);
            code.deleteText(start, end);
            code.insertText(start, rs);
        } else {
            txt = MyEditorSheetHelper.getCurrentCodeAreaSQLText();
            String rs = trimXmlConversionElement(txt);
            code.clear();
            code.appendText(rs);
        }
        MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
    }

    /**
     * 大于小于转为 xml中的写法
     * @param str
     * @return
     */
    private static String useXmlConversionElement(String str){
        str =  str.replaceAll("<>", "#-#");
        str =  str.replaceAll("<=", "-##");
        str =  str.replaceAll(">=", "##-");

        str =  str.replaceAll("<", "<![CDATA[ < ]]>");
        str =  str.replaceAll(">", "<![CDATA[ > ]]>");

        str =  str.replaceAll("#-#", "<![CDATA[ <> ]]>");
        str =  str.replaceAll("-##", "<![CDATA[ <= ]]>");
        str =  str.replaceAll("##-", "<![CDATA[ >= ]]>");

        return str;
    }

    /**
     * 大于小于在 xml中的写法 该为普通
     * @param str
     * @return
     */
    private static String trimXmlConversionElement(String str){

        while (str.contains("<![CDATA[ <> ]]>")){
            str =   str.replace("<![CDATA[ <> ]]>", "<>");
        }

        while (str.contains("<![CDATA[ <= ]]>")){
            str =   str.replace("<![CDATA[ <= ]]>", "<=");
        }
        while (str.contains("<![CDATA[ >= ]]>")){
            str =   str.replace("<![CDATA[ >= ]]>", ">=");
        }
         while (str.contains("<![CDATA[ < ]]>")){
             str =   str.replace("<![CDATA[ < ]]>", "<");
        }
         while (str.contains("<![CDATA[ > ]]>")){
             str =   str.replace("<![CDATA[ > ]]>", ">");
        }

        return str;
    }
}
