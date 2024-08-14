package net.tenie.Sqlucky.sdk.sql;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import javafx.application.Platform;
import javafx.scene.control.IndexRange;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.fxmisc.richtext.CodeArea;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SqlUtils {
    // 注册格式化sql的拓展函数, 可以在格式化前和格式化后执行自定义的格式化
    public static List<Function<String, String>> regFormatFuncPre = new ArrayList<>();
    public static List<Function<String, String>> regFormatFuncAft = new ArrayList<>();
    // 代码格式化
    public static void formatSqlText() {

        CodeArea code = MyEditorSheetHelper.getCodeArea();
        String txt = code.getSelectedText();
        if (StrUtils.isNotNullOrEmpty(txt)) {
            IndexRange i = code.getSelection();
            int start = i.getStart();
            int end = i.getEnd();
            txt = preFormat(txt);
            txt = SqlFormatter.format(txt);
            txt = aftFormat(txt);
            code.deleteText(start, end);
            code.insertText(start, txt);
        } else {
            txt = MyEditorSheetHelper.getCurrentCodeAreaSQLText();
            txt = preFormat(txt);
            txt = SqlFormatter.format(txt);
            txt = aftFormat(txt);
            code.clear();
            code.appendText(txt);
        }
        MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
    }

    private static String preFormat(String txt){
        String tmp = txt;
        if(! regFormatFuncPre.isEmpty()){
            for(var func: regFormatFuncPre){
                tmp = func.apply(tmp);
            }
        }
        return tmp;
    }
    private static String aftFormat(String txt){
        String tmp = txt;
        if(! regFormatFuncAft.isEmpty()){
            for(var func: regFormatFuncAft){
                tmp = func.apply(tmp);
            }
        }
        return tmp;
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
    // 删除空行
    public static void cleanEmptyLine() {
        CodeArea code = MyEditorSheetHelper.getCodeArea();
        String txt = code.getSelectedText();
        if (StrUtils.isNotNullOrEmpty(txt)) {
            IndexRange i = code.getSelection();
            int start = i.getStart();
            int end = i.getEnd();

            String rs = StrUtils.cleanEmptyLine(txt);
            code.deleteText(start, end);
            code.insertText(start, rs);
        } else {
            txt = MyEditorSheetHelper.getCurrentCodeAreaSQLText();
            String rs = StrUtils.cleanEmptyLine(txt);
            code.clear();
            code.appendText(rs);
        }
        MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
    }

    /**
     * 清空多余空白符号
     */
    public static void cleanBlankChar() {
        Platform.runLater(() -> {
            String text = MyEditorSheetHelper.getActivationEditorSelectTextOrAllText();
            if (StrUtils.isNullOrEmpty(text))
                return;
            String rs = StrUtils.cleanrRedundantBlank(text);
            MyEditorSheetHelper.replaceSelectTextOrAllText(rs);
            MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
        });

    }





    /**
     * myBatis xml的大于小于符号 , 转为正常符号
     */
    public static void trimMyBatisXml() {
        Platform.runLater(() -> {
            String text = MyEditorSheetHelper.getActivationEditorSelectTextOrAllText();
            if (StrUtils.isNullOrEmpty(text))
                return;

            String rs = trimXmlConversionElement(text);
            MyEditorSheetHelper.replaceSelectTextOrAllText(rs);

        });
//        CodeArea code = MyEditorSheetHelper.getCodeArea();
//        String txt = code.getSelectedText();
//        if (StrUtils.isNotNullOrEmpty(txt)) {
//            IndexRange i = code.getSelection();
//            int start = i.getStart();
//            int end = i.getEnd();
//
//            String rs = trimXmlConversionElement(txt);
//            code.deleteText(start, end);
//            code.insertText(start, rs);
//        } else {
//            txt = MyEditorSheetHelper.getCurrentCodeAreaSQLText();
//            String rs = trimXmlConversionElement(txt);
//            code.clear();
//            code.appendText(rs);
//        }
//        MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
    }

    /**
     * 大于小于转为 xml中的写法
     * @param str
     * @return
     */
    public static String useXmlConversionElement(String str){
//        // 1. 先把文本中的xml元素 替换为占位符, 避免把xml标记符号也
        StrUtils.matherString msVal = StrUtils.getXmlEleMatcher(str);
        // 替换后的文本
//        str = msVal.newString();

        str =  str.replaceAll("<>", "#-#");
        str =  str.replaceAll("<=", "-##");
        str =  str.replaceAll(">=", "##-");

        str =  str.replaceAll("<", "@_@@");
        str =  str.replaceAll(">", "@@_@");

        str =  str.replaceAll("#-#", "<![CDATA[ <> ]]>");
        str =  str.replaceAll("-##", "<![CDATA[ <= ]]>");
        str =  str.replaceAll("##-", "<![CDATA[ >= ]]>");


        str =  str.replaceAll("@_@@", " <![CDATA[ < ]]> ");
        str =  str.replaceAll("@@_@", " <![CDATA[ > ]]> ");

        // 注释掉xml元素
//        str = StrUtils.recoverStringMatcher(msVal, str);
        return str;
    }

    /**
     * xml中的的转义字符串写法转化为普通字符串, 并且把xml中的元素注释掉
     * 1. 先把转义符替换成正常字符
     * 2. 把xml的元素<></> 注释掉
     * 3. 把 mybatis元素 #{} ${} 注释掉
     * @param str
     * @return
     */
    public static String trimXmlConversionElement(String str){
        if(! (str.contains("&lt;") ||
                str.contains("&gt;") ||
                str.contains("<![CDATA[") ||
                str.contains("< ! [CDATA[") ||
                str.contains("]]>")) ){
            return str;
        }
//        while (str.contains("<![CDATA[ <> ]]>")){
//            str =   str.replace("<![CDATA[ <> ]]>", "<>");
//        }
//
//        while (str.contains("<![CDATA[ <= ]]>")){
//            str =   str.replace("<![CDATA[ <= ]]>", "<=");
//        }
//        while (str.contains("<![CDATA[ >= ]]>")){
//            str =   str.replace("<![CDATA[ >= ]]>", ">=");
//        }
//        while (str.contains("<![CDATA[ < ]]>")){
//            str =   str.replace("<![CDATA[ < ]]>", "<");
//        }
//        while (str.contains("<![CDATA[ > ]]>")){
//            str =   str.replace("<![CDATA[ > ]]>", ">");
//        }
        //  &lt; < 小于号
        while (str.contains("&lt;")){
            str =   str.replace("&lt;", "<");
        }
        // &gt; > 大于号
        while (str.contains("&gt;")){
            str =   str.replace("&gt;", ">");
        }
        while (str.contains("<![CDATA[")){
            str =   str.replace("<![CDATA[", "");
        }
        while (str.contains("< ! [CDATA[")){
            str =   str.replace("< ! [CDATA[", "");
        }
        while (str.contains("]]>")){
            str =   str.replace("]]>", "");
        }


//        // 1. 先把文本中的xml元素 替换为占位符, 避免把xml标记符号也
//        StrUtils.matherString msVal = StrUtils.getXmlEleMatcher(str);
//        // 替换后的文本
//        str = msVal.newString();
//
//
//        // 注释掉xml元素
//        str = StrUtils.recoverStringMatcherToComment(msVal, str);
//
//        str = myBatisElementAddComment(str);
        return str;
    }

    /**
     * mysql 的元素注释掉, 避免sql执行报错, 如 ${} #{}
     */
    private static String myBatisElementAddComment(String text){
        // 1. 先把文本中的字符串替换为占位符
        StrUtils.matherString msVal = StrUtils.getStringMatcher(text);
        // 替换后的文本
        text = msVal.newString();
        List<IndexRange>  indexRangeList= StrUtils.getMyBatisEleRangeList(text);
        for(int i = indexRangeList.size() -1; i > -1 ; i --){
            IndexRange ir = indexRangeList.get(i);
            text = StrUtils.textAddCommentsByRange(text, ir);
        }

        text = StrUtils.recoverStringMatcher(msVal, text);
        return text;

    }
}
